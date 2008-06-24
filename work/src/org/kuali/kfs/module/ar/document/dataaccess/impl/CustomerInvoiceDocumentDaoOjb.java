/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.dataaccess.impl;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao;

public class CustomerInvoiceDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerInvoiceDocumentDao {

    private static org.apache.log4j.Logger LOG = 
        org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao#getInvoiceByOrganizationInvoiceNumber(java.lang.String)
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("organizationInvoiceNumber", organizationInvoiceNumber);
        
        return (CustomerInvoiceDocument) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao#getInvoiceByInvoiceDocumentNumber(java.lang.String)
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String documentNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("documentNumber", documentNumber);
        return (CustomerInvoiceDocument) getPersistenceBrokerTemplate().getObjectByQuery(QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria));
    }
    
}
