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
 * Created on Aug 11, 2004
 *
 */
package org.kuali.kfs.pdp.web.struts;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.pdp.GeneralUtilities;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.SecurityRecord;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KualiInteger;


/**
 * @author delyea
 */
public class PaymentDetailAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailAction.class);
    private PaymentDetailService paymentDetailService;
    private PaymentGroupService paymentGroupService;
    private Map payees;

    public PaymentDetailAction() {
        setPaymentGroupService(SpringContext.getBean(PaymentGroupService.class));
        setPaymentDetailService(SpringContext.getBean(PaymentDetailService.class));

        // TODO This should probably be a table
        payees = new HashMap();
        payees.put(PdpConstants.PayeeIdTypeCodes.PAYEE_ID, "Payee ID");
        payees.put(PdpConstants.PayeeIdTypeCodes.SSN, "SSN");
        payees.put(PdpConstants.PayeeIdTypeCodes.EMPLOYEE_ID, "Employee ID");
        payees.put(PdpConstants.PayeeIdTypeCodes.FEIN, "FEIN");
        payees.put(PdpConstants.PayeeIdTypeCodes.VENDOR_ID, "Vendor ID");
        payees.put(PdpConstants.PayeeIdTypeCodes.OTHER, "Other");
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isLimitedViewRole() || sr.isViewAllRole() || sr.isViewIdRole() || sr.isViewBankRole()||sr.isSysAdminRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");
        HttpSession session = request.getSession();
        LOG.debug("executeLogic() ************* SESSION ID = " + session.getId() + "  and created " + (new Timestamp(session.getCreationTime())).toString());

        String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);
        LOG.debug("executeLogic() btnPressed is " + buttonPressed);
        Integer detailId;
        PaymentDetail pd = (PaymentDetail) session.getAttribute("PaymentDetail");
        List indivList = (List) session.getAttribute("indivSearchResults");
        List batchIndivList = (List) session.getAttribute("batchIndivSearchResults");

        if ((indivList == null) && (batchIndivList == null) && (pd == null)) {
            // Handle Session Expiration
            LOG.info("executeLogic() Payment Detail and both search results are null");
            return mapping.findForward("pdp_session_timeout");
        }
        else {
            if (request.getParameter("DetailId") != null) {
                // Payment Detail ID was passed - find payment Detail with the ID
                LOG.debug("executeLogic() Detail ID passed in Parms: " + request.getParameter("DetailId"));
                detailId = new Integer(request.getParameter("DetailId"));
                pd = paymentDetailService.get(detailId);

                pd.setLastDisbursementActionDate(this.getDisbursementActionExpirationDate(request));
                getPayeeDescriptor(session, pd);
                getDisbursementPaymentList(session, pd);
                session.setAttribute("size", new Integer(pd.getPaymentGroup().getPaymentDetails().size()));
                session.setAttribute("PaymentDetail", pd);
            }
            else if (pd == null) {
                // Handle Session Expiration
                LOG.info("executeLogic() Payment Detail object 'pd' is null");
                return mapping.findForward("pdp_session_timeout");
            }
            else if (buttonPressed.startsWith("btnUpdate")) {
                // Update Payment Detail in Session after action has been performed
                // (status might have changed)
                detailId = pd.getId().intValue();
                pd = paymentDetailService.get(detailId);

                pd.setLastDisbursementActionDate(this.getDisbursementActionExpirationDate(request));
                getPayeeDescriptor(session, pd);
                getDisbursementPaymentList(session, pd);
                session.setAttribute("size", new Integer(pd.getPaymentGroup().getPaymentDetails().size()));
                session.setAttribute("PaymentDetail", pd);
            }
            // Use of a default tab (Summary Tab)
            if ((GeneralUtilities.isStringEmpty(buttonPressed)) || (buttonPressed.startsWith("btnUpdate"))) {
                request.setAttribute("btnPressed", "btnSummaryTab");
            }
            else {
                request.setAttribute("btnPressed", buttonPressed);
            }
        }

        return mapping.findForward("display");
    }

    private void getPayeeDescriptor(HttpSession session, PaymentDetail pd) {
        // Get descriptor of Payee ID Type based on Code in DB
        Iterator i = payees.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            if (pd != null) {
                if (key.equals(pd.getPaymentGroup().getPayeeIdTypeCd())) {
                    session.setAttribute("payeeIdTypeDesc", payees.get(key));
                }
                if (key.equals(pd.getPaymentGroup().getAlternatePayeeIdTypeCd())) {
                    session.setAttribute("alternatePayeeIdTypeDesc", payees.get(key));
                }
            }
        }
    }

    private void getDisbursementPaymentList(HttpSession session, PaymentDetail pd) {
        List paymentDetailList = new ArrayList();
        KualiInteger disbNbr = pd.getPaymentGroup().getDisbursementNbr();
        session.removeAttribute("disbNbrTotalPayments");
        session.removeAttribute("disbursementDetailsList");

        if ((disbNbr != null) && (disbNbr != KualiInteger.ZERO)) {
            List paymentGroupList = paymentGroupService.getByDisbursementNumber(disbNbr.intValue());
            for (Iterator iter = paymentGroupList.iterator(); iter.hasNext();) {
                PaymentGroup elem = (PaymentGroup) iter.next();
                paymentDetailList.addAll(elem.getPaymentDetails());
            }
            session.setAttribute("disbNbrTotalPayments", new Integer(paymentDetailList.size()));
            session.setAttribute("disbursementDetailsList", paymentDetailList);
        }
    }

    public void setPayees(Map m) {
        payees = m;
    }

    public void setPaymentDetailService(PaymentDetailService p) {
        paymentDetailService = p;
    }

    public void setPaymentGroupService(PaymentGroupService p) {
        paymentGroupService = p;
    }
}
