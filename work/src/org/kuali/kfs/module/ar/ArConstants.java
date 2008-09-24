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
package org.kuali.kfs.module.ar;

public class ArConstants {

    //System Parameters
    public static final String INSTITUTION_NAME = "INSTITUTION_NAME";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD = "GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD";
    public static final String GLPE_WRITEOFF_GENERATION_METHOD = "GLPE_WRITEOFF_GENERATION_METHOD";
    public static final String MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE = "MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE";
    public static final String REMIT_TO_ADDRESS_EDITABLE_IND = "REMIT_TO_ADDRESS_EDITABLE_IND";
    public static final String REMIT_TO_NAME_EDITABLE_IND = "REMIT_TO_NAME_EDITABLE_IND";
    public static final String GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND = "GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND";
    public static final String INVOICE_RECURRENCE_INTERVALS = "INVOICE_RECURRENCE_INTERVALS";
    public static final String MAXIMUM_RECURRENCES_BY_INTERVAL = "MAXIMUM_RECURRENCES_BY_INTERVAL";
    public static final String ENABLE_SALES_TAX_IND = "ENABLE_SALES_TAX_IND";
    public static final String CUSTOMER_INVOICE_AGE = "CUSTOMER_INVOICE_AGE";

    //Valid number of days the invoice due date can be more than invoice creation date.
    public static final int VALID_NUMBER_OF_DAYS_INVOICE_DUE_DATE_PAST_INVOICE_DATE = 90;

    public static final String AR_SUPERVISOR_GROUP_NAME = "AR_ROLE_MAINTAINERS";

    public static final String NEW_CUSTOMER_INVOICE_DETAIL_ERROR_PATH_PREFIX = "newCustomerInvoiceDetail";
    public static final String NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX = "newCashControlDetail";

    public static final String CUSTOMER_INVOICE_DOCUMENT_GL_POSTING_HELPER_BEAN_ID = "customerInvoiceDocumentGeneralLedgerPostingHelper";

    public static final String CUSTOMER_INVOICE_DETAIL_UOM_DEFAULT = "EA"; //TODO: System parameter?
    
    public static final String LOOKUP_CUSTOMER_NAME = "customerName";
    public static final String LOOKUP_CUSTOMER_NUMBER = "customerNumber";
    public static final String LOOKUP_INVOICE_NUMBER = "invoiceNumber";
    
    // PaymentApplicationDocument errors:
    public static final String ERROR_PAYMENT_APPLICATION_DOCUMNET_AMOUNT_TO_BE_APPLIED_MUST_BE_NUMERIC = "error.ar.paymentApplication.amountToBeAppliedMustBeNumeric";
    public static final String ERROR_PAYMENT_APPLICATION_DOCUMNET_AMOUNT_TO_BE_APPLIED_MUST_BE_POSITIVE = "error.ar.paymentApplication.amountToBeAppliedMustBePositive";
    public static final String ERROR_PAYMENT_APPLICATION_DOCUMNET_AMOUNT_TO_BE_APPLIED_MUST_BE_LESS_EQUAL_TO_DETAIL_AMOUNT = "error.ar.paymentApplication.amountToBeAppliedMustBeLesOrEqualWithTheDetailAmount";

    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART = "1";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_SUBFUND = "2";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU = "3";
    public static final String GLPE_WRITEOFF_GENERATION_METHOD_CHART = "1";
    public static final String GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT = "2";
    public static final String COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION = "ConfirmationForCopyingInvoiceWithDiscounts";

    public static final String CUSTOMER_INVOICE_DETAIL_DEFAULT_DISCOUNT_DESCRIPTION_PREFIX = "LINE ITEM DISCOUNT";

    public static class PaymentMediumCode {
        public static final String CASH = "CA";
        public static final String CHECK = "CK";
        public static final String WIRE_TRANSFER = "WT";
        public static final String CREDIT_CARD = "CR";
    }
    
    public static class CustomerAgingReportFields {        
        // Report Options
        public static final String PROCESSING_ORG = "Processing Organization";
        public static final String BILLING_ORG = "Billing Organization";
        public static final String ACCT = "Account";
    }    

    public static class OrganizationAccountingOptionsConstants {
        public static final String SHOW_EDIT_PAYMENTS_DEFAULTS_TAB = GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU;
        public static final String ORG_ACCT_DEFAULT_RECEIVABLE_TAB_NAME = "Edit Organization Receivable Account Defaults";
        public static final String ORG_ACCT_DEFAULT_WRITEOFF_TAB_NAME = "Edit Organization Writeoff Account Defaults";
    }

    public static class CustomerCreditMemoStatuses {
        public static final String INITIATE = "INIT";
        public static final String IN_PROCESS = "INPR";
    }

    public static class CustomerInvoiceWriteoffStatuses {
        public static final String INITIATE = "INIT";
        public static final String IN_PROCESS = "INPR";
    }    

    public static class CustomerCreditMemoConstants {
        public static final String CUSTOMER_CREDIT_MEMO_ITEM_QUANTITY = "qty";
        public static final String CUSTOMER_CREDIT_MEMO_ITEM_TOTAL_AMOUNT = "itemAmount";
        public static final String BOTH_QUANTITY_AND_ITEM_TOTAL_AMOUNT_ENTERED = "both";
        public static final String GENERATE_CUSTOMER_CREDIT_MEMO_DOCUMENT_QUESTION_ID = "GenerateCustomerCreditMemoDocumentQuestionID";
    }

    public static final String ORGANIZATION_RECEIVABLE_ACCOUNT_DEFAULTS = "Organization Receivable Account Defaults";
    public static final String DISCOUNT_PREFIX = "DISCOUNT - ";
    public static final String GLPE_WRITEOFF_OBJECT_CODE_BY_CHART = "GLPE_WRITEOFF_OBJECT_CODE_BY_CHART";
    public static final String NO_COLLECTION_STATUS_STRING = "No Collection Status";
    public static final Object CUSTOMER_INVOICE_WRITEOFF_SUMMARY_ACTION = "viewSummary";
    public static final String CUSTOMER_INVOICE_WRITEOFF_DOCUMENT_DESCRIPTION = "Writeoff for ";


    public static class Workgroups {
        public static final String WORKGROUP_AR_SUPER_USERS = "AR_SUPER_USERS";
    }

    public static final String CREATE_TAX_REGION_FROM_LOOKUP_PARM = "createTaxRegionFromLookup";

}
