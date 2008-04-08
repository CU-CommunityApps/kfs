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
package org.kuali.module.cams;

import org.kuali.core.bo.Campus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.bo.Room;
import org.kuali.kfs.bo.State;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;


/**
 * Constants for cams business object property names.
 */
public class CamsPropertyConstants {

    public static class Asset {
        public static final String DOCUMENT_TYPE_CODE = "documentTypeCode";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String PRIMARY_DEPRECIATION_METHOD = "primaryDepreciationMethodCode";
        public static final String SALVAGE_AMOUNT = "salvageAmount";
        public static final String ASSET_DATE_OF_SERVICE = "capitalAssetInServiceDate";
        public static final String ASSET_RETIREMENT_FISCAL_YEAR = "retirementFiscalYear";
        public static final String ASSET_RETIREMENT_FISCAL_MONTH = "retirementPeriodCode";
        public static final String ASSET_INVENTORY_STATUS = "inventoryStatusCode";
        public static final String ASSET_WARRANTY_WARRANTY_NUMBER = "assetWarranty.warrantyNumber";
        public static final String NATIONAL_STOCK_NUMBER = "nationalStockNumber";
        public static final String GOVERNMENT_TAG_NUMBER = "governmentTagNumber";
        public static final String OLD_TAG_NUMBER = "oldTagNumber";
        public static final String AGENCY_NUMBER = "agencyNumber";
        public static final String ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE = "organizationOwnerChartOfAccountsCode";
        public static final String ORGANIZATION_OWNER_ACCOUNT_NUMBER = "organizationOwnerAccountNumber";
        public static final String VENDOR_NAME = "vendorName";
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String CAMPUS_CODE = "campusCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
        public static final String CAPITAL_ASSET_TYPE_CODE = "capitalAssetTypeCode";
        public static final String CAPITAL_ASSET_DESCRIPTION = "capitalAssetDescription";
        public static final String ASSET_DEPRECIATION_DATE = "depreciationDate";
        public static final String ASSET_PAYMENTS = "assetPayments";
        public static final String ESTIMATED_SELLING_PRICE = "estimatedSellingPrice";
        public static final String ESTIMATED_FABRICATION_COMPLETION_DATE = "estimatedFabricationCompletionDate";
        public static final String FABRICATION_ESTIMATED_TOTAL_AMOUNT = "fabricationEstimatedTotalAmount";
        public static final String MANUFACTURER_NAME = "manufacturerName";
        public static final String MANUFACTURER_MODEL_NUMBER = "manufacturerModelNumber";
        public static final String CREATE_DATE = "createDate";
        public static final String FINANCIAL_DOCUMENT_POSTING_PERIOD_CODE = "financialDocumentPostingPeriodCode";
        public static final String FINANCIAL_DOCUMENT_POSTING_YEAR = "financialDocumentPostingYear";
        public static final String REPRESENTATIVE_UNIVERSAL_IDENTIFIER = "representativeUniversalIdentifier";
        public static final String ASSET_REPRESENTATIVE = "assetRepresentative";
        public static final String RECEIVE_DATE = "receiveDate";
        public static final String REPLACEMENT_AMOUNT = "replacementAmount";
        public static final String SERIAL_NUMBER = "serialNumber";
        public static final String LAST_INVENTORY_DATE = "lastInventoryDate";
        public static final String TOTAL_COST_AMOUNT = "totalCostAmount";
        public static final String FEDERAL_CONTRIBUTION = "federalContribution";
        public static final String ORGANIZATION_OWNER_ACCOUNT = "organizationOwnerAccount";
        public static final String CAPITAL_ASSET_TYPE = "capitalAssetType";
        public static final String RETIREMENT_INFO_MERGED_TARGET = "retirementInfo.assetRetirementGlobal.mergedTargetCapitalAssetNumber";
        public static final String ASSET_LOCATIONS = "assetLocations";
    }

    public static class AssetLocation {
        public static final String ASSET_LOCATION_CONTACT_NAME = "offCampusLocation.assetLocationContactName";
        public static final String ASSET_LOCATION_STREET_ADDRESS = "offCampusLocation.assetLocationStreetAddress";
        public static final String ASSET_LOCATION_CITY_NAME = "offCampusLocation.assetLocationCityName";
        public static final String ASSET_LOCATION_STATE_CODE = "offCampusLocation.assetLocationStateCode";
        public static final String ASSET_LOCATION_ZIP_CODE = "offCampusLocation.assetLocationZipCode";
        public static final String ASSET_LOCATION_COUNTRY_CODE = "offCampusLocation.assetLocationCountryCode";
    }

    public static class AssetHeader {
        public static final String DOCUMENT_NUMBER = "documentNumber";
        public static final String DOCUMENT_HEADER = "documentHeader";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
    }

    public static class AssetObject {
        public static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    }

    public static class AssetPayment {
        public static final String TRANSFER_PAYMENT_CODE = "transferPaymentCode";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
        public static final String ORIGINATION_CODE = "financialSystemOriginationCode";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
        public static final String OBJECT_CODE = "financialObjectCode";
        public static final String FINANCIAL_OBJECT = "financialObject";
        public static final String SUB_OBJECT_CODE = "financialSubObjectCode";
        public static final String OBJECT_TYPE_CODE = "financialObject.financialObjectTypeCode";
        public static final String PROJECT_CODE = "projectCode";
        public static final String PAYMENT_SEQ_NUMBER = "paymentSequenceNumber";
        public static final String TRANSACTION_DC_CODE = "transactionDebitCreditCode";
        public static final String ORGANIZATION_REFERENCE_ID = "organizationReferenceId";
        public static final String PURCHASE_ORDER_NUMBER = "purchaseOrderNumber";
        public static final String REQUISITION_NUMBER = "requisitionNumber";
        public static final String FINANCIAL_DOCUMENT_POSTING_DATE = "financialDocumentPostingDate";
        public static final String FINANCIAL_DOCUMENT_POSTING_YEAR = "financialDocumentPostingYear";
        public static final String FINANCIAL_DOCUMENT_POSTING_PERIOD_CODE = "financialDocumentPostingPeriodCode";
        public static final String ACCOUNT_CHARGE_AMOUNT = "accountChargeAmount";

        public static final String PRIMARY_DEPRECIATION_BASE_AMOUNT = "primaryDepreciationBaseAmount";
        public static final String ACCUMULATED_DEPRECIATION_AMOUNT = "accumulatedPrimaryDepreciationAmount";
        public static final String PREVIOUS_YEAR_DEPRECIATION_AMOUNT = "previousYearPrimaryDepreciationAmount";
        public static final String PERIOD_1_DEPRECIATION_AMOUNT = "period1Depreciation1Amount";
        public static final String PERIOD_2_DEPRECIATION_AMOUNT = "period2Depreciation1Amount";
        public static final String PERIOD_3_DEPRECIATION_AMOUNT = "period3Depreciation1Amount";
        public static final String PERIOD_4_DEPRECIATION_AMOUNT = "period4Depreciation1Amount";
        public static final String PERIOD_5_DEPRECIATION_AMOUNT = "period5Depreciation1Amount";
        public static final String PERIOD_6_DEPRECIATION_AMOUNT = "period6Depreciation1Amount";
        public static final String PERIOD_7_DEPRECIATION_AMOUNT = "period7Depreciation1Amount";
        public static final String PERIOD_8_DEPRECIATION_AMOUNT = "period8Depreciation1Amount";
        public static final String PERIOD_9_DEPRECIATION_AMOUNT = "period9Depreciation1Amount";
        public static final String PERIOD_10_DEPRECIATION_AMOUNT = "period10Depreciation1Amount";
        public static final String PERIOD_11_DEPRECIATION_AMOUNT = "period11Depreciation1Amount";
        public static final String PERIOD_12_DEPRECIATION_AMOUNT = "period12Depreciation1Amount";
    }
    
    public static class AssetPaymentDetail {
        public static final String DOCUMENT_TYPE_CODE="expenditureFinancialDocumentTypeCode";
    }
    
    public static class AssetType {
        public static final String ASSET_DEPRECIATION_LIFE_LIMIT = "depreciableLifeLimit";
    }

    public static class Pretag {
        public static final String CAMPUS_TAG_NUMBER = "campusTagNumber";
        public static final String PRETAG_DETAIL_CAMPUS_TAG_NUMBER = "pretagDetail.campusTagNumber";
    }

    public static class AssetOrganization {
        public static final String ASSET_ORGANIZATION = "assetOrganization";
        public static final String ORGANIZATION_TAG_NUMBER = "organizationTagNumber";
    }

    public static class AssetTransferDocument {
        public static final String ORGANIZATION = "organization";
        public static final String ORGANIZATION_OWNER_ACCOUNT = "organizationOwnerAccount";
        public static final String ORGANIZATION_OWNER_ACCOUNT_NUMBER = "organizationOwnerAccountNumber";
        public static final String ORGANIZATION_OWNER_CHART_OF_ACCOUNTS_CODE = "organizationOwnerChartOfAccountsCode";
        public static final String BUILDING_CODE = "buildingCode";
        public static final String BUILDING = "building";
        public static final String CAMPUS_CODE = "campusCode";
        public static final String BUILDING_ROOM_NUMBER = "buildingRoomNumber";
        public static final String ASSET_HEADER = "assetHeader";
        public static final String CAMPUS = "campus";
        public static final String ORGANIZATION_OWNER_CHART_OF_ACCOUNTS = "organizationOwnerChartOfAccounts";
        public static final String OFF_CAMPUS_STATE = "offCampusState";
        public static final String BUILDING_ROOM = "buildingRoom";
        public static final String OFF_CAMPUS_STATE_CODE = "offCampusStateCode";
        public static final String OFF_CAMPUS_ADDRESS = "offCampusAddress";
        public static final String OFF_CAMPUS_CITY = "offCampusCityName";
        public static final String OFF_CAMPUS_ZIP = "offCampusZipCode";
        public static final String LOCATION_TAB = "locationTabKey";
    }

    public static class AssetComponent {
        public static final String COMPONENT_NUMBER = "componentNumber";
        public static final String CAPITAL_ASSET_NUMBER = "capitalAssetNumber";
    }

    public static class AssetRetirementGlobal {
        public static final String SHARED_RETIREMENT_INFO = "sharedRetirementInfo";
    }

    public static class AssetRetirementGlobalDetail {
        public static final String RETIREMENT_CHART_OF_ACCOUNTS_CODE = "retirementChartOfAccountsCode";
        public static final String RETIREMENT_ACCOUNT_NUMBER = "retirementAccountNumber";
        public static final String RETIREMENT_CONTACT_NAME = "retirementContactName";
        public static final String RETIREMENT_INSTITUTION_NAME = "retirementInstitutionName";
        public static final String RETIREMENT_STREET_ADDRESS = "retirementStreetAddress";
        public static final String RETIREMENT_CITY_NAME = "retirementCityName";
        public static final String RETIREMENT_STATE_CODE = "retirementStateCode";
        public static final String RETIREMENT_ZIP_CODE = "retirementZipCode";
        public static final String RETIREMENT_COUNTRY_CODE = "retirementCountryCode";
        public static final String RETIREMENT_PHONE_NUMBER = "retirementPhoneNumber";
        public static final String ESTIMATED_SELLING_PRICE = "estimatedSellingPrice";
        public static final String SALE_PRICE = "salePrice";
        public static final String CASH_RECEIPT_FINANCIAL_DOCUMENT_NUMBER = "cashReceiptFinancialDocumentNumber";
        public static final String HANDLING_FEE_AMOUNT = "handlingFeeAmount";
        public static final String PREVENTIVE_MAINTENANCE_AMOUNT = "preventiveMaintenanceAmount";
        public static final String BUYER_DESCRIPTION = "buyerDescription";
        public static final String PAID_CASE_NUMBER = "paidCaseNumber";
    }
}
