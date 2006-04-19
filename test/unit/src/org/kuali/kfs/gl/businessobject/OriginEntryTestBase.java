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
package org.kuali.module.gl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.test.KualiTestBaseWithSpring;
import org.springframework.beans.factory.BeanFactory;

public class OriginEntryTestBase extends KualiTestBaseWithSpring {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryTestBase.class);

  protected BeanFactory beanFactory;
  protected TestDateTimeService dateTimeService;
  protected PersistenceService persistenceService;
  protected UnitTestSqlDao unitTestSqlDao = null;
  protected OriginEntryDao originEntryDao = null;
  protected OriginEntryGroupDao originEntryGroupDao = null;
  protected KualiConfigurationService kualiConfigurationService = null;
  protected Date date = new Date();

  public OriginEntryTestBase() {
    super();
  }

  /* (non-Javadoc)
   * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
   */
  protected void setUp() throws Exception {
    super.setUp();

    LOG.debug("setUp() starting");

    beanFactory = SpringServiceLocator.getBeanFactory();

    dateTimeService = (TestDateTimeService)beanFactory.getBean("testDateTimeService");

    // Other objects needed for the tests
    persistenceService = (PersistenceService)beanFactory.getBean("persistenceService");
    unitTestSqlDao = (UnitTestSqlDao)beanFactory.getBean("glUnitTestSqlDao");
    originEntryDao = (OriginEntryDao)beanFactory.getBean("glOriginEntryDao");
    originEntryGroupDao = (OriginEntryGroupDao)beanFactory.getBean("glOriginEntryGroupDao");
    kualiConfigurationService = (KualiConfigurationService)beanFactory.getBean("kualiConfigurationService");

    // Set all enhancements to off
    resetAllEnhancementFlags();
  }


  protected class EntryHolder {
    public String groupCode;
    public String transactionLine;
    public EntryHolder(String groupCode,String transactionLine) {
      this.groupCode = groupCode;
      this.transactionLine = transactionLine;
    }
  }

  protected void loadInputTransactions(String groupCode,String[] transactions) {
    OriginEntryGroup group = createNewGroup(groupCode);
    loadTransactions(transactions,group);
  }

  protected void loadTransactions(String[] transactions, OriginEntryGroup group) {
    for (int i = 0; i < transactions.length; i++) {
        createEntry(transactions[i], group);
    }
  }

  protected void clearExpenditureTable() {
    unitTestSqlDao.sqlCommand("delete from gl_expend_trn_t");
  }

  protected void clearSufficientFundBalanceTable() {
    unitTestSqlDao.sqlCommand("delete from gl_sf_balances_t");    
  }

  protected void clearGlEntryTable(String fin_coa_cd,String account_nbr) {
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
    unitTestSqlDao.sqlCommand("delete from gl_origin_entry_t");
    unitTestSqlDao.sqlCommand("delete from gl_origin_entry_grp_t");
  }

  protected OriginEntryGroup createNewGroup(String code) {
    OriginEntryGroup group = new OriginEntryGroup();
    group.setDate(new java.sql.Date(date.getTime()));
    group.setProcess(Boolean.TRUE);
    group.setScrub(Boolean.TRUE);
    group.setValid(Boolean.TRUE);
    group.setSourceCode(code);
    originEntryGroupDao.save(group);
    return group;
  }

  protected OriginEntry createEntry(String line, OriginEntryGroup group) {
    OriginEntry entry = new OriginEntry(line);
    entry.setGroup(group);
    
    // This is being done to fool the caching.  If it isn't done, when
    // we try to retrieve this entry later, none of the referenced tables will
    // be loaded.
    persistenceService.retrieveNonKeyFields(entry);

    originEntryDao.saveOriginEntry(entry);
    return entry;
  }

  /**
   * Check all the entries in gl_origin_entry_t against the data passed in EntryHolder[].  If any of them
   * are different, assert an error.
   * 
   * @param requiredEntries
   */
  protected void assertOriginEntries(int groupCount, EntryHolder[] requiredEntries, Date runDateForOffsets, boolean isCostSharing) {
      
      List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t order by origin_entry_grp_src_cd");
      assertEquals("Number of groups is wrong", groupCount, groups.size());

      Collection c = originEntryDao.testingGetAllEntries();
      assertEquals("Wrong number of transactions in Origin Entry",requiredEntries.length,c.size());

      int count = 0;
      for (Iterator iter = c.iterator(); iter.hasNext();) {
        OriginEntry foundTransaction = (OriginEntry)iter.next();

        // Check group
        int group = getGroup(groups,requiredEntries[count].groupCode);
        assertEquals("Group for transaction " + foundTransaction.getEntryId() + " is wrong",group,foundTransaction.getEntryGroupId().intValue());

        // Check transaction - this is done this way so that Anthill prints the two transactions to make
        // resolving the issue easier.
        
        String expected = requiredEntries[count].transactionLine.substring(0, 173);//trim();
        String found    = foundTransaction.getLine().substring(0, 173);//trim();
        
        String desc = expected.substring(51, 92);
        // If it's an offset entry, adjusted the expected entry to expect the runDate for the transactionDate.
        if(null != runDateForOffsets) {
            
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String formattedRunDate = simpleDateFormat.format(runDateForOffsets);
            
            if("GENERATED OFFSET".equals(desc.trim())
                    || (isCostSharing
                            && (desc.startsWith("GENERATED ")
                                && !desc.startsWith("GENERATED CAPITALIZATION")))
                    || "FR-".equals(desc.substring(28, 31))
               ) {
                
                StringBuffer modifiedExpectation = new StringBuffer();
                modifiedExpectation.append(expected.substring(0, 109));
                
                // Splice in the current runDate.
                modifiedExpectation.append(formattedRunDate);
                
                modifiedExpectation.append(expected.substring(119));
                
                // .. and modify our expectations.
                expected = modifiedExpectation.toString();
                
            }
            
            // Document number on generated cost share entries includes the run date as the last
            // five characters of the document number.
            if(isCostSharing 
                    && desc.startsWith("GENERATED ") 
                    && !desc.startsWith("GENERATED CAPITALIZATION")) {
                
                simpleDateFormat = new SimpleDateFormat("MM/dd");
                formattedRunDate = simpleDateFormat.format(runDateForOffsets);
                
                StringBuffer modifiedExpectation = new StringBuffer();
                modifiedExpectation.append(expected.substring(0, 41));
                
                // Splice in the current runDate.
                modifiedExpectation.append(formattedRunDate);
                
                modifiedExpectation.append(expected.substring(46));
                
                // .. and modify our expectations.
                expected = modifiedExpectation.toString();
                
            }
            
        }
        
        if (!found.equals(expected)) {
          
          System.err.println("Expected transaction: " + expected);
          System.err.println("Found transaction:    " + found);
          
          fail("Transaction " + foundTransaction.getEntryId() + " doesn't match expected output");
          
        }
        count++;
      }
  }

  protected void assertOriginEntries(int groupCount, EntryHolder[] requiredEntries) {
      assertOriginEntries(groupCount, requiredEntries, null, false);
  }
  
  protected void assertOriginEntries(int groupCount, EntryHolder[] requiredEntries, Date runDateForOffsets) {
      
      assertOriginEntries(groupCount, requiredEntries, runDateForOffsets, false);
      
  }
  
  protected int getGroup(List groups,String groupCode) {
    for (Iterator iter = groups.iterator(); iter.hasNext();) {
      Map element = (Map)iter.next();

      String sourceCode = (String)element.get("ORIGIN_ENTRY_GRP_SRC_CD");
      if ( groupCode.equals(sourceCode) ) {
        BigDecimal groupId = (BigDecimal)element.get("ORIGIN_ENTRY_GRP_ID");
        return groupId.intValue();
      }
    }
    return -1;
  }

  protected static String BUDGET_YEAR_ENABLED_FLAG = "BUDGET_YEAR_ENABLED_FLAG";
  protected static String ICR_ENCUMBRANCE_ENABLED_FLAG = "ICR_ENCUMBRANCE_ENABLED_FLAG";
  protected static String FLEXIBLE_OFFSET_ENABLED_FLAG = "FLEXIBLE_OFFSET_ENABLED_FLAG";
  protected static String FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG = "FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG";    

  protected void resetAllEnhancementFlags() {
    setApplicationConfigurationFlag(OriginEntryTestBase.BUDGET_YEAR_ENABLED_FLAG,false);
    setApplicationConfigurationFlag(OriginEntryTestBase.ICR_ENCUMBRANCE_ENABLED_FLAG,false);
    setApplicationConfigurationFlag(OriginEntryTestBase.FLEXIBLE_OFFSET_ENABLED_FLAG,false);
    setApplicationConfigurationFlag(OriginEntryTestBase.FLEXIBLE_CLAIM_ON_CASH_BANK_ENABLED_FLAG,false);    
  }

  protected boolean getApplicationConfigurationFlag(String name) {
    return kualiConfigurationService.getRequiredApplicationParameterIndicator("SYSTEM",name);
  }

  protected void setApplicationConfigurationFlag(String name,boolean value) {
    unitTestSqlDao.sqlCommand("delete from fs_parm_t where fs_scr_nm = 'SYSTEM' and fs_parm_nm = '" + name + "'");
    unitTestSqlDao.sqlCommand("insert into fs_parm_t (fs_scr_nm,fs_parm_nm,obj_id,ver_nbr,fs_parm_txt,fs_parm_desc,fs_mult_val_ind," +
        "fs_parm_oper,fs_active_ind) values ('SYSTEM','" + name + "',SYS_GUID(),1,'" + (value ? "Y" : "N") + "','Y','N',null,'Y')");    
  }
}
