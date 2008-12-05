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

import org.kuali.kfs.integration.purap.PurApItem;
import org.kuali.kfs.module.purap.document.validation.AddPurchasingAccountsPayableItemRule;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;

/**
 * Add Purchasing Accounts Payble Item Event. 
 * This is triggered when a user presses the add button for a given document's item line.
 */
public final class AddPurchasingAccountsPayableItemEvent extends PurchasingAccountsPayableItemEventBase {
    /**
     * Constructs an AddItemEvent with the given errorPathPrefix, document, and item.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked on
     * @param item the item being added 
     */
    public AddPurchasingAccountsPayableItemEvent(String errorPathPrefix, Document document, PurApItem item) {
        super("adding item to document " + getDocumentId(document), errorPathPrefix, document, item);
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AddPurchasingAccountsPayableItemRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddPurchasingAccountsPayableItemRule) rule).processAddItemBusinessRules((AccountingDocument) getDocument(), getItem());
    }
}
