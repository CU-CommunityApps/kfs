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
package org.kuali.kfs.module.bc.document.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.integration.businessobject.LaborLedgerObject;
import org.kuali.kfs.integration.service.LaborModuleService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAppointmentFundingReason;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.module.bc.util.SalarySettingCalculator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * implements the service methods defined in the SalarySettingService
 * 
 * @see org.kuali.kfs.module.bc.document.service.SalarySettingService
 */
@Transactional
public class SalarySettingServiceImpl implements SalarySettingService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingServiceImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private LaborModuleService laborModuleService;
    private BudgetDocumentService budgetDocumentService;
    private BenefitsCalculationService benefitsCalculationService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#getDisabled()
     */
    public boolean isSalarySettingDisabled() {
        // TODO for now just return false, implement application parameter if decision is made implement this functionality
        return false;

        // return kualiConfigurationService.getApplicationParameterIndicator(KFSConstants.ParameterGroups.SYSTEM,
        // BCConstants.DISABLE_SALARY_SETTING_FLAG);

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateHourlyPayRate(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public BigDecimal calculateHourlyPayRate(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("calculateHourlyPayRate() start");

        KualiInteger requestedAmount = appointmentFunding.getAppointmentRequestedAmount();
        BigDecimal fteQuantity = this.calculateFteQuantityFromAppointmentFunding(appointmentFunding);

        BigDecimal annualWorkingHours = BigDecimal.valueOf(BudgetParameterFinder.getAnnualWorkingHours());
        BigDecimal totalPayHoursForYear = fteQuantity.multiply(annualWorkingHours);
        BigDecimal hourlyPayRate = requestedAmount.divide(totalPayHoursForYear).setScale(2, BigDecimal.ROUND_HALF_EVEN);

        return hourlyPayRate;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateAnnualPayAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public KualiInteger calculateAnnualPayAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("calculateAnnualPayAmount() start");

        BigDecimal hourlyPayRate = appointmentFunding.getAppointmentRequestedPayRate();
        BigDecimal fteQuantity = this.calculateFteQuantityFromAppointmentFunding(appointmentFunding);

        BigDecimal annualWorkingHours = BigDecimal.valueOf(BudgetParameterFinder.getAnnualWorkingHours());
        BigDecimal totalPayHoursForYear = fteQuantity.multiply(annualWorkingHours);
        KualiInteger annualPayAmount = new KualiInteger(hourlyPayRate.multiply(totalPayHoursForYear));

        return annualPayAmount;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#normalizePayRateAndAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void normalizePayRateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("normalizePayRateAndAmount() start");

        KualiInteger currentAnnualPayAmount = appointmentFunding.getAppointmentRequestedAmount();
        if (currentAnnualPayAmount != null && currentAnnualPayAmount.isNonZero()) {
            BigDecimal hourlyPayRate = this.calculateHourlyPayRate(appointmentFunding);
            appointmentFunding.setAppointmentRequestedPayRate(hourlyPayRate);
        }

        BigDecimal currentHourlyPayRate = appointmentFunding.getAppointmentRequestedPayRate();
        if (currentHourlyPayRate != null) {
            KualiInteger annualPayAmount = this.calculateAnnualPayAmount(appointmentFunding);
            appointmentFunding.setAppointmentRequestedAmount(annualPayAmount);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateFteQuantityForAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public BigDecimal calculateFteQuantityFromAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("calculateFteQuantity() start");

        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION);
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
        if (position == null) {
            return null;
        }

        Integer payMonth = position.getIuPayMonths();
        Integer fundingMonth = appointmentFunding.getAppointmentFundingMonth();
        BigDecimal requestedTimePercent = appointmentFunding.getAppointmentRequestedTimePercent();

        return this.calculateFteQuantity(payMonth, fundingMonth, requestedTimePercent);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#calculateFteQuantity(java.lang.Integer, java.lang.Integer,
     *      java.math.BigDecimal)
     */
    public BigDecimal calculateFteQuantity(Integer payMonth, Integer fundingMonth, BigDecimal requestedTimePercent) {
        LOG.debug("calculateFteQuantity() start");

        if (payMonth == null || fundingMonth == null || requestedTimePercent == null) {
            return null;
        }

        BigDecimal payMonthAsDecimal = BigDecimal.valueOf(payMonth);
        BigDecimal fundingMonthAsDecimal = BigDecimal.valueOf(fundingMonth);
        BigDecimal fundingMonthPercent = fundingMonthAsDecimal.divide(payMonthAsDecimal, 4, BigDecimal.ROUND_HALF_EVEN);

        return requestedTimePercent.multiply(fundingMonthPercent).divide(KFSConstants.ONE_HUNDRED.bigDecimalValue());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#isHourlyPaid(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    public boolean isHourlyPaid(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        LOG.debug("isHourlyPaid() start");

        Integer fiscalYear = pendingBudgetConstructionGeneralLedger.getUniversityFiscalYear();
        String chartOfAccountsCode = pendingBudgetConstructionGeneralLedger.getChartOfAccountsCode();
        String objectCode = pendingBudgetConstructionGeneralLedger.getFinancialObjectCode();

        return this.isHourlyPaidObject(fiscalYear, chartOfAccountsCode, objectCode);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#isHourlyPaid(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    public boolean isHourlyPaidObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        LOG.debug("isHourlyPaid() start");

        LaborLedgerObject laborLedgerObject = laborModuleService.retrieveLaborLedgerObject(fiscalYear, chartOfAccountsCode, objectCode);

        if (laborLedgerObject == null) {
            return false;
        }

        return BudgetParameterFinder.getBiweeklyPayTypeCodes().contains(laborLedgerObject.getFinancialObjectPayTypeCode());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#canBeVacant(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean canBeVacant(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("canBeVacant(List, PendingBudgetConstructionAppointmentFunding) start");

        if (!this.canBeVacant(appointmentFunding)) {
            return false;
        }

        return this.findVacantAppointmentFunding(appointmentFundings, appointmentFunding) == null;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#canBeVacant(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public PendingBudgetConstructionAppointmentFunding findVacantAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("findVacantAppointmentFunding() start");

        Map<String, Object> keyFieldValues = appointmentFunding.getValuesMap();
        List<String> keyFields = new ArrayList<String>();
        keyFields.addAll(keyFieldValues.keySet());

        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = this.createVacantAppointmentFunding(appointmentFunding);

        // determine whether there is vacant for the given appointment funding in its list
        for (PendingBudgetConstructionAppointmentFunding fundingLine : appointmentFundings) {
            if (ObjectUtil.equals(fundingLine, vacantAppointmentFunding, keyFields)) {
                return fundingLine;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#canBeVacant(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public boolean canBeVacant(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("canBeVacant() start");

        if (appointmentFunding.isNewLineIndicator()) {
            return false;
        }

        // the given funding line has not been deleted
        if (appointmentFunding.isAppointmentFundingDeleteIndicator()) {
            return false;
        }

        // the given funding line cannot be a vacant line
        String emplid = appointmentFunding.getEmplid();
        if (BCConstants.VACANT_EMPLID.equals(emplid)) {
            return false;
        }

        // check if the associated position is valid and active
        BudgetConstructionPosition position = appointmentFunding.getBudgetConstructionPosition();
        if (position == null || !position.isBudgetedPosition() || !position.isEffective()) {
            return false;
        }

        // check if there is an existing vacant appintment funcding for the given funding line
        boolean hasBeenVacated = this.hasBeenVacated(appointmentFunding);
        if (hasBeenVacated) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#vacateAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public PendingBudgetConstructionAppointmentFunding vacateAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("vacateAppointmentFunding() start");

        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = this.createVacantAppointmentFunding(appointmentFunding);
        this.markAsDelete(appointmentFunding);

        return vacantAppointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#vacateAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public PendingBudgetConstructionAppointmentFunding vacateAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = this.vacateAppointmentFunding(appointmentFunding);

        if (vacantAppointmentFunding != null) {
            appointmentFundings.add(vacantAppointmentFunding);
        }

        return vacantAppointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#purgeAppointmentFunding(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void purgeAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        this.preprocessFundingReason(appointmentFunding);

        businessObjectService.delete(appointmentFunding);
        appointmentFundings.remove(appointmentFunding);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#adjustRequestedSalaryByAmount(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void adjustRequestedSalaryByAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("adjustRequestedSalaryByAmount() start");

        int inputAdjustmentAmount = appointmentFunding.getAdjustmentAmount().intValue();

        KualiInteger adjustmentAmount = new KualiInteger(inputAdjustmentAmount);
        KualiInteger csfAmount = this.getCsfAmount(appointmentFunding);
        KualiInteger appointmentRequestedAmount = csfAmount.add(adjustmentAmount);

        appointmentFunding.setAppointmentRequestedAmount(appointmentRequestedAmount);

        if (appointmentFunding.isHourlyPaid()) {
            this.normalizePayRateAndAmount(appointmentFunding);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#adjustRequestedSalaryByPercent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void adjustRequestedSalaryByPercent(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        LOG.debug("adjustRequestedSalaryByPercent() start");

        KualiInteger csfAmount = this.getCsfAmount(appointmentFunding);

        if (csfAmount.isNonZero()) {
            KualiDecimal percent = appointmentFunding.getAdjustmentAmount();
            BigDecimal adjustedAmount = csfAmount.multiply(percent).divide(KFSConstants.ONE_HUNDRED);

            KualiInteger appointmentRequestedAmount = new KualiInteger(adjustedAmount).add(csfAmount);
            appointmentFunding.setAppointmentRequestedAmount(appointmentRequestedAmount);
        }

        if (appointmentFunding.isHourlyPaid()) {
            this.normalizePayRateAndAmount(appointmentFunding);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#saveSalarySetting(org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion)
     */
    public void saveSalarySetting(SalarySettingExpansion salarySettingExpansion) {
        LOG.debug("saveSalarySetting() start");

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        this.resetDeletedFundingLines(appointmentFundings);

        KualiInteger requestedAmountTotal = SalarySettingCalculator.getAppointmentRequestedAmountTotal(appointmentFundings);
        KualiInteger changes = KualiInteger.ZERO;

        if (requestedAmountTotal != null) {
            KualiInteger annualBalanceAmount = salarySettingExpansion.getAccountLineAnnualBalanceAmount();
            changes = (annualBalanceAmount != null) ? requestedAmountTotal.subtract(annualBalanceAmount) : requestedAmountTotal;
        }

        salarySettingExpansion.setAccountLineAnnualBalanceAmount(requestedAmountTotal);
        businessObjectService.save(salarySettingExpansion);

        if (changes.isNonZero()) {
            List<BudgetConstructionMonthly> budgetConstructionMonthly = this.updateMonthlyAmounts(salarySettingExpansion, changes);
            businessObjectService.save(budgetConstructionMonthly);
        }
        
        // recalculate the benefit
        String documentNumber = salarySettingExpansion.getDocumentNumber();
        Integer fiscalYear = salarySettingExpansion.getUniversityFiscalYear();
        String chartOfAccounts = salarySettingExpansion.getChartOfAccountsCode();
        String accountNumber = salarySettingExpansion.getAccountNumber();
        String subAccountNumber = salarySettingExpansion.getSubAccountNumber();        
        benefitsCalculationService.calculateAllBudgetConstructionGeneralLedgerBenefits(documentNumber, fiscalYear, chartOfAccounts, accountNumber, subAccountNumber);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#saveSalarySetting(org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion)
     */
    public void saveAppointmentFundings(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        LOG.debug("saveAppointmentFundings() start");

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            this.preprocessFundingReason(appointmentFunding);

            this.preprocessLeaveRequest(appointmentFunding);
        }

        businessObjectService.save(appointmentFundings);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#updateSalarySettingExpansion(org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion)
     */
    @SuppressWarnings("deprecation")
    public void updateSalarySettingExpansion(SalarySettingExpansion salarySettingExpansion) {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();

        KualiInteger requestedAmountTotal = SalarySettingCalculator.getAppointmentRequestedAmountTotal(appointmentFundings);
        KualiInteger changes = KualiInteger.ZERO;

        if (requestedAmountTotal != null) {
            KualiInteger annualBalanceAmount = salarySettingExpansion.getAccountLineAnnualBalanceAmount();
            changes = (annualBalanceAmount != null) ? annualBalanceAmount.subtract(requestedAmountTotal) : requestedAmountTotal;
        }

        salarySettingExpansion.setAccountLineAnnualBalanceAmount(requestedAmountTotal);

        if (changes.isNonZero()) {
            salarySettingExpansion.setBudgetConstructionMonthly(this.updateMonthlyAmounts(salarySettingExpansion, changes));
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#retriveSalarySalarySettingExpansion(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public SalarySettingExpansion retriveSalarySalarySettingExpansion(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BudgetConstructionHeader budgetDocument = this.getBudgetConstructionHeader(appointmentFunding);

        Map<String, Object> fieldValues = ObjectUtil.buildPropertyMap(appointmentFunding, SalarySettingExpansion.getPrimaryKeyFields());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetDocument.getDocumentNumber());

        return (SalarySettingExpansion) businessObjectService.findByPrimaryKey(SalarySettingExpansion.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#resetAppointmentFunding(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void resetAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.setAppointmentRequestedAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentRequestedTimePercent(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedPayRate(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedFteQuantity(BigDecimal.ZERO);

        appointmentFunding.setAppointmentRequestedCsfAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentRequestedCsfFteQuantity(BigDecimal.ZERO);
        appointmentFunding.setAppointmentRequestedCsfTimePercent(BigDecimal.ZERO);

        appointmentFunding.setAppointmentTotalIntendedAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentTotalIntendedFteQuantity(BigDecimal.ZERO);

        appointmentFunding.setAppointmentFundingDurationCode(BCConstants.APPOINTMENT_FUNDING_DURATION_DEFAULT);

        appointmentFunding.setPositionObjectChangeIndicator(false);
        appointmentFunding.setPositionSalaryChangeIndicator(false);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#markAsDelete(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void markAsDelete(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        this.resetAppointmentFunding(appointmentFunding);

        appointmentFunding.setAppointmentFundingDeleteIndicator(true);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.SalarySettingService#revert(java.util.List,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    public void revert(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        PendingBudgetConstructionAppointmentFunding vacantFunding = this.findVacantAppointmentFunding(appointmentFundings, appointmentFunding);

        if (vacantFunding != null) {
            appointmentFundings.remove(vacantFunding);
        }

        PendingBudgetConstructionAppointmentFunding newAppointmentFunding = (PendingBudgetConstructionAppointmentFunding) businessObjectService.retrieve(appointmentFunding);
        appointmentFundings.add(newAppointmentFunding);
        appointmentFundings.remove(appointmentFunding);
    }

    /**
     * get the budget document with the information provided by the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the budget document with the information provided by the given appointment funding
     */
    private BudgetConstructionHeader getBudgetConstructionHeader(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();

        return budgetDocumentService.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
    }

    /**
     * reset the amount values of each line in the given appointment fundings as zeros and remove the reason annotations if the line
     * is marked as deleted
     * 
     * @param pendingBudgetConstructionAppointmentFunding the given appointment fundings
     */
    private void resetDeletedFundingLines(List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding) {
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : pendingBudgetConstructionAppointmentFunding) {
            if (!appointmentFunding.isAppointmentFundingDeleteIndicator() || appointmentFunding.isPersistedDeleteIndicator()) {
                continue;
            }

            this.markAsDelete(appointmentFunding);
            List<BudgetConstructionAppointmentFundingReason> reasons = appointmentFunding.getBudgetConstructionAppointmentFundingReason();
            if (reasons != null) {
                reasons.clear();
            }
        }
    }

    /**
     * adjust existing monthly request amounts using an even spread of the changes
     * 
     * @param pendingBudgetConstructionGeneralLedger the given pending Budget Construction General Ledger record
     * @param changes the given changes that will be used to adjust monthly request amounts using an even spread
     */
    private List<BudgetConstructionMonthly> updateMonthlyAmounts(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, KualiInteger changes) {
        BigInteger countOfMonth = BigInteger.valueOf(BCConstants.BC_MONTHLY_AMOUNTS.size());
        BigInteger[] distribution = changes.bigIntegerValue().divideAndRemainder(countOfMonth);

        KualiInteger monthlyAdjustment = new KualiInteger(distribution[0]);
        int remainder = distribution[1].intValue();

        Map<String, Object> keyMap = pendingBudgetConstructionGeneralLedger.buildPrimaryKeyMap();
        Collection<BudgetConstructionMonthly> monthlyGLRecordsCollection = businessObjectService.findMatching(BudgetConstructionMonthly.class, keyMap);

        List<BudgetConstructionMonthly> monthlyGLRecords = new ArrayList<BudgetConstructionMonthly>();
        for (BudgetConstructionMonthly monthlyGLRecord : monthlyGLRecordsCollection) {
            this.updateMonthlyAmounts(monthlyGLRecord, monthlyAdjustment, remainder);
            monthlyGLRecords.add(monthlyGLRecord);
        }

        return monthlyGLRecords;
    }

    /**
     * update the monthly amounts of the given monthly GL record with the given amount
     * 
     * @param monthlyGLRecord the given montly GL record that will be updated
     * @param adjustment the amount used to adjust the monthly amounts
     * @param remainder the sulpus that can be used to adjust the monthly amounts util it is used up
     */
    private void updateMonthlyAmounts(BudgetConstructionMonthly monthlyGLRecord, KualiInteger adjustment, int remainder) {
        int indexOfMonth = 1;
        for (String[] monthlyAmountFieldPair : BCConstants.BC_MONTHLY_AMOUNTS) {
            String monthlyAmountField = monthlyAmountFieldPair[0];

            if (!PropertyUtils.isReadable(monthlyGLRecord, monthlyAmountField) || !PropertyUtils.isWriteable(monthlyGLRecord, monthlyAmountField)) {
                continue;
            }

            try {
                KualiInteger currentMonthlyAmount = (KualiInteger) PropertyUtils.getProperty(monthlyGLRecord, monthlyAmountField);
                KualiInteger newMonthlyAmount = this.getNewMonthlyAmount(currentMonthlyAmount, adjustment, indexOfMonth++, remainder);

                PropertyUtils.setProperty(monthlyGLRecord, monthlyAmountField, newMonthlyAmount);
            }
            catch (Exception e) {
                LOG.fatal("Cannot update the monthly amount." + e);
                throw new RuntimeException("Cannot update the monthly amount." + e);
            }
        }
    }

    /**
     * calculate the new monthly amount based on the given information
     * 
     * @param currentMonthlyAmount the current monthly amount
     * @param adjustment the given adjustment that will be added into currentLineAmount
     * @param indexOfMonth the traversing index of the current month, which is used to determine whether current monthly amount can
     *        get additional adjustment from surplus amount
     * @param remainder the sulpus that can be used to adjust the monthly amounts util it is used up. If the surplus is greater than
     *        indexOfMonth, the monthly amount can increase by 1.
     * @return the new monthly amount calculated from the given information
     */
    private KualiInteger getNewMonthlyAmount(KualiInteger currentMonthlyAmount, KualiInteger adjustment, int indexOfMonth, int remainder) {
        KualiInteger newMonthlyAmount = currentMonthlyAmount.add(adjustment);

        return remainder < indexOfMonth ? newMonthlyAmount : newMonthlyAmount.add(KFSConstants.ONE);
    }

    /**
     * get the csf tracker amount of the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return the csf tracker amount of the given appointment funding if any; otherwise, return zero
     */
    private KualiInteger getCsfAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (appointmentFunding == null) {
            return KualiInteger.ZERO;
        }

        BudgetConstructionCalculatedSalaryFoundationTracker csfTracker = appointmentFunding.getEffectiveCSFTracker();
        if (csfTracker == null) {
            return KualiInteger.ZERO;
        }

        return csfTracker.getCsfAmount();
    }

    /**
     * determine whether there exists at lease one vacant funding line for the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if there exists at lease one vacant funding line for the given appointment funding; otherwise, return false
     */
    private boolean hasBeenVacated(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        Map<String, Object> keyFieldValues = appointmentFunding.getValuesMap();
        keyFieldValues.put(KFSPropertyConstants.EMPLID, BCConstants.VACANT_EMPLID);

        return businessObjectService.countMatching(PendingBudgetConstructionAppointmentFunding.class, keyFieldValues) > 0;
    }

    /**
     * create a vacant appointment funding based on the given budget funding
     * 
     * @param appointmentFunding the given appointment funding
     * @return a vacant appointment funding
     */
    private PendingBudgetConstructionAppointmentFunding createVacantAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = new PendingBudgetConstructionAppointmentFunding();

        ObjectUtil.buildObjectWithoutReferenceFields(vacantAppointmentFunding, appointmentFunding);
        vacantAppointmentFunding.setEmplid(BCConstants.VACANT_EMPLID);
        vacantAppointmentFunding.setAppointmentFundingDeleteIndicator(false);
        vacantAppointmentFunding.setPersistedDeleteIndicator(false);

        return vacantAppointmentFunding;
    }

    /**
     * preprocess the funding reason of the given appointment funding before the funding is saved
     * 
     * @param appointmentFunding the given appointment funding
     */
    private void preprocessFundingReason(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        businessObjectService.deleteMatching(BudgetConstructionAppointmentFundingReason.class, appointmentFunding.getValuesMap());

        List<BudgetConstructionAppointmentFundingReason> fundingReasons = appointmentFunding.getBudgetConstructionAppointmentFundingReason();
        if (StringUtils.isBlank(fundingReasons.get(0).getAppointmentFundingReasonCode())) {
            fundingReasons.clear();
        }
    }

    /**
     * preprocess the leave request of the given appointment funding before the funding is saved
     * 
     * @param appointmentFunding the given appointment funding
     */
    private void preprocessLeaveRequest(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String durationCode = appointmentFunding.getAppointmentFundingDurationCode();

        if (StringUtils.isEmpty(durationCode) || StringUtils.equals(durationCode, BCConstants.APPOINTMENT_FUNDING_DURATION_DEFAULT)) {
            appointmentFunding.setAppointmentRequestedCsfAmount(KualiInteger.ZERO);
            appointmentFunding.setAppointmentRequestedCsfFteQuantity(BigDecimal.ZERO);
            appointmentFunding.setAppointmentRequestedCsfTimePercent(BigDecimal.ZERO);
        }
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
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
     * Sets the laborModuleService attribute value.
     * 
     * @param laborModuleService The laborModuleService to set.
     */
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }

    /**
     * Sets the budgetDocumentService attribute value.
     * 
     * @param budgetDocumentService The budgetDocumentService to set.
     */
    public void setBudgetDocumentService(BudgetDocumentService budgetDocumentService) {
        this.budgetDocumentService = budgetDocumentService;
    }

    /**
     * Sets the benefitsCalculationService attribute value.
     * 
     * @param benefitsCalculationService The benefitsCalculationService to set.
     */
    public void setBenefitsCalculationService(BenefitsCalculationService benefitsCalculationService) {
        this.benefitsCalculationService = benefitsCalculationService;
    }
}
