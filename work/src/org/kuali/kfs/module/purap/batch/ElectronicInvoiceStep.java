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
package org.kuali.kfs.module.purap.batch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.batch.service.ElectronicInvoiceHelperService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;

public class ElectronicInvoiceStep extends AbstractStep {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceStep.class);

    private ElectronicInvoiceHelperService electronicInvoiceHelperService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType electronicInvoiceInputFileType;

    public boolean execute(String jobName, 
                           Date jobRunDate) {
        
        File dir = new File(electronicInvoiceInputFileType.getDirectoryPath());
        File[] files = dir.listFiles();
        
        boolean processSuccess = true;
        List<File> processedFiles = new ArrayList();
        for (File file : files){
            processSuccess = electronicInvoiceHelperService.loadElectronicInvoiceFile(file);
            if (processSuccess) {
                processedFiles.add(file);
            }
        }

//        removeDoneFiles(processedFiles);

        return processSuccess;
    }

    /**
     * Clears out associated .done files for the processed data files.
     */
    private void removeDoneFiles(List<File> dataFiles) {
        for (File dataFile : dataFiles) {
            File doneFile;
            try {
                doneFile = new File(StringUtils.substringBeforeLast(dataFile.getCanonicalPath(), ".") + ".done");
                if (doneFile.exists()) {
                    if (!doneFile.delete()){
                        /**
                         * FIXME : is it required to do anything here... but in the gl step, there is no check like this.
                         */
                    }
                }
            }
            catch (IOException e) {
                /**
                 * FIXME: Dont know what to do here
                 */
            }
            
        }
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setElectronicInvoiceInputFileType(BatchInputFileType electronicInvoiceInputFileType) {
        this.electronicInvoiceInputFileType = electronicInvoiceInputFileType;
    }

    public void setElectronicInvoiceHelperService(ElectronicInvoiceHelperService electronicInvoiceHelperService) {
        this.electronicInvoiceHelperService = electronicInvoiceHelperService;
    }
}
