/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;

import static org.kuali.Constants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.Constants.AMOUNT_PROPERTY_NAME;
import static org.kuali.Constants.ZERO;
import static org.kuali.KeyConstants.ERROR_DOCUMENT_BALANCE_CONSIDERING_SOURCE_AND_TARGET_AMOUNTS;
import static org.kuali.KeyConstants.ERROR_ZERO_AMOUNT;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.ACCOUNT_NUMBER_GLOBAL_RESTRICTION_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.GLOBAL_FIELD_RESTRICTIONS_GROUP_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.MCC_OBJECT_CODE_GROUP_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.MCC_OBJECT_SUB_TYPE_GROUP_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.MCC_PARM_PREFIX;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.OBJECT_CONSOLIDATION_GLOBAL_RESTRICTION_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM;
import static org.kuali.module.financial.rules.ProcurementCardDocumentRuleConstants.SUB_FUND_GLOBAL_RESTRICTION_PARM_NM;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTransactionDetail;
import org.kuali.module.financial.document.ProcurementCardDocument;
import org.kuali.module.financial.document.ProcurementCardDocumentAuthorizer.ProcurementCardRouteLevels;

import edu.iu.uis.eden.exception.WorkflowException;


/**
 * Business rule(s) applicable to Procurement Card document.
 * 
 * @author Kuali Financial Transactions Team ()
 */
public class ProcurementCardDocumentRule extends TransactionalDocumentRuleBase {

    /**
     * Inserts proper errorPath, otherwise functions just like super.
     * 
     * @see org.kuali.core.rule.UpdateAccountingLineRule#processUpdateAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processUpdateAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        fixErrorPath(transactionalDocument, accountingLine);

        return super.processUpdateAccountingLineBusinessRules(transactionalDocument, accountingLine, updatedAccountingLine);
    }

    /**
     * Only target lines can be changed, so we need to only validate them
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        boolean allow = true;

        if (accountingLine instanceof ProcurementCardTargetAccountingLine) {
            LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

            // Somewhat of an ugly hack... the goal is to have fixErrorPath run for cases where it's _not_ a new accounting line.
            // If it is a new accounting line, all is well. But if it isn't then this might be a case where approve is called
            // and we need to run validation with proper errorPath for that.
            if (accountingLine.getSequenceNumber() != null) {
                fixErrorPath(transactionalDocument, accountingLine);
            }

            LOG.debug("beginning object code validation ");
            allow = validateObjectCode(transactionalDocument, accountingLine);

            LOG.debug("beginning account number validation ");
            allow = allow & validateAccountNumber(transactionalDocument, accountingLine);

            LOG.debug("end validating accounting line, has errors: " + allow);
        }

        return allow;
    }

    /**
     * Only target lines can be changed, so we need to only validate them
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        return processCustomAddAccountingLineBusinessRules(transactionalDocument, updatedAccountingLine);
    }

    /**
     * Only target lines can be changed, so we need to only validate them
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        return processCustomAddAccountingLineBusinessRules(transactionalDocument, accountingLine);
    }

    /**
     * Checks object codes restrictions, including restrictions in parameters table.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @return boolean
     */
    public boolean validateObjectCode(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        ProcurementCardDocument pcDocument = (ProcurementCardDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = PropertyConstants.FINANCIAL_OBJECT_CODE;
        boolean objectCodeAllowed = true;

        /* object code exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* make sure object code is active */
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.putError(errorKey, KeyConstants.ERROR_INACTIVE, "object code");
            objectCodeAllowed = false;
        }

        /* check object type global restrictions */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, OBJECT_TYPE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectTypeCode(), errorKey, "Object type");

        /* check object sub type global restrictions */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, OBJECT_SUB_TYPE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectSubTypeCode(), errorKey, "Object sub type");

        /* check object level global restrictions */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, OBJECT_LEVEL_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectLevelCode(), errorKey, "Object level");

        /* check object consolidation global restrictions */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, OBJECT_CONSOLIDATION_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getObjectCode().getFinancialObjectLevel().getFinancialConsolidationObjectCode(), errorKey, "Object consolidation code");

        /* get mcc restriction from transaction */
        String mccRestriction = "";
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) accountingLine;
        List pcTransactions = pcDocument.getTransactionEntries();
        for (Iterator iter = pcTransactions.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                mccRestriction = transactionEntry.getProcurementCardVendor().getTransactionMerchantCategoryCode();
            }
        }

        if (StringUtils.isBlank(mccRestriction)) {
            return objectCodeAllowed;
        }

        /* check object code is in permitted list for mcc */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(MCC_OBJECT_CODE_GROUP_NM, MCC_PARM_PREFIX + mccRestriction, accountingLine.getFinancialObjectCode(), errorKey, "Object code");

        /* check object sub type is in permitted list for mcc */
        objectCodeAllowed = objectCodeAllowed && executeApplicationParameterRestriction(MCC_OBJECT_SUB_TYPE_GROUP_NM, MCC_PARM_PREFIX + mccRestriction, accountingLine.getObjectCode().getFinancialObjectSubTypeCode(), errorKey, "Object sub type code");

        return objectCodeAllowed;
    }

    /**
     * Checks account number restrictions, including restrictions in parameters table.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @return boolean
     */
    public boolean validateAccountNumber(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        ProcurementCardDocument pcDocument = (ProcurementCardDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = PropertyConstants.ACCOUNT_NUMBER;
        boolean accountNumberAllowed = true;

        /* account exist and object exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getAccount()) || ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* global account number restrictions */
        accountNumberAllowed = accountNumberAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, ACCOUNT_NUMBER_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccountNumber(), errorKey, "Account number");

        /* global sub fund restrictions */
        accountNumberAllowed = accountNumberAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, SUB_FUND_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccount().getSubFundGroupCode(), errorKey, "Sub fund code");

        /* global function code restrictions */
        accountNumberAllowed = accountNumberAllowed && executeApplicationParameterRestriction(GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, FUNCTION_CODE_GLOBAL_RESTRICTION_PARM_NM, accountingLine.getAccount().getFinancialHigherEdFunctionCd(), errorKey, "Function code");

        return accountNumberAllowed;
    }

    /**
     * Overrides TransactionalDocumentRuleBase.isDocumentBalanceValid and changes the default debit/credit comparision to checking
     * the target total against the total balance. If they don't balance, and error message is produced that is more appropriate for
     * PCDO.
     * 
     * @param transactionalDocument
     * @return boolean True if the document is balanced, false otherwise.
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        ProcurementCardDocument pcDocument = (ProcurementCardDocument) transactionalDocument;

        KualiDecimal targetTotal = pcDocument.getTargetTotal();
        KualiDecimal sourceTotal = pcDocument.getSourceTotal();

        boolean isValid = targetTotal.compareTo(sourceTotal) == 0;

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_BALANCE_CONSIDERING_SOURCE_AND_TARGET_AMOUNTS, new String[] { sourceTotal.toString(), targetTotal.toString() });
        }

        return isValid;
    }

    /**
     * On procurement card, positive source amounts are credits, negative source amounts are debits
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDebit(TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) throws IllegalStateException {
        // disallow error correction
        IsDebitUtils.disallowErrorCorrectionDocumentCheck(this, transactionalDocument);
        return IsDebitUtils.isDebitConsideringSection(this, transactionalDocument, accountingLine);
    }

    /**
     * Override for fiscal officer full approve, in which case any account can be used.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#accountIsAccessible(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean accountIsAccessible(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();
        List activeNodes = null;
        try {
            activeNodes = Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            LOG.error("Error getting active nodes " + e.getMessage());
            throw new RuntimeException("Error getting active nodes " + e.getMessage());
        }

        if (workflowDocument.stateIsEnroute() && activeNodes.contains(ProcurementCardRouteLevels.ACCOUNT_REVIEW_FULL_EDIT)) {
            return true;
        }

        return super.accountIsAccessible(transactionalDocument, accountingLine);
    }

    /**
     * For transactions that are credits back from the bank, accounting lines can be negative. It still checks that an amount is not
     * zero.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();

        // Check for zero, negative amounts (non-correction), positive amounts (correction)
        String correctsDocumentId = document.getDocumentHeader().getFinancialDocumentInErrorNumber();
        if (ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
            return false;
        }

        return true;
    }

    /**
     * Override to avoid seeing ERROR_DOCUMENT_SINGLE_ACCOUNTING_LINE_SECTION_TOTAL_CHANGED error message on PCDO.
     * 
     * @param propertyName
     * @param persistedSourceLineTotal
     * @param currentSourceLineTotal
     */
    @Override
    protected void buildTotalChangeErrorMessage(String propertyName, KualiDecimal persistedSourceLineTotal, KualiDecimal currentSourceLineTotal) {
        return;
    }

    /**
     * Fix the GlobalVariables.getErrorMap errorPath for how PCDO needs them in order to properly display errors on the interface.
     * This is different from kuali accounting lines because instead PCDO has accounting lines insides of transactions. Hence the
     * error path is slighly different.
     * 
     * @param transactionalDocument has to be a ProcurementCardDocument; could do the casting outside of this method, but it'd make
     *        the callers a bit more messy
     * @param accountingLine has to be a ProcurementCardTargetAccountingLine; could do the casting outside of this method, but it'd
     *        make the callers a bit more messy
     */
    private void fixErrorPath(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        // retrieve the transactionLine (and index for errorPath) that the accountingLine is home to
        ProcurementCardTargetAccountingLine targetAccountingLine = (ProcurementCardTargetAccountingLine) accountingLine;
        int transactionLineIndex = targetAccountingLine.getFinancialDocumentTransactionLineNumber() - 1; // index = line - 1
        List transactionEntries = ((ProcurementCardDocument) transactionalDocument).getTransactionEntries();
        ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) transactionEntries.get(transactionLineIndex);

        String errorPath = PropertyConstants.DOCUMENT;

        // Loop over the accounting lines and find the accountingLine (argument to method). Keeping a counter so we can
        // build the errorPath in the end.
        int accountingLineCounter = 0;
        for (Iterator iterTargetAccountingLines = transactionEntry.getTargetAccountingLines().iterator(); iterTargetAccountingLines.hasNext(); accountingLineCounter++) {
            ProcurementCardTargetAccountingLine loopTargetAccountingLine = (ProcurementCardTargetAccountingLine) iterTargetAccountingLines.next();

            if (loopTargetAccountingLine.getSequenceNumber().equals(targetAccountingLine.getSequenceNumber())) {
                // Found the item, capture error path and break. We're done.
                errorPath = errorPath + "." + PropertyConstants.TRANSACTION_ENTRIES + "[" + transactionLineIndex + "]." + PropertyConstants.TARGET_ACCOUNTING_LINES + "[" + accountingLineCounter + "]";
                break;
            }
        }

        // Clearing the error path is not a universal solution but should work for PCDO. In this case it's the only
        // choice because KualiRuleService.applyRules will miss to remove the previous transaction added error path
        // (only this method knows how it is called).
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(errorPath);
    }
}