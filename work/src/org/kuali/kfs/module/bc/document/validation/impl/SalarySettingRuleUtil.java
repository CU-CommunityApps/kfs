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
package org.kuali.kfs.module.bc.document.validation.impl;

import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPA;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPF;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.NONE;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.integration.businessobject.LaborLedgerObject;
import org.kuali.kfs.integration.service.LaborModuleService;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.service.BudgetConstructionPositionService;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * provide a set of rule elements for salary setting.
 */
public class SalarySettingRuleUtil {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingRuleUtil.class);

    private static LaborModuleService laborModuleService = SpringContext.getBean(LaborModuleService.class);
    private static DictionaryValidationService dictionaryValidationService = SpringContext.getBean(DictionaryValidationService.class);
    private static SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private static BudgetConstructionPositionService budgetConstructionPositionService = SpringContext.getBean(BudgetConstructionPositionService.class);

    /**
     * determine if the fields in the given appointment funding line are in the correct formats defined in the data dictionary
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the fields in the given appointment funding line are in the correct formats defined in the data dictionary;
     *         otherwise, false
     */
    public static boolean isFieldFormatValid(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        dictionaryValidationService.validateBusinessObject(appointmentFunding);
        int currentErrorCount = GlobalVariables.getErrorMap().getErrorCount();

        return currentErrorCount == originalErrorCount;
    }

    /**
     * determine whether the requested funding month of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested funding month of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedFundingMonth(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        Integer fundingMonths = appointmentFunding.getAppointmentFundingMonth();
        Integer normalWorkMonths = appointmentFunding.getBudgetConstructionPosition().getIuNormalWorkMonths();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (fundingMonths == null) {
            return false;
        }

        // Requested funding months must be between 0 and position normal work months if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            return fundingMonths >= 0 && fundingMonths <= normalWorkMonths;
        }

        // Requested funding months must equal to position normal work months if no leave
        return fundingMonths == normalWorkMonths;
    }

    /**
     * determine whether the requested leave csf amount of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested leave csf amount of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedCsfAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        KualiInteger csfAmount = appointmentFunding.getAppointmentRequestedCsfAmount();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (csfAmount == null) {
            return false;
        }

        // Requested csf amount must be greater than 0 if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            return csfAmount.isPositive();
        }

        return true;
    }

    /**
     * determine whether the requested leave csf time percent of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested leave csf time percent of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedCsfTimePercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BigDecimal csfTimePercent = appointmentFunding.getAppointmentRequestedCsfTimePercent();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (csfTimePercent == null) {
            return false;
        }

        // Requested csf amount must be greater than 0 if there is a leave
        if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
            return csfTimePercent.compareTo(BigDecimal.ZERO) > 0;
        }

        return true;
    }

    /**
     * determine whether the requested salary amount of the given appointment funding is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested salary amount of the given appointment funding is valid; otherwise, false
     */
    public static boolean isValidRequestedAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();

        return requestedAmount != null && !requestedAmount.isNegative();
    }

    /**
     * determine whether the requested FTE is valid
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the requested FTE is valid; otherwise, false
     */
    public static boolean isValidRequestedFteQuantity(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (requestedAmount == null || requestedFteQuantity == null) {
            return false;
        }

        if (requestedAmount.isPositive()) {
            return requestedFteQuantity.compareTo(BigDecimal.ZERO) > 0;
        }
        else if (requestedAmount.isZero()) {
            return requestedFteQuantity.compareTo(BigDecimal.ZERO) == 0;
        }

        return true;
    }
    
    /**
     * request salary amount must be zero for full year leave
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if request salary amount is zero for full year leave; otherwise, false
     */
    public static boolean isRequestedAmountZeroWhenFullYearLeave(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        // Request Salary Amount must be zero because these leave codes are for full year leave without pay.
        if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
            KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
            return requestedAmount == null || requestedAmount.isZero();
        }
        
        return true;
    }

    /**
     * request fte quantity must be zero for full year leave
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the request fte quantity is zero for full year leave; otherwise, false
     */
    public static boolean isRequestedFteQuantityZeroWhenFullYearLeave(PendingBudgetConstructionAppointmentFunding appointmentFunding) {        
        String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

        // Request Salary Amount must be zero because these leave codes are for full year leave without pay.
        if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
            BigDecimal requestedFteQuantity = appointmentFunding.getAppointmentRequestedFteQuantity();
            return requestedFteQuantity == null || requestedFteQuantity.compareTo(BigDecimal.ZERO) == 0;
        }
        
        return true;
    }

    /**
     * determine whether the given appointment funding is required to be detail salary funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the given appointment funding is required to be detail salary funding; otherwise, false
     */
    public static boolean isDetailSalaryFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String objectCode = appointmentFunding.getFinancialObjectCode();

        LaborLedgerObject laborObject = laborModuleService.retrieveLaborLedgerObject(fiscalYear, chartOfAccountsCode, objectCode);
        return laborObject.isDetailPositionRequiredIndicator();
    }

    /**
     * determine whether the object code of the given appointment funding matches the position default object code
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the object code of the given appointment funding matches the position default object code; otherwise, false
     */
    public static boolean isObjectCodeMatchingDefaultOfPosition(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String defaultObjectCode = appointmentFunding.getBudgetConstructionPosition().getIuDefaultObjectCode();
        String objectCode = appointmentFunding.getFinancialObjectCode();

        return StringUtils.equals(objectCode, defaultObjectCode);
    }

    /**
     * determine if there is an appointment funding in the given list that has the same key information as the specified appointment
     * funding
     * 
     * @param appointmentFundings the given appointment funding collection
     * @param appointmentFunding the given appointment funding
     * @return true if there is an appointment funding in the given list that has the same key information as the specified
     *         appointment funding; otherwise, false
     */
    public static boolean hasSameExistingLine(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        return salarySettingService.findAppointmentFunding(appointmentFundings, appointmentFunding) != null;
    }

    /**
     * determine whether the given appointment funding is associated with a valid active budgetable position
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the given appointment funding is associated with a valid active budgetable position; otherwise, false
     */
    public static boolean hasVaildBudgetablePosition(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();

        return budgetConstructionPositionService.isBudgetablePosition(position);
    }
}
