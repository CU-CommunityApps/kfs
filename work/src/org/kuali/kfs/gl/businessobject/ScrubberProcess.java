/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.FinancialSystemParameter;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.exceptions.InvalidFlexibleOffsetException;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.module.gl.bo.CollectorDetail;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryable;
import org.kuali.module.gl.bo.OriginEntryLite;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.OriginEntryLiteService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.RunDateService;
import org.kuali.module.gl.service.ScrubberGroupService;
import org.kuali.module.gl.service.ScrubberProcessObjectCodeOverride;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.service.OriginEntryableLookupService;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.CachingLookup;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.DocumentGroupData;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.kuali.module.gl.util.ScrubberStatus;
import org.kuali.module.gl.util.StringHelper;
import org.springframework.util.StringUtils;

/**
 * This class has the logic for the scrubber. It is required because the scrubber process needs instance variables. Instance
 * variables in a spring service are shared between all code calling the service. This will make sure each run of the scrubber has
 * it's own instance variables instead of being shared.
 * 
 * 
 */
public class ScrubberProcess {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberProcess.class);

    public static final String TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE = "CE";
    public static final String TRANSACTION_TYPE_OFFSET = "O";
    public static final String TRANSACTION_TYPE_CAPITALIZATION = "C";
    public static final String TRANSACTION_TYPE_LIABILITY = "L";
    public static final String TRANSACTION_TYPE_TRANSFER = "T";
    public static final String TRANSACTION_TYPE_COST_SHARE = "CS";
    public static final String TRANSACTION_TYPE_OTHER = "X";

    public static final String COST_SHARE_CODE = "CSHR";

    public static final String COST_SHARE_TRANSFER_ENTRY_IND = "***";
    
    // These lengths are different then database field lengths, hence they are not from the DD
    public static final int COST_SHARE_ENCUMBRANCE_ENTRY_MAXLENGTH = 28;
    public static final int DEMERGER_TRANSACTION_LEDGET_ENTRY_DESCRIPTION = 33;
    public static final int OFFSET_MESSAGE_MAXLENGTH = 33;
    
    /* Services required */
    public FlexibleOffsetAccountService flexibleOffsetAccountService;
    public DocumentTypeService documentTypeService;
    public OriginEntryService originEntryService;
    public OriginEntryLiteService originEntryLiteService;
    public OriginEntryGroupService originEntryGroupService;
    public DateTimeService dateTimeService;
    public OffsetDefinitionService offsetDefinitionService;
    public ObjectCodeService objectCodeService;
    public KualiConfigurationService kualiConfigurationService;
    public UniversityDateDao universityDateDao;
    public PersistenceService persistenceService;
    public ReportService reportService;
    public ScrubberValidator scrubberValidator;
    public ScrubberProcessObjectCodeOverride scrubberProcessObjectCodeOverride;
    public RunDateService runDateService;
    public ThreadLocal<OriginEntryableLookupService> referenceLookup = new ThreadLocal<OriginEntryableLookupService>();

    public Map<String, FinancialSystemParameter> parameters;
    public Map<String, KualiParameterRule> rules;

    // this will only be populated when in collector mode, otherwise the memory requirements will be huge
    public Map<OriginEntryable, OriginEntryable> unscrubbedToUnscrubbedEntries;
    
    /* These are all different forms of the run date for this job */
    public Date runDate;
    public Calendar runCal;
    public UniversityDate universityRunDate;
    public String offsetString;
    
    /* These are the output groups */
    public OriginEntryGroup validGroup;
    public OriginEntryGroup errorGroup;
    public OriginEntryGroup expiredGroup;

    /* Unit Of Work info */
    public UnitOfWorkInfo unitOfWork;
    public KualiDecimal scrubCostShareAmount;

    /* Statistics for the reports */
    public ScrubberReportData scrubberReport;
    public Map<Transaction, List<Message>> scrubberReportErrors;
    public List<Message> transactionErrors;
    public DemergerReportData demergerReport;
    
    /* Description names */
    public String offsetDescription;
    public String capitalizationDescription;
    public String liabilityDescription;
    public String transferDescription;
    public String costShareDescription;

    /* Misc stuff */
    public boolean reportOnlyMode;
    /**
     * Whether this instance is being used to support the scrubbing of a collector batch
     */
    public boolean collectorMode;
    
    public ScrubberGroupService scrubberGroupService;
    
    /**
     * These parameters are all the dependencies.
     */
    public ScrubberProcess(FlexibleOffsetAccountService flexibleOffsetAccountService, DocumentTypeService documentTypeService, OriginEntryService originEntryService, OriginEntryGroupService originEntryGroupService, DateTimeService dateTimeService, OffsetDefinitionService offsetDefinitionService, ObjectCodeService objectCodeService, KualiConfigurationService kualiConfigurationService, UniversityDateDao universityDateDao, PersistenceService persistenceService, ReportService reportService, ScrubberValidator scrubberValidator, ScrubberProcessObjectCodeOverride scrubberProcessObjectCodeOverride, RunDateService runDateService, OriginEntryLiteService originEntryLiteService) {
        super();
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
        this.documentTypeService = documentTypeService;
        this.originEntryService = originEntryService;
        this.originEntryLiteService = originEntryLiteService;
        this.originEntryGroupService = originEntryGroupService;
        this.dateTimeService = dateTimeService;
        this.offsetDefinitionService = offsetDefinitionService;
        this.objectCodeService = objectCodeService;
        this.kualiConfigurationService = kualiConfigurationService;
        this.universityDateDao = universityDateDao;
        this.persistenceService = persistenceService;
        this.reportService = reportService;
        this.scrubberValidator = scrubberValidator;
        this.unscrubbedToUnscrubbedEntries = new HashMap<OriginEntryable, OriginEntryable>();
        this.scrubberProcessObjectCodeOverride = scrubberProcessObjectCodeOverride;
        this.runDateService = runDateService;

        parameters = kualiConfigurationService.getParametersByGroup(GLConstants.GL_SCRUBBER_GROUP);
        rules = kualiConfigurationService.getRulesByGroup(GLConstants.GL_SCRUBBER_GROUP);
        
        collectorMode = false;
    }

    /**
     * Scrub this single group read only. This will only output the scrubber report. It won't output any other groups.
     * 
     * @param group
     */
    public void scrubGroupReportOnly(OriginEntryGroup group,String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");

        scrubEntries(group,documentNumber);
    }

    public void scrubEntries() {
        scrubEntries(null,null);
    }
    
    /**
     * Scrubs the origin entry and ID billing details if the given batch.  Store all scrubber output into the collectorReportData parameter.
     * 
     * NOTE: DO NOT CALL ANY OF THE scrub* METHODS OF THIS CLASS AFTER CALLING THIS METHOD FOR EVERY UNIQUE INSTANCE OF THIS CLASS, OR THE COLLECTOR REPORTS MAY BE CORRUPTED
     * 
     * @param batch
     * @param collectorReportData
     */
    public ScrubberStatus scrubCollectorBatch(CollectorBatch batch, CollectorReportData collectorReportData) {
        collectorMode = true;
        
        /*/ explicit service dependence
        if (!(originEntryService instanceof MemoryOriginEntryServiceImpl && originEntryGroupService instanceof MemoryOriginEntryServiceImpl && originEntryGroupService == originEntryService)) {
            // when doing the collector scrubbing, we have an explicit dependence on the service implementation of the originEntryGroupService and originEntryService
            throw new IllegalStateException("service configuration error: collector scrubbing requires a special service implementation");
        }*/
        OriginEntryGroup group = originEntryGroupService.createGroup(batch.getTransmissionDate(), OriginEntrySource.COLLECTOR, false, false, false);
        for (OriginEntry originEntry : batch.getOriginEntries()) {
            originEntry.setGroup(group);
            originEntryService.save(originEntry);
        }
        
        // first, scrub the origin entries
        scrubEntries(group, null);
        // the scrubber process has just updated several member variables of this class.  Store these values for the collector report
        collectorReportData.setBatchOriginEntryScrubberErrors(batch, scrubberReportErrors);
        collectorReportData.setScrubberReportData(batch, scrubberReport);
        collectorReportData.setDemergerReportData(batch, demergerReport);
        
        ScrubberStatus scrubberStatus = new ScrubberStatus();
        scrubberStatus.setInputGroup(group);
        scrubberStatus.setValidGroup(validGroup);
        scrubberStatus.setErrorGroup(errorGroup);
        scrubberStatus.setExpiredGroup(expiredGroup);
        scrubberStatus.setUnscrubbedToScrubbedEntries(unscrubbedToUnscrubbedEntries);
        return scrubberStatus;
    }

    /**
     * Scrub all entries that need it in origin entry. Put valid scrubbed entries in a scrubber valid group, put errors in a
     * scrubber error group, and transactions with an expired account in the scrubber expired account group.
     */
    public void scrubEntries(OriginEntryGroup group,String documentNumber) {
        LOG.debug("scrubEntries() started");

        // We are in report only mode if we pass a group to this method.
        // if not, we are in batch mode and we scrub the backup group
        reportOnlyMode = (group != null) && !collectorMode;
                    
        scrubberReportErrors = new HashMap<Transaction, List<Message>>();

        // setup an object to hold the "default" date information
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        universityRunDate = universityDateDao.getByPrimaryKey(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        setOffsetString();
        setDescriptions();

        // Create the groups that will store the valid and error entries that come out of the scrubber
        // We don't need groups for the reportOnlyMode
        if (!reportOnlyMode) {
            if (collectorMode) {
                // for collector mode, these groups are not meant to be permanently persisted.
                // after the collector is done, it will delete these groups, but in case there's a failure and the following groups aren't created,
                // we set all of the group flags to false to prevent these groups from entering the nightly cycle
                validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_VALID, false, false, false);
                errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_ERROR, false, false, false);
                expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_EXPIRED, false, false, false);
            }
            else {
                validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_VALID, true, true, false);
                errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_ERROR, false, true, false);
                expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_EXPIRED, false, true, false);
            }
        }

        // get the origin entry groups to be processed by Scrubber
        Collection<OriginEntryGroup> groupsToScrub = null;
        if (reportOnlyMode || collectorMode) {
            groupsToScrub = new ArrayList<OriginEntryGroup>();
            groupsToScrub.add(group);
        }
        else {
            groupsToScrub = originEntryGroupService.getAllScrubbableBackupGroups();
        }
        LOG.debug("scrubEntries() number of groups to scrub: " + groupsToScrub.size());

        // generate the reports based on the origin entries to be processed by scrubber
        if (reportOnlyMode) {
            reportService.generateScrubberLedgerSummaryReportOnline(runDate, group,documentNumber);
        }
        else if (collectorMode) {
            // defer report generation for later
        }
        else {
            reportService.generateScrubberLedgerSummaryReportBatch(runDate, groupsToScrub);
        }

        // Scrub all of the OriginEntryGroups waiting to be scrubbed as of runDate.
        scrubberReport = new ScrubberReportData();
        for (Iterator iteratorOverGroups = groupsToScrub.iterator(); iteratorOverGroups.hasNext();) {
            OriginEntryGroup originEntryGroup = (OriginEntryGroup) iteratorOverGroups.next();
            LOG.debug("scrubEntries() Scrubbing group " + originEntryGroup.getId());

            processGroup(originEntryGroup);

            if (!reportOnlyMode && !collectorMode) {
                // Mark the origin entry group as being processed ...
                originEntryGroup.setProcess(Boolean.FALSE);

                // ... and save the origin entry group with the new process flag.
                originEntryGroupService.save(originEntryGroup);
            }
        }

        long startAfterGroups = System.currentTimeMillis();
        // generate the scrubber status summary report
        if (reportOnlyMode) {
            reportService.generateOnlineScrubberStatisticsReport( group.getId(), runDate, scrubberReport, scrubberReportErrors,documentNumber);
        }
        else if (collectorMode) {
            // defer report generation for later
        }
        else {
            reportService.generateBatchScrubberStatisticsReport(runDate, scrubberReport, scrubberReportErrors);
        }
        LOG.fatal("After report1 " + (System.currentTimeMillis() - startAfterGroups));

        // run the demerger if during regular nightly processing and collector processing
        if (!reportOnlyMode) {
            performDemerger(errorGroup, validGroup);
        }

        LOG.fatal("After demerger " + (System.currentTimeMillis() - startAfterGroups));
        
        // Run the reports
        if ( reportOnlyMode ) {
            // Run transaction list
            reportService.generateScrubberTransactionsOnline(runDate, group,documentNumber);
        }
        else if (collectorMode) {
            // defer report generation for later
        }
        else {
            long start2 = System.currentTimeMillis();
            // Run bad balance type report and removed transaction report
            reportService.generateScrubberBadBalanceTypeListingReport(runDate, groupsToScrub);
            LOG.fatal("Bad Bal report " + (System.currentTimeMillis() - start2));
            start2 = System.currentTimeMillis();
            reportService.generateScrubberRemovedTransactions(runDate, errorGroup);
            LOG.fatal("Removed trans report " + (System.currentTimeMillis() - start2));
        }
        LOG.fatal("After report2 " + (System.currentTimeMillis() - startAfterGroups));
    }

    /**
     * The demerger process reads all of the documents in the error group, then moves all of the original entries for that document
     * from the valid group to the error group. It does not move generated entries to the error group. Those are deleted. It also
     * modifies the doc number and origin code of cost share transfers.
     * 
     * @param errorGroup
     * @param validGroup
     */
    public void performDemerger(OriginEntryGroup errorGroup, OriginEntryGroup validGroup) {
        LOG.debug("performDemerger() started");

        // Without this step, the job fails with Optimistic Lock Exceptions
        persistenceService.clearCache();

        demergerReport = new DemergerReportData();

        OriginEntryStatistics eOes = originEntryService.getStatistics(errorGroup.getId());
        demergerReport.setErrorTransactionsRead(eOes.getRowCount());

        long start = System.currentTimeMillis();
        // Read all the documents from the error group and move all non-generated
        // transactions for these documents from the valid group into the error group
        Collection<OriginEntry> errorDocuments = originEntryService.getDocumentsByGroup(errorGroup);
        Iterator<OriginEntry> i = errorDocuments.iterator();
        while (i.hasNext()) {
            OriginEntry document = i.next();
            
            // Get all the transactions for the document in the valid group
            Iterator<OriginEntryLite> transactions = originEntryLiteService.getEntriesByDocument(validGroup, document.getDocumentNumber(), document.getFinancialDocumentTypeCode(), document.getFinancialSystemOriginationCode());

            while (transactions.hasNext()) {
                OriginEntryLite transaction = transactions.next();

                String transactionType = getTransactionType(transaction);

                if (TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE.equals(transactionType)) {
                    demergerReport.incrementCostShareEncumbranceTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_OFFSET.equals(transactionType)) {
                    demergerReport.incrementOffsetTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_CAPITALIZATION.equals(transactionType)) {
                    demergerReport.incrementCapitalizationTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_LIABILITY.equals(transactionType)) {
                    demergerReport.incrementLiabilityTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_TRANSFER.equals(transactionType)) {
                    demergerReport.incrementTransferTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
                    demergerReport.incrementCostShareTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else {
                    demergerReport.incrementErrorTransactionsSaved();
                    transaction.setEntryGroupId(errorGroup.getId());
                    originEntryLiteService.save(transaction);
                }
            }
        }
        LOG.fatal("Dem1 " + (System.currentTimeMillis() - start));
        
        start = System.currentTimeMillis();
        // Read all the transactions in the error group and delete the generated ones
        Iterator<OriginEntryLite> ie = originEntryLiteService.getEntriesByGroup(errorGroup);
        while (ie.hasNext()) {
            OriginEntryLite transaction = ie.next();

            String transactionType = getTransactionType(transaction);

            if (TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE.equals(transactionType)) {
                demergerReport.incrementCostShareEncumbranceTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_OFFSET.equals(transactionType)) {
                demergerReport.incrementOffsetTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_CAPITALIZATION.equals(transactionType)) {
                demergerReport.incrementCapitalizationTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_LIABILITY.equals(transactionType)) {
                demergerReport.incrementLiabilityTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_TRANSFER.equals(transactionType)) {
                demergerReport.incrementTransferTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
                demergerReport.incrementCostShareTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
        }
        LOG.fatal("Dem2 " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        // Read all the transactions in the valid group and update the cost share transactions
        Iterator<OriginEntryLite> it = originEntryLiteService.getEntriesByGroup(validGroup);
        while (it.hasNext()) {
            OriginEntryLite transaction = it.next();
            demergerReport.incrementValidTransactionsSaved();

            String transactionType = getTransactionType(transaction);
            if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
                transaction.setFinancialDocumentTypeCode(KFSConstants.TRANSFER_FUNDS);
                transaction.setFinancialSystemOriginationCode(KFSConstants.COST_SHARE);
                StringBuffer docNbr = new StringBuffer(COST_SHARE_CODE);

                String desc = transaction.getTransactionLedgerEntryDescription();

                docNbr.append(desc.substring(36, 38));
                docNbr.append("/");
                docNbr.append(desc.substring(38, 40));
                transaction.setDocumentNumber(docNbr.toString());

                transaction.setTransactionLedgerEntryDescription(desc.substring(0, DEMERGER_TRANSACTION_LEDGET_ENTRY_DESCRIPTION));

                originEntryLiteService.save(transaction);
            }
        }

        LOG.fatal("Dem3 " + (System.currentTimeMillis() - start));
        
        start = System.currentTimeMillis();
        
        eOes = originEntryService.getStatistics(errorGroup.getId());
        demergerReport.setErrorTransactionWritten(eOes.getRowCount());

        LOG.fatal("Dem4 " + (System.currentTimeMillis() - start));
        
        start = System.currentTimeMillis();
        
        if (!collectorMode) {
            reportService.generateScrubberDemergerStatisticsReports(runDate, demergerReport);
        }
        LOG.fatal("Dem5 " + (System.currentTimeMillis() - start));
        
    }

    /**
     * Determine the type of the transaction by looking at attributes
     * 
     * @param transaction Transaction to identify
     * @return CE (Cost share encumbrance, O (Offset), C (apitalization), L (Liability), T (Transfer), CS (Cost Share), X (Other)
     */
    public String getTransactionType(OriginEntryable transaction) {
        if (TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE.equals(transaction.getFinancialBalanceTypeCode())) {
            return TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE;
        }
        String desc = transaction.getTransactionLedgerEntryDescription();

        if (desc == null) {
            return TRANSACTION_TYPE_OTHER;
        }

        if (desc.startsWith(offsetDescription) && desc.indexOf(COST_SHARE_TRANSFER_ENTRY_IND) > -1) {
            return TRANSACTION_TYPE_COST_SHARE;
        }
        if (desc.startsWith(costShareDescription) && desc.indexOf(COST_SHARE_TRANSFER_ENTRY_IND) > -1) {
            return TRANSACTION_TYPE_COST_SHARE;
        }
        if (desc.startsWith(offsetDescription)) {
            return TRANSACTION_TYPE_OFFSET;
        }
        if (desc.startsWith(capitalizationDescription)) {
            return TRANSACTION_TYPE_CAPITALIZATION;
        }
        if (desc.startsWith(liabilityDescription)) {
            return TRANSACTION_TYPE_LIABILITY;
        }
        if (desc.startsWith(transferDescription)) {
            return TRANSACTION_TYPE_TRANSFER;
        }
        return TRANSACTION_TYPE_OTHER;
    }

    public OriginEntryable lastEntry = null;
    /**
     * This will process a group of origin entries.
     * 
     * The COBOL code was refactored a lot to get this so there isn't a 1 to 1 section of Cobol relating to this.
     * 
     * @param originEntryGroup Group to process
     */
    public void processGroup(OriginEntryGroup originEntryGroup) {
        this.referenceLookup.get().setLookupService(SpringContext.getBean(CachingLookup.class));

        OriginEntryable lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();

        LOG.info("Starting Scrubber Process process group... limit of " + TRANS_SIZE);
        Iterator entries = originEntryLiteService.getEntriesByGroup(originEntryGroup);
        while (entries.hasNext()) {
            // we have to copy the elements from the iterator b/c when we call the scrubber group service,
            // elements in the entries iterator will be inaccessible b/c the transaction is suspended
            scrubberGroupService.scrubGroup(this, entries);
        }

        if (!collectorMode) {
            // Generate last offset (if necessary)
            generateOffset(lastEntry);
        }
        
        this.referenceLookup.get().setLookupService(null);
    }

    public boolean isFatal(List<Message> errors) {
        for (Iterator<Message> iter = errors.iterator(); iter.hasNext();) {
            Message element = iter.next();
            if (element.getType() == Message.TYPE_FATAL) {
                return true;
            }
        }
        return false;
    }

    public KualiParameterRule getRule(String rule) {
        KualiParameterRule r = rules.get(rule);
        if (r == null) {
            throw new IllegalArgumentException("Business Rule: " + GLConstants.GL_SCRUBBER_GROUP + "/" + rule + " does not exist");
        }
        return r;
    }

    public FinancialSystemParameter getParameter(String param) {
        FinancialSystemParameter p = parameters.get(param);
        if (p == null) {
            throw new IllegalArgumentException("Financial System Parameter: " + GLConstants.GL_SCRUBBER_GROUP + "/" + param + " does not exist");
        }
        return p;
    }

    /**
     * 3000-COST-SHARE to 3100-READ-OFSD in the cobol
     * 
     * Generate Cost Share Entries
     * 
     * @param scrubbedEntry
     */
    public TransactionError generateCostShareEntries(OriginEntryable scrubbedEntry) {
        LOG.debug("generateCostShareEntries() started");

        OriginEntry costShareEntry = OriginEntry.copyFromOriginEntryable(scrubbedEntry);
        
        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);

        costShareEntry.setFinancialObjectCode((getParameter(GLConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE)).getFinancialSystemParameterText());
        costShareEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        costShareEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinancialObjectTypeTransferExpenseCd());
        costShareEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        StringBuffer description = new StringBuffer();
        description.append(costShareDescription);
        description.append(" ").append(scrubbedEntry.getAccountNumber());
        description.append(offsetString);
        costShareEntry.setTransactionLedgerEntryDescription(description.toString());

        costShareEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount);
        if (scrubCostShareAmount.isPositive()) {
            costShareEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            costShareEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            costShareEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount.negated());
        }

        costShareEntry.setTransactionDate(runDate);
        costShareEntry.setOrganizationDocumentNumber(null);
        costShareEntry.setProjectCode(KFSConstants.getDashProjectCode());
        costShareEntry.setOrganizationReferenceId(null);
        costShareEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareEntry.setReferenceFinancialDocumentNumber(null);
        costShareEntry.setFinancialDocumentReversalDate(null);
        costShareEntry.setTransactionEncumbranceUpdateCode(null);

        createOutputEntry(costShareEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntry costShareOffsetEntry = new OriginEntry(costShareEntry);
        costShareOffsetEntry.setTransactionLedgerEntryDescription(getOffsetMessage());

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), KFSConstants.TRANSFER_FUNDS, scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if (offsetDefinition.getFinancialObject() == null) {
                StringBuffer objectCodeKey = new StringBuffer();
                objectCodeKey.append(offsetDefinition.getUniversityFiscalYear());
                objectCodeKey.append("-").append(offsetDefinition.getChartOfAccountsCode());
                objectCodeKey.append("-").append(offsetDefinition.getFinancialObjectCode());

                Message m = new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND) + " (" + objectCodeKey.toString() + ")", Message.TYPE_FATAL);
                LOG.debug("generateCostShareEntries() Error 1 object not found");
                return new TransactionError(costShareEntry, m);
            }

            costShareOffsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            costShareOffsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            costShareOffsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

        }
        else {
            Map<Transaction, List<Message>> errors = new HashMap<Transaction, List<Message>>();

            StringBuffer offsetKey = new StringBuffer("cost share transfer ");
            offsetKey.append(scrubbedEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(scrubbedEntry.getChartOfAccountsCode());
            offsetKey.append("-TF-");
            offsetKey.append(scrubbedEntry.getFinancialBalanceTypeCode());

            Message m = new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + " (" + offsetKey.toString() + ")", Message.TYPE_FATAL);

            LOG.debug("generateCostShareEntries() Error 2 offset not found");
            return new TransactionError(costShareEntry, m);
        }

        costShareOffsetEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());

        if (costShareEntry.isCredit()) {
            costShareOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            costShareOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }

        try {
            flexibleOffsetAccountService.updateOffset(costShareOffsetEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            Message m = new Message(e.getMessage(), Message.TYPE_FATAL);
            LOG.debug("generateCostShareEntries() Cost Share Transfer Flexible Offset Error: " + e.getMessage());
            return new TransactionError(costShareEntry, m);
        }

        createOutputEntry(costShareOffsetEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntry costShareSourceAccountEntry = new OriginEntry(costShareEntry);

        description = new StringBuffer();
        description.append(costShareDescription);
        description.append(" ").append(scrubbedEntry.getAccountNumber());
        description.append(offsetString);
        costShareSourceAccountEntry.setTransactionLedgerEntryDescription(description.toString());

        costShareSourceAccountEntry.setChartOfAccountsCode(scrubbedEntryA21SubAccount.getCostShareChartOfAccountCode());
        costShareSourceAccountEntry.setAccountNumber(scrubbedEntryA21SubAccount.getCostShareSourceAccountNumber());

        setCostShareObjectCode(costShareSourceAccountEntry, scrubbedEntry);
        costShareSourceAccountEntry.setSubAccountNumber(scrubbedEntryA21SubAccount.getCostShareSourceSubAccountNumber());

        if (StringHelper.isNullOrEmpty(costShareSourceAccountEntry.getSubAccountNumber())) {
            costShareSourceAccountEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        costShareSourceAccountEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        costShareSourceAccountEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinancialObjectTypeTransferExpenseCd());
        costShareSourceAccountEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        costShareSourceAccountEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount);
        if (scrubCostShareAmount.isPositive()) {
            costShareSourceAccountEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            costShareSourceAccountEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            costShareSourceAccountEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount.negated());
        }

        costShareSourceAccountEntry.setTransactionDate(runDate);
        costShareSourceAccountEntry.setOrganizationDocumentNumber(null);
        costShareSourceAccountEntry.setProjectCode(KFSConstants.getDashProjectCode());
        costShareSourceAccountEntry.setOrganizationReferenceId(null);
        costShareSourceAccountEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareSourceAccountEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareSourceAccountEntry.setReferenceFinancialDocumentNumber(null);
        costShareSourceAccountEntry.setFinancialDocumentReversalDate(null);
        costShareSourceAccountEntry.setTransactionEncumbranceUpdateCode(null);

        createOutputEntry(costShareSourceAccountEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntry costShareSourceAccountOffsetEntry = new OriginEntry(costShareSourceAccountEntry);
        costShareSourceAccountOffsetEntry.setTransactionLedgerEntryDescription(getOffsetMessage());

        // Lookup the new offset definition.
        offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), KFSConstants.TRANSFER_FUNDS, scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if (offsetDefinition.getFinancialObject() == null) {
                Map<Transaction, List<Message>> errors = new HashMap<Transaction, List<Message>>();

                StringBuffer objectCodeKey = new StringBuffer();
                objectCodeKey.append(costShareEntry.getUniversityFiscalYear());
                objectCodeKey.append("-").append(scrubbedEntry.getChartOfAccountsCode());
                objectCodeKey.append("-").append(scrubbedEntry.getFinancialObjectCode());

                Message m = new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND) + " (" + objectCodeKey.toString() + ")", Message.TYPE_FATAL);

                LOG.debug("generateCostShareEntries() Error 3 object not found");
                return new TransactionError(costShareSourceAccountEntry, m);
            }

            costShareSourceAccountOffsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            costShareSourceAccountOffsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            costShareSourceAccountOffsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            Map<Transaction, List<Message>> errors = new HashMap<Transaction, List<Message>>();

            StringBuffer offsetKey = new StringBuffer("cost share transfer source ");
            offsetKey.append(scrubbedEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(scrubbedEntry.getChartOfAccountsCode());
            offsetKey.append("-TF-");
            offsetKey.append(scrubbedEntry.getFinancialBalanceTypeCode());

            Message m = new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + " (" + offsetKey.toString() + ")", Message.TYPE_FATAL);

            LOG.debug("generateCostShareEntries() Error 4 offset not found");
            return new TransactionError(costShareSourceAccountEntry, m);
        }

        costShareSourceAccountOffsetEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());

        if (scrubbedEntry.isCredit()) {
            costShareSourceAccountOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            costShareSourceAccountOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }

        try {
            flexibleOffsetAccountService.updateOffset(costShareSourceAccountOffsetEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            Message m = new Message(e.getMessage(), Message.TYPE_FATAL);
            LOG.debug("generateCostShareEntries() Cost Share Transfer Account Flexible Offset Error: " + e.getMessage());
            return new TransactionError(costShareEntry, m);
        }

        createOutputEntry(costShareSourceAccountOffsetEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        scrubCostShareAmount = KualiDecimal.ZERO;

        LOG.debug("generateCostShareEntries() successful");
        return null;
    }

    /**
     * Get all the transaction descriptions from the param table
     * 
     */
    public void setDescriptions() {
        offsetDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_OFFSET);
        capitalizationDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_CAPITALIZATION);
        liabilityDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_LIABILITY);
        costShareDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_COST_SHARE);
        transferDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_TRANSFER);
    }

    /**
     * Generate the flag for the end of specific descriptions. This will be used in the demerger step
     * 
     */
    public void setOffsetString() {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setMaximumIntegerDigits(2);
        nf.setMinimumFractionDigits(0);
        nf.setMinimumIntegerDigits(2);

        offsetString = COST_SHARE_TRANSFER_ENTRY_IND + nf.format(runCal.get(Calendar.MONTH) + 1) + nf.format(runCal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Generate the offset message with the flag at the end
     * 
     * 
     * @return Offset message
     */
    public String getOffsetMessage() {
        String msg = offsetDescription + GLConstants.getSpaceTransactionLedgetEntryDescription();

        return msg.substring(0, OFFSET_MESSAGE_MAXLENGTH) + offsetString;
    }

    /**
     * Lines 4694 - 4798 of the Pro Cobol listing on Confluence
     * 
     * Generate capitalization entries if necessary
     * 
     * @param scrubbedEntry
     * @return null if no error, message if error
     */
    public String processCapitalization(OriginEntryable scrubbedEntry) {
        if (!KFSConstants.ACTIVE_INDICATOR.equals((getParameter(GLConstants.GlScrubberGroupParameters.CAPITALIZATION_IND)).getFinancialSystemParameterText())) {
            return null;
        }

        OriginEntry capitalizationEntry = OriginEntry.copyFromOriginEntryable(scrubbedEntry);

        KualiParameterRule documentTypeCodes = getRule(GLConstants.GlScrubberGroupRules.CAPITALIZATION_DOC_TYPE_CODES);
        KualiParameterRule fiscalPeriodCodes = getRule(GLConstants.GlScrubberGroupRules.CAPITALIZATION_FISCAL_PERIOD_CODES);
        KualiParameterRule objectSubTypeCodes = getRule(GLConstants.GlScrubberGroupRules.CAPITALIZATION_OBJ_SUB_TYPE_CODES);
        KualiParameterRule subFundGroupCodes = getRule(GLConstants.GlScrubberGroupRules.CAPITALIZATION_SUB_FUND_GROUP_CODES);
        KualiParameterRule chartCodes = getRule(GLConstants.GlScrubberGroupRules.CAPITALIZATION_CHART_CODES);
        
        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && documentTypeCodes.succeedsRule(scrubbedEntry.getFinancialDocumentTypeCode()) && fiscalPeriodCodes.succeedsRule(scrubbedEntry.getUniversityFiscalPeriodCode()) && objectSubTypeCodes.succeedsRule(scrubbedEntryObjectCode.getFinancialObjectSubTypeCode()) && subFundGroupCodes.succeedsRule(scrubbedEntryAccount.getSubFundGroupCode()) && chartCodes.succeedsRule(scrubbedEntry.getChartOfAccountsCode())) {

            String objectSubTypeCode = scrubbedEntryObjectCode.getFinancialObjectSubTypeCode();

            FinancialSystemParameter objectParameter = parameters.get(GLConstants.GlScrubberGroupParameters.CAPITALIZATION_SUBTYPE_OBJECT_PREFIX + objectSubTypeCode);
            if (objectParameter != null) {
                capitalizationEntry.setFinancialObjectCode(objectParameter.getFinancialSystemParameterText());
                persistenceService.retrieveReferenceObject(capitalizationEntry, KFSPropertyConstants.FINANCIAL_OBJECT);
            }

            capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinancialObjectTypeAssetsCd());
            persistenceService.retrieveReferenceObject(capitalizationEntry, KFSPropertyConstants.OBJECT_TYPE);
            capitalizationEntry.setTransactionLedgerEntryDescription(capitalizationDescription);

            plantFundAccountLookup(scrubbedEntry, capitalizationEntry);

            capitalizationEntry.setUniversityFiscalPeriodCode(scrubbedEntry.getUniversityFiscalPeriodCode());

            createOutputEntry(capitalizationEntry, validGroup);
            scrubberReport.incrementCapitalizationEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            capitalizationEntry.setVersionNumber(null);
            capitalizationEntry.setEntryId(null);

            capitalizationEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
            capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());

            if (scrubbedEntry.isDebit()) {
                capitalizationEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                capitalizationEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            try {
                flexibleOffsetAccountService.updateOffset(capitalizationEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.debug("processCapitalization() Capitalization Flexible Offset Error: " + e.getMessage());
                return e.getMessage();
            }

            createOutputEntry(capitalizationEntry, validGroup);
            scrubberReport.incrementCapitalizationEntryGenerated();
        }
        return null;
    }

    /**
     * Lines 4855 - 4979 of the Pro Cobol listing on Confluence
     * 
     * Generate the plant indebtedness entries
     * 
     * @param scrubbedEntry
     * @return null if no error, message if error
     */
    public String processPlantIndebtedness(OriginEntryable scrubbedEntry) {
        if (!KFSConstants.ACTIVE_INDICATOR.equals((getParameter(GLConstants.GlScrubberGroupParameters.PLANT_INDEBTEDNESS_IND)).getFinancialSystemParameterText())) {
            return null;
        }

        OriginEntry plantIndebtednessEntry = OriginEntry.copyFromOriginEntryable(scrubbedEntry);

        KualiParameterRule objectSubTypeCodes = getRule(GLConstants.GlScrubberGroupRules.PLANT_INDEBTEDNESS_OBJ_SUB_TYPE_CODES);
        KualiParameterRule subFundGroupCodes = getRule(GLConstants.GlScrubberGroupRules.PLANT_INDEBTEDNESS_SUB_FUND_GROUP_CODES);
        
        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);
        Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && 
                subFundGroupCodes.succeedsRule(scrubbedEntryAccount.getSubFundGroupCode()) && 
                objectSubTypeCodes.succeedsRule(scrubbedEntryObjectCode.getFinancialObjectSubTypeCode())) {

            plantIndebtednessEntry.setTransactionLedgerEntryDescription(KFSConstants.PLANT_INDEBTEDNESS_ENTRY_DESCRIPTION);

            if (scrubbedEntry.isDebit()) {
                plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

            plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
            plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
            plantIndebtednessEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

            try {
                flexibleOffsetAccountService.updateOffset(plantIndebtednessEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.error("processPlantIndebtedness() Flexible Offset Exception (1)",e);
                LOG.debug("processPlantIndebtedness() Plant Indebtedness Flexible Offset Error: " + e.getMessage());
                return e.getMessage();
            }

            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

            plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntry.getFinancialObjectCode());
            plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntry.getFinancialObjectTypeCode());
            plantIndebtednessEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

            plantIndebtednessEntry.setTransactionLedgerEntryDescription(scrubbedEntry.getTransactionLedgerEntryDescription());

            plantIndebtednessEntry.setAccountNumber(scrubbedEntry.getAccountNumber());
            plantIndebtednessEntry.setSubAccountNumber(scrubbedEntry.getSubAccountNumber());

            if (scrubbedEntry.getChartOfAccountsCode().equals(scrubbedEntryAccount.getOrganization().getChartOfAccountsCode()) &&
                    scrubbedEntryAccount.getOrganizationCode().equals(scrubbedEntryAccount.getOrganizationCode()) && 
                    scrubbedEntry.getAccountNumber().equals(scrubbedEntryAccount.getAccountNumber()) && 
                    scrubbedEntry.getChartOfAccountsCode().equals(scrubbedEntryAccount.getChartOfAccountsCode())) {
                plantIndebtednessEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getCampusPlantAccountNumber());
                plantIndebtednessEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getCampusPlantChartCode());
            }

            plantIndebtednessEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

            StringBuffer litGenPlantXferFrom = new StringBuffer();
            litGenPlantXferFrom.append(transferDescription + " ");
            litGenPlantXferFrom.append(scrubbedEntry.getChartOfAccountsCode()).append(" ");
            litGenPlantXferFrom.append(scrubbedEntry.getAccountNumber());
            plantIndebtednessEntry.setTransactionLedgerEntryDescription(litGenPlantXferFrom.toString());

            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

            plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
            plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
            plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

            if (scrubbedEntry.isDebit()) {
                plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            try {
                flexibleOffsetAccountService.updateOffset(plantIndebtednessEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.error("processPlantIndebtedness() Flexible Offset Exception (2)",e);
                LOG.debug("processPlantIndebtedness() Plant Indebtedness Flexible Offset Error: " + e.getMessage());
                return e.getMessage();
            }

            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();
        }

        return null;
    }

    /**
     * Lines 4799 to 4839 of the Pro Cobol list of the scrubber on Confluence
     * 
     * Generate the liability entries
     * 
     * @param scrubbedEntry
     * @return null if no error, message if error
     */
    public String processLiabilities(OriginEntryable scrubbedEntry) {
        if (!KFSConstants.ACTIVE_INDICATOR.equals((getParameter(GLConstants.GlScrubberGroupParameters.LIABILITY_IND)).getFinancialSystemParameterText())) {
            return null;
        }

        OriginEntry liabilityEntry = OriginEntry.copyFromOriginEntryable(scrubbedEntry);

        KualiParameterRule chartCodes = getRule(GLConstants.GlScrubberGroupRules.LIABILITY_CHART_CODES);
        KualiParameterRule docTypeCodes = getRule(GLConstants.GlScrubberGroupRules.LIABILITY_DOC_TYPE_CODES);
        KualiParameterRule fiscalPeriods = getRule(GLConstants.GlScrubberGroupRules.LIABILITY_FISCAL_PERIOD_CODES);
        KualiParameterRule objSubTypeCodes = getRule(GLConstants.GlScrubberGroupRules.LIABILITY_OBJ_SUB_TYPE_CODES);
        KualiParameterRule subFundGroupCodes = getRule(GLConstants.GlScrubberGroupRules.LIABILITY_SUB_FUND_GROUP_CODES);
        
        Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);
        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        ObjectCode scrubbedEntryFinancialObject = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && docTypeCodes.succeedsRule(scrubbedEntry.getFinancialDocumentTypeCode()) && fiscalPeriods.succeedsRule(scrubbedEntry.getUniversityFiscalPeriodCode()) && objSubTypeCodes.succeedsRule(scrubbedEntryFinancialObject.getFinancialObjectSubTypeCode()) && subFundGroupCodes.succeedsRule(scrubbedEntryAccount.getSubFundGroupCode()) && chartCodes.succeedsRule(scrubbedEntry.getChartOfAccountsCode())) {

            liabilityEntry.setFinancialObjectCode((getParameter(GLConstants.GlScrubberGroupParameters.LIABILITY_OBJECT_CODE)).getFinancialSystemParameterText());
            liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeLiabilitiesCode());

            liabilityEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());
            liabilityEntry.setTransactionLedgerEntryDescription(liabilityDescription);
            plantFundAccountLookup(scrubbedEntry, liabilityEntry);

            createOutputEntry(liabilityEntry, validGroup);
            scrubberReport.incrementLiabilityEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            liabilityEntry.setVersionNumber(null);
            liabilityEntry.setEntryId(null);

            // ... and now generate the offset half of the liability entry
            liabilityEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
            liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());

            if (liabilityEntry.isDebit()) {
                liabilityEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                liabilityEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            try {
                flexibleOffsetAccountService.updateOffset(liabilityEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.debug("processLiabilities() Liability Flexible Offset Error: " + e.getMessage());
                return e.getMessage();
            }

            createOutputEntry(liabilityEntry, validGroup);
            scrubberReport.incrementLiabilityEntryGenerated();
        }
        return null;
    }

    /**
     * 4000-PLANT-FUND-ACCT to 4000-PLANT-FUND-ACCT-EXIT in cobol
     * 
     * @param scrubbedEntry basis for plant fund entry
     * @param liabilityEntry liability entry
     */
    public void plantFundAccountLookup(OriginEntryable scrubbedEntry, OriginEntry liabilityEntry) {

        KualiParameterRule campusObjSubTypeCodes = getRule(GLConstants.GlScrubberGroupRules.PLANT_FUND_CAMPUS_OBJECT_SUB_TYPE_CODES);
        KualiParameterRule orgObjSubTypeCodes = getRule(GLConstants.GlScrubberGroupRules.PLANT_FUND_ORG_OBJECT_SUB_TYPE_CODES);

        liabilityEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.ACCOUNT);
        
        ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

        if (liabilityEntry.getChartOfAccountsCode().equals(liabilityEntry.getAccount().getOrganization().getChartOfAccountsCode()) && scrubbedEntryAccount.getOrganizationCode().equals(liabilityEntry.getAccount().getOrganization().getOrganizationCode()) && liabilityEntry.getAccountNumber().equals(liabilityEntry.getAccount().getAccountNumber()) && liabilityEntry.getChartOfAccountsCode().equals(liabilityEntry.getAccount().getChartOfAccountsCode())) {
            persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.FINANCIAL_OBJECT);
            persistenceService.retrieveReferenceObject(liabilityEntry.getAccount(), KFSPropertyConstants.ORGANIZATION);

            String objectSubTypeCode = scrubbedEntryObjectCode.getFinancialObjectSubTypeCode();

            if (campusObjSubTypeCodes.succeedsRule(objectSubTypeCode)) {
                liabilityEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getCampusPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getCampusPlantChartCode());

                persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.ACCOUNT);
                persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.CHART);
            }
            else if (orgObjSubTypeCodes.succeedsRule(objectSubTypeCode)) {
                liabilityEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getOrganizationPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getOrganizationPlantChartCode());

                persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.ACCOUNT);
                persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.CHART);
            }
        }
    }

    /**
     * 3200-COST-SHARE-ENC to 3200-CSE-EXIT in the COBOL
     * 
     * The purpose of this method is to generate a "Cost Share Encumbrance" transaction for the current transaction and its offset.
     * 
     * The cost share chart and account for current transaction are obtained from the CA_A21_SUB_ACCT_T table. This method calls the
     * method SET-OBJECT-2004 to get the Cost Share Object Code. It then writes out the cost share transaction. Next it read the
     * GL_OFFSET_DEFN_T table for the offset object code that corresponds to the cost share object code. In addition to the object
     * code it needs to get subobject code. It then reads the CA_OBJECT_CODE_T table to make sure the offset object code found in
     * the GL_OFFSET_DEFN_T is valid and to get the object type code associated with this object code. It writes out the offset
     * transaction and returns.
     * 
     * @param scrubbedEntry
     */
    public TransactionError generateCostShareEncumbranceEntries(OriginEntryable scrubbedEntry) {
        LOG.debug("generateCostShareEncumbranceEntries() started");

        OriginEntry costShareEncumbranceEntry = OriginEntry.copyFromOriginEntryable(scrubbedEntry);

        // First 28 characters of the description, padding to 28 if shorter)
        StringBuffer buffer = new StringBuffer((scrubbedEntry.getTransactionLedgerEntryDescription() + GLConstants.getSpaceTransactionLedgetEntryDescription()).substring(0, COST_SHARE_ENCUMBRANCE_ENTRY_MAXLENGTH));

        buffer.append("FR-");
        buffer.append(costShareEncumbranceEntry.getChartOfAccountsCode());
        buffer.append(costShareEncumbranceEntry.getAccountNumber());

        costShareEncumbranceEntry.setTransactionLedgerEntryDescription(buffer.toString());

        A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);
        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        
        costShareEncumbranceEntry.setChartOfAccountsCode(scrubbedEntryA21SubAccount.getCostShareChartOfAccountCode());
        costShareEncumbranceEntry.setAccountNumber(scrubbedEntryA21SubAccount.getCostShareSourceAccountNumber());
        costShareEncumbranceEntry.setSubAccountNumber(scrubbedEntryA21SubAccount.getCostShareSourceSubAccountNumber());

        if (!StringUtils.hasText(costShareEncumbranceEntry.getSubAccountNumber())) {
            costShareEncumbranceEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        costShareEncumbranceEntry.setFinancialBalanceTypeCode(scrubbedEntryOption.getCostShareEncumbranceBalanceTypeCd());
        setCostShareObjectCode(costShareEncumbranceEntry, scrubbedEntry);
        costShareEncumbranceEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        costShareEncumbranceEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        if (!StringUtils.hasText(scrubbedEntry.getTransactionDebitCreditCode())) {
            if (scrubbedEntry.getTransactionLedgerEntryAmount().isPositive()) {
                costShareEncumbranceEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }
            else {
                costShareEncumbranceEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                costShareEncumbranceEntry.setTransactionLedgerEntryAmount(scrubbedEntry.getTransactionLedgerEntryAmount().negated());
            }
        }

        costShareEncumbranceEntry.setTransactionDate(runDate);

        costShareEncumbranceEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareEncumbranceEntry, validGroup);
        scrubberReport.incrementCostShareEncumbranceGenerated();

        OriginEntry costShareEncumbranceOffsetEntry = new OriginEntry(costShareEncumbranceEntry);

        costShareEncumbranceOffsetEntry.setTransactionLedgerEntryDescription(offsetDescription);

        OffsetDefinition offset = offsetDefinitionService.getByPrimaryId(costShareEncumbranceEntry.getUniversityFiscalYear(), costShareEncumbranceEntry.getChartOfAccountsCode(), costShareEncumbranceEntry.getFinancialDocumentTypeCode(), costShareEncumbranceEntry.getFinancialBalanceTypeCode());

        if (offset != null) {
            if (offset.getFinancialObject() == null) {
                StringBuffer offsetKey = new StringBuffer();
                offsetKey.append(offset.getUniversityFiscalYear());
                offsetKey.append("-");
                offsetKey.append(offset.getChartOfAccountsCode());
                offsetKey.append("-");
                offsetKey.append(offset.getFinancialObjectCode());

                LOG.debug("generateCostShareEncumbranceEntries() object code not found");
                return new TransactionError(costShareEncumbranceEntry, new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD) + "(" + offsetKey.toString() + ")", Message.TYPE_FATAL));
            }
            costShareEncumbranceOffsetEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            costShareEncumbranceOffsetEntry.setFinancialObject(offset.getFinancialObject());
            costShareEncumbranceOffsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            StringBuffer offsetKey = new StringBuffer("Cost share encumbrance ");
            offsetKey.append(costShareEncumbranceEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getChartOfAccountsCode());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getFinancialDocumentTypeCode());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getFinancialBalanceTypeCode());

            LOG.debug("generateCostShareEncumbranceEntries() offset not found");
            return new TransactionError(costShareEncumbranceEntry, new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + "(" + offsetKey.toString() + ")", Message.TYPE_FATAL));
        }

        costShareEncumbranceOffsetEntry.setFinancialObjectTypeCode(offset.getFinancialObject().getFinancialObjectTypeCode());

        if (costShareEncumbranceEntry.isCredit()) {
            costShareEncumbranceOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            costShareEncumbranceOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }

        costShareEncumbranceOffsetEntry.setTransactionDate(runDate);
        costShareEncumbranceOffsetEntry.setOrganizationDocumentNumber(null);
        costShareEncumbranceOffsetEntry.setProjectCode(KFSConstants.getDashProjectCode());
        costShareEncumbranceOffsetEntry.setOrganizationReferenceId(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialDocumentNumber(null);
        costShareEncumbranceOffsetEntry.setReversalDate(null);
        costShareEncumbranceOffsetEntry.setTransactionEncumbranceUpdateCode(null);

        costShareEncumbranceOffsetEntry.setTransactionScrubberOffsetGenerationIndicator(true);

        try {
            flexibleOffsetAccountService.updateOffset(costShareEncumbranceOffsetEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            Message m = new Message(e.getMessage(), Message.TYPE_FATAL);
            LOG.debug("generateCostShareEncumbranceEntries() Cost Share Encumbrance Flexible Offset Error: " + e.getMessage());
            return new TransactionError(costShareEncumbranceOffsetEntry, m);
        }

        createOutputEntry(costShareEncumbranceOffsetEntry, validGroup);
        scrubberReport.incrementCostShareEncumbranceGenerated();

        LOG.debug("generateCostShareEncumbranceEntries() returned successfully");
        return null;
    }

    /**
     * This code is SET-OBJECT-2004 to 2520-INIT-SCRB-AREA in the Cobol
     * 
     * @param costShareEntry GL Entry for cost share
     * @param originEntry Scrubbed GL Entry that this is based on
     */
    public void setCostShareObjectCode(OriginEntry costShareEntry, OriginEntryable originEntry) {
        
        ObjectCode originEntryFinancialObject = referenceLookup.get().getFinancialObject(originEntry);

        if (originEntryFinancialObject == null) {
            addTransactionError(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND), originEntry.getFinancialObjectCode(), Message.TYPE_FATAL);
        }

        String originEntryObjectLevelCode = (originEntryFinancialObject == null) ? "" : originEntryFinancialObject.getFinancialObjectLevelCode();

        String financialOriginEntryObjectCode = originEntry.getFinancialObjectCode();
        String originEntryObjectCode = scrubberProcessObjectCodeOverride.getOriginEntryObjectCode(originEntryObjectLevelCode, financialOriginEntryObjectCode);

        // General rules
        if ( originEntryObjectCode.equals(financialOriginEntryObjectCode) ) {
            FinancialSystemParameter param = parameters.get(GLConstants.GlScrubberGroupParameters.COST_SHARE_LEVEL_OBJECT_PREFIX + originEntryObjectLevelCode);
            if ( param == null ) {
                param = getParameter(GLConstants.GlScrubberGroupParameters.COST_SHARE_LEVEL_OBJECT_DEFAULT);
                if ( param == null ) {
                    throw new IllegalArgumentException("Missing " + GLConstants.GL_SCRUBBER_GROUP + "/" + GLConstants.GlScrubberGroupParameters.COST_SHARE_LEVEL_OBJECT_DEFAULT + " parameter in system parameters table");
                } else {
                    originEntryObjectCode = param.getFinancialSystemParameterText();
                }
            } else {
                if ( param.getFinancialSystemParameterText() == null ) {
                    // Don't do anything with the object code
                } else {
                    originEntryObjectCode = param.getFinancialSystemParameterText();                    
                }
            }
        }

        // Lookup the new object code
        ObjectCode objectCode = objectCodeService.getByPrimaryId(costShareEntry.getUniversityFiscalYear(), costShareEntry.getChartOfAccountsCode(), originEntryObjectCode);
        if (objectCode != null) {
            costShareEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            costShareEntry.setFinancialObjectCode(originEntryObjectCode);
        }
        else {
            addTransactionError(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_COST_SHARE_OBJECT_NOT_FOUND), costShareEntry.getFinancialObjectCode(), Message.TYPE_FATAL);
        }
    }

    /**
     * The purpose of this method is to build the actual offset transaction. It does this by performing the following steps: 1.
     * Getting the offset object code and offset subobject code from the GL Offset Definition Table. 2. For the offset object code
     * it needs to get the associated object type, object subtype, and object active code.
     * 
     * This code is 3000-OFFSET to SET-OBJECT-2004 in the Cobol
     * 
     * @param scrubbedEntry
     */
    public boolean generateOffset(OriginEntryable scrubbedEntry) {
        LOG.debug("generateOffset() started");

        // There was no previous unit of work so we need no offset
        if (scrubbedEntry == null) {
            return true;
        }

        // If the offset amount is zero, don't bother to lookup the offset definition ...
        if (unitOfWork.offsetAmount.isZero()) {
            return true;
        }

        KualiParameterRule docTypeRule = getRule(GLConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES);
        if (docTypeRule.failsRule(scrubbedEntry.getFinancialDocumentTypeCode())) {
            return true;
        }

        // do nothing if flexible offset is enabled and scrubber offset indicator of the document
        // type code is turned off in the document type table
        String documentTypeCode = scrubbedEntry.getFinancialDocumentTypeCode();
        DocumentType documentType = documentTypeService.getDocumentTypeByCode(documentTypeCode);
        if ((!documentType.isTransactionScrubberOffsetGenerationIndicator()) && flexibleOffsetAccountService.getEnabled()) {
            return true;
        }

        // Create an offset
        OriginEntry offsetEntry = OriginEntry.copyFromOriginEntryable(scrubbedEntry);
        offsetEntry.setTransactionLedgerEntryDescription(offsetDescription);

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialDocumentTypeCode(), scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if (offsetDefinition.getFinancialObject() == null) {
                StringBuffer offsetKey = new StringBuffer(offsetDefinition.getUniversityFiscalYear());
                offsetKey.append("-");
                offsetKey.append(offsetDefinition.getChartOfAccountsCode());
                offsetKey.append("-");
                offsetKey.append(offsetDefinition.getFinancialObjectCode());

                putTransactionError(offsetEntry, kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND), offsetKey.toString(), Message.TYPE_FATAL);
                
                createOutputEntry(offsetEntry, errorGroup);
                scrubberReport.incrementErrorRecordWritten();
                return false;
            }

            offsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            offsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());

            offsetEntry.setFinancialSubObject(null);
            offsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            StringBuffer sb = new StringBuffer("Unit of work offset ");
            sb.append(scrubbedEntry.getUniversityFiscalYear());
            sb.append("-");
            sb.append(scrubbedEntry.getChartOfAccountsCode());
            sb.append("-");
            sb.append(scrubbedEntry.getFinancialDocumentTypeCode());
            sb.append("-");
            sb.append(scrubbedEntry.getFinancialBalanceTypeCode());

            putTransactionError(offsetEntry, kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), sb.toString(), Message.TYPE_FATAL);

            createOutputEntry(offsetEntry, errorGroup);
            scrubberReport.incrementErrorRecordWritten();
            return false;
        }

        offsetEntry.setFinancialObjectTypeCode(offsetEntry.getFinancialObject().getFinancialObjectTypeCode());
        offsetEntry.setTransactionLedgerEntryAmount(unitOfWork.offsetAmount);

        if (unitOfWork.offsetAmount.isPositive()) {
            offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            offsetEntry.setTransactionLedgerEntryAmount(unitOfWork.offsetAmount.negated());
        }

        offsetEntry.setOrganizationDocumentNumber(null);
        offsetEntry.setOrganizationReferenceId(null);
        offsetEntry.setReferenceFinancialDocumentTypeCode(null);
        offsetEntry.setReferenceDocumentType(null);
        offsetEntry.setReferenceFinancialSystemOriginationCode(null);
        offsetEntry.setReferenceFinancialDocumentNumber(null);
        offsetEntry.setTransactionEncumbranceUpdateCode(null);
        offsetEntry.setProjectCode(KFSConstants.getDashProjectCode());
        offsetEntry.setTransactionDate(runDate);

        try {
            flexibleOffsetAccountService.updateOffset(offsetEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            LOG.debug("generateOffset() Offset Flexible Offset Error: " + e.getMessage());
            putTransactionError(offsetEntry, e.getMessage(), "", Message.TYPE_FATAL);
            return true;
        }

        createOutputEntry(offsetEntry, validGroup);
        scrubberReport.incrementOffsetEntryGenerated();
        return true;
    }

    /**
     * Save an entry in origin entry
     * 
     * @param entry Entry to save
     * @param group Group to save it in
     */
    public void createOutputEntry(OriginEntryable entry, OriginEntryGroup group) {
        // Write the entry if we aren't running in report only or collector mode.
        if ( reportOnlyMode || collectorMode ) {
            // If the group is null don't write it because the error and expired groups aren't created in reportOnlyMode 
            if ( group != null ) {
                entry.setEntryGroupId(group.getId());
                originEntryLiteService.save((OriginEntryLite)entry);
            }
        }
        else {
            entry.setEntryGroupId(group.getId());
            originEntryLiteService.save((OriginEntryLite)entry);
        }
    }

    /**
     * If object is null, generate an error
     * 
     * @param glObject object to test
     * @param errorMessage error message if glObject is null
     * @param errorValue value of glObject to print in the error message
     * @param type Type of message (fatal or warning)
     * @return true of glObject is null
     */
    public boolean ifNullAddTransactionErrorAndReturnFalse(Object glObject, String errorMessage, String errorValue, int type) {
        if (glObject == null) {
            if (StringUtils.hasText(errorMessage)) {
                addTransactionError(errorMessage, errorValue, type);
            }
            else {
                addTransactionError("Unexpected null object", glObject.getClass().getName(), type);
            }
            return false;
        }
        return true;
    }

    /**
     * Add an error message to the list of messages for this transaction
     * 
     * @param errorMessage Error message
     * @param errorValue Value that is in error
     * @param type Type of error (Fatal or Warning)
     */
    public void addTransactionError(String errorMessage, String errorValue, int type) {
        transactionErrors.add(new Message(errorMessage + " (" + errorValue + ")", type));
    }

    public void putTransactionError(Transaction s, String errorMessage, String errorValue, int type) {
        List te = new ArrayList();
        te.add(new Message(errorMessage + "(" + errorValue + ")", type));
        scrubberReportErrors.put(s, te);
    }

    public class UnitOfWorkInfo {
        // Unit of work key
        public Integer univFiscalYr = 0;
        public String finCoaCd = "";
        public String accountNbr = "";
        public String subAcctNbr = "";
        public String finBalanceTypCd = "";
        public String fdocTypCd = "";
        public String fsOriginCd = "";
        public String fdocNbr = "";
        public Date fdocReversalDt = new Date(dateTimeService.getCurrentDate().getTime());
        public String univFiscalPrdCd = "";

        // Data about unit of work
        public boolean entryMode = true;
        public KualiDecimal offsetAmount = KualiDecimal.ZERO;
        public String scrbFinCoaCd;
        public String scrbAccountNbr;

        public UnitOfWorkInfo() {
        }

        public UnitOfWorkInfo(OriginEntryable e) {
            univFiscalYr = e.getUniversityFiscalYear();
            finCoaCd = e.getChartOfAccountsCode();
            accountNbr = e.getAccountNumber();
            subAcctNbr = e.getSubAccountNumber();
            finBalanceTypCd = e.getFinancialBalanceTypeCode();
            fdocTypCd = e.getFinancialDocumentTypeCode();
            fsOriginCd = e.getFinancialSystemOriginationCode();
            fdocNbr = e.getDocumentNumber();
            fdocReversalDt = e.getFinancialDocumentReversalDate();
            univFiscalPrdCd = e.getUniversityFiscalPeriodCode();
        }

        public boolean isSameUnitOfWork(OriginEntryable e) {
            // Compare the key fields
            return univFiscalYr.equals(e.getUniversityFiscalYear()) && finCoaCd.equals(e.getChartOfAccountsCode()) && accountNbr.equals(e.getAccountNumber()) && subAcctNbr.equals(e.getSubAccountNumber()) && finBalanceTypCd.equals(e.getFinancialBalanceTypeCode()) && fdocTypCd.equals(e.getFinancialDocumentTypeCode()) && fsOriginCd.equals(e.getFinancialSystemOriginationCode()) && fdocNbr.equals(e.getDocumentNumber()) && ObjectHelper.isEqual(fdocReversalDt, e.getFinancialDocumentReversalDate()) && univFiscalPrdCd.equals(e.getUniversityFiscalPeriodCode());
        }

        public String toString() {
            return univFiscalYr + finCoaCd + accountNbr + subAcctNbr + finBalanceTypCd + fdocTypCd + fsOriginCd + fdocNbr + fdocReversalDt + univFiscalPrdCd;
        }

        public OriginEntry getOffsetTemplate() {
            OriginEntry e = new OriginEntry();
            e.setUniversityFiscalYear(univFiscalYr);
            e.setChartOfAccountsCode(finCoaCd);
            e.setAccountNumber(accountNbr);
            e.setSubAccountNumber(subAcctNbr);
            e.setFinancialBalanceTypeCode(finBalanceTypCd);
            e.setFinancialDocumentTypeCode(fdocTypCd);
            e.setFinancialSystemOriginationCode(fsOriginCd);
            e.setDocumentNumber(fdocNbr);
            e.setFinancialDocumentReversalDate(fdocReversalDt);
            e.setUniversityFiscalPeriodCode(univFiscalPrdCd);
            return e;
        }
    }

    public class TransactionError {
        public Transaction transaction;
        public Message message;

        public TransactionError(Transaction t, Message m) {
            transaction = t;
            message = m;
        }
    }
        
    /**
     * This method modifies the run date if it is before the cutoff time specified by the RunTimeService
     * 
     * See https://test.kuali.org/jira/browse/KULRNE-70
     * 
     * This method is public to facilitate unit testing
     * 
     * @param currentDate
     * @return
     */
    public Date calculateRunDate(java.util.Date currentDate) {
        return new Date(runDateService.calculateRunDate(currentDate).getTime());
    }

    /**
     * Sets the referenceLookup attribute value.
     * @param referenceLookup The referenceLookup to set.
     */
    public void setReferenceLookup(OriginEntryableLookupService referenceLookup) {
        this.referenceLookup.set(referenceLookup);
        this.scrubberValidator.setReferenceLookup(referenceLookup);
    }

    public ScrubberGroupService getScrubberGroupService() {
        return scrubberGroupService;
    }

    public void setScrubberGroupService(ScrubberGroupService scrubberGroupService) {
        this.scrubberGroupService = scrubberGroupService;
    }
    
    public static final int TRANS_SIZE = 500;
}
