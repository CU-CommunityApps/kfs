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
import static org.kuali.kfs.module.tem.TemConstants.REMAINING_DISTRIBUTION_ATTRIBUTE;
import static org.kuali.kfs.module.tem.TemConstants.SHOW_REPORTS_ATTRIBUTE;
import static org.kuali.kfs.sys.KFSPropertyConstants.DOCUMENT_NUMBER;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.web.struts.DisbursementVoucherForm;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.AccountingDocumentRelationship;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelRelocationDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelRelocationAuthorizer;
import org.kuali.kfs.module.tem.document.service.TravelRelocationService;
import org.kuali.kfs.module.tem.document.web.bean.TravelRelocationMvcWrapperBean;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.module.tem.report.ExpenseSummaryReport;
import org.kuali.kfs.module.tem.report.NonEmployeeCertificationReport;
import org.kuali.kfs.module.tem.report.SummaryByDayReport;
import org.kuali.kfs.module.tem.report.service.ExpenseSummaryReportService;
import org.kuali.kfs.module.tem.report.service.NonEmployeeCertificationReportService;
import org.kuali.kfs.module.tem.report.service.SummaryByDayReportService;
import org.kuali.kfs.module.tem.report.util.BarcodeHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelRelocationAction extends TravelActionBase {

    public static Logger LOG = Logger.getLogger(TravelRelocationAction.class);

    private static final String[] reloMethodToCallExclusionArray = { "recalculate", "calculate", "recalculateTripDetailTotal" };

    /**
     * Refreshes collections on the document
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase form) throws WorkflowException {
        super.loadDocument(form);
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        final TravelRelocationDocument document = reloForm.getTravelRelocationDocument();

        refreshCollectionsFor(document);
        reloForm.setDistribution(getAccountingDistributionService().buildDistributionFrom(reloForm.getTravelDocument()));
        initializeAssignAccounts(reloForm);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.execute(mapping, form, request, response);

        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        final TravelRelocationDocument document = ((TravelRelocationForm) form).getTravelRelocationDocument();
        final String travelIdentifier = document.getTravelDocumentIdentifier();

        setButtonPermissions(reloForm);
        setContactMasking(reloForm);
        setTaxSelectable(reloForm);

        if (document.getTraveler() != null && document.getTraveler().getPrincipalId() != null) {
            document.getTraveler().setPrincipalName(getPersonService().getPerson(document.getTraveler().getPrincipalId()).getPrincipalName());
        }

        if (ObjectUtils.isNotNull(document.getActualExpenses())) {
            document.enableExpenseTypeSpecificFields(document.getActualExpenses());
        }

        refreshRelatedDocuments(reloForm);

        if (!reloForm.getMethodToCall().equalsIgnoreCase("dochandler")) {
            if (!getCalculateIgnoreList().contains(reloForm.getMethodToCall())) {
                recalculateTripDetailTotalOnly(mapping, form, request, response);
            }

            KualiDecimal totalRemaining = KualiDecimal.ZERO;
            for (final AccountingDistribution dist : reloForm.getDistribution()) {
                totalRemaining = totalRemaining.add(dist.getRemainingAmount());
            }

            request.setAttribute(REMAINING_DISTRIBUTION_ATTRIBUTE, totalRemaining);
        }

        showAccountDistribution(request, document);

        request.setAttribute(SHOW_REPORTS_ATTRIBUTE, !document.getDocumentHeader().getWorkflowDocument().isInitiated());

        final KualiDecimal paymentTotal = document.getPaymentAmount();
        if (paymentTotal != null && !ObjectUtils.isNull(document.getTravelPayment())) {
            document.getTravelPayment().setCheckTotalAmount(paymentTotal);
        }

        if (reloForm.getAccountDistributionsourceAccountingLines() == null || reloForm.getAccountDistributionsourceAccountingLines().isEmpty()) {
            initializeAssignAccounts(reloForm);
        }

        return retval;
    }

    protected void setContactMasking(TravelRelocationForm reloForm) {
        reloForm.setCanUnmask(reloForm.isUserDocumentInitiator());
    }

    /**
     * This method sets all the boolean properties on the form to determine what buttons can be displayed depending on what is going
     * on
     */
    protected void setButtonPermissions(TravelRelocationForm form) {
        final TravelRelocationAuthorizer authorizer = getDocumentAuthorizer(form);
        form.setCanCertify(authorizer.canCertify(form.getTravelRelocationDocument(), GlobalVariables.getUserSession().getPerson()));
        setCanCalculate(form);
    }

    protected void setTaxSelectable(final TravelRelocationForm form) {
        final TravelRelocationAuthorizer authorizer = getDocumentAuthorizer(form);
        form.getTravelRelocationDocument().setTaxSelectable(authorizer.canTaxSelectable(GlobalVariables.getUserSession().getPerson()));
    }

    @Override
    protected Class getMvcWrapperInterface() {
        return TravelRelocationMvcWrapperBean.class;
    }

    /**
     * Do initialization for a new {@link TravelRelocationDocument}
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        final TravelRelocationForm travelForm = (TravelRelocationForm) kualiDocumentFormBase;
        final TravelRelocationDocument document = (TravelRelocationDocument) travelForm.getDocument();
        getTravelRelocationService().addListenersTo(document);

        if (!StringUtils.isBlank(travelForm.getTravelDocumentIdentifier())) {
            LOG.debug("Creating relocation for document number " + travelForm.getTravelDocumentIdentifier());

            final TravelRelocationDocument oldRelocation = getTravelRelocation(travelForm.getTravelDocumentIdentifier());
            if (oldRelocation != null) {
                LOG.debug("Setting traveler with id " + oldRelocation.getTravelerDetailId());
                document.setTravelerDetailId(oldRelocation.getTravelerDetailId());
                document.refreshReferenceObject(TemPropertyConstants.TRAVELER);
                LOG.debug("Traveler is "+ document.getTraveler()+ " with customer number "+ document.getTraveler().getCustomerNumber());

                if (document.getTraveler().getPrincipalId() != null) {
                    document.getTraveler().setPrincipalName(getPersonService().getPerson(document.getTraveler().getPrincipalId()).getPrincipalName());
                }
                document.setProfileId(oldRelocation.getTemProfileId());
                document.updatePayeeTypeForReimbursable();

                document.setTripDescription(oldRelocation.getTripDescription());
                document.setPrimaryDestinationName(oldRelocation.getPrimaryDestinationName());
                document.setTripBegin(oldRelocation.getTripBegin());
                document.setTripEnd(oldRelocation.getTripEnd());
                document.setJobClsCode(oldRelocation.getJobClsCode());
                document.setReasonCode(oldRelocation.getReasonCode());
                document.setFromAddress1(oldRelocation.getFromAddress1());
                document.setFromAddress2(oldRelocation.getFromAddress2());
                document.setFromCity(oldRelocation.getFromCity());
                document.setFromCountryCode(oldRelocation.getFromCountryCode());
                document.setFromStateCode(oldRelocation.getFromStateCode());
                document.setToAddress1(oldRelocation.getToAddress1());
                document.setToAddress2(oldRelocation.getToAddress2());
                document.setToCity(oldRelocation.getToCity());
                document.setToCountryCode(oldRelocation.getToCountryCode());
                document.setToStateCode(oldRelocation.getToStateCode());
                document.setTravelDocumentIdentifier(oldRelocation.getTravelDocumentIdentifier());
                document.getDocumentHeader().setOrganizationDocumentNumber(oldRelocation.getDocumentHeader().getOrganizationDocumentNumber());

                final AccountingDocumentRelationship relationship = buildRelationshipToProgenitorDocument(oldRelocation, document);
                getBusinessObjectService().save(relationship);

                // we're not the progenitor so let's force a refresh of notes
                final List<Note> notes = getNoteService().getByRemoteObjectId(oldRelocation.getNoteTarget().getObjectId());
                document.setNotes(notes);
            }
        } else {
            document.setTripProgenitor(true); // this is the trip progenitor
        }
        initializeAssignAccounts(travelForm);
    }

    protected void createDVDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        Document doc = getDocumentService().getNewDocument(kualiDocumentFormBase.getDocTypeName());

        kualiDocumentFormBase.setDocument(doc);
        kualiDocumentFormBase.setDocTypeName(doc.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());

        ((DisbursementVoucherDocument) kualiDocumentFormBase.getDocument()).initiateDocument();

        // set wire charge message in form
        ((DisbursementVoucherForm) kualiDocumentFormBase).setWireChargeMessage(retrieveWireChargeMessage());
    }

    /**
     * Performs necessary updates after the requester on the relocation document was updated, such as updating the payee type
     * @param document the document to update
     */
    @Override
    protected void performRequesterRefresh(TravelDocument document, TravelFormBase travelForm, HttpServletRequest request) {
        ((TravelRelocationDocument)document).updatePayeeTypeForReimbursable();
        updateAccountsWithNewProfile(travelForm, document.getTemProfile());
    }

    protected TravelRelocationService getTravelRelocationService() {
        return SpringContext.getBean(TravelRelocationService.class);
    }

    /**
     * Action method for adding an {@link ActualExpense} instance to the {@link TravelDocument}
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward addActualExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ActionForward retval = super.addActualExpenseLine(mapping, form, request, response);
        // recalculate(mapping, form, request, response);

        return retval;
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
        // recalculate(mapping, form, request, response);

        return retval;
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

    protected void refreshCollectionsFor(final TravelRelocationDocument relocation) {
        if (!relocation.getDocumentHeader().getWorkflowDocument().isInitiated()) {
            LOG.debug("Refreshing objects in relocation");
            relocation.refreshReferenceObject(TemPropertyConstants.TRAVELER);
            relocation.refreshReferenceObject(TemPropertyConstants.ACTUAL_EXPENSES);
            relocation.refreshReferenceObject(TemPropertyConstants.SPECIAL_CIRCUMSTANCES);
        }
    }

    /**
     * Uses the {@link TravelRelocationService} to lookup a {@link TravelRelocationDocument} instance via its
     * <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to location a {@link TravelRelocationDocument} with
     * @return {@link TravelRelocationDocument} instance
     */
    protected TravelRelocationDocument getTravelRelocation(final String travelDocumentIdentifier) {
        Collection<TravelRelocationDocument> reloList = getTravelRelocationService().findByIdentifier(travelDocumentIdentifier);
        if (ObjectUtils.isNotNull(reloList) && reloList.iterator().hasNext()) {
            return reloList.iterator().next();
        }

        return null;
    }

    /**
     * Action method for creating a {@link ExpenseSummaryReport} and producing a PDF from it
     */
    public ActionForward viewExpenseSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        reloForm.setDocument(getTravelRelocationService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelRelocationDocument relocation = reloForm.getTravelRelocationDocument();
        final ExpenseSummaryReport report = getExpenseSummaryReportService().buildReport(relocation);
        report.setReportTitle("Moving and Relocation");

        final ByteArrayOutputStream baos = getTravelReportService().buildReport(report);
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, String.format(TemConstants.EXPENSE_SUMMARY_REPORT_TITLE, relocation.getTravelDocumentIdentifier()));

        return null;
    }

    /**
     * Action method for creating a {@link SummaryByDayReport} and producing a PDF from it.
     */
    public ActionForward viewSummaryByDay(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        reloForm.setDocument(getTravelRelocationService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelRelocationDocument relocation = reloForm.getTravelRelocationDocument();
        final SummaryByDayReport report = getSummaryByDayReportService().buildReport(relocation);

        final ByteArrayOutputStream baos = getTravelReportService().buildReport(report);
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, String.format(TemConstants.SUMMARY_BY_DAY_TITLE, relocation.getTravelDocumentIdentifier()));

        return null;
    }

    public ActionForward printCoversheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        final String documentNumber = request.getParameter(DOCUMENT_NUMBER);
        if (documentNumber != null && !documentNumber.isEmpty()) {
            reloForm.setDocument(getTravelRelocationService().find(documentNumber));
        }
        final TravelRelocationDocument relocation = reloForm.getTravelRelocationDocument();

        final Coversheet cover = getTravelRelocationService().generateCoversheetFor(relocation);
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        cover.print(stream);

        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", stream, String.format(COVERSHEET_FILENAME_FORMAT, relocation.getTravelDocumentIdentifier()));

        return null;
    }

    /**
     * Action method for creating a {@link Non Employee forms} and producing a PDF from it
     */
    public ActionForward viewNonEmployeeForms(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final TravelRelocationForm reloForm = (TravelRelocationForm) form;
        reloForm.setDocument(getTravelRelocationService().find(request.getParameter(DOCUMENT_NUMBER)));
        final TravelRelocationDocument relocation = reloForm.getTravelRelocationDocument();
        final NonEmployeeCertificationReport report = getNonEmployeeCertificationReportService().buildReport(relocation);
        BarcodeHelper barcode = new BarcodeHelper();
        report.setBarcodeImage(barcode.generateBarcodeImage(relocation.getDocumentNumber()));
        File reportFile = getNonEmployeeCertificationReportService().generateReport(report);

        StringBuilder fileName = new StringBuilder();
        fileName.append(relocation.getTravelDocumentIdentifier());
        fileName.append(TemConstants.NON_EMPLOYEE_CERTIFICATION_REPORT_TITLE);
        fileName.append(KFSConstants.ReportGeneration.PDF_FILE_EXTENSION);
        if (reportFile.length() == 0) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        displayPDF(request, response, reportFile, fileName);

        return null;
    }

    /**
     * Forward to MAPPING_BASIC. The newRelocation button is assumed to have java script
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward newRelocation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected NonEmployeeCertificationReportService getNonEmployeeCertificationReportService() {
        return SpringContext.getBean(NonEmployeeCertificationReportService.class);
    }

    protected ExpenseSummaryReportService getExpenseSummaryReportService() {
        return SpringContext.getBean(ExpenseSummaryReportService.class);
    }

    protected SummaryByDayReportService getSummaryByDayReportService() {
        return SpringContext.getBean(SummaryByDayReportService.class);
    }

    @Override
    protected List<String> getCalculateIgnoreList() {
        return Arrays.asList(reloMethodToCallExclusionArray);
    }
}
