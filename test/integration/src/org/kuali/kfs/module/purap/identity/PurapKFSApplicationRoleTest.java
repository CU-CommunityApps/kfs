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
package org.kuali.kfs.module.purap.identity;

import java.util.Collection;

import org.kuali.kfs.module.purap.identity.PurapKimAttributes;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.kfs.sys.identity.RoleTestBase;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.PersonService;

@ConfigureContext
public class PurapKFSApplicationRoleTest extends RoleTestBase {    
    public static final String PURAP_NAMESPACE = "KFS-PURAP";

    public static final String STRAIGHT_COMMODITY_USER = "season";
    public static final String WILDCARD_COMMODITY_USER = "cdbookma";
    public static final String BAD_WILDCARD_COMMODITY_USER = "fwillhit";
    public static final String COMMODITY_CAMPUS = "BL";
    public static final String COMMODITY_CODE = "1113";
    public static final String COMMODITY_REVIEWER_ROLE_NAME = "Commodity Reviewer";
    
    public void testCommodityReviewRoleTypeService() {
        AttributeSet roleQualifiers = new AttributeSet();
        roleQualifiers.put(KfsKimAttributes.CAMPUS_CODE, COMMODITY_CAMPUS);
        roleQualifiers.put(KfsKimAttributes.PURCHASING_COMMODITY_CODE, COMMODITY_CODE);
        
        assertUserIsRoleMember(getPrincipalIdByName(STRAIGHT_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
        assertUserIsRoleMember(getPrincipalIdByName(WILDCARD_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
        assertUserIsNotRoleMember(getPrincipalIdByName(BAD_WILDCARD_COMMODITY_USER), PURAP_NAMESPACE, COMMODITY_REVIEWER_ROLE_NAME, roleQualifiers);
    }
    
    public AttributeSet buildRoleQualificationForSensitiveData(String sensitiveDataCode) {
        AttributeSet roleQualification = new AttributeSet();
        roleQualification.put(PurapKimAttributes.SENSITIVE_DATA_CODE, sensitiveDataCode);
        return roleQualification;
    }

    public static final String SENSITIVE_DATA_1 = "ANIM";
    public static final String SENSITIVE_DATA_2 = "RADI";
    public static final String SENSITIVE_DATA_3 = "ANIM;RADI";
    public static final String SENSITIVE_DATA_REVIEWER = "bhhallow";
    public static final String SENSITIVE_DATA_ROLE_NAME = "Sensitive Data Viewer";

    public void testSensitiveDataRoleTypeService() {
        assertUserIsRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_1));
        assertUserIsNotRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_2));
        assertUserIsRoleMember(getPrincipalIdByName(SENSITIVE_DATA_REVIEWER), PURAP_NAMESPACE, SENSITIVE_DATA_ROLE_NAME, buildRoleQualificationForSensitiveData(SENSITIVE_DATA_3));
    }
}
