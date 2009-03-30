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
package org.kuali.kfs.module.ld.batch.service.impl;

import static org.kuali.kfs.module.ld.LaborConstants.DestinationNames.ORIGN_ENTRY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.batch.service.VerifyTransaction;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.exception.LoadException;
import org.kuali.kfs.gl.report.Summary;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborConstants.Poster;
import org.kuali.kfs.module.ld.batch.LaborPosterStep;
import org.kuali.kfs.module.ld.batch.service.LaborPosterService;
import org.kuali.kfs.module.ld.batch.service.LaborReportService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.document.validation.impl.TransactionFieldValidator;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.util.LaborLedgerUnitOfWork;
import org.kuali.kfs.module.ld.util.ReportRegistry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.gl.report.TextReportHelper;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Labor Ledger Poster accepts pending entries generated by Labor Ledger e-docs (such as Salary Expense Transfer and Benefit
 * Expense Transfer), and combines them with entries from external systems. It edits the entries for validity. Invalid entries can
 * be marked for Labor Ledger Error Correction process. The Poster writes valid entries to the Labor Ledger Entry table, updates
 * balances in the Labor Ledger Balance table, and summarizes the entries for posting to the General Ledger.
 */
@Transactional
public class LaborPosterServiceImpl implements LaborPosterService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborPosterServiceImpl.class);

    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;

    private LaborReportService laborReportService;
    private DateTimeService dateTimeService;
    private VerifyTransaction laborPosterTransactionValidator;
    private ParameterService parameterService;
    // private CachingDao cachingDao;

    private PostTransaction laborLedgerEntryPoster;
    private PostTransaction laborLedgerBalancePoster;
    private PostTransaction laborGLLedgerEntryPoster;

    private final static int STEP = 1;
    private final static int LINE_INTERVAL = 2;

    private PrintStream POSTER_OUTPUT_GLE_FILE_ps;
    private PrintStream POSTER_OUTPUT_ERR_FILE_ps;


    private OriginEntryGroup validGroup;
    private OriginEntryGroup errorGroup;
    
    private TextReportHelper<Transaction> textReportHelper;
    private PrintStream reportPrintStream;
    
    private Map<String,Integer> reportSummary = new HashMap<String,Integer>();
    int numberOfErrorOriginEntry;

    private String batchFileDirectoryName;

    enum GROUP_TYPE {
        VALID, ERROR
    }

    // private Map<Transaction, List<Message>> laborLedgerEntriesErrorMap = new HashMap<Transaction, List<Message>>();
    private Map<Transaction, List<Message>> errorMap = new HashMap<Transaction, List<Message>>();

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborPosterService#postMainEntries()
     */
    public void postMainEntries() {
        LOG.info("postMainEntries() started");
        
        Date runDate = dateTimeService.getCurrentSqlDate();
        String outputFileName = this.postLaborLedgerEntries(runDate);
        //this.postLaborGLEntries(outputFileName, runDate);

    }

    /**
     * post the qualified origin entries into Labor Ledger tables
     * 
     * @param validGroup the origin entry group that holds the valid transactions
     * @param invalidGroup the origin entry group that holds the invalid transactions
     * @param runDate the data when the process is running
     */
    private String postLaborLedgerEntries(Date runDate) {
        LOG.info("postLaborLedgerEntries() started..........................");
        String reportsDirectory = ReportRegistry.getReportsDirectory();
        numberOfErrorOriginEntry = 0;
        // change file name to FIS

        String postInputFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String postOutFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.POSTER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String postErrFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String reportFileName = reportsDirectory + File.separator + "Labor_Poster_Main_" +  runDate.toString() + ".txt";

        FileReader INPUT_GLE_FILE = null;
        String GLEN_RECORD;
        BufferedReader INPUT_GLE_FILE_br;
        try {
            INPUT_GLE_FILE = new FileReader(postInputFileName);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            POSTER_OUTPUT_GLE_FILE_ps = new PrintStream(postOutFileName);
            POSTER_OUTPUT_ERR_FILE_ps = new PrintStream(postErrFileName);
            reportPrintStream = new PrintStream(reportFileName);


        }
        catch (IOException e) {
            LOG.error("postLaborLedgerEntries cannot open file: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);

        int lineNumber = 0;
        int loadedCount = 0;
        boolean errorsLoading = false;

        int numberOfSelectedOriginEntry = 0;
        LaborOriginEntry laborOriginEntry = new LaborOriginEntry();
        LaborLedgerUnitOfWork laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();
        
        textReportHelper = new TextReportHelper<Transaction>("LABOR POSTER REPORT", reportPrintStream, dateTimeService, laborOriginEntry);
        textReportHelper.writeErrorHeader();
        reportSummary.put(laborLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT, new Integer(0));
        reportSummary.put(laborLedgerBalancePoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT, new Integer(0));
        reportSummary.put(laborLedgerBalancePoster.getDestinationName() + "," + KFSConstants.OperationType.UPDATE, new Integer(0));
        reportSummary.put(laborGLLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT, new Integer(0));

        try {
            String currentLine = INPUT_GLE_FILE_br.readLine();

            while (currentLine != null) {
                try {
                    lineNumber++;
                    if (!StringUtils.isEmpty(currentLine) && !StringUtils.isBlank(currentLine.trim())) {
                        try {
                            laborOriginEntry = new LaborOriginEntry();
                            laborOriginEntry.setFromTextFileForBatch(currentLine, lineNumber);

                        }
                        catch (LoadException e) {
                            errorsLoading = true;
                        }

                        loadedCount++;
                        if (loadedCount % 1000 == 0) {
                            LOG.info(loadedCount + " " + laborOriginEntry.toString());
                        }
                        if (postSingleEntryIntoLaborLedger(laborOriginEntry, reportSummary,  runDate)) {
                            
                            summarizeLaborGLEntries(laborOriginEntry, laborLedgerUnitOfWork, runDate, lineNumber);
                            
                            
                            numberOfSelectedOriginEntry++;
                            laborOriginEntry = null;
                        }


                    }
                    currentLine = INPUT_GLE_FILE_br.readLine();

                }
                catch (RuntimeException ioe) {
                    // catch here again, it should be from postSingleEntryIntoLaborLedger
                    LOG.error("postLaborLedgerEntries stopped due to: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);
                    throw new RuntimeException("Unable to execute: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);

                }
            }

            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
            POSTER_OUTPUT_GLE_FILE_ps.close();
            POSTER_OUTPUT_ERR_FILE_ps.close();
            // cachingDao.commit();

            textReportHelper.writeStatisticsHeader();
            reportPrintStream.printf("                                  SEQUENTIAL RECORDS READ                    %,9d\n", lineNumber);
            reportPrintStream.printf("                                  LLEN RECORDS INSERTED (LD_LDGR_ENTR_T)     %,9d\n", reportSummary.get(laborLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT));
            reportPrintStream.printf("                                  LLBL RECORDS INSERTED (LD_LDGR_BAL_T)      %,9d\n", reportSummary.get(laborLedgerBalancePoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT));
            reportPrintStream.printf("                                  LLBL RECORDS UPDATED  (LD_LDGR_BAL_T)      %,9d\n", reportSummary.get(laborLedgerBalancePoster.getDestinationName() + "," + KFSConstants.OperationType.UPDATE));
            reportPrintStream.printf("                                  LLGL RECORDS INSERTED (LD_LBR_GL_ENTRY_T)  %,9d\n", reportSummary.get(laborGLLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT));
            reportPrintStream.printf("                                  WARNING RECORDS WRITTEN                    %,9d\n", numberOfErrorOriginEntry);
            reportPrintStream.close();
            
            // shawn - need to change to use file??
            // laborReportService.generateErrorTransactionListing(invalidGroup, ReportRegistry.LABOR_POSTER_ERROR, reportsDirectory,
            // runDate);

        }
        catch (IOException ioe) {
            LOG.error("postLaborLedgerEntries stopped due to: " + ioe.getMessage(), ioe);
            throw new RuntimeException("Unable to execute: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);
        }


        return postOutFileName;
    }

    /**
     * post the given entry into the labor ledger tables if the entry is qualified; otherwise report error
     * 
     * @param originEntry the given origin entry, a transaction
     * @param reportSummary the report summary object that need to be update when a transaction is posted
     * @param errorMap a map that holds the invalid transaction and corresponding error message
     * @param validGroup the origin entry group that holds the valid transactions
     * @param invalidGroup the origin entry group that holds the invalid transactions
     * @param runDate the data when the process is running
     * @return true if the given transaction is posted into ledger tables; otherwise, return false
     */
    // private boolean postSingleEntryIntoLaborLedger(LaborOriginEntry originEntry, List<Summary> reportSummary, Map<Transaction,
    // List<Message>> errorMap, OriginEntryGroup validGroup, OriginEntryGroup invalidGroup, Date runDate) {
    private boolean postSingleEntryIntoLaborLedger(LaborOriginEntry originEntry,  Map<String,Integer> reportSummary, Date runDate) {
        // reject the entry that is not postable
        if (!isPostableEntry(originEntry)) {
            return false;
        }

        // reject the invalid entry so that it can be available for error correction
        List<Message> errors = new ArrayList();
        try {
            errors = this.validateEntry(originEntry);
        }
        catch (Exception e) {
            errors.add(new Message(e.toString() + " occurred for this record.", Message.TYPE_FATAL));
        }

        if (errors != null && !errors.isEmpty()) {
            textReportHelper.writeErrors(originEntry, errors);
            numberOfErrorOriginEntry += errors.size();
            writeErrorEntry(originEntry);
            return false;
        }

         String operationOnLedgerEntry = postAsLedgerEntry(originEntry, runDate);
         addReporting(reportSummary, laborLedgerEntryPoster.getDestinationName(), operationOnLedgerEntry);
        
         String operationOnLedgerBalance = updateLedgerBalance(originEntry, runDate);
         addReporting(reportSummary, laborLedgerBalancePoster.getDestinationName(), operationOnLedgerBalance);

        return true;
    }

    /**
     * determine if the given origin entry need to be posted
     * 
     * @param originEntry the given origin entry, a transcation
     * @return true if the transaction is eligible for poster process; otherwise; return false
     */
    private boolean isPostableEntry(LaborOriginEntry originEntry) {
        if (TransactionFieldValidator.checkZeroTotalAmount(originEntry) != null) {
            return false;
        }
        else if (TransactionFieldValidator.checkPostableObjectCode(originEntry, this.getObjectsNotProcessed()) != null) {
            return false;
        }
        return true;
    }

    /**
     * validate the given entry, and generate an error list if the entry cannot meet the business rules
     * 
     * @param originEntry the given origin entry, a transcation
     * @return error message list. If the given transaction is invalid, the list has message(s); otherwise, it is empty
     */
    private List<Message> validateEntry(LaborOriginEntry originEntry) {
        return laborPosterTransactionValidator.verifyTransaction(originEntry);
    }

    /**
     * post the processed entry into the approperiate group, either valid or invalid group
     * 
     * @param originEntry the given origin entry, a transaction
     * @param entryGroup the origin entry group that the transaction will be assigned
     * @param postDate the data when the transaction is processes
     */
    private void postAsProcessedOriginEntry(String line, GROUP_TYPE groupCode, Date postDate) {
        try {
            createOutputEntry(line, groupCode);
        }
        catch (IOException ioe) {
            LOG.error("postAsProcessedOriginEntry stopped due to: " + ioe.getMessage(), ioe);
            throw new RuntimeException("Unable to execute: " + ioe.getMessage(), ioe);
        }

    }

    /**
     * post the given entry to the labor entry table
     * 
     * @param originEntry the given origin entry, a transaction
     * @param postDate the data when the transaction is processes return the operation type of the process
     */
    private String postAsLedgerEntry(LaborOriginEntry originEntry, Date postDate) {
        return laborLedgerEntryPoster.post(originEntry, 0, postDate);
    }

    /**
     * update the labor ledger balance for the given entry
     * 
     * @param originEntry the given origin entry, a transaction
     * @param postDate the data when the transaction is processes return the operation type of the process
     */
    private String updateLedgerBalance(LaborOriginEntry originEntry, Date postDate) {
        return laborLedgerBalancePoster.post(originEntry, 0, postDate);
    }

    /**
     * post the valid origin entries in the given group into General Ledger
     * 
     * @param validGroup the origin entry group that contains the valid transactions determined in the Labor Poster
     * @param runDate the data when the process is running
     */
    
    //We don't need this method anymore because make LaborPoster as 1 step
//    private void postLaborGLEntries(String outputFileName, Date runDate) {
//        LOG.info("postLaborGLEntries() started");
//
//        String reportsDirectory = ReportRegistry.getReportsDirectory();
//        reportSummary = this.buildReportSummaryForLaborGLPosting(reportSummary);
//
//        // TODO: check and make a code here from getConsolidatedEntryCollectionByGroup
//        // Collection<LaborOriginEntry> entries = laborOriginEntryService.getConsolidatedEntryCollectionByGroup(validGroup);
//        // int numberOfOriginEntries = laborOriginEntryService.getCountOfEntriesInSingleGroup(validGroup);
//        int numberOfOriginEntries = 0;
//        int numberOfSelectedOriginEntry = 0;
//
//        Collection<LaborOriginEntry> entryCollection = new ArrayList();
//        List<Message> errors = new ArrayList();
//
//        FileReader GLE_FILE;
//        try {
//            GLE_FILE = new FileReader(outputFileName);
//        }
//        catch (FileNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//
//        BufferedReader GLE_FILE_br = new BufferedReader(GLE_FILE);
//        int lineNumber = 0;
//        LaborOriginEntry laborOriginEntry = new LaborOriginEntry();
//        boolean errorsLoading = false;
//        int loadedCount = 0;
//        LaborLedgerUnitOfWork laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();
//
//        try {
//            String currentLine = GLE_FILE_br.readLine();
//            while (currentLine != null) {
//                lineNumber++;
//                if (!StringUtils.isEmpty(currentLine) && !StringUtils.isBlank(currentLine.trim())) {
//                    try {
//                        laborOriginEntry = new LaborOriginEntry();
//                        laborOriginEntry.setFromTextFileForBatch(currentLine, lineNumber);
//                    }
//                    catch (LoadException e) {
//                        errorsLoading = true;
//                    }
//
//
//                    // shawn - setup below two fields before making consolidated list
//                    laborOriginEntry.setTransactionLedgerEntryDescription(getTransactionDescription(laborOriginEntry));
//                    laborOriginEntry.setTransactionEncumbranceUpdateCode(this.getEncumbranceUpdateCode(laborOriginEntry));
//
//                    if (laborLedgerUnitOfWork.canContain(laborOriginEntry)) {
//                        laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry);
//                    }
//                    else {
//                        // laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry,
//                        // LaborConstants.consolidationAttributesOfOriginEntry());
//                        laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);
//                        entryCollection.add(laborLedgerUnitOfWork.getWorkingEntry());
//                    }
//
//                    loadedCount++;
//                    if (loadedCount % 1000 == 0) {
//                        LOG.info(loadedCount + " " + laborOriginEntry.toString());
//                    }
//                }
//                currentLine = GLE_FILE_br.readLine();
//            }
//
//            GLE_FILE_br.close();
//            GLE_FILE.close();
//        }
//        catch (IOException e) {
//            // FIXME: do whatever should be done here
//            LOG.error("postLaborGLEntries stopped due to: " + e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//
//        for (LaborOriginEntry originEntryForGL : entryCollection) {
//            try {
//                errors = this.isPostableForLaborGLEntry(originEntryForGL);
//                if (errors.isEmpty()) {
//                    String operationType = laborGLLedgerEntryPoster.post(originEntryForGL, 0, runDate);
//                    Summary.updateReportSummary(reportSummary, laborGLLedgerEntryPoster.getDestinationName(), operationType, STEP, 0);
//                    numberOfSelectedOriginEntry++;
//                }
//
//            }
//            catch (RuntimeException ioe) {
//                // catch here again, it should be from postSingleEntryIntoLaborLedger
//                LOG.error("postLaborGLEntries stopped due to: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);
//                throw new RuntimeException("Unable to execute: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);
//            }
//        }
//
//        numberOfOriginEntries = lineNumber;
//        int numberOfBypassedOriginEntry = numberOfOriginEntries - numberOfSelectedOriginEntry;
//
//        Summary.updateReportSummary(reportSummary, ORIGN_ENTRY, KFSConstants.OperationType.BYPASS, numberOfBypassedOriginEntry, 0);
//        // Summary.updateReportSummary(reportSummary, ORIGN_ENTRY, KFSConstants.OperationType.REPORT_ERROR, errorMap.size(), 0);
//        laborReportService.generateStatisticsReport(reportSummary, errorMap, ReportRegistry.LABOR_POSTER_GL_SUMMARY, reportsDirectory, runDate);
//    }

    /**
     * determine if the given origin entry can be posted back to Labor GL entry
     * 
     * @param originEntry the given origin entry, atransaction
     * @return a message list. The list has message(s) if the given origin entry cannot be posted back to Labor GL entry; otherwise,
     *         it is empty
     */
    private List<Message> isPostableForLaborGLEntry(LaborOriginEntry originEntry) {
        List<Message> errors = new ArrayList<Message>();
        MessageBuilder.addMessageIntoList(errors, TransactionFieldValidator.checkPostablePeridCode(originEntry, getPeriodCodesNotProcessed()));
        MessageBuilder.addMessageIntoList(errors, TransactionFieldValidator.checkPostableBalanceTypeCode(originEntry, getBalanceTypesNotProcessed()));
        MessageBuilder.addMessageIntoList(errors, TransactionFieldValidator.checkZeroTotalAmount(originEntry));
        return errors;
    }

    /**
     * build a report summary list for labor ledger posting
     * 
     * @return a report summary list for labor ledger posting
     */
    private List<Summary> buildReportSummaryForLaborLedgerPosting(List<Summary> reportSummary) {

        String destination = laborLedgerEntryPoster.getDestinationName();
        reportSummary.add(new Summary(reportSummary.size() + LINE_INTERVAL, "", 0));
        reportSummary.addAll(Summary.buildDefualtReportSummary(destination, reportSummary.size() + LINE_INTERVAL));

        destination = laborLedgerBalancePoster.getDestinationName();
        reportSummary.add(new Summary(reportSummary.size() + LINE_INTERVAL, "", 0));
        reportSummary.addAll(Summary.buildDefualtReportSummary(destination, reportSummary.size() + LINE_INTERVAL));

        return reportSummary;
    }

    /**
     * build a report summary list for labor general ledger posting
     * 
     * @return a report summary list for labor general ledger posting
     */
    private List<Summary> buildReportSummaryForLaborGLPosting(List<Summary> reportSummary) {

        String destination = laborGLLedgerEntryPoster.getDestinationName();
        reportSummary.add(new Summary(reportSummary.size() + LINE_INTERVAL, "", 0));
        Summary.updateReportSummary(reportSummary, destination, KFSConstants.OperationType.INSERT, 0, reportSummary.size() + LINE_INTERVAL);

        return reportSummary;
    }


    /**
     * Get a set of the balance type codes that are bypassed by Labor Poster
     * 
     * @return a set of the balance type codes that are bypassed by Labor Poster
     */
    public List<String> getBalanceTypesNotProcessed() {
        return parameterService.getParameterValues(LaborPosterStep.class, Poster.BALANCE_TYPES_NOT_PROCESSED);
    }

    /**
     * Get a set of the object codes that are bypassed by Labor Poster
     * 
     * @return a set of the object codes that are bypassed by Labor Poster
     */
    public List<String> getObjectsNotProcessed() {
        return parameterService.getParameterValues(LaborPosterStep.class, Poster.OBJECT_CODES_NOT_PROCESSED);
    }

    /**
     * Get a set of the fiscal period codes that are bypassed by Labor Poster
     * 
     * @return a set of the fiscal period codes that are bypassed by Labor Poster
     */
    public List<String> getPeriodCodesNotProcessed() {
        return parameterService.getParameterValues(LaborPosterStep.class, Poster.PERIOD_CODES_NOT_PROCESSED);
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
     * Sets the laborLedgerBalancePoster attribute value.
     * 
     * @param laborLedgerBalancePoster The laborLedgerBalancePoster to set.
     */
    public void setLaborLedgerBalancePoster(PostTransaction laborLedgerBalancePoster) {
        this.laborLedgerBalancePoster = laborLedgerBalancePoster;
    }

    /**
     * Sets the laborGLLedgerEntryPoster attribute value.
     * 
     * @param laborGLLedgerEntryPoster The laborGLLedgerEntryPoster to set.
     */
    public void setLaborGLLedgerEntryPoster(PostTransaction laborGLLedgerEntryPoster) {
        this.laborGLLedgerEntryPoster = laborGLLedgerEntryPoster;
    }

    /**
     * Sets the laborLedgerEntryPoster attribute value.
     * 
     * @param laborLedgerEntryPoster The laborLedgerEntryPoster to set.
     */
    public void setLaborLedgerEntryPoster(PostTransaction laborLedgerEntryPoster) {
        this.laborLedgerEntryPoster = laborLedgerEntryPoster;
    }

    /**
     * Sets the laborOriginEntryService attribute value.
     * 
     * @param laborOriginEntryService The laborOriginEntryService to set.
     */
    public void setLaborOriginEntryService(LaborOriginEntryService laborOriginEntryService) {
        this.laborOriginEntryService = laborOriginEntryService;
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
     * Sets the laborReportService attribute value.
     * 
     * @param laborReportService The laborReportService to set.
     */
    public void setLaborReportService(LaborReportService laborReportService) {
        this.laborReportService = laborReportService;
    }

    /**
     * Sets the laborPosterTransactionValidator attribute value.
     * 
     * @param laborPosterTransactionValidator The laborPosterTransactionValidator to set.
     */
    public void setLaborPosterTransactionValidator(VerifyTransaction laborPosterTransactionValidator) {
        this.laborPosterTransactionValidator = laborPosterTransactionValidator;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    private void createOutputEntry(String entry, GROUP_TYPE groupCode) throws IOException {
        OriginEntryGroup group = null;
        PrintStream ps = null;
        try {
            switch (groupCode) {
                case VALID:
                    group = validGroup;
                    ps = POSTER_OUTPUT_GLE_FILE_ps;
                    break;
                case ERROR:
                    group = errorGroup;
                    ps = POSTER_OUTPUT_ERR_FILE_ps;
                    break;
            }
            ps.printf("%s\n", entry);

        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * @return the encumbrance update code
     */
    private String getEncumbranceUpdateCode(LaborOriginEntry laborOriginEntry) {
        String encumbranceUpdateCode = laborOriginEntry.getTransactionEncumbranceUpdateCode();
        if (KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(encumbranceUpdateCode) || KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(encumbranceUpdateCode)) {
            return encumbranceUpdateCode;
        }
        return null;
    }

    /**
     * @return the transaction description
     */
    private String getTransactionDescription(LaborOriginEntry laborOriginEntry) {
        String documentTypeCode = laborOriginEntry.getFinancialDocumentTypeCode();
        String description = getDescriptionMap().get(documentTypeCode);
        description = StringUtils.isNotEmpty(description) ? description : laborOriginEntry.getTransactionLedgerEntryDescription();

        // make sure the length of the description cannot excess the specified maximum
        int transactionDescriptionMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(Entry.class, KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC).intValue();
        if (StringUtils.isNotEmpty(description) && description.length() > transactionDescriptionMaxLength) {
            description = StringUtils.left(description, transactionDescriptionMaxLength);
        }

        return description;
    }

    /**
     * @return the description dictionary that can be used to look up approperite description
     */
    public static Map<String, String> getDescriptionMap() {
        Map<String, String> descriptionMap = new HashMap<String, String>();

        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.NORMAL_PAY, "NORMAL PAYROLL ACTIVITY");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.ACCRUALS_REVERSAL, "PAYROLL ACCRUAL REVERSAL");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.ACCRUALS, "PAYROLL ACCRUALS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.CHECK_CANCELLATION, "PAYROLL CHECK CANCELLATIONS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.ENCUMBRANCE, "PAYROLL ENCUMBRANCES");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.EXPENSE_TRANSFER_ET, "PAYROLL EXPENSE TRANSFERS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.EXPENSE_TRANSFER_SACH, "PAYROLL EXPENSE TRANSFERS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.HAND_DRAWN_CHECK, "PAYROLL HAND DRAWN CHECK PAYMENTS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.OVERPAYMENT, "PAYROLL OVERPAYMENT COLLECTIONS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.RETROACTIVE_ADJUSTMENT, "PAYROLL RETROACTIVE ADJUSTMENTS");

        // Shawn - for IU
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.EXPENSE_TRANSFER_BT, "PAYROLL EXPENSE TRANSFERS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.EXPENSE_TRANSFER_ST, "PAYROLL EXPENSE TRANSFERS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.EXPENSE_TRANSFER_YEBT, "PAYROLL EXPENSE TRANSFERS");
        descriptionMap.put(LaborConstants.PayrollDocumentTypeCode.EXPENSE_TRANSFER_YEST, "PAYROLL EXPENSE TRANSFERS");

        return descriptionMap;
    }

    // public void setCachingDao(CachingDao cachingDao) {
    // this.cachingDao = cachingDao;
    // }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
    
   
    /**
     * summary the valid origin entries for the General Ledger
     * 
     * @param laborOriginEntry the current entry to check for summarization
     * @param laborLedgerUnitOfWork the current (in process) summarized entry for the GL
     * @param runDate the data when the process is running
     * @param lineNumber the line in the input file (used for error message only)
     */
    private void summarizeLaborGLEntries(LaborOriginEntry laborOriginEntry, LaborLedgerUnitOfWork laborLedgerUnitOfWork, Date runDate, int lineNumber) {
        //shawn - setup below two fields before making consolidated list
        laborOriginEntry.setTransactionLedgerEntryDescription(getTransactionDescription(laborOriginEntry));
        laborOriginEntry.setTransactionEncumbranceUpdateCode(this.getEncumbranceUpdateCode(laborOriginEntry));

        if (laborLedgerUnitOfWork.canContain(laborOriginEntry)) {
            laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry);
        } else {
            writeLaborGLEntry(laborLedgerUnitOfWork, runDate, lineNumber);
            laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);
        }
    }


    private void writeLaborGLEntry(LaborLedgerUnitOfWork laborLedgerUnitOfWork, Date runDate, int lineNumber) {
        List<Message> errors = new ArrayList<Message>();
        
        try{
            errors = this.isPostableForLaborGLEntry(laborLedgerUnitOfWork.getWorkingEntry());
            if (errors.isEmpty()) {
                String operationType = laborGLLedgerEntryPoster.post(laborLedgerUnitOfWork.getWorkingEntry(), 0, runDate);
                addReporting(reportSummary, laborGLLedgerEntryPoster.getDestinationName(), operationType);
            }
        } catch (RuntimeException ioe) {
            //catch here again, it should be from postSingleEntryIntoLaborLedger
            LOG.error("postLaborGLEntries stopped due to: " + ioe.getMessage() + " on line number : " + lineNumber, ioe);
            throw new RuntimeException("Unable to execute: " + ioe.getMessage() + " on line number : " + lineNumber , ioe);
        } 
    }
    
    private void addReporting(Map<String,Integer> reporting, String destination, String operation) {
        String key = destination + "," + operation;
        if (reporting.containsKey(key)) {
            Integer c = (Integer) reporting.get(key);
            reporting.put(key, new Integer(c.intValue() + 1));
        }
        else {
            reporting.put(key, new Integer(1));
        }
    }
    
    private void writeErrorEntry(LaborOriginEntry originEntry) {
        try {
            POSTER_OUTPUT_ERR_FILE_ps.printf("%s\n", originEntry.getLine());
        } catch (Exception e) {
            LOG.error("postAsProcessedOriginEntry stopped due to: " + e.getMessage(), e);
            throw new RuntimeException("Unable to execute: " + e.getMessage(), e);
        }
    }

}
