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
 * Created on Aug 11, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.BatchMaintenanceDao;
import org.kuali.kfs.pdp.service.ReferenceService;


/**
 * @author delyea
 */
public class BatchMaintenanceDaoOjb extends PlatformAwareDaoBaseOjb implements BatchMaintenanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchMaintenanceDaoOjb.class);
    private ReferenceService referenceService;

    public BatchMaintenanceDaoOjb() {
        super();
    }

    public void setReferenceService(ReferenceService ref) {
        referenceService = ref;
    }

    /**
     * doBatchPaymentsHaveOpenStatus() Return true if all payments in batch have an 'OPEN' status. Return false if all payments in
     * batch do not have an 'OPEN' status. The query in this method searches the payment detail table for payments of the given
     * batchId where the status equals any status other than 'OPEN'. If any rows exist with a status other than 'OPEN', return
     * false.
     * 
     * @param batchId Integer value of batch id of payments to search.
     * @return boolean true = all payments are 'OPEN'; false = all payments are not 'OPEN'
     */
    public boolean doBatchPaymentsHaveOpenStatus(Integer batchId) {
        LOG.debug("doBatchPaymentsHaveOpenStatus() enter method.");
        List codeList = new ArrayList();
        List statusList = referenceService.getAll("PaymentStatus");
        for (Iterator i = statusList.iterator(); i.hasNext();) {
            PaymentStatus element = (PaymentStatus) i.next();
            if (!(element.getCode().equals("OPEN"))) {
                codeList.add(element.getCode());
            }
        }

        Criteria crit = new Criteria();
        crit.addEqualTo("batchId", batchId);
        crit.addIn("paymentStatusCode", codeList);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(PaymentGroup.class, crit);
        q.setAttributes(new String[] { "paymentStatusCode" });
        q.addGroupBy("paymentStatusCode");

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (i.hasNext()) {
            LOG.debug("doBatchPaymentsHaveOpenStatus() Not all payment groups have status 'OPEN'.");
            TransactionalServiceUtils.exhaustIterator(i);
            return false;
        }
        else {
            LOG.debug("doBatchPaymentsHaveOpenStatus() All payment groups have status 'OPEN'.");
            return true;
        }
    }// end doBatchPaymentsHaveOpenStatus()

    /**
     * doBatchPaymentsHaveHeldStatus() Return true if all payments in batch have an 'HELD' status. Return false if all payments in
     * batch do not have an 'HELD' status. The query in this method searches the payment detail table for payments of the given
     * batchId where the status equals any status other than 'HELD'. If any rows exist with a status other than 'HELD', return
     * false.
     * 
     * @param batchId Integer value of batch id of payments to search.
     * @return boolean true = all payments are 'HELD'; false = all payments are not 'HELD'
     */
    public boolean doBatchPaymentsHaveHeldStatus(Integer batchId) {
        LOG.debug("doBatchPaymentsHaveHeldStatus() enter method.");
        List codeList = new ArrayList();
        List statusList = referenceService.getAll("PaymentStatus");
        for (Iterator i = statusList.iterator(); i.hasNext();) {
            PaymentStatus element = (PaymentStatus) i.next();
            if (!(element.getCode().equals("HELD"))) {
                codeList.add(element.getCode());
            }
        }

        Criteria crit = new Criteria();
        crit.addEqualTo("batchId", batchId);
        crit.addIn("paymentStatusCode", codeList);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(PaymentGroup.class, crit);
        q.setAttributes(new String[] { "paymentStatusCode" });
        q.addGroupBy("paymentStatusCode");

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (i.hasNext()) {
            LOG.debug("doBatchPaymentsHaveHeldStatus() Not all payment groups have status 'HELD'.");
            TransactionalServiceUtils.exhaustIterator(i);
            return false;
        }
        else {
            LOG.debug("doBatchPaymentsHaveHeldStatus() All payment groups have status 'HELD'.");
            return true;
        }
    }// end doBatchPaymentsHaveHeldStatus()
}
