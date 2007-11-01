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
package org.kuali.module.gl.dao.ojb;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.dao.CorrectionChangeGroupDao;

public class CorrectionChangeGroupDaoOjb extends PlatformAwareDaoBaseOjb implements CorrectionChangeGroupDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionChangeGroupDaoOjb.class);

    /**
     * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#delete(org.kuali.module.gl.bo.CorrectionChangeGroup)
     */
    public void delete(CorrectionChangeGroup group) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(group);
    }

    /**
     * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#findByDocumentNumber(java.lang.String)
     */
    public Collection findByDocumentNumber(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        QueryByCriteria query = QueryFactory.newQuery(CorrectionChangeGroup.class, criteria);

        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String,
     *      java.lang.Integer)
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String documentNumber, Integer CorrectionChangeGroupNumber) {
        LOG.debug("findByDocumentNumberAndCorrectionChangeGroupNumber() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo("correctionChangeGroupLineNumber", CorrectionChangeGroupNumber);

        QueryByCriteria query = QueryFactory.newQuery(CorrectionChangeGroup.class, criteria);

        return (CorrectionChangeGroup) getPersistenceBrokerTemplate().getObjectByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.CorrectionChangeGroupDao#save(org.kuali.module.gl.bo.CorrectionChangeGroup)
     */
    public void save(CorrectionChangeGroup group) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(group);
    }
}
