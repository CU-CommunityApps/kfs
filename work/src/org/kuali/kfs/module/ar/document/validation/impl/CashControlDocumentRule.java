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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.PaymentMedium;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.validation.AddCashControlDetailRule;
import org.kuali.kfs.module.ar.document.validation.DeleteCashControlDetailRule;
import org.kuali.kfs.module.ar.document.validation.GenerateReferenceDocumentRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;

/**
 * This class holds the business rules for the AR Cash Control Document
 */
public class CashControlDocumentRule extends TransactionalDocumentRuleBase implements AddCashControlDetailRule<CashControlDocument>, DeleteCashControlDetailRule<CashControlDocument>, GenerateReferenceDocumentRule<CashControlDocument> {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashControlDocumentRule.class);

    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomSaveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {

        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        CashControlDocument ccDocument = (CashControlDocument) document;

        ccDocument.refreshReferenceObject("customerPaymentMedium");
        ccDocument.refreshReferenceObject("generalLedgerPendingEntries");

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        if (errorMap.isEmpty()) {
            isValid &= checkUserOrgOptions(ccDocument);
            isValid &= checkRefDocNumber(ccDocument);
            isValid &= validateCashControlDetails(ccDocument);
        }

        return isValid;

    }

    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        CashControlDocument cashControlDocument = (CashControlDocument) document;

        if (isValid) {
            isValid &= checkUserOrgOptions(cashControlDocument);
            isValid &= checkPaymentMedium(cashControlDocument);
            isValid &= checkRefDocNumber(cashControlDocument);
            isValid &= validateCashControlDetails(cashControlDocument);
            isValid &= checkCashControlDocumentHasDetails(cashControlDocument);
        }

        return isValid;

    }

    /**
     * @see org.kuali.core.rules.TransactionalDocumentRuleBase#processCustomApproveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {

        boolean isValid = super.processCustomApproveDocumentBusinessRules(approveEvent);
        CashControlDocument cashControlDocument = (CashControlDocument) approveEvent.getDocument();

        cashControlDocument.refreshReferenceObject("customerPaymentMedium");
        cashControlDocument.refreshReferenceObject("generalLedgerPendingEntries");

        isValid &= checkAllAppDocsApproved(cashControlDocument);
        isValid &= checkGLPEsCreated(cashControlDocument);

        return isValid;

    }

    /**
     * This method checks the CashControlDetail line amount is not zero or negative.
     * 
     * @param document the CashControldocument
     * @param detail the CashControlDetail
     * @return true is amount is valid, false otherwise
     */
    public boolean checkLineAmount(CashControlDocument document, CashControlDetail detail) {

        boolean isValid = true;

        // line amount cannot be zero
        if (detail.getFinancialDocumentLineAmount().isZero()) {
            GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.FINANCIAL_DOCUMENT_LINE_AMOUNT, ArConstants.ERROR_LINE_AMOUNT_CANNOT_BE_ZERO);
            isValid = false;
        }
        // line amount cannot be negative
        if (detail.getFinancialDocumentLineAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.FINANCIAL_DOCUMENT_LINE_AMOUNT, ArConstants.ERROR_LINE_AMOUNT_CANNOT_BE_NEGATIVE);
            isValid = false;
        }
        return isValid;

    }

    /**
     * This method checks if the CashControlDocument has any details to be processed.
     * 
     * @param cashControlDocument the CashControlDocument
     * @return true if it has details, false otherwise
     */
    public boolean checkCashControlDocumentHasDetails(CashControlDocument cashControlDocument) {

        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        if (cashControlDocument.getCashControlDetails().isEmpty()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CASH_CONTROL_DETAILS, ArConstants.ERROR_NO_LINES_TO_PROCESS);
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        return isValid;

    }

    /**
     * This method checks that payment medium has a valid value
     * 
     * @param document
     * @return true if valid, false otherwise
     */
    public boolean checkPaymentMedium(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        String paymentMediumCode = document.getCustomerPaymentMediumCode();

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("customerPaymentMediumCode", paymentMediumCode);

        PaymentMedium paymentMedium = (PaymentMedium) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PaymentMedium.class, criteria);

        if (paymentMedium == null) {
            GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.CUSTOMER_PAYMENT_MEDIUM_CODE, ArConstants.ERROR_PAYMENT_MEDIUM_IS_NOT_VALID);
            isValid = false;
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that reference document number is not null when payment medium is Cash.
     * 
     * @param document CashControlDocument
     * @return true if valid, false otherwise
     */
    public boolean checkRefDocNumber(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        String paymentMedium = document.getCustomerPaymentMediumCode();
        if (ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(paymentMedium)) {
            String refDocNumber = document.getReferenceFinancialDocumentNumber();
            try {
                Long.parseLong(refDocNumber);
                if (StringUtils.isBlank(refDocNumber)) {
                    GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArConstants.ERROR_REFERENCE_DOC_NUMBER_CANNOT_BE_NULL_FOR_PAYMENT_MEDIUM_CASH);
                    isValid = false;
                }
                else {
                    boolean docExists = SpringContext.getBean(DocumentService.class).documentExists(refDocNumber);
                    if (!docExists) {
                        GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArConstants.ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH);
                        isValid = false;
                    }
                }
            }
            catch (NumberFormatException nfe) {
                GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.REFERENCE_FINANCIAL_DOC_NBR, ArConstants.ERROR_REFERENCE_DOC_NUMBER_MUST_BE_VALID_FOR_PAYMENT_MEDIUM_CASH);
                isValid = false;
            }

        }
        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that the GLPEs have been created
     * 
     * @param document CashControlDocument
     * @return true if not null, false otherwise
     */
    public boolean checkGLPEsCreated(CashControlDocument cashControlDocument) {

        boolean isValid = true;
        List<GeneralLedgerPendingEntry> glpes = cashControlDocument.getGeneralLedgerPendingEntries();

        // if payment medium is not Cash the general ledger pending entries must not be empty; if payment medium is Cash then a Cash
        // Receipt Document must be created prior to creating the Cash Control document and it's number should be set in Reference
        // Document number
        if (!ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(cashControlDocument.getCustomerPaymentMediumCode()) && (glpes == null || glpes.isEmpty())) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, ArConstants.ERROR_GLPES_NOT_CREATED);
            isValid = false;
        }

        return isValid;

    }

    /**
     * This method checks that user organization options has a valid value
     * 
     * @param document CashConrolDocument
     * @return true if valid, false otherwise
     */
    public boolean checkUserOrgOptions(CashControlDocument document) {

        boolean isValid = true;
        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

        ChartOrgHolder user = SpringContext.getBean(FinancialSystemUserService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        String chartCode = user.getChartOfAccountsCode();
        String orgCode = user.getOrganizationCode();

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chartCode);
        criteria.put("organizationCode", orgCode);
        OrganizationOptions organizationOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        if (ObjectUtils.isNull(organizationOptions)) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ORGANIZATION_CODE, ArConstants.ERROR_ORGANIZATION_OPTIONS_MUST_BE_SET_FOR_USER_ORG);
            isValid = false;
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.AddCashControlDetailRule#processAddCashControlDetailBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.kfs.module.ar.businessobject.CashControlDetail)
     */
    public boolean processAddCashControlDetailBusinessRules(CashControlDocument transactionalDocument, CashControlDetail cashControlDetail) {

        boolean success = true;
        success &= checkGLPEsNotGenerated(transactionalDocument);
        if (success) {
            success &= validateCashControlDetail(transactionalDocument, cashControlDetail);
        }
        return success;

    }

    /**
     * This method validates CashControlDetail
     * 
     * @param document CashControlDocument
     * @param cashControlDetail CashControlDetail
     * @return true if CashControlDetail is valid, false otherwise
     */
    private boolean validateCashControlDetail(CashControlDocument document, CashControlDetail cashControlDetail) {

        ErrorMap errorMap = GlobalVariables.getErrorMap();

        boolean isValid = true;

        int originalErrorCount = errorMap.getErrorCount();
        // call the DD validation which checks basic data integrity
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(cashControlDetail);
        isValid = (errorMap.getErrorCount() == originalErrorCount);

        // validate customer number and line amount
        if (isValid) {
            String customerNumber = cashControlDetail.getCustomerNumber();
            // if customer number is not empty check that it is valid and not inactive
            if (customerNumber != null && !customerNumber.equals("")) {
                isValid &= checkCustomerNumber(customerNumber);
                isValid &= checkCustomerIsActive(customerNumber);
            }
            // check if line amount is valid
            isValid &= checkLineAmount(document, cashControlDetail);
        }

        return isValid;

    }

    /**
     * This method validates cash control document's details
     * 
     * @param cashControlDocument CashControldocument
     * @return true if valid, false otherwise
     */
    public boolean validateCashControlDetails(CashControlDocument cashControlDocument) {

        GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;

        for (int i = 0; i < cashControlDocument.getCashControlDetails().size(); i++) {

            CashControlDetail cashControlDetail = cashControlDocument.getCashControlDetail(i);
            String propertyName = KFSPropertyConstants.CASH_CONTROL_DETAIL + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);

            isValid &= validateCashControlDetail(cashControlDocument, cashControlDetail);

            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }

        GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
        return isValid;

    }

    /**
     * This method checks that the customer number is valid and not an inactive customer when it is not empty
     * 
     * @param cashControlDetail
     * @return true if valid, false otherwise
     */
    private boolean checkCustomerNumber(String customerNumber) {
        boolean isValid = true;

        if (customerNumber != null && !customerNumber.equals("")) {

            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("customerNumber", customerNumber);

            Customer customer = (Customer) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Customer.class, criteria);

            if (customer == null) {
                GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.CUSTOMER_NUMBER, ArConstants.ERROR_CUSTOMER_NUMBER_IS_NOT_VALID);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * This method checks if the customer is active
     * 
     * @param customerNumber
     * @return true if customer is active, false otherwise
     */
    private boolean checkCustomerIsActive(String customerNumber) {
        boolean isValid = true;

        if (customerNumber != null && !customerNumber.equals("")) {

            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put("customerNumber", customerNumber);

            Customer customer = (Customer) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Customer.class, criteria);

            if (customer != null && !customer.isCustomerActiveIndicator()) {

                GlobalVariables.getErrorMap().putError(ArConstants.CashControlDocumentFields.CUSTOMER_NUMBER, ArConstants.ERROR_CUSTOMER_IS_INACTIVE);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * This method checks if GLPEs have been already generated
     * 
     * @param cashControlDocument the cash control document
     * @return true if it was not generated, false otherwise
     */
    public boolean checkGLPEsNotGenerated(CashControlDocument cashControlDocument) {

        boolean success = true;
        List<GeneralLedgerPendingEntry> glpes = cashControlDocument.getGeneralLedgerPendingEntries();

        if (glpes != null && !glpes.isEmpty()) {
            success = false;
            GlobalVariables.getErrorMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, ArConstants.ERROR_DELETE_ADD_APP_DOCS_NOT_ALLOWED_AFTER_GLPES_GEN);
        }
        return success;

    }

    /**
     * This method checks if all application documents are in approved or in final state
     * 
     * @param cashControlDocument
     * @return true if all application documents approved/final, false otherwise
     */
    public boolean checkAllAppDocsApproved(CashControlDocument cashControlDocument) {

        boolean allAppDocsApproved = true;

        for (int i = 0; i < cashControlDocument.getCashControlDetails().size(); i++) {

            CashControlDetail cashControlDetail = cashControlDocument.getCashControlDetail(i);
            PaymentApplicationDocument applicationDocument = cashControlDetail.getReferenceFinancialDocument();
            KualiWorkflowDocument workflowDocument = applicationDocument.getDocumentHeader().getWorkflowDocument();

            if (!(workflowDocument.stateIsApproved() || workflowDocument.stateIsFinal())) {
                allAppDocsApproved = false;

                String propertyName = KFSPropertyConstants.CASH_CONTROL_DETAIL + "[" + i + "]";
                GlobalVariables.getErrorMap().addToErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);
                GlobalVariables.getErrorMap().addToErrorPath(propertyName);
                GlobalVariables.getErrorMap().put(ArConstants.CashControlDocumentFields.APPLICATION_DOC_STATUS, ArConstants.ERROR_ALL_APPLICATION_DOCS_MUST_BE_APPROVED);
                GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
                GlobalVariables.getErrorMap().removeFromErrorPath(KFSConstants.DOCUMENT_PROPERTY_NAME);

                break;
            }

        }

        return allAppDocsApproved;

    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.DeleteCashControlDetailRule#processDeleteCashControlDetailBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.kfs.module.ar.businessobject.CashControlDetail)
     */
    public boolean processDeleteCashControlDetailBusinessRules(CashControlDocument transactionalDocument, CashControlDetail cashControlDetail) {

        boolean success = true;
        success &= checkGLPEsNotGenerated(transactionalDocument);
        return success;

    }

    /**
     * @see org.kuali.kfs.module.ar.document.validation.GenerateReferenceDocumentRule#processGenerateReferenceDocumentBusinessRules(org.kuali.core.document.TransactionalDocument)
     */
    public boolean processGenerateReferenceDocumentBusinessRules(CashControlDocument transactionalDocument) {

        boolean success = true;
        success &= checkPaymentMedium(transactionalDocument);
        if (success) {
            success &= checkGLPEsNotGenerated(transactionalDocument);
        }
        return success;

    }

}
