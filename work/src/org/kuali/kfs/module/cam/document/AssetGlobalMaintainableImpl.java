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
package org.kuali.kfs.module.cam.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetDepreciationConvention;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetOrganization;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.AssetType;
import org.kuali.kfs.module.cam.businessobject.defaultvalue.NextAssetNumberFinder;
import org.kuali.kfs.module.cam.document.service.AssetDateService;
import org.kuali.kfs.module.cam.document.service.AssetGlobalService;
import org.kuali.kfs.module.cam.document.validation.impl.AssetGlobalRule;
import org.kuali.kfs.module.cam.document.workflow.RoutingAssetNumber;
import org.kuali.kfs.module.cam.document.workflow.RoutingAssetTagNumber;
import org.kuali.kfs.module.cam.util.KualiDecimalUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global assets
 */
public class AssetGlobalMaintainableImpl extends KualiGlobalMaintainableImpl implements GenericRoutingInfo {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalMaintainableImpl.class);
    private Set<RoutingData> routingInfo;

    /**
     * Get Asset from AssetGlobal
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        document.getNewMaintainableObject().setGenerateDefaultValues(false);

        // set "asset number" and "type code" from URL
        setSeparateSourceCapitalAssetParameters(assetGlobal, parameters);
        setFinancialDocumentTypeCode(assetGlobal, parameters);

        // set current date for Global and Separate.
        assetGlobal.setLastInventoryDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

        // populate required fields for "Asset Separate" doc
        if (getAssetGlobalService().isAssetSeparateDocument(assetGlobal)) {
            Asset asset = getAsset(assetGlobal);
            AssetOrganization assetOrganization = getAssetOrganization(assetGlobal);
            populateAssetSeparateAssetDetails(assetGlobal, asset, assetOrganization);
            populateAssetSeparatePaymentDetails(assetGlobal, asset);
            
            AssetGlobalRule.validateAssetTotalCostMatchesPaymentTotalCost(assetGlobal);
            
            if (getAssetGlobalService().isAssetSeparateByPaymentDocument(assetGlobal)) {
                AssetGlobalRule.validateAssetAlreadySeparated(assetGlobal.getSeparateSourceCapitalAssetNumber());
            }
        }

        super.processAfterNew(document, parameters);
    }

    /**
     * Get Asset from AssetGlobal
     * 
     * @param assetGlobal
     * @return Asset
     */
    private Asset getAsset(AssetGlobal assetGlobal) {
        HashMap map = new HashMap();
        map.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, assetGlobal.getSeparateSourceCapitalAssetNumber());
        Asset asset = (Asset) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Asset.class, map);
        return asset;
    }

    /**
     * Get AssetOrganization from AssetGlobal
     * 
     * @param assetGlobal
     * @return AssetOrganization
     */
    private AssetOrganization getAssetOrganization(AssetGlobal assetGlobal) {
        HashMap map = new HashMap();
        map.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, assetGlobal.getSeparateSourceCapitalAssetNumber());
        AssetOrganization assetOrganization = (AssetOrganization) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AssetOrganization.class, map);
        return assetOrganization;
    }

    /**
     * Populate Asset Details for Asset Separate document
     * 
     * @param assetGlobal
     * @param asset
     * @param assetOrganization
     */
    private void populateAssetSeparateAssetDetails(AssetGlobal assetGlobal, Asset asset, AssetOrganization assetOrganization) {
        assetGlobal.setOrganizationOwnerAccountNumber(asset.getOrganizationOwnerAccountNumber());
        assetGlobal.setOrganizationOwnerChartOfAccountsCode(asset.getOrganizationOwnerChartOfAccountsCode());
        assetGlobal.setAgencyNumber(asset.getAgencyNumber());
        assetGlobal.setAcquisitionTypeCode(asset.getAcquisitionTypeCode());
        assetGlobal.setInventoryStatusCode(asset.getInventoryStatusCode());
        assetGlobal.setConditionCode(asset.getConditionCode());
        assetGlobal.setCapitalAssetDescription(asset.getCapitalAssetDescription());
        assetGlobal.setCapitalAssetTypeCode(asset.getCapitalAssetTypeCode());
        assetGlobal.setVendorName(asset.getVendorName());
        assetGlobal.setManufacturerName(asset.getManufacturerName());
        assetGlobal.setManufacturerModelNumber(asset.getManufacturerModelNumber());
        if (ObjectUtils.isNotNull(assetOrganization)) {
            assetGlobal.setOrganizationText(assetOrganization.getOrganizationText());
        }
        // added in case of NULL date in DB
        if (asset.getLastInventoryDate() == null) {
            assetGlobal.setLastInventoryDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        }
        else {
            assetGlobal.setLastInventoryDate(new java.sql.Date(asset.getLastInventoryDate().getTime()));
        }
        assetGlobal.setCreateDate(asset.getCreateDate());
        assetGlobal.setCapitalAssetInServiceDate(asset.getCapitalAssetInServiceDate());
        assetGlobal.setLandCountyName(asset.getLandCountyName());
        assetGlobal.setLandAcreageSize(asset.getLandAcreageSize());
        assetGlobal.setLandParcelNumber(asset.getLandParcelNumber());
    }

    /**
     * Populate Asset Payment Details for Asset Separate document. It will do this whether we are seperating by asset or payment. If it is by asset
     * it picks up all the payments and sets the total amount on the document of that per the asset. If it is by payment it picks only the payment
     * out we are interested in and set the document total amount to that payment only.
     * 
     * @param assetGlobal
     * @param asset
     */
    private void populateAssetSeparatePaymentDetails(AssetGlobal assetGlobal, Asset asset) {
        // clear and create temp AssetPaymentDetail list
        assetGlobal.getAssetPaymentDetails().clear();
        List<AssetPaymentDetail> newAssetPaymentDetailList = assetGlobal.getAssetPaymentDetails();

        if (!getAssetGlobalService().isAssetSeparateByPaymentDocument(assetGlobal)) {
            // Separate by Asset. Pick all payments up
            
            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                // create new AssetPaymentDetail
                AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail(assetPayment);
    
                // add assetPaymentDetail to AssetPaymentDetail list
                newAssetPaymentDetailList.add(assetPaymentDetail);
            }
            
            // Set total amount per asset
            assetGlobal.setTotalCostAmount(asset.getTotalCostAmount());
            assetGlobal.setSeparateSourceRemainingAmount(asset.getTotalCostAmount());
        } else {
            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                // Separate by Payment. Pick only the appropriate payment up and then break
                
                if (assetPayment.getPaymentSequenceNumber().equals(assetGlobal.getSeparateSourcePaymentSequenceNumber())) {
                    // create new AssetPaymentDetail
                    AssetPaymentDetail assetPaymentDetail = new AssetPaymentDetail(assetPayment);
        
                    // add assetPaymentDetail to AssetPaymentDetail list
                    newAssetPaymentDetailList.add(assetPaymentDetail);
                    
                    // Set total amount per payment
                    assetGlobal.setTotalCostAmount(assetPayment.getAccountChargeAmount());
                    assetGlobal.setSeparateSourceRemainingAmount(assetPayment.getAccountChargeAmount());
                    
                    break;
                }
            }
        }

        assetGlobal.setSeparateSourceTotalAmount(KualiDecimal.ZERO);
        
        // set AssetGlobal payment details with new payment details
        assetGlobal.setAssetPaymentDetails(newAssetPaymentDetailList);
    }

    /**
     * Set capital asset number and payment sequence number from URL on the AssetGlobal BO. It only does so if each is available.
     * 
     * @see org.kuali.module.cams.lookup.AssetLookupableHelperServiceImpl#getSeparateUrl(BusinessObject)
     * @see org.kuali.module.cams.lookup.AssetPaymentLookupableHelperServiceImpl#getSeparateUrl(BusinessObject)
     * @param assetGlobal
     * @param parameters
     */
    private void setSeparateSourceCapitalAssetParameters(AssetGlobal assetGlobal, Map<String, String[]> parameters) {
        String[] separateSourceCapitalAssetNumber = parameters.get(CamsPropertyConstants.AssetGlobal.SEPARATE_SOURCE_CAPITAL_ASSET_NUMBER);
        if (separateSourceCapitalAssetNumber != null) {
            assetGlobal.setSeparateSourceCapitalAssetNumber(Long.parseLong(separateSourceCapitalAssetNumber[0].toString()));
        }
        
        String[] separateSourcePaymentSequenceNumber = parameters.get(CamsPropertyConstants.AssetGlobal.SEPERATE_SOURCE_PAYMENT_SEQUENCE_NUMBER);
        if (separateSourcePaymentSequenceNumber != null) {
            assetGlobal.setSeparateSourcePaymentSequenceNumber(Integer.parseInt(separateSourcePaymentSequenceNumber[0].toString()));
        }
    }

    /**
     * Set document type code from URL.
     * 
     * @see org.kuali.module.cams.lookup.AssetLookupableHelperServiceImpl#getSeparateUrl(BusinessObject)
     * @param assetGlobal
     * @param parameters
     */
    private void setFinancialDocumentTypeCode(AssetGlobal assetGlobal, Map<String, String[]> parameters) {
        String[] financialDocumentTypeCode = parameters.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        if (financialDocumentTypeCode != null) {
            assetGlobal.setFinancialDocumentTypeCode(financialDocumentTypeCode[0].toString());
        }
    }

    /**
     * Hook for quantity and setting asset numbers.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();

        if (CamsPropertyConstants.AssetGlobal.ASSET_PAYMENT_DETAILS.equalsIgnoreCase(collectionName)) {
            handAssetPaymentsCollection(collectionName, assetGlobal);
        }
        if (CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS.equalsIgnoreCase(collectionName)) {
            handleAssetSharedDetailsCollection(collectionName, assetGlobal);
        }
        int sharedDetailsIndex = assetGlobal.getAssetSharedDetails().size() - 1;
        if (sharedDetailsIndex > -1 && (CamsPropertyConstants.AssetGlobal.ASSET_SHARED_DETAILS + "[" + sharedDetailsIndex + "]." + CamsPropertyConstants.AssetGlobalDetail.ASSET_UNIQUE_DETAILS).equalsIgnoreCase(collectionName)) {
            handleAssetUniqueCollection(collectionName, assetGlobal);
        }
        super.addNewLineToCollection(collectionName);
    }

    /**
     * Sets required fields with specific values when an individual unique asset added.
     * 
     * @param collectionName
     */
    private void handleAssetUniqueCollection(String collectionName, AssetGlobal assetGlobal) {
        AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) newCollectionLines.get(collectionName);

        if (ObjectUtils.isNotNull(assetGlobalDetail)) {
            assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());

            // if not set, populate unique asset fields using original asset data. "Asset Separate" doc (location tab)
            if (ObjectUtils.isNotNull(assetGlobal)) {
                if (getAssetGlobalService().isAssetSeparateDocument(assetGlobal)) {
                    if (assetGlobalDetail.getCapitalAssetTypeCode() == null) {
                        assetGlobalDetail.setCapitalAssetTypeCode(assetGlobal.getCapitalAssetTypeCode());
                    }
                    if (assetGlobalDetail.getCapitalAssetDescription() == null) {
                        assetGlobalDetail.setCapitalAssetDescription(assetGlobal.getCapitalAssetDescription());
                    }
                    if (assetGlobalDetail.getManufacturerName() == null) {
                        assetGlobalDetail.setManufacturerName(assetGlobal.getManufacturerName());
                    }
                    if (assetGlobalDetail.getSeparateSourceAmount() == null) {
                        assetGlobalDetail.setSeparateSourceAmount(KualiDecimal.ZERO);
                    }
                }
            }
        }
    }

    /**
     * Sets required fields with specific values when multiple unique assets added (i.e. field "Quantity Of Assets To Be Created").
     * 
     * @param collectionName
     * @param assetGlobal
     */
    private void handleAssetSharedDetailsCollection(String collectionName, AssetGlobal assetGlobal) {
        AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) newCollectionLines.get(collectionName);
        Integer locationQuantity = assetGlobalDetail.getLocationQuantity();
        while (locationQuantity != null && locationQuantity > 0) {
            AssetGlobalDetail newAssetUnique = new AssetGlobalDetail();
            newAssetUnique.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());

            // populate unique asset fields using original asset data. "Asset Separate" doc (location tab)
            if (getAssetGlobalService().isAssetSeparateDocument(assetGlobal)) {
                newAssetUnique.setCapitalAssetTypeCode(assetGlobal.getCapitalAssetTypeCode());
                newAssetUnique.setCapitalAssetDescription(assetGlobal.getCapitalAssetDescription());
                newAssetUnique.setManufacturerName(assetGlobal.getManufacturerName());
                newAssetUnique.setSeparateSourceAmount(KualiDecimal.ZERO);
            }
            assetGlobalDetail.getAssetGlobalUniqueDetails().add(newAssetUnique);
            newAssetUnique.setNewCollectionRecord(true);
            locationQuantity--;
        }
    }

    /**
     * Sets the default values in some of the fields of the asset payment section
     * 
     * @param collectionName
     * @param assetGlobal
     */
    private void handAssetPaymentsCollection(String collectionName, AssetGlobal assetGlobal) {
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) newCollectionLines.get(collectionName);
        if (assetPaymentDetail != null) {
            assetPaymentDetail.setSequenceNumber(assetGlobal.incrementFinancialDocumentLineNumber());
            // Set for document number and document type code
            if (getAssetGlobalService().existsInGroup(CamsConstants.AssetGlobal.NON_NEW_ACQUISITION_CODE_GROUP, assetGlobal.getAcquisitionTypeCode())) {
                assetPaymentDetail.setExpenditureFinancialDocumentNumber(documentNumber);
                assetPaymentDetail.setExpenditureFinancialDocumentTypeCode(CamsConstants.AssetGlobal.ADD_ASSET_DOCUMENT_TYPE_CODE);
                assetPaymentDetail.setExpenditureFinancialSystemOriginationCode(KFSConstants.ORIGIN_CODE_KUALI);
            }
        }
    }

    /**
     * Creates locking representation for this global document. The locking is only applicable for assets that are being split. The assets
     * that are being created do not need to be locked since they don't exist yet.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        
        AssetGlobalService assetGlobalService = SpringContext.getBean(AssetGlobalService.class);
        
        if (assetGlobalService.isAssetSeparateDocument(assetGlobal)) {
            MaintenanceLock assetSeperateMaintenanceLock = new MaintenanceLock();
            StringBuffer lockRep = new StringBuffer();

            lockRep.append(Asset.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(assetGlobal.getSeparateSourceCapitalAssetNumber());

            assetSeperateMaintenanceLock.setDocumentNumber(assetGlobal.getDocumentNumber());
            assetSeperateMaintenanceLock.setLockingRepresentation(lockRep.toString());
            maintenanceLocks.add(assetSeperateMaintenanceLock);
        }
            
        return maintenanceLocks;
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        super.prepareForSave();
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
        List<AssetGlobalDetail> newDetails = new TypedArrayList(AssetGlobalDetail.class);
        AssetGlobalDetail newAssetGlobalDetail = null;
        if (!assetSharedDetails.isEmpty() && !assetSharedDetails.get(0).getAssetGlobalUniqueDetails().isEmpty()) {

            for (AssetGlobalDetail locationDetail : assetSharedDetails) {
                List<AssetGlobalDetail> assetGlobalUniqueDetails = locationDetail.getAssetGlobalUniqueDetails();

                for (AssetGlobalDetail detail : assetGlobalUniqueDetails) {
                    // read from location and set it to detail
                    detail.setCampusCode(locationDetail.getCampusCode());
                    detail.setBuildingCode(locationDetail.getBuildingCode());
                    detail.setBuildingRoomNumber(locationDetail.getBuildingRoomNumber());
                    detail.setBuildingSubRoomNumber(locationDetail.getBuildingSubRoomNumber());
                    detail.setOffCampusName(locationDetail.getOffCampusName());
                    detail.setOffCampusAddress(locationDetail.getOffCampusAddress());
                    detail.setOffCampusCityName(locationDetail.getOffCampusCityName());
                    detail.setOffCampusStateCode(locationDetail.getOffCampusStateCode());
                    detail.setOffCampusCountryCode(locationDetail.getOffCampusCountryCode());
                    detail.setOffCampusZipCode(locationDetail.getOffCampusZipCode());
                    newDetails.add(detail);
                }

            }
        }

        if (assetGlobal.getCapitalAssetTypeCode() != null) {
            assetGlobal.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.CAPITAL_ASSET_TYPE);
            AssetType capitalAssetType = assetGlobal.getCapitalAssetType();
            if (ObjectUtils.isNotNull(capitalAssetType)) {
                if (capitalAssetType.getDepreciableLifeLimit() != null && capitalAssetType.getDepreciableLifeLimit().intValue() != 0) {
                    assetGlobal.setCapitalAssetInServiceDate(assetGlobal.getCreateDate() == null ? SpringContext.getBean(DateTimeService.class).getCurrentSqlDate() : assetGlobal.getCreateDate());
                }
                else {
                    assetGlobal.setCapitalAssetInServiceDate(null);
                }
                computeDepreciationDate(assetGlobal);
            }
        }
        assetGlobal.getAssetGlobalDetails().clear();
        assetGlobal.setAssetGlobalDetails(newDetails);
    }

    /**
     * computes depreciation date
     * 
     * @param assetGlobal
     */
    private void computeDepreciationDate(AssetGlobal assetGlobal) {
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        if (assetPaymentDetails != null && !assetPaymentDetails.isEmpty()) {

            LOG.debug("Compute depreciation date based on asset type, depreciation convention and in-service date");
            AssetPaymentDetail firstAssetPaymentDetail = assetPaymentDetails.get(0);
            ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(firstAssetPaymentDetail.getPostingYear(), firstAssetPaymentDetail.getChartOfAccountsCode(), firstAssetPaymentDetail.getFinancialObjectCode());
            if (ObjectUtils.isNotNull(objectCode)) {
                Map<String, String> primaryKeys = new HashMap<String, String>();
                primaryKeys.put(CamsPropertyConstants.AssetDepreciationConvention.FINANCIAL_OBJECT_SUB_TYPE_CODE, objectCode.getFinancialObjectSubTypeCode());
                AssetDepreciationConvention depreciationConvention = (AssetDepreciationConvention) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(AssetDepreciationConvention.class, primaryKeys);
                Date depreciationDate = SpringContext.getBean(AssetDateService.class).computeDepreciationDate(assetGlobal.getCapitalAssetType(), depreciationConvention, assetGlobal.getCapitalAssetInServiceDate());
                assetGlobal.setCapitalAssetDepreciationDate(depreciationDate);
            }
        }
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        assetGlobal.refresh();
        List<AssetGlobalDetail> assetGlobalDetails = assetGlobal.getAssetGlobalDetails();
        AssetGlobalDetail currLocationDetail = null;
        HashMap<String, AssetGlobalDetail> locationMap = new HashMap<String, AssetGlobalDetail>();
        AssetGlobalDetail copyValue = null;
        for (AssetGlobalDetail detail : assetGlobalDetails) {
            copyValue = (AssetGlobalDetail) ObjectUtils.deepCopy(detail);
            copyValue.getAssetGlobalUniqueDetails().clear();
            String key = generateLocationKey(copyValue);
            if ((currLocationDetail = locationMap.get(key)) == null) {
                currLocationDetail = copyValue;
                locationMap.put(key, currLocationDetail);
            }
            currLocationDetail.getAssetGlobalUniqueDetails().add(copyValue);
            currLocationDetail.setLocationQuantity(currLocationDetail.getAssetGlobalUniqueDetails().size());
        }
        assetGlobal.getAssetSharedDetails().clear();
        assetGlobal.getAssetSharedDetails().addAll(locationMap.values());

        // No update could be made to payment if it's created from CAB. Here, disable delete button if payment already added into
        // collection.
        if (assetGlobal.isCapitalAssetBuilderOriginIndicator()) {
            for (AssetPaymentDetail payment : assetGlobal.getAssetPaymentDetails()) {
                payment.setNewCollectionRecord(false);
            }
        }
    }

    private String generateLocationKey(AssetGlobalDetail location) {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getCampusCode() == null ? "" : location.getCampusCode().trim().toLowerCase());
        builder.append(location.getBuildingCode() == null ? "" : location.getBuildingCode().trim().toLowerCase());
        builder.append(location.getBuildingRoomNumber() == null ? "" : location.getBuildingRoomNumber().trim().toLowerCase());
        builder.append(location.getBuildingSubRoomNumber() == null ? "" : location.getBuildingSubRoomNumber().trim().toLowerCase());
        builder.append(location.getOffCampusName() == null ? "" : location.getOffCampusName().trim().toLowerCase());
        builder.append(location.getOffCampusAddress() == null ? "" : location.getOffCampusAddress().trim().toLowerCase());
        builder.append(location.getOffCampusCityName() == null ? "" : location.getOffCampusCityName().trim().toLowerCase());
        builder.append(location.getOffCampusStateCode() == null ? "" : location.getOffCampusStateCode().trim().toLowerCase());
        builder.append(location.getOffCampusZipCode() == null ? "" : location.getOffCampusZipCode().trim().toLowerCase());
        builder.append(location.getOffCampusCountryCode() == null ? "" : location.getOffCampusCountryCode().trim().toLowerCase());
        return builder.toString();
    }

    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
        // adjust the quantity
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        List<AssetGlobalDetail> sharedDetailsList = assetGlobal.getAssetSharedDetails();

        // each shared detail is a group of new assets to be created.
        // so to equally split the source amount into all new assets (all groups),
        // we need to get the total of ALL location quantities from each shared detail group
        int locationQtyTotal = 0;
        if (!sharedDetailsList.isEmpty()) {
            for (AssetGlobalDetail sharedDetail : sharedDetailsList) {
                sharedDetail.setLocationQuantity(sharedDetail.getAssetGlobalUniqueDetails().size());
                locationQtyTotal += sharedDetail.getLocationQuantity();
            }
        }

        // button actions for Asset Separate document
        if (getAssetGlobalService().isAssetSeparateDocument(assetGlobal) && sharedDetailsList.size() >= 1) {
            String[] customAction = parameters.get(KNSConstants.CUSTOM_ACTION);

            // calculate equal source total amounts and set separate source amount fields
            if (customAction != null && CamsConstants.CALCULATE_EQUAL_SOURCE_AMOUNTS_BUTTON.equals(customAction[0])) {
                KualiDecimalUtils kualiDecimalService = new KualiDecimalUtils(assetGlobal.getTotalCostAmount(), CamsConstants.CURRENCY_USD);
                // add source asset to the current location quantity
                KualiDecimal[] equalSourceAmountsArray = kualiDecimalService.allocate(locationQtyTotal + 1);
                setEqualSeparateSourceAmounts(equalSourceAmountsArray, assetGlobal);
                
                recalculateTotalAmount(assetGlobal);
            }

            // calculate source asset remaining amount
            if (customAction != null && (CamsConstants.CALCULATE_SEPARATE_SOURCE_REMAINING_AMOUNT_BUTTON.equals(customAction[0]))) {
                // Don't do anything because we are anyway recalculating always below
            }
            
            // Do recalculate every time even if button (CamsConstants.CALCULATE_SEPARATE_SOURCE_REMAINING_AMOUNT_BUTTON) wasn't pressed. We
            // do that so that it also happens on add / delete lines.
            recalculateTotalAmount(assetGlobal);
        }
    }

    /**
     * Recalculate amounts in the Recalculate Total Amount Tab
     * @param assetGlobal
     */
    protected void recalculateTotalAmount(AssetGlobal assetGlobal) {
        // set Less Additions
        assetGlobal.setSeparateSourceTotalAmount(getAssetGlobalService().getUniqueAssetsTotalAmount(assetGlobal));
        // set Remaining Total Amount
        assetGlobal.setSeparateSourceRemainingAmount(assetGlobal.getTotalCostAmount().subtract(getAssetGlobalService().getUniqueAssetsTotalAmount(assetGlobal)));
    }
    
    /**
     * Separates the current asset amount equally into new unique assets.
     * 
     * @param kualiDecimalArray
     * @param assetGlobal
     */
    public void setEqualSeparateSourceAmounts(KualiDecimal[] equalSourceAmountsArray, AssetGlobal assetGlobal) {
        int i = 0;
        for (AssetGlobalDetail assetSharedDetail : assetGlobal.getAssetSharedDetails()) {
            for (AssetGlobalDetail assetGlobalUniqueDetail : assetSharedDetail.getAssetGlobalUniqueDetails()) {
                assetGlobalUniqueDetail.setSeparateSourceAmount(equalSourceAmountsArray[i]);
                i++;
            }
        }
    }

    /**
     * Gets the routingInfo attribute.
     * 
     * @return Returns the routingInfo.
     */
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Sets the routingInfo attribute value.
     * 
     * @param routingInfo The routingInfo to set.
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        routingInfo = new HashSet<RoutingData>();
        Set<OrgReviewRoutingData> organizationRoutingSet = new HashSet<OrgReviewRoutingData>();
        Set<RoutingAccount> accountRoutingSet = new HashSet<RoutingAccount>();
        Set<RoutingAssetNumber> assetNumberRoutingSet = new HashSet<RoutingAssetNumber>();
        Set<RoutingAssetTagNumber> assetTagNumberRoutingSet = new HashSet<RoutingAssetTagNumber>();

        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();

        // Asset information
        if (assetGlobal.getOrganizationOwnerChartOfAccountsCode() != null && ObjectUtils.isNotNull(assetGlobal.getOrganizationOwnerAccount())) {
            organizationRoutingSet.add(new OrgReviewRoutingData(assetGlobal.getOrganizationOwnerChartOfAccountsCode(), assetGlobal.getOrganizationOwnerAccount().getOrganizationCode()));
        }
        if (assetGlobal.getOrganizationOwnerChartOfAccountsCode() != null && assetGlobal.getOrganizationOwnerAccountNumber() != null) {
            accountRoutingSet.add(new RoutingAccount(assetGlobal.getOrganizationOwnerChartOfAccountsCode(), assetGlobal.getOrganizationOwnerAccountNumber()));
        }
        if (assetGlobal.getSeparateSourceCapitalAssetNumber() != null) {
            assetNumberRoutingSet.add(new RoutingAssetNumber(assetGlobal.getSeparateSourceCapitalAssetNumber().toString()));
        }
        if (ObjectUtils.isNotNull(assetGlobal.getSeparateSourceCapitalAsset())) {
            assetTagNumberRoutingSet.add(new RoutingAssetTagNumber(assetGlobal.getSeparateSourceCapitalAsset().getCampusTagNumber()));
        }

        // Storing data
        RoutingData organizationRoutingData = new RoutingData();
        organizationRoutingData.setRoutingType(KualiOrgReviewAttribute.class.getSimpleName());
        organizationRoutingData.setRoutingSet(organizationRoutingSet);
        routingInfo.add(organizationRoutingData);

        RoutingData accountRoutingData = new RoutingData();
        accountRoutingData.setRoutingType(KualiAccountAttribute.class.getSimpleName());
        accountRoutingData.setRoutingSet(accountRoutingSet);
        routingInfo.add(accountRoutingData);

        RoutingData assetNumberRoutingData = new RoutingData();
        assetNumberRoutingData.setRoutingType(RoutingAssetNumber.class.getSimpleName());
        assetNumberRoutingData.setRoutingSet(assetNumberRoutingSet);
        routingInfo.add(assetNumberRoutingData);

        RoutingData assetTagNumberRoutingData = new RoutingData();
        assetTagNumberRoutingData.setRoutingType(RoutingAssetTagNumber.class.getSimpleName());
        assetTagNumberRoutingData.setRoutingSet(assetTagNumberRoutingSet);
        routingInfo.add(assetTagNumberRoutingData);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#handleRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void handleRouteStatusChange(DocumentHeader documentHeader) {
        super.handleRouteStatusChange(documentHeader);
        if (((AssetGlobal) getBusinessObject()).isCapitalAssetBuilderOriginIndicator()) {
            SpringContext.getBean(CapitalAssetBuilderModuleService.class).notifyRouteStatusChange(documentHeader);
        }
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return Asset.class;
    }

    private AssetGlobalService getAssetGlobalService() {
        return SpringContext.getBean(AssetGlobalService.class);
    }
}