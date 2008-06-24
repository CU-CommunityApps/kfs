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

import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.web.struts.BaseAction;
import org.kuali.kfs.pdp.businessobject.PaymentDetailSearch;
import org.kuali.kfs.pdp.exception.ConfigurationError;
import org.kuali.kfs.pdp.web.struts.PaymentDetailSearchForm;
import org.kuali.kfs.pdp.service.PaymentDetailSearchService;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;
import org.kuali.kfs.pdp.GeneralUtilities;


/**
 * @author delyea
 */
public class PaymentSearchAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentSearchAction.class);

    private PaymentDetailSearchService paymentDetailSearchService;

    public PaymentSearchAction() {
        setPaymentDetailSearchService(SpringContext.getBean(PaymentDetailSearchService.class));
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isLimitedViewRole() || sr.isViewAllRole() || sr.isViewIdRole() || sr.isViewBankRole()||sr.isSysAdminRole();
    }

    private int getSearchResultsPerPage() {
        return getParameterInteger(ParameterConstants.PRE_DISBURSEMENT_LOOKUP.class, PdpConstants.ApplicationParameterKeys.SEARCH_RESULTS_PER_PAGE);
    }

    private int getMaxSearchTotal() {
        return getParameterInteger(ParameterConstants.PRE_DISBURSEMENT_LOOKUP.class, PdpConstants.ApplicationParameterKeys.SEARCH_RESULTS_TOTAL);
    }

    private int getParameterInteger(Class componentClass, String parm) {
        String srpp = SpringContext.getBean(ParameterService.class).getParameterValue(componentClass, parm);
        if (srpp != null) {
            try {
                return Integer.parseInt(srpp);
            }
            catch (NumberFormatException e) {
                throw new ConfigurationError(parm + " is not a number");
            }
        }
        else {
            throw new ConfigurationError("Unable to find " + parm);
        }
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("executeLogic() starting");

        String forward = "search";
        HttpSession session = request.getSession();
        LOG.debug("executeLogic() ************* SESSION ID = " + session.getId() + "  and created " + (new Timestamp(session.getCreationTime())).toString());
        // before we attempt to do a search first validate the form fields.
        PaymentDetailSearchForm searchForm = (PaymentDetailSearchForm) form;
        ActionMessages errors = searchForm.validate(mapping, request);
        if (errors != null && errors.size() > 0) {
            request.setAttribute("PaymentDetailSearchForm", searchForm);
            return null;
        }
        List searchResults = null;
        Object perPage = session.getAttribute("perPage");
        if ((perPage == null) || (perPage.toString() == "")) {
            session.setAttribute("perPage", getSearchResultsPerPage());
        }

        ActionMessages actionErrors = new ActionMessages();
        String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);

        PaymentDetailSearchForm pdsf = (PaymentDetailSearchForm) form;
        LOG.debug("executeLogic() pdsf is " + pdsf);
        LOG.debug("executeLogic() buttonPressed is " + buttonPressed);

        if (buttonPressed.startsWith("btnSearch")) {
            // Code for Searching for Individual Payments
            clearObjects(session, actionErrors);
            PaymentDetailSearch pds = pdsf.getPaymentDetailSearch();
            searchResults = paymentDetailSearchService.getAllPaymentsForSearchCriteria(pds);

            searchResults = checkList(searchResults, session, actionErrors);
            if (GeneralUtilities.isStringEmpty(pdsf.getDisbursementNbr())) {
                pdsf.setOldDisbursementNbr("");
            }
            session.setAttribute("PaymentDetailSearchFormSession", pdsf);
        }
        else if (buttonPressed.startsWith("btnClear")) {
            // Code to clear the form
            session.removeAttribute("PaymentDetailSearchFormSession");
            Boolean b = pdsf.getAdvancedSearch();
            String oldDisbursementNbr = pdsf.getOldDisbursementNbr();
            pdsf.clearForm();
            pdsf.setOldDisbursementNbr(oldDisbursementNbr);
            pdsf.setAdvancedSearch(b);
        }
        else if (buttonPressed.startsWith("btnBack")) {
            pdsf = (PaymentDetailSearchForm) session.getAttribute("PaymentDetailSearchFormSession");
            if (pdsf != null) {
                // Code to use BreadCrumb Links
                PaymentDetailSearch pds = pdsf.getPaymentDetailSearch();
                searchResults = paymentDetailSearchService.getAllPaymentsForSearchCriteria(pds);
                searchResults = checkList(searchResults, session, actionErrors);
            }
            else {
                LOG.info("executeLogic() PaymentDetailSearchForm 'pdsf' from session is null");
                return mapping.findForward("pdp_session_timeout");
            }
        }
        else if (buttonPressed.startsWith("btnUpdate")) {
            pdsf = (PaymentDetailSearchForm) session.getAttribute("PaymentDetailSearchFormSession");
            session.removeAttribute("indivSearchResults");
        }
        else {
            clearObjects(session, actionErrors);
        }

        // If we had errors, save them.
        if (!actionErrors.isEmpty()) {
            saveErrors(request, actionErrors);
            for (Iterator iter = actionErrors.get(); iter.hasNext();) {
                ActionMessage element = (ActionMessage) iter.next();
                LOG.debug("executeLogic() ActionErrors Element = " + element.getKey());
            }
        }

        request.setAttribute("PaymentDetailSearchForm", pdsf);
        return mapping.findForward(forward);
    }

    /**
     * Clear stored session objects as well as actionErrors.
     * 
     * @param request
     * @return
     */
    protected void clearObjects(HttpSession session, ActionMessages actionErrors) {

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
    protected List checkList(List searchResults, HttpSession session, ActionMessages actionErrors) {
        session.removeAttribute("indivSearchResults");
        Integer searchSize = getMaxSearchTotal();
        int maxSize = searchSize.intValue() + 1;
        int returnSize = searchSize.intValue() - 1;

        if (searchResults != null) {
            LOG.debug("executeLogic() Search returned having found " + searchResults.size() + " results");
            if (searchResults.size() == 0) {
                actionErrors.add("errors", new ActionMessage("PaymentSearchAction.emptyresults.invalid"));
            }
            else if (searchResults.size() < maxSize) {
                session.setAttribute("indivSearchResults", searchResults);
            }
            else {
                actionErrors.add("errors", new ActionMessage("PaymentSearchAction.listover.invalid"));
                session.setAttribute("indivSearchResults", searchResults.subList(0, returnSize));
            }
        }
        else {
            actionErrors.add("errors", new ActionMessage("PaymentSearchAction.emptyresults.invalid"));
        }
        return searchResults;
    }

    public void setPaymentDetailSearchService(PaymentDetailSearchService p) {
        paymentDetailSearchService = p;
    }
}
