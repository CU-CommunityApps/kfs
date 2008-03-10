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
package org.kuali.module.ar.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.bo.NonAppliedHolding;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.rule.event.AddCashControlDetailEvent;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.web.struts.form.CashControlDocumentForm;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

public class CashControlDocumentAction extends KualiTransactionalDocumentActionBase {
    
    /**
     * Adds handling for cash control detail amount updates.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
       ActionForward forward = super.execute(mapping, form, request, response);
        CashControlDocumentForm ccForm = (CashControlDocumentForm) form;
        CashControlDocument ccDoc = ccForm.getCashControlDocument();

        if (ccDoc != null) {
            String refFinancialDocNbr = ccDoc.getReferenceFinancialDocumentNumber();
            ccForm.setHasGeneratedRefDoc(refFinancialDocNbr != null && !refFinancialDocNbr.equals(""));
        }

        if (ccForm.hasDocumentId()) {
            ccDoc = ccForm.getCashControlDocument();

            ccDoc.setCashControlTotalAmount(calculateCashControlTotal(ccDoc)); // recalc b/c changes to the amounts could
            // have happened
        }
        
        DocumentService documentService = KNSServiceLocator.getDocumentService();
        if(ccDoc.getCashControlDetails().size()>0)
        {
            for(CashControlDetail detail : ccDoc.getCashControlDetails())
            {
               PaymentApplicationDocument paymentDoc= (PaymentApplicationDocument)documentService.getByDocumentHeaderId(detail.getReferenceFinancialDocumentNumber());
               detail.setReferenceFinancialDocument(paymentDoc);
            }
        }

        // proceed as usual
        return forward;
    }

    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        CashControlDocumentForm form = (CashControlDocumentForm) kualiDocumentFormBase;
        CashControlDocument document = form.getCashControlDocument();
        

        //set up the default values for the AR DOC Header (SHOULD PROBABLY MAKE THIS A SERVICE)
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode(currentUser.getChartOfAccountsCode());
        accountsReceivableDocumentHeader.setProcessingOrganizationCode(currentUser.getOrganizationCode());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        
    }        
    
    /**
     * This method adds a new cash control detail
     * @param mapping action mapping
     * @param form action form
     * @param request 
     * @param response
     * @return forward action
     * @throws Exception
     */
    public ActionForward addCashControlDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument cashControlDocument = cashControlDocForm.getCashControlDocument();
        if (cashControlDocument.getReferenceFinancialDocumentNumber() != null && !cashControlDocument.getReferenceFinancialDocumentNumber().equals("")) {
            GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
            GlobalVariables.getErrorMap().putError("referenceFinancialDocumentNumber", ArConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL);
            GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        CashControlDetail newCashControlDetail = cashControlDocForm.getNewCashControlDetail();
        newCashControlDetail.setDocumentNumber(cashControlDocument.getDocumentNumber());

        // apply rules for the new cash control detail
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddCashControlDetailEvent(ArConstants.NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX, cashControlDocument, newCashControlDetail));

        // add the new detail if rules passed
        if (rulePassed) {

            DocumentService documentService = KNSServiceLocator.getDocumentService();

            // create a new PaymentApplicationdocument
            PaymentApplicationDocument doc = (PaymentApplicationDocument) documentService.getNewDocument("PaymentApplicationDocument");
            // set a description to say that this application document has been created by the CashControldocument
            doc.getDocumentHeader().setFinancialDocumentDescription("Created by Cash Control Document");

            // set up the default values for the AR DOC Header
            AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
            accountsReceivableDocumentHeader.setDocumentNumber(doc.getDocumentNumber());
            doc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

            // set the non applied holding
            NonAppliedHolding nonAppliedHolding = new NonAppliedHolding();
            nonAppliedHolding.setReferenceFinancialDocumentNumber(doc.getDocumentNumber());
            doc.setNonAppliedHolding(nonAppliedHolding);

            // the line amount for the new PaymentApplicationDocument should be the line amount in the new cash control detail
            doc.getDocumentHeader().setFinancialDocumentTotalAmount(newCashControlDetail.getFinancialDocumentLineAmount());

            // refresh nonupdatable references and save the PaymentApplicationDocument
            doc.refreshNonUpdateableReferences();
            documentService.saveDocument(doc);

            // update new cash control detail fields to refer to the new created PaymentApplicationDocument
            newCashControlDetail.setReferenceFinancialDocument(doc);
            newCashControlDetail.setReferenceFinancialDocumentNumber(doc.getDocumentNumber());
            // newCashControlDetail.setStatus(doc.getDocumentHeader().getWorkflowDocument().getStatusDisplayValue());

            // add cash control detail
            cashControlDocument.addCashControlDetail(newCashControlDetail);

            // set a new blank cash control detail
            cashControlDocForm.setNewCashControlDetail(new CashControlDetail());

        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }
    
    /**
     * This method deletes a cash control detail
     * @param mapping action mapping 
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward deleteCashControlDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument cashControlDocument = cashControlDocForm.getCashControlDocument();
        if (cashControlDocument.getReferenceFinancialDocumentNumber() != null && !cashControlDocument.getReferenceFinancialDocumentNumber().equals("")) {
            GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
            GlobalVariables.getErrorMap().putError("referenceFinancialDocumentNumber", ArConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL);
            GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        int indexOfLineToDelete = getLineToDelete(request);
        CashControlDetail cashControlDetail = cashControlDocument.getCashControlDetail(indexOfLineToDelete);
        DocumentService documentService = KNSServiceLocator.getDocumentService();

        PaymentApplicationDocument applicationDocument = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(cashControlDetail.getReferenceFinancialDocumentNumber());
        documentService.cancelDocument(applicationDocument, "The document has been deleted from the Cash Control Document details");
        // cashControlDocument.deleteCashControlDetail(indexOfLineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method generates the reference document.
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward generateRefDoc(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument cashControlDocument = cashControlDocForm.getCashControlDocument();
        if (cashControlDocument.getReferenceFinancialDocumentNumber() != null && !cashControlDocument.getReferenceFinancialDocumentNumber().equals("")) {
            GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
            GlobalVariables.getErrorMap().putError("referenceFinancialDocumentNumber", ArConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL);
            GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        String paymentMediumCode = cashControlDocument.getCustomerPaymentMediumCode();

        if (paymentMediumCode == null || paymentMediumCode.equals("")) {
            GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
            GlobalVariables.getErrorMap().putError("customerPaymentMediumCode", ArConstants.ERROR_PAYMENT_MEDIUM_CANNOT_BE_NULL);
            GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        }
        else {
            String refDocNbr = createReferenceDocument(paymentMediumCode);
            cashControlDocument.setReferenceFinancialDocumentNumber(refDocNbr);

            cashControlDocForm.setHasGeneratedRefDoc(false);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }
    
    /**
     * Recalculates the cash control total since user could have changed it during their update.
     * 
     * @param cashControlDocument
     */
    private KualiDecimal calculateCashControlTotal(CashControlDocument cashControlDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        for(CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {
           
            total = total.add(cashControlDetail.getFinancialDocumentLineAmount());
        }
        return total;
    }
    
    /**
     * This method creates the reference document.
     * @param paymentMediumCode the code of the selected payment medium
     * @return the reference document number
     */
    private String createReferenceDocument(String paymentMediumCode) {
        return "254689";
    }
}
