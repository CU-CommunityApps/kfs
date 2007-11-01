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
package org.kuali.module.labor.rule.event;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerBenefitClearingPendingEntriesRule;

/**
 * Event used to re/generate general ledger pending entries for a transactional document.
 */
public final class GenerateLaborLedgerBenefitClearingPendingEntriesEvent extends KualiDocumentEventBase {
    private GeneralLedgerPendingEntrySequenceHelper sequenceHelper;

    /**
     * Constructs a GenerateLaborLedgerBenfitClearingPendingEntriesEvent with the given errorPathPrefix, document, accountingLine,
     * and counter
     * 
     * @param errorPathPrefix
     * @param generalLedgerPostingDocument
     * @param accountingLine
     * @param sequenceHelper
     */
    public GenerateLaborLedgerBenefitClearingPendingEntriesEvent(String errorPathPrefix, LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        super("creating generateLaborLedgerBenefitClearingPendingEntries event for document " + getDocumentId(accountingDocument), errorPathPrefix, accountingDocument);
        // note that we want to override the parent b/c we do want this by reference here
        // the parent does a deepCopy and we don't want that b/c we need to set the GLPEs into the document
        super.document = accountingDocument;
        this.sequenceHelper = sequenceHelper;
    }

    /**
     * Constructs a GenerateLaaborLedgerBenefitClearingPendingEntriesEvent with the given document and accountingLine
     * 
     * @param generalLedgerPostingDocument
     * @param accountingLine
     * @param sequenceHelper
     */
    public GenerateLaborLedgerBenefitClearingPendingEntriesEvent(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        this("", accountingDocument, sequenceHelper);
    }

    /**
     * @return TransactionalDocument associated with this event
     */
    public TransactionalDocument getTransactionalDocument() {
        return (TransactionalDocument) getDocument();
    }

    /**
     * @return sequenceHelper associated with this event
     */
    public GeneralLedgerPendingEntrySequenceHelper getSequenceHelper() {
        return sequenceHelper;
    }

    /**
     * This method sets the value of the sequenceHelper.
     * 
     * @param sequenceHelper
     */
    public void setSequenceHelper(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        this.sequenceHelper = sequenceHelper;
    }

    public void validate() {
        super.validate();
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return GenerateLaborLedgerBenefitClearingPendingEntriesRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((GenerateLaborLedgerBenefitClearingPendingEntriesRule) rule).processGenerateLaborLedgerBenefitClearingPendingEntries((LaborLedgerPostingDocument) getDocument(), getSequenceHelper());
    }
}
