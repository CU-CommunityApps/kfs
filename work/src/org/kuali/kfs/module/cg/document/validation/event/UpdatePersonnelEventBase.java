/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.document.validation.impl.BudgetDocumentRule;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.event.KualiDocumentEventBase;

/**
 * Class capturing an update personnel event.
 */
public class UpdatePersonnelEventBase extends KualiDocumentEventBase implements UpdatePersonnelEvent {

    private final List personnel;

    /**
     * Constructs a UpdateNonpersonnelEventBase.java.
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public UpdatePersonnelEventBase(String errorPathPrefix, Document document, List personnel) {
        super("adding periodLine to document " + getDocumentId(document), errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.personnel = new ArrayList(personnel);
    }

    /**
     * Constructs an AddAccountingLineEvent for the given document and accountingLine
     * 
     * @param document
     * @param accountingLine
     */
    public UpdatePersonnelEventBase(Document document, List personnel) {
        this("", document, personnel);
    }

    /**
     * @see org.kuali.kfs.module.cg.document.validation.event.UpdateNonpersonnelEvent#getNewNonpersonnelItems()
     */
    public List getPersonnel() {
        // TODO Auto-generated method stub
        return this.personnel;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return BudgetDocumentRule.class;
    }

    /**
     * @see org.kuali.rice.kns.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.kns.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((BudgetDocumentRule) rule).processUpdatePersonnelBusinessRules(getDocument());
    }

}
