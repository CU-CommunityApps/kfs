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
package org.kuali.module.cams.document.validation.impl;

import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.cams.service.AssetService;

/**
 * This class validates if asset is locked by other document, if so return false
 */
public class AssetPaymentLockValidation extends GenericValidation {

    private AssetService assetService;

    /**
     * Validates asset to ensure it is not locked by any other document
     * 
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AssetPaymentDocument assetPaymentDocument = (AssetPaymentDocument) event.getDocument();
        if (assetService.isAssetLocked(assetPaymentDocument.getDocumentNumber(), assetPaymentDocument.getCapitalAssetNumber())) {
            return false;
        }
        return true;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
}
