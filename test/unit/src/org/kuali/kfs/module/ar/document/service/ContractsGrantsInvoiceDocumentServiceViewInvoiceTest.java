/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.fixture.ContractsGrantsInvoiceDocumentFixture;
import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the ContractsGrantsInvoiceDocumentService
 */
@ConfigureContext(session = khuntley)
public class ContractsGrantsInvoiceDocumentServiceViewInvoiceTest extends KualiTestBase {

    protected static final String PRINCIPAL_ID1 = "6164904958"; //orton
    protected static final String PRINCIPAL_ID2 = "6165100953"; //fkirklan
    protected static final String PRINCIPAL_ID3 = "6170904981"; //cotowle
    protected static final String PRINCIPAL_ID4 = "6172204597"; //hykrug
    protected static final String PRINCIPAL_ID5 = "6176006856"; //mylovela
    protected static final String PRINCIPAL_ID6 = "6176204753"; //syfredri

    protected static final String CUSTOMER_NAME_STARTING_LETTER = "A";
    protected static final String CUSTOMER_NAME_ENDING_LETTER = "B";

    private RoleService roleService;
    private DocumentService documentService;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    private ContractsGrantsInvoiceDocument invoice;


    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentService = SpringContext.getBean(DocumentService.class);
        roleService = SpringContext.getBean(RoleService.class);
        contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        invoice = ContractsGrantsInvoiceDocumentFixture.CG_INV_DOC1.createContractsGrantsInvoiceDocument(documentService);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(invoice.getDocumentNumber());
        invoice.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        Map<String, String> qualifications = new HashMap<String, String>(4);
        qualifications.put(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, "BL");
        qualifications.put(ArKimAttributes.BILLING_ORGANIZATION_CODE, "BL");
        qualifications.put(ArKimAttributes.CUSTOMER_NAME_STARTING_LETTER, "A");
        qualifications.put(ArKimAttributes.CUSTOMER_NAME_ENDING_LETTER, "B");
        roleService.assignPrincipalToRole(PRINCIPAL_ID1, ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualifications);
        qualifications = new HashMap<String, String>(2);
        qualifications.put(ArKimAttributes.BILLING_CHART_OF_ACCOUNTS_CODE, "BL");
        qualifications.put(ArKimAttributes.BILLING_ORGANIZATION_CODE, "ACAC");
        roleService.assignPrincipalToRole(PRINCIPAL_ID2, ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualifications);

        qualifications = new HashMap<String, String>(4);
        qualifications.put(ArKimAttributes.PROCESSING_CHART_OF_ACCOUNTS_CODE, "BL");
        qualifications.put(ArKimAttributes.PROCESSING_ORGANIZATION_CODE, "BL");
        qualifications.put(ArKimAttributes.CUSTOMER_NAME_STARTING_LETTER, "A");
        qualifications.put(ArKimAttributes.CUSTOMER_NAME_ENDING_LETTER, "B");
        roleService.assignPrincipalToRole(PRINCIPAL_ID4, ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualifications);
        qualifications = new HashMap<String, String>(2);
        qualifications.put(ArKimAttributes.PROCESSING_CHART_OF_ACCOUNTS_CODE, "BL");
        qualifications.put(ArKimAttributes.PROCESSING_ORGANIZATION_CODE, "ACAC");
        roleService.assignPrincipalToRole(PRINCIPAL_ID5, ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualifications);
}

    public void testCanViewInvoiceBilling() {
        invoice.setCustomerName("ABERCROMBIE");
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAD");
        assertTrue("should have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID1));
        invoice.setBillByChartOfAccountCode(null);
        invoice.setBilledByOrganizationCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID1));
    }

    public void testCannotViewInvoiceCustomerNameDoesNotMatchBilling() {
        invoice.setCustomerName("COLUMBIA");
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID1));
        invoice.setBillByChartOfAccountCode(null);
        invoice.setBilledByOrganizationCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID1));
    }

    public void testCannotViewInvoiceCustomerChartDoesNotMatchBilling() {
        invoice.setCustomerName("ABERCROMBIE");
        invoice.setBillByChartOfAccountCode("EA");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID1));
        invoice.setBillByChartOfAccountCode(null);
        invoice.setBilledByOrganizationCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("EA");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID1));
    }

    public void testCanViewInvoice2Billing() {
        invoice.setCustomerName("COLUMBIA");
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAC");
        assertTrue("should have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID2));
        invoice.setBillByChartOfAccountCode(null);
        invoice.setBilledByOrganizationCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAC");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID2));
    }

    public void testCannotViewInvoiceCustomerOrgDoesNotMatchBilling() {
        invoice.setCustomerName("ABERCROMBIE");
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID2));
        invoice.setBillByChartOfAccountCode(null);
        invoice.setBilledByOrganizationCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID2));
    }

    public void testCannotViewInvoiceCustomerDoesNotHaveRoleBilling() {
        invoice.setCustomerName("ABERCROMBIE");
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID3));
        invoice.setBillByChartOfAccountCode(null);
        invoice.setBilledByOrganizationCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID3));
    }

    public void testCanViewInvoiceProcessing() {
        invoice.setCustomerName("ABERCROMBIE");
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertTrue("should have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID4));
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode(null);
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID4));
    }

    public void testCannotViewInvoiceCustomerNameDoesNotMatchProcessing() {
        invoice.setCustomerName("COLUMBIA");
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID4));
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode(null);
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID4));
    }

    public void testCannotViewInvoiceCustomerChartDoesNotMatchProcessing() {
        invoice.setCustomerName("ABERCROMBIE");
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("EA");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID4));
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode(null);
        invoice.setBillByChartOfAccountCode("EA");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID4));
    }

    public void testCanViewInvoice2Processing() {
        invoice.setCustomerName("COLUMBIA");
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAC");
        assertTrue("should have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID5));
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode(null);
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAC");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID5));
    }

    public void testCannotViewInvoiceCustomerOrgDoesNotMatchProcessing() {
        invoice.setCustomerName("ABERCROMBIE");
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID5));
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode(null);
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID5));
    }

    public void testCannotViewInvoiceCustomerDoesNotHaveRoleProcessing() {
        invoice.setCustomerName("ABERCROMBIE");
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode("BL");
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID6));
        invoice.getAccountsReceivableDocumentHeader().setProcessingChartOfAccountCode(null);
        invoice.getAccountsReceivableDocumentHeader().setProcessingOrganizationCode(null);
        invoice.setBillByChartOfAccountCode("BL");
        invoice.setBilledByOrganizationCode("ACAD");
        assertFalse("should not have been able to view invoice", contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, PRINCIPAL_ID6));
    }

}