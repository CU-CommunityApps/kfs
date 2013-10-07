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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemConstants.COVERSHEET_FILENAME_FORMAT;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_REPORTS_ATTRIBUTE;
import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT_NUMBER;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelEntertainmentDocument;
import org.kuali.kfs.module.tem.document.service.TravelEntertainmentDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.TravelEntertainmentMvcWrapperBean;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.module.tem.report.EntertainmentHostCertificationReport;
import org.kuali.kfs.module.tem.report.NonEmployeeCertificationReport;
import org.kuali.kfs.module.tem.report.service.NonEmployeeCertificationReportService;
import org.kuali.kfs.module.tem.report.service.TravelEntertainmentHostCertificationService;
import org.kuali.kfs.module.tem.report.util.BarcodeHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class...
 */
public class TravelEntertainmentAction extends TravelActionBase {

    public static Logger LOG = Logger.getLogger(TravelEntertainmentAction.class);

    public static final String[] ATTENDEE_ATTRIBUTE_NAMES = { TemPropertyConstants.AttendeeProperties.ATTENDEE_TYPE, TemPropertyConstants.AttendeeProperties.COMPANY, TemPropertyConstants.AttendeeProperties.TITLE,TemPropertyConstants.AttendeeProperties.NAME};
    public static final Integer[] MAX_LENGTH={10,40,40,40};

    /**
     * Constructs a TravelEntertainmentDocumentAction.java.
     */
    public TravelEntertainmentAction() {

    }

    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.docHandler(mapping, form, request, response);
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();

        initializeNewAttendeeLines(entForm.getNewAttendeeLines(), document.getAttendee());
        setEntHostCertificationWarning(document);

        return retval;
    }


    /**
     * Initiates the document based on another entertainment reimbursement if the expected query fields (needed: travelDocumentIdentifier; maybe: fromDocumentNumber) are filled in
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase form) throws WorkflowException {
        super.createDocument(form);

        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();

        final String identifierStr = entForm.getTravelDocumentIdentifier();
        final String fromDocumentNumber = entForm.getFromDocumentNumber();

        if (!StringUtils.isBlank(identifierStr)){
            if (!StringUtils.isBlank(fromDocumentNumber)){
                LOG.debug("Creating reimbursement for document number "+ identifierStr);
                document.setTravelDocumentIdentifier(identifierStr);
                TravelDocument travelDocument = (TravelDocument) getDocumentService().getByDocumentHeaderId(fromDocumentNumber);

                LOG.debug("Setting traveler with id "+ travelDocument.getTravelerDetailId());
                document.setTravelerDetailId(travelDocument.getTravelerDetailId());
                document.refreshReferenceObject(TemPropertyConstants.TRAVELER);
                LOG.debug("Traveler is "+ document.getTraveler()+ " with customer number "+ document.getTraveler().getCustomerNumber());

                if (document.getTraveler().getPrincipalId() != null) {
                    document.getTraveler().setPrincipalName(getPersonService().getPerson(document.getTraveler().getPrincipalId()).getPrincipalName());
                }

                document.setPrimaryDestinationId(travelDocument.getPrimaryDestinationId());
                document.setTripType(travelDocument.getTripType());
                document.setTripTypeCode(travelDocument.getTripTypeCode());

                document.setExpenseLimit(travelDocument.getExpenseLimit());
                document.configureTraveler(travelDocument.getTemProfileId(), travelDocument.getTraveler());
                document.getDocumentHeader().setOrganizationDocumentNumber(travelDocument.getDocumentHeader().getOrganizationDocumentNumber());

                document.setActualExpenses((List<ActualExpense>) getTravelDocumentService().copyActualExpenses(travelDocument.getActualExpenses(), document.getDocumentNumber()));

                // add new detail for the copied actualExpenses
                if (document.getActualExpenses() != null && !document.getActualExpenses().isEmpty()) {
                    for (int i = 0; i < document.getActualExpenses().size(); i++) {
                        entForm.getNewActualExpenseLines().add(new ActualExpense());
                    }
                }
            }
            else{
                populateFromPreviousENTDoc(document, identifierStr);
            }
        }
    }

    /**
     * Refreshes the collections on the document
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase form) throws WorkflowException {
        super.loadDocument(form);
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();

        refreshCollectionsFor(document);
    }

    //TODO: is this really necessary?
    private void initializeTemProfiles(TravelEntertainmentDocument document) {
        if (document.getHostProfile() == null && document.getHostProfileId() != null) {
            document.setHostProfileId(document.getHostProfileId());
        }
        if (document.getTemProfile() == null && document.getTemProfileId() != null) {
            document.setProfileId(document.getTemProfileId());
        }
    }

    private void populateFromPreviousENTDoc(TravelEntertainmentDocument document, String temDocId) {
        try {
            TravelEntertainmentDocument entDocument = (TravelEntertainmentDocument) getDocumentService().getByDocumentHeaderId(temDocId);
            document.setTravelerDetailId(entDocument.getTravelerDetailId());
            document.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            if (document.getAttendee() != null) {
                document.setAttendee(entDocument.getAttendee());
            }
            if (document.getTraveler() != null) {
                document.setTraveler(entDocument.getTraveler());
            }
            document.setHostProfileId(entDocument.getHostProfileId());
            document.setTemProfileId(entDocument.getTemProfileId());
            document.setEventTitle(entDocument.getEventTitle());
            document.setPurposeCode(entDocument.getPurposeCode());
            document.setPaymentMethod(entDocument.getPaymentMethod());
            document.setTripBegin(entDocument.getTripBegin());
            document.setTripEnd(entDocument.getTripEnd());
            document.setSpouseIncluded(entDocument.getSpouseIncluded());
            document.setDescription(entDocument.getDescription());
            document.setHostCertified(entDocument.getHostCertified());
            document.setNonEmployeeCertified(entDocument.getNonEmployeeCertified());
        }
        catch (WorkflowException ex) {
            // TODO Auto-generated catch block
            ex.printStackTrace();
        }
    }

    protected void initializeNewAttendeeLines(final List<Attendee> newAttendeeLines, List<Attendee> attendees) {
        for (Attendee attendee : attendees) {
            if (ObjectUtils.isNull(attendee)) {
                Attendee newAttendeeLine = new Attendee();
                newAttendeeLine.setAttendeeType(attendee.getAttendeeType());
                newAttendeeLine.setCompany(attendee.getCompany());
                newAttendeeLine.setName(attendee.getName());
                newAttendeeLine.setTitle(attendee.getTitle());
                newAttendeeLines.add(newAttendeeLine);
            }
        }
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.execute(mapping, form, request, response);
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final TravelEntertainmentDocument document = (TravelEntertainmentDocument) ((TravelEntertainmentForm) form).getDocument();
        final String travelIdentifier = document.getTravelDocumentIdentifier();

        initializeNames(entForm, document);
        setEntHostCertificationWarning(document);
        setButtonPermissions(entForm);

        if(ObjectUtils.isNotNull(document.getActualExpenses())){
            document.enableExpenseTypeSpecificFields(document.getActualExpenses());
        }

        if(!getCalculateIgnoreList().contains(entForm.getMethodToCall())){
            recalculateTripDetailTotalOnly(mapping, form, request, response);
        }

        refreshRelatedDocuments(entForm);

        showAccountDistribution(request, document);

        request.setAttribute(SHOW_REPORTS_ATTRIBUTE, !document.getDocumentHeader().getWorkflowDocument().isInitiated());

        entForm.setCanPrintHostCertification(document.canShowHostCertification());

        final KualiDecimal reimbursableTotal = document.getReimbursableTotal();
        if (reimbursableTotal != null && !ObjectUtils.isNull(document.getTravelPayment())) {
            document.getTravelPayment().setCheckTotalAmount(reimbursableTotal);
        }

        return retval;
    }

    private void initializeNames(final TravelEntertainmentForm entForm, final TravelEntertainmentDocument document) {
        if (document.getHostProfile() != null) {
            document.setHostName(document.getHostProfile().getLastName() + KFSConstants.COMMA + document.getHostProfile().getFirstName() + KFSConstants.BLANK_SPACE +
                    (document.getHostProfile().getMiddleName() == null ? KFSConstants.BLANK_SPACE : document.getHostProfile().getMiddleName()));
        }
        entForm.getNewAttendeeLines().get(0).setName(getTravelerCompleteName(document.getAttendeeDetail()));
    }

    private String getTravelerCompleteName(TravelerDetail travelerdetail) {
        String completeName = KFSConstants.EMPTY_STRING;
        if (validateTravelerDetailName(travelerdetail)) {
            completeName = travelerdetail.getLastName() + KFSConstants.COMMA + travelerdetail.getFirstName() + KFSConstants.BLANK_SPACE +
                    (travelerdetail.getMiddleName() == null ? KFSConstants.BLANK_SPACE : travelerdetail.getMiddleName());
        }
        return completeName;
    }

    private boolean validateTravelerDetailName(TravelerDetail travelerdetail){
        if (travelerdetail == null) {
            return false;
        }
        if (travelerdetail.getLastName() == null) {
            return false;
        }
        if (travelerdetail.getFirstName() == null) {
            return false;
        }
        if (travelerdetail.getLastName().equals(KFSConstants.EMPTY_STRING) && travelerdetail.getFirstName().equals(KFSConstants.EMPTY_STRING)) {
            return false;
        }

        return true;
    }

    private void setEntHostCertificationWarning(final TravelEntertainmentDocument document) {
        if (document.getDocumentHeader().getWorkflowDocument().isInitiated() || document.getDocumentHeader().getWorkflowDocument().isSaved()) {
            boolean entertainmentHostAttached = false;
            List<Note> notes = document.getNotes();
            for (Note note : notes) {
                if (ObjectUtils.isNotNull(note.getAttachment()) && TemConstants.AttachmentTypeCodes.ATTACHMENT_TYPE_ENT_HOST_CERT.equals(note.getAttachment().getAttachmentTypeCode())) {
                    entertainmentHostAttached = true;
                    break;
                }
            }

            if (document.getHostProfile() != null && document.getTemProfile() != null && !document.getHostProfile().getProfileId().equals(document.getTemProfile().getProfileId())) {
                GlobalVariables.getMessageMap().putWarning(TemPropertyConstants.EntertainmentFields.HOST_NAME, TemKeyConstants.HOST_CERTIFICATION_REQUIRED_IND);
            }
        }
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
        return recalculateTripDetailTotal(mapping, form, request, response);
    }

    protected void refreshCollectionsFor(final TravelEntertainmentDocument entDoc) {
        if (!entDoc.getDocumentHeader().getWorkflowDocument().isInitiated()) {
            LOG.debug("Refreshing objects in entertainment document");
            entDoc.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            entDoc.refreshReferenceObject(TemPropertyConstants.TRIP_TYPE);
            entDoc.refreshReferenceObject(TemPropertyConstants.ACTUAL_EXPENSES);
            entDoc.refreshReferenceObject(TemPropertyConstants.SPECIAL_CIRCUMSTANCES);
        }
    }

    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;

        final String refreshCaller = entForm.getRefreshCaller();
        if (!StringUtils.isBlank(refreshCaller)) {
            final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();

            if (TemConstants.TRAVELER_PROFILE_DOC_LOOKUPABLE.equals(refreshCaller)) {
                performRequesterRefresh(document);
            }
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Performs necessary updates after the requester on the relocation document was updated, such as updating the payee type
     * @param document the document to update
     */
    protected void performRequesterRefresh(TravelEntertainmentDocument document) {
        document.updatePayeeTypeForReimbursable();
    }

    public ActionForward printCoversheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final String documentNumber = request.getParameter(DOCUMENT_NUMBER);
        if (documentNumber != null && !documentNumber.isEmpty()) {
            entForm.setDocument(getEntertainmentDocumentService().find(documentNumber));
        }
        final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();
        final Coversheet cover = getEntertainmentDocumentService().generateCoversheetFor(document);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cover.print(stream);

        WebUtils.saveMimeOutputStreamAsFile(response, KFSConstants.ReportGeneration.PDF_MIME_TYPE, stream, String.format(COVERSHEET_FILENAME_FORMAT, document.getDocumentNumber()));

        return null;
    }

    public ActionForward viewNonEmployeeForms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        entForm.setDocument(getEntertainmentDocumentService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelEntertainmentDocument entertainment = entForm.getEntertainmentDocument();
        final NonEmployeeCertificationReport report = getNonEmployeeCertificationReportService().buildReport(entertainment);
        BarcodeHelper barcode = new BarcodeHelper();
        report.setBarcodeImage(barcode.generateBarcodeImage(entertainment.getDocumentNumber()));
        File reportFile = getNonEmployeeCertificationReportService().generateReport(report);

        StringBuilder fileName = new StringBuilder();
        fileName.append(entForm.getDocument().getDocumentNumber());
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);
        if (reportFile.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        displayPDF(request, response, reportFile, fileName);

        return null;
    }

    public ActionForward viewEntertainmentCertification(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelEntertainmentForm entForm = (TravelEntertainmentForm) form;
        final String documentNumber = request.getParameter(DOCUMENT_NUMBER);
        if (documentNumber != null && !documentNumber.isEmpty()) {
            entForm.setDocument(getEntertainmentDocumentService().find(documentNumber));
        }
        final TravelEntertainmentDocument document = entForm.getEntertainmentDocument();
        entForm.setDocument(getEntertainmentDocumentService().find(request.getParameter(DOCUMENT_NUMBER)));
        final EntertainmentHostCertificationReport report = getEntertainmentHostCertificationService().buildReport(document);
        BarcodeHelper barcode = new BarcodeHelper();
        report.setBarcodeImage(barcode.generateBarcodeImage(document.getDocumentNumber()));
        File reportFile = getEntertainmentHostCertificationService().generateEntertainmentHostCertReport(report);

        StringBuilder fileName = new StringBuilder();
        fileName.append(document.getDocumentNumber());
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);
        if (reportFile.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        displayPDF(request, response, reportFile, fileName);

        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //remove the emergency contacts so that it does not get validated
        final TravelEntertainmentForm entertainment = (TravelEntertainmentForm) form;
        TravelDocument document = entertainment.getTravelDocument();
        document.getTraveler().getEmergencyContacts().clear();

        return super.route(mapping, form, request, response);
    }

    public ActionForward addAttendeeLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelFormBase travelForm = (TravelFormBase) form;
        final TravelEntertainmentMvcWrapperBean mvcWrapper = newMvcDelegate(form);
        mvcWrapper.setNewAttendeeLine(mvcWrapper.getNewAttendeeLines().get(getSelectedLine(request)));
        travelForm.getObservable().notifyObservers(mvcWrapper);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteAttendeeLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelFormBase travelForm = (TravelFormBase) form;
        final TravelMvcWrapperBean mvcWrapper = newMvcDelegate(form);
        travelForm.getObservable().notifyObservers(new Object[] { mvcWrapper, getSelectedLine(request) });
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Import Attendees to the document from a spreadsheet.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward importAttendees(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelEntertainmentForm travelForm           = (TravelEntertainmentForm) form;
        final TravelEntertainmentMvcWrapperBean mvcWrapper = newMvcDelegate(form);

        travelForm.getObservable().notifyObservers(new Object[] { mvcWrapper, new String(travelForm.getAttendeesImportFile().getFileData()) });

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method sets all the boolean properties on the form to determine what buttons can be displayed depending on what is going
     * on
     */
    protected void setButtonPermissions(TravelEntertainmentForm form) {
        setCanCalculate(form);
    }

    @Override
    public ActionForward downloadBOAttachment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return super.downloadBOAttachment(mapping, form, request, response);
    }

    protected void setContactMasking(TravelEntertainmentForm entForm) {
        entForm.setCanUnmask(entForm.isUserDocumentInitiator());
    }

    /**
     * Overridden to use reimbursable total for document
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelActionBase#getAccountingLineAmountToFillIn(org.kuali.kfs.module.tem.document.web.struts.TravelFormBase)
     */
    @Override
    protected KualiDecimal getAccountingLineAmountToFillIn(TravelFormBase travelReqForm) {
        KualiDecimal amount = new KualiDecimal(0);

        TEMReimbursementDocument travelDocument = (TEMReimbursementDocument)travelReqForm.getTravelDocument();
        KualiDecimal reimbursementTotal = travelDocument.getReimbursableTotal();

        final List<TemSourceAccountingLine> accountingLines = travelDocument.getSourceAccountingLines();

        KualiDecimal accountingTotal = new KualiDecimal(0);
        for (TemSourceAccountingLine accountingLine : accountingLines) {
            if (travelDocument.getDefaultCardTypeCode().equals(accountingLine.getCardType())) {
                accountingTotal = accountingTotal.add(accountingLine.getAmount());
            }
        }

        if (!ObjectUtils.isNull(reimbursementTotal)) {
            amount = reimbursementTotal.subtract(accountingTotal);
        }

        return amount;
    }

    protected TravelEntertainmentHostCertificationService getEntertainmentHostCertificationService() {
        return SpringContext.getBean(TravelEntertainmentHostCertificationService.class);
    }

    protected TravelEntertainmentDocumentService getEntertainmentDocumentService() {
        return SpringContext.getBean(TravelEntertainmentDocumentService.class);
    }

    protected NonEmployeeCertificationReportService getNonEmployeeCertificationReportService() {
        return SpringContext.getBean(NonEmployeeCertificationReportService.class);
    }

    @Override
    protected Class<? extends TravelMvcWrapperBean> getMvcWrapperInterface() {
        return TravelEntertainmentMvcWrapperBean.class;
    }

}
