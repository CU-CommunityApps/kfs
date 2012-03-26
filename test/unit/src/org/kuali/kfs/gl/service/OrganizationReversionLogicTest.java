/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http:www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.service;

import java.io.File;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.OrganizationReversionService;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.OrganizationReversionCategoryLogic;
import org.kuali.kfs.gl.batch.service.OrganizationReversionProcess;
import org.kuali.kfs.gl.batch.service.OrganizationReversionProcessService;
import org.kuali.kfs.gl.batch.service.OrganizationReversionUnitOfWorkService;
import org.kuali.kfs.gl.batch.service.impl.CashOrganizationReversionCategoryLogic;
import org.kuali.kfs.gl.batch.service.impl.OrganizationReversionMockServiceImpl;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Tests that the Organization Reversion process generates the proper origin entries under
 * certain circumstances with each of the Org Reversion logics: A, C1, C2, N1, N2, R1, R2
 */
@ConfigureContext
public class OrganizationReversionLogicTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationReversionLogicTest.class);

    public static final String DEFAULT_BALANCE_CHART = "BL";
    public static final String DEFAULT_BALANCE_ACCOUNT_NBR = "1031400";

    private OrganizationReversionService organizationReversionService;
    private BalanceService balanceService;
    private OrganizationReversionCategoryLogic cashOrganizationReversionCategoryLogic;
    private OrganizationReversionProcessService organizationReversionProcessService;
    private PriorYearAccountService priorYearAccountService;
    private OrganizationReversionUnitOfWorkService orgReversionUnitOfWorkService;
    private OrganizationReversionProcess orgRevProcess;
    private Integer currentFiscalYear;
    private Integer previousFiscalYear;
    private String batchDirectory;

    /**
     * These enums are balance fixtures to test.  These fixtures come from the spreadsheet org+reversion.xls, posted as an attachment to JIRA KULRNE-5652
     * A quick summary is this:
     * Each scenario needs a fixture for current balance and actual balance; scenarios 4 - 7 also have encumbrances.
     * Scenario 1: Budget exceeds actual
     * Scenario 2: Actual exceeds budget
     * Scenario 3: Budget equals actual
     * Scenario 4: Budget exceeds actual + encumbrance
     * Scenario 5: Budget equals actual + encumbrance
     * Scenario 6: Actual + encumbrance exceeds budget
     * Scenario 7: Actual exceeds budget, and there's an encumbrance to boot
     */
    enum BALANCE_FIXTURE {
        SCENARIO1_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(5000)), SCENARIO1_ACTUAL_BALANCE("AC", new KualiDecimal(3000)),

        SCENARIO2_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(4000)), SCENARIO2_ACTUAL_BALANCE("AC", new KualiDecimal(7000)),

        SCENARIO3_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(2000)), SCENARIO3_ACTUAL_BALANCE("AC", new KualiDecimal(2000)),

        SCENARIO4_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(5000)), SCENARIO4_ACTUAL_BALANCE("AC", new KualiDecimal(3000)), SCENARIO4_ENCUMBRANCE_BALANCE("IE", new KualiDecimal(500)),

        SCENARIO5_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(5000)), SCENARIO5_ACTUAL_BALANCE("AC", new KualiDecimal(3000)), SCENARIO5_ENCUMBRANCE_BALANCE("IE", new KualiDecimal(2000)),

        SCENARIO6_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(5000)), SCENARIO6_ACTUAL_BALANCE("AC", new KualiDecimal(3000)), SCENARIO6_ENCUMBRANCE_BALANCE("IE", new KualiDecimal(2500)),

        SCENARIO7_CURRENT_BUDGET_BALANCE("CB", new KualiDecimal(4000)), SCENARIO7_ACTUAL_BALANCE("AC", new KualiDecimal(7000)), SCENARIO7_ENCUMBRANCE_BALANCE("IE", new KualiDecimal(500));

        private String objectCode;
        private String balanceTypeCode;
        private String objectTypeCode;
        private KualiDecimal amount;
        private Date timestamp;
        private static final String DATE_FORMAT = "yyyy-MM-dd";
        private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BALANCE_FIXTURE.class);

        /**
         * Constructs a OrganizationReversionLogicTest.BALANCE_FIXTURE instance
         * @param balanceTypeCode the type code of the balance to create
         * @param amount the amount of the balance to create
         */
        private BALANCE_FIXTURE(String balanceTypeCode, KualiDecimal amount) {
            this.balanceTypeCode = balanceTypeCode;
            this.amount = amount;
        }

        /**
         * Sets the object code of the object (so the same fixture can be used for multiple categories)
         * @param objectCode the object code to set
         */
        public void setObjectCode(String objectCode) {
            this.objectCode = objectCode;
        }

        /**
         * Converts the fixture into a balance to test
         * 
         * @return a balance represented by this fixture
         */
        public Balance convertToBalance() {
            Balance balance = new Balance();
            balance.setUniversityFiscalYear(TestUtils.getFiscalYearForTesting());
            balance.setChartOfAccountsCode(DEFAULT_BALANCE_CHART);
            balance.setAccountNumber(DEFAULT_BALANCE_ACCOUNT_NBR);
            balance.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            balance.setObjectCode(objectCode);
            balance.setSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            balance.setBalanceTypeCode(balanceTypeCode);
            balance.setObjectTypeCode("EX");
            balance.setAccountLineAnnualBalanceAmount(amount);
            balance.setBeginningBalanceLineAmount(KualiDecimal.ZERO);
            balance.setContractsGrantsBeginningBalanceAmount(KualiDecimal.ZERO);
            balance.setMonth1Amount(amount);
            balance.setMonth2Amount(KualiDecimal.ZERO);
            balance.setMonth3Amount(KualiDecimal.ZERO);
            balance.setMonth4Amount(KualiDecimal.ZERO);
            balance.setMonth5Amount(KualiDecimal.ZERO);
            balance.setMonth6Amount(KualiDecimal.ZERO);
            balance.setMonth7Amount(KualiDecimal.ZERO);
            balance.setMonth8Amount(KualiDecimal.ZERO);
            balance.setMonth9Amount(KualiDecimal.ZERO);
            balance.setMonth10Amount(KualiDecimal.ZERO);
            balance.setMonth11Amount(KualiDecimal.ZERO);
            balance.setMonth12Amount(KualiDecimal.ZERO);
            balance.setMonth13Amount(KualiDecimal.ZERO);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
                java.util.Date jud = sdf.parse(SpringContext.getBean(ParameterService.class).getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM));
                balance.setTimestamp(new java.sql.Date(jud.getTime()));
            }
            catch (ParseException e) {
                LOG.debug("Parse date exception while parsing transaction date");
            }
            balance.refresh();
            return balance;
        }
    };

    /**
     * Fixtures that have object codes that will go into certain categories.  OrganizationReversionMockServiceImpl
     * makes certain that each category represents different logic.
     * @see org.kuali.kfs.gl.batch.service.impl.OrganizationReversionMockServiceImpl
     */
    enum OBJECT_CODE_FIXTURE {
        C01_ORG_WAGES_CODE("3000"), // C01 = A logic
        C02_SALARY_FRINGES_CODE("2400"), // C02 = C1 logic
        C03_FINANCIAL_AID_CODE("5880"), // C03 = C2 logic
        C04_CAPITAL_EQUIP_CODE("7200"), // C04 = N1 logic
        C05_RESERVE_CODE("7990"), // C05 = N2 logic
        C08_TRAVEL_CODE("6000"), // C08 = R1 logic
        C09_OTHER_EXPENSE_CODE("4100"); // C09 = R2 logic

        private String code;

        /**
         * Constructs a OrganizationReversionLogicTest.OBJECT_CODE_FIXTURE instance
         * @param code the object code represented by this fixture
         */
        private OBJECT_CODE_FIXTURE(String code) {
            this.code = code;
        }

        /**
         * Returns the object code
         * @return the object code
         */
        public String getCode() {
            return this.code;
        }
    }

    /**
     * Does the prelinary work of setting up the test...getting the org reversion mock, setting up
     * the parameters for the job, and creates the OrganizationReversionProcessImpl object that will actually
     * complete the job
     * @see org.kuali.kfs.gl.businessobject.OriginEntryTestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        organizationReversionService = SpringContext.getBean(OrganizationReversionService.class,"glOrganizationReversionMockService");
        DateTimeService dtService = SpringContext.getBean(DateTimeService.class);
        balanceService = SpringContext.getBean(BalanceService.class);
        cashOrganizationReversionCategoryLogic = SpringContext.getBean(CashOrganizationReversionCategoryLogic.class);
        priorYearAccountService = SpringContext.getBean(PriorYearAccountService.class);
        orgReversionUnitOfWorkService = SpringContext.getBean(OrganizationReversionUnitOfWorkService.class);
        organizationReversionProcessService = SpringContext.getBean(OrganizationReversionProcessService.class);
        batchDirectory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory")+"/gl/test_directory/originEntry";
        
        File batchDirectoryFile = new File(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory")+"/gl/test_directory");
        batchDirectoryFile.mkdir();
        batchDirectoryFile = new File(batchDirectory);
        batchDirectoryFile.mkdir();

        orgRevProcess = SpringContext.getBean(OrganizationReversionProcess.class,"glOrganizationReversionTestProcess");
    }

    /**
     * Makes sure that a new OrganizationReversionProcessImpl will be used for the next test
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        orgRevProcess = null;
        
        File batchDirectoryFile = new File(batchDirectory);
        for (File f : batchDirectoryFile.listFiles()) {
            f.delete();
        }
        batchDirectoryFile.delete();
        batchDirectoryFile = new File(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory")+"/gl/test_directory");
        batchDirectoryFile.delete();
    }

    /**
     * Given a list of balances, saves those balances and runs the organization reversion process on them
     * 
     * @param balancesToTestAgainst the balances for the specific test
     * @return an OriginEntryGroup where the resultant origin entries were saved
     */
    protected List<? extends OriginEntryInformation> runOrganizationReversionProcess(List<Balance> balancesToTestAgainst) {
   
        clearGlBalanceTable();
        clearBatchFiles();
        
        //we do not need to call clearCache() since no dao and jdbc calls mixted in this method.
        //refer to KFSMI-7637
        // persistenceService.clearCache();
        
        Map<String, ?> jobParameters = organizationReversionProcessService.getJobParameters();
        currentFiscalYear = new Integer(((Number)jobParameters.get(KFSConstants.UNIV_FISCAL_YR)).intValue() + 1);
        previousFiscalYear = new Integer(((Number)jobParameters.get(KFSConstants.UNIV_FISCAL_YR)).intValue());
        Map<String, Integer> organizationReversionCounts = new HashMap<String, Integer>();
        
        for (Balance bal : balancesToTestAgainst) {
            bal.setUniversityFiscalYear(previousFiscalYear);            
            SpringContext.getBean(BusinessObjectService.class).save(bal);
        }

        orgRevProcess.setHoldGeneratedOriginEntries(true);
        orgRevProcess.organizationReversionProcess(jobParameters, organizationReversionCounts);

        // ye olde sanity check
        assertEquals("Balances Read", new Integer(balancesToTestAgainst.size()), new Integer(orgRevProcess.getBalancesRead()));
        
        return orgRevProcess.getGeneratedOriginEntries();
    }

    /**
     * Asserts that certain fields in the given origin entry equal given parameters
     * @param originEntry the actual origin entry
     * @param fiscalYear the expected fiscal year
     * @param periodCode the expected period code
     * @param chart the expected chart
     * @param account the expected account
     * @param objectCode the expected object code
     * @param balanceType the expected balance type
     * @param objectType the expected object type
     * @param amount the expected amount
     */
    private void assertOriginEntry(OriginEntryInformation originEntry, Integer fiscalYear, String periodCode, String chart, String account, String objectCode, String balanceType, String objectType, KualiDecimal amount) {
        assertEquals("Origin Entry " + originEntry.toString() + " Fiscal Year", fiscalYear, originEntry.getUniversityFiscalYear());
        assertEquals("Origin Entry " + originEntry.toString() + " Fiscal Period", periodCode, originEntry.getUniversityFiscalPeriodCode());
        assertEquals("Origin Entry " + originEntry.toString() + " Chart of Accounts", chart, originEntry.getChartOfAccountsCode());
        assertEquals("Origin Entry " + originEntry.toString() + " Account Number", account, originEntry.getAccountNumber());
        assertEquals("Origin Entry " + originEntry.toString() + " Object Code", objectCode, originEntry.getFinancialObjectCode());
        assertEquals("Origin Entry " + originEntry.toString() + " Balance Type", balanceType, originEntry.getFinancialBalanceTypeCode());
        assertEquals("Origin Entry " + originEntry.toString() + " Object Type", objectType, originEntry.getFinancialObjectTypeCode());
        assertEquals("Origin Entry " + originEntry.toString() + " Amount", amount, originEntry.getTransactionLedgerEntryAmount());
    }

    /**
     * Takes all of the origin entries in an Iterator of entries and puts them into a List, for easier
     * manipulation.
     * 
     * @param originEntriesIterator an Iterator of origin entries
     * @return all of the entries in a list
     */
    private List<OriginEntryFull> putOriginEntriesInList(Iterator<OriginEntryFull> originEntriesIterator) {
        List<OriginEntryFull> entries = new ArrayList<OriginEntryFull>();
        while (originEntriesIterator.hasNext()) {
            entries.add(originEntriesIterator.next());
        }
        return entries;
    }

    /**
     * Logs all the entries in a list of origin entries
     * 
     * @param originEntries the origin entries to log
     */
    private void logAllEntries(List<? extends OriginEntryInformation> originEntries) {
        for (OriginEntryInformation entry : originEntries) {
            LOG.info(entry.getLine());
        }
    }

    /*
     * ************** SCENARIO 1 *****************
     */
    /**
     * Tests that Logic A generates the correct origin entries for Scenario 1, budget exceeds actual
     */
    public void testBudgetGreaterThanActualNoEncumbrancesLogicA() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();
        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(2), new Integer(generatedEntries.size()));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic C1 generates the correct origin entries for Scenario 1, budget exceeds actual
     */
    public void testBudgetGreaterThanActualNoEncumbrancesLogicC1() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic C2 generates the correct origin entries for Scenario 1, budget exceeds actual
     */
    public void testBudgetGreaterThanActualNoEncumbrancesLogicC2() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic N1 generates the correct origin entries for Scenario 1, budget exceeds actual
     */
    public void testBudgetGreaterThanActualNoEncumbrancesLogicN1() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic N2 generates the correct origin entries for Scenario 1, budget exceeds actual
     */
    public void testBudgetGreaterThanActualNoEncumbrancesLogicN2() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic R1 generates the correct origin entries for Scenario 1, budget exceeds actual
     */
    public void testBudgetGreaterThanActualNoEncumbrancesLogicR1() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic R2 generates the correct origin entries for Scenario 1, budget exceeds actual
     */
    public void testBudgetGreaterThanActualNoEncumbrancesLogicR2() {
        LOG.info("budget greater than actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO1_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO1_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /*
     * ************** SCENARIO 2 *****************
     */
    /**
     * Tests that Logic A generates the correct origin entries for Scenario 2, actual exceeds budget
     */
    public void testBudgetLessThanActualNoEncumbrancesALogic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic C1 generates the correct origin entries for Scenario 2, actual exceeds budget
     */
    public void testBudgetLessThanActualNoEncumbrancesC1Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic C2 generates the correct origin entries for Scenario 2, actual exceeds budget
     */
    public void testBudgetLessThanActualNoEncumbrancesC2Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic N1 generates the correct origin entries for Scenario 2, actual exceeds budget
     */
    public void testBudgetLessThanActualNoEncumbrancesN1Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, "1031400", "5000", "CB", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic N2 generates the correct origin entries for Scenario 2, actual exceeds budget
     */
    public void testBudgetLessThanActualNoEncumbrancesN2Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic R1 generates the correct origin entries for Scenario 2, actual exceeds budget
     */
    public void testBudgetLessThanActualNoEncumbrancesR1Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic R2 generates the correct origin entries for Scenario 2, actual exceeds budget
     */
    public void testBudgetLessThanActualNoEncumbrancesR2Logic() {
        LOG.info("actual greater than budget, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO2_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO2_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    /*
     * ************** SCENARIO 3 *****************
     */
    /**
     * Tests that Logic A generates the correct origin entries for Scenario 3, budget equals actual
     */
    public void testBudgetEqualsActualNoEncumbrancesALogic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(0));
    }

    /**
     * Tests that Logic C1 generates the correct origin entries for Scenario 3, budget equals actual
     */
    public void testBudgetEqualsActualNoEncumbrancesC1Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(0));
    }

    /**
     * Tests that Logic C2 generates the correct origin entries for Scenario 3, budget equals actual
     */
    public void testBudgetEqualsActualNoEncumbrancesC2Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(0));
    }

    /**
     * Tests that Logic N1 generates the correct origin entries for Scenario 3, budget equals actual
     */
    public void testBudgetEqualsActualNoEncumbrancesN1Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(0));
    }

    /**
     * Tests that Logic N2 generates the correct origin entries for Scenario 3, budget equals actual
     */
    public void testBudgetEqualsActualNoEncumbrancesN2Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(0));
    }

    /**
     * Tests that Logic R1 generates the correct origin entries for Scenario 3, budget equals actual
     */
    public void testBudgetEqualsActualNoEncumbrancesR1Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(0));
    }

    /**
     * Tests that Logic R2 generates the correct origin entries for Scenario 3, budget equals actual
     */
    public void testBudgetEqualsActualNoEncumbrancesR2Logic() {
        LOG.info("budget equals actual, no encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO3_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO3_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);
        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(0));
    }

    /*
     * ************** SCENARIO 4 *****************
     */
    /**
     * Tests that Logic A generates the correct origin entries for Scenario 4, budget exceeds actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicA() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic C1 generates the correct origin entries for Scenario 4, budget exceeds actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicC1() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic C2 generates the correct origin entries for Scenario 4, budget exceeds actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicC2() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic N1 generates the correct origin entries for Scenario 4, budget exceeds actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicN1() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(4));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-1500));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(1500));
        assertOriginEntry(generatedEntries.get(2), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(500));
        assertOriginEntry(generatedEntries.get(3), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(500));
    }

    /**
     * Tests that Logic N2 generates the correct origin entries for Scenario 4, budget exceeds actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicN2() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic R1 generates the correct origin entries for Scenario 4, budget exceeds actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicR1() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(4));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-1500));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(1500));
        assertOriginEntry(generatedEntries.get(2), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(500));
        assertOriginEntry(generatedEntries.get(3), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(500));
    }

    /**
     * Tests that LogicR2 generates the correct origin entries for Scenario 4, budget exceeds actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceGreaterThanEncumbrancesLogicR2() {
        LOG.info("budget greater than actual, variance greater than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO4_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO4_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO4_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /*
     * ************** SCENARIO 5 *****************
     */
    /**
     * Tests that Logic A generates the correct origin entries for Scenario 5, budget equals actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicA() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic C1 generates the correct origin entries for Scenario 5, budget equals actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicC1() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic C2 generates the correct origin entries for Scenario 5, budget equals actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicC2() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic N1 generates the correct origin entries for Scenario 5, budget equals actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicN1() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic N2 generates the correct origin entries for Scenario 5, budget equals actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicN2() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic R1 generates the correct origin entries for Scenario 5, budget equals actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicR1() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic R2 generates the correct origin entries for Scenario 5, budget equals actual + encumbrance
     */
    public void testBudgetGreaterThanActualVarianceEqualsEncumbrancesLogicR2() {
        LOG.info("budget greater than actual, variance equals encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO5_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO5_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO5_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /*
     * ************** SCENARIO 6 *****************
     */
    /**
     * Tests that Logic A generates the correct origin entries for Scenario 6, Actual + encumbrance exceeds budget
     */
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicA() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic C1 generates the correct origin entries for Scenario 6, Actual + encumbrance exceeds budget
     */
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicC1() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic C2 generates the correct origin entries for Scenario 6, Actual + encumbrance exceeds budget
     */
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicC2() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic N1 generates the correct origin entries for Scenario 6, Actual + encumbrance exceeds budget
     */
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicN1() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic N2 generates the correct origin entries for Scenario 6, Actual + encumbrance exceeds budget
     */
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicN2() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic R1 generates the correct origin entries for Scenario 6, Actual + encumbrance exceeds budget
     */
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicR1() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(2000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(2000));
    }

    /**
     * Tests that Logic R2 generates the correct origin entries for Scenario 6, Actual + encumbrance exceeds budget
     */
    public void testBudgetGreaterThanActualVariancesLessThanEncumbrancesLogicR2() {
        LOG.info("budget great than actual, variance less than encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO6_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO6_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO6_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(-2000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(2000));
    }

    /*
     * ************** SCENARIO 7 *****************
     */
    /**
     * Tests that Logic A generates the correct origin entries for Scenario 7, Actual exceeds budget, and there's an encumbrance
     */
    public void testActualGreaterThanBudgetWithEncumbrancesLogicA() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check A
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C01_ORG_WAGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic C1 generates the correct origin entries for Scenario 7, Actual exceeds budget, and there's an encumbrance
     */
    public void testActualGreaterThanBudgetWithEncumbrancesLogicC1() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C02_SALARY_FRINGES_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic C2 generates the correct origin entries for Scenario 7, Actual exceeds budget, and there's an encumbrance
     */
    public void testActualGreaterThanBudgetWithEncumbrancesLogicC2() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check C2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C03_FINANCIAL_AID_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic N1 generates the correct origin entries for Scenario 7, Actual exceeds budget, and there's an encumbrance
     */
    public void testActualGreaterThanBudgetWithEncumbrancesLogicN1() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C04_CAPITAL_EQUIP_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic N2 generates the correct origin entries for Scenario 7, Actual exceeds budget, and there's an encumbrance
     */
    public void testActualGreaterThanBudgetWithEncumbrancesLogicN2() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check N2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C05_RESERVE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "0110", "CB", "IN", new KualiDecimal(-3000));
        assertOriginEntry(generatedEntries.get(1), currentFiscalYear, "01", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "5000", "CB", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic R1 generates the correct origin entries for Scenario 7, Actual exceeds budget, and there's an encumbrance
     */
    public void testActualGreaterThanBudgetWithEncumbrancesLogicR1() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R1
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C08_TRAVEL_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }

    /**
     * Tests that Logic R2 generates the correct origin entries for Scenario 7, Actual exceeds budget, and there's an encumbrance
     */
    public void testActualGreaterThanBudgetWithEncumbrancesLogicR2() {
        LOG.info("actual greater than budget, with encumbrances test started...");
        List<Balance> balancesToCheck = new ArrayList<Balance>();

        // add balances to check R2
        Balance cbBalance = BALANCE_FIXTURE.SCENARIO7_CURRENT_BUDGET_BALANCE.convertToBalance();
        cbBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance acBalance = BALANCE_FIXTURE.SCENARIO7_ACTUAL_BALANCE.convertToBalance();
        acBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        Balance encumbranceBalance = BALANCE_FIXTURE.SCENARIO7_ENCUMBRANCE_BALANCE.convertToBalance();
        encumbranceBalance.setObjectCode(OBJECT_CODE_FIXTURE.C09_OTHER_EXPENSE_CODE.getCode());
        balancesToCheck.add(cbBalance);
        balancesToCheck.add(acBalance);
        balancesToCheck.add(encumbranceBalance);

        List<? extends OriginEntryInformation> generatedEntries = runOrganizationReversionProcess(balancesToCheck);

        // sanity check - all the balances were selected?
        assertEquals("balances to check were all selected? ", new Integer(balancesToCheck.size()), new Integer(orgRevProcess.getBalancesSelected()));

        
        logAllEntries(generatedEntries);

        assertEquals("correct number of origin entries returned? ", new Integer(generatedEntries.size()), new Integer(2));

        // check the origin entries
        assertOriginEntry(generatedEntries.get(0), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, DEFAULT_BALANCE_ACCOUNT_NBR, "7900", "RE", "EX", new KualiDecimal(3000));
        assertOriginEntry(generatedEntries.get(1), previousFiscalYear, "13", DEFAULT_BALANCE_CHART, OrganizationReversionMockServiceImpl.DEFAULT_BUDGET_REVERSION_ACCOUNT, "7900", "RE", "EX", new KualiDecimal(-3000));
    }
}
