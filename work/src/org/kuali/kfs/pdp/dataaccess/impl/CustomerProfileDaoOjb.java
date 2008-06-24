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
/*
 * Created on Aug 7, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.UserRequired;
import org.kuali.kfs.pdp.dataaccess.CustomerProfileDao;


/**
 * @author jsissom
 */
public class CustomerProfileDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerProfileDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileDaoOjb.class);

    private UniversalUserService userService;

    public CustomerProfileDaoOjb() {
        super();
    }

    // Inject
    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }

    private void updateUser(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            updateUser((CustomerProfile) iter.next());
        }
    }

    private void updateUser(CustomerProfile b) {
        UserRequired ur = (UserRequired) b;
        try {
            ur.updateUser(userService);
        }
        catch (UserNotFoundException e) {
            b.setLastUpdateUser(null);
        }
    }

    public List getAll() {
        LOG.debug("getAll() started");

        QueryByCriteria qbc = new QueryByCriteria(CustomerProfile.class);
        qbc.addOrderByAscending("chartCode");
        qbc.addOrderByAscending("orgCode");
        qbc.addOrderByAscending("subUnitCode");

        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        updateUser(l);
        return l;
    }

    public CustomerProfile get(Integer custId) {
        LOG.debug("get(id) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", custId);

        CustomerProfile cp = (CustomerProfile) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(CustomerProfile.class, criteria));
        if (cp != null) {
            updateUser(cp);
        }
        // List s = cp.getCustomerBanks();
        // LOG.error("get() Banks (customerId: " + cp.getId() + "):");
        // for (Iterator iter = s.iterator(); iter.hasNext();) {
        // CustomerBank element = (CustomerBank) iter.next();
        // LOG.error("get() BANK: " + element);
        // }
        return cp;
    }

    public CustomerProfile get(String chartCode, String orgCode, String subUnitCode) {
        LOG.debug("get(chart,org,sub unit) started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartCode", chartCode);
        criteria.addEqualTo("orgCode", orgCode);
        criteria.addEqualTo("subUnitCode", subUnitCode);

        CustomerProfile cp = (CustomerProfile) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(CustomerProfile.class, criteria));
        if (cp != null) {
            updateUser(cp);
        }
        return cp;
    }

    public void save(CustomerProfile cp) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(cp);
    }

    public void saveCustomerBank(CustomerBank cb) {
        LOG.debug("saveCustomerBank() started");

        getPersistenceBrokerTemplate().store(cb);
    }

    public void deleteCustomerBank(CustomerBank cb) {
        LOG.debug("deleteCustomerBank() started");

        getPersistenceBrokerTemplate().delete(cb);
    }
}
