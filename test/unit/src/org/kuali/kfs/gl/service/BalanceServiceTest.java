/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service;

import static org.kuali.core.util.SpringServiceLocator.getBalanceService;
import static org.kuali.core.util.SpringServiceLocator.getBeanFactory;
import static org.kuali.core.util.SpringServiceLocator.getDateTimeService;

import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * various tests for BalanceService, especially as it supports Account business rules; using hardcoded SQL for bootstrapping
 * 
 * 
 */
@WithTestSpringContext
public class BalanceServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceServiceTest.class);
    private final static String ACCOUNT_NUMBER = "999test";
    private final static String CHART = "zx";

    private static String DELETE_BALANCES = "delete from GL_BALANCE_T where ";
    private static String RAW_BALANCES = "select * from GL_BALANCE_T where ";
    private static String INSERT_BALANCE = "insert into GL_BALANCE_T(FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,UNIV_FISCAL_YR,FIN_SUB_OBJ_CD,FIN_OBJECT_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,FIN_BEG_BAL_LN_AMT,ACLN_ANNL_BAL_AMT) values('" + CHART + "','" + ACCOUNT_NUMBER + "','sub',";

    private static boolean runOnce = true;

    private static Account account = new Account();
    static {
        account.setAccountNumber(ACCOUNT_NUMBER);
        account.setChartOfAccountsCode(CHART);
        // jkeller: added Chart object since now used by the BalanceServiceImpl class.
        Chart chart = new Chart();
        chart.setFundBalanceObjectCode("9899");
        chart.setChartOfAccountsCode(CHART);
        account.setChartOfAccounts(chart);
    }


    private UnitTestSqlDao unitTestSqlDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        unitTestSqlDao = (UnitTestSqlDao) getBeanFactory().getBean("glUnitTestSqlDao");
        Integer fiscalYear = getDateTimeService().getCurrentFiscalYear();

        if (runOnce) {
            DELETE_BALANCES += "UNIV_FISCAL_YR=" + fiscalYear + " AND ACCOUNT_NBR='" + ACCOUNT_NUMBER + "'";
            RAW_BALANCES += "UNIV_FISCAL_YR=" + fiscalYear + " AND ACCOUNT_NBR='" + ACCOUNT_NUMBER + "'";
            INSERT_BALANCE += fiscalYear + ",";
            runOnce = false; // do not run again
        }

    }

    private void insertBalance(String objectTypeCode, String balanceTypeCode, String objectCode, KualiDecimal beginningAmount, KualiDecimal finalAmount) {
        unitTestSqlDao.sqlCommand(INSERT_BALANCE + "'123','" + objectCode + "','" + balanceTypeCode + "','" + objectTypeCode + "'," + beginningAmount + "," + finalAmount + ")");
    }

    public void purgeTestData() {
        unitTestSqlDao.sqlCommand(DELETE_BALANCES);

        List results = unitTestSqlDao.sqlSelect(RAW_BALANCES);
        assertNotNull("List shouldn't be null", results);
        assertEquals("Should return 0 results", 0, results.size());

    }

    public void testNetToZero() {
        List results;
        purgeTestData();

        assertTrue("should net to zero when no rows exist", getBalanceService().fundBalanceWillNetToZero(account));

        insertBalance("EE", "FF", "9899", new KualiDecimal(1.5), new KualiDecimal(2.5));
        results = unitTestSqlDao.sqlSelect(RAW_BALANCES);
        assertNotNull("List shouldn't be null", results);
        assertEquals("Should return 1 result", 1, results.size());

        assertTrue("should net to zero with non-AC balance Type Code", getBalanceService().fundBalanceWillNetToZero(account));

        insertBalance("CH", "AC", "9899", new KualiDecimal(1.5), new KualiDecimal(2.5));
        assertFalse(getBalanceService().fundBalanceWillNetToZero(account));

        // Negate the income balance with an equal expense balance
        insertBalance("EE", "AC", "9899", new KualiDecimal(2), new KualiDecimal(2));
        assertTrue("should net to zero after adding corresponding expenses", getBalanceService().fundBalanceWillNetToZero(account));
        purgeTestData();
    }

    public void testHasAssetLiabilityFundBalanceBalances() {
        List results;
        purgeTestData();
        assertFalse("no rows means no balances", getBalanceService().hasAssetLiabilityFundBalanceBalances(account));
        String fundBalanceObjectCode = "9899"; // TODO - get this from Service? Or System Options?
        insertBalance("LI", "AC", "9899", new KualiDecimal(1.5), new KualiDecimal(2.5));
        assertFalse("should ignore 9899 balance", getBalanceService().hasAssetLiabilityFundBalanceBalances(account));
        insertBalance("LI", "AC", "1234", new KualiDecimal(1.5), new KualiDecimal(2.5));
        assertTrue("expect nonzero balance for non-9899 balance", getBalanceService().hasAssetLiabilityFundBalanceBalances(account));


    }

}
