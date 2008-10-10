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
 * Created on Jul 12, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.sys.businessobject.TimestampedBusinessObjectBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * 
 */

public class PaymentDetail extends TimestampedBusinessObjectBase {
    private static BigDecimal zero = new BigDecimal(0);

    private KualiInteger id; // PMT_DTL_ID
    private String invoiceNbr; // INV_NBR
    private Timestamp invoiceDate; // INV_DT
    private String purchaseOrderNbr; // PO_NBR
    private String custPaymentDocNbr; // CUST_PMT_DOC_NBR
    private String financialDocumentTypeCode; // FDOC_TYP_CD
    private String requisitionNbr; // REQS_NBR
    private String organizationDocNbr; // ORG_DOC_NBR
    private BigDecimal origInvoiceAmount; // ORIG_INV_AMT
    private BigDecimal netPaymentAmount; // NET_PMT_AMT
    private BigDecimal invTotDiscountAmount; // INV_TOT_DSCT_AMT
    private BigDecimal invTotShipAmount; // INV_TOT_SHP_AMT
    private BigDecimal invTotOtherDebitAmount; // INV_TOT_OTHR_DEBIT_AMT
    private BigDecimal invTotOtherCreditAmount; // INV_TOT_OTHR_CRDT_AMT
    private Boolean primaryCancelledPayment; // PDP_PRM_PMT_CNCL_IND
    private Timestamp lastDisbursementActionDate;

    private List<PaymentAccountDetail> accountDetail = new ArrayList<PaymentAccountDetail>();
    private List<PaymentNoteText> notes = new ArrayList<PaymentNoteText>();

    private KualiInteger paymentGroupId;
    private PaymentGroup paymentGroup;

    public PaymentDetail() {
        super();
    }

    public boolean isDetailAmountProvided() {
        return (origInvoiceAmount != null) || (invTotDiscountAmount != null) || (invTotShipAmount != null) || (invTotOtherDebitAmount != null) || (invTotOtherCreditAmount != null);
    }

    public BigDecimal getCalculatedPaymentAmount() {
        BigDecimal orig_invoice_amt = origInvoiceAmount == null ? zero : origInvoiceAmount;
        BigDecimal invoice_tot_discount_amt = invTotDiscountAmount == null ? zero : invTotDiscountAmount;
        BigDecimal invoice_tot_ship_amt = invTotShipAmount == null ? zero : invTotShipAmount;
        BigDecimal invoice_tot_other_debits = invTotOtherDebitAmount == null ? zero : invTotOtherDebitAmount;
        BigDecimal invoice_tot_other_credits = invTotOtherCreditAmount == null ? zero : invTotOtherCreditAmount;

        BigDecimal t = orig_invoice_amt.subtract(invoice_tot_discount_amt);
        t = t.add(invoice_tot_ship_amt);
        t = t.add(invoice_tot_other_debits);
        t = t.subtract(invoice_tot_other_credits);

        return t;
    }

    public boolean isDisbursementActionAllowed() {
        if (paymentGroup.getDisbursementDate() == null) {
            if ("EXTR".equals(paymentGroup.getPaymentStatus().getCode())) {
                return false;
            }
            return true;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(paymentGroup.getDisbursementDate());
        c.set(Calendar.HOUR, 11);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 59);
        c.set(Calendar.AM_PM, Calendar.PM);
        Timestamp disbursementDate = new Timestamp(c.getTimeInMillis());
        // date is equal to or after lastActionDate Allowed
        return ((disbursementDate.compareTo(this.lastDisbursementActionDate)) >= 0);
    }

    /**
     * @return total of all account detail amounts
     */
    public BigDecimal getAccountTotal() {
        BigDecimal acctTotal = new BigDecimal(0.00);

        for (PaymentAccountDetail paymentAccountDetail : accountDetail) {
            if (paymentAccountDetail.getAccountNetAmount() != null) {
                acctTotal = acctTotal.add(paymentAccountDetail.getAccountNetAmount());
            }
        }

        return acctTotal;
    }

    public Timestamp getLastDisbursementActionDate() {
        return lastDisbursementActionDate;
    }

    public void setLastDisbursementActionDate(Timestamp lastDisbursementActionDate) {
        this.lastDisbursementActionDate = lastDisbursementActionDate;
    }

    public Timestamp getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(Timestamp invoiceDate) {
        this.invoiceDate = invoiceDate;
    }
    
    /**
     * Takes a <code>String</code> and attempt to format as <code>Timestamp</code for setting the
     * invoiceDate field
     * 
     * @param invoiceDate Timestamp as string
     */
    public void setInvoiceDate(String invoiceDate) throws ParseException {
        this.invoiceDate = new Timestamp(SpringContext.getBean(DateTimeService.class).convertToSqlDate(invoiceDate).getTime());
    }

    /**
     * @hibernate.set name="accountDetail"
     * @hibernate.collection-key column="pmt_dtl_id"
     * @hibernate.collection-one-to-many class="edu.iu.uis.pdp.bo.PaymentAccountDetail"
     */
    public List<PaymentAccountDetail> getAccountDetail() {
        return accountDetail;
    }

    public void setAccountDetail(List<PaymentAccountDetail> ad) {
        accountDetail = ad;
    }

    public void addAccountDetail(PaymentAccountDetail pad) {
        pad.setPaymentDetail(this);
        accountDetail.add(pad);
    }

    public void deleteAccountDetail(PaymentAccountDetail pad) {
        accountDetail.remove(pad);
    }

    public List<PaymentNoteText> getNotes() {
        return notes;
    }

    public void setNotes(List<PaymentNoteText> n) {
        notes = n;
    }

    public void addNote(PaymentNoteText pnt) {
        pnt.setPaymentDetail(this);
        notes.add(pnt);
    }
    
    /**
     * Constructs a new <code>PaymentNoteText</code> for the given payment text and adds to the detail <code>List</code>
     * 
     * @param paymentText note text
     */
    public void addPaymentText(String paymentText) {
        PaymentNoteText paymentNoteText = new PaymentNoteText();

        paymentNoteText.setCustomerNoteText(paymentText);
        paymentNoteText.setCustomerNoteLineNbr(new KualiInteger(this.notes.size() + 1));

        addNote(paymentNoteText);
    }

    public void deleteNote(PaymentNoteText pnt) {
        notes.remove(pnt);
    }

    /**
     * @hibernate.id column="PMT_DTL_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_PMT_DTL_ID_SEQ"
     * @return
     */
    public KualiInteger getId() {
        return id;
    }

    /**
     * @return
     * @hibernate.property column="CUST_PMT_DOC_NBR" length="9"
     */
    public String getCustPaymentDocNbr() {
        return custPaymentDocNbr;
    }

    /**
     * @return
     * @hibernate.property column="INV_NBR" length="14"
     */
    public String getInvoiceNbr() {
        return invoiceNbr;
    }

    /**
     * @return
     * @hibernate.property column="INV_TOT_DSCT_AMT" length="14"
     */
    public BigDecimal getInvTotDiscountAmount() {
        return invTotDiscountAmount;
    }

    /**
     * @return
     * @hibernate.property column="INV_TOT_OTHR_CRDT_AMT" length="14"
     */
    public BigDecimal getInvTotOtherCreditAmount() {
        return invTotOtherCreditAmount;
    }

    /**
     * @return
     * @hibernate.property column="INV_TOT_OTHR_DEBIT_AMT" length="14"
     */
    public BigDecimal getInvTotOtherDebitAmount() {
        return invTotOtherDebitAmount;
    }

    /**
     * @return
     * @hibernate.property column="INV_TOT_SHP_AMT" length="14"
     */
    public BigDecimal getInvTotShipAmount() {
        return invTotShipAmount;
    }

    /**
     * @return
     * @hibernate.property column="NET_PMT_AMT" length="14"
     */
    public BigDecimal getNetPaymentAmount() {
        return netPaymentAmount;
    }

    /**
     * @return
     * @hibernate.property column="ORG_DOC_NBR" length="10"
     */
    public String getOrganizationDocNbr() {
        return organizationDocNbr;
    }

    /**
     * @return
     * @hibernate.property column="ORIG_INV_AMT" length="14"
     */
    public BigDecimal getOrigInvoiceAmount() {
        return origInvoiceAmount;
    }

    /**
     * @return
     * @hibernate.property column="PO_NBR" length="9"
     */
    public String getPurchaseOrderNbr() {
        return purchaseOrderNbr;
    }

    /**
     * @return
     * @hibernate.property column="REQS_NBR" length=8"
     */
    public String getRequisitionNbr() {
        return requisitionNbr;
    }

    /**
     * @return Returns the paymentGroup.
     */
    public PaymentGroup getPaymentGroup() {
        return paymentGroup;
    }

    /**
     * @param string
     */
    public void setCustPaymentDocNbr(String string) {
        custPaymentDocNbr = string;
    }

    /**
     * @param integer
     */
    public void setId(KualiInteger integer) {
        id = integer;
    }

    /**
     * @param string
     */
    public void setInvoiceNbr(String string) {
        invoiceNbr = string;
    }

    /**
     * @param decimal
     */
    public void setInvTotDiscountAmount(BigDecimal decimal) {
        invTotDiscountAmount = decimal;
    }

    /**
     * @param decimal
     */
    public void setInvTotOtherCreditAmount(BigDecimal decimal) {
        invTotOtherCreditAmount = decimal;
    }

    /**
     * @param decimal
     */
    public void setInvTotOtherDebitAmount(BigDecimal decimal) {
        invTotOtherDebitAmount = decimal;
    }

    /**
     * @param decimal
     */
    public void setInvTotShipAmount(BigDecimal decimal) {
        invTotShipAmount = decimal;
    }

    /**
     * @param decimal
     */
    public void setNetPaymentAmount(BigDecimal decimal) {
        netPaymentAmount = decimal;
    }

    /**
     * @param string
     */
    public void setOrganizationDocNbr(String string) {
        organizationDocNbr = string;
    }

    /**
     * @param decimal
     */
    public void setOrigInvoiceAmount(BigDecimal decimal) {
        origInvoiceAmount = decimal;
    }

    /**
     * @param string
     */
    public void setPurchaseOrderNbr(String string) {
        purchaseOrderNbr = string;
    }

    /**
     * @param string
     */
    public void setRequisitionNbr(String string) {
        requisitionNbr = string;
    }

    /**
     * @return Returns the financialDocumentTypeCode.
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    /**
     * @return Returns the primaryCancelledPayment.
     */
    public Boolean getPrimaryCancelledPayment() {
        return primaryCancelledPayment;
    }

    /**
     * @param primaryCancelledPayment The primaryCancelledPayment to set.
     */
    public void setPrimaryCancelledPayment(Boolean primaryCancelledPayment) {
        this.primaryCancelledPayment = primaryCancelledPayment;
    }

    /**
     * @param paymentGroup The paymentGroup to set.
     */
    public void setPaymentGroup(PaymentGroup paymentGroup) {
        this.paymentGroup = paymentGroup;
    }

    /**
     * Gets the paymentGroupId attribute.
     * 
     * @return Returns the paymentGroupId.
     */
    public KualiInteger getPaymentGroupId() {
        return paymentGroupId;
    }

    /**
     * Sets the paymentGroupId attribute value.
     * 
     * @param paymentGroupId The paymentGroupId to set.
     */
    public void setPaymentGroupId(KualiInteger paymentGroupId) {
        this.paymentGroupId = paymentGroupId;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PaymentDetail)) {
            return false;
        }
        PaymentDetail o = (PaymentDetail) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(61, 67).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }

    /**
     * Returns the value of the system parameter that contains the disbursement cancellation email address
     */
    public String getDisbursementCancellationEmailAddress() {
        return SpringContext.getBean(ParameterService.class).getParameterValue(PaymentDetail.class, PdpParameterConstants.DISBURSEMENT_CANCELLATION_EMAIL_ADDRESSES);
    }

}
