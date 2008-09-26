/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document;


import static org.kuali.kfs.sys.fixture.UserNameFixture.APPLETON;

import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.UserNameFixture.RJWEISS;
import static org.kuali.kfs.sys.fixture.UserNameFixture.RORENFRO;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.fixture.CreditMemoDocumentFixture;
import org.kuali.kfs.module.purap.fixture.CreditMemoItemFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;

/**
 * This class is used to create and test populated CreditMemo Documents of various kinds.
 */
@ConfigureContext(session = APPLETON)
public class CreditMemoDocumentTest extends KualiTestBase {
    public static final Class<CreditMemoDocument> DOCUMENT_CLASS = CreditMemoDocument.class;
    private static final String ACCOUNT_REVIEW = "Account Review";
    
    private RequisitionDocument requisitionDocument = null;
    private CreditMemoDocument creditMemoDocument = null;
   
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    protected void tearDown() throws Exception {
        creditMemoDocument = null;
        super.tearDown();      
    }
    
    private List<CreditMemoItemFixture> getItemParametersFromFixtures() {
        List<CreditMemoItemFixture> list = new ArrayList<CreditMemoItemFixture>();
        list.add(CreditMemoItemFixture.CM_ITEM_NO_APO);
        return list;
    }

    private int getExpectedPrePeCount() {
        return 0;
    }
    
    public final void testAddItem() throws Exception {
        List<AccountsPayableItem> items = new ArrayList<AccountsPayableItem>();
        items.add(CreditMemoItemFixture.CM_ITEM_NO_APO.createCreditMemoItem());

        int expectedItemTotal = items.size();
        AccountsPayableDocumentTestUtils.testAddItem((AccountsPayableDocument)DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        creditMemoDocument = buildSimpleDocument();
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(creditMemoDocument, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {       
        creditMemoDocument = buildSimpleDocument();       
        creditMemoDocument.setAccountsPayableProcessorIdentifier("khuntley");
        //creditMemoDocument.setPurchaseOrderIdentifier(createAPORequisition());
        AccountingDocumentTestUtils.testSaveDocument(creditMemoDocument, SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final CreditMemoDocument routeDocument(PaymentRequestDocument preqDocument) throws Exception {
        creditMemoDocument = buildSimpleDocument();
        creditMemoDocument.setPaymentRequestDocument(preqDocument);
        CreditMemoItem cmItem = (CreditMemoItem) creditMemoDocument.getItem(0);
        cmItem.setPreqInvoicedTotalQuantity(new KualiDecimal(1));
        cmItem.setItemQuantity(new KualiDecimal(1));    
        cmItem.setPreqTotalAmount(new KualiDecimal(1));
        SpringContext.getBean(CreditMemoService.class).calculateCreditMemo(creditMemoDocument);
        creditMemoDocument.prepareForSave();       
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        assertFalse("R".equals(creditMemoDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        AccountingDocumentTestUtils.routeDocument(creditMemoDocument, "saving copy source docu ament", null, documentService);
        WorkflowTestUtils.waitForStatusChange(creditMemoDocument.getDocumentHeader().getWorkflowDocument(), "F");    
        return creditMemoDocument;
    }

    /*
    @ConfigureContext(session = APPLETON, shouldCommitTransactions=true)
    public final void testRouteDocumentToFinal() throws Exception {
        // To pass validation, the Credit Memo must be associated with another PO or PREQ.
        PurchaseOrderDocumentTest poDocTest = new PurchaseOrderDocumentTest();
        PurchaseOrderDocument po = poDocTest.buildSimpleDocument();
        po.refreshNonUpdateableReferences();
        AccountingDocumentTestUtils.saveDocument(po,SpringContext.getBean(DocumentService.class));
        String poId = po.getDocumentNumber();
        po = (PurchaseOrderDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poId);
        Integer purchaseOrderIdentifier = po.getPurapDocumentIdentifier();
        
        // Create and save the Credit Memo.
        creditMemoDocument = buildSimpleDocument();
        creditMemoDocument.setPurchaseOrderIdentifier(purchaseOrderIdentifier);
        CreditMemoItem creditMemoItem = (CreditMemoItem)creditMemoDocument.getItemByLineNumber(1);
        creditMemoItem.setPoExtendedPrice(new KualiDecimal(1.00));
        creditMemoItem.setPoInvoicedTotalQuantity(creditMemoItem.getItemQuantity());    
        creditMemoDocument.prepareForSave();
        AccountingDocumentTestUtils.saveDocument(creditMemoDocument, SpringContext.getBean(DocumentService.class));
        
        // Process and Calculate.
        //SpringContext.getBean(AccountsPayableService.class).updateItemList(creditMemoDocument);
        SpringContext.getBean(CreditMemoCreateService.class).populateDocumentAfterInit(creditMemoDocument);
        SpringContext.getBean(CreditMemoService.class).calculateCreditMemo(creditMemoDocument);
        assertFalse("R".equals(creditMemoDocument.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        
        // Route and test.
        AccountingDocumentTestUtils.routeDocument(creditMemoDocument, "routing document", null, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForStatusChange(creditMemoDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);
        final String docId = creditMemoDocument.getDocumentNumber();
        creditMemoDocument = (CreditMemoDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        assertTrue("Document should now be final.", creditMemoDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
    }
    */

    public CreditMemoDocument buildSimpleDocument() throws Exception {
        return CreditMemoDocumentFixture.CM_ONLY_REQUIRED_FIELDS.createCreditMemoDocument();
    }
    
    private UserNameFixture getInitialUserName() {
        return RJWEISS;
    }

    protected UserNameFixture getTestUserName() {
        return RORENFRO;
    }
    
    /*
    private Integer createAPORequisition() throws Exception {
        requisitionDocument = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        final String docId = requisitionDocument.getDocumentNumber();
        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
        WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
       
        changeCurrentUser(RORENFRO);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as RORENFRO", null);

        WorkflowTestUtils.waitForStatusChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(KHUNTLEY);
        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        //assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
        // get related POs, if count = 1 then all is well
        Integer linkIdentifier = requisitionDocument.getAccountsPayablePurchasingDocumentLinkIdentifier();
        List<PurchaseOrderView> relatedPOs = SpringContext.getBean(PurapService.class).getRelatedViews(PurchaseOrderView.class, linkIdentifier);
        Integer POId = relatedPOs.get(0).getPurapDocumentIdentifier();

        assertNotNull(relatedPOs);
        assertTrue(relatedPOs.size() > 0);
        return POId;
    }
    */
}
