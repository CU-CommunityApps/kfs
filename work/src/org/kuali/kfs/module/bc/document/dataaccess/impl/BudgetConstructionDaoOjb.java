/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.budget.dao.ojb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCConstants.OrgSelControlOption;
import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.chart.bo.Delegate;

/**
 * This class is the OJB implementation of the BudgetConstructionDao interface.
 */
public class BudgetConstructionDaoOjb extends PlatformAwareDaoBaseOjb implements BudgetConstructionDao {
    
    private DocumentTypeService documentTypeService;

    /**
     * This gets a BudgetConstructionHeader using the candidate key chart, account, subaccount, fiscalyear
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return BudgetConstructionHeader
     */
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("subAccountNumber", subAccountNumber);
        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        return (BudgetConstructionHeader) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionHeader.class, criteria));
    }

    /**
     * This saves a BudgetConstructionHeader object to the database
     * 
     * @param bcHeader
     */
    public void saveBudgetConstructionHeader(BudgetConstructionHeader bcHeader) {
        getPersistenceBrokerTemplate().store(bcHeader);
    }

    /**
     * This gets a BudgetConstructionFundingLock using the primary key chart, account, subaccount, fiscalyear, pUId
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @param personUniversalIdentifier
     * @return BudgetConstructionFundingLock
     */
    public BudgetConstructionFundingLock getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String personUniversalIdentifier) {
        // LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("appointmentFundingLockUserId", personUniversalIdentifier);
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("subAccountNumber", subAccountNumber);
        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        return (BudgetConstructionFundingLock) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionFundingLock.class, criteria));
    }

    /**
     * This saves a BudgetConstructionFundingLock to the database
     * 
     * @param budgetConstructionFundingLock
     */
    public void saveBudgetConstructionFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock) {
        getPersistenceBrokerTemplate().store(budgetConstructionFundingLock);
    }

    /**
     * This deletes a BudgetConstructionFundingLock from the database
     * 
     * @param budgetConstructionFundingLock
     */
    public void deleteBudgetConstructionFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock) {
        getPersistenceBrokerTemplate().delete(budgetConstructionFundingLock);
    }

    /**
     * This gets the set of BudgetConstructionFundingLocks asssociated with a BC EDoc (account). Each BudgetConstructionFundingLock
     * has the positionNumber dummy attribute set to the associated Position that is locked. A positionNumber value of "NotFnd"
     * indicates the BudgetConstructionFundingLock is an orphan.
     * 
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @param fiscalYear
     * @return Collection<BudgetConstructionFundingLock>
     */
    public Collection<BudgetConstructionFundingLock> getFlocksForAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {
        Collection<BudgetConstructionFundingLock> fundingLocks;

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("subAccountNumber", subAccountNumber);
        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        fundingLocks = (Collection<BudgetConstructionFundingLock>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionFundingLock.class, criteria));
        BudgetConstructionFundingLock fundingLock;
        Iterator<BudgetConstructionFundingLock> iter = fundingLocks.iterator();
        while (iter.hasNext()) {
            fundingLock = iter.next();
            fundingLock.setPositionNumber(getPositionAssociatedWithFundingLock(fundingLock));
        }

        return fundingLocks;
    }

    private String getPositionAssociatedWithFundingLock(BudgetConstructionFundingLock budgetConstructionFundingLock) {

        String positionNumber = "NotFnd"; // default if there is no associated position that is locked (orphaned)

        Criteria criteria = new Criteria();
        criteria.addEqualTo("pendingBudgetConstructionAppointmentFunding.chartOfAccountsCode", budgetConstructionFundingLock.getChartOfAccountsCode());
        criteria.addEqualTo("pendingBudgetConstructionAppointmentFunding.accountNumber", budgetConstructionFundingLock.getAccountNumber());
        criteria.addEqualTo("pendingBudgetConstructionAppointmentFunding.subAccountNumber", budgetConstructionFundingLock.getSubAccountNumber());
        criteria.addEqualTo("pendingBudgetConstructionAppointmentFunding.universityFiscalYear", budgetConstructionFundingLock.getUniversityFiscalYear());
        criteria.addEqualTo("positionLockUserIdentifier", budgetConstructionFundingLock.getAppointmentFundingLockUserId());
        String[] columns = new String[] { "positionNumber" };
        ReportQueryByCriteria q = QueryFactory.newReportQuery(BudgetConstructionPosition.class, columns, criteria, true);
        PersistenceBroker pb = getPersistenceBroker(true);

        Iterator<Object[]> iter = pb.getReportQueryIteratorByQuery(q);

        if (iter.hasNext()) {
            Object[] objs = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iter);
            if (objs[0] != null) {
                positionNumber = (String) objs[0];
            }
        }
        return positionNumber;
    }

    /**
     * Gets a BudgetConstructionPosition from the database by the primary key positionNumber, fiscalYear
     * 
     * @param positionNumber
     * @param fiscalYear
     * @return BudgetConstructionPosition
     */
    public BudgetConstructionPosition getByPrimaryId(String positionNumber, Integer fiscalYear) {
        // LOG.debug("getByPrimaryId() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("positionNumber", positionNumber);
        criteria.addEqualTo("universityFiscalYear", fiscalYear);

        return (BudgetConstructionPosition) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(BudgetConstructionPosition.class, criteria));
    }

    /**
     * Saves a BudgetConstructionPosition to the database
     * 
     * @param bcPosition
     */
    public void saveBudgetConstructionPosition(BudgetConstructionPosition bcPosition) {
        getPersistenceBrokerTemplate().store(bcPosition);
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetConstructionDao#deleteBudgetConstructionPullupByUserId(java.lang.String)
     */
    public void deleteBudgetConstructionPullupByUserId(String personUserIdentifier) {

        Criteria criteria = new Criteria();
        criteria.addEqualTo("personUniversalIdentifier", personUserIdentifier);
        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(BudgetConstructionPullup.class, criteria));

    }

    /**
     * @see org.kuali.module.budget.dao.BudgetConstructionDao#getBudgetConstructionPullupFlagSetByUserId(java.lang.String)
     */
    public List getBudgetConstructionPullupFlagSetByUserId(String personUserIdentifier) {
        List orgs = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        criteria.addGreaterThan("pullFlag", OrgSelControlOption.NO.getKey());
        orgs = (List) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(BudgetConstructionPullup.class, criteria));
        if (orgs.isEmpty() || orgs.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetConstructionDao#getBcPullupChildOrgs(java.lang.String, java.lang.String,
     *      java.lang.String)
     */
    public List getBudgetConstructionPullupChildOrgs(String personUniversalIdentifier, String chartOfAccountsCode, String organizationCode) {
        List orgs = new ArrayList();

        Criteria cycleCheckCriteria = new Criteria();
        cycleCheckCriteria.addEqualToField("chartOfAccountsCode", "reportsToChartOfAccountsCode");
        cycleCheckCriteria.addEqualToField("organizationCode", "reportsToOrganizationCode");
        cycleCheckCriteria.setEmbraced(true);
        cycleCheckCriteria.setNegative(true);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("reportsToChartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("reportsToOrganizationCode", organizationCode);
        criteria.addEqualTo("personUniversalIdentifier", personUniversalIdentifier);
        criteria.addAndCriteria(cycleCheckCriteria);

        QueryByCriteria query = QueryFactory.newQuery(BudgetConstructionPullup.class, criteria);
        query.addOrderByAscending("organization.organizationName");

        orgs = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        if (orgs.isEmpty() || orgs.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetConstructionDao#getPendingBudgetConstructionAppointmentFundingRequestSum(org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger)
     */
    public KualiInteger getPendingBudgetConstructionAppointmentFundingRequestSum(PendingBudgetConstructionGeneralLedger salaryDetailLine) {
        KualiInteger salarySum = KualiInteger.ZERO;

        Criteria criteria = new Criteria();
        criteria.addEqualTo("universityFiscalYear", salaryDetailLine.getUniversityFiscalYear());
        criteria.addEqualTo("chartOfAccountsCode", salaryDetailLine.getChartOfAccountsCode());
        criteria.addEqualTo("accountNumber", salaryDetailLine.getAccountNumber());
        criteria.addEqualTo("subAccountNumber", salaryDetailLine.getSubAccountNumber());
        criteria.addEqualTo("financialObjectCode", salaryDetailLine.getFinancialObjectCode());
        criteria.addEqualTo("financialSubObjectCode", salaryDetailLine.getFinancialSubObjectCode());
        String[] columns = new String[] { "financialObjectCode", "financialSubObjectCode", "sum(appointmentRequestedAmount)" };
        ReportQueryByCriteria q = QueryFactory.newReportQuery(PendingBudgetConstructionAppointmentFunding.class, columns, criteria, true);
        q.addGroupBy(new String[] { "financialObjectCode", "financialSubObjectCode" });
        PersistenceBroker pb = getPersistenceBroker(true);

        Iterator<Object[]> iter = pb.getReportQueryIteratorByQuery(q);

        if (iter.hasNext()) {
            Object[] objs = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iter);
            if (objs[2] != null) {
                salarySum = new KualiInteger((BigDecimal) objs[2]);
            }
        }
        return salarySum;
    }


    /**
     * @see org.kuali.module.budget.dao.BudgetConstructionDao#getDocumentPBGLFringeLines(java.lang.String, java.util.List)
     */
    public List getDocumentPBGLFringeLines(String documentNumber, List fringeObjects){
        List documentPBGLfringeLines = new ArrayList();
        
        // TODO need to make sure we are getting the data that was updated by the jdbc benefits calc calls
        // we probably should just add a clearcache call at the end of all JDBC public methods that update the DB
        getPersistenceBrokerTemplate().clearCache();
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        criteria.addIn("financialObjectCode", fringeObjects);
        QueryByCriteria query = QueryFactory.newQuery(PendingBudgetConstructionGeneralLedger.class, criteria);
        query.addOrderByAscending("financialObjectCode");
        documentPBGLfringeLines = (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);

        return documentPBGLfringeLines;
    }

    /**
     * @see org.kuali.module.budget.dao.BudgetConstructionDao#isDelegate(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean isDelegate(String chartOfAccountsCode, String accountNumber, String personUniversalIdentifier) {
        
        boolean retval = false;
        
        // active BC account delegates are marked with the BC document type or the special "ALL" document type
        List docTypes = new ArrayList();
        docTypes.add(BCConstants.DOCUMENT_TYPE_CODE_ALL);
        docTypes.add(documentTypeService.getDocumentTypeCodeByClass(BudgetConstructionDocument.class));
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        criteria.addEqualTo("accountDelegateSystemId", personUniversalIdentifier);
        criteria.addEqualTo("accountDelegateActiveIndicator", "Y");
        criteria.addIn("financialDocumentTypeCode", docTypes);
        QueryByCriteria query = QueryFactory.newQuery(Delegate.class, criteria);
        if (getPersistenceBrokerTemplate().getCount(query) > 0){
            retval = true;
        }
        
        return retval;
    }

    /**
     * Sets the documentTypeService attribute value.
     * @param documentTypeService The documentTypeService to set.
     */
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
    
    
}
