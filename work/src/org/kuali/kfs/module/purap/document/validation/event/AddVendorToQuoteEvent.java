/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.event;

import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.validation.impl.AddVendorToQuoteRule;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;



/**
 * Add a vendor to the quote tab event. 
 * This is triggered when a user presses the add vendor button for a given vendor.
 */
public final class AddVendorToQuoteEvent extends AttributedDocumentEventBase {
    
    private PurchaseOrderVendorQuote vendorQuote;
    
    /**
     * Constructs an AddVendorToQuoteEvent with the given errorPathPrefix and document.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     */
    public AddVendorToQuoteEvent(String errorPathPrefix, Document document, PurchaseOrderVendorQuote vendorQuote) {
        super("adding vendor to document " + getDocumentId(document), errorPathPrefix, document);
        this.vendorQuote = vendorQuote;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AddVendorToQuoteRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddVendorToQuoteRule) rule).processAddVendorBusinessRules((PurchaseOrderDocument)getDocument(), vendorQuote);
    }
}