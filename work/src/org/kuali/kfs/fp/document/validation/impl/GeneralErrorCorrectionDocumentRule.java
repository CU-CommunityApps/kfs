/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import static org.kuali.kfs.fp.document.validation.impl.GeneralErrorCorrectionDocumentRuleConstants.TRANSACTION_LEDGER_ENTRY_DESCRIPTION_DELIMITER;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_ORIGIN_CODE;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * Business rule(s) applicable to <code>{@link org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument}</code>
 * instances.
 */
public class GeneralErrorCorrectionDocumentRule extends AccountingDocumentRuleBase {

    /**
     * Convenience method for accessing delimiter for the <code>TransactionLedgerEntryDescription</code> of a
     * <code>{@link GeneralLedgerPendingEntry}</code>
     * 
     * @return String delimiter for transaction ledger entry description
     */
    protected String getEntryDescriptionDelimiter() {
        return TRANSACTION_LEDGER_ENTRY_DESCRIPTION_DELIMITER;
    }

    /**
     * Helper method for business rules concerning <code>{@link AccountingLine}</code> instances.
     * 
     * @param document submitted accounting document
     * @param accountingLine accounting line of submitted accounting document
     * @return true if object and object sub type are allowed and if required reference fields are valid
     */
    private boolean processGenericAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        ObjectCode objectCode = accountingLine.getObjectCode();

        retval = isObjectTypeAndObjectSubTypeAllowed(objectCode);

        if (retval) {
            retval = isRequiredReferenceFieldsValid(accountingLine);
        }

        return retval;
    }

    /**
     * The GEC allows one sided documents for correcting - so if one side is empty, the other side must have at least two lines in
     * it. The balancing rules take care of validation of amounts.
     * 
     * @param transactionalDocument submitted accounting document
     * @return true if number of account line required is met
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAccountingLinesRequiredNumberForRoutingMet(org.kuali.rice.kns.document.FinancialDocument)
     */
    @Override
    protected boolean isAccountingLinesRequiredNumberForRoutingMet(AccountingDocument transactionalDocument) {
        return isOptionalOneSidedDocumentAccountingLinesRequiredNumberForRoutingMet(transactionalDocument);
    }

    /**
     * Overrides to call super and then GEC specific accounting line rules.
     * 
     * @param document submitted accounting document
     * @param accountingLine accounting line in accounting document
     * @return true if accounting line can be added without any problems
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
     */
    @Override
    public boolean processCustomAddAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;
        retval = super.processCustomAddAccountingLineBusinessRules(document, accountingLine);
        if (retval) {
            retval = processGenericAccountingLineBusinessRules(document, accountingLine);
        }
        return retval;
    }


    /**
     * Overrides to call super and then GEC specific accounting line rules.
     * 
     * @param document submitted accounting document
     * @param accountingLine accounting line in document
     * @return true if accounting line can be reviewed without any problems
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
     */
    @Override
    public boolean processCustomReviewAccountingLineBusinessRules(AccountingDocument document, AccountingLine accountingLine) {
        boolean retval = true;

        retval = super.processCustomReviewAccountingLineBusinessRules(document, accountingLine);
        if (retval) {
            retval = processGenericAccountingLineBusinessRules(document, accountingLine);
        }

        return retval;
    }

    /**
     * Used to determine of object code sub types are valid with the object type code.
     * 
     * @param code object code
     * @return true if object type and object sub type for passed in object code are allowed
     */
    protected boolean isObjectTypeAndObjectSubTypeAllowed(ObjectCode code) {
        return SpringContext.getBean(ParameterService.class).getParameterEvaluator(GeneralErrorCorrectionDocument.class, GeneralErrorCorrectionDocumentRuleConstants.VALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, GeneralErrorCorrectionDocumentRuleConstants.INVALID_OBJECT_SUB_TYPES_BY_OBJECT_TYPE, code.getFinancialObjectTypeCode(), code.getFinancialObjectSubTypeCode()).evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectSubTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
    }

    /**
     * This method checks that values exist in the two reference fields ENCUMBRANCE.
     * 
     * @param accountingLine accounting line
     * @return true if all of the required external encumbrance reference fields are valid, false otherwise.
     */
    private boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;
        Class alclass = null;
        BusinessObjectEntry boe;

        if (accountingLine instanceof SourceAccountingLine) {
            alclass = SourceAccountingLine.class;
        }
        else if (accountingLine instanceof TargetAccountingLine) {
            alclass = TargetAccountingLine.class;
        }

        boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(alclass.getName());
        if (StringUtils.isEmpty(accountingLine.getReferenceOriginCode())) {
            putRequiredPropertyError(boe, REFERENCE_ORIGIN_CODE);
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
            putRequiredPropertyError(boe, REFERENCE_NUMBER);
            valid = false;
        }
        return valid;
    }
}
