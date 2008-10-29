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
package org.kuali.kfs.module.purap.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.batch.service.PurapRunDateService;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.service.PdpExtractService;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpConstants.PurapParameterConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of PdpExtractService
 */
@Transactional
public class PdpExtractServiceImpl implements PdpExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdpExtractServiceImpl.class);

    private PaymentRequestService paymentRequestService;
    private BusinessObjectService businessObjectService;
    private PaymentFileService paymentFileService;
    private ParameterService parameterService;
    private CustomerProfileService customerProfileService;
    private DateTimeService dateTimeService;
    private org.kuali.rice.kim.service.PersonService personService;
    private PaymentGroupService paymentGroupService;
    private PaymentDetailService paymentDetailService;
    private CreditMemoService creditMemoService;
    private DocumentService documentService;
    private PurapRunDateService purapRunDateService;
    private PdpEmailService paymentFileEmailService;
    private BankService bankService;
    private DocumentTypeService documentTypeService;
    private PurapAccountingServiceImpl purapAccountingService;

    /**
     * @see org.kuali.kfs.module.purap.service.PdpExtractService#extractImmediatePaymentsOnly()
     */
    public void extractImmediatePaymentsOnly() {
        LOG.debug("extractImmediatePaymentsOnly() started");

        Date processRunDate = dateTimeService.getCurrentDate();
        extractPayments(true, processRunDate);
    }

    /**
     * @see org.kuali.kfs.module.purap.service.PdpExtractService#extractPayments(Date)
     */
    public void extractPayments(Date runDate) {
        LOG.debug("extractPayments() started");

        extractPayments(false, runDate);
    }

    /**
     * Extracts payments from the database
     * 
     * @param immediateOnly whether to pick up immediate payments only
     * @param processRunDate time/date to use to put on the {@link Batch} that's created; and when immediateOnly is false, is also
     *        used as the maximum allowed PREQ pay date when searching PREQ documents eligible to have payments extracted
     */
    private void extractPayments(boolean immediateOnly, Date processRunDate) {
        LOG.debug("extractPayments() started");

        String userId = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_USER_ID);
        Person uuser = personService.getPersonByPrincipalName(userId);
        if (uuser == null) {
            LOG.error("extractPayments() Unable to find user " + userId);
            throw new IllegalArgumentException("Unable to find user " + userId);
        }

        List<String> campusesToProcess = getChartCodes(immediateOnly, processRunDate);
        for (String campus : campusesToProcess) {
            extractPaymentsForCampus(campus, uuser, processRunDate, immediateOnly);
        }
    }

    /**
     * Handle a single campus
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     */
    private void extractPaymentsForCampus(String campusCode, Person puser, Date processRunDate, boolean immediateOnly) {
        LOG.debug("extractPaymentsForCampus() started for campus: " + campusCode);

        Batch batch = createBatch(campusCode, puser, processRunDate);

        Integer count = 0;
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        // Do all the special ones
        Totals totals = extractSpecialPaymentsForChart(campusCode, puser, processRunDate, batch, immediateOnly);
        count = count + totals.count;
        totalAmount = totalAmount.add(totals.totalAmount);

        if (!immediateOnly) {
            // Do all the regular ones (including credit memos)
            totals = extractRegularPaymentsForChart(campusCode, puser, processRunDate, batch);
            count = count + totals.count;
            totalAmount = totalAmount.add(totals.totalAmount);
        }

        batch.setPaymentCount(new KualiInteger(count));
        batch.setPaymentTotalAmount(totalAmount);

        businessObjectService.save(batch);
        paymentFileEmailService.sendLoadEmail(batch);
    }

    /**
     * Get all the payments that could be combined with credit memos
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     * @param batch
     * @return Totals
     */
    private Totals extractRegularPaymentsForChart(String campusCode, Person puser, Date processRunDate, Batch batch) {
        LOG.debug("START - extractRegularPaymentsForChart()");

        Totals totals = new Totals();

        java.sql.Date onOrBeforePaymentRequestPayDate = new java.sql.Date(purapRunDateService.calculateRunDate(processRunDate).getTime());

        List<String> preqsWithOutstandingCreditMemos = new ArrayList<String>();

        Set<VendorGroupingHelper> vendors = creditMemoService.getVendorsOnCreditMemosToExtract(campusCode);
        for (VendorGroupingHelper vendor : vendors) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Processing Vendor: " + vendor);
            }

            Map<String, List<PaymentRequestDocument>> bankCodePaymentRequests = new HashMap<String, List<PaymentRequestDocument>>();
            Map<String, List<CreditMemoDocument>> bankCodeCreditMemos = new HashMap<String, List<CreditMemoDocument>>();

            // Get all the matching credit memos
            Collection<CreditMemoDocument> vendorMemos = creditMemoService.getCreditMemosToExtractByVendor(campusCode, vendor);
            for (CreditMemoDocument cmd : vendorMemos) {
                List<CreditMemoDocument> bankMemos = new ArrayList<CreditMemoDocument>();
                if (bankCodeCreditMemos.containsKey(cmd.getBankCode())) {
                    bankMemos = (List<CreditMemoDocument>) bankCodeCreditMemos.get(cmd.getBankCode());
                }

                bankMemos.add(cmd);
                bankCodeCreditMemos.put(cmd.getBankCode(), bankMemos);
            }

            // get all matching payment requests
            Collection<PaymentRequestDocument> vendorPreqs = paymentRequestService.getPaymentRequestsToExtractByVendor(campusCode, vendor, onOrBeforePaymentRequestPayDate);
            for (PaymentRequestDocument prd : vendorPreqs) {
                List<PaymentRequestDocument> bankPreqs = new ArrayList<PaymentRequestDocument>();
                if (bankCodePaymentRequests.containsKey(prd.getBankCode())) {
                    bankPreqs = (List<PaymentRequestDocument>) bankCodePaymentRequests.get(prd.getBankCode());
                }

                bankPreqs.add(prd);
                bankCodePaymentRequests.put(prd.getBankCode(), bankPreqs);
            }

            // if bank functionality enabled, create bundles by bank, else just by vendor
            if (bankService.isBankSpecificationEnabled()) {
                for (String bankCode : bankCodePaymentRequests.keySet()) {
                    // if we have credit memos with bank code, process together, else let the preq go and will get picked up below
                    // and processed as a single payment group
                    if (bankCodeCreditMemos.containsKey(bankCode)) {
                        processPaymentBundle(bankCodePaymentRequests.get(bankCode), bankCodeCreditMemos.get(bankCode), totals, preqsWithOutstandingCreditMemos, puser, processRunDate, batch);
                    }
                }
            }
            else {
                if (vendorMemos.isEmpty()) {
                    processPaymentBundle((List<PaymentRequestDocument>) vendorPreqs, (List<CreditMemoDocument>) vendorMemos, totals, preqsWithOutstandingCreditMemos, puser, processRunDate, batch);
                }
            }
        }

        LOG.debug("processing PREQs without CMs");

        // Get all the payment requests to process that do not have credit memos
        Collection<PaymentRequestDocument> paymentRequests = paymentRequestService.getPaymentRequestToExtractByChart(campusCode, onOrBeforePaymentRequestPayDate);
        for (PaymentRequestDocument prd : paymentRequests) {
            // if in the list created above, don't create the payment group
            if (!preqsWithOutstandingCreditMemos.contains(prd.getDocumentNumber())) {
                PaymentGroup paymentGroup = processSinglePaymentRequestDocument(prd, batch, puser, processRunDate);

                totals.count = totals.count + paymentGroup.getPaymentDetails().size();
                totals.totalAmount = totals.totalAmount.add(paymentGroup.getNetPaymentAmount());
            }
        }

        LOG.debug("END - extractRegularPaymentsForChart()");
        return totals;
    }

    /**
     * Processes the list of payment requests and credit memos as a payment group pending
     * 
     * @param paymentRequests
     * @param creditMemos
     * @param totals
     * @param preqsWithOutstandingCreditMemos
     * @param puser
     * @param processRunDate
     * @param batch
     */
    protected void processPaymentBundle(List<PaymentRequestDocument> paymentRequests, List<CreditMemoDocument> creditMemos, Totals totals, List<String> preqsWithOutstandingCreditMemos, Person puser, Date processRunDate, Batch batch) {
        KualiDecimal paymentRequestAmount = KualiDecimal.ZERO;
        for (PaymentRequestDocument paymentRequestDocument : paymentRequests) {
            paymentRequestAmount = paymentRequestAmount.add(paymentRequestDocument.getGrandTotal());
        }

        KualiDecimal creditMemoAmount = KualiDecimal.ZERO;
        for (CreditMemoDocument creditMemoDocument : creditMemos) {
            creditMemoAmount = creditMemoAmount.add(creditMemoDocument.getCreditMemoAmount());
        }

        // if payment amount greater than credit, create bundle
        boolean bundleCreated = false;
        if (paymentRequestAmount.compareTo(creditMemoAmount) >= 0) {
            PaymentGroup paymentGroup = buildPaymentGroup(paymentRequests, creditMemos, batch);

            if (validatePaymentGroup(paymentGroup)) {
                this.businessObjectService.save(paymentGroup);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Created PaymentGroup: " + paymentGroup.getId());
                }

                totals.count++;
                totals.totalAmount = totals.totalAmount.add(paymentGroup.getNetPaymentAmount());

                // mark the CMs and PREQs as processed
                for (CreditMemoDocument cm : creditMemos) {
                    updateCreditMemo(cm, puser, processRunDate);
                }

                for (PaymentRequestDocument pr : paymentRequests) {
                    updatePaymentRequest(pr, puser, processRunDate);
                }

                bundleCreated = true;
            }
        }

        if (!bundleCreated) {
            // add payment request doc numbers to list so they don't get picked up later
            for (PaymentRequestDocument doc : paymentRequests) {
                preqsWithOutstandingCreditMemos.add(doc.getDocumentNumber());
            }
        }
    }

    /**
     * Handle a single payment request with no credit memos
     * 
     * @param paymentRequestDocument
     * @param batch
     * @param puser
     * @param processRunDate
     * @return PaymentGroup
     */
    private PaymentGroup processSinglePaymentRequestDocument(PaymentRequestDocument paymentRequestDocument, Batch batch, Person puser, Date processRunDate) {
        List<PaymentRequestDocument> prds = new ArrayList<PaymentRequestDocument>();
        List<CreditMemoDocument> cmds = new ArrayList<CreditMemoDocument>();
        prds.add(paymentRequestDocument);

        PaymentGroup paymentGroup = buildPaymentGroup(prds, cmds, batch);
        if (validatePaymentGroup(paymentGroup)) {
            this.businessObjectService.save(paymentGroup);
            updatePaymentRequest(paymentRequestDocument, puser, processRunDate);
        }

        return paymentGroup;
    }

    /**
     * Get all the special payments for a campus and process them
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     * @param batch
     * @return Totals
     */
    private Totals extractSpecialPaymentsForChart(String campusCode, Person puser, Date processRunDate, Batch batch, boolean immediatesOnly) {
        Totals totals = new Totals();

        Collection<PaymentRequestDocument> paymentRequests = null;
        if (immediatesOnly) {
            paymentRequests = paymentRequestService.getImmediatePaymentRequestsToExtract(campusCode);
        }
        else {
            java.sql.Date onOrBeforePaymentRequestPayDate = new java.sql.Date(purapRunDateService.calculateRunDate(processRunDate).getTime());
            paymentRequests = paymentRequestService.getPaymentRequestsToExtractSpecialPayments(campusCode, onOrBeforePaymentRequestPayDate);
        }

        for (PaymentRequestDocument prd : paymentRequests) {
            PaymentGroup pg = processSinglePaymentRequestDocument(prd, batch, puser, processRunDate);

            totals.count = totals.count + pg.getPaymentDetails().size();
            totals.totalAmount = totals.totalAmount.add(pg.getNetPaymentAmount());
        }

        return totals;
    }

    /**
     * Mark a credit memo as extracted
     * 
     * @param creditMemoDocument
     * @param puser
     * @param processRunDate
     */
    private void updateCreditMemo(CreditMemoDocument creditMemoDocument, Person puser, Date processRunDate) {
        try {
            CreditMemoDocument doc = (CreditMemoDocument) documentService.getByDocumentHeaderId(creditMemoDocument.getDocumentNumber());
            doc.setExtractedDate(new java.sql.Date(processRunDate.getTime()));
            creditMemoService.saveDocumentWithoutValidation(doc);
        }
        catch (WorkflowException e) {
            throw new IllegalArgumentException("Unable to retrieve credit memo: " + creditMemoDocument.getDocumentNumber());
        }
    }

    /**
     * Mark a payment request as extracted
     * 
     * @param prd
     * @param puser
     * @param processRunDate
     */
    private void updatePaymentRequest(PaymentRequestDocument paymentRequestDocument, Person puser, Date processRunDate) {
        try {
            PaymentRequestDocument doc = (PaymentRequestDocument) documentService.getByDocumentHeaderId(paymentRequestDocument.getDocumentNumber());
            doc.setExtractedDate(new java.sql.Date(processRunDate.getTime()));
            paymentRequestService.saveDocumentWithoutValidation(doc);
        }
        catch (WorkflowException e) {
            throw new IllegalArgumentException("Unable to retrieve payment request: " + paymentRequestDocument.getDocumentNumber());
        }
    }

    /**
     * Create the PDP payment group from a list of payment requests & credit memos
     * 
     * @param paymentRequests
     * @param creditMemos
     * @param batch
     * @return PaymentGroup
     */
    private PaymentGroup buildPaymentGroup(List<PaymentRequestDocument> paymentRequests, List<CreditMemoDocument> creditMemos, Batch batch) {
        // There should always be at least one Payment Request Document in the list.
        PaymentGroup paymentGroup = null;
        if (creditMemos.size() > 0) {
            CreditMemoDocument firstCmd = creditMemos.get(0);
            paymentGroup = populatePaymentGroup(firstCmd, batch);
        }
        else {
            PaymentRequestDocument firstPrd = paymentRequests.get(0);
            paymentGroup = populatePaymentGroup(firstPrd, batch);
        }

        for (PaymentRequestDocument pr : paymentRequests) {
            PaymentDetail pd = populatePaymentDetail(pr, batch);
            paymentGroup.addPaymentDetails(pd);
        }
        for (CreditMemoDocument cm : creditMemos) {
            PaymentDetail pd = populatePaymentDetail(cm, batch);
            paymentGroup.addPaymentDetails(pd);
        }

        return paymentGroup;
    }

    /**
     * Checks payment group note lines does not exceed the maximum allowed
     * 
     * @param paymentGroup group to validate
     * @return true if group is valid, false otherwise
     */
    private boolean validatePaymentGroup(PaymentGroup paymentGroup) {
        // Check to see if the payment group has too many note lines to be printed on a check
        List<PaymentDetail> payDetails = paymentGroup.getPaymentDetails();

        int noteLines = 0;
        for (PaymentDetail paymentDetail : payDetails) {
            noteLines++; // Add one for each payment detail; summary of each detail is included on check and counts as a line
            noteLines += paymentDetail.getNotes().size(); // get all the possible notes for a given detail
        }

        int maxNoteLines = getMaxNoteLines();
        if (noteLines > maxNoteLines) {
            // compile list of all doc numbers that make up payment group
            List<String> preqDocIds = new ArrayList<String>();
            List<String> cmDocIds = new ArrayList<String>();

            String creditMemoDocTypeCode = documentTypeService.getDocumentTypeCodeByClass(CreditMemoDocument.class);

            List<PaymentDetail> pds = paymentGroup.getPaymentDetails();
            for (PaymentDetail payDetail : pds) {
                if (creditMemoDocTypeCode.equals(payDetail.getFinancialDocumentTypeCode())) {
                    cmDocIds.add(payDetail.getCustPaymentDocNbr());
                }
                else {
                    preqDocIds.add(payDetail.getCustPaymentDocNbr());
                }
            }

            // send warning email and prevent group from being processed by returning false
            paymentFileEmailService.sendExceedsMaxNotesWarningEmail(cmDocIds, preqDocIds, noteLines, maxNoteLines);
            
            return false;
        }

        return true;
    }

    /**
     * @return configured maximum number of note lines allowed
     */
    private int getMaxNoteLines() {
        String maxLines = parameterService.getParameterValue(ParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpConstants.ApplicationParameterKeys.MAX_NOTE_LINES);
        if (StringUtils.isBlank(maxLines)) {
            throw new RuntimeException("System parameter for max note lines is blank");
        }

        return Integer.parseInt(maxLines);
    }

    /**
     * Create a PDP payment detail record from a Credit Memo document
     * 
     * @param creditMemoDocument Credit Memo to use for payment detail
     * @param batch current PDP batch object
     * @return populated PaymentDetail line
     */
    private PaymentDetail populatePaymentDetail(CreditMemoDocument creditMemoDocument, Batch batch) {
        PaymentDetail paymentDetail = new PaymentDetail();

        String invoiceNumber = creditMemoDocument.getCreditMemoNumber();
        if (invoiceNumber.length() > 25) {
            invoiceNumber = invoiceNumber.substring(0, 25);
        }
        paymentDetail.setInvoiceNbr(invoiceNumber);
        paymentDetail.setCustPaymentDocNbr(creditMemoDocument.getDocumentNumber());

        if (creditMemoDocument.getPurapDocumentIdentifier() != null) {
            paymentDetail.setPurchaseOrderNbr(creditMemoDocument.getPurapDocumentIdentifier().toString());
        }

        if (creditMemoDocument.getPurchaseOrderDocument() != null) {
            if (creditMemoDocument.getPurchaseOrderDocument().getRequisitionIdentifier() != null) {
                paymentDetail.setRequisitionNbr(creditMemoDocument.getPurchaseOrderDocument().getRequisitionIdentifier().toString());
            }

            if (creditMemoDocument.getDocumentHeader().getOrganizationDocumentNumber() != null) {
                paymentDetail.setOrganizationDocNbr(creditMemoDocument.getDocumentHeader().getOrganizationDocumentNumber());
            }
        }

        String creditMemoDocType = documentTypeService.getDocumentTypeCodeByClass(CreditMemoDocument.class);
        paymentDetail.setFinancialDocumentTypeCode(creditMemoDocType);
        paymentDetail.setFinancialSystemOriginCode(KFSConstants.ORIGIN_CODE_KUALI);

        paymentDetail.setInvoiceDate(new Timestamp(creditMemoDocument.getCreditMemoDate().getTime()));
        paymentDetail.setOrigInvoiceAmount(creditMemoDocument.getCreditMemoAmount().negated());
        paymentDetail.setNetPaymentAmount(creditMemoDocument.getDocumentHeader().getFinancialDocumentTotalAmount().negated());

        KualiDecimal shippingAmount = KualiDecimal.ZERO;
        KualiDecimal discountAmount = KualiDecimal.ZERO;
        KualiDecimal creditAmount = KualiDecimal.ZERO;
        KualiDecimal debitAmount = KualiDecimal.ZERO;

        for (Iterator iter = creditMemoDocument.getItems().iterator(); iter.hasNext();) {
            CreditMemoItem item = (CreditMemoItem) iter.next();

            KualiDecimal itemAmount = KualiDecimal.ZERO;
            if (item.getExtendedPrice() != null) {
                itemAmount = item.getExtendedPrice();
            }
            if (PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode())) {
                discountAmount = discountAmount.subtract(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE.equals(item.getItemTypeCode())) {
                shippingAmount = shippingAmount.subtract(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE.equals(item.getItemTypeCode())) {
                shippingAmount = shippingAmount.add(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE.equals(item.getItemTypeCode())) {
                debitAmount = debitAmount.subtract(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE.equals(item.getItemTypeCode())) {
                if (itemAmount.isNegative()) {
                    creditAmount = creditAmount.subtract(itemAmount);
                }
                else {
                    debitAmount = debitAmount.subtract(itemAmount);
                }
            }
        }

        paymentDetail.setInvTotDiscountAmount(discountAmount);
        paymentDetail.setInvTotShipAmount(shippingAmount);
        paymentDetail.setInvTotOtherCreditAmount(creditAmount);
        paymentDetail.setInvTotOtherDebitAmount(debitAmount);

        paymentDetail.setPrimaryCancelledPayment(Boolean.FALSE);

        addAccounts(creditMemoDocument, paymentDetail, creditMemoDocType);
        addNotes(creditMemoDocument, paymentDetail);

        return paymentDetail;
    }

    /**
     * Create a PDP Payment Detail record from a Payment Request document
     * 
     * @param paymentRequestDocument Payment Request to use for Payment Detail
     * @param batch current PDP batch object
     * @return populated PaymentDetail line
     */
    private PaymentDetail populatePaymentDetail(PaymentRequestDocument paymentRequestDocument, Batch batch) {
        PaymentDetail paymentDetail = new PaymentDetail();

        paymentDetail.setCustPaymentDocNbr(paymentRequestDocument.getDocumentNumber());

        String invoiceNumber = paymentRequestDocument.getInvoiceNumber();
        if (invoiceNumber.length() > 25) {
            invoiceNumber = invoiceNumber.substring(0, 25);
        }
        paymentDetail.setInvoiceNbr(invoiceNumber);

        if (paymentRequestDocument.getPurapDocumentIdentifier() != null) {
            paymentDetail.setPurchaseOrderNbr(paymentRequestDocument.getPurchaseOrderIdentifier().toString());
        }

        if (paymentRequestDocument.getPurchaseOrderDocument().getRequisitionIdentifier() != null) {
            paymentDetail.setRequisitionNbr(paymentRequestDocument.getPurchaseOrderDocument().getRequisitionIdentifier().toString());
        }

        if (paymentRequestDocument.getDocumentHeader().getOrganizationDocumentNumber() != null) {
            paymentDetail.setOrganizationDocNbr(paymentRequestDocument.getDocumentHeader().getOrganizationDocumentNumber());
        }

        String paymentRequestDocType = documentTypeService.getDocumentTypeCodeByClass(PaymentRequestDocument.class);
        paymentDetail.setFinancialDocumentTypeCode(paymentRequestDocType);
        paymentDetail.setFinancialSystemOriginCode(KFSConstants.ORIGIN_CODE_KUALI);
        
        paymentDetail.setInvoiceDate(new Timestamp(paymentRequestDocument.getInvoiceDate().getTime()));
        paymentDetail.setOrigInvoiceAmount(paymentRequestDocument.getVendorInvoiceAmount());
        paymentDetail.setNetPaymentAmount(paymentRequestDocument.getDocumentHeader().getFinancialDocumentTotalAmount());

        KualiDecimal shippingAmount = KualiDecimal.ZERO;
        KualiDecimal discountAmount = KualiDecimal.ZERO;
        KualiDecimal creditAmount = KualiDecimal.ZERO;
        KualiDecimal debitAmount = KualiDecimal.ZERO;

        for (Iterator iter = paymentRequestDocument.getItems().iterator(); iter.hasNext();) {
            PaymentRequestItem item = (PaymentRequestItem) iter.next();

            KualiDecimal itemAmount = KualiDecimal.ZERO;
            if (item.getTotalRemitAmount() != null) {
                itemAmount = item.getTotalRemitAmount();
            }
            if (PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE.equals(item.getItemTypeCode())) {
                discountAmount = discountAmount.add(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_SHIP_AND_HAND_CODE.equals(item.getItemTypeCode())) {
                shippingAmount = shippingAmount.add(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_FREIGHT_CODE.equals(item.getItemTypeCode())) {
                shippingAmount = shippingAmount.add(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_MIN_ORDER_CODE.equals(item.getItemTypeCode())) {
                debitAmount = debitAmount.add(itemAmount);
            }
            else if (PurapConstants.ItemTypeCodes.ITEM_TYPE_MISC_CODE.equals(item.getItemTypeCode())) {
                if (itemAmount.isNegative()) {
                    creditAmount = creditAmount.add(itemAmount);
                }
                else {
                    debitAmount = debitAmount.add(itemAmount);
                }
            }
        }

        paymentDetail.setInvTotDiscountAmount(discountAmount);
        paymentDetail.setInvTotShipAmount(shippingAmount);
        paymentDetail.setInvTotOtherCreditAmount(creditAmount);
        paymentDetail.setInvTotOtherDebitAmount(debitAmount);

        paymentDetail.setPrimaryCancelledPayment(Boolean.FALSE);

        addAccounts(paymentRequestDocument, paymentDetail, paymentRequestDocType);
        addNotes(paymentRequestDocument, paymentDetail);

        return paymentDetail;
    }

    /**
     * Add accounts to a PDP Payment Detail
     * 
     * @param accountsPayableDocument
     * @param paymentDetail
     * @param documentType
     */
    private void addAccounts(AccountsPayableDocument accountsPayableDocument, PaymentDetail paymentDetail, String documentType) {
        String creditMemoDocType = documentTypeService.getDocumentTypeCodeByClass(CreditMemoDocument.class);
        List<SourceAccountingLine> sourceAccountingLines = purapAccountingService.generateSourceAccountsForVendorRemit(accountsPayableDocument);
        for (SourceAccountingLine sourceAccountingLine : sourceAccountingLines) {
          KualiDecimal lineAmount = sourceAccountingLine.getAmount();  
          PaymentAccountDetail paymentAccountDetail = new PaymentAccountDetail();
          paymentAccountDetail.setAccountNbr(sourceAccountingLine.getAccountNumber());

                if (creditMemoDocType.equals(documentType)) {
                    lineAmount = lineAmount.negated();
                }

          paymentAccountDetail.setAccountNetAmount(sourceAccountingLine.getAmount());
          paymentAccountDetail.setFinChartCode(sourceAccountingLine.getChartOfAccountsCode());
          paymentAccountDetail.setFinObjectCode(sourceAccountingLine.getFinancialObjectCode());
          paymentAccountDetail.setFinSubObjectCode(sourceAccountingLine.getFinancialSubObjectCode());
          paymentAccountDetail.setOrgReferenceId(sourceAccountingLine.getOrganizationReferenceId());
          paymentAccountDetail.setProjectCode(sourceAccountingLine.getProjectCode());
          paymentAccountDetail.setSubAccountNbr(sourceAccountingLine.getSubAccountNumber());

            paymentDetail.addAccountDetail(paymentAccountDetail);
        }
//ORIGINAL METHOD
//        String creditMemoDocType = documentTypeService.getDocumentTypeCodeByClass(CreditMemoDocument.class);
//
//        // Calculate the total amount for each account across all items
//        Map<AccountingInfo, KualiDecimal> accountTotals = new HashMap<AccountingInfo, KualiDecimal>();
//
//        for (Iterator iter = accountsPayableDocument.getItems().iterator(); iter.hasNext();) {
//            AccountsPayableItem item = (AccountsPayableItem) iter.next();
//
//            for (Iterator iterator = item.getSourceAccountingLines().iterator(); iterator.hasNext();) {
//                PurApAccountingLine accountingLine = (PurApAccountingLine) iterator.next();
//                AccountingInfo accountingInfo = new AccountingInfo(accountingLine.getChartOfAccountsCode(), accountingLine.getAccountNumber(), accountingLine.getSubAccountNumber(), accountingLine.getFinancialObjectCode(), accountingLine.getFinancialSubObjectCode(), accountingLine.getOrganizationReferenceId(), accountingLine.getProjectCode());
//
//                KualiDecimal lineAmount = accountingLine.getAmount();
//                if (creditMemoDocType.equals(documentType)) {
//                    lineAmount = lineAmount.negated();
//                }
//
//                if (accountTotals.containsKey(accountingInfo)) {
//                    KualiDecimal total = lineAmount.add(accountTotals.get(accountingInfo));
//                    accountTotals.put(accountingInfo, total);
//                }
//                else {
//                    accountTotals.put(accountingInfo, lineAmount);
//                }
//            }
//        }
//
//        for (AccountingInfo accountingInfo : accountTotals.keySet()) {
//            PaymentAccountDetail paymentAccountDetail = new PaymentAccountDetail();
//            paymentAccountDetail.setAccountNbr(accountingInfo.account);
//            paymentAccountDetail.setAccountNetAmount(accountTotals.get(accountingInfo));
//            paymentAccountDetail.setFinChartCode(accountingInfo.chart);
//            paymentAccountDetail.setFinObjectCode(accountingInfo.objectCode);
//            paymentAccountDetail.setFinSubObjectCode(accountingInfo.subObjectCode);
//            paymentAccountDetail.setOrgReferenceId(accountingInfo.orgReferenceId);
//            paymentAccountDetail.setProjectCode(accountingInfo.projectCode);
//            paymentAccountDetail.setSubAccountNbr(accountingInfo.subAccount);
//
//            paymentDetail.addAccountDetail(paymentAccountDetail);
//        }
    }

    /**
     * Add Notes to a PDP Payment Detail
     * 
     * @param accountsPayableDocument
     * @param paymentDetail
     */
    private void addNotes(AccountsPayableDocument accountsPayableDocument, PaymentDetail paymentDetail) {
        int count = 1;

        if (accountsPayableDocument instanceof PaymentRequestDocument) {
            PaymentRequestDocument prd = (PaymentRequestDocument) accountsPayableDocument;

            if (prd.getSpecialHandlingInstructionLine1Text() != null) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(new KualiInteger(count++));
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine1Text());
                paymentDetail.addNote(pnt);
            }

            if (prd.getSpecialHandlingInstructionLine2Text() != null) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(new KualiInteger(count++));
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine2Text());
                paymentDetail.addNote(pnt);
            }

            if (prd.getSpecialHandlingInstructionLine3Text() != null) {
                PaymentNoteText pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(new KualiInteger(count++));
                pnt.setCustomerNoteText(prd.getSpecialHandlingInstructionLine3Text());
                paymentDetail.addNote(pnt);
            }
        }

        if (accountsPayableDocument.getNoteLine1Text() != null) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(count++));
            pnt.setCustomerNoteText(accountsPayableDocument.getNoteLine1Text());
            paymentDetail.addNote(pnt);
        }

        if (accountsPayableDocument.getNoteLine2Text() != null) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(count++));
            pnt.setCustomerNoteText(accountsPayableDocument.getNoteLine2Text());
            paymentDetail.addNote(pnt);
        }

        if (accountsPayableDocument.getNoteLine3Text() != null) {
            PaymentNoteText pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(count++));
            pnt.setCustomerNoteText(accountsPayableDocument.getNoteLine3Text());
            paymentDetail.addNote(pnt);
        }
    }

    /**
     * Populate the PDP Payment Group from fields on a payment request
     * 
     * @param paymentRequestDocument
     * @param batch
     * @return PaymentGroup
     */
    private PaymentGroup populatePaymentGroup(PaymentRequestDocument paymentRequestDocument, Batch batch) {
        LOG.debug("populatePaymentGroup() payment request documentNumber: " + paymentRequestDocument.getDocumentNumber());

        PaymentGroup paymentGroup = new PaymentGroup();

        paymentGroup.setBatchId(batch.getId());
        paymentGroup.setPaymentStatusCode(KFSConstants.PdpConstants.PAYMENT_OPEN_STATUS_CODE);

        String postalCode = paymentRequestDocument.getVendorPostalCode();
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(paymentRequestDocument.getVendorCountry())) {
            // Add a dash in the zip code if necessary
            if (postalCode.length() > 5) {
                postalCode = postalCode.substring(0, 5) + "-" + postalCode.substring(5);
            }
        }

        paymentGroup.setPayeeName(paymentRequestDocument.getVendorName());
        paymentGroup.setPayeeId(paymentRequestDocument.getVendorHeaderGeneratedIdentifier() + "-" + paymentRequestDocument.getVendorDetailAssignedIdentifier());
        paymentGroup.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.VENDOR_ID);

        if (paymentRequestDocument.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode() != null) {
            paymentGroup.setPayeeOwnerCd(paymentRequestDocument.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode());
        }

        if (paymentRequestDocument.getVendorCustomerNumber() != null) {
            paymentGroup.setCustomerInstitutionNumber(paymentRequestDocument.getVendorCustomerNumber());
        }

        paymentGroup.setLine1Address(paymentRequestDocument.getVendorLine1Address());
        paymentGroup.setLine2Address(paymentRequestDocument.getVendorLine2Address());
        paymentGroup.setLine3Address("");
        paymentGroup.setLine4Address("");
        paymentGroup.setCity(paymentRequestDocument.getVendorCityName());
        paymentGroup.setState(paymentRequestDocument.getVendorStateCode());
        paymentGroup.setZipCd(postalCode);
        paymentGroup.setCountry(paymentRequestDocument.getVendorCountryCode());
        paymentGroup.setCampusAddress(Boolean.FALSE);

        if (paymentRequestDocument.getPaymentRequestPayDate() != null) {
            paymentGroup.setPaymentDate(new Timestamp(paymentRequestDocument.getPaymentRequestPayDate().getTime()));
        }

        paymentGroup.setPymtAttachment(paymentRequestDocument.getPaymentAttachmentIndicator());
        paymentGroup.setProcessImmediate(paymentRequestDocument.getImmediatePaymentIndicator());
        paymentGroup.setPymtSpecialHandling(StringUtils.isNotBlank(paymentRequestDocument.getSpecialHandlingInstructionLine1Text()) || StringUtils.isNotBlank(paymentRequestDocument.getSpecialHandlingInstructionLine2Text()) || StringUtils.isNotBlank(paymentRequestDocument.getSpecialHandlingInstructionLine3Text()));
        paymentGroup.setTaxablePayment(Boolean.FALSE);
        paymentGroup.setNraPayment(VendorConstants.OwnerTypes.NR.equals(paymentRequestDocument.getVendorDetail().getVendorHeader().getVendorOwnershipCode()));
        paymentGroup.setCombineGroups(Boolean.TRUE);

        return paymentGroup;
    }

    /**
     * Populates a PaymentGroup record from a Credit Memo document
     * 
     * @param creditMemoDocument
     * @param batch
     * @return PaymentGroup
     */
    private PaymentGroup populatePaymentGroup(CreditMemoDocument creditMemoDocument, Batch batch) {
        LOG.debug("populatePaymentGroup() credit memo documentNumber: " + creditMemoDocument.getDocumentNumber());

        PaymentGroup paymentGroup = new PaymentGroup();
        paymentGroup.setBatchId(batch.getId());
        paymentGroup.setPaymentStatusCode(KFSConstants.PdpConstants.PAYMENT_OPEN_STATUS_CODE);

        String postalCode = creditMemoDocument.getVendorPostalCode();
        if (KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(creditMemoDocument.getVendorCountry())) {
            // Add a dash in the zip code if necessary
            if (postalCode.length() > 5) {
                postalCode = postalCode.substring(0, 5) + "-" + postalCode.substring(5);
            }
        }

        paymentGroup.setPayeeName(creditMemoDocument.getVendorName());
        paymentGroup.setPayeeId(creditMemoDocument.getVendorHeaderGeneratedIdentifier() + "-" + creditMemoDocument.getVendorDetailAssignedIdentifier());
        paymentGroup.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.VENDOR_ID);

        if (creditMemoDocument.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode() != null) {
            paymentGroup.setPayeeOwnerCd(creditMemoDocument.getVendorDetail().getVendorHeader().getVendorOwnershipCategoryCode());
        }

        if (creditMemoDocument.getVendorCustomerNumber() != null) {
            paymentGroup.setCustomerInstitutionNumber(creditMemoDocument.getVendorCustomerNumber());
        }

        paymentGroup.setLine1Address(creditMemoDocument.getVendorLine1Address());
        paymentGroup.setLine2Address(creditMemoDocument.getVendorLine2Address());
        paymentGroup.setLine3Address("");
        paymentGroup.setLine4Address("");
        paymentGroup.setCity(creditMemoDocument.getVendorCityName());
        paymentGroup.setState(creditMemoDocument.getVendorStateCode());
        paymentGroup.setZipCd(postalCode);
        paymentGroup.setCountry(creditMemoDocument.getVendorCountryCode());
        paymentGroup.setCampusAddress(Boolean.FALSE);

        if (creditMemoDocument.getCreditMemoDate() != null) {
            paymentGroup.setPaymentDate(new Timestamp(creditMemoDocument.getCreditMemoDate().getTime()));
        }

        paymentGroup.setPymtAttachment(Boolean.FALSE);
        paymentGroup.setProcessImmediate(Boolean.FALSE);
        paymentGroup.setPymtSpecialHandling(Boolean.FALSE);
        paymentGroup.setTaxablePayment(Boolean.FALSE);
        paymentGroup.setNraPayment(VendorConstants.OwnerTypes.NR.equals(creditMemoDocument.getVendorDetail().getVendorHeader().getVendorOwnershipCode()));
        paymentGroup.setCombineGroups(Boolean.TRUE);

        return paymentGroup;
    }

    /**
     * Create a new PDP batch
     * 
     * @param campusCode
     * @param puser
     * @param processRunDate
     * @return Batch
     */
    private Batch createBatch(String campusCode, Person puser, Date processRunDate) {
        String orgCode = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_EPIC_ORG_CODE);
        String subUnitCode = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_EPIC_SBUNT_CODE);
        CustomerProfile customer = customerProfileService.get(campusCode, orgCode, subUnitCode);
        if (customer == null) {
            throw new IllegalArgumentException("Unable to find customer profile for " + campusCode + "/" + orgCode + "/" + subUnitCode);
        }

        // Create the group for this campus
        Batch batch = new Batch();
        batch.setCustomerProfile(customer);
        batch.setCustomerFileCreateTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setFileProcessTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setPaymentFileName(PurapConstants.PDP_PURAP_EXTRACT_FILE_NAME);
        batch.setSubmiterUser(puser);

        // Set these for now, we will update them later
        batch.setPaymentCount(KualiInteger.ZERO);
        batch.setPaymentTotalAmount(KualiDecimal.ZERO);

        businessObjectService.save(batch);

        return batch;
    }

    /**
     * Find all the campuses that have payments to process
     * 
     * @return List<String>
     */
    private List<String> getChartCodes(boolean immediatesOnly, Date processRunDate) {
        List<String> output = new ArrayList<String>();

        Collection<PaymentRequestDocument> paymentRequests = null;
        if (immediatesOnly) {
            paymentRequests = paymentRequestService.getImmediatePaymentRequestsToExtract(null);
        }
        else {
            java.sql.Date onOrBeforePaymentRequestPayDate = new java.sql.Date(purapRunDateService.calculateRunDate(processRunDate).getTime());
            paymentRequests = paymentRequestService.getPaymentRequestsToExtract(onOrBeforePaymentRequestPayDate);
        }

        for (PaymentRequestDocument prd : paymentRequests) {
            if (!output.contains(prd.getProcessingCampusCode())) {
                output.add(prd.getProcessingCampusCode());
            }
        }

        return output;
    }

    /**
     * Holds total count and amount for extract
     */
    private class Totals {
        public Integer count = 0;
        public KualiDecimal totalAmount = KualiDecimal.ZERO;
    }

    /**
     * Holds temporary accounting information for combining into payment accounting details
     */
    private class AccountingInfo {
        private String chart;
        private String account;
        private String subAccount;
        private String objectCode;
        private String subObjectCode;
        private String orgReferenceId;
        private String projectCode;

        public AccountingInfo(String c, String a, String s, String o, String so, String or, String pc) {
            setChart(c);
            setAccount(a);
            setSubAccount(s);
            setObjectCode(o);
            setSubObjectCode(so);
            setOrgReferenceId(or);
            setProjectCode(pc);
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public void setChart(String chart) {
            this.chart = chart;
        }

        public void setObjectCode(String objectCode) {
            this.objectCode = objectCode;
        }

        public void setOrgReferenceId(String orgReferenceId) {
            this.orgReferenceId = orgReferenceId;
        }

        public void setProjectCode(String projectCode) {
            if (projectCode == null) {
                this.projectCode = KFSConstants.getDashProjectCode();
            }
            else {
                this.projectCode = projectCode;
            }
        }

        public void setSubAccount(String subAccount) {
            if (subAccount == null) {
                this.subAccount = KFSConstants.getDashSubAccountNumber();
            }
            else {
                this.subAccount = subAccount;
            }
        }

        public void setSubObjectCode(String subObjectCode) {
            if (subObjectCode == null) {
                this.subObjectCode = KFSConstants.getDashFinancialSubObjectCode();
            }
            else {
                this.subObjectCode = subObjectCode;
            }
        }

        private String key() {
            return chart + "~" + account + "~" + subAccount + "~" + objectCode + "~" + subObjectCode + "~" + orgReferenceId + "~" + projectCode;
        }

        public int hashCode() {
            return new HashCodeBuilder(3, 5).append(key()).toHashCode();
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof AccountingInfo)) {
                return false;
            }
            AccountingInfo thisobj = (AccountingInfo) obj;
            return new EqualsBuilder().append(key(), thisobj.key()).isEquals();
        }
    }

    /**
     * Sets the paymentRequestService attribute value.
     * 
     * @param paymentRequestService The paymentRequestService to set.
     */
    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
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
     * Sets the paymentFileService attribute value.
     * 
     * @param paymentFileService The paymentFileService to set.
     */
    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
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
     * Sets the customerProfileService attribute value.
     * 
     * @param customerProfileService The customerProfileService to set.
     */
    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the personService attribute value.
     * 
     * @param personService The personService to set.
     */
    public void setPersonService(org.kuali.rice.kim.service.PersonService personService) {
        this.personService = personService;
    }

    /**
     * Sets the paymentGroupService attribute value.
     * 
     * @param paymentGroupService The paymentGroupService to set.
     */
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    /**
     * Sets the paymentDetailService attribute value.
     * 
     * @param paymentDetailService The paymentDetailService to set.
     */
    public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    /**
     * Sets the creditMemoService attribute value.
     * 
     * @param creditMemoService The creditMemoService to set.
     */
    public void setCreditMemoService(CreditMemoService creditMemoService) {
        this.creditMemoService = creditMemoService;
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
     * Sets the purapRunDateService attribute value.
     * 
     * @param purapRunDateService The purapRunDateService to set.
     */
    public void setPurapRunDateService(PurapRunDateService purapRunDateService) {
        this.purapRunDateService = purapRunDateService;
    }

    /**
     * Sets the paymentFileEmailService attribute value.
     * 
     * @param paymentFileEmailService The paymentFileEmailService to set.
     */
    public void setPaymentFileEmailService(PdpEmailService paymentFileEmailService) {
        this.paymentFileEmailService = paymentFileEmailService;
    }

    /**
     * Sets the bankService attribute value.
     * 
     * @param bankService The bankService to set.
     */
    public void setBankService(BankService bankService) {
        this.bankService = bankService;
    }

    /**
     * Sets the documentTypeService attribute value.
     * 
     * @param documentTypeService The documentTypeService to set.
     */
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    public void setPurapAccountingService(PurapAccountingServiceImpl purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
}
}

