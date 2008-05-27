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
package org.kuali.module.budget.web.struts.action;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants.ReportGeneration;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BudgetConstructionDocumentReportMode;
import org.kuali.module.budget.web.struts.form.ReportRunnerForm;

/**
 * Action class to display document reports and dumps menu
 */
public class ReportRunnerAction extends KualiAction {

    /**
     * Handles any special onetime setup when called from another screen action
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportRunnerForm reportRunnerForm = (ReportRunnerForm) form;

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Called by the close button on the reportrunner.jsp
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return returnToCaller((ReportRunnerForm) form);
    }

    /**
     * Sets up the parameters to pass back to the calling (parent) action.
     * 
     * @param reportRunnerForm
     * @return
     * @throws Exception
     */
    public ActionForward returnToCaller(ReportRunnerForm reportRunnerForm) throws Exception {

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, reportRunnerForm.getReturnFormKey());
        parameters.put(KFSConstants.ANCHOR, reportRunnerForm.getReturnAnchor());
        parameters.put(KFSConstants.REFRESH_CALLER, "ReportRunner");

        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.BC_DOCUMENT_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * Runs the reports or dump selected by the user using the BudgetConstructionDocumentReportMode to help determine the various
     * objects needed to actually build the report data and render the report.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward performReportDump(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ReportRunnerForm reportRunnerForm = (ReportRunnerForm) form;
        String personUserIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();

        int selectIndex = this.getSelectedLine(request);
        String reportModeName = reportRunnerForm.getBudgetConstructionDocumentReportModes().get(selectIndex).getReportModeName();

        // TODO add calls to services here. We need the build/clean for the account object detail report mt table and method(s)
        // to call the account object, funding, monthly reports
        // and method to call the dump screen for the account,funding and month dump.

        switch (selectIndex) {
            case 0: {
            }

            
            case 1: {
            }
            
            
            case 2: {
                
                
            }
        }


        // for report dumps foward to dump action to display formatting screen
        // if (reportRunnerForm.getBudgetConstructionDocumentReportModes().get(selectIndex).dump) {
        // String dumpUrl = this.buildReportDumpForwardURL(reportRunnerForm, mapping, reportModeName);
        // return new ActionForward(dumpUrl, true);
        // }

        // TODO call method to build mt and/or render the report
        // stuff below is just to test output works.

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("we are running report: ".getBytes());
        baos.write(reportModeName.getBytes());
        WebUtils.saveMimeOutputStreamAsFile(response, ReportGeneration.TEXT_MIME_TYPE, baos, "tgary.txt");
        return null;
    }

    /**
     * Builds URL for the report dump url.
     */
    private String buildReportDumpForwardURL(ReportRunnerForm reportRunnerForm, ActionMapping mapping, String documentReportMode) {
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(reportRunnerForm, BCConstants.FORMKEY_PREFIX));
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, reportRunnerForm.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
        parameters.put(BCConstants.Report.REPORT_MODE, documentReportMode);

        // TODO may need another parm to indicate this is a Budget Document dump, not Organization dump.
        // no driving mt table to dump multiple accounts, just one account (document) here

        return UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.REPORT_DUMP_ACTION, parameters);
    }
}
