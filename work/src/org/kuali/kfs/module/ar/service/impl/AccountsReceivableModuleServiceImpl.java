/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.ar.AccountReceivableCustomerInvoice;
import org.kuali.kfs.integration.ar.AccountsReceivableCashControlDetail;
import org.kuali.kfs.integration.ar.AccountsReceivableCashControlDocument;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerInvoiceDetail;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.kfs.integration.ar.AccountsReceivableInvoicePaidApplied;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.integration.ar.AccountsReceivableNonInvoiced;
import org.kuali.kfs.integration.ar.AccountsReceivableOrganizationOptions;
import org.kuali.kfs.integration.ar.AccountsReceivablePaymentApplicationDocument;
import org.kuali.kfs.integration.ar.AccountsReceivableSystemInformation;
import org.kuali.kfs.integration.ar.AccountsRecievableCustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.integration.ar.AccountsRecievableDocumentHeader;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants.CustomerTypeFields;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.module.ar.businessobject.CustomerType;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.module.ar.document.service.impl.ReceivableAccountingLineService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * The KFS AR module implementation of the AccountsReceivableModuleService
 */
public class AccountsReceivableModuleServiceImpl implements AccountsReceivableModuleService {
    protected static final String CASH_CONTROL_ELECTRONIC_PAYMENT_CLAIMING_DOCUMENT_GENERATION_STRATEGY_BEAN_NAME = "cashControlElectronicPaymentClaimingDocumentHelper";

    protected Lookupable customerLookupable;

    /**
     * @see org.kuali.kfs.integration.service.AccountsReceivableModuleService#getAccountsReceivablePaymentClaimingStrategy()
     */
    public ElectronicPaymentClaimingDocumentGenerationStrategy getAccountsReceivablePaymentClaimingStrategy() {
        return SpringContext.getBean(ElectronicPaymentClaimingDocumentGenerationStrategy.class, AccountsReceivableModuleServiceImpl.CASH_CONTROL_ELECTRONIC_PAYMENT_CLAIMING_DOCUMENT_GENERATION_STRATEGY_BEAN_NAME);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#addNoteToPaymentRequestDocument(java.lang.String,
     *      java.lang.String)
     */
    public void addNoteToRelatedPaymentRequestDocument(String relatedDocumentNumber, String noteText) {
        SpringContext.getBean(PaymentApplicationDocumentService.class).addNoteToRelatedPaymentRequestDocument(relatedDocumentNumber, noteText);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getProcessingOrganizationForRelatedPaymentRequestDocument(java.lang.String)
     */
    public Organization getProcessingOrganizationForRelatedPaymentRequestDocument(String relatedDocumentNumber) {
        return SpringContext.getBean(PaymentApplicationDocumentService.class).getProcessingOrganizationForRelatedPaymentRequestDocument(relatedDocumentNumber);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#searchForCustomers(java.util.Map)
     */
    public Collection<AccountsReceivableCustomer> searchForCustomers(Map<String, String> fieldValues) {
        customerLookupable.setBusinessObjectClass(Customer.class);
        List<BusinessObject> results = customerLookupable.getSearchResults(fieldValues);

        Collection<AccountsReceivableCustomer> customers = new ArrayList<AccountsReceivableCustomer>();
        for (BusinessObject result : results) {
            customers.add((AccountsReceivableCustomer) result);
        }

        return customers;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findCustomer(java.lang.String)
     */
    public AccountsReceivableCustomer findCustomer(String customerNumber) {
    	Map<String, Object> primaryKey = new HashMap<String, Object>();
    	primaryKey.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
        
        return getKualiModuleService().getResponsibleModuleService(Customer.class).getExternalizableBusinessObject(Customer.class, primaryKey);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#searchForCustomerAddresses(java.util.Map)
     */
    public Collection<AccountsReceivableCustomerAddress> searchForCustomerAddresses(Map<String, String> fieldValues) {
    	return SpringContext.getBean(BusinessObjectService.class).findMatching(CustomerAddress.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#findCustomerAddress(java.lang.String)
     */
    public AccountsReceivableCustomerAddress findCustomerAddress(String customerNumber, String customerAddressIdentifer) {
        Map<String, String> addressKey = new HashMap<String, String>();
        addressKey.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
        addressKey.put(KFSPropertyConstants.CUSTOMER_ADDRESS_IDENTIFIER, customerAddressIdentifer);

        return (AccountsReceivableCustomerAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CustomerAddress.class, addressKey);
    }

    public void setCustomerLookupable(Lookupable customerLookupable) {
        this.customerLookupable = customerLookupable;
    }

    /**
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getOpenCustomerInvoice(java.lang.String)
     */
    public AccountReceivableCustomerInvoice getOpenCustomerInvoice(String customerInvoiceDocumentNumber) {
        return SpringContext.getBean(BusinessObjectService.class).findBySinglePrimaryKey(CustomerInvoiceDocument.class, customerInvoiceDocumentNumber);
    }

    /**
<<<<<<< .working
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getCustomerInvoiceOpenAmount(java.util.List,
     *      java.lang.Integer)
=======
     * @see org.kuali.kfs.integration.ar.AccountsReceivableModuleService#getCustomerInvoiceOpenAmount(java.util.List, java.lang.Integer, java.sql.Date)
>>>>>>> .merge-right.r33267
     */
    @Override
    public Map<String, KualiDecimal> getCustomerInvoiceOpenAmount(List<String> customerTypeCodes, Integer customerInvoiceAge, Date invoiceBillingDateFrom) {
        Map<String, KualiDecimal> customerInvoiceOpenAmountMap = new HashMap<String, KualiDecimal>();

        Collection<? extends AccountReceivableCustomerInvoice> customerInvoiceDocuments = this.getOpenCustomerInvoices(customerTypeCodes, customerInvoiceAge, invoiceBillingDateFrom);
        if (ObjectUtils.isNull(customerInvoiceDocuments)) {
            return customerInvoiceOpenAmountMap;
        }

        for (AccountReceivableCustomerInvoice invoiceDocument : customerInvoiceDocuments) {
            KualiDecimal openAmount = invoiceDocument.getOpenAmount();

            if (ObjectUtils.isNotNull(openAmount) && openAmount.isPositive()) {
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
        CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);

        return customerInvoiceDocumentService.getAllAgingInvoiceDocumentsByCustomerTypes(customerTypeCodes, customerInvoiceAge, invoiceBillingDateFrom);
    }

    @Override
    public AccountsReceivableCustomer createCustomer() {
        return new Customer();
    }

    @Override
    public AccountsReceivableCustomerAddress createCustomerAddress() {
        return new CustomerAddress();
    }

    @Override
    public String getNextCustomerNumber(AccountsReceivableCustomer customer) {
        return SpringContext.getBean(CustomerService.class).getNextCustomerNumber((Customer) customer);
    }

    @Override
    public void saveCustomer(AccountsReceivableCustomer customer) {
        SpringContext.getBean(BusinessObjectService.class).save((Customer) customer);
    }

    @Override
    public List<AccountsReceivableCustomerType> findByCustomerTypeDescription(String customerTypeDescription) {
        Map<String, String> fieldMap = new HashMap<String, String>();
        fieldMap.put(CustomerTypeFields.CUSTOMER_TYPE_DESC, customerTypeDescription);
        List<AccountsReceivableCustomerType> customerTypes = (List<AccountsReceivableCustomerType>) SpringContext.getBean(BusinessObjectService.class).findMatching(CustomerType.class, fieldMap);

        return customerTypes;
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrgOptionsIfExists(String chartOfAccountsCode, String organizationCode) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chartOfAccountsCode);
        criteria.put("organizationCode", organizationCode);
        return (AccountsReceivableOrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);
    }

    @Override
    public AccountsReceivableOrganizationOptions getOrganizationOptionsByPrimaryKey(Map<String, String> criteria) {
        return (AccountsReceivableOrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);
    }

    @Override
    public void saveCustomerInvoiceDocument(AccountReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        SpringContext.getBean(DocumentService.class).saveDocument((CustomerInvoiceDocument) customerInvoiceDocument);
    }

    @Override
    public Document blanketApproveCustomerInvoiceDocument(AccountReceivableCustomerInvoice customerInvoiceDocument) throws WorkflowException {
        return SpringContext.getBean(DocumentService.class).blanketApproveDocument((CustomerInvoiceDocument) customerInvoiceDocument, null, null);
    }

    @Override
    public AccountsRecievableCustomerInvoiceRecurrenceDetails createCustomerInvoiceRecurrenceDetails() {
        return new CustomerInvoiceRecurrenceDetails();
    }

    @Override
    public AccountsRecievableDocumentHeader createAccountsReceivableDocumentHeader() {
        return new org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader();
    }

    @Override
    public ChartOrgHolder getPrimaryOrganization() {
        return SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
    }

    @Override
    public AccountsReceivableSystemInformation getSystemInformationByProcessingChartOrgAndFiscalYear(String chartOfAccountsCode, String organizationCode, Integer currentFiscalYear) {
        return SpringContext.getBean(SystemInformationService.class).getByProcessingChartOrgAndFiscalYear(chartOfAccountsCode, organizationCode, currentFiscalYear);
    }

    @Override
    public boolean isUsingReceivableFAU() {
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        return receivableOffsetOption != null ? ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption) : false;
    }

    @Override
    public void setReceivableAccountingLineForCustomerInvoiceDocument(AccountReceivableCustomerInvoice document) {
        SpringContext.getBean(ReceivableAccountingLineService.class).setReceivableAccountingLineForCustomerInvoiceDocument((CustomerInvoiceDocument) document);
    }

    @Override
    public AccountsReceivableCustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String processingChartCode, String processingOrgCode) {
        return SpringContext.getBean(CustomerInvoiceDetailService.class).getCustomerInvoiceDetailFromCustomerInvoiceItemCode(invoiceItemCode, processingChartCode, processingOrgCode);
    }

    @Override
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(AccountsReceivableCustomerInvoiceDetail customerInvoiceDetail) {
        return SpringContext.getBean(CustomerInvoiceDetailService.class).getAccountsReceivableObjectCodeBasedOnReceivableParameter((CustomerInvoiceDetail) customerInvoiceDetail);
    }

    @Override
    public void recalculateCustomerInvoiceDetail(AccountReceivableCustomerInvoice customerInvoiceDocument, AccountsReceivableCustomerInvoiceDetail detail) {
        SpringContext.getBean(CustomerInvoiceDetailService.class).recalculateCustomerInvoiceDetail((CustomerInvoiceDocument) customerInvoiceDocument, (CustomerInvoiceDetail) detail);
    }

    @Override
    public void prepareCustomerInvoiceDetailForAdd(AccountsReceivableCustomerInvoiceDetail detail, AccountReceivableCustomerInvoice customerInvoiceDocument) {
        SpringContext.getBean(CustomerInvoiceDetailService.class).prepareCustomerInvoiceDetailForAdd((CustomerInvoiceDetail) detail, (CustomerInvoiceDocument) customerInvoiceDocument);   
    }

    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(AccountReceivableCustomerInvoice invoice) {
        return SpringContext.getBean(CustomerInvoiceDocumentService.class).getOpenAmountForCustomerInvoiceDocument((CustomerInvoiceDocument) invoice);     
    }

    @Override
    public Collection<AccountReceivableCustomerInvoice> getOpenInvoiceDocumentsByCustomerNumber(String customerNumber) {
        Collection<AccountReceivableCustomerInvoice> invoices = new ArrayList<AccountReceivableCustomerInvoice>();
        
        Collection<CustomerInvoiceDocument> result = SpringContext.getBean(CustomerInvoiceDocumentService.class).getOpenInvoiceDocumentsByCustomerNumber(customerNumber);
        
        if (result != null && !result.isEmpty()) {
            invoices.addAll(result);
        }
        
        return invoices;
    }

    @Override
    public AccountsReceivableNonInvoiced createNonInvoiced() {
        return new NonInvoiced();
    }

    @Override
    public AccountsReceivableInvoicePaidApplied createInvoicePaidApplied() {
        return new InvoicePaidApplied();
    }

    @Override
    public AccountsRecievableDocumentHeader getNewAccountsReceivableDocumentHeader(String processingChart, String processingOrg) {
        return SpringContext.getBean(AccountsReceivableDocumentHeaderService.class).getNewAccountsReceivableDocumentHeader(processingChart, processingOrg);
    }

    @Override
    public AccountsReceivablePaymentApplicationDocument createPaymentApplicationDocument() {
        try {
            return (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getNewDocument(PaymentApplicationDocument.class);
        }
        catch (WorkflowException ex) {
            ex.printStackTrace();
        }
        
        return new PaymentApplicationDocument();
    }

    @Override
    public AccountsReceivableCashControlDetail createCashControlDetail() {
        return new CashControlDetail();
    }

    @Override
    public AccountsReceivableCashControlDocument createCashControlDocument() {
        try {
            return (CashControlDocument) SpringContext.getBean(DocumentService.class).getNewDocument(CashControlDocument.class);
        }
        catch (WorkflowException ex) {
            ex.printStackTrace();
        }
        
        return new CashControlDocument();
    }

    @Override
    public Document blanketApprovePaymentApplicationDocument(AccountsReceivablePaymentApplicationDocument paymentApplicationDocument, String travelDocumentIdentifier) throws WorkflowException {
        return SpringContext.getBean(DocumentService.class).blanketApproveDocument((PaymentApplicationDocument) paymentApplicationDocument, "Blanket Approving APP with travelDocumentIdentifier " + travelDocumentIdentifier, null);
    }
    
    public KualiModuleService getKualiModuleService() {
        return SpringContext.getBean(KualiModuleService.class);
    }
    
    @Override
    public AccountReceivableCustomerInvoice createCustomerInvoiceDocument() {
        try {
            return (CustomerInvoiceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(CustomerInvoiceDocument.class);
        }
        catch (WorkflowException ex) {
            ex.printStackTrace();
        }
        
        return new CustomerInvoiceDocument();
    }
}
