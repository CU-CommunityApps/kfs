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
 * Created on Aug 16, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.FormatProcess;
import org.kuali.module.pdp.dao.FormatProcessDao;


/**
 * @author jsissom
 */
public class FormatProcessDaoOjb extends PlatformAwareDaoBaseOjb implements FormatProcessDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatProcessDaoOjb.class);

    public FormatProcessDaoOjb() {
        super();
    }

    public FormatProcess getByCampus(String campus) {
        LOG.debug("getByCampus() starting");

        Criteria c = new Criteria();
        c.addEqualTo("physicalCampusProcessCode", campus);

        return (FormatProcess) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(FormatProcess.class, c));
    }

    public void removeByCampus(String campus) {
        LOG.debug("removeByCampus() starting");

        FormatProcess fp = getByCampus(campus);
        if (fp != null) {
            getPersistenceBrokerTemplate().delete(fp);
        }
    }

    public void add(String campus, Date now) {
        LOG.debug("add() starting");

        FormatProcess fp = new FormatProcess();
        fp.setPhysicalCampusProcessCode(campus);
        fp.setBeginFormat(new Timestamp(now.getTime()));

        getPersistenceBrokerTemplate().store(fp);
    }
}
