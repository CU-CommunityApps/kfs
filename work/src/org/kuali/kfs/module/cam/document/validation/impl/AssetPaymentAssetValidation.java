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

import java.util.List;

import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class validates if asset is locked by other document, if so return false
 */
public class AssetPaymentAssetValidation extends GenericValidation {
    private AssetService assetService;
    /**
     * Validates asset to ensure it is not locked by any other document
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails =assetPaymentDocument.getAssetPaymentAssetDetail(); 

        //Validating the asset doesn't already exists in the doc
        boolean valid=true;
        int position_a=-1;
        for(AssetPaymentAssetDetail assetPaymentAssetDetail:assetPaymentAssetDetails) {
            position_a++;
            String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "."+CamsPropertyConstants.AssetPaymentDocument.ASSET_PAYMENT_ASSET_DETAIL + "["+position_a+"]."+ CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER;

            //Validating the asset exists in the asset table.
            if (ObjectUtils.isNull(assetPaymentAssetDetail.getAsset())) {
                GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_CAPITAL_ASSET_NUMBER, assetPaymentAssetDetail.getCapitalAssetNumber().toString());
                valid &= false;
            }

            //Validating the asset is a capital asset
            if (!this.getAssetService().isCapitalAsset(assetPaymentAssetDetail.getAsset())) {
                GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.Payment.ERROR_NON_CAPITAL_ASSET, assetPaymentAssetDetail.getCapitalAssetNumber().toString());
                valid &= false;          
            }

            //Validating the asset hasn't been retired
            if (this.getAssetService().isAssetRetired(assetPaymentAssetDetail.getAsset())) {
                GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.Retirement.ERROR_NON_ACTIVE_ASSET_RETIREMENT, assetPaymentAssetDetail.getCapitalAssetNumber().toString());
                valid &= false;
            }

//            int position_b=-1;
//            //Checking for duplicated assets. Just in case.
//            for(AssetPaymentAssetDetail assetPaymentAssetDetail2:assetPaymentAssetDetails) {
//                position_b++;
//                if (position_a == position_b)
//                    continue;
//                
//                if (assetPaymentAssetDetail.getCapitalAssetNumber().compareTo(assetPaymentAssetDetail2.getCapitalAssetNumber()) == 0) {
//                    GlobalVariables.getErrorMap().putError(errorPath, CamsKeyConstants.Payment.ERROR_ASSET_EXISTS_IN_DOCUMENT, assetPaymentAssetDetail.getCapitalAssetNumber().toString());                
//                    valid &= false;
//                }
//            }
        }
        return valid;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
}