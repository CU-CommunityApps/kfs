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
package org.kuali.kfs.module.tem.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.batch.TaxableRamificationNotificationStep;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * if the cash advance is not cleared on an expense report, the system will generate a tax ramification document showing all taxable
 * income of travelers/hosts.
 */
public class TaxableRamificationDocument extends FinancialSystemTransactionalDocumentBase {
    private final static Logger LOG = Logger.getLogger(TaxableRamificationDocument.class);

    private String arInvoiceDocNumber;
    private Date dueDate;

    private KualiDecimal invoiceAmount;
    private KualiDecimal openAmount;

    private String taxableRamificationNotice;

    private String travelDocumentIdentifier;
    private Integer travelerDetailId;
    private String travelAdvanceDocumentNumber;

    private TravelerDetail travelerDetail;
    private TravelAdvance travelAdvance;

    private List<TemSourceAccountingLine> advanceAccountingLines;

    /**
     * Gets the arInvoiceDocNumber attribute.
     *
     * @return Returns the arInvoiceDocNumber.
     */
    public String getArInvoiceDocNumber() {
        return arInvoiceDocNumber;
    }

    /**
     * Sets the arInvoiceDocNumber attribute value.
     *
     * @param arInvoiceDocNumber The arInvoiceDocNumber to set.
     */
    public void setArInvoiceDocNumber(String arInvoiceDocNumber) {
        this.arInvoiceDocNumber = arInvoiceDocNumber;
    }

    /**
     * Gets the dueDate attribute.
     *
     * @return Returns the dueDate.
     */
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the dueDate attribute value.
     *
     * @param dueDate The dueDate to set.
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the invoiceAmount attribute.
     *
     * @return Returns the invoiceAmount.
     */
    public KualiDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute value.
     *
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(KualiDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the openAmount attribute.
     *
     * @return Returns the openAmount.
     */
    public KualiDecimal getOpenAmount() {
        return openAmount;
    }

    /**
     * Sets the openAmount attribute value.
     *
     * @param openAmount The openAmount to set.
     */
    public void setOpenAmount(KualiDecimal openAmount) {
        this.openAmount = openAmount;
    }

    /**
     * Gets the travelerDetail attribute.
     *
     * @return Returns the travelerDetail.
     */
    public TravelerDetail getTravelerDetail() {
        return travelerDetail;
    }

    /**
     * Sets the travelerDetail attribute value.
     *
     * @param travelerDetail The travelerDetail to set.
     */
    public void setTravelerDetail(TravelerDetail travelerDetail) {
        this.travelerDetail = travelerDetail;
    }

    /**
     * Gets the travelAdvance attribute.
     *
     * @return Returns the travelAdvance.
     */
    public TravelAdvance getTravelAdvance() {
        return travelAdvance;
    }

    /**
     * Sets the travelAdvance attribute value.
     *
     * @param travelAdvance The travelAdvance to set.
     */
    public void setTravelAdvance(TravelAdvance travelAdvance) {
        this.travelAdvance = travelAdvance;
    }

    /**
     * Gets the travelDocumentIdentifier attribute.
     *
     * @return Returns the travelDocumentIdentifier.
     */
    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    /**
     * Sets the travelDocumentIdentifier attribute value.
     *
     * @param travelDocumentIdentifier The travelDocumentIdentifier to set.
     */
    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }

    /**
     * Gets the travelerDetailId attribute.
     *
     * @return Returns the travelerDetailId.
     */
    public Integer getTravelerDetailId() {
        return travelerDetailId;
    }

    /**
     * Sets the travelerDetailId attribute value.
     *
     * @param travelerDetailId The travelerDetailId to set.
     */
    public void setTravelerDetailId(Integer travelerDetailId) {
        this.travelerDetailId = travelerDetailId;
    }

    /**
     * Gets the travelAdvanceId attribute.
     *
     * @return Returns the travelAdvanceId.
     */
    public String getTravelDocumentNumber() {
        return travelAdvanceDocumentNumber;
    }

    /**
     * Sets the travelAdvanceId attribute value.
     *
     * @param travelAdvanceId The travelAdvanceId to set.
     */
    public void setTravelAdvanceDocumentNumber(String travelAdvanceDocumentNumber) {
        this.travelAdvanceDocumentNumber = travelAdvanceDocumentNumber;
    }

    /**
     * Gets the taxableRamificationNotice attribute.
     * @return Returns the taxableRamificationNotice.
     */
    public String getTaxableRamificationNotice() {
        return taxableRamificationNotice;
    }

    /**
     * Sets the taxableRamificationNotice attribute value.
     * @param taxableRamificationNotice The taxableRamificationNotice to set.
     */
    public void setTaxableRamificationNotice(String taxableRamificationNotice) {
        this.taxableRamificationNotice = taxableRamificationNotice;
    }

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.RouteNodeNames.REQUIRED_ACCOUNT_NOTIFICATION)) {
            return requireFiscalOfficerFYI();
        }

        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    protected boolean requireFiscalOfficerFYI() {
        return this.getParameterService().getParameterValueAsBoolean(TaxableRamificationNotificationStep.class, TemConstants.TaxRamificationParameter.SEND_FYI_TO_FISCAL_OFFICER_IND);
    }

    /**
     * Read only method to look up accounting lines associated with the advance.  Couldn't do this in OJB because collection-descriptor
     * assumes the collection is related to your PK.  Because it's stupid.
     * @return the accounting lines associated with the travel advance associated with this document
     */
    public List<TemSourceAccountingLine> getAdvanceAccountingLines() {
        if (advanceAccountingLines == null) {
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, getTravelDocumentNumber());
            fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_TYPE_CODE, TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE);

            advanceAccountingLines = new ArrayList<TemSourceAccountingLine>();
            advanceAccountingLines.addAll(SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(TemSourceAccountingLine.class, fieldValues, KFSPropertyConstants.SEQUENCE_NUMBER, true));
        }
        return advanceAccountingLines;
    }

}
