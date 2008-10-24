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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.businessobject.PaymentStatus;
import org.kuali.kfs.pdp.dataaccess.FormatPaymentDao;
import org.kuali.kfs.sys.service.KualiCodeService;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;

public class FormatPaymentDaoOjb extends PlatformAwareDaoBaseOjb implements FormatPaymentDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatPaymentDaoOjb.class);

    private KualiCodeService kualiCodeService;
    
    /**
     * @see org.kuali.kfs.pdp.dataaccess.FormatPaymentDao#markPaymentsForFormat(org.kuali.kfs.pdp.businessobject.PaymentProcess, java.util.List, java.util.Date, java.lang.String)
     */
    public void markPaymentsForFormat(PaymentProcess proc, List customers, Date paydate, String paymentTypes) {
        LOG.debug("markPaymentsForFormat() started");

        Timestamp now = new Timestamp((new Date()).getTime());
        java.sql.Date sqlDate = new java.sql.Date(paydate.getTime());
        Calendar c = Calendar.getInstance();
        c.setTime(sqlDate);
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 59);
        c.set(Calendar.AM_PM, Calendar.PM);
        Timestamp paydateTs = new Timestamp(c.getTime().getTime());

        LOG.debug("markPaymentsForFormat() last update = " + now);
        LOG.debug("markPaymentsForFormat() entered paydate = " + paydate);
        LOG.debug("markPaymentsForFormat() actual paydate = " + paydateTs);

        PaymentStatus format = (PaymentStatus) this.kualiCodeService.getByCode(PaymentStatus.class, PdpConstants.PaymentStatusCodes.FORMAT);

        List customerIds = new ArrayList();
        for (Iterator iter = customers.iterator(); iter.hasNext();) {
            CustomerProfile element = (CustomerProfile) iter.next();
            customerIds.add(element.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn("batch.customerId", customerIds);
        criteria.addEqualTo("paymentStatusCode", PdpConstants.PaymentStatusCodes.OPEN);

        if (PdpConstants.PaymentTypes.DISBURSEMENTS_WITH_SPECIAL_HANDLING.equals(paymentTypes)) {
            // special handling only
            criteria.addEqualTo("pymtSpecialHandling", Boolean.TRUE);
        }
        else if (PdpConstants.PaymentTypes.DISBURSEMENTS_NO_SPECIAL_HANDLING.equals(paymentTypes)) {
            // no special handling only
            criteria.addEqualTo("pymtSpecialHandling", Boolean.FALSE);
        }
        else if (PdpConstants.PaymentTypes.DISBURSEMENTS_WITH_ATTACHMENTS.equals(paymentTypes)) {
            // attachments only
            criteria.addEqualTo("pymtAttachment", Boolean.TRUE);
        }
        else if (PdpConstants.PaymentTypes.DISBURSEMENTS_NO_ATTACHMENTS.equals(paymentTypes)) {
            // no attachments only
            criteria.addEqualTo("pymtAttachment", Boolean.FALSE);
        }

        if (PdpConstants.PaymentTypes.PROCESS_IMMEDIATE.equals(paymentTypes)) {
            criteria.addEqualTo("processImmediate", Boolean.TRUE);
        }
        else {
            // (Payment date <= usePaydate OR immediate = TRUE)
            Criteria criteria1 = new Criteria();
            criteria1.addEqualTo("processImmediate", Boolean.TRUE);

            Criteria criteria2 = new Criteria();
            criteria2.addLessOrEqualThan("paymentDate", paydateTs);
            criteria1.addOrCriteria(criteria2);

            criteria.addAndCriteria(criteria1);
        }

        Iterator groupIterator = getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
        while (groupIterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) groupIterator.next();
            paymentGroup.setLastUpdate(paydateTs);
            paymentGroup.setPaymentStatus(format);
            paymentGroup.setProcess(proc);
            getPersistenceBrokerTemplate().store(paymentGroup);
        }
    }

    /**
     * @see org.kuali.kfs.pdp.dataaccess.FormatPaymentDao#unmarkPaymentsForFormat(org.kuali.kfs.pdp.businessobject.PaymentProcess)
     */
    public void unmarkPaymentsForFormat(PaymentProcess proc) {
        LOG.debug("unmarkPaymentsForFormat() started");

        Timestamp now = new Timestamp((new Date()).getTime());

        PaymentStatus openStatus = (PaymentStatus) kualiCodeService.getByCode(PaymentStatus.class, PdpConstants.PaymentStatusCodes.OPEN);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processId", proc.getId());
        criteria.addEqualTo("paymentStatusCode", PdpConstants.PaymentStatusCodes.FORMAT);

        Iterator groupIterator = getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentGroup.class, criteria));
        while (groupIterator.hasNext()) {
            PaymentGroup paymentGroup = (PaymentGroup) groupIterator.next();
            paymentGroup.setLastUpdate(now);
            paymentGroup.setPaymentStatus(openStatus);
            getPersistenceBrokerTemplate().store(paymentGroup);
        }
    }

    public KualiCodeService getKualiCodeService() {
        return kualiCodeService;
    }

    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }
}
