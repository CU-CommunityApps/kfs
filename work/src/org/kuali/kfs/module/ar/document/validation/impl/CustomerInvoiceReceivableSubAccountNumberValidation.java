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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
public class CustomerInvoiceReceivableSubAccountNumberValidation extends GenericValidation {

    private CustomerInvoiceDocument customerInvoiceDocument;
    
    public boolean validate(AttributedDocumentEvent event) {
        if (StringUtils.isNotEmpty(customerInvoiceDocument.getPaymentSubAccountNumber())) {
            customerInvoiceDocument.refreshReferenceObject(ArConstants.CustomerInvoiceDocumentFields.PAYMENT_SUB_ACCOUNT);
            if (ObjectUtils.isNull(customerInvoiceDocument.getPaymentSubAccount())) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + ArConstants.CustomerInvoiceDocumentFields.PAYMENT_SUB_ACCOUNT_NUMBER, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_ACCOUNT_NUMBER);
                return false;
            }
        }

        return true;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

}
