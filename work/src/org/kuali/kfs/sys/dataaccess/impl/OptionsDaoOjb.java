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
package org.kuali.kfs.sys.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.sys.businessobject.Options;
import org.kuali.kfs.sys.dataaccess.OptionsDao;

/**
 * 
 * 
 */
public class OptionsDaoOjb extends PlatformAwareDaoBaseOjb implements OptionsDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OptionsDaoOjb.class);

    public OptionsDaoOjb() {
        super();
    }

    public Options getByPrimaryId(Integer universityFiscalYear) {
        LOG.debug("getByPrimaryId() started");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", universityFiscalYear);

        QueryByCriteria qbc = QueryFactory.newQuery(Options.class, crit);
        return (Options) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }
}
