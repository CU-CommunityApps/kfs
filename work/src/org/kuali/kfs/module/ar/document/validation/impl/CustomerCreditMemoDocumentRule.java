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
package org.kuali.module.ar.rules;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.UnknownDocumentIdException;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.rule.ContinueCustomerCreditMemoDocumentRule;
import org.kuali.module.ar.rule.RecalculateCustomerCreditMemoDetailRule;
import org.kuali.module.ar.rule.RecalculateCustomerCreditMemoDocumentRule;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class holds the business rules for the AR Credit Memo Document
 */

public class CustomerCreditMemoDocumentRule extends TransactionalDocumentRuleBase implements RecalculateCustomerCreditMemoDetailRule<TransactionalDocument>,
                                                                                             RecalculateCustomerCreditMemoDocumentRule<TransactionalDocument>,
                                                                                             ContinueCustomerCreditMemoDocumentRule<TransactionalDocument> {
    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= processRecalculateCustomerCreditMemoDocumentRules((TransactionalDocument)document, true);

        return isValid;
    }
    
    /**
     * @see org.kuali.module.ar.rule.RecalculateCustomerCreditMemoDetailRule#processRecalculateCustomerCreditMemoDetailRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.module.ar.bo.CustomerCreditMemoDetail)
     */
    public boolean processRecalculateCustomerCreditMemoDetailRules(TransactionalDocument document, CustomerCreditMemoDetail customerCreditMemoDetail) {
        boolean success = true;
 
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)document;
        String inputKey = isQtyOrItemAmountEntered(customerCreditMemoDetail);
        
        // 'Qty' was entered
        if (StringUtils.equals(ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY,inputKey)) {
            success &= isValueGreaterThanZero(customerCreditMemoDetail.getCreditMemoItemQuantity());
            success &= isCustomerCreditMemoQtyGreaterThanInvoiceQty(customerCreditMemoDetail);
        }
        // 'Item Amount' was entered
        else if (StringUtils.equals(ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT,inputKey)) { 
            success &= isValueGreaterThanZero(customerCreditMemoDetail.getCreditMemoItemTotalAmount());
            success &= isCustomerCreditMemoItemAmountGreaterThanInvoiceOpenItemAmount(customerCreditMemoDetail,customerCreditMemoDocument);
        }
        // if there is no input or if both 'Qty' and 'Item Amount' were entered -> wrong input
        else
            success = false;
        
        return success;
    }
    
    private String isQtyOrItemAmountEntered(CustomerCreditMemoDetail customerCreditMemoDetail) {

            BigDecimal customerCreditMemoItemQty = customerCreditMemoDetail.getCreditMemoItemQuantity();
            KualiDecimal customerCreditMemoItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
            String inputKey = "";
           
            if (ObjectUtils.isNotNull(customerCreditMemoItemQty) && ObjectUtils.isNotNull(customerCreditMemoItemAmount)) {
                inputKey = ArConstants.CustomerCreditMemoConstants.BOTH_QUANTITY_AND_ITEM_TOTAL_AMOUNT_ENTERED;
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT);
            } else if (ObjectUtils.isNotNull(customerCreditMemoItemQty))
                inputKey = ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY;
            else if (ObjectUtils.isNotNull(customerCreditMemoItemAmount))
                inputKey = ArConstants.CustomerCreditMemoConstants.CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT;
            
            return inputKey;
    }
    
    private boolean isValueGreaterThanZero(BigDecimal value) {
        boolean validValue = (value.compareTo(BigDecimal.ZERO) == 1 ?true:false);
        if (!validValue)
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO); 
        return validValue;
    }
    
    private boolean isValueGreaterThanZero(KualiDecimal value) {
        boolean validValue = value.isPositive();
        if (!validValue)
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO); 
        return validValue;
    }
    
    private boolean isCustomerCreditMemoItemAmountGreaterThanInvoiceOpenItemAmount(CustomerCreditMemoDetail customerCreditMemoDetail,CustomerCreditMemoDocument customerCreditMemoDocument){
        int lineNumber = customerCreditMemoDetail.getReferenceInvoiceItemNumber().intValue();
        KualiDecimal invoiceOpenItemAmount = customerCreditMemoDetail.getInvoiceOpenItemAmount();
        KualiDecimal creditMemoItemAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount();
        
        boolean validItemAmount = creditMemoItemAmount.isLessEqual(invoiceOpenItemAmount);
        if (!validItemAmount)
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_TOTAL_AMOUNT, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_GREATER_THAN_INVOICE_ITEM_AMOUNT);
            
        return validItemAmount;
    }
    
    private boolean isCustomerCreditMemoQtyGreaterThanInvoiceQty(CustomerCreditMemoDetail customerCreditMemoDetail) {
        KualiDecimal invoiceOpenItemQty = customerCreditMemoDetail.getInvoiceOpenItemQuantity();
        KualiDecimal customerCreditMemoItemQty = new KualiDecimal(customerCreditMemoDetail.getCreditMemoItemQuantity());
        
        // customer credit memo quantity must not be greater than invoice open item quantity
        boolean validQuantity = (customerCreditMemoItemQty.compareTo(invoiceOpenItemQty) < 1?true:false);
        if (!validQuantity)
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_ITEM_QUANTITY, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_GREATER_THAN_INVOICE_ITEM_QUANTITY);
        
        return validQuantity; 
    }
    
    private KualiDecimal getInvoiceOpenItemQuantity(CustomerCreditMemoDetail customerCreditMemoDetail,CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal invoiceItemUnitPrice = customerInvoiceDetail.getInvoiceItemUnitPrice();
        KualiDecimal invoiceOpenItemAmount = customerCreditMemoDetail.getInvoiceOpenItemAmount();
        KualiDecimal invoiceOpenItemQuantity = invoiceOpenItemAmount.divide(invoiceItemUnitPrice);
        
        return invoiceOpenItemQuantity;
    }
    
    /**
     * @see org.kuali.module.ar.rule.RecalculateCustomerCreditMemoDocumentRule#processRecalculateCustomerCreditMemoDocumentRules(org.kuali.kfs.document.AccountingDocument)
     */
    public boolean processRecalculateCustomerCreditMemoDocumentRules(TransactionalDocument document, boolean printErrMsgFlag) {
        boolean success = true;
        boolean crmDataEnteredFlag = false;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)document;
        List<CustomerCreditMemoDetail> customerCreditMemoDetails = customerCreditMemoDocument.getCreditMemoDetails();
        int i = 0;
        String propertyName;
        
        for (CustomerCreditMemoDetail customerCreditMemoDetail:customerCreditMemoDetails) {
            propertyName = KFSConstants.CUSTOMER_CREDIT_MEMO_DETAIL_PROPERTY_NAME + "[" + i +"]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            
            // validate only if there is input data
            if (!isQtyOrItemAmountEntered(customerCreditMemoDetail).equals(StringUtils.EMPTY)) {
                crmDataEnteredFlag = true;
                success &= processRecalculateCustomerCreditMemoDetailRules(customerCreditMemoDocument,customerCreditMemoDetail);
            }
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
            i++;
        }
        
        success &= crmDataEnteredFlag;
        
        // print error message if 'Submit'/'Save'/'Blanket Approved' button is pressed and there is no CRM data entered
        if (!crmDataEnteredFlag && printErrMsgFlag)
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_NO_DATA_TO_SUBMIT);
        
        return success;
    }

    /**
     * @see org.kuali.module.ar.rule.ContinueCustomerCreditMemoDocumentRule#processContinueCustomerCreditMemoDocumentRules(org.kuali.kfs.document.AccountingDocument)
     */
    public boolean processContinueCustomerCreditMemoDocumentRules(TransactionalDocument document) {
        boolean success;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) document;
   
        success = checkIfInvoiceNumberIsValid(customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber());
        if (success)
            success = checkIfThereIsNoAnotherCRMInRouteForTheInvoice(customerCreditMemoDocument.getFinancialDocumentReferenceInvoiceNumber());
        
        return success;
    }
    
    private boolean checkIfInvoiceNumberIsValid(String invDocumentNumber) {
        boolean success = true;
        
        if (ObjectUtils.isNull(invDocumentNumber) || StringUtils.isBlank(invDocumentNumber)) {
            success = false;
            GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT__INVOICE_DOCUMENT_NUMBER_IS_REQUIRED);
        } else {    
            CustomerInvoiceDocumentService service = SpringContext.getBean(CustomerInvoiceDocumentService.class);
            CustomerInvoiceDocument customerInvoiceDocument = service.getInvoiceByInvoiceDocumentNumber(invDocumentNumber);
        
            if (ObjectUtils.isNull(customerInvoiceDocument)) {
                success = false;
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVALID_INVOICE_DOCUMENT_NUMBER);
            }
        }
        return success;
    }
    /**
     * 
     * This method checks if there is no another CRM in route for the invoice
     * not in route if CRM status is one of the following: processed, cancelled, or disapproved
     * @param invoice
     * @return
     */
    private boolean checkIfThereIsNoAnotherCRMInRouteForTheInvoice(String invoiceDocumentNumber) {

        KualiWorkflowDocument workflowDocument;
        boolean success = true;
        
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("financialDocumentReferenceInvoiceNumber", invoiceDocumentNumber);
        
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Collection<CustomerCreditMemoDocument> customerCreditMemoDocuments = 
            businessObjectService.findMatching(CustomerCreditMemoDocument.class, fieldValues);
        
        // no CRMs associated with the invoice are found
        if (customerCreditMemoDocuments.isEmpty())
            return success;
        
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        
        for(CustomerCreditMemoDocument customerCreditMemoDocument : customerCreditMemoDocuments) {
            try {
                workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(Long.valueOf(customerCreditMemoDocument.getDocumentNumber()), user);
            }
            catch (WorkflowException e) {
                throw new UnknownDocumentIdException("no document found for documentHeaderId '" + customerCreditMemoDocument.getDocumentNumber() + "'", e);
            }
            
            if (!(workflowDocument.stateIsProcessed() || workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerCreditMemoDocumentFields.CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER, ArConstants.ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_ONE_CRM_IN_ROUTE_PER_INVOICE);
                success = false;
                break;
            }
        }
        return success;  
    }
    
}
