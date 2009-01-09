/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRate;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.dataaccess.AccountDao;
import org.kuali.kfs.coa.dataaccess.ChartDao;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryRateDetailDao;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryTypeDao;
import org.kuali.kfs.coa.dataaccess.SubAccountDao;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterIndirectCostRecoveryEntriesStep;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.batch.service.VerifyTransaction;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.gl.dataaccess.ExpenditureTransactionDao;
import org.kuali.kfs.gl.dataaccess.ReversalDao;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.ReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.exception.InvalidFlexibleOffsetException;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of PosterService
 */
@Transactional
public class PosterServiceImpl implements PosterService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceImpl.class);

    public static final KualiDecimal WARNING_MAX_DIFFERENCE = new KualiDecimal("0.03");
    public static final String DATE_FORMAT_STRING = "yyyyMMdd";

    private List transactionPosters;
    private VerifyTransaction verifyTransaction;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private ReversalDao reversalDao;
    private UniversityDateDao universityDateDao;
    private AccountingPeriodService accountingPeriodService;
    private ExpenditureTransactionDao expenditureTransactionDao;
    private IndirectCostRecoveryRateDetailDao indirectCostRecoveryRateDetailDao;
    private ObjectCodeService objectCodeService;
    private SubAccountService subAccountService;
    private OffsetDefinitionService offsetDefinitionService;
    private ReportService reportService;
    private ParameterService parameterService;
    private KualiConfigurationService configurationService;
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private RunDateService runDateService;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;

    /**
     * Post scrubbed GL entries to GL tables.
     */
    public void postMainEntries() {
        LOG.debug("postMainEntries() started");
        postEntries(PosterService.MODE_ENTRIES);
    }

    /**
     * Post reversal GL entries to GL tables.
     */
    public void postReversalEntries() {
        LOG.debug("postReversalEntries() started");
        postEntries(PosterService.MODE_REVERSAL);
    }

    /**
     * Post ICR GL entries to GL tables.
     */
    public void postIcrEntries() {
        LOG.debug("postIcrEntries() started");
        postEntries(PosterService.MODE_ICR);
    }

    /**
     * Actually post the entries. The mode variable decides which entries to post.
     * 
     * @param mode the poster's current run mode
     */
    private void postEntries(int mode) {
        LOG.debug("postEntries() started");

        String validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
        String invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
        OriginEntryGroup validGroup = null;
        OriginEntryGroup invalidGroup = null;

        Date executionDate = new Date(dateTimeService.getCurrentDate().getTime());
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());

        UniversityDate runUniversityDate = universityDateDao.getByPrimaryKey(runDate);

        Collection groups = null;
        Iterator reversalTransactions = null;
        switch (mode) {
            case PosterService.MODE_ENTRIES:
                validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
                groups = originEntryGroupService.getGroupsToPost();
                reportService.generatePosterMainLedgerSummaryReport(executionDate, runDate, groups);
                break;
            case PosterService.MODE_REVERSAL:
                validEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_ERROR;
                reversalTransactions = reversalDao.getByDate(runDate);
                reportService.generatePosterReversalLedgerSummaryReport(executionDate, runDate, reversalTransactions);
                break;
            case PosterService.MODE_ICR:
                validEntrySourceCode = OriginEntrySource.ICR_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.ICR_POSTER_ERROR;
                groups = originEntryGroupService.getIcrGroupsToPost();
                reportService.generatePosterIcrLedgerSummaryReport(executionDate, runDate, groups);
                break;
            default:
                throw new IllegalArgumentException("Invalid poster mode " + mode);
        }

        // Create new Groups for output transactions
        validGroup = originEntryGroupService.createGroup(runDate, validEntrySourceCode, true, true, false);
        invalidGroup = originEntryGroupService.createGroup(runDate, invalidEntrySourceCode, false, true, false);

        Map reportError = new HashMap();

        // Build the summary map so all the possible combinations of destination &
        // operation
        // are included in the summary part of the report.
        Map reportSummary = new HashMap();
        for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
            PostTransaction poster = (PostTransaction) posterIter.next();
            reportSummary.put(poster.getDestinationName() + "," + GeneralLedgerConstants.DELETE_CODE, new Integer(0));
            reportSummary.put(poster.getDestinationName() + "," + GeneralLedgerConstants.INSERT_CODE, new Integer(0));
            reportSummary.put(poster.getDestinationName() + "," + GeneralLedgerConstants.UPDATE_CODE, new Integer(0));
        }

        int ecount = 0;
        try {
            if ((mode == PosterService.MODE_ENTRIES) || (mode == PosterService.MODE_ICR)) {
                LOG.debug("postEntries() Processing groups");
                for (Iterator iter = groups.iterator(); iter.hasNext();) {
                    OriginEntryGroup group = (OriginEntryGroup) iter.next();

                    Iterator entries = originEntryService.getEntriesByGroup(group);
                    while (entries.hasNext()) {
                        Transaction tran = (Transaction) entries.next();

                        postTransaction(tran, mode, reportSummary, reportError, invalidGroup, validGroup, runUniversityDate);

                        if (++ecount % 1000 == 0) {
                            LOG.info("postEntries() Posted Entry " + ecount);
                        }
                    }

                    // Mark this group so we don't process it again next time the poster runs
                    group.setProcess(Boolean.FALSE);
                    originEntryGroupService.save(group);
                }
            }
            else {
                LOG.debug("postEntries() Processing reversal transactions");

                final String GL_REVERSAL_T = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Reversal.class).getFullTableName();

                while (reversalTransactions.hasNext()) {
                    Transaction tran = (Transaction) reversalTransactions.next();
                    addReporting(reportSummary, GL_REVERSAL_T, GeneralLedgerConstants.SELECT_CODE);

                    postTransaction(tran, mode, reportSummary, reportError, invalidGroup, validGroup, runUniversityDate);

                    if (++ecount % 1000 == 0) {
                        LOG.info("postEntries() Posted Entry " + ecount);
                    }
                }

                // Report Reversal poster valid transactions
                reportService.generatePosterReversalTransactionsListing(executionDate, runDate, validGroup);
            }
        }
        catch (RuntimeException e) {
            LOG.info("postEntries() failed posting Entry " + ecount);
            throw e;
        }

        LOG.info("postEntries() done, total count = " + ecount);

        // Generate the reports
        reportService.generatePosterStatisticsReport(executionDate, runDate, reportSummary, transactionPosters, reportError, mode);
        reportService.generatePosterErrorTransactionListing(executionDate, runDate, invalidGroup, mode);
    }

    /**
     * Runs the given transaction through each transaction posting algorithms associated with this instance
     * 
     * @param tran a transaction to post
     * @param mode the mode the poster is running in
     * @param reportSummary a Map of summary counts generated by the posting process
     * @param reportError a Map of errors encountered during posting
     * @param invalidGroup the group to save invalid entries to
     * @param validGroup the gorup to save valid posted entries into
     * @param runUniversityDate the university date of this poster run
     */
    private void postTransaction(Transaction tran, int mode, Map reportSummary, Map reportError, OriginEntryGroup invalidGroup, OriginEntryGroup validGroup, UniversityDate runUniversityDate) {

        List errors = new ArrayList();

        Transaction originalTransaction = tran;

        final String GL_ORIGIN_ENTRY_T = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(OriginEntryFull.class).getFullTableName();

        // Update select count in the report
        if (mode == PosterService.MODE_ENTRIES) {
            addReporting(reportSummary, GL_ORIGIN_ENTRY_T, GeneralLedgerConstants.SELECT_CODE);
        }
        else {
            addReporting(reportSummary, GL_ORIGIN_ENTRY_T + " (ICR)", GeneralLedgerConstants.SELECT_CODE);
        }

        // If these are reversal entries, we need to reverse the entry and
        // modify a few fields
        if (mode == PosterService.MODE_REVERSAL) {
            Reversal reversal = new Reversal(tran);

            // Reverse the debit/credit code
            if (KFSConstants.GL_DEBIT_CODE.equals(reversal.getTransactionDebitCreditCode())) {
                reversal.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else if (KFSConstants.GL_CREDIT_CODE.equals(reversal.getTransactionDebitCreditCode())) {
                reversal.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            UniversityDate udate = universityDateDao.getByPrimaryKey(reversal.getFinancialDocumentReversalDate());
            if (udate != null) {
                reversal.setUniversityFiscalYear(udate.getUniversityFiscalYear());
                reversal.setUniversityFiscalPeriodCode(udate.getUniversityFiscalAccountingPeriod());

                AccountingPeriod ap = accountingPeriodService.getByPeriod(reversal.getUniversityFiscalPeriodCode(), reversal.getUniversityFiscalYear());
                if (ap != null) {
                    if (!ap.isActive()) { // Make sure accounting period is closed
                        reversal.setUniversityFiscalYear(runUniversityDate.getUniversityFiscalYear());
                        reversal.setUniversityFiscalPeriodCode(runUniversityDate.getUniversityFiscalAccountingPeriod());
                    }
                    reversal.setFinancialDocumentReversalDate(null);
                    String newDescription = KFSConstants.GL_REVERSAL_DESCRIPTION_PREFIX + reversal.getTransactionLedgerEntryDescription();
                    if (newDescription.length() > 40) {
                        newDescription = newDescription.substring(0, 40);
                    }
                    reversal.setTransactionLedgerEntryDescription(newDescription);
                }
                else {
                    errors.add(configurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_IN_ACCOUNTING_PERIOD_TABLE));
                }
            }
            else {
                errors.add(configurationService.getPropertyString(KFSKeyConstants.ERROR_REVERSAL_DATE_NOT_IN_UNIV_DATE_TABLE));
            }

            PersistenceService ps = SpringContext.getBean(PersistenceService.class);
            ps.retrieveNonKeyFields(reversal);
            tran = reversal;
        }

        if (errors.size() == 0) {
            errors = verifyTransaction.verifyTransaction(tran);
        }

        // Now check each poster to see if it needs to verify the transaction. If
        // it returns errors, we won't post it
        for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
            PostTransaction poster = (PostTransaction) posterIter.next();
            if (poster instanceof VerifyTransaction) {
                VerifyTransaction vt = (VerifyTransaction) poster;

                errors.addAll(vt.verifyTransaction(tran));
            }
        }

        if (errors.size() > 0) {
            // Error on this transaction
            reportError.put(tran, errors);
            addReporting(reportSummary, "WARNING", GeneralLedgerConstants.SELECT_CODE);

            originEntryService.createEntry(tran, invalidGroup);
        }
        else {
            // No error so post it
            for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
                PostTransaction poster = (PostTransaction) posterIter.next();
                String actionCode = poster.post(tran, mode, runUniversityDate.getUniversityDate());

                if (actionCode.startsWith(GeneralLedgerConstants.ERROR_CODE)) {
                    errors = new ArrayList();
                    errors.add(actionCode);
                    reportError.put(tran, errors);
                }
                else if (actionCode.indexOf(GeneralLedgerConstants.INSERT_CODE) >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), GeneralLedgerConstants.INSERT_CODE);
                }
                else if (actionCode.indexOf(GeneralLedgerConstants.UPDATE_CODE) >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), GeneralLedgerConstants.UPDATE_CODE);
                }
                else if (actionCode.indexOf(GeneralLedgerConstants.DELETE_CODE) >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), GeneralLedgerConstants.DELETE_CODE);
                }
                else if (actionCode.indexOf(GeneralLedgerConstants.SELECT_CODE) >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), GeneralLedgerConstants.SELECT_CODE);
                }
            }

            if (errors.size() == 0) {
                originEntryService.createEntry(tran, validGroup);

                // Delete the reversal entry
                if (mode == PosterService.MODE_REVERSAL) {
                    reversalDao.delete((Reversal) originalTransaction);
                    addReporting(reportSummary, MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Reversal.class).getFullTableName(), GeneralLedgerConstants.DELETE_CODE);
                }
            }
        }
    }

    /**
     * This step reads the expenditure table and uses the data to generate Indirect Cost Recovery transactions.
     */
    public void generateIcrTransactions() {
        LOG.debug("generateIcrTransactions() started");

        Date executionDate = dateTimeService.getCurrentSqlDate();
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());

        OriginEntryGroup group = originEntryGroupService.createGroup(runDate, OriginEntrySource.ICR_TRANSACTIONS, true, true, false);

        Map<ExpenditureTransaction, List<Message>> reportErrors = new HashMap();

        int reportExpendTranRetrieved = 0;
        int reportExpendTranDeleted = 0;
        int reportExpendTranKept = 0;
        int reportOriginEntryGenerated = 0;

        Iterator expenditureTransactions = expenditureTransactionDao.getAllExpenditureTransactions();
        while (expenditureTransactions.hasNext()) {
            ExpenditureTransaction et = (ExpenditureTransaction) expenditureTransactions.next();
            reportExpendTranRetrieved++;

            KualiDecimal transactionAmount = et.getAccountObjectDirectCostAmount();
            KualiDecimal distributionAmount = KualiDecimal.ZERO;

            if (shouldIgnoreExpenditureTransaction(et)) {
                continue;
            }
            
            IndirectCostRecoveryGenerationMetadata icrGenerationMetadata = retrieveSubAccountIndirectCostRecoveryMetadata(et, reportErrors);
            if (icrGenerationMetadata == null) {
                // ICR information was not set up properly for sub-account, default to using ICR information from the account
                icrGenerationMetadata = retrieveAccountIndirectCostRecoveryMetadata(et);
            }
            
            Collection<IndirectCostRecoveryRateDetail> automatedEntries = indirectCostRecoveryRateDetailDao.getActiveRateDetailsByRate(et.getUniversityFiscalYear(), icrGenerationMetadata.getFinancialIcrSeriesIdentifier());
            int automatedEntriesCount = automatedEntries.size();

            if (automatedEntriesCount > 0) {
                for (Iterator icrIter = automatedEntries.iterator(); icrIter.hasNext();) {
                    IndirectCostRecoveryRateDetail icrEntry = (IndirectCostRecoveryRateDetail) icrIter.next();
                    KualiDecimal generatedTransactionAmount = null;

                    if (!icrIter.hasNext()) {
                        generatedTransactionAmount = distributionAmount;

                        // Log differences that are over WARNING_MAX_DIFFERENCE
                        if (getPercentage(transactionAmount, icrEntry.getAwardIndrCostRcvyRatePct()).subtract(distributionAmount).abs().isGreaterThan(WARNING_MAX_DIFFERENCE)) {
                            addIndirectCostRecoveryReportError(et, Message.TYPE_WARNING, "ADJUSTMENT GREATER THAN " + WARNING_MAX_DIFFERENCE, reportErrors);
                        }
                    }
                    else if (icrEntry.getTransactionDebitIndicator().equals(KFSConstants.GL_DEBIT_CODE)) {
                        generatedTransactionAmount = getPercentage(transactionAmount, icrEntry.getAwardIndrCostRcvyRatePct());
                        distributionAmount = distributionAmount.add(generatedTransactionAmount);
                    }
                    else if (icrEntry.getTransactionDebitIndicator().equals(KFSConstants.GL_CREDIT_CODE)) {
                        generatedTransactionAmount = getPercentage(transactionAmount, icrEntry.getAwardIndrCostRcvyRatePct());
                        distributionAmount = distributionAmount.subtract(generatedTransactionAmount);
                    }
                    else {
                        // Log if D / C code not found
                        addIndirectCostRecoveryReportError(et, Message.TYPE_WARNING, "DEBIT OR CREDIT CODE NOT FOUND", reportErrors);
                    }

                    generateTransactions(et, icrEntry, generatedTransactionAmount, runDate, group, reportErrors, icrGenerationMetadata);
                    reportOriginEntryGenerated = reportOriginEntryGenerated + 2;
                }
            }

            // Delete expenditure record
            expenditureTransactionDao.delete(et);
            reportExpendTranDeleted++;
        }

        reportService.generatePosterIcrStatisticsReport(executionDate, runDate, reportErrors, reportExpendTranRetrieved, reportExpendTranDeleted, reportExpendTranKept, reportOriginEntryGenerated);
    }

    /**
     * Generate a transfer transaction and an offset transaction
     * 
     * @param et an expenditure transaction
     * @param icrEntry the indirect cost recovery entry
     * @param generatedTransactionAmount the amount of the transaction
     * @param runDate the transaction date for the newly created origin entry
     * @param group the group to save the origin entry to
     * @param icrGenerationMetadata metadata used to generate ICR entries, based on information coming from either the sub-account or the account associated
     * with the expenditure transaction
     */
    private void generateTransactions(ExpenditureTransaction et, IndirectCostRecoveryRateDetail icrRateDetail, KualiDecimal generatedTransactionAmount, Date runDate,
            OriginEntryGroup group, Map<ExpenditureTransaction, List<Message>> reportErrors, IndirectCostRecoveryGenerationMetadata icrGenerationMetadata) {
        BigDecimal pct = new BigDecimal(icrRateDetail.getAwardIndrCostRcvyRatePct().toString());
        pct = pct.divide(BDONEHUNDRED);

        OriginEntryFull e = new OriginEntryFull();
        e.setTransactionLedgerEntrySequenceNumber(0);

        // SYMBOL_USE_EXPENDITURE_ENTRY means we use the field from the expenditure entry, SYMBOL_USE_IRC_FROM_ACCOUNT
        // means we use the ICR field from the account record, otherwise, use the field in the icrEntry
        if (GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(icrRateDetail.getFinancialObjectCode()) || GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(icrRateDetail.getFinancialObjectCode())) {
            e.setFinancialObjectCode(et.getObjectCode());
            e.setFinancialSubObjectCode(et.getSubObjectCode());
        }
        else {
            e.setFinancialObjectCode(icrRateDetail.getFinancialObjectCode());
            if (GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(icrRateDetail.getFinancialSubObjectCode())) {
                e.setFinancialSubObjectCode(et.getSubObjectCode());
            }
            else {
                e.setFinancialSubObjectCode(icrRateDetail.getFinancialSubObjectCode());
            }
        }

        if (GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(icrRateDetail.getAccountNumber())) {
            e.setAccountNumber(et.getAccountNumber());
            e.setChartOfAccountsCode(et.getChartOfAccountsCode());
            e.setSubAccountNumber(et.getSubAccountNumber());
        }
        else if (GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(icrRateDetail.getAccountNumber())) {
            e.setAccountNumber(icrGenerationMetadata.getIndirectCostRecoveryAccountNumber());
            e.setChartOfAccountsCode(icrGenerationMetadata.getIndirectCostRecoveryChartOfAccountsCode());
            e.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        else {
            e.setAccountNumber(icrRateDetail.getAccountNumber());
            e.setSubAccountNumber(icrRateDetail.getSubAccountNumber());
            e.setChartOfAccountsCode(icrRateDetail.getChartOfAccountsCode());
            // TODO Reporting thing line 1946
        }

        e.setFinancialDocumentTypeCode(parameterService.getParameterValue(PosterIndirectCostRecoveryEntriesStep.class, KFSConstants.SystemGroupParameterNames.GL_INDIRECT_COST_RECOVERY));
        e.setFinancialSystemOriginationCode(parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_STRING);
        e.setDocumentNumber(sdf.format(runDate));
        if (KFSConstants.GL_DEBIT_CODE.equals(icrRateDetail.getTransactionDebitIndicator())) {
            e.setTransactionLedgerEntryDescription(getChargeDescription(pct, et.getObjectCode(), icrGenerationMetadata.getIndirectCostRecoveryTypeCode(), et.getAccountObjectDirectCostAmount().abs()));
        }
        else {
            e.setTransactionLedgerEntryDescription(getOffsetDescription(pct, et.getAccountObjectDirectCostAmount().abs(), et.getChartOfAccountsCode(), et.getAccountNumber()));
        }
        e.setTransactionDate(new java.sql.Date(runDate.getTime()));
        e.setTransactionDebitCreditCode(icrRateDetail.getTransactionDebitIndicator());
        e.setFinancialBalanceTypeCode(et.getBalanceTypeCode());
        e.setUniversityFiscalYear(et.getUniversityFiscalYear());
        e.setUniversityFiscalPeriodCode(et.getUniversityFiscalAccountingPeriod());

        ObjectCode oc = objectCodeService.getByPrimaryId(e.getUniversityFiscalYear(), e.getChartOfAccountsCode(), e.getFinancialObjectCode());
        if (oc == null) {
            // TODO This should be a report thing, not an exception
            throw new IllegalArgumentException(configurationService.getPropertyString(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND_FOR) + e.getUniversityFiscalYear() + "," + e.getChartOfAccountsCode() + "," + e.getFinancialObjectCode());
        }
        e.setFinancialObjectTypeCode(oc.getFinancialObjectTypeCode());

        if (generatedTransactionAmount.isNegative()) {
            if (KFSConstants.GL_DEBIT_CODE.equals(icrRateDetail.getTransactionDebitIndicator())) {
                e.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                e.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }
            e.setTransactionLedgerEntryAmount(generatedTransactionAmount.negated());
        }
        else {
            e.setTransactionLedgerEntryAmount(generatedTransactionAmount);
        }

        if (et.getBalanceTypeCode().equals(et.getOption().getExtrnlEncumFinBalanceTypCd()) || et.getBalanceTypeCode().equals(et.getOption().getIntrnlEncumFinBalanceTypCd()) || et.getBalanceTypeCode().equals(et.getOption().getPreencumbranceFinBalTypeCd()) || et.getBalanceTypeCode().equals(et.getOption().getCostShareEncumbranceBalanceTypeCd())) {
            e.setDocumentNumber(parameterService.getParameterValue(PosterIndirectCostRecoveryEntriesStep.class, KFSConstants.SystemGroupParameterNames.GL_INDIRECT_COST_RECOVERY));
        }
        e.setProjectCode(et.getProjectCode());
        if (GeneralLedgerConstants.getDashOrganizationReferenceId().equals(et.getOrganizationReferenceId())) {
            e.setOrganizationReferenceId(null);
        }
        else {
            e.setOrganizationReferenceId(et.getOrganizationReferenceId());
        }

        // TODO 2031-2039
        originEntryService.createEntry(e, group);

        // Now generate Offset
        e = new OriginEntryFull(e);
        if (KFSConstants.GL_DEBIT_CODE.equals(e.getTransactionDebitCreditCode())) {
            e.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            e.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        e.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        
        String offsetBalanceSheetObjectCodeNumber = determineIcrOffsetBalanceSheetObjectCodeNumber(e, et, icrRateDetail);
        e.setFinancialObjectCode(offsetBalanceSheetObjectCodeNumber);

        ObjectCode balSheetObjectCode = objectCodeService.getByPrimaryId(icrRateDetail.getUniversityFiscalYear(), e.getChartOfAccountsCode(), offsetBalanceSheetObjectCodeNumber);
        if (balSheetObjectCode == null) {
            String messageText = configurationService.getPropertyString(KFSKeyConstants.ERROR_INVALID_OFFSET_OBJECT_CODE) + icrRateDetail.getUniversityFiscalYear() + "-" + e.getChartOfAccountsCode() + "-" + offsetBalanceSheetObjectCodeNumber;
            addIndirectCostRecoveryReportError(et, Message.TYPE_WARNING, messageText, reportErrors);
        }
        else {
            e.setFinancialObjectTypeCode(balSheetObjectCode.getFinancialObjectTypeCode());
        }

        if (KFSConstants.GL_DEBIT_CODE.equals(icrRateDetail.getTransactionDebitIndicator())) {
            e.setTransactionLedgerEntryDescription(getChargeDescription(pct, et.getObjectCode(), icrGenerationMetadata.getIndirectCostRecoveryTypeCode(), et.getAccountObjectDirectCostAmount().abs()));
        }
        else {
            e.setTransactionLedgerEntryDescription(getOffsetDescription(pct, et.getAccountObjectDirectCostAmount().abs(), et.getChartOfAccountsCode(), et.getAccountNumber()));
        }

        try {
            flexibleOffsetAccountService.updateOffset(e);
        }
        catch (InvalidFlexibleOffsetException ex) {
            addIndirectCostRecoveryReportError(et, Message.TYPE_WARNING, "FAILED TO GENERATE FLEXIBLE OFFSETS " + ex.getMessage(), reportErrors);
            LOG.warn("FAILED TO GENERATE FLEXIBLE OFFSETS FOR EXPENDITURE TRANSACTION " + et.toString(), ex);
        }

        originEntryService.createEntry(e, group);
    }

    private static KualiDecimal ONEHUNDRED = new KualiDecimal("100");
    private static DecimalFormat DFPCT = new DecimalFormat("#0.000");
    private static DecimalFormat DFAMT = new DecimalFormat("##########.00");
    private static BigDecimal BDONEHUNDRED = new BigDecimal("100");

    /**
     * Returns ICR Generation Metadata based on SubAccount information if the SubAccount on the expenditure transaction is properly set up for ICR 
     * 
     * @param et
     * @param reportErrors
     * @return null if the ET does not have a SubAccount properly set up for ICR
     */
    protected IndirectCostRecoveryGenerationMetadata retrieveSubAccountIndirectCostRecoveryMetadata(ExpenditureTransaction et, Map<ExpenditureTransaction, List<Message>> reportErrors) {
        SubAccount subAccount = subAccountService.getByPrimaryId(et.getChartOfAccountsCode(), et.getAccountNumber(), et.getSubAccountNumber());
        if (ObjectUtils.isNotNull(subAccount) && ObjectUtils.isNotNull(subAccount.getA21SubAccount())) {
            A21SubAccount a21SubAccount = subAccount.getA21SubAccount();
            if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryTypeCode()) && 
                    StringUtils.isBlank(a21SubAccount.getFinancialIcrSeriesIdentifier()) &&
                    StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryChartOfAccountsCode()) && 
                    StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryAccountNumber())) {
                // all ICR fields were blank, therefore, this sub account was not set up for ICR
                return null;
            }

            // these fields will be used to construct warning messages
            String warningMessagePattern = configurationService.getPropertyString(KFSKeyConstants.WARNING_ICR_GENERATION_PROBLEM_WITH_A21SUBACCOUNT_FIELD_BLANK_INVALID);
            String subAccountBOLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccount.class.getName()).getObjectLabel();
            String subAccountValue = subAccount.getChartOfAccountsCode() + "-" + subAccount.getAccountNumber() + "-" + subAccount.getSubAccountNumber();
            String accountBOLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Account.class.getName()).getObjectLabel();
            String accountValue = et.getChartOfAccountsCode() + "-" + et.getAccountNumber();
            
            boolean subAccountOK = true;
            
            // there were some ICR fields that were filled in, make sure they're all filled in and are valid values
            if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryTypeCode()) || 
                    ObjectUtils.isNull(a21SubAccount.getIndirectCostRecoveryType())) {
                String errorFieldName = dataDictionaryService.getAttributeShortLabel(A21SubAccount.class, KFSPropertyConstants.INDIRECT_COST_RECOVERY_TYPE_CODE);
                String warningMessage = MessageFormat.format(warningMessagePattern, errorFieldName, subAccountBOLabel, subAccountValue, accountBOLabel, accountValue);
                addIndirectCostRecoveryReportError(et, Message.TYPE_WARNING, warningMessage, reportErrors);
                subAccountOK = false;
            }
            
            if (StringUtils.isBlank(a21SubAccount.getFinancialIcrSeriesIdentifier())) {
                Map<String, Object> icrRatePkMap = new HashMap<String, Object>();
                icrRatePkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, et.getUniversityFiscalYear());
                icrRatePkMap.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, a21SubAccount.getFinancialIcrSeriesIdentifier());
                IndirectCostRecoveryRate indirectCostRecoveryRate = (IndirectCostRecoveryRate) businessObjectService.findByPrimaryKey(IndirectCostRecoveryRate.class, icrRatePkMap);
                if (indirectCostRecoveryRate == null) {
                    String errorFieldName = dataDictionaryService.getAttributeShortLabel(A21SubAccount.class, KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER);
                    String warningMessage = MessageFormat.format(warningMessagePattern, errorFieldName, subAccountBOLabel, subAccountValue, accountBOLabel, accountValue);
                    addIndirectCostRecoveryReportError(et, Message.TYPE_WARNING, warningMessage, reportErrors);
                    subAccountOK = false;
                }
            }
            
            if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryChartOfAccountsCode()) ||
                    StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryAccountNumber()) ||
                    ObjectUtils.isNull(a21SubAccount.getIndirectCostRecoveryAccount())) {
                String errorFieldName = dataDictionaryService.getAttributeShortLabel(A21SubAccount.class, KFSPropertyConstants.INDIRECT_COST_RECOVERY_CHART_OF_ACCOUNTS_CODE) + "/" +
                        dataDictionaryService.getAttributeShortLabel(A21SubAccount.class, KFSPropertyConstants.INDIRECT_COST_RECOVERY_ACCOUNT_NUMBER);
                String warningMessage = MessageFormat.format(warningMessagePattern, errorFieldName, subAccountBOLabel, subAccountValue, accountBOLabel, accountValue);
                addIndirectCostRecoveryReportError(et, Message.TYPE_WARNING, warningMessage, reportErrors);
                subAccountOK = false;
            }
            
            if (subAccountOK) {
                IndirectCostRecoveryGenerationMetadata metadata = new IndirectCostRecoveryGenerationMetadata();
                metadata.setFinancialIcrSeriesIdentifier(a21SubAccount.getFinancialIcrSeriesIdentifier());
                metadata.setIndirectCostRecoveryTypeCode(a21SubAccount.getIndirectCostRecoveryTypeCode());
                metadata.setIndirectCostRecoveryChartOfAccountsCode(a21SubAccount.getIndirectCostRecoveryChartOfAccountsCode());
                metadata.setIndirectCostRecoveryAccountNumber(a21SubAccount.getIndirectCostRecoveryAccountNumber());
                metadata.setIndirectCostRecoveryAccount(a21SubAccount.getIndirectCostRecoveryAccount());
                return metadata;
            }
        }
        return null;
    }
    
    
    protected IndirectCostRecoveryGenerationMetadata retrieveAccountIndirectCostRecoveryMetadata(ExpenditureTransaction et) {
        Account account = et.getAccount();
        
        IndirectCostRecoveryGenerationMetadata metadata = new IndirectCostRecoveryGenerationMetadata();
        metadata.setFinancialIcrSeriesIdentifier(account.getFinancialIcrSeriesIdentifier());
        metadata.setIndirectCostRecoveryTypeCode(account.getAcctIndirectCostRcvyTypeCd());
        metadata.setIndirectCostRecoveryChartOfAccountsCode(account.getIndirectCostRcvyFinCoaCode());
        metadata.setIndirectCostRecoveryAccountNumber(account.getIndirectCostRecoveryAcctNbr());
        metadata.setIndirectCostRecoveryAccount(account.getIndirectCostRecoveryAcct());
        return metadata;
    }

    /**
     * Registers an error or warning message to an {@link ExpenditureTransaction}, and does not overwrite any other error messages associated
     * with it.
     * 
     * @param et the {@link ExpenditureTransaction} being processed that needs to be associated with a message
     * @param messageType one of {@link Message#TYPE_FATAL} or {@link Message#TYPE_WARNING}, probably the latter
     * @param messageText the message text
     * @param reportErrors the mappings of {@link ExpenditureTransaction}s to messages
     */
    protected void addIndirectCostRecoveryReportError(ExpenditureTransaction et, int messageType, String messageText, Map<ExpenditureTransaction, List<Message>> reportErrors) {
        List<Message> messages = reportErrors.get(et);
        if (messages == null) {
            messages = new ArrayList<Message>();
            reportErrors.put(et, messages);
        }
        messages.add(new Message(messageText, messageType));
    }
    
    /**
     * Generates a percent of a KualiDecimal amount (great for finding out how much of an origin entry should be recouped by indirect cost recovery)
     * 
     * @param amount the original amount
     * @param percent the percentage of that amount to calculate 
     * @return the percent of the amount
     */
    private KualiDecimal getPercentage(KualiDecimal amount, BigDecimal percent) {
        BigDecimal result = amount.bigDecimalValue().multiply(percent).divide(BDONEHUNDRED, 2, BigDecimal.ROUND_DOWN);
        return new KualiDecimal(result);
    }

    /**
     * Generates the description of a charge
     * 
     * @param rate the ICR rate for this entry
     * @param objectCode the object code of this entry
     * @param type the ICR type code of this entry's account
     * @param amount the amount of this entry
     * @return a description for the charge entry
     */
    private String getChargeDescription(BigDecimal rate, String objectCode, String type, KualiDecimal amount) {
        BigDecimal newRate = rate.multiply(PosterServiceImpl.BDONEHUNDRED);

        StringBuffer desc = new StringBuffer("CHG ");
        if (newRate.doubleValue() < 10) {
            desc.append(" ");
        }
        desc.append(DFPCT.format(newRate));
        desc.append("% ON ");
        desc.append(objectCode);
        desc.append(" (");
        desc.append(type);
        desc.append(")  ");
        String amt = DFAMT.format(amount);
        while (amt.length() < 13) {
            amt = " " + amt;
        }
        desc.append(amt);
        return desc.toString();
    }

    /**
     * Returns the description of a debit origin entry created by generateTransactions
     * 
     * @param rate the ICR rate that relates to this entry
     * @param amount the amount of this entry
     * @param chartOfAccountsCode the chart codce of the debit entry
     * @param accountNumber the account number of the debit entry
     * @return a description for the debit entry
     */
    private String getOffsetDescription(BigDecimal rate, KualiDecimal amount, String chartOfAccountsCode, String accountNumber) {
        BigDecimal newRate = rate.multiply(PosterServiceImpl.BDONEHUNDRED);

        StringBuffer desc = new StringBuffer("RCV ");
        if (newRate.doubleValue() < 10) {
            desc.append(" ");
        }
        desc.append(DFPCT.format(newRate));
        desc.append("% ON ");
        String amt = DFAMT.format(amount);
        while (amt.length() < 13) {
            amt = " " + amt;
        }
        desc.append(amt);
        desc.append(" FRM ");
        // desc.append(chartOfAccountsCode);
        // desc.append("-");
        desc.append(accountNumber);
        return desc.toString();
    }

    /**
     * Increments a named count holding statistics about posted transactions
     * 
     * @param reporting a Map of counts generated by this process
     * @param destination the destination of a given transaction
     * @param operation the operation being performed on the transaction
     */
    private void addReporting(Map reporting, String destination, String operation) {
        String key = destination + "," + operation;
        if (reporting.containsKey(key)) {
            Integer c = (Integer) reporting.get(key);
            reporting.put(key, new Integer(c.intValue() + 1));
        }
        else {
            reporting.put(key, new Integer(1));
        }
    }
    
    protected String determineIcrOffsetBalanceSheetObjectCodeNumber(OriginEntry offsetEntry, ExpenditureTransaction et, IndirectCostRecoveryRateDetail icrRateDetail) {
        String icrEntryDocumentType = parameterService.getParameterValue(PosterIndirectCostRecoveryEntriesStep.class, KFSConstants.SystemGroupParameterNames.GL_INDIRECT_COST_RECOVERY);
        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(offsetEntry.getUniversityFiscalYear(), offsetEntry.getChartOfAccountsCode(), icrEntryDocumentType, et.getBalanceTypeCode());
        return offsetDefinition.getFinancialObjectCode();
    }

    public void setVerifyTransaction(VerifyTransaction vt) {
        verifyTransaction = vt;
    }

    public void setTransactionPosters(List p) {
        transactionPosters = p;
    }

    public void setOriginEntryService(OriginEntryService oes) {
        originEntryService = oes;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService oes) {
        originEntryGroupService = oes;
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setReversalDao(ReversalDao red) {
        reversalDao = red;
    }

    public void setUniversityDateDao(UniversityDateDao udd) {
        universityDateDao = udd;
    }

    public void setAccountingPeriodService(AccountingPeriodService aps) {
        accountingPeriodService = aps;
    }

    public void setExpenditureTransactionDao(ExpenditureTransactionDao etd) {
        expenditureTransactionDao = etd;
    }

    public void setIndirectCostRecoveryRateDetailDao(IndirectCostRecoveryRateDetailDao iaed) {
        indirectCostRecoveryRateDetailDao = iaed;
    }

    public void setObjectCodeService(ObjectCodeService ocs) {
        objectCodeService = ocs;
    }

    public void setReportService(ReportService rs) {
        reportService = rs;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }

    public RunDateService getRunDateService() {
        return runDateService;
    }

    public void setRunDateService(RunDateService runDateService) {
        this.runDateService = runDateService;
    }

    public void setSubAccountService(SubAccountService subAccountService) {
        this.subAccountService = subAccountService;
    }

    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    protected boolean shouldIgnoreExpenditureTransaction(ExpenditureTransaction et) {
        if (ObjectUtils.isNotNull(et.getOption())) {
            SystemOptions options = et.getOption();
            return StringUtils.isNotBlank(options.getActualFinancialBalanceTypeCd()) && 
                    !options.getActualFinancialBalanceTypeCd().equals(et.getBalanceTypeCode());
        }
        return true;
    }
}
