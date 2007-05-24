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
package org.kuali.module.gl.dao.ojb;

import java.sql.Date;
import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.gl.dao.CorrectionDocumentDao;
import org.kuali.module.gl.document.CorrectionDocument;

public class CorrectionDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements CorrectionDocumentDao {

    public Collection<CorrectionDocument> getCorrectionDocumentsFinalizedOn(Date documentFinalDate) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(Constants.DOCUMENT_HEADER_PROPERTY_NAME + "." + PropertyConstants.DOCUMENT_FINAL_DATE, documentFinalDate);
        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(CorrectionDocument.class, criteria));
    }
}
