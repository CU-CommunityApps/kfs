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
package org.kuali.module.cams.lookup;

import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.service.AssetService;

/**
 * This class overrids the base getActionUrls method
 */
public class AssetLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    AssetService assetService;
    
    /**
     * Custom action urls for Asset.
     * 
     * @see org.kuali.core.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject bo) {
        StringBuffer actions = new StringBuffer();

        /** TODO per authorization don't show some links * */
        /** TODO per Asset status don't show some links * */

        actions.append(getMaintenanceUrl(bo, KFSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL));
        actions.append("&nbsp;&nbsp;");
        actions.append(getLoanUrl(bo));
        actions.append("&nbsp;&nbsp;");
        actions.append(getMergeUrl(bo));
        actions.append("&nbsp;&nbsp;");
        actions.append(getPaymentUrl(bo));
        actions.append("&nbsp;&nbsp;");
        actions.append(CamsConstants.AssetActions.SEPARATE);
        actions.append("&nbsp;&nbsp;");
        actions.append(getTransferUrl(bo));

        return actions.toString();
    }

    private Object getMergeUrl(BusinessObject bo) {
        // TODO use system parameter
        Asset asset = (Asset) bo;
        return "<a href=\"maintenance.do?methodToCall=newWithExisting&businessObjectClassName=org.kuali.module.cams.bo.AssetRetirementGlobal&" + KFSConstants.OVERRIDE_KEYS + "=retirementReasonCode" + KFSConstants.FIELD_CONVERSIONS_SEPERATOR + "mergedTargetCapitalAssetNumber&docFormKey=88888888&retirementReasonCode=M&mergedTargetCapitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.MERGE + "</a>";
    }

    private String getLoanUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        return "<a href=\"../camsEquipmentLoanOrReturn.do?methodToCall=docHandler&command=initiate&docTypeName=EquipmentLoanOrReturnDocument&capitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.LOAN + "</a>";
    }

    private String getTransferUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        return "<a href=\"../camsAssetTransfer.do?methodToCall=docHandler&command=initiate&docTypeName=AssetTransferDocument&capitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.TRANSFER + "</a>";
    }

    private String getPaymentUrl(BusinessObject bo) {
        Asset asset = (Asset) bo;
        String anchor = CamsConstants.AssetActions.PAYMENT;

        //Only active capital assets will have the payment link.
        if (getAssetService().isCapitalAsset(asset) && !getAssetService().isAssetRetired(asset))
            anchor = "<a href=\"../camsAssetPayment.do?methodToCall=docHandler&command=initiate&docTypeName=AssetPaymentDocument&capitalAssetNumber=" + asset.getCapitalAssetNumber() + "\">" + CamsConstants.AssetActions.PAYMENT + "</a>";

        return anchor;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

}
