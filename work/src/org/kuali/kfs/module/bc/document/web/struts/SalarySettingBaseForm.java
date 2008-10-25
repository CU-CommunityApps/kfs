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
package org.kuali.kfs.module.bc.document.web.struts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.module.bc.util.SalarySettingCalculator;
import org.kuali.kfs.module.bc.util.SalarySettingFieldsHolder;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * the base Struts form for salary setting
 */
public abstract class SalarySettingBaseForm extends BudgetExpansionForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingBaseForm.class);

    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;
    private SalarySettingFieldsHolder salarySettingFieldsHolder;

    private boolean hideAdjustmentMeasurement = true;
    private String adjustmentMeasurement;
    private KualiDecimal adjustmentAmount;

    private boolean hideDetails = false;
    private Map<String, String> editingMode;

    private boolean budgetByAccountMode;
    private boolean singleAccountMode;
    private boolean salarySettingClosed;

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

    private Person person = GlobalVariables.getUserSession().getPerson();

    /**
     * get the refresh caller name of the current form
     * 
     * @return the refresh caller name of the current form
     */
    public abstract String getRefreshCallerName();

    /**
     * get the key map for the salary setting item: salary expension, position, or incumbent
     * 
     * @return the key map for the salary setting item
     */
    public abstract Map<String, Object> getKeyMapOfSalarySettingItem();

    /**
     * refresh the the appointment funding lines and make them have connections with associated objects
     */
    public void populateBCAFLines() {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.getAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            this.refreshBCAFLine(appointmentFunding);
        }
    }

    /**
     * do some operations on the appointment funding lines. The operations may be included updating and sorting. If everything goes
     * well, return true; otherwise, false
     */
    public boolean postProcessBCAFLines() {
        this.populateBCAFLines();

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = this.getAppointmentFundings();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();
            String chartCode = appointmentFunding.getChartOfAccountsCode();
            String objectCode = appointmentFunding.getFinancialObjectCode();

            boolean vacatable = salarySettingService.canBeVacant(appointmentFundings, appointmentFunding);
            appointmentFunding.setVacatable(vacatable);

            boolean budgetable = budgetDocumentService.isAssociatedWithBudgetableDocument(appointmentFunding);
            appointmentFunding.setBudgetable(budgetable);

            boolean hourlyPaid = salarySettingService.isHourlyPaidObject(fiscalYear, chartCode, objectCode);
            appointmentFunding.setHourlyPaid(hourlyPaid);
        }

        DynamicCollectionComparator.sort(appointmentFundings, KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.EMPLID);
        return true;
    }

    /**
     * Populates the dependent fields of objects contained within the BCAF line
     */
    public void refreshBCAFLine(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.refreshNonUpdateableReferences();
        appointmentFunding.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        appointmentFunding.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNT);
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_CALCULATED_SALARY_FOUNDATION_TRACKER);
    }

    /**
     * Updates authorization-related form fields based on the current form contents
     */
    public void populateAuthorizationFields(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        useBCAuthorizer(documentAuthorizer);

        if (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getName(), "view", this.getAccountNumber() + ", " + this.getSubAccountNumber());
        }
    }

    /**
     * setup the budget construction authorization
     */
    public void useBCAuthorizer(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        Map<String, String> editModeFromSession = documentAuthorizer.getEditModeFromSession();
        if (editModeFromSession == null || editModeFromSession.isEmpty()) {
            this.initializeEditingMode(documentAuthorizer);
            return;
        }

        this.setEditingMode(editModeFromSession);
    }

    /**
     * initialize the editing mode for salary setting
     */
    public void initializeEditingMode(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        Map<String, String> initialEditingMode = this.getInitialEditingMode(documentAuthorizer);

        GlobalVariables.getUserSession().removeObject(BCConstants.BC_DOC_EDIT_MODE_SESSIONKEY);
        GlobalVariables.getUserSession().addObject(BCConstants.BC_DOC_EDIT_MODE_SESSIONKEY, initialEditingMode);

        this.setEditingMode(initialEditingMode);
    }

    /**
     * initialize the editing mode for salary setting
     */
    public Map<String, String> getInitialEditingMode(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        if (this.isBudgetByAccountMode()) {
            Person kualiUser = GlobalVariables.getUserSession().getPerson();

            return documentAuthorizer.getEditMode(this.getUniversityFiscalYear(), this.getChartOfAccountsCode(), this.getAccountNumber(), this.getSubAccountNumber(), kualiUser);
        }
        else {
            // user got here through organization salary setting - check that the user is a BC org approver somewhere
            return documentAuthorizer.getEditMode();
        }
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the hideAdjustmentMeasurement attribute.
     * 
     * @return Returns the hideAdjustmentMeasurement.
     */
    public boolean isHideAdjustmentMeasurement() {
        return hideAdjustmentMeasurement;
    }

    /**
     * Sets the hideAdjustmentMeasurement attribute value.
     * 
     * @param hideAdjustmentMeasurement The hideAdjustmentMeasurement to set.
     */
    public void setHideAdjustmentMeasurement(boolean hideAdjustmentMeasurement) {
        this.hideAdjustmentMeasurement = hideAdjustmentMeasurement;
    }

    /**
     * Gets the adjustmentMeasurement attribute.
     * 
     * @return Returns the adjustmentMeasurement.
     */
    public String getAdjustmentMeasurement() {
        return adjustmentMeasurement;
    }

    /**
     * Sets the adjustmentMeasurement attribute value.
     * 
     * @param adjustmentMeasurement The adjustmentMeasurement to set.
     */
    public void setAdjustmentMeasurement(String adjustmentMeasurement) {
        this.adjustmentMeasurement = adjustmentMeasurement;
    }

    /**
     * Gets the adjustmentAmount attribute.
     * 
     * @return Returns the adjustmentAmount.
     */
    public KualiDecimal getAdjustmentAmount() {
        return adjustmentAmount;
    }

    /**
     * Sets the adjustmentAmount attribute value.
     * 
     * @param adjustmentAmount The adjustmentAmount to set.
     */
    public void setAdjustmentAmount(KualiDecimal adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    /**
     * Gets the hideDetails attribute.
     * 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * Sets the hideDetails attribute value.
     * 
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

    /**
     * Gets the editingMode attribute.
     * 
     * @return Returns the editingMode.
     */
    public Map<String, String> getEditingMode() {
        return editingMode;
    }

    /**
     * Sets the editingMode attribute value.
     * 
     * @param editingMode The editingMode to set.
     */
    public void setEditingMode(Map<String, String> editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * Gets the budgetByAccountMode attribute.
     * 
     * @return Returns the budgetByAccountMode.
     */
    public boolean isBudgetByAccountMode() {
        return budgetByAccountMode;
    }

    /**
     * Sets the budgetByAccountMode attribute value.
     * 
     * @param budgetByAccountMode The budgetByAccountMode to set.
     */
    public void setBudgetByAccountMode(boolean budgetByAccountMode) {
        this.budgetByAccountMode = budgetByAccountMode;
    }

    /**
     * Gets the singleAccountMode attribute.
     * 
     * @return Returns the singleAccountMode.
     */
    public boolean isSingleAccountMode() {
        return singleAccountMode;
    }

    /**
     * Sets the singleAccountMode attribute value.
     * 
     * @param singleAccountMode The singleAccountMode to set.
     */
    public void setSingleAccountMode(boolean singleAccountMode) {
        this.singleAccountMode = singleAccountMode;
    }

    /**
     * Gets the appointmentFundings attribute.
     * 
     * @return Returns the appointmentFundings.
     */
    public abstract List<PendingBudgetConstructionAppointmentFunding> getAppointmentFundings();

    /**
     * Gets the appointmentRequestedCsfAmountTotal.
     * 
     * @return Returns the appointmentRequestedCsfAmountTotal.
     */
    public KualiInteger getAppointmentRequestedCsfAmountTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfAmountTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfTimePercentTotal.
     * 
     * @return Returns the appointmentRequestedCsfTimePercentTotal.
     */
    public BigDecimal getAppointmentRequestedCsfTimePercentTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfTimePercentTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfStandardHoursTotal.
     * 
     * @return Returns the appointmentRequestedCsfStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedCsfStandardHoursTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfStandardHoursTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfFteQuantityTotal.
     * 
     * @return Returns the appointmentRequestedCsfFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedCsfFteQuantityTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfFteQuantityTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedAmountTotal.
     * 
     * @return Returns the appointmentRequestedAmountTotal.
     */
    public KualiInteger getAppointmentRequestedAmountTotal() {
        return SalarySettingCalculator.getAppointmentRequestedAmountTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedTimePercentTotal.
     * 
     * @return Returns the appointmentRequestedTimePercentTotal.
     */
    public BigDecimal getAppointmentRequestedTimePercentTotal() {
        return SalarySettingCalculator.getAppointmentRequestedTimePercentTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedStandardHoursTotal.
     * 
     * @return Returns the appointmentRequestedStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedStandardHoursTotal() {
        return SalarySettingCalculator.getAppointmentRequestedStandardHoursTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedFteQuantityTotal.
     * 
     * @return Returns the appointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedFteQuantityTotal() {
        return SalarySettingCalculator.getAppointmentRequestedFteQuantityTotal(this.getEffectivePendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the csfAmountTotal.
     * 
     * @return Returns the csfAmountTotal.
     */
    public KualiInteger getCsfAmountTotal() {
        return SalarySettingCalculator.getCsfAmountTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the csfTimePercentTotal.
     * 
     * @return Returns the csfTimePercentTotal.
     */
    public BigDecimal getCsfTimePercentTotal() {
        return SalarySettingCalculator.getCsfTimePercentTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the csfStandardHoursTotal.
     * 
     * @return Returns the csfStandardHoursTotal.
     */
    public BigDecimal getCsfStandardHoursTotal() {
        return SalarySettingCalculator.getCsfStandardHoursTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the csfFullTimeEmploymentQuantityTotal.
     * 
     * @return Returns the csfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantityTotal() {
        return SalarySettingCalculator.getCsfFullTimeEmploymentQuantityTotal(this.getAppointmentFundings());
    }

    /**
     * Gets the percentChangeTotal attribute.
     * 
     * @return Returns the percentChangeTotal.
     */
    public KualiDecimal getPercentChangeTotal() {
        KualiInteger csfAmountTotal = this.getCsfAmountTotal();
        KualiInteger requestedAmountTotal = this.getAppointmentRequestedAmountTotal();

        return SalarySettingCalculator.getPercentChange(csfAmountTotal, requestedAmountTotal);
    }

    /**
     * Gets the EffectivePendingBudgetConstructionAppointmentFunding.
     * 
     * @return Returns the EffectivePendingBudgetConstructionAppointmentFunding.
     */
    public List<PendingBudgetConstructionAppointmentFunding> getEffectivePendingBudgetConstructionAppointmentFunding() {
        return SalarySettingCalculator.getEffectiveAppointmentFundings(this.getAppointmentFundings());
    }

    /**
     * Gets the salarySettingFieldsHolder attribute.
     * 
     * @return Returns the salarySettingFieldsHolder.
     */
    public SalarySettingFieldsHolder getSalarySettingFieldsHolder() {
        if (salarySettingFieldsHolder == null) {
            salarySettingFieldsHolder = new SalarySettingFieldsHolder();
            ObjectUtil.buildObject(salarySettingFieldsHolder, this);
        }

        return salarySettingFieldsHolder;
    }

    /**
     * Gets the person attribute.
     * 
     * @return Returns the person.
     */
    public Person getPerson() {
        return person;
    }

    /**
     * Gets the salarySettingClosed attribute.
     * 
     * @return Returns the salarySettingClosed.
     */
    public boolean isSalarySettingClosed() {
        return salarySettingClosed;
    }

    /**
     * Sets the salarySettingClosed attribute value.
     * 
     * @param salarySettingClosed The salarySettingClosed to set.
     */
    public void setSalarySettingClosed(boolean salarySettingClosed) {
        this.salarySettingClosed = salarySettingClosed;
    }

    /**
     * Gets the readOnlyEntry attribute.
     * 
     * @return Returns the readOnlyEntry.
     */
    public boolean isViewOnlyEntry() {
        /*
         * boolean viewOnly = false; if
         * (editingMode.containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)) { viewOnly =
         * Boolean.valueOf(editingMode.get(KfsAuthorizationConstants.BudgetConstructionEditMode.SYSTEM_VIEW_ONLY)); } if (viewOnly) {
         * return true; } if (editingMode.containsKey(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY)) { viewOnly =
         * !Boolean.valueOf(editingMode.get(KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY)); } else { viewOnly =
         * true; } return viewOnly;
         */

        // TODO: restore the logic above
        List<String> messageList = GlobalVariables.getMessageList();
        if (!messageList.contains(BCKeyConstants.WARNING_AUTHORIZATION_DISABLED)) {
            messageList.add(BCKeyConstants.WARNING_AUTHORIZATION_DISABLED);
        }
        return false;
    }

    /**
     * Gets the payrollIncumbentFeedIndictor attribute.
     * 
     * @return Returns the payrollIncumbentFeedIndictor.
     */
    public boolean isPayrollIncumbentFeedIndictor() {
        return BudgetParameterFinder.getPayrollIncumbentFeedIndictor();
    }

    /**
     * Gets the payrollPositionFeedIndicator attribute.
     * 
     * @return Returns the payrollPositionFeedIndicator.
     */
    public boolean isPayrollPositionFeedIndicator() {
        return BudgetParameterFinder.getPayrollPositionFeedIndicator();
    }
}

