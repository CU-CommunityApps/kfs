/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ObjectTypeService;
import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntryAsset;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.util.ObjectValueUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentHeaderService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class GlLineServiceImpl implements GlLineService {
    private static final String CAB_DESC_PREFIX = "CAB created for FP ";
    
    protected BusinessObjectService businessObjectService;
    protected AssetGlobalService assetGlobalService;
    protected ObjectTypeService objectTypeService;
    
    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#createAssetGlobalDocument(java.util.List,
     *      org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public Document createAssetGlobalDocument(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) throws WorkflowException {
        // initiate a new document
        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        MaintenanceDocument document = (MaintenanceDocument) documentService.getNewDocument(DocumentTypeName.ASSET_ADD_GLOBAL);

        // create asset global
        AssetGlobal assetGlobal = createAssetGlobal(primary, document);
        assetGlobal.setCapitalAssetBuilderOriginIndicator(true);
        assetGlobal.setAcquisitionTypeCode(getAssetGlobalService().getNewAcquisitionTypeCode());

        updatePreTagInformation(primary, document, assetGlobal, capitalAssetLineNumber);

        assetGlobal.getAssetPaymentDetails().addAll(createAssetPaymentDetails(primary, document, 0, capitalAssetLineNumber));

        // save the document
        document.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_NEW_ACTION);
        document.getDocumentHeader().setDocumentDescription(CAB_DESC_PREFIX + primary.getDocumentNumber());
        document.getNewMaintainableObject().setBusinessObject(assetGlobal);
        document.getNewMaintainableObject().setBoClass(assetGlobal.getClass());

        documentService.saveDocument(document);

        //mark the capital asset as processed..
        markCapitalAssetProcessed(primary, capitalAssetLineNumber);

        deactivateGLEntries(primary, document, capitalAssetLineNumber);
        
        return document;
    }

    /**
     * De-activate the GL Entries
     * 
     * @param primary
     * @param capitalAssetLineNumber
     */
    protected void markCapitalAssetProcessed(GeneralLedgerEntry primary, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(primary, capitalAssetLineNumber);
        //if it is create asset...
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            capitalAssetInformation.setCapitalAssetProcessedIndicator(true);
            getBusinessObjectService().save(capitalAssetInformation);
        }
    }
    
    /**
     * De-activate the GL Entry
     * 
     * @param entries GL Entry
     * @param document Document
     */
    protected void deactivateGLEntries(GeneralLedgerEntry entry, Document document, Integer capitalAssetLineNumber) {
        //now deactivate the gl line..
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry, capitalAssetLineNumber);
        createGeneralLedgerEntryAsset(entry, document, capitalAssetLineNumber); 
        
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            List<CapitalAssetAccountsGroupDetails> groupAccountingLines = capitalAssetInformation.getCapitalAssetAccountsGroupDetails();
            for (CapitalAssetAccountsGroupDetails accountingLine : groupAccountingLines) {
                if (entry.getDocumentNumber().equals(accountingLine.getDocumentNumber()) &&
                        entry.getChartOfAccountsCode().equals(accountingLine.getChartOfAccountsCode()) &&
                        entry.getAccountNumber().equals(accountingLine.getAccountNumber()) &&
                        entry.getFinancialObjectCode().equals(accountingLine.getFinancialObjectCode())) {
                    
                    KualiDecimal lineAmount = accountingLine.getAmount();
                    
                    //update submitted amount on the gl entry and save the results.
                    updateTransactionSumbitGlEntryAmount(entry, lineAmount);
                }
            }
        }
    }

    /**
     * This method reads the pre-tag information and creates objects for asset global document
     * 
     * @param entry GL Line
     * @param document Asset Global Maintenance Document
     * @param assetGlobal Asset Global Object
     */
    protected void updatePreTagInformation(GeneralLedgerEntry entry, MaintenanceDocument document, AssetGlobal assetGlobal, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry, capitalAssetLineNumber);
        //if it is create asset...
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR.equals(capitalAssetInformation.getCapitalAssetActionIndicator())) {
                List<CapitalAssetInformationDetail> capitalAssetInformationDetails = capitalAssetInformation.getCapitalAssetInformationDetails();
                for (CapitalAssetInformationDetail capitalAssetInformationDetail : capitalAssetInformationDetails) {
                    // This is not added to constructor in CAMS to provide module isolation from CAMS
                    AssetGlobalDetail assetGlobalDetail = new AssetGlobalDetail();
                    assetGlobalDetail.setDocumentNumber(document.getDocumentNumber());
                    assetGlobalDetail.setCampusCode(capitalAssetInformationDetail.getCampusCode());
                    assetGlobalDetail.setBuildingCode(capitalAssetInformationDetail.getBuildingCode());
                    assetGlobalDetail.setBuildingRoomNumber(capitalAssetInformationDetail.getBuildingRoomNumber());
                    assetGlobalDetail.setBuildingSubRoomNumber(capitalAssetInformationDetail.getBuildingSubRoomNumber());
                    assetGlobalDetail.setSerialNumber(capitalAssetInformationDetail.getCapitalAssetSerialNumber());
                    assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
                    assetGlobalDetail.setCampusTagNumber(capitalAssetInformationDetail.getCapitalAssetTagNumber());
                    
                    AssetGlobalDetail uniqueAsset = new AssetGlobalDetail();
                    ObjectValueUtils.copySimpleProperties(assetGlobalDetail, uniqueAsset);
                    assetGlobalDetail.getAssetGlobalUniqueDetails().add(uniqueAsset);
                    assetGlobal.getAssetSharedDetails().add(assetGlobalDetail);
                }
                
                assetGlobal.setVendorName(capitalAssetInformation.getVendorName());
                assetGlobal.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE);
                assetGlobal.setCapitalAssetTypeCode(capitalAssetInformation.getCapitalAssetTypeCode());
                assetGlobal.setManufacturerName(capitalAssetInformation.getCapitalAssetManufacturerName());
                assetGlobal.setManufacturerModelNumber(capitalAssetInformation.getCapitalAssetManufacturerModelNumber());
                assetGlobal.setCapitalAssetDescription(capitalAssetInformation.getCapitalAssetDescription());
            } 
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public List<CapitalAssetInformation> findCapitalAssetInformation(GeneralLedgerEntry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        List<CapitalAssetInformation> assetInformation = (List<CapitalAssetInformation>) businessObjectService.findMatching(CapitalAssetInformation.class, primaryKeys);
        return assetInformation;
    }
    
    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public CapitalAssetInformation findCapitalAssetInformation(GeneralLedgerEntry entry, Integer capitalAssetLineNumber) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.ASSET_LINE_NUMBER, capitalAssetLineNumber.toString());

        CapitalAssetInformation assetInformation = (CapitalAssetInformation) businessObjectService.findByPrimaryKey(CapitalAssetInformation.class, primaryKeys);
        return assetInformation;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findAllCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public List<CapitalAssetInformation> findAllCapitalAssetInformation(GeneralLedgerEntry entry) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        
        List<CapitalAssetInformation> assetInformation = (List<CapitalAssetInformation>) businessObjectService.findMatchingOrderBy(CapitalAssetInformation.class, primaryKeys, CabPropertyConstants.CapitalAssetInformation.ACTION_INDICATOR, true);

        return assetInformation;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findCapitalAssetInformationForGLLine(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public List<CapitalAssetInformation> findCapitalAssetInformationForGLLine(GeneralLedgerEntry entry) {
        String capitalAssetLineType = KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) ? KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE : KFSConstants.TARGET_ACCT_LINE_TYPE_CODE;

        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        
        List<CapitalAssetInformation> assetInformation = (List<CapitalAssetInformation>) businessObjectService.findMatchingOrderBy(CapitalAssetInformation.class, primaryKeys, CabPropertyConstants.CapitalAssetInformation.ACTION_INDICATOR, true);

        List<CapitalAssetInformation> matchingAssets = new ArrayList<CapitalAssetInformation>();
        
        for (CapitalAssetInformation capitalAsset : assetInformation) {
            addToCapitalAssets(matchingAssets, capitalAsset, entry, capitalAssetLineType);
        }
        
        return matchingAssets;
    }
    
    /**
     * Compares the gl line to the group accounting lines in each capital asset and
     * when finds a match, adds the capital asset to the list of matching assets
     * @param matchingAssets
     * @param capitalAsset
     * @param entry
     * @param capitalAssetLineType
     */
    protected void addToCapitalAssets(List<CapitalAssetInformation> matchingAssets, CapitalAssetInformation capitalAsset, GeneralLedgerEntry entry, String capitalAssetLineType) {
        List<CapitalAssetAccountsGroupDetails> groupAccountLines = capitalAsset.getCapitalAssetAccountsGroupDetails();
        
        for (CapitalAssetAccountsGroupDetails groupAccountLine : groupAccountLines) {
            if (groupAccountLine.getDocumentNumber().equals(entry.getDocumentNumber()) &&
                    groupAccountLine.getChartOfAccountsCode().equals(entry.getChartOfAccountsCode()) && 
                    groupAccountLine.getAccountNumber().equals(entry.getAccountNumber()) && 
                    groupAccountLine.getFinancialObjectCode().equals(entry.getFinancialObjectCode())) {
                matchingAssets.add(capitalAsset);
                break;
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findUnprocessedCapitalAssetInformation(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry)
     */
    public long findUnprocessedCapitalAssetInformation(GeneralLedgerEntry entry) {
        Map<String, String> fieldValues = new HashMap<String, String>();

        fieldValues.put(CabPropertyConstants.CapitalAssetInformation.DOCUMENT_NUMBER, entry.getDocumentNumber());
        fieldValues.put(CabPropertyConstants.CapitalAssetInformation.ASSET_PROCESSED_IND, KFSConstants.CapitalAssets.CAPITAL_ASSET_PROCESSED_IND);
        
        List<CapitalAssetInformation> assetInformation = (List<CapitalAssetInformation>) businessObjectService.findMatching(CapitalAssetInformation.class, fieldValues);
        if (ObjectUtils.isNotNull(assetInformation)) {
            return assetInformation.size();
        }
        
        return 0;
    }
    
    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#findMatchingGeneralLedgerEntry(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int)
     */
    public Collection<GeneralLedgerEntry> findMatchingGeneralLedgerEntry(String documentNumber, String chartCode, String accountNumber, String finalcialObjectcode, String debitOrCreditCode) {
        Collection<GeneralLedgerEntry> matchingGLEntry = new ArrayList<GeneralLedgerEntry>();
        
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, documentNumber);
        fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.CHART_OF_ACCOUNTS_CODE, chartCode);
        fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.ACCOUNT_NUMBER, accountNumber);
        fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.FINANCIAL_OBJECT_CODE, finalcialObjectcode);
        fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.TRANSACTION_DEBIT_CREDIT_CODE, debitOrCreditCode);

        matchingGLEntry = (List<GeneralLedgerEntry>) businessObjectService.findMatching(GeneralLedgerEntry.class, fieldValues);

        return matchingGLEntry;
    }
    
    /**
     * Creates general ledger entry asset
     * 
     * @param entry GeneralLedgerEntry
     * @param maintDoc Document
     */
    protected void createGeneralLedgerEntryAsset(GeneralLedgerEntry entry, Document document, Integer capitalAssetLineNumber) {
        // store the document number
        GeneralLedgerEntryAsset entryAsset = new GeneralLedgerEntryAsset();
        entryAsset.setGeneralLedgerAccountIdentifier(entry.getGeneralLedgerAccountIdentifier());
        entryAsset.setCapitalAssetBuilderLineNumber(capitalAssetLineNumber);
        entryAsset.setCapitalAssetManagementDocumentNumber(document.getDocumentNumber());
        entry.getGeneralLedgerEntryAssets().add(entryAsset);
    }


    /**
     * Creates asset global
     * 
     * @param entry GeneralLedgerEntry
     * @param maintDoc MaintenanceDocument
     * @return AssetGlobal
     */
    protected AssetGlobal createAssetGlobal(GeneralLedgerEntry entry, MaintenanceDocument maintDoc) {
        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setOrganizationOwnerChartOfAccountsCode(entry.getChartOfAccountsCode());
        assetGlobal.setOrganizationOwnerAccountNumber(entry.getAccountNumber());
        assetGlobal.setDocumentNumber(maintDoc.getDocumentNumber());
        assetGlobal.setConditionCode(CamsConstants.Asset.CONDITION_CODE_E);
        
        // CSU 6702 BEGIN
        //year end changes
        String docType = DocumentTypeName.ASSET_ADD_GLOBAL;
        ParameterEvaluatorService parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        ParameterEvaluator evaluator = parameterEvaluatorService.getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
        if (evaluator.evaluationSucceeds()) {
            Integer closingYear = new Integer(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));
            if (entry.getUniversityFiscalYear().equals(closingYear + 1)) {
                //default asset global year end accounting period drop down to current period instead of closing period(period 13)
                assetGlobal.setUniversityFiscalPeriodName("");
            }
        }
        // CSU 6702 END    
        
        return assetGlobal;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.GlLineService#createAssetPaymentDocument(org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry, java.lang.Integer)
     */
    public Document createAssetPaymentDocument(GeneralLedgerEntry primaryGlEntry, Integer capitalAssetLineNumber) throws WorkflowException {
        // Find out the GL Entry
        // initiate a new document
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        AssetPaymentDocument document = (AssetPaymentDocument) documentService.getNewDocument(DocumentTypeName.ASSET_PAYMENT);
        document.setCapitalAssetBuilderOriginIndicator(true);

        //populate the capital asset line distribution amount code to the payment document.
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(primaryGlEntry, capitalAssetLineNumber);

        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            document.setAssetPaymentAllocationTypeCode(capitalAssetInformation.getDistributionAmountCode());
            document.setAllocationFromFPDocuments(true);
        }
        
        document.getDocumentHeader().setDocumentDescription(CAB_DESC_PREFIX + primaryGlEntry.getDocumentNumber());
        updatePreTagInformation(primaryGlEntry, document, capitalAssetLineNumber);

        // Asset Payment Detail - sourceAccountingLines on the document....
        document.getSourceAccountingLines().addAll(createAssetPaymentDetails(primaryGlEntry, document, 0, capitalAssetLineNumber));
        
        KualiDecimal assetAmount = KualiDecimal.ZERO;
        
        List<SourceAccountingLine> sourceAccountingLines = document.getSourceAccountingLines();
        for (SourceAccountingLine sourceAccountingLine : sourceAccountingLines) {
            assetAmount = assetAmount.add(sourceAccountingLine.getAmount());
        }
        
        List<AssetPaymentAssetDetail> assetPaymentDetails = document.getAssetPaymentAssetDetail();
        for (AssetPaymentAssetDetail assetPaymentDetail : assetPaymentDetails) {
            assetPaymentDetail.setAllocatedAmount(assetAmount);
        }
        
        // Asset payment asset detail
        // save the document
        documentService.saveDocument(document);
        markCapitalAssetProcessed(primaryGlEntry, capitalAssetLineNumber);
        deactivateGLEntries(primaryGlEntry, document, capitalAssetLineNumber);
        
        return document;
    }

    /**
     * Updates pre tag information received from FP document
     * 
     * @param entry GeneralLedgerEntry
     * @param document AssetPaymentDocument
     */
    protected void updatePreTagInformation(GeneralLedgerEntry entry, AssetPaymentDocument document, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry, capitalAssetLineNumber);
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            //if it is modify asset...
            if (KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR.equals(capitalAssetInformation.getCapitalAssetActionIndicator())) {
                AssetPaymentAssetDetail assetPaymentAssetDetail = new AssetPaymentAssetDetail();
                assetPaymentAssetDetail.setDocumentNumber(document.getDocumentNumber());
                // get the allocated amount for the capital asset....
                assetPaymentAssetDetail.setCapitalAssetNumber(capitalAssetInformation.getCapitalAssetNumber());
                assetPaymentAssetDetail.setAllocatedAmount(KualiDecimal.ZERO);
                assetPaymentAssetDetail.setAllocatedUserValue(assetPaymentAssetDetail.getAllocatedAmount());
                assetPaymentAssetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentAssetDetail.ASSET);
                Asset asset = assetPaymentAssetDetail.getAsset();
                if (ObjectUtils.isNotNull(asset)) {
                    assetPaymentAssetDetail.setPreviousTotalCostAmount(asset.getTotalCostAmount() != null ? asset.getTotalCostAmount() : KualiDecimal.ZERO);
                    document.getAssetPaymentAssetDetail().add(assetPaymentAssetDetail);
                }
            }
        }
    }

    /**
     * Creates asset payment details based on accounting lines distributed
     * for the given capital asset.
     * @param entry
     * @param document
     * @param seqNo
     * @param capitalAssetLineNumber
     * @return List<AssetPaymentDetail>
     */
    protected List<AssetPaymentDetail> createAssetPaymentDetails(GeneralLedgerEntry entry, Document document, int seqNo, Integer capitalAssetLineNumber) {
        List<AssetPaymentDetail> appliedPayments = new ArrayList<AssetPaymentDetail>();
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry, capitalAssetLineNumber);
        
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            List<CapitalAssetAccountsGroupDetails> groupAccountingLines = capitalAssetInformation.getCapitalAssetAccountsGroupDetails();
            Integer paymentSequenceNumber = 1;
            
            for (CapitalAssetAccountsGroupDetails accountingLine : groupAccountingLines) {
                AssetPaymentDetail detail = new AssetPaymentDetail();
                detail.setDocumentNumber(document.getDocumentNumber());
                detail.setSequenceNumber(paymentSequenceNumber++);
                detail.setPostingYear(entry.getUniversityFiscalYear());
                detail.setPostingPeriodCode(entry.getUniversityFiscalPeriodCode());
                detail.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
                detail.setAccountNumber(replaceFiller(accountingLine.getAccountNumber()));
                detail.setSubAccountNumber(replaceFiller(entry.getSubAccountNumber()));
                detail.setFinancialObjectCode(replaceFiller(accountingLine.getFinancialObjectCode()));
                detail.setProjectCode(replaceFiller(entry.getProjectCode()));
                detail.setOrganizationReferenceId(replaceFiller(entry.getOrganizationReferenceId()));
                detail.setAmount(getAccountingLineAmountForPaymentDetail(entry, accountingLine));
                detail.setExpenditureFinancialSystemOriginationCode(replaceFiller(entry.getFinancialSystemOriginationCode()));
                detail.setExpenditureFinancialDocumentNumber(entry.getDocumentNumber());
                detail.setExpenditureFinancialDocumentTypeCode(replaceFiller(entry.getFinancialDocumentTypeCode()));
                detail.setExpenditureFinancialDocumentPostedDate(entry.getTransactionDate());
                detail.setPurchaseOrderNumber(replaceFiller(entry.getReferenceFinancialDocumentNumber()));
                detail.setTransferPaymentIndicator(false);
                detail.refreshNonUpdateableReferences();
                appliedPayments.add(detail);
            }
        }
        
        return appliedPayments;
    }
    
    /**
     * 
     * @param entry GL entry
     * @param accountingLine accounting line in the capital asset
     * @return accountingLineAmount
     */
    protected KualiDecimal getAccountingLineAmountForPaymentDetail(GeneralLedgerEntry entry, CapitalAssetAccountsGroupDetails accountingLine) {
        KualiDecimal accountLineAmount = accountingLine.getAmount();

        List<String> expenseObjectTypes = objectTypeService.getExpenseAndTransferObjectTypesForPayments();
        List<String> incomeObjectTypes = objectTypeService.getIncomeAndTransferObjectTypesForPayments();
        
        //we are dealing with error correction document so the from amount line should become positive.
        if (isDocumentAnErrorCorrection(entry)) {
            if (KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode())) {
                return accountLineAmount.negated();
            }
            
            return accountLineAmount;
        }
        
        if (expenseObjectTypes.contains(entry.getFinancialObjectTypeCode()) && 
                KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE.equals(accountingLine.getFinancialDocumentLineTypeCode()) && 
                KFSConstants.GL_CREDIT_CODE.equals(entry.getTransactionDebitCreditCode()) &&
                accountLineAmount.compareTo(KualiDecimal.ZERO) > 0) {
            return accountLineAmount.negated();
        }
       
        if (incomeObjectTypes.contains(entry.getFinancialObjectTypeCode()) && accountLineAmount.compareTo(KualiDecimal.ZERO) > 0) {
            return accountLineAmount.negated();
        }
        
        return accountLineAmount;
    }
    
    /**
     * determines if the document is an error correction document...
     * @param entry
     * @return true if the document is an error correction else false
     */
    protected boolean isDocumentAnErrorCorrection(GeneralLedgerEntry entry) {
        DocumentHeader docHeader = SpringContext.getBean(DocumentHeaderService.class).getDocumentHeaderById(entry.getDocumentNumber());
        FinancialSystemDocumentHeader fsDocumentHeader = (FinancialSystemDocumentHeader) docHeader;
        
        return StringUtils.isNotBlank(fsDocumentHeader.getFinancialDocumentInErrorNumber());
    }
    /**
     * updates the submit amount by the amount on the accounting line.  When submit amount equals
     * transaction ledger amount, the activity status code is marked as in route status.
     * @param matchingGLEntry
     * @param accountLineAmount
     */
    protected void updateTransactionSumbitGlEntryAmount(GeneralLedgerEntry matchingGLEntry, KualiDecimal accountLineAmount) {
        //update submitted amount on the gl entry and save the results.
        KualiDecimal submitTotalAmount = KualiDecimal.ZERO;
        if (ObjectUtils.isNotNull(matchingGLEntry.getTransactionLedgerSubmitAmount())) {
            submitTotalAmount = matchingGLEntry.getTransactionLedgerSubmitAmount();
        }
        
        matchingGLEntry.setTransactionLedgerSubmitAmount(submitTotalAmount.add(accountLineAmount.abs()));

        if (matchingGLEntry.getTransactionLedgerSubmitAmount().equals(matchingGLEntry.getTransactionLedgerEntryAmount())) {
            matchingGLEntry.setActivityStatusCode(CabConstants.ActivityStatusCode.ENROUTE);
        }
        
        //save the updated gl entry in CAB
        this.getBusinessObjectService().save(matchingGLEntry);
    }
    
    /**
     * Creates asset payment detail based on GL line. to CAB
     * 
     * @param entry GeneralLedgerEntry
     * @param document Document
     * @return AssetPaymentDetail
     */
    protected AssetPaymentDetail createAssetPaymentDetail(GeneralLedgerEntry entry, Document document, int seqNo, Integer capitalAssetLineNumber) {
        // This is not added to constructor in CAMS to provide module isolation from CAMS
        AssetPaymentDetail detail = new AssetPaymentDetail();
        detail.setDocumentNumber(document.getDocumentNumber());
        detail.setSequenceNumber(seqNo);
        detail.setPostingYear(entry.getUniversityFiscalYear());
        detail.setPostingPeriodCode(entry.getUniversityFiscalPeriodCode());
        detail.setChartOfAccountsCode(entry.getChartOfAccountsCode());
        detail.setAccountNumber(replaceFiller(entry.getAccountNumber()));
        detail.setSubAccountNumber(replaceFiller(entry.getSubAccountNumber()));
        detail.setFinancialObjectCode(replaceFiller(entry.getFinancialObjectCode()));
        detail.setFinancialSubObjectCode(replaceFiller(entry.getFinancialSubObjectCode())); 
        detail.setProjectCode(replaceFiller(entry.getProjectCode()));
        detail.setOrganizationReferenceId(replaceFiller(entry.getOrganizationReferenceId()));
        KualiDecimal capitalAssetAmount = getCapitalAssetAmount(entry, capitalAssetLineNumber);
        detail.setAmount(capitalAssetAmount);
        detail.setExpenditureFinancialSystemOriginationCode(replaceFiller(entry.getFinancialSystemOriginationCode()));
        detail.setExpenditureFinancialDocumentNumber(entry.getDocumentNumber());
        detail.setExpenditureFinancialDocumentTypeCode(replaceFiller(entry.getFinancialDocumentTypeCode()));
        detail.setExpenditureFinancialDocumentPostedDate(entry.getTransactionDate());
        detail.setPurchaseOrderNumber(replaceFiller(entry.getReferenceFinancialDocumentNumber()));
        detail.setTransferPaymentIndicator(false);
        
        return detail;
    }
    
    /**
     * retrieves the amount from the capital asset
     * @param entry
     * @param capitalAssetLineNumber
     * @return capital asset amount.
     */
    protected KualiDecimal getCapitalAssetAmount(GeneralLedgerEntry entry, Integer capitalAssetLineNumber) {
        CapitalAssetInformation capitalAssetInformation = findCapitalAssetInformation(entry, capitalAssetLineNumber);
        if (ObjectUtils.isNotNull(capitalAssetInformation)) {
            return capitalAssetInformation.getCapitalAssetLineAmount();
        }
        
        return KualiDecimal.ZERO;
    }
    
    /**
     * If the value contains only the filler characters, then return blank
     * 
     * @param val Value
     * @return blank if value if a filler
     */
    protected String replaceFiller(String val) {
        if (val == null) {
            return "";
        }
        char[] charArray = val.trim().toCharArray();
        for (char c : charArray) {
            if (c != '-') {
                return val;
            }
        }
        return "";
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
    
    private AssetGlobalService getAssetGlobalService() {
        return assetGlobalService;
    }

    public void setAssetGlobalService(AssetGlobalService assetGlobalService) {
        this.assetGlobalService = assetGlobalService;
    }
    
    /**
     * Sets the objectTypeService attribute value.
     * 
     * @param objectTypeService The objectTypeService to set.
     */
    public void setObjectTypeService(ObjectTypeService objectTypeService) {
        this.objectTypeService = objectTypeService;
    }
}
