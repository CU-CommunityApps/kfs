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

import static org.kuali.module.financial.rules.ServiceBillingDocumentRuleConstants.RESTRICTED_OBJECT_TYPE_CODES;
import static org.kuali.module.financial.rules.ServiceBillingDocumentRuleConstants.SERVICE_BILLING_DOCUMENT_SECURITY_GROUPING;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.financial.bo.ServiceBillingControl;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.PropertyConstants;
import org.kuali.KeyConstants;

/**
 * Business rule(s) applicable to Service Billing documents. They differ from {@link InternalBillingDocumentRule} by not routing for
 * fiscal officer approval. Instead, they route straight to final, by a formal pre-agreement between the service provider and the
 * department being billed, based on the service provider's ability to provide documentation for all transactions. These agreements
 * are configured in the Service Billing Control table by workgroup and income account number. This class enforces those agreements.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ServiceBillingDocumentRule extends InternalBillingDocumentRule {

    /**
     * @see TransactionalDocumentRuleBase#accountIsAccessible(TransactionalDocument, AccountingLine)
     */
    @Override
    protected boolean accountIsAccessible(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            // The use from hasAccessibleAccountingLines() is not important for SB, which routes straight to final.
            return accountingLine.isTargetAccountingLine() || serviceBillingIncomeAccountIsAccessible(accountingLine, null);
        }
        return super.accountIsAccessible(transactionalDocument, accountingLine);
    }

    /**
     * @see TransactionalDocumentRuleBase#checkAccountingLineAccountAccessibility(org.kuali.core.document.TransactionalDocument, org.kuali.core.bo.AccountingLine, org.kuali.module.financial.rules.TransactionalDocumentRuleBase.AccountingLineAction)
     */
    @Override
    protected boolean checkAccountingLineAccountAccessibility(TransactionalDocument transactionalDocument, AccountingLine accountingLine, AccountingLineAction action) {
        // Duplicate code from accountIsAccessible() to avoid unnecessary calls to SB control and Workgroup services.
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            return accountingLine.isTargetAccountingLine() || serviceBillingIncomeAccountIsAccessible(accountingLine, action);
        }
        if (!super.accountIsAccessible(transactionalDocument, accountingLine)) {
            GlobalVariables.getErrorMap().put(PropertyConstants.ACCOUNT_NUMBER, action.accessibilityErrorKey, accountingLine.getAccountNumber(), GlobalVariables.getUserSession().getKualiUser().getPersonUserIdentifier()); 
            return false;
        }
        return true;
    }

    /**
     * Checks the account and user against the SB control table.
     * 
     * @param accountingLine from the income section
     * @param action kind of error messages to generate, if not null
     * @return whether the current user is authorized to use the given account in the SB income section
     */
    private boolean serviceBillingIncomeAccountIsAccessible(AccountingLine accountingLine, AccountingLineAction action) {
        assert accountingLine.isSourceAccountingLine();
        String chartOfAccountsCode = accountingLine.getChartOfAccountsCode();
        String accountNumber = accountingLine.getAccountNumber();
        if (StringUtils.isEmpty(chartOfAccountsCode) || StringUtils.isEmpty(accountNumber)) {
            // Ignore empty key because hasAccessibleAccountingLines() may not validate beforehand.
            return false;
        }
        ServiceBillingControl control = SpringServiceLocator.getServiceBillingControlService().getByPrimaryId(chartOfAccountsCode, accountNumber);
        if (ObjectUtils.isNull(control)) {
            if (action != null) {
                GlobalVariables.getErrorMap().put(PropertyConstants.ACCOUNT_NUMBER, noServiceBillingControlErrorKey(action), accountingLine.getAccountNumber());
            }
            return false;
        }
        try {
            KualiUser currentUser = GlobalVariables.getUserSession().getKualiUser();
            // todo: isMember(String) instead of going through KualiGroupService?
            KualiGroup group = SpringServiceLocator.getKualiGroupService().getByGroupName(control.getWorkgroupName());
            if (currentUser.isMember(group)) {
                return true;
            }
            else {
                if (action != null) {
                    GlobalVariables.getErrorMap().put(PropertyConstants.ACCOUNT_NUMBER, notControlGroupMemberErrorKey(action), accountingLine.getAccountNumber(), currentUser.getPersonUserIdentifier(), group.getGroupName());
                }
                return false;
            }
        }
        catch (GroupNotFoundException e) {
            LOG.error("invalid workgroup in SB control for " + chartOfAccountsCode + accountNumber, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @param action
     * @return the error key for not having an SB control
     */
    private String noServiceBillingControlErrorKey(AccountingLineAction action) {
        switch (action) {
            case ADD:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NO_SB_CTRL;
            case UPDATE:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NO_SB_CTRL;
            case DELETE:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NO_SB_CTRL;
            default:
                throw new AssertionError(action);
        }
    }

    /**
     * @param action
     * @return the error key for not being a member of the Workgroup of the necessary SB control
     */
    private String notControlGroupMemberErrorKey(AccountingLineAction action) {
        switch (action) {
            case ADD:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD_NOT_IN_SB_CTRL_GRP;
            case UPDATE:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE_NOT_IN_SB_CTRL_GRP;
            case DELETE:
                return KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE_NOT_IN_SB_CTRL_GRP;
            default:
                throw new AssertionError(action);
        }
    }

    /**
     * @see org.kuali.module.financial.rules.InternalBillingDocumentRule#getObjectTypeRule()
     */
    @Override
    protected KualiParameterRule getObjectTypeRule() {
        return KualiParameterRule.and(super.getObjectTypeRule(), getParameterRule(SERVICE_BILLING_DOCUMENT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES));
    }

    /**
     * Sets extra accounting line field in explicit GLPE. IB doesn't have this field.
     * 
     * @see TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument, AccountingLine,
     *      GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        String description = accountingLine.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(description)) {
            explicitEntry.setTransactionLedgerEntryDescription(description);
        }
    }

    /**
     * further restricts to income/expense object type codes
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        if (!isIncome(accountingLine) && !isExpense(accountingLine)) {
            throw new IllegalStateException(IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage);
        }

        return super.isDebit(transactionalDocument, accountingLine);
    }
}