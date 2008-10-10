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
/*
 * Created on Aug 2, 2004
 *
 */
package org.kuali.kfs.pdp.web.struts;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.kfs.pdp.GeneralUtilities;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.BatchSearch;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;
import org.kuali.kfs.pdp.document.service.BatchMaintenanceService;
import org.kuali.kfs.pdp.service.BatchSearchService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;


/**
 * @author delyea
 */
public class BatchSearchAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSearchAction.class);

    private BatchSearchService batchSearchService;
    private BatchMaintenanceService batchMaintenanceService;

    public BatchSearchAction() {
        setBatchSearchService(SpringContext.getBean(BatchSearchService.class));
        setBatchMaintenanceService(SpringContext.getBean(BatchMaintenanceService.class));
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isLimitedViewRole() || sr.isViewAllRole() || sr.isViewIdRole() || sr.isViewBankRole()||sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("executeLogic() starting");
        String forward = "search";
        HttpSession session = request.getSession();
        List searchResults = null;
        Object perPage = session.getAttribute("perPage");
        if ((perPage == null) || (perPage.toString() == "")) {
            session.setAttribute("perPage", getSearchResultsPerPage());
        }

        ActionErrors actionErrors = new ActionErrors();
        String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);

        BatchSearchForm bsf = (BatchSearchForm) form;
        LOG.debug("executeLogic() bsf is " + bsf);
        LOG.debug("executeLogic() Type of search is Batch");
        LOG.debug("executeLogic() buttonPressed is " + buttonPressed);

        if ((buttonPressed.endsWith("Hold")) || (buttonPressed.endsWith("Cancel"))) {
            // Code to display updated individual search results
            LOG.debug("executeLogic() Returned to Search from Update Screen");
            session.removeAttribute("batchIndivSearchResults");
            bsf = (BatchSearchForm) session.getAttribute("BatchSearchFormSession");
            if (bsf == null) {
                LOG.info("executeLogic() BatchSearchForm 'bsf' from session is null");
                return mapping.findForward("pdp_session_timeout");
            }
            else {
                Batch btch = (Batch) session.getAttribute("BatchDetail");
                Integer bId = null;
                if (btch != null) {
                    // If we still have the batch then get the payments in it again to update statuses
                    bId = btch.getId().intValue();
                    LOG.debug("executeLogic() Batch ID being tested is " + bId);
                    searchResults = batchSearchService.getAllSingleBatchPayments(bId);
                    searchResults = checkList(searchResults, session, "Indiv", actionErrors);
                    if (batchMaintenanceService.doBatchPaymentsHaveHeldStatus(bId)) {
                        request.setAttribute("allAreHeld", "true");
                    }
                    return mapping.findForward("batchdetail");
                }
                else {
                    BatchSearch bs = bsf.getBatchSearch();
                    searchResults = batchSearchService.getAllBatchesForSearchCriteria(bs);
                    searchResults = checkList(searchResults, session, "", actionErrors);
                }
            }
        }
        else if (buttonPressed.startsWith("btnUpdate")) {
            // Code to display updated individual search results
            LOG.debug("executeLogic() Returned to Search from Update Screen");
            session.removeAttribute("batchIndivSearchResults");
            bsf = (BatchSearchForm) session.getAttribute("BatchSearchFormSession");
            if (bsf.equals(null)) {
                LOG.info("executeLogic() BatchSearchForm 'bsf' from session is null");
                return mapping.findForward("pdp_session_timeout");
            }
            else {
                Batch btch = (Batch) session.getAttribute("BatchDetail");
                Integer bId = null;
                if (btch != null) {
                    // If we still have the batch then get the payments in it again to update statuses
                    bId = btch.getId().intValue();
                    LOG.debug("executeLogic() Batch ID being tested is " + bId);
                    searchResults = batchSearchService.getAllSingleBatchPayments(bId);
                    searchResults = checkList(searchResults, session, "Indiv", actionErrors);
                    if (batchMaintenanceService.doBatchPaymentsHaveHeldStatus(bId)) {
                        request.setAttribute("allAreHeld", "true");
                    }
                    return mapping.findForward("batchdetail");
                }
                else {
                    BatchSearch bs = bsf.getBatchSearch();
                    searchResults = batchSearchService.getAllBatchesForSearchCriteria(bs);
                    searchResults = checkList(searchResults, session, "", actionErrors);
                }
            }
        }
        else {
            if (buttonPressed.startsWith("btnSearch")) {
                // Code for Searching for Batches
                clearObjects(session, actionErrors);
                BatchSearch bs = bsf.getBatchSearch();
                searchResults = batchSearchService.getAllBatchesForSearchCriteria(bs);

                searchResults = checkList(searchResults, session, "", actionErrors);
                session.setAttribute("BatchSearchFormSession", bsf);
            }
            else if (buttonPressed.startsWith("btnBatchDetail")) {
                session.removeAttribute("batchIndivSearchResults");
                Integer bId = GeneralUtilities.convertStringToInteger(request.getParameter("BatchId"));
                List l = (List) session.getAttribute("batchSearchResults");

                if (l == null) {
                    LOG.info("executeLogic() Batch Search Results variable 'l' from session is null");
                    return mapping.findForward("pdp_session_timeout");
                }
                else {
                    if (bId == null) {
                        return mapping.findForward("pdp_system_error");
                    }
                    else {
                        for (Iterator iter = l.iterator(); iter.hasNext();) {
                            Batch element = (Batch) iter.next();
                            if (element.getId().equals(bId)) {
                                LOG.debug("executeLogic() Found Batch relating to batchId " + bId);
                                session.setAttribute("BatchDetail", element);
                                LOG.debug("executeLogic() Batch contains " + element.getPaymentCount() + " payment(s)");
                            }
                        }
                        searchResults = batchSearchService.getAllSingleBatchPayments(bId);
                        searchResults = checkList(searchResults, session, "Indiv", actionErrors);
                        if (batchMaintenanceService.doBatchPaymentsHaveHeldStatus(bId)) {
                            request.setAttribute("allAreHeld", "true");
                        }
                    }
                }
                return mapping.findForward("batchdetail");
            }
            else if (buttonPressed.startsWith("btnBack")) {
                bsf = (BatchSearchForm) session.getAttribute("BatchSearchFormSession");
                if (bsf != null) {
                    // Code to use BreadCrumb Links
                    if (buttonPressed.endsWith("Indiv")) {
                        return mapping.findForward("batchdetail");
                    }
                }
                else {
                    LOG.info("executeLogic() BatchSearchForm 'bsf' from session is null");
                    return mapping.findForward("pdp_session_timeout");
                }
            }
            else if (buttonPressed.startsWith("btnClear")) {
                // Code to clear the form
                bsf.clearForm();
                session.removeAttribute("BatchSearchFormSession");
            }
            else {
                clearObjects(session, actionErrors);
            }
        }

        // If we had errors, save them.
        if (!actionErrors.isEmpty()) {
            saveErrors(request, actionErrors);
            for (Iterator iter = actionErrors.get(); iter.hasNext();) {
                ActionMessage element = (ActionMessage) iter.next();
                LOG.debug("executeLogic() ActionErrors Element = " + element.getKey());
            }
        }

        request.setAttribute("BatchSearchForm", bsf);
        return mapping.findForward(forward);
    }

    /**
     * Clear stored session objects as well as actionErrors.
     * 
     * @param request
     * @return
     */
    protected void clearObjects(HttpSession session, ActionErrors actionErrors) {
        // Individual Search Variables in Session
        session.removeAttribute("indivSearchResults");
        session.removeAttribute("PaymentDetailSearchFormSession");

        // Batch Search Variables in Session
        session.removeAttribute("batchSearchResults");
        session.removeAttribute("batchIndivSearchResults");
        session.removeAttribute("BatchDetail");
        session.removeAttribute("BatchSearchFormSession");

        actionErrors.clear();
    }

    /**
     * Takes in the list from Search & the Search Type and updates appropriate variables and the list itself.
     * 
     * @param request
     * @param searchResults
     * @param searchType
     * @return searchResults
     */
    protected List checkList(List searchResults, HttpSession session, String resultListType, ActionErrors actionErrors) {
        session.removeAttribute("batch" + resultListType + "SearchResults");
        int searchSize = getMaxSearchTotal();
        int maxSize = searchSize + 1;
        int returnSize = searchSize - 1;

        if (searchResults != null) {
            LOG.debug("executeLogic() Search returned having found " + searchResults.size() + " results");
            if (searchResults.size() == 0) {
                actionErrors.add("errors", new ActionMessage("BatchSearchAction.emptyresults.invalid"));
            }
            else if (searchResults.size() < maxSize) {
                session.setAttribute("batch" + resultListType + "SearchResults", searchResults);
            }
            else {
                if (resultListType != "") {
                    session.setAttribute("batch" + resultListType + "SearchResults", searchResults);
                }
                else {
                    actionErrors.add("errors", new ActionMessage("BatchSearchAction.listover.invalid"));
                    session.setAttribute("batch" + resultListType + "SearchResults", searchResults.subList(0, returnSize));
                }
            }
        }
        else {
            actionErrors.add("errors", new ActionMessage("BatchSearchAction.emptyresults.invalid"));
        }
        return searchResults;
    }

    private int getSearchResultsPerPage() {
        return GeneralUtilities.getParameterInteger(SpringContext.getBean(ParameterService.class), ParameterConstants.PRE_DISBURSEMENT_LOOKUP.class, PdpConstants.ApplicationParameterKeys.SEARCH_RESULTS_PER_PAGE);
    }

    private int getMaxSearchTotal() {
        return GeneralUtilities.getParameterInteger(SpringContext.getBean(ParameterService.class), ParameterConstants.PRE_DISBURSEMENT_LOOKUP.class, PdpConstants.ApplicationParameterKeys.SEARCH_RESULTS_TOTAL);
    }

    public void setBatchSearchService(BatchSearchService b) {
        batchSearchService = b;
    }

    /**
     * @param batchMaintenanceService The batchMaintenanceService to set.
     */
    public void setBatchMaintenanceService(BatchMaintenanceService batchMaintenanceService) {
        this.batchMaintenanceService = batchMaintenanceService;
    }
}
