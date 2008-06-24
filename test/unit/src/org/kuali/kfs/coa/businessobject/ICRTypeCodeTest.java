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
package org.kuali.kfs.coa.businessobject;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.ConfigureContext;

/**
 * Tests of the ICRTypeCode BO.
 */
@ConfigureContext
public class ICRTypeCodeTest extends KualiTestBase {

    /**
     * The isActive method should always return true, at least until a phase 2 task adds active indicators to all BOs.
     */
    public void testIsActive() {
        ICRTypeCode bo = (ICRTypeCode) (SpringContext.getBean(BusinessObjectService.class).findAll(ICRTypeCode.class).toArray()[0]);
        assertEquals(true, bo.isActive());
    }
}
