/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for InvoiceGeneralDetail
 */
public enum InvoiceGeneralDetailFixture {

    INV_GNRL_DTL1("5678", "comment", "2011-05-02 - 2012-04-30", "MILE", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(1), new KualiDecimal(99987.00), KualiDecimal.ZERO, KualiDecimal.ZERO, null),
    INV_GNRL_DTL2("5680", "comment", "2011-05-02 - 2012-04-30", "ANNU", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(13.00), new KualiDecimal(99987.00), KualiDecimal.ZERO, KualiDecimal.ZERO, null),
    INV_GNRL_DTL3("5678", "comment", "2011-05-02 - 2012-04-30", "MNTH", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(1), new KualiDecimal(99999.00), KualiDecimal.ZERO, KualiDecimal.ZERO, null),
    INV_GNRL_DTL4("5678", "comment", "2011-05-02 - 2012-04-30", "PDBS", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), new KualiDecimal(1), new KualiDecimal(99999.00), KualiDecimal.ZERO, KualiDecimal.ZERO, null),
    INV_GNRL_DTL5("5678", "comment", "2011-05-02 - 2012-04-30", "MILE", false, null, "GTMS - Grant - Milestone", new KualiDecimal(100000.00), KualiDecimal.ZERO, new KualiDecimal(99987.00), KualiDecimal.ZERO, KualiDecimal.ZERO, null);

    private String documentNumber;
    private String comment;
    private String awardDateRange;
    private String billingFrequency;
    private boolean finalBillIndicator;
    private String billingPeriod;
    private String instrumentTypeCode;
    private KualiDecimal awardTotal = KualiDecimal.ZERO;
    private KualiDecimal newTotalBilled = KualiDecimal.ZERO;
    private KualiDecimal amountRemainingToBill = KualiDecimal.ZERO;
    private KualiDecimal billedToDateAmount = KualiDecimal.ZERO;
    private KualiDecimal costShareAmount = KualiDecimal.ZERO;
    private Date lastBilledDate;

    private InvoiceGeneralDetailFixture(String documentNumber, String comment, String awardDateRange, String billingFrequency, boolean finalBillIndicator, String billingPeriod, String instrumentTypeCode, KualiDecimal awardTotal, KualiDecimal newTotalBilled, KualiDecimal amountRemainingToBill, KualiDecimal billedToDateAmount, KualiDecimal costShareAmount, Date lastBilledDate) {
        this.documentNumber = documentNumber;
        this.comment = comment;
        this.awardDateRange = awardDateRange;
        this.billingFrequency = billingFrequency;
        this.finalBillIndicator = finalBillIndicator;
        this.billingPeriod = billingPeriod;
        this.instrumentTypeCode = instrumentTypeCode;
        this.awardTotal = awardTotal;
        this.newTotalBilled = newTotalBilled;
        this.amountRemainingToBill = amountRemainingToBill;
        this.billedToDateAmount = billedToDateAmount;
        this.costShareAmount = costShareAmount;
        this.lastBilledDate = lastBilledDate;
    }

    public InvoiceGeneralDetail createInvoiceGeneralDetail() {
        InvoiceGeneralDetail invoiceGeneralDetail = new InvoiceGeneralDetail();
        invoiceGeneralDetail.setDocumentNumber(this.documentNumber);
        invoiceGeneralDetail.setComment(comment);
        invoiceGeneralDetail.setAwardDateRange(awardDateRange);
        invoiceGeneralDetail.setBillingFrequency(billingFrequency);
        invoiceGeneralDetail.setFinalBillIndicator(finalBillIndicator);
        invoiceGeneralDetail.setBillingPeriod(billingPeriod);
        invoiceGeneralDetail.setInstrumentTypeCode(instrumentTypeCode);
        invoiceGeneralDetail.setAwardTotal(awardTotal);
        invoiceGeneralDetail.setNewTotalBilled(newTotalBilled);
        invoiceGeneralDetail.setAmountRemainingToBill(amountRemainingToBill);
        invoiceGeneralDetail.setBilledToDateAmount(billedToDateAmount);
        invoiceGeneralDetail.setCostShareAmount(costShareAmount);
        invoiceGeneralDetail.setLastBilledDate(lastBilledDate);
        return invoiceGeneralDetail;
    }
}
