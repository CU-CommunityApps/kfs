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
package org.kuali.kfs.coa.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;

public class ChartRoleTypeServiceImpl extends KimRoleTypeServiceBase {

    @Override
    public boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        if (!qualification.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE) || !roleQualifier.containsKey(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)) {
            throw new RuntimeException("Chart of accounts code not found in qualifier.");
        }
        return StringUtils.equals(qualification.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE), roleQualifier.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
    }

}
