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
package org.kuali.module.labor.dao.ojb;

import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.APRIL;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.AUGUST;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.DECEMBER;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.FEBRUARY;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.JANUARY;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.JULY;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.JUNE;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.MARCH;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.MAY;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.NOVEMBER;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.OCTOBER;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.SEPTEMBER;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.YEAR_END;
import static org.kuali.module.labor.util.ConsolidationUtil.buildConsolidatedQuery;
import static org.kuali.module.labor.util.ConsolidationUtil.buildGroupByCollection;
import static org.kuali.module.labor.util.ConsolidationUtil.sum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.chart.service.ObjectTypeService;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.EmployeeFunding;
import org.kuali.module.labor.bo.LaborBalanceSummary;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.dao.LaborLedgerBalanceDao;
import org.kuali.module.labor.util.ConsolidationUtil;
import org.kuali.module.labor.util.ObjectUtil;

/**
 * This is the data access object for ledger balance.
 * 
 * @see org.kuali.module.labor.bo.LedgerBalance
 */
public class LaborLedgerBalanceDaoOjb extends PlatformAwareDaoBaseOjb implements LaborLedgerBalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerBalanceDaoOjb.class);
    private KualiConfigurationService kualiConfigurationService;

    private BalanceTypService balanceTypService;
    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer year) {
        LOG.debug("findBalancesForFiscalYear() started");

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        QueryByCriteria query = QueryFactory.newQuery(LedgerBalance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }
    
    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findBalancesForFiscalYear(java.lang.Integer, java.util.Map)
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues) {

        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);

        QueryByCriteria query = QueryFactory.newQuery(LedgerBalance.class, criteria);
        
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findBalance(java.util.Map, boolean)
     */
    public Iterator<LedgerBalance> findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");

        Query query = this.getBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#getConsolidatedBalanceRecordCount(java.util.Map)
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues) {
        LOG.debug("getBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // build the query for balance search
    private Query getBalanceQuery(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getBalanceQuery(Map, boolean) started");
        LOG.debug("Building criteria from map fields: " + fieldValues.keySet());

        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            buildConsolidatedQuery(query, sum(JULY.propertyName), sum(AUGUST.propertyName), sum(SEPTEMBER.propertyName), sum(OCTOBER.propertyName), sum(NOVEMBER.propertyName), sum(DECEMBER.propertyName), sum(JANUARY.propertyName), sum(FEBRUARY.propertyName), sum(MARCH.propertyName), sum(APRIL.propertyName), sum(MAY.propertyName), sum(JUNE.propertyName), sum(YEAR_END.propertyName));
        }

        return query;
    }

    // build the query for balance search
    private ReportQueryByCriteria getBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        Collection<String> groupByList = buildGroupByCollection();
        groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.remove(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);
        return query;
    }

    /**
     * This method builds the query criteria based on the input field map
     * 
     * @param fieldValues
     * @param balance
     * @return a query criteria
     */
    private Criteria buildCriteriaFromMap(Map fieldValues, LedgerBalance balance) {
        Map localFieldValues = new HashMap();
        localFieldValues.putAll(fieldValues);

        Criteria criteria = new Criteria();

        // handle encumbrance balance type
        String propertyName = KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) localFieldValues.get(propertyName);
            if (KFSConstants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
                
                // parse the university fiscal year since it's a required field from the lookups
                String universityFiscalYearStr = (String) localFieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
                Integer universityFiscalYear = new Integer(universityFiscalYearStr);

                criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypService.getEncumbranceBalanceTypes(universityFiscalYear));
            }
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, new LedgerBalance()));
        return criteria;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findCurrentFunds(java.util.Map)
     */
    public List<LedgerBalance> findCurrentFunds(Map fieldValues) {
        LOG.debug("Start findCurrentFunds()");

        Iterator<Object[]> queryResults = this.findCurrentFundsRawData(fieldValues);
        List<LedgerBalance> currentFundsCollection = new ArrayList<LedgerBalance>();
        while (queryResults != null && queryResults.hasNext()) {
            currentFundsCollection.add(this.marshalFundsAsLedgerBalance(queryResults.next()));
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findEncumbranceFunds(java.util.Map)
     */
    public List<LedgerBalance> findEncumbranceFunds(Map fieldValues) {
        LOG.debug("Start findEncumbranceFunds()");

        Iterator<Object[]> queryResults = this.findEncumbranceFundsRawData(fieldValues);
        List<LedgerBalance> currentFundsCollection = new ArrayList<LedgerBalance>();
        while (queryResults != null && queryResults.hasNext()) {
            currentFundsCollection.add(this.marshalFundsAsLedgerBalance(queryResults.next()));
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findCurrentEmployeeFunds(java.util.Map)
     */
    public List<EmployeeFunding> findCurrentEmployeeFunds(Map fieldValues) {
        LOG.debug("Start findCurrentEmployeeFunds()");

        Iterator<Object[]> queryResults = this.findCurrentFundsRawData(fieldValues);
        List<EmployeeFunding> currentFundsCollection = new ArrayList<EmployeeFunding>();
        while (queryResults != null && queryResults.hasNext()) {
            currentFundsCollection.add(this.marshalFundsAsEmployeeFunding(queryResults.next()));
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findEncumbranceEmployeeFunds(java.util.Map)
     */
    public List<EmployeeFunding> findEncumbranceEmployeeFunds(Map fieldValues) {
        LOG.debug("Start findCurrentEmployeeFunds()");

        Iterator<Object[]> queryResults = this.findEncumbranceFundsRawData(fieldValues);
        List<EmployeeFunding> currentFundsCollection = new ArrayList<EmployeeFunding>();
        while (queryResults != null && queryResults.hasNext()) {
            currentFundsCollection.add(this.marshalFundsAsEmployeeFunding(queryResults.next()));
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findBalanceSummary(java.lang.Integer, java.util.Collection)
     */
    public List<LaborBalanceSummary> findBalanceSummary(Integer fiscalYear, Collection<String> balanceTypes) {
        LOG.debug("Start findBalanceSummary()");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypes);

        Iterator<Object[]> queryResults = this.findBalanceSummaryRawData(criteria);
        List<LaborBalanceSummary> balanceSummaryCollection = new ArrayList<LaborBalanceSummary>();
        while (queryResults != null && queryResults.hasNext()) {
            balanceSummaryCollection.add(this.marshalFundsAsLaborBalanceSummary(queryResults.next()));
        }
        return balanceSummaryCollection;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#save(org.kuali.module.labor.bo.LedgerBalance)
     */
    public void save(LedgerBalance ledgerBalance) {
        getPersistenceBrokerTemplate().store(ledgerBalance);
    }

    // get the current funds according to the given criteria
    private Iterator<Object[]> findCurrentFundsRawData(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LedgerBalance());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);

        List<String> objectTypeCodes = new ArrayList<String>();
        objectTypeCodes.add(LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE);
        objectTypeCodes.add(LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE);
        criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, objectTypeCodes);

        return this.findFundsRawData(criteria);
    }

    // get the encumbrance funds according to the given criteria
    private Iterator<Object[]> findEncumbranceFundsRawData(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LedgerBalance());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_INTERNAL_ENCUMBRANCE);

        return this.findFundsRawData(criteria);
    }

    // get the funds based on the given criteria
    private Iterator<Object[]> findFundsRawData(Criteria criteria) {
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        List<String> groupByList = this.getGroupByListForFundingInquiry();
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        List<String> getAttributeList = getAttributeListForFundingInquiry(false);
        String[] attributes = (String[]) getAttributeList.toArray(new String[getAttributeList.size()]);
        query.setAttributes(attributes);

        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // get the balance summary based on the given criteria
    private Iterator<Object[]> findBalanceSummaryRawData(Criteria criteria) {
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        List<String> groupByList = this.getGroupByListForBalanceSummary();
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        List<String> getAttributeList = this.getAttributeListForBalanceSummary(false);
        String[] attributes = (String[]) getAttributeList.toArray(new String[getAttributeList.size()]);
        query.setAttributes(attributes);

        query.addOrderByAscending(groupByList.get(0));
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // marshal into AccountStatusBaseFunds from the query result
    private LedgerBalance marshalFundsAsLedgerBalance(Object[] queryResult) {
        LedgerBalance ledgerBalance = new LedgerBalance();
        List<String> keyFields = this.getAttributeListForFundingInquiry(true);

        ObjectUtil.buildObject(ledgerBalance, queryResult, keyFields);
        return ledgerBalance;
    }

    // marshal into AccountStatusBaseFunds from the query result
    private EmployeeFunding marshalFundsAsEmployeeFunding(Object[] queryResult) {
        EmployeeFunding employeeFunding = new EmployeeFunding();
        List<String> keyFields = this.getAttributeListForFundingInquiry(true);

        ObjectUtil.buildObject(employeeFunding, queryResult, keyFields);
        return employeeFunding;
    }

    // marshal into AccountStatusBaseFunds from the query result
    private LaborBalanceSummary marshalFundsAsLaborBalanceSummary(Object[] queryResult) {
        return new LaborBalanceSummary(queryResult);
    }

    // define the attribute list that can be used to group the search results
    private List<String> getGroupByListForFundingInquiry() {
        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        groupByList.add(KFSPropertyConstants.POSITION_NUMBER);
        groupByList.add(KFSPropertyConstants.EMPLID);
        return groupByList;
    }

    // define the return attribute list for funding query
    private List<String> getAttributeListForFundingInquiry(boolean isAttributeNameNeeded) {
        List<String> attributeList = getGroupByListForFundingInquiry();
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.FINANCIAL_BEGINNING_BALANCE_LINE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT, isAttributeNameNeeded));
        return attributeList;
    }

    // define the attribute list that can be used to group the search results
    private List<String> getGroupByListForBalanceSummary() {
        List<String> groupByList = new ArrayList<String>();
        groupByList.add("account.subFundGroup.fundGroupCode");
        return groupByList;
    }

    // define the return attribute list for balance summary
    private List<String> getAttributeListForBalanceSummary(boolean isAttributeNameNeeded) {
        List<String> attributeList = getGroupByListForBalanceSummary();
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.FINANCIAL_BEGINNING_BALANCE_LINE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH1_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH2_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH3_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH4_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH5_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH6_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH7_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH8_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH9_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH10_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH11_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH12_AMOUNT, isAttributeNameNeeded));
        attributeList.add(ConsolidationUtil.wrapAttributeName(KFSPropertyConstants.MONTH13_AMOUNT, isAttributeNameNeeded));
        return attributeList;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBalanceTypService(BalanceTypService balanceTypService) {
        this.balanceTypService = balanceTypService;
    }
}