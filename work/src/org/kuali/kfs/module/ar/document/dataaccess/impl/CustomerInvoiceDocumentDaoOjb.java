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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class CustomerInvoiceDocumentDaoOjb extends PlatformAwareDaoBaseOjb implements CustomerInvoiceDocumentDao {

    private static org.apache.log4j.Logger LOG = 
        org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentDaoOjb.class);
    
    public Collection getAll() {
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, (Criteria) null);
        //qbc.addOrderByAscending("customerPurchaseOrderNumber");
        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }

    public Collection getAllOpen() {
        Criteria criteria = new Criteria();
        criteria.addEqualTo("openInvoiceIndicator", true);
        
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);

        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }

    public Collection getOpenByCustomerNumber(String customerNumber) {
        // select i.* 
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        // where h.cust_nbr = ?
        
        //  OJB deals with the inner join automatically, because we have it setup with 
        // accountsReceivableDocumentHeader as a ReferenceDescriptor to Invoice.
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsReceivableDocumentHeader.customerNumber", customerNumber);
        criteria.addEqualTo("openInvoiceIndicator", "true");
        
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);
        
        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }
    
    public Collection getOpenByCustomerName(String customerName) {
        // select i.* 
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        //   inner join ar_cust_t c 
        //   on h.cust_nbr = c.cust_nbr 
        // where c.cust_nm like ? 
        
        Criteria criteria = new Criteria();
        criteria.addLike("accountsReceivableDocumentHeader.customer.customerName", customerName);
        criteria.addEqualTo("openInvoiceIndicator", "true");
        
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);
        
        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }
    
    public Collection getOpenByCustomerType(String customerTypeCode) {
        // select i.* 
        // from ar_doc_hdr_t h inner join ar_inv_doc_t i 
        //   on h.fdoc_nbr = i.fdoc_nbr 
        //   inner join ar_cust_t c 
        //   on h.cust_nbr = c.cust_nbr 
        // where c.cust_typ_cd = ?  
        
        //  OJB deals with the inner join automatically, because we have it setup with 
        // accountsReceivableDocumentHeader as a ReferenceDescriptor to Invoice, and Customer 
        // as a referencedescriptor to accountsReceivableDocumentHeader.
        Criteria criteria = new Criteria();
        criteria.addEqualTo("accountsReceivableDocumentHeader.customer.customerTypeCode", customerTypeCode);
        criteria.addEqualTo("openInvoiceIndicator", "true");
        
        QueryByCriteria qbc = QueryFactory.newQuery(CustomerInvoiceDocument.class, criteria);
        
        Collection customerinvoicedocuments = getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        List invoiceList = new ArrayList(customerinvoicedocuments);
        return invoiceList;
    }

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
