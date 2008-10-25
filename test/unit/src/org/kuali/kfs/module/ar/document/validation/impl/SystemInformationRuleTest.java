/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;

/*
* This class tests the business rules for the SystemInformation Maint. Doc.
*/
@ConfigureContext(session = khuntley)
public class SystemInformationRuleTest extends MaintenanceRuleTestBase {

    SystemInformation systemInformation;
    
    private static Integer UNIVERSITY_FISCAL_YEAR = new Integer(2008);
    private static String EXPENSE_OBJECT_CODE = "1958";
    private static String INCOME_OBJECT_CODE = "1999";
    private static String CHART_CODE = "BA";
    private static String ORGANIZATION_CODE = "ACPR";
   
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        systemInformation = new SystemInformation();
        systemInformation.setUniversityFiscalYear(UNIVERSITY_FISCAL_YEAR);
        systemInformation.setProcessingChartOfAccountCode(CHART_CODE);
        systemInformation.setProcessingOrganizationCode(ORGANIZATION_CODE);
    }
    
    
    
    /**
     * This method tests if the checkSalesTaxObjectValidCode rule returns true when salesTaxFinancialObjectCode is set to an income object code
     */

    public void testCheckSalesTaxObjectValidCode_True(){
        systemInformation.setSalesTaxFinancialObjectCode(INCOME_OBJECT_CODE);
        systemInformation.refreshReferenceObject("salesTaxFinancialObject");
        SystemInformationRule rule = (SystemInformationRule) setupMaintDocRule(newMaintDoc(systemInformation), SystemInformationRule.class);
        
        boolean result = rule.checkSalesTaxObjectValidCode(systemInformation);
        assertEquals( "When sales tax object code is " + INCOME_OBJECT_CODE + ", checkSalesTaxObjectValidCode should return true. ", true, result );        
    }

    /**
     * This method tests if the checkSalesTaxObjectValidCode rule returns false when salesTaxFinancialObjectCode is set to an expense object code
     */

    public void testCheckSalesTaxObjectValidCode_False(){
        systemInformation.setSalesTaxFinancialObjectCode(EXPENSE_OBJECT_CODE);
        systemInformation.refreshReferenceObject("salesTaxFinancialObject");
        SystemInformationRule rule = (SystemInformationRule) setupMaintDocRule(newMaintDoc(systemInformation), SystemInformationRule.class);
        
        boolean result = rule.checkSalesTaxObjectValidCode(systemInformation);
        assertEquals( "When sales tax object code is " + EXPENSE_OBJECT_CODE + ", checkSalesTaxObjectValidCode should return false. ", false, result );  
        
    }
}

