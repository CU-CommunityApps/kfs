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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.NonAppliedHoldingService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentApplicationDocumentServiceImpl implements PaymentApplicationDocumentService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocumentServiceImpl.class);;

    private BusinessObjectService businessObjectService;
    private NonAppliedHoldingService nonAppliedHoldingService;

    public CashControlDocument getCashControlDocumentForPaymentApplicationDocument(PaymentApplicationDocument document) {
        return getCashControlDocumentForPaymentApplicationDocumentNumber(document.getDocumentNumber());
    }

    public PaymentApplicationDocument createPaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) {
        PaymentApplicationDocument applicationDocument = null;
        try {
            applicationDocument =
                (PaymentApplicationDocument) KNSServiceLocator.getDocumentService().getNewDocument(PaymentApplicationDocument.class);
        } catch(WorkflowException we) {
            LOG.error("Failed to create payment application document to match invoice.", we);
            return null;
        }

        // KULAR-290
        // This code is basically copied from PaymentApplicationDocumentAction.createDocument
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(applicationDocument.getDocumentNumber());
        applicationDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        
        // This code is needed for the code below but isn't copied from anywhere.
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        Integer universityFiscalYear = universityDateService.getCurrentFiscalYear();
        String universityFiscalPeriodCode = universityDateService.getCurrentUniversityDate().getAccountingPeriod().getUniversityFiscalPeriodCode();
        
        // This code is basically copied from PaymentApplicationDocumentAction.quickApply
        for(CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts()) {
            updateCustomerInvoiceDetailInfo(applicationDocument, customerInvoiceDetail);
            Integer invoicePaidAppliedItemNbr = applicationDocument.getAppliedPayments().size() + 1;
            InvoicePaidApplied invoicePaidApplied = 
                createInvoicePaidAppliedForInvoiceDetail(
                    customerInvoiceDetail, applicationDocument.getDocumentNumber(), universityFiscalYear, 
                    universityFiscalPeriodCode, customerInvoiceDetail.getOpenAmount(), invoicePaidAppliedItemNbr);
            // if there was not another invoice paid applied already created for the current detail then invoicePaidApplied will not be null
            if (invoicePaidApplied != null) {
                // add it to the payment application document list of applied payments
                applicationDocument.getAppliedPayments().add(invoicePaidApplied);
                customerInvoiceDetail.setAmountToBeApplied(customerInvoiceDetail.getAmount());
            }
            updateCustomerInvoiceDetailInfo(applicationDocument, customerInvoiceDetail);
        }
        
        return applicationDocument;
    }
    
    public PaymentApplicationDocument createAndSavePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PaymentApplicationDocument applicationDocument = createPaymentApplicationToMatchInvoice(customerInvoiceDocument);
        documentService.saveDocument(applicationDocument);
        return applicationDocument;
    }
    
    public PaymentApplicationDocument createSaveAndApprovePaymentApplicationToMatchInvoice(CustomerInvoiceDocument customerInvoiceDocument, String approvalAnnotation, List workflowNotificationRecipients) throws WorkflowException {
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        PaymentApplicationDocument applicationDocument = createAndSavePaymentApplicationToMatchInvoice(customerInvoiceDocument);
        documentService.approveDocument(applicationDocument,approvalAnnotation,workflowNotificationRecipients);
        return applicationDocument;
    }
    
    public KualiDecimal getTotalAppliedAmountForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;
        Collection<InvoicePaidApplied> invoicePaidApplieds = document.getAppliedPayments();

        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            total = total.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }

        return total;
    }

    public KualiDecimal getTotalToBeAppliedForPaymentApplicationDocument(String paymentApplicationDocumentNumber) {
        KualiDecimal total = KualiDecimal.ZERO;

        // TODO Auto-generated method stub
        // for test purpose only
        total = total.add(new KualiDecimal(1100));

        return total;
    }

    public KualiDecimal getTotalUnappliedFundsForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal total = KualiDecimal.ZERO;

        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();
        Collection<NonAppliedHolding> nonAppliedHoldings = nonAppliedHoldingService.getNonAppliedHoldingsForCustomer(customerNumber);

        for (NonAppliedHolding nonAppliedHolding : nonAppliedHoldings) {
            total = total.add(nonAppliedHolding.getFinancialDocumentLineAmount());
        }

        return total;
    }

    public KualiDecimal getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(PaymentApplicationDocument document) {
        KualiDecimal totalUnapplied = getTotalUnappliedFundsForPaymentApplicationDocument(document);
        KualiDecimal totalApplied = getTotalAppliedAmountForPaymentApplicationDocument(document);
        return totalUnapplied.subtract(totalApplied);
    }

    @SuppressWarnings("unchecked")
    public CashControlDocument getCashControlDocumentForPaymentApplicationDocumentNumber(String paymentApplicationDocumentNumber) {
        if (null == paymentApplicationDocumentNumber) {
            return null;
        }
        CashControlDocument document = null;
        Map criteria = new HashMap();
        criteria.put("referenceFinancialDocumentNumber", paymentApplicationDocumentNumber);

        Collection matches = businessObjectService.findMatching(CashControlDetail.class, criteria);
        if (matches.size() > 0) {
            CashControlDetail detail = (CashControlDetail) matches.iterator().next();
            Map ccdocCriteria = new HashMap();
            ccdocCriteria.put("documentNumber", detail.getDocumentNumber());
            Object ccdoc = businessObjectService.findByPrimaryKey(CashControlDocument.class, ccdocCriteria);
            document = (CashControlDocument) ccdoc;
        }
        return document;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService#createInvoicePaidAppliedForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail,
     *      org.kuali.rice.kns.util.KualiDecimal)
     */
    public InvoicePaidApplied createInvoicePaidAppliedForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, String applicationDocNbr, Integer universityFiscalYear, String universityFiscalPeriodCode, KualiDecimal amount, Integer invoicePaidAppliedItemNbr) {

        Collection<InvoicePaidApplied> invoicePaidApplieds = customerInvoiceDetail.getInvoicePaidApplieds();
        InvoicePaidApplied invoicePaidApplied = null;
        boolean found = false;

        if (invoicePaidApplieds != null && invoicePaidApplieds.size() > 0) {
            for (InvoicePaidApplied pdApp : invoicePaidApplieds) {
                if (pdApp.getDocumentNumber().equals(applicationDocNbr) && pdApp.getFinancialDocumentReferenceInvoiceNumber().equals(customerInvoiceDetail.getDocumentNumber()) && pdApp.getInvoiceItemNumber().equals(customerInvoiceDetail.getSequenceNumber())) {
                    pdApp.setInvoiceItemAppliedAmount(amount);
                    found = true;
                    break;
                }
            }
        }

        if (!found) {

            invoicePaidApplied = new InvoicePaidApplied();

            invoicePaidApplied.setDocumentNumber(applicationDocNbr);
            
            invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDetail.getDocumentNumber());
            invoicePaidApplied.setInvoiceItemNumber(customerInvoiceDetail.getSequenceNumber());
            invoicePaidApplied.setInvoiceItemAppliedAmount(amount);
            invoicePaidApplied.setUniversityFiscalYear(universityFiscalYear);
            invoicePaidApplied.setUniversityFiscalPeriodCode(universityFiscalPeriodCode);
            invoicePaidApplied.setPaidAppliedItemNumber(invoicePaidAppliedItemNbr);

        }

        return invoicePaidApplied;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public NonAppliedHoldingService getNonAppliedHoldingService() {
        return nonAppliedHoldingService;
    }

    public void setNonAppliedHoldingService(NonAppliedHoldingService nonAppliedHoldingService) {
        this.nonAppliedHoldingService = nonAppliedHoldingService;
    }
    
    /**
     * This method update customer invoice detail information.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    public void updateCustomerInvoiceDetailInfo(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        // update information for customer invoice detail: update the list of invoice paid applieds, compute applied amount and balance(should be done in this order as
        // balance calculation depends on applied amount )
        updateCustomerInvoiceDetailAppliedPayments(applicationDocument, customerInvoiceDetail);
        updateCustomerInvoiceDetailAppliedAmount(customerInvoiceDetail);
        updateCustomerInvoiceDetailBalance(customerInvoiceDetail);
        updateAmountAppliedOnDetail(applicationDocument, customerInvoiceDetail);
    }

    /**
     * This method updates the applied amount for the given customer invoice detail.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    private void updateCustomerInvoiceDetailAppliedAmount(CustomerInvoiceDetail customerInvoiceDetail) {
        ArrayList<InvoicePaidApplied> invoicePaidApplieds = new ArrayList(customerInvoiceDetail.getInvoicePaidApplieds());
        KualiDecimal appliedAmount = customerInvoiceDetail.getAppliedAmount();

        // TODO we might want to compute this based on the applied payments on this doc...
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }
        customerInvoiceDetail.setAppliedAmount(appliedAmount);
    }

    /**
     * This method updates the balance for the given customer invoice detail.
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    public void updateCustomerInvoiceDetailBalance(CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal totalAmount = customerInvoiceDetail.getAmount();
        KualiDecimal appliedAmount = customerInvoiceDetail.getAppliedAmount();
        KualiDecimal balance = totalAmount.subtract(appliedAmount);
        customerInvoiceDetail.setBalance(balance);
    }

    /**
     * This method will update the list of the applied payments for this customer invoice detail taking into account the applied
     * payments on the form that are not yet saved in the db
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     */
    public void updateCustomerInvoiceDetailAppliedPayments(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        String applicationDocNumber = applicationDocument.getDocumentNumber();

        // get the invoice paid applieds for this detail that where saved in the db for this app doc
        InvoicePaidAppliedService invoicePaidAppliedService = SpringContext.getBean(InvoicePaidAppliedService.class);
        Collection<InvoicePaidApplied> detailInvPaidApplieds = invoicePaidAppliedService.getInvoicePaidAppliedsForCustomerInvoiceDetail(customerInvoiceDetail, applicationDocNumber);

        Collection<InvoicePaidApplied> invPaidAppliedsFormForThisDetail = getInvoicePaidAppliedsForDetail(applicationDocument.getAppliedPayments(), customerInvoiceDetail);

        Collection<InvoicePaidApplied> invPaidAppliedsToBeAdded = new ArrayList<InvoicePaidApplied>();

        // go over the invoice paid applieds from the form for this detail and check if they are in the detail inv paid applieds list; if not add the in the invPaidAppliedsToBeAdded collection
        for (InvoicePaidApplied invoicePaidApplied2 : invPaidAppliedsFormForThisDetail) {
            boolean found = false;
            for (InvoicePaidApplied invoicePaidApplied1 : detailInvPaidApplieds) {

                String invoiceNumber1 = invoicePaidApplied1.getFinancialDocumentReferenceInvoiceNumber();
                String invoiceNumber2 = invoicePaidApplied2.getFinancialDocumentReferenceInvoiceNumber();
                Integer detailNumber1 = invoicePaidApplied1.getInvoiceItemNumber();
                Integer detailNumber2 = invoicePaidApplied2.getInvoiceItemNumber();
                Integer paidAppliedNumber1 = invoicePaidApplied1.getPaidAppliedItemNumber();
                Integer paidAppliedNumber2 = invoicePaidApplied2.getPaidAppliedItemNumber();

                if (invoiceNumber1.equals(invoiceNumber2) && detailNumber1.equals(detailNumber2) && paidAppliedNumber1.equals(paidAppliedNumber2)) {
                    found = true;
                    break;
                }

            }
            if (!found) {
                invPaidAppliedsToBeAdded.add(invoicePaidApplied2);
            }
        }

        detailInvPaidApplieds.addAll(invPaidAppliedsToBeAdded);

        customerInvoiceDetail.setInvoicePaidApplieds(detailInvPaidApplieds);
    }
    
    /**
     * This method gets the invoice paid applieds from the form for the given invoice detail
     * 
     * @param applicationDocumentForm
     * @param customerInvoiceDetail
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForDetail(Collection<InvoicePaidApplied> invPaidAppliedsForm, CustomerInvoiceDetail customerInvoiceDetail) {
        // get the invoice paid applieds from the form
        Collection<InvoicePaidApplied> invPaidAppliedsFormForThisDetail = new ArrayList<InvoicePaidApplied>();

        // get the invoice paid applieds from the form for this detail
        for (InvoicePaidApplied invoicePaidApplied : invPaidAppliedsForm) {
            if (invoicePaidApplied.getFinancialDocumentReferenceInvoiceNumber().equals(customerInvoiceDetail.getDocumentNumber()) && invoicePaidApplied.getInvoiceItemNumber().equals(customerInvoiceDetail.getSequenceNumber())) {
                invPaidAppliedsFormForThisDetail.add(invoicePaidApplied);
            }
        }
        return invPaidAppliedsFormForThisDetail;
    }

    /**
     * This method updates amount to be applied on invoice detail
     * @param applicationDocumentForm
     */
    public void updateAmountAppliedOnDetail(PaymentApplicationDocument applicationDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        Collection<InvoicePaidApplied> paidAppliedsFromDocument = applicationDocument.getAppliedPayments();
        Collection<InvoicePaidApplied> invoicePaidApplieds = getInvoicePaidAppliedsForDetail(paidAppliedsFromDocument, customerInvoiceDetail);
        for (InvoicePaidApplied invoicePaidApplied : invoicePaidApplieds) {
            customerInvoiceDetail.setAmountToBeApplied(invoicePaidApplied.getInvoiceItemAppliedAmount());
            //there should be actualy only one paid applied per detail
            break;
        }
    }

}
