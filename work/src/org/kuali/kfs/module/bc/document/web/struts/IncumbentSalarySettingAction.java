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
package org.kuali.kfs.module.bc.document.web.struts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * the struts action for the salary setting for incumbent
 */
public class IncumbentSalarySettingAction extends DetailSalarySettingAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncumbentSalarySettingAction.class);

    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private BudgetConstructionIntendedIncumbentService budgetConstructionIntendedIncumbentService = SpringContext.getBean(BudgetConstructionIntendedIncumbentService.class);

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#loadExpansionScreen(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncumbentSalarySettingForm incumbentSalarySettingForm = (IncumbentSalarySettingForm) form;
        ErrorMap callBackErrors = incumbentSalarySettingForm.getCallBackErrors();

        // update incumbent record if required
        if (incumbentSalarySettingForm.isRefreshIncumbentBeforeSalarySetting()) {
            budgetConstructionIntendedIncumbentService.refreshIncumbentFromExternal(incumbentSalarySettingForm.getEmplid());
        }

        // use the passed url parms to get the record from DB
        Map<String, Object> fieldValues = incumbentSalarySettingForm.getKeyMapOfSalarySettingItem();
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, fieldValues);

        if (budgetConstructionIntendedIncumbent == null) {
            String emplid = (String) fieldValues.get(KFSPropertyConstants.EMPLID);
            
            callBackErrors.putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_INCUMBENT_NOT_FOUND, emplid);
            return this.returnToCaller(mapping, form, request, response);
        }

        incumbentSalarySettingForm.setBudgetConstructionIntendedIncumbent(budgetConstructionIntendedIncumbent);
        if (incumbentSalarySettingForm.isSingleAccountMode()) {
            incumbentSalarySettingForm.pickAppointmentFundingsForSingleAccount();
        }

        // acquire position and funding locks for the associated funding lines
        if (!incumbentSalarySettingForm.isViewOnlyEntry()) {
            incumbentSalarySettingForm.postProcessBCAFLines();
            incumbentSalarySettingForm.setNewBCAFLine(incumbentSalarySettingForm.createNewAppointmentFundingLine());
            
            boolean accessModeUpdated = incumbentSalarySettingForm.updateAccessMode(callBackErrors);
            if (!accessModeUpdated) {
                return this.returnToCaller(mapping, form, request, response);
            }

            boolean gotLocks = incumbentSalarySettingForm.acquirePositionAndFundingLocks(callBackErrors);
            if (!gotLocks) {
                return this.returnToCaller(mapping, form, request, response);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#getFundingAwareObjectName()
     */
    @Override
    protected String getFundingAwareObjectName() {
        return BCPropertyConstants.BUDGET_CONSTRUCTION_INTENDED_INCUMBENT;
    }
}
