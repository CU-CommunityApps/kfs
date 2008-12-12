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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.document.validation.impl.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.event.ApproveDocumentEvent;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentApplicationDocumentRule extends GeneralLedgerPostingDocumentRuleBase {
    
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentRule.class);

    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        
        PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument)document;
        
        // Validate the applied payments
        for(InvoicePaidApplied invoicePaidApplied : paymentApplicationDocument.getInvoicePaidApplieds()) {
            if(!PaymentApplicationDocumentRuleUtil.validateInvoicePaidApplied(invoicePaidApplied)) {
                isValid = false;
                LOG.info("One of the invoice paid applieds for the payment application document is not valid.");
            }
        }
        
        // Validate the nonInvoiced payments
        for(NonInvoiced nonInvoiced : paymentApplicationDocument.getNonInvoiceds()) {
            try {
                if(!PaymentApplicationDocumentRuleUtil.validateNonInvoiced(nonInvoiced, paymentApplicationDocument.getBalanceToBeApplied())) {
                    isValid = false;
                    LOG.info("One of the non-invoiced lines on the payment application document is not valid.");
                }
            } catch(WorkflowException workflowException) {
                isValid = false;
                LOG.error("Workflow exception encountered when trying to validate non invoiced line of payment application document.", workflowException);
            }
        }
        
        // Validate non applied holdings
        NonAppliedHolding nonAppliedHolding = paymentApplicationDocument.getNonAppliedHolding();
        try {
            if(!PaymentApplicationDocumentRuleUtil.validateUnapplied(paymentApplicationDocument)) {
                isValid = false;
                LOG.info("The unapplied line on the payment application document is not valid.");
            }
        } catch(WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to validate nonAppliedHolding attribute of payment application document.", workflowException);
        }
        
        // Validate that the cumulative amount applied doesn't exceed the amount owed.
        try {
            if(!paymentApplicationDocument.getCashControlTotalAmount().isGreaterEqual(paymentApplicationDocument.getTotalApplied())) {
                isValid = false;
                LOG.info("The total amount applied exceeds the total amount owed per the cash control document total amount.");
            }
        } catch(WorkflowException workflowException) {
            isValid = false;
            LOG.error("Workflow exception encountered when trying to get validate the total applied amount for payment application document.", workflowException);
        }
        
        return isValid;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        
        //  if the super failed, dont even bother running these rules
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        if (!isValid) return false;
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        PaymentApplicationDocument paymentApplicationDocument = (PaymentApplicationDocument) document;

        try {
            //  dont let PayApp docs started from CashControl docs through if not all funds are applied
            if (!KualiDecimal.ZERO.equals(paymentApplicationDocument.getBalanceToBeApplied())) {
                isValid &= false;
                errorMap.putError(
                    KNSConstants.GLOBAL_ERRORS,
                    ArKeyConstants.PaymentApplicationDocumentErrors.FULL_AMOUNT_NOT_APPLIED);
                LOG.info("The payment application document was not fully applied.");
            }
        } catch(WorkflowException w) {
            LOG.error("Exception encountered while validating PaymentApplicationDocument against business rules during routing", w);
        }
        
        return isValid;
    }
    
    /**
     * @param doc
     * @return true if the organization document number on the document header is not blank.
     */
    protected boolean containsCashControlDocument(PaymentApplicationDocument doc) {
        return (StringUtils.isNotBlank(doc.getDocumentHeader().getOrganizationDocumentNumber()));
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.rule.event.ApproveDocumentEvent)
     */
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        //PaymentApplicationDocument aDocument = (PaymentApplicationDocument)approveEvent.getDocument();
        return isValid;
    }

}
