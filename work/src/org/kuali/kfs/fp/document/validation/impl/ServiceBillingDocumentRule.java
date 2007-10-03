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

import static org.kuali.module.financial.rules.ServiceBillingDocumentRuleConstants.RESTRICTED_OBJECT_TYPE_CODES;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.document.ServiceBillingDocument;

/**
 * Business rule(s) applicable to Service Billing documents. They differ from {@link InternalBillingDocumentRule} by not routing for
 * fiscal officer approval. Instead, they route straight to final, by a formal pre-agreement between the service provider and the
 * department being billed, based on the service provider's ability to provide documentation for all transactions. These agreements
 * are configured in the Service Billing Control table by workgroup and income account number. This class enforces those agreements.
 */
public class ServiceBillingDocumentRule extends InternalBillingDocumentRule {

    /**
     * @see FinancialDocumentRuleBase#accountIsAccessible(FinancialDocument, AccountingLine)
     */
    @Override
    protected boolean accountIsAccessible(AccountingDocument financialDocument, AccountingLine accountingLine) {
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            // The use from hasAccessibleAccountingLines() is not important for SB, which routes straight to final.
            return accountingLine.isTargetAccountingLine() || ServiceBillingDocumentRuleUtil.serviceBillingIncomeAccountIsAccessible(accountingLine, null);
        }
        return super.accountIsAccessible(financialDocument, accountingLine);
    }

    /**
     * @see FinancialDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.financial.rules.FinancialDocumentRuleBase.AccountingLineAction)
     */
    @Override
    protected boolean checkAccountingLineAccountAccessibility(AccountingDocument financialDocument, AccountingLine accountingLine, AccountingLineAction action) {
        // Duplicate code from accountIsAccessible() to avoid unnecessary calls to SB control and Workgroup services.
        KualiWorkflowDocument workflowDocument = financialDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            return accountingLine.isTargetAccountingLine() || ServiceBillingDocumentRuleUtil.serviceBillingIncomeAccountIsAccessible(accountingLine, action);
        }
        if (!super.accountIsAccessible(financialDocument, accountingLine)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, action.accessibilityErrorKey, accountingLine.getAccountNumber(), GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier());
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.module.financial.rules.InternalBillingDocumentRule#getObjectTypeRule()
     */
    @Override
    protected Parameter getObjectTypeRule() {
        return getKualiConfigurationService().mergeParameters(super.getObjectTypeRule(), SpringContext.getBean(ParameterService.class).getParameter(ServiceBillingDocument.class,RESTRICTED_OBJECT_TYPE_CODES));
    }

    /**
     * Sets extra accounting line field in explicit GLPE. IB doesn't have this field.
     * 
     * @see FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(FinancialDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument financialDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        String description = accountingLine.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(description)) {
            explicitEntry.setTransactionLedgerEntryDescription(description);
        }
    }

    /**
     * further restricts to income/expense object type codes
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(AccountingDocument financialDocument, AccountingLine accountingLine) {
        if (!isIncome(accountingLine) && !isExpense(accountingLine)) {
            throw new IllegalStateException(IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage);
        }

        return super.isDebit(financialDocument, accountingLine);
    }
}