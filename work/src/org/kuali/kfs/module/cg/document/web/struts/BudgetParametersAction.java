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
package org.kuali.kfs.module.cg.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.module.cg.KraConstants;
import org.kuali.kfs.module.cg.businessobject.AgencyExtension;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetFringeRate;
import org.kuali.kfs.module.cg.businessobject.BudgetGraduateAssistantRate;
import org.kuali.kfs.module.cg.businessobject.BudgetModular;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.businessobject.BudgetTask;
import org.kuali.kfs.module.cg.businessobject.GraduateAssistantRate;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.validation.event.InsertPeriodLineEventBase;
import org.kuali.kfs.module.cg.document.service.BudgetFringeRateService;
import org.kuali.kfs.module.cg.document.service.BudgetModularService;
import org.kuali.kfs.module.cg.document.web.struts.BudgetForm;


/**
 * This class handles Actions for Research Administration.
 */

public class BudgetParametersAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetParametersAction.class);

    /**
     * This method overrides the BudgetAction execute method. It does so for the purpose of recalculating Personnel expenses any
     * time the Personnel page is accessed
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward superForward = super.execute(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // On first load, set the default task name for the initial task.
        if (budgetForm.getBudgetDocument().getTaskListSize() == 0) {
            String DEFAULT_BUDGET_TASK_NAME = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, KraConstants.DEFAULT_BUDGET_TASK_NAME);
            budgetForm.getNewTask().setBudgetTaskName(DEFAULT_BUDGET_TASK_NAME + " 1");
            budgetForm.getNewTask().setBudgetTaskOnCampus(true);
        }

        // Set default budget types
        setupBudgetTypes(budgetForm);

        // pre-fetch academic year subdivision names for later use
        setupAcademicYearSubdivisionConstants(budgetForm);

        // Disable/enable appropriate navigation tabs
        budgetForm.checkHeaderNavigation();

        return superForward;
    }

    public ActionForward saveParameters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // Need to retain modular task number, since it's set on this page
        Integer budgetModularTaskNumber = null;
        if (!ObjectUtils.isNull(budgetForm.getBudgetDocument().getBudget().getModularBudget())) {
            budgetModularTaskNumber = budgetForm.getBudgetDocument().getBudget().getModularBudget().getBudgetModularTaskNumber();
        }

        List referenceObjects = new ArrayList();

        referenceObjects.add("budgetAgency");
        referenceObjects.add("federalPassThroughAgency");
        referenceObjects.add("projectDirector");
        referenceObjects.add("nonpersonnelItems");
        referenceObjects.add("personnel");
        referenceObjects.add("modularBudget");
        referenceObjects.add("indirectCost");
        referenceObjects.add("thirdPartyCostShareItems");
        referenceObjects.add("institutionCostShareItems");
        referenceObjects.add("institutionCostSharePersonnelItems");

        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetForm.getBudgetDocument().getBudget(), referenceObjects);

        List docReferenceObjects = new ArrayList();
        docReferenceObjects.add("adhocPersons");
        docReferenceObjects.add("adhocOrgs");
        docReferenceObjects.add("adhocWorkgroups");

        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budgetForm.getBudgetDocument(), docReferenceObjects);

        if (budgetForm.getBudgetDocument().getBudget().isAgencyModularIndicator()) {
            if (ObjectUtils.isNull(budgetForm.getBudgetDocument().getBudget().getModularBudget())) {
                // Modular budget with no modular data generated. So generate it.
                SpringContext.getBean(BudgetModularService.class).generateModularBudget(budgetForm.getBudgetDocument().getBudget());
            }
            budgetForm.getBudgetDocument().getBudget().getModularBudget().setBudgetModularTaskNumber(budgetModularTaskNumber);
        }

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        // Logic for Cost Share question.
        ActionForward preRulesForward = preRulesCheck(mapping, form, request, response, "saveParameters");
        if (preRulesForward != null) {
            return preRulesForward;
        }

        super.save(mapping, form, request, response);

        if (GlobalVariables.getErrorMap().size() == 0) {
            if (budgetForm.isAuditActivated()) {
                return mapping.findForward("auditmode");
            }

            // This is so that tab states are not shared between parameters and overview.
            budgetForm.newTabState(true, true);

            return super.overview(mapping, budgetForm, request, response);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward copyFringeRateLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get the form
        BudgetForm budgetForm = (BudgetForm) form;

        BudgetFringeRateService bfrService = SpringContext.getBean(BudgetFringeRateService.class);
        for (BudgetFringeRate budgetFringeRate : budgetForm.getBudgetDocument().getBudget().getFringeRates()) {
            budgetFringeRate.setContractsAndGrantsFringeRateAmount(budgetFringeRate.getAppointmentTypeFringeRateAmount());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward copyInstitutionCostShareLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get the form
        BudgetForm budgetForm = (BudgetForm) form;

        BudgetFringeRateService bfrService = SpringContext.getBean(BudgetFringeRateService.class);
        for (BudgetFringeRate budgetFringeRate : budgetForm.getBudgetDocument().getBudget().getFringeRates()) {
            budgetFringeRate.setInstitutionCostShareFringeRateAmount(budgetFringeRate.getAppointmentTypeCostShareFringeRateAmount());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /*
     * A struts action to copy the the Graduate Asst. rates from the system rate to the current budget
     */
    public ActionForward copySystemGraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get the form
        BudgetForm budgetForm = (BudgetForm) form;

        for (BudgetGraduateAssistantRate budgetGraduateAssistantRate : budgetForm.getBudgetDocument().getBudget().getGraduateAssistantRates()) {
            budgetGraduateAssistantRate.refreshNonUpdateableReferences();
            GraduateAssistantRate systemRate = budgetGraduateAssistantRate.getGraduateAssistantRate();
            budgetGraduateAssistantRate.setCampusMaximumPeriod1Rate(systemRate.getCampusMaximumPeriod1Rate());
            budgetGraduateAssistantRate.setCampusMaximumPeriod2Rate(systemRate.getCampusMaximumPeriod2Rate());
            budgetGraduateAssistantRate.setCampusMaximumPeriod3Rate(systemRate.getCampusMaximumPeriod3Rate());
            budgetGraduateAssistantRate.setCampusMaximumPeriod4Rate(systemRate.getCampusMaximumPeriod4Rate());
            budgetGraduateAssistantRate.setCampusMaximumPeriod5Rate(systemRate.getCampusMaximumPeriod5Rate());
            budgetGraduateAssistantRate.setCampusMaximumPeriod6Rate(systemRate.getCampusMaximumPeriod6Rate());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    public ActionForward insertPeriodLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // check any business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new InsertPeriodLineEventBase(budgetForm.getDocument(), budgetForm.getNewPeriod()));

        if (rulePassed) {
            budgetForm.getBudgetDocument().addPeriod(budgetForm.getNewPeriod());
            budgetForm.setNewPeriod(new BudgetPeriod());
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deletePeriodLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ((BudgetForm) form).getBudgetDocument().setPeriodToDelete(Integer.toString(getLineToDelete(request)));
        ActionForward preRulesForward = preRulesCheck(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward insertTaskLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        budgetForm.getBudgetDocument().addTask(budgetForm.getNewTask());
        budgetForm.setNewTask(new BudgetTask());
        budgetForm.getNewTask().setBudgetTaskOnCampus(true);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteTaskLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ((BudgetForm) form).getBudgetDocument().setTaskToDelete(Integer.toString(getLineToDelete(request)));
        ActionForward preRulesForward = preRulesCheck(mapping, form, request, response);
        if (preRulesForward != null) {
            return preRulesForward;
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;
        Budget budget = budgetForm.getBudgetDocument().getBudget();
        if (request.getParameter(KFSConstants.REFRESH_CALLER) != null) {
            String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);
            // check to see if we are coming back from a lookup
            if (refreshCaller.equals(KFSConstants.KUALI_LOOKUPABLE_IMPL)) {
                if ("true".equals(request.getParameter("document.budget.agencyToBeNamedIndicator"))) {
                    // coming back from Agency lookup - To Be Named selected
                    budget.setBudgetAgency(null);
                    budget.setBudgetAgencyNumber(null);
                    BudgetModular modularBudget = budget.getModularBudget() != null ? budget.getModularBudget() : new BudgetModular(budget.getDocumentNumber());
                    resetModularBudget(budget, modularBudget);
                    budget.setModularBudget(modularBudget);
                }
                else if (request.getParameter("document.budget.budgetAgencyNumber") != null) {
                    // coming back from an Agnecy lookup - Agency selected
                    budget.setAgencyToBeNamedIndicator(false);
                    BudgetModular modularBudget = budget.getModularBudget() != null ? budget.getModularBudget() : new BudgetModular(budget.getDocumentNumber());
                    budget.refreshReferenceObject("budgetAgency");
                    budget.getBudgetAgency().refresh();
                    if (budget.getBudgetAgency().getAgencyExtension() != null) {
                        AgencyExtension agencyExtension = budget.getBudgetAgency().getAgencyExtension();
                        modularBudget.setBudgetModularIncrementAmount(agencyExtension.getBudgetModularIncrementAmount());
                        modularBudget.setBudgetPeriodMaximumAmount(agencyExtension.getBudgetPeriodMaximumAmount());
                    }
                    else {
                        resetModularBudget(budget, modularBudget);
                    }
                    budget.setModularBudget(modularBudget);
                }
                else if (request.getParameter("document.budget.budgetProjectDirectorUniversalIdentifier") != null) {
                    // Coming back from project director lookup - project director selected
                    budgetForm.getBudgetDocument().getBudget().setProjectDirectorToBeNamedIndicator(false);
                    budgetForm.getBudgetDocument().getBudget().refreshReferenceObject("projectDirector");
                }
                else if ("true".equals(request.getParameter("document.budget.projectDirectorToBeNamedIndicator"))) {
                    // Coming back from project director lookup - Name Later selected
                    budgetForm.getBudgetDocument().getBudget().setProjectDirector(null);
                    budgetForm.getBudgetDocument().getBudget().setBudgetProjectDirectorUniversalIdentifier(null);
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private static void resetModularBudget(Budget budget, BudgetModular modularBudget) {
        modularBudget.setBudgetModularTaskNumber(null);
        budget.setAgencyModularIndicator(false);
    }

    public ActionForward clearFedPassthrough(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        Budget budget = budgetForm.getBudgetDocument().getBudget();

        budget.setFederalPassThroughAgencyNumber(null);
        budget.setFederalPassThroughAgency(null);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward basic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
