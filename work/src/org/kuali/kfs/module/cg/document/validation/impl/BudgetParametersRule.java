/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetFringeRate;
import org.kuali.kfs.module.cg.businessobject.BudgetGraduateAssistantRate;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.service.BudgetFringeRateService;
import org.kuali.kfs.module.cg.document.service.BudgetGraduateAssistantRateService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class BudgetParametersRule {
    private String MAXIMUM_PERIOD_LENGTH;
    private String PERIOD_IDENTIFIER;
    private String NEW_PERIOD_IDENTIFIER;

    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;
    private BudgetFringeRateService budgetFringeRateService;
    private BudgetGraduateAssistantRateService budgetGradAsstRateService;

    /**
     * 
     */
    protected BudgetParametersRule() {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        MAXIMUM_PERIOD_LENGTH = parameterService.getParameterValue(BudgetDocument.class, CGConstants.MAXIMUM_PERIOD_LENGTH);
        PERIOD_IDENTIFIER = parameterService.getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.PERIOD_IDENTIFIER);
        NEW_PERIOD_IDENTIFIER = parameterService.getParameterValue(BudgetDocument.class, CGConstants.NEW_PERIOD_IDENTIFIER);

        dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        budgetFringeRateService = SpringContext.getBean(BudgetFringeRateService.class);
        budgetGradAsstRateService = SpringContext.getBean(BudgetGraduateAssistantRateService.class);
    }

    protected boolean isParametersValid(BudgetDocument budgetDocument) {
        boolean valid = true;
        valid &= isProjectDirectorValid(budgetDocument);
        valid &= isAgencyValid(budgetDocument);
        valid &= isFedPassThroughAgencyValid(budgetDocument);
        valid &= isInflationRatesValid(budgetDocument);
        valid &= isPeriodListValid(budgetDocument.getBudget().getPeriods(), budgetDocument.getBudget().isAgencyModularIndicator(), true);
        valid &= isTaskListValid(budgetDocument.getBudget().getTasks());
        valid &= isFringeRateListValid(budgetDocument);
        valid &= isGraduateAssistantRateListValid(budgetDocument);
        return valid;
    }

    /**
     * Checks business rules related to adding a Period.
     * 
     * @param Document document
     * @return boolean True if the document is valid, false otherwise.
     */
    protected boolean processAddPeriodBusinessRules(Document document, BudgetPeriod budgetPeriod) {
        if (!(document instanceof BudgetDocument)) {
            return false;
        }

        boolean valid = true;

        BudgetDocument budgetDocument = (BudgetDocument) document;

        GlobalVariables.getErrorMap().addToErrorPath("document");

        valid &= isPeriodValid(budgetPeriod, NEW_PERIOD_IDENTIFIER, new Integer(0), false);

        List currentPeriods = budgetDocument.getBudget().getPeriods();
        if (currentPeriods != null && currentPeriods.size() > 0) {
            BudgetPeriod lastPeriod = (BudgetPeriod) currentPeriods.get(currentPeriods.size() - 1);
            valid &= isConsecutivePeriods(lastPeriod.getBudgetPeriodEndDate(), budgetPeriod.getBudgetPeriodBeginDate(), PERIOD_IDENTIFIER + " " + lastPeriod.getBudgetPeriodSequenceNumber().toString(), NEW_PERIOD_IDENTIFIER, "budget.period.notConsecutive_0");
        }

        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return valid;
    }

    protected boolean isInflationRatesValid(BudgetDocument budgetDocument) {
        boolean valid = true;
        KualiDecimal MAX_INFLATION_RATE = new KualiDecimal(SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.BUDGET_MAX_INFLATION_RATE_PARAMETER_NAME));

        if (budgetDocument.getBudget().getBudgetPersonnelInflationRate() != null && budgetDocument.getBudget().getBudgetPersonnelInflationRate().isGreaterThan(MAX_INFLATION_RATE)) {
            GlobalVariables.getErrorMap().putError("budget.budgetPersonnelInflationRate", CGKeyConstants.ERROR_INVALID_VALUE, new String[] { dataDictionaryService.getAttributeLabel(Budget.class, "budgetPersonnelInflationRate") });
            valid = false;
        }

        if (budgetDocument.getBudget().getBudgetNonpersonnelInflationRate() != null && budgetDocument.getBudget().getBudgetNonpersonnelInflationRate().isGreaterThan(MAX_INFLATION_RATE)) {
            GlobalVariables.getErrorMap().putError("budget.budgetNonpersonnelInflationRate", CGKeyConstants.ERROR_INVALID_VALUE, new String[] { dataDictionaryService.getAttributeLabel(Budget.class, "budgetNonpersonnelInflationRate") });
            valid = false;
        }

        return valid;
    }

    /**
     * Checks whether a list of periods is valid - consecutive, not too long or too short, and each individual period is valid.
     * 
     * @param List periods
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isPeriodListValid(List periods, boolean modularBudget, boolean validatePeriodDatesValid) {
        boolean valid = true;

        valid &= isNumPeriodsValid(periods, modularBudget);

        for (int i = 0; i < periods.size(); i++) {
            BudgetPeriod currentPeriod = (BudgetPeriod) periods.get(i);

            valid &= isPeriodValid(currentPeriod, PERIOD_IDENTIFIER + " " + currentPeriod.getBudgetPeriodSequenceNumber().toString(), currentPeriod.getBudgetPeriodSequenceNumber(), validatePeriodDatesValid);

            if (i > 0) {
                BudgetPeriod previousPeriod = (BudgetPeriod) periods.get(i - 1);
                valid &= isConsecutivePeriods(previousPeriod.getBudgetPeriodEndDate(), currentPeriod.getBudgetPeriodBeginDate(), PERIOD_IDENTIFIER + " " + previousPeriod.getBudgetPeriodSequenceNumber().toString(), PERIOD_IDENTIFIER + " " + currentPeriod.getBudgetPeriodSequenceNumber().toString(), "budget.period.notConsecutive_" + currentPeriod.getBudgetPeriodSequenceNumber().toString());
            }
        }
        return valid;
    }

    /**
     * Checks whether budgetDocument has a valid Project Director.
     * 
     * @param BudgetDocument budgetDocument
     * @return boolean True if the Project Director is valid, false otherwise.
     */
    protected boolean isProjectDirectorValid(BudgetDocument budgetDocument) {

        if (!StringUtils.isBlank(budgetDocument.getBudget().getBudgetProjectDirectorUniversalIdentifier()) || budgetDocument.getBudget().isProjectDirectorToBeNamedIndicator()) {
            return true;
        }

        GlobalVariables.getErrorMap().putError("budget.budgetProjectDirectorUniversalIdentifier", CGKeyConstants.ERROR_MISSING, new String[] { dataDictionaryService.getAttributeLabel(Budget.class, "budgetProjectDirectorUniversalIdentifier") });

        return false;
    }

    /**
     * Checks whether budgetDocument has a valid Agency.
     * 
     * @param BudgetDocument budgetDocument
     * @return boolean True if the Agency is valid, false otherwise.
     */
    protected boolean isAgencyValid(BudgetDocument budgetDocument) {

        boolean valid = true;
        
        if (budgetDocument.getBudget().isAgencyToBeNamedIndicator()) {
            return true;
        }
        
        budgetDocument.getBudget().refreshReferenceObject("budgetAgency");
        if (StringUtils.isBlank(budgetDocument.getBudget().getBudgetAgencyNumber()) || budgetDocument.getBudget().getBudgetAgency() ==  null) {
            valid = false;
            GlobalVariables.getErrorMap().putError("budget.budgetAgencyNumber", CGKeyConstants.ERROR_MISSING, new String[] { dataDictionaryService.getAttributeLabel(Budget.class, "budgetAgency") });
        }

        return valid;
    }

    /**
     * Checks whether budgetDocument has a valid Agency.
     * 
     * @param BudgetDocument budgetDocument
     * @return boolean True if the Agency is valid, false otherwise.
     */
    protected boolean isFedPassThroughAgencyValid(BudgetDocument budgetDocument) {

        boolean valid = true;
        
        
        budgetDocument.getBudget().refreshReferenceObject("federalPassThroughAgency");
        
        if (!StringUtils.isBlank(budgetDocument.getBudget().getFederalPassThroughAgencyNumber()) && budgetDocument.getBudget().getFederalPassThroughAgency() ==  null) {
            valid = false;
            GlobalVariables.getErrorMap().putError("budget.federalPassThroughAgencyNumber", CGKeyConstants.ERROR_INVALID_VALUE, new String[] { dataDictionaryService.getAttributeLabel(Budget.class, "federalPassThroughAgency") });
        }

        return valid;
    }
    
    /**
     * Checks whether budgetPeriod is valid - start & end date are in proper order, period is not too long.
     * 
     * @param BudgetPeriod budgetPeriod
     * @param String periodLabel The label identifying the period you want printed in the error message.
     * @param Integer periodNumber The budgetPeriod's sequence number.
     * @return boolean True if the budgetPeriod is valid, false otherwise.
     */
    protected boolean isPeriodValid(BudgetPeriod budgetPeriod, String periodLabel, Integer periodNumber, boolean validateDatesExist) {
        boolean valid = true;

        if (budgetPeriod.getBudgetPeriodBeginDate() != null && budgetPeriod.getBudgetPeriodEndDate() != null) {
            String MAXIMUM_PERIOD_LENGTH = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.MAXIMUM_PERIOD_LENGTH);

            valid &= isPeriodDatesOrderValid(budgetPeriod.getBudgetPeriodBeginDate(), budgetPeriod.getBudgetPeriodEndDate(), periodLabel, "budget.period.invalidOrder_" + periodNumber);

            if (valid) {
                valid &= isPeriodLengthValid(budgetPeriod.getBudgetPeriodBeginDate(), budgetPeriod.getBudgetPeriodEndDate(), CGConstants.maximumPeriodLengthUnits, Integer.parseInt(MAXIMUM_PERIOD_LENGTH), periodLabel, "budget.period.invalidLength_" + periodNumber);
            }
        } else if (validateDatesExist) {
            GlobalVariables.getErrorMap().putError("budget.period.invalidLength_" + periodNumber, CGKeyConstants.ERROR_PARAMETERS_DATES_MISSING, new String[] { periodLabel });
            valid = false;
        }

        return valid;
    }

    /**
     * Checks whether number of periods is valid - not too long or too short.
     * 
     * @param List periods
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isNumPeriodsValid(List periods, boolean modularBudget) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String MINIMUM_NUMBER_OF_PERIODS = parameterService.getParameterValue(BudgetDocument.class, CGConstants.MINIMUM_NUMBER_OF_PERIODS);
        String MAXIMUM_NUMBER_OF_PERIODS = parameterService.getParameterValue(BudgetDocument.class, CGConstants.MAXIMUM_NUMBER_OF_PERIODS);
        String MAXIMUM_NUMBER_MODULAR_PERIODS = parameterService.getParameterValue(BudgetDocument.class, CGConstants.MAXIMUM_NUMBER_MODULAR_PERIODS);

        if (periods.size() > Integer.parseInt(MAXIMUM_NUMBER_OF_PERIODS)) {
            GlobalVariables.getErrorMap().putError("budget.period.tooMany", CGKeyConstants.ERROR_TOO_MANY, new String[] { MAXIMUM_NUMBER_OF_PERIODS, "period" });
            return false;
        }
        else if (modularBudget) {
            if (periods.size() > Integer.parseInt(MAXIMUM_NUMBER_MODULAR_PERIODS)) {
                GlobalVariables.getErrorMap().putError("budget.period.modularTooMany", CGKeyConstants.ERROR_MODULAR_TOO_MANY, new String[] { MAXIMUM_NUMBER_MODULAR_PERIODS, "period" });
            }
        }

        if (periods.size() < Integer.parseInt(MINIMUM_NUMBER_OF_PERIODS)) {
            GlobalVariables.getErrorMap().putError("budget.period.notEnough", CGKeyConstants.ERROR_NOT_ENOUGH, new String[] { MINIMUM_NUMBER_OF_PERIODS, "period" });
            return false;
        }

        return true;
    }

    /**
     * Checks whether end date of one period & start date of next are consecutive.
     * 
     * @param Date formerPeriodEndDate
     * @param Date latterPeriodStartDate
     * @param String formerPeriodIdentifier
     * @param String latterPeriodIdentifier
     * @param String errorKey
     * @return boolean True if the dates are consecutive, false otherwise.
     */
    protected boolean isConsecutivePeriods(Date formerPeriodEndDate, Date latterPeriodStartDate, String formerPeriodIdentifier, String latterPeriodIdentifier, String errorKey) {

        if (formerPeriodEndDate == null || latterPeriodStartDate == null) {
            return true;
        }

        GregorianCalendar formerPeriodEndPlusOneDay = new GregorianCalendar();
        formerPeriodEndPlusOneDay.setTime(formerPeriodEndDate);
        formerPeriodEndPlusOneDay.add(Calendar.DATE, 1);

        GregorianCalendar latterPeriodStart = new GregorianCalendar();
        latterPeriodStart.setTime(latterPeriodStartDate);

        if (!formerPeriodEndPlusOneDay.equals(latterPeriodStart)) {
            GlobalVariables.getErrorMap().putError(errorKey, CGKeyConstants.ERROR_NONCONSECUTIVE, new String[] { formerPeriodIdentifier, latterPeriodIdentifier });
            return false;
        }

        return true;
    }

    /**
     * Checks whether startDate is before or equal to endDate.
     * 
     * @param Date startDate
     * @param Date endDate
     * @param String periodIdentifier
     * @param String errorKey
     * @return boolean True if the order is valid, false otherwise.
     */
    protected boolean isPeriodDatesOrderValid(Date startDate, Date endDate, String periodIdentifier, String errorKey) {
        if (startDate != null && endDate != null && startDate.compareTo(endDate) > 0) {
            GlobalVariables.getErrorMap().putError(errorKey, CGKeyConstants.ERROR_INVALID_ORDERING, new String[] { periodIdentifier });
            return false;
        }
        return true;
    }

    /**
     * Checks whether time period between startDate and endDate is within acceptable limit.
     * 
     * @param Date startDate
     * @param Date endDate
     * @param int validPeriodLengthUnits
     * @param int validPeriodLength
     * @param String periodIdentifier
     * @param String errorKey
     * @return boolean True if the length is valid, false otherwise.
     */
    protected boolean isPeriodLengthValid(Date startDate, Date endDate, int validPeriodLengthUnits, int validPeriodLength, String periodIdentifier, String errorKey) {
        try {
            GregorianCalendar startDatePlusMaximumPeriodLength = new GregorianCalendar();
            startDatePlusMaximumPeriodLength.setTime(startDate);
            startDatePlusMaximumPeriodLength.add(validPeriodLengthUnits, validPeriodLength);

            GregorianCalendar endCal = new GregorianCalendar();
            endCal.setTime(endDate);

            if (!endCal.before(startDatePlusMaximumPeriodLength)) {
                GlobalVariables.getErrorMap().putError(errorKey, CGKeyConstants.ERROR_INVALID_PERIOD_LENGTH, new String[] { periodIdentifier });
                return false;
            }

            return true;
        }
        catch (NullPointerException e) {
            return true;
        }
    }

    /**
     * Checks whether budgetTaskList is valid - not too big or small.
     * 
     * @param List budgetTaskList
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isTaskListValid(List budgetTaskList) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String MINIMUM_NUMBER_OF_TASKS = parameterService.getParameterValue(BudgetDocument.class, CGConstants.MINIMUM_NUMBER_OF_TASKS);
        String MAXIMUM_NUMBER_OF_TASKS = parameterService.getParameterValue(BudgetDocument.class, CGConstants.MAXIMUM_NUMBER_OF_TASKS);

        if (budgetTaskList.size() > Integer.parseInt(MAXIMUM_NUMBER_OF_TASKS)) {
            String[] tooManyTasksError = new String[] { MAXIMUM_NUMBER_OF_TASKS, "task" };
            GlobalVariables.getErrorMap().putError("budget.tasks.tooMany", CGKeyConstants.ERROR_TOO_MANY, tooManyTasksError);
            return false;
        }

        if (budgetTaskList.size() < Integer.parseInt(MINIMUM_NUMBER_OF_TASKS)) {
            String[] notEnoughTasksError = new String[] { MINIMUM_NUMBER_OF_TASKS, "task" };
            GlobalVariables.getErrorMap().putError("budget.tasks.notEnough", CGKeyConstants.ERROR_NOT_ENOUGH, notEnoughTasksError);
            return false;
        }
        return true;
    }

    /**
     * Checks whether fringeRateList is valid.
     * 
     * @param Document budgetDocument
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isFringeRateListValid(BudgetDocument budgetDocument) {
        List<BudgetFringeRate> fringeRateList = budgetDocument.getBudget().getFringeRates();
        boolean valid = true;
        boolean isRateChanged = false;
        KualiDecimal maximumRate = new KualiDecimal(100);
        int i = 0;
        for (BudgetFringeRate budgetFringeRate : fringeRateList) {
            BudgetFringeRate currentDatabaseFringe = budgetFringeRateService.getBudgetFringeRate(budgetFringeRate.getDocumentNumber(), budgetFringeRate.getInstitutionAppointmentTypeCode());

            boolean currentRateNotEqualSystemRate = false;

            // extract the fringe rate from the budgetFringeRate object
            KualiDecimal contractsAndGrantsFringeRate = budgetFringeRate.getContractsAndGrantsFringeRateAmount();

            // extract the institution cost share object from the budgetFringeRate object
            KualiDecimal institutionCostShare = budgetFringeRate.getInstitutionCostShareFringeRateAmount();

            // check to see if the system value is different than the user input value
            if ((contractsAndGrantsFringeRate != null && budgetFringeRate.getAppointmentTypeFringeRateAmount().compareTo(contractsAndGrantsFringeRate) != 0) || (institutionCostShare != null && budgetFringeRate.getAppointmentTypeCostShareFringeRateAmount().compareTo(institutionCostShare) != 0)) {
                currentRateNotEqualSystemRate = true;
            }

            // if the current rate is different than the system rate, check to see which one was changed last. if the system rate
            // was the last one to change, no justification is required.
            isRateChanged |= currentRateNotEqualSystemRate && // the rates are different
                    (budgetFringeRate.getBudgetLastUpdateTimestamp() == null || // hasn't been saved yet
                    (currentDatabaseFringe.getBudgetLastUpdateTimestamp().after(currentDatabaseFringe.getAppointmentType().getLastUpdate()) || // budget
                    // rate
                    // updated
                    // last
                    // (newer
                    // than
                    // system
                    // rate)
                    (!budgetFringeRate.getInstitutionCostShareFringeRateAmount().equals(currentDatabaseFringe.getInstitutionCostShareFringeRateAmount()) || // one
                    // of
                    // the
                    // reates
                    // has
                    // changed
                    // since
                    // last
                    // save
                    !budgetFringeRate.getContractsAndGrantsFringeRateAmount().equals(currentDatabaseFringe.getContractsAndGrantsFringeRateAmount()))));

            // check whether rates are within valid range
            if (budgetFringeRate.getContractsAndGrantsFringeRateAmount().isGreaterThan(maximumRate)) {
                valid = false;
                GlobalVariables.getErrorMap().putError("budget.fringeRate[" + i + "].contractsAndGrantsFringeRateAmount", CGKeyConstants.ERROR_FRINGE_RATE_TOO_LARGE);
            }

            if (budgetFringeRate.getInstitutionCostShareFringeRateAmount().isGreaterThan(maximumRate)) {
                valid = false;
                GlobalVariables.getErrorMap().putError("budget.fringeRate[" + i + "].institutionCostShareFringeRateAmount", CGKeyConstants.ERROR_FRINGE_RATE_TOO_LARGE);
            }

            i++;
        }

        // get the Rate Change Justification
        String fringeRateChange = budgetDocument.getBudget().getBudgetFringeRateDescription();
        // if there is a rate change and the justification note is not filled in then display an error
        if (isRateChanged && fringeRateChange == null) {
            GlobalVariables.getErrorMap().putError("budget.fringeRate", CGKeyConstants.ERROR_FRINGE_RATE_CHANGE_JUSTIFICATION_REQUIRED, new String[] {});
            valid = false;
        }
        return valid;
    }


    /**
     * Checks whether graduateAssistantRateList is valid - not too big or small.
     * 
     * @param BudgetDocument budgetDocument
     * @return boolean True if the list is valid, false otherwise.
     */
    protected boolean isGraduateAssistantRateListValid(BudgetDocument budgetDocument) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        List graduateAssistantRateList = budgetDocument.getBudget().getGraduateAssistantRates();
        int numberOfAcademicYearSubdivisions = Integer.parseInt(parameterService.getParameterValue(BudgetDocument.class, CGConstants.KRA_BUDGET_NUMBER_OF_ACADEMIC_YEAR_SUBDIVISIONS));
        String[] academicYearSubdivisionNames = null;
        boolean rateChanged = false;
        boolean valid = true;

        for (int i = 0; i < graduateAssistantRateList.size(); i++) {
            // get the current graduate rate object from the list collection
            BudgetGraduateAssistantRate budgetGraduateAssistantRate = (BudgetGraduateAssistantRate) graduateAssistantRateList.get(i);

            BudgetGraduateAssistantRate currentDatabaseGradRate = budgetGradAsstRateService.getBudgetGraduateAssistantRate(budgetGraduateAssistantRate.getDocumentNumber(), budgetGraduateAssistantRate.getCampusCode());


            for (int anAcademicYearSubdivisionIndex = 1; anAcademicYearSubdivisionIndex <= numberOfAcademicYearSubdivisions; anAcademicYearSubdivisionIndex++) {
                KualiDecimal rateForTesting = budgetGraduateAssistantRate.getCampusMaximumPeriodRate(anAcademicYearSubdivisionIndex);
                KualiDecimal systemRateForComparison = budgetGraduateAssistantRate.getGraduateAssistantRate().getCampusMaximumPeriodRate(anAcademicYearSubdivisionIndex);

                if (rateForTesting != null) {
                    if (!SpringContext.getBean(BudgetGraduateAssistantRateService.class).isValidGraduateAssistantRate(rateForTesting)) {
                        if (academicYearSubdivisionNames == null) {
                            academicYearSubdivisionNames = parameterService.getParameterValues(BudgetDocument.class, CGConstants.KRA_BUDGET_ACADEMIC_YEAR_SUBDIVISION_NAMES).toArray(new String[] {});
                        }
                        String[] graduateAssistantRateErrorMessage = { academicYearSubdivisionNames[anAcademicYearSubdivisionIndex - 1], budgetGraduateAssistantRate.getCampusCode() };
                        GlobalVariables.getErrorMap().putError("budget.graduateAssistantRate[" + i + "].campusMaximumPeriod" + anAcademicYearSubdivisionIndex + "Rate", CGKeyConstants.ERROR_GRAD_RATE_TOO_HIGH, graduateAssistantRateErrorMessage);
                        valid = false;
                    }


                    if (systemRateForComparison != null) {
                        rateChanged |= rateForTesting.compareTo(systemRateForComparison) != 0 && // the rates are different
                                (budgetGraduateAssistantRate.getLastUpdateTimestamp() == null || // hasn't been saved yet
                                (currentDatabaseGradRate.getLastUpdateTimestamp().after(currentDatabaseGradRate.getGraduateAssistantRate().getLastUpdateTimestamp()) || // budget
                                // rate
                                // updated
                                // last
                                // (newer
                                // than
                                // system
                                // rate)
                                !budgetGraduateAssistantRate.equals(currentDatabaseGradRate)));
                    }
                }
                else {
                    if (academicYearSubdivisionNames == null)
                        academicYearSubdivisionNames = parameterService.getParameterValues(BudgetDocument.class, CGConstants.KRA_BUDGET_ACADEMIC_YEAR_SUBDIVISION_NAMES).toArray(new String[] {});
                    String[] graduateAssistantRateErrorMessage = { budgetGraduateAssistantRate.getCampusCode() + " " + academicYearSubdivisionNames[anAcademicYearSubdivisionIndex - 1] + " Current Rate" };
                    GlobalVariables.getErrorMap().putError("budget.graduateAssistantRate[" + i + "].campusMaximumPeriod" + anAcademicYearSubdivisionIndex + "Rate", KFSKeyConstants.ERROR_REQUIRED, graduateAssistantRateErrorMessage);
                    valid = false;
                }
            }
        }

        // get the Rate Change Justification
        String gradFringeRateChange = budgetDocument.getBudget().getBudgetFringeRateDescription();
        // if there is a rate change and the justification note is not filled in then display an error
        if (rateChanged && gradFringeRateChange == null) {
            GlobalVariables.getErrorMap().putError("budget.grad", CGKeyConstants.ERROR_GRAD_RATE_CHANGE_JUSTIFICATION_REQUIRED, new String[] {});
            valid = false;
        }
        return valid;
    }
}
