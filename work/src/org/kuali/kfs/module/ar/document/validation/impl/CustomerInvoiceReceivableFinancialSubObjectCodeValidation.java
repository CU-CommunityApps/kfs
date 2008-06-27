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
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;
public class CustomerInvoiceReceivableFinancialSubObjectCodeValidation extends GenericValidation {

    private CustomerInvoiceDocument customerInvoiceDocument;
    
    public boolean validate(AttributedDocumentEvent event) {
        if (StringUtils.isNotEmpty(customerInvoiceDocument.getPaymentFinancialSubObjectCode())) {
            customerInvoiceDocument.refreshReferenceObject(DOCUMENT_ERROR_PREFIX + ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_SUB_OBJECT);
            if (ObjectUtils.isNull(customerInvoiceDocument.getPaymentFinancialSubObject())) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + ArConstants.CustomerInvoiceDocumentFields.PAYMENT_FINANCIAL_SUB_OBJECT_CODE, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_OBJECT_CODE);
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
