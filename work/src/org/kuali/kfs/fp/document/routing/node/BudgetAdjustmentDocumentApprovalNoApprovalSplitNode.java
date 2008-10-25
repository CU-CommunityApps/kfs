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
package org.kuali.kfs.fp.document.routing.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentAccountingLine;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.sys.businessobject.AccountResponsibility;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.engine.RouteContext;
import org.kuali.rice.kew.engine.RouteHelper;
import org.kuali.rice.kew.engine.node.SplitNode;
import org.kuali.rice.kew.engine.node.SplitResult;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kim.service.PersonService;

/**
 * Checks for conditions on a Budget Adjustment document that allow auto-approval by the initiator. If these conditions are not met,
 * standard financial routing is performed. The conditions for auto-approval are: 1) Single account used on document 2) Initiator is
 * fiscal officer or primary delegate for the account 3) Only current adjustments are being made 4) The fund group for the account
 * is not contract and grants 5) current income/expense decrease amount must equal increase amount
 */
public class BudgetAdjustmentDocumentApprovalNoApprovalSplitNode implements SplitNode {

    public SplitResult process(RouteContext routeContext, RouteHelper routeHelper) throws Exception {
        boolean autoApprovalAllowed = true;

        // retrieve ba document
        String documentID = routeContext.getDocument().getRouteHeaderId().toString();
        BudgetAdjustmentDocument budgetDocument = (BudgetAdjustmentDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentID);

        // new list so that sourceAccountingLines isn't modified by addAll statement. Important for
        // total calculations below.
        List accountingLines = new ArrayList();
        accountingLines.addAll(budgetDocument.getSourceAccountingLines());
        accountingLines.addAll(budgetDocument.getTargetAccountingLines());

        /* only one account can be present on document and only current adjustments allowed */
        String chart = "";
        String accountNumber = "";
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (StringUtils.isNotBlank(accountNumber)) {
                if (!accountNumber.equals(line.getAccountNumber()) && !chart.equals(line.getChartOfAccountsCode())) {
                    autoApprovalAllowed = false;
                    break;
                }
            }

            if (line.getBaseBudgetAdjustmentAmount().isNonZero()) {
                autoApprovalAllowed = false;
                break;
            }
            chart = line.getChartOfAccountsCode();
            accountNumber = line.getAccountNumber();
        }

        // check remaining conditions
        if (autoApprovalAllowed) {
            // initiator should be fiscal officer or primary delegate for account
            Person initiator = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByPrincipalName(budgetDocument.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId());
            List userAccounts = SpringContext.getBean(AccountService.class).getAccountsThatUserIsResponsibleFor(initiator);
            Account userAccount = null;
            for (Iterator iter = userAccounts.iterator(); iter.hasNext();) {
                AccountResponsibility account = (AccountResponsibility) iter.next();
                if (accountNumber.equals(account.getAccount().getAccountNumber()) && chart.equals(account.getAccount().getChartOfAccountsCode())) {
                    userAccount = account.getAccount();
                    break;
                }
            }

            if (userAccount == null) {
                autoApprovalAllowed = false;
            }
            else {
                // fund group should not be CG
                if (userAccount.isForContractsAndGrants()) {
                    autoApprovalAllowed = false;
                }

                // current income/expense decrease amount must equal increase amount
                if (!budgetDocument.getSourceCurrentBudgetIncomeTotal().equals(budgetDocument.getTargetCurrentBudgetIncomeTotal()) || !budgetDocument.getSourceCurrentBudgetExpenseTotal().equals(budgetDocument.getTargetCurrentBudgetExpenseTotal())) {
                    autoApprovalAllowed = false;
                }
            }
        }

        List branchNames = new ArrayList();
        if (autoApprovalAllowed) {
            branchNames.add("NoApprovalBranch");
        }
        else {
            branchNames.add("ApprovalBranch");
        }

        return new SplitResult(branchNames);
    }
}

