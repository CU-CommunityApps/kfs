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
package org.kuali.module.financial.document.validation.impl;

import static org.kuali.kfs.KFSPropertyConstants.REVERSAL_DATE;
import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;
import org.kuali.kfs.validation.GenericValidation;
import org.kuali.module.financial.document.JournalVoucherDocument;

/**
 * A validation of the reversal date on a Journal Voucher document
 */
public class JournalVoucherReversalDateValidation extends GenericValidation {
    private JournalVoucherDocument journalVoucherForValidation;

    /**
     * Checks that if the reveral date for the document is not null, that the reversal date has not already occurred
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        java.sql.Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight();
        if (null != getJournalVoucherForValidation().getReversalDate() && getJournalVoucherForValidation().getReversalDate().before(today)) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + REVERSAL_DATE, KFSKeyConstants.ERROR_DOCUMENT_INCORRECT_REVERSAL_DATE);
            return false;
        }
        return true;
    }

    /**
     * Gets the journalVoucherForValidation attribute. 
     * @return Returns the journalVoucherForValidation.
     */
    public JournalVoucherDocument getJournalVoucherForValidation() {
        return journalVoucherForValidation;
    }

    /**
     * Sets the journalVoucherForValidation attribute value.
     * @param journalVoucherForValidation The journalVoucherForValidation to set.
     */
    public void setJournalVoucherForValidation(JournalVoucherDocument journalVoucherForDocument) {
        this.journalVoucherForValidation = journalVoucherForDocument;
    }
}
