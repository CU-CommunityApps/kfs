/*
 * Copyright 2013 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the PaymentSourceToExtractServiceImpl and TravelAuthorizatoinDocumentPaymentServices which will feed travel authorizations
 * and travel authorization amendments with travel advances to PDP
 */
@Transactional
public class TravelAuthorizationDocumentExtractionHelperServiceImpl implements PaymentSourceToExtractService<TravelAuthorizationDocument> {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TravelAuthorizationDocumentExtractionHelperServiceImpl.class);
    protected TravelDocumentDao travelDocumentDao;
    protected TravelPaymentsHelperService travelPaymentsHelperService;
    protected DocumentService documentService;
    protected PaymentSourceHelperService paymentSourceHelperService;
    protected ParameterService parameterService;

    /**
     *
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#retrievePaymentSourcesByCampus(boolean)
     */
    @Override
    public Map<String, List<TravelAuthorizationDocument>> retrievePaymentSourcesByCampus(boolean immediatesOnly) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrievePaymentSourcesByCampus() started");
        }

        Map<String, List<TravelAuthorizationDocument>> documentsByCampus = new HashMap<String, List<TravelAuthorizationDocument>>();
        final List<? extends TravelAuthorizationDocument> authorizations = retrieveAllApprovedAuthorizationDocuments(immediatesOnly);
        Map<String, String> initiatorCampuses = new HashMap<String, String>();
        for (TravelAuthorizationDocument document : authorizations) {
            final String campusCode = getTravelPaymentsHelperService().findCampusForDocument(document, initiatorCampuses);
            if (!StringUtils.isBlank(campusCode)) {
                List<TravelAuthorizationDocument> documentsForCampus = documentsByCampus.get(campusCode);
                if (documentsForCampus == null) {
                    documentsForCampus = new ArrayList<TravelAuthorizationDocument>();
                    documentsByCampus.put(campusCode, documentsForCampus);
                }
                documentsForCampus.add(document);
            }
        }
        return documentsByCampus;
    }

    /**
     * Retrieves all the TravelReimbursement, TravelRelocation, and TravelEntertainment documents paid by check at approved status in one convenient call
     * @param immediatesOnly true if only those documents marked for immediate payment should be retrieved, false if all qualifying documents should be retrieved
     * @return all of the documents to process in a list
     */
    protected List<? extends TravelAuthorizationDocument> retrieveAllApprovedAuthorizationDocuments(boolean immediatesOnly) {
        List<TravelAuthorizationDocument> authorizationsToProcess = new ArrayList<TravelAuthorizationDocument>();
        final Collection<? extends TravelAuthorizationDocument> authorizations = getTravelDocumentDao().getAuthorizationsAndAmendmentsByHeaderStatus(KFSConstants.DocumentStatusCodes.APPROVED, immediatesOnly);
        for (TravelAuthorizationDocument authorization : authorizations) {
            if (authorization.shouldProcessAdvanceForDocument()) {
                authorizationsToProcess.add(authorization);
            }
        }
        return authorizationsToProcess;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelAuthorizationDocumentPaymentService#cancelReimbursableDocument(org.kuali.kfs.module.tem.document.TravelAuthorizationDocument, java.sql.Date)
     */
    @Override
    public void cancelPayment(TravelAuthorizationDocument authorizationDoc, Date cancelDate) {
        if (authorizationDoc.getAdvanceTravelPayment().getCancelDate() == null) {
            try {
                authorizationDoc.getAdvanceTravelPayment().setCancelDate(cancelDate);
                getPaymentSourceHelperService().handleEntryCancellation(authorizationDoc, this);
                authorizationDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
                // save the document
                getDocumentService().saveDocument(authorizationDoc, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            }
            catch (WorkflowException we) {
                LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + authorizationDoc.getDocumentNumber() + " " + we);
                throw new RuntimeException(we);
            }
        }
    }

    /**
     * Returns true if the GLPE has a doc type of TACA or TAWF
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#shouldRollBackPendingEntry(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
     */
    @Override
    public boolean shouldRollBackPendingEntry(GeneralLedgerPendingEntry entry) {
        return StringUtils.equals(entry.getFinancialDocumentTypeCode(), TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CHECK_ACH_DOCUMENT) || StringUtils.equals(entry.getFinancialDocumentTypeCode(), TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_WIRE_OR_FOREIGN_DRAFT_DOCUMENT);
    }

    /**
     * Sets the paid date on the travel payment
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#markAsPaid(org.kuali.kfs.sys.document.PaymentSource, java.sql.Date)
     */
    @Override
    public void markAsPaid(TravelAuthorizationDocument paymentSource, Date processDate) {
        try {
            paymentSource.getAdvanceTravelPayment().setPaidDate(processDate);
            getDocumentService().saveDocument(paymentSource, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + paymentSource.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * This involves rolling back the extracted and paid dates and setting the document to approved
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#resetFromExtraction(org.kuali.kfs.sys.document.PaymentSource)
     */
    @Override
    public void resetFromExtraction(TravelAuthorizationDocument paymentSource) {
        try {
            paymentSource.getAdvanceTravelPayment().setExtractDate(null);
            paymentSource.getAdvanceTravelPayment().setPaidDate(null);
            paymentSource.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
            getDocumentService().saveDocument(paymentSource, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + paymentSource.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#createPaymentGroup(org.kuali.rice.krad.document.Document, java.sql.Date)
     */
    @Override
    public PaymentGroup createPaymentGroup(TravelAuthorizationDocument authorizationDoc, Date processDate) {
        if (authorizationDoc.shouldProcessAdvanceForDocument()) {
            PaymentGroup pg = getTravelPaymentsHelperService().buildGenericPaymentGroup(authorizationDoc.getTraveler(), authorizationDoc.getTemProfile(), authorizationDoc.getAdvanceTravelPayment(), authorizationDoc.getFinancialDocumentBankCode());

            // now add the payment detail
            final PaymentDetail paymentDetail = buildPaymentDetail(authorizationDoc, processDate);
            pg.addPaymentDetails(paymentDetail);
            paymentDetail.setPaymentGroup(pg);

            return pg;
        }
        return null;
    }

    /**
     * Builds the PaymentDetail for the given authorization document
     * @param document the authorization document to create a payment for
     * @param processRunDate the date when the extraction is occurring
     * @return a PaymentDetail to add to the PaymentGroup
     */
    protected PaymentDetail buildPaymentDetail(TravelAuthorizationDocument document, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("buildPaymentDetail() started");
        }

        PaymentDetail pd = getTravelPaymentsHelperService().buildGenericPaymentDetail(document.getDocumentHeader(), processRunDate, document.getAdvanceTravelPayment(), getTravelPaymentsHelperService().getInitiator(document), getAchCheckDocumentType(document));
        pd.setPurchaseOrderNbr(document.getTravelDocumentIdentifier());
        pd.setOrganizationDocNbr(document.getTravelDocumentIdentifier());
        // Handle accounts
        final List<PaymentAccountDetail> paymentAccounts = this.getTravelPaymentsHelperService().buildGenericPaymentAccountDetails(document.getAdvanceAccountingLines());
        for (PaymentAccountDetail pad : paymentAccounts) {
            pd.addAccountDetail(pad);
        }

        return pd;
    }

    /**
     * Uses the value in the KFS-TEM / Document / PRE_DISBURSEMENT_EXTRACT_ORGANIZATION parameter
     * @see org.kuali.kfs.sys.document.PaymentSource#getPreDisbursementCustomerProfileUnit()
     */
    @Override
    public String getPreDisbursementCustomerProfileUnit() {
        final String unit = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_ORG_CODE);
        return unit;
    }


    /**
     * Uses the value in the KFS-TEM / Document / PRE_DISBURSEMENT_EXTRACT_SUB_UNIT
     * @see org.kuali.kfs.sys.document.PaymentSource#getPreDisbursementCustomerProfileSubUnit()
     */
    @Override
    public String getPreDisbursementCustomerProfileSubUnit() {
        final String subUnit = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_SBUNT_CODE);
        return subUnit;
    }

    /**
     * Marks the advance travel payment as extracted
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#markAsExtracted(org.kuali.rice.krad.document.Document, java.sql.Date)
     */
    @Override
    public void markAsExtracted(TravelAuthorizationDocument document, Date sqlProcessRunDate, KualiInteger paymentGroupId) {
        try {
            document.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.Payments.EXTRACTED);
            document.getAdvanceTravelPayment().setExtractDate(sqlProcessRunDate);
            getDocumentService().saveDocument(document, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("Could not save TravelAuthorizationDocument document #" + document.getDocumentNumber() + ": " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * If advance on the document should be processed, returns the check total from the advance travel payment; otherwise returns 0
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#getPaymentAmount(org.kuali.rice.krad.document.Document)
     */
    @Override
    public KualiDecimal getPaymentAmount(TravelAuthorizationDocument document) {
        if (document.shouldProcessAdvanceForDocument()) {
            return document.getAdvanceTravelPayment().getCheckTotalAmount();
        }
        return KualiDecimal.ZERO;
    }

    /**
     * Returns "TACA"
     * @see org.kuali.kfs.sys.document.PaymentSource#getAchCheckDocumentType()
     */
    @Override
    public String getAchCheckDocumentType(TravelAuthorizationDocument document) {
        return TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CHECK_ACH_DOCUMENT;
    }

    /**
     * Returns true if doc type is TACA, false otherwise
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#handlesAchCheckDocumentType(java.lang.String)
     */
    @Override
    public boolean handlesAchCheckDocumentType(String achCheckDocumentType) {
        return StringUtils.equals(achCheckDocumentType, TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CHECK_ACH_DOCUMENT);
    }

    /**
     * Returns the value of the KFS-TEM / Document / IMMEDIATE_EXTRACT_NOTIFICATION_FROM_EMAIL_ADDRESS parameter
     * @see org.kuali.kfs.sys.document.PaymentSource#getImmediateExtractEMailFromAddress()
     */
    @Override
    public String getImmediateExtractEMailFromAddress() {
        return getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, KFSParameterKeyConstants.PdpExtractBatchParameters.IMMEDIATE_EXTRACT_FROM_ADDRESS_PARM_NM);
    }

    /**
     * Returns the value of the KFS-TEM / Document / IMMEDIATE_EXTRACT_NOTIFICATION_TO_EMAIL_ADDRESSES parameter
     * @see org.kuali.kfs.sys.document.PaymentSource#getImmediateExtractEmailToAddresses()
     */
    @Override
    public List<String> getImmediateExtractEmailToAddresses() {
        List<String> toAddresses = new ArrayList<String>();
        toAddresses.addAll(getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, KFSParameterKeyConstants.PdpExtractBatchParameters.IMMEDIATE_EXTRACT_TO_ADDRESSES_PARM_NM));
        return toAddresses;
    }

    /**
     * Determines if the payment would be 0 - if it's greater than that, it should be extracted
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#shouldExtractPayment(org.kuali.kfs.sys.document.PaymentSource)
     */
    @Override
    public boolean shouldExtractPayment(TravelAuthorizationDocument paymentSource) {
        return KualiDecimal.ZERO.isLessThan(getPaymentAmount(paymentSource));
    }

    /**
     * @return an implementation of the DAO for TravelDocuments
     */
    public TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    /**
     * Sets the implementation of the DAO for TravelDocuments for this service to use
     * @param parameterService an implementation of the data access object for travel documents
     */
    public void setTravelDocumentDao(TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    /**
     * @return an implementation of the TravelPaymentsHelperService
     */
    public TravelPaymentsHelperService getTravelPaymentsHelperService() {
        return travelPaymentsHelperService;
    }

    /**
     * Sets the implementation of the TravelPaymentsHelperService for this service to use
     * @param travelPaymentsHelperService an implementation of the TravelPaymentsHelperService
     */
    public void setTravelPaymentsHelperService(TravelPaymentsHelperService travelPaymentsHelperService) {
        this.travelPaymentsHelperService = travelPaymentsHelperService;
    }

    /**
     * @return an implementation of the DocumentService
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the implementation of the DocumentService for this service to use
     * @param parameterService an implementation of DocumentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * @return an implementation of the PaymentSourceHelperService
     */
    public PaymentSourceHelperService getPaymentSourceHelperService() {
        return paymentSourceHelperService;
    }

    /**
     * Sets the implementation of the PaymentSourceHelperService for this service to use
     * @param parameterService an implementation of PaymentSourceHelperService
     */
    public void setPaymentSourceHelperService(PaymentSourceHelperService paymentSourceHelperService) {
        this.paymentSourceHelperService = paymentSourceHelperService;
    }

    /**
     * @return the injected implementation of the ParameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Injects an implementation of the ParameterService
     * @param parameterService the implementation of the ParameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
