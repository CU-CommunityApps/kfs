/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.batch.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.batch.ExtractProcessLog;
import org.kuali.kfs.module.cab.batch.PreAssetTaggingStep;
import org.kuali.kfs.module.cab.batch.dataaccess.ExtractDao;
import org.kuali.kfs.module.cab.batch.dataaccess.PurchasingAccountsPayableItemAssetDao;
import org.kuali.kfs.module.cab.batch.service.BatchExtractService;
import org.kuali.kfs.module.cab.batch.service.ReconciliationService;
import org.kuali.kfs.module.cab.businessobject.BatchParameters;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GlAccountLineGroup;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccountHistory;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccountHistory;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLineBase;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides default implementation of {@link BatchExtractService}
 */
@Transactional
public class BatchExtractServiceImpl implements BatchExtractService {

    protected static final Logger LOG = Logger.getLogger(BatchExtractServiceImpl.class);
    protected BusinessObjectService businessObjectService;
    protected ExtractDao extractDao;
    protected DateTimeService dateTimeService;
    protected ParameterService parameterService;
    protected PurchasingAccountsPayableItemAssetDao purchasingAccountsPayableItemAssetDao;

    /**
     * Creates a batch parameters object reading values from configured system parameters for CAB Extract
     * 
     * @return BatchParameters
     */
    protected BatchParameters createCabBatchParameters() {
        BatchParameters parameters = new BatchParameters();
        parameters.setLastRunTime(getCabLastRunTimestamp());
        parameters.setIncludedFinancialBalanceTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.BALANCE_TYPES));
        parameters.setIncludedFinancialObjectSubTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.OBJECT_SUB_TYPES));
        parameters.setExcludedChartCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.CHARTS));
        parameters.setExcludedDocTypeCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.DOCUMENT_TYPES));
        parameters.setExcludedFiscalPeriods(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.FISCAL_PERIODS));
        parameters.setExcludedSubFundCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.SUB_FUND_GROUPS));
        return parameters;
    }

    /**
     * Creates a batch parameters object reading values from configured system parameters for Pre Tagging Extract
     * 
     * @return BatchParameters
     */
    protected BatchParameters createPreTagBatchParameters() {
        BatchParameters parameters = new BatchParameters();
        parameters.setLastRunDate(getPreTagLastRunDate());
        parameters.setIncludedFinancialObjectSubTypeCodes(parameterService.getParameterValues(PreAssetTaggingStep.class, CabConstants.Parameters.OBJECT_SUB_TYPES));
        parameters.setExcludedChartCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.CHARTS));
        parameters.setExcludedSubFundCodes(parameterService.getParameterValues(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.SUB_FUND_GROUPS));
        parameters.setCapitalizationLimitAmount(new BigDecimal(parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.CAPITALIZATION_LIMIT_AMOUNT)));
        return parameters;
    }

    /**
     * Retrieves a credit memo document for a specific document number
     * 
     * @param entry GL Line
     * @return CreditMemoDocument
     */
    protected CreditMemoDocument findCreditMemoDocument(Entry entry) {
        CreditMemoDocument creditMemoDocument = null;
        Map<String, String> keys = new LinkedHashMap<String, String>();
        keys.put(CabPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
        Collection<CreditMemoDocument> matchingCms = businessObjectService.findMatching(CreditMemoDocument.class, keys);
        if (matchingCms != null && matchingCms.size() == 1) {
            creditMemoDocument = matchingCms.iterator().next();
        }
        return creditMemoDocument;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#findElgibleGLEntries()
     */
    public Collection<Entry> findElgibleGLEntries(ExtractProcessLog processLog) {
        BatchParameters parameters = createCabBatchParameters();
        processLog.setLastExtractTime(parameters.getLastRunTime());
        return getExtractDao().findMatchingGLEntries(parameters);
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#findPreTaggablePOAccounts()
     */
    public Collection<PurchaseOrderAccount> findPreTaggablePOAccounts() {
        BatchParameters parameters = createPreTagBatchParameters();
        return getExtractDao().findPreTaggablePOAccounts(parameters);
    }


    /**
     * Retrieves a payment request document for a specific document number
     * 
     * @param entry GL Line
     * @return PaymentRequestDocument
     */
    protected PaymentRequestDocument findPaymentRequestDocument(Entry entry) {
        PaymentRequestDocument paymentRequestDocument = null;
        Map<String, String> keys = new LinkedHashMap<String, String>();
        keys.put(CabPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
        Collection<PaymentRequestDocument> matchingPreqs = businessObjectService.findMatching(PaymentRequestDocument.class, keys);
        if (matchingPreqs != null && matchingPreqs.size() == 1) {
            paymentRequestDocument = matchingPreqs.iterator().next();
        }
        return paymentRequestDocument;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#findPurapPendingGLEntries()
     */
    public Collection<GeneralLedgerPendingEntry> findPurapPendingGLEntries() {
        Collection<GeneralLedgerPendingEntry> purapPendingGLEntries = extractDao.findPurapPendingGLEntries(createCabBatchParameters());
        return purapPendingGLEntries;
    }


    /**
     * Computes the last run time stamp, if null then it gives yesterday
     * 
     * @return Last run time stamp
     */
    protected Timestamp getCabLastRunTimestamp() {
        Timestamp lastRunTime;
        String lastRunTS = parameterService.getParameterValue(ParameterConstants.CAPITAL_ASSET_BUILDER_BATCH.class, CabConstants.Parameters.LAST_EXTRACT_TIME);
        Date yesterday = DateUtils.add(dateTimeService.getCurrentDate(), Calendar.DAY_OF_MONTH, -1);
        try {
            lastRunTime = lastRunTS == null ? new Timestamp(yesterday.getTime()) : new Timestamp(DateUtils.parseDate(lastRunTS, new String[] { CabConstants.DATE_FORMAT_TS }).getTime());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return lastRunTime;
    }

    protected java.sql.Date getPreTagLastRunDate() {
        java.sql.Date lastRunDt;
        String lastRunTS = parameterService.getParameterValue(PreAssetTaggingStep.class, CabConstants.Parameters.LAST_EXTRACT_DATE);
        Date yesterday = DateUtils.add(dateTimeService.getCurrentDate(), Calendar.DAY_OF_MONTH, -1);
        try {
            lastRunDt = lastRunTS == null ? new java.sql.Date(yesterday.getTime()) : new java.sql.Date(DateUtils.parseDate(lastRunTS, new String[] { CabConstants.DATE_FORMAT_DT }).getTime());
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return lastRunDt;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#saveFPLines(java.util.List)
     */
    public void saveFPLines(List<Entry> fpLines, ExtractProcessLog processLog) {
        for (Entry fpLine : fpLines) {
            // If entry is not duplicate, non-null and non-zero, then insert into CAB
            ReconciliationService reconciliationService = SpringContext.getBean(ReconciliationService.class);
            if (fpLine.getTransactionLedgerEntryAmount() == null || fpLine.getTransactionLedgerEntryAmount().isZero()) {
                // amount is zero or null
                processLog.addIgnoredGLEntry(fpLine);
            }
            else if (reconciliationService.isDuplicateEntry(fpLine)) {
                // GL is duplicate
                processLog.addDuplicateGLEntry(fpLine);
            }
            else {
                GeneralLedgerEntry glEntry = new GeneralLedgerEntry(fpLine);
                businessObjectService.save(glEntry);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#savePOLines(java.util.List)
     */
    public void savePOLines(List<Entry> poLines, ExtractProcessLog processLog) {
        ReconciliationService reconciliationService = SpringContext.getBean(ReconciliationService.class);
        // This is a list of pending GL entries created after last GL process and Cab Batch extract
        Collection<GeneralLedgerPendingEntry> purapPendingGLEntries = findPurapPendingGLEntries();
        // PurAp Account Line history comes from PURAP module
        Collection<PurApAccountingLineBase> purapAcctLines = findPurapAccountHistory();
        // Pass the records to reconciliation service method
        reconciliationService.reconcile(poLines, purapPendingGLEntries, purapAcctLines);

        // for each valid GL entry there is a collection of valid PO Doc and Account Lines
        Collection<GlAccountLineGroup> matchedGroups = reconciliationService.getMatchedGroups();

        for (GlAccountLineGroup group : matchedGroups) {
            Entry entry = group.getTargetEntry();
            GeneralLedgerEntry generalLedgerEntry = new GeneralLedgerEntry(entry);
            businessObjectService.save(generalLedgerEntry);

            PurchasingAccountsPayableDocument cabPurapDoc = findPurchasingAccountsPayableDocument(entry);
            // if document is found already, update the active flag
            if (ObjectUtils.isNotNull(cabPurapDoc) && !cabPurapDoc.isActive()) {
                cabPurapDoc.setActive(true);
            }
            else if (ObjectUtils.isNull(cabPurapDoc)) {
                cabPurapDoc = createPurchasingAccountsPayableDocument(entry);
            }
            if (cabPurapDoc != null) {
                // Keep track of unique item lines
                HashMap<String, PurchasingAccountsPayableItemAsset> assetItems = new HashMap<String, PurchasingAccountsPayableItemAsset>();

                // Keep track of unique account lines
                HashMap<String, PurchasingAccountsPayableLineAssetAccount> assetAcctLines = new HashMap<String, PurchasingAccountsPayableLineAssetAccount>();

                List<PurApAccountingLineBase> matchedPurApAcctLines = group.getMatchedPurApAcctLines();

                for (PurApAccountingLineBase purApAccountingLine : matchedPurApAcctLines) {
                    PurApItem purapItem = purApAccountingLine.getPurapItem();
                    PurchasingAccountsPayableItemAsset itemAsset = findMatchingPurapAssetItem(cabPurapDoc, purapItem);
                    String itemAssetKey = cabPurapDoc.getDocumentNumber() + "-" + purapItem.getItemIdentifier();

                    if (itemAsset == null && (itemAsset = assetItems.get(itemAssetKey)) == null) {
                        itemAsset = createPurchasingAccountsPayableItemAsset(cabPurapDoc, purapItem);
                        cabPurapDoc.getPurchasingAccountsPayableItemAssets().add(itemAsset);
                        assetItems.put(itemAssetKey, itemAsset);
                    }
                    PurchasingAccountsPayableLineAssetAccount assetAccount = createPurchasingAccountsPayableLineAssetAccount(generalLedgerEntry, cabPurapDoc, purApAccountingLine, itemAsset);
                    String acctLineKey = cabPurapDoc.getDocumentNumber() + "-" + itemAsset.getAccountsPayableLineItemIdentifier() + "-" + itemAsset.getCapitalAssetBuilderLineNumber() + "-" + generalLedgerEntry.getGeneralLedgerAccountIdentifier();

                    if ((assetAccount = assetAcctLines.get(acctLineKey)) == null) {
                        // if new unique account line within GL, then create a new account line
                        assetAccount = createPurchasingAccountsPayableLineAssetAccount(generalLedgerEntry, cabPurapDoc, purApAccountingLine, itemAsset);
                        assetAcctLines.put(acctLineKey, assetAccount);
                        itemAsset.getPurchasingAccountsPayableLineAssetAccounts().add(assetAccount);
                    }
                    else {
                        // if account line key matches within same GL Entry, combine the amount
                        assetAccount.getItemAccountTotalAmount().add(purApAccountingLine.getAmount());
                    }
                }
                businessObjectService.save(cabPurapDoc);
            }
            else {
                LOG.error("Could not create a valid PurchasingAccountsPayableDocument object for document number " + entry.getDocumentNumber());
            }
        }
        updateProcessLog(processLog, reconciliationService);
    }

    /**
     * Retrieves Payment Request Account History and Credit Memo account history, combines them into a single list
     * 
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#findPurapAccountHistory()
     */
    public Collection<PurApAccountingLineBase> findPurapAccountHistory() {
        Collection<PurApAccountingLineBase> purapAcctLines = new ArrayList<PurApAccountingLineBase>();
        Collection<CreditMemoAccountHistory> cmAccountHistory = extractDao.findCreditMemoAccountHistory(createCabBatchParameters());
        Collection<PaymentRequestAccountHistory> preqAccountHistory = extractDao.findPaymentRequestAccountHistory(createCabBatchParameters());
        if (cmAccountHistory != null) {
            purapAcctLines.addAll(cmAccountHistory);
        }
        if (preqAccountHistory != null) {
            purapAcctLines.addAll(preqAccountHistory);
        }
        return purapAcctLines;
    }

    /**
     * Creates a new instance of PurchasingAccountsPayableLineAssetAccount using values provided from dependent objects
     * 
     * @param generalLedgerEntry General Ledger Entry record
     * @param cabPurapDoc CAB PurAp Document
     * @param purApAccountingLine PurAp accounting line
     * @param itemAsset CAB PurAp Item Asset
     * @return New PurchasingAccountsPayableLineAssetAccount
     */
    protected PurchasingAccountsPayableLineAssetAccount createPurchasingAccountsPayableLineAssetAccount(GeneralLedgerEntry generalLedgerEntry, PurchasingAccountsPayableDocument cabPurapDoc, PurApAccountingLineBase purApAccountingLine, PurchasingAccountsPayableItemAsset itemAsset) {
        PurchasingAccountsPayableLineAssetAccount assetAccount = new PurchasingAccountsPayableLineAssetAccount();
        assetAccount.setDocumentNumber(cabPurapDoc.getDocumentNumber());
        assetAccount.setAccountsPayableLineItemIdentifier(itemAsset.getAccountsPayableLineItemIdentifier());
        assetAccount.setCapitalAssetBuilderLineNumber(itemAsset.getCapitalAssetBuilderLineNumber());
        assetAccount.setGeneralLedgerAccountIdentifier(generalLedgerEntry.getGeneralLedgerAccountIdentifier());
        assetAccount.setItemAccountTotalAmount(purApAccountingLine.getAmount());
        assetAccount.setActive(true);
        assetAccount.setVersionNumber(0L);
        return assetAccount;
    }

    /**
     * Updates the entries into process log
     * 
     * @param processLog Extract Process Log
     * @param reconciliationService Reconciliation Service data
     */
    protected void updateProcessLog(ExtractProcessLog processLog, ReconciliationService reconciliationService) {
        processLog.addIgnoredGLEntries(reconciliationService.getIgnoredEntries());
        processLog.addDuplicateGLEntries(reconciliationService.getDuplicateEntries());
        Collection<GlAccountLineGroup> misMatchedGroups = reconciliationService.getMisMatchedGroups();
        for (GlAccountLineGroup glAccountLineGroup : misMatchedGroups) {
            processLog.addMismatchedGLEntries(glAccountLineGroup.getSourceEntries());
        }
    }

    /**
     * Finds PurchasingAccountsPayableDocument using document number
     * 
     * @param entry GL Entry
     * @return PurchasingAccountsPayableDocument
     */
    protected PurchasingAccountsPayableDocument findPurchasingAccountsPayableDocument(Entry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.PurchasingAccountsPayableDocument.DOCUMENT_NUMBER, entry.getDocumentNumber());
        // check if doc is already in CAB
        PurchasingAccountsPayableDocument cabPurapDoc = (PurchasingAccountsPayableDocument) businessObjectService.findByPrimaryKey(PurchasingAccountsPayableDocument.class, primaryKeys);
        return cabPurapDoc;
    }

    /**
     * Creates a new PurchasingAccountsPayableItemAsset using Purchasing Accounts payable item
     * 
     * @param cabPurapDoc Cab Purap Document
     * @param apItem Accounts Payable Item
     * @return PurchasingAccountsPayableItemAsset
     */
    protected PurchasingAccountsPayableItemAsset createPurchasingAccountsPayableItemAsset(PurchasingAccountsPayableDocument cabPurapDoc, PurApItem apItem) {
        PurchasingAccountsPayableItemAsset itemAsset = new PurchasingAccountsPayableItemAsset();
        itemAsset.setDocumentNumber(cabPurapDoc.getDocumentNumber());
        itemAsset.setAccountsPayableLineItemIdentifier(apItem.getItemIdentifier());
        itemAsset.setCapitalAssetBuilderLineNumber(purchasingAccountsPayableItemAssetDao.findMaxCabLineNumber(cabPurapDoc.getDocumentNumber(), apItem.getItemIdentifier()) + 1);
        itemAsset.setAccountsPayableLineItemDescription(apItem.getItemDescription());
        itemAsset.setAccountsPayableItemQuantity(apItem.getItemQuantity());
        itemAsset.setActive(true);
        itemAsset.setVersionNumber(0L);
        return itemAsset;
    }

    /**
     * This method creates PurchasingAccountsPayableDocument from a GL Entry and AP Document
     * 
     * @param entry GL Entry
     * @param apDoc AP Document
     * @return PurchasingAccountsPayableDocument
     */
    protected PurchasingAccountsPayableDocument createPurchasingAccountsPayableDocument(Entry entry) {
        AccountsPayableDocumentBase apDoc = null;
        PurchasingAccountsPayableDocument cabPurapDoc = null;
        // If document is not in CAB, create a new document to save in CAB
        if (CabConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
            // find PREQ
            apDoc = findPaymentRequestDocument(entry);
        }
        else if (CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
            // find CM
            apDoc = findCreditMemoDocument(entry);
        }
        if (apDoc == null) {
            LOG.error("A valid Purchasing Document (PREQ or CM) could not be found for this document number " + entry.getDocumentNumber());
        }
        else {
            cabPurapDoc = new PurchasingAccountsPayableDocument();
            cabPurapDoc.setDocumentNumber(entry.getDocumentNumber());
            cabPurapDoc.setPurapDocumentIdentifier(apDoc.getPurapDocumentIdentifier());
            cabPurapDoc.setPurchaseOrderIdentifier(apDoc.getPurchaseOrderIdentifier());
            cabPurapDoc.setDocumentTypeCode(entry.getFinancialDocumentTypeCode());
            cabPurapDoc.setActive(true);
            cabPurapDoc.setVersionNumber(0L);
        }
        return cabPurapDoc;
    }

    /**
     * Finds out the active CAB Asset Item matching the line from PurAP.
     * 
     * @param cabPurapDoc CAB PurAp document
     * @param apItem AP Item
     * @return PurchasingAccountsPayableItemAsset
     */
    protected PurchasingAccountsPayableItemAsset findMatchingPurapAssetItem(PurchasingAccountsPayableDocument cabPurapDoc, PurApItem apItem) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.DOCUMENT_NUMBER, cabPurapDoc.getDocumentNumber());
        keys.put(CabPropertyConstants.PurchasingAccountsPayableItemAsset.ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER, apItem.getItemIdentifier());
        Collection<PurchasingAccountsPayableItemAsset> matchingItems = businessObjectService.findMatching(PurchasingAccountsPayableItemAsset.class, keys);
        if (matchingItems != null && !matchingItems.isEmpty() && matchingItems.size() == 1) {
            PurchasingAccountsPayableItemAsset itmAsset = matchingItems.iterator().next();
            // if still active and never split or submitted to CAMS
            if (itmAsset.isActive() && itmAsset.getCapitalAssetManagementDocumentNumber() == null) {
                return itmAsset;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#separatePOLines(java.util.List, java.util.List,
     *      java.util.Collection)
     */
    public void separatePOLines(List<Entry> fpLines, List<Entry> purapLines, Collection<Entry> elgibleGLEntries) {
        for (Entry entry : elgibleGLEntries) {
            if (CabConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
                purapLines.add(entry);
            }
            else if (!CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                fpLines.add(entry);
            }
            else if (CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                Map<String, String> fieldValues = new HashMap<String, String>();
                fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, entry.getDocumentNumber());
                // check if vendor credit memo, then include as FP line
                Collection<CreditMemoDocument> matchingCreditMemos = businessObjectService.findMatching(CreditMemoDocument.class, fieldValues);
                for (CreditMemoDocument creditMemoDocument : matchingCreditMemos) {
                    if (creditMemoDocument.getPurchaseOrderIdentifier() == null) {
                        fpLines.add(entry);
                    }
                    else {
                        purapLines.add(entry);
                    }
                }
            }
        }
    }


    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#updateLastExtractTime(java.sql.Timestamp)
     */
    public void updateLastExtractTime(Timestamp time) {
        Map<String, String> primaryKeys = new LinkedHashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_BATCH);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAME, CabConstants.Parameters.LAST_EXTRACT_TIME);
        Parameter parameter = (Parameter) businessObjectService.findByPrimaryKey(Parameter.class, primaryKeys);
        if (parameter != null) {
            SimpleDateFormat format = new SimpleDateFormat(CabConstants.DATE_FORMAT_TS);
            parameter.setParameterValue(format.format(time));
            businessObjectService.save(parameter);
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#savePreTagLines(java.util.Collection)
     */
    public void savePreTagLines(Collection<PurchaseOrderAccount> preTaggablePOAccounts) {
        HashSet<String> savedLines = new HashSet<String>();
        for (PurchaseOrderAccount purchaseOrderAccount : preTaggablePOAccounts) {
            PurchaseOrderItem purapItem = purchaseOrderAccount.getPurapItem();
            PurchaseOrderDocument purchaseOrder = purapItem.getPurchaseOrder();
            if (ObjectUtils.isNotNull(purchaseOrder)) {
                Integer poId = purchaseOrder.getPurapDocumentIdentifier();
                Integer itemLineNumber = purapItem.getItemLineNumber();
                if (poId != null && itemLineNumber != null) {
                    Map<String, Object> primaryKeys = new HashMap<String, Object>();
                    primaryKeys.put(CabPropertyConstants.Pretag.PURCHASE_ORDER_NUMBER, poId);
                    primaryKeys.put(CabPropertyConstants.Pretag.LINE_ITEM_NUMBER, itemLineNumber);
                    // check if already in pre-tag table
                    Pretag pretag = (Pretag) businessObjectService.findByPrimaryKey(Pretag.class, primaryKeys);
                    if (ObjectUtils.isNull(pretag) && savedLines.add("" + poId + "-" + itemLineNumber)) {
                        pretag = new Pretag();
                        pretag.setPurchaseOrderNumber(poId.toString());
                        pretag.setLineItemNumber(Long.valueOf(itemLineNumber));
                        KualiDecimal quantity = purapItem.getItemInvoicedTotalQuantity();
                        pretag.setQuantityInvoiced(quantity != null ? quantity : new KualiDecimal(1));
                        pretag.setVendorName(purchaseOrder.getVendorName());
                        pretag.setAssetTopsDescription(purapItem.getItemDescription());
                        pretag.setPretagCreateDate(dateTimeService.getCurrentSqlDate());
                        pretag.setChartOfAccountsCode(purchaseOrder.getChartOfAccountsCode());
                        pretag.setOrganizationCode(purchaseOrder.getOrganizationCode());
                        pretag.setActive(true);
                        businessObjectService.save(pretag);
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.batch.service.BatchExtractService#updateLastExtractDate(java.sql.Date)
     */
    public void updateLastExtractDate(java.sql.Date dt) {
        Map<String, String> primaryKeys = new LinkedHashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAMESPACE_CODE, CabConstants.Parameters.NAMESPACE);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_DETAIL_TYPE_CODE, CabConstants.Parameters.DETAIL_TYPE_PRE_ASSET_TAGGING_STEP);
        primaryKeys.put(CabPropertyConstants.Parameter.PARAMETER_NAME, CabConstants.Parameters.LAST_EXTRACT_DATE);
        Parameter parameter = (Parameter) businessObjectService.findByPrimaryKey(Parameter.class, primaryKeys);
        if (parameter != null) {
            SimpleDateFormat format = new SimpleDateFormat(CabConstants.DATE_FORMAT_DT);
            parameter.setParameterValue(format.format(dt));
            businessObjectService.save(parameter);
        }
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the extractDao attribute.
     * 
     * @return Returns the extractDao.
     */
    public ExtractDao getExtractDao() {
        return extractDao;
    }

    /**
     * Sets the extractDao attribute value.
     * 
     * @param extractDao The extractDao to set.
     */
    public void setExtractDao(ExtractDao extractDao) {
        this.extractDao = extractDao;
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
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
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
     * Gets the purchasingAccountsPayableItemAssetDao attribute.
     * 
     * @return Returns the purchasingAccountsPayableItemAssetDao.
     */
    public PurchasingAccountsPayableItemAssetDao getPurchasingAccountsPayableItemAssetDao() {
        return purchasingAccountsPayableItemAssetDao;
    }

    /**
     * Sets the purchasingAccountsPayableItemAssetDao attribute value.
     * 
     * @param purchasingAccountsPayableItemAssetDao The purchasingAccountsPayableItemAssetDao to set.
     */
    public void setPurchasingAccountsPayableItemAssetDao(PurchasingAccountsPayableItemAssetDao purchasingAccountsPayableItemAssetDao) {
        this.purchasingAccountsPayableItemAssetDao = purchasingAccountsPayableItemAssetDao;
    }


}
