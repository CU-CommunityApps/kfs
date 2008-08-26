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
 * Created on Sep 1, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.pdp.dataaccess.ProcessSummaryDao;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.springframework.transaction.annotation.Transactional;


/**
 * 
 */
@Transactional
public class PostFormatProcessSummary extends TransientBusinessObjectBase {
    private static BigDecimal zero = new BigDecimal(0);
    private List processSummary = new ArrayList();

    public PostFormatProcessSummary() {
        super();
    }

    public List getProcessSummaryList() {
        return processSummary;
    }

    /**
     * Add the paymentdetail info to our summary list
     * 
     * @param pd
     */
    public void add(PaymentGroup pg) {
        ProcessSummary ps = findProcessSummary(pg);

        // If it's not in our list, add it
        if (ps == null) {
            ps = new ProcessSummary();
            ps.setBeginDisbursementNbr(new Integer(0));
            ps.setCustomer(pg.getBatch().getCustomerProfile());
            ps.setDisbursementType(pg.getDisbursementType());
            ps.setEndDisbursementNbr(new Integer(0));
            ps.setProcess(pg.getProcess());
            ps.setProcessTotalAmount(new BigDecimal(0));
            ps.setProcessTotalCount(new Integer(0));
            ps.setSortGroupId(pg.getSortGroupId());
            processSummary.add(ps);
        }

        // Update the total & count
        ps.setProcessTotalAmount(ps.getProcessTotalAmount().add(pg.getNetPaymentAmount()));
        ps.setProcessTotalCount(new Integer(ps.getProcessTotalCount().intValue() + pg.getPaymentDetails().size()));
    }

    /**
     * Save all the process summary records
     * 
     * @param pdd
     */
    public void save(ProcessSummaryDao psd) {
        for (Iterator iter = processSummary.iterator(); iter.hasNext();) {
            ProcessSummary ps = (ProcessSummary) iter.next();
            psd.save(ps);
        }
    }

    // See if we already have a summary record. If we do, return it,
    // if not, return null;
    private ProcessSummary findProcessSummary(PaymentGroup pg) {

        for (Iterator iter = processSummary.iterator(); iter.hasNext();) {
            ProcessSummary ps = (ProcessSummary) iter.next();

            DisbursementType dt = pg.getDisbursementType();
            System.out.println("!!!!!ps null = " + (ps == null));
            System.out.println("ps.getCustomer() null = " + (ps.getCustomer() == null));
            System.out.println("pg null = " + (pg == null));
            System.out.println("pg.getBatch() null = " + (pg.getBatch() == null));
            System.out.println("pg.getBatch().getCustomerProfile() null = " + (pg.getBatch().getCustomerProfile() == null));
            System.out.println("ps.getDisbursementType() null = " + (ps.getDisbursementType() == null));
            System.out.println("dt null = " + (dt == null));
            System.out.println("ps.getSortGroupId() null = " + (ps.getSortGroupId() == null));
            System.out.println("pg.getSortGroupId() null = " + (pg.getSortGroupId() == null));
            System.out.println("ps.getProcess() null = " + (ps.getProcess() == null));
            System.out.println("pg.getProcess() null = " + (pg.getProcess() == null));
            if (ps.getCustomer().equals(pg.getBatch().getCustomerProfile()) && ps.getDisbursementType().equals(dt) && ps.getSortGroupId().equals(pg.getSortGroupId()) && ps.getProcess().equals(pg.getProcess())) {
                return ps;
            }
        }
        return null;
    }

    /**
     * @param pg Update the disbursement number information
     * @param range
     */
    public void setDisbursementNumber(PaymentGroup pg, Integer nbr) {
        ProcessSummary ps = findProcessSummary(pg);
        if (ps != null) {
            if (ps.getBeginDisbursementNbr().intValue() == 0) {
                ps.setBeginDisbursementNbr(nbr);
                ps.setEndDisbursementNbr(nbr);
            }
            else {
                ps.setEndDisbursementNbr(nbr);
            }
        }
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap(); 

        m.put("processSummary", this.processSummary);
        
        return m;
    }
}
