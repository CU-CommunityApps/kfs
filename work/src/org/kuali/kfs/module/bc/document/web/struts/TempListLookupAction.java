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
package org.kuali.module.budget.web.struts.action;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiLookupAction;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.service.OrganizationBCDocumentSearchService;
import org.kuali.module.budget.service.OrganizationSalarySettingSearchService;
import org.kuali.module.budget.web.struts.form.TempListLookupForm;

/**
 * This class...
 */
public class TempListLookupAction extends KualiLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TempListLookupAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward;
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // TODO use switch here
        if (tempListLookupForm.getBusinessObjectClassName().equals("org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbentSelect")) {
            SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildIntendedIncumbentSelect(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
        }
        if (tempListLookupForm.getBusinessObjectClassName().equals("org.kuali.module.budget.bo.BudgetConstructionPositionSelect")) {
            SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildPositionSelect(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
        }
        // TODO may need to pass another parameter for building variations of AccountSelect
        if (tempListLookupForm.getBusinessObjectClassName().equals("org.kuali.module.budget.bo.BudgetConstructionAccountSelect")) {
            SpringContext.getBean(OrganizationBCDocumentSearchService.class).buildAccountSelectPullList(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
            GlobalVariables.getMessageList().add("message.budget.accountList");
        }

        forward = super.start(mapping, form, request, response);
        if (tempListLookupForm.isShowInitialResults()) {
            forward = search(mapping, form, request, response);
        }

        return forward;
    }

    /**
     * This differs from KualiLookupAction.clearValues in that any atributes marked hidden will not be cleared. This is to support
     * BC temp tables that use personUniversalIdentifier to operate on the set of rows associated with the current user.
     * 
     * @see org.kuali.core.web.struts.action.KualiLookupAction#clearValues(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!field.getFieldType().equals(Field.RADIO) && !field.getFieldType().equals(Field.HIDDEN)) {
                    field.setPropertyValue(field.getDefaultValue());
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiLookupAction#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // TODO use switch here
        if (tempListLookupForm.getBusinessObjectClassName().equals("org.kuali.module.budget.bo.BudgetConstructionIntendedIncumbentSelect")) {
            SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanIntendedIncumbentSelect(tempListLookupForm.getPersonUniversalIdentifier());
        }
        if (tempListLookupForm.getBusinessObjectClassName().equals("org.kuali.module.budget.bo.BudgetConstructionPositionSelect")) {
            SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanPositionSelect(tempListLookupForm.getPersonUniversalIdentifier());
        }
        if (tempListLookupForm.getBusinessObjectClassName().equals("org.kuali.module.budget.bo.BudgetConstructionAccountSelect")) {
            SpringContext.getBean(OrganizationBCDocumentSearchService.class).cleanAccountSelectPullList(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
        }

        return super.cancel(mapping, form, request, response);
    }

}
