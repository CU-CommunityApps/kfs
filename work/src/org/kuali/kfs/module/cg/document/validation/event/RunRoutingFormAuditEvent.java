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
package org.kuali.kfs.module.cg.document.validation.event;

import org.kuali.kfs.module.cg.document.validation.impl.RoutingFormDocumentRule;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.KualiDocumentEventBase;

/**
 * Class capturing a run audit event.
 */
public class RunRoutingFormAuditEvent extends KualiDocumentEventBase implements KualiDocumentEvent {

    /**
     * Constructs a RunAuditEvent with the given errorPathPrefix and document.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public RunRoutingFormAuditEvent(String errorPathPrefix, Document document) {
        super("Running audit on " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a RunAuditEvent with the given document.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public RunRoutingFormAuditEvent(Document document) {
        this("", document);
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return RoutingFormDocumentRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((RoutingFormDocumentRule) rule).processRunAuditBusinessRules(getDocument());
    }
}
