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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentApplicationIsFullBalanceAppliedValidation extends GenericValidation {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationIsFullBalanceAppliedValidation.class);
    private PaymentApplicationDocument document;
    
    public boolean validate(AttributedDocumentEvent event) {
        
        boolean isValid = true;
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        PaymentApplicationDocument paymentApplicationDocument = getDocument();

        //  dont let PayApp docs started from CashControl docs through if not all funds are applied
        if (paymentApplicationDocument.hasCashControlDocument()) {
            KualiDecimal balanceToBeApplied;
            try {
                balanceToBeApplied = paymentApplicationDocument.getBalanceToBeApplied();
            }
            catch (WorkflowException e) {
                throw new RuntimeException("WorkflowException thrown when trying to retrieve CashControlDocument.", e);
            } 
            if (!KualiDecimal.ZERO.equals(balanceToBeApplied)) {
                isValid &= false;
                errorMap.putError(
                    KNSConstants.GLOBAL_ERRORS,
                    ArKeyConstants.PaymentApplicationDocumentErrors.FULL_AMOUNT_NOT_APPLIED);
            }
        }
        
        return isValid;
    }

    public PaymentApplicationDocument getDocument() {
        return document;
    }

    public void setDocument(PaymentApplicationDocument document) {
        this.document = document;
    }

}
