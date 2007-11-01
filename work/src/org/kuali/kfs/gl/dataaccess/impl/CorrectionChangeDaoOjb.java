/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.dao.ojb;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.CorrectionChange;
import org.kuali.module.gl.dao.CorrectionChangeDao;

public class CorrectionChangeDaoOjb extends PlatformAwareDaoBaseOjb implements CorrectionChangeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionChangeDaoOjb.class);

    /**
     * @see org.kuali.module.gl.dao.CorrectionChangeDao#delete(org.kuali.module.gl.bo.CorrectionChange)
     */
    public void delete(CorrectionChange spec) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(spec);
    }

    /**
     * @see org.kuali.module.gl.dao.CorrectionChangeDao#findByDocumentHeaderIdAndCorrectionGroupNumber(java.lang.String,
     *      java.lang.Integer)
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String documentNumber, Integer correctionGroupLineNumber) {
        LOG.debug("findByDocumentHeaderIdAndCorrectionGroupNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo("correctionChangeGroupLineNumber", correctionGroupLineNumber);

        QueryByCriteria query = QueryFactory.newQuery(CorrectionChange.class, criteria);

        return (List) getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.CorrectionChangeDao#save(org.kuali.module.gl.bo.CorrectionChange)
     */
    public void save(CorrectionChange spec) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(spec);
    }
}
