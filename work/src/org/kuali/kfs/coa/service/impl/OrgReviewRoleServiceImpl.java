/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.kfs.coa.identity.KfsKimDocDelegateMember;
import org.kuali.kfs.coa.identity.KfsKimDocDelegateType;
import org.kuali.kfs.coa.identity.KfsKimDocRoleMember;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleContract;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberContract;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.krad.exception.ValidationException;
import org.springframework.cache.annotation.Cacheable;

public class OrgReviewRoleServiceImpl implements OrgReviewRoleService {

    protected static final String ROLES_TO_CONSIDER_CACHE_NAME = "OrgReviewRoleRolesToConsider";
    // note: this assumes that all use the KFS-SYS namespace
    protected static final Map<String,Role> ROLE_CACHE = new HashMap<String, Role>();
    protected static final Map<String,Map<String,KimAttribute>> ATTRIBUTE_CACHE = new HashMap<String, Map<String,KimAttribute>>();

    protected Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
    {
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT);
        potentialParentDocumentTypeNames = Collections.unmodifiableSet(potentialParentDocumentTypeNames);
    }
    protected DocumentTypeService documentTypeService;

    @Override
    public void populateOrgReviewRoleFromRoleMember(OrgReviewRole orr, String roleMemberId) {
        RoleService roleService = KimApiServiceLocator.getRoleService();
        RoleMemberQueryResults roleMembers = roleService.findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ID, roleMemberId))));
        RoleMember roleMember = null;
        if(roleMembers!=null && !roleMembers.getResults().isEmpty() ){
            roleMember = roleMembers.getResults().get(0);
        }
        orr.setRoleMemberId(roleMember.getId());
        orr.setKimDocumentRoleMember(roleMember);

        Role roleInfo = roleService.getRole(roleMember.getRoleId());
        KimType typeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(roleInfo.getKimTypeId());
        List<KfsKimDocumentAttributeData> attributes = orr.getAttributeSetAsQualifierList(typeInfo, roleMember.getAttributes());
        orr.setAttributes(attributes);
        orr.setRoleRspActions(roleService.getRoleMemberResponsibilityActions(roleMember.getId()));
        orr.setRoleId(roleMember.getRoleId());
        if ( roleMember.getActiveFromDate() != null ) {
            orr.setActiveFromDate(roleMember.getActiveFromDate().toDate());
        } else {
            orr.setActiveFromDate( null );
        }
        if ( roleMember.getActiveToDate() != null ) {
            orr.setActiveToDate(roleMember.getActiveToDate().toDate());
        } else {
            orr.setActiveToDate( null );
        }
        populateObjectExtras(orr);
    }

    @Override
    public void populateOrgReviewRoleFromDelegationMember(OrgReviewRole orr, String delegationMemberId) {
        RoleService roleService = KimApiServiceLocator.getRoleService();
        DelegateMember delegationMember = roleService.getDelegationMemberById(delegationMemberId);
        DelegateType delegation = roleService.getDelegateTypeByDelegationId(delegationMember.getDelegationId());
        Role roleInfo = roleService.getRole(delegation.getRoleId());
        KimType typeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(roleInfo.getKimTypeId());

        orr.setDelegationMemberId(delegationMember.getDelegationMemberId());
        orr.setRoleMemberId(delegationMember.getRoleMemberId());
        orr.setRoleRspActions(roleService.getRoleMemberResponsibilityActions(delegationMember.getRoleMemberId()));
        orr.setAttributes(orr.getAttributeSetAsQualifierList(typeInfo, delegationMember.getAttributes()));
        orr.setRoleId(delegation.getRoleId());
        orr.setDelegationTypeCode(delegation.getDelegationType().getCode());
        orr.setRoleDocumentDelegationMember(delegationMember);
        populateObjectExtras(orr);
    }

    protected void populateObjectExtras( OrgReviewRole orr ) {
        RoleContract role = orr.getRole();
        //Set the role details
        orr.setRoleName(role.getName());
        orr.setNamespaceCode(role.getNamespaceCode());

        orr.setChartOfAccountsCode(orr.getAttributeValue(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
        orr.setOrganizationCode(orr.getAttributeValue(KfsKimAttributes.ORGANIZATION_CODE));
        orr.setOverrideCode(orr.getAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE));
        orr.setFromAmount(orr.getAttributeValue(KfsKimAttributes.FROM_AMOUNT));
        orr.setToAmount(orr.getAttributeValue(KfsKimAttributes.TO_AMOUNT));
        orr.setFinancialSystemDocumentTypeCode(orr.getAttributeValue(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));

//        orr.getChart().setChartOfAccountsCode(orr.getChartOfAccountsCode());
//        orr.getOrganization().setOrganizationCode(orr.getOrganizationCode());

        if(orr.getRoleRspActions()!=null && orr.getRoleRspActions().size()>0){
            orr.setActionTypeCode(orr.getRoleRspActions().get(0).getActionTypeCode());
            orr.setPriorityNumber(orr.getRoleRspActions().get(0).getPriorityNumber()==null?"":String.valueOf(orr.getRoleRspActions().get(0).getPriorityNumber()));
            orr.setActionPolicyCode(orr.getRoleRspActions().get(0).getActionPolicyCode());
            orr.setForceAction(orr.getRoleRspActions().get(0).isForceAction());
        }
//        if(orr.getPerson()!=null){
//            orr.setPrincipalMemberPrincipalId(orr.getPerson().getPrincipalId());
//            orr.setPrincipalMemberPrincipalName(orr.getPerson().getPrincipalName());
//        }
//        // RICE20 : this is using the wrong role
//        if(orr.getRole()!=null){
//            orr.setRoleMemberRoleId(orr.getRole().getId());
//            orr.setRoleMemberRoleNamespaceCode(orr.getRole().getNamespaceCode());
//            orr.setRoleMemberRoleName(orr.getRole().getName());
//        }
//        if(orr.getGroup()!=null){
//            orr.setGroupMemberGroupId(orr.getGroup().getId());
//            orr.setGroupMemberGroupNamespaceCode(orr.getGroup().getNamespaceCode());
//            orr.setGroupMemberGroupName(orr.getGroup().getName());
//        }
    }

    @Override
    public boolean isValidDocumentTypeForOrgReview(String documentTypeName){
        if(StringUtils.isEmpty(documentTypeName)){
            return false;
        }

        return !getRolesToConsiderInternal(documentTypeName).isEmpty();
    }

    @Override
    public void validateDocumentType(String documentTypeName) throws ValidationException {
        try{
            getRolesToConsider(documentTypeName);
        } catch(Exception ex){
            throw new ValidationException(ex.getMessage());
        }
    }

    @Override
    public boolean hasOrganizationHierarchy(final String documentTypeName) {
        if(StringUtils.isBlank(documentTypeName)) {
            return false;
        }
        return getDocumentTypeService().hasRouteNodeForDocumentTypeName(KFSConstants.RouteLevelNames.ORGANIZATION_HIERARCHY, documentTypeName);
    }

    @Override
    public boolean hasAccountingOrganizationHierarchy(final String documentTypeName) {
        if(StringUtils.isBlank(documentTypeName)) {
            return false;
        }
        return getDocumentTypeService().hasRouteNodeForDocumentTypeName(KFSConstants.RouteLevelNames.ACCOUNTING_ORGANIZATION_HIERARCHY, documentTypeName);
    }

    @Override
    public String getClosestOrgReviewRoleParentDocumentTypeName(final String documentTypeName){
        if(StringUtils.isBlank(documentTypeName)) {
            return null;
        }
        return KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(documentTypeName), potentialParentDocumentTypeNames);
    }

    /**
     *  1. Check WorkflowInfo.hasNode(documentTypeName, nodeName) to see if the document type selected has
     *  OrganizationHierarchy and/or AccountingOrganizationHierarchy - if it has either or both,
     *  set the Review Types radio group appropriately and make it read only.
     *  2. Else, if KFS is the document type selected, set the Review Types radio group to both and leave it editable.
     *  3. Else, if FinancialSystemTransactionalDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  set the Review Types radio group to Organization Accounting Only and leave it editable.
     *  4. Else, if FinancialSystemComplexMaintenanceDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  set the Review Types radio group to Organization Only and make read-only.
     *  5. Else, if FinancialSystemSimpleMaintenanceDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  this makes no sense and should generate an error.
     * @param documentTypeName
     * @param hasOrganizationHierarchy
     * @param hasAccountingOrganizationHierarchy
     * @param closestParentDocumentTypeName
     * @return
     */
    @Override
    @Cacheable(value=ROLES_TO_CONSIDER_CACHE_NAME,key="#p0")
    public List<String> getRolesToConsider(String documentTypeName) throws ValidationException {
        List<String> rolesToConsider = getRolesToConsiderInternal(documentTypeName);
        if ( rolesToConsider.isEmpty() ) {
            throw new ValidationException("Invalid document type chosen for Organization Review: " + documentTypeName);
        }
        return rolesToConsider;
    }

    protected List<String> getRolesToConsiderInternal(String documentTypeName) {
        List<String> rolesToConsider = new ArrayList<String>(2);
        if(StringUtils.isBlank(documentTypeName) || KFSConstants.ROOT_DOCUMENT_TYPE.equals(documentTypeName) ){
            rolesToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
        }
        String closestParentDocumentTypeName = getClosestOrgReviewRoleParentDocumentTypeName(documentTypeName);
        if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT)
                || KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestParentDocumentTypeName)) {
            rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
        } else {
            boolean hasOrganizationHierarchy = hasOrganizationHierarchy(documentTypeName);
            boolean hasAccountingOrganizationHierarchy = hasAccountingOrganizationHierarchy(documentTypeName);
            if(hasOrganizationHierarchy || documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT)
                    || KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT.equals(closestParentDocumentTypeName) ) {
                rolesToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            }
            if(hasAccountingOrganizationHierarchy) {
                rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            }
        }
        return rolesToConsider;
    }

    @Override
    public void saveOrgReviewRoleToKim( OrgReviewRole orr ) {
        if(orr.isDelegate() || orr.isCreateDelegation()){
            // Save delegation(s)
            List<KfsKimDocDelegateType> objectsToSave = getDelegations(orr);
            if(objectsToSave!=null){
                for(KfsKimDocDelegateType delegateInfo: objectsToSave){
                    for(KfsKimDocDelegateMember delegateMemberInfo: delegateInfo.getMembers()){
//                        KimApiServiceLocator.getRoleService().saveDelegationMemberForRole(delegateMemberInfo.getDelegationMemberId(),
//                            delegateMemberInfo.getRoleMemberId(), delegateMemberInfo.getMemberId(),
//                            delegateMemberInfo.getType().getCode(), delegateInfo.getDelegationType().getCode(),
//                            delegateInfo.getRoleId(), delegateMemberInfo.getAttributes(),
//                            fromDate, toDate);
                    }
                }
            }
        } else{
            // Save role member(s)
            List<KfsKimDocRoleMember> objectsToSave = getRoleMembers(orr);
            if(objectsToSave!=null){
                for(RoleMemberContract roleMember: objectsToSave){
                    RoleResponsibilityAction.Builder roleRspActionToSave = getRoleRspAction(orr, roleMember);
                    if ( orr.isEdit() ) {
                        RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(roleMember);
                        updatedRoleMember.setRoleRspActions( Collections.singletonList(roleRspActionToSave) );
                        roleMember = KimApiServiceLocator.getRoleService().updateRoleMember( updatedRoleMember.build() );
                    } else {
                        RoleMember.Builder newRoleMember = RoleMember.Builder.create(roleMember);
                        newRoleMember.setRoleRspActions( Collections.singletonList(roleRspActionToSave) );
                        roleMember = KimApiServiceLocator.getRoleService().createRoleMember( newRoleMember.build() );
                    }
                    orr.setRoleMemberId(roleMember.getId());
                    orr.setORMId(roleMember.getId());
                }
            }
        }

    }

    protected List<KfsKimDocDelegateType> getDelegations(OrgReviewRole orr) {
        // List<DelegateMember> delegationMembers = new ArrayList<DelegateMember>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        if (roleNamesToSaveFor != null) {
            List<KfsKimDocDelegateType> roleDelegations = new ArrayList<KfsKimDocDelegateType>(roleNamesToSaveFor.size());
            for (String roleName : roleNamesToSaveFor) {
                Role roleInfo = getRoleInfo(roleName);
                KfsKimDocDelegateType roleDelegation = new KfsKimDocDelegateType(roleInfo);
                roleDelegation.setMembers(getDelegationMembersToSave(orr));
                roleDelegations.add(roleDelegation);
            }
            return roleDelegations;
        }
        return Collections.emptyList();
    }


    protected Role getRoleInfo( String roleName ) {
        if ( roleName == null ) {
            return null;
        }
        Role role = ROLE_CACHE.get(roleName);
        if ( role == null ) {
            role = KimApiServiceLocator.getRoleService().getRoleByNamespaceCodeAndName( KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
            synchronized ( ROLE_CACHE ) {
                ROLE_CACHE.put(roleName, role);
            }
        }
        return role;
    }

    protected List<KfsKimDocDelegateMember> getDelegationMembersToSave(OrgReviewRole orr){
        List<KfsKimDocDelegateMember> objectsToSave = new ArrayList<KfsKimDocDelegateMember>();
        KfsKimDocDelegateMember delegationMember = null;
        if(orr.isEdit() && !orr.isCreateDelegation()){
            delegationMember = new KfsKimDocDelegateMember( KimApiServiceLocator.getRoleService().getDelegationMemberById(orr.getDelegationMemberId()) );
        }

        if(delegationMember==null){
            delegationMember = new KfsKimDocDelegateMember();
            if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
                String roleId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
                delegationMember.setMemberId(roleId);
                delegationMember.setType(MemberType.ROLE);
            } else if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
                Group groupInfo = KimApiServiceLocator.getGroupService().getGroupByNamespaceCodeAndName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                delegationMember.setMemberId(groupInfo.getId());
                delegationMember.setType(MemberType.GROUP);
            } else if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
                Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                delegationMember.setMemberId(principal.getPrincipalId());
                delegationMember.setType(MemberType.PRINCIPAL);
            }
        }
        delegationMember.setDelegationType(DelegationType.fromCode( orr.getDelegationTypeCode() ));
        delegationMember.setAttributes(getAttributes(orr, orr.getKimTypeId()));
        delegationMember.setActiveFromDate( new DateTime( orr.getActiveFromDate() ) );
        delegationMember.setActiveToDate( new DateTime( orr.getActiveToDate() ) );
        delegationMember.setRoleMemberId(orr.getRoleMemberId());
        return Collections.singletonList(delegationMember);
    }

    protected KfsKimDocRoleMember getRoleMemberToSave(Role roleInfo, OrgReviewRole orr){
        KfsKimDocRoleMember roleMember = null;
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            String memberId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
            roleMember = new KfsKimDocRoleMember(roleInfo.getId(), MemberType.ROLE, memberId);
        }
        if(roleMember==null){
            if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
                Group groupInfo = KimApiServiceLocator.getGroupService().getGroupByNamespaceCodeAndName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                roleMember = new KfsKimDocRoleMember(roleInfo.getId(), MemberType.GROUP, groupInfo.getId() );
            }
        }
        if(roleMember==null){
            if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
                Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                roleMember = new KfsKimDocRoleMember(roleInfo.getId(), MemberType.PRINCIPAL, principal.getPrincipalId());
            }
        }
        if ( roleMember != null ) {
            if(orr.isEdit()){
                roleMember.setId(orr.getRoleMemberId());
            }
            roleMember.setAttributes(getAttributes(orr, roleInfo.getKimTypeId()));
            if ( orr.getActiveFromDate() != null ) {
                roleMember.setActiveFromDate( new DateTime( orr.getActiveFromDate().getTime() ) );
            }
            if ( orr.getActiveToDate() != null ) {
                roleMember.setActiveToDate( new DateTime( orr.getActiveToDate().getTime() ) );
            }
        }
        return roleMember;
    }

    protected List<String> getRolesToSaveFor(List<String> roleNamesToConsider, String reviewRolesIndicator){
        if(roleNamesToConsider!=null){
            List<String> roleToSaveFor = new ArrayList<String>();
            if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            } else{
                roleToSaveFor.addAll(roleNamesToConsider);
            }
            return roleToSaveFor;
        } else {
            return Collections.emptyList();
        }
    }

    protected List<KfsKimDocRoleMember> getRoleMembers(OrgReviewRole orr){
        List<KfsKimDocRoleMember> objectsToSave = new ArrayList<KfsKimDocRoleMember>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        if(roleNamesToSaveFor!=null){
            for(String roleName: roleNamesToSaveFor){
                Role roleInfo = getRoleInfo(roleName);
                KfsKimDocRoleMember roleMemberToSave = getRoleMemberToSave(roleInfo, orr);
                if ( roleMemberToSave != null ) {
                    objectsToSave.add(roleMemberToSave);
                }
            }
        }
        return objectsToSave;
    }

    protected Map<String,String> getAttributes(OrgReviewRole orr, String kimTypeId){
        if( StringUtils.isBlank(kimTypeId) ) {
            return Collections.emptyMap();
        }

        List<KfsKimDocumentAttributeData> attributeDataList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attributeData = getAttribute(kimTypeId, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, orr.getChartOfAccountsCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.ORGANIZATION_CODE, orr.getOrganizationCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, orr.getFinancialSystemDocumentTypeCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, orr.getOverrideCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.FROM_AMOUNT, orr.getFromAmountStr());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.TO_AMOUNT, orr.getToAmountStr());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        return orr.getQualifierAsAttributeSet(attributeDataList);
    }

    protected RoleResponsibilityAction.Builder getRoleRspAction(OrgReviewRole orr, RoleMemberContract roleMember){
        //Assuming that there is only one responsibility for an org role
        //Get it now given the role id
        List<RoleResponsibility> roleResponsibilityInfos = KimApiServiceLocator.getRoleService().getRoleResponsibilities(roleMember.getRoleId());
        //Assuming that there is only 1 responsibility for both the org review roles
        if ( roleResponsibilityInfos == null || roleResponsibilityInfos.isEmpty() ) {
            throw new IllegalStateException("The Org Review Role id:"+roleMember.getRoleId()+" does not have any responsibility associated with it");
        }
        RoleResponsibility roleResponsibilityInfo = roleResponsibilityInfos.get(0);

        RoleResponsibilityAction.Builder rra = RoleResponsibilityAction.Builder.create();
        if ( StringUtils.isNotBlank( roleMember.getId() ) ) {
            List<RoleResponsibilityAction> origRoleRspActions = KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(roleMember.getId());
            if ( origRoleRspActions!=null && !origRoleRspActions.isEmpty() ) {
                rra.setId(origRoleRspActions.get(0).getId());
            }
        }

        rra.setRoleMemberId(roleMember.getId());
        rra.setRoleResponsibilityId(roleResponsibilityInfo.getRoleResponsibilityId());
        rra.setActionTypeCode(orr.getActionTypeCode());
        rra.setActionPolicyCode(orr.getActionPolicyCode());
        if(StringUtils.isNotBlank(orr.getPriorityNumber())){
            try{
                rra.setPriorityNumber(Integer.parseInt(orr.getPriorityNumber()));
            } catch(Exception nfx){
                rra.setPriorityNumber(null);
            }
        }
        rra.setForceAction(orr.isForceAction());
        return rra;
    }

    protected KfsKimDocumentAttributeData getAttribute( String kimTypeId, String attributeName, String attributeValue ) {
        if ( StringUtils.isNotBlank(attributeValue) ) {
            KimAttribute attribute = getAttributeDefinition(kimTypeId, attributeName);
            if( attribute != null ){
                KfsKimDocumentAttributeData attributeData = new KfsKimDocumentAttributeData();
                attributeData.setKimTypId(kimTypeId);
                attributeData.setAttrVal(attributeValue);
                attributeData.setKimAttrDefnId(attribute.getId());
                attributeData.setKimAttribute(attribute);
                return attributeData;
            }
        }
        return null;
    }

    protected KimAttribute getAttributeDefinition( String kimTypeId, String attributeName ) {
        // attempt to pull from cache
        Map<String,KimAttribute> typeAttributes = ATTRIBUTE_CACHE.get(kimTypeId);
        // if type has not been loaded, init
        if ( typeAttributes == null ) {
            KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId);
            if ( kimType != null ) {
                List<KimTypeAttribute> attributes = kimType.getAttributeDefinitions();
                typeAttributes = new HashMap<String, KimAttribute>();
                if ( attributes != null ) {
                    // build the map and put it into the cache
                    for ( KimTypeAttribute att : attributes ) {
                        typeAttributes.put( att.getKimAttribute().getAttributeName(), att.getKimAttribute() );
                    }
                }
                synchronized ( ATTRIBUTE_CACHE ) {
                    ATTRIBUTE_CACHE.put(kimTypeId, typeAttributes);
                }
            }
        }
        // now, see if the attribute is in there
        if ( typeAttributes != null ) {
            return typeAttributes.get(attributeName);
        }
        return null;
    }


    protected DocumentTypeService getDocumentTypeService() {
        if ( documentTypeService == null ) {
            documentTypeService = KewApiServiceLocator.getDocumentTypeService();
        }
        return documentTypeService;
    }

}
