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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PretagDetail;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.document.service.PurApLineDocumentService;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderCapitalAssetSystem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
public class PurApLineDocumentServiceImpl implements PurApLineDocumentService {
    private static final Logger LOG = Logger.getLogger(PurApLineDocumentServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private DocumentService documentService;
    private PurApLineService purApLineService;

    public String processApplyPayment(PurchasingAccountsPayableItemAsset selectedItem, PurApLineForm purApForm, PurApLineSession purApLineSession) throws WorkflowException {
        AssetPaymentDocument newDocument = (AssetPaymentDocument) documentService.getNewDocument(AssetPaymentDocument.class);
        newDocument.getDocumentHeader().setDocumentDescription(CabConstants.NEW_ASSET_DOCUMENT_DESC);
        // set assetPaymentDetail list
        createAssetPaymentDetails(newDocument.getSourceAccountingLines(), selectedItem, newDocument.getDocumentNumber(), purApForm.getRequisitionIdentifier());

        // If PurAp user entered capitalAssetNumbers, include them in the Asset Payment Document.
        if (selectedItem.getPurApItemAssets() != null && !selectedItem.getPurApItemAssets().isEmpty()) {
            createAssetPaymentAssetDetails(newDocument.getAssetPaymentAssetDetail(), selectedItem, newDocument.getDocumentNumber());

        }
        // set origin code in the Asset Payment Document
        newDocument.setCapitalAssetBuilderOriginIndicator(true);
        documentService.saveDocument(newDocument);

        postProcessCreatingDocument(selectedItem, purApForm, purApLineSession, newDocument.getDocumentNumber());
        return newDocument.getDocumentNumber();
    }


    /**
     * Create AssetPaymentAssetDetail List for assetPaymentDocument.
     * 
     * @param assetPaymentAssetDetails
     * @param selectedItem
     * @param documentNumber
     */
    private void createAssetPaymentAssetDetails(List assetPaymentAssetDetails, PurchasingAccountsPayableItemAsset selectedItem, String documentNumber) {
        for (ItemCapitalAsset capitalAssetNumber : selectedItem.getPurApItemAssets()) {
            // check if capitalAssetNumber is a valid value or not.
            if (isAssetNumberValid(capitalAssetNumber.getCapitalAssetNumber())) {
                AssetPaymentAssetDetail assetDetail = new AssetPaymentAssetDetail();

                assetDetail.setCapitalAssetNumber(capitalAssetNumber.getCapitalAssetNumber());
                assetDetail.refreshReferenceObject(CamsPropertyConstants.AssetPaymentAssetDetail.ASSET);
                assetDetail.setDocumentNumber(documentNumber);

                AssetService assetService = SpringContext.getBean(AssetService.class);
                Asset candidateAsset = assetDetail.getAsset();
                // asset must be an active & not retired. Duplication check is done during feeding asset numbers from PurAp.
                if (ObjectUtils.isNotNull(candidateAsset) && assetService.isCapitalAsset(candidateAsset) && !assetService.isAssetRetired(candidateAsset)) {
                    assetDetail.setPreviousTotalCostAmount(assetDetail.getAsset().getTotalCostAmount());
                    assetPaymentAssetDetails.add(assetDetail);
                }

            }
        }
    }


    /**
     * Check the asset table if given capitalAssetNumber is valid or not.
     * 
     * @param capitalAssetNumber
     * @return
     */
    private boolean isAssetNumberValid(Long capitalAssetNumber) {
        Map pKeys = new HashMap<String, Object>();

        pKeys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, capitalAssetNumber);

        Asset asset = (Asset) businessObjectService.findByPrimaryKey(Asset.class, pKeys);

        return ObjectUtils.isNotNull(asset);
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processCreateAsset(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset,
     *      org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public String processCreateAsset(PurchasingAccountsPayableItemAsset selectedItem, PurApLineForm purApForm, PurApLineSession purApLineSession) throws WorkflowException {
        // Create new CAMS asset global document
        MaintenanceDocument newDocument = (MaintenanceDocument) documentService.getNewDocument(CabConstants.ASSET_GLOBAL_MAINTENANCE_DOCUMENT);
        newDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
        newDocument.getDocumentHeader().setDocumentDescription(CabConstants.NEW_ASSET_DOCUMENT_DESC);

        // populate pre-tagging entry
        Pretag preTag = purApLineService.getPreTagLineItem(purApForm.getPurchaseOrderIdentifier(), selectedItem.getItemLineNumber());

        // create asset global BO instance
        AssetGlobal assetGlobal = createAssetGlobal(selectedItem, newDocument.getDocumentNumber(), preTag, purApForm.getRequisitionIdentifier());

        // save asset global BO to the document
        newDocument.getNewMaintainableObject().setBusinessObject(assetGlobal);
        documentService.saveDocument(newDocument);

        postProcessCreatingDocument(selectedItem, purApForm, purApLineSession, newDocument.getDocumentNumber());

        // pretag looks go into an infinite loop at OJB ??
        if (isItemPretagged(preTag)) {
            businessObjectService.save(preTag);
        }
        return newDocument.getDocumentNumber();
    }


    /**
     * Process item line, cab document after creating CAMs document.
     * 
     * @param selectedItem
     * @param purApForm
     * @param purApLineSession
     * @param documentNumber
     */
    private void postProcessCreatingDocument(PurchasingAccountsPayableItemAsset selectedItem, PurApLineForm purApForm, PurApLineSession purApLineSession, String documentNumber) {
        // save CAMS document number in CAB
        selectedItem.setCapitalAssetManagementDocumentNumber(documentNumber);

        // in-activate item, item account and glEntry(conditionally)
        inActivateItem(selectedItem);

        // update submit amount in the associated general ledger entries.
        updateGlEntrySubmitAmount(selectedItem, purApLineSession.getGlEntryUpdateList());

        // in-activate document if all the associated items are inactive.
        if (ObjectUtils.isNotNull(selectedItem.getPurchasingAccountsPayableDocument())) {
            // link reference from item to document should be set in PurApLineAction.getSelectedLineItem().
            purApLineService.inActivateDocument(selectedItem.getPurchasingAccountsPayableDocument());
        }

        setFormActiveItemIndicator(purApForm);
        // persistent to the table
        purApLineService.processSaveBusinessObjects(purApForm, purApLineSession);
        // In-activate general ledger afterwards because we need to persistent account changes first. 
        List<GeneralLedgerEntry> glEntryUpdatesList = getGlEntryInActivedList(selectedItem);
        if (glEntryUpdatesList != null && !glEntryUpdatesList.isEmpty()) {
            businessObjectService.save(glEntryUpdatesList);
        }
    }


    /**
     * Update transactionLedgerSubmitAmount in the associated generalLedgerEntry for each item account.
     * 
     * @param selectedItem
     */
    private void updateGlEntrySubmitAmount(PurchasingAccountsPayableItemAsset selectedItem, List glEntryList) {
        GeneralLedgerEntry glEntry = null;
        for (PurchasingAccountsPayableLineAssetAccount account : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            glEntry = account.getGeneralLedgerEntry();

            if (ObjectUtils.isNotNull(glEntry)) {
                if (glEntry.getTransactionLedgerSubmitAmount() != null) {
                    glEntry.setTransactionLedgerSubmitAmount(glEntry.getTransactionLedgerSubmitAmount().add(account.getItemAccountTotalAmount()));
                }
                else {
                    glEntry.setTransactionLedgerSubmitAmount(new KualiDecimal(account.getItemAccountTotalAmount().toString()));
                }
            }
            // add to the session for persistence
            glEntryList.add(glEntry);
        }
    }

    /**
     * In-activate form activeItemExist indicator if all items associating with the form are inactive.
     * 
     * @param purApForm
     */
    private void setFormActiveItemIndicator(PurApLineForm purApForm) {
        for (PurchasingAccountsPayableDocument document : purApForm.getPurApDocs()) {
            if (document.isActive()) {
                purApForm.setActiveItemExist(true);
            }
        }
        purApForm.setActiveItemExist(false);
    }


    /**
     * Build asset details/shared details/unique details lists for new asset global document
     * 
     * @param selectedItem
     * @param newDocument
     * @param assetGlobal
     */
    private void setAssetGlobalDetails(PurchasingAccountsPayableItemAsset selectedItem, AssetGlobal assetGlobal, Pretag preTag, PurchaseOrderCapitalAssetSystem capitalAssetSystem) {
        // build assetGlobalDetail list( unique details list)
        List<AssetGlobalDetail> assetDetailsList = assetGlobal.getAssetGlobalDetails();
        // shared location details list
        List<AssetGlobalDetail> sharedDetails = assetGlobal.getAssetSharedDetails();
        for (int i = 0; i < selectedItem.getAccountsPayableItemQuantity().intValue(); i++) {
            AssetGlobalDetail assetGlobalDetail = new AssetGlobalDetail();
            assetGlobalDetail.setDocumentNumber(assetGlobal.getDocumentNumber());
            assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
            assetDetailsList.add(assetGlobalDetail);
            // build assetSharedDetails and assetGlobalUniqueDetails list. There two lists will be used to rebuild
            // assetGlobalDetails list when AssetGlobalMaintainableImpl.prepareForSave() is called during save the document.
            AssetGlobalDetail sharedDetail = new AssetGlobalDetail();
            sharedDetail.getAssetGlobalUniqueDetails().add(assetGlobalDetail);
            sharedDetails.add(sharedDetail);
        }

        // feeding data from pre-tag details into shared location details list and unique detail list
        if (isItemPretagged(preTag)) {
            setAssetDetailFromPreTag(preTag, sharedDetails, assetDetailsList);
        }
        else if (ObjectUtils.isNotNull(capitalAssetSystem)) {
            // feeding data from PurAp user input into assetGlobalDetail List (the list of asset global unique details reference)
            setAssetGlobalDetailFromPurAp(capitalAssetSystem, sharedDetails);
        }
    }

    /**
     * Set asset global detail location information from PurAp input. In this method, no grouping for shared location because
     * AssetGlobalMaintainableImpl.processAfterRetrieve() will group the shared location anyway.
     * 
     * @param capitalAssetSystem
     * @param assetDetailsList
     */
    private void setAssetGlobalDetailFromPurAp(PurchaseOrderCapitalAssetSystem capitalAssetSystem, List<AssetGlobalDetail> assetSharedDetail) {
        capitalAssetSystem.refreshReferenceObject(PurapPropertyConstants.CAPITAL_ASSET_LOCATIONS);
        List<CapitalAssetLocation> capitalAssetLocations = capitalAssetSystem.getCapitalAssetLocations();

        if (ObjectUtils.isNotNull(capitalAssetLocations) && !capitalAssetLocations.isEmpty()) {
            Iterator<CapitalAssetLocation> locationIterator = capitalAssetLocations.iterator();
            int locationQuantity = 0;
            CapitalAssetLocation assetLocation = null;
            for (AssetGlobalDetail assetDetail : assetSharedDetail) {
                // Each line item can have multiple locations and each location can have a quantity value with it.
                if (locationQuantity <= 0 && locationIterator.hasNext()) {
                    assetLocation = locationIterator.next();
                    if (assetLocation.getItemQuantity() != null) {
                        locationQuantity = assetLocation.getItemQuantity().intValue();
                    }
                    else {
                        // if Purap not set item quantity, CAB batch should set it to default value 1. So we set location quantity
                        // the same value.
                        locationQuantity = 1;
                    }
                }
                else if (locationQuantity <= 0 && !locationIterator.hasNext()) {
                    break;
                }

                assetDetail.setCampusCode(assetLocation.getCampusCode());
                if (assetLocation.isOffCampusIndicator()) {
                    // off-campus
                    assetDetail.setOffCampusCityName(assetLocation.getCapitalAssetCityName());
                    assetDetail.setOffCampusAddress(assetLocation.getCapitalAssetLine1Address());
                    assetDetail.setOffCampusCountryCode(assetLocation.getCapitalAssetCountryCode());
                    assetDetail.setOffCampusStateCode(assetLocation.getCapitalAssetStateCode());
                    assetDetail.setOffCampusZipCode(assetLocation.getCapitalAssetPostalCode());
                }
                else {
                    // on-campus
                    assetDetail.setBuildingCode(assetLocation.getBuildingCode());
                    assetDetail.setBuildingRoomNumber(assetLocation.getBuildingRoomNumber());
                }

                locationQuantity--;
            }
        }

    }


    /**
     * Feeding data into assetGlobalDetail list from preTagDetail
     * 
     * @param preTag
     * @param assetDetailsList
     */
    private void setAssetDetailFromPreTag(Pretag preTag, List<AssetGlobalDetail> assetSharedDetails, List<AssetGlobalDetail> assetUniqueDetails) {
        // preTag.refreshReferenceObject(CabPropertyConstants.Pretag.PRE_TAG_DETAIS);

        Iterator<AssetGlobalDetail> sharedDetailsIterator = assetSharedDetails.iterator();
        Iterator<AssetGlobalDetail> uniqueDetailsIterator = assetUniqueDetails.iterator();
        for (PretagDetail preTagDetail : preTag.getPretagDetails()) {
            if (sharedDetailsIterator.hasNext()) {
                // set shared location details
                AssetGlobalDetail sharedDetail = sharedDetailsIterator.next();
                sharedDetail.setBuildingCode(preTagDetail.getBuildingCode());
                sharedDetail.setBuildingRoomNumber(preTagDetail.getBuildingRoomNumber());
                sharedDetail.setBuildingSubRoomNumber(preTagDetail.getBuildingSubRoomNumber());
                sharedDetail.setCampusCode(preTagDetail.getCampusCode());
                // in-activate pre-tagging detail
                preTagDetail.setActive(false);
            }
            if (uniqueDetailsIterator.hasNext()) {
                // set asset unique detail
                AssetGlobalDetail uniqueDetail = uniqueDetailsIterator.next();
                uniqueDetail.setGovernmentTagNumber(preTagDetail.getGovernmentTagNumber());
                uniqueDetail.setNationalStockNumber(preTagDetail.getNationalStockNumber());
                uniqueDetail.setCampusTagNumber(preTagDetail.getCampusTagNumber());
                uniqueDetail.setOrganizationInventoryName(preTag.getOrganizationInventoryName());
                // in-activate pre-tagging detail
                preTagDetail.setActive(false);
            }
        }
        // In-activate preTag if possible.
        inActivatePreTag(preTag);
    }

    /**
     * In-activate preTag if all its preTagDetail entry are inactive.
     * 
     * @param preTag
     */
    private void inActivatePreTag(Pretag preTag) {
        // get the number of inactive pre-tag detail.
        int inActiveCounter = 0;
        for (PretagDetail preTagDetail : preTag.getPretagDetails()) {
            if (!preTagDetail.isActive()) {
                inActiveCounter++;
            }
        }
        // if the number of inactive preTagDetail is equal or greater than (when quantityInvoiced is decimal) quantityInvoiced,
        // in-activate the preTag active field.
        if (preTag.getQuantityInvoiced().isLessEqual(new KualiDecimal(inActiveCounter))) {
            preTag.setActive(false);
        }
    }

    /**
     * Build asset payment details list for new asset global document.
     * 
     * @param selectedItem
     * @param assetGlobal
     * @param requisitionIdentifier
     */
    private void createAssetPaymentDetails(List<AssetPaymentDetail> assetPaymentList, PurchasingAccountsPayableItemAsset selectedItem, String documentNumber, Integer requisitionIdentifier) {
        int seq = 1;

        for (PurchasingAccountsPayableLineAssetAccount account : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            account.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.GENERAL_LEDGER_ENTRY);
            GeneralLedgerEntry glEntry = account.getGeneralLedgerEntry();

            if (ObjectUtils.isNotNull(glEntry)) {
                AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail();
                // initialize payment detail fields
                assetPaymentDetail.setDocumentNumber(documentNumber);
                assetPaymentDetail.setSequenceNumber(Integer.valueOf(seq++));
                assetPaymentDetail.setChartOfAccountsCode(glEntry.getChartOfAccountsCode());
                assetPaymentDetail.setAccountNumber(replaceFiller(glEntry.getAccountNumber()));
                assetPaymentDetail.setSubAccountNumber(replaceFiller(glEntry.getSubAccountNumber()));
                assetPaymentDetail.setFinancialObjectCode(replaceFiller(glEntry.getFinancialObjectCode()));
                assetPaymentDetail.setFinancialSubObjectCode(replaceFiller(glEntry.getFinancialSubObjectCode()));
                assetPaymentDetail.setProjectCode(replaceFiller(glEntry.getProjectCode()));
                assetPaymentDetail.setOrganizationReferenceId(glEntry.getOrganizationReferenceId());
                assetPaymentDetail.setPostingYear(glEntry.getUniversityFiscalYear());
                assetPaymentDetail.setPostingPeriodCode(glEntry.getUniversityFiscalPeriodCode());
                assetPaymentDetail.setExpenditureFinancialSystemOriginationCode(replaceFiller(glEntry.getFinancialSystemOriginationCode()));
                assetPaymentDetail.setExpenditureFinancialDocumentNumber(glEntry.getDocumentNumber());
                assetPaymentDetail.setExpenditureFinancialDocumentTypeCode(replaceFiller(glEntry.getFinancialDocumentTypeCode()));
                assetPaymentDetail.setExpenditureFinancialDocumentPostedDate(glEntry.getTransactionPostingDate());
                assetPaymentDetail.setAmount(account.getItemAccountTotalAmount());
                assetPaymentDetail.setPurchaseOrderNumber(replaceFiller(glEntry.getReferenceFinancialDocumentNumber()));
                assetPaymentDetail.setRequisitionNumber(requisitionIdentifier.toString());
                assetPaymentDetail.setTransferPaymentIndicator(false);

                // add to payment list
                assetPaymentList.add(assetPaymentDetail);
            }
        }
    }


    /**
     * In-activate item, item Account and generalLedgerEntry active indicator.
     * 
     * @param selectedItem
     * @param glEntryList
     */
    private void inActivateItem(PurchasingAccountsPayableItemAsset selectedItem) {
        for (PurchasingAccountsPayableLineAssetAccount selectedAccount : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            // in-active account.
            selectedAccount.setActive(false);
        }
        // in-activate selected Item
        selectedItem.setActive(false);
    }

    /**
     * Update GL Entry active indicator to false if all its amount are consumed by submit CAMs document.Return the general ledger entry changes as a list.
     * 
     * @param glEntryList
     * @param selectedAccount
     * @param glEntry
     */
    private List<GeneralLedgerEntry> getGlEntryInActivedList(PurchasingAccountsPayableItemAsset selectedItem) {
        List<GeneralLedgerEntry> glEntryUpdateList = new ArrayList<GeneralLedgerEntry>();

        for (PurchasingAccountsPayableLineAssetAccount selectedAccount : selectedItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            GeneralLedgerEntry glEntry = selectedAccount.getGeneralLedgerEntry();
            boolean glEntryHasActiveAccount = false;
            glEntry.refreshReferenceObject(CabPropertyConstants.GeneralLedgerEntry.PURAP_LINE_ASSET_ACCOUNTS);
            for (PurchasingAccountsPayableLineAssetAccount account : glEntry.getPurApLineAssetAccounts()) {
                // check if all accounts are inactive status excluding the selected account.
                if (!(selectedAccount.getDocumentNumber().equalsIgnoreCase(account.getDocumentNumber()) && selectedAccount.getAccountsPayableLineItemIdentifier().equals(account.getAccountsPayableLineItemIdentifier()) && selectedAccount.getCapitalAssetBuilderLineNumber().equals(account.getCapitalAssetBuilderLineNumber())) && account.isActive()) {
                    glEntryHasActiveAccount = true;
                    break;
                }
            }

            // if one account shows active, won't in-activate this general ledger entry.
            if (!glEntryHasActiveAccount) {
                glEntry.setActive(false);
                glEntryUpdateList.add(glEntry);
            }
        }
        return glEntryUpdateList;
    }


    protected String replaceFiller(String val) {
        return val == null ? "" : val.trim().replaceAll("-", "");
    }

    /**
     * Create AssetGlobal BO and feed data from pre-asset tagging table.
     * 
     * @param selectedItem
     * @param newDocument
     * @param preTag
     * @return
     */
    private AssetGlobal createAssetGlobal(PurchasingAccountsPayableItemAsset selectedItem, String documentNumber, Pretag preTag, Integer requisitionIdentifier) {
        AssetGlobal assetGlobal = new AssetGlobal();
        assetGlobal.setDocumentNumber(documentNumber);
        assetGlobal.setCapitalAssetDescription(selectedItem.getAccountsPayableLineItemDescription());
        assetGlobal.setConditionCode(CamsConstants.CONDITION_CODE_E);
        assetGlobal.setPrimaryDepreciationMethodCode(CamsConstants.DEPRECIATION_METHOD_STRAIGHT_LINE_CODE);
        assetGlobal.setAcquisitionTypeCode(CamsConstants.AssetGlobal.NEW_ACQUISITION_TYPE_CODE);
        assetGlobal.setInventoryStatusCode(CamsConstants.InventoryStatusCode.CAPITAL_ASSET_ACTIVE_IDENTIFIABLE);
        // set origin code in the Asset Payment Document
        assetGlobal.setCapitalAssetBuilderOriginIndicator(true);

        PurchaseOrderCapitalAssetSystem capitalAssetSystem = null;
        // feeding data from pre-asset tagging table.
        if (isItemPretagged(preTag)) {
            setAssetGlobalFromPreTag(preTag, assetGlobal);
        }
        else if (selectedItem.getCapitalAssetSystemIdentifier() != null) {
            // check and set if purAp has new asset information
            capitalAssetSystem = findCapitalAssetSystem(selectedItem.getCapitalAssetSystemIdentifier());
            if (ObjectUtils.isNotNull(capitalAssetSystem)) {
                setAssetGlobalFromPurAp(assetGlobal, capitalAssetSystem);
            }
        }

        // set asset global detail list
        setAssetGlobalDetails(selectedItem, assetGlobal, preTag, capitalAssetSystem);

        // build payments list for asset global
        createAssetPaymentDetails(assetGlobal.getAssetPaymentDetails(), selectedItem, documentNumber, requisitionIdentifier);

        // set total cost
        setAssetGlobalTotalCost(assetGlobal);
        // Set Asset Global organization owner account
        setAssetGlobalOrgOwnerAccount(assetGlobal);

        return assetGlobal;
    }


    private boolean isItemPretagged(Pretag preTag) {
        return ObjectUtils.isNotNull(preTag) && ObjectUtils.isNotNull(preTag.getPretagDetails()) && !preTag.getPretagDetails().isEmpty();
    }


    /**
     * Set asset information from PurAp PurchaseOrderCapitalAssetSystem.
     * 
     * @param assetGlobal
     * @param capitalAssetSystem
     */
    private void setAssetGlobalFromPurAp(AssetGlobal assetGlobal, PurchaseOrderCapitalAssetSystem capitalAssetSystem) {
        assetGlobal.setManufacturerName(capitalAssetSystem.getCapitalAssetManufacturerName());
        assetGlobal.setManufacturerModelNumber(capitalAssetSystem.getCapitalAssetModelDescription());
        assetGlobal.setCapitalAssetTypeCode(capitalAssetSystem.getCapitalAssetTypeCode());
    }


    private PurchaseOrderCapitalAssetSystem findCapitalAssetSystem(Integer capitalAssetSystemIdentifier) {
        Map pKeys = new HashMap<String, Object>();

        pKeys.put(PurapPropertyConstants.CAPITAL_ASSET_SYSTEM_IDENTIFIER, capitalAssetSystemIdentifier);
        return (PurchaseOrderCapitalAssetSystem) businessObjectService.findByPrimaryKey(PurchaseOrderCapitalAssetSystem.class, pKeys);
    }


    /**
     * Set Asset Global org owner account and chart code. It's assigned by selecting the account that contributed the most dollars
     * on the payment request.
     * 
     * @param assetGlobal
     */
    private void setAssetGlobalOrgOwnerAccount(AssetGlobal assetGlobal) {
        AssetPaymentDetail maxCostPayment = null;

        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            if (maxCostPayment == null || assetPaymentDetail.getAmount().isGreaterThan(maxCostPayment.getAmount())) {
                maxCostPayment = assetPaymentDetail;
            }
        }

        if (maxCostPayment != null) {
            assetGlobal.setOrganizationOwnerAccountNumber(maxCostPayment.getAccountNumber());
            assetGlobal.setOrganizationOwnerChartOfAccountsCode(maxCostPayment.getChartOfAccountsCode());
        }
    }


    /**
     * Set Asset Global total cost amount.
     * 
     * @param assetGlobal
     */
    private void setAssetGlobalTotalCost(AssetGlobal assetGlobal) {
        KualiDecimal totalCost = KualiDecimal.ZERO;
        for (AssetPaymentDetail assetPaymentDetail : assetGlobal.getAssetPaymentDetails()) {
            totalCost = totalCost.add(assetPaymentDetail.getAmount());
        }

        assetGlobal.setTotalCostAmount(totalCost);
    }


    /**
     * Feeding data from preTag and set into asset global for shared information.
     * 
     * @param preTag
     * @param assetGlobal
     */
    private void setAssetGlobalFromPreTag(Pretag preTag, AssetGlobal assetGlobal) {
        assetGlobal.setManufacturerName(preTag.getManufacturerName());
        assetGlobal.setManufacturerModelNumber(preTag.getManufacturerModelNumber());
        assetGlobal.setCapitalAssetTypeCode(preTag.getCapitalAssetTypeCode());
        assetGlobal.setOrganizationText(preTag.getOrganizationText());
        assetGlobal.setRepresentativeUniversalIdentifier(preTag.getRepresentativeUniversalIdentifier());
        assetGlobal.setVendorName(preTag.getVendorName());
        // acquisition type code is set to "P"(Pre-asset tagging)
        assetGlobal.setAcquisitionTypeCode(CamsConstants.AssetGlobal.PRE_TAGGING_ACQUISITION_TYPE_CODE);
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
     * Gets the documentService attribute.
     * 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }


    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the purApLineService attribute.
     * 
     * @return Returns the purApLineService.
     */
    public PurApLineService getPurApLineService() {
        return purApLineService;
    }

    /**
     * Sets the purApLineService attribute value.
     * 
     * @param purApLineService The purApLineService to set.
     */
    public void setPurApLineService(PurApLineService purApLineService) {
        this.purApLineService = purApLineService;
    }


}
