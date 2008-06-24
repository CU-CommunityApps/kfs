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
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.pdp.GeneralUtilities;
import org.kuali.kfs.pdp.businessobject.PaymentDetailSearch;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;
import org.kuali.kfs.pdp.service.PaymentDetailSearchService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;


/**
 * @author delyea
 */
public class SessionTestAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SessionTestAction.class);

    private PaymentDetailSearchService paymentDetailSearchService;

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return true;
    }

    private int getSearchResultsPerPage() {
        return GeneralUtilities.getParameterInteger(SpringContext.getBean(ParameterService.class), ParameterConstants.PRE_DISBURSEMENT_LOOKUP.class, "SEARCH_RESULTS_PER_PAGE");
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.info("executeLogic() starting");
        String forward = "test";
        String searchStatus = (String) request.getParameter("searchStatus");
        HttpSession session = request.getSession();
        HttpSession session2 = request.getSession(false);
        StringBuffer displayString = new StringBuffer();

        displayString.append("<br><br><br>");
        displayString.append("COMING INTO ACTION...");
        displayString.append("<br><br><br>");
        displayString.append("Variable 'session' has attribute names:<br>");
        String str = "SESSION ID = " + session.getId() + "  and created " + (new Timestamp(session.getCreationTime())).toString();
        displayString.append(str + "<br>");
        Enumeration enumer = session.getAttributeNames();
        while (enumer.hasMoreElements()) {
            String paramName = (String) enumer.nextElement();
            displayString.append(paramName + "<br>");
        }
        displayString.append("<br><br><br>");
        displayString.append("Variable 'session2' has attribute names:<br>");
        str = "SESSION ID = " + session2.getId() + "  and created " + (new Timestamp(session2.getCreationTime())).toString();
        displayString.append(str + "<br>");
        Enumeration enumer2 = session2.getAttributeNames();
        while (enumer2.hasMoreElements()) {
            String paramName = (String) enumer2.nextElement();
            displayString.append(paramName + "<br>");
        }
        displayString.append("<br><br><br>");

        LOG.debug("executeLogic() " + str);
        Object perPage = session.getAttribute("perPage");
        if (perPage != null) {
            str = " Variable 'perPage' = " + perPage;
        }
        else {
            Integer variable = getSearchResultsPerPage();
            str = " Variable 'perPage' is null setting as = " + variable;
            session.setAttribute("perPage", variable);
        }
        displayString.append(str + "<br><br>");

        if (searchStatus == null) {
            searchStatus = "HELD";
        }
        displayString.append("Using search status '" + searchStatus + "'<br><br>");

        String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);
        PaymentDetailSearchForm pdsf = (PaymentDetailSearchForm) form;
        LOG.debug("executeLogic() pdsf is " + pdsf);
        LOG.debug("executeLogic() buttonPressed is " + buttonPressed);
        if (buttonPressed.startsWith("btnSubmit")) {
            // Code for Searching for Individual Payments
            PaymentDetailSearch pds = new PaymentDetailSearch();
            pds.setPaymentStatusCode(searchStatus);
            List searchResults = paymentDetailSearchService.getAllPaymentsForSearchCriteria(pds);

            session.removeAttribute("indivSearchResults");
            displayString.append("removing attribute 'indivSearchResults' in 'session' variable<br>");
            if (searchResults != null) {
                displayString.append("Search returned having found " + searchResults.size() + " results<br><br>");
                session.setAttribute("indivSearchResults", searchResults);
                displayString.append("set up attribute 'indivSearchResults' in 'session' variable<br>");
            }
            else {
                displayString.append("Search returned having found NO results<br><br>");
            }
            pdsf.setPaymentStatusCode(searchStatus);
            session.setAttribute("PaymentDetailSearchFormSession", pdsf);
            displayString.append("set up attribute 'PaymentDetailSearchFormSession' in 'session' variable<br>");
        }
        request.setAttribute("PaymentDetailSearchForm", pdsf);
        displayString.append("<br><br><br>");
        displayString.append("BEFORE USING FORWARD...");
        displayString.append("<br><br><br>");
        displayString.append("Variable 'session' has attribute names:<br>");
        str = "SESSION ID = " + session.getId() + "  and created " + (new Timestamp(session.getCreationTime())).toString();
        displayString.append(str + "<br>");
        Enumeration enumer3 = session.getAttributeNames();
        while (enumer3.hasMoreElements()) {
            String paramName = (String) enumer3.nextElement();
            displayString.append(paramName + "<br>");
        }
        displayString.append("<br><br><br>");
        displayString.append("Variable 'session2' has attribute names:<br>");
        str = "SESSION ID = " + session2.getId() + "  and created " + (new Timestamp(session2.getCreationTime())).toString();
        displayString.append(str + "<br>");
        Enumeration enumer4 = session2.getAttributeNames();
        while (enumer4.hasMoreElements()) {
            String paramName = (String) enumer4.nextElement();
            displayString.append(paramName + "<br>");
        }
        displayString.append("<br><br><br>");
        request.setAttribute("logMessages", displayString.toString());
        return mapping.findForward(forward);
    }

    public void setPaymentDetailSearchService(PaymentDetailSearchService p) {
        paymentDetailSearchService = p;
    }
}
