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
package org.kuali.kfs.module.cg.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cg.businessobject.Purpose;
import org.kuali.kfs.module.cg.document.service.PurposeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;

public class PurposeServiceImpl implements PurposeService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cg.document.service.PurposeService#getPurposes()
     */
    public List<Purpose> getPurposes() {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.DATA_OBJECT_MAINTENANCE_CODE_ACTIVE_INDICATOR, KFSConstants.ACTIVE_INDICATOR);

        Collection col = businessObjectService.findMatchingOrderBy(Purpose.class, fieldValues, KFSPropertyConstants.USER_SORT_NUMBER, true);

        return new ArrayList(col);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
