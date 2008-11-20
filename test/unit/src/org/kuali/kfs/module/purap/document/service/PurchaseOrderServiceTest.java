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
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kuluser;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentWithCommodityCodeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;


@ConfigureContext(session = kuluser, shouldCommitTransactions=true)
public class PurchaseOrderServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceTest.class);

    private DocumentService docService;
    private PurchaseOrderService poService;

    @Override
    protected void setUp() throws Exception {      
        super.setUp();
        if (null == docService) {
            docService = SpringContext.getBean(DocumentService.class);
        }
        if (null == poService) {
            poService = SpringContext.getBean(PurchaseOrderService.class);
        }
    }


    @ConfigureContext(session = kuluser, shouldCommitTransactions=true)
    public final void testPurchaseOrderRetransmit() throws Exception {   
        // Create and save a minimally-populated basic PO document for each test.
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        po.refreshNonUpdateableReferences();
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);

        PurchaseOrderDocument poRetrans = null;
        try {
            poRetrans = poService.createAndSavePotentialChangeDocument(
                    po.getDocumentNumber(), 
                    PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, 
                    PurchaseOrderStatuses.PENDING_RETRANSMIT);  
            po = poService.getPurchaseOrderByDocumentNumber(po.getDocumentNumber());
        }
        catch (ValidationException ve) {
            throw new ValidationException(GlobalVariables.getErrorMap().toString() + ve);
        }      
        assertMatchRetransmit(po, poRetrans); 
        ((PurchaseOrderItem)poRetrans.getItem(0)).setItemSelectedForRetransmitIndicator(true);
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            DateTimeService dtService = SpringContext.getBean(DateTimeService.class);
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(poRetrans.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(dtService.getCurrentDate().getTime());
            sbFilename.append(".pdf");
            poService.retransmitPurchaseOrderPDF(poRetrans, baosPDF);
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }
    
    @ConfigureContext(session = kuluser, shouldCommitTransactions=true)
    public final void testPurchaseOrderPrint() throws Exception {        
        PurchaseOrderDocument po = 
            PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS_MULTI_ITEMS.createPurchaseOrderDocument();
        po.setStatusCode(PurchaseOrderStatuses.OPEN);
        po.refreshNonUpdateableReferences();
        po.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(po, docService);
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            DateTimeService dtService = SpringContext.getBean(DateTimeService.class);
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(dtService.getCurrentDate().getTime());
            sbFilename.append(".pdf");
            poService.performPrintPurchaseOrderPDFOnly(po.getDocumentNumber(), baosPDF);
            assertTrue(baosPDF.size()>0);
        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
    }

    
    @ConfigureContext(session = kuluser, shouldCommitTransactions=true)
    public final void testGetInternalPurchasingDollarLimit() {
        PurchaseOrderDocument po = PurchaseOrderDocumentFixture.PO_WITH_VENDOR_CONTRACT.createPurchaseOrderDocument();

        VendorContract contract = new VendorContract();
        Integer contrID = po.getVendorContractGeneratedIdentifier();
        KualiDecimal contrAPOLimit = new KualiDecimal(100000.00);
        contract.setVendorContractGeneratedIdentifier(contrID);
        contract.setOrganizationAutomaticPurchaseOrderLimit(contrAPOLimit);
        
        ContractManager manager = new ContractManager();
        Integer mngrCode = po.getContractManagerCode();
        manager.setContractManagerCode(mngrCode);
        KualiDecimal mngrDDLimit = new KualiDecimal(15000.00);  
        manager.setContractManagerDelegationDollarLimit(mngrDDLimit);

        VendorService vdService = SpringContext.getBean(VendorService.class);
        String chartCode = po.getChartOfAccountsCode();
        String orgCode = po.getOrganizationCode();
        KualiDecimal contrDLimit = vdService.getApoLimitFromContract(contrID, chartCode, orgCode);
        KualiDecimal result;

        // contract == null & manager != null
        po.setContractManager(manager);
        result = poService.getInternalPurchasingDollarLimit(po);
        assertEquals(result, mngrDDLimit);
        
        // contract != null & manager == null
        po.setVendorContract(contract);
        po.setContractManager(null);
        result = poService.getInternalPurchasingDollarLimit(po);
        assertEquals(result, contrDLimit);
        
        // contract != null & manager != null, and contract limit > manager limit
        po.setVendorContract(contract);
        po.setContractManager(manager);
        result = poService.getInternalPurchasingDollarLimit(po);
        assertEquals(result, contrDLimit);
        
        // contract != null & manager != null, and contract limit < manager limit
        po.setVendorContract(contract);
        po.setContractManager(manager);
        KualiDecimal mngrDDLimit1 = new KualiDecimal(150000.00);  
        manager.setContractManagerDelegationDollarLimit(mngrDDLimit1);
        result = poService.getInternalPurchasingDollarLimit(po);
        assertEquals(result, mngrDDLimit1);
    }
        
    /**
     * Matches an existing Purchase Order Document with a retransmited PO Document newly generated from it;
     * Fails the assertion if any of the copied persistent fields don't match.
     */
    public static void assertMatchRetransmit(PurchaseOrderDocument doc1, PurchaseOrderDocument doc2) {
        // match posting year
        if (StringUtils.isNotBlank(doc1.getPostingPeriodCode()) && StringUtils.isNotBlank(doc2.getPostingPeriodCode())) {
            assertEquals(doc1.getPostingPeriodCode(), doc2.getPostingPeriodCode());
        }
        assertEquals(doc1.getPostingYear(), doc2.getPostingYear());
    
        // match important fields in PO       
        assertEquals(doc1.getVendorHeaderGeneratedIdentifier(), doc2.getVendorHeaderGeneratedIdentifier());
        assertEquals(doc1.getVendorDetailAssignedIdentifier(), doc2.getVendorDetailAssignedIdentifier());
        assertEquals(doc1.getVendorName(), doc2.getVendorName());
        assertEquals(doc1.getVendorNumber(), doc2.getVendorNumber());
    
        assertEquals(doc1.getChartOfAccountsCode(), doc2.getChartOfAccountsCode());
        assertEquals(doc1.getOrganizationCode(), doc2.getOrganizationCode());
        assertEquals(doc1.getDeliveryCampusCode(), doc2.getDeliveryCampusCode());
        assertEquals(doc1.getDeliveryRequiredDate(), doc2.getDeliveryRequiredDate());
        assertEquals(doc1.getRequestorPersonName(), doc2.getRequestorPersonName());
        assertEquals(doc1.getContractManagerCode(), doc2.getContractManagerCode());
        assertEquals(doc1.getVendorContractName(), doc2.getVendorContractName());
        assertEquals(doc1.getPurchaseOrderAutomaticIndicator(), doc2.getPurchaseOrderAutomaticIndicator());
        assertEquals(doc1.getPurchaseOrderTransmissionMethodCode(), doc2.getPurchaseOrderTransmissionMethodCode());
    
        assertEquals(doc1.getRequisitionIdentifier(), doc2.getRequisitionIdentifier());
        assertEquals(doc1.getPurchaseOrderPreviousIdentifier(), doc2.getPurchaseOrderPreviousIdentifier());
        assertEquals(doc1.getPurchaseOrderCreateTimestamp(), doc2.getPurchaseOrderCreateTimestamp());
    }    
    
    /**
     * Tests that the PurchaseOrderService would attempt to update vendor with missing commodity codes.
     * 
     */
    @ConfigureContext(session = kuluser, shouldCommitTransactions=false)
    public void testUpdateVendorCommodityCode() {
        PurchaseOrderDocument po = PurchaseOrderDocumentWithCommodityCodeFixture.PO_VALID_ACTIVE_COMMODITY_CODE_WITH_VENDOR_COMMODITY_CODE.createPurchaseOrderDocument();
        
        VendorDetail updatedVendor = poService.updateVendorWithMissingCommodityCodesIfNecessary(po);
        
        assertFalse(updatedVendor == null);

        if (updatedVendor != null) {
            boolean foundMatching = false;
            for (VendorCommodityCode vcc : updatedVendor.getVendorCommodities()) {
                if (vcc.getPurchasingCommodityCode().equals(((PurchaseOrderItem) po.getItem(0)).getPurchasingCommodityCode())) {
                    foundMatching = true;
                    break;
                }
            }

            assertTrue(foundMatching);
        }
    }
    
}

