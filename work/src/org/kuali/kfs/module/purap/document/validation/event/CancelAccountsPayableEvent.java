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

import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.validation.CancelAccountsPayableRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.event.KualiDocumentEventBase;

/**
 * Cancel event for Accounts Payable Document
 * This could be triggered when a user presses the cancel button.
 */
public final class CancelAccountsPayableEvent extends KualiDocumentEventBase {

    /**
     * Overridden constructor.
     * 
     * @param document the document for this event
     */
    public CancelAccountsPayableEvent(Document document) {
        this(KFSConstants.EMPTY_STRING, document);
    }

    /**
     * Constructs a CancelAccountsPayableEvent with the given errorPathPrefix and document.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked upon
     */
    public CancelAccountsPayableEvent(String errorPathPrefix, Document document) {
        super("calculating on document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return CancelAccountsPayableRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((CancelAccountsPayableRule) rule).processCancelAccountsPayableBusinessRules((AccountsPayableDocument) getDocument());
    }
}
