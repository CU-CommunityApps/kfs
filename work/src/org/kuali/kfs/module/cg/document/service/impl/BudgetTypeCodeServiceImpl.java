/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.kra.budget.service.impl;

import java.util.Collection;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.kra.budget.bo.BudgetTypeCode;
import org.kuali.module.kra.budget.service.BudgetTypeCodeService;

/**
 * This class is the service implementation for the Budget Type Codes interface. This is the default, Kuali provided implementation.
 */
public class BudgetTypeCodeServiceImpl implements BudgetTypeCodeService {

    private BusinessObjectService businessObjectService;

    public Collection getDefaultBudgetTypeCodes() {
        return businessObjectService.findAll(BudgetTypeCode.class);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
