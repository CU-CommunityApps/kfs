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
package org.kuali.module.purap.dao.ojb;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.bo.NegativePaymentRequestApprovalLimit;
import org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao;

/**
 * OJB Implementation of NegativePaymentRequestApprovalLimitDao.
 */
public class NegativePaymentRequestApprovalLimitDaoOjb extends PlatformAwareDaoBaseOjb implements NegativePaymentRequestApprovalLimitDao {
    private static Logger LOG = Logger.getLogger(NegativePaymentRequestApprovalLimitDaoOjb.class);

    /**
     * @see org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao#findByChart(java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChart(String chartCode) {
        LOG.debug("Entering findByChart(String)");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartCode);
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findByChart(String)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao#findByChartAndAccount(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndAccount(String chartCode, String accountNumber) {
        LOG.debug("Entering findByChartAndAccount(String, String)");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartCode);
        criteria.addEqualTo("accountNumber", accountNumber);
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findByChartAndAccount(String, String)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao#findByChartAndOrganization(java.lang.String,
     *      java.lang.String)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findByChartAndOrganization(String chartCode, String organizationCode) {
        LOG.debug("Entering findByChartAndOrganization(String, String)");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartCode);
        criteria.addEqualTo("organizationCode", organizationCode);
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findByChartAndOrganization(String, String)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao#findAboveLimit(org.kuali.core.util.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findAboveLimit(KualiDecimal limit) {
        LOG.debug("Entering findAboveLimit(KualiDecimal)");
        Criteria criteria = new Criteria();
        criteria.addGreaterThan("negativePaymentRequestApprovalLimitAmount", limit);
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findAboveLimit(KualiDecimal)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao#findBelowLimit(org.kuali.core.util.KualiDecimal)
     */
    public Collection<NegativePaymentRequestApprovalLimit> findBelowLimit(KualiDecimal limit) {
        LOG.debug("Entering findBelowLimit(KualiDecimal)");
        Criteria criteria = new Criteria();
        criteria.addLessThan("negativePaymentRequestApprovalLimitAmount", limit);
        Query query = new QueryByCriteria(NegativePaymentRequestApprovalLimit.class, criteria);
        LOG.debug("Leaving findBelowLimit(KualiDecimal)");
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

}
