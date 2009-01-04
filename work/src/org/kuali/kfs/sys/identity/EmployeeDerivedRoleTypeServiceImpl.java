/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.bo.entity.KimEntity;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class EmployeeDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    private static IdentityManagementService identityManagementService;
    private static BusinessObjectService businessObjectService;
    
    protected static final String A_EMPLOYEE_STATUS_CODE = "A";
    protected static final String STAFF_AFFILIATION_TYPE_CODE = "STAFF";
    protected static final String FCLTY_AFFILIATION_TYPE_CODE = "FCLTY";
    protected static final String P_EMPLOYEE_TYPE_CODE = "P";
    
    /**
     *  Requirements:
     *  Derived Role: Employee - EmployeeDerivedRoleTypeService
     *  - KFS-SYS Active Faculty or Staff: 
     *      principals where EMP_STAT_CD = A in KRIM_ENTITY_EMP_INFO_T and AFLTN_TYP_CD = STAFF or AFLTN_TYP_CD = FCLTY 
     *      in KRIM_ENTITY_AFLTN_T
     *  - KFS-SYS Active Professional Employee: 
     *      principals where EMP_STAT_CD = A & EMP_TYPE_CD = P in KRIM_ENTITY_EMP_INFO_T
     *   
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<String> getPrincipalIdsFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        List<String> principalIds = new ArrayList<String>();

        String principalId = qualification.get(KimConstants.PropertyNames.PRINCIPAL_ID);

        if(hasApplicationRole(principalId, null, namespaceCode, roleName, qualification))
            principalIds.add( principalId );
        else
            principalIds = getPrincipalIds(getIdentityManagementService().lookupEntitys(buildCriteria(roleName, null)));
        
        return principalIds;
    }

    protected List<String> getPrincipalIds(List<? extends KimEntity> kimEntities){
        List<String> principalIds = new ArrayList<String>();
        for(KimEntity kimEntity: kimEntities){
            principalIds.addAll(getPrincipalIds(kimEntity));
        }
        return principalIds;
    }
    
    protected List<String> getPrincipalIds(KimEntity kimEntity){
        List<String> principalIds = new ArrayList<String>();
        for(KimPrincipal kimPrincipal: kimEntity.getPrincipals())
            principalIds.add(kimPrincipal.getPrincipalId());
        return principalIds;
    }
    
    /***
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(
            String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification){

        if(StringUtils.isEmpty(principalId))
            return false;

        List<KimEntity> kimEntities = getIdentityManagementService().lookupEntitys(buildCriteria(roleName, principalId));
        return kimEntities!=null && kimEntities.size()>0;
    }

    protected Map<String, String> buildCriteria(String roleName, String principalId){
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("active", KFSConstants.ACTIVE_INDICATOR);
        
        criteria.put("employmentInformation.active", KFSConstants.ACTIVE_INDICATOR);
        criteria.put("employmentInformation.employeeStatusCode", A_EMPLOYEE_STATUS_CODE);
        
        if(KFSConstants.SysKimConstants.ACTIVE_FACULTY_OR_STAFF_KIM_ROLE_NAME.equals(roleName)){
            criteria.put("employmentInformation.affiliation.active", KFSConstants.ACTIVE_INDICATOR);
            criteria.put("employmentInformation.affiliation.affiliationTypeCode", 
                    STAFF_AFFILIATION_TYPE_CODE+"|"+FCLTY_AFFILIATION_TYPE_CODE);

        } else if(KFSConstants.SysKimConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME.equals(roleName)){
            criteria.put("employmentInformation.employeeTypeCode", P_EMPLOYEE_TYPE_CODE);

        }
        if(StringUtils.isNotEmpty(principalId)){
            criteria.put("principals.active", KFSConstants.ACTIVE_INDICATOR);
            criteria.put("principals.principalId", principalId);
        }
        return criteria;
    }

    /**
     * @return the IdentityManagementService
     */
    protected IdentityManagementService getIdentityManagementService(){
        if (identityManagementService == null ) {
            identityManagementService = KIMServiceLocator.getIdentityManagementService();
        }
        return identityManagementService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null ) {
            businessObjectService = KNSServiceLocator.getBusinessObjectService();
        }
        return businessObjectService;

    }

}