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
package org.kuali.kfs.module.cg.document.authorization;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentPermissionsService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;

/**
 * DocumentAuthorizer class for KRA Budget Documents.
 */
public class BudgetDocumentAuthorizer extends ResearchDocumentAuthorizer {
    private static Log LOG = LogFactory.getLog(BudgetDocumentAuthorizer.class);

    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditMode(Document d, UniversalUser u) {

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        ResearchDocumentPermissionsService permissionsService = SpringContext.getBean(ResearchDocumentPermissionsService.class);
        BudgetDocument budgetDocument = (BudgetDocument) d;
        String permissionCode = AuthorizationConstants.EditMode.UNVIEWABLE;
        KualiWorkflowDocument workflowDocument = budgetDocument.getDocumentHeader().getWorkflowDocument();

        // Check initiator
        if (workflowDocument.getInitiatorNetworkId().equalsIgnoreCase(u.getPersonUserIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
            return finalizeEditMode(budgetDocument, permissionCode);
        }

        // Check project director
        if (u.getPersonUniversalIdentifier().equals(budgetDocument.getBudget().getBudgetProjectDirectorUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, parameterService.getParameterValue(BudgetDocument.class, CGConstants.PROJECT_DIRECTOR_BUDGET_PERMISSION));
        }

        // Check default org permissions - project director
        if (!budgetDocument.getBudget().getPersonnel().isEmpty()) {
            if (permissionsService.isUserInOrgHierarchy(budgetDocument.buildProjectDirectorReportXml(true), KualiWorkflowUtils.KRA_BUDGET_DOC_TYPE, u.getPersonUniversalIdentifier())) {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, parameterService.getParameterValue(BudgetDocument.class, CGConstants.PROJECT_DIRECTOR_ORG_BUDGET_PERMISSION));
            }
        }

        // Check default org permissions - cost sharing orgs
        if (permissionsService.isUserInOrgHierarchy(budgetDocument.buildCostShareOrgReportXml(true), KualiWorkflowUtils.KRA_BUDGET_DOC_TYPE, u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, parameterService.getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.COST_SHARE_ORGS_BUDGET_PERMISSION));
        }

        permissionCode = getPermissionCodeByPrecedence(permissionCode, getAdHocEditMode(budgetDocument, u));

        return finalizeEditMode(budgetDocument, permissionCode);
    }

    /**
     * Overrides most of the inherited flags so that the buttons behave exactly like they used to in the obsoleted
     * budgetDocumentControls.tag
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        LOG.debug("calling BudgetDocumentAuthorizer.getDocumentActionFlags");

        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        flags.setCanAcknowledge(false);
        flags.setCanApprove(false);
        flags.setCanBlanketApprove(false);
        flags.setCanCancel(false);
        flags.setCanDisapprove(false);
        flags.setCanFYI(false);
        flags.setCanClose(false);
        flags.setCanSave(true);
        flags.setCanAnnotate(true);

        BudgetDocument budgetDocument = (BudgetDocument) document;

        // use inherited canRoute, canAnnotate, and canReload values

        return flags;
    }
}
