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

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.AddCheckRule;
import org.kuali.core.rule.DeleteCheckRule;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.rule.UpdateCheckRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * Business rule(s) applicable to CashReceipt documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class CashReceiptDocumentRule extends TransactionalDocumentRuleBase implements AddCheckRule, DeleteCheckRule, UpdateCheckRule, CashReceiptDocumentRuleConstants {

    /**
     * Cash Receipt documents allow both positive and negative values, so we only need to check for zero amounts.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();

        if (Constants.ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().put(Constants.AMOUNT_PROPERTY_NAME, KeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            return false;
        }

        return true;
    }

    /**
     * Implements Cash Receipt specific rule checks for the cash reconciliation section, to make sure that the cash, check, and coin
     * totals are not negative.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        return !CashReceiptDocumentRuleUtil.areCashTotalsNegative((CashReceiptDocument) document);
    }

    /**
     * This overrides to call super, then to make sure that the cash drawer for the verification unit associated with this CR doc is
     * open. If it's not, the the rule fails.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(Document document) {
        boolean valid = super.processCustomApproveDocumentBusinessRules(document);

        if (valid) {
            CashReceiptDocument crd = (CashReceiptDocument) document;

            String unitName = SpringServiceLocator.getCashReceiptService().getCashReceiptVerificationUnitForCampusCode(crd.getCampusLocationCode());
            CashDrawer cd = SpringServiceLocator.getCashDrawerService().getByWorkgroupName(unitName, false);
            if (cd == null) {
                throw new IllegalStateException("There is no cash drawer associated with unitName '" + unitName + "' from cash receipt " + crd.getFinancialDocumentNumber());
            }
            else if (cd.isClosed()) {
                GlobalVariables.getErrorMap().put(Constants.GLOBAL_ERRORS, KeyConstants.CashReceipt.MSG_CASH_DRAWER_CLOSED_VERIFICATION_NOT_ALLOWED, cd.getWorkgroupName());
                valid = false;
            }
        }

        return valid;
    }

    /**
     * For Cash Receipt documents, the document is balanced if the sum total of checks and cash and coin equals the sum total of the
     * accounting lines. In addition, the sum total of checks and cash and coin must be greater than zero.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        CashReceiptDocument cr = (CashReceiptDocument) transactionalDocument;

        // make sure that cash reconciliation total is greater than zero
        boolean isValid = cr.getSumTotalAmount().compareTo(Constants.ZERO) > 0;
        if (!isValid) {
            GlobalVariables.getErrorMap().put(DOCUMENT_ERROR_PREFIX + PropertyConstants.SUM_TOTAL_AMOUNT, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_NO_CASH_RECONCILIATION_TOTAL);
        }

        if (isValid) {
            // make sure the document is in balance
            isValid = cr.getSourceTotal().compareTo(cr.getSumTotalAmount()) == 0;

            if (!isValid) {
                GlobalVariables.getErrorMap().put(DOCUMENT_ERROR_PREFIX + PropertyConstants.SUM_TOTAL_AMOUNT, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_BALANCE);
            }
        }

        return isValid;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to CashReceipt specific rules. This method leverages
     * the APC for checking restricted object type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectTypeAllowed(accountingLine);

        if (valid) {
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(KUALI_TRANSACTION_PROCESSING_CASH_RECEIPT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            if (rule.failsRule(objectCode.getFinancialObjectTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().put(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectTypeCode() });
            }
        }

        return valid;
    }

    /**
     * Overrides to validate specific object codes for the Cash Receipt document. This method leverages the APC for checking
     * restricted object consolidation values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectConsolidationAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectConsolidationAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectConsolidationAllowed(accountingLine);

        if (valid) {
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(KUALI_TRANSACTION_PROCESSING_CASH_RECEIPT_SECURITY_GROUPING, RESTRICTED_CONSOLIDATED_OBJECT_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            ObjLevel objectLevel = objectCode.getFinancialObjectLevel();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            String consolidatedObjectCode = objectLevel.getConsolidatedObjectCode();

            if (rule.failsRule(consolidatedObjectCode)) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().put(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_CONSOLIDATED_OBJECT_CODE, new String[] { objectCode.getFinancialObjectCode(), objectLevel.getFinancialObjectLevelCode(), consolidatedObjectCode });
            }
        }

        return valid;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to CashReceipt specific rules. This method leverages
     * the APC for checking restricted object sub type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectSubTypeAllowed(accountingLine);

        if (valid) {
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(KUALI_TRANSACTION_PROCESSING_CASH_RECEIPT_SECURITY_GROUPING, RESTRICTED_OBJECT_SUB_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            if (rule.failsRule(objectCode.getFinancialObjectSubTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().put(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
            }
        }

        return valid;
    }

    /**
     * Cash receipt documents do not utilize the target accounting line list. A CR doc is one sided, so this method should always
     * return true.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Cash receipt documents need at least one accounting line. Had to override to supply a Cash Receipt specific method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        if (0 == transactionalDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().put(DOCUMENT_ERROR_PREFIX + PropertyConstants.SOURCE_ACCOUNTING_LINES, KeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Overrides to set the entry's description to the description from the accounting line, if a value exists.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        String accountingLineDescription = accountingLine.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(accountingLineDescription)) {
            explicitEntry.setTransactionLedgerEntryDescription(accountingLineDescription);
        }
    }

    /**
     * Checks to make sure that the check passed in passes all data dictionary validation and that the amount is positive.
     * 
     * @see org.kuali.core.rule.AddCheckRule#processAddCheckBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processAddCheckBusinessRules(TransactionalDocument transactionalDocument, Check check) {
        boolean isValid = validateCheck(check);

        return isValid;
    }

    /**
     * Default implementation does nothing now.
     * 
     * @see org.kuali.core.rule.DeleteCheckRule#processDeleteCheckBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processDeleteCheckBusinessRules(TransactionalDocument transactionalDocument, Check check) {
        boolean processed = true;

        return processed;
    }

    /**
     * Checks to make sure that the check passed in passes all data dictionary validation and that the amount is positive.
     * 
     * @see org.kuali.core.rule.UpdateCheckRule#processUpdateCheckRule(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processUpdateCheckRule(TransactionalDocument transactionalDocument, Check check) {
        boolean isValid = validateCheck(check);

        return isValid;
    }

    /**
     * This method validates checks for a CR document.
     * 
     * @param check
     * @return boolean
     */
    private boolean validateCheck(Check check) {
        // validate the specific check coming in
        SpringServiceLocator.getDictionaryValidationService().validateBusinessObject(check);

        boolean isValid = GlobalVariables.getErrorMap().isEmpty();

        // check to make sure the amount is also valid
        if (check.getAmount().compareTo(Constants.ZERO) <= 0) {
            GlobalVariables.getErrorMap().put(PropertyConstants.CHECK_AMOUNT, KeyConstants.CashReceipt.ERROR_NEGATIVE_CHECK_AMOUNT, PropertyConstants.CHECKS);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Method used by <code>{@link org.kuali.module.financial.service.CashReceiptCoverSheetService}</code> to determine of the
     * <code>{@link CashReceiptDocument}</code> validates business rules for generating a cover page. <br/> <br/> Rule is the
     * <code>{@link Document}</code> must be ENROUTE.
     * 
     * @param document
     * @return boolean
     */
    public boolean isCoverSheetPrintable(CashReceiptDocument document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());
    }

    /**
     * @see IsDebitUtils#isDebitNotConsideringLineSection(TransactionalDocumentRuleBase, TransactionalDocument, AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        //error corrections are not allowed
        IsDebitUtils.disallowErrorCorrectionDocumentCheck(this, transactionalDocument);
        return IsDebitUtils.isDebitNotConsideringLineSection(this, transactionalDocument, accountingLine);
    }

}
