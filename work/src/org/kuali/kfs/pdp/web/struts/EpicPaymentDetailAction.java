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
package org.kuali.kfs.pdp.web.struts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.kuali.kfs.pdp.GeneralUtilities;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.dataaccess.PayeeTypeDao;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.context.SpringContext;

public class EpicPaymentDetailAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EpicPaymentDetailAction.class);
    private PaymentDetailService paymentDetailService;
    private PaymentGroupService paymentGroupService;
    private PayeeTypeDao payeeTypeDao;

    public EpicPaymentDetailAction() {
        setPaymentDetailService(SpringContext.getBean(PaymentDetailService.class));
        setPaymentGroupService(SpringContext.getBean(PaymentGroupService.class));
        setPayeeTypeDao(SpringContext.getBean(PayeeTypeDao.class));
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");

        String buttonPressed = GeneralUtilities.whichButtonWasPressed(request);

        LOG.debug("executeLogic() btnPressed is " + buttonPressed);
        String custPaymentDocNbr = null;
        String fdocTypeCode = null;
        ActionErrors errors = new ActionErrors();
        PaymentDetail pd = (PaymentDetail) request.getSession().getAttribute("PaymentDetail");
        List indivList = (List) request.getSession().getAttribute("indivSearchResults");
        List batchIndivList = (List) request.getSession().getAttribute("batchIndivSearchResults");

        if ((request.getParameter("sourceDocNbr") == null) && (request.getParameter("docTypeCode") == null) && (GeneralUtilities.isStringEmpty(buttonPressed))) {
            // No Source Doc Number given and no button pressed
            request.setAttribute("title", "No Source Document Number Error");
            errors.add("error", new ActionMessage("paymentDetail.epic.noSourceDocGiven"));
            saveErrors(request, errors);
            return mapping.findForward("pdp_message");
        }
        else if ((request.getParameter("sourceDocNbr") != null) && (request.getParameter("docTypeCode") != null)) {
            // Source Doc Number was passed - find payment Detail with the Sopurce Doc Number
            LOG.debug("executeLogic() Source Document Number passed in Parms: " + request.getParameter("sourceDocNbr"));
            LOG.debug("executeLogic() Doc Type passed in Parms: " + request.getParameter("docTypeCode"));
            custPaymentDocNbr = (String) request.getParameter("sourceDocNbr");
            fdocTypeCode = (String) request.getParameter("docTypeCode");
            pd = paymentDetailService.getDetailForEpic(custPaymentDocNbr, fdocTypeCode);

            if (pd != null) {
                pd.setLastDisbursementActionDate(this.getDisbursementActionExpirationDate(request));
                getPayeeDescriptor(request, pd);
                getDisbursementPaymentList(request, pd);
                request.getSession().setAttribute("size", new Integer(pd.getPaymentGroup().getPaymentDetails().size()));
                request.getSession().setAttribute("PaymentDetail", pd);
            }
            else {
                // No PDP Payment for Epic Source Doc Number given
                request.setAttribute("title", "No Payment Found Error");
                errors.add("error", new ActionMessage("paymentDetail.epic.noPaymentFound", custPaymentDocNbr));
                saveErrors(request, errors);
                return mapping.findForward("pdp_message");
            }
        }
        else {
            if (pd == null) {
                // Handle Session Expiration
                return mapping.findForward("pdp_session_timeout");
            }
            else if (buttonPressed.startsWith("btnUpdate")) {
                // Update Payment Detail in Session after action has been performed
                // (status might have changed)
                Integer detailId = pd.getId();
                pd = paymentDetailService.get(detailId);

                pd.setLastDisbursementActionDate(this.getDisbursementActionExpirationDate(request));
                getPayeeDescriptor(request, pd);
                getDisbursementPaymentList(request, pd);
                request.getSession().setAttribute("size", new Integer(pd.getPaymentGroup().getPaymentDetails().size()));
                request.getSession().setAttribute("PaymentDetail", pd);
            }
        }

        // Use of a default tab (Summary Tab)
        if ((GeneralUtilities.isStringEmpty(buttonPressed)) || (buttonPressed.startsWith("btnUpdate"))) {
            request.setAttribute("btnPressed", "btnSummaryTab");
        }
        else {
            request.setAttribute("btnPressed", buttonPressed);
        }

        return mapping.findForward("display");
    }

    public void getPayeeDescriptor(HttpServletRequest request, PaymentDetail pd) {
        // Get descriptor of Payee ID Type based on Code in DB
        Map<String, String> payees = payeeTypeDao.getAll();
        Iterator i = payees.keySet().iterator();
        while (i.hasNext()) {
            String key = (String) i.next();
            if (pd != null) {
                if (key.equals(pd.getPaymentGroup().getPayeeIdTypeCd())) {
                    request.getSession().setAttribute("payeeIdTypeDesc", payees.get(key));
                }
                if (key.equals(pd.getPaymentGroup().getAlternatePayeeIdTypeCd())) {
                    request.getSession().setAttribute("alternatePayeeIdTypeDesc", payees.get(key));
                }
            }
        }
    }

    public void getDisbursementPaymentList(HttpServletRequest request, PaymentDetail pd) {
        List paymentDetailList = new ArrayList();
        Integer disbNbr = pd.getPaymentGroup().getDisbursementNbr();
        request.getSession().removeAttribute("disbNbrTotalPayments");
        request.getSession().removeAttribute("disbursementDetailsList");

        if ((disbNbr != null) && (disbNbr != new Integer(0))) {
            List paymentGroupList = paymentGroupService.getByDisbursementNumber(disbNbr);
            for (Iterator iter = paymentGroupList.iterator(); iter.hasNext();) {
                PaymentGroup elem = (PaymentGroup) iter.next();
                paymentDetailList.addAll(elem.getPaymentDetails());
            }
            request.getSession().setAttribute("disbNbrTotalPayments", new Integer(paymentDetailList.size()));
            request.getSession().setAttribute("disbursementDetailsList", paymentDetailList);
        }
    }

    public void setPayeeTypeDao(PayeeTypeDao p) {
        payeeTypeDao = p;
    }

    public void setPaymentDetailService(PaymentDetailService p) {
        paymentDetailService = p;
    }

    public void setPaymentGroupService(PaymentGroupService p) {
        paymentGroupService = p;
    }
}
