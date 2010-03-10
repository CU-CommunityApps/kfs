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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidCurrentCash;
import org.kuali.kfs.module.endow.document.service.KemidCurrentCashOpenRecordsService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This KemidCurrentCashOpenRecordsServiceImpl class provides the implementation for the method to test whether a KEMID has open
 * records in Current Cash: records with values greater or less than zero.
 */
public class KemidCurrentCashOpenRecordsServiceImpl implements KemidCurrentCashOpenRecordsService {

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.KemidCurrentCashOpenRecordsService#hasKemidOpenRecordsInCurrentCash(java.lang.String)
     */
    public boolean hasKemidOpenRecordsInCurrentCash(String kemid) {
        boolean hasOpenRecords = false;

        Map pkMap = new HashMap();
        pkMap.put(EndowPropertyConstants.KEMID, kemid);
        KemidCurrentCash kemidCurrentCash = (KemidCurrentCash) businessObjectService.findByPrimaryKey(KemidCurrentCash.class, pkMap);

        if (ObjectUtils.isNotNull(kemidCurrentCash)) {
            // if the record has values greater or less than zero than return true
            if (kemidCurrentCash.getCurrentIncomeCash().isNonZero() || kemidCurrentCash.getCurrentPrincipalCash().isNonZero()) {
                hasOpenRecords = true;
            }
        }
        return hasOpenRecords;
    }

    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService.
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
