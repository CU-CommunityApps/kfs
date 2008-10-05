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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.ParameterEvaluator;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class DisbursementVoucherNonEmployeeTraveValidation extends GenericValidation {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherNonEmployeeTraveValidation.class);

    private ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private AccountingDocument accountingDocumentForValidation;
    
    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {        
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherNonEmployeeTravel nonEmployeeTravel = document.getDvNonEmployeeTravel();
        
        if (!isTravelNonEmplPaymentReason(document)) {
            return true;
        }
        
        ErrorMap errors = GlobalVariables.getErrorMap();
        errors.addToErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);

        /* check that vendor is no an employee, and if they are, then report error and stop validation */
        if(document.getDvPayeeDetail().isEmployee()) {
            errors.putError(KFSConstants.GENERAL_NONEMPLOYEE_TAB_ERRORS, "");
            return false;
        }
        
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObjectsRecursively(document.getDvNonEmployeeTravel(), 1);

        /* travel from and to state required if country is us */
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(nonEmployeeTravel.getDvTravelFromCountryCode()) && StringUtils.isBlank(nonEmployeeTravel.getDisbVchrTravelFromStateCode())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_TRAVEL_FROM_STATE_CODE, KFSKeyConstants.ERROR_DV_TRAVEL_FROM_STATE);
        }
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(nonEmployeeTravel.getDisbVchrTravelToCountryCode()) && StringUtils.isBlank(nonEmployeeTravel.getDisbVchrTravelToStateCode())) {
            errors.putError(KFSPropertyConstants.DISB_VCHR_TRAVEL_TO_STATE_CODE, KFSKeyConstants.ERROR_DV_TRAVEL_TO_STATE);
        }

        if (!errors.isEmpty()) {
            errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);
            return false;
        }

        /* must fill in all required per diem fields if any field is filled in */
        boolean perDiemSectionComplete = validatePerDiemSection(document, errors);

        /* must fill in all required personal vehicle fields if any field is filled in */
        boolean personalVehicleSectionComplete = validatePersonalVehicleSection(document, errors);

        /* must have per diem change message if actual amount is different from calculated amount */
        if (perDiemSectionComplete) { // Only validate if per diem section is filled in
            if (nonEmployeeTravel.getDisbVchrPerdiemCalculatedAmt().compareTo(nonEmployeeTravel.getDisbVchrPerdiemActualAmount()) != 0 && StringUtils.isBlank(nonEmployeeTravel.getDvPerdiemChangeReasonText())) {
                errors.putError(KFSPropertyConstants.DV_PERDIEM_CHANGE_REASON_TEXT, KFSKeyConstants.ERROR_DV_PERDIEM_CHANGE_REQUIRED);
            }
        }

        /* make sure per diem fields have not changed since the per diem amount calculation */
        if (perDiemSectionComplete) { // Only validate if per diem section is filled in
            KualiDecimal calculatedPerDiem = SpringContext.getBean(DisbursementVoucherTravelService.class).calculatePerDiemAmount(nonEmployeeTravel.getDvPerdiemStartDttmStamp(), nonEmployeeTravel.getDvPerdiemEndDttmStamp(), nonEmployeeTravel.getDisbVchrPerdiemRate());
            if (calculatedPerDiem.compareTo(nonEmployeeTravel.getDisbVchrPerdiemCalculatedAmt()) != 0) {
                errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KFSKeyConstants.ERROR_DV_PER_DIEM_CALC_CHANGE);
            }
        }

        /* total on non-employee travel must equal Check Total */
        /* if tax has been take out, need to add back in the tax amount for the check */
        KualiDecimal paidAmount = document.getDisbVchrCheckTotalAmount();
        paidAmount = paidAmount.add(SpringContext.getBean(DisbursementVoucherTaxService.class).getNonResidentAlienTaxAmount(document));
        // Ignore this rule if the DV has been coded for NRA tax, because amounts will not balance after tax coding.
        boolean nraTaxCoded = StringUtils.isNotBlank(document.getDvNonResidentAlienTax().getIncomeClassCode()) && StringUtils.equalsIgnoreCase("N", document.getDvNonResidentAlienTax().getIncomeClassCode());
        if (!nraTaxCoded && paidAmount.compareTo(document.getDvNonEmployeeTravel().getTotalTravelAmount()) != 0) {
            errors.putErrorWithoutFullErrorPath(KFSConstants.DV_CHECK_TRAVEL_TOTAL_ERROR, KFSKeyConstants.ERROR_DV_TRAVEL_CHECK_TOTAL);
        }

        /* make sure mileage fields have not changed since the mileage amount calculation */
        if (personalVehicleSectionComplete) {
            KualiDecimal currentCalcAmt = document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt();
            KualiDecimal currentActualAmt = document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount();
            if (ObjectUtils.isNotNull(currentCalcAmt) && ObjectUtils.isNotNull(currentActualAmt)) {
                KualiDecimal calculatedMileageAmount = SpringContext.getBean(DisbursementVoucherTravelService.class).calculateMileageAmount(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(), document.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());
                if (calculatedMileageAmount.compareTo(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt()) != 0) {
                    errors.putErrorWithoutFullErrorPath(KFSConstants.GENERAL_NONEMPLOYEE_TAB_ERRORS, KFSKeyConstants.ERROR_DV_MILEAGE_CALC_CHANGE);
                }

                // determine if the rule is flagged off in the parm setting
                boolean performTravelMileageLimitInd = parameterService.getIndicatorParameter(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.NONEMPLOYEE_TRAVEL_ACTUAL_MILEAGE_LIMIT_PARM_NM);
                if (performTravelMileageLimitInd) {
                    // if actual amount is greater than calculated amount
                    if (currentCalcAmt.subtract(currentActualAmt).isNegative()) {
                        errors.putError(KFSPropertyConstants.DV_PERSONAL_CAR_AMOUNT, KFSKeyConstants.ERROR_DV_ACTUAL_MILEAGE_TOO_HIGH);
                    }
                }
            }
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_EMPLOYEE_TRAVEL);

        return errors.isEmpty();
    }
    
    /**
     * This method checks to see if the per diem section of the non employee travel tab contains any values. If this section
     * contains any values, the section is validated to ensure that all the required fields for this section are populated.
     * 
     * @param document submitted disbursement voucher document
     * @param errors map containing any generated errors 
     * @return true if per diem section is used by user and that all fields contain values.
     */
    private boolean validatePerDiemSection(DisbursementVoucherDocument document, ErrorMap errors) {
        boolean perDiemSectionComplete = true;

        // Checks to see if any per diem fields are filled in
        boolean perDiemUsed = StringUtils.isNotBlank(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCategoryName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount());

        // If any per diem fields contain data, validates that all required per diem fields are filled in
        if (perDiemUsed) {
            if (StringUtils.isBlank(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCategoryName())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_CATEGORY_NAME, KFSKeyConstants.ERROR_DV_PER_DIEM_CATEGORY);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemRate())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_RATE, KFSKeyConstants.ERROR_DV_PER_DIEM_RATE);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemCalculatedAmt())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_CALCULATED_AMT, KFSKeyConstants.ERROR_DV_PER_DIEM_CALC_AMT);
                perDiemSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPerdiemActualAmount())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERDIEM_ACTUAL_AMOUNT, KFSKeyConstants.ERROR_DV_PER_DIEM_ACTUAL_AMT);
                perDiemSectionComplete = false;
            }
        }
        perDiemSectionComplete = perDiemSectionComplete && perDiemUsed;
        return perDiemSectionComplete;
    }

    /**
     * This method checks to see if the per diem section of the non employee travel tab contains any values. If this section
     * contains any values, the section is validated to ensure that all the required fields for this section are populated.
     * 
     * @param document submitted disbursement voucher document
     * @param errors map containing any generated errors 
     * @return true if per diem section is used by user and that all fields contain values.
     */
    private boolean validatePersonalVehicleSection(DisbursementVoucherDocument document, ErrorMap errors) {
        boolean personalVehicleSectionComplete = true;

        // Checks to see if any per diem fields are filled in
        boolean personalVehilcleUsed = ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromCityName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromStateCode()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToCityName()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToStateCode()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt()) || ObjectUtils.isNotNull(document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount());


        // If any per diem fields contain data, validates that all required per diem fields are filled in
        if (personalVehilcleUsed) {
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromCityName())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_AUTO_FROM_CITY_NAME, KFSKeyConstants.ERROR_DV_AUTO_FROM_CITY);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToCityName())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_AUTO_TO_CITY_NAME, KFSKeyConstants.ERROR_DV_AUTO_TO_CITY);
                personalVehicleSectionComplete = false;
            }

            // are state fields required always or only for US travel?
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoFromStateCode())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_AUTO_FROM_STATE_CODE, KFSKeyConstants.ERROR_DV_AUTO_FROM_STATE);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrAutoToStateCode())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_AUTO_TO_STATE_CODE, KFSKeyConstants.ERROR_DV_AUTO_TO_STATE);
                personalVehicleSectionComplete = false;
            }
            // end state field validation


            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount())) {
                errors.putError(KFSPropertyConstants.DV_PERSONAL_CAR_MILEAGE_AMOUNT, KFSKeyConstants.ERROR_DV_MILEAGE_AMT);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrMileageCalculatedAmt())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_MILEAGE_CALCULATED_AMT, KFSKeyConstants.ERROR_DV_MILEAGE_CALC_AMT);
                personalVehicleSectionComplete = false;
            }
            if (ObjectUtils.isNull(document.getDvNonEmployeeTravel().getDisbVchrPersonalCarAmount())) {
                errors.putError(KFSPropertyConstants.DISB_VCHR_PERSONAL_CAR_AMOUNT, KFSKeyConstants.ERROR_DV_MILEAGE_ACTUAL_AMT);
                personalVehicleSectionComplete = false;
            }
        }
        personalVehicleSectionComplete = personalVehicleSectionComplete && personalVehilcleUsed;
        return personalVehicleSectionComplete;
    }

    /**
     * Returns whether the document's payment reason is for travel by a non-employee
     * 
     * @param disbursementVoucherDocument submitted disbursement voucher document
     * @return true if payment reason is travel by a non-employee
     * 
     */
    private boolean isTravelNonEmplPaymentReason(DisbursementVoucherDocument disbursementVoucherDocument) {
        ParameterEvaluator travelNonEmplPaymentReasonEvaluator = parameterService.getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherRuleConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, disbursementVoucherDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode());
        return travelNonEmplPaymentReasonEvaluator.evaluationSucceeds();
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * 
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
