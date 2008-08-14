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
package org.kuali.kfs.pdp.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.businessobject.AchBank;
import org.kuali.kfs.pdp.dataaccess.AchBankDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class AchBankDaoOjb extends PlatformAwareDaoBaseOjb implements AchBankDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchBankDaoOjb.class);

    /**
     * @see org.kuali.kfs.pdp.dataaccess.AchBankDao#save(org.kuali.kfs.pdp.businessobject.AchBank)
     */
    public void save(AchBank ab) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(ab);
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.AchBankDao#getBank(java.lang.String)
     */
    public AchBank getBank(String bankRoutingNumber) {
        LOG.debug("getBank() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("bankRoutingNumber", bankRoutingNumber);

        return (AchBank)getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(AchBank.class,criteria));
    }
}
