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

/**
 * Error Key Constants for KFS-AR.
 */
public class ArKeyConstants {

    //Customer Invoice Document errors:
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_TOTAL_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailTotalAmount";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_UNIT_PRICE_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailUnitPrice";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailQuantityPrice";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_INVALID_ITEM_CODE = "error.document.customerInvoiceDocument.invalidCustomerInvoiceDetailItemCode";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_DISCOUNT_AMOUNT_GREATER_THAN_PARENT_AMOUNT = "error.document.customerInvoiceDocument.discountAmountGreaterThanParentAmount";
    public static final String ERROR_CUSTOMER_INVOICE_DETAIL_SYSTEM_INFORMATION_DISCOUNT_DOES_NOT_EXIST = "error.document.customerInvoiceDocument.systemInformationDiscountDoesNotExist";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_CHART_OF_ACCOUNTS_CODE = "error.document.customerInvoiceDocument.invalidBilledByChartOfAccountsCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLED_BY_ORGANIZATION_CODE = "error.document.customerInvoiceDocument.invalidBilledByOrganizationCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_MORE_THAN_X_DAYS = "error.document.customerInvoiceDocument.invalidInvoiceDueDateMoreThanXDays";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_INVOICE_DUE_DATE_BEFORE_OR_EQUAL_TO_BILLING_DATE = "error.document.customerInvoiceDocument.invalidInvoiceDueDateBeforeOrEqualBillingDate";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CUSTOMER_NUMBER = "error.document.customerInvoiceDocument.invalidCustomerNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_NO_CUSTOMER_INVOICE_DETAILS = "error.document.customerInvoiceDocument.noCustomerInvoiceDetails";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILLING_PROCESSING_ORGANIZATION_IN_ORG_OPTIONS = "error.document.customerInvoiceDocument.invalidBilingProcessingOrgInOrgOptions";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_CHART_OF_ACCOUNTS_CODE = "error.document.customerInvoiceDocument.invalidPaymentChartOfAccountsCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_ACCOUNT_NUMBER = "error.document.customerInvoiceDocument.invalidPaymentAccountNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_ACCOUNT_NUMBER = "error.document.customerInvoiceDocument.invalidPaymentSubAccountNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_OBJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_OBJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentSubObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_PROJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentProjectCode";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SHIP_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.invalidShipToAddressIdentifier";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILL_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.invalidBillToAddressIdentifier";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INACTIVE_SHIP_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.inactiveShipToAddressIdentifier";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INACTIVE_BILL_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.inactiveBillToAddressIdentifier";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CHART_WITH_NO_AR_OBJ_CD = "error.document.customerInvoiceDocument.invalidChartWithNoARObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_WITH_NO_AR_OBJ_CD = "error.document.customerInvoiceDocument.invalidSubFundWithNoARObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_AR_OBJ_CD_IN_PARM = "error.document.customerInvoiceDocument.invalidSubFundARObjectCodeInParm";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_UNIT_OF_MEASURE_CD = "error.document.customerInvoiceDocument.invalidUnitOfMeasureCode";
    
    // Invoice Recurrence errors/warnings:
    public static final String ERROR_RECURRING_INVOICE_NUMBER_MUST_BE_APPROVED = "error.document.invoiceRecurrenceMaintenance.invoiceMustBeApproved";
    public static final String ERROR_MAINTENANCE_DOCUMENT_ALREADY_EXISTS = "error.document.invoiceRecurrenceMaintenance.maintenanceDocumentsExists";
    public static final String ERROR_INVOICE_RECURRENCE_BEGIN_DATE_EARLIER_THAN_TODAY = "error.document.invoiceRecurrenceMaintenance.beginDateMustBeEarlierThanToday";
    public static final String ERROR_INVOICE_DOES_NOT_EXIST = "error.document.invoiceRecurrenceMaintenance.invoiceDoesNotExist";
    public static final String ERROR_END_DATE_EARLIER_THAN_BEGIN_DATE = "error.document.invoiceRecurrenceMaintenance.endDateEarlierThanBeginDate";
    public static final String ERROR_END_DATE_OR_TOTAL_NUMBER_OF_RECURRENCES = "error.document.invoiceRecurrenceMaintenance.enterEndDateOrTotalNumberOfRecurrences";
    public static final String ERROR_TOTAL_NUMBER_OF_RECURRENCES_GREATER_THAN_ALLOWED = "error.document.invoiceRecurrenceMaintenance.totalRecurrencesMoreThanAllowed";
    public static final String ERROR_END_DATE_AND_TOTAL_NUMBER_OF_RECURRENCES_NOT_VALID = "error.document.invoiceRecurrenceMaintenance.endDateAndTotalNumberOfRecurrencesNotValid";
    public static final String ERROR_INVOICE_RECURRENCE_INTERVAL_CODE_IS_REQUIRED = "error.document.invoiceRecurrenceMaintenance.intervalCodeIsRequired";

    // Organization Accounting Defaults errors
    public static final class OrganizationAccountingDefaultErrors {
        public static final String WRITE_OFF_OBJECT_CODE_INVALID = "error.document.organizationAccountingDefaultMaintenance.writeOffObjectCodeInvalid";
        public static final String LATE_CHARGE_OBJECT_CODE_INVALID = "error.document.organizationAccountingDefaultMaintenance.lateChargeObjectCodeInvalid";
        public static final String DEFAULT_INVOICE_FINANCIAL_OBJECT_CODE_INVALID = "error.document.organizationAccountingDefaultMaintenance.defaultInvoiceFinancialObjectCodeInvalid";
        public static final String DEFAULT_CHART_OF_ACCOUNTS_REQUIRED_IF_DEFAULT_OBJECT_CODE_EXISTS = "error.document.organizationAccountingDefaultMaintenance.defaultInvoiceChartOfAccountsCodeMustExist";
        public static final String ERROR_WRITEOFF_OBJECT_CODE_REQUIRED = "error.document.customerInvoiceDocument.writeoffFinancialObjectCodeRequired";
        public static final String ERROR_WRITEOFF_CHART_OF_ACCOUNTS_CODE_REQUIRED = "error.document.customerInvoiceDocument.writeoffChartOfAccountsCodeRequired";
        public static final String ERROR_WRITEOFF_ACCOUNT_NUMBER_REQUIRED = "error.document.customerInvoiceDocument.writeoffAccountNumberRequired";
        public static final String ERROR_PAYMENT_CHART_OF_ACCOUNTS_CODE_REQUIRED = "error.document.customerInvoiceDocument.paymentChartOfAccountsCodeRequired";
        public static final String ERROR_PAYMENT_ACCOUNT_NUMBER_REQUIRED = "error.document.customerInvoiceDocument.paymentAccountNumberRequired";
        public static final String ERROR_PAYMENT_OBJECT_CODE_REQUIRED = "error.document.customerInvoiceDocument.paymentPaymentObjectCodeRequired";        
    }

    // System Information errors
    public static final class SystemInformation {
        //public static final String SALES_TAX_OBJECT_CODE_INVALID = "error.SystemInformation.salesTaxObjectCodeInvalid";
    }

    // Invoice Item Code errors
    public static final class InvoiceItemCode {
        public static final String NONPOSITIVE_ITEM_DEFAULT_PRICE = "error.invoiceItemCode.nonPositiveNumericValue";
        public static final String NONPOSITIVE_ITEM_DEFAULT_QUANTITY = "error.invoiceItemCode.nonPositiveNumericValue";
        public static final String ORG_OPTIONS_DOES_NOT_EXIST_FOR_CHART_AND_ORG = "error.invoiceItemCode.orgOptionsDoesNotExistForChartAndOrg";
    }

    // Customer Type errors
    public static class CustomerTypeConstants {
        public static final String ERROR_CUSTOMER_TYPE_DUPLICATE_VALUE = "error.document.customerType.duplicateValue";
    }

    // Customer Load messages
    public static final class CustomerLoad {
        public static final String MESSAGE_BATCH_UPLOAD_TITLE_CUSTOMER = "message.ar.customerLoad.batchUpload.title";
    }
    // Customer errors
    public static class CustomerConstants {
        public static final String MESSAGE_CUSTOMER_WITH_SAME_NAME_EXISTS = "message.document.customerMaintenance.customerWithSameNameExists";
        public static final String GENERATE_CUSTOMER_QUESTION_ID = "GenerateCustomerQuestionID";
        public static final String ERROR_AT_LEAST_ONE_ADDRESS = "error.document.customer.addressRequired";
        public static final String ERROR_ONLY_ONE_PRIMARY_ADDRESS = "error.document.customer.oneAndOnlyOnePrimaryAddressRequired";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY = "P";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE = "A";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE_US = "US";
        public static final String ERROR_CUSTOMER_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US = "error.document.customer.stateCodeIsRequiredWhenCountryUS";
        public static final String ERROR_CUSTOMER_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US = "error.document.customer.zipCodeIsRequiredWhenCountryUS";
        public static final String ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US = "error.document.customer.addressInternationalProvinceNameIsRequiredWhenCountryNonUS";
        public static final String ERROR_CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US = "error.document.customer.internationalMailCodeIsRequiredWhenCountryNonUS";
        public static final String ERROR_TAX_NUMBER_IS_REQUIRED = "error.document.customer.taxNumberRequired";
        public static final String ACTIONS_REPORT = "report";
    }

    // Customer Credit Memo errors/warnings:
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_INVALID_DATA_INPUT = "error.document.customerCreditMemoDocument.invalidDataInput";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerCreditMemoDocument.invalidCustomerCreditMemoItemQuantity";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_LESS_THAN_OR_EQUAL_TO_ZERO = "error.document.customerCreditMemoDocument.invalidCustomerCreditMemoItemAmount";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_QUANTITY_GREATER_THAN_INVOICE_ITEM_QUANTITY = "error.document.customerCreditMemoDocument.itemQuantityGreaterThanParentItemQuantity";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DETAIL_ITEM_AMOUNT_GREATER_THAN_INVOICE_ITEM_AMOUNT = "error.document.customerCreditMemoDocument.itemAmountGreaterThanParentItemAmount";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVALID_INVOICE_DOCUMENT_NUMBER = "error.document.customerCreditMemoDocument.invalidInvoiceDocumentNumber";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_ONE_CRM_IN_ROUTE_PER_INVOICE = "error.document.customerCreditMemoDocument.onlyOneCRMInRoutePerInvoice";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT__INVOICE_DOCUMENT_NUMBER_IS_REQUIRED = "error.document.customerCreditMemoDocument.invRefNumberIsRequired";
    public static final String WARNING_CUSTOMER_CREDIT_MEMO_DOCUMENT_INVOICE_HAS_DISCOUNT = "warning.documnet.customerCreditMemoDocument.invoiceHasAppliedDiscount";
    public static final String ERROR_CUSTOMER_CREDIT_MEMO_DOCUMENT_NO_DATA_TO_SUBMIT = "error.document.customerCreditMemoDocument.noDataToSubmit";

    // Cash Control Document errors
    public static final String ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH = "error.ar.ReferenceDocNumberCannotBeNullforPaymentMediumCash";
    public static final String ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH = "error.ar.ReferenceDocNumberMustBeValidforPaymentMediumCash";
    public static final String ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG = "error.ar.OrganizationOptionsMustBeSet";
    public static final String ERROR_PAYMENT_MEDIUM_IS_NOT_VALID = "error.ar.CustomerPaymentMediumIsNotValid";
    public static final String ERROR_ALL_APPLICATION_DOCS_MUST_BE_APPROVED = "error.ar.AllApplicationDocumentsMustBeApproved";
    public static final String ERROR_DELETE_ADD_APP_DOCS_NOT_ALLOWED_AFTER_GLPES_GEN = "error.ar.DeleteAddApplicationDocNotAllowedAfterGLPEsGenerated";
    public static final String ERROR_NO_LINES_TO_PROCESS = "error.ar.NoLinesToProcess";
    public static final String ERROR_LINE_AMOUNT_CANNOT_BE_ZERO = "error.ar.LineAmountCannotBeZero";
    public static final String ERROR_LINE_AMOUNT_CANNOT_BE_NEGATIVE = "error.ar.LineAmountCannotBeNegative";
    public static final String ERROR_GLPES_NOT_CREATED = "error.ar.GLPEsNotCreated";
    public static final String ERROR_CUSTOMER_NUMBER_IS_NOT_VALID = "error.ar.CustomerNumberIsNotValid";
    public static final String ERROR_CUSTOMER_IS_INACTIVE = "error.ar.CustomerIsInactive";

    public static final String CREATED_BY_CASH_CTRL_DOC = "message.ar.createdByCashControlDocument";
    public static final String DOCUMENT_DELETED_FROM_CASH_CTRL_DOC = "message.ar.documentDeletedFromCashControl";
    public static final String ELECTRONIC_PAYMENT_CLAIM = "message.ar.electronicPaymentClaim";

    // Customer Invoice Writeoff Document errors
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_CHART_WRITEOFF_OBJECT_DOESNT_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffObjectDoesntExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_MUST_EXIST = "error.document.customerInvoiceWriteoff.writeoffFAUMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_CHART_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUChartMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_ACCOUNT_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUAccountMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_OBJECT_CODE_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUObjectMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_INVOICE_HAS_CREDIT_BALANCE = "error.document.customerInvoiceWriteoff.invoiceHasCreditBalance";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_INVALID_EXPLANATION = "error.document.customerInvoiceWriteoff.invalidExplanation";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_EMPTY_EXPLANATION = "error.document.customerInvoiceWriteoff.emptyExplanation";

    // Organization Options errors
    public static class OrganizationOptionsErrors {
        public static final String SYS_INFO_DOES_NOT_EXIST_FOR_PROCESSING_CHART_AND_ORG = "error.document.organizationOptions.sysInfoDoesNotExistForProcessingChartAndOrg";
        public static final String ERROR_ORG_OPTIONS_ZIP_CODE_REQUIRED = "error.document.organizationOptions.orgOptionsZipCodeRequired";
    }    

    public static class PaymentApplicationDocumentErrors {
        public static final String AMOUNT_TO_BE_APPLIED_EXCEEDS_AMOUNT_OUTSTANDING = "error.document.paymentApplication.amountToBeAppliedExceedsAmountOutstanding";
        public static final String AMOUNT_TO_BE_APPLIED_CANNOT_BE_ZERO = "error.document.paymentApplication.amountToBeAppliedCannotBeZero";
        public static final String FULL_AMOUNT_NOT_APPLIED = "error.document.paymentApplication.fullAmountNotApplied";
        public static final String AMOUNT_TO_BE_APPLIED_EXCEEDS_OPEN_INVOICE_DETAIL_AMOUNT = "error.document.paymentApplication.amountToBeAppliedExceedsOpenInvoiceDetailAmount";
        public static final String AMOUNT_TO_BE_APPLIED_MUST_BE_POSTIIVE = "error.document.paymentApplication.amountToBeAppliedMustBePositive";
        public static final String NON_AR_AMOUNT_REQUIRED = "error.document.paymentApplication.nonArLineRequired";
        public static final String NON_AR_AMOUNT_MUST_BE_POSITIVE = "error.document.paymentApplication.nonArLineAmountMustBePositive";
        public static final String NON_AR_AMOUNT_EXCEEDS_SELECTED_INVOICE_BALANCE = "error.document.paymentApplication.nonArLineAmountExceedsInvoiceBalance";
    }
    
}
