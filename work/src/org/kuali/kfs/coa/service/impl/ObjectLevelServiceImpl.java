/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This service implementation is the default implementation of the ObjLevel service that is delivered with Kuali.
 */
public class ObjectLevelServiceImpl implements ObjectLevelService {
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.coa.service.ObjectLevelService#getByPrimaryId(java.lang.String, java.lang.String)
     */
    @Override
    public ObjectLevel getByPrimaryId(String chartOfAccountsCode, String objectLevelCode) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, objectLevelCode);
        return getBusinessObjectService().findByPrimaryKey(ObjectLevel.class, keys);
    }

    @Override
    public List<ObjectLevel> getObjectLevelsByConsolidationsIds(List<String> consolidationIds) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE, consolidationIds);

        List<ObjectLevel> results = new ArrayList<ObjectLevel>();
        results.addAll(getBusinessObjectService().findMatching(ObjectLevel.class, fieldValues));
        return results;
    }

    @Override
    public List<ObjectLevel> getObjectLevelsByLevelIds(List<String> levelCodes) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, levelCodes);

        List<ObjectLevel> results = new ArrayList<ObjectLevel>();
        results.addAll(getBusinessObjectService().findMatching(ObjectLevel.class, fieldValues));
        return results;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
