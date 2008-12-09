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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class RequisitionProcessAdditionalValidation extends GenericValidation {

    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        valid = validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit((PurchasingDocument)event.getDocument());

        return valid;
    }
    
    /**
     * Validate that if the PurchaseOrderTotalLimit is not null then the TotalDollarAmount cannot be greater than the
     * PurchaseOrderTotalLimit.
     * 
     * @param purDocument the requisition document to be validated
     * @return boolean true if the TotalDollarAmount is less than the PurchaseOrderTotalLimit and false otherwise.
     */
    public boolean validateTotalDollarAmountIsLessThanPurchaseOrderTotalLimit(PurchasingDocument purDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        if (ObjectUtils.isNotNull(purDocument.getPurchaseOrderTotalLimit()) && ObjectUtils.isNotNull(((AmountTotaling) purDocument).getTotalDollarAmount())) {
            if (((AmountTotaling) purDocument).getTotalDollarAmount().isGreaterThan(purDocument.getPurchaseOrderTotalLimit())) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_TOTAL_LIMIT, PurapKeyConstants.ERROR_PURCHASE_ORDER_EXCEEDING_TOTAL_LIMIT);
            }
        }
        GlobalVariables.getErrorMap().clearErrorPath();

        return valid;
    }

}
