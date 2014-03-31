/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.bean.TravelReimbursementMvcWrapperBean;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.web.ui.ExtraButton;

/**
 * Travel Reimbursement Form
 *
 */
public class TravelReimbursementForm extends TravelFormBase implements TravelReimbursementMvcWrapperBean {
    private List<Serializable> history;
    private Date startDate;
    private Date endDate;

    private boolean canReturn;
    private boolean canCertify;
    private boolean canUnmask = false;
    private TravelAdvance newTravelAdvanceLine;

    private String travelDocumentIdentifier;

    /**
     * Constructor
     */
    public TravelReimbursementForm() {
        super();
    }

    /**
     *
     */
    @Override
    public void populate(final HttpServletRequest request) {

        //get original dates
        final Date startDateIn = getTravelReimbursementDocument().getTripBegin();
        final Date endDateIn = getTravelReimbursementDocument().getTripEnd();

        //populate new stuff
        super.populate(request);

        final Date currentStart = getTravelReimbursementDocument().getTripBegin();
        final Date currentEnd = getTravelReimbursementDocument().getTripEnd();
        if (currentStart != null) {
            setStartDate(currentStart);
        }
        if (currentEnd != null) {
            setEndDate(currentEnd);
        }

   }

    @Override
    public boolean isDefaultOpenPaymentInfoTab() {
        if(TemConstants.TravelReimbursementStatusCodeKeys.AWAIT_TRVL_MGR.equals(getDocument().getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus())) {
            return true;
        }

        return super.isDefaultOpenPaymentInfoTab();
    }

    /**
     * Creates a MAP for all the buttons to appear on the Travel Authorization Form, and sets the attributes of these buttons.
     *
     * @return the button map created.
     */
    protected Map<String, ExtraButton> createButtonsMap() {
        final HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();

        result.putAll(createDVExtraButtonMap());
        result.putAll(createNewReimbursementButtonMap());

        return result;
    }

    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();
        final Map<String, ExtraButton> buttonsMap = createButtonsMap();

        boolean enablePayments = getParameterService().getParameterValueAsBoolean(TravelReimbursementDocument.class, VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND);
        if (enablePayments && !SpringContext.getBean(TravelDocumentService.class).isUnsuccessful(this.getTravelDocument())){
            if (getTravelReimbursementDocument().canPayDVToVendor()) {
                extraButtons.add(buttonsMap.get("methodToCall.payDVToVendor"));
            }
        }

        if (getDocumentActions().keySet().contains(TemConstants.TravelAuthorizationActions.CAN_NEW_REIMBURSEMENT)) {
            extraButtons.add(buttonsMap.get("methodToCall.newReimbursement"));
        }

        return extraButtons;
    }

    @Override
    public boolean canCertify() {
        return canCertify;
    }

    @Override
    public boolean getCanCertify() {
        return canCertify;
    }

    @Override
    public void setCanCertify(final boolean canCertify) {
        this.canCertify = canCertify;
    }

    /**
     * Get Travel Reimbursement Document
     *
     * @return TravelReimbursementForm
     */
    @Override
    public TravelReimbursementDocument getTravelReimbursementDocument() {
        return (TravelReimbursementDocument) getDocument();
    }

    /**
     * Retrieve the name of the document identifier field for datadictionary queries
     *
     * @return String with the field name of the document identifier
     */
    @Override
    protected String getDocumentIdentifierFieldName() {
        return "travelDocumentIdentifier";
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT;
    }

    @Override
    public List<Serializable> getHistory() {
        return this.history;
    }

    @Override
    public void setHistory(final List<Serializable> history) {
        this.history = history;
    }

    /**
     * Gets the startDate attribute.
     *
     * @return Returns the startDate.
     */
    @Override
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Sets the startDate attribute value.
     *
     * @param startDate The startDate to set.
     */
    @Override
    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Gets the endDate attribute.
     *
     * @return Returns the endDate.
     */
    @Override
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Sets the endDate attribute value.
     *
     * @param endDate The endDate to set.
     */
    @Override
    public void setEndDate(final Date endDate) {
        try{
            this.endDate = endDate;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Pull the object code for the new line from the distribution, not the trip type
     * @param financialDocument the financial document which needs a new accounting line
     * @return a new accounting line for the form
     */
    @Override
    protected SourceAccountingLine createNewSourceAccountingLine(AccountingDocument financialDocument) {
        SourceAccountingLine accountingLine = super.createNewSourceAccountingLine(financialDocument);
        accountingLine.setFinancialObjectCode(KFSConstants.EMPTY_STRING);
        return accountingLine;
    }

    protected TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    public PersonService getPersonService() {
        return SpringContext.getBean(PersonService.class);
    }

    protected TravelAuthorizationService getTravelAuthorizationService() {
        return SpringContext.getBean(TravelAuthorizationService.class);
    }

    @Override
    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    public TravelAdvance getNewTravelAdvanceLine() {
        return newTravelAdvanceLine;
    }

    public void setNewAdvanceLine(TravelAdvance newTravelAdvanceLine) {
        this.newTravelAdvanceLine = newTravelAdvanceLine;
    }

    public boolean isCanUnmask() {
        return canUnmask;
    }

    public void setCanUnmask(boolean canUnmask) {
        this.canUnmask = canUnmask;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelFormBase#getTravelPaymentFormAction()
     */
    @Override
    public String getTravelPaymentFormAction() {
        return TemConstants.TRAVEL_REIMBURESMENT_ACTION_NAME;
    }

    /**
     * @return the travel document identifier if it has been set
     */
    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    /**
     * Sets the travel document identifier to populate from
     * @param travelDocumentIdentifier the travel document identifier to populate from
     */
    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }
}
