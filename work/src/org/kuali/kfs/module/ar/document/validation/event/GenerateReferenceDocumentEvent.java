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

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.validation.DeleteCashControlDetailRule;
import org.kuali.kfs.module.ar.document.validation.GenerateReferenceDocumentRule;

public class GenerateReferenceDocumentEvent extends KualiDocumentEventBase {

    /**
     * Constructs a GenerateReferenceDocumentEvent.java.
     * @param errorPathPrefix
     * @param document
     */
    public GenerateReferenceDocumentEvent(String errorPathPrefix, Document document) {
        super("Generate reference document for document " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return GenerateReferenceDocumentRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    @SuppressWarnings("unchecked")
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((GenerateReferenceDocumentRule) rule).processGenerateReferenceDocumentBusinessRules((TransactionalDocument) getDocument());
    }
}
