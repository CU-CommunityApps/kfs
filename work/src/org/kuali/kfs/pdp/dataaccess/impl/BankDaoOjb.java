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
import org.kuali.kfs.pdp.businessobject.Bank;
import org.kuali.kfs.pdp.businessobject.UserRequired;
import org.kuali.kfs.pdp.dataaccess.BankDao;


/**
 * @author jsissom
 */
public class BankDaoOjb extends PlatformAwareDaoBaseOjb implements BankDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankDaoOjb.class);

    private UniversalUserService userService;

    public BankDaoOjb() {
        super();
    }

    // Inject
    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }

    public List getAll() {
        LOG.debug("getAll() started");

        QueryByCriteria qbc = new QueryByCriteria(Bank.class);
        qbc.addOrderBy("name", true);

        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        updateUser(l);

        return l;
    }

    public List getAll(boolean active) {
        LOG.debug("getAll() started");

        Criteria c = new Criteria();
        c.addEqualTo("active", new Boolean(active));

        QueryByCriteria qbc = new QueryByCriteria(Bank.class, c);
        qbc.addOrderBy("name", true);

        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        updateUser(l);
        return l;
    }

    private void updateUser(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            updateUser((Bank) iter.next());
        }
    }

    private void updateUser(Bank b) {
        UserRequired ur = (UserRequired) b;
        try {
            ur.updateUser(userService);
        }
        catch (UserNotFoundException e) {
            b.setLastUpdateUser(null);
        }
    }

    public Bank get(Integer bankId) {
        LOG.debug("get() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("id", bankId);

        Bank b = (Bank) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Bank.class, criteria));
        if (b != null) {
            updateUser(b);
        }
        return b;
    }

    public List getAllBanksForDisbursementType(String type) {
        LOG.debug("getAllBanksForDisbursementType() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("disbursementTypeCode", type);

        QueryByCriteria qbc = new QueryByCriteria(Bank.class, criteria);
        qbc.addOrderBy("name", true);

        List l = (List) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(Bank.class, criteria));
        updateUser(l);
        return l;
    }

    public void save(Bank b) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(b);
    }
}
