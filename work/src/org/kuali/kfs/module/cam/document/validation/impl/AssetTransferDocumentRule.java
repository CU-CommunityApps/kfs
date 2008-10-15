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
package org.kuali.kfs.module.cam.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants.Transfer;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.module.cam.document.service.AssetLocationService;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.AssetTransferService;
import org.kuali.kfs.module.cam.document.service.AssetLocationService.LocationField;
import org.kuali.kfs.sys.businessobject.State;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.UniversalUserService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

public class AssetTransferDocumentRule extends GeneralLedgerPostingDocumentRuleBase {

    protected static final Map<LocationField, String> LOCATION_FIELD_MAP = new HashMap<LocationField, String>();
    static {
        LOCATION_FIELD_MAP.put(LocationField.CAMPUS_CODE, CamsPropertyConstants.AssetTransferDocument.CAMPUS_CODE);
        LOCATION_FIELD_MAP.put(LocationField.BUILDING_CODE, CamsPropertyConstants.AssetTransferDocument.BUILDING_CODE);
        LOCATION_FIELD_MAP.put(LocationField.ROOM_NUMBER, CamsPropertyConstants.AssetTransferDocument.BUILDING_ROOM_NUMBER);
        LOCATION_FIELD_MAP.put(LocationField.SUB_ROOM_NUMBER, CamsPropertyConstants.AssetTransferDocument.BUILDING_SUB_ROOM_NUMBER);
        LOCATION_FIELD_MAP.put(LocationField.STREET_ADDRESS, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_ADDRESS);
        LOCATION_FIELD_MAP.put(LocationField.CITY_NAME, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_CITY);
        LOCATION_FIELD_MAP.put(LocationField.STATE_CODE, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_STATE_CODE);
        LOCATION_FIELD_MAP.put(LocationField.ZIP_CODE, CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_ZIP);
        LOCATION_FIELD_MAP.put(LocationField.COUNTRY_CODE, CamsPropertyConstants.AssetGlobalDetail.OFF_CAMPUS_COUNTRY_CODE);
        LOCATION_FIELD_MAP.put(LocationField.LOCATION_TAB_KEY, CamsPropertyConstants.AssetTransferDocument.LOCATION_TAB);
    }

    private UniversityDateService universityDateService;
    private AssetPaymentService assetPaymentService;
    private AssetService assetService;

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        Asset asset = assetTransferDocument.getAsset();
        if (SpringContext.getBean(AssetService.class).isAssetLocked(document.getDocumentNumber(), asset.getCapitalAssetNumber())) {
            return false;
        }

        return checkReferencesExist(assetTransferDocument);

        /*
         * if (checkReferencesExist(assetTransferDocument) && validateAssetObjectCodeDefn(assetTransferDocument, asset)) {
         * SpringContext.getBean(AssetTransferService.class).createGLPostables(assetTransferDocument); if
         * (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(assetTransferDocument)) {
         * throw new ValidationException("General Ledger GLPE generation failed"); } return true; } return false;
         */
    }

    private boolean validateAssetObjectCodeDefn(AssetTransferDocument assetTransferDocument, Asset asset) {
        boolean valid = true;
        List<AssetPayment> assetPayments = asset.getAssetPayments();
        for (AssetPayment assetPayment : assetPayments) {
            if (SpringContext.getBean(AssetPaymentService.class).isPaymentEligibleForGLPosting(assetPayment) && !assetPayment.getAccountChargeAmount().isZero()) {
                // validate if asset object code
                AssetObjectCode originAssetObjectCode = SpringContext.getBean(AssetObjectCodeService.class).findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
                if (ObjectUtils.isNull(originAssetObjectCode)) {
                    putError(CamsConstants.DOCUMENT_NUMBER_PATH, CamsKeyConstants.Transfer.ERROR_ASSET_OBJECT_CODE_NOT_FOUND, asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
                    valid &= false;
                }

                AssetObjectCode targetAssetObjectCode = SpringContext.getBean(AssetObjectCodeService.class).findAssetObjectCode(assetTransferDocument.getOrganizationOwnerChartOfAccountsCode(), assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
                if (ObjectUtils.isNull(targetAssetObjectCode)) {
                    putError(CamsConstants.DOCUMENT_NUMBER_PATH, CamsKeyConstants.Transfer.ERROR_ASSET_OBJECT_CODE_NOT_FOUND, assetTransferDocument.getOrganizationOwnerChartOfAccountsCode(), assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
                    valid &= false;
                }
            }
        }
        return valid;
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        if (!super.processCustomRouteDocumentBusinessRules(document))
            return false;

        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        Asset asset = assetTransferDocument.getAsset();

        if (SpringContext.getBean(AssetService.class).isAssetLocked(document.getDocumentNumber(), asset.getCapitalAssetNumber())) {
            return false;
        }
        boolean valid = true;
        if (SpringContext.getBean(AssetService.class).isAssetRetired(asset)) {
            valid &= false;
            GlobalVariables.getErrorMap().putError(CamsConstants.DOC_HEADER_PATH, CamsKeyConstants.Transfer.ERROR_ASSET_RETIRED_NOTRANSFER, asset.getCapitalAssetNumber().toString(), asset.getRetirementReason().getRetirementReasonName());
        }

        if (valid) {
            valid &= applyRules(document);
        }

        if (validateAssetObjectCodeDefn(assetTransferDocument, asset)) {
            SpringContext.getBean(AssetTransferService.class).createGLPostables(assetTransferDocument);
            if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(assetTransferDocument)) {
                throw new ValidationException("General Ledger GLPE generation failed");
            }
            valid &= true;
        }
        else {
            valid &= false;
        }
        return valid;
    }

    /**
     * This method applies business rules
     * 
     * @param document Transfer Document
     * @return true if all rules are pass
     */
    protected boolean applyRules(Document document) {
        boolean valid = true;
        // check if selected account has plant fund accounts
        AssetTransferDocument assetTransferDocument = (AssetTransferDocument) document;
        valid &= validateOwnerAccount(assetTransferDocument);
        // validate if location info is available, campus or off-campus
        valid &= validateLocation(assetTransferDocument);
        if (assetTransferDocument.isInterdepartmentalSalesIndicator()) {
            if (StringUtils.isBlank(assetTransferDocument.getTransferOfFundsFinancialDocumentNumber())) {
                putError(CamsPropertyConstants.AssetTransferDocument.TRANSFER_FUND_FINANCIAL_DOC_NUM, CamsKeyConstants.Transfer.ERROR_TRFR_FDOC_REQUIRED);
                valid &= false;
            }
        }
        return valid;
    }


    /**
     * This method validates location information provided by the user
     * 
     * @param assetTransferDocument Transfer Document
     * @return true is location information is valid for the asset type
     */
    protected boolean validateLocation(AssetTransferDocument assetTransferDocument) {
        GlobalVariables.getErrorMap().addToErrorPath(CamsConstants.DOCUMENT_PATH);
        Asset asset = assetTransferDocument.getAsset();
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.CAPITAL_ASSET_TYPE);
        boolean isCapitalAsset = SpringContext.getBean(AssetService.class).isCapitalAsset(asset);
        boolean valid = SpringContext.getBean(AssetLocationService.class).validateLocation(LOCATION_FIELD_MAP, assetTransferDocument, isCapitalAsset, asset.getCapitalAssetType());
        GlobalVariables.getErrorMap().removeFromErrorPath(CamsConstants.DOCUMENT_PATH);
        return valid;
    }


    /**
     * This method checks if reference objects exist in the database or not
     * 
     * @param assetTransferDocument Transfer document
     * @return true if all objects exists in db
     */
    protected boolean checkReferencesExist(AssetTransferDocument assetTransferDocument) {
        boolean valid = true;
        if (StringUtils.isNotBlank(assetTransferDocument.getOrganizationOwnerChartOfAccountsCode())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS);
            if (ObjectUtils.isNull(assetTransferDocument.getOrganizationOwnerChartOfAccounts())) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE, CamsKeyConstants.Transfer.ERROR_OWNER_CHART_CODE_INVALID, assetTransferDocument.getOrganizationOwnerChartOfAccountsCode());
                valid &= false;
            }
        }

        if (StringUtils.isNotBlank(assetTransferDocument.getOrganizationOwnerAccountNumber())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
            if (ObjectUtils.isNull(assetTransferDocument.getOrganizationOwnerAccount())) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_OWNER_ACCT_INVALID, assetTransferDocument.getOrganizationOwnerAccountNumber(), assetTransferDocument.getOrganizationOwnerChartOfAccountsCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getTransferOfFundsFinancialDocumentNumber())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.TRANSFER_FUND_FINANCIAL_DOC);
            if (ObjectUtils.isNull(assetTransferDocument.getTransferOfFundsFinancialDocument())) {
                putError(CamsPropertyConstants.AssetTransferDocument.TRANSFER_FUND_FINANCIAL_DOC_NUM, CamsKeyConstants.Transfer.ERROR_TRFR_FDOC_INVALID);
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getCampusCode())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.CAMPUS);
            if (ObjectUtils.isNull(assetTransferDocument.getCampus())) {
                putError(CamsPropertyConstants.AssetTransferDocument.CAMPUS_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_CAMPUS_CODE, assetTransferDocument.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getBuildingCode())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.BUILDING);
            if (ObjectUtils.isNull(assetTransferDocument.getBuilding()) || !assetTransferDocument.getBuilding().isActive()) {
                putError(CamsPropertyConstants.AssetTransferDocument.BUILDING_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_BUILDING_CODE, assetTransferDocument.getBuildingCode(), assetTransferDocument.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getBuildingRoomNumber())) {
            assetTransferDocument.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.BUILDING_ROOM);
            if (ObjectUtils.isNull(assetTransferDocument.getBuildingRoom()) || !assetTransferDocument.getBuildingRoom().isActive()) {
                putError(CamsPropertyConstants.AssetTransferDocument.BUILDING_ROOM_NUMBER, CamsKeyConstants.AssetLocation.ERROR_INVALID_ROOM_NUMBER, assetTransferDocument.getBuildingCode(), assetTransferDocument.getBuildingRoomNumber(), assetTransferDocument.getCampusCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getOffCampusStateCode())) {
            State stateCode = assetTransferDocument.getOffCampusState();
            if (ObjectUtils.isNull(stateCode)) {
                putError(CamsPropertyConstants.AssetTransferDocument.OFF_CAMPUS_STATE_CODE, CamsKeyConstants.AssetLocation.ERROR_INVALID_OFF_CAMPUS_STATE, assetTransferDocument.getOffCampusStateCode());
                valid &= false;
            }
        }
        if (StringUtils.isNotBlank(assetTransferDocument.getAssetRepresentative().getPersonUserIdentifier())) {
            UniversalUserService universalUserService = SpringContext.getBean(UniversalUserService.class);
            try {
                UniversalUser universalUser = universalUserService.getUniversalUserByAuthenticationUserId(assetTransferDocument.getAssetRepresentative().getPersonUserIdentifier());
                assetTransferDocument.setAssetRepresentative(universalUser);
                assetTransferDocument.setRepresentativeUniversalIdentifier(universalUser.getPersonUniversalIdentifier());
            }
            catch (UserNotFoundException e) {
                putError(CamsPropertyConstants.AssetTransferDocument.REP_USER_AUTH_ID, Transfer.ERROR_INVALID_USER_AUTH_ID, assetTransferDocument.getAssetRepresentative().getPersonUserIdentifier());
                valid &= false;
            }
        }
        return valid;
    }


    /**
     * This method validates the new owner organization and account provided
     * 
     * @param assetTransferDocument
     * @return
     */
    protected boolean validateOwnerAccount(AssetTransferDocument assetTransferDocument) {
        boolean valid = true;
        // check if account is active
        Account organizationOwnerAccount = assetTransferDocument.getOrganizationOwnerAccount();
        Asset asset = assetTransferDocument.getAsset();
        if (ObjectUtils.isNotNull(organizationOwnerAccount) && (organizationOwnerAccount.isExpired() || !organizationOwnerAccount.isActive())) {
            // show error if account is not active
            putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_OWNER_ACCT_NOT_ACTIVE, assetTransferDocument.getOrganizationOwnerChartOfAccountsCode(), assetTransferDocument.getOrganizationOwnerAccountNumber());
            valid &= false;
        }
        else if (getAssetService().isCapitalAsset(asset) && !asset.getAssetPayments().isEmpty()) {
            // for a capital asset, check if plant account is defined
            Org ownerOrg = organizationOwnerAccount.getOrganization();
            Account campusPlantAccount = ownerOrg.getCampusPlantAccount();
            Account organizationPlantAccount = ownerOrg.getOrganizationPlantAccount();
            String finObjectSubTypeCode = asset.getFinancialObjectSubTypeCode();
            if (finObjectSubTypeCode == null) {
                AssetPayment firstAssetPayment = asset.getAssetPayments().get(0);
                firstAssetPayment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
                finObjectSubTypeCode = firstAssetPayment.getFinancialObject().getFinancialObjectSubTypeCode();
            }
            boolean assetMovable = getAssetService().isMovableFinancialObjectSubtypeCode(finObjectSubTypeCode);
            if (assetMovable && ObjectUtils.isNull(organizationPlantAccount)) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_ORG_PLANT_FUND_UNKNOWN);
                valid &= false;
            }
            if (!assetMovable && ObjectUtils.isNull(campusPlantAccount)) {
                putError(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT_NUMBER, CamsKeyConstants.Transfer.ERROR_CAMPUS_PLANT_FUND_UNKNOWN);
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * Convenience method to append the path prefix
     */
    public TypedArrayList putError(String propertyName, String errorKey, String... errorParameters) {
        return GlobalVariables.getErrorMap().putError(CamsConstants.DOCUMENT_PATH + "." + propertyName, errorKey, errorParameters);
    }

    public UniversityDateService getUniversityDateService() {
        if (this.universityDateService == null) {
            this.universityDateService = SpringContext.getBean(UniversityDateService.class);
        }
        return universityDateService;
    }


    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }


    public AssetPaymentService getAssetPaymentService() {
        if (this.assetPaymentService == null) {
            this.assetPaymentService = SpringContext.getBean(AssetPaymentService.class);

        }
        return assetPaymentService;
    }


    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }

    public AssetService getAssetService() {
        if (this.assetService == null) {
            this.assetService = SpringContext.getBean(AssetService.class);
        }
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
}
