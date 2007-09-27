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
package org.kuali.module.financial.rules;

import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.document.Document;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.bo.Check;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.financial.rule.AddCheckRule;
import org.kuali.module.financial.rule.DeleteCheckRule;
import org.kuali.module.financial.rule.UpdateCheckRule;


/**
 * Business rule(s) applicable to CashReceipt documents.
 */
public class CashReceiptDocumentRule extends CashReceiptFamilyRule implements AddCheckRule, DeleteCheckRule, UpdateCheckRule {
    /**
     * Implements Cash Receipt specific rule checks for the cash reconciliation section, to make sure that the cash, check, and coin
     * totals are not negative.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        if (isValid) {
            isValid &= validateAccountingLineTotal((CashReceiptFamilyBase) document);
            isValid &= !CashReceiptDocumentRuleUtil.areCashTotalsInvalid((CashReceiptDocument) document);
            isValid &= validateAccountAndObjectCodeAllLines((CashReceiptFamilyBase) document);
        }

        return isValid;
    }
    
    /**
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean isValid = true;
        isValid &= validateAccountAndObjectCode(accountingLine, accountingLine.isSourceAccountingLine(), true, 0);
        return isValid;
    }


    /**
     * Checks to make sure that the check passed in passes all data dictionary validation and that the amount is positive.
     * 
     * @see org.kuali.core.rule.AddCheckRule#processAddCheckBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processAddCheckBusinessRules(AccountingDocument FinancialDocument, Check check) {
        boolean isValid = validateCheck(check);

        return isValid;
    }

    /**
     * Default implementation does nothing now.
     * 
     * @see org.kuali.core.rule.DeleteCheckRule#processDeleteCheckBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processDeleteCheckBusinessRules(AccountingDocument FinancialDocument, Check check) {
        boolean processed = true;

        return processed;
    }

    /**
     * Checks to make sure that the check passed in passes all data dictionary validation and that the amount is positive.
     * 
     * @see org.kuali.core.rule.UpdateCheckRule#processUpdateCheckRule(org.kuali.core.document.FinancialDocument,
     *      org.kuali.module.financial.bo.Check)
     */
    public boolean processUpdateCheckRule(AccountingDocument FinancialDocument, Check check) {
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
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(check);

        boolean isValid = GlobalVariables.getErrorMap().isEmpty();

        // check to make sure the amount is also valid
        if (check.getAmount().isZero()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHECK_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_ZERO_CHECK_AMOUNT, KFSPropertyConstants.CHECKS);
            isValid = false;
        }
        else if (check.getAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHECK_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_NEGATIVE_CHECK_AMOUNT, KFSPropertyConstants.CHECKS);
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
    public boolean isCoverSheetPrintable(CashReceiptFamilyBase document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());
    }

    /**
     * 
     * This method validates all the accounting lines for the right
     * account/object code pairings, if the account is a sales tax account
     * @param document
     * @return
     */
    private boolean validateAccountAndObjectCodeAllLines(AccountingDocument document) {
        boolean isValid = true;
        List<AccountingLine> sourceLines = document.getSourceAccountingLines();
        List<AccountingLine> targetLines = document.getTargetAccountingLines();
        int index = 0;
        for(AccountingLine accountingLine : sourceLines) {
            boolean source = false;
            source = accountingLine.isSourceAccountingLine();
            validateAccountAndObjectCode(accountingLine, source, false, index);
            index++;
        }
        
        return isValid;
    }
    
    /**
     * 
     * This method processes the accounting line to make sure if a sales tax
     * account is used the right object code is used with it
     * @param accountingLine
     * @return
     */
    private boolean validateAccountAndObjectCode(AccountingLine accountingLine, boolean source, boolean newLine, int index) {
        boolean isValid = true;
        Parameter objCdAndAccountRule = getKualiConfigurationService().getParameter(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DOCUMENT, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES);
        //get the object code and account
        String objCd = accountingLine.getFinancialObjectCode();
        String account = accountingLine.getAccountNumber();
        if(!StringUtils.isEmpty(objCd) && !StringUtils.isEmpty(account)) {
            String[] params = getKualiConfigurationService().getParameterValues(objCdAndAccountRule);
            boolean acctsMatched = false;
            for(int i = 0; i < params.length; i++) {
                String paramAcct = params[i].split(":")[0];
                if(account.equalsIgnoreCase(paramAcct)) {
                    acctsMatched = true;
                }
            }
            if(acctsMatched) {
                String compare = account + ":" + objCd;
                if(getKualiConfigurationService().failsRule( objCdAndAccountRule, compare ) ) {
                    isValid = false; 
                } 
            }
            
        }
        if(!isValid) {
            String pathPrefix = "";
            if(source && !newLine) {
                pathPrefix = "document." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";
            } else if(!source && !newLine){
                pathPrefix = "document." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";
            } else if(source && newLine) {
                pathPrefix = KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
            } else if(!source && newLine) {
                pathPrefix = KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME;
            }
            GlobalVariables.getErrorMap().addToErrorPath(pathPrefix);
            
            GlobalVariables.getErrorMap().putError("accountNumber", ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD, account, objCd);
            
            GlobalVariables.getErrorMap().removeFromErrorPath(pathPrefix);
        }
        return isValid;
    }
}
