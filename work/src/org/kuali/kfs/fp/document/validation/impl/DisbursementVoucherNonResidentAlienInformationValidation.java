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
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonResidentAlienTax;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.NonResidentAlienTaxPercent;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherWorkGroupService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class DisbursementVoucherNonResidentAlienInformationValidation extends GenericValidation implements DisbursementVoucherRuleConstants {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherNonResidentAlienInformationValidation.class);

    private DisbursementVoucherWorkGroupService disbursementVoucherWorkGroupService = SpringContext.getBean(DisbursementVoucherWorkGroupService.class);;
    private AccountingDocument accountingDocumentForValidation;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) accountingDocumentForValidation;
        DisbursementVoucherNonResidentAlienTax nonResidentAlienTax = document.getDvNonResidentAlienTax();
        DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();
        
        FinancialSystemUser financialSystemUser = GlobalVariables.getUserSession().getFinancialSystemUser(); 
        if (!payeeDetail.isDisbVchrAlienPaymentCode() || !disbursementVoucherWorkGroupService.isUserInTaxGroup(financialSystemUser)) {
            return true;
        }
        
        ErrorMap errors = GlobalVariables.getErrorMap();
        errors.addToErrorPath(KFSPropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);

        /* income class code required */
        if (StringUtils.isBlank(nonResidentAlienTax.getIncomeClassCode())) {
            errors.putError(KFSPropertyConstants.INCOME_CLASS_CODE, KFSKeyConstants.ERROR_REQUIRED, "Income class code ");
        }
        else {
            /* for foreign source or treaty exempt, non reportable, tax percents must be 0 and gross indicator can not be checked */
            if (nonResidentAlienTax.isForeignSourceIncomeCode() || nonResidentAlienTax.isIncomeTaxTreatyExemptCode() || NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(nonResidentAlienTax.getIncomeClassCode())) {

                if ((nonResidentAlienTax.getFederalIncomeTaxPercent() != null && !(KualiDecimal.ZERO.equals(nonResidentAlienTax.getFederalIncomeTaxPercent())))) {
                    errors.putError(KFSPropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_FEDERAL_TAX_NOT_ZERO);
                }

                if ((nonResidentAlienTax.getStateIncomeTaxPercent() != null && !(KualiDecimal.ZERO.equals(nonResidentAlienTax.getStateIncomeTaxPercent())))) {
                    errors.putError(KFSPropertyConstants.STATE_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_STATE_TAX_NOT_ZERO);
                }

                if (nonResidentAlienTax.isIncomeTaxGrossUpCode()) {
                    errors.putError(KFSPropertyConstants.INCOME_TAX_GROSS_UP_CODE, KFSKeyConstants.ERROR_DV_GROSS_UP_INDICATOR);
                }

                if (NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(nonResidentAlienTax.getIncomeClassCode()) && StringUtils.isNotBlank(nonResidentAlienTax.getPostalCountryCode())) {
                    errors.putError(KFSPropertyConstants.POSTAL_COUNTRY_CODE, KFSKeyConstants.ERROR_DV_POSTAL_COUNTRY_CODE);
                }
            }
            else {
                if (nonResidentAlienTax.getFederalIncomeTaxPercent() == null) {
                    errors.putError(KFSPropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_REQUIRED, "Federal tax percent ");
                }
                else {
                    // check tax percent is in non-resident alien tax percent table for income class code
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(nonResidentAlienTax.getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(FEDERAL_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(nonResidentAlienTax.getFederalIncomeTaxPercent());

                    PersistableBusinessObject retrievedPercent = SpringContext.getBean(BusinessObjectService.class).retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.putError(KFSPropertyConstants.FEDERAL_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_INVALID_FED_TAX_PERCENT, new String[] { nonResidentAlienTax.getFederalIncomeTaxPercent().toString(), nonResidentAlienTax.getIncomeClassCode() });
                    }
                }

                if (nonResidentAlienTax.getStateIncomeTaxPercent() == null) {
                    errors.putError(KFSPropertyConstants.STATE_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_REQUIRED, "State tax percent ");
                }
                else {
                    NonResidentAlienTaxPercent taxPercent = new NonResidentAlienTaxPercent();
                    taxPercent.setIncomeClassCode(nonResidentAlienTax.getIncomeClassCode());
                    taxPercent.setIncomeTaxTypeCode(STATE_TAX_TYPE_CODE);
                    taxPercent.setIncomeTaxPercent(nonResidentAlienTax.getStateIncomeTaxPercent());

                    PersistableBusinessObject retrievedPercent = SpringContext.getBean(BusinessObjectService.class).retrieve(taxPercent);
                    if (retrievedPercent == null) {
                        errors.putError(KFSPropertyConstants.STATE_INCOME_TAX_PERCENT, KFSKeyConstants.ERROR_DV_INVALID_STATE_TAX_PERCENT, nonResidentAlienTax.getStateIncomeTaxPercent().toString(), nonResidentAlienTax.getIncomeClassCode());
                    }
                }
            }
        }

        /* country code required, unless income type is nonreportable */
        if (StringUtils.isBlank(nonResidentAlienTax.getPostalCountryCode()) && !NRA_TAX_INCOME_CLASS_NON_REPORTABLE.equals(nonResidentAlienTax.getIncomeClassCode())) {
            errors.putError(KFSPropertyConstants.POSTAL_COUNTRY_CODE, KFSKeyConstants.ERROR_REQUIRED, "Country code ");
        }

        errors.removeFromErrorPath(KFSPropertyConstants.DV_NON_RESIDENT_ALIEN_TAX);

        return errors.isEmpty();
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
