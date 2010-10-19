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
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleManagementService;

/**
 * Test of various KFS application roles.
 */
@ConfigureContext
public class KFSApplicationRoleTest extends RoleTestBase {
        
    public static final String KFS_PRINCIPAL_NAME = "khuntley";
    public static final String NON_KFS_PRINCIPAL_NAME = "bcoffee";
    public static final String FINANCIAL_SYSTEM_USER_ROLE_NAME = "User";
    
    public void testFinancialSystemUserRoleTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(KFS_PRINCIPAL_NAME), KFSConstants.ParameterNamespaces.KFS, FINANCIAL_SYSTEM_USER_ROLE_NAME, new AttributeSet());
        assertUserIsNotRoleMember(getPrincipalIdByName(NON_KFS_PRINCIPAL_NAME), KFSConstants.ParameterNamespaces.KFS, FINANCIAL_SYSTEM_USER_ROLE_NAME, new AttributeSet());
    }
    
    public static final String ACCOUNT_DERIVED_CHART = "BL";
    public static final String ACCOUNT_DERIVED_ACCOUNT = "1031400";
    
    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE = "AV";
    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART = "BA";
    public static final String ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER = "9012025";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_DOC_TYPE = "DI";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_CHART = "BA";
    public static final String ACCOUNT_DERIVED_DELEGATE_SECONDARY_ACCOUNT_NUMBER = "9019995";
    public static final String ACCOUNT_DERIVED_DELEGATE_PRINCIPAL = "rmunroe";
    
    
    
    public void testAccountDerivedRoleTypeService() {
        AccountService accountService = SpringContext.getBean(AccountService.class);
        
        Account account = accountService.getByPrimaryId(ACCOUNT_DERIVED_CHART , ACCOUNT_DERIVED_ACCOUNT);
        AttributeSet roleQualifications = new AttributeSet();
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, account.getAccountNumber());
        roleQualifications.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, "10.0");
        roleQualifications.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE);
        
        // 1. test fiscal officer
        assertUserIsSingleMemberInRole(account.getAccountFiscalOfficerSystemIdentifier(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME, roleQualifications);
        
        // 2. test account supervisor
        assertUserIsSingleMemberInRole(account.getAccountsSupervisorySystemsIdentifier(), KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.ACCOUNT_SUPERVISOR_KIM_ROLE_NAME, roleQualifications);
        
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        final String delegatePrincipalId = getPrincipalIdByName(ACCOUNT_DERIVED_DELEGATE_PRINCIPAL);
        // 3. test primary delegate
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        roleQualifications.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE);
        assertUserIsRoleMember(delegatePrincipalId, KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_PRIMARY_DELEGATE_KIM_ROLE_NAME, roleQualifications);
        
        // 4. test secondary delegate
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_SECONDARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_SECONDARY_ACCOUNT_NUMBER);
        roleQualifications.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_SECONDARY_DOC_TYPE);
        assertUserIsRoleMember(delegatePrincipalId, KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_SECONDARY_DELEGATE_KIM_ROLE_NAME, roleQualifications);
        
        // does the FO pick up the delegates?
        roleQualifications.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, ACCOUNT_DERIVED_DELEGATE_PRIMARY_CHART);
        roleQualifications.put(KfsKimAttributes.ACCOUNT_NUMBER, ACCOUNT_DERIVED_DELEGATE_PRIMARY_ACCOUNT_NUMBER);
        roleQualifications.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, ACCOUNT_DERIVED_DELEGATE_PRIMARY_DOC_TYPE);
        Collection<RoleMembershipInfo> roleMembers = getRoleMembers(KFSConstants.ParameterNamespaces.KFS, KFSConstants.SysKimConstants.FISCAL_OFFICER_KIM_ROLE_NAME, roleQualifications);
        RoleMembershipInfo fo = roleMembers.iterator().next();
        roleMembers.iterator().hasNext();
        List<String> delegateIds = new ArrayList<String>();
        for (DelegateInfo delegate : fo.getDelegates()) {
            delegateIds.add(delegate.getMemberId());
        }
        assertTrue("Fiscal Officer delegates does not contain primary delegate", delegateIds.contains(delegatePrincipalId));
    }
        
    public static final String ORG_HIERARCHY_ORG_1_CHART = "BL";
    public static final String ORG_HIERARCHY_ORG_1_ORG = "BL";
    public static final String ORG_HIERARCHY_ORG_2_CHART = "BL";
    public static final String ORG_HIERARCHY_ORG_2_ORG = "ANTH";
    public static final String ORG_HIERARCHY_ORG_3_CHART = "KO";
    public static final String ORG_HIERARCHY_ORG_3_ORG = "CADM";
    public static final String ORG_HIERARCHY_REVIEWER = "cknotts";
    public static final String ORG_HIERARCHY_ROLE_NAME = "Organization Reviewer";
    public static final String GOOD_DOCUMENT_TYPE_NAME = "ACCT";
    
    public AttributeSet buildOrganizationHierarchyRoleQualifiers(String chartCode, String organizationCode, String docTypeName) {
        AttributeSet roleQualifiers = new AttributeSet();
        roleQualifiers.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chartCode);
        roleQualifiers.put(KfsKimAttributes.ORGANIZATION_CODE, organizationCode);
        roleQualifiers.put(KfsKimAttributes.DOCUMENT_TYPE_NAME, docTypeName);
        return roleQualifiers;
    }
    
    public void testOrganizationHierarchyTypeServce() {
        assertUserIsRoleMember(getPrincipalIdByName(ORG_HIERARCHY_REVIEWER), KFSConstants.ParameterNamespaces.KFS, ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_1_CHART, ORG_HIERARCHY_ORG_1_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(getPrincipalIdByName(ORG_HIERARCHY_REVIEWER), KFSConstants.ParameterNamespaces.KFS, ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_2_CHART, ORG_HIERARCHY_ORG_2_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsNotRoleMember(getPrincipalIdByName(ORG_HIERARCHY_REVIEWER), KFSConstants.ParameterNamespaces.KFS, ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(ORG_HIERARCHY_ORG_3_CHART, ORG_HIERARCHY_ORG_3_ORG, GOOD_DOCUMENT_TYPE_NAME));
    }
    
    public static final String OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE = "KFS-CAM";
    public static final String OPTIONAL_ORG_HIERARCHY_ROLE_NAME = "Processor";
    public static final String OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_CHART = "BL";
    public static final String OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_ORG = "ARSC";
    public static final String OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_CHART = "BL";
    public static final String OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_ORG = "CHEM";
    public static final String OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_MEMBER = "agetz";
    public static final String OPTIONAL_ORG_HIERARCHY_DESCENDING_HIGH_LEVEL_MEMBER = "avogel";
    public static final String OPTIONAL_ORG_HIERARCHY_NON_DESCENDING_HIGH_LEVEL_MEMBER = "ayoung";
    
    public void testOrganizationHierarchyOptionalTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_DESCENDING_HIGH_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsNotRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_NON_DESCENDING_HIGH_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        
        assertUserIsNotRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_LOW_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_DESCENDING_HIGH_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
        assertUserIsRoleMember(getPrincipalIdByName(OPTIONAL_ORG_HIERARCHY_NON_DESCENDING_HIGH_LEVEL_MEMBER), OPTIONAL_ORG_HIERARCHY_ROLE_NAMESPACE, OPTIONAL_ORG_HIERARCHY_ROLE_NAME, buildOrganizationHierarchyRoleQualifiers(OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_CHART, OPTIONAL_ORG_HIERARCHY_HIGH_LEVEL_ORG, GOOD_DOCUMENT_TYPE_NAME));
    }
    
    public static final String ACCOUNTING_ORG_HIERARCHY_ROLE_NAME = "Accounting Reviewer";
    public static final String ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG = "PSY";
    public static final String ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_ORG = "HMNF";
    public static final String ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_CHART = "BL";
    public static final String ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_ORG = "ARSC";
    public static final String ACCOUNTING_ORG_HIERARCHY_MEMBER = "jrichard";
    public static final String ACCOUNTING_ORG_HIERARCHY_DOC_TYPE = "REQS";
    public static final String ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT = "25000.03";
    public static final String ACCOUNTING_ORG_HIERARCHY_NOT_QUITE_ENOUGH_AMOUNT = "3";
    
    public AttributeSet buildAccountingOrganizationHierarchyReviewRoleQualifiers(String chartCode, String organizationCode, String documentTypeName, String amount) {
        AttributeSet roleQualifiers = buildOrganizationHierarchyRoleQualifiers(chartCode, organizationCode, documentTypeName);
        roleQualifiers.put(KfsKimAttributes.FINANCIAL_DOCUMENT_TOTAL_AMOUNT, amount);
        roleQualifiers.put(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, "");
        return roleQualifiers;
    }
    
    public void testAccountOrganizationHierarchyTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(ACCOUNTING_ORG_HIERARCHY_MEMBER), KFSConstants.ParameterNamespaces.KFS, ACCOUNTING_ORG_HIERARCHY_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsRoleMember(getPrincipalIdByName(ACCOUNTING_ORG_HIERARCHY_MEMBER), KFSConstants.ParameterNamespaces.KFS, ACCOUNTING_ORG_HIERARCHY_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_LOWER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsNotRoleMember(getPrincipalIdByName(ACCOUNTING_ORG_HIERARCHY_MEMBER), KFSConstants.ParameterNamespaces.KFS, ACCOUNTING_ORG_HIERARCHY_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_CHART, ACCOUNTING_ORG_HIERARCHY_TOO_HIGH_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_ENOUGH_AMOUNT));
        assertUserIsNotRoleMember(getPrincipalIdByName(ACCOUNTING_ORG_HIERARCHY_MEMBER), KFSConstants.ParameterNamespaces.KFS, ACCOUNTING_ORG_HIERARCHY_ROLE_NAME, buildAccountingOrganizationHierarchyReviewRoleQualifiers(ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_CHART, ACCOUNTING_ORG_HIERARCHY_HIGHER_LEVEL_ORG, ACCOUNTING_ORG_HIERARCHY_DOC_TYPE, ACCOUNTING_ORG_HIERARCHY_NOT_QUITE_ENOUGH_AMOUNT));
    }
    
}
