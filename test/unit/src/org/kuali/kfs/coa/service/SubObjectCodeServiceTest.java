/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import static org.kuali.kfs.util.SpringServiceLocator.getSubObjectCodeService;

import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the SubObjectCode service.
 * 
 * 
 */
@WithTestSpringContext
public class SubObjectCodeServiceTest extends KualiTestBase {

    /**
     * Test that the service returns null if any of the parameters are empty.
     */
    public void testEmptyParam() {
        SubObjCd resultSubObjectCode = getSubObjectCodeService().getByPrimaryId(new Integer(0), TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = getSubObjectCodeService().getByPrimaryId(TestConstants.Data4.POSTING_YEAR, "", TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = getSubObjectCodeService().getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, "", TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = getSubObjectCodeService().getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, "", TestConstants.Data4.SUBOBJECT_CODE);
        assertNull(resultSubObjectCode);

        resultSubObjectCode = getSubObjectCodeService().getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, "");
        assertNull(resultSubObjectCode);
    }

    /**
     * Test that the service returns the correct results based on the given pararmeters.
     */
    public void testService() {
        SubObjCd resultSubObjectCode = null;

        resultSubObjectCode = getSubObjectCodeService().getByPrimaryId(TestConstants.Data4.POSTING_YEAR, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE);
        assertNotNull(resultSubObjectCode);
        assertTrue(resultSubObjectCode.isFinancialSubObjectActiveIndicator());

    }
}
