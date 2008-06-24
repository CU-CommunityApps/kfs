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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.rule.event.AddAdHocRoutePersonEvent;
import org.kuali.core.rule.event.AddAdHocRouteWorkgroupEvent;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.service.WebAuthenticationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.module.cg.KraConstants;
import org.kuali.kfs.module.cg.KraKeyConstants;
import org.kuali.kfs.module.cg.businessobject.AdhocOrg;
import org.kuali.kfs.module.cg.businessobject.AdhocPerson;
import org.kuali.kfs.module.cg.businessobject.AdhocWorkgroup;
import org.kuali.kfs.module.cg.document.ResearchDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;

import edu.iu.uis.eden.clientapp.IDocHandler;

public abstract class ResearchDocumentActionBase extends KualiDocumentActionBase {

    public ResearchDocumentActionBase() {
        // TODO Auto-generated constructor stub
    }

    /**
     * This method will load the document, which was previously saved
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        researchForm.setDocId(researchForm.getDocument().getDocumentNumber());
        super.loadDocument(researchForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    /**
     * Overriding headerTab to customize how clearing tab state works on Budget. Specifically, additional attributes (selected task
     * and period) should be cleared any time header navigation occurs.
     */
    public ActionForward headerTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ((KualiForm) form).setTabStates(new HashMap());

        return super.headerTab(mapping, form, request, response);
    }


    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.docHandler(mapping, form, request, response);
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;

        if (IDocHandler.INITIATE_COMMAND.equals(researchForm.getCommand())) {
            // do something?
            researchForm.getResearchDocument().initialize();
        }
        return forward;
    }


    public ActionForward notes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        ResearchDocumentFormBase researchDocumentForm = (ResearchDocumentFormBase) form;

        if (researchDocumentForm.getDocument().getDocumentHeader().isBoNotesSupport() && (researchDocumentForm.getDocument().getDocumentHeader().getBoNotes() == null || researchDocumentForm.getDocument().getDocumentHeader().getBoNotes().isEmpty())) {
            researchDocumentForm.getDocument().refreshReferenceObject("documentHeader");
        }

        researchDocumentForm.setTabStates(new HashMap());

        return mapping.findForward("notes");
    }

    @Override
    public ActionForward insertAdHocRoutePerson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        // check authorization
        DocumentActionFlags flags = getDocumentActionFlags(researchDocument);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("ad-hoc route", researchDocument);
        }

        AdHocRoutePerson adHocRoutePerson = (AdHocRoutePerson) researchForm.getNewAdHocRoutePerson();

        // check business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAdHocRoutePersonEvent(researchDocument, (AdHocRoutePerson) researchForm.getNewAdHocRoutePerson()));

        if (rulePassed) {
            AdhocPerson newAdHocPermission = researchForm.getNewAdHocPerson();
            UniversalUser user = SpringContext.getBean(UniversalUserService.class).getUniversalUser(new AuthenticationUserId(adHocRoutePerson.getId()));
            newAdHocPermission.setPersonUniversalIdentifier(user.getPersonUniversalIdentifier());
            user.setPersonUserIdentifier(StringUtils.upperCase(user.getPersonUserIdentifier()));
            if (adHocRoutePerson.getActionRequested() == null || StringUtils.isBlank(adHocRoutePerson.getActionRequested())) {
                newAdHocPermission.setAdhocTypeCode(KraConstants.AD_HOC_PERMISSION);
            }
            else {
                newAdHocPermission.setActionRequested(adHocRoutePerson.getActionRequested());
                newAdHocPermission.setAdhocTypeCode(KraConstants.AD_HOC_APPROVER);
            }
            newAdHocPermission.setUser(user);
            newAdHocPermission.setPersonAddedTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            newAdHocPermission.setAddedByPerson(SpringContext.getBean(WebAuthenticationService.class).getNetworkId(request));
            researchDocument.getAdhocPersons().add(newAdHocPermission);
            researchForm.setNewAdHocPerson(new AdhocPerson());
            researchForm.setNewAdHocRoutePerson(new AdHocRoutePerson());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward insertAdHocRouteWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        // check authorization
        DocumentActionFlags flags = getDocumentActionFlags(researchDocument);
        if (!flags.getCanAdHocRoute()) {
            throw buildAuthorizationException("ad-hoc route", researchDocument);
        }

        AdHocRouteWorkgroup adHocRouteWorkgroup = (AdHocRouteWorkgroup) researchForm.getNewAdHocRouteWorkgroup();

        // check business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAdHocRouteWorkgroupEvent(researchDocument, (AdHocRouteWorkgroup) researchForm.getNewAdHocRouteWorkgroup()));

        if (rulePassed) {
            AdhocWorkgroup newAdHocWorkgroup = new AdhocWorkgroup(adHocRouteWorkgroup.getId());
            if (adHocRouteWorkgroup.getActionRequested() == null) {
                newAdHocWorkgroup.setAdhocTypeCode(KraConstants.AD_HOC_PERMISSION);
            }
            else {
                newAdHocWorkgroup.setActionRequested(adHocRouteWorkgroup.getActionRequested());
                newAdHocWorkgroup.setAdhocTypeCode(KraConstants.AD_HOC_APPROVER);
            }
            newAdHocWorkgroup.setPermissionCode(researchForm.getNewAdHocWorkgroupPermissionCode());
            newAdHocWorkgroup.setPersonAddedTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            newAdHocWorkgroup.setAddedByPerson(SpringContext.getBean(WebAuthenticationService.class).getNetworkId(request));
            researchDocument.getAdhocWorkgroups().add(newAdHocWorkgroup);
            researchForm.setNewAdHocRouteWorkgroup(new AdHocRouteWorkgroup());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will remove the selected ad-hoc person from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();
        researchDocument.getAdhocPersons().remove(getLineToDelete(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will remove the selected ad-hoc person from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteWorkgroup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();
        researchDocument.getAdhocWorkgroups().remove(getLineToDelete(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will add a new ad-hoc org to the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();

        if (researchForm.getNewAdHocOrg().getFiscalCampusCode() == null) {
            // Add page error.
            GlobalVariables.getErrorMap().putError("newAdHocOrg", KraKeyConstants.ERROR_NO_ORG_SELECTED, new String[] {});
        }
        else {
            AdhocOrg newAdHocOrg = researchForm.getNewAdHocOrg();
            if (newAdHocOrg.getActionRequested() == null) {
                newAdHocOrg.setAdhocTypeCode(KraConstants.AD_HOC_PERMISSION);
            }
            else {
                newAdHocOrg.setAdhocTypeCode(KraConstants.AD_HOC_APPROVER);
            }
            newAdHocOrg.setPersonAddedTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            newAdHocOrg.setAddedByPerson(SpringContext.getBean(WebAuthenticationService.class).getNetworkId(request));
            researchDocument.getAdhocOrgs().add(newAdHocOrg);
            researchForm.setNewAdHocOrg(new AdhocOrg());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will remove the selected ad-hoc org from the list.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteOrg(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ResearchDocumentFormBase researchForm = (ResearchDocumentFormBase) form;
        ResearchDocument researchDocument = (ResearchDocument) researchForm.getDocument();
        researchDocument.getAdhocOrgs().remove(getLineToDelete(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
