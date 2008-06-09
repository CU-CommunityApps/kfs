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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.rule.event.ContinueCustomerCreditMemoDocumentEvent;
import org.kuali.module.ar.rule.event.RecalculateCustomerCreditMemoDetailEvent;
import org.kuali.module.ar.rule.event.RecalculateCustomerCreditMemoDocumentEvent;
import org.kuali.module.ar.service.CustomerCreditMemoDetailService;
import org.kuali.module.ar.service.CustomerCreditMemoDocumentService;
import org.kuali.module.ar.web.struts.form.CustomerCreditMemoDocumentForm;

import edu.iu.uis.eden.exception.WorkflowException;

//public class CustomerCreditMemoDocumentAction extends KualiAccountingDocumentActionBase {
public class CustomerCreditMemoDocumentAction extends KualiTransactionalDocumentActionBase {
    
    public CustomerCreditMemoDocumentAction() {
        super();
    }
    
    /**
     * Do initialization for a new customer credit memo.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((CustomerCreditMemoDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }
    
    /**
     * Clears out init tab.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearInitTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();
        customerCreditMemoDocument.clearInitFields();
        
        return super.refresh(mapping, form, request, response);
    }   
    
    /**
     * Handles continue request. This request comes from the initial screen which gives ref. invoice number.
     * Based on that, the customer credit memo is initially populated.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward continueCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();
        
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new ContinueCustomerCreditMemoDocumentEvent(errorPath,customerCreditMemoDocument));
        if (rulePassed)
            customerCreditMemoDocument.populateCustomerCreditMemoDetails();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Based on user input this method recalculates a customer credit memo detail
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward recalculateCustomerCreditMemoDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocumentForm.getDocument();
        
        int indexOfLineToRecalculate = getSelectedLine(request);
        CustomerCreditMemoDetail customerCreditMemoDetail = customerCreditMemoDocument.getCreditMemoDetails().get(indexOfLineToRecalculate);
     
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.CUSTOMER_CREDIT_MEMO_DETAIL_PROPERTY_NAME + "[" + indexOfLineToRecalculate + "]";

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerCreditMemoDetailEvent(errorPath, customerCreditMemoDocument, customerCreditMemoDetail));
        if (rulePassed) {
            CustomerCreditMemoDetailService customerCreditMemoDetailService = SpringContext.getBean(CustomerCreditMemoDetailService.class);
            customerCreditMemoDetailService.recalculateCustomerCreditMemoDetail(customerCreditMemoDetail,customerCreditMemoDocument);
        } else {
            customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetail);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method refreshes a customer credit memo detail
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward refreshCustomerCreditMemoDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerCreditMemoDocumentForm customerCreditMemoDocForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocForm.getDocument();
        int indexOfLineToRefresh = getSelectedLine(request);
        
        CustomerCreditMemoDetail customerCreditMemoDetail = customerCreditMemoDocument.getCreditMemoDetails().get(indexOfLineToRefresh);
        
        customerCreditMemoDetail.setCreditMemoItemQuantity(null);
        customerCreditMemoDetail.setCreditMemoItemTotalAmount(null);
        customerCreditMemoDetail.setCreditMemoItemTaxAmount(KualiDecimal.ZERO);
        customerCreditMemoDetail.setCreditMemoLineTotalAmount(KualiDecimal.ZERO);
        
        customerCreditMemoDocument.recalculateTotals(customerCreditMemoDetail);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
 
    /**
     * This method refreshes customer credit memo details and line with totals
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward refreshCustomerCreditMemoDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerCreditMemoDocumentForm customerCreditMemoDocForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocForm.getDocument();
    
        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();
        for( CustomerCreditMemoDetail customerCreditMemoDetail : customerCreditMemoDetails ){
            customerCreditMemoDetail.setCreditMemoItemQuantity(null);
            customerCreditMemoDetail.setCreditMemoItemTotalAmount(null);
            customerCreditMemoDetail.setCreditMemoItemTaxAmount(KualiDecimal.ZERO);
            customerCreditMemoDetail.setCreditMemoLineTotalAmount(KualiDecimal.ZERO);
            customerCreditMemoDetail.setDuplicateCreditMemoItemTotalAmount(null);
        }
        customerCreditMemoDocument.setCrmTotalItemAmount(KualiDecimal.ZERO);
        customerCreditMemoDocument.setCrmTotalTaxAmount(KualiDecimal.ZERO);
        customerCreditMemoDocument.setCrmTotalAmount(KualiDecimal.ZERO);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Based on user input this method recalculates customer credit memo document <=> all customer credit memo details
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward recalculateCustomerCreditMemoDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocumentForm.getDocument();

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerCreditMemoDocumentEvent(errorPath,customerCreditMemoDocument));
        if (rulePassed) {
            CustomerCreditMemoDocumentService customerCreditMemoDocumentService = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
            customerCreditMemoDocumentService.recalculateCustomerCreditMemoDocument(customerCreditMemoDocument);
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
