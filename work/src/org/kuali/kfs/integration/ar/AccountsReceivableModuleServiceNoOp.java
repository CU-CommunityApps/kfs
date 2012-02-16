/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.integration.ar;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class AccountsReceivableModuleServiceNoOp implements AccountsReceivableModuleService {

    private Logger LOG = Logger.getLogger(getClass()); 

    public ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy() {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return new ElectronicPaymentClaimingDocumentGenerationStrategy() {
            public boolean userMayUseToClaim(Person claimingUser) {
                return false;
            }
            public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
                return null;
            }
            public String getClaimingDocumentWorkflowDocumentType() {
                return null;
            }
            public String getDocumentLabel() {
                return "AR NoOp Module Service";
            }
            public boolean isDocumentReferenceValid(String referenceDocumentNumber) {
                return false;
            }
            
        };
    }

    public void addNoteToRelatedPaymentRequestDocument(String paymentRequestDocumentNumber, String noteText) {
        // do nothing
    }

    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber) {
        return null;
    }

    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues) {
        return null;
    }

    public AccountsReceivableCustomer findCustomer(String customerNumber) {
        return null;
    }

    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues) {
        return null;
    }

    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer) {
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenCustomerInvoice(java.lang.String)
     */
    public AccountReceivableCustomerInvoice getOpenCustomerInvoice(String customerInvoiceDocumentNumber) {   
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );

        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(CustomerInvoiceDocument.class, customerInvoiceDocumentNumber);
    }
    
    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getCustomerInvoiceOpenAmount(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Map<String, KualiDecimal> getCustomerInvoiceOpenAmount(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );

        Map<String, KualiDecimal> customerInvoiceOpenAmountMap = new HashMap<String, KualiDecimal>();       
        
        Collection<? extends AccountReceivableCustomerInvoice> customerInvoiceDocuments = this.getOpenCustomerInvoices(customerTypeCodes, customerInvoiceAge, invoiceBillingDateFrom);
        if(ObjectUtils.isNull(customerInvoiceDocuments)){
            return customerInvoiceOpenAmountMap;
        }
        
        for(AccountReceivableCustomerInvoice invoiceDocument : customerInvoiceDocuments){
            KualiDecimal openAmount = invoiceDocument.getOpenAmount();
            
            if(ObjectUtils.isNotNull(openAmount) && openAmount.isPositive()){
                customerInvoiceOpenAmountMap.put(invoiceDocument.getDocumentNumber(), openAmount);
            }
        }
        
        return customerInvoiceOpenAmountMap;
    }
    
    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenCustomerInvoices(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Collection<? extends AccountReceivableCustomerInvoice> getOpenCustomerInvoices(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );

        CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        
        return customerInvoiceDocumentService.getAllAgingInvoiceDocumentsByCustomerTypes(customerTypeCodes, customerInvoiceAge, invoiceBillingDateFrom);
    }

    @Override
    public AccountsReceivableCustomer createCustomer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableCustomerAddress createCustomerAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNextCustomerNumber(AccountsReceivableCustomer newCustomer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveCustomer(AccountsReceivableCustomer customer) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<AccountsReceivableCustomerType> findByCustomerTypeDescription(String customerTypeDescription) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrganizationOptionsByPrimaryKey(Map<String, String> criteria) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveCustomerInvoiceDocument(AccountReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Document blanketApproveCustomerInvoiceDocument(AccountReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsRecievableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsRecievableDocumentHeader createAccountsReceivableDocumentHeader() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ChartOrgHolder getPrimaryOrganization() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isUsingReceivableFAU() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountReceivableCustomerInvoice document) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void recalculateCustomerInvoiceDetail(AccountReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountReceivableCustomerInvoice customerInvoiceDocument) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountReceivableCustomerInvoice invoice) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<AccountReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumber(String customerNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableNonInvoiced createNonInvoiced() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableInvoicePaidApplied createInvoicePaidApplied() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsRecievableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivablePaymentApplicationDocument createPaymentApplicationDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableCashControlDetail createCashControlDetail() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountsReceivableCashControlDocument createCashControlDocument() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document blanketApprovePaymentApplicationDocument(AccountsReceivablePaymentApplicationDocument paymentApplicationDocument, String travelDocumentIdentifier) throws WorkflowException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AccountReceivableCustomerInvoice createCustomerInvoiceDocument() {
        // TODO Auto-generated method stub
        return null;
    }   
}
