/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AgencyServiceFee;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.util.ObjectUtils;

@SuppressWarnings("deprecation")
public class ImportedExpensePendingEntryServiceImpl implements ImportedExpensePendingEntryService{

    private static Logger LOG = org.apache.log4j.Logger.getLogger(ImportedExpensePendingEntryServiceImpl.class);

    DateTimeService dateTimeService;
    UniversityDateService universityDateService;
    ParameterService parameterService;
    GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    TravelDocumentService travelDocumentService;
    ObjectCodeService objectCodeService;
    DataDictionaryService dataDictionaryService;


    /**
     * @see org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService#checkAndAddPendingEntriesToList(java.util.List, org.kuali.kfs.module.tem.businessobject.AgencyStagingData, boolean, boolean)
     */
    @Override
    public boolean checkAndAddPendingEntriesToList(List<GeneralLedgerPendingEntry> pendingEntries, List<GeneralLedgerPendingEntry> entryList, AgencyStagingData agencyData, boolean isCredit, boolean generateOffset){
        boolean result = true;
        //if offset is expected, there should be two entries, otherwise only one
        int expectedEntrySize = generateOffset? 2 : 1;

        if (pendingEntries.size() != expectedEntrySize){
            LOG.error("Failed to create a " + (isCredit? " CREDIT" : "DEBIT") + " GLPE for agency: " + agencyData.getId() + " tripId: " + agencyData.getTripId());
            result = false;
        }else{
            //validated, add to list
            entryList.addAll(pendingEntries);
        }

        return result;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService#buildGeneralLedgerPendingEntry(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.module.tem.businessobject.TripAccountingInformation, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, java.lang.String, java.lang.String, org.kuali.rice.kns.util.KualiDecimal, java.lang.String)
     */
    @Override
    public GeneralLedgerPendingEntry buildGeneralLedgerPendingEntry(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper,
            String chartCode, String objectCode, KualiDecimal amount, String glCredtiDebitCode){

        ObjectCode objectCd = getObjectCodeService().getByPrimaryIdForCurrentYear(chartCode, objectCode);
        if (ObjectUtils.isNull(objectCd)) {
            LOG.error("ERROR: Could not get an ObjectCode for chart code: " + chartCode + " object code: " + objectCode);
            return null;
        }

        GeneralLedgerPendingEntry glpe = buildBasicDistributionPendingEntry(sequenceHelper);
        //setup document number by agency data
        glpe.setDocumentNumber(getImportExpenseDocumentNumber(agencyData));
        // customizations for glpe
        glpe.setChartOfAccountsCode(chartCode);
        glpe.setFinancialObjectCode(objectCode);
        glpe.setFinancialSubObjectCode(StringUtils.defaultIfEmpty(info.getSubObjectCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode()));
        glpe.setFinancialObjectTypeCode(objectCd.getFinancialObjectTypeCode());
        glpe.setProjectCode(StringUtils.defaultIfEmpty(info.getProjectCode(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode()));
        glpe.setTransactionLedgerEntryAmount(amount);
        glpe.setTransactionDebitCreditCode(glCredtiDebitCode);
        glpe.setTransactionDate(agencyData.getTransactionPostingDate());
        glpe.setOrganizationDocumentNumber(StringUtils.isNotEmpty(agencyData.getTravelerId())? agencyData.getTravelerId() : agencyData.getTripId());
        glpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);

        return glpe;
    }

    /**
     * Builds a really basic distribution pending entry from a sequence helper; this can be used as the basis for other customizations by different methods
     * @param sequenceHelper the sequence helper to help us set a sequence number on the GLPE
     * @return the beginnings of a GLPE
     */
    protected GeneralLedgerPendingEntry buildBasicDistributionPendingEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        GeneralLedgerPendingEntry glpe = new GeneralLedgerPendingEntry();
        final String DIST_INCOME_DOC_TYPE = KFSConstants.FinancialDocumentTypeCodes.DISTRIBUTION_OF_INCOME_AND_EXPENSE;


        glpe.setVersionNumber(new Long(1));
        glpe.setUniversityFiscalYear(universityDateService.getCurrentFiscalYear());
        glpe.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        glpe.setFinancialDocumentTypeCode(DIST_INCOME_DOC_TYPE);
        glpe.setFinancialSystemOriginationCode(TemConstants.TEM_IMPORTED_SYS_ORIG_CD);
        glpe.setTransactionLedgerEntryDescription(TemConstants.TEM_IMPORTED_GLPE_DESC);
        glpe.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        glpe.setOrganizationReferenceId(DIST_INCOME_DOC_TYPE +TemConstants.IMPORTED_FLAG);

        sequenceHelper.increment();

        return glpe;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService#buildDebitPendingEntry(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.module.tem.businessobject.TripAccountingInformation, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, java.lang.String, org.kuali.rice.kns.util.KualiDecimal, boolean)
     */
    @Override
    public List<GeneralLedgerPendingEntry> buildDebitPendingEntry(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String objectCode, KualiDecimal amount, boolean generateOffset){

        List<GeneralLedgerPendingEntry> entryList = new ArrayList<GeneralLedgerPendingEntry>();

        GeneralLedgerPendingEntry pendingEntry = buildGeneralLedgerPendingEntry(agencyData, info, sequenceHelper, info.getTripChartCode(), objectCode, amount, KFSConstants.GL_DEBIT_CODE);
        if(ObjectUtils.isNotNull(pendingEntry )) {
            pendingEntry.setAccountNumber(info.getTripAccountNumber());
            pendingEntry.setSubAccountNumber(StringUtils.defaultIfEmpty(info.getTripSubAccountNumber(), GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber()));

            LOG.info("Created DEBIT GLPE: " + pendingEntry.getDocumentNumber() + " for AGENCY Import Expense: " + agencyData.getId() + " TripId: " + agencyData.getTripId()
                + "\n\n" + ReflectionToStringBuilder.reflectionToString(pendingEntry, ToStringStyle.MULTI_LINE_STYLE));

            //add to list if entry was created successfully
            entryList.add(pendingEntry);
            //handling offset
            if (generateOffset){
                generateOffsetPendingEntry(entryList, sequenceHelper, pendingEntry);
            }
        }
        return entryList;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService#buildCreditPendingEntry(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.module.tem.businessobject.TripAccountingInformation, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, java.lang.String, org.kuali.rice.kns.util.KualiDecimal, boolean)
     */
    @Override
    public List<GeneralLedgerPendingEntry> buildCreditPendingEntry(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String objectCode, KualiDecimal amount, boolean generateOffset){
        List<GeneralLedgerPendingEntry> entryList = new ArrayList<GeneralLedgerPendingEntry>();

        //get chart code from parameter
        String chartCode = parameterService.getParameterValueAsString(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_CLEARING_CHART);

        GeneralLedgerPendingEntry pendingEntry = buildGeneralLedgerPendingEntry(agencyData, info, sequenceHelper, chartCode, objectCode, amount, KFSConstants.GL_CREDIT_CODE);
        if(ObjectUtils.isNotNull(pendingEntry )) {
            pendingEntry.setAccountNumber(parameterService.getParameterValueAsString(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_CLEARING_ACCOUNT));
            pendingEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber());
        }

        LOG.info("Created CREDIT GLPE: " + pendingEntry.getDocumentNumber() + " for AGENCY Import Expense: " + agencyData.getId() + " TripId: " + agencyData.getTripId()
                + "\n\n" + ReflectionToStringBuilder.reflectionToString(pendingEntry, ToStringStyle.MULTI_LINE_STYLE));

        //add to list if entry was created successfully
        if (ObjectUtils.isNotNull(pendingEntry)) {
            entryList.add(pendingEntry);
            //handling offset
            if (generateOffset){
                generateOffsetPendingEntry(entryList, sequenceHelper, pendingEntry);
            }
        }
        return entryList;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService#buildCreditPendingEntryForImportedExpense(org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, java.lang.String)
     */
    @Override
    public List<GeneralLedgerPendingEntry> buildDistributionEntriesForCTSExpense(ImportedExpense expense, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String travelDocumentIdentifier){
        List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();

        final String chartCode = parameterService.getParameterValueAsString(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_CLEARING_CHART);
        final String accountNumber = parameterService.getParameterValueAsString(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_CLEARING_ACCOUNT);
        final String creditObjectCode = parameterService.getParameterValueAsString(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_CLEARING_OBJECT_CODE);

        ObjectCode objectCd = getObjectCodeService().getByPrimaryIdForCurrentYear(chartCode, creditObjectCode);
        if (ObjectUtils.isNull(objectCd)) {
            LOG.error("ERROR: Could not get an ObjectCode for chart code: " + chartCode + " object code: " + creditObjectCode);
            return null;
        }

        GeneralLedgerPendingEntry explicitEntry = buildBasicDistributionPendingEntry(sequenceHelper);
        //setup document number by agency data
        explicitEntry.setDocumentNumber(expense.getDocumentNumber());
        // customizations for glpe
        explicitEntry.setChartOfAccountsCode(chartCode);
        explicitEntry.setAccountNumber(accountNumber);
        explicitEntry.setFinancialObjectCode(creditObjectCode);
        explicitEntry.setFinancialObjectTypeCode(objectCd.getFinancialObjectTypeCode());
        explicitEntry.setTransactionLedgerEntryAmount(expense.getConvertedAmount());
        explicitEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        explicitEntry.setTransactionDate(dateTimeService.getCurrentSqlDate());
        explicitEntry.setOrganizationDocumentNumber(travelDocumentIdentifier);

        entries.add(explicitEntry);

        generateOffsetPendingEntry(entries, sequenceHelper, explicitEntry);

        return entries;
    }


    /**
     * @see org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService#buildCreditPendingEntry(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.module.tem.businessobject.TripAccountingInformation, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, java.lang.String, org.kuali.rice.kns.util.KualiDecimal, boolean)
     */
    @Override
    public List<GeneralLedgerPendingEntry> buildServiceFeeCreditPendingEntry(AgencyStagingData agencyData, TripAccountingInformation info, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AgencyServiceFee serviceFee, KualiDecimal amount, boolean generateOffset){
        List<GeneralLedgerPendingEntry> entryList = new ArrayList<GeneralLedgerPendingEntry>();

        GeneralLedgerPendingEntry pendingEntry = buildGeneralLedgerPendingEntry(agencyData, info, sequenceHelper, serviceFee.getCreditChartCode(), serviceFee.getCreditObjectCode(), amount, KFSConstants.GL_CREDIT_CODE);
        if(ObjectUtils.isNotNull(pendingEntry )) {
            pendingEntry.setAccountNumber(serviceFee.getCreditAccountNumber());
            pendingEntry.setSubAccountNumber(GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber());
        }

        LOG.info("Created ServiceFee CREDIT GLPE: " + pendingEntry.getDocumentNumber() + " for AGENCY Import Expense: " + agencyData.getId() + " TripId: " + agencyData.getTripId()
                + "\n\n" + ReflectionToStringBuilder.reflectionToString(pendingEntry, ToStringStyle.MULTI_LINE_STYLE));

        //add to list if entry was created successfully
        if (ObjectUtils.isNotNull(pendingEntry)) {
            entryList.add(pendingEntry);
            //handling offset
            if (generateOffset){
                generateOffsetPendingEntry(entryList, sequenceHelper, pendingEntry);
            }
        }
        return entryList;
    }

    /**
     * Generate the offset entry from the given pending entry.  If entry is created successfully, add to the entry list
     *
     * @param entryList
     * @param sequenceHelper
     * @param pendingEntry
     */
    private void generateOffsetPendingEntry(List<GeneralLedgerPendingEntry> entryList, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry pendingEntry){
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(pendingEntry);
        boolean success = generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(universityDateService.getCurrentFiscalYear(), pendingEntry, sequenceHelper, offsetEntry);
        sequenceHelper.increment();
        if (success){
            LOG.info("Created OFFSET GLPE: " + pendingEntry.getDocumentNumber() + "\n" + ReflectionToStringBuilder.reflectionToString(pendingEntry, ToStringStyle.MULTI_LINE_STYLE));
            entryList.add(offsetEntry);
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService#generateDocumentImportedExpenseGeneralLedgerPendingEntries(org.kuali.kfs.module.tem.document.TravelDocument, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper, boolean, java.lang.String)
     */
    @Override
    public boolean generateDocumentImportedExpenseGeneralLedgerPendingEntries(TravelDocument travelDocument, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isCredit, String docType){
        LOG.debug("processGenerateGeneralLedgerPendingEntries(TravelDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, boolean) - start");

        glpeSourceDetail.getObjectCode().setChartOfAccountsCode(glpeSourceDetail.getChartOfAccountsCode());
        glpeSourceDetail.getObjectCode().setFinancialObjectCode(glpeSourceDetail.getFinancialObjectCode());
        glpeSourceDetail.getObjectCode().setUniversityFiscalYear(glpeSourceDetail.getPostingYear());

        ((TemSourceAccountingLine)glpeSourceDetail).getObjectTypeCode();

        // handle the explicit entry
        // create a reference to the explicitEntry to be populated, so we can pass to the offset method later
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        processExplicitGeneralLedgerPendingEntry(travelDocument, sequenceHelper, glpeSourceDetail, explicitEntry);

        explicitEntry.setFinancialDocumentTypeCode(docType);
        explicitEntry.setTransactionLedgerEntryDescription(TemConstants.TEM_IMPORTED_GLPE_DESC);
        explicitEntry.setOrganizationDocumentNumber(travelDocument.getTravelDocumentIdentifier());
        explicitEntry.setDocumentNumber(getImportExpenseDocumentNumber(travelDocument));
        explicitEntry.setOrganizationReferenceId(travelDocument.getFinancialDocumentTypeCode()+TemConstants.IMPORTED_FLAG);
        final String transactionCode = isCredit ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE;
        explicitEntry.setTransactionDebitCreditCode(transactionCode);

        // increment the sequence counter
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        explicitEntry.setOrganizationReferenceId(travelDocument.getFinancialDocumentTypeCode()+TemConstants.IMPORTED_FLAG);
        boolean success = processOffsetGeneralLedgerPendingEntry(travelDocument, sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry);
        sequenceHelper.increment();

        LOG.debug("processGenerateGeneralLedgerPendingEntries(TravelDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper, boolean) - end");
        return success;
    }

    /**
     *
     * @param travelDocument
     * @param sequenceHelper
     * @param glpeSourceDetail
     * @param explicitEntry
     */
    private void processExplicitGeneralLedgerPendingEntry(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry) {
        LOG.debug("processExplicitGeneralLedgerPendingEntry(TravelDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry) ");

        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(travelDocument, glpeSourceDetail, sequenceHelper, explicitEntry);
        travelDocument.addPendingEntry(explicitEntry);
    }

    /**
     *
     * @param travelDocument
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     * @param offsetEntry
     * @return
     */
    private boolean processOffsetGeneralLedgerPendingEntry(TravelDocument travelDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("processOffsetGeneralLedgerPendingEntry(TravelDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) ");

        // populate the offset entry
        boolean success = generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(travelDocument.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
        travelDocument.addPendingEntry(offsetEntry);
        return success;
    }

    /**
     * Returns the Document Number to set on the GLPE
     *
     * @param travelDocument
     * @return
     */
    protected String getImportExpenseDocumentNumber(TravelDocument travelDocument) {
        if (ObjectUtils.isNotNull(travelDocument)) {
            return travelDocument.getDocumentNumber();
        }
        else {
            return null;
        }
    }

    /**
     * Returns the Document Number to set on the GLPE
     *
     * @param agencyData
     * @return
     */
    protected String getImportExpenseDocumentNumber(AgencyStagingData agencyData) {
        TravelDocument travelDocument = getTravelDocumentService().getParentTravelDocument(agencyData.getTripId());
        return getImportExpenseDocumentNumber(travelDocument);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
