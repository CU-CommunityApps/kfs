/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.dataaccess.OrganizationDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.util.TransactionalServiceUtils;

/**
 * This class is the OJB implementation of the OrganizationDao interface.
 */
public class OrganizationDaoOjb extends PlatformAwareDaoBaseOjb implements OrganizationDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationDaoOjb.class);

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getByPrimaryId(java.lang.String, java.lang.String)
     */
    public Org getByPrimaryId(String chartOfAccountsCode, String organizationCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("organizationCode", organizationCode);

        return (Org) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(Org.class, criteria));
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#save(org.kuali.kfs.coa.businessobject.Org)
     */
    public void save(Org organization) {
        getPersistenceBrokerTemplate().store(organization);
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getActiveAccountsByOrg(java.lang.String, java.lang.String)
     */
    public List getActiveAccountsByOrg(String chartOfAccountsCode, String organizationCode) {

        List accounts = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("organizationCode", organizationCode);
        criteria.addEqualTo("active", Boolean.FALSE);

        accounts = (List) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Account.class, criteria));

        if (accounts.isEmpty() || accounts.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return accounts;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getActiveChildOrgs(java.lang.String, java.lang.String)
     */
    public List getActiveChildOrgs(String chartOfAccountsCode, String organizationCode) {

        List orgs = new ArrayList();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("reportsToChartOfAccountsCode", chartOfAccountsCode);
        criteria.addEqualTo("reportsToOrganizationCode", organizationCode);
        criteria.addEqualTo("active", Boolean.TRUE);

        orgs = (List) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Org.class, criteria));

        if (orgs.isEmpty() || orgs.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getActiveOrgsByType(java.lang.String)
     */
    public List<Org> getActiveOrgsByType(String organizationTypeCode) {
        List<Org> orgs = new ArrayList<Org>();

        Criteria criteria = new Criteria();
        criteria.addEqualTo("organizationTypeCode", organizationTypeCode);
        criteria.addEqualTo("active", Boolean.TRUE);

        orgs = (List<Org>) getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(Org.class, criteria));

        if (orgs.isEmpty() || orgs.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return orgs;
    }

    /**
     * we insist that the root organization be active
     * 
     * @see org.kuali.kfs.coa.dataaccess.OrganizationDao#getRootOrganizationCode(java.lang.String, java.lang.String)
     */
    public String[] getRootOrganizationCode(String rootChart, String selfReportsOrgTypeCode) {
        String[] returnValues = { null, null };
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, rootChart);
        criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_TYPE_CODE, selfReportsOrgTypeCode);
        // the root organization must be active
        criteria.addEqualTo(KFSPropertyConstants.ORGANIZATION_ACTIVE_INDICATOR, KFSConstants.ACTIVE_INDICATOR);
        String[] attributeList = { KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.ORGANIZATION_CODE };
        ReportQueryByCriteria rptQuery = new ReportQueryByCriteria(Org.class, attributeList, criteria);
        Iterator Results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rptQuery);
        if (Results.hasNext()) {
            Object[] returnList = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(Results);
            returnValues[0] = (String) returnList[0];
            returnValues[1] = (String) returnList[1];
        }
        return returnValues;
    }

}
