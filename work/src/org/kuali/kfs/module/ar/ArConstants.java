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
    public static final String ERROR_CUSTOMER_NUMBER_IS_NOT_VALID = "error.ar.CustomerNumberIsNotValid";
    public static final String ERROR_CUSTOMER_IS_INACTIVE = "error.ar.CustomerIsInactive";
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
    
    public static final String LOOKUP_CUSTOMER_NAME = "customerName";
    public static final String LOOKUP_CUSTOMER_NUMBER = "customerNumber";
    public static final String LOOKUP_INVOICE_NUMBER = "invoiceNumber";

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

    // Invoice Recurrence errors/warnings:
    public static final String ERROR_RECURRING_INVOICE_NUMBER_MUST_BE_APPROVED = "error.document.invoiceRecurrenceMaintenance.invoiceMustBeApproved";
    public static final String ERROR_MAINTENANCE_DOCUMENT_ALREADY_EXISTS = "error.document.invoiceRecurrenceMaintenance.maintenanceDocumentsExists";
    public static final String ERROR_INVOICE_RECURRENCE_BEGIN_DATE_EARLIER_THAN_TODAY = "error.document.invoiceRecurrenceMaintenance.beginDateMustBeEarlierThanToday";
    public static final String ERROR_INVOICE_DOES_NOT_EXIST = "error.document.invoiceRecurrenceMaintenance.invoiceDoesNotExist";
    public static final String ERROR_END_DATE_EARLIER_THAN_BEGIN_DATE = "error.document.invoiceRecurrenceMaintenance.endDateEarlierThanBeginDate";
    public static final String ERROR_END_DATE_OR_TOTAL_NUMBER_OF_RECURRENCES = "error.document.invoiceRecurrenceMaintenance.enterEndDateOrTotalNumberOfRecurrences";
    public static final String ERROR_TOTAL_NUMBER_OF_RECURRENCES_GREATER_THAN_ALLOWED = "error.document.invoiceRecurrenceMaintenance.totalRecurrencesMoreThanAllowed";
    public static final String ERROR_END_DATE_AND_TOTAL_NUMBER_OF_RECURRENCES_NOT_VALID = "error.document.invoiceRecurrenceMaintenance.endDateAndTotalNumberOfRecurrencesNotValid";

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

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_CHART_OF_ACCOUNTS_CODE = "error.document.customerInvoiceDocument.invalidPaymentChartOfAccountsCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_ACCOUNT_NUMBER = "error.document.customerInvoiceDocument.invalidPaymentAccountNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_ACCOUNT_NUMBER = "error.document.customerInvoiceDocument.invalidPaymentSubAccountNumber";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_OBJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_SUB_OBJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentSubObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_PAYMENT_PROJECT_CODE = "error.document.customerInvoiceDocument.invalidPaymentProjectCode";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SHIP_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.invalidShipToAddressIdentifier";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_BILL_TO_ADDRESS_IDENTIFIER = "error.document.customerInvoiceDocument.invalidBillToAddressIdentifier";

    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CHART_WITH_NO_AR_OBJ_CD = "error.document.customerInvoiceDocument.invalidChartWithNoARObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_WITH_NO_AR_OBJ_CD = "error.document.customerInvoiceDocument.invalidSubFundWithNoARObjectCode";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_AR_OBJ_CD_IN_PARM = "error.document.customerInvoiceDocument.invalidSubFundARObjectCodeInParm";
    public static final String ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_UNIT_OF_MEASURE_CD = "error.document.customerInvoiceDocument.invalidUnitOfMeasureCode";
    
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


    public static final String CUSTOMER_INVOICE_DOCUMENT_INVOICE_ITEM_CODE_PROPERTY = "invoiceItemCode";
    public static final String CUSTOMER_INVOICE_DETAIL_DEFAULT_DISCOUNT_DESCRIPTION_PREFIX = "LINE ITEM DISCOUNT";

    //Customer Invoice Writeoff errors
    public static final String ERROR_ORG_ACCT_DEFAULT_FOR_USER_MUST_EXIST = "error.ar.OrganizationAccountingDefaultMustExist";
    public static final String ERROR_ORG_ACCT_DEFAULT_WRITEOFF_MUST_EXIST = "error.ar.OrganizationAccountingDefaultWriteoffMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_CHART_WRITEOFF_OBJECT_DOESNT_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffObjectDoesntExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_MUST_EXIST = "error.document.customerInvoiceWriteoff.writeoffFAUMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_CHART_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUChartMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_ACCOUNT_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUAccountMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_FAU_OBJECT_CODE_MUST_EXIST = "error.document.customerInvoiceWriteoff.chartWriteoffFAUObjectMustExist";
    public static final String ERROR_CUSTOMER_INVOICE_WRITEOFF_INVOICE_HAS_CREDIT_BALANCE = "error.document.customerInvoiceWriteoff.invoiceHasCreditBalance";

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
        public static final String CUSTOMER_NUMBER = "customerNumber";
    }

    public static class CustomerCreditMemoDocumentFields {
        public static final String CREDIT_MEMO_ITEM_QUANTITY = "creditMemoItemQuantity";
        public static final String CREDIT_MEMO_ITEM_TOTAL_AMOUNT = "creditMemoItemTotalAmount";
        public static final String CREDIT_MEMO_DOCUMENT_REF_INVOICE_NUMBER = "financialDocumentReferenceInvoiceNumber";
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
        public static final String UNIT_OF_MEASURE_CODE = "itemUnitOfMeasureCode";


        public static final String CUSTOMER = "customer";
        public static final String CUSTOMER_NUMBER = "accountsReceivableDocumentHeader.customerNumber";

        public static final String INVOICE_DUE_DATE = "invoiceDueDate";
        public static final String BILLED_BY_ORGANIZATION = "billedByOrganization";
        public static final String BILLED_BY_ORGANIZATION_CODE = "billedByOrganizationCode";

        public static final String BILL_BY_CHART_OF_ACCOUNT = "billByChartOfAccount";
        public static final String BILL_BY_CHART_OF_ACCOUNT_CODE = "billByChartOfAccountCode";

        public static final String INVOICE_ITEM_UNIT_PRICE = "invoiceItemUnitPrice";
        public static final String INVOICE_ITEM_QUANTITY = "invoiceItemQuantity";

        public static final String PROCESSING_CHART_OF_ACCOUNT_CODE = "accountsReceivableDocumentHeader.processingChartOfAccountCode";

        public static final String SHIP_TO_ADDRESS_IDENTIFIER = "customerShipToAddressIdentifier";
        public static final String BILL_TO_ADDRESS_IDENTIFIER = "customerBillToAddressIdentifier";
    }
    
    public static class CustomerInvoiceWriteoffDocumentFields {
        public static final String CUSTOMER_INVOICE_DETAILS_FOR_WRITEOFF = "customerInvoiceDetailsForWriteoff";
    }

    public static class OrganizationOptionsFields {
        public static final String PROCESSING_CHART_OF_ACCOUNTS_CODE = "processingChartOfAccountCode";
        public static final String ORGANIZATION_CHECK_PAYABLE_TO_NAME = "organizationCheckPayableToName";
        public static final String ORGANIZATION_REMIT_TO_ADDRESS_NAME = "organizationRemitToAddressName";
        public static final String ORGANIZATION_REMIT_TO_LINE1_STREET_ADDRESS = "organizationRemitToLine1StreetAddress";
        public static final String ORGANIZATION_REMIT_TO_LINE2_STREET_ADDRESS = "organizationRemitToLine2StreetAddress";
        public static final String ORGANIZATION_REMIT_TO_CITY_NAME = "organizationRemitToCityName";
        public static final String ORGANIZATION_REMIT_TO_STATE_CODE = "organizationRemitToStateCode";
        public static final String ORGANIZATION_REMIT_TO_ZIP_CODE = "organizationRemitToZipCode";
    }
    
    public static class OrganizationOptionsErrors {
        public static final String SYS_INFO_DOES_NOT_EXIST_FOR_PROCESSING_CHART_AND_ORG = "error.document.organizationOptions.sysInfoDoesNotExistForProcessingChartAndOrg";
    }    
    

    public static class OrganizationAccountingDefaultFields {
        
        public static final String LATE_CHARGE_OBJECT_CODE = "organizationLateChargeObjectCode";
        public static final String INVOICE_CHART_OF_ACCOUNTS_CODE = "defaultInvoiceChartOfAccountsCode";
        public static final String PAYMENT_CHART_OF_ACCOUNTS_CODE = "defaultPaymentChartOfAccountsCode";
        public static final String PAYMENT_ACCOUNT_NUMBER = "defaultPaymentAccountNumber";
        public static final String PAYMENT_FINANCIAL_OBJECT_CODE = "defaultPaymentFinancialObjectCode";
        
        public static final String WRITEOFF_FINANCIAL_OBJECT_CODE = "writeoffFinancialObjectCode";
        public static final String WRITEOFF_CHART_OF_ACCOUNTS_CODE = "writeoffChartOfAccountsCode";
        public static final String WRITEOFF_ACCOUNT_NUMBER = "writeoffAccountNumber";
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
    public static final String CUSTOMER_INVOICE_DOCUMENT_UNIT_OF_MEASURE_PROPERTY = "invoiceItemUnitOfMeasureCode";
    public static final String UNIT_OF_MEASURE_PROPERTY = "itemUnitOfMeasureCode";
    public static final String DISCOUNT_PREFIX = "DISCOUNT - ";
    public static final String GLPE_WRITEOFF_OBJECT_CODE_BY_CHART = "GLPE_WRITEOFF_OBJECT_CODE_BY_CHART";

    public static class CustomerFields {
        public static final String CUSTOMER_TAB_ADDRESSES = "customerAddresses";
        public static final String CUSTOMER_ADDRESS_TYPE_CODE = "customerAddressTypeCode";
        public static final String CUSTOMER_ADDRESS_IDENTIFIER = "customerAddressIdentifier";
        public static final String CUSTOMER_ADDRESS_NUMBER = "customerNumber";
        public static final String CUSTOMER_ADDRESS_STATE_CODE = "customerStateCode";
        public static final String CUSTOMER_ADDRESS_ZIP_CODE = "customerZipCode";
        public static final String CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME = "customerAddressInternationalProvinceName";
        public static final String CUSTOMER_ADDRESS_INTERNATIONAL_MAIL_CODE = "customerInternationalMailCode";
        public static final String CUSTOMER_SOCIAL_SECURITY_NUMBER = "customerSocialSecurityNumberIdentifier";
    }

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
    }

    public static class CustomerTypeFields {
        public static final String CUSTOMER_TYPE_DESC = "customerTypeDescription";
    }

    public static class CustomerTypeConstants {
        public static final String ERROR_CUSTOMER_TYPE_DUPLICATE_VALUE = "error.document.customerType.duplicateValue";
    }

    public static class Workgroups {
        public static final String WORKGROUP_AR_SUPER_USERS = "AR_SUPER_USERS";
    }

    public static final class InvoiceItemCode {
        public static final String NONPOSITIVE_ITEM_DEFAULT_PRICE = "error.invoiceItemCode.nonPositiveNumericValue";
        public static final String NONPOSITIVE_ITEM_DEFAULT_QUANTITY = "error.invoiceItemCode.nonPositiveNumericValue";
        public static final String ORG_OPTIONS_DOES_NOT_EXIST_FOR_CHART_AND_ORG = "error.invoiceItemCode.orgOptionsDoesNotExistForChartAndOrg";
    }

    public static final class SystemInformation {
        public static final String SALES_TAX_OBJECT_CODE_INVALID = "error.SystemInformation.salesTaxObjectCodeInvalid";
    }

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
    public static final class InvoiceRecurrenceFields {
        public static final String RECURRING_INVOICE_NUMBER = "documentNumber";
        public static final String INVOICE_RECURRENCE_BEGIN_DATE = "documentRecurrenceBeginDate";
        public static final String INVOICE_RECURRENCE_END_DATE = "documentRecurrenceEndDate";
        public static final String INVOICE_RECURRENCE_TOTAL_RECURRENCE_NUMBER = "documentTotalRecurrenceNumber";
    }

    

}
