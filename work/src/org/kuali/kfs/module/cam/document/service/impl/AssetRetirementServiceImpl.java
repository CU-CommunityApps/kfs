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
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetGlpeSourceDetail;
import org.kuali.kfs.module.cam.businessobject.AssetObjectCode;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.gl.CamsGeneralLedgerPendingEntrySourceBase;
import org.kuali.kfs.module.cam.document.service.AssetObjectCodeService;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.ObjectValueUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.service.UniversityDateService;

public class AssetRetirementServiceImpl implements AssetRetirementService {

    private enum AmountCategory {
        CAPITALIZATION {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setCapitalization(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_PLANT_FUND_FOR_FMS);
                postable.setAmount(assetPayment.getAccountChargeAmount());
                postable.setFinancialObjectCode(assetObjectCode.getCapitalizationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getCapitalizationFinancialObject());
            };

            boolean isObjectCodeExists(AssetObjectCode assetObjectCode) {
                assetObjectCode.refreshReferenceObject(CamsPropertyConstants.AssetObjectCode.CAPITALIZATION_FINANCIAL_OBJECT);
                if (ObjectUtils.isNull(assetObjectCode.getCapitalizationFinancialObject())) {
                    return false;
                }
                return true;
            };
        },
        ACCUMMULATE_DEPRECIATION {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setAccumulatedDepreciation(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_ACCUMULATED_DEPRECIATION);
                postable.setAmount(assetPayment.getAccumulatedPrimaryDepreciationAmount());
                postable.setFinancialObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObjectCode());
                postable.setObjectCode(assetObjectCode.getAccumulatedDepreciationFinancialObject());
            };

            boolean isObjectCodeExists(AssetObjectCode assetObjectCode) {
                assetObjectCode.refreshReferenceObject(CamsPropertyConstants.AssetObjectCode.ACCUMULATED_DEPRECIATION_FINANCIAL_OBJECT);
                if (ObjectUtils.isNull(assetObjectCode.getAccumulatedDepreciationFinancialObject())) {
                    return false;
                }
                return true;
            };
        },
        OFFSET_AMOUNT {
            void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode) {
                postable.setCapitalizationOffset(true);
                postable.setFinancialDocumentLineDescription(CamsConstants.AssetRetirementGlobal.LINE_DESCRIPTION_GAIN_LOSS_DISPOSITION);
                postable.setAmount(assetPayment.getAccountChargeAmount().subtract(assetPayment.getAccumulatedPrimaryDepreciationAmount()));
                postable.setFinancialObjectCode(SpringContext.getBean(ParameterService.class).getParameterValue(AssetRetirementGlobal.class, CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE).trim());
                postable.setObjectCode(getOffsetFinancialObject(assetPayment.getAsset()));
            };

            boolean isObjectCodeExists(AssetObjectCode assetObjectCode) {
                return true;
            };
        };

        abstract void setParams(AssetGlpeSourceDetail postable, AssetPayment assetPayment, AssetObjectCode assetObjectCode);

        abstract boolean isObjectCodeExists(AssetObjectCode assetObjectCode);
    }

    private UniversityDateService universityDateService;
    private AssetObjectCodeService assetObjectCodeService;
    private BusinessObjectService businessObjectService;
    private AssetPaymentService assetPaymentService;
    private ParameterService parameterService;

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public AssetObjectCodeService getAssetObjectCodeService() {
        return assetObjectCodeService;
    }

    public void setAssetObjectCodeService(AssetObjectCodeService assetObjectCodeService) {
        this.assetObjectCodeService = assetObjectCodeService;
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public AssetPaymentService getAssetPaymentService() {
        return assetPaymentService;
    }

    public void setAssetPaymentService(AssetPaymentService assetPaymentService) {
        this.assetPaymentService = assetPaymentService;
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredBySoldOrAuction(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    public boolean isAssetRetiredBySoldOrAuction(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.AUCTION.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.SOLD.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredByExternalTransferOrGift(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    public boolean isAssetRetiredByExternalTransferOrGift(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.GIFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredByMerged(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    public boolean isAssetRetiredByMerged(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.MERGED.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAssetRetiredByTheft(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    public boolean isAssetRetiredByTheft(AssetRetirementGlobal assetRetirementGlobal) {
        return CamsConstants.AssetRetirementReasonCode.THEFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode());
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#getAssetRetirementReasonName(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal)
     */
    public String getAssetRetirementReasonName(AssetRetirementGlobal assetRetirementGlobal) {
        return assetRetirementGlobal.getRetirementReason() == null ? new String() : assetRetirementGlobal.getRetirementReason().getRetirementReasonName();
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#generateOffsetPaymentsForEachSource(org.kuali.kfs.module.cam.businessobject.Asset,
     *      java.util.List, java.lang.String)
     */
    public void generateOffsetPaymentsForEachSource(Asset sourceAsset, List<PersistableBusinessObject> persistables, String currentDocumentNumber) {
        List<AssetPayment> offsetPayments = new TypedArrayList(AssetPayment.class);
        Integer maxSequenceNo = assetPaymentService.getMaxSequenceNumber(sourceAsset.getCapitalAssetNumber());

        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                AssetPayment offsetPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(sourcePayment, offsetPayment);
                offsetPayment.setFinancialDocumentTypeCode(AssetRetirementGlobal.ASSET_RETIREMENT_DOCTYPE_CD);
                offsetPayment.setDocumentNumber(currentDocumentNumber);
                offsetPayment.setPaymentSequenceNumber(++maxSequenceNo);
                assetPaymentService.adjustPaymentAmounts(offsetPayment, true, false);
                offsetPayments.add(offsetPayment);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error occured while creating offset payment in retirement", e);
        }
        persistables.addAll(offsetPayments);
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#generateNewPaymentForTarget(org.kuali.kfs.module.cam.businessobject.Asset,
     *      org.kuali.kfs.module.cam.businessobject.Asset, java.util.List, java.lang.Integer, java.lang.String)
     */
    public Integer generateNewPaymentForTarget(Asset targetAsset, Asset sourceAsset, List<PersistableBusinessObject> persistables, Integer maxSequenceNo, String currentDocumentNumber) {
        List<AssetPayment> newPayments = new TypedArrayList(AssetPayment.class);
        try {
            for (AssetPayment sourcePayment : sourceAsset.getAssetPayments()) {
                AssetPayment newPayment = new AssetPayment();
                ObjectValueUtils.copySimpleProperties(sourcePayment, newPayment);
                newPayment.setCapitalAssetNumber(targetAsset.getCapitalAssetNumber());
                newPayment.setFinancialDocumentTypeCode(AssetRetirementGlobal.ASSET_RETIREMENT_DOCTYPE_CD);
                newPayment.setPaymentSequenceNumber(++maxSequenceNo);
                newPayment.setDocumentNumber(currentDocumentNumber);
                assetPaymentService.adjustPaymentAmounts(newPayment, false, true);
                newPayments.add(newPayment);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error occured while creating new payment in retirement", e);
        }
        persistables.addAll(newPayments);
        return maxSequenceNo;
    }


    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isRetirementReasonCodeInGroup(java.lang.String, java.lang.String)
     */
    public boolean isRetirementReasonCodeInGroup(String reasonCodeGroup, String reasonCode) {
        if (StringUtils.isBlank(reasonCodeGroup) || StringUtils.isBlank(reasonCode)) {
            return false;
        }
        return Arrays.asList(reasonCodeGroup.split(";")).contains(reasonCode);
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#isAllowedRetireMultipleAssets(java.lang.String)
     */
    public boolean isAllowedRetireMultipleAssets(String retirementReasonCode) {
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();
        return currentUser.isMember(CamsConstants.Workgroups.WORKGROUP_MULTIPLE_ASSET_RETIREMENT_WORKGROUP);
    }

    /**
     * 
     * @see org.kuali.kfs.module.cam.document.service.AssetRetirementService#createGLPostables(org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal,
     *      org.kuali.module.cams.gl.CamsGlPosterBase)
     */
    public boolean createGLPostables(AssetRetirementGlobal assetRetirementGlobal, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster) {
        boolean success = true;

        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            Asset asset = assetRetirementGlobalDetail.getAsset();

            for (AssetPayment assetPayment : asset.getAssetPayments()) {
                List<GeneralLedgerPendingEntrySourceDetail> postables = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
                if (success = generateGlPostablesForOnePayment(assetRetirementGlobal.getDocumentNumber(), assetRetirementGlPoster, asset, assetPayment, postables)) {
                    assetRetirementGlPoster.getPostables().addAll(postables);
                }
            }

        }
        return success;
    }

    /**
     * 
     * Generate a collection of Postables for each payment.
     * 
     * @param documentNumber
     * @param assetRetirementGlPoster
     * @param asset
     * @param assetPayment
     * @return
     */
    private boolean generateGlPostablesForOnePayment(String documentNumber, CamsGeneralLedgerPendingEntrySourceBase assetRetirementGlPoster, Asset asset, AssetPayment assetPayment, List<GeneralLedgerPendingEntrySourceDetail> postables) {
        boolean success = true;
        Account plantAccount = getPlantFundAccount(asset, assetPayment);

        if (ObjectUtils.isNotNull(plantAccount)) {
            KualiDecimal accountChargeAmount = assetPayment.getAccountChargeAmount();
            KualiDecimal accumlatedDepreciationAmount = assetPayment.getAccumulatedPrimaryDepreciationAmount();

            if (accountChargeAmount != null && !accountChargeAmount.isZero()) {
                success = createNewPostable(AmountCategory.CAPITALIZATION, asset, assetPayment, documentNumber, plantAccount, postables);
            }

            if (accumlatedDepreciationAmount != null && !accumlatedDepreciationAmount.isZero()) {
                success = createNewPostable(AmountCategory.ACCUMMULATE_DEPRECIATION, asset, assetPayment, documentNumber, plantAccount, postables);
            }

            if (accountChargeAmount != null && accumlatedDepreciationAmount != null && !accountChargeAmount.subtract(accumlatedDepreciationAmount).isZero()) {
                success = createNewPostable(AmountCategory.OFFSET_AMOUNT, asset, assetPayment, documentNumber, plantAccount, postables);
            }
        }
        return success;
    }

    /**
     * 
     * This method creates one postable and sets the values.
     * 
     * @param category
     * @param asset
     * @param assetPayment
     * @param documentNumber
     * @param plantAccount
     * @return
     */
    private boolean createNewPostable(AmountCategory category, Asset asset, AssetPayment assetPayment, String documentNumber, Account plantAccount, List<GeneralLedgerPendingEntrySourceDetail> postables) {
        boolean success = true;
        AssetGlpeSourceDetail postable = new AssetGlpeSourceDetail();

        AssetObjectCode assetObjectCode = assetObjectCodeService.findAssetObjectCode(asset.getOrganizationOwnerChartOfAccountsCode(), assetPayment.getFinancialObject().getFinancialObjectSubTypeCode());
        if (category.isObjectCodeExists(assetObjectCode)) {
            category.setParams(postable, assetPayment, assetObjectCode);

            // Set Postable attributes which are common among Capitalized, Accumulated Depreciation and gain/loss disposition .
            postable.setDocumentNumber(documentNumber);
            postable.setAccount(plantAccount);
            postable.setAccountNumber(plantAccount.getAccountNumber());
            postable.setBalanceTypeCode(CamsConstants.GL_BALANCE_TYPE_CDE_AC);
            postable.setChartOfAccountsCode(asset.getOrganizationOwnerChartOfAccountsCode());

            postable.setPostingYear(universityDateService.getCurrentFiscalYear());
            // Fields copied from payment
            postable.setFinancialSubObjectCode(assetPayment.getFinancialSubObjectCode());
            postable.setProjectCode(assetPayment.getProjectCode());
            postable.setSubAccountNumber(assetPayment.getSubAccountNumber());
            postable.setOrganizationReferenceId(assetPayment.getOrganizationReferenceId());
            postables.add(postable);
        }
        else {
            success = false;
        }
        return success;
    }


    /**
     * Get the offset Object Code.
     * 
     * @param asset
     * @return
     */
    static private ObjectCode getOffsetFinancialObject(Asset asset) {
        Map pkMap = new HashMap();
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String gainDispositionObjectCode = parameterService.getParameterValue(AssetRetirementGlobal.class, CamsConstants.Parameters.DEFAULT_GAIN_LOSS_DISPOSITION_OBJECT_CODE);
        pkMap.put("universityFiscalYear", universityDateService.getCurrentFiscalYear());
        pkMap.put("chartOfAccountsCode", asset.getOrganizationOwnerChartOfAccountsCode());
        pkMap.put("financialObjectCode", gainDispositionObjectCode);
        ObjectCode offsetFinancialObject = (ObjectCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ObjectCode.class, pkMap);

        if (ObjectUtils.isNull(offsetFinancialObject)) {
            throw new ReferentialIntegrityException("Object code is not defined for this universityFiscalYear=" + universityDateService.getCurrentFiscalYear() + ", chartOfAccountsCode=" + asset.getOrganizationOwnerChartOfAccountsCode() + ", financialObjectCode=" + gainDispositionObjectCode);
        }

        return offsetFinancialObject;
    }


    /**
     * 
     * Get the corresponding Plant Fund Account object based on the payment's financialObjectSubTypeCode.
     * 
     * @param asset
     * @param payment
     * @return
     */
    private Account getPlantFundAccount(Asset asset, AssetPayment payment) {
        Account plantFundAccount = null;

        payment.refreshReferenceObject(CamsPropertyConstants.AssetPayment.FINANCIAL_OBJECT);
        asset.refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);

        if (ObjectUtils.isNotNull(payment.getFinancialObject()) && ObjectUtils.isNotNull(asset.getOrganizationOwnerAccount())) {
            String financialObjectSubTypeCode = payment.getFinancialObject().getFinancialObjectSubTypeCode();

            if (StringUtils.isNotBlank(financialObjectSubTypeCode)) {
                if (Arrays.asList(parameterService.getParameterValue(Asset.class, CamsConstants.Parameters.MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).split(";")).contains(financialObjectSubTypeCode)) {
                    plantFundAccount = asset.getOrganizationOwnerAccount().getOrganization().getCampusPlantAccount();
                }
                else if (Arrays.asList(parameterService.getParameterValue(Asset.class, CamsConstants.Parameters.NON_MOVABLE_EQUIPMENT_OBJECT_SUB_TYPES).split(";")).contains(financialObjectSubTypeCode)) {
                    plantFundAccount = asset.getOrganizationOwnerAccount().getOrganization().getOrganizationPlantAccount();
                }
            }
        }

        return plantFundAccount;
    }
}
