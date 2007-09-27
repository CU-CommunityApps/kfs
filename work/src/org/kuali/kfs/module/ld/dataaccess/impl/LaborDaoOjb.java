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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.gl.bo.Encumbrance;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.bo.AccountStatusCurrentFunds;
import org.kuali.module.labor.bo.EmployeeFunding;
import org.kuali.module.labor.bo.July1PositionFunding;
import org.kuali.module.labor.bo.LaborCalculatedSalaryFoundationTracker;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.dao.LaborDao;
import org.kuali.module.labor.util.ConsolidationUtil;
import org.kuali.module.labor.util.ObjectUtil;

/**
 * This class is for Labor Distribution DAO database queries
 */
public class LaborDaoOjb extends PlatformAwareDaoBaseOjb implements LaborDao {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LaborDaoOjb.class);

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getCSFTrackerData(java.util.Map)
     */
    @Deprecated
    public Object getCSFTrackerTotal(Map fieldValues) {

        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new CalculatedSalaryFoundationTracker()));

        ReportQueryByCriteria query = QueryFactory.newReportQuery(CalculatedSalaryFoundationTracker.class, criteria);

        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);

        query.setAttributes(new String[] { ConsolidationUtil.sum(KFSPropertyConstants.CSF_AMOUNT) });
        query.addGroupBy(groupBy);

        Object[] csf = null;

        Iterator<Object[]> calculatedSalaryFoundationTracker = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (calculatedSalaryFoundationTracker != null && calculatedSalaryFoundationTracker.hasNext()) {
            csf = calculatedSalaryFoundationTracker.next();
        }
        KualiDecimal csfAmount = KualiDecimal.ZERO;
        if (csf != null) {
            csfAmount = new KualiDecimal(csf[0].toString());
        }
        return csfAmount;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getEncumbranceTotal(java.util.Map)
     */
    public Object getEncumbranceTotal(Map fieldValues) {

        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new AccountStatusCurrentFunds()));

        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountStatusCurrentFunds.class, criteria);

        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);

        query.setAttributes(new String[] { ConsolidationUtil.sum(LaborConstants.BalanceInquiries.ANNUAL_BALANCE) + " + " + ConsolidationUtil.sum(LaborConstants.BalanceInquiries.CONTRACT_GRANT_BB_AMOUNT) });
        query.addGroupBy(groupBy);

        Object[] encumbrances = null;

        Iterator<Object[]> accountStatusCurrentFunds = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (accountStatusCurrentFunds != null && accountStatusCurrentFunds.hasNext()) {
            encumbrances = accountStatusCurrentFunds.next();
        }
        KualiDecimal encumbranceTotal = KualiDecimal.ZERO;
        if (encumbrances != null)
            encumbranceTotal = new KualiDecimal(encumbrances[0].toString());
        return encumbranceTotal;
    }

    
    /**
     * @see org.kuali.module.labor.dao.LaborDao#getEncumbranceTotal(java.util.Map)
     */
    public Collection getJuly1(Map fieldValues) {
        Map fieldCriteria = new HashMap();        
        fieldCriteria.putAll(fieldValues);        
        fieldCriteria.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        
        ArrayList objectTypeCodes = new ArrayList();
        Criteria criteria = new Criteria();
        //criteria.addBetween(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, LaborConstants.BalanceInquiries.laborLowValueObjectCode, LaborConstants.BalanceInquiries.laborHighValueObjectCode);
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldCriteria, new July1PositionFunding()));
        QueryByCriteria query = QueryFactory.newQuery(July1PositionFunding.class, criteria);
        
        Collection<July1PositionFunding> july1PositionFundings = getPersistenceBrokerTemplate().getCollectionByQuery(query);                
        return july1PositionFundings;
        
/*        
        List july1Container = new ArrayList();
        July1PositionFunding july1DataObject = null; 
            
        Map fieldCriteria = new HashMap();
        boolean consolidate = false;
        fieldCriteria.putAll(fieldValues);        
        fieldCriteria.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        
        List attributeList = new ArrayList();
        String[] attributes;

        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldCriteria, new July1PositionFunding()));
                
        if (!consolidate) {
            attributeList.add(KFSPropertyConstants.JULY1_BUDGET_AMOUNT);
        } else {            
            attributeList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            attributeList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            attributeList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
            attributeList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            attributeList.add(KFSPropertyConstants.EMPLID);
            attributeList.add(KFSPropertyConstants.POSITION_NUMBER);
        }

        ReportQueryByCriteria query = QueryFactory.newReportQuery(July1PositionFunding.class, criteria);
        
        if (consolidate) {
            attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
            
            query.addGroupBy(attributes.clone());
            attributeList.add(ConsolidationUtil.sum(KFSPropertyConstants.JULY1_BUDGET_AMOUNT));
        }       
        attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);
        Iterator d = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);


        System.out.println("XXXXXXXXXXX:"+d.toString());
        while(d.hasNext()) {            
            Object[] data = (Object[])d.next();
            System.out.println("*****************"+data.toString());
            for(int i=0;i<data.length;i++) {
                System.out.printf("Element[%d]: %s",i,data[i].toString());                
            }
        }
        return d;*/
    }
    
    /**
     * @see org.kuali.module.labor.dao.LaborDao#getBaseFunds(java.util.Map)
     */
    @Deprecated
    public Iterator getBaseFunds(Map fieldValues, boolean isConsolidated) {
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, LaborConstants.BalanceInquiries.BALANCE_CODE);
        return getAccountStatus(AccountStatusBaseFunds.class, fieldValues, isConsolidated);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getCurrentFunds(java.util.Map)
     */
    public Iterator getCurrentFunds(Map fieldValues, boolean isConsolidated) {
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, LaborConstants.BalanceInquiries.ACTUALS_CODE);
        return getAccountStatus(AccountStatusCurrentFunds.class, fieldValues, isConsolidated);
    }

    private <T> Iterator getAccountStatus(Class<T> clazz, Map fieldValues, boolean isConsolidated) {
        Query query = getAccountStatusQuery(clazz, fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    // build the query for balance search
    private <T> Query getAccountStatusQuery(Class<T> clazz, Map fieldValues, boolean isConsolidated) {
        LOG.debug("getAccountStatusQuery(Class<T>, Map, boolean) started");
        LOG.debug("Building criteria from map fields: " + fieldValues.entrySet());

        Criteria criteria = new Criteria();
        try {
            criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, clazz.newInstance()));
        }
        catch (Exception e) {
            LOG.error("Could not add and criteria properly for " + clazz);
            throw new RuntimeException(e);
        }

        ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, criteria);
        LOG.debug("Built query: " + query);

        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            ConsolidationUtil.buildConsolidatedQuery(query, ConsolidationUtil.sum(AccountingPeriodProperties.JULY.propertyName));
        }

        return query;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getEmployeeFunding(java.util.Map)
     */
    @Deprecated
    public Iterator getEmployeeFunding(Map fieldValues) {

        ArrayList objectTypeCodes = new ArrayList();
        objectTypeCodes.add(LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE);
        objectTypeCodes.add(LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE);

        Criteria criteria = new Criteria();
        criteria.addEqualToField(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "'" + LaborConstants.BalanceInquiries.ACTUALS_CODE + "'");
        criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, objectTypeCodes);
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new EmployeeFunding()));
        ReportQueryByCriteria query = QueryFactory.newReportQuery(EmployeeFunding.class, criteria);

        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.POSITION_NUMBER);
        groupByList.add(KFSPropertyConstants.EMPLID);

        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        List<String> attributeList = new ArrayList<String>(groupByList);
        attributeList.add(0, ConsolidationUtil.sum(KFSPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT));
        query.setAttributes((String[]) attributeList.toArray(new String[attributeList.size()]));

        query.addGroupBy(groupBy);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getJuly1PositionFunding(java.util.Map)
     */
    public Collection getJuly1PositionFunding(Map fieldValues) {

        ArrayList objectTypeCodes = new ArrayList();
        Criteria criteria = new Criteria();
        criteria.addBetween(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, LaborConstants.BalanceInquiries.laborLowValueObjectCode, LaborConstants.BalanceInquiries.laborHighValueObjectCode);
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new July1PositionFunding()));
        QueryByCriteria query = QueryFactory.newQuery(July1PositionFunding.class, criteria);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
}
