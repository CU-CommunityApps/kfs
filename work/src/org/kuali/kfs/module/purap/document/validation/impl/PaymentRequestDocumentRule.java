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
package org.kuali.module.purap.rules;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;


public class PaymentRequestDocumentRule extends AccountsPayableDocumentRuleBase {

/**
     * Tabs included on Payment Request Documents are:
     *   Invoice
     * 
     * @see org.kuali.module.purap.rules.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        valid &= processInvoiceValidation((PaymentRequestDocument)purapDocument);
        valid &= processPurchaseOrderIDValidation((PaymentRequestDocument)purapDocument);
        valid &= processPaymentRequestDateValidation((PaymentRequestDocument)purapDocument);
        return valid;
    }

//  TODO should we call our validation here?
//  @Override
//  protected boolean processCustomSaveDocumentBusinessRules(Document document) {
//      boolean isValid = true;
//      PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) document;
//      return isValid &= processValidation(purapDocument);
//  }

   
          @Override
          protected boolean processCustomSaveDocumentBusinessRules(Document document) {
              boolean isValid = true;
              PaymentRequestDocument  paymentRequestDocument = (PaymentRequestDocument) document;
              return isValid &= processValidation(paymentRequestDocument);
          }

      
/**
     * This method performs any validation for the Invoice tab.
     * 
     * @param preqDocument
     * @return
     */
    public boolean processInvoiceValidation(PaymentRequestDocument preqDocument) {
        boolean valid = true;
        //TODO code validation here
        return valid;
    }
    
    boolean processPurchaseOrderIDValidation(PaymentRequestDocument document) {
       
        boolean valid = true;
       
        Integer POID = document.getPurchaseOrderIdentifier();
       
       // I think only the current PO can have the pending action indicator to be "Y". For all the other POs with the same PO number the pending indicator 
       // should be always "N". So I think we only need to check if for the current PO the Pending indicator is "Y" and it is not a Retransmit doc, then 
       //we don't allow users to create a PREQ. Correct? 
      //given a PO number the use enters in the Init screen, for the rule "Error if the PO is not open" we also only need to check this rule against the current PO, Correct? 
       PurchaseOrderDocument purchaseOrderDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(document.getPurchaseOrderIdentifier());
       if (ObjectUtils.isNull(purchaseOrderDocument)) {
            //GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_BEGIN_DATE, PurapKeyConstants.ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE);
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            valid &= false;
       } else if (!StringUtils.equals(purchaseOrderDocument.getStatusCode(),PurapConstants.PurchaseOrderStatuses.OPEN)){
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_OPEN);
            valid &= false;
            // if the PO is pending and it is not a Retransmit, we cannot generate a Payment Request for it:
      // } else if (purchaseOrderDocument.isPendingActionIndicator() & !StringUtils.equals(purchaseOrderDocument.getDocumentHeader().getWorkflowDocument().getDocumentType(), PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)){
      //      GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
       //     valid &= false;
       }
       
         
        return valid;
    }
    
    boolean processPaymentRequestDateValidation(PaymentRequestDocument document){
        
        boolean valid = true;
        //valid &= isInvoiceDateAfterToday(document.getInvoiceDate());
        if (isInvoiceDateAfterToday(document.getInvoiceDate())) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.INVOICE_DATE, PurapKeyConstants.ERROR_INVALID_INVOICE_DATE);
                 valid &= false;
        }
        return valid;
    }
    // This moved to PaymentRequestService to be used from action logic for Continue  Button:
    
    boolean processPaymentRequestDuplicateValidation(PaymentRequestDocument document){  
        
        boolean valid = true;
        //  check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
        Integer POID = document.getPurchaseOrderIdentifier();
        List<PaymentRequestDocument> preqs = SpringServiceLocator.getPaymentRequestService().getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(POID, document.getVendorInvoiceAmount(), document.getInvoiceDate());
        
        if (preqs.size() > 0) {
         // boolean addedError = false;
          List cancelled = new ArrayList();
          List voided = new ArrayList();
          for (Iterator iter = preqs.iterator(); iter.hasNext();) {
            PaymentRequestDocument testPREQ = (PaymentRequestDocument) iter.next();
            if ( (!(PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getStatusCode()))) && 
                 (!(PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getStatusCode()))) ) {
                 GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT);
              //addedError = true;s
                valid &= false;
              break;
            } else if (PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getStatusCode())) {
              voided.add(testPREQ);
            } else if (PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getStatusCode())) {
              cancelled.add(testPREQ);
            }
          }
          // custom error message for duplicates related to cancelled/voided PREQs
         //if (!addedError) {
          if (valid) {
            if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
              //messages.add("errors.duplicate.invoice.date.amount.cancelledOrVoided");
              GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLEDORVOIDED);
              valid &= false;
            } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
              //messages.add("errors.duplicate.invoice.date.amount.voided");
              GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_VOIDED);
              valid &= false;
            } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
              //messages.add("errors.duplicate.invoice.date.amount.cancelled");
              GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLED);
              valid &= false;
            }
          }
        }
        return valid;
    } 

    
    public boolean isInvoiceDateAfterToday(Date invoiceDate) {
        // Check invoice date to make sure it is today or before
        DateTimeService dateTimeService = SpringServiceLocator.getDateTimeService();
        
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 11);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 59);
        Timestamp nowTime = new Timestamp(now.getTimeInMillis());
        Calendar invoiceDateC = Calendar.getInstance();
        invoiceDateC.setTime(invoiceDate);
        // set time to midnight
        invoiceDateC.set(Calendar.HOUR, 0);
        invoiceDateC.set(Calendar.MINUTE, 0);
        invoiceDateC.set(Calendar.SECOND, 0);
        invoiceDateC.set(Calendar.MILLISECOND, 0);
        Timestamp invoiceDateTime = new Timestamp(invoiceDateC.getTimeInMillis());
        return ( (invoiceDateTime.compareTo(nowTime)) > 0 );
      }
    
    // This has been moved to PaymentRequestService because we needed the return type of HashMap

    public HashMap<String, String> paymentRequestDuplicateMessages(PaymentRequestDocument document){
        HashMap<String,String> msgs; 
        msgs =  new HashMap<String,String>();
        
//      check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
        Integer POID = document.getPurchaseOrderIdentifier();
        List<PaymentRequestDocument> preqs = SpringServiceLocator.getPaymentRequestService().getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(POID, document.getVendorInvoiceAmount(), document.getInvoiceDate());
        
        if (preqs.size() > 0) {
          boolean addedMessage = false;
          List cancelled = new ArrayList();
          List voided = new ArrayList();
          for (Iterator iter = preqs.iterator(); iter.hasNext();) {
            PaymentRequestDocument testPREQ = (PaymentRequestDocument) iter.next();
            if ( (!(PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getStatusCode()))) && 
                 (!(PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getStatusCode()))) ) {
                 msgs.put(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT);
                 addedMessage = true;
              
              break;
            } else if (PurapConstants.PaymentRequestStatuses.CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getStatusCode())) {
              voided.add(testPREQ);
            } else if (PurapConstants.PaymentRequestStatuses.CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getStatusCode())) {
              cancelled.add(testPREQ);
            }
          }
          // custom error message for duplicates related to cancelled/voided PREQs
         if (!addedMessage) {
          //if (valid) {
            if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
              //messages.add("errors.duplicate.invoice.date.amount.cancelledOrVoided");
                msgs.put(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLEDORVOIDED);
              
            } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
              //messages.add("errors.duplicate.invoice.date.amount.voided");
                msgs.put(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_VOIDED);
                addedMessage = true;
              //valid &= false;
            } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
              //messages.add("errors.duplicate.invoice.date.amount.cancelled");
                msgs.put(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.MESSAGE_DUPLICATE_INVOICE_DATE_AMOUNT_CANCELLED);
                addedMessage = true;
            }
          }
        }
         
        return msgs;
    }
    
    /**
     * @param purchaseOrderId
     * @param invoiceNumber
     * @param invoiceAmount
     * @param invoiceDate
     * @param expiredAccounts
     * @param closedAccounts
     * @return
     */
   /*
    public PaymentRequestInitializationValidationErrors verifyPreqInitialization(
        Integer purchaseOrderId, String invoiceNumber, BigDecimal invoiceAmount, Timestamp invoiceDate,
        List expiredAccounts, List closedAccounts, User u) {
      SERVICELOG.debug("verifyPreqInitialization() started");
        LOG.debug("verifyPreqInitialization started");
      List messages = new ArrayList();
      List expirAcctList = new ArrayList();
      List closeAcctList = new ArrayList();
      
      PaymentRequestInitializationValidationErrors initValidationErrors = new PaymentRequestInitializationValidationErrors();

      PurchaseOrder po = purchaseOrderService.getPurchaseOrderById(purchaseOrderId,u);
      
      if (po == null) {
        // no PO was found in the system - notify the user
        messages.add("errors.po.not.exist");
        initValidationErrors.errorMessages = messages;
        SERVICELOG.debug("verifyPreqInitialization() ended");    
        return initValidationErrors;
      }
      
      // Verify that there exists at least 1 item left to be invoiced
      initValidationErrors.setPurchaseOrderNumberToUse(purchaseOrderId);
      boolean zeroDollar = true;
      for (Iterator itemIter = po.getItems().iterator(); itemIter.hasNext();) {
        PurchaseOrderItem poi = (PurchaseOrderItem) itemIter.next();
        BigDecimal encumberedQuantity = poi.getOutstandingEncumberedQuantity() == null ? zero : poi.getOutstandingEncumberedQuantity();
        if (encumberedQuantity.compareTo(zero) == 1) {
          zeroDollar = false;
          break;
        }
      }
      
      // if messages exist now there is no need to check anything else
      if (!messages.isEmpty()) {
        initValidationErrors.errorMessages = messages;
        SERVICELOG.debug("verifyPreqInitialization() ended");
        return initValidationErrors;
      }
      
      // Check invoice date to make sure it is today or before
      if (this.isInvoiceDateAfterToday(invoiceDate)) {
        messages.add("errors.invalid.invoice.date");
      }
//      Timestamp now = new Timestamp( (new Date()).getTime() );
//      if ( invoiceDate.getTime() > now.getTime() ) {
//        messages.add("errors.invalid.invoice.date");
//      }
      
      if (EpicConstants.PO_STAT_OPEN.equals(po.getPurchaseOrderStatus().getCode())) {
        //then check that there are no other non-cancelled PREQs for this vendor number and invoice number
        Integer vendorDetailAssignedId = po.getVendorDetailAssignedId();
        Integer vendorHeaderGeneratedId = po.getVendorHeaderGeneratedId();

        List preqs = getPaymentRequestsByVendorNumberInvoiceNumber(vendorHeaderGeneratedId,vendorDetailAssignedId,invoiceNumber);
        if (preqs.size() > 0) {
          boolean addedError = false;
          List cancelled = new ArrayList();
          List voided = new ArrayList();
          for (Iterator iter = preqs.iterator(); iter.hasNext();) {
            PaymentRequest testPREQ = (PaymentRequest) iter.next();
            if ( (!(EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode()))) && 
                 (!(EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode()))) ) {
              messages.add("errors.duplicate.vendor.invoice");
              addedError = true;
              break;
            } else if (EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode())) {
              voided.add(testPREQ);
            } else if (EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode())) {
              cancelled.add(testPREQ);
            }
          }
          // custom error message for duplicates related to cancelled/voided PREQs
          if (!addedError) {
            if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
              messages.add("errors.duplicate.vendor.invoice.cancelledOrVoided");
            } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
              messages.add("errors.duplicate.vendor.invoice.voided");
            } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
              messages.add("errors.duplicate.vendor.invoice.cancelled");
            }
          }
        }
        
        //check that the invoice date and invoice total amount entered are not on any existing non-cancelled PREQs for this PO
        preqs = getPaymentRequestsByPOIdInvoiceAmountInvoiceDate(po.getId(), invoiceAmount, invoiceDate);
        if (preqs.size() > 0) {
          boolean addedError = false;
          List cancelled = new ArrayList();
          List voided = new ArrayList();
          for (Iterator iter = preqs.iterator(); iter.hasNext();) {
            PaymentRequest testPREQ = (PaymentRequest) iter.next();
            if ( (!(EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode()))) && 
                 (!(EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode()))) ) {
              messages.add("errors.duplicate.invoice.date.amount");
              addedError = true;
              break;
            } else if (EpicConstants.PREQ_STAT_CANCELLED_IN_PROCESS.equals(testPREQ.getStatus().getCode())) {
              voided.add(testPREQ);
            } else if (EpicConstants.PREQ_STAT_CANCELLED_POST_APPROVE.equals(testPREQ.getStatus().getCode())) {
              cancelled.add(testPREQ);
            }
          }
          // custom error message for duplicates related to cancelled/voided PREQs
          if (!addedError) {
            if ( (!(voided.isEmpty())) && (!(cancelled.isEmpty())) ) {
              messages.add("errors.duplicate.invoice.date.amount.cancelledOrVoided");
            } else if ( (!(voided.isEmpty())) && (cancelled.isEmpty()) ) {
              messages.add("errors.duplicate.invoice.date.amount.voided");
            } else if ( (voided.isEmpty()) && (!(cancelled.isEmpty())) ) {
              messages.add("errors.duplicate.invoice.date.amount.cancelled");
            }
          }
        }
        
        this.checkForExpiredOrClosedAccounts(po, initValidationErrors, closedAccounts, expiredAccounts);
        
      } else if (EpicConstants.PO_STAT_PAYMENT_HOLD.equals(po.getPurchaseOrderStatus().getCode())) {
        //PO is not open - notify the user
        messages.add("errors.po.status.hold");
      } else {
        //PO is not open - notify the user
        messages.add("errors.po.not.open");
      }

      if ( 1 == 2 ) {
        // TODO 2006: delyea PREQ CLOSE PO: Add code to return encumberances check for auto close of PO
        initValidationErrors.setCanAutoClosePO(false);
        //initValidationErrors.setCanAutoClosePO(generalLedgerService.isPOAutoCloseEligible(prd.getPreq()) && (prd.getPreq().getPurchaseOrder().getRecurringPaymentType() == null));
      }

      initValidationErrors.errorMessages = messages;
      SERVICELOG.debug("verifyPreqInitialization() ended");    
      return initValidationErrors;
    }
    */
}
