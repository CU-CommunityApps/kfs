/*
 * Copyright 2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/PurapKeyConstants.java,v $
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

/**
 * Holds error key constants for PURAP.
 * 
 */
public class PurapKeyConstants {

    public static final String PURAP_GENERAL_POTENTIAL_DUPLICATE = "error.document.purap.potentialDuplicate";
    
    //Vendor Maintenance
    public static final String ERROR_VENDOR_TYPE_REQUIRES_TAX_NUMBER = "error.vendorMaint.VendorTypeRequiresTaxNumber";
    public static final String ERROR_VENDOR_TAX_TYPE_AND_NUMBER_COMBO_EXISTS = "error.vendorMaint.addVendor.vendor.exists";
    public static final String ERROR_VENDOR_NAME_REQUIRED = "error.vendorMaint.vendorName.required";
    public static final String ERROR_VENDOR_BOTH_NAME_REQUIRED = "error.vendorMaint.bothNameRequired";
    public static final String ERROR_VENDOR_NAME_INVALID = "error.vendorMaint.nameInvalid";
    public static final String ERROR_VENDOR_TAX_TYPE_ALLOWED = "error.vendorMaint.tax.type.allowed";
    public static final String ERROR_VENDOR_MAX_MIN_ORDER_AMOUNT = "error.vendorMaint.minimumAmt.invalid";
    public static final String ERROR_VENDOR_TAX_TYPE_CANNOT_BE_BLANK = "error.vendorMaint.tax.type.cannot.be.blank";
    public static final String ERROR_VENDOR_TAX_TYPE_CANNOT_BE_SET = "error.vendorMaint.tax.type.cannot.be.set";
    public static final String ERROR_VENDOR_PARENT_NEEDS_CHANGED = "error.vendorMaint.vendorParent.needs.changed";
    public static final String ERROR_TAX_NUMBER_ALL_DIGITS_AND_LENGTH= "error.vendorMaint.taxNumber.isAllDigits.and.length";
    public static final String ERROR_TAX_NUMBER_ALL_ZEROES="error.vendorMaint.taxNumber.isAllZeroes";
    public static final String ERROR_FIRST_THREE_SSN = "error.vendorMaint.first.three.ssn";
    public static final String ERROR_MIDDLE_TWO_SSN = "error.vendorMaint.middle.two.ssn";
    public static final String ERROR_LAST_FOUR_SSN = "error.vendorMaint.last.four.ssn";
    public static final String ERROR_FIRST_TWO_FEIN = "error.vendorMaint.first.two.fein";
    public static final String ERROR_TAX_NUMBER_INVALID = "error.vendorMaint.taxNumber.invalid";
    public static final String ERROR_TAX_NUMBER_NOT_ALLOWED = "error.vendorMaint.taxNumber.notAllowed";
    
    //Vendor Lookup
    public static final String ERROR_VENDOR_LOOKUP_NAME_TOO_SHORT = "error.vendorLookup.name.too.short";
    public static final String ERROR_VENDOR_LOOKUP_FEWER_THAN_MIN_CRITERIA = "error.vendorLookup.min.criteria.fewer";
    public static final String ERROR_VENDOR_LOOKUP_PAYEE_ID_NO_STARTING_P = "error.vendorLookup.payeeId.no.starting.p";
    public static final String ERROR_VENDOR_LOOKUP_TYPE_NO_NAME_OR_STATE = "error.vendorLookup.type.no.name.or.state";
    public static final String ERROR_VENDOR_LOOKUP_STATUS_NO_NAME = "error.vendorLookup.status.no.name";
    public static final String ERROR_VENDOR_LOOKUP_TAX_NUM_ALL_ZEROES = "error.vendorLookup.taxNum.all.zeroes";
    public static final String ERROR_VENDOR_LOOKUP_VNDR_NUM_TOO_MANY_DASHES = "error.vendorLookup.vndrNum.dashes.tooMany";
    public static final String ERROR_VENDOR_LOOKUP_VNDR_NUM_DASHES_ONLY = "error.vendorLookup.vndrNum.dashes.only";
    public static final String ERROR_VENDOR_LOOKUP_STATE_NO_TYPE = "error.vendorLookup.state.no.type";
    
    //Vendor Maintenance Address
    public static final String ERROR_US_REQUIRES_STATE = "error.vendorMaint.vendorAddress.USRequiresState";
    public static final String ERROR_US_REQUIRES_ZIP = "error.vendorMaint.vendorAddress.USRequiresZip";
    public static final String ERROR_FAX_NUMBER = "error.vendorMaint.vendorAddress.faxNumber";
    public static final String ERROR_ADDRESS_TYPE = "error.vendorMaint.vendorAddress.addressType";
    
    // Vendor Maintenance Contract
    public static final String ERROR_VENDOR_CONTRACT_NO_APO_LIMIT = "error.vendorContract.noApoLimit";
    public static final String ERROR_VENDOR_CONTRACT_BEGIN_DATE_AFTER_END = "error.vendorContract.beginDateAfterEnd";
    public static final String ERROR_VENDOR_CONTRACT_BEGIN_DATE_NO_END_DATE = "error.vendorContract.beginDateNoEndDate";
    public static final String ERROR_VENDOR_CONTRACT_END_DATE_NO_BEGIN_DATE = "error.vendorContract.endDateNoBeginDate";
    public static final String ERROR_VENDOR_CONTRACT_ORG_EXCLUDED_WITH_APO_LIMIT = "error.vendorContractOrg.excludedWithApoLimit";
    public static final String ERROR_VENDOR_CONTRACT_ORG_NOT_EXCLUDED_NO_APO_LIMIT = "error.vendorContractOrg.notExcludedNoApoLimit";

    //Purchase Order & Requisition
    public static final String ERROR_PURCHASE_ORDER_BEGIN_DATE_AFTER_END = "error.purchaseOrder.beginDateAfterEnd";
    public static final String ERROR_PURCHASE_ORDER_BEGIN_DATE_NO_END_DATE = "error.purchaseOrder.beginDateNoEndDate";
    public static final String ERROR_PURCHASE_ORDER_END_DATE_NO_BEGIN_DATE = "error.purchaseOrder.endDateNoBeginDate";
    public static final String ERROR_RECURRING_DATE_NO_TYPE = "errors.recurring.type";
    public static final String ERROR_RECURRING_TYPE_NO_DATE = "errors.recurring.dates";
    public static final String ERROR_POSTAL_CODE_INVALID = "errors.postalCode.invalid";
    public static final String ERROR_FAX_NUMBER_INVALID = "errors.faxNumber.invalid";
    public static final String ERROR_FAX_NUMBER_PO_TRANSMISSION_TYPE = "error.faxNumber.PoTransmissionType";
    public static final String REQ_TOTAL_GREATER_THAN_PO_TOTAL_LIMIT = "error.purchaseOrderTotalLimit";
    public static final String ERROR_REQ_COPY_EXPIRED_CONTRACT = "error.requisition.copy.expired.contract";
    public static final String ERROR_REQ_COPY_INACTIVE_VENDOR = "error.requisition.copy.inactive.vendor";
}