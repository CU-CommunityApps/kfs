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
package org.kuali.kfs.fp.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.PaymentReasonCode;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ErrorMap;

/**
 * implementing the service methods defined in DisbursementVoucherPaymentReasonService
 * 
 * @see DisbursementVoucherPaymentReasonService
 */
public class DisbursementVoucherPaymentReasonServiceImpl implements DisbursementVoucherPaymentReasonService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPaymentReasonServiceImpl.class);

    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;
    private DisbursementVoucherPayeeService disbursementVoucherPayeeService;

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isPayeeQualifiedForPayment(org.kuali.kfs.fp.businessobject.DisbursementPayee,
     *      java.lang.String)
     */
    public boolean isPayeeQualifiedForPayment(DisbursementPayee payee, String paymentReasonCode) {
        List<String> payeeTypeCodes = this.getPayeeTypesByPaymentReason(paymentReasonCode);
        return this.isPayeeQualifiedForPayment(payee, paymentReasonCode, payeeTypeCodes);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isPayeeQualifiedForPayment(org.kuali.kfs.fp.businessobject.DisbursementPayee,
     *      java.lang.String, java.util.List)
     */
    public boolean isPayeeQualifiedForPayment(DisbursementPayee payee, String paymentReasonCode, List<String> payeeTypeCodes) {
        if (payeeTypeCodes == null || payeeTypeCodes.isEmpty()) {
            return false;
        }

        String payeeTypeCode = payee.getPayeeTypeCode();
        if (!payeeTypeCodes.contains(payeeTypeCode)) {
            return false;
        }

        if (this.isMovingPaymentReason(paymentReasonCode) && disbursementVoucherPayeeService.isVendor(payee)) {
            // Only vendors who are individuals can be paid moving expenses
            return disbursementVoucherPayeeService.isPayeeIndividualVendor(payee);
        }

        if (this.isPrepaidTravelPaymentReason(paymentReasonCode)) {
            boolean isActiveVendorEmployee = payee.isActive();
            isActiveVendorEmployee &= disbursementVoucherPayeeService.isVendor(payee);
            isActiveVendorEmployee &= disbursementVoucherPayeeService.isEmployee(payee);

            // Active vendor employees cannot be paid for prepaid travel
            return !isActiveVendorEmployee;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isMovingPaymentReason(java.lang.String)
     */
    public boolean isMovingPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.MOVING_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isPrepaidTravelPaymentReason(java.lang.String)
     */
    public boolean isPrepaidTravelPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isResearchPaymentReason(java.lang.String)
     */
    public boolean isResearchPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.RESEARCH_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#isRevolvingFundPaymentReason(java.lang.String)
     */
    public boolean isRevolvingFundPaymentReason(String paymentReasonCode) {
        String typeParameterName = DisbursementVoucherConstants.REVOLVING_FUND_PAYMENT_REASONS_PARM_NM;
        return this.isPaymentReasonOfType(typeParameterName, paymentReasonCode);
    }
    
    public String getReserchNonVendorPayLimit() {
        return parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_NON_VENDOR_PAY_LIMIT_AMOUNT_PARM_NM);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#getPayeeTypesByPaymentReason(java.lang.String)
     */
    public List<String> getPayeeTypesByPaymentReason(String paymentReasonCode) {
        return parameterService.getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherConstants.VALID_PAYEE_TYPES_BY_PAYMENT_REASON_PARM, paymentReasonCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentReasonService#getPaymentReasonByPrimaryId(java.lang.String)
     */
    public PaymentReasonCode getPaymentReasonByPrimaryId(String paymentReasonCode) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.CODE, paymentReasonCode);

        return (PaymentReasonCode) businessObjectService.findByPrimaryKey(PaymentReasonCode.class, primaryKeys);
    }

    // post the usage of the given payee type code
    public void postPaymentReasonCodeUsage(String paymentReasonCode, ErrorMap errorMap) {
        List<String> payeeTypeCodes = this.getPayeeTypesByPaymentReason(paymentReasonCode);
        if (payeeTypeCodes == null || payeeTypeCodes.isEmpty()) {
            return;
        }

        String descriptivePayeeTypes = this.getDescriptivePayeeTypes(payeeTypeCodes);
        String descriptivePaymentReason = this.getPaymentReasonByPrimaryId(paymentReasonCode).getCodeAndDescription();
        if (payeeTypeCodes.size() > 1) {
            String messageKey = KFSKeyConstants.WARNING_DV_PAYMENT_REASON_VALID_FOR_MULTIPLE_PAYEE_TYPES;
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, messageKey, descriptivePaymentReason, descriptivePayeeTypes);
        }
        else if (payeeTypeCodes.size() == 1) {
            String messageKey = KFSKeyConstants.WARNING_DV_PAYMENT_REASON_VALID_FOR_SINGEL_PAYEE_TYPE;
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, messageKey, descriptivePaymentReason, descriptivePayeeTypes);
        }

        if (this.isResearchPaymentReason(paymentReasonCode)) {
            String limit = this.getReserchNonVendorPayLimit();
            String messageKey = KFSKeyConstants.WARNING_DV_REASERCH_PAYMENT_REASON;

            List<String> vendorTypeCodes = new ArrayList<String>();
            vendorTypeCodes.addAll(payeeTypeCodes);
            vendorTypeCodes.remove(DisbursementVoucherConstants.DV_PAYEE_TYPE_EMPLOYEE);
            String vendorTypes = this.getDescriptivePayeeTypes(vendorTypeCodes);

            errorMap.putError(KFSConstants.GLOBAL_ERRORS, messageKey, descriptivePaymentReason, descriptivePayeeTypes, vendorTypes, limit);
        }

        if (this.isMovingPaymentReason(paymentReasonCode)) {
            String messageKey = KFSKeyConstants.WARNING_DV_MOVING_PAYMENT_REASON;
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, messageKey);
        }

        if (this.isPrepaidTravelPaymentReason(paymentReasonCode)) {
            String messageKey = KFSKeyConstants.WARNING_DV_PREPAID_TRAVEL_PAYMENT_REASON;
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, messageKey, descriptivePaymentReason, descriptivePayeeTypes);
        }
    }

    // get the descriptive payee types according to the given payee type codes
    private String getDescriptivePayeeTypes(List<String> payeeTypeCodes) {
        if (payeeTypeCodes == null || payeeTypeCodes.isEmpty()) {
            return StringUtils.EMPTY;
        }

        String oneSpace = " ";
        StringBuilder payeeTypesAsString = new StringBuilder();
        for (String payeeTypeCode : payeeTypeCodes) {
            String payeeTypeDescription = SpringContext.getBean(DisbursementVoucherPayeeService.class).getPayeeTypeDescription(payeeTypeCode);

            int index = payeeTypeCodes.indexOf(payeeTypeCode);
            if (index == 0) {
                payeeTypesAsString.append(payeeTypeDescription);
            }
            else if (index < payeeTypeCodes.size() - 1) {
                payeeTypesAsString.append(KFSConstants.COMMA).append(oneSpace).append(payeeTypeDescription);
            }
            else if (index == payeeTypeCodes.size() - 1) {
                payeeTypesAsString.append(oneSpace).append(KFSConstants.AND).append(oneSpace).append(payeeTypeDescription);
            }
        }

        return payeeTypesAsString.toString();
    }

    /**
     * determine whether the given payment reason is of type that is specified by typeParameterName
     * 
     * @param typeParameterName the given type parameter name
     * @param paymentReasonCode the given reason code
     * @return true if the given payment reason is of type that is specified by typeParameterName; otherwise, false
     */
    private boolean isPaymentReasonOfType(String typeParameterName, String paymentReasonCode) {
        return parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, typeParameterName, paymentReasonCode).evaluationSucceeds();
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the disbursementVoucherPayeeService attribute value.
     * 
     * @param disbursementVoucherPayeeService The disbursementVoucherPayeeService to set.
     */
    public void setDisbursementVoucherPayeeService(DisbursementVoucherPayeeService disbursementVoucherPayeeService) {
        this.disbursementVoucherPayeeService = disbursementVoucherPayeeService;
    }
}
