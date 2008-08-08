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

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.SaveOnlyDocumentEvent;
import org.kuali.kfs.module.purap.document.validation.SelectSystemPurapRule;
import org.kuali.kfs.sys.KFSConstants;

/**
 * SelectSystem Event for Accounts Payable Document.
 * This could be triggered when a user presses the SelectSystem button to go to the next page.
 */
public final class SelectSystemPurapEvent extends SaveOnlyDocumentEvent {

    /**
     * Overridden constructor.
     * 
     * @param document the document for this event
     */
    public SelectSystemPurapEvent(Document document) {
        this(KFSConstants.EMPTY_STRING, document);
    }

    /**
     * Constructs a SelectSystemPurapEvent with the given errorPathPrefix and document.
     * 
     * @param errorPathPrefix the error path
     * @param document document the event was invoked upon
     */
    public SelectSystemPurapEvent(String errorPathPrefix, Document document) {
        super("selecting system for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return SelectSystemPurapRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((SelectSystemPurapRule) rule).processSelectSystemPurapBusinessRules((TransactionalDocument) getDocument());
    }
}
