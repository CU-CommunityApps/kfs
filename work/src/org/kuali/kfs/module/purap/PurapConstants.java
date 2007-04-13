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
package org.kuali.module.purap;

import java.util.HashMap;

/**
 * Holds constants for PURAP.
 * 
 */
public class PurapConstants {

    public static class Workgroups {
        public static final String WORKGROUP_ACCOUNTS_PAYABLE = "PURAP.WORKGROUP.ACCOUNTS_PAYABLE"; 
        public static final String WORKGROUP_PURCHASING = "PURAP.WORKGROUP.PURCHASING"; 
        public static final String WORKGROUP_TAXNBR_ACCESSIBLE = "PURAP.WORKGROUP.TAXNBR_ACCESSIBLE"; 
    }
    
    public static final String DOC_ADHOC_NODE_NAME = "Adhoc Routing";
    public static final String ASSIGN_CONTRACT_DOC_ERROR_COMPLETING_POST_PROCESSING = "Unable to save the contract manager for the following Requisitions: ";
    public static final String NOTE_TAB_WARNING = "noteWarning";
    public static final String STATUS_HISTORY_TAB_WARNING = "statusHistoryWarning";
    //Delivery Tag
    public static final String DELIVERY_BUILDING_OTHER = "Other";
    public static final String DELIVERY_BUILDING_OTHER_CODE = "OTH";
    
    //PDF Constants
    public static final String IMAGE_TEMP_PATH = "PURAP.IMAGE.TEMP.PATH";
    public static final String PDF_DIRECTORY = "PURAP.PDF.DIRECTORY";
    public static final String STATUS_INQUIRY_URL = "PURAP.STATUS.INQUIRY.URL";
    public static final String PURCHASING_DIRECTOR_IMAGE_PREFIX = "PURAP.PUR.DIRECTOR.IMAGE.PREFIX";
    public static final String PURCHASING_DIRECTOR_IMAGE_EXTENSION = "PURAP.PUR.DIRECTOR.IMAGE.EXTENSION";
    public static final String CONTRACT_MANAGER_IMAGE_PREFIX = "PURAP.CONTRACT.MGR.IMAGE.PREFIX";
    public static final String CONTRACT_MANAGER_IMAGE_EXTENSION = "PURAP.CONTRACT.MGR.IMAGE.EXTENSION";
    public static final String LOGO_IMAGE_PREFIX = "PURAP.PDF.LOGO.IMAGE.PREFIX";
    public static final String LOGO_IMAGE_EXTENSION = "PURAP.PDF.LOGO.IMAGE.EXTENSION";
    public static final String PDF_IMAGES_AVAILABLE_INDICATOR = "PURAP.PDF.IMAGES.AVAILABLE.INDICATOR";
    
    public static class RequisitionStatuses {
        public static String IN_PROCESS = "INPR";
        public static String CANCELLED = "CANC";
        public static String CLOSED = "CLOS";
        public static String AWAIT_CONTENT_APRVL = "ACNT";
        public static String AWAIT_SUB_ACCT_APRVL = "ASUB";
        public static String AWAIT_FISCAL_APRVL = "AFIS";
        public static String AWAIT_CHART_APRVL = "ACHA";
        public static String AWAIT_SEP_OF_DUTY_APRVL = "ASOD";
        public static String DAPRVD_CONTENT = "DCNT";
        public static String DAPRVD_SUB_ACCT = "DSUB";
        public static String DAPRVD_FISCAL = "DFIS";
        public static String DAPRVD_CHART = "DCHA";
        public static String DAPRVD_SEP_OF_DUTY = "DSOD";
        public static String AWAIT_CONTRACT_MANAGER_ASSGN = "ACMR";
        public static String CONTRACT_MANAGER_ASSGN = "CMRA";
    }

    public static class POCostSources {
        public static String ESTIMATE = "EST";
    }
    
    public static class POTransmissionMethods {
        public static String FAX = "FAX";
        public static String PRINT = "PRIN";
        public static String NOPRINT = "NOPR";
        public static String ELECTRONIC = "ELEC";
    }
    
    public static int REQ_B2B_ALLOW_COPY_DAYS = 5;
    
    public static class RequisitionSources {
        public static String STANDARD_ORDER = "STAN";
        public static String B2B = "B2B";
    }

    // Requisition Tab Errors
    public static final String DELIVERY_TAB_ERRORS = "document.delivery*";
    public static final String VENDOR_ERRORS = "document.vendor*";
    public static final String ADDITIONAL_TAB_ERRORS = "document.requestor*,document.purchaseOrderTransmissionMethodCode,document.chartOfAccountsCode,document.organizationCode,document.purchaseOrderCostSourceCode,document.purchaseOrderTotalLimit";
    
    // Assign Contract Manager Tab Errors
    public static final String ASSIGN_CONTRACT_MANAGER_TAB_ERRORS = "document.unassignedRequisition*";

    public static class PurchaseOrderStatuses {
        public static String IN_PROCESS = "INPR";
        public static String WAITING_FOR_VENDOR = "WVEN";
        public static String WAITING_FOR_DEPARTMENT = "WDPT";
        public static String OPEN = "OPEN";
        public static String CLOSED = "CLOS";
        public static String CANCELLED = "CANC";
        public static String PAYMENT_HOLD = "PHOL";
        public static String AWAIT_TAX_APRVL = "WTAX";
        public static String AWAIT_BUDGET_APRVL = "WBUD";
        public static String AWAIT_CONTRACTS_GRANTS_APRVL = "WCG";
        public static String AWAIT_PURCHASING_APRVL = "WPUR";
        public static String AWAIT_SPECIAL_APRVL = "WSPC";
        public static String DAPRVD_TAX = "DTAX";
        public static String DAPRVD_BUDGET = "DBUD";
        public static String DAPRVD_CONTRACTS_GRANTS = "DCG";
        public static String DAPRVD_PURCHASING = "DPUR";
        public static String DAPRVD_SPECIAL = "DSPC";
        public static String CXML_ERROR = "CXER";
        public static String PENDING_CXML = "CXPE";
        public static String PENDING_FAX = "FXPE";
        public static String PENDING_PRINT = "PRPE";
        public static String QUOTE = "QUOT";
        public static String VOID = "VOID";
        public static String AMENDMENT = "AMND";
    }
    
	
    public static class ItemTypeCodes {
        // ITEM TYPES
        public static String ITEM_TYPE_ITEM_CODE = "ITEM";
        public static String ITEM_TYPE_FREIGHT_CODE = "FRHT";
        public static String ITEM_TYPE_SHIP_AND_HAND_CODE = "SPHD";
        public static String ITEM_TYPE_TRADE_IN_CODE = "TRDI";
        public static String ITEM_TYPE_ORDER_DISCOUNT_CODE = "ORDS";        
        public static String ITEM_TYPE_SERVICE_CODE = "SRVC";
    }
    
    //Item constants
    public static int DOLLAR_AMOUNT_MIN_SCALE = 2;
    public static int UNIT_PRICE_MAX_SCALE = 4;
    
    public static class PurchaseOrderDocTypes {
        public static String PURCHASE_ORDER_REOPEN_DOCUMENT  = "KualiPurchaseOrderReopenDocument";
        public static String PURCHASE_ORDER_CLOSE_DOCUMENT  = "KualiPurchaseOrderCloseDocument";
        public static String PURCHASE_ORDER_DOCUMENT  = "KualiPurchaseOrderDocument";
        public static String PURCHASE_ORDER_RETRANSMIT_DOCUMENT  = "KualiPurchaseOrderRetransmitDocument";
        public static String PURCHASE_ORDER_PRINT_DOCUMENT  = "KualiPurchaseOrderPrintDocument";
        public static String PURCHASE_ORDER_VOID_DOCUMENT  = "KualiPurchaseOrderVoidDocument";
        public static String PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT  = "KualiPurchaseOrderPaymentHoldDocument";
    }

    private static HashMap<String, String> purchaseOrderDocTypes()
    {
        HashMap<String,String> mapSLF; 
        mapSLF =  new HashMap<String,String>();
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, "purchaseOrderPostProcessorCloseService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, "purchaseOrderPostProcessorReopenService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT, "purchaseOrderPostProcessorVoidService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT, "purchaseOrderPostProcessorPrintService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, "purchaseOrderPostProcessorRetransmitService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT, "purchaseOrderPostProcessorPaymentHoldService");
        mapSLF.put(PurchaseOrderDocTypes.PURCHASE_ORDER_DOCUMENT, "purchaseOrderPostProcessorService");
        return mapSLF;
    }
    public final static HashMap<String,String> PURCHASE_ORDER_DOC_TYPE_MAP =
                        purchaseOrderDocTypes();
    
    public static class PODocumentsStrings {
        public static String CLOSE_QUESTION = "POClose";
        public static String CLOSE_CONFIRM = "POCloseConfirm";
        public static String CLOSE_NOTE_PREFIX = "Note entered while closing a Purchase Order : ";
        
        public static String REOPEN_PO_QUESTION = "ReopenPO";
        public static String CONFIRM_REOPEN_QUESTION = "ConfirmReopen"; 
        public static String REOPEN_NOTE_PREFIX = "Note entered while reopening a Purchase Order : ";
        
        public static String VOID_QUESTION = "POVoid";
        public static String VOID_CONFIRM = "POVoidConfirm";
        public static String VOID_NOTE_PREFIX = "Note entered while voiding a Purchase Order : ";
        
        public static String PAYMENT_HOLD_QUESTION = "POPaymentHold";
        public static String PAYMENT_HOLD_CONFIRM = "POPaymentHoldConfirm";
        public static String PAYMENT_HOLD_NOTE_PREFIX = "Note entered while putting a Purchase Order in Payment Hold status :";
        
        public static String SINGLE_CONFIRMATION_QUESTION = "singleConfirmationQuestion";
    }
    
    public static class PaymentRequestStatuses {
        public static String INITIATE = "INIT";
        public static String IN_PROCESS = "INPR";
        public static String CANCELLED_POST_APPROVE = "CANC";
        public static String CANCELLED_IN_PROCESS = "VOID";
       /* Modify as required:
        public static String CANCELLED = "CANC";
        public static String CLOSED = "CLOS";
        public static String AWAIT_CONTENT_APRVL = "ACNT";
        public static String AWAIT_SUB_ACCT_APRVL = "ASUB";
        public static String AWAIT_FISCAL_APRVL = "AFIS";
        public static String AWAIT_CHART_APRVL = "ACHA";
        public static String AWAIT_SEP_OF_DUTY_APRVL = "ASOD";
        public static String DAPRVD_CONTENT = "DCNT";
        public static String DAPRVD_SUB_ACCT = "DSUB";
        public static String DAPRVD_FISCAL = "DFIS";
        public static String DAPRVD_CHART = "DCHA";
        public static String DAPRVD_SEP_OF_DUTY = "DSOD";
        public static String AWAIT_CONTRACT_MANAGER_ASSGN = "ACMR";
        public static String CONTRACT_MANAGER_ASSGN = "CMRA";
        */
/*        
//      PAYMENT REQUEST STATUSES 
        public static String PREQ_STAT_IN_PROCESS = "INPR";   // In process (not routed yet)
        public static String PREQ_STAT_CANCELLED_POST_APPROVE = "CANC";
        public static String PREQ_STAT_CANCELLED_IN_PROCESS = "VOID";
        public static String PREQ_STAT_AP_APPROVED = "APAD";
        public static String PREQ_STAT_AUTO_APPROVED = "AUTO";
        public static String PREQ_STAT_DEPARTMENT_APPROVED = "DPTA";
        public static String PREQ_STAT_AWAIT_SUB_ACCOUNT_APRVL = "ASAA";   // Waiting for Sub Acct Manager approval
        public static String PREQ_STAT_AWAIT_FISCAL_OFFICER_APRVL = "AFOA";   // Waiting for Fiscal Officer approval
        public static String PREQ_STAT_AWAIT_CHART_APRVL = "ACHA";   // Waiting for Chart/Org approval
        public static String PREQ_STAT_AWAIT_TAX_APRVL = "ATAX";   // Waiting for Tax approval
        public static String PREQ_STAT_PENDING_E_INVOICE = "PEIN";
        
        // PAYMENT REQUEST STATUSES TO BE AUTO APPROVED
        public static String[] PREQ_STATUSES_FOR_AUTO_APPROVE = {PREQ_STAT_AWAIT_SUB_ACCOUNT_APRVL,PREQ_STAT_AWAIT_FISCAL_OFFICER_APRVL,PREQ_STAT_AWAIT_CHART_APRVL};
        
        // PAYMENT REQUEST PAY DATE CALCULATION DAYS
        public static int PREQ_PAY_DATE_CALCULATION_DAYS = 28;
        
        // PREQ CANCEL FORWARDS
        public static String PREQ_CANCEL_FORWARD_DOC_HANDLER = "dochandler";
        public static String PREQ_CANCEL_FORWARD_TAB_PAGE = "editpreq";
     */   
    }

    public static class PREQDocumentsStrings {
        public static String DUPLICATE_DATE_AMONT_QUESTION = "PREQDuplicateDateAmount";
        
}
}