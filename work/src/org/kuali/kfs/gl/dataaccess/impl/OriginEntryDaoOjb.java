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
package org.kuali.module.gl.dao.ojb;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

public class OriginEntryDaoOjb extends PersistenceBrokerDaoSupport implements OriginEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryDaoOjb.class);

    private static final String ENTRY_GROUP_ID = "entryGroupId";
    private static final String ENTRY_ID = "entryId";
    private static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
    private static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
    private static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";
    private static final String FINANCIAL_DOCUMENT_NUMBER = "financialDocumentNumber";
    private static final String FINANCIAL_DOCUMENT_REVERSAL_DATE = "financialDocumentReversalDate";
    private static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
    private static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    private static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
    private static final String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
    private static final String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
    private static final String TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER = "transactionLedgerEntrySequenceNumber";
    private static final String TRANSACTION_LEDGER_ENTRY_DESCRIPTION = "transactionLedgerEntryDescription";
    private static final String TRANSACTION_LEDGER_ENTRY_AMOUNT = "transactionLedgerEntryAmount";
    private static final String TRANSACTION_DEBIT_CREDIT_CODE = "transactionDebitCreditCode";


    /**
     * 
     */
    public OriginEntryDaoOjb() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getGroupCounts()
     */
    public Iterator getGroupCounts() {
        LOG.debug("getGroupCounts() started");

        Criteria crit = new Criteria();

        ReportQueryByCriteria q = QueryFactory.newReportQuery(OriginEntry.class, crit);
        q.setAttributes(new String[] { ENTRY_GROUP_ID, "count(*)" });
        q.addGroupBy(ENTRY_GROUP_ID);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#deleteEntry(org.kuali.module.gl.bo.OriginEntry)
     */
    public void deleteEntry(OriginEntry oe) {
        LOG.debug("deleteEntry() started");

        getPersistenceBrokerTemplate().delete(oe);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getDocumentsByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator getDocumentsByGroup(OriginEntryGroup oeg) {
        LOG.debug("getDocumentsByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(ENTRY_GROUP_ID, oeg.getId());

        ReportQueryByCriteria q = QueryFactory.newReportQuery(OriginEntry.class, criteria);
        q.setAttributes(new String[] { "financialDocumentNumber","financialDocumentTypeCode","financialSystemOriginationCode" });

        q.setDistinct(true);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getMatchingEntries(java.util.Map)
     */
    public Iterator<OriginEntry> getMatchingEntries(Map searchCriteria) {
        LOG.debug("getMatchingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        qbc.addOrderByAscending(ENTRY_GROUP_ID);
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    public Iterator<OriginEntry> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");


        Collection ids = new ArrayList();
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();
            ids.add(element.getId());
        }

        Criteria crit1 = new Criteria();
        crit1.addIn(ENTRY_GROUP_ID, ids);

        Criteria crit2 = new Criteria();
        crit2.addIsNull(FINANCIAL_BALANCE_TYPE_CODE);

        Criteria crit3 = new Criteria();
        crit3.addEqualTo(FINANCIAL_BALANCE_TYPE_CODE, "  ");

        crit2.addOrCriteria(crit3);

        crit1.addAndCriteria(crit2);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, crit1);
        qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
        qbc.addOrderByAscending(ACCOUNT_NUMBER);
        qbc.addOrderByAscending(SUB_ACCOUNT_NUMBER);

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * This method is special because of the order by. It is used in the scrubber. The getMatchingEntries wouldn't work because of
     * the required order by.
     * 
     */
    public Iterator<OriginEntry> getEntriesByGroup(OriginEntryGroup oeg, int sort) {
        LOG.debug("getEntriesByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(ENTRY_GROUP_ID, oeg.getId());

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);

        if (sort == OriginEntryDao.SORT_DOCUMENT) {
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_NUMBER);
            qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(ACCOUNT_NUMBER);
            qbc.addOrderByAscending(SUB_ACCOUNT_NUMBER);
            qbc.addOrderByAscending(FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_REVERSAL_DATE);
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_YEAR);
            // The above order by fields are required by the scrubber process. Adding these
            // fields makes the data in the exact same order as the COBOL scrubber.
            qbc.addOrderByAscending(FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(FINANCIAL_SUB_OBJECT_CODE);
            qbc.addOrderByAscending(FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_DESCRIPTION);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_AMOUNT);
            qbc.addOrderByAscending(TRANSACTION_DEBIT_CREDIT_CODE);
        } else if (sort == OriginEntryDao.SORT_REPORT ) {
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_DEBIT_CREDIT_CODE);            
            qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(ACCOUNT_NUMBER);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_CODE);
        } else {
            qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(ACCOUNT_NUMBER);
            qbc.addOrderByAscending(SUB_ACCOUNT_NUMBER);
        }

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * This method should only be used in unit tests. It loads all the gl_origin_entry_t rows in memory into a collection. This
     * won't work for production because there would be too many rows to load into memory.
     * 
     * @return
     */
    public Collection<OriginEntry> testingGetAllEntries() {
        LOG.debug("testingGetAllEntries() started");

        Criteria criteria = new Criteria();
        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        qbc.addOrderByAscending(ENTRY_GROUP_ID);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @param entry the entry to save.
     */
    public void saveOriginEntry(OriginEntry entry) {
        LOG.debug("saveOriginEntry() started");

        if ((entry != null) && (entry.getTransactionLedgerEntryDescription() != null) && (entry.getTransactionLedgerEntryDescription().length() > 40)) {
            entry.setTransactionLedgerEntryDescription(entry.getTransactionLedgerEntryDescription().substring(0, 40));
        }
        getPersistenceBrokerTemplate().store(entry);
    }

    /**
     * Delete entries matching searchCriteria search criteria.
     * 
     * @param searchCriteria
     */
    public void deleteMatchingEntries(Map searchCriteria) {
        LOG.debug("deleteMatchingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);

        // This is required because deleteByQuery leaves the cache alone so future queries
        // could return origin entries that don't exist. Clearing the cache makes OJB
        // go back to the database for everything to make sure valid data is returned.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#deleteGroups(java.util.Collection)
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups) {
        LOG.debug("deleteGroups() started");

        List ids = new ArrayList();
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();
            ids.add(element.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(ENTRY_GROUP_ID, ids);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);

        // This is required because deleteByQuery leaves the cache alone so future queries
        // could return origin entries that don't exist. Clearing the cache makes OJB
        // go back to the database for everything to make sure valid data is returned.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getMatchingEntriesByCollection(java.util.Map)
     */
    public Collection<OriginEntry> getMatchingEntriesByCollection(Map searchCriteria) {
        LOG.debug("getMatchingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        qbc.addOrderByAscending(ENTRY_GROUP_ID);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.module.gl.dao.OriginEntryDao#getSummaryByGroupId(java.util.List)
     */
    public Iterator getSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getSummaryByGroupId() started");

        Collection ids = new ArrayList();
        for (Iterator iter = groupIdList.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();
            ids.add(element.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(PropertyConstants.ENTRY_GROUP_ID, ids);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(OriginEntry.class, criteria);

        String attributeList[] = { PropertyConstants.UNIVERSITY_FISCAL_YEAR, PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, "sum(" + PropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")", "count(" + PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE + ")" };

        String groupList[] = { PropertyConstants.UNIVERSITY_FISCAL_YEAR, PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE };

        query.setAttributes(attributeList);
        query.addGroupBy(groupList);

        // add the sorting criteria
        for (int i = 0; i < groupList.length; i++) {
            query.addOrderByAscending(groupList[i]);
        }

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    public OriginEntry getExactMatchingEntry(Integer entryId) {
        LOG.debug("getMatchingEntries() started");
        OriginEntry oe = new OriginEntry();
        // in case of no matching entry
        try {
            oe = (OriginEntry) getPersistenceBrokerTemplate().getObjectById(OriginEntry.class, entryId);

        }
        catch (Exception e) {
        }

        return oe;
    }
}
