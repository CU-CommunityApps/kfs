/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.bo.user.UniversityUser;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.web.struts.form.DisbursementVoucherForm;

/**
 * This class handles Actions for the DisbursementVoucher.
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherAction extends KualiTransactionalDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        /* refresh from dv payee lookup */
        if (Constants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller())
                && request.getParameter("document.dvPayeeDetail.disbVchrPayeeIdNumber") != null
                && document.getDvPayeeDetail().isPayee()) {
            String payeeIdNumber = ((DisbursementVoucherDocument) dvForm.getDocument()).getDvPayeeDetail()
                    .getDisbVchrPayeeIdNumber();
            Payee refreshPayee = new Payee();
            refreshPayee.setPayeeIdNumber(payeeIdNumber);
            refreshPayee = (Payee) SpringServiceLocator.getBusinessObjectService().retrieve(refreshPayee);
            ((DisbursementVoucherDocument) dvForm.getDocument()).templatePayee(refreshPayee);
        }
        /* refresh from employee lookup */
        else if (Constants.KUALI_LOOKUPABLE_IMPL.equals(dvForm.getRefreshCaller())
                && request.getParameter("document.dvPayeeDetail.disbVchrPayeeIdNumber") != null
                && document.getDvPayeeDetail().isEmployee()) {
            String emplUuid = ((DisbursementVoucherDocument) dvForm.getDocument()).getDvPayeeDetail()
            .getDisbVchrPayeeIdNumber();
            UniversityUser employee = new UniversityUser();
            employee.setUuId(emplUuid);
            employee = (UniversityUser) SpringServiceLocator.getBusinessObjectService().retrieve(employee);
        }
        

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Calls service to generate the disbursement voucher cover sheet as a pdf.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printDisbursementVoucherCoverSheet(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;

        return mapping.findForward(Constants.MAPPING_BASIC);

    }


    /**
     * Calls service to generate tax accounting lines.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward generateNonResidentAlienTaxLines(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;

        return mapping.findForward(Constants.MAPPING_BASIC);

    }


    /**
     * Hook into performLookup to switch the payee lookup based on the payee type selected.
     * @see org.kuali.core.web.struts.action.KualiAction#performLookup(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // substitute bo class and mapping if the type is Employee, lookup already setup for Payee
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        if (document.getDvPayeeDetail().isEmployee()) {
            String fullParameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
            String boClassName = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_BOPARM_LEFT_DEL,
                    Constants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
            String conversionFields = StringUtils.substringBetween(fullParameter, Constants.METHOD_TO_CALL_PARM1_LEFT_DEL,
                    Constants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
            fullParameter = StringUtils.replace(fullParameter, boClassName, "org.kuali.core.bo.user.UniversityUser");
            fullParameter = StringUtils.replace(fullParameter, conversionFields, "uuId:document.dvPayeeDetail.disbVchrPayeeIdNumber");
            request.setAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE, fullParameter);
        }

        return super.performLookup(mapping, form, request, response);
    }


}