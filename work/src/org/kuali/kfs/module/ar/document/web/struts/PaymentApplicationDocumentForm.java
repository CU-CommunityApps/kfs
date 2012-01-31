/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.web.struts;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.springframework.util.CollectionUtils;

public class PaymentApplicationDocumentForm extends FinancialSystemTransactionalDocumentFormBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentForm.class);;

    protected String selectedInvoiceDocumentNumber;
    protected String enteredInvoiceDocumentNumber;
    protected String selectedCustomerNumber;
    protected KualiDecimal unappliedCustomerAmount;
    protected PaymentApplicationInvoiceApply selectedInvoiceApplication;
    protected NonInvoiced nonInvoicedAddLine;
    protected Integer nextNonInvoicedLineNumber;
    protected KualiDecimal nonAppliedHoldingAmount;
    protected String nonAppliedHoldingCustomerNumber;

    protected List<PaymentApplicationInvoiceApply> invoiceApplications;
    protected List<CustomerInvoiceDocument> invoices;

    // used for non-cash-control pay app docs
    protected List<PaymentApplicationDocument> nonAppliedControlDocs;
    protected List<NonAppliedHolding> nonAppliedControlHoldings;
    protected Map<String, KualiDecimal> nonAppliedControlAllocations;
    protected Map<String, KualiDecimal> distributionsFromControlDocs;

    /**
     * Constructs a PaymentApplicationDocumentForm.java.
     */
    @SuppressWarnings("unchecked")
    public PaymentApplicationDocumentForm() {
        super();
        nonInvoicedAddLine = new NonInvoiced();
        invoices = new ArrayList<CustomerInvoiceDocument>();
        selectedInvoiceApplication = null;
        invoiceApplications = new ArrayList<PaymentApplicationInvoiceApply>();
        nonAppliedControlDocs = new ArrayList<PaymentApplicationDocument>();
        nonAppliedControlHoldings = new ArrayList<NonAppliedHolding>();
        nonAppliedControlAllocations = new HashMap<String, KualiDecimal>();
        distributionsFromControlDocs = new HashMap<String, KualiDecimal>();
    }

    /**
     * @param property
     * @param source
     * @param altText
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "APP";
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.ServletRequest)
     */
    @Override
    public void reset(ActionMapping mapping, ServletRequest request) {
        super.reset(mapping, request);
        for (PaymentApplicationInvoiceApply application : invoiceApplications) {
            application.setQuickApply(false);
        }
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        // Set the next non-invoiced line number
        PaymentApplicationDocument paymentApplicationDocument = getPaymentApplicationDocument();
        if (ObjectUtils.isNotNull(paymentApplicationDocument.getNonInvoicedDistributions())) {
            for (NonInvoicedDistribution u : paymentApplicationDocument.getNonInvoicedDistributions()) {
                if (null == getNextNonInvoicedLineNumber()) {
                    setNextNonInvoicedLineNumber(u.getFinancialDocumentLineNumber());
                }
                else if (u.getFinancialDocumentLineNumber() > getNextNonInvoicedLineNumber()) {
                    setNextNonInvoicedLineNumber(u.getFinancialDocumentLineNumber());
                }
            }
        }

        if (null == getNextNonInvoicedLineNumber()) {
            setNextNonInvoicedLineNumber(1);
        }

        // This step doesn't affect anything persisted to the database. It allows proper calculation
        // of amounts for the display.
        String customerNumber = null;
        String docId = getDocument().getDocumentNumber();
        if (ObjectUtils.isNotNull(request.getParameter(KFSConstants.PARAMETER_DOC_ID)) && ObjectUtils.isNull(getDocument().getDocumentNumber())) {
            // The document hasn't yet been set on the form. Let's look it up manually so that we can get the customer number.
            docId = request.getParameter(KFSConstants.PARAMETER_DOC_ID).trim();
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            Document d;
            try {
                d = documentService.getByDocumentHeaderId(docId);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("WorkflowException thrown when trying to load docId [" + docId + "]", e);
            }
            PaymentApplicationDocument pDocument = (PaymentApplicationDocument) d;
            AccountsReceivableDocumentHeader arHeader = pDocument.getAccountsReceivableDocumentHeader();
            if (ObjectUtils.isNotNull(arHeader)) {
                customerNumber = arHeader.getCustomerNumber();
            }


            if (ObjectUtils.isNull(getSelectedInvoiceApplication())) {
                if (ObjectUtils.isNull(invoices) || invoices.isEmpty()) {
                    if (ObjectUtils.isNotNull(customerNumber)) {
                        // to get the paymentApplicaiton Document using the DocumentService
                        Collection<ContractsGrantsInvoiceDocument> cgInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
                        Collection<ContractsGrantsInvoiceDocument> newCGInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
                        Collection<CustomerInvoiceDocument> newCustInvoices = new ArrayList<CustomerInvoiceDocument>();
                        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                        // To check the document type of the invoice document and retrieve invoices based on that - this would help
                        // accomodating CG Invoices.


                        if (ObjectUtils.isNotNull(pDocument.getInvoiceDocumentType()) && pDocument.getInvoiceDocumentType().equalsIgnoreCase(ArConstants.INV_DOCUMENT_TYPE)) {
                            // get open invoices for the current customer
                            CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);

                            Collection<CustomerInvoiceDocument> openInvoicesForCustomer;
                            // if it is not a correction then get open invoices
                            // otherwise, if it is a payment correction, then get the invoices from what the original
                            // PaymentApplicationDocument was applied to.
                            if (!pDocument.isPaymentApplicationCorrection()) {
                                openInvoicesForCustomer = customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerNumber(customerNumber);
                            }
                            else {
                                openInvoicesForCustomer = customerInvoiceDocumentService.getCustomerInvoiceDocumentAppliedByPaymentApplicationNumber(pDocument.getDocumentHeader().getFinancialDocumentInErrorNumber());
                            }

                            // To add only Customer Invoices to the list. This is done specific for cash control and payment
                            // applications.
                            for (CustomerInvoiceDocument custInv : openInvoicesForCustomer) {
                                try {
                                    String docType = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(custInv.getDocumentNumber()).getDocumentHeader().getWorkflowDocument().getDocumentType();
                                    if (docType.equalsIgnoreCase(ArConstants.INV_DOCUMENT_TYPE)) {
                                        newCustInvoices.add(custInv);
                                    }
                                }
                                catch (WorkflowException e) {
                                    throw new RuntimeException("Caught WorkflowException trying to get document type name from Workflow", e);
                                }
                            }


                            setInvoices(new ArrayList<CustomerInvoiceDocument>(newCustInvoices));
                        }
                        // When the invoices are of type CG Invoices.
                        else if (ObjectUtils.isNotNull(pDocument.getInvoiceDocumentType()) && pDocument.getInvoiceDocumentType().equalsIgnoreCase(ArConstants.CGIN_DOCUMENT_TYPE)) {
                            // To retrieve the batch file directory name as "reports/cg"
                            ModuleConfiguration systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-AR").getModuleConfiguration();

                            String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);

                            String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());
                            String errOutputFile = destinationFolderPath + File.separator + ArConstants.BatchFileSystem.LOC_CREATION_PMT_APP_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
                            // To create error file and store all the errors in it.
                            File errOutPutFile = new File(errOutputFile);
                            PrintStream outputFileStream = null;

                            try {
                                outputFileStream = new PrintStream(errOutPutFile);
                            }
                            catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // Get the CG Service to deal with each loc creation type.
                            ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);

                            if (!pDocument.isPaymentApplicationCorrection()) {
                                if (StringUtils.isNotEmpty(pDocument.getLocCreationType())) {

                                    if (pDocument.getLocCreationType().equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND)) {
                                        // Retrieve open and final CG Invoices that have loc creation type = LOC Fund.
                                        if (StringUtils.isNotEmpty(pDocument.getLetterOfCreditFundCode())) {
                                            cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByLOCFund(pDocument.getLetterOfCreditFundCode(), outputFileStream);
                                        }

                                    }
                                    else if (pDocument.getLocCreationType().equalsIgnoreCase(ArConstants.LOC_BY_LOC_FUND_GRP)) {
                                        // Retrieve open and final CG Invoices that have loc creation type = LOC Fund group.
                                        if (StringUtils.isNotEmpty(pDocument.getLetterOfCreditFundGroupCode())) {
                                            cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByLOCFundGroup(pDocument.getLetterOfCreditFundGroupCode(), outputFileStream);
                                        }
                                    }

                                    outputFileStream.close();// Closing the file here to avoid any further writing to the document.
                                }
                                else {
                                    // Retrieve open and final CG Invoices that have a customer number defined. This would ideally
                                    // not occur.
                                    cgInvoices = contractsGrantsInvoiceDocumentService.retrieveOpenAndFinalCGInvoicesByCustomerNumber(customerNumber, outputFileStream);
                                }
                            }
                            else {
                                cgInvoices = contractsGrantsInvoiceDocumentService.getContractsGrantsInvoiceDocumentAppliedByPaymentApplicationNumber(pDocument.getDocumentHeader().getFinancialDocumentInErrorNumber());
                            }

                            if (!CollectionUtils.isEmpty(cgInvoices)) {
                                // To add only CG Invoices to the list. This is done specific for cash control and payment
                                // applications.
                                for (ContractsGrantsInvoiceDocument cgInv : cgInvoices) {
                                    try {
                                        String docType = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(cgInv.getDocumentNumber()).getDocumentHeader().getWorkflowDocument().getDocumentType();
                                        if (docType.equalsIgnoreCase(ArConstants.CGIN_DOCUMENT_TYPE)) {
                                            newCGInvoices.add(cgInv);
                                        }
                                    }
                                    catch (WorkflowException e) {
                                        throw new RuntimeException("Caught WorkflowException trying to get document type name from Workflow", e);
                                    }
                                }
                                setInvoices(new ArrayList<CustomerInvoiceDocument>(newCGInvoices));


                            }
                            else {
                                LOG.error("There were no invoices retreived. Please refer to the log file " + ArConstants.BatchFileSystem.LOC_CREATION_PMT_APP_ERROR_OUTPUT_FILE + ArConstants.BatchFileSystem.EXTENSION + " for more details.");
                            }
                        }
                        else {
                            throw new IllegalArgumentException("The Invoice Document Type parameter passed in was null or blank.");
                        }

                        if (invoices != null && !invoices.isEmpty()) {
                            setSelectedInvoiceDocumentNumber(invoices.get(0).getDocumentNumber());
                        }
                        setupInvoiceWrappers(docId);
                    }

                }// Increasing the scope of the payment application document, to use it for further calculations.
            }
        }
    }

    /**
     * @param payAppDocNumber
     */
    protected void setupInvoiceWrappers(String payAppDocNumber) {
        if (StringUtils.isBlank(payAppDocNumber)) {
            throw new IllegalArgumentException("The payAppDocNumber parameter passed in was null or blank.");
        }

        // clear any existing
        invoiceApplications.clear();

        if (invoices == null || invoices.isEmpty()) {
            return;
        }

        for (CustomerInvoiceDocument invoice : invoices) {
            PaymentApplicationInvoiceApply invoiceApplication = new PaymentApplicationInvoiceApply(payAppDocNumber, invoice);
            addInvoiceApplication(invoiceApplication);
        }
    }

    /**
     * @return
     */
    public Map<String, PaymentApplicationInvoiceApply> getInvoiceApplicationsByDocumentNumber() {
        Map<String, PaymentApplicationInvoiceApply> m = new HashMap<String, PaymentApplicationInvoiceApply>();
        for (PaymentApplicationInvoiceApply i : invoiceApplications) {
            m.put(i.getDocumentNumber(), i);
        }
        return m;
    }

    /**
     * @return
     */
    public Integer getNextNonInvoicedLineNumber() {
        return nextNonInvoicedLineNumber;
    }

    /**
     * @param nextNonInvoicedLineNumber
     */
    public void setNextNonInvoicedLineNumber(Integer nextNonInvoicedLineNumber) {
        this.nextNonInvoicedLineNumber = nextNonInvoicedLineNumber;
    }

    /**
     * @return
     */
    public KualiDecimal getNonArTotal() {
        return null == getPaymentApplicationDocument() ? KualiDecimal.ZERO : getPaymentApplicationDocument().getNonArTotal();
    }

    /**
     * This method gets the payment application document
     * 
     * @return the payment application document
     */
    public PaymentApplicationDocument getPaymentApplicationDocument() {
        return (PaymentApplicationDocument) getDocument();
    }

    /**
     * For a given invoiceDocNumber and invoiceItemNumber, this method will return any paidApplieds that match those two fields, if
     * any exists. Otherwise it will return null.
     */
    public InvoicePaidApplied getPaidAppliedForInvoiceDetail(String invoiceDocNumber, Integer invoiceItemNumber) {
        if (StringUtils.isBlank(invoiceDocNumber)) {
            throw new IllegalArgumentException("The parameter [invoiceDocNumber] passed in was blank or null.");
        }
        if (invoiceItemNumber == null || invoiceItemNumber.intValue() < 1) {
            throw new IllegalArgumentException("The parameter [invoiceItemNumber] passed in was blank, zero or negative.");
        }
        PaymentApplicationDocument payAppDoc = getPaymentApplicationDocument();
        List<InvoicePaidApplied> paidApplieds = payAppDoc.getInvoicePaidApplieds();
        for (InvoicePaidApplied paidApplied : paidApplieds) {
            if (invoiceDocNumber.equalsIgnoreCase(paidApplied.getFinancialDocumentReferenceInvoiceNumber())) {
                if (invoiceItemNumber.equals(paidApplied.getInvoiceItemNumber())) {
                    return paidApplied;
                }
            }
        }
        return null;
    }

    public String getSelectedInvoiceDocumentNumber() {
        return selectedInvoiceDocumentNumber;
    }

    public void setSelectedInvoiceDocumentNumber(String selectedInvoiceDocumentNumber) {
        this.selectedInvoiceDocumentNumber = selectedInvoiceDocumentNumber;
    }

    public KualiDecimal getUnappliedCustomerAmount() {
        return unappliedCustomerAmount;
    }

    public void setUnappliedCustomerAmount(KualiDecimal unappliedCustomerAmount) {
        this.unappliedCustomerAmount = unappliedCustomerAmount;
    }

    /**
     * @return
     */
    public List<PaymentApplicationInvoiceDetailApply> getSelectedInvoiceDetailApplications() {
        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication();
        List<PaymentApplicationInvoiceDetailApply> detailApplications = null;
        if (ObjectUtils.isNotNull(invoiceApplication)) {
            detailApplications = invoiceApplication.getDetailApplications();
            if (null == detailApplications) {
                detailApplications = new ArrayList<PaymentApplicationInvoiceDetailApply>();
            }
        }
        return detailApplications;
    }

    /**
     * @return
     */
    public List<PaymentApplicationInvoiceApply> getNonSelectedInvoiceApplications() {
        String selectedInvoiceNumber = getSelectedInvoiceApplication().getDocumentNumber();

        List<PaymentApplicationInvoiceApply> nonSelectedInvoiceApplications = new ArrayList<PaymentApplicationInvoiceApply>();
        for (PaymentApplicationInvoiceApply invoiceApplication : invoiceApplications) {
            if (!invoiceApplication.getDocumentNumber().equalsIgnoreCase(selectedInvoiceNumber)) {
                nonSelectedInvoiceApplications.add(invoiceApplication);
            }
        }
        return nonSelectedInvoiceApplications;
    }

    public List<PaymentApplicationInvoiceApply> getInvoiceApplications() {
        return invoiceApplications;
    }

    /**
     * @return
     */
    public PaymentApplicationInvoiceApply getSelectedInvoiceApplication() {
        String docNumber = getSelectedInvoiceDocumentNumber();
        if (ObjectUtils.isNotNull(docNumber)) {
            return getInvoiceApplicationsByDocumentNumber().get(docNumber);
        }
        else {
            List<PaymentApplicationInvoiceApply> i = invoiceApplications;
            if (i.isEmpty()) {
                return null;
            }
            else {
                return invoiceApplications.get(0);
            }
        }
    }

    public List<CustomerInvoiceDocument> getInvoices() {
        return invoices;
    }

    public void setInvoices(ArrayList<CustomerInvoiceDocument> invoices) {
        this.invoices = invoices;
    }

    public String getEnteredInvoiceDocumentNumber() {
        return enteredInvoiceDocumentNumber;
    }

    public void setEnteredInvoiceDocumentNumber(String enteredInvoiceDocumentNumber) {
        this.enteredInvoiceDocumentNumber = enteredInvoiceDocumentNumber;
    }

    /**
     * This special casing for negative applieds is a display issue. We basically dont want to ever display that they applied a
     * negative amount, even while they may have an unsaved document with negative applications that are failing validations.
     * 
     * @return
     */
    public KualiDecimal getTotalApplied() {
        KualiDecimal totalApplied = getPaymentApplicationDocument().getTotalApplied();
        if (totalApplied.isPositive()) {
            return totalApplied;
        }
        else {
            return KualiDecimal.ZERO;
        }
    }

    public KualiDecimal getUnallocatedBalance() {
        return getTotalFromControl().subtract(getTotalApplied());
    }

    /**
     * Returns the control total available for this document, whether its a cash-control style payapp, or a nonapplied style payapp.
     * 
     * @return
     */
    public KualiDecimal getTotalFromControl() {
        PaymentApplicationDocument payAppDoc = (PaymentApplicationDocument) getDocument();
        if (payAppDoc.hasCashControlDetail()) {
            return payAppDoc.getTotalFromControl();
        }
        else {
            return getNonAppliedControlAvailableUnappliedAmount();
        }
    }

    /**
     * This method retrieves a specific customer invoice detail from the list, by array index
     * 
     * @param index the index of the customer invoice detail to retrieve
     * @return a CustomerInvoiceDetail
     */
    public PaymentApplicationInvoiceDetailApply getInvoiceDetailApplication(int index) {
        List<PaymentApplicationInvoiceDetailApply> details = getSelectedInvoiceDetailApplications();
        return details.get(index);
    }

    /**
     * This method retrieves a specific customer invoice from the list, by array index
     * 
     * @param index the index of the customer invoice to retrieve
     * @return a CustomerInvoiceDocument
     */
    public PaymentApplicationInvoiceApply getInvoiceApplication(int index) {
        return (PaymentApplicationInvoiceApply) invoiceApplications.get(index);
    }

    @SuppressWarnings("unchecked")
    public void setInvoiceDetailApplication(int key, PaymentApplicationInvoiceDetailApply value) {
        getSelectedInvoiceDetailApplications().set(key, value);
    }

    /**
     * @return
     */
    public KualiDecimal getSelectedInvoiceBalance() {
        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication();
        return invoiceApplication.getOpenAmount();
    }

    /**
     * @return
     */
    public KualiDecimal getSelectedInvoiceTotalAmount() {
        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication();
        return invoiceApplication.getInvoice().getSourceTotal();
    }

    /**
     * @return
     */
    public KualiDecimal getAmountAppliedDirectlyToInvoice() {
        PaymentApplicationInvoiceApply invoiceApplicationToFind = getSelectedInvoiceApplication();
        KualiDecimal amount = new KualiDecimal(0);
        for (PaymentApplicationInvoiceApply invoiceApplication : invoiceApplications) {
            if (invoiceApplicationToFind.getDocumentNumber().equalsIgnoreCase(invoiceApplication.getDocumentNumber())) {
                amount = amount.add(invoiceApplication.getAmountToApply());
            }
        }
        return amount;
    }

    /**
     * This method gets the previous invoice document number
     * 
     * @return the previous invoice document number
     */
    public String getPreviousInvoiceDocumentNumber() {
        CustomerInvoiceDocument _previousInvoiceDocument = null;

        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication();
        CustomerInvoiceDocument selectedInvoiceDocument = invoiceApplication == null ? null : invoiceApplication.getInvoice();
        if (null == selectedInvoiceDocument || 2 > invoices.size()) {
            _previousInvoiceDocument = null;
        }
        else {
            Iterator<CustomerInvoiceDocument> iterator = invoices.iterator();
            CustomerInvoiceDocument previousInvoiceDocument = iterator.next();
            String selectedInvoiceDocumentNumber = selectedInvoiceDocument.getDocumentNumber();
            if (null != selectedInvoiceDocumentNumber && selectedInvoiceDocumentNumber.equals(previousInvoiceDocument.getDocumentNumber())) {
                _previousInvoiceDocument = null;
            }
            else {
                while (iterator.hasNext()) {
                    CustomerInvoiceDocument currentInvoiceDocument = iterator.next();
                    String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                    if (null != currentInvoiceDocumentNumber && currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                        _previousInvoiceDocument = previousInvoiceDocument;
                    }
                    else {
                        previousInvoiceDocument = currentInvoiceDocument;
                    }
                }
            }
        }

        return null == _previousInvoiceDocument ? "" : _previousInvoiceDocument.getDocumentNumber();
    }

    /**
     * This method gets the next invoice document number
     * 
     * @return the next invoice document number
     */
    public String getNextInvoiceDocumentNumber() {
        CustomerInvoiceDocument _nextInvoiceDocument = null;

        PaymentApplicationInvoiceApply invoiceApplication = getSelectedInvoiceApplication();
        CustomerInvoiceDocument selectedInvoiceDocument = invoiceApplication == null ? null : invoiceApplication.getInvoice();
        if (null == selectedInvoiceDocument || 2 > invoices.size()) {
            _nextInvoiceDocument = null;
        }
        else {
            Iterator<CustomerInvoiceDocument> iterator = invoices.iterator();
            while (iterator.hasNext()) {
                CustomerInvoiceDocument currentInvoiceDocument = iterator.next();
                String currentInvoiceDocumentNumber = currentInvoiceDocument.getDocumentNumber();
                if (currentInvoiceDocumentNumber.equals(selectedInvoiceDocument.getDocumentNumber())) {
                    if (iterator.hasNext()) {
                        _nextInvoiceDocument = iterator.next();
                    }
                    else {
                        _nextInvoiceDocument = null;
                    }
                }
            }
        }

        return null == _nextInvoiceDocument ? "" : _nextInvoiceDocument.getDocumentNumber();
    }

    /**
     * This method gets the Cash Control document for the payment application document
     * 
     * @return the cash control document
     */
    public CashControlDocument getCashControlDocument() {
        return getPaymentApplicationDocument().getCashControlDocument();
    }

    public NonInvoiced getNonInvoicedAddLine() {
        return nonInvoicedAddLine;
    }

    public void setNonInvoicedAddLine(NonInvoiced nonInvoicedAddLine) {
        this.nonInvoicedAddLine = nonInvoicedAddLine;
    }

    /**
     * @return
     */
    public Integer getNonInvoicedAddLineItemNumber() {
        Integer number = new Integer(0);
        if (null != getPaymentApplicationDocument()) {
            Collection<NonInvoiced> items = getPaymentApplicationDocument().getNonInvoiceds();
            for (NonInvoiced item : items) {
                Integer i = item.getFinancialDocumentLineNumber();
                if (i > number) {
                    number = i;
                }
            }
        }
        return number + 1;
    }

    /**
     * @param documentNumber
     * @return
     */
    public PaymentApplicationInvoiceApply getInvoiceApplication(String documentNumber) {
        if (StringUtils.isBlank(documentNumber)) {
            throw new RuntimeException("The parameter passed in [documentNumber] was null or blank.");
        }
        for (PaymentApplicationInvoiceApply invoiceApplication : invoiceApplications) {
            if (documentNumber.equalsIgnoreCase(invoiceApplication.getDocumentNumber())) {
                return invoiceApplication;
            }
        }
        return null;
    }

    /**
     * @param invoiceApplicationToAdd
     */
    public void addInvoiceApplication(PaymentApplicationInvoiceApply invoiceApplicationToAdd) {
        if (invoiceApplicationToAdd == null) {
            throw new RuntimeException("The parameter passed in [invoiceApplicationToAdd] was null.");
        }
        for (int i = 0; i < invoiceApplications.size(); i++) {
            PaymentApplicationInvoiceApply invoiceApplication = invoiceApplications.get(i);
            if (invoiceApplicationToAdd.getDocumentNumber().equalsIgnoreCase(invoiceApplication.getDocumentNumber())) {
                invoiceApplications.set(i, invoiceApplicationToAdd);
            }
        }
        invoiceApplications.add(invoiceApplicationToAdd);
    }

    public String getSelectedCustomerNumber() {
        return selectedCustomerNumber;
    }

    public void setSelectedCustomerNumber(String selectedCustomerNumber) {
        this.selectedCustomerNumber = StringUtils.isBlank(selectedCustomerNumber) ? null : selectedCustomerNumber.toUpperCase();
    }

    public KualiDecimal getNonAppliedHoldingAmount() {
        return nonAppliedHoldingAmount;
    }

    public void setNonAppliedHoldingAmount(KualiDecimal nonAppliedHoldingAmount) {
        this.nonAppliedHoldingAmount = nonAppliedHoldingAmount;
    }

    public String getNonAppliedHoldingCustomerNumber() {
        return nonAppliedHoldingCustomerNumber;
    }

    public void setNonAppliedHoldingCustomerNumber(String nonAppliedHoldingCustomerNumber) {
        this.nonAppliedHoldingCustomerNumber = nonAppliedHoldingCustomerNumber;
    }

    public List<PaymentApplicationDocument> getNonAppliedControlDocs() {
        return nonAppliedControlDocs;
    }

    public void setNonAppliedControlDocs(List<PaymentApplicationDocument> nonAppliedControlDocs) {
        this.nonAppliedControlDocs = nonAppliedControlDocs;
    }

    /**
     * Returns the total amount of previously NonApplied funds available to apply to invoices and other applications on this
     * document.
     * 
     * @return
     */
    public KualiDecimal getNonAppliedControlAvailableUnappliedAmount() {
        KualiDecimal amount = KualiDecimal.ZERO;
        for (NonAppliedHolding nonAppliedHolding : nonAppliedControlHoldings) {
            amount = amount.add(nonAppliedHolding.getAvailableUnappliedAmount());
        }
        return amount;
    }

    public List<NonAppliedHolding> getNonAppliedControlHoldings() {
        return nonAppliedControlHoldings;
    }

    public void setNonAppliedControlHoldings(List<NonAppliedHolding> nonAppliedControlHoldings) {
        this.nonAppliedControlHoldings = nonAppliedControlHoldings;
    }

    /**
     * Used for when the doc is final, to show the control docs section.
     * 
     * @return
     */
    public Map<String, KualiDecimal> getDistributionsFromControlDocs() {
        if (distributionsFromControlDocs == null || distributionsFromControlDocs.isEmpty()) {
            distributionsFromControlDocs = getPaymentApplicationDocument().getDistributionsFromControlDocuments();
        }
        return distributionsFromControlDocs;
    }

    /**
     * Used for when the doc is live, to show the control docs section.
     * 
     * @return
     */
    public Map<String, KualiDecimal> getNonAppliedControlAllocations() {
        if (nonAppliedControlAllocations == null || nonAppliedControlAllocations.isEmpty()) {
            nonAppliedControlAllocations = getPaymentApplicationDocument().allocateFundsFromUnappliedControls(nonAppliedControlHoldings, getTotalApplied());
        }
        return nonAppliedControlAllocations;
    }

    public void setNonAppliedControlAllocations(Map<String, KualiDecimal> nonAppliedControlAllocations) {
        this.nonAppliedControlAllocations = nonAppliedControlAllocations;
    }

    /**
     * @param documentNumber
     * @return
     */
    public KualiDecimal getNonAppliedControlAllocation(String documentNumber) {
        if (!getNonAppliedControlAllocations().containsKey(documentNumber)) {
            return KualiDecimal.ZERO;
        }
        return getNonAppliedControlAllocations().get(documentNumber);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase#populateFalseCheckboxes(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void populateFalseCheckboxes(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap.get("checkboxToReset") != null) {
            final String[] checkboxesToReset = request.getParameterValues("checkboxToReset");
            if (checkboxesToReset != null && checkboxesToReset.length > 0) {
                for (int i = 0; i < checkboxesToReset.length; i++) {
                    String propertyName = (String) checkboxesToReset[i];
                    if (!StringUtils.isBlank(propertyName) && parameterMap.get(propertyName) == null) {
                        try {
                            populateForProperty(propertyName, KimConstants.KIM_ATTRIBUTE_BOOLEAN_FALSE_STR_VALUE_DISPLAY, parameterMap);
                        }
                        catch (RuntimeException ex) {

                        }
                    }
                    else if (!StringUtils.isBlank(propertyName) && parameterMap.get(propertyName) != null && parameterMap.get(propertyName).length >= 1 && parameterMap.get(propertyName)[0].equalsIgnoreCase("on")) {
                        try {
                            populateForProperty(propertyName, KimConstants.KIM_ATTRIBUTE_BOOLEAN_TRUE_STR_VALUE_DISPLAY, parameterMap);
                        }
                        catch (RuntimeException ex) {

                        }
                    }
                }
            }
        }
    }

}
