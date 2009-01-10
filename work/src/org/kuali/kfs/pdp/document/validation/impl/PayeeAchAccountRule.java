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
package org.kuali.kfs.pdp.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccountX;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * Performs business rules for the Payee ACH Account maintenance document
 */
public class PayeeAchAccountRule extends MaintenanceDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeACHAccountX.class);

    private PayeeACHAccountX oldPayeeAchAccount;
    private PayeeACHAccountX newPayeeAchAccount;

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    public void setupConvenienceObjects() {
        LOG.info("setupConvenienceObjects called");

        // setup oldPayeeAchAccount convenience objects, make sure all possible sub-objects are populated
        oldPayeeAchAccount = (PayeeACHAccountX) super.getOldBo();

        // setup newPayeeAchAccount convenience objects, make sure all possible sub-objects are populated
        newPayeeAchAccount = (PayeeACHAccountX) super.getNewBo();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");

        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean validEntry = true;

        String payeeIdTypeCd, payeeUserId, feinNumber, dvPayeeId, ssn;
        KualiInteger vendorGnrtdId, vendorAsndId;

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        payeeIdTypeCd = newPayeeAchAccount.getPayeeIdentifierTypeCode();
        if (payeeIdTypeCd == null) {
            return validEntry;
        }

        // Create a query to do a lookup on.
        Map<String, Object> criteria = new HashMap<String, Object>();
        String identifierField = "";

        if (payeeIdTypeCd.equals(PdpConstants.PayeeIdTypeCodes.EMPLOYEE_ID)) {
            identifierField = KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER;

            payeeUserId = newPayeeAchAccount.getPrincipalId();
            if (payeeUserId == null) {
                validEntry = false;
            }
            else
                criteria.put(identifierField, payeeUserId);
        }
        else if (payeeIdTypeCd.equals(PdpConstants.PayeeIdTypeCodes.VENDOR_ID)) {
            identifierField = KFSPropertyConstants.VENDOR_NUMBER;

            vendorGnrtdId = newPayeeAchAccount.getVendorHeaderGeneratedIdentifier();
            vendorAsndId = newPayeeAchAccount.getVendorDetailAssignedIdentifier();
            if ((vendorGnrtdId == null) || (vendorAsndId == null)) {
                validEntry = false;
            }
            if (validEntry) {
                criteria.put(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorGnrtdId);
                criteria.put(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorAsndId);
            }
        }

        if (validEntry) {
            validEntry &= checkForDuplicateRecord(criteria, identifierField);
        }
        else {
            putFieldError(identifierField, KFSKeyConstants.ERROR_REQUIRED, getFieldLabel(identifierField));
        }

        return validEntry;
    }

    /**
     * Checks to verify record is not a duplicate for payee id. Do not check for a duplicate record if the following conditions are
     * true 1. editing an existing record (old primary key = new primary key) 2. new PSD code = old PSD code 3. new payee type code =
     * old payee type code 4. depending of the value of payee type code, new correspoding PayeeId = old corresponding PayeeId
     * 
     * @param criteria
     * @param identifierField id field for payee
     * @return true if record is not duplicate, false otherwise
     */
    protected boolean checkForDuplicateRecord(Map<String, Object> criteria, String identifierField) {
        String newPayeeIdTypeCd = newPayeeAchAccount.getPayeeIdentifierTypeCode();
        String newAchTransactionType = newPayeeAchAccount.getAchTransactionType();

        boolean valid = true;

        if (newPayeeAchAccount.getAchAccountGeneratedIdentifier() != null && oldPayeeAchAccount.getAchAccountGeneratedIdentifier() != null && newPayeeAchAccount.getAchAccountGeneratedIdentifier().equals(oldPayeeAchAccount.getAchAccountGeneratedIdentifier())) {
            if (newPayeeIdTypeCd.equals(oldPayeeAchAccount.getPayeeIdentifierTypeCode()) && newAchTransactionType.equals(oldPayeeAchAccount.getAchTransactionType())) {
                if (newPayeeIdTypeCd.equals(PdpConstants.PayeeIdTypeCodes.EMPLOYEE_ID)) {
                    if (newPayeeAchAccount.getPrincipalId().equals(oldPayeeAchAccount.getPrincipalId()))
                        return valid;
                }
                else if (newPayeeIdTypeCd.equals(PdpConstants.PayeeIdTypeCodes.VENDOR_ID)) {
                    if (newPayeeAchAccount.getVendorHeaderGeneratedIdentifier().equals(oldPayeeAchAccount.getVendorHeaderGeneratedIdentifier()) && newPayeeAchAccount.getVendorDetailAssignedIdentifier().equals(oldPayeeAchAccount.getVendorDetailAssignedIdentifier()))
                        return valid;
                }
            }
        }

        // check for a duplicate record if creating a new record or editing an old one and above mentioned conditions are not true
        criteria.put(PdpPropertyConstants.ACH_TRANSACTION_TYPE, newAchTransactionType);
        criteria.put(PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, newPayeeIdTypeCd);

        int matches = SpringContext.getBean(BusinessObjectService.class).countMatching(PayeeACHAccountX.class, criteria);
        if (matches > 0) {
            putFieldError(identifierField, KFSKeyConstants.ERROR_DOCUMENT_PAYEEACHACCOUNTMAINT_DUPLICATE_RECORD);
            valid = false;
        }

        return valid;
    }

}
