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

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.document.ResearchDocument;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.module.cg.document.service.ResearchDocumentPermissionsService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils;

public class RoutingFormDocumentAuthorizer extends ResearchDocumentAuthorizer {

    @Override
    public Map getEditMode(Document d, UniversalUser u) {

        Map editModes = new HashMap();

        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        ResearchDocumentPermissionsService permissionsService = SpringContext.getBean(ResearchDocumentPermissionsService.class);
        RoutingFormDocument routingFormDocument = (RoutingFormDocument) d;
        String permissionCode = AuthorizationConstants.EditMode.UNVIEWABLE;
        KualiWorkflowDocument workflowDocument = routingFormDocument.getDocumentHeader().getWorkflowDocument();

        // Check initiator
        if (workflowDocument.getInitiatorNetworkId().equalsIgnoreCase(u.getPersonUserIdentifier())) {
            if (workflowDocument.stateIsEnroute()) {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
            }
            else {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
            }
            return this.finalizeEditMode(routingFormDocument, permissionCode);
        }

        // Check personnel
        for (RoutingFormPersonnel person : routingFormDocument.getRoutingFormPersonnel()) {
            if (u.getPersonUniversalIdentifier().equals(person.getPersonUniversalIdentifier())) {
                person.refresh();
                String role = person.getPersonRole().getPersonRoleCode();
                if (CGConstants.PROJECT_DIRECTOR_CODE.equals(role) || CGConstants.CO_PROJECT_DIRECTOR_CODE.equals(role)) {
                    if (workflowDocument.getRouteHeader().getDocRouteLevel() > CGConstants.projectDirectorRouteLevel) {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
                    }
                    else {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
                    }

                    return this.finalizeEditMode(routingFormDocument, permissionCode);
                }
                if (CGConstants.CONTACT_PERSON_ADMINISTRATIVE_CODE.equals(role) || CGConstants.CONTACT_PERSON_PROPOSAL_CODE.equals(role)) {
                    permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
                }
            }
        }

        // Org approvers are view-only
        if (permissionsService.isUserInOrgHierarchy(routingFormDocument.buildProjectDirectorReportXml(true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }

        if (permissionsService.isUserInOrgHierarchy(routingFormDocument.buildCostShareOrgReportXml(true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }

        if (permissionsService.isUserInOrgHierarchy(routingFormDocument.buildOtherOrgReportXml(true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }

        permissionCode = getPermissionCodeByPrecedence(permissionCode, getAdHocEditMode(routingFormDocument, u));

        return this.finalizeEditMode(routingFormDocument, permissionCode);
    }


    /**
     * Overriding to check for Budget Overwrite Mode. It was being bypassed in most cases.
     * 
     * @see org.kuali.kfs.module.cg.document.authorization.ResearchDocumentAuthorizer#finalizeEditMode(org.kuali.kfs.module.cg.document.ResearchDocument,
     *      java.lang.String)
     */
    @Override
    protected Map finalizeEditMode(ResearchDocument researchDocument, String permissionCode) {
        // TODO Auto-generated method stub
        Map editModes = super.finalizeEditMode(researchDocument, permissionCode);

        RoutingFormDocument rfd = (RoutingFormDocument) researchDocument;
        if (rfd.getRoutingFormBudgetNumber() != null) {
            editModes.put(CGConstants.AuthorizationConstants.BUDGET_LINKED, "TRUE");
            if (!GlobalVariables.getMessageList().contains(CGKeyConstants.BUDGET_OVERRIDE))
                GlobalVariables.getMessageList().add(0, CGKeyConstants.BUDGET_OVERRIDE);
        }

        return editModes;
    }


    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {

        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        RoutingFormDocument routingFormDocument = (RoutingFormDocument) document;

        flags.setCanAcknowledge(false);

        if (!flags.getCanRoute() && routingFormDocument.isUserProjectDirector(user.getPersonUniversalIdentifier()) && routingFormDocument.getDocumentHeader().getWorkflowDocument().stateIsSaved()) {
            flags.setCanRoute(true);
        }
        // flags.setCanApprove(false);
        flags.setCanBlanketApprove(false);
        flags.setCanCancel(false);
        // flags.setCanDisapprove(false);
        flags.setCanFYI(false);
        flags.setCanClose(false);
        flags.setCanSave(true);

        return flags;
    }
}
