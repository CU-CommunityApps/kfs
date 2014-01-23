/*
 * Copyright 2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.ProcurementCardTargetAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.RouteLevelNames;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.util.KRADConstants;

public class ProcurementCardAccountingLineAuthorizer extends CapitalAccountingLinesAuthorizerBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcurementCardAccountingLineAuthorizer.class);

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getKimHappyPropertyNameForField(java.lang.String)
     */
    @Override
    protected String getKimHappyPropertyNameForField(String convertedName) {
        String name = stripDocumentPrefixFromName(convertedName);
        name = name.replaceAll("\\[\\d+\\]", StringUtils.EMPTY);
        name = name.replaceFirst("(.)*transactionEntries\\.", StringUtils.EMPTY);

        return name;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getDeleteLineMethod(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getDeleteLineMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        final String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        String lineIndex = this.getLineContainerIndex(accountingLineProperty);
        String lineContainer = this.getLineContainer(accountingLineProperty) + ".";

        return KRADConstants.DELETE_METHOD + infix + "Line." + lineContainer + "line" + accountingLineIndex + ".anchoraccounting" + infix + "Anchor";
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getAddMethod(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getAddMethod(AccountingLine accountingLine, String accountingLineProperty) {
        Integer lineIndex = ((ProcurementCardTargetAccountingLine) accountingLine).getTransactionContainerIndex();
        String lineIndexString = lineIndex.toString();

        String infix = getActionInfixForNewAccountingLine(accountingLine, accountingLineProperty);

        return KFSConstants.INSERT_METHOD + infix + "Line" + ".transactionEntries[" + lineIndex + "]";
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getBalanceInquiryMethod(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer)
     */
    @Override
    protected String getBalanceInquiryMethod(AccountingLine accountingLine, String accountingLineProperty, Integer accountingLineIndex) {
        String infix = getActionInfixForExtantAccountingLine(accountingLine, accountingLineProperty);
        String lineContainer = this.getLineContainer(accountingLineProperty) + ".";

        return KFSConstants.PERFORMANCE_BALANCE_INQUIRY_FOR_METHOD + infix + "Line." + lineContainer + "line" + accountingLineIndex + ".anchoraccounting" + infix + "existingLineLineAnchor" + accountingLineIndex;
    }

    protected String getLineContainer(String accountingLineProperty) {
        String lineContainer = stripDocumentPrefixFromName(accountingLineProperty);
        return StringUtils.substringBeforeLast(lineContainer, ".");
    }

    protected String getLineContainerIndex(String accountingLineProperty) {
        String lineContainer = this.getLineContainer(accountingLineProperty);
        return StringUtils.substringBetween(lineContainer, "[", "]");
    }

    /**
     * Overrides to handle the special case for PCDO routed to AccountFullEdit node, at which point the FO (and the delegates) should
     * be able to add new accounts or change any existing account to any other account even if s/he is not the FO of the new account.
     * Note: This method is called by hasEditPermissionOnField and hasEditPermissionOnAccountingLine. The best place to override with
     * the above logic would have been these two methods; but both of them are definded as final for some reason, so we put the logic here.
     *
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineEditPermissionByFieldName(AccountingDocument,AccountingLine,String,Person)
     */
    @Override
    protected boolean determineEditPermissionByFieldName(AccountingDocument accountingDocument, AccountingLine accountingLine, String fieldName, Person currentUser) {
        // 1. If this method is called, we know it's a PCDO document here.
        // 2. Check that the document is at AccountFullEdit route node
        if (accountingDocument.getDocumentHeader() != null &&
                accountingDocument.getDocumentHeader().getWorkflowDocument() != null) {
            Set<String> currentNodes = accountingDocument.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames();
            if (currentNodes != null && currentNodes.contains(RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT)) {
                // 3. Check that the current user has the permission to edit the document, which means in this case he can edit the accounting line
                if (getDocumentAuthorizer(accountingDocument).canEdit(accountingDocument, currentUser)) {
                    // if above conditions satisfy, then we can skip further validation on permission checking,
                    // since any user that can edit the accounting lines will be able to add/change it to any other account
                    return true;
                }
            }
        }

        return super.determineEditPermissionByFieldName(accountingDocument, accountingLine, fieldName, currentUser);
    }

}
