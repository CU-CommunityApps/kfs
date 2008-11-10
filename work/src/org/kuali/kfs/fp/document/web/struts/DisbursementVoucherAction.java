/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.web.struts;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.businessobject.WireCharge;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherCoverSheetService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTaxService;
import org.kuali.kfs.fp.document.service.DisbursementVoucherTravelService;
import org.kuali.kfs.fp.document.service.impl.DisbursementVoucherCoverSheetServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * This class handles Actions for the DisbursementVoucher.
 */
public class DisbursementVoucherAction extends KualiAccountingDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherAction.class);

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward dest = super.execute(mapping, form, request, response);

        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        if (form != null) {
            DisbursementVoucherDocument dvDoc = (DisbursementVoucherDocument) dvForm.getDocument();
            if (dvDoc != null) {
                DisbursementVoucherNonEmployeeTravel dvNet = dvDoc.getDvNonEmployeeTravel();
                if (dvNet != null) {
                    // clear values derived from travelMileageAmount if that amount has been (manually) cleared
                    Integer amount = dvNet.getDvPersonalCarMileageAmount();
                    if ((amount == null) || (amount.intValue() == 0)) {
                        clearTravelMileageAmount(dvNet);
                    }

                    // clear values derived from perDiemRate if that amount has been (manually) cleared
                    KualiDecimal rate = dvNet.getDisbVchrPerdiemRate();
                    if ((rate == null) || rate.isZero()) {
                        clearTravelPerDiem(dvNet);
                    }
                }
            }
        }

        return dest;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        SpringContext.getBean(DisbursementVoucherPayeeService.class).checkPayeeAddressForChanges((DisbursementVoucherDocument) dvForm.getDocument());

        return super.approve(mapping, form, request, response);
    }

    /**
     * Do initialization for a new disbursement voucher
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((DisbursementVoucherDocument) kualiDocumentFormBase.getDocument()).initiateDocument();

        // set wire charge message in form
        ((DisbursementVoucherForm) kualiDocumentFormBase).setWireChargeMessage(retrieveWireChargeMessage());
    }

    /**
     * Calls service to generate the disbursement voucher cover sheet as a pdf.
     */
    public ActionForward printDisbursementVoucherCoverSheet(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get directory of template
        String directory = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_HELP_URL_KEY);

        DisbursementVoucherDocument document = (DisbursementVoucherDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(request.getParameter(KFSPropertyConstants.DOCUMENT_NUMBER));

        // set workflow document back into form to prevent document authorizer "invalid (null)
        // document.documentHeader.workflowDocument" since we are bypassing form submit and just linking directly to the action
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        dvForm.getDocument().getDocumentHeader().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DisbursementVoucherCoverSheetService coverSheetService = SpringContext.getBean(DisbursementVoucherCoverSheetService.class);

        coverSheetService.generateDisbursementVoucherCoverSheet(directory, DisbursementVoucherCoverSheetServiceImpl.DV_COVERSHEET_TEMPLATE_NM, document, baos);
        String fileName = document.getDocumentNumber() + "_cover_sheet.pdf";
        WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName);
        return (null);

    }

    /**
     * Calculates the travel per diem amount.
     */
    public ActionForward calculateTravelPerDiem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        try {
            // call service to calculate per diem
            KualiDecimal perDiemAmount = SpringContext.getBean(DisbursementVoucherTravelService.class).calculatePerDiemAmount(dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemEndDttmStamp(), dvDocument.getDvNonEmployeeTravel().getDisbVchrPerdiemRate());

            dvDocument.getDvNonEmployeeTravel().setDisbVchrPerdiemCalculatedAmt(perDiemAmount);
            dvDocument.getDvNonEmployeeTravel().setDisbVchrPerdiemActualAmount(perDiemAmount);
        }
        catch (RuntimeException e) {
            String errorMessage = e.getMessage();

            if (StringUtils.isBlank(errorMessage)) {
                errorMessage = "The per diem amount could not be calculated.  Please ensure all required per diem fields are filled in before attempting to calculate the per diem amount.";
            }

            LOG.error("Error in calculating travel per diem: " + errorMessage);
            GlobalVariables.getErrorMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_CUSTOM, errorMessage);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * Clears the travel per diem amount
     */
    public ActionForward clearTravelPerDiem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeTravel dvNet = dvDocument.getDvNonEmployeeTravel();
        if (dvNet != null) {
            clearTravelPerDiem(dvNet);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * This method...
     * 
     * @param dvNet
     */
    private void clearTravelPerDiem(DisbursementVoucherNonEmployeeTravel dvNet) {
        dvNet.setDisbVchrPerdiemCalculatedAmt(null);
        dvNet.setDisbVchrPerdiemActualAmount(null);
    }

    /**
     * Calculates the travel mileage amount.
     */
    public ActionForward calculateTravelMileageAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        if (dvDocument.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount() == null) {
            LOG.error("Total Mileage must be given");
            GlobalVariables.getErrorMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_REQUIRED, "Total Mileage");
        }

        if (dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp() == null) {
            LOG.error("Travel Start Date must be given");
            GlobalVariables.getErrorMap().putError("DVNonEmployeeTravelErrors", KFSKeyConstants.ERROR_REQUIRED, "Travel Start Date");
        }

        if (GlobalVariables.getErrorMap().isEmpty()) {
            // call service to calculate mileage amount
            KualiDecimal mileageAmount = SpringContext.getBean(DisbursementVoucherTravelService.class).calculateMileageAmount(dvDocument.getDvNonEmployeeTravel().getDvPersonalCarMileageAmount(), dvDocument.getDvNonEmployeeTravel().getDvPerdiemStartDttmStamp());

            dvDocument.getDvNonEmployeeTravel().setDisbVchrMileageCalculatedAmt(mileageAmount);
            dvDocument.getDvNonEmployeeTravel().setDisbVchrPersonalCarAmount(mileageAmount);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears the travel mileage amount
     */
    public ActionForward clearTravelMileageAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeTravel dvNet = dvDocument.getDvNonEmployeeTravel();
        if (dvNet != null) {
            clearTravelMileageAmount(dvNet);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * reset the travel mileage amount as null
     */
    private void clearTravelMileageAmount(DisbursementVoucherNonEmployeeTravel dvNet) {
        dvNet.setDisbVchrMileageCalculatedAmt(null);
        dvNet.setDisbVchrPersonalCarAmount(null);
    }


    /**
     * Adds a new employee travel expense line.
     */
    public ActionForward addNonEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeExpense newExpenseLine = dvForm.getNewNonEmployeeExpenseLine();

        // validate line
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.NEW_NONEMPLOYEE_EXPENSE_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newExpenseLine);

        // Ensure all fields are filled in before attempting to add a new expense line
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCode())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_CODE, KFSKeyConstants.ERROR_DV_EXPENSE_CODE);
        }
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCompanyName())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_COMPANY_NAME, KFSKeyConstants.ERROR_DV_EXPENSE_COMPANY_NAME);
        }
        if (ObjectUtils.isNull(newExpenseLine.getDisbVchrExpenseAmount())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_AMOUNT, KFSKeyConstants.ERROR_DV_EXPENSE_AMOUNT);
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.NEW_NONEMPLOYEE_EXPENSE_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.getDvNonEmployeeTravel().addDvNonEmployeeExpenseLine(newExpenseLine);
            dvForm.setNewNonEmployeeExpenseLine(new DisbursementVoucherNonEmployeeExpense());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds a new employee pre paid travel expense line.
     */
    public ActionForward addPrePaidNonEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherNonEmployeeExpense newExpenseLine = dvForm.getNewPrePaidNonEmployeeExpenseLine();

        // validate line
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.NEW_PREPAID_EXPENSE_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newExpenseLine);

        // Ensure all fields are filled in before attempting to add a new expense line
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCode())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_PRE_PAID_EXPENSE_CODE, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_CODE);
        }
        if (StringUtils.isBlank(newExpenseLine.getDisbVchrPrePaidExpenseCompanyName())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_PRE_PAID_EXPENSE_COMPANY_NAME, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_COMPANY_NAME);
        }
        if (ObjectUtils.isNull(newExpenseLine.getDisbVchrExpenseAmount())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.DISB_VCHR_EXPENSE_AMOUNT, KFSKeyConstants.ERROR_DV_PREPAID_EXPENSE_AMOUNT);
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.NEW_PREPAID_EXPENSE_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.getDvNonEmployeeTravel().addDvPrePaidEmployeeExpenseLine(newExpenseLine);
            dvForm.setNewPrePaidNonEmployeeExpenseLine(new DisbursementVoucherNonEmployeeExpense());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a non employee travel expense line.
     */
    public ActionForward deleteNonEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        int deleteIndex = getLineToDelete(request);
        dvDocument.getDvNonEmployeeTravel().getDvNonEmployeeExpenses().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a pre paid travel expense line.
     */
    public ActionForward deletePrePaidEmployeeExpenseLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        int deleteIndex = getLineToDelete(request);
        dvDocument.getDvNonEmployeeTravel().getDvPrePaidEmployeeExpenses().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Adds a new pre conference registrant line.
     */
    public ActionForward addPreConfRegistrantLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        DisbursementVoucherPreConferenceRegistrant newRegistrantLine = dvForm.getNewPreConferenceRegistrantLine();

        // validate line
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.NEW_PRECONF_REGISTRANT_LINE);
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(newRegistrantLine);
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSPropertyConstants.NEW_PRECONF_REGISTRANT_LINE);

        if (GlobalVariables.getErrorMap().isEmpty()) {
            dvDocument.addDvPrePaidRegistrantLine(newRegistrantLine);
            dvForm.setNewPreConferenceRegistrantLine(new DisbursementVoucherPreConferenceRegistrant());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * Deletes a pre conference registrant line.
     */
    public ActionForward deletePreConfRegistrantLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) dvForm.getDocument();

        int deleteIndex = getLineToDelete(request);
        dvDocument.getDvPreConferenceDetail().getDvPreConferenceRegistrants().remove(deleteIndex);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * Calls service to generate tax accounting lines and updates nra tax line string in action form.
     */
    public ActionForward generateNonResidentAlienTaxLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        /* user should not have generate button if not in tax group, but check just to make sure */
        if (!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP))) {
            LOG.info("User requested generateNonResidentAlienTaxLines who is not in the kuali tax group.");
            GlobalVariables.getErrorMap().putError(KFSConstants.DV_NRATAX_TAB_ERRORS, KFSKeyConstants.ERROR_DV_NRA_PERMISSIONS_GENERATE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        DisbursementVoucherTaxService taxService = SpringContext.getBean(DisbursementVoucherTaxService.class);

        /* call service to generate new tax lines */
        GlobalVariables.getErrorMap().addToErrorPath("document");
        taxService.processNonResidentAlienTax(document);
        GlobalVariables.getErrorMap().removeFromErrorPath("document");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Calls service to clear tax accounting lines and updates nra tax line string in action form.
     */
    public ActionForward clearNonResidentAlienTaxLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        /* user should not have generate button if not in tax group, but check just to make sure */
        if (!KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(GlobalVariables.getUserSession().getPerson().getPrincipalId(), org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, SpringContext.getBean(ParameterService.class).getParameterValue(DisbursementVoucherDocument.class, KFSConstants.FinancialApcParms.DV_TAX_WORKGROUP))) {
            LOG.info("User requested generateNonResidentAlienTaxLines who is not in the kuali tax group.");
            GlobalVariables.getErrorMap().putError(KFSConstants.DV_NRATAX_TAB_ERRORS, KFSKeyConstants.ERROR_DV_NRA_PERMISSIONS_GENERATE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        DisbursementVoucherTaxService taxService = SpringContext.getBean(DisbursementVoucherTaxService.class);

        /* call service to clear previous lines */
        taxService.clearNRATaxLines(document);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Builds the wire charge message for the current fiscal year.
     * 
     * @return the wire charge message for the current fiscal year
     */
    private String retrieveWireChargeMessage() {
        String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.MESSAGE_DV_WIRE_CHARGE);
        WireCharge wireCharge = new WireCharge();
        wireCharge.setUniversityFiscalYear(SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        wireCharge = (WireCharge) SpringContext.getBean(BusinessObjectService.class).retrieve(wireCharge);
        Object[] args = { wireCharge.getDomesticChargeAmt(), wireCharge.getForeignChargeAmt() };

        return MessageFormat.format(message, args);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;

        ActionForward actionAfterPayeeLookup = this.refreshAfterPayeeSelection(mapping, dvForm, request);
        if (actionAfterPayeeLookup != null) {
            return actionAfterPayeeLookup;
        }

        return super.refresh(mapping, form, request, response);
    }

    // do refresh after a payee is selected
    private ActionForward refreshAfterPayeeSelection(ActionMapping mapping, DisbursementVoucherForm dvForm, HttpServletRequest request) {
        String refreshCaller = dvForm.getRefreshCaller();

        boolean isPayeeLookupable = KFSConstants.KUALI_DISBURSEMENT_PAYEE_LOOKUPABLE_IMPL.equals(refreshCaller);
        boolean isAddressLookupable = KFSConstants.KUALI_VENDOR_ADDRESS_LOOKUPABLE_IMPL.equals(refreshCaller);

        // do not execute the further refreshing logic if the refresh caller is not a lookupable
        if (!isPayeeLookupable && !isAddressLookupable) {
            return null;
        }

        // do not execute the further refreshing logic if a payee is not selected
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();
        String payeeIdNumber = document.getDvPayeeDetail().getDisbVchrPayeeIdNumber();
        if (payeeIdNumber == null) {
            return null;
        }

        dvForm.setPayeeIdNumber(payeeIdNumber);
        dvForm.setHasMultipleAddresses(false);
        document.getDvPayeeDetail().setDisbursementVoucherPayeeTypeCode(dvForm.getPayeeTypeCode());

        // determine whether the selected vendor has multiple addresses. If so, redirect to the address selection screen
        if (isPayeeLookupable && dvForm.isVendor()) {
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorNumber(payeeIdNumber);
            refreshVendorDetail = (VendorDetail) SpringContext.getBean(BusinessObjectService.class).retrieve(refreshVendorDetail);

            VendorAddress defaultVendorAddress = null;
            if (refreshVendorDetail != null) {
                List<VendorAddress> vendorAddresses = refreshVendorDetail.getVendorAddresses();
                boolean hasMultipleAddresses = vendorAddresses != null && vendorAddresses.size() > 1;
                dvForm.setHasMultipleAddresses(hasMultipleAddresses);

                if (vendorAddresses != null) {
                    defaultVendorAddress = vendorAddresses.get(0);
                }
            }

            if (dvForm.hasMultipleAddresses()) {
                return renderVendorAddressSelection(mapping, request, dvForm);
            }
            else if (defaultVendorAddress != null) {
                setupPayeeAsVendor(dvForm, payeeIdNumber, defaultVendorAddress.getVendorAddressGeneratedIdentifier().toString());
            }

            return null;
        }

        if (isPayeeLookupable && dvForm.isEmployee()) {
            this.setupPayeeAsEmployee(dvForm, payeeIdNumber);
        }

        String payeeAddressIdentifier = request.getParameter(KFSPropertyConstants.VENDOR_ADDRESS_GENERATED_ID);
        if (isAddressLookupable && payeeAddressIdentifier != null) {
            setupPayeeAsVendor(dvForm, payeeIdNumber, payeeAddressIdentifier);
        }

        return null;
    }

    /**
     * Hook into performLookup to switch the payee lookup based on the payee type selected.
     */
    public ActionForward performLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DisbursementVoucherForm dvForm = (DisbursementVoucherForm) form;
        DisbursementVoucherDocument document = (DisbursementVoucherDocument) dvForm.getDocument();

        String paymentReasonCode = document.getDvPayeeDetail().getDisbVchrPaymentReasonCode();
        if (StringUtils.isBlank(paymentReasonCode)) {
            GlobalVariables.getErrorMap().putError("document.dvPayeeDetail.disbVchrPaymentReasonCode", KFSKeyConstants.ERROR_DV_PAYMENT_REASON_NOT_SELECTED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        return super.performLookup(mapping, form, request, response);
    }

    /**
     * render the vendor address lookup results if there are multiple addresses for the selected vendor
     */
    private ActionForward renderVendorAddressSelection(ActionMapping mapping, HttpServletRequest request, DisbursementVoucherForm dvForm) {
        Properties props = new Properties();

        props.put(KNSConstants.SUPPRESS_ACTIONS, Boolean.toString(true));
        props.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, VendorAddress.class.getName());
        props.put(KNSConstants.LOOKUP_ANCHOR, KNSConstants.ANCHOR_TOP_OF_FORM);
        props.put(KNSConstants.LOOKED_UP_COLLECTION_NAME, KFSPropertyConstants.VENDOR_ADDRESSES);

        String filedConversion = "vendorAddressGeneratedIdentifier:vendorAddressGeneratedIdentifier,vendorHeaderGeneratedIdentifier:vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier:vendorDetailAssignedIdentifier";
        props.put(KNSConstants.CONVERSION_FIELDS_PARAMETER, filedConversion);

        props.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, dvForm.getVendorHeaderGeneratedIdentifier());
        props.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, dvForm.getVendorDetailAssignedIdentifier());
        props.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        props.put(KNSConstants.RETURN_LOCATION_PARAMETER, this.getReturnLocation(request, mapping));
        props.put(KNSConstants.BACK_LOCATION, this.getReturnLocation(request, mapping));

        props.put(KNSConstants.LOOKUP_AUTO_SEARCH, "Yes");
        props.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);

        props.put(KNSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(dvForm));
        props.put(KNSConstants.DOC_NUM, dvForm.getDocument().getDocumentNumber());

        String url = UrlFactory.parameterizeUrl(getBasePath(request) + "/kr/" + KNSConstants.LOOKUP_ACTION, props);

        return new ActionForward(url, true);
    }
    
    /**
     * setup the payee as an employee with the given id number
     */
    private void setupPayeeAsEmployee(DisbursementVoucherForm dvForm, String payeeIdNumber) {
        Person person = (Person) SpringContext.getBean(PersonService.class).getPerson(payeeIdNumber);
        if (person != null) {
            ((DisbursementVoucherDocument) dvForm.getDocument()).templateEmployee(person);
        }
        else {
            LOG.error("Exception while attempting to retrieve universal user by universal user id " + payeeIdNumber);
        }
    }

    /**
     * setup the payee as a vendor with the given id number and address id
     */
    private void setupPayeeAsVendor(DisbursementVoucherForm dvForm, String payeeIdNumber, String payeeAddressIdentifier) {
        VendorDetail vendorDetail = new VendorDetail();
        vendorDetail.setVendorNumber(payeeIdNumber);
        vendorDetail = (VendorDetail) SpringContext.getBean(BusinessObjectService.class).retrieve(vendorDetail);
        
        VendorAddress vendorAddress = new VendorAddress();
        if (StringUtils.isNotBlank(payeeAddressIdentifier)) {
            try {
                vendorAddress.setVendorAddressGeneratedIdentifier(new Integer(payeeAddressIdentifier));
                vendorAddress = (VendorAddress) SpringContext.getBean(BusinessObjectService.class).retrieve(vendorAddress);
            }
            catch (Exception x) {
                LOG.error("Exception while attempting to retrieve vendor address for vendor address id " + payeeAddressIdentifier + ": " + x);
            }
        }
        
        ((DisbursementVoucherDocument) dvForm.getDocument()).templateVendor(vendorDetail, vendorAddress);
    }
}
