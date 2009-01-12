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
package org.kuali.kfs.fp.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementPayee;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorType;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.bo.AdHocRouteRecipient;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * implementing the service methods defined in DisbursementVoucherPayeeService
 * 
 * @see DisbursementVoucherPayeeService
 */
public class DisbursementVoucherPayeeServiceImpl implements DisbursementVoucherPayeeService {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPayeeServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    private ParameterService parameterService;
    private VendorService vendorService;

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#getPayeeTypeDescription(java.lang.String)
     */
    public String getPayeeTypeDescription(String payeeTypeCode) {
        String payeeTypeDescription = StringUtils.EMPTY;

        if (DisbursementVoucherConstants.DV_PAYEE_TYPE_EMPLOYEE.equals(payeeTypeCode)) {
            payeeTypeDescription = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NON_VENDOR_EMPLOYEE_PAYEE_TYPE_LABEL_PARM_NM);
        }
        else if (DisbursementVoucherConstants.DV_PAYEE_TYPE_VENDOR.equals(payeeTypeCode)) {
            payeeTypeDescription = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.PO_AND_DV_PAYEE_TYPE_LABEL_PARM_NM);
        }
        else if (DisbursementVoucherConstants.DV_PAYEE_TYPE_REVOLVING_FUND_VENDOR.equals(payeeTypeCode)) {
            payeeTypeDescription = this.getVendorTypeDescription(VendorConstants.VendorTypes.REVOLVING_FUND);
        }
        else if (DisbursementVoucherConstants.DV_PAYEE_TYPE_SUBJECT_PAYMENT_VENDOR.equals(payeeTypeCode)) {
            payeeTypeDescription = this.getVendorTypeDescription(VendorConstants.VendorTypes.SUBJECT_PAYMENT);
        }

        return payeeTypeDescription;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isEmployee(org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail)
     */
    public boolean isEmployee(DisbursementVoucherPayeeDetail dvPayeeDetail) {
        // If is vendor, then check vendor employee flag
        if (this.isVendor(dvPayeeDetail)) {
            VendorDetail vendor = vendorService.getByVendorNumber(dvPayeeDetail.getDisbVchrPayeeIdNumber());
            return vendor == null ? false : vendorService.isVendorInstitutionEmployee(vendor.getVendorHeaderGeneratedIdentifier());
        }

        String payeeTypeCode = dvPayeeDetail.getDisbursementVoucherPayeeTypeCode();
        return DisbursementVoucherConstants.DV_PAYEE_TYPE_EMPLOYEE.equals(payeeTypeCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isEmployee(org.kuali.kfs.fp.businessobject.DisbursementPayee)
     */
    public boolean isEmployee(DisbursementPayee payee) {
        // If is vendor, then check vendor employee flag
        if (this.isVendor(payee)) {
            VendorDetail vendor = vendorService.getByVendorNumber(payee.getPayeeIdNumber());
            return vendor == null ? false : vendorService.isVendorInstitutionEmployee(vendor.getVendorHeaderGeneratedIdentifier());
        }

        String payeeTypeCode = payee.getPayeeTypeCode();
        return DisbursementVoucherConstants.DV_PAYEE_TYPE_EMPLOYEE.equals(payeeTypeCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isVendor(org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail)
     */
    public boolean isVendor(DisbursementVoucherPayeeDetail dvPayeeDetail) {
        String payeeTypeCode = dvPayeeDetail.getDisbursementVoucherPayeeTypeCode();
        return DisbursementVoucherConstants.VENDOR_PAYEE_TYPE_CODES.contains(payeeTypeCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isVendor(org.kuali.kfs.fp.businessobject.DisbursementPayee)
     */
    public boolean isVendor(DisbursementPayee payee) {
        String payeeTypeCode = payee.getPayeeTypeCode();
        return DisbursementVoucherConstants.VENDOR_PAYEE_TYPE_CODES.contains(payeeTypeCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isPayeeIndividualVendor(org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail)
     */
    public boolean isPayeeIndividualVendor(DisbursementVoucherPayeeDetail dvPayeeDetail) {
        return this.isVendor(dvPayeeDetail) ? this.isPayeeIndividualVendor(dvPayeeDetail.getDisbVchrPayeeIdNumber()) : false;
    }
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isTaxReviewRequired(String)
     */
    public boolean isTaxReviewRequired(String payeeTaxControlCode) {
        ParameterService paramService = SpringContext.getBean(ParameterService.class);
        List<String> taxControlCodes = paramService.getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherConstants.TAX_CONTROL_CODES_REQUIRING_TAX_REVIEW_PARM_NM);
        
        return taxControlCodes != null && taxControlCodes.contains(payeeTaxControlCode);
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#isPayeeIndividualVendor(org.kuali.kfs.fp.businessobject.DisbursementPayee)
     */
    public boolean isPayeeIndividualVendor(DisbursementPayee payee) {
        return this.isVendor(payee) ? this.isPayeeIndividualVendor(payee.getPayeeIdNumber()) : false;
    }

    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#checkPayeeAddressForChanges(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    public void checkPayeeAddressForChanges(DisbursementVoucherDocument dvDoc) {
        Map<String, String> pks = new HashMap<String, String>();
        pks.put("documentNumber", dvDoc.getDocumentNumber());

        DisbursementVoucherDocument savedDv = (DisbursementVoucherDocument) businessObjectService.findByPrimaryKey(DisbursementVoucherDocument.class, pks);
        DisbursementVoucherPayeeDetail newPayeeDetail = dvDoc.getDvPayeeDetail();
        DisbursementVoucherPayeeDetail oldPayeeDetail = savedDv.getDvPayeeDetail();

        if (ObjectUtils.isNotNull(oldPayeeDetail) && ObjectUtils.isNotNull(newPayeeDetail)) {
            if (!oldPayeeDetail.hasSameAddress(newPayeeDetail)) {// Addresses don't match, so let's start the recording of
                // changes

                // Put a note on the document to record the change to the address
                try {
                    String noteText = buildPayeeChangedNoteText(newPayeeDetail, oldPayeeDetail);

                    int noteMaxSize = dataDictionaryService.getAttributeMaxLength("Note", "noteText");

                    // Break up the note into multiple pieces if the note is too large to fit in the database field.
                    while (noteText.length() > noteMaxSize) {
                        int fromIndex = 0;
                        fromIndex = noteText.lastIndexOf(';', noteMaxSize);

                        String noteText1 = noteText.substring(0, fromIndex);
                        Note note1 = documentService.createNoteFromDocument(dvDoc, noteText1);
                        documentService.addNoteToDocument(dvDoc, note1);
                        noteText = noteText.substring(fromIndex);
                    }

                    Note note = documentService.createNoteFromDocument(dvDoc, noteText);
                    documentService.addNoteToDocument(dvDoc, note);
                }
                catch (Exception e) {
                    LOG.error("Exception while attempting to create or add note: " + e);
                }

                // Send out FYIs to all previous approvers so they're aware of the changes to the address
                try {
                    Set<Person> priorApprovers = dvDoc.getDocumentHeader().getWorkflowDocument().getAllPriorApprovers();
                    String initiatorUserId = dvDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiatorPrincipalId();
                    Person finSysUser = SpringContext.getBean(PersonService.class).getPerson(initiatorUserId);
                    setupFYIs(dvDoc, priorApprovers, finSysUser.getPrincipalName());
                }
                catch (WorkflowException we) {
                    LOG.error("Exception while attempting to retrieve all prior approvers from workflow: " + we);
                }
                catch (Exception unfe) {
                    LOG.error("Exception while attempting to retrieve all prior approvers for a disbursement voucher: " + unfe);
                }
            }
        }
    }

    /**
     * This method...
     * 
     * @param newPayeeDetail
     * @param oldPayeeDetail
     * @return
     */
    private String buildPayeeChangedNoteText(DisbursementVoucherPayeeDetail newPayeeDetail, DisbursementVoucherPayeeDetail oldPayeeDetail) {
        StringBuilder noteText = new StringBuilder();
        String valueLabel = "";
        try {
            noteText.append("The following changes were made to the payee address: ");

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_LINE1_ADDR);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeLine1Addr(), newPayeeDetail.getDisbVchrPayeeLine1Addr()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_LINE2_ADDR);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeLine2Addr(), newPayeeDetail.getDisbVchrPayeeLine2Addr()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_CITY_NAME);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeCityName(), newPayeeDetail.getDisbVchrPayeeCityName()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_STATE_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeStateCode(), newPayeeDetail.getDisbVchrPayeeStateCode()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ZIP_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeZipCode(), newPayeeDetail.getDisbVchrPayeeZipCode()));

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_COUNTRY_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeCountryCode(), newPayeeDetail.getDisbVchrPayeeCountryCode()));
        }
        catch (Exception ex) {
            LOG.error("Error while attempting to build out note text for payee address change note: " + ex);
        }

        return noteText.toString();
    }

    /**
     * This method...
     * 
     * @param valueName
     * @param oldValue
     * @param newValue
     * @return
     */
    private String buildAddressValueDifferenceText(String valueName, String oldValue, String newValue) {
        // Nothing to log if values are still the same
        if (StringUtils.equals(oldValue, newValue)) {
            return "";
        }

        StringBuilder text = new StringBuilder();

        text.append(valueName).append(" was changed from ");
        text.append(oldValue == null ? "(no value entered)" : oldValue).append(" to ");
        text.append(newValue).append("; ");

        return text.toString();
    }


    /**
     * This method...
     * 
     * @param dvDoc
     * @param priorApprovers
     */
    private void setupFYIs(DisbursementVoucherDocument dvDoc, Set<Person> priorApprovers, String initiatorUserId) {
        List<AdHocRouteRecipient> adHocRoutePersons = dvDoc.getAdHocRoutePersons();

        // Add FYI for each approver who has already approved the document
        for (Person approver : priorApprovers) {
            String approverPersonUserId = approver.getPrincipalName();
            adHocRoutePersons.add(buildFyiRecipient(approverPersonUserId));
        }

        // Add FYI for initiator
        adHocRoutePersons.add(buildFyiRecipient(initiatorUserId));
    }

    /**
     * This method...
     * 
     * @param userId
     * @return
     */
    private AdHocRouteRecipient buildFyiRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(KEWConstants.ACTION_REQUEST_FYI_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }

    // get the description of the vendor type with the given vendor type code
    private String getVendorTypeDescription(String vendorTypeCode) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.VENDOR_TYPE_CODE, vendorTypeCode);

        VendorType vendorType = (VendorType) businessObjectService.findByPrimaryKey(VendorType.class, primaryKeys);
        return ObjectUtils.isNotNull(vendorType) ? vendorType.getVendorTypeDescription() : StringUtils.EMPTY;
    }

    // determine whether the given payee id number is associated with an individual vendor
    private boolean isPayeeIndividualVendor(String payeeIdNumber) {
        List<String> individualOwnerShipTypeCodes = parameterService.getParameterValues(DisbursementVoucherDocument.class, DisbursementVoucherConstants.INDIVIDUAL_OWNERSHIP_TYPES_PARM_NM);

        VendorDetail vendor = vendorService.getByVendorNumber(payeeIdNumber);
        if (vendor != null && individualOwnerShipTypeCodes != null) {
            return individualOwnerShipTypeCodes.contains(vendor.getVendorHeader().getVendorOwnershipCode());
        }

        return false;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the vendorService attribute value.
     * @param vendorService The vendorService to set.
     */
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }
}
