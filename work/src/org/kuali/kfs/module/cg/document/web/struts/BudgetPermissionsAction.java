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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.kfs.module.cg.businessobject.AdhocOrg;
import org.kuali.kfs.module.cg.businessobject.AdhocPerson;
import org.kuali.kfs.module.cg.document.web.struts.BudgetForm;

/**
 * This class handles Actions for Research Administration permissions page.
 */
public class BudgetPermissionsAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetPermissionsAction.class);

    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        List adhocPermissions = budgetForm.getBudgetDocument().getAdhocPersons();
        List adhocOrgs = budgetForm.getBudgetDocument().getAdhocOrgs();
        List adhocWorkgroups = budgetForm.getBudgetDocument().getAdhocWorkgroups();

        this.load(mapping, budgetForm, request, response);

        budgetForm.getBudgetDocument().setAdhocPersons(adhocPermissions);
        budgetForm.getBudgetDocument().setAdhocOrgs(adhocOrgs);
        budgetForm.getBudgetDocument().setAdhocWorkgroups(adhocWorkgroups);

        ActionForward forward = super.save(mapping, budgetForm, request, response);

        budgetForm.getBudgetDocument().populateDocumentForRouting();
        budgetForm.getBudgetDocument().getDocumentHeader().getWorkflowDocument().saveRoutingData();

        return forward;
    }

    @Override
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        budgetForm.setNewAdHocPerson(new AdhocPerson());
        budgetForm.setNewAdHocOrg(new AdhocOrg());
        budgetForm.setNewAdHocRouteWorkgroup(new AdHocRouteWorkgroup());
        budgetForm.setNewAdHocWorkgroupPermissionCode("");

        ActionForward forward = super.reload(mapping, budgetForm, request, response);

        return forward;
    }
}
