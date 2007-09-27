/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.util.Map;

import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionDetail;
import org.kuali.module.chart.service.OrganizationReversionService;
import org.kuali.module.chart.service.impl.OrganizationReversionServiceImpl;

public class OrganizationReversionMockService extends OrganizationReversionServiceImpl implements OrganizationReversionService {
    public static final String DEFAULT_BUDGET_REVERSION_CHART = "BL";
    public static final String DEFAULT_BUDGET_REVERSION_ACCOUNT = "0211301";
    public static final String DEFAULT_CASH_REVERSION_CHART = "BL";
    public static final String DEFAULT_CASH_REVERSION_ACCOUNT = "0211401";
    
    /**
     * @see org.kuali.module.chart.service.impl.OrganizationReversionServiceImpl#getByPrimaryId(java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    public OrganizationReversion getByPrimaryId(Integer fiscalYear, String chartCode, String orgCode) {
        // always return the same OrganizationReversion no matter what
        OrganizationReversion orgRev = new OrganizationReversion();
        orgRev.setChartOfAccountsCode(chartCode);
        orgRev.setUniversityFiscalYear(fiscalYear);
        orgRev.setOrganizationCode(orgCode);
        orgRev.setBudgetReversionChartOfAccountsCode(DEFAULT_BUDGET_REVERSION_CHART);
        orgRev.setBudgetReversionAccountNumber(DEFAULT_BUDGET_REVERSION_ACCOUNT);
        orgRev.setCashReversionFinancialChartOfAccountsCode(DEFAULT_CASH_REVERSION_CHART);
        orgRev.setCashReversionAccountNumber(DEFAULT_CASH_REVERSION_ACCOUNT);
        orgRev.setCarryForwardByObjectCodeIndicator(true);
        
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C01", KFSConstants.RULE_CODE_A));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C02", KFSConstants.RULE_CODE_C1));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C03", KFSConstants.RULE_CODE_C2));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C04", KFSConstants.RULE_CODE_N1));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C05", KFSConstants.RULE_CODE_N2));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C06", KFSConstants.RULE_CODE_A));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C07", KFSConstants.RULE_CODE_A));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C08", KFSConstants.RULE_CODE_R1));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C09", KFSConstants.RULE_CODE_R2));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C10", KFSConstants.RULE_CODE_A));
        orgRev.addOrganizationReversionDetail(createDetail(fiscalYear, chartCode, orgCode, "C11", KFSConstants.RULE_CODE_A));
        
        return orgRev;
    }

    /**
     * This method...
     * @param fiscalYear
     * @param chartCode
     * @param orgCode
     * @param categoryCode
     * @param categoryAlgorithm
     */
    private OrganizationReversionDetail createDetail(Integer fiscalYear, String chartCode, String orgCode, String categoryCode, String categoryAlgorithm) {
        OrganizationReversionDetail detail = new OrganizationReversionDetail();
        detail.setUniversityFiscalYear(new Integer(fiscalYear.intValue()-1));
        detail.setChartOfAccountsCode(chartCode);
        detail.setOrganizationCode(orgCode);
        detail.setOrganizationReversionCategoryCode(categoryCode);
        detail.setOrganizationReversionCode(categoryAlgorithm);
        detail.setOrganizationReversionObjectCode("5000");
        return detail;
    }
    

}
