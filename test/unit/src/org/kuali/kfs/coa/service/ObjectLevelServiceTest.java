/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/unit/src/org/kuali/kfs/coa/service/ObjectLevelServiceTest.java,v $
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
package org.kuali.module.chart.service;

import static org.kuali.core.util.SpringServiceLocator.getObjectLevelService;

import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the ObjLevel service.
 * 
 * 
 */
@WithTestSpringContext
public class ObjectLevelServiceTest extends KualiTestBase {

    public void testFindById() {
        ObjLevel objectLevel = getObjectLevelService().getByPrimaryId("UA", "BASE");
        assertEquals("Object Level Code should be BASE", objectLevel.getFinancialObjectLevelCode(), "BASE");
    }
}
