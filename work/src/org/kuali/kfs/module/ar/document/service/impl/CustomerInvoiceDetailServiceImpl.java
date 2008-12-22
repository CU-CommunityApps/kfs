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

import java.math.BigDecimal;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerInvoiceDetailServiceImpl implements CustomerInvoiceDetailService {

    private DateTimeService dateTimeService;
    private UniversityDateService universityDateService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private InvoicePaidAppliedService invoicePaidAppliedService;
    private AccountsReceivableTaxService accountsReceivableTaxService;


    private TaxService taxService;

    // private CustomerInvoiceDetailDao customerInvoiceDetailDao;


    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getAppliedAmountForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public KualiDecimal getAppliedAmountForInvoiceDetail(CustomerInvoiceDetail invoiceDetail) {
        KualiDecimal total = new KualiDecimal(0);

        for (InvoicePaidApplied paidApplied : getInvoicePaidAppliedsForInvoiceDetail(invoiceDetail)) {
            total = total.add(paidApplied.getInvoiceItemAppliedAmount());
        }

        return total;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getInvoicePaidAppliedsForInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        return invoicePaidAppliedService.getInvoicePaidAppliedsForCustomerInvoiceDetail(customerInvoiceDetail);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getInvoicePaidAppliedsForInvoiceDetail(java.lang.String,
     *      java.lang.Integer)
     */
    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoiceDetail(String documentNumber, Integer sequenceNumber) {
        return getInvoicePaidAppliedsForInvoiceDetail(getCustomerInvoiceDetail(documentNumber, sequenceNumber));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getAppliedAmountForInvoiceDetail(java.lang.String,
     *      java.lang.Integer)
     */
    public KualiDecimal getAppliedAmountForInvoiceDetail(String customerInvoiceDocumentNumber, Integer sequenceNumber) {
        return getAppliedAmountForInvoiceDetail(getCustomerInvoiceDetail(customerInvoiceDocumentNumber, sequenceNumber));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getAddCustomerInvoiceDetail(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefault(Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("universityFiscalYear", universityFiscalYear);
        criteria.put("chartOfAccountsCode", chartOfAccountsCode);
        criteria.put("organizationCode", organizationCode);

        OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
        if (ObjectUtils.isNotNull(organizationAccountingDefault)) {
            customerInvoiceDetail.setChartOfAccountsCode(organizationAccountingDefault.getDefaultInvoiceChartOfAccountsCode());
            customerInvoiceDetail.setAccountNumber(organizationAccountingDefault.getDefaultInvoiceAccountNumber());
            customerInvoiceDetail.setSubAccountNumber(organizationAccountingDefault.getDefaultInvoiceSubAccountNumber());
            customerInvoiceDetail.setFinancialObjectCode(organizationAccountingDefault.getDefaultInvoiceFinancialObjectCode());
            customerInvoiceDetail.setFinancialSubObjectCode(organizationAccountingDefault.getDefaultInvoiceFinancialSubObjectCode());
            customerInvoiceDetail.setProjectCode(organizationAccountingDefault.getDefaultInvoiceProjectCode());
            customerInvoiceDetail.setOrganizationReferenceId(organizationAccountingDefault.getDefaultInvoiceOrganizationReferenceIdentifier());
        }

        customerInvoiceDetail.setInvoiceItemTaxAmount(new KualiDecimal(0.00));
        customerInvoiceDetail.setInvoiceItemQuantity(new BigDecimal(1));
        customerInvoiceDetail.setInvoiceItemUnitOfMeasureCode(ArConstants.CUSTOMER_INVOICE_DETAIL_UOM_DEFAULT);
        // KULAR-448 customerInvoiceDetail.setInvoiceItemServiceDate(dateTimeService.getCurrentSqlDate());
        customerInvoiceDetail.setTaxableIndicator(false);
        
        return customerInvoiceDetail;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getAddLineCustomerInvoiceDetailForCurrentUserAndYear()
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromOrganizationAccountingDefaultForCurrentYear() {
        Integer currentUniversityFiscalYear = universityDateService.getCurrentFiscalYear();
        ChartOrgHolder currentUser = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        return getCustomerInvoiceDetailFromOrganizationAccountingDefault(currentUniversityFiscalYear, currentUser.getChartOfAccountsCode(), currentUser.getOrganizationCode());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getCustomerInvoiceDetailFromCustomerInvoiceItemCode(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCode(String invoiceItemCode, String chartOfAccountsCode, String organizationCode) {

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("invoiceItemCode", invoiceItemCode);
        criteria.put("chartOfAccountsCode", chartOfAccountsCode);
        criteria.put("organizationCode", organizationCode);
        CustomerInvoiceItemCode customerInvoiceItemCode = (CustomerInvoiceItemCode) businessObjectService.findByPrimaryKey(CustomerInvoiceItemCode.class, criteria);

        CustomerInvoiceDetail customerInvoiceDetail = null;
        if (ObjectUtils.isNotNull(customerInvoiceItemCode)) {

            customerInvoiceDetail = new CustomerInvoiceDetail();
            customerInvoiceDetail.setChartOfAccountsCode(customerInvoiceItemCode.getDefaultInvoiceChartOfAccountsCode());
            customerInvoiceDetail.setAccountNumber(customerInvoiceItemCode.getDefaultInvoiceAccountNumber());
            customerInvoiceDetail.setSubAccountNumber(customerInvoiceItemCode.getDefaultInvoiceSubAccountNumber());
            customerInvoiceDetail.setFinancialObjectCode(customerInvoiceItemCode.getDefaultInvoiceFinancialObjectCode());
            customerInvoiceDetail.setFinancialSubObjectCode(customerInvoiceItemCode.getDefaultInvoiceFinancialSubObjectCode());
            customerInvoiceDetail.setProjectCode(customerInvoiceItemCode.getDefaultInvoiceProjectCode());

            customerInvoiceDetail.setOrganizationReferenceId(customerInvoiceItemCode.getDefaultInvoiceOrganizationReferenceIdentifier());
            customerInvoiceDetail.setInvoiceItemCode(customerInvoiceItemCode.getInvoiceItemCode());
            customerInvoiceDetail.setInvoiceItemDescription(customerInvoiceItemCode.getInvoiceItemDescription());
            customerInvoiceDetail.setInvoiceItemUnitPrice(customerInvoiceItemCode.getItemDefaultPrice());
            customerInvoiceDetail.setInvoiceItemUnitOfMeasureCode(customerInvoiceItemCode.getDefaultUnitOfMeasureCode());
            customerInvoiceDetail.setInvoiceItemQuantity(customerInvoiceItemCode.getItemDefaultQuantity());

            // KULAR-448 customerInvoiceDetail.setInvoiceItemServiceDate(dateTimeService.getCurrentSqlDate());

            // TODO set sales tax accordingly
            customerInvoiceDetail.setInvoiceItemTaxAmount(new KualiDecimal(0.00));

            // set amount = unit price * quantity
            customerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();

        }

        return customerInvoiceDetail;
    }

    public List<String> getCustomerInvoiceDocumentNumbersByAccountNumber(String accountNumber) {

        Map fieldValues = new HashMap();
        fieldValues.put("accountNumber", accountNumber);

        Collection<CustomerInvoiceDetail> customerInvoiceDetails = businessObjectService.findMatching(CustomerInvoiceDetail.class, fieldValues);
        List<String> docNumbers = new ArrayList<String>();
        for (Iterator itr = customerInvoiceDetails.iterator(); itr.hasNext();) {
            CustomerInvoiceDetail detail = (CustomerInvoiceDetail) itr.next();
            docNumbers.add(detail.getDocumentNumber());
        }

        return docNumbers;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser(java.lang.String)
     */
    public CustomerInvoiceDetail getCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser(String invoiceItemCode) {
        ChartOrgHolder currentUser = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        return getCustomerInvoiceDetailFromCustomerInvoiceItemCode(invoiceItemCode, currentUser.getChartOfAccountsCode(), currentUser.getOrganizationCode());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getDiscountCustomerInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail,
     *      java.lang.Integer, java.lang.String, java.lang.String)
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail, Integer universityFiscalYear, String chartOfAccountsCode, String organizationCode) {

        CustomerInvoiceDetail discountCustomerInvoiceDetail = (CustomerInvoiceDetail) ObjectUtils.deepCopy(customerInvoiceDetail);
        discountCustomerInvoiceDetail.setInvoiceItemUnitPriceToNegative();
        discountCustomerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();
        discountCustomerInvoiceDetail.setInvoiceItemDescription(ArConstants.DISCOUNT_PREFIX + StringUtils.trimToEmpty(customerInvoiceDetail.getInvoiceItemDescription()));

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put("universityFiscalYear", universityFiscalYear);
        criteria.put("processingChartOfAccountCode", chartOfAccountsCode);
        criteria.put("processingOrganizationCode", organizationCode);

        SystemInformation systemInformation = (SystemInformation) businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);
        if (ObjectUtils.isNotNull(systemInformation)) {
            discountCustomerInvoiceDetail.setFinancialObjectCode(systemInformation.getDiscountObjectCode());
        }

        return discountCustomerInvoiceDetail;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getDiscountCustomerInvoiceDetailForCurrentYear(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetailForCurrentYear(CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument) {
        Integer currentUniversityFiscalYear = universityDateService.getCurrentFiscalYear();
        String processingChartOfAccountsCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        String processingOrganizationCode = customerInvoiceDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        return getDiscountCustomerInvoiceDetail(customerInvoiceDetail, currentUniversityFiscalYear, processingChartOfAccountsCode, processingOrganizationCode);
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#recalculateCustomerInvoiceDetail(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public void recalculateCustomerInvoiceDetail(CustomerInvoiceDocument customerInvoiceDocument, CustomerInvoiceDetail customerInvoiceDetail) {

        // make sure amounts are negative when they are supposed to be
        if (!customerInvoiceDocument.isInvoiceReversal() && customerInvoiceDetail.isDiscountLine()) {
            customerInvoiceDetail.setInvoiceItemUnitPriceToNegative();
        }
        else if (customerInvoiceDocument.isInvoiceReversal() && !customerInvoiceDetail.isDiscountLine()) {
            customerInvoiceDetail.setInvoiceItemUnitPriceToNegative();
        }
        KualiDecimal pretaxAmount = customerInvoiceDetail.getInvoiceItemPreTaxAmount();


        KualiDecimal taxAmount = KualiDecimal.ZERO;
        if (accountsReceivableTaxService.isCustomerInvoiceDetailTaxable(customerInvoiceDocument, customerInvoiceDetail)) {
            
            String postalCode = accountsReceivableTaxService.getPostalCodeForTaxation(customerInvoiceDocument);
            taxAmount = taxService.getTotalSalesTaxAmount(dateTimeService.getCurrentSqlDate(), postalCode, pretaxAmount);
        }

        customerInvoiceDetail.setInvoiceItemTaxAmount(taxAmount);
        customerInvoiceDetail.setAmount(taxAmount.add(pretaxAmount));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#updateAccountsForCorrespondingDiscount(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public void updateAccountsForCorrespondingDiscount(CustomerInvoiceDetail parent) {

        CustomerInvoiceDetail discount = parent.getDiscountCustomerInvoiceDetail();
        if (ObjectUtils.isNotNull(discount)) {
            if (StringUtils.isNotEmpty(parent.getAccountNumber())) {
                discount.setAccountNumber(parent.getAccountNumber());
            }
            if (StringUtils.isNotEmpty(parent.getSubAccountNumber())) {
                discount.setSubAccountNumber(parent.getSubAccountNumber());
            }
        }
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getCustomerInvoiceDetail(java.lang.String,
     *      java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
    public CustomerInvoiceDetail getCustomerInvoiceDetail(String documentNumber, Integer sequenceNumber) {
        Map criteria = new HashMap();
        criteria.put("documentNumber", documentNumber);
        criteria.put("sequenceNumber", sequenceNumber);

        return (CustomerInvoiceDetail) businessObjectService.findByPrimaryKey(CustomerInvoiceDetail.class, criteria);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getOpenAmount(java.lang.Integer,
     *      org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public KualiDecimal getOpenAmount(CustomerInvoiceDetail customerInvoiceDetail) {
        KualiDecimal totalAppliedAmount = KualiDecimal.ZERO;
        KualiDecimal appliedAmount;
        KualiDecimal openAmount;
        KualiDecimal invoiceItemAmount;

        Collection<InvoicePaidApplied> results = invoicePaidAppliedService.getApprovedInvoicePaidAppliedsForCustomerInvoiceDetail(customerInvoiceDetail);
        for (InvoicePaidApplied invoicePaidApplied : results) {
            appliedAmount = invoicePaidApplied.getInvoiceItemAppliedAmount();
            if (ObjectUtils.isNotNull(appliedAmount))
                totalAppliedAmount = totalAppliedAmount.add(appliedAmount);
        }
        invoiceItemAmount = customerInvoiceDetail.getAmount();
        openAmount = invoiceItemAmount.subtract(totalAppliedAmount);

        return openAmount;
    }

    /**
     * get open amount snapshot from previous date
     */
    public KualiDecimal getOpenAmountByDate(CustomerInvoiceDetail customerInvoiceDetail, Date runDate) {
        KualiDecimal totalAppliedAmount = KualiDecimal.ZERO;
        KualiDecimal appliedAmount;
        KualiDecimal openAmount;
        KualiDecimal invoiceItemAmount;
        Date invoicePaidDate;

        Collection<InvoicePaidApplied> results = invoicePaidAppliedService.getApprovedInvoicePaidAppliedsForCustomerInvoiceDetail(customerInvoiceDetail);
        for (InvoicePaidApplied invoicePaidApplied : results) {
            appliedAmount = invoicePaidApplied.getInvoiceItemAppliedAmount();
            invoicePaidDate = invoicePaidApplied.getDocumentHeader().getDocumentFinalDate();
// get the paid date and use that to limit the adds from below
            if (ObjectUtils.isNotNull(appliedAmount)&&!invoicePaidDate.after(runDate))
                totalAppliedAmount = totalAppliedAmount.add(appliedAmount);
        }
        invoiceItemAmount = customerInvoiceDetail.getAmount();
        openAmount = invoiceItemAmount.subtract(totalAppliedAmount);

        return openAmount;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getCustomerInvoiceDetailsForInvoice(java.lang.String)
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(String customerInvoiceDocumentNumber) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("documentNumber", customerInvoiceDocumentNumber);

        return businessObjectService.findMatching(CustomerInvoiceDetail.class, criteria);
    }


    public KualiDecimal getAppliedAmountFromSpecificDocument(String documentNumber, String referenceCustomerInvoiceDocumentNumber) {

        KualiDecimal appliedAmountFromSpecificDocument = KualiDecimal.ZERO;
        Collection<InvoicePaidApplied> results = getInvoicePaidAppliedsFromSpecificDocument(documentNumber, referenceCustomerInvoiceDocumentNumber);
        for (InvoicePaidApplied invoicePaidApplied : results) {
            appliedAmountFromSpecificDocument = appliedAmountFromSpecificDocument.add(invoicePaidApplied.getInvoiceItemAppliedAmount());
        }

        return appliedAmountFromSpecificDocument;
    }

    public Collection<InvoicePaidApplied> getInvoicePaidAppliedsFromSpecificDocument(String documentNumber, String referenceCustomerInvoiceDocumentNumber) {
        return invoicePaidAppliedService.getInvoicePaidAppliedsFromSpecificDocument(documentNumber, referenceCustomerInvoiceDocumentNumber);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getCustomerInvoiceDetailsForInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForInvoice(CustomerInvoiceDocument customerInvoiceDocument) {
        if (null == customerInvoiceDocument) {
            return new ArrayList<CustomerInvoiceDetail>();
        }
        return getCustomerInvoiceDetailsForInvoice(customerInvoiceDocument.getDocumentNumber());
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#updateAccountsReceivableObjectCode(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public void updateAccountsReceivableObjectCode(CustomerInvoiceDetail customerInvoiceDetail) {
        customerInvoiceDetail.setAccountsReceivableObjectCode(getAccountsReceivableObjectCodeBasedOnReceivableParameter(customerInvoiceDetail));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getAccountsReceivableObjectCodeBasedOnReceivableParameter(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    public String getAccountsReceivableObjectCodeBasedOnReceivableParameter(CustomerInvoiceDetail customerInvoiceDetail) {
        String receivableOffsetOption = parameterService.getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        String accountsReceivableObjectCode = null;
        if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_CHART.equals(receivableOffsetOption)) {
            if (StringUtils.isNotEmpty(customerInvoiceDetail.getChartOfAccountsCode())) {
                customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.CHART);
                accountsReceivableObjectCode = customerInvoiceDetail.getChart().getFinAccountsReceivableObj().getFinancialObjectCode();
            }
        }
        else if (ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_SUBFUND.equals(receivableOffsetOption)) {
            if (StringUtils.isNotEmpty(customerInvoiceDetail.getAccountNumber())) {
                customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                accountsReceivableObjectCode = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND, customerInvoiceDetail.getAccount().getSubFundGroupCode());
            }
        }
        return accountsReceivableObjectCode;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#prepareCustomerInvoiceDetailForAdd(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail,
     *      org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public void prepareCustomerInvoiceDetailForAdd(CustomerInvoiceDetail customerInvoiceDetail, CustomerInvoiceDocument customerInvoiceDocument) {
        recalculateCustomerInvoiceDetail(customerInvoiceDocument, customerInvoiceDetail);
        updateAccountsReceivableObjectCode(customerInvoiceDetail);
    }


    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * @param invoicePaidAppliedService
     */
    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }


    public InvoicePaidAppliedService getInvoicePaidAppliedService() {
        return invoicePaidAppliedService;
    }

    public TaxService getTaxService() {
        return taxService;
    }

    public void setTaxService(TaxService taxService) {
        this.taxService = taxService;
    }
    
    public AccountsReceivableTaxService getAccountsReceivableTaxService() {
        return accountsReceivableTaxService;
    }

    public void setAccountsReceivableTaxService(AccountsReceivableTaxService accountsReceivableTaxService) {
        this.accountsReceivableTaxService = accountsReceivableTaxService;
    }    
}
