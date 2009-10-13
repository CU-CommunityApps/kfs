/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.kns.service.impl.InactivationBlockingDetectionServiceImpl;

/**
 * This class overrides the base Inactivation Blocking Detection Service.  It is intended to be used with ObjectCode or ObjectCodeCurrent BOs when they
 * represent the BLOCKED bo.
 */
public class ObjectCodeCurrentInactivationBlockingDetectionServiceImpl extends InactivationBlockingDetectionServiceImpl {
    private UniversityDateService universityDateService;
    
    @Override
    protected Map<String, Object> buildInactivationBlockerQueryMap(BusinessObject blockedBo, InactivationBlockingMetadata inactivationBlockingMetadata) {
        ObjectCode blockedObjectCode = (ObjectCode) blockedBo;
        if (universityDateService.getCurrentFiscalYear().equals(blockedObjectCode.getUniversityFiscalYear())) {
            return super.buildInactivationBlockerQueryMap(blockedBo, inactivationBlockingMetadata);
        }
        return null;
        
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

}
