/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
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
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.report.TransactionListingReport;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborConstants.Poster;
import org.kuali.kfs.module.ld.batch.LaborPosterStep;
import org.kuali.kfs.module.ld.batch.service.LaborPosterService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.document.validation.impl.TransactionFieldValidator;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.service.LaborTransactionDescriptionService;
import org.kuali.kfs.module.ld.util.LaborLedgerUnitOfWork;
import org.kuali.kfs.module.ld.util.LaborOriginEntryFileIterator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;
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
    private LaborTransactionDescriptionService laborTransactionDescriptionService;

    private ReportWriterService reportWriterService;
    private ReportWriterService errorListingReportWriterService;
    private ReportWriterService ledgerSummaryReportWriterService;
    private ReportWriterService laborGlEntryStatisticsReportWriterService;

    private DateTimeService dateTimeService;
    private VerifyTransaction laborPosterTransactionValidator;
    private ParameterService parameterService;

    private PostTransaction laborLedgerEntryPoster;
    private PostTransaction laborLedgerBalancePoster;
    private PostTransaction laborGLLedgerEntryPoster;

    private int numberOfErrorOriginEntry;

    private String batchFileDirectoryName;
    private PrintStream POSTER_OUTPUT_ERR_FILE_ps;
    
    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborPosterService#postMainEntries()
     */
    public void postMainEntries() {
        LOG.debug("postMainEntries() started");

        Date runDate = dateTimeService.getCurrentSqlDate();
        this.postLaborLedgerEntries(runDate);
    }

    /**
     * post the qualified origin entries into Labor Ledger tables
     * 
     * @param validGroup the origin entry group that holds the valid transactions
     * @param invalidGroup the origin entry group that holds the invalid transactions
     * @param runDate the data when the process is running
     */
    protected void postLaborLedgerEntries(Date runDate) {
        LOG.debug("postLaborLedgerEntries() started..........................");
        numberOfErrorOriginEntry = 0;
        // change file name to FIS

        String postInputFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String postErrFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        FileReader INPUT_GLE_FILE = null;
        try {
            INPUT_GLE_FILE = new FileReader(postInputFileName);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            POSTER_OUTPUT_ERR_FILE_ps = new PrintStream(postErrFileName);
        }
        catch (IOException e) {
            LOG.error("postLaborLedgerEntries cannot open file: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        int lineNumber = 0;
        int loadedCount = 0;

        int numberOfSelectedOriginEntry = 0;
        LaborLedgerUnitOfWork laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();

        LedgerSummaryReport ledgerSummaryReport = new LedgerSummaryReport();

        Map<String, Integer> reportSummary = this.constructPosterReportSummary();
        Map<String, Integer> glEntryReportSummary = this.constructGlEntryReportSummary();

        try {
            BufferedReader INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
            String currentLine = INPUT_GLE_FILE_br.readLine();

            while (currentLine != null) {
                LaborOriginEntry laborOriginEntry = null;
                
                try {
                    lineNumber++;
                    if (!StringUtils.isEmpty(currentLine) && !StringUtils.isBlank(currentLine.trim())) {
                        laborOriginEntry = new LaborOriginEntry();
                    
                        // checking parsing process and stop poster when it has errors.
                        List<Message> parsingError = new ArrayList<Message>();
                        parsingError = laborOriginEntry.setFromTextFileForBatch(currentLine, lineNumber);
                        if (parsingError.size() > 0) {
                            throw new RuntimeException("Exception happened from parsing process");
                        }

                        loadedCount++;
                        if (loadedCount % 1000 == 0) {
                            LOG.info(loadedCount + " " + laborOriginEntry.toString());
                        }

                        boolean isPostable = this.postSingleEntryIntoLaborLedger(laborOriginEntry, reportSummary, runDate, currentLine);
                        if (isPostable) {
                            this.updateReportSummary(glEntryReportSummary, ORIGN_ENTRY, KFSConstants.OperationType.READ);
                            this.writeLaborGLEntry(laborOriginEntry, laborLedgerUnitOfWork, runDate, lineNumber, glEntryReportSummary);

                            ledgerSummaryReport.summarizeEntry(laborOriginEntry);

                            numberOfSelectedOriginEntry++;
                            laborOriginEntry = null;
                        }
                    }

                    currentLine = INPUT_GLE_FILE_br.readLine();
                }
                catch (RuntimeException re) {
                    // catch here again, it should be from postSingleEntryIntoLaborLedger
                    LOG.error("postLaborLedgerEntries stopped due to: " + re.getMessage() + " on line number : " + loadedCount, re);
                    LOG.error("laborOriginEntry failure occured on: " + laborOriginEntry == null ? null : laborOriginEntry.toString());
                    throw new RuntimeException("Unable to execute: " + re.getMessage() + " on line number : " + loadedCount, re);
                }
            }

            this.writeLaborGLEntry(null, laborLedgerUnitOfWork, runDate, lineNumber, glEntryReportSummary);

            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
            POSTER_OUTPUT_ERR_FILE_ps.close();

            this.fillPosterReportWriter(lineNumber, reportSummary, glEntryReportSummary);
            this.fillGlEntryReportWriter(glEntryReportSummary);

            // Generate Error Listing Report
            ledgerSummaryReport.writeReport(ledgerSummaryReportWriterService);
            new TransactionListingReport().generateReport(errorListingReportWriterService, new LaborOriginEntryFileIterator(new File(postErrFileName)));
        }
        catch (IOException ioe) {
            LOG.error("postLaborLedgerEntries stopped due to: " + ioe.getMessage(), ioe);
            throw new RuntimeException("Unable to execute: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);
        }
    }

    /**
     * post the given entry into the labor ledger tables if the entry is qualified; otherwise report error
     * 
     * @param originEntry the given origin entry, a transaction
     * @param reportSummary the report summary object that need to be update when a transaction is posted
     * @param runDate the data when the process is running
     * @return true if the given transaction is posted into ledger tables; otherwise, return false
     */
    protected boolean postSingleEntryIntoLaborLedger(LaborOriginEntry originEntry, Map<String, Integer> reportSummary, Date runDate, String line) {
        // reject the invalid entry so that it can be available for error correction
        List<Message> errors = new ArrayList<Message>();
        try {
            errors = this.validateEntry(originEntry);
        }
        catch (Exception e) {
            errors.add(new Message(e.toString() + " occurred for this record.", Message.TYPE_FATAL));
        }

        if (errors != null && !errors.isEmpty()) {
            reportWriterService.writeError(originEntry, errors);
            numberOfErrorOriginEntry += errors.size();
            writeErrorEntry(line);
            return false;
        }

        String operationOnLedgerEntry = postAsLedgerEntry(originEntry, runDate);
        updateReportSummary(reportSummary, laborLedgerEntryPoster.getDestinationName(), operationOnLedgerEntry);

        String operationOnLedgerBalance = updateLedgerBalance(originEntry, runDate);
        updateReportSummary(reportSummary, laborLedgerBalancePoster.getDestinationName(), operationOnLedgerBalance);

        return true;
    }

    /**
     * validate the given entry, and generate an error list if the entry cannot meet the business rules
     * 
     * @param originEntry the given origin entry, a transcation
     * @return error message list. If the given transaction is invalid, the list has message(s); otherwise, it is empty
     */
    protected List<Message> validateEntry(LaborOriginEntry originEntry) {
        return laborPosterTransactionValidator.verifyTransaction(originEntry);
    }

    /**
     * post the given entry to the labor entry table
     * 
     * @param originEntry the given origin entry, a transaction
     * @param postDate the data when the transaction is processes return the operation type of the process
     */
    protected String postAsLedgerEntry(LaborOriginEntry originEntry, Date postDate) {
        return laborLedgerEntryPoster.post(originEntry, 0, postDate, null);
    }

    /**
     * update the labor ledger balance for the given entry
     * 
     * @param originEntry the given origin entry, a transaction
     * @param postDate the data when the transaction is processes return the operation type of the process
     */
    protected String updateLedgerBalance(LaborOriginEntry originEntry, Date postDate) {
        return laborLedgerBalancePoster.post(originEntry, 0, postDate, null);
    }

    /**
     * determine if the given origin entry can be posted back to Labor GL entry
     * 
     * @param originEntry the given origin entry, atransaction
     * @return a message list. The list has message(s) if the given origin entry cannot be posted back to Labor GL entry; otherwise,
     *         it is empty
     */
    protected List<Message> isPostableForLaborGLEntry(LaborOriginEntry originEntry) {
        List<Message> errors = new ArrayList<Message>();
        MessageBuilder.addMessageIntoList(errors, TransactionFieldValidator.checkPostablePeridCode(originEntry, getPeriodCodesNotProcessed()));
        MessageBuilder.addMessageIntoList(errors, TransactionFieldValidator.checkPostableBalanceTypeCode(originEntry, getBalanceTypesNotProcessed()));
        MessageBuilder.addMessageIntoList(errors, TransactionFieldValidator.checkZeroTotalAmount(originEntry));
        return errors;
    }

    // construct a poster report summary object
    protected void fillPosterReportWriter(int lineNumber, Map<String, Integer> reportSummary, Map<String, Integer> glEntryReportSummary) {
        reportWriterService.writeStatisticLine("SEQUENTIAL RECORDS READ                    %,9d", lineNumber);
        reportWriterService.writeStatisticLine("LLEN RECORDS INSERTED (LD_LDGR_ENTR_T)     %,9d", reportSummary.get(laborLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT));
        reportWriterService.writeStatisticLine("LLBL RECORDS INSERTED (LD_LDGR_BAL_T)      %,9d", reportSummary.get(laborLedgerBalancePoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT));
        reportWriterService.writeStatisticLine("LLBL RECORDS UPDATED  (LD_LDGR_BAL_T)      %,9d", reportSummary.get(laborLedgerBalancePoster.getDestinationName() + "," + KFSConstants.OperationType.UPDATE));
        reportWriterService.writeStatisticLine("LLGL RECORDS INSERTED (LD_LBR_GL_ENTRY_T)  %,9d", glEntryReportSummary.get(laborGLLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT));
        reportWriterService.writeStatisticLine("WARNING RECORDS WRITTEN                    %,9d", numberOfErrorOriginEntry);
    }

    // fill the poster report writer with the collected data
    protected Map<String, Integer> constructPosterReportSummary() {
        Map<String, Integer> reportSummary = new HashMap<String, Integer>();
        reportSummary.put(laborLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT, 0);
        reportSummary.put(laborLedgerBalancePoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT, 0);
        reportSummary.put(laborLedgerBalancePoster.getDestinationName() + "," + KFSConstants.OperationType.UPDATE, 0);
        reportSummary.put(laborGLLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT, 0);
        return reportSummary;
    }

    // construct a gl entry report summary object
    protected Map<String, Integer> constructGlEntryReportSummary() {
        Map<String, Integer> glEntryReportSummary = new HashMap<String, Integer>();
        glEntryReportSummary.put(ORIGN_ENTRY + "," + KFSConstants.OperationType.READ, 0);
        glEntryReportSummary.put(ORIGN_ENTRY + "," + KFSConstants.OperationType.BYPASS, 0);
        glEntryReportSummary.put(ORIGN_ENTRY + "," + KFSConstants.OperationType.SELECT, 0);
        glEntryReportSummary.put(ORIGN_ENTRY + "," + KFSConstants.OperationType.REPORT_ERROR, 0);
        glEntryReportSummary.put(laborGLLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT, 0);

        return glEntryReportSummary;
    }

    // fill the gl entry report writer with the collected data
    protected void fillGlEntryReportWriter(Map<String, Integer> glEntryReportSummary) {
        laborGlEntryStatisticsReportWriterService.writeStatisticLine("NUMBER OF RECORDS READ              %,9d", glEntryReportSummary.get(ORIGN_ENTRY + "," + KFSConstants.OperationType.READ));
        laborGlEntryStatisticsReportWriterService.writeStatisticLine("NUMBER OF RECORDS BYPASSED          %,9d", glEntryReportSummary.get(ORIGN_ENTRY + "," + KFSConstants.OperationType.BYPASS));
        laborGlEntryStatisticsReportWriterService.writeStatisticLine("NUMBER OF RECORDS SELECTED          %,9d", glEntryReportSummary.get(ORIGN_ENTRY + "," + KFSConstants.OperationType.SELECT));
        laborGlEntryStatisticsReportWriterService.writeStatisticLine("NUMBER OF RECORDS IN ERROR          %,9d", glEntryReportSummary.get(ORIGN_ENTRY + "," + KFSConstants.OperationType.REPORT_ERROR));
        laborGlEntryStatisticsReportWriterService.writeStatisticLine("NUMBER OF RECORDS INSERTED          %,9d", glEntryReportSummary.get(laborGLLedgerEntryPoster.getDestinationName() + "," + KFSConstants.OperationType.INSERT));
    }

    /**
     * summary the valid origin entries for the General Ledger
     * 
     * @param laborOriginEntry the current entry to check for summarization
     * @param laborLedgerUnitOfWork the current (in process) summarized entry for the GL
     * @param runDate the data when the process is running
     * @param lineNumber the line in the input file (used for error message only)
     */
    protected LaborOriginEntry summarizeLaborGLEntries(LaborOriginEntry laborOriginEntry, LaborLedgerUnitOfWork laborLedgerUnitOfWork, Date runDate, int lineNumber, Map<String, Integer> glEntryReportSummary) {
        // KFSMI-5308: Description update moved here due to requirement for this to happen before consolidation
        if(ObjectUtils.isNotNull(laborOriginEntry)) {
            String description = laborTransactionDescriptionService.getTransactionDescription(laborOriginEntry);
            if(StringUtils.isNotEmpty(description)) {
                laborOriginEntry.setTransactionLedgerEntryDescription(description);
            }
        }
        
        LaborOriginEntry summarizedEntry = null;
        if (laborLedgerUnitOfWork.canContain(laborOriginEntry)) {
            laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry);
            updateReportSummary(glEntryReportSummary, ORIGN_ENTRY, KFSConstants.OperationType.SELECT);
        }
        else {
            summarizedEntry = laborLedgerUnitOfWork.getWorkingEntry();
            laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);
        }

        return summarizedEntry;
    }

    protected void writeLaborGLEntry(LaborOriginEntry laborOriginEntry, LaborLedgerUnitOfWork laborLedgerUnitOfWork, Date runDate, int lineNumber, Map<String, Integer> glEntryReportSummary) {
        LaborOriginEntry summarizedEntry = summarizeLaborGLEntries(laborOriginEntry, laborLedgerUnitOfWork, runDate, lineNumber, glEntryReportSummary);
        if (summarizedEntry == null || laborOriginEntry == null) { 
            return;
        }

        try {
            List<Message> errors = this.isPostableForLaborGLEntry(summarizedEntry);
            if (errors == null || errors.isEmpty()) {
                String operationType = laborGLLedgerEntryPoster.post(summarizedEntry, 0, runDate, null);
                updateReportSummary(glEntryReportSummary, laborGLLedgerEntryPoster.getDestinationName(), operationType);
            }
            else {
                updateReportSummary(glEntryReportSummary, ORIGN_ENTRY, KFSConstants.OperationType.BYPASS);
            }
        }
        catch (RuntimeException ioe) {
            // catch here again, it should be from postSingleEntryIntoLaborLedger
            LOG.error("postLaborGLEntries stopped due to: " + ioe.getMessage() + " on line number : " + lineNumber, ioe);
            throw new RuntimeException("Unable to execute: " + ioe.getMessage() + " on line number : " + lineNumber, ioe);
        }
    }

    protected void updateReportSummary(Map<String, Integer> reportSummary, String destination, String operation) {
        String key = destination + "," + operation;

        if (reportSummary.containsKey(key)) {
            Integer count = reportSummary.get(key);
            reportSummary.put(key, count + 1);
        }
        else {
            reportSummary.put(key, 1);
        }
    }

    protected void writeErrorEntry(String line) {
        try {
            POSTER_OUTPUT_ERR_FILE_ps.printf("%s\n", line);
        }
        catch (Exception e) {
            LOG.error("postAsProcessedOriginEntry stopped due to: " + e.getMessage(), e);
            throw new RuntimeException("Unable to execute: " + e.getMessage(), e);
        }
    }

    /**
     * Get a set of the balance type codes that are bypassed by Labor Poster
     * 
     * @return a set of the balance type codes that are bypassed by Labor Poster
     */
    public Collection<String> getBalanceTypesNotProcessed() {
        return parameterService.getParameterValuesAsString(LaborPosterStep.class, Poster.BALANCE_TYPES_NOT_PROCESSED);
    }

    /**
     * Get a set of the fiscal period codes that are bypassed by Labor Poster
     * 
     * @return a set of the fiscal period codes that are bypassed by Labor Poster
     */
    public Collection<String> getPeriodCodesNotProcessed() {
        return parameterService.getParameterValuesAsString(LaborPosterStep.class, Poster.PERIOD_CODES_NOT_PROCESSED);
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
     * Sets the laborTransactionDescriptionService attribute value.
     * @param laborTransactionDescriptionService The laborTransactionDescriptionService to set.
     */
    public void setLaborTransactionDescriptionService(LaborTransactionDescriptionService laborTransactionDescriptionService) {
        this.laborTransactionDescriptionService = laborTransactionDescriptionService;
    }
    
    /**
     * Sets the reportWriterService
     * 
     * @param reportWriterService The reportWriterService to set.
     */
    public void setReportWriterService(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }

    /**
     * Sets the errorListingReportWriterService
     * 
     * @param errorListingReportWriterService The errorListingReportWriterService to set.
     */
    public void setErrorListingReportWriterService(ReportWriterService errorListingReportWriterService) {
        this.errorListingReportWriterService = errorListingReportWriterService;
    }

    /**
     * Sets the ledgerSummaryReportWriterService
     * 
     * @param ledgerSummaryReportWriterService The ledgerSummaryReportWriterService to set.
     */
    public void setLedgerSummaryReportWriterService(ReportWriterService ledgerSummaryReportWriterService) {
        this.ledgerSummaryReportWriterService = ledgerSummaryReportWriterService;
    }

    /**
     * Sets the laborPosterTransactionValidator attribute value.
     * 
     * @param laborPosterTransactionValidator The laborPosterTransactionValidator to set.
     */
    public void setLaborPosterTransactionValidator(VerifyTransaction laborPosterTransactionValidator) {
        this.laborPosterTransactionValidator = laborPosterTransactionValidator;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the laborGlEntryStatisticsReportWriterService attribute value.
     * 
     * @param laborGlEntryStatisticsReportWriterService The laborGlEntryStatisticsReportWriterService to set.
     */
    public void setLaborGlEntryStatisticsReportWriterService(ReportWriterService laborGlEntryStatisticsReportWriterService) {
        this.laborGlEntryStatisticsReportWriterService = laborGlEntryStatisticsReportWriterService;
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * 
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
