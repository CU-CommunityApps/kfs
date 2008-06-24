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
package org.kuali.kfs.module.ar.document.validation.event;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.document.validation.event.CheckEventBase;

public abstract class CustomerInvoiceDetailEventBase extends KualiDocumentEventBase implements CustomerInvoiceDetailEvent {

    private static final Logger LOG = Logger.getLogger(CustomerInvoiceDetailEventBase.class);
    
    private final CustomerInvoiceDetail customerInvoiceDetail;
    
    public CustomerInvoiceDetailEventBase(String description, String errorPathPrefix, Document document, CustomerInvoiceDetail customerInvoiceDetail) {
        super(description, errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.customerInvoiceDetail = (CustomerInvoiceDetail) ObjectUtils.deepCopy(customerInvoiceDetail);
        
        logEvent();
    }

    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#validate()
     */
    public void validate() {
        super.validate();
        if (getCustomerInvoiceDetail() == null) {
            throw new IllegalArgumentException("invalid (null) customer invoice detail");
        }
    }

    /**
     * Logs the event type and some information about the associated accountingLine
     */
    private void logEvent() {
        StringBuffer logMessage = new StringBuffer(StringUtils.substringAfterLast(this.getClass().getName(), "."));
        logMessage.append(" with ");

        // vary logging detail as needed
        if (customerInvoiceDetail == null) {
            logMessage.append("null customerInvoiceDetail");
        }
        else {
            logMessage.append(" customer invoice detail# ");
            logMessage.append( customerInvoiceDetail.getSequenceNumber() );
        }

        LOG.debug(logMessage);
    }

}
