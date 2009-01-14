/*
 * Copyright 2009 The Kuali Foundation.
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class RoutingFormDocumentPresentationController extends FinancialSystemTransactionalDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        RoutingFormDocument routingFormDocument = (RoutingFormDocument) document;
        if (StringUtils.isNotBlank(routingFormDocument.getRoutingFormBudgetNumber())) {
            editModes.add(CGConstants.AuthorizationConstants.BUDGET_LINKED);

            // TODO: find a good place for the following code
            if (!GlobalVariables.getMessageList().contains(CGKeyConstants.BUDGET_OVERRIDE)) {
                GlobalVariables.getMessageList().add(0, CGKeyConstants.BUDGET_OVERRIDE);
            }
        }

        return editModes;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canEdit(Document document) {
        RoutingFormDocument routingFormDocument = (RoutingFormDocument) document;
        KualiWorkflowDocument workflowDocument = routingFormDocument.getDocumentHeader().getWorkflowDocument();
        String documentStatusCode = routingFormDocument.getDocumentHeader().getFinancialDocumentStatusCode();

        // Check initiator
        Person user = GlobalVariables.getUserSession().getPerson();
        if (workflowDocument.userIsInitiator(user)) {
            return !(workflowDocument.stateIsEnroute() || this.isFinalStatusCode(documentStatusCode));
        }

        // Check personnel
        for (RoutingFormPersonnel personnel : routingFormDocument.getRoutingFormPersonnel()) {
            if (StringUtils.equals(user.getPrincipalId(), personnel.getPrincipalId())) {
                personnel.refreshNonUpdateableReferences();
                String role = personnel.getPersonRole().getPersonRoleCode();

                if (CGConstants.PROJECT_DIRECTOR_CODE.equals(role) || CGConstants.CO_PROJECT_DIRECTOR_CODE.equals(role)) {
                    Integer currentRouteLevel = workflowDocument.getRouteHeader().getDocRouteLevel();
                    return (currentRouteLevel <= CGConstants.projectDirectorRouteLevel) && !this.isFinalStatusCode(documentStatusCode);
                }

                if (CGConstants.CONTACT_PERSON_ADMINISTRATIVE_CODE.equals(role) || CGConstants.CONTACT_PERSON_PROPOSAL_CODE.equals(role)) {
                    return false;
                }
            }
        }

        return super.canEdit(document);
    }

    // determine whether the given document status is in a final state
    private boolean isFinalStatusCode(String documentStatusCode) {
        List<String> finalStatusCodes = Arrays.asList(KFSConstants.DocumentStatusCodes.APPROVED, KFSConstants.DocumentStatusCodes.DISAPPROVED, KFSConstants.DocumentStatusCodes.CANCELLED);

        return finalStatusCodes.contains(documentStatusCode);
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canAdHocRoute(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canAdHocRoute(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canApprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canApprove(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canBlanketApprove(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canCancel(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canCancel(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canDisapprove(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canDisapprove(Document document) {
        return false;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentPresentationControllerBase#canSave(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean canSave(Document document) {
        return true;
    }
}
