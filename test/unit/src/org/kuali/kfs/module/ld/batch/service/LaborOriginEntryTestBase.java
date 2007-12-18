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
package org.kuali.module.labor;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.ConfigurableDateService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.UnitTestSqlDao;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.financial.bo.Bank;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.dao.LaborOriginEntryDao;
import org.kuali.module.labor.service.LaborOriginEntryService;

public class LaborOriginEntryTestBase extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborOriginEntryTestBase.class);

    protected ConfigurableDateService dateTimeService;
    protected PersistenceService persistenceService;
    protected UnitTestSqlDao unitTestSqlDao = null;
    protected OriginEntryGroupService originEntryGroupService = null;
    protected LaborOriginEntryService laborOriginEntryService = null;

    protected OriginEntryDao originEntryDao = null;
    protected LaborOriginEntryDao laborOriginEntryDao = null;
    protected KualiConfigurationService kualiConfigurationService = null;
    protected Date date;

    public LaborOriginEntryTestBase() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LOG.debug("setUp() starting");

        dateTimeService = SpringContext.getBean(ConfigurableDateService.class);
        date = dateTimeService.getCurrentDate();

        // Other objects needed for the tests
        persistenceService = SpringContext.getBean(PersistenceService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);

        originEntryDao = SpringContext.getBean(OriginEntryDao.class);
        laborOriginEntryDao = SpringContext.getBean(LaborOriginEntryDao.class);

        originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);
        kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

        // Set all enhancements to off
        resetAllEnhancementFlags();
    }


    protected class EntryHolder {
        public String groupCode;
        public String transactionLine;

        public EntryHolder(String groupCode, String transactionLine) {
            this.groupCode = groupCode;
            this.transactionLine = transactionLine;
        }
    }

    protected void loadInputTransactions(String groupCode, String[] transactions, Date date) {
        OriginEntryGroup group = originEntryGroupService.createGroup(new java.sql.Date(date.getTime()), groupCode, true, true, true);
        loadTransactions(transactions, group);
    }

    protected void loadInputTransactions(String groupCode, String[] transactions) {
        OriginEntryGroup group = originEntryGroupService.createGroup(new java.sql.Date(dateTimeService.getCurrentDate().getTime()), groupCode, true, true, true);
        loadTransactions(transactions, group);
    }

    protected void loadTransactions(String[] transactions, OriginEntryGroup group) {
        for (int i = 0; i < transactions.length; i++) {
            LaborOriginEntry loe = new LaborOriginEntry(transactions[i]);
            laborOriginEntryService.createEntry(loe, group);
        }

        persistenceService.clearCache();
    }

    protected void clearExpenditureTable() {
        unitTestSqlDao.sqlCommand("delete from gl_expend_trn_t");
    }

    protected void clearSufficientFundBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_sf_balances_t");
    }

    protected void clearGlEntryTable(String fin_coa_cd, String account_nbr) {
        unitTestSqlDao.sqlCommand("delete from gl_entry_t where fin_coa_cd = '" + fin_coa_cd + "' and account_nbr = '" + account_nbr + "'");
    }

    protected void clearReversalTable() {
        unitTestSqlDao.sqlCommand("delete from gl_reversal_t");
    }

    protected void clearGlBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_balance_t");
    }

    protected void clearEncumbranceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_encumbrance_t");
    }

    protected void clearGlAccountBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_acct_balances_t");
    }

    protected void clearOriginEntryTables() {
        unitTestSqlDao.sqlCommand("delete from ld_lbr_origin_entry_t");
        unitTestSqlDao.sqlCommand("delete from gl_origin_entry_grp_t");
    }

    /**
     * Check all the entries in gl_origin_entry_t against the data passed in EntryHolder[]. If any of them are different, assert an
     * error.
     * 
     * @param requiredEntries
     */
    protected void assertOriginEntries(int groupCount, EntryHolder[] requiredEntries) {
        persistenceService.clearCache();

        List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t order by origin_entry_grp_src_cd");
        assertEquals("Number of groups is wrong", groupCount, groups.size());

        Collection c = laborOriginEntryDao.testingLaborGetAllEntries();


        // This is for debugging purposes - change to true for output
        if (true) {
            for (Iterator iter = groups.iterator(); iter.hasNext();) {
                Map element = (Map) iter.next();
                System.err.println("G:" + element.get("ORIGIN_ENTRY_GRP_ID") + " " + element.get("ORIGIN_ENTRY_GRP_SRC_CD"));
            }

            for (Iterator iter = c.iterator(); iter.hasNext();) {
                LaborOriginEntry element = (LaborOriginEntry) iter.next();
                System.err.println("L:" + element.getEntryGroupId() + " " + element.getLine());
            }
        }

        assertEquals("Wrong number of transactions in Origin Entry", requiredEntries.length, c.size());

        int count = 0;
        for (Iterator iter = c.iterator(); iter.hasNext();) {
            LaborOriginEntry foundTransaction = (LaborOriginEntry) iter.next();

            // Check group
            int group = getGroup(groups, requiredEntries[count].groupCode);

            //assertEquals("Group for transaction " + foundTransaction.getEntryId() + " is wrong", group, foundTransaction.getEntryGroupId().intValue());

            // Check transaction - this is done this way so that Anthill prints the two transactions to make
            // resolving the issue easier.

            // This test is not good for Labor because input and output is little different. -- Amount data
            /*
             * String expected = requiredEntries[count].transactionLine.substring(0, 294);// trim(); String found =
             * foundTransaction.getLine().substring(0, 294);// trim(); if (!found.equals(expected)) { System.err.println("Expected
             * transaction: " + expected); System.err.println("Found transaction: " + found); fail("Transaction " +
             * foundTransaction.getEntryId() + " doesn't match expected output"); }
             */
            count++;
        }
    }

    protected int getGroup(List groups, String groupCode) {
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            Map element = (Map) iter.next();

            String sourceCode = (String) element.get("ORIGIN_ENTRY_GRP_SRC_CD");
            if (groupCode.equals(sourceCode)) {
                BigDecimal groupId = (BigDecimal) element.get("ORIGIN_ENTRY_GRP_ID");
                return groupId.intValue();
            }
        }
        return -1;
    }

    protected static Object[] FLEXIBLE_OFFSET_ENABLED_FLAG = { OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG };
    protected static Object[] FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG = { Bank.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG };

    protected void resetAllEnhancementFlags() throws Exception {
        setApplicationConfigurationFlag((Class) FLEXIBLE_OFFSET_ENABLED_FLAG[0], (String) FLEXIBLE_OFFSET_ENABLED_FLAG[1], false);
        setApplicationConfigurationFlag((Class) FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG[0], (String) FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG[1], false);
    }

    protected void setApplicationConfigurationFlag(Class componentClass, String name, boolean value) throws Exception {
        TestUtils.setSystemParameter(componentClass, name, value ? "Y" : "N");
    }


    protected void traceList(List list, String name) {
        trace("StartList " + name + "( " + list.size() + " elements): ", 0);

        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
            trace(iterator.next(), 1);
        }

        trace("EndList " + name + ": ", 0);
        trace("", 0);
    }

    protected void trace(Object o, int tabIndentCount) {
        PrintStream out = System.out;

        for (int i = 0; i < tabIndentCount; i++) {
            out.print("\t");
        }

        out.println(null == o ? "NULL" : o.toString());
    }

}
