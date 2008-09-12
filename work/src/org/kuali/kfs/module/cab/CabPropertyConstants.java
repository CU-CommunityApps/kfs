/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab;


public class CabPropertyConstants {
    public static class GeneralLedgerEntry {
        public static final String TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER = "transactionLedgerEntrySequenceNumber";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
        public static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    }

    public static class Parameter {
        public static final String PARAMETER_NAME = "parameterName";
        public static final String PARAMETER_DETAIL_TYPE_CODE = "parameterDetailTypeCode";
        public static final String PARAMETER_NAMESPACE_CODE = "parameterNamespaceCode";

    }

    public static class Entry {
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "financialObject.financialObjectSubTypeCode";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String TRANSACTION_DATE_TIME_STAMP = "transactionDateTimeStamp";
        public static final String DOCUMENT_NUMBER = "documentNumber";
    }

    public static class PurchasingAccountsPayableDocument {
        public static final String PURCHASE_ORDER_IDENTIFIER = "purchaseOrderIdentifier";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String PURCHASEING_ACCOUNTS_PAYABLE_ITEM_ASSETS = "purchasingAccountsPayableItemAssets";
    }

    public static class PurchasingAccountsPayableItemAsset {
        public static final String ACCOUNTS_PAYABLE_LINE_ITEM_IDENTIFIER = "accountsPayableLineItemIdentifier";
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String ITEM_LINE_NUMBER = "itemLineNumber";
        public static final String CAPITAL_ASSET_BUILDER_LINE_NUMBER = "capitalAssetBuilderLineNumber";
        public static final String ACCOUNTS_PAYABLE_ITEM_QUANTITY = "accountsPayableItemQuantity";
        public static final String SPLIT_QTY = "splitQty";
    }

    public static class PurchasingAccountsPayableLineAssetAccount {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String PUR_ITM_ID = "accountsPayableLineItemIdentifier";
        public static final String CAB_LINE_NUMBER = "capitalAssetBuilderLineNumber";
        public static final String GENERAL_LEDGER_ENTRY = "generalLedgerEntry";
        public static final String PURAP_ITEM_ASSET = "purchasingAccountsPayableItemAsset";
    }

    public static class Pretag {
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String PRETAG_DETAIL_CAMPUS_TAG_NUMBER = "pretagDetail.campusTagNumber";
    }

    public static final String DOCUMENT_NUMBER = "documentNumber";

    public static class PurApLineForm {
        public static final String PURAP_DOCS = "purApDocs";
        public static final String MERGE_QTY = "mergeQty";
        public static final String MERGE_DESC = "mergeDesc";
    }

    public static class GeneralLedgerPendingEntry {
        public static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
        public static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "financialObject.financialObjectSubTypeCode";
        public static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String TRANSACTION_ENTRY_PROCESSED_TS = "transactionEntryProcessedTs";
    }

    public static class CreditMemoAccountHistory {
        public static final Object ACCOUNT_HISTORY_TIMESTAMP = "accountHistoryTimestamp";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
    }

    public static class PaymentRequestAccountHistory {
        public static final Object ACCOUNT_HISTORY_TIMESTAMP = "accountHistoryTimestamp";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String FINANCIAL_OBJECT_FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
    }

    public static class Account {
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    }

    public static class PreTagExtract {
        public static final String FINANCIAL_OBJECT_SUB_TYPE_CODE = "objectCode.financialObjectSubTypeCode";
        public static final String ACCOUNT_SUB_FUND_GROUP_CODE = "account.subFundGroupCode";
        public static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
        public static final String PURAP_ITEM_UNIT_PRICE = "purapItem.itemUnitPrice";
        public static final String PO_STATUS_CODE = "purapItem.purapDocument.statusCode";
        public static final String PO_INITIAL_OPEN_DATE = "purapItem.purapDocument.purchaseOrderInitialOpenDate";
        public static final String PURAP_CAPITAL_ASSET_SYSTEM_STATE_CODE = "purapItem.purapDocument.capitalAssetSystemStateCode";
    }
}
