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

import static org.kuali.kfs.module.tem.TemConstants.CERTIFICATION_STATEMENT_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.EMPLOYEE_TEST_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_ACCOUNT_DISTRIBUTION_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND;
import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_EMERGENCY_CONTACT_LINE;
import static org.kuali.kfs.sys.KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.NEW_SOURCE_LINE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.SingleConfirmationQuestion;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.authorization.TravelAuthorizationAuthorizer;
import org.kuali.kfs.module.tem.document.validation.event.AddEmergencyContactLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelAuthorizationMvcWrapperBean;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.BlankFormFile;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Handles action events through the struts framework for the {@link TravelAuthorizationDocument}
 */
public class TravelAuthorizationAction extends TravelActionBase {

    public static Logger LOG = Logger.getLogger(TravelAuthorizationAction.class);

    public static final String DOCUMENT_ERROR_PREFIX = "document.";
    public static final String NEW_SOURCE_LINE_OBJECT_CODE = String.format("%s.%s", NEW_SOURCE_LINE, FINANCIAL_OBJECT_CODE);
    private DocumentDao documentDao;

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        // try pulling the transpo modes from the form or the request
        String[] transpoModes = request.getParameterValues("selectedTransportationModes");

        final ActionForward retval = super.execute(mapping, form, request, response);
        TravelAuthorizationDocument travelAuthDocument = (TravelAuthorizationDocument) authForm.getDocument();
        if (transpoModes != null) {
            authForm.setSelectedTransportationModes(Arrays.asList(transpoModes));
        }
        else {
            authForm.setSelectedTransportationModes(authForm.getTravelAuthorizationDocument().getTransportationModeCodes());
        }
        refreshTransportationModesAfterButtonAction(travelAuthDocument, authForm);

        // should we refresh the trip type, upon which so much depends?  let's check and do so if we need to
        if (!StringUtils.isBlank(travelAuthDocument.getTripTypeCode())) {
            if (ObjectUtils.isNull(travelAuthDocument.getTripType()) || !StringUtils.equals(travelAuthDocument.getTripType().getCode(), travelAuthDocument.getTripTypeCode())) {
                travelAuthDocument.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
            }
        } else {
            travelAuthDocument.setTripType(null);
        }

        if (travelAuthDocument.getTraveler() != null && travelAuthDocument.getTraveler().getPrincipalId() != null) {
            travelAuthDocument.getTraveler().setPrincipalName(getPersonService().getPerson(travelAuthDocument.getTraveler().getPrincipalId()).getPrincipalName());
        }

        setButtonPermissions(authForm);
        String perDiemPercentage = getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.FIRST_AND_LAST_DAY_PER_DIEM_PERCENTAGE);
        final String travelIdentifier = travelAuthDocument.getTravelDocumentIdentifier();

        authForm.setPerDiemPercentage(perDiemPercentage);

        disablePerDiemExpenes(travelAuthDocument);

        if(ObjectUtils.isNotNull(travelAuthDocument.getActualExpenses())){
            travelAuthDocument.enableExpenseTypeSpecificFields(travelAuthDocument.getActualExpenses());
        }

        LOG.debug("Got "+ authForm.getRelatedDocuments().size()+ " related documents");

        if (!isReturnFromObjectCodeLookup(authForm, request)) {
            getTravelEncumbranceService().updateEncumbranceObjectCode(travelAuthDocument, authForm.getNewSourceLine());
        }

        getMessages(authForm);

        // update the list of related documents
        refreshRelatedDocuments(authForm);

        if (((TravelFormBase) form).getMethodToCall().equalsIgnoreCase(KFSConstants.DOC_HANDLER_METHOD) && travelAuthDocument.getPrimaryDestinationId() != null){
            if (travelAuthDocument.getPrimaryDestinationId().intValue() == TemConstants.CUSTOM_PRIMARY_DESTINATION_ID){
                travelAuthDocument.getPrimaryDestination().setPrimaryDestinationName(travelAuthDocument.getPrimaryDestinationName());
                travelAuthDocument.getPrimaryDestination().setCounty(travelAuthDocument.getPrimaryDestinationCounty());
                travelAuthDocument.getPrimaryDestination().getRegion().setRegionName(travelAuthDocument.getPrimaryDestinationCountryState());
            }
        }

        request.setAttribute(CERTIFICATION_STATEMENT_ATTRIBUTE, getCertificationStatement(travelAuthDocument));
        request.setAttribute(EMPLOYEE_TEST_ATTRIBUTE, isEmployee(travelAuthDocument.getTraveler()));

        // force recalculate
        if(!getCalculateIgnoreList().contains(authForm.getMethodToCall())){
            recalculateTripDetailTotalOnly(mapping, form, request, response);
        }

        setupTravelAdvances(authForm);
        travelAuthDocument.propagateAdvanceInformationIfNeeded();
        populateAnyMissingAdvanceAccountingLineObjectCodes(authForm);

        if (travelAuthDocument.getTravelAdvances() != null && !travelAuthDocument.getTravelAdvances().isEmpty()) {
            authForm.setShowTravelAdvancesForTrip(true);
        }

        setTabStateForEmergencyContacts(authForm);

        return retval;
    }

    /**
     * Decides whether to show the tab which allows the traveler to accept the rules with receiving a travel advance
     * @param form the form for the travel authorization
     */
    private void setupTravelAdvances(TravelAuthorizationForm form) {
        TravelAuthorizationDocument document = form.getTravelAuthorizationDocument();
        boolean waitingOnTraveler = document.getAppDocStatus().equals(TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_TRVLR);
        String initiator = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        String travelerID = "";
        boolean showPolicy = false;
        if (document.getTraveler() != null){
            travelerID = document.getTraveler().getPrincipalId();
            if (travelerID != null){
                //traveler must accept policy, if initiator is arranger, the traveler will have to accept later.
                showPolicy = initiator.equals(travelerID) || GlobalVariables.getUserSession().getPrincipalId().equals(travelerID);
            }
            else{ //Non-kim traveler, arranger accepts policy
                showPolicy = true;
            }
        }

        form.setWaitingOnTraveler(waitingOnTraveler);
        form.setShowPolicy(showPolicy);
    }

    /**
     * Extended to handle the overrides for the advances accounting lines
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#processAccountingLineOverrides(org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase)
     */
    @Override
    protected void processAccountingLineOverrides(KualiAccountingDocumentFormBase transForm) {
        super.processAccountingLineOverrides(transForm);
        processAccountingLineOverrides(((TravelAuthorizationForm)transForm).getNewAdvanceAccountingLine());
        if (transForm.hasDocumentId()) {
            TravelAuthorizationDocument authorizationDocument = (TravelAuthorizationDocument) transForm.getDocument();
            if (!ObjectUtils.isNull(authorizationDocument.getAdvanceAccountingLines()) && !authorizationDocument.getAdvanceAccountingLines().isEmpty()) {
                processAccountingLineOverrides(authorizationDocument,authorizationDocument.getAdvanceAccountingLines());
            }
        }
    }

    /**
     * If the parameter KFS-TEM / TravelAuthorization / TRAVEL_ADVANCE_OBJECT_CODE is set and some of the advance accounting lines do not have object codes set, fill the object codes in
     * @param form the form with the document to update
     */
    protected void populateAnyMissingAdvanceAccountingLineObjectCodes(TravelAuthorizationForm form) {
        final String advanceAccountingLineObjectCode = getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.TRAVEL_ADVANCE_OBJECT_CODE, KFSConstants.EMPTY_STRING);

        if (!StringUtils.isBlank(advanceAccountingLineObjectCode)) {
            final TravelAuthorizationDocument authorizationDocument = form.getTravelAuthorizationDocument();
            if (authorizationDocument.getAdvanceAccountingLines() != null && !authorizationDocument.getAdvanceAccountingLines().isEmpty()) {
                for (TemSourceAccountingLine accountingLine : authorizationDocument.getAdvanceAccountingLines()) {
                    if (StringUtils.isBlank(accountingLine.getFinancialObjectCode())) {
                        accountingLine.setFinancialObjectCode(advanceAccountingLineObjectCode);
                    }
                }
            }
        }
    }

    /**
     * This action executes an insert of an accounting line associated with an advance
     * @param mapping this is the mapping
     * @param form this is the form
     * @param request we have to assume this is a "request" of some sort
     * @param response is this used?  for...something?
     * @return ActionForward we promise to return one of these things
     * @throws Exception 'cause sometimes bad things happen
     */
    public ActionForward insertAdvanceAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authorizationDocumentForm = (TravelAuthorizationForm) form;
        TemSourceAccountingLine line = authorizationDocumentForm.getNewAdvanceAccountingLine();

        boolean rulePassed = true;
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(TemPropertyConstants.NEW_ADVANCE_ACCOUNTING_LINE, authorizationDocumentForm.getDocument(), line));

        if (rulePassed) {
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(line);
            authorizationDocumentForm.setAnchor(TemConstants.SOURCE_ANCHOR);
            authorizationDocumentForm.getTravelAuthorizationDocument().addAdvanceAccountingLine(line);
            authorizationDocumentForm.setNewAdvanceAccountingLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will remove an accounting line associated with the travel advance from a FinancialDocument. This assumes that the user presses the delete button
     * for a specific accounting line on the document and that the document is represented by a FinancialDocumentFormBase.  If these assumptions are not meant...wow.
     * That's trouble, isn't it?
     * @param mapping a thing
     * @param form another thing
     * @param request I'm sure the Struts documentation covers this in detail
     * @param response for the remaining amount of time we're using Struts
     * @return ActionForward ah well...enjoy this, upcoming javadoc'ers
     * @throws Exception thrown if a bad thing happens
     */
    public ActionForward deleteAdvanceAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authorizationDocumentForm = (TravelAuthorizationForm) form;

        int deleteIndex = getLineToDelete(request);
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + TemPropertyConstants.ADVANCE_ACCOUNTING_LINES + "[" + deleteIndex + "]";
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new DeleteAccountingLineEvent(errorPath, authorizationDocumentForm.getDocument(), authorizationDocumentForm.getTravelAuthorizationDocument().getAdvanceAccountingLine(deleteIndex), false));

        // if the rule evaluation passed, let's delete it
        if (rulePassed) {
            authorizationDocumentForm.setAnchor(TemConstants.SOURCE_ANCHOR);
            authorizationDocumentForm.getTravelAuthorizationDocument().getAdvanceAccountingLines().remove(deleteIndex);
        }
        else {
            String[] errorParams = new String[] { "source", Integer.toString(deleteIndex + 1) };
            GlobalVariables.getMessageMap().putError(errorPath, KFSKeyConstants.ERROR_ACCOUNTINGLINE_DELETERULE_INVALIDACCOUNT, errorParams);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Takes care of storing the action form in the User session and forwarding to the balance inquiry report menu action for an accounting line
     * associated with a travel advance
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryForAdvanceAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemSourceAccountingLine line = getAdvanceAccountingLine(form, request);
        return performBalanceInquiryForAccountingLine(mapping, form, request, line);
    }

    /**
     * This method is a helper method that will return a source accounting line. The reason we're making it protected in here is so
     * that we can override this method in some of the modules. PurchasingActionBase is one of the subclasses that will be
     * overriding this, because in PurchasingActionBase, we'll need to get the source accounting line using both an item index and
     * an account index.
     *
     * @param form
     * @param request
     * @param isSource
     * @return
     */
    protected TemSourceAccountingLine getAdvanceAccountingLine(ActionForm form, HttpServletRequest request) {
        final int lineIndex = getSelectedLine(request);
        TemSourceAccountingLine line = (TemSourceAccountingLine) ObjectUtils.deepCopy(((TravelAuthorizationForm) form).getTravelAuthorizationDocument().getAdvanceAccountingLine(lineIndex));
        return line;
    }

    /**
     * Determines if the request is a return from an Object Code lookup. In this case we want special treatment. We know the return
     * from a lookup by checking the refreshCaller attribute. We know it's for an object code when an object code for a
     * newSourceLine is present in the request parameter.
     *
     * @param form to get refresh caller from
     * @param request to get the object code parameter from
     * @return boolean true if return from an object code lookup or false otherwise
     */
    protected boolean isReturnFromObjectCodeLookup(final TravelAuthorizationForm form, final HttpServletRequest request) {
        return (form.getRefreshCaller() != null && request.getParameter(NEW_SOURCE_LINE_OBJECT_CODE) != null);
    }

    private void getMessages(TravelAuthorizationForm authForm) {
        TravelAuthorizationDocument document = authForm.getTravelAuthorizationDocument();

        if (!StringUtils.isBlank(document.getAppDocStatus())) {
            if (document.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD)) {
                KNSGlobalVariables.getMessageList().add(TemKeyConstants.TA_MESSAGE_HOLD_DOCUMENT_TEXT, new String[] { document.getHoldRequestorPersonName() });
            }
            else if (document.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION)) {
                KNSGlobalVariables.getMessageList().add(TemKeyConstants.TA_MESSAGE_RETIRED_DOCUMENT_TEXT);
            }
            else if (document.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT)) {
                KNSGlobalVariables.getMessageList().add(TemKeyConstants.TA_MESSAGE_AMEND_DOCUMENT_TEXT);
            }
        }
    }

    /**
     * This method sets all the boolean properties on the form to determine what buttons can be displayed depending on what is going
     * on
     */
    protected void setButtonPermissions(TravelAuthorizationForm authForm) {
        canSave(authForm);
        setCanCalculate(authForm);
        setCanReturnToFisicalOfficer(authForm);
        hideButtons(authForm);
    }

    protected void hideButtons(TravelAuthorizationForm authForm) {
        boolean can = false;

        TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
        can = documentAuthorizer.hideButtons(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        if (can){
            authForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
            authForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_CLOSE);
            authForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_SEND_ADHOC_REQUESTS);
            authForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_COPY);
            authForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_RELOAD);
        }
    }

    protected void canSave(TravelAuthorizationForm authForm) {
        boolean can = !(isFinal(authForm) || isProcessed(authForm));
        if (can) {
            TravelAuthorizationAuthorizer documentAuthorizer = getDocumentAuthorizer(authForm);
            can = documentAuthorizer.canSave(authForm.getTravelAuthorizationDocument(), GlobalVariables.getUserSession().getPerson());
        }

        if (!can){
            boolean isTravelManager = getTravelDocumentService().isTravelManager(GlobalVariables.getUserSession().getPerson());
            boolean isDelinquent = authForm.getTravelAuthorizationDocument().getDelinquentAction() != null;

            if (isTravelManager && isDelinquent){
                can = true;
            }
        }

        if (can) {
            authForm.getDocumentActions().put(KRADConstants.KUALI_ACTION_CAN_SAVE,true);
        }
        else{
            authForm.getDocumentActions().remove(KRADConstants.KUALI_ACTION_CAN_SAVE);
        }
    }

    /**
     * Overriding this so that we can populate the transportation modes before a user leaves the page
     *
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#performLookup(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        List<String> selectedTransportationModes = authForm.getTempSelectedTransporationModes();
        if (selectedTransportationModes != null) {
            authForm.getTravelAuthorizationDocument().setTransportationModeCodes(selectedTransportationModes);
        }
        else {
            authForm.getTravelAuthorizationDocument().setTransportationModeCodes(new ArrayList<String>());
        }

        return super.performLookup(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        authForm.setSelectedTransportationModes(authForm.getTravelAuthorizationDocument().getTransportationModeCodes());

        String refreshCaller = authForm.getRefreshCaller();

        LOG.debug("refresh call is: "+ refreshCaller);
        String groupTravelerId = request.getParameter("newGroupTravelerLine.groupTravelerEmpId");
        // if a cancel occurred on address lookup we need to reset the payee id and type, rest of fields will still have correct
        // information
        if (refreshCaller == null) {
            authForm.setTravelerId(authForm.getTempTravelerId());
        }

        ActionForward actionAfterPrimaryDestinationLookup = this.refreshAfterPrimaryDestinationLookup(mapping, authForm, request);
        if (actionAfterPrimaryDestinationLookup != null) {
            return actionAfterPrimaryDestinationLookup;
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     *
     * @param travelReqDoc
     * @param request
     * @param authForm
     */
    protected void refreshTransportationModesAfterButtonAction(TravelAuthorizationDocument travelReqDoc, TravelAuthorizationForm authForm) {
        List<String> selectedTransportationModes = authForm.getTempSelectedTransporationModes();
        if (selectedTransportationModes != null) {
            LOG.debug("selected transportation modes are: "+ selectedTransportationModes.toString());
            travelReqDoc.setTransportationModeCodes(selectedTransportationModes);
        }
        else {
            LOG.debug("setting selected transportation modes to empty list");
            travelReqDoc.setTransportationModeCodes(new ArrayList<String>());
        }
    }

    @Override
    protected void performRequesterRefresh(TravelDocument document, TravelFormBase travelForm, HttpServletRequest request) {
        LOG.debug("Looking up customer with number "+ document.getTraveler().getCustomerNumber());
        document.getTraveler().refreshReferenceObject(TemPropertyConstants.CUSTOMER);
        document.getTraveler().refreshReferenceObject(TemPropertyConstants.TRAVELER_TYPE);
        LOG.debug("Got "+ document.getTraveler().getCustomer());

        if (document.getTraveler().getPrincipalId() != null) {
            final String principalName = getPersonService().getPerson(document.getTraveler().getPrincipalId()).getPrincipalName();
            document.getTraveler().setPrincipalName(principalName);
        }

        ((TravelAuthorizationDocument)document).updatePayeeTypeForAuthorization();
        updateAccountsWithNewProfile(travelForm, document.getTemProfile());
    }

    /**
     * This method removes an other travel expense from this collection
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return the page to forward back to
     * @throws Exception
     */
    @Override
    public ActionForward deleteActualExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.deleteActualExpenseLine(mapping, form, request, response);
        //recalculate(mapping, form, request, response);

        return retval;
    }

    /**
     * This method adds a new emergency contact into the travel request document
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addEmergencyContactLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelReqDoc = (TravelAuthorizationDocument) travelauthForm.getDocument();

        TravelerDetailEmergencyContact newEmergencyContactLine = travelauthForm.getNewEmergencyContactLine();
        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddEmergencyContactLineEvent(NEW_EMERGENCY_CONTACT_LINE, travelauthForm.getDocument(), newEmergencyContactLine));

        if (rulePassed) {
            travelReqDoc.addEmergencyContactLine(newEmergencyContactLine);
            travelauthForm.setNewEmergencyContactLine(new TravelerDetailEmergencyContact());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method removes an emergency contact from this collection
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteEmergencyContactLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelReqDoc = (TravelAuthorizationDocument) travelauthForm.getDocument();

        int deleteIndex = getLineToDelete(request);
        travelReqDoc.getTraveler().getEmergencyContacts().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#updatePerDiemExpenses(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward updatePerDiemExpenses(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm taForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument document = taForm.getTravelAuthorizationDocument();

        ActionForward forward = super.updatePerDiemExpenses(mapping, form, request, response);
        taForm.getNewSourceLine().setAmount(this.getAccountingLineAmountToFillIn(taForm));
        getTravelEncumbranceService().updateEncumbranceObjectCode(document, taForm.getNewSourceLine());

        return forward;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument travelReqDoc = authForm.getTravelAuthorizationDocument();
        String tripTypeCode = travelReqDoc.getTripTypeCode();

        LOG.debug("Got special circumstances "+ authForm.getTravelAuthorizationDocument().getSpecialCircumstances());
        LOG.debug("Save Called on "+ getClass().getSimpleName()+ " for "+ authForm.getDocument().getClass().getSimpleName());
        LOG.debug("Saving document traveler detail "+ travelReqDoc.getTravelerDetailId());

        return super.save(mapping, form, request, response);
    }

    /**
     * Recalculates the Expenses Total Tab
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

//        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
//        TravelAuthorizationDocument travelReqDoc = authForm.getTravelAuthorizationDocument();
//        travelReqDoc.propagateAdvanceInformationIfNeeded();
        return recalculateTripDetailTotal(mapping, form, request, response);
    }

    // Custom button actions
    /**
     * For use with a specific set of methods of this class that create new purchase order-derived document types in response to
     * user actions, including <code>amendTa</code>. It employs the question framework to ask the user for a response before
     * creating and routing the new document. The response should consist of a note detailing a reason, and either yes or no. This
     * method can be better understood if it is noted that it will be gone through twice (via the question framework); when each
     * question is originally asked, and again when the yes/no response is processed, for confirmation.
     *
     * @param mapping These are boiler-plate.
     * @param form "
     * @param request "
     * @param response "
     * @param questionType A string identifying the type of question being asked.
     * @param confirmType A string identifying which type of question is being confirmed.
     * @param documentType A string, the type of document to create
     * @param notePrefix A string to appear before the note in the BO Notes tab
     * @param messageType A string to appear on the PO once the question framework is done, describing the action taken
     * @param operation A string, the verb to insert in the original question describing the action to be taken
     * @return An ActionForward
     * @throws Exception
     */
    @Override
    protected ActionForward askQuestionsAndPerformDocumentAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {
        LOG.debug("askQuestionsAndPerformDocumentAction started.");
        TravelAuthorizationForm taForm = (TravelAuthorizationForm) form;
        TravelDocumentBase taDoc = taForm.getTravelAuthorizationDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        try {
            ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

            // Start in logic for confirming the proposed operation.
            if (ObjectUtils.isNull(question)) {
                String message = "";
                String key = kualiConfiguration.getPropertyValueAsString(TemKeyConstants.TA_QUESTION_DOCUMENT);
                message = StringUtils.replace(key, "{0}", operation);
                // Ask question if not already asked.
                return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {

                    // If 'No' is the button clicked, just reload the doc
                    return returnToPreviousPage(mapping, taForm);
                }
                else if (question.equals(confirmType) && buttonClicked.equals(SingleConfirmationQuestion.OK)) {

                    // This is the case when the user clicks on "OK" in the end.
                    // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
                    return mapping.findForward(KFSConstants.MAPPING_PORTAL);
                }
                else {
                    // Have to check length on value entered.
                    String introNoteMessage = notePrefix + KFSConstants.BLANK_SPACE;

                    // Build out full message.
                    noteText = introNoteMessage + reason;
                    int noteTextLength = noteText.length();

                    // Get note text max length from DD.
                    int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();

                    if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                        // Figure out exact number of characters that the user can enter.
                        int reasonLimit = noteTextMaxLength - noteTextLength;

                        if (ObjectUtils.isNull(reason)) {
                            // Prevent a NPE by setting the reason to a blank string.
                            reason = "";
                        }

                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyValueAsString(TemKeyConstants.TA_QUESTION_DOCUMENT), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, TemKeyConstants.ERROR_TA_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                    }
                }
            }
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            ActionForward returnActionForward = null;
            String newStatus = null;
            if (documentType.equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT)) {
                newStatus = TemConstants.TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT;
                returnActionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                noteText = noteText + " (Previous Document Id is " + taForm.getDocId() + ")";
            }
            else if (questionType.equals(TemConstants.HOLD_TA_QUESTION)) {
                newStatus = TemConstants.TravelAuthorizationStatusCodeKeys.REIMB_HELD;
            }
            else if (questionType.equals(TemConstants.REMOVE_HOLD_TA_QUESTION)) {
                newStatus = TemConstants.TravelAuthorizationStatusCodeKeys.OPEN_REIMB;
            }

            Note newNote = getDocumentService().createNoteFromDocument(taDoc, noteText);


            // newNote.setNoteText(noteText);
            // newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.DOCUMENT_HEADER_NOTE_TYPE.getCode());
            // getDocumentService().addNoteToDocument(taDoc, newNote);
            taForm.setNewNote(newNote);

            taForm.setAttachmentFile(new BlankFormFile());

            insertBONote(mapping, taForm, request, response);
            // save the new state on the document
            taDoc.updateAndSaveAppDocStatus(newStatus);

            // send FYI for Hold, Remove Hold
            if (questionType.equals(TemConstants.REMOVE_HOLD_TA_QUESTION) || questionType.equals(TemConstants.HOLD_TA_QUESTION)) {
                getTravelDocumentService().addAdHocFYIRecipient(taDoc, taDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
            }

            SpringContext.getBean(DocumentService.class).saveDocument(taDoc);


            if (ObjectUtils.isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else {
                return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, kualiConfiguration.getPropertyValueAsString(messageType), "temSingleConfirmationQuestion", questionType, "");
            }
        }
        catch (ValidationException ve) {
            throw ve;
        }
    }

    /**
     * Is invoked when the user pressed on the Amend button on a Travel Authorization page to amend the TA. It will display the
     * question page to the user to ask whether the user really wants to amend the TA and ask the user to enter a reason in the text
     * area. If the user has entered the reason, it will invoke a service method to do the processing for amending the TA, then
     * display a Single Confirmation page to inform the user that the <code>TravelAuthorizationAmendmentDocument</code> has been
     * routed.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     * @see org.kuali.kfs.module.tem.document.TravelAuthorizationAmendmentDocument
     */
    public ActionForward amendTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Amend TA started");
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);
        if (inq.wasQuestionAsked()) {
            if (request.getParameterMap().containsKey(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME)){
                getDocumentService().saveDocument(((TravelAuthorizationForm)form).getDocument(), AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            }
        }
        ActionForward forward = askQuestionsAndPerformDocumentAction(inq, TemConstants.AMENDMENT_TA_QUESTION);

        return forward;
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward holdTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Hold TA started");
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);
        return askQuestionsAndPerformDocumentAction(inq, TemConstants.HOLD_TA_QUESTION);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeHoldTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Remove Hold TA started");
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);
        return askQuestionsAndPerformDocumentAction(inq, TemConstants.REMOVE_HOLD_TA_QUESTION);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancelTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Cancel TA started");
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);
        return askQuestionsAndPerformDocumentAction(inq, TemConstants.CANCEL_TA_QUESTION);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward closeTa(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        LOG.debug("Close TA started");
        String operation = "Close ";

        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = getInquisitive(mapping, form, request, response);

        return askQuestionsAndPerformDocumentAction(inq, TemConstants.CLOSE_TA_QUESTION);
    }

    /**
     * Forward to MAPPING_BASIC. The newReimbursement button is assumed to have java script
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward newReimbursement(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     */
    protected Inquisitive<TravelAuthorizationDocument, ActionForward> getInquisitive(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request, final HttpServletResponse response) {
        final Inquisitive<TravelAuthorizationDocument, ActionForward> inq = new StrutsInquisitor<TravelAuthorizationDocument, TravelAuthorizationForm, TravelAuthorizationAction>(mapping, (TravelAuthorizationForm) form, this, request, response);

        return inq;
    }

    /**
     *
     * @param inq
     * @param questionId
     * @return
     * @throws Exception
     */
    protected ActionForward askQuestionsAndPerformDocumentAction(final Inquisitive<TravelAuthorizationDocument, ActionForward> inq, final String questionId) throws Exception {
        final QuestionHandler<TravelAuthorizationDocument> questionHandler = getQuestionHandler(questionId);

        if (inq.wasQuestionAsked()) {
            return questionHandler.handleResponse(inq);
        }

        return questionHandler.askQuestion(inq);
    }

    /**
     * This is a utility method used to prepare to and to return to a previous page, making sure that the buttons will be restored
     * in the process.
     *
     * @param kualiDocumentFormBase The Form, considered as a KualiDocumentFormBase, as it typically is here.
     * @return An actionForward mapping back to the original page.
     */
    protected ActionForward returnToPreviousPage(ActionMapping mapping, KualiDocumentFormBase kualiDocumentFormBase) {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    protected KualiDecimal getAccountingLineAmountToFillIn(TravelFormBase travelFormBase) {
        TravelAuthorizationForm travelAuthForm = (TravelAuthorizationForm) travelFormBase;
        KualiDecimal amount = new KualiDecimal(0);

        KualiDecimal encTotal = travelAuthForm.getTravelAuthorizationDocument().getEncumbranceTotal();
        KualiDecimal expenseTotal = travelAuthForm.getTravelAuthorizationDocument().getExpenseLimit();

        List<SourceAccountingLine> accountingLines = travelAuthForm.getTravelAuthorizationDocument().getSourceAccountingLines();

        KualiDecimal accountingTotal = new KualiDecimal(0);
        for (SourceAccountingLine accountingLine : accountingLines) {
            accountingTotal = accountingTotal.add(accountingLine.getAmount());
        }

        if (ObjectUtils.isNull(expenseTotal)) {
            amount = encTotal.subtract(accountingTotal);
        }
        else if (expenseTotal.isLessThan(encTotal)) {
            amount = expenseTotal.subtract(accountingTotal);
        }
        else {
            amount = encTotal.subtract(accountingTotal);
        }

        return amount;
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        // before we try to insert - set the card type to encumbrance
        TemSourceAccountingLine accountingLine = (TemSourceAccountingLine)(((TravelAuthorizationForm)form).getNewSourceLine());
        accountingLine.setCardType(TemConstants.ENCUMBRANCE);

        super.insertSourceLine(mapping, form, request, response);

        // after we insert and have a blank line - set the amount to fill in
        travelauthForm.getNewSourceLine().setAmount(this.getAccountingLineAmountToFillIn(travelauthForm));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#deleteSourceLine(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward deleteSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.deleteSourceLine(mapping, form, request, response);
        TravelAuthorizationForm travelauthForm = (TravelAuthorizationForm) form;
        travelauthForm.getNewSourceLine().setAmount(this.getAccountingLineAmountToFillIn(travelauthForm));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#cancel(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            reverseAmendment((TravelAuthorizationForm) form);
        }

        return super.cancel(mapping, form, request, response);
    }

    @Override
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        if (ConfirmationQuestion.YES.equals(buttonClicked)) {
            reverseAmendment((TravelAuthorizationForm) form);
        }
        return super.disapprove(mapping, form, request, response);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#close(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object buttonClicked = request.getParameter(KRADConstants.QUESTION_CLICKED_BUTTON);
        /*if (ConfirmationQuestion.NO.equals(buttonClicked)) {
            reverseAmendment((TravelAuthorizationForm) form);
        }*/
        return super.close(mapping, form, request, response);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        recalculateTripDetailTotalOnly(mapping, form, request, response);
        TravelAuthorizationForm authForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument document = authForm.getTravelAuthorizationDocument();
        document.propagateAdvanceInformationIfNeeded();

        return super.route(mapping, form, request, response);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#blanketApprove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (((TravelAuthorizationForm)form).getTravelAuthorizationDocument().shouldProcessAdvanceForDocument()) {
            ((TravelAuthorizationForm)form).getTravelAuthorizationDocument().propagateAdvanceInformationIfNeeded();
            ((TravelAuthorizationForm)form).getTravelAuthorizationDocument().getTravelAdvance().setTravelAdvancePolicy(true);
        }

        return super.blanketApprove(mapping, form, request, response);
    }

    /**
     * At PaymentMethod node, propogate the advance amount if needed
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#approve(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isAtApprovalPropagateAdvanceInfoNode(((TravelAuthorizationForm)form).getTravelAuthorizationDocument().getDocumentHeader().getWorkflowDocument().getCurrentNodeNames()) && ((TravelAuthorizationForm)form).getTravelAuthorizationDocument().shouldProcessAdvanceForDocument()) {
            ((TravelAuthorizationForm)form).getTravelAuthorizationDocument().propagateAdvanceInformationIfNeeded();
        }
        return super.approve(mapping, form, request, response);
    }

    /**
     * Determines if the approval request is coming from a node which should propagate advance information (specifically the amount of the advance)
     * @param nodeNames the Set of nodes where the document is currently at
     * @return true if the advance info should be propagated, false otherwise
     */
    protected boolean isAtApprovalPropagateAdvanceInfoNode(Set<String> nodeNames) {
        if (nodeNames != null && !nodeNames.isEmpty()) {
            return nodeNames.contains(KFSConstants.RouteLevelNames.PAYMENT_METHOD) || nodeNames.contains(TemWorkflowConstants.RouteNodeNames.AP_TRAVEL);
        }
        return false;
    }

    /**
     * action which clears out an advance - this allows travel managers to clear advance as needed
     */
    public ActionForward clearAdvance(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (((TravelAuthorizationForm)form).getTravelAuthorizationDocument().shouldProcessAdvanceForDocument()) {
            final TravelAuthorizationDocument doc = ((TravelAuthorizationForm)form).getTravelAuthorizationDocument();
            doc.getTravelAdvance().clear();
            // clean up due date and payment amount from travel payment
            if (!ObjectUtils.isNull(doc.getAdvanceTravelPayment())) {
                doc.getAdvanceTravelPayment().setDueDate(null);
                doc.getAdvanceTravelPayment().setCheckTotalAmount(null);
                doc.getAdvanceTravelPayment().setPaymentMethodCode(KFSConstants.PaymentMethod.ACH_CHECK.getCode()); // set to check, so that foreign draft or wire transfer is not processed
            }
            // clean up accounting lines
            if (doc.allParametersForAdvanceAccountingLinesSet()) {
                doc.getAdvanceAccountingLine(0).setAmount(KualiDecimal.ZERO);
            } else {
                doc.setAdvanceAccountingLines(new ArrayList<TemSourceAccountingLine>());
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Update the TA status if the amendment is canceled or closed and not saved.
     *
     * @param form
     */
    protected void reverseAmendment(TravelAuthorizationForm form) {
        if (form.getTravelAuthorizationDocument() instanceof TravelAuthorizationAmendmentDocument) {
            TravelDocument travelDocument = form.getTravelAuthorizationDocument();
            getTravelDocumentService().revertOriginalDocument(travelDocument, TravelAuthorizationStatusCodeKeys.OPEN_REIMB);
        }
    }

    /**
     * Overridden to always return TravelAuthorizationDocument even if we're dealing with an amendment or a close
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#showAccountDistribution(javax.servlet.http.HttpServletRequest, org.kuali.rice.krad.document.Document)
     */
    @Override
    protected void showAccountDistribution(HttpServletRequest request, Document document) {
        if (document instanceof TravelAuthorizationDocument) {
            final boolean showAccountDistribution = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, DISPLAY_ACCOUNTING_DISTRIBUTION_TAB_IND);
            request.setAttribute(SHOW_ACCOUNT_DISTRIBUTION_ATTRIBUTE, showAccountDistribution);
        } else {
            super.showAccountDistribution(request, document);
        }
    }

    /**
     * Overridden so that if a TAA is copied, it actually generates a new TA
     * @see org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase#copy(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelAuthorizationForm travelAuthForm = (TravelAuthorizationForm) form;
        TravelAuthorizationDocument doc = travelAuthForm.getTravelAuthorizationDocument();

        if (!travelAuthForm.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_COPY)) {
            throw buildAuthorizationException("copy", doc);
        }

        if (doc.shouldRevertToOriginalAuthorizationOnCopy()) {
            TravelAuthorizationDocument newTaDoc = doc.toCopyTA();
            travelAuthForm.setDocument(newTaDoc);
            travelAuthForm.setDocTypeName(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT);
        } else {
            ((Copyable) travelAuthForm.getTransactionalDocument()).toCopy();
        }

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    protected void setTabStateForEmergencyContacts(TravelAuthorizationForm form) {
        TravelAuthorizationDocument travelAuthDocument = (TravelAuthorizationDocument) form.getDocument();

        boolean openTab = false;
        Collection<String> internationalTrips = getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.INTERNATIONAL_TRIP_TYPES);
        if (travelAuthDocument.getTripTypeCode() != null) {
            if (internationalTrips.contains(travelAuthDocument.getTripTypeCode())){
                openTab = true;
            }

            if (openTab) {
                String tabKey = WebUtils.generateTabKey(TemConstants.TabTitles.EMERGENCY_CONTACT_INFORMATION_TAB_TITLE);
                form.getTabStates().put(tabKey, KualiForm.TabState.OPEN.name());
            }
        }
        return;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.bean.TravelReimbursementMvcWrapperBean
     * @see org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#getMvcWrapperInterface()
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#newMvcDelegate(ActionForm)
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelStrutsObservable
     */
    @Override
    protected Class getMvcWrapperInterface() {
        return TravelAuthorizationMvcWrapperBean.class;
    }

}
