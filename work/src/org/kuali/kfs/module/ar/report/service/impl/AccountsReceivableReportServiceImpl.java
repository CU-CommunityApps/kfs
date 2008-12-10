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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerCreditMemoDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.businessobject.defaultvalue.InstitutionNameValueFinder;
import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerCreditMemoDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.module.ar.report.service.CustomerCreditMemoReportService;
import org.kuali.kfs.module.ar.report.service.CustomerInvoiceReportService;
import org.kuali.kfs.module.ar.report.service.CustomerStatementReportService;
import org.kuali.kfs.module.ar.report.service.OCRLineService;
import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoDetailReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerInvoiceReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerStatementDetailReportDataHolder;
import org.kuali.kfs.module.ar.report.util.CustomerStatementReportDataHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.format.PhoneNumberFormatter;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class AccountsReceivableReportServiceImpl implements AccountsReceivableReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsReceivableReportServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private OptionsService optionsService;
    private UniversityDateService universityDateService;
    private DateTimeService dateTimeService;

    private PhoneNumberFormatter phoneNumberFormatter = new PhoneNumberFormatter();

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateCreditMemo(org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument)
     */
    public File generateCreditMemo(CustomerCreditMemoDocument creditMemo) throws WorkflowException{
        CustomerCreditMemoReportDataHolder reportDataHolder = new CustomerCreditMemoReportDataHolder();
        initializeDateTimeService();
        String invoiceNumber = creditMemo.getFinancialDocumentReferenceInvoiceNumber();
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        CustomerInvoiceDocument invoice = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(invoiceNumber);
        String custID = invoice.getAccountsReceivableDocumentHeader().getCustomerNumber();

        CustomerAddressService addrService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress billToAddr = addrService.getPrimaryAddress(custID);

        Map<String, String> creditMemoMap = new HashMap<String, String>();
        creditMemoMap.put("docNumber", creditMemo.getDocumentNumber());
        creditMemoMap.put("refDocNumber", invoice.getDocumentNumber());
        Date date = creditMemo.getDocumentHeader().getDocumentFinalDate();
        if (ObjectUtils.isNotNull(date)) {
            creditMemoMap.put("createDate", dateTimeService.toDateString(date));
        }
        reportDataHolder.setCreditmemo(creditMemoMap);

        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", custID);
        if (billToAddr != null) { 
            customerMap.put("Name", billToAddr.getCustomerAddressName());
            customerMap.put("StreetAddressLine1", billToAddr.getCustomerLine1StreetAddress());
            customerMap.put("StreetAddressLine2", billToAddr.getCustomerLine2StreetAddress());
            customerMap.put("City", billToAddr.getCustomerCityName());

            if (ObjectUtils.isNotNull(billToAddr.getCustomerCountryCode())) {
                if (billToAddr.getCustomerCountryCode().equals("US")) { 
                    customerMap.put("State", billToAddr.getCustomerStateCode());
                    customerMap.put("Zipcode", billToAddr.getCustomerZipCode());
                } else {
                    customerMap.put("State", billToAddr.getCustomerAddressInternationalProvinceName());
                    customerMap.put("Zipcode", billToAddr.getCustomerInternationalMailCode());
                    customerMap.put("Country", billToAddr.getCustomerCountry().getPostalCountryName());
                }
            }
        }

        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        if (ObjectUtils.isNotNull(invoice.getCustomerPurchaseOrderNumber())) {
            invoiceMap.put("poNumber", invoice.getCustomerPurchaseOrderNumber());
        }
        if(invoice.getCustomerPurchaseOrderDate() != null) {
            invoiceMap.put("poDate", dateTimeService.toDateString(invoice.getCustomerPurchaseOrderDate()));
        }

        String initiatorID = invoice.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        Person user = null;
        try {
            user = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPersonByPrincipalName(initiatorID);
        } catch (Exception e) {
            LOG.error("Exception thrown from PersonService.getPersonByPrincipalName('" + initiatorID + "').", e);
            throw new RuntimeException("Exception thrown from PersonService.getPersonByPrincipalName('" + initiatorID + "').", e);
        }
        if (user == null) {
            throw new RuntimeException("User '" + initiatorID + "' could not be retrieved from PersonService.");
        }

        invoiceMap.put("invoicePreparer", user.getFirstName()+" "+user.getLastName() );
        invoiceMap.put("headerField", (ObjectUtils.isNull(invoice.getInvoiceHeaderText())?"":invoice.getInvoiceHeaderText()));
        invoiceMap.put("billingOrgName", invoice.getBilledByOrganization().getOrganizationName());
        invoiceMap.put("pretaxAmount", invoice.getInvoiceItemPreTaxAmountTotal().toString());
        invoiceMap.put("taxAmount", invoice.getInvoiceItemTaxAmountTotal().toString());
        //KualiDecimal taxPercentage = invoice.getStateTaxPercent().add(invoice.getLocalTaxPercent());
        KualiDecimal taxPercentage = new KualiDecimal(6.85);
        invoiceMap.put("taxPercentage", "*** "+taxPercentage.toString()+"%");
        invoiceMap.put("invoiceAmountDue", invoice.getSourceTotal().toString());
        invoiceMap.put("ocrLine", "");
        reportDataHolder.setInvoice(invoiceMap);

        Map<String, String> sysinfoMap = new HashMap<String, String>();
        InstitutionNameValueFinder finder = new InstitutionNameValueFinder();

        Org billingOrg = invoice.getBilledByOrganization();
        String chart = billingOrg.getChartOfAccountsCode();
        String org = billingOrg.getOrganizationCode();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode",chart );
        criteria.put("organizationCode", org);
        OrganizationOptions orgOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();
        criteria = new HashMap<String, String>();

        Org processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();

        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = (SystemInformation)businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        sysinfoMap.put("univName", finder.getValue());
        String univAddr = processingOrg.getOrganizationCityName() +", "+ 
        processingOrg.getOrganizationStateCode() +" "+ processingOrg.getOrganizationZipCode();
        sysinfoMap.put("univAddr", univAddr);
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID# "+sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }

        reportDataHolder.setSysinfo(sysinfoMap);

        invoiceMap.put("billingOrgFax", (ObjectUtils.isNull(orgOptions.getOrganizationFaxNumber())?"":orgOptions.getOrganizationFaxNumber()));
        invoiceMap.put("billingOrgPhone", orgOptions.getOrganizationPhoneNumber());

        creditMemo.populateCustomerCreditMemoDetailsAfterLoad();
        List<CustomerCreditMemoDetail> detailsList = creditMemo.getCreditMemoDetails();
        List<CustomerCreditMemoDetailReportDataHolder> details = new ArrayList<CustomerCreditMemoDetailReportDataHolder>();
        CustomerCreditMemoDetailReportDataHolder detailDataHolder;
        for (CustomerCreditMemoDetail detail : detailsList) {
            detailDataHolder = new CustomerCreditMemoDetailReportDataHolder(detail, detail.getCustomerInvoiceDetail());
            details.add(detailDataHolder);
        }

        reportDataHolder.setDetails(details);

        Date runDate = dateTimeService.getCurrentSqlDate();
        CustomerCreditMemoReportService service = SpringContext.getBean(CustomerCreditMemoReportService.class);
        File report = service.generateReport(reportDataHolder, runDate);

        return report;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public File generateInvoice(CustomerInvoiceDocument invoice) {

        CurrencyFormatter currencyFormatter = new CurrencyFormatter();
        
        CustomerInvoiceReportDataHolder reportDataHolder = new CustomerInvoiceReportDataHolder();
        String custID = invoice.getAccountsReceivableDocumentHeader().getCustomerNumber();
        CustomerService custService = SpringContext.getBean(CustomerService.class);
        Customer cust = custService.getByPrimaryKey(custID);
        Integer customerBillToAddressIdentifier = invoice.getCustomerBillToAddressIdentifier();
        Integer customerShipToAddressIdentifier = invoice.getCustomerShipToAddressIdentifier();
        CustomerAddressService addrService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress billToAddr = addrService.getByPrimaryKey(custID, customerBillToAddressIdentifier);
        CustomerAddress shipToAddr = addrService.getByPrimaryKey(custID, customerShipToAddressIdentifier);

        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", custID);
        if (billToAddr != null){ 
            customerMap.put("billToName", billToAddr.getCustomerAddressName());
            customerMap.put("billToStreetAddressLine1", billToAddr.getCustomerLine1StreetAddress());
            customerMap.put("billToStreetAddressLine2", billToAddr.getCustomerLine2StreetAddress());
            String billCityStateZip = "";
            if (billToAddr.getCustomerCountryCode().equals("US")) { 
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerStateCode(), billToAddr.getCustomerZipCode());
            } else {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerAddressInternationalProvinceName(), billToAddr.getCustomerInternationalMailCode());
                customerMap.put("billToCountry", billToAddr.getCustomerCountry().getPostalCountryName());
            }
            customerMap.put("billToCityStateZip", billCityStateZip);
        }
        if (shipToAddr !=null) {
            customerMap.put("shipToName", shipToAddr.getCustomerAddressName());
            customerMap.put("shipToStreetAddressLine1", shipToAddr.getCustomerLine1StreetAddress());
            customerMap.put("shipToStreetAddressLine2", shipToAddr.getCustomerLine2StreetAddress());
            String shipCityStateZip = "";
            if (shipToAddr.getCustomerCountryCode().equals("US")) { 
                shipCityStateZip = generateCityStateZipLine(shipToAddr.getCustomerCityName(), shipToAddr.getCustomerStateCode(), shipToAddr.getCustomerZipCode());
            } else {
                shipCityStateZip = generateCityStateZipLine(shipToAddr.getCustomerCityName(), shipToAddr.getCustomerAddressInternationalProvinceName(), shipToAddr.getCustomerInternationalMailCode());
                customerMap.put("shipToCountry", shipToAddr.getCustomerCountry().getPostalCountryName());
            }
            customerMap.put("shipToCityStateZip", shipCityStateZip);
        }
        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        invoiceMap.put("poNumber", invoice.getCustomerPurchaseOrderNumber());
        initializeDateTimeService();
        if(invoice.getCustomerPurchaseOrderDate() != null) {
            invoiceMap.put("poDate", dateTimeService.toDateString(invoice.getCustomerPurchaseOrderDate()));
        }

        String initiatorID = invoice.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
        Person user = null;
        try {
            user = org.kuali.rice.kim.service.KIMServiceLocator.getPersonService().getPersonByPrincipalName(initiatorID);
        } catch (Exception e) {
            LOG.error("Exception thrown from PersonService.getPersonByPrincipalName('" + initiatorID + "').", e);
            throw new RuntimeException("Exception thrown from PersonService.getPersonByPrincipalName('" + initiatorID + "').", e);
        }
        if (user == null) {
            throw new RuntimeException("User '" + initiatorID + "' could not be retrieved from PersonService.");
        }
        
        invoiceMap.put("invoicePreparer", user.getFirstName()+" "+user.getLastName() );
        invoiceMap.put("headerField", invoice.getInvoiceHeaderText());
        invoiceMap.put("customerOrg", invoice.getBilledByOrganizationCode());
        invoiceMap.put("docNumber", invoice.getDocumentNumber());
        invoiceMap.put("invoiceDueDate", dateTimeService.toDateString(invoice.getInvoiceDueDate()));
        invoiceMap.put("createDate", dateTimeService.toDateString(invoice.getDocumentHeader().getWorkflowDocument().getCreateDate()));
        invoiceMap.put("invoiceAttentionLineText", StringUtils.upperCase(invoice.getInvoiceAttentionLineText()));
        invoiceMap.put("billingOrgName", invoice.getBilledByOrganization().getOrganizationName());
        invoiceMap.put("pretaxAmount", currencyFormatter.format(invoice.getInvoiceItemPreTaxAmountTotal()).toString());
        invoiceMap.put("taxAmount", currencyFormatter.format(invoice.getInvoiceItemTaxAmountTotal()).toString());
        invoiceMap.put("taxPercentage", ""); // suppressing this as its useless ... see KULAR-415
        invoiceMap.put("invoiceAmountDue", currencyFormatter.format(invoice.getSourceTotal()).toString());
        invoiceMap.put("invoiceTermsText", invoice.getInvoiceTermsText());

        OCRLineService ocrService = SpringContext.getBean(OCRLineService.class);
        String ocrLine = ocrService.generateOCRLine(invoice.getSourceTotal(), custID, invoice.getDocumentNumber());
        invoiceMap.put("ocrLine", ocrLine);
        CustomerInvoiceDetailService invoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        List<CustomerInvoiceDetail> detailsList = (List<CustomerInvoiceDetail>)invoiceDetailService.getCustomerInvoiceDetailsForInvoice(invoice);
        CustomerInvoiceDetail firstDetail = (CustomerInvoiceDetail)detailsList.get(0);
        String firstChartCode = firstDetail.getChartOfAccountsCode();
        String firstAccount = firstDetail.getAccountNumber();
        invoiceMap.put("chartAndAccountOfFirstItem", firstChartCode+firstAccount);

        Map<String, String> sysinfoMap = new HashMap<String, String>();
        InstitutionNameValueFinder finder = new InstitutionNameValueFinder();

        Org billingOrg = invoice.getBilledByOrganization();
        String chart = billingOrg.getChartOfAccountsCode();
        String org = billingOrg.getOrganizationCode();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", chart);
        criteria.put("organizationCode", org);
        OrganizationOptions orgOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();
        criteria = new HashMap<String, String>();

        Org processingOrg = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization();

        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = (SystemInformation)businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);

        sysinfoMap.put("univName", StringUtils.upperCase(finder.getValue()));
        sysinfoMap.put("univAddr", generateCityStateZipLine(processingOrg.getOrganizationCityName(), processingOrg.getOrganizationStateCode(), processingOrg.getOrganizationZipCode()));
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID #"+sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }
        sysinfoMap.put("checkPayableTo", orgOptions.getOrganizationCheckPayableToName());
        sysinfoMap.put("remitToName", orgOptions.getOrganizationRemitToAddressName());
        sysinfoMap.put("remitToAddressLine1", orgOptions.getOrganizationRemitToLine1StreetAddress());
        sysinfoMap.put("remitToAddressLine2", orgOptions.getOrganizationRemitToLine2StreetAddress());

        sysinfoMap.put("remitToCityStateZip", generateCityStateZipLine(orgOptions.getOrganizationRemitToCityName(), orgOptions.getOrganizationRemitToStateCode(), orgOptions.getOrganizationRemitToZipCode()));
        
        invoiceMap.put("billingOrgFax", (String)phoneNumberFormatter.format(orgOptions.getOrganizationFaxNumber()));
        invoiceMap.put("billingOrgPhone", (String)phoneNumberFormatter.format(orgOptions.getOrganizationPhoneNumber()));

        invoiceMap.put("orgOptionsMessageText", orgOptions.getOrganizationMessageText());

        reportDataHolder.setSysinfo(sysinfoMap);
        reportDataHolder.setDetails(detailsList);
        reportDataHolder.setInvoice(invoiceMap);

        Date runDate = dateTimeService.getCurrentSqlDate();
        CustomerInvoiceReportService service = SpringContext.getBean(CustomerInvoiceReportService.class);
        File report = service.generateReport(reportDataHolder, runDate);

        invoice.setPrintDate(runDate);
        invoice.setPrintInvoiceIndicator("Y");
        DocumentService docService = SpringContext.getBean(DocumentService.class);
        try {
            docService.saveDocument(invoice);
        }
        catch (WorkflowException e) {
            LOG.error(e);
        }
        return report;
    }

    /**
     * 
     * This method...
     * @param billingChartCode
     * @param billingOrgCode
     * @param customerNumber
     * @param details
     * @return
     */
    public File generateStatement(String billingChartCode, String billingOrgCode, String customerNumber, Org processingOrg, List<CustomerStatementDetailReportDataHolder> details){

        CustomerStatementReportDataHolder reportDataHolder = new CustomerStatementReportDataHolder();
        CustomerAddressService addrService = SpringContext.getBean(CustomerAddressService.class);
        CustomerAddress billToAddr = addrService.getPrimaryAddress(customerNumber);

        initializeDateTimeService();
        
        Map<String, String> customerMap = new HashMap<String, String>();
        customerMap.put("id", customerNumber);
        if (billToAddr != null){ 
            customerMap.put("billToName", billToAddr.getCustomerAddressName());
            customerMap.put("billToStreetAddressLine1", billToAddr.getCustomerLine1StreetAddress());
            customerMap.put("billToStreetAddressLine2", billToAddr.getCustomerLine2StreetAddress());
            String billCityStateZip = "";
            if (billToAddr.getCustomerCountryCode().equals("US")) { 
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerStateCode(), billToAddr.getCustomerZipCode());
            } else {
                billCityStateZip = generateCityStateZipLine(billToAddr.getCustomerCityName(), billToAddr.getCustomerAddressInternationalProvinceName(), billToAddr.getCustomerInternationalMailCode());
                customerMap.put("billToCountry", billToAddr.getCustomerCountry().getPostalCountryName());
            }
            customerMap.put("billToCityStateZip", billCityStateZip);
        }

        reportDataHolder.setCustomer(customerMap);

        Map<String, String> invoiceMap = new HashMap<String, String>();
        invoiceMap.put("createDate", dateTimeService.toDateString(dateTimeService.getCurrentDate()));
        invoiceMap.put("customerOrg", billingOrgCode);
        Org billingOrg = SpringContext.getBean(OrganizationService.class).getByPrimaryId(billingChartCode, billingOrgCode);
        invoiceMap.put("billingOrgName", billingOrg.getOrganizationName());
        
        KualiDecimal amountDue = new KualiDecimal(0);
        for (CustomerStatementDetailReportDataHolder data : details) {
            if (data.getFinancialDocumentTotalAmountCharge()!=null) {
                amountDue = amountDue.add(data.getFinancialDocumentTotalAmountCharge());
            }
            if (data.getFinancialDocumentTotalAmountCredit()!=null) {
                amountDue = amountDue.subtract(data.getFinancialDocumentTotalAmountCredit());
            }
        }
        invoiceMap.put("amountDue", amountDue.toString());
        String ocrLine = SpringContext.getBean(OCRLineService.class).generateOCRLine(amountDue, customerNumber, null);
        invoiceMap.put("ocrLine", ocrLine);
        
        Map<String, String> sysinfoMap = new HashMap<String, String>();
        InstitutionNameValueFinder finder = new InstitutionNameValueFinder();
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", billingChartCode);
        criteria.put("organizationCode", billingOrgCode);
        OrganizationOptions orgOptions = (OrganizationOptions) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationOptions.class, criteria);

        sysinfoMap.put("checkPayableTo", orgOptions.getOrganizationCheckPayableToName());
        sysinfoMap.put("remitToName", orgOptions.getOrganizationRemitToAddressName());
        sysinfoMap.put("remitToAddressLine1", orgOptions.getOrganizationRemitToLine1StreetAddress());
        sysinfoMap.put("remitToAddressLine2", orgOptions.getOrganizationRemitToLine2StreetAddress());
        sysinfoMap.put("remitToCityStateZip", generateCityStateZipLine(orgOptions.getOrganizationRemitToCityName(), orgOptions.getOrganizationRemitToStateCode(), orgOptions.getOrganizationRemitToZipCode()));

        invoiceMap.put("billingOrgFax", (String)phoneNumberFormatter.format(orgOptions.getOrganizationFaxNumber()));
        invoiceMap.put("billingOrgPhone", (String)phoneNumberFormatter.format(orgOptions.getOrganizationPhoneNumber()));

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();

        criteria.clear();
        criteria.put("universityFiscalYear", fiscalYear);
        criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
        criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
        SystemInformation sysinfo = (SystemInformation)businessObjectService.findByPrimaryKey(SystemInformation.class, criteria);
        
        sysinfoMap.put("univName", StringUtils.upperCase(finder.getValue()));
        sysinfoMap.put("univAddr", generateCityStateZipLine(processingOrg.getOrganizationCityName(), processingOrg.getOrganizationStateCode(), processingOrg.getOrganizationZipCode()));
        if (sysinfo != null) {
            sysinfoMap.put("FEIN", "FED ID #"+sysinfo.getUniversityFederalEmployerIdentificationNumber());
        }

        calculateAgingAmounts(details, invoiceMap); // HOLD OFF ON THIS UNTIL THE CODING IS TESTED AND WORKING - KULAR-530
        
        reportDataHolder.setSysinfo(sysinfoMap);
        reportDataHolder.setDetails(details);
        reportDataHolder.setInvoice(invoiceMap);

        Date runDate = dateTimeService.getCurrentSqlDate();
        CustomerStatementReportService service = SpringContext.getBean(CustomerStatementReportService.class);
        File f = service.generateReport(reportDataHolder, runDate);
        return f;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByBillingOrg(java.lang.String, java.lang.String, java.sql.Date)
     */
    public List<File> generateInvoicesByBillingOrg(String chartCode, String orgCode, Date date) {
        initializeDateTimeService();
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection<CustomerInvoiceDocument> invoices = invoiceDocService.getCustomerInvoiceDocumentsByBillingChartAndOrg(chartCode, orgCode);
        List<File> reports = new ArrayList<File>();

        for (CustomerInvoiceDocument doc : invoices) {
            if (doc.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                if (ArConstants.PrintInvoiceOptions.PRINT_BY_BILLING_ORG.equalsIgnoreCase(doc.getPrintInvoiceIndicator())) {
                    if (date == null) {
                        reports.add(generateInvoice(doc));
                    }
                    else if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getCreateDate())!=null) {
                        if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getCreateDate()).equals(dateTimeService.toDateString(date))) {
                            reports.add(generateInvoice(doc));
                        }
                    }
                }
            }
        }
        return reports;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByProcessingOrg(java.lang.String, java.lang.String, java.sql.Date)
     */
    public List<File> generateInvoicesByProcessingOrg(String chartCode, String orgCode, Date date){
        initializeDateTimeService();
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        List<CustomerInvoiceDocument> invoices = invoiceDocService.getCustomerInvoiceDocumentsByProcessingChartAndOrg(chartCode, orgCode);
        List<File> reports = new ArrayList<File>();
        for (CustomerInvoiceDocument doc : invoices) {
            if (doc.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
                if (ArConstants.PrintInvoiceOptions.PRINT_BY_PROCESSING_ORG.equalsIgnoreCase(doc.getPrintInvoiceIndicator())) {
                    if (date == null) {
                        reports.add(generateInvoice(doc));
                    }
                    else if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getCreateDate())!=null) {
                        if (dateTimeService.toDateString(doc.getDocumentHeader().getWorkflowDocument().getCreateDate()).equals(dateTimeService.toDateString(date))) {
                            reports.add(generateInvoice(doc));
                        }
                    }
                }
            }
        }
        return reports;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateInvoicesByInitiator(java.lang.String)
     */
    public List<File> generateInvoicesByInitiator(String initiator) {

        List<File> reports = new ArrayList<File>();
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection<CustomerInvoiceDocument> invoices = invoiceDocService.getAllCustomerInvoiceDocuments();
        for (CustomerInvoiceDocument invoice : invoices) {
            if (ArConstants.PrintInvoiceOptions.PRINT_BY_USER.equalsIgnoreCase(invoice.getPrintInvoiceIndicator())) {
                if (invoice.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId().equals(initiator)) {
                    reports.add(generateInvoice(invoice));
                }
            }
        }
        return reports;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByBillingOrg(java.lang.String, java.lang.String)
     */
    public List<File> generateStatementByBillingOrg(String chartCode, String orgCode) {

        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection<CustomerInvoiceDocument> invoices = invoiceDocService.getCustomerInvoiceDocumentsByBillingChartAndOrg(chartCode, orgCode);
        List<CustomerStatementDetailReportDataHolder> details = new ArrayList<CustomerStatementDetailReportDataHolder>();
        CustomerCreditMemoDocumentService service = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
        List<File> reports = new ArrayList<File>();
        CustomerInvoiceDocument ctrlDoc = new CustomerInvoiceDocument();
        for (CustomerInvoiceDocument invoice : invoices) {
            if (invoice.isOpenInvoiceIndicator()) {
                Collection<CustomerCreditMemoDocument> creditMemos = service.getCustomerCreditMemoDocumentByInvoiceDocument(invoice.getDocumentNumber());
                for (CustomerCreditMemoDocument doc : creditMemos) {
                    doc.populateCustomerCreditMemoDetailsAfterLoad();
                    CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(doc.getDocumentHeader(), doc.getInvoice().getAccountsReceivableDocumentHeader().getProcessingOrganization(), "Credit Memo");
                    details.add(detail);
                }
                CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(invoice.getDocumentHeader(), invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(), "Invoice");
                details.add(detail);

            }
            if (invoice.compareTo(ctrlDoc) != 0) {  
                reports.add(generateStatement(invoice.getBillByChartOfAccountCode(), invoice.getBilledByOrganizationCode(), invoice.getAccountsReceivableDocumentHeader().getCustomerNumber(), invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(), details));
                details.clear();
            }
            ctrlDoc = invoice;
        }
        return reports;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByAccount(java.lang.String)
     */
    public List<File> generateStatementByAccount(String accountNumber) {
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection<CustomerInvoiceDocument> invoices = invoiceDocService.getCustomerInvoiceDocumentsByAccountNumber(accountNumber);

        List<CustomerInvoiceDocument> invoiceList = new ArrayList<CustomerInvoiceDocument>(invoices);
        Collections.sort(invoiceList);

        List<CustomerStatementDetailReportDataHolder> details = new ArrayList<CustomerStatementDetailReportDataHolder>();
        CustomerCreditMemoDocumentService service = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
        CustomerInvoiceDocument ctrlDoc = new CustomerInvoiceDocument();
        List<File> reports = new ArrayList<File>();

        for (CustomerInvoiceDocument invoice : invoiceList) {
            if (invoice.isOpenInvoiceIndicator()) {
                Collection<CustomerCreditMemoDocument> creditMemos = service.getCustomerCreditMemoDocumentByInvoiceDocument(invoice.getDocumentNumber());
                for (CustomerCreditMemoDocument doc : creditMemos) {
                    doc.populateCustomerCreditMemoDetailsAfterLoad();
                    CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(doc.getDocumentHeader(), doc.getInvoice().getAccountsReceivableDocumentHeader().getProcessingOrganization(), "Credit Memo");
                    details.add(detail);
                }
                CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(invoice.getDocumentHeader(), invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(), "Invoice");
                details.add(detail);

            }
            if (invoice.compareTo(ctrlDoc) != 0) {  
                reports.add(generateStatement(invoice.getBillByChartOfAccountCode(), invoice.getBilledByOrganizationCode(), invoice.getAccountsReceivableDocumentHeader().getCustomerNumber(), invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(), details));
                details.clear();
            }
            ctrlDoc = invoice;

        }
        return reports;
    }

    /**
     * 
     * @see org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService#generateStatementByCustomer(java.lang.String)
     */
    public List<File> generateStatementByCustomer(String customerNumber) {
        CustomerInvoiceDocumentService invoiceDocService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
        Collection<CustomerInvoiceDocument> invoices = invoiceDocService.getCustomerInvoiceDocumentsByCustomerNumber(customerNumber);

        List<CustomerInvoiceDocument> invoiceList = new ArrayList<CustomerInvoiceDocument>(invoices);
        Collections.sort(invoiceList);

        List<CustomerStatementDetailReportDataHolder> details = new ArrayList<CustomerStatementDetailReportDataHolder>();
        CustomerCreditMemoDocumentService service = SpringContext.getBean(CustomerCreditMemoDocumentService.class);
        CustomerInvoiceDocument ctrlDoc = new CustomerInvoiceDocument();

        List<File> reports = new ArrayList<File>();
        for (CustomerInvoiceDocument invoice : invoiceList) {
            if (invoice.isOpenInvoiceIndicator()) {
                Collection<CustomerCreditMemoDocument> creditMemos = service.getCustomerCreditMemoDocumentByInvoiceDocument(invoice.getDocumentNumber());
                for (CustomerCreditMemoDocument doc : creditMemos) {
                    doc.populateCustomerCreditMemoDetailsAfterLoad();
                    CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(doc.getDocumentHeader(), doc.getInvoice().getAccountsReceivableDocumentHeader().getProcessingOrganization(), "Credit Memo");
                    details.add(detail);
                }
                CustomerStatementDetailReportDataHolder detail = new CustomerStatementDetailReportDataHolder(invoice.getDocumentHeader(), invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(), "Invoice");
                details.add(detail);
            }
            if (invoice.compareTo(ctrlDoc) != 0) {  
                reports.add(generateStatement(invoice.getBillByChartOfAccountCode(), invoice.getBilledByOrganizationCode(), invoice.getAccountsReceivableDocumentHeader().getCustomerNumber(), invoice.getAccountsReceivableDocumentHeader().getProcessingOrganization(), details));
                details.clear();
            }
            ctrlDoc = invoice;
        }
        return reports;
    }

    /**
     * 
     * This method...
     * @param city
     * @param state
     * @param zipCode
     * @return
     */
    private String generateCityStateZipLine(String city, String state, String zipCode) {
        StringBuffer cityStateZip = new StringBuffer(city);
        cityStateZip.append(", ").append(state);
        cityStateZip.append("  ").append(zipCode);
        return cityStateZip.toString();
    }

    /**
     * 
     * This method...
     * @param details
     * @param invoiceMap
     */
    private void calculateAgingAmounts(List<CustomerStatementDetailReportDataHolder> details, Map<String, String> invoiceMap) {
        KualiDecimal total0to30 = KualiDecimal.ZERO;
        KualiDecimal total31to60 = KualiDecimal.ZERO;
        KualiDecimal total61to90 = KualiDecimal.ZERO;
        KualiDecimal total91toSYSPR = KualiDecimal.ZERO;
        KualiDecimal totalSYSPRplus1orMore = KualiDecimal.ZERO;

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        String nbrDaysForLastBucket = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerAgingReportDetail.class, "CUSTOMER_INVOICE_AGE");     // ArConstants.CUSTOMER_INVOICE_AGE); // default is 120

        Date today = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        java.util.Date reportRunDate = dateTimeService.getCurrentDate();
        java.util.Date cutoffdate30 = DateUtils.addDays(reportRunDate, -30);
        java.util.Date cutoffdate60 = DateUtils.addDays(reportRunDate, -60);
        java.util.Date cutoffdate90 = DateUtils.addDays(reportRunDate, -90);
        java.util.Date cutoffdate120 = DateUtils.addDays(reportRunDate, -1*Integer.parseInt(nbrDaysForLastBucket));
        
        Map<String, Object> knownCustomers = new HashMap<String, Object>(details.size());

        KualiDecimal totalbalance = new KualiDecimal(0.00);
        CustomerAgingReportDetail custDetail = null;
        String previousCustomerNumber = "";
        int detailnum=0;
        // iterate over all invoices consolidating balances for each customer
        for(CustomerStatementDetailReportDataHolder csdrdh : details) {
            CustomerInvoiceDocumentService customerInvoiceDocumentService = SpringContext.getBean(CustomerInvoiceDocumentService.class);
            CustomerInvoiceDocument ci = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(csdrdh.getDocumentNumber());
            for (CustomerInvoiceDetail cid : customerInvoiceDocumentService.getCustomerInvoiceDetailsForCustomerInvoiceDocument(ci)) {
                String invoiceDocumentNumber = cid.getDocumentNumber();
                CustomerInvoiceDocument custInvoice = SpringContext.getBean(CustomerInvoiceDocumentService.class).getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
                Date approvalDate;
                detailnum++;
                approvalDate=custInvoice.getBillingDate(); // use this if above isn't set since this is never null
                if (ObjectUtils.isNull(approvalDate)) {
                    continue;
                }
    
                if(custInvoice!=null && SpringContext.getBean(CustomerInvoiceDetailService.class).getOpenAmount(cid).isNonZero()) {   
    
                    Customer customerobj = custInvoice.getCustomer();                        
                    String customerNumber = customerobj.getCustomerNumber();    // tested and works
                    String customerName = customerobj.getCustomerName();  // tested and works
    
                    if (knownCustomers.containsKey(customerNumber)) { 
                        custDetail = (CustomerAgingReportDetail) knownCustomers.get(customerNumber);
                    } else {
                        custDetail = new CustomerAgingReportDetail();
                        custDetail.setCustomerName(customerName);
                        custDetail.setCustomerNumber(customerNumber);
                        knownCustomers.put(customerNumber, custDetail);
                    }
                    if (!approvalDate.after(reportRunDate) && !approvalDate.before(cutoffdate30)) {                                
                        custDetail.setUnpaidBalance0to30(cid.getOpenAmount().add(custDetail.getUnpaidBalance0to30()));
                        total0to30 = total0to30.add(cid.getOpenAmount());
                    }
                    if (approvalDate.before(cutoffdate30) && !approvalDate.before(cutoffdate60)) {               
                        custDetail.setUnpaidBalance31to60(cid.getOpenAmount().add(custDetail.getUnpaidBalance31to60()));
                        total31to60 = total31to60.add(cid.getOpenAmount());
                    }
                    if (approvalDate.before(cutoffdate60) && !approvalDate.before(cutoffdate90)) {
                        custDetail.setUnpaidBalance61to90(cid.getOpenAmount().add(custDetail.getUnpaidBalance61to90()));
                        total61to90 = total61to90.add(cid.getOpenAmount());
                    }
                    if (approvalDate.before(cutoffdate90) && !approvalDate.before(cutoffdate120)) {
                        custDetail.setUnpaidBalance91toSYSPR(cid.getOpenAmount().add(custDetail.getUnpaidBalance91toSYSPR()));
                        total91toSYSPR = total91toSYSPR.add(cid.getOpenAmount());
                    }
                    if (approvalDate.before(cutoffdate120)) {
                        custDetail.setUnpaidBalanceSYSPRplus1orMore(cid.getOpenAmount().add(custDetail.getUnpaidBalanceSYSPRplus1orMore()));
                        totalSYSPRplus1orMore = totalSYSPRplus1orMore.add(cid.getOpenAmount());
                    }            
                }        
            } 
        }
        
        invoiceMap.put("total0to30", total0to30.toString());
        invoiceMap.put("total31to60", total31to60.toString());
        invoiceMap.put("total61to90", total61to90.toString());
        invoiceMap.put("total90plus", total91toSYSPR.toString());
        
    }
    
    /**
     * Initializes the dateTimeService attribute. 
     * 
     * TODO: Probably should replace this with getters and setters and have the values assigned by Spring injection
     */
    public void initializeDateTimeService() {
        if(dateTimeService==null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
    }
    
}
