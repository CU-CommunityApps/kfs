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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;

/**
 * Business rule(s) applicable to Benefit Expense Transfer documents.
 */
public class BenefitExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BenefitExpenseTransferDocumentRule.class);

    /**
     * Constructs a BenefitExpenseTransferDocumentRule.java.
     */
    public BenefitExpenseTransferDocumentRule() {
        super();
    }

    /**
     * Overrides the method in class laborExpenseTransferDocumentRules in order validate the object code is a fringe benefit object
     * code and that the source and target lines have the same account number.
     * 
     * @param accountingDocument
     * @param accountingLine
     * @return boolean false when an error is found in any validation
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isValid = super.processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);

        // only fringe benefit labor object codes are allowed on the benefit expense transfer document
        if (!isFringeBenefitObjectCode(accountingLine)) {
            reportError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, LaborKeyConstants.INVALID_FRINGE_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            isValid = false;
        }

        // Only check this rule for source accounting lines
        boolean isTargetLine = accountingLine.isTargetAccountingLine();
        if (!isTargetLine) {
            // ensure the accounts in source accounting lines are same
            if (!hasSameAccount(accountingDocument, accountingLine)) {
                reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_ACCOUNT_NOT_SAME);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Validates the target and source lines have the same object code.
     * 
     * @param document
     * @return boolean false when the source and target lines have different object codes.
     * @see org.kuali.kfs.module.ld.document.validation.impl.LaborExpenseTransferDocumentRules#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) document;

        // benefit transfers cannot be made between two different fringe benefit labor object codes.
        if (isValid) {
            boolean hasSameFringeBenefitObjectCodes = hasSameFringeBenefitObjectCodes(expenseTransferDocument);
            if (!hasSameFringeBenefitObjectCodes) {
                reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.DISTINCT_OBJECT_CODE_ERROR);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Generates the expense pending entries.
     * 
     * @param document LaborLedgerPostingDocument type
     * @param accountingLine AccountingLine type
     * @return boolean
     * @see org.kuali.kfs.module.ld.document.validation.impl.LaborExpenseTransferDocumentRules#processGenerateLaborLedgerPendingEntries(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument,
     *      org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateLaborLedgerPendingEntries(LaborLedgerPostingDocument document, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("started processGenerateLaborLedgerPendingEntries()");

        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;
        List<LaborLedgerPendingEntry> expensePendingEntries = LaborPendingEntryGenerator.generateExpensePendingEntries(document, expenseTransferAccountingLine, sequenceHelper);
        document.getLaborLedgerPendingEntries().addAll(expensePendingEntries);

        return true;
    }

    /**
     * Determines whether the object code of given accounting line is a fringe benefit labor object code
     * 
     * @param accountingLine the given accounting line
     * @return true if the object code of given accounting line is a fringe benefit labor object code; otherwise, false
     */
    private boolean isFringeBenefitObjectCode(AccountingLine accountingLine) {
        LOG.debug("started isFringeBenefitObjectCode");
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        LaborObject laborObject = expenseTransferAccountingLine.getLaborObject();
        if (laborObject == null) {
            return false;
        }

        String fringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();
        return StringUtils.equals(LaborConstants.BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE, fringeOrSalaryCode);
    }

    /**
     * Determines whether the given accouting line has the same account as the source accounting lines
     * 
     * @param accountingDocument the given accouting document
     * @param accountingLine the given accounting line
     * @return true if the given accouting line has the same account as the source accounting lines; otherwise, false
     */
    private boolean hasSameAccount(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        LOG.debug("started hasSameAccount(AccountingDocument, AccountingLine)");

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        Account cachedAccount = accountingLine.getAccount();
        for (AccountingLine sourceAccountingLine : sourceAccountingLines) {
            Account account = sourceAccountingLine.getAccount();

            // account number was not retrieved correctly, so the two statements are used to populate the fields manually
            account.setChartOfAccountsCode(sourceAccountingLine.getChartOfAccountsCode());
            account.setAccountNumber(sourceAccountingLine.getAccountNumber());

            if (!account.equals(cachedAccount)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether target accouting lines have the same fringe benefit object codes as source accounting lines
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accouting lines have the same fringe benefit object codes as source accounting lines; otherwise, false
     */
    protected boolean hasSameFringeBenefitObjectCodes(AccountingDocument accountingDocument) {
        LOG.debug("started hasSameFringeBenefitObjectCodes(accountingDocument)");
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;

        Set<String> objectCodesFromSourceLine = new HashSet<String>();
        for (Object sourceAccountingLine : expenseTransferDocument.getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) sourceAccountingLine;
            objectCodesFromSourceLine.add(line.getFinancialObjectCode());
        }

        Set<String> objectCodesFromTargetLine = new HashSet<String>();
        for (Object targetAccountingLine : expenseTransferDocument.getTargetAccountingLines()) {
            AccountingLine line = (AccountingLine) targetAccountingLine;
            objectCodesFromTargetLine.add(line.getFinancialObjectCode());
        }

        if (objectCodesFromSourceLine.size() != objectCodesFromTargetLine.size()) {
            return false;
        }

        return objectCodesFromSourceLine.containsAll(objectCodesFromTargetLine);
    }
}
