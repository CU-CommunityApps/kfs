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

import java.util.Iterator;

import org.kuali.core.document.Document;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;

public class CustomerInvoiceDocumentPreRules extends PreRulesContinuationBase {

    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.Document)
     */
    @Override
    public boolean doRules(Document document) {

        CustomerInvoiceDocument doc = (CustomerInvoiceDocument)document;
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        updateCustomerInvoiceDetails(service, doc);
        
        return true;
    }

    /**
     * @param service
     * @param document
     */
    @SuppressWarnings("unchecked")
    private void updateCustomerInvoiceDetails(CustomerInvoiceDetailService service, CustomerInvoiceDocument document){
        CustomerInvoiceDetail customerInvoiceDetail;
        for( Iterator i = document.getSourceAccountingLines().iterator(); i.hasNext(); ){
            customerInvoiceDetail = (CustomerInvoiceDetail)i.next();
            service.recalculateCustomerInvoiceDetail(document, customerInvoiceDetail);
            service.updateAccountsForCorrespondingDiscount(customerInvoiceDetail);
        }
    }
}
