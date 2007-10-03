/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.action;

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.Note;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderQuoteList;
import org.kuali.module.purap.bo.PurchaseOrderQuoteListVendor;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.question.SingleConfirmationQuestion;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorConstants.AddressTypes;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorPhoneNumber;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles specific Actions requests for the Requisition.
 */
public class PurchaseOrderAction extends PurchasingActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderAction.class);
    
    //TODO f2f: need a jira for removing the refresh for quotes (3 calls to that method in this class)
    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // Handling lookups for alternate vendor for escrow payment that are only specific to Purchase Order
        if (request.getParameter("document.alternateVendorHeaderGeneratedIdentifier") != null && request.getParameter("document.alternateVendorDetailAssignedIdentifier") != null) {
            Integer alternateVendorDetailAssignedId = document.getAlternateVendorDetailAssignedIdentifier();
            Integer alternateVendorHeaderGeneratedId = document.getAlternateVendorHeaderGeneratedIdentifier();
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorDetailAssignedIdentifier(alternateVendorDetailAssignedId);
            refreshVendorDetail.setVendorHeaderGeneratedIdentifier(alternateVendorHeaderGeneratedId);
            refreshVendorDetail = (VendorDetail) businessObjectService.retrieve(refreshVendorDetail);
            document.templateAlternateVendor(refreshVendorDetail);
        }
        
        // Handling lookups for quote vendor search that is specific to Purchase Order
        if (request.getParameter("document.purchaseOrderQuoteListIdentifier") != null) {
            // do a lookup and add all the vendors!
            Integer poQuoteListIdentifier = document.getPurchaseOrderQuoteListIdentifier();
            PurchaseOrderQuoteList poQuoteList = new PurchaseOrderQuoteList();
            poQuoteList.setPurchaseOrderQuoteListIdentifier(poQuoteListIdentifier);
            poQuoteList = (PurchaseOrderQuoteList) businessObjectService.retrieve(poQuoteList);
            for (PurchaseOrderQuoteListVendor poQuoteListVendor : poQuoteList.getQuoteListVendors()) {
                VendorDetail newVendor = poQuoteListVendor.getVendorDetail();
                PurchaseOrderVendorQuote newPOVendorQuote = new PurchaseOrderVendorQuote();
                newPOVendorQuote.setVendorName(newVendor.getVendorName());
                newPOVendorQuote.setVendorHeaderGeneratedIdentifier(newVendor.getVendorHeaderGeneratedIdentifier());
                newPOVendorQuote.setVendorDetailAssignedIdentifier(newVendor.getVendorDetailAssignedIdentifier());
                newPOVendorQuote.setDocumentNumber(document.getDocumentNumber());
                for (VendorAddress address : newVendor.getVendorAddresses()) {
                    if (AddressTypes.PURCHASE_ORDER.equals(address.getVendorAddressTypeCode())) {
                        newPOVendorQuote.setVendorCityName(address.getVendorCityName());
                        newPOVendorQuote.setVendorCountryCode(address.getVendorCountryCode());
                        newPOVendorQuote.setVendorLine1Address(address.getVendorLine1Address());
                        newPOVendorQuote.setVendorLine2Address(address.getVendorLine2Address());
                        newPOVendorQuote.setVendorPostalCode(address.getVendorZipCode());
                        newPOVendorQuote.setVendorStateCode(address.getVendorStateCode());
                        break;
                    }
                }

                String tmpPhoneNumber = null;
                for (VendorPhoneNumber phone : newVendor.getVendorPhoneNumbers()) {
                    if (VendorConstants.PhoneTypes.PO.equals(phone.getVendorPhoneTypeCode())) {
                        newPOVendorQuote.setVendorPhoneNumber(phone.getVendorPhoneNumber());
                    }
                    if (VendorConstants.PhoneTypes.FAX.equals(phone.getVendorPhoneTypeCode())) {
                        newPOVendorQuote.setVendorFaxNumber(phone.getVendorPhoneNumber());
                    }
                    if (VendorConstants.PhoneTypes.PHONE.equals(phone.getVendorPhoneTypeCode())) {
                        tmpPhoneNumber = phone.getVendorPhoneNumber();
                    }
                }
                if (StringUtils.isEmpty(newPOVendorQuote.getVendorPhoneNumber()) && !StringUtils.isEmpty(tmpPhoneNumber)) {
                    newPOVendorQuote.setVendorPhoneNumber(tmpPhoneNumber);
                }
                document.getPurchaseOrderVendorQuotes().add(newPOVendorQuote);
                document.refreshNonUpdateableReferences();
            }
        }
        
        // Handling lookups for quote vendor search that is specific to Purchase Order
        if (request.getParameter("document.newQuoteVendorHeaderGeneratedIdentifier") != null && request.getParameter("document.newQuoteVendorDetailAssignedIdentifier") != null) {
            // retrieve this vendor from DB and add it to the end of the list
            VendorDetail newVendor = SpringContext.getBean(VendorService.class).getVendorDetail(document.getNewQuoteVendorHeaderGeneratedIdentifier(), document.getNewQuoteVendorDetailAssignedIdentifier());
            PurchaseOrderVendorQuote newPOVendorQuote = new PurchaseOrderVendorQuote();
            newPOVendorQuote.setVendorName(newVendor.getVendorName());
            newPOVendorQuote.setVendorHeaderGeneratedIdentifier(newVendor.getVendorHeaderGeneratedIdentifier());
            newPOVendorQuote.setVendorDetailAssignedIdentifier(newVendor.getVendorDetailAssignedIdentifier());
            newPOVendorQuote.setDocumentNumber(document.getDocumentNumber());
            for (VendorAddress address : newVendor.getVendorAddresses()) {
                if (AddressTypes.PURCHASE_ORDER.equals(address.getVendorAddressTypeCode())) {
                    newPOVendorQuote.setVendorCityName(address.getVendorCityName());
                    newPOVendorQuote.setVendorCountryCode(address.getVendorCountryCode());
                    newPOVendorQuote.setVendorLine1Address(address.getVendorLine1Address());
                    newPOVendorQuote.setVendorLine2Address(address.getVendorLine2Address());
                    newPOVendorQuote.setVendorPostalCode(address.getVendorZipCode());
                    newPOVendorQuote.setVendorStateCode(address.getVendorStateCode());
                    break;
                }
            }

            String tmpPhoneNumber = null;
            for (VendorPhoneNumber phone : newVendor.getVendorPhoneNumbers()) {
                if (VendorConstants.PhoneTypes.PO.equals(phone.getVendorPhoneTypeCode())) {
                    newPOVendorQuote.setVendorPhoneNumber(phone.getVendorPhoneNumber());
                }
                if (VendorConstants.PhoneTypes.FAX.equals(phone.getVendorPhoneTypeCode())) {
                    newPOVendorQuote.setVendorFaxNumber(phone.getVendorPhoneNumber());
                }
                if (VendorConstants.PhoneTypes.PHONE.equals(phone.getVendorPhoneTypeCode())) {
                    tmpPhoneNumber = phone.getVendorPhoneNumber();
                }
            }
            if (StringUtils.isEmpty(newPOVendorQuote.getVendorPhoneNumber()) && !StringUtils.isEmpty(tmpPhoneNumber)) {
                newPOVendorQuote.setVendorPhoneNumber(tmpPhoneNumber);
            }

            document.getPurchaseOrderVendorQuotes().add(newPOVendorQuote);
        }
        
        String newStipulation = request.getParameter(RicePropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_STIPULATION_DESCRIPTION);
        if (StringUtils.isNotEmpty(newStipulation)) {
            poForm.getNewPurchaseOrderVendorStipulationLine().setVendorStipulationDescription(newStipulation);
        }
        return super.refresh(mapping, form, request, response);
    }

    /**
     * Inactivate an item from the po document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward inactivateItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        PurchaseOrderDocument purDocument = (PurchaseOrderDocument) purchasingForm.getDocument();
        PurchaseOrderItem item = (PurchaseOrderItem)purDocument.getItem(getSelectedLine(request));
        item.setItemActiveIndicator(false);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method is for use with a specific set of methods of this class that create new PO-derived document types in response to user
     * actions, including closePo, reopenPo, paymentHoldPo, removeHoldP, amendPo, and voidPo.  It employs the question framework to ask
     * the user for a reponse before creating and routing the new document.  The response should consist of a note detailing a reason,
     * and either yes or no.  This method can be better understood if it is noted that it will be gone through twice (via the question
     * framework); when each question is originally asked, and again when the yes/no response is processed, for confirmation.
     * 
     * @param mapping           These are boiler-plate.
     * @param form              "
     * @param request           "
     * @param response          "
     * 
     * @param questionType      A string identifying the type of question being asked.
     * @param confirmType       A string identifying which type of question is being confirmed.
     * @param documentType      A string, the type of document to create
     * @param notePrefix        A string to appear before the note in the BO Notes tab     
     * @param messageType       A string to appear on the PO once the question framework is done, describing the action taken   
     * @param operation         A string, the verb to insert in the original question describing the action to be taken
     * 
     * @return An ActionForward
     * @throws Exception
     */
    private ActionForward askQuestionsAndPerformDocumentAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {
        LOG.debug("askQuestionsAndPerformDocumentAction started.");
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        try {
            KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);
    
            // Start in logic for confirming the close.
            if (ObjectUtils.isNull(question)) {
                String key = kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT);
                String message = StringUtils.replace(key, "{0}", operation);
    
                // Ask question if not already asked.
                return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
                    // If 'No' is the button clicked, just reload the doc
                    return returnToPreviousPage(mapping, kualiDocumentFormBase);
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
                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                    }
                }
            }
            // below used as placeholder to allow code to specify actionForward to return if not a 'success question'
            ActionForward returnActionForward = null;
            if (!po.isPendingActionIndicator()) {
                /*  Below if-else code block calls PurchaseOrderService methods that will throw ValidationException
                 *  objects if errors occur during any process in the attempt to perform it's actions.  Assume
                 *  if these return successfully that the PurchaseOrderDocument object returned from each is the 
                 *  newly created document and that all actions in the method were run correctly
                 * 
                 *  NOTE: IF BELOW IF-ELSE IS EDITED THE NEW METHODS CALLED MUST THROW ValidationException OBJECT
                 *  IF AN ERROR IS ADDED TO THE GlobalVariables
                 */
                if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
                    po = SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), documentType, PurchaseOrderStatuses.AMENDMENT);
                    returnActionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                else {
                    po = SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), documentType, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
                }
                if (!GlobalVariables.getErrorMap().isEmpty()) {
                    throw new ValidationException("errors occurred during new PO creation");
                }
                
                String previousDocumentId = kualiDocumentFormBase.getDocId();
                // assume at this point document was created properly and 'po' variable is new PurchaseOrderDocument created
                kualiDocumentFormBase.setDocument(po);
                kualiDocumentFormBase.setDocId(po.getDocumentNumber());
                kualiDocumentFormBase.setDocTypeName(po.getDocumentHeader().getWorkflowDocument().getDocumentType());

                Note newNote = new Note();
                if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
                    noteText = noteText + " (Previous Document Id is " + previousDocumentId + ")";
                }
                newNote.setNoteText(noteText);
                newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                kualiDocumentFormBase.setNewNote(newNote);
                insertBONote(mapping, kualiDocumentFormBase, request, response);
                if (StringUtils.isNotEmpty(messageType)) {
                    GlobalVariables.getMessageList().add(messageType);
                }
            }
            if (ObjectUtils.isNotNull(returnActionForward)) {
                // TODO delyea - should this be a privatized method?
                addExtraButtons(kualiDocumentFormBase);
                return returnActionForward;
            }
            else {
                return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, kualiConfiguration.getPropertyString(messageType), PODocumentsStrings.SINGLE_CONFIRMATION_QUESTION, questionType, "");
            }
        }
        catch (ValidationException ve) {
            addExtraButtons(kualiDocumentFormBase);
            throw ve;
        }
    }

    public ActionForward closePo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("ClosePO started.");
        String operation = "Close ";
        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.CLOSE_QUESTION, PODocumentsStrings.CLOSE_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, PODocumentsStrings.CLOSE_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT, operation);
    }

    public ActionForward paymentHoldPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("PaymentHoldPO started.");
        String operation = "Hold Payment ";
        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.PAYMENT_HOLD_QUESTION, PODocumentsStrings.PAYMENT_HOLD_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT, PODocumentsStrings.PAYMENT_HOLD_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_PAYMENT_HOLD, operation);
    }

    public ActionForward removeHoldPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("RemoveHoldPO started.");
        String operation = "Remove Payment Hold ";
        ActionForward forward = askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.REMOVE_HOLD_QUESTION, PODocumentsStrings.REMOVE_HOLD_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT, PODocumentsStrings.REMOVE_HOLD_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_REMOVE_HOLD, operation);

        // Also need to send an FYI to the AP workgroup.
        // KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        // PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        // WorkgroupVO workgroupVO =
        // SpringContext.getBean(WorkflowGroupService.class).getWorkgroupByGroupName(PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        // SpringContext.getBean(PurchaseOrderService.class).sendFYItoWorkgroup(po, kualiDocumentFormBase.getAnnotation(),
        // workgroupVO.getWorkgroupId() );

        return forward;
    }

    /**
     * This method is invoked when the user pressed on the Open Order button on a Purchase Order page that has status "Close" to
     * reopen the PO. It will display the question page to the user to ask whether the user really wants to reopen the PO and ask
     * the user to enter a reason in the text area. If the user has entered the reason, it will invoke the updateFlagsAndRoute
     * service method to do the processing for reopening a PO, then display a Single Confirmation page to inform the user that the
     * PO Reopen Document has been routed.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward reopenPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Reopen PO started");
        String operation = "Reopen ";
        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.REOPEN_PO_QUESTION, PODocumentsStrings.CONFIRM_REOPEN_QUESTION, PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, PODocumentsStrings.REOPEN_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_REOPEN_DOCUMENT, operation);
    }

    /**
     * This method...
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward amendPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Amend PO started");
        String operation = "Amend ";
        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.AMENDMENT_PO_QUESTION, PODocumentsStrings.CONFIRM_AMENDMENT_QUESTION, PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PODocumentsStrings.AMENDMENT_NOTE_PREFIX, null, operation);
    }

    public ActionForward voidPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Void PO started");
        String operation = "Void ";
        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.VOID_QUESTION, PODocumentsStrings.VOID_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT, PODocumentsStrings.VOID_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_VOID_DOCUMENT, operation);
    }
    
    /**
     * This is a utility method used to prepare to and to return to a previous page, making sure that the buttons will be restored in the
     * process.
     * 
     * @param kualiDocumentFormBase     The Form, considered as a KualiDocumentFormBase, as it typically is here.
     * @return  An actionForward mapping back to the original page.
     */
    protected ActionForward returnToPreviousPage(ActionMapping mapping, KualiDocumentFormBase kualiDocumentFormBase) {
        addExtraButtons(kualiDocumentFormBase);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    private void addExtraButtons(KualiDocumentFormBase kualiDocumentFormBase) {
        ((PurchaseOrderForm) kualiDocumentFormBase).addButtons();
    }
    
    @Override
    public ActionForward refreshAccountSummary(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.refreshAccountSummary(mapping, form, request, response);
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase)form;
        addExtraButtons(kualiDocumentFormBase);
        return forward;
    }

    /**
     * This method is executed when the user click on the "print" button on a Purchase Order Print Document page. On a non
     * javascript enabled browser, it will display a page with 2 buttons. One is to display the PDF, the other is to view
     * the PO tabbed page where the PO document statuses, buttons, etc had already been updated (the updates of those
     * occurred while the performPurchaseOrderFirstTransmitViaPrinting method is invoked.
     * On a javascript enabled browser, it will display both the PO tabbed page containing the updated PO document info
     * and the pdf on the next window/tab of the browser.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward firstTransmitPrintPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String poDocId = request.getParameter("docId");
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            SpringContext.getBean(PurchaseOrderService.class).performPurchaseOrderFirstTransmitViaPrinting(poDocId, baosPDF);
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        String basePath = getBasePath(request);
        String docId = ((PurchaseOrderForm)form).getDocId();
        String methodToCallPrintPurchaseOrderPDF = "printPurchaseOrderPDFOnly";
        String methodToCallDocHandler = "docHandler";
        String printPOPDFUrl = getUrlForPrintPO(basePath, docId, methodToCallPrintPurchaseOrderPDF);
        String displayPOTabbedPageUrl = getUrlForPrintPO(basePath, docId, methodToCallDocHandler);
        request.setAttribute("printPOPDFUrl", printPOPDFUrl);
        request.setAttribute("displayPOTabbedPageUrl", displayPOTabbedPageUrl);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(PurchaseOrderDocument.class);
        request.setAttribute("purchaseOrderLabel", label);
        return mapping.findForward("printPurchaseOrderPDF");
    }

    private String getUrlForPrintPO(String basePath, String docId, String methodToCall) {
        StringBuffer result = new StringBuffer(basePath);
        result.append("/purapPurchaseOrder.do?methodToCall=");
        result.append(methodToCall);
        result.append("&docId=");
        result.append(docId);
        result.append("&command=displayDocSearchView");
        return result.toString();
    }
    
    public ActionForward printPurchaseOrderPDFOnly(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String poDocId = request.getParameter("docId");
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            // will throw validation exception if errors occur
            SpringContext.getBean(PurchaseOrderService.class).performPrintPurchaseOrderPDFOnly(poDocId, baosPDF);

            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            String useJavascript = request.getParameter("useJavascript");
            if (useJavascript == null || useJavascript.equalsIgnoreCase("false")) {
                sbContentDispValue.append("attachment");
            }
            else {
                sbContentDispValue.append("inline");
            }
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(poDocId);
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        return null;
    }
    
    
    public ActionForward printPoQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        String poDocId = request.getParameter("docId");
//        PurchaseOrderDocument po = (PurchaseOrderDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
//        Integer poSelectedVendorId = new Integer(request.getParameter("quoteVendorId"));
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        PurchaseOrderVendorQuote poVendorQuote = po.getPurchaseOrderVendorQuotes().get(getSelectedLine(request));
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        poVendorQuote.setTransmitPrintDisplayed(false);
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            // for testing Generate PO PDF, set the APO to true
            po.setPurchaseOrderAutomaticIndicator(true);
            boolean success = SpringContext.getBean(PurchaseOrderService.class).printPurchaseOrderQuotePDF(po, poVendorQuote, baosPDF);

            if (!success) {
                poVendorQuote.setTransmitPrintDisplayed(true);
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            //sbContentDispValue.append("inline");
            sbContentDispValue.append("attachment");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        return null;
    }

    public ActionForward printPoQuoteList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_LIST_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            // for testing Generate PO PDF, set the APO to true
            po.setPurchaseOrderAutomaticIndicator(true);
            boolean success = SpringContext.getBean(PurchaseOrderService.class).printPurchaseOrderQuoteRequestsListPDF(po,  baosPDF);

            if (!success) {
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(KFSConstants.MAPPING_PORTAL);
            }
            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            //sbContentDispValue.append("inline");
            sbContentDispValue.append("attachment");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        return null;
    }

    /*  TODO PURAP - should this method be transmitting the PO or just setting up the dates?
     *             - should this method be saving the entire PO or just the vendorQuote object (if in fact nothing on the PO is edited)
     */
    public ActionForward transmitPurchaseOrderQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        PurchaseOrderVendorQuote vendorQuote = (PurchaseOrderVendorQuote)po.getPurchaseOrderVendorQuotes().get(getSelectedLine(request));
        if (PurapConstants.QuoteTransmitTypes.PRINT.equals(vendorQuote.getPurchaseOrderQuoteTransmitTypeCode())) {
            vendorQuote.setPurchaseOrderQuoteTransmitDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            vendorQuote.setTransmitPrintDisplayed(true);
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(po);
        }
        else {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_TRANSMIT_TYPE_NOT_SELECTED);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is invoked when the user clicks on the Select All button on a Purchase Order Retransmit document. It will select
     * the checkboxes of all the items to be included in the retransmission of the PO.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectAllForRetransmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        List<PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem item : items) {
            item.setItemSelectedForRetransmitIndicator(true);
        }
        return returnToPreviousPage(mapping, kualiDocumentFormBase);
    }

    /**
     * This method is invoked when the user clicks on the Deselect All button on a Purchase Order Retransmit document. It will
     * uncheck the checkboxes of all the items to be excluded from the retransmission of the PO.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deselectAllForRetransmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        List<PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem item : items) {
            item.setItemSelectedForRetransmitIndicator(false);
        }
        return returnToPreviousPage(mapping, kualiDocumentFormBase);
    }

    /**
     * This method is invoked when the user clicks on the Retransmit button on both the PO tabbed page and on the Purchase Order
     * Retransmit Document page, which is essentially a PO tabbed page with the other irrelevant tabs being hidden. If it was
     * invoked from the PO tabbed page, if the PO's pending indicator is false, this method will invoke the updateFlagsAndRoute in
     * the PurchaseOrderService to update the flags, create the PurchaseOrderRetransmitDocument and route it. If the routing was
     * successful, we'll display the Purchase Order Retransmit Document page to the user, containing the newly created and routed
     * PurchaseOrderRetransmitDocument and a retransmit button as well as a list of items that the user can select to be
     * retransmitted. If it was invoked from the Purchase Order Retransmit Document page, we'll invoke the
     * retransmitPurchaseOrderPDF method to create a PDF document based on the PO information and the items that were selected by
     * the user on the Purchase Order Retransmit Document page to be retransmitted, then display the PDF to the browser.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward retransmitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();

        boolean success;
        if (po.isPendingActionIndicator()) {
            success = false;
            // TODO PURAP - below should be using a propertyName value to show error at a document level.... not field specific
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
        }
        else {
            po = SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        }

        kualiDocumentFormBase.setDocument(po);
        // we only need to set the editing mode to displayRetransmitTab if it's not yet
        // in the editingMode.
        if (!kualiDocumentFormBase.getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB)) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer(po);
            kualiDocumentFormBase.populateAuthorizationFields(documentAuthorizer);
        }
        return returnToPreviousPage(mapping, kualiDocumentFormBase);
    }

    public ActionForward printingRetransmitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();

        List items = po.getItems();
        String retransmitHeader = po.getRetransmitHeader();
        po = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(po.getDocumentNumber());
        //setting the isItemSelectedForRetransmitIndicator items of the PO obtained from the database based on its value from 
        //the po from the form
        setItemSelectedForRetransmitIndicatorFromPOInForm(items, po.getItems());
        po.setRetransmitHeader(retransmitHeader);
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            // below method will throw ValidationException if errors are found
            SpringContext.getBean(PurchaseOrderService.class).retransmitPurchaseOrderPDF(po, baosPDF);

            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            sbContentDispValue.append("inline");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
            return mapping.findForward(KFSConstants.MAPPING_ERROR);
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        return null;
    }

    private void setItemSelectedForRetransmitIndicatorFromPOInForm(List itemsFromForm, List itemsFromDB) {
        int i=0;
        for (PurchaseOrderItem itemFromForm : (List<PurchaseOrderItem>)itemsFromForm) {
            ((PurchaseOrderItem)(itemsFromDB.get(i))).setItemSelectedForRetransmitIndicator(itemFromForm.isItemSelectedForRetransmitIndicator());
            i++;
        }
    }
    
    /**
     * This method is to check on a few conditions that would cause a warning message to be displayed on top of the Purchase Order
     * page.
     * 
     * @param po the PurchaseOrderDocument whose status and indicators are to be checked in the conditions
     * @return boolean true if the Purchase Order doesn't have any warnings and false otherwise.
     */
    private void checkForPOWarnings(PurchaseOrderDocument po, ActionMessages messages) {
        // "This is not the current version of this Purchase Order." (curr_ind = N and doc status is not enroute)
        if (!po.isPurchaseOrderCurrentIndicator() && !po.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_NOT_CURRENT);
        }
        // "This document is a pending action. This is not the current version of this Purchase Order" (curr_ind = N and doc status
        // is enroute)
        if (!po.isPurchaseOrderCurrentIndicator() && po.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_PENDING_ACTION_NOT_CURRENT);
        }
        // "There is a pending action on this Purchase Order." (pend_action = Y)
        if (po.isPendingActionIndicator()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_PENDING_ACTION);
        }

        if (!po.isPurchaseOrderCurrentIndicator()) {
            ActionMessage noteMessage = new ActionMessage(PurapKeyConstants.WARNING_PURCHASE_ORDER_ALL_NOTES);
            ActionMessage statusHistoryMessage = new ActionMessage(PurapKeyConstants.WARNING_PURCHASE_ORDER_ENTIRE_STATUS_HISTORY);
            messages.add(PurapConstants.NOTE_TAB_WARNING, noteMessage);
            messages.add(PurapConstants.STATUS_HISTORY_TAB_WARNING, statusHistoryMessage);
        }
    }

    /**
     * Add a stipulation to the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addStipulation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        if (StringUtils.isBlank(poForm.getNewPurchaseOrderVendorStipulationLine().getVendorStipulationDescription())) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_STIPULATION, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
        }
        else {
            PurchaseOrderVendorStipulation newStipulation = poForm.getAndResetNewPurchaseOrderVendorStipulationLine();
            document.getPurchaseOrderVendorStipulations().add(newStipulation);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete a stipulation from the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteStipulation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        document.getPurchaseOrderVendorStipulations().remove(getSelectedLine(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) This
     *      method overrides the docHandler method in the superclass. In addition to doing the normal process in the superclass and
     *      returning its action forward from the superclass, it also invokes the checkForPOWarnings method to check on a few
     *      conditions that could have caused warning messages to be displayed on top of Purchase Order page.
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.docHandler(mapping, form, request, response);
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        addExtraButtons(kualiDocumentFormBase);
        PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();
        ActionMessages messages = new ActionMessages();
        checkForPOWarnings(po, messages);
        saveMessages(request, messages);
      
        return forward;
    }
   
    /*  TODO PURAP/delyea - should the following be a custom save event instead of the current business logic in the action method?
     */
    public ActionForward initiateQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        if (!PurapConstants.PurchaseOrderStatuses.IN_PROCESS.equals(document.getStatusCode())) {
            // PO must be "in process" in order to initiate a quote
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NOT_IN_PROCESS);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        document.setStatusCode(PurapConstants.PurchaseOrderStatuses.QUOTE);
        Date currentSqlDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        Date expDate = new Date(currentSqlDate.getTime() + (10 * 24 * 60 * 60 * 1000)); //add 10 days - TODO: need to move this into a DB setting
        document.setPurchaseOrderQuoteDueDate(expDate);
        document.getPurchaseOrderVendorQuotes().clear();
        SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(document);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        poForm.getNewPurchaseOrderVendorQuote().setDocumentNumber(document.getDocumentNumber());
        document.getPurchaseOrderVendorQuotes().add(poForm.getNewPurchaseOrderVendorQuote());
        poForm.setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        document.refreshNonUpdateableReferences();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        document.getPurchaseOrderVendorQuotes().remove(getSelectedLine(request));
        document.refreshNonUpdateableReferences();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /*  TODO PURAP/delyea - should the following be a custom save event instead of the current business logic in the action method?
     */
    public ActionForward completeQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        PurchaseOrderVendorQuote awardedQuote = new PurchaseOrderVendorQuote();
        // verify quote status fields
        if (poForm.getAwardedVendorNumber() == null) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NO_VENDOR_AWARDED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else {
            awardedQuote = document.getPurchaseOrderVendorQuote(poForm.getAwardedVendorNumber().intValue());
            if (awardedQuote.getPurchaseOrderQuoteStatusCode() == null) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NOT_TRANSMITTED);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        
        // use question framework to make sure they REALLY want to complete the quote...
        String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_AWARD);
        String vendorRow = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_AWARD_ROW);
        
        String tempRows = "";
        for (PurchaseOrderVendorQuote poQuote : document.getPurchaseOrderVendorQuotes()) {
            String tempRow = vendorRow;
            tempRow = StringUtils.replace(tempRow, "{0}", poQuote.getVendorName());
        if (poQuote.getPurchaseOrderQuoteAwardDate() == null) {
                if (awardedQuote.getVendorNumber().equals(poQuote.getVendorNumber())) {
                    tempRow = StringUtils.replace(tempRow, "{1}", SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().toString());
        }
        else {
                    tempRow = StringUtils.replace(tempRow, "{1}", "");
        }
            }
            else {
                tempRow = StringUtils.replace(tempRow, "{1}", poQuote.getPurchaseOrderQuoteAwardDate().toString());
            }
            if (poQuote.getPurchaseOrderQuoteStatusCode() != null) {
                tempRow = StringUtils.replace(tempRow, "{2}", poQuote.getPurchaseOrderQuoteStatusCode());
            }
            else {
                tempRow = StringUtils.replace(tempRow, "{2}", "N/A");
            }
            if (poQuote.getPurchaseOrderQuoteRankNumber() != null) {
                tempRow = StringUtils.replace(tempRow, "{3}", poQuote.getPurchaseOrderQuoteRankNumber());
            }
            else {
                tempRow = StringUtils.replace(tempRow, "{3}", "N/A");
            }
            tempRows += tempRow;
        }
        message = StringUtils.replace(message, "{0}", tempRows);

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);

        if (question == null) {
            // ask question if not already asked
            return performQuestionWithoutInput(mapping, form, request, response, PODocumentsStrings.CONFIRM_AWARD_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION,  PODocumentsStrings.CONFIRM_AWARD_RETURN, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PODocumentsStrings.CONFIRM_AWARD_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // set awarded date
                awardedQuote.setPurchaseOrderQuoteAwardDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

                // PO vendor information updated with awarded vendor
                document.setVendorName(awardedQuote.getVendorName());
                document.setVendorNumber(awardedQuote.getVendorNumber());
                document.setVendorHeaderGeneratedIdentifier(awardedQuote.getVendorHeaderGeneratedIdentifier());
                document.setVendorDetailAssignedIdentifier(awardedQuote.getVendorDetailAssignedIdentifier());
//                document.setVendorDetail(poQuote.getVendorDetail());
                document.setVendorLine1Address(awardedQuote.getVendorLine1Address());
                document.setVendorLine2Address(awardedQuote.getVendorLine2Address());
                document.setVendorCityName(awardedQuote.getVendorCityName());
                document.setVendorStateCode(awardedQuote.getVendorStateCode());
                document.setVendorCountryCode(awardedQuote.getVendorCountryCode());
                document.setVendorPhoneNumber(awardedQuote.getVendorPhoneNumber());
                document.setVendorFaxNumber(awardedQuote.getVendorFaxNumber());

                document.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
            }
        }
        SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(document);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /*  TODO PURAP/delyea - should the following be a custom save event instead of the current business logic in the action method?
     */
    public ActionForward cancelQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        for (PurchaseOrderVendorQuote quotedVendors : document.getPurchaseOrderVendorQuotes()) {
            if (quotedVendors.getPurchaseOrderQuoteTransmitDate() != null) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUOTE_TRANSMITTED, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_CANCEL_QUOTE);
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);

        if (question == null) {
            // ask question if not already asked
            return performQuestionWithInput(mapping, form, request, response, PODocumentsStrings.CONFIRM_CANCEL_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION,  PODocumentsStrings.CONFIRM_CANCEL_RETURN, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PODocumentsStrings.CONFIRM_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);

                if (StringUtils.isEmpty(reason)) {
                    return performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PODocumentsStrings.CONFIRM_CANCEL_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION,  PODocumentsStrings.CONFIRM_CANCEL_RETURN, "", "", PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "250");
                }
                /*   TODO PURAP - should this code below have a save at the end?
                 *                Isn't that why we're asking the confirming question because this code below will cancel the quote 
                 *                and save the PO... otherwise why do we need a confirming question since they would have to save 
                 *                after canceling anyway?
                 */
                document.getPurchaseOrderVendorQuotes().clear();
                Note cancelNote = new Note();
                cancelNote.setAuthorUniversalIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
                String reasonPrefix = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.PURCHASE_ORDER_CANCEL_QUOTE_NOTE_TEXT);
                cancelNote.setNoteText(reasonPrefix + reason);
                document.addNote(cancelNote);
                document.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        // this should probably be moved into a private instance variable
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        // logic for cancel question
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_CANCEL_QUESTION, kualiConfiguration.getPropertyString("document.question.cancel.text"), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CANCEL, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((KFSConstants.DOCUMENT_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // if no button clicked just reload the doc
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            // else go to cancel logic below
        }

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        SpringContext.getBean(DocumentService.class).cancelDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation());

        return returnToSender(mapping, kualiDocumentFormBase);
    }
    
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm)form;
        PurchaseOrderDocument po = poForm.getPurchaseOrderDocument();
        
        if(StringUtils.isNotBlank(po.getStatusCode()) &&
           StringUtils.isNotBlank(po.getStatusChange()) &&
           (!StringUtils.equals(po.getStatusCode(), po.getStatusChange()))) {
            
            KualiWorkflowDocument workflowDocument = po.getDocumentHeader().getWorkflowDocument();
            if (ObjectUtils.isNull(workflowDocument) || workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
                return this.askSaveQuestions(mapping, form, request, response,PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION);
            }
        }
        
        return super.save(mapping, form, request, response);
    }
    
    private ActionForward askSaveQuestions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType) {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);
        ActionForward forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
        String notePrefix = "";
        
        if (StringUtils.equals(questionType,PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION) && ObjectUtils.isNull(question)) {
            String message = kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_MANUAL_STATUS_CHANGE);           
            try {
                return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
                // If 'No' is the button clicked, just reload the doc
                addExtraButtons(kualiDocumentFormBase);
                return forward;
            }
            
            // Build out full message.
            if (StringUtils.equals(questionType,PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION)) {
                /* THIS MAP MOVED BY DELYEA FROM PurapConstants TO HERE
                 * TODO PURAP - The map below is hard coding values that need to be coming from the database instead
                 */
                Map<String,String> manuallyChangeableStatuses = new HashMap<String, String>();
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.IN_PROCESS,"In Process");
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.WAITING_FOR_VENDOR,"Waiting for Vendor");
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT,"Waiting for Department");

                String key = kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_MANUAL_STATUS_CHANGE_NOTE_PREFIX);
                String oldStatus = manuallyChangeableStatuses.get(po.getStatusCode());
                String newStatus = manuallyChangeableStatuses.get(po.getStatusChange());
                key = StringUtils.replace(key, "{0}", (StringUtils.isBlank(oldStatus) ? " " : oldStatus));
                notePrefix = StringUtils.replace(key, "{1}", (StringUtils.isBlank(newStatus) ? " " : newStatus));
            }
            String noteText = notePrefix + KFSConstants.BLANK_SPACE + reason;
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
                
                try {
                    if (StringUtils.equals(questionType,PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION)) {
                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_MANUAL_STATUS_CHANGE), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (StringUtils.equals(questionType,PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION)) {
                executeManualStatusChange(po);
                try {
                    forward = super.save(mapping, form, request, response);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            Note newNote = new Note();
            newNote.setNoteText(noteText);
            newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
            kualiDocumentFormBase.setNewNote(newNote);
            try {
                insertBONote(mapping, kualiDocumentFormBase, request, response);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        addExtraButtons(kualiDocumentFormBase);
        return forward;
    }
    
    private void executeManualStatusChange(PurchaseOrderDocument po) {
        try {           
            SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(po, po.getStatusChange());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.kuali.module.purap.web.struts.action.PurchasingAccountsPayableActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        PurchaseOrderForm form = (PurchaseOrderForm)kualiDocumentFormBase;
        PurchaseOrderDocument po = (PurchaseOrderDocument)form.getDocument();
        form.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
    }

}