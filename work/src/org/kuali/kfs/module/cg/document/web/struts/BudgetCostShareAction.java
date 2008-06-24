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
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetInstitutionCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.businessobject.BudgetThirdPartyCostShare;
import org.kuali.kfs.sys.KFSConstants;

/**
 * Action for BudgetCostShare page.
 */
public class BudgetCostShareAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetParametersAction.class);

    /**
     * Data preparation for Cost Share page.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        Budget budget = budgetForm.getBudgetDocument().getBudget();

        // Partial re-load to avoid lots of hidden variables.
        budget.refreshReferenceObject("personnel");
        budget.refreshReferenceObject("nonpersonnelItems");
        budget.refreshReferenceObject("indirectCost");

        // super.execute has to be called before re-creating BudgetCostShareFormHelper because the super call may
        // completly reload data such as for example for the reload button
        ActionForward forward = super.execute(mapping, form, request, response);

        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        setupBudgetCostSharePermissionDisplay(budgetForm);

        return forward;
    }

    /**
     * Recalculate button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // execute method already does this work.

        return super.update(mapping, form, request, response);
    }

    /**
     * Insert Institution Cost Share button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertInstitutionCostShareDirect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // Add the new institution cost share item and create a new one
        budgetForm.getBudgetDocument().addInstitutionCostShare(budgetForm.getBudgetDocument().getBudget().getPeriods(), budgetForm.getNewInstitutionCostShare());
        budgetForm.setNewInstitutionCostShare(new BudgetInstitutionCostShare());

        // Make sure new values are taken into account for calculations.
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));
        budgetForm.getNewInstitutionCostShare().setPermissionIndicator(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete Institution Cost Share button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteInstitutionCostShareDirect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        budgetForm.getBudgetDocument().getBudget().getInstitutionCostShareItems().remove(getLineToDelete(request));

        // Make sure new values are taken into account for calculations.
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Insert Third Party Cost Share button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertThirdPartyCostShareDirect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // Add the new third party cost share item and create a new one
        budgetForm.getBudgetDocument().addThirdPartyCostShare(budgetForm.getBudgetDocument().getBudget().getPeriods(), budgetForm.getNewThirdPartyCostShare());
        budgetForm.setNewThirdPartyCostShare(new BudgetThirdPartyCostShare());

        // Make sure new values are taken into account for calculations.
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete Third Party Cost Share button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteThirdPartyCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        budgetForm.getBudgetDocument().getBudget().getThirdPartyCostShareItems().remove(getLineToDelete(request));

        // Make sure new values are taken into account for calculations.
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Save button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveBudgetCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        Budget budget = budgetForm.getBudgetDocument().getBudget();
        List<BudgetPeriod> periods = budgetForm.getBudgetDocument().getBudget().getPeriods();

        Integer institutionCostShareNextSequenceNumber = budgetForm.getBudgetDocument().getInstitutionCostShareNextSequenceNumber();
        Integer thirdPartyCostShareNextSequenceNumber = budgetForm.getBudgetDocument().getThirdPartyCostShareNextSequenceNumber();
        List institutionCostShare = new ArrayList(budget.getInstitutionCostShareItems());
        List institutionCostSharePersonnel = new ArrayList(budget.getInstitutionCostSharePersonnelItems());
        List thirdPartyCostShare = new ArrayList(budget.getThirdPartyCostShareItems());

        this.load(mapping, form, request, response);

        // Only set the objects that potentially changed. The interface doesn't keep hiddens for fields that arn't used. Careful
        // not to use "budget" variable above, that doesn't effect budgetForm.
        if (budget.isInstitutionCostShareIndicator()) {
            budgetForm.getBudgetDocument().setInstitutionCostShareNextSequenceNumber(institutionCostShareNextSequenceNumber);
            budgetForm.getBudgetDocument().getBudget().setInstitutionCostShareItems(institutionCostShare);
            budgetForm.getBudgetDocument().getBudget().setInstitutionCostSharePersonnelItems(institutionCostSharePersonnel);
        }
        if (budget.isBudgetThirdPartyCostShareIndicator()) {
            budgetForm.getBudgetDocument().setThirdPartyCostShareNextSequenceNumber(thirdPartyCostShareNextSequenceNumber);
            budgetForm.getBudgetDocument().getBudget().setThirdPartyCostShareItems(thirdPartyCostShare);
        }

        return super.save(mapping, form, request, response);
    }

    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.reload(mapping, form, request, response);
        ((BudgetForm) form).getNewInstitutionCostShare().setPermissionIndicator(true);
        return forward;
    }
}
