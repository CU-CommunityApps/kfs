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
package org.kuali.module.gl.service.impl.orgreversion;

import java.util.Map;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class OrganizationReversionCategoryTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionCategoryTest.class);

    private OrganizationReversionService organizationReversionService;
    private Map<String, OrganizationReversionCategoryLogic> categories;
    private ObjectCodeService objectCodeService;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        Map<String, OrganizationReversionService> orgRevServiceBeans = SpringContext.getBeansOfType(OrganizationReversionService.class);
        organizationReversionService = orgRevServiceBeans.get("organizationReversionService");
        categories = organizationReversionService.getCategories();
        objectCodeService = SpringContext.getBean(ObjectCodeService.class);
    }

    public void testC01Reversion() {
        String category = "C01";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not wages", cat.containsObjectCode(se));

        ObjectCode wages = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "3854");
        assertNotNull("Wages Object code should exist", wages);

        assertTrue("Wages object is wages", cat.containsObjectCode(wages));
    }

    public void testC02Reversion() {
        String category = "C02";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not salary/fringes", cat.containsObjectCode(se));

        ObjectCode wages = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "3854");
        assertNotNull("Wages Object code should exist", wages);

        assertFalse("Wages object is not salary/fringes", cat.containsObjectCode(wages));

        ObjectCode sal = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "2000");
        assertNotNull("Salary Object code should exist", sal);

        assertTrue("Wages object is salary/fringes", cat.containsObjectCode(sal));
    }

    public void testC03Reversion() {
        String category = "C03";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not financial aid", cat.containsObjectCode(se));

        ObjectCode fr = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5885");
        assertNotNull("Fee Remission Object code should exist", fr);

        assertTrue("Fee Remission object is financial aid", cat.containsObjectCode(fr));
    }

    public void testC04Reversion() {
        String category = "C04";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not capital equipment", cat.containsObjectCode(se));

        ObjectCode ce = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "7677");
        assertNotNull("Capital Equip Object code should exist", ce);

        assertTrue("Art object is capital equipment", cat.containsObjectCode(ce));
    }

    public void testC05Reversion() {
        String category = "C05";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not reserve", cat.containsObjectCode(se));
        // TODO True test
    }

    public void testC06Reversion() {
        String category = "C06";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not transfer out", cat.containsObjectCode(se));
        // TODO True test
    }

    public void testC07Reversion() {
        String category = "C07";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not transfer in", cat.containsObjectCode(se));
        // TODO True test
    }

    public void testC08Reversion() {
        String category = "C08";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not travel", cat.containsObjectCode(se));
        // TODO True test
    }

    public void testC09Reversion() {
        String category = "C09";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertTrue("S&E is S&E", cat.containsObjectCode(se));
        // TODO False test
    }

    public void testC10Reversion() {
        String category = "C10";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not asses expend", cat.containsObjectCode(se));
        // TODO True test
    }

    public void testC11Reversion() {
        String category = "C11";
        OrganizationReversionCategoryLogic cat = (OrganizationReversionCategoryLogic) categories.get(category);
        assertNotNull("Category not found", cat);

        ObjectCode se = objectCodeService.getByPrimaryId(new Integer(2007), "BL", "5000");
        assertNotNull("S&E Object code should exist", se);

        assertFalse("S&E is not revenue", cat.containsObjectCode(se));
        // TODO True test
    }
}
