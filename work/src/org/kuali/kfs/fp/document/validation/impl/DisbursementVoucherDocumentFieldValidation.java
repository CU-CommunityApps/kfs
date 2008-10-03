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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;

public class DisbursementVoucherDocumentFieldValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherDocumentFieldValidation.class);

    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        ErrorMap errors = GlobalVariables.getErrorMap();

        // validate document required fields
        SpringContext.getBean(DictionaryValidationService.class).validateDocument(document);

        // validate payee fields
        errors.addToErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(document.getDvPayeeDetail());
        errors.removeFromErrorPath(KFSPropertyConstants.DV_PAYEE_DETAIL);
        if (!errors.isEmpty()) {
            return false;
        }

        /* special handling name & address required if special handling is indicated */
        if (document.isDisbVchrSpecialHandlingCode()) {
            if (StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrSpecialHandlingPersonName()) || StringUtils.isBlank(document.getDvPayeeDetail().getDisbVchrSpecialHandlingLine1Addr())) {
                errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_SPECHAND_TAB_ERRORS, KFSKeyConstants.ERROR_DV_SPECIAL_HANDLING);
            }
        }

        /* if no documentation is selected, must be a note explaining why */
        if (DisbursementVoucherRuleConstants.NO_DOCUMENTATION_LOCATION.equals(document.getDisbursementVoucherDocumentationLocationCode()) && hasNoNotes(document)) {
            errors.putError(KFSPropertyConstants.DISBURSEMENT_VOUCHER_DOCUMENTATION_LOCATION_CODE, KFSKeyConstants.ERROR_DV_NO_DOCUMENTATION_NOTE_MISSING);
        }

        /* if special handling indicated, must be a note explaining why */
        if (document.isDisbVchrSpecialHandlingCode() && hasNoNotes(document)) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PAYMENT_TAB_ERRORS, KFSKeyConstants.ERROR_DV_SPECIAL_HANDLING_NOTE_MISSING);
        }

        /* if exception attached indicated, must be a note explaining why */
        if (document.isExceptionIndicator() && hasNoNotes(document)) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_PAYMENT_TAB_ERRORS, KFSKeyConstants.ERROR_DV_EXCEPTION_ATTACHED_NOTE_MISSING);
        }

        return errors.isEmpty();
    }

    /**
     * Return true if disbursement voucher does not have any notes
     * 
     * @param document submitted disbursement voucher document
     * @return whether the given document has no notes
     */
    private static boolean hasNoNotes(DisbursementVoucherDocument document) {
        ArrayList<Note> notes = SpringContext.getBean(NoteService.class).getByRemoteObjectId(document.getDocumentNumber());
        return (notes == null || notes.size() == 0);
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
