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
package org.kuali.module.ar;

public class ArConstants {

    //System Parameters
    public static final String INSTITUTION_NAME = "INSTITUTION_NAME";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD = "GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD";
    public static final String MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE = "MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE";
    public static final String REMIT_TO_ADDRESS_EDITABLE_IND = "REMIT_TO_ADDRESS_EDITABLE_IND";
    public static final String REMIT_TO_NAME_EDITABLE_IND = "REMIT_TO_NAME_EDITABLE_IND";
    
    //constants for CashControlDocument
    public static final String ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH = "error.ar.ReferenceDocNumberCannotBeNullforPaymentMediumCash";
    public static final String ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH = "error.ar.ReferenceDocNumberMustBeValidforPaymentMediumCash";
    public static final String ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL = "error.ar.ReferenceDocNumberCannotBeNull";
    public static final String ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG = "error.ar.OrganizationOptionsMustBeSet";
    public static final String ERROR_PAYMENT_MEDIUM_CANNOT_BE_NULL = "error.ar.CustomerPaymentMediumCannotBeNull";
    public static final String ERROR_PAYMENT_MEDIUM_IS_NOT_VALID = "error.ar.CustomerPaymentMediumIsNotValid";
    public static final String ERROR_ALL_APPLICATION_DOCS_MUST_BE_APPROVED = "error.ar.AllApplicationDocumentsMustBeApproved";
    public static final String ERROR_DELETE_ADD_APP_DOCS_NOT_ALLOWED_AFTER_GLPES_GEN = "error.ar.DeleteAddApplicationDocNotAllowedAfterGLPEsGenerated";
    public static final String ERROR_NO_LINES_TO_PROCESS = "error.ar.NoLinesToProcess";
    public static final String ERROR_LINE_AMOUNT_CANNOT_BE_ZERO = "error.ar.LineAmountCannotBeZero";
    public static final String ERROR_LINE_AMOUNT_CANNOT_BE_NEGATIVE = "error.ar.LineAmountCannotBeNegative";
    public static final String ERROR_GLPES_NOT_CREATED = "error.ar.GLPEsNotCreated";
    public static final String CASH_CONTROL_TOTAL = "ar.CashControlTotal";

    public static final String CREATED_BY_CASH_CTRL_DOC = "message.ar.createdByCashControlDocument";
    public static final String DOCUMENT_DELETED_FROM_CASH_CTRL_DOC = "message.ar.documentDeletedFromCashControl";
    public static final String ELECTRONIC_PAYMENT_CLAIM = "message.ar.electronicPaymentClaim";

    //Valid number of days the invoice due date can be more than invoice creation date.
    public static final int VALID_NUMBER_OF_DAYS_INVOICE_DUE_DATE_PAST_INVOICE_DATE = 90;

    public static final String AR_SUPERVISOR_GROUP_NAME = "AR_ROLE_MAINTAINERS";

    public static final String NEW_CUSTOMER_INVOICE_DETAIL_ERROR_PATH_PREFIX = "newCustomerInvoiceDetail";
    public static final String NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX = "newCashControlDetail";

    public static final String CUSTOMER_INVOICE_DOCUMENT_GL_POSTING_HELPER_BEAN_ID = "customerInvoiceDocumentGeneralLedgerPostingHelper";

    public static final String CUSTOMER_INVOICE_DETAIL_UOM_DEFAULT = "EA"; //TODO: System parameter?

    //Customer Invoice Document errors:
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailTotalAmount";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailUnitPrice";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailQuantityPrice";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_INVALID_ITEM_CODE = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailItemCode";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_DISCOUNT_AMOUNT_GREATER_THAN_PARENT_AMOUNT = "error.document.customerInvoiceDocument.discountAmountGreaterThanParentAmount";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_CHART_OF_ACCOUNTS_CODE = "error.document.customerInvoiceDocument.invalidBilledByChartOfAccountsCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_ORGANIZATION_CODE = "error.document.customerInvoiceDocument.invalidBilledByOrganizationCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_MORE_THAN_X_DAYS = "error.document.customerInvoiceDocument.invalidInvoiceDueDateMoreThanXDays";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_BEFORE_OR_EQUAL_TO_BILLING_DATE = "error.document.customerInvoiceDocument.invalidInvoiceDueDateBeforeOrEqualBillingDate";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CUSTOMER_NUMBER = "error.document.customerInvoiceDocument.invalidCustomerNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_NO_CUSTOMER_INVOICE_DETAILS = "error.document.customerInvoiceDocument.noCustomerInvoiceDetails";


    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_PAYMENT_CHART_OF_ACCOUNTS_CODE_REQUIRED = "error.document.customerInvoiceDocument.paymentChartOfAccountsCodeRequired";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_ACCOUNT_NUMBER_REQUIRED = "error.document.customerInvoiceDocument.paymentAccountNumberRequired";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_OBJECT_CODE_REQUIRED = "error.document.customerInvoiceDocument.paymentPaymentProjectCodeRequired";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_CHART_OF_ACCOUNTS_CODE = "error.document.customerInvoiceDocument.invalidPaymentChartOfAccountsCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_ACCOUNT_NUMBER = "error.document.customerInvoiceDocument.invalidPaymentAccountNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_ACCOUNT_NUMBER = "error.document.customerInvoiceDocument.invalidPaymentSubAccountNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_OBJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_OBJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentSubObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_PROJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentProjectCode";

    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART = "1";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_SUBFUND = "2";
    public static final String GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU = "3";
    
    public static final String COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION = "ConfirmationForCopyingInvoiceWithDiscounts";


    public static final String CUSTOMER_INVOICE_DOCUMENT_INVOICE_ITEM_CODE_PROPERTY = "invoiceItemCode";
    
    
    public static final String CUSTOMER_INVOICE_DETAIL_DEFAULT_DISCOUNT_DESCRIPTION_PREFIX = "LINE ITEM DISCOUNT";
    
    public static class PaymentMediumCode {
        public static final String CASH = "CA";
        public static final String CHECK = "CK";
        public static final String WIRE_TRANSFER = "WT";
        public static final String CREDIT_CARD = "CR";
    }

    public static class CashControlDocumentFields {
        public static final String FINANCIAL_DOCUMENT_LINE_AMOUNT = "financialDocumentLineAmount";
        public static final String REFERENCE_FINANCIAL_DOC_NBR = "referenceFinancialDocumentNumber";
        public static final String APPLICATION_DOC_STATUS = "status";
        public static final String ORGANIZATION_DOC_NBR = "organizationDocumentNumber";
        public static final String CUSTOMER_PAYMENT_MEDIUM_CODE = "customerPaymentMediumCode";
    }

    public static class CustomerInvoiceDocumentFields {
        public static final String PAYMENT_CHART_OF_ACCOUNTS_CODE = "paymentChartOfAccountsCode";
        public static final String PAYMENT_FINANCIAL_OBJECT_CODE = "paymentFinancialObjectCode";
        public static final String PAYMENT_FINANCIAL_SUB_OBJECT_CODE = "paymentFinancialSubObjectCode";
        public static final String PAYMENT_ACCOUNT_NUMBER = "paymentAccountNumber";
        public static final String PAYMENT_SUB_ACCOUNT_NUMBER = "paymentSubAccountNumber";
        public static final String PAYMENT_PROJECT_CODE = "paymentProjectCode";

        public static final String PAYMENT_CHART_OF_ACCOUNTS = "paymentChartOfAccounts";
        public static final String PAYMENT_FINANCIAL_OBJECT = "paymentFinancialObject";
        public static final String PAYMENT_FINANCIAL_SUB_OBJECT = "paymentFinancialSubObject";
        public static final String PAYMENT_ACCOUNT = "paymentAccount";
        public static final String PAYMENT_SUB_ACCOUNT = "paymentSubAccount";
        public static final String PAYMENT_PROJECT = "paymentProject";

        public static final String CUSTOMER_INVOICE_DETAILS = "accountingLines";
        public static final String INVOICE_ITEM_CODE = "invoiceItemCode";

        public static final String CUSTOMER = "customer";
        public static final String CUSTOMER_NUMBER = "accountsReceivableDocumentHeader.customerNumber";

        public static final String INVOICE_DUE_DATE = "invoiceDueDate";
        public static final String BILLED_BY_ORGANIZATION = "billedByOrganization";
        public static final String BILLED_BY_ORGANIZATION_CODE = "billedByOrganizationCode";

        public static final String BILL_BY_CHART_OF_ACCOUNT = "billByChartOfAccount";
        public static final String BILL_BY_CHART_OF_ACCOUNT_CODE = "billByChartOfAccountCode";

        public static final String INVOICE_ITEM_UNIT_PRICE = "invoiceItemUnitPrice";
        public static final String INVOICE_ITEM_QUANTITY = "invoiceItemQuantity";
    }

    public static class OrganizationOptionsConstants {
        public static final String ORGANIZATION_CHECK_PAYABLE_TO_NAME = "organizationCheckPayableToName";
        public static final String ORGANIZATION_REMIT_TO_ADDRESS_NAME = "organizationRemitToAddressName";
        public static final String ORGANIZATION_REMIT_TO_LINE1_STREET_ADDRESS = "organizationRemitToLine1StreetAddress";
        public static final String ORGANIZATION_REMIT_TO_LINE2_STREET_ADDRESS = "organizationRemitToLine2StreetAddress";
        public static final String ORGANIZATION_REMIT_TO_CITY_NAME = "organizationRemitToCityName";
        public static final String ORGANIZATION_REMIT_TO_STATE_CODE = "organizationRemitToStateCode";
        public static final String ORGANIZATION_REMIT_TO_ZIP_CODE = "organizationRemitToZipCode";
    }
    
    public static class OrganizationAccountingOptionsConstants {
        public static final String SHOW_EDIT_PAYMENTS_DEFAULTS_TAB = GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU;
        public static final String NAME_OF_THE_TAB_TO_HIDE = "Edit Organization Receivable Account Defaults";
    }
    
    public static class CustomerCreditMemoStatuses {
        public static final String INITIATE = "INIT";
        public static final String IN_PROCESS = "INPR";
    }

    //TODO: this has to be removed when it will be set up as part of bootstrap data
    public static final String WIRE_ORG = "WIRE";
    public static final String ORGANIZATION_RECEIVABLE_ACCOUNT_DEFAULTS = "Organization Receivable Account Defaults";
    public static final String CUSTOMER_INVOICE_DOCUMENT_UNIT_OF_MEASURE_PROPERTY = "invoiceItemUnitOfMeasureCode";
    public static final String UNIT_OF_MEASURE_PROPERTY = "itemUnitOfMeasureCode";
    public static final String DISCOUNT_PREFIX = "DISCOUNT - ";

}
