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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BudgetConstructionReportMode;
import org.kuali.kfs.module.bc.BCConstants.OrgSelControlOption;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSelect;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbentSelect;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPositionSelect;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.module.bc.document.service.BudgetOrganizationTreeService;
import org.kuali.kfs.module.bc.document.service.BudgetPushPullService;
import org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.module.bc.report.ReportControlListBuildHelper;
import org.kuali.kfs.module.bc.util.BudgetUrlUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Handles organization budget action requests from menu.
 */
public class OrganizationSelectionTreeAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrganizationSelectionTreeAction.class);
    
    private PermissionService permissionService = SpringContext.getBean(PermissionService.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {

        AuthorizationType adHocAuthorizationType = new AuthorizationType.AdHocRequest(this.getClass(), methodToCall);
        if (!SpringContext.getBean(KualiModuleService.class).isAuthorized(GlobalVariables.getUserSession().getFinancialSystemUser(), adHocAuthorizationType)) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName());
            throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUserIdentifier(), adHocAuthorizationType, getKualiModuleService().getResponsibleModule(((KualiForm) form).getClass()));
        }

        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
        try {
            List<Org> pointOfViewOrgs = permissionService.getOrgReview(universalUser);
            if (pointOfViewOrgs.isEmpty()) {
                GlobalVariables.getErrorMap().putError("pointOfViewOrg", "error.budget.userNotOrgApprover");
            }

        }
        catch (Exception e) {
            throw new AuthorizationException(universalUser.getPersonUserIdentifier(), this.getClass().getName(), "Can't determine organization approver status.");
        }
    }

    /**
     * Sets up the initial mode of the drill down screen based on a passed in calling mode attribute This can be one of five modes -
     * pullup, pushdown, reports, salset, account. Each mode causes a slightly different rendition of the controls presented to the
     * user, but the basic point of view selection and organization drill down functionality is the same in all five modes.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm orgSelTreeForm = (OrganizationSelectionTreeForm) form;
        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();

        // check if user has only one available point of view. if so, select that point of view and build selection
        List<Org> pointOfViewOrgs = permissionService.getOrgReview(universalUser);
        if (pointOfViewOrgs != null && pointOfViewOrgs.size() == 1) {
            orgSelTreeForm.setCurrentPointOfViewKeyCode(pointOfViewOrgs.get(0).getChartOfAccountsCode() + "-" + pointOfViewOrgs.get(0).getOrganizationCode());

            return performBuildPointOfView(mapping, form, request, response);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called by the close button. It removes the user's BudgetConstructionPullup table rows and returns the user to the seleection
     * screen action.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // depopulate any selection subtrees for the user
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();
        SpringContext.getBean(BudgetOrganizationTreeService.class).cleanPullup(personUserIdentifier);

        return super.returnToCaller(mapping, form, request, response);
    }

    /**
     * Implements functionality behind the refresh button on the Organization Selection Tree screen. This is also called when the
     * value of the point of view select control changed and javascript is enabled on the user's browser
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performBuildPointOfView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        // check for point of view change
        if (organizationSelectionTreeForm.getCurrentPointOfViewKeyCode() != null) {
            if ((organizationSelectionTreeForm.getPreviousPointOfViewKeyCode() == null) || (!organizationSelectionTreeForm.getPreviousPointOfViewKeyCode().equalsIgnoreCase(organizationSelectionTreeForm.getCurrentPointOfViewKeyCode()) == true)) {
                String[] flds = organizationSelectionTreeForm.getCurrentPointOfViewKeyCode().split("[-]");
                organizationSelectionTreeForm.setPreviousPointOfViewKeyCode(organizationSelectionTreeForm.getCurrentPointOfViewKeyCode());

                HashMap map = new HashMap();
                map.put("chartOfAccountsCode", flds[0]);
                map.put("organizationCode", flds[1]);
                organizationSelectionTreeForm.setPointOfViewOrg((BudgetConstructionOrganizationReports) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionOrganizationReports.class, map));

                // build a new selection subtree
                String personUniversalIdentifier = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();
                SpringContext.getBean(BudgetOrganizationTreeService.class).buildPullupSql(personUniversalIdentifier, flds[0], flds[1]);

                // initialize the selection tool to the root
                map.put("personUniversalIdentifier", personUniversalIdentifier);
                organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BusinessObjectService.class).findMatching(BudgetConstructionPullup.class, map));
                organizationSelectionTreeForm.populateSelectionSubTreeOrgs();
                organizationSelectionTreeForm.setPreviousBranchOrgs(new TypedArrayList(BudgetConstructionPullup.class));
            }
        }
        else {
            organizationSelectionTreeForm.setPointOfViewOrg(new BudgetConstructionOrganizationReports());
            organizationSelectionTreeForm.setSelectionSubTreeOrgs(new TypedArrayList(BudgetConstructionPullup.class));
            organizationSelectionTreeForm.setPreviousBranchOrgs(new TypedArrayList(BudgetConstructionPullup.class));
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handles saving the BudgetConstructionPullup current row to the previous branches stack and displaying the associated
     * children.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward navigateDown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        String personUniversalIdentifier = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        // reset any set pullflags in the database before navigation
        SpringContext.getBean(BudgetOrganizationTreeService.class).resetPullFlag(personUniversalIdentifier);

        // push parent org onto the branch stack
        organizationSelectionTreeForm.getPreviousBranchOrgs().add(organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)));

        // get the children
        String chartOfAccountsCode = organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)).getChartOfAccountsCode();
        String organizationCode = organizationSelectionTreeForm.getSelectionSubTreeOrgs().get(this.getSelectedLine(request)).getOrganizationCode();
        organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BudgetOrganizationTreeService.class).getPullupChildOrgs(personUniversalIdentifier, chartOfAccountsCode, organizationCode));
        organizationSelectionTreeForm.populateSelectionSubTreeOrgs();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Handles navigation back to a previous branch BudgetConstructionPullup row displaying the associated parent and it's siblings
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward navigateUp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        String personUniversalIdentifier = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        // reset any set pullflags in the database before navigation
        SpringContext.getBean(BudgetOrganizationTreeService.class).resetPullFlag(personUniversalIdentifier);

        // pop the parent org off the branch stack
        int popIdx = this.getSelectedLine(request);
        BudgetConstructionPullup previousBranchOrg = organizationSelectionTreeForm.getPreviousBranchOrgs().remove(popIdx);
        if (popIdx == 0) {
            organizationSelectionTreeForm.setPreviousBranchOrgs(new TypedArrayList(BudgetConstructionPullup.class));

            // reinitialize the selection tool to the root
            HashMap map = new HashMap();
            map.put("chartOfAccountsCode", previousBranchOrg.getChartOfAccountsCode());
            map.put("organizationCode", previousBranchOrg.getOrganizationCode());
            map.put("personUniversalIdentifier", personUniversalIdentifier);
            organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BusinessObjectService.class).findMatching(BudgetConstructionPullup.class, map));
            organizationSelectionTreeForm.populateSelectionSubTreeOrgs();

        }
        else {
            organizationSelectionTreeForm.setPreviousBranchOrgs(organizationSelectionTreeForm.getPreviousBranchOrgs().subList(0, popIdx));

            // get the parent and parent siblings
            String chartOfAccountsCode = previousBranchOrg.getReportsToChartOfAccountsCode();
            String organizationCode = previousBranchOrg.getReportsToOrganizationCode();
            organizationSelectionTreeForm.setSelectionSubTreeOrgs((List<BudgetConstructionPullup>) SpringContext.getBean(BudgetOrganizationTreeService.class).getPullupChildOrgs(personUniversalIdentifier, chartOfAccountsCode, organizationCode));
            organizationSelectionTreeForm.populateSelectionSubTreeOrgs();
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.YES.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears the pullFlag for all displayed subtree organizations
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward clearAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.NO.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectPullOrgAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.ORG.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectPullSubOrgAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.SUBORG.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectPullBothAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.BOTH.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectPushOrgLevAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.ORGLEV.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectPushMgrLevAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.MGRLEV.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectPushOrgMgrLevAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.ORGMGRLEV.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectPushLevOneAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.LEVONE.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlag for all displayed subtree organizations to the setting implied by the method name.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward selectPushLevZeroAll(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;
        setPullFlag(organizationSelectionTreeForm.getSelectionSubTreeOrgs(), OrgSelControlOption.LEVZERO.getKey());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the pullFlags for each of the objects in the list to the pullFlagValue.
     * 
     * @param selOrgs
     * @param pullFlagValue
     */
    protected void setPullFlag(List<BudgetConstructionPullup> selOrgs, Integer pullFlagValue) {
        for (int i = 0; i < selOrgs.size(); i++) {
            selOrgs.get(i).setPullFlag(pullFlagValue);
        }
    }

    /**
     * Checks that at least one organization is selected and stores the selection settings. If no organization is selected, an error
     * message is displayed to the user.
     * 
     * @param selectionSubTreeOrgs
     * @return boolean - true if there was a selection and the list was saved, otherwise false
     */
    protected boolean storedSelectedOrgs(List<BudgetConstructionPullup> selectionSubTreeOrgs) {
        boolean foundSelected = false;

        // check to see if at least one pullflag is set and store the pullflag settings for currently displayed set of orgs
        for (BudgetConstructionPullup budgetConstructionPullup : selectionSubTreeOrgs) {
            if (budgetConstructionPullup.getPullFlag() > 0) {
                foundSelected = true;
            }
        }

        // if selection was found, save the org tree seletions, otherwise build error message
        if (foundSelected) {
            SpringContext.getBean(BusinessObjectService.class).save(selectionSubTreeOrgs);
        }
        else {
            GlobalVariables.getErrorMap().putError(BCConstants.SELECTION_SUB_TREE_ORGS, BCKeyConstants.ERROR_BUDGET_ORG_NOT_SELECTED);
        }

        return foundSelected;
    }

    /**
     * Checks the selection and calls the Position Pick list screen action.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performPositionPick(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, organizationSelectionTreeForm, BCConstants.TempListLookupMode.POSITION_SELECT, BudgetConstructionPositionSelect.class.getName(), null);

        return new ActionForward(url, true);
    }

    /**
     * Checks the selection and calls the Budgeted Incumbents Pick list screen action.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performIncumbentPick(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, organizationSelectionTreeForm, BCConstants.TempListLookupMode.INTENDED_INCUMBENT_SELECT, BudgetConstructionIntendedIncumbentSelect.class.getName(), null);

        return new ActionForward(url, true);
    }

    /**
     * Checks the selection and calls the Budget Documents pick list screen action.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performShowBudgetDocs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // build table but give a message if empty
        int rowCount = SpringContext.getBean(OrganizationBCDocumentSearchService.class).buildAccountSelectPullList(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier(), organizationSelectionTreeForm.getUniversityFiscalYear());
        if (rowCount == 0) {
            GlobalVariables.getMessageList().add("error.inquiry");
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, organizationSelectionTreeForm, BCConstants.TempListLookupMode.ACCOUNT_SELECT_BUDGETED_DOCUMENTS, BudgetConstructionAccountSelect.class.getName(), null);

        return new ActionForward(url, true);
    }

    /**
     * Checks the selection and performs the Pull up screen action.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performPullUp(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // get the needed params from the form and environment
        String pointOfViewCharOfAccountsCode = organizationSelectionTreeForm.getPointOfViewOrg().getChartOfAccountsCode();
        String pointOfViewOrganizationCode = organizationSelectionTreeForm.getPointOfViewOrg().getOrganizationCode();
        Integer bcFiscalYear = organizationSelectionTreeForm.getUniversityFiscalYear();
        String personUniversalIdentifier = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        SpringContext.getBean(BudgetPushPullService.class).pullupSelectedOrganizationDocuments(personUniversalIdentifier, bcFiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);

        // build Budgeted Account list of Documents set at level that is less than the user's point of view
        // build process should return number of accounts in list, if non-zero call display otherwise add successful pullup message
        // if no accounts are on the list
        int rowCount = SpringContext.getBean(BudgetPushPullService.class).buildPullUpBudgetedDocuments(personUniversalIdentifier, bcFiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
        if (rowCount != 0) {
            String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, organizationSelectionTreeForm, BCConstants.TempListLookupMode.ACCOUNT_SELECT_PULLUP_DOCUMENTS, BudgetConstructionAccountSelect.class.getName(), null);

            return new ActionForward(url, true);
        }
        
        GlobalVariables.getMessageList().add(BCKeyConstants.MSG_ORG_PULL_UP_SUCCESSFUL);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Checks at least one org was selected then calls organization push/pull service to built the account list for budgeted
     * documents below the user's point of view and forwards to the temp list action to display the results.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performShowPullUpBudgetDocs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        String pointOfViewCharOfAccountsCode = organizationSelectionTreeForm.getPointOfViewOrg().getChartOfAccountsCode();
        String pointOfViewOrganizationCode = organizationSelectionTreeForm.getPointOfViewOrg().getOrganizationCode();
        Integer bcFiscalYear = organizationSelectionTreeForm.getUniversityFiscalYear();
        String personUniversalIdentifier = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        // call service to build account list data
        int rowCount = SpringContext.getBean(BudgetPushPullService.class).buildPullUpBudgetedDocuments(personUniversalIdentifier, bcFiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
        if (rowCount == 0) {
            String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(BCKeyConstants.ERROR_NO_ACCOUNTS_PULL_UP);
            message = MessageFormat.format(message, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
            organizationSelectionTreeForm.addMessage(message);

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, organizationSelectionTreeForm, BCConstants.TempListLookupMode.ACCOUNT_SELECT_PULLUP_DOCUMENTS, BudgetConstructionAccountSelect.class.getName(), null);

        return new ActionForward(url, true);
    }

    /**
     * Checks the selection and performs the Push down screen action.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performPushDown(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // get the needed params from the form and environment
        String pointOfViewCharOfAccountsCode = organizationSelectionTreeForm.getPointOfViewOrg().getChartOfAccountsCode();
        String pointOfViewOrganizationCode = organizationSelectionTreeForm.getPointOfViewOrg().getOrganizationCode();
        Integer bcFiscalYear = organizationSelectionTreeForm.getUniversityFiscalYear();
        String personUniversalIdentifier = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        SpringContext.getBean(BudgetPushPullService.class).pushdownSelectedOrganizationDocuments(personUniversalIdentifier, bcFiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);

        // build Budgeted Account list of Documents set at level that is less than the user's point of view
        // build process should return number of accounts in list, if non-zero call display
        // otherwise add successful pullup message if no accounts are on the list
        int rowCount = SpringContext.getBean(BudgetPushPullService.class).buildPushDownBudgetedDocuments(personUniversalIdentifier, bcFiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
        if (rowCount != 0) {
            String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, organizationSelectionTreeForm, BCConstants.TempListLookupMode.ACCOUNT_SELECT_PUSHDOWN_DOCUMENTS, BudgetConstructionAccountSelect.class.getName(), null);

            return new ActionForward(url, true);        
        }
        
        GlobalVariables.getMessageList().add(BCKeyConstants.MSG_ORG_PUSH_DOWN_SUCCESSFUL);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Checks at least one org was selected then calls organization push/pull service to built the account list for budgeted
     * documents at the user's point of view and forwards to the temp list action to display the results.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performShowPushDownBudgetDocs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        if (!storedSelectedOrgs(organizationSelectionTreeForm.getSelectionSubTreeOrgs())) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        String pointOfViewCharOfAccountsCode = organizationSelectionTreeForm.getPointOfViewOrg().getChartOfAccountsCode();
        String pointOfViewOrganizationCode = organizationSelectionTreeForm.getPointOfViewOrg().getOrganizationCode();
        Integer bcFiscalYear = organizationSelectionTreeForm.getUniversityFiscalYear();
        String personUniversalIdentifier = GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier();

        // call service to build account list data
        int rowCount = SpringContext.getBean(BudgetPushPullService.class).buildPushDownBudgetedDocuments(personUniversalIdentifier, bcFiscalYear, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
        if (rowCount == 0) {
            String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(BCKeyConstants.ERROR_NO_ACCOUNTS_PUSH_DOWN);
            message = MessageFormat.format(message, pointOfViewCharOfAccountsCode, pointOfViewOrganizationCode);
            organizationSelectionTreeForm.addMessage(message);

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // forward to temp list action for displaying results
        String url = BudgetUrlUtil.buildTempListLookupUrl(mapping, organizationSelectionTreeForm, BCConstants.TempListLookupMode.ACCOUNT_SELECT_PUSHDOWN_DOCUMENTS, BudgetConstructionAccountSelect.class.getName(), null);

        return new ActionForward(url, true);
    }

    /**
     * Handles forwarding to account list or report selection screen.
     */
    public ActionForward performReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OrganizationSelectionTreeForm organizationSelectionTreeForm = (OrganizationSelectionTreeForm) form;

        List<BudgetConstructionPullup> selectionSubTreeOrgs = organizationSelectionTreeForm.getSelectionSubTreeOrgs();
        if (!storedSelectedOrgs(selectionSubTreeOrgs)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // set up buildhelper to be used by called action
        ReportControlListBuildHelper buildHelper = (ReportControlListBuildHelper) GlobalVariables.getUserSession().retrieveObject(BCConstants.Report.CONTROL_BUILD_HELPER_SESSION_NAME);
        if (buildHelper == null) {
            buildHelper = new ReportControlListBuildHelper();
        }

        BudgetConstructionReportMode reportMode = setupReportMode(request, organizationSelectionTreeForm);
        buildHelper.addBuildRequest(organizationSelectionTreeForm.getCurrentPointOfViewKeyCode(), removeUnselectedSubTreeOrgs(selectionSubTreeOrgs), reportMode.reportBuildMode);
        GlobalVariables.getUserSession().addObject(BCConstants.Report.CONTROL_BUILD_HELPER_SESSION_NAME, buildHelper);

        // check if there are any accounts above user's point of view, if so forward to account listing page. if not, forward to
        // report select(subfund or object code) screen
        String[] pointOfViewFields = organizationSelectionTreeForm.getCurrentPointOfViewKeyCode().split("[-]");
        int rowCount = SpringContext.getBean(OrganizationBCDocumentSearchService.class).buildBudgetedAccountsAbovePointsOfView(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier(), organizationSelectionTreeForm.getUniversityFiscalYear(), pointOfViewFields[0], pointOfViewFields[1]);

        // in case of 2PLG or Sync report should move to account list page.
        boolean forceToAccountListScreen = false;
        String reportModeName = organizationSelectionTreeForm.getReportMode();
        if (reportModeName.equals(BudgetConstructionReportMode.TWOPLG_LIST_REPORT.reportModeName) || reportModeName.equals(BudgetConstructionReportMode.SYNCHRONIZATION_PROBLEMS_REPORT.reportModeName)) {
            forceToAccountListScreen = true;
        }

        String forwardURL = "";
        if (rowCount != 0 || forceToAccountListScreen) {
            forwardURL = buildAccountListForwardURL(organizationSelectionTreeForm, mapping, forceToAccountListScreen);
        }
        else {
            forwardURL = buildReportSelectForwardURL(organizationSelectionTreeForm, mapping);
        }

        return new ActionForward(forwardURL, true);
    }

    /**
     * Removes unselected SubTreeOrgs since selectionSubTreeOrgs contains all SubTreeOrgs.
     * 
     * @param selectionSubTreeOrgs
     * @return
     */
    public List<BudgetConstructionPullup> removeUnselectedSubTreeOrgs(List<BudgetConstructionPullup> selectionSubTreeOrgs) {
        List<BudgetConstructionPullup> returnList = new ArrayList();
        for (BudgetConstructionPullup pullUp : selectionSubTreeOrgs) {
            if (pullUp.getPullFlag() > 0) {
                returnList.add(pullUp);
            }
        }

        return returnList;
    }

    /**
     * Parses the report name from the methodToCall request parameter and retrieves the associated ReportMode.
     * 
     * @param request - HttpServletRequest containing the methodToCall parameter
     * @param organizationSelectionTreeForm - OrganizationSelectionTreeForm to set report mode on
     * @return BudgetConstructionReportMode - mode associated with parsed report name
     */
    private BudgetConstructionReportMode setupReportMode(HttpServletRequest request, OrganizationSelectionTreeForm organizationSelectionTreeForm) {
        String fullParameter = (String) request.getAttribute(KNSConstants.METHOD_TO_CALL_ATTRIBUTE);
        String reportName = StringUtils.substringBetween(fullParameter, KNSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KNSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        organizationSelectionTreeForm.setReportMode(reportName);

        return BudgetConstructionReportMode.getBudgetConstructionReportModeByName(organizationSelectionTreeForm.getReportMode());
    }

    /**
     * Builds URL for the action listing action.
     */
    private String buildAccountListForwardURL(OrganizationSelectionTreeForm organizationSelectionTreeForm, ActionMapping mapping, boolean forceToAccountListScreen) {
        Map<String, String> urlParms = new HashMap<String, String>();
        urlParms.put(BCConstants.Report.REPORT_MODE, organizationSelectionTreeForm.getReportMode());
        urlParms.put(BCConstants.CURRENT_POINT_OF_VIEW_KEYCODE, organizationSelectionTreeForm.getCurrentPointOfViewKeyCode());

        if (forceToAccountListScreen) {
            urlParms.put(BCConstants.FORCE_TO_ACCOUNT_LIST_SCREEN, "true");
        }
        else {
            urlParms.put(BCConstants.FORCE_TO_ACCOUNT_LIST_SCREEN, "false");
        }

        if ((organizationSelectionTreeForm.getReportMode().equals(BudgetConstructionReportMode.ACCOUNT_SUMMARY_REPORT.reportModeName) && organizationSelectionTreeForm.isAccountSummaryConsolidation()) || (organizationSelectionTreeForm.getReportMode().equals(BudgetConstructionReportMode.ACCOUNT_OBJECT_DETAIL_REPORT.reportModeName) && organizationSelectionTreeForm.isAccountObjectDetailConsolidation()) || (organizationSelectionTreeForm.getReportMode().equals(BudgetConstructionReportMode.MONTH_SUMMARY_REPORT.reportModeName) && organizationSelectionTreeForm.isMonthObjectSummaryConsolidation())) {
            urlParms.put(BCConstants.Report.REPORT_CONSOLIDATION, "true");
        }

        return BudgetUrlUtil.buildTempListLookupUrl(mapping, organizationSelectionTreeForm, BCConstants.TempListLookupMode.ACCOUNT_SELECT_ABOVE_POV, BudgetConstructionAccountSelect.class.getName(), urlParms);
    }

    /**
     * Builds URL for the organization selection action.
     */
    private String buildReportSelectForwardURL(OrganizationSelectionTreeForm organizationSelectionTreeForm, ActionMapping mapping) {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(BCConstants.Report.REPORT_MODE, organizationSelectionTreeForm.getReportMode());
        parameters.put(BCConstants.CURRENT_POINT_OF_VIEW_KEYCODE, organizationSelectionTreeForm.getCurrentPointOfViewKeyCode());

        if ((organizationSelectionTreeForm.getReportMode().equals(BudgetConstructionReportMode.ACCOUNT_SUMMARY_REPORT.reportModeName) && organizationSelectionTreeForm.isAccountSummaryConsolidation()) || (organizationSelectionTreeForm.getReportMode().equals(BudgetConstructionReportMode.ACCOUNT_OBJECT_DETAIL_REPORT.reportModeName) && organizationSelectionTreeForm.isAccountObjectDetailConsolidation()) || (organizationSelectionTreeForm.getReportMode().equals(BudgetConstructionReportMode.MONTH_SUMMARY_REPORT.reportModeName) && organizationSelectionTreeForm.isMonthObjectSummaryConsolidation())) {
            parameters.put(BCConstants.Report.REPORT_CONSOLIDATION, "true");
        }

        return BudgetUrlUtil.buildBudgetUrl(mapping, organizationSelectionTreeForm, BCConstants.ORG_REPORT_SELECTION_ACTION, parameters);
    }

}
