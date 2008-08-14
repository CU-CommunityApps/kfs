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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class provides services related to the customer invoice document
 */
public interface CustomerInvoiceDetailService {
    
    /**
     * This method returns a customer invoice detail for use on the CustomerInvoiceDocumentAction.  If corresponding
     * organization accounting default exists for billing chart and org, then the customer invoice detail is defaulted
     * using the organization accounting default values.
     * 
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefault(Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode);
    
    /**
     * This method returns a customer invoice detail for current user and current fiscal year for use on the CustomerInvoiceDocumentAction.  If corresponding
     * organization accounting default exists for billing chart and org, then the customer invoice detail is defaulted
     * using the organization accounting default values.
     * 
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefaultForCurrentYear();
    
    
    public List<String> getCustomerInvoiceDocumentNumbersByAccountNumber(String accountNumber);
    
    /**
     * This method returns a customer invoice detail from a customer invoice item code for a current user
     * @param invoiceItemCode
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser( String invoiceItemCode );
    
    /**
     * This method...
     * @param invoiceItemCode
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode( String invoiceItemCode, String chartOfAccountsCode, String organizationCode);
    
    
    /**
     * This method returns a discount customer invoice detail based on a customer invoice detail, the chart of accounts code
     * @param customerInvoiceDetail
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetail( CustomerInvoiceDetail customerInvoiceDetail, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode );
    
    
    /**
     * This method returns a discount customer invoice detail for the current year
     * 
     * @param customerInvoiceDetail
     * @param chartOfAccountsCode
     * @param organizationCode
     * @return
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetailForCurrentYear( CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument );
 
    /**
     * This method returns a customer invoice detail for the current year
     * 
     * @param documentNumber
     * @param sequenceNumber
     * @return
     */    
    public CustomerInvoiceDetail getCustomerInvoiceDetail(String documentNumber,Integer sequenceNumber);
    
    /**
     * This method returns a customer invoice detail open item amount
     * 
     * @param documentNumber
     * @param invoiceItemCode
     * @return
     */  
    public KualiDecimal getOpenAmount(CustomerInvoiceDetail customerInvoiceDetail);
    
    /**
     * This method is used to recalculate a customer invoice detail based on updated values
     * @param customerInvoiceDetail
     */
    public void recalculateCustomerInvoiceDetail( CustomerInvoiceDocument document, CustomerInvoiceDetail customerInvoiceDetail );
    
    /**
     * This method is used to update account for corresponding discount line based on parent line's account
     * @param parentDiscountCustomerInvoiceDetail
     */
    public void updateAccountsForCorrespondingDiscount( CustomerInvoiceDetail parentDiscountCustomerInvoiceDetail );
    
    /**
     * This method is used update the accounts receivable object code if receivable options 1 or 2 are selected.
     * @param customerInvoiceDetail
     */
    public void updateAccountsReceivableObjectCode( CustomerInvoiceDetail customerInvoiceDetail );
    
    /**
     * This method returns the correct accounts receivable object code based on a receivable parameter.
     * @return
     */
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter( CustomerInvoiceDetail customerInvoiceDetail);
    
    /**
     * This method is used make sure the amounts are calculated correctly and the correct AR object code is in place
     * @param customerInvoiceDetail
     */
    public void prepareCustomerInvoiceDetailForAdd( CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument );

    /**
     * @param customerInvoiceDocument
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(CustomerInvoiceDocument customerInvoiceDocument);
    
    /**
     * @param customerInvoiceDocumentNumber
     * @return
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(String customerInvoiceDocumentNumber);
    

    /**
     * Calculate the total amount applied to this CustomerInvoiceDetail
     * 
     * @param invoiceDetail
     * @return
     */
    public KualiDecimal getAppliedAmountForInvoiceDetail(CustomerInvoiceDetail invoiceDetail);

    /**
     * @param customerInvoiceDocumentNumber
     * @param sequenceNumber
     * @return
     */
    public KualiDecimal getAppliedAmountForInvoiceDetail(String customerInvoiceDocumentNumber, Integer sequenceNumber);

    /**
     * @param customerInvoiceDetail
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail);

    /**
     * @param documentNumber
     * @param sequenceNumber
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoiceDetail(String documentNumber, Integer sequenceNumber);
    
    
    /**
     * This method returns a collection of invoice paid applieds that were applied from a specific document
     * 
     * @param documentNumber
     * @return
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsFromSpecificDocument(String documentNumber, String referenceCustomerInvoiceDocumentNumber);
    
    
    /**
     * This method returns the total applied amount from a specific document to a particular invoice
     * 
     * @param documentNumber
     * @return
     */
    public KualiDecimal getAppliedAmountFromSpecificDocument(String documentNumber, String referenceCustomerInvoiceDocumentNumber);
}
