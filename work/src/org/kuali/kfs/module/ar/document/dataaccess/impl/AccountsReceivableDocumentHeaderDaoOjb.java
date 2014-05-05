/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.dataaccess.AccountsReceivableDocumentHeaderDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

public class AccountsReceivableDocumentHeaderDaoOjb extends PlatformAwareDaoBaseOjb implements AccountsReceivableDocumentHeaderDao {

    public Collection getARDocumentHeadersByCustomerNumber(String customerNumber) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER, customerNumber);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria));
    }

    public Collection getARDocumentHeadersByCustomerNumberByProcessingOrgCodeAndChartCode(String customerNumber, String processingChartOfAccountCode, String processingOrganizationCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER, customerNumber);
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.PROCESSING_COA_CODE, processingChartOfAccountCode);
        criteria.addEqualTo(KFSConstants.CustomerOpenItemReport.PROCESSING_ORGANIZATION_CODE, processingOrganizationCode);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria));
    }

    public List<String> getARDocumentNumbersIncludingHiddenApplicationByCustomerNumber(String customerNumber){
        // get the AR documents by the customer b=number
        List<String> documentNumbers = new ArrayList<String>();
        Criteria criteria1 = new Criteria();
        criteria1.addEqualTo(KFSConstants.CustomerOpenItemReport.CUSTOMER_NUMBER,customerNumber);

        //Collection<AccountsReceivableDocumentHeader> arHeadersByCustomer = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria1));
        ReportQueryByCriteria query = new ReportQueryByCriteria(AccountsReceivableDocumentHeader.class, new String[]{KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER}, criteria1);
        Iterator results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while(results.hasNext()) {
            Object[] res = (Object[]) results.next();
            documentNumbers.add((String)res[0]);
        }

        // get the payment application documents, which belong to the customer but not in specified in AR_DOC_HDR_T
        if (!documentNumbers.isEmpty()) {
            Set<String> appDocumentNumbers = new HashSet<String>();
            Criteria criteria2 = new Criteria();
            criteria2.addIn("financialDocumentReferenceInvoiceNumber",documentNumbers);
            //Collection<InvoicePaidApplied> invoicePaidAppliedList = getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(InvoicePaidApplied.class, criteria2));
            query = new ReportQueryByCriteria(InvoicePaidApplied.class, new String[]{KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER}, criteria2);
            results = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
            while(results.hasNext()) {
                Object[] res = (Object[])results.next();
                documentNumbers.add((String)res[0]);
            }
        }

        return documentNumbers;
    }

    public Collection<AccountsReceivableDocumentHeader> getARDocumentHeadersIncludingHiddenApplicationByCustomerNumber(String customerNumber) {
        List<String> documentNumbers = getARDocumentNumbersIncludingHiddenApplicationByCustomerNumber(customerNumber);

        // get the final AR documents
        if (!documentNumbers.isEmpty()) {
            Criteria criteria3 = new Criteria();
            criteria3.addIn(KFSConstants.CustomerOpenItemReport.DOCUMENT_NUMBER,documentNumbers);
            return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(AccountsReceivableDocumentHeader.class, criteria3));
        }
        return new ArrayList<AccountsReceivableDocumentHeader>();
    }
}
