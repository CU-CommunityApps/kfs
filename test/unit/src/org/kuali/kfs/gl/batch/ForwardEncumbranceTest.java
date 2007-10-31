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
package org.kuali.module.gl.batch;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.A21SubAccountService;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.batch.closing.year.service.impl.helper.EncumbranceClosingRuleHelper;
import org.kuali.module.gl.batch.closing.year.util.EncumbranceClosingOriginEntryFactory;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.util.FatalErrorException;
import org.kuali.module.gl.util.OriginEntryOffsetPair;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class ForwardEncumbranceTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ForwardEncumbranceTest.class);
    
    enum ENCUMBRANCE_FIXTURE {
        COST_SHARE_ENCUMBRANCE("BL", "4531413", "CS001", "7100", "EX", "EE");
        
        // to find account: select ca_prior_yr_acct_t.fin_coa_cd, ca_prior_yr_acct_t.ACCOUNT_NBR, CA_A21_SUB_ACCT_T.sub_acct_nbr from (ca_prior_yr_acct_t join ca_sub_fund_grp_t on ca_prior_yr_acct_t.SUB_FUND_GRP_CD = ca_sub_fund_grp_t.SUB_FUND_GRP_CD) join CA_A21_SUB_ACCT_T on CA_PRIOR_YR_ACCT_T.fin_coa_cd = CA_A21_SUB_ACCT_T.fin_coa_cd and CA_PRIOR_YR_ACCT_T.account_nbr = CA_A21_SUB_ACCT_T.account_nbr where ca_sub_fund_grp_t.FUND_GRP_CD = 'CG' and CA_A21_SUB_ACCT_T.sub_acct_typ_cd = 'CS'
        // this was a rough one, it needed a cost share sub account and a CG sub fund group fund group type
        
        private String chart;
        private String accountNumber;
        private String subAccountNumber;
        private String objectCode;
        private String balanceType;
        private String objectTypeCode;
        
        private ENCUMBRANCE_FIXTURE(String chart, String accountNumber, String subAccountNumber, String objectCode, String balanceType, String objectTypeCode) {
            this.chart = chart;
            this.accountNumber = accountNumber;
            this.subAccountNumber = subAccountNumber;
            this.objectCode = objectCode;
            this.balanceType = balanceType;
            this.objectTypeCode = objectTypeCode;
        }
        
        public Encumbrance convertToEncumbrance() {
            Encumbrance e = new Encumbrance();
            Integer fy = new Integer(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().intValue() - 1);
            e.setUniversityFiscalYear(fy);
            e.setChartOfAccountsCode(chart);
            e.setAccountNumber(accountNumber);
            e.setSubAccountNumber(subAccountNumber);
            e.setObjectCode(objectCode);
            e.setSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            e.setBalanceTypeCode(balanceType);
            e.setDocumentTypeCode("EXEN"); // we don't need this field
            e.setOriginCode("EP");
            e.setDocumentNumber("000001"); // we don't need this field
            GregorianCalendar lastYear = new GregorianCalendar();
            lastYear.add(Calendar.YEAR, -1);
            e.setTransactionEncumbranceDate(new java.sql.Date(lastYear.getTimeInMillis()));
            e.setTransactionEncumbranceDescription("MONKEYS-R-US IS THE NEWEST AND GREATEST STORE IN THE ENTIRE TRI-STATE AREA");
            e.setAccountLineEncumbranceAmount(new KualiDecimal(1000));
            e.setAccountLineEncumbranceClosedAmount(KualiDecimal.ZERO);
            return e;
        }
        
        public String getObjectType() {
            return this.objectTypeCode;
        }
    }
    
    public void testEncumbranceSelection() {
        EncumbranceClosingRuleHelper helper = new EncumbranceClosingRuleHelper();
        helper.setA21SubAccountService(SpringContext.getBean(A21SubAccountService.class));
        helper.setKualiConfigurationService(kualiConfigurationService);
        helper.setPriorYearAccountService(SpringContext.getBean(PriorYearAccountService.class));
        helper.setSubFundGroupService(SpringContext.getBean(SubFundGroupService.class));
        
        assertTrue(helper.anEntryShouldBeCreatedForThisEncumbrance(ENCUMBRANCE_FIXTURE.COST_SHARE_ENCUMBRANCE.convertToEncumbrance()));
    }
    
    public void testCostShareSelection() throws Exception {
        EncumbranceClosingRuleHelper helper = new EncumbranceClosingRuleHelper();
        helper.setA21SubAccountService(SpringContext.getBean(A21SubAccountService.class));
        helper.setKualiConfigurationService(kualiConfigurationService);
        helper.setPriorYearAccountService(SpringContext.getBean(PriorYearAccountService.class));
        helper.setSubFundGroupService(SpringContext.getBean(SubFundGroupService.class));
        
        Encumbrance encumbrance = ENCUMBRANCE_FIXTURE.COST_SHARE_ENCUMBRANCE.convertToEncumbrance();
        OriginEntryOffsetPair entryPair = EncumbranceClosingOriginEntryFactory.createBeginningBalanceEntryOffsetPair(encumbrance, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear(), new java.sql.Date(new GregorianCalendar().getTimeInMillis()));
        
        assertTrue(helper.isEncumbranceEligibleForCostShare(entryPair.getEntry(), entryPair.getOffset(), encumbrance, ENCUMBRANCE_FIXTURE.COST_SHARE_ENCUMBRANCE.getObjectType()));
        
        OriginEntryOffsetPair costShareEntryPair = EncumbranceClosingOriginEntryFactory.createCostShareBeginningBalanceEntryOffsetPair(encumbrance, new java.sql.Date(new GregorianCalendar().getTimeInMillis()));
        LOG.info(costShareEntryPair.getEntry().getLine());
        LOG.info(costShareEntryPair.getOffset().getLine());
        
        assertTrue(costShareEntryPair.getOffset().getLine().indexOf("GENERATED") >= 0);
    }
}
