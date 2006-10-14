/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/unit/src/org/kuali/kfs/coa/service/OrganizationReversionTestCase.java,v $
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

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.test.KualiTestBase;

/**
 * This class...
 * 
 * 
 */
public class OrganizationReversionTestCase extends KualiTestBase {

    public void testGetByPrimaryKey() throws Exception {
        OrganizationReversionService organizationReversionService = SpringServiceLocator.getOrganizationReversionService();
        assertNotNull("Service shouldn't be null", organizationReversionService);

        Integer fiscalYear = new Integer("2004");

        OrganizationReversion notexist = organizationReversionService.getByPrimaryId(fiscalYear, "BL", "TEST");
        assertNull("BL-TEST org reversion shouldn't exist in table", notexist);

        OrganizationReversion exist = organizationReversionService.getByPrimaryId(fiscalYear, "BL", "PSY");
        assertNotNull("BL-PSY should exist in table", exist);

    }
}
