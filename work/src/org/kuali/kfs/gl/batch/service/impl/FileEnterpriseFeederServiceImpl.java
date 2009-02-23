/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.EnterpriseFeederNotificationService;
import org.kuali.kfs.gl.batch.service.EnterpriseFeederService;
import org.kuali.kfs.gl.batch.service.FileEnterpriseFeederHelperService;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.impl.EnterpriseFeederStatusAndErrorMessagesWrapper;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.kns.service.DateTimeService;

/**
 * This class iterates through the files in the enterprise feeder staging directory, which is injected by Spring. Note: this class
 * is NOT annotated as transactional. This allows the helper service, which is defined as transactional, to do a per-file
 * transaction.
 */
public class FileEnterpriseFeederServiceImpl implements EnterpriseFeederService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileEnterpriseFeederServiceImpl.class);

    private String directoryName;
    private String glOriginEntryDirectoryName;

    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private FileEnterpriseFeederHelperService fileEnterpriseFeederHelperService;
    private EnterpriseFeederNotificationService enterpriseFeederNotificationService;
    private String reconciliationTableId;

    /**
     * Feeds file sets in the directory whose name is returned by the invocation to getDirectoryName()
     * 
     * @see org.kuali.kfs.gl.batch.service.EnterpriseFeederService#feed(java.lang.String)
     */
    public void feed(String processName, boolean performNotifications) {
        // ensure that this feeder implementation may not be run concurrently on this JVM

        // to consider: maybe use java NIO classes to perform done file locking?
        synchronized (FileEnterpriseFeederServiceImpl.class) {
            if (StringUtils.isBlank(directoryName)) {
                throw new IllegalArgumentException("directoryName not set for FileEnterpriseFeederServiceImpl.");
            }
            FileFilter doneFileFilter = new SuffixFileFilter(DONE_FILE_SUFFIX);

            File enterpriseFeedFile = null;
            String enterpriseFeedFileName = GeneralLedgerConstants.BatchFileSystem.ENTERPRISE_FEED + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
            enterpriseFeedFile = new File(glOriginEntryDirectoryName + File.separator + enterpriseFeedFileName);
            
            PrintStream enterpriseFeedPs = null;
            try {
                enterpriseFeedPs = new PrintStream(enterpriseFeedFile);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("enterpriseFeedFile doesn't exist " + enterpriseFeedFileName);
            }
            
            //OriginEntryGroup originEntryGroup = createNewGroupForFeed(OriginEntrySource.ENTERPRISE_FEED);
            //LOG.info("New group ID created for enterprise feeder service run: " + originEntryGroup.getId());
            LOG.info("New File created for enterprise feeder service run: " + enterpriseFeedFileName);

            File directory = new File(directoryName);
            if (!directory.exists() || !directory.isDirectory()) {
                throw new RuntimeException("Directory doesn't exist and or it's not really a directory " + directoryName);
            }

            File[] doneFiles = directory.listFiles(doneFileFilter);
            reorderDoneFiles(doneFiles);

            for (File doneFile : doneFiles) {
                File dataFile = null;
                File reconFile = null;
                

                EnterpriseFeederStatusAndErrorMessagesWrapper statusAndErrors = new EnterpriseFeederStatusAndErrorMessagesWrapper();
                statusAndErrors.setErrorMessages(new ArrayList<Message>());

                try {
                    dataFile = getDataFile(doneFile);
                    reconFile = getReconFile(doneFile);
                    

                    if (dataFile == null) {
                        LOG.error("Unable to find data file for done file: " + doneFile.getAbsolutePath());
                        statusAndErrors.getErrorMessages().add(new Message("Unable to find data file for done file: " + doneFile.getAbsolutePath(), Message.TYPE_FATAL));
                        statusAndErrors.setStatus(new RequiredFilesMissingStatus());
                    }
                    if (reconFile == null) {
                        LOG.error("Unable to find recon file for done file: " + doneFile.getAbsolutePath());
                        statusAndErrors.getErrorMessages().add(new Message("Unable to find recon file for done file: " + doneFile.getAbsolutePath(), Message.TYPE_FATAL));
                        statusAndErrors.setStatus(new RequiredFilesMissingStatus());
                    }
                    if (dataFile != null && reconFile != null) {
                        LOG.info("Data file: " + dataFile.getAbsolutePath());
                        LOG.info("Reconciliation File: " + reconFile.getAbsolutePath());

                        fileEnterpriseFeederHelperService.feedOnFile(doneFile, dataFile, reconFile, enterpriseFeedPs, processName, reconciliationTableId, statusAndErrors);
                    }
                }
                catch (RuntimeException e) {
                    // we need to be extremely resistant to a file load failing so that it doesn't prevent other files from loading
                    LOG.error("Caught exception when feeding done file: " + doneFile.getAbsolutePath());
                }
                finally {
                    boolean doneFileDeleted = doneFile.delete();
                    if (!doneFileDeleted) {
                        statusAndErrors.getErrorMessages().add(new Message("Unable to delete done file: " + doneFile.getAbsolutePath(), Message.TYPE_FATAL));
                    }
                    if (performNotifications) {
                        enterpriseFeederNotificationService.notifyFileFeedStatus(processName, statusAndErrors.getStatus(), doneFile, dataFile, reconFile, statusAndErrors.getErrorMessages());
                    }
                }
            }
            
            enterpriseFeedPs.close();
            String enterpriseFeedDoneFileName = enterpriseFeedFileName.replace(GeneralLedgerConstants.BatchFileSystem.EXTENSION, GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION);
            File enterpriseFeedDoneFile = new File (glOriginEntryDirectoryName + File.separator + enterpriseFeedDoneFileName);
            if (!enterpriseFeedDoneFile.exists()){
                try {
                    enterpriseFeedDoneFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            }
            //markGroupReady(originEntryGroup);
        }
    }

    /**
     * Reorders the files in case there's a dependency on the order in which files are fed upon. For this implementation, the
     * purpose is to always order files in a way such that unit testing will be predictable.
     * 
     * @param doneFiles
     */
    protected void reorderDoneFiles(File[] doneFiles) {
        // sort the list so that the unit tests will have more predictable results
        Arrays.sort(doneFiles);
    }

    /**
     * Given the doneFile, this method finds the data file corresponding to the done file
     * 
     * @param doneFile
     * @return a File for the data file, or null if the file doesn't exist or is not readable
     */
    protected File getDataFile(File doneFile) {
        String doneFileAbsPath = doneFile.getAbsolutePath();
        if (!doneFileAbsPath.endsWith(DONE_FILE_SUFFIX)) {
            throw new IllegalArgumentException("DOne file name must end with " + DONE_FILE_SUFFIX);
        }
        String dataFileAbsPath = StringUtils.removeEnd(doneFileAbsPath, DONE_FILE_SUFFIX) + DATA_FILE_SUFFIX;
        File dataFile = new File(dataFileAbsPath);
        if (!dataFile.exists() || !dataFile.canRead()) {
            LOG.error("Cannot find/read data file " + dataFileAbsPath);
            return null;
        }
        return dataFile;
    }

    /**
     * Given the doneFile, this method finds the reconciliation file corresponding to the data file
     * 
     * @param doneFile
     * @return a file for the reconciliation data, or null if the file doesn't exist or is not readable
     */
    protected File getReconFile(File doneFile) {
        String doneFileAbsPath = doneFile.getAbsolutePath();
        if (!doneFileAbsPath.endsWith(DONE_FILE_SUFFIX)) {
            throw new IllegalArgumentException("DOne file name must end with " + DONE_FILE_SUFFIX);
        }
        String reconFileAbsPath = StringUtils.removeEnd(doneFileAbsPath, DONE_FILE_SUFFIX) + RECON_FILE_SUFFIX;
        File reconFile = new File(reconFileAbsPath);
        if (!reconFile.exists() || !reconFile.canRead()) {
            LOG.error("Cannot find/read data file " + reconFileAbsPath);
            return null;
        }
        return reconFile;
    }

    /**
     * Creates a new origin entry group to which the origin entries in the files will be added
     * 
     * @param groupSourceCode the origin entry group for the entries from the enterprise feed
     * @param valid
     * @param process
     * @param scrub
     * @return
     */
    protected OriginEntryGroup createNewGroupForFeed(String groupSourceCode) {
        return originEntryGroupService.createGroup(dateTimeService.getCurrentSqlDate(), groupSourceCode, true, false, true);
    }

    /**
     * This method marks that an origin entry group
     * 
     * @param originEntryGroup
     */
    protected void markGroupReady(OriginEntryGroup originEntryGroup) {
        originEntryGroup.setProcess(true);
        originEntryGroupService.save(originEntryGroup);
    }

    /**
     * Gets the directoryName attribute.
     * 
     * @return Returns the directoryName.
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Sets the directoryName attribute value.
     * 
     * @param directoryName The directoryName to set.
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    /**
     * Gets the originEntryGroupService attribute.
     * 
     * @return Returns the originEntryGroupService.
     */
    public OriginEntryGroupService getOriginEntryGroupService() {
        return originEntryGroupService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the fileEnterpriseFeederHelperService attribute.
     * 
     * @return Returns the fileEnterpriseFeederHelperService.
     */
    public FileEnterpriseFeederHelperService getFileEnterpriseFeederHelperService() {
        return fileEnterpriseFeederHelperService;
    }

    /**
     * Sets the fileEnterpriseFeederHelperService attribute value.
     * 
     * @param fileEnterpriseFeederHelperService The fileEnterpriseFeederHelperService to set.
     */
    public void setFileEnterpriseFeederHelperService(FileEnterpriseFeederHelperService fileEnterpriseFeederHelperServiceImpl) {
        this.fileEnterpriseFeederHelperService = fileEnterpriseFeederHelperServiceImpl;
    }

    /**
     * Gets the enterpriseFeederNotificationService attribute.
     * 
     * @return Returns the enterpriseFeederNotificationService.
     */
    public EnterpriseFeederNotificationService getEnterpriseFeederNotificationService() {
        return enterpriseFeederNotificationService;
    }

    /**
     * Sets the enterpriseFeederNotificationService attribute value.
     * 
     * @param enterpriseFeederNotificationService The enterpriseFeederNotificationService to set.
     */
    public void setEnterpriseFeederNotificationService(EnterpriseFeederNotificationService enterpriseFeederNotificationService) {
        this.enterpriseFeederNotificationService = enterpriseFeederNotificationService;
    }

    /**
     * Gets the reconciliationTableId attribute.
     * 
     * @return Returns the reconciliationTableId.
     */
    public String getReconciliationTableId() {
        return reconciliationTableId;
    }

    /**
     * Sets the reconciliationTableId attribute value.
     * 
     * @param reconciliationTableId The reconciliationTableId to set.
     */
    public void setReconciliationTableId(String reconciliationTableId) {
        this.reconciliationTableId = reconciliationTableId;
    }

    public void setGlOriginEntryDirectoryName(String glOriginEntryDirectoryName) {
        this.glOriginEntryDirectoryName = glOriginEntryDirectoryName;
    }
}
