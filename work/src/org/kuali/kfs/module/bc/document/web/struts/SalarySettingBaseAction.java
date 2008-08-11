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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.KualiModule;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;

public abstract class SalarySettingBaseAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingBaseAction.class);

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);
    private List<String> messageList = GlobalVariables.getMessageList();

    /**
     * loads the data for the expansion screen based on the passed in url parameters
     */
    public abstract ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        salarySettingForm.populateAuthorizationFields(new BudgetConstructionDocumentAuthorizer());

        ActionForward forward = super.execute(mapping, form, request, response);

        boolean isSuccessfullyProcessed = salarySettingForm.postProcessBCAFLines();
        if (!isSuccessfullyProcessed) {
            return this.returnToCaller(mapping, form, request, response);
        }

        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    public void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();
        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());

        if (!getKualiModuleService().isAuthorized(currentUser, bcAuthorizationType)) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName());

            KualiModule module = getKualiModuleService().getResponsibleModule(this.getClass());
            throw new ModuleAuthorizationException(currentUser.getPersonUserIdentifier(), bcAuthorizationType, module);
        }
    }

    /**
     * save the information in the current form into underlying data store
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;

        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Save For Salary Setting by Incumbent");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.BudgetExpansionAction#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // return to the calller directly
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;

        if (salarySettingForm.isViewOnlyEntry()) {
            messageList.add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
            return this.returnToCaller(mapping, form, request, response);
        }

        // ask the question unless it has not been answered
        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (StringUtils.isBlank(question)) {
            String questionText = kualiConfiguration.getPropertyString(KFSKeyConstants.QUESTION_SAVE_BEFORE_CLOSE);
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
        }

        // save the salary setting if the user answers to the question with "Yes"
        String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if (StringUtils.equals(KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, question) && StringUtils.equals(ConfirmationQuestion.YES, buttonClicked)) {
            ActionForward saveAction = this.save(mapping, form, request, response);

            if (!messageList.contains(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED)) {
                messageList.add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
            }

            return saveAction;
        }

        messageList.add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
        return this.returnToCaller(mapping, form, request, response);
    }

    /**
     * vacate the specified appointment funding line
     */
    public ActionForward vacateSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        salarySettingService.vacateAppointmentFunding(appointmentFundings, appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * delete the selected salary setting line
     */
    public ActionForward purgeSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        salarySettingService.purgeAppointmentFunding(appointmentFundings, appointmentFunding);
        messageList.add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * mark the selected salary setting line as deleted
     */
    public ActionForward deleteSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        salarySettingService.markAsDelete(appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * revert the selected salary setting line that just has been marked as deleted
     */
    public ActionForward revertSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        salarySettingService.revert(appointmentFundings, appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adjust the salary amount of the specified funding line
     */
    public ActionForward adjustSalarySettingLinePercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        KualiDecimal adjustmentAmount = appointmentFunding.getAdjustmentAmount();
        String adjustmentMeasurement = appointmentFunding.getAdjustmentMeasurement();
        if (adjustmentAmount == null || adjustmentMeasurement == null) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_EMPTY_ADJUSTMENT_FIELD);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        this.adjustSalary(appointmentFunding);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adjust the salary amounts of all funding lines
     */
    public ActionForward adjustAllSalarySettingLinesPercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        KualiDecimal adjustmentAmount = salarySettingForm.getAdjustmentAmount();
        String adjustmentMeasurement = salarySettingForm.getAdjustmentMeasurement();

        if (adjustmentAmount == null || adjustmentMeasurement == null) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_EMPTY_ADJUSTMENT_FIELD);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            appointmentFunding.setAdjustmentAmount(adjustmentAmount);
            appointmentFunding.setAdjustmentMeasurement(adjustmentMeasurement);

            this.adjustSalary(appointmentFunding);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * normalize the hourly pay rate and annual pay amount
     */
    public ActionForward normalizePayRateAndAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        BigDecimal payRate = appointmentFunding.getAppointmentRequestedPayRate();
        KualiInteger annualAmount = appointmentFunding.getAppointmentRequestedAmount();
        if (payRate == null && annualAmount == null) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_EMPTY_PAY_RATE_ANNUAL_AMOUNT);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        salarySettingService.normalizePayRateAndAmount(appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adjust the requested salary amount of the given appointment funding line
     */
    public void adjustSalary(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (appointmentFunding.getEffectiveCSFTracker() == null || appointmentFunding.isAppointmentFundingDeleteIndicator()) {
            return;
        }

        String adjustmentMeasurement = appointmentFunding.getAdjustmentMeasurement();
        if (BCConstants.SalaryAdjustmentMeasurement.PERCENT.measurement.equals(adjustmentMeasurement)) {
            salarySettingService.adjustRequestedSalaryByPercent(appointmentFunding);
        }
        else if (BCConstants.SalaryAdjustmentMeasurement.AMOUNT.measurement.equals(adjustmentMeasurement)) {
            salarySettingService.adjustRequestedSalaryByAmount(appointmentFunding);
        }
    }

    /**
     * get the selected appointment funding line
     */
    protected PendingBudgetConstructionAppointmentFunding getSelectedFundingLine(HttpServletRequest request, SalarySettingBaseForm salarySettingForm) {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        int indexOfSelectedLine = this.getSelectedLine(request);
        return appointmentFundings.get(indexOfSelectedLine);
    }

    /**
     * execute the rules associated with the given event
     * 
     * @param event the event that just occured
     * @return true if the rules associated with the given event pass; otherwise, false
     */
    protected boolean invokeRules(KualiDocumentEvent event) {
        return SpringContext.getBean(KualiRuleService.class).applyRules(event);
    }
}
