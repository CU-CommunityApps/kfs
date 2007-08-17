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
package org.kuali.module.kra.document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.authorization.DocumentAuthorizerBase;
import org.kuali.core.service.AuthorizationService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowGroupService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.bo.AdhocPerson;
import org.kuali.module.kra.bo.AdhocWorkgroup;
import org.kuali.module.kra.service.ResearchDocumentPermissionsService;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.clientapp.WorkflowInfo;
import edu.iu.uis.eden.clientapp.vo.ActionRequestVO;
import edu.iu.uis.eden.clientapp.vo.ReportCriteriaVO;
import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;
import edu.iu.uis.eden.exception.WorkflowException;

public class ResearchDocumentAuthorizer extends DocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(ResearchDocumentAuthorizer.class);
    
    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    protected String getAdHocEditMode(ResearchDocument researchDocument, UniversalUser u) {
        
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        ResearchDocumentPermissionsService permissionsService = SpringContext.getBean(ResearchDocumentPermissionsService.class);
        String permissionCode = AuthorizationConstants.EditMode.UNVIEWABLE;
        KualiWorkflowDocument workflowDocument = researchDocument.getDocumentHeader().getWorkflowDocument();
        
        // Check ad-hoc user permissions
        AdhocPerson budgetAdHocPermission = permissionsService.getAdHocPerson(researchDocument.getDocumentNumber(), u.getPersonUniversalIdentifier());
        if (budgetAdHocPermission != null) {
            if (KraConstants.PERMISSION_MOD_CODE.equals(budgetAdHocPermission.getPermissionCode())) {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
            } else {
                permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
            }
        }
        
        // check ad-hoc workgroup permissions
        List<AdhocWorkgroup> adhocWorkgroups = permissionsService.getAllAdHocWorkgroups(researchDocument.getDocumentNumber());
        WorkflowInfo info2 = new WorkflowInfo();
        List<KualiGroup> personGroups = SpringContext.getBean(UniversalUserService.class).getUsersGroups(u);
        
        for (AdhocWorkgroup adhocWorkgroup: adhocWorkgroups) {
            WorkgroupVO workgroup;
            try {
                workgroup = SpringContext.getBean(WorkflowGroupService.class).getWorkgroupByGroupName(adhocWorkgroup.getWorkgroupName());
            } catch (WorkflowException ex) {
                throw new RuntimeException("Caught workflow exception: " + ex);
            }
            
            if (!ObjectUtils.isNull(workgroup)) {
                if (kualiGroupsContainWorkgroup(workgroup.getWorkgroupName(), personGroups)) {
                    if (adhocWorkgroup.getPermissionCode().equals(KraConstants.PERMISSION_MOD_CODE)) {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
                        break;
                    } else {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
                    }
                }
            }
        }
        
        // now check ad-hoc workgroups in route log
        ReportCriteriaVO criteria = new ReportCriteriaVO();
        try {
            criteria.setRouteHeaderId(workflowDocument.getRouteHeaderId());
            WorkflowInfo info = new WorkflowInfo();
            ActionRequestVO[] requests = info.getActionRequests(workflowDocument.getRouteHeaderId());
            for (int i = 0; i < requests.length; i++) {
                ActionRequestVO request = (ActionRequestVO) requests[i];
                if (request.isWorkgroupRequest()) {
                    WorkgroupVO workgroup = request.getWorkgroupVO();
                    if (kualiGroupsContainWorkgroup(workgroup.getWorkgroupName(), personGroups)) {
                        permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
                        break;
                    }
                }
            }
        } catch (WorkflowException ex) {
            throw new RuntimeException("Caught workflow exception: " + ex);
        }
        
        // Check ad-hoc org permissions (mod first, then read)
        if (permissionsService.isUserInOrgHierarchy(researchDocument.buildAdhocOrgReportXml(KraConstants.PERMISSION_MOD_CODE, true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
        }
        
        if (permissionsService.isUserInOrgHierarchy(researchDocument.buildAdhocOrgReportXml(KraConstants.PERMISSION_READ_CODE, true), KualiWorkflowUtils.KRA_ROUTING_FORM_DOC_TYPE, u.getPersonUniversalIdentifier())) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }
        
        // Check global document type permissions
        if (canModify(workflowDocument.getDocumentType(), u)) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.FULL_ENTRY);
        }
        
        if (canView(workflowDocument.getDocumentType(), u)) {
            permissionCode = getPermissionCodeByPrecedence(permissionCode, AuthorizationConstants.EditMode.VIEW_ONLY);
        }
        
        return permissionCode;
    }
    
    /**
     * Set the permission code to the "higher-precedent" value, based on the 2 values passed in
     * 
     * @param String orgXml
     * @param String uuid
     * @return boolean
     */
    protected String getPermissionCodeByPrecedence(String currentCode, String candidateCode) {
        if (currentCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) || candidateCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY)) {
            return AuthorizationConstants.EditMode.FULL_ENTRY;
        }
        if (currentCode.equals(AuthorizationConstants.EditMode.VIEW_ONLY) || candidateCode.equals(AuthorizationConstants.EditMode.VIEW_ONLY)) {
            return AuthorizationConstants.EditMode.VIEW_ONLY;
        }
        return AuthorizationConstants.EditMode.UNVIEWABLE;
    }
    
    /**
     * Finalize the permission code & the map and return
     * 
     * @param ResearchDocument researchDocument
     * @param String permissionCode
     * @return Map
     */
    protected Map finalizeEditMode(ResearchDocument researchDocument, String permissionCode) {
        // If doc is approved, full entry should become view only
        if (permissionCode.equals(AuthorizationConstants.EditMode.FULL_ENTRY) 
                && (researchDocument.getDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.APPROVED)
                        || researchDocument.getDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.DISAPPROVED)
                        || researchDocument.getDocumentHeader().getFinancialDocumentStatusCode().equals(KFSConstants.DocumentStatusCodes.CANCELLED))) {
            permissionCode = AuthorizationConstants.EditMode.VIEW_ONLY;
        }
        
        Map editModeMap = new HashMap();
        editModeMap.put(permissionCode, "TRUE");
        return editModeMap;
    }
    
    private boolean kualiGroupsContainWorkgroup(String workgroupId, List<KualiGroup> groups) {
        for (KualiGroup group: groups) {
            if (group.getGroupName().equals(workgroupId)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check whether user is a global modifier
     * 
     * @param documentTypeName
     * @param user
     * @return true if the given user is allowed to modify documents of the given document type
     */
    public boolean canModify(String documentTypeName, UniversalUser user) {
        return SpringContext.getBean(AuthorizationService.class).isAuthorized(user, KFSConstants.PERMISSION_MODIFY, documentTypeName);
    }
    
    /**
     * Check whether user is a global viewer
     * 
     * @param documentTypeName
     * @param user
     * @return true if the given user is allowed to view documents of the given document type
     */
    public boolean canView(String documentTypeName, UniversalUser user) {
        return SpringContext.getBean(AuthorizationService.class).isAuthorized(user, KFSConstants.PERMISSION_VIEW, documentTypeName);
    }
}