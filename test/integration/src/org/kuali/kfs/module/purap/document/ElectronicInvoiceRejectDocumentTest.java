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
package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.APPLETON;
import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoadSummary;
import org.kuali.kfs.module.purap.fixture.ElectronicInvoiceLoadSummaryFixture;
import org.kuali.kfs.module.purap.fixture.ElectronicInvoiceRejectDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Used to create and test populated Requisition Documents of various kinds.
 */
@ConfigureContext(session = APPLETON)
public class ElectronicInvoiceRejectDocumentTest extends KualiTestBase {
    public static final Class<ElectronicInvoiceRejectDocument> DOCUMENT_CLASS = ElectronicInvoiceRejectDocument.class;

    private ElectronicInvoiceRejectDocument eirDoc = null;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        eirDoc = null;
        super.tearDown();
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
//        ElectronicInvoiceLoadSummary eils = ElectronicInvoiceLoadSummaryFixture.EILS_BASIC.createElectronicInvoiceLoadSummary();
//        BusinessObjectService boService =  KNSServiceLocator.getBusinessObjectService();
//        boService.save(eils);
//
//        eirDoc = ElectronicInvoiceRejectDocumentFixture.EIR_ONLY_REQUIRED_FIELDS.createElectronicInvoiceRejectDocument(eils);
//        eirDoc.prepareForSave();
//
//        
//        DocumentService documentService = SpringContext.getBean(DocumentService.class);
//        assertFalse("R".equals(eirDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
//        saveDocument(eirDoc, "saving copy source document", documentService);
//        Document document = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
//        assertTrue("Document should  be saved.", document.getDocumentHeader().getWorkflowDocument().stateIsSaved());
//        Document result = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
//        assertMatch(eirDoc, result);
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions = false)
    public final void testRouteDocument() throws Exception {
//        ElectronicInvoiceLoadSummary eils = ElectronicInvoiceLoadSummaryFixture.EILS_BASIC.createElectronicInvoiceLoadSummary();
//        BusinessObjectService boService =  KNSServiceLocator.getBusinessObjectService();
//        boService.save(eils);
//        
//        eirDoc = ElectronicInvoiceRejectDocumentFixture.EIR_ONLY_REQUIRED_FIELDS.createElectronicInvoiceRejectDocument(eils);
//        eirDoc.prepareForSave();
//        
//        DocumentService documentService = SpringContext.getBean(DocumentService.class);
//        assertFalse("R".equals(eirDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
//        routeDocument(eirDoc, "saving copy source document", documentService);
//        WorkflowTestUtils.waitForStatusChange(eirDoc.getDocumentHeader().getWorkflowDocument(), KEWConstants.ROUTE_HEADER_FINAL_CD);
//        Document result = documentService.getByDocumentHeaderId(eirDoc.getDocumentNumber());
//        assertTrue("Document should  be final.", result.getDocumentHeader().getWorkflowDocument().stateIsFinal());
    }

    @ConfigureContext(session = APPLETON, shouldCommitTransactions = false)
    public final void testRouteDocumentToFinal() throws Exception {
//        eirDoc = ElectronicInvoiceRejectDocumentFixture.EIR_ONLY_REQUIRED_FIELDS.createElectronicInvoiceRejectDocument();

//        final String docId = requisitionDocument.getDocumentNumber();
//        AccountingDocumentTestUtils.routeDocument(requisitionDocument, SpringContext.getBean(DocumentService.class));
//        WorkflowTestUtils.waitForNodeChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
//
//        // the document should now be routed to VPUTMAN as Fiscal Officer
//        changeCurrentUser(RORENFRO);
//        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
//        assertTrue("At incorrect node.", WorkflowTestUtils.isAtNode(requisitionDocument, ACCOUNT_REVIEW));
//        assertTrue("Document should be enroute.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
//        assertTrue("RORENFRO should have an approve request.", requisitionDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
//        SpringContext.getBean(DocumentService.class).approveDocument(requisitionDocument, "Test approving as RORENFRO", null);
//
//        WorkflowTestUtils.waitForStatusChange(requisitionDocument.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);
//
//        changeCurrentUser(KHUNTLEY);
//        requisitionDocument = (RequisitionDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
//        assertTrue("Document should now be final.", requisitionDocument.getDocumentHeader().getWorkflowDocument().stateIsFinal());
    }


    /**
     * Helper method to route the document.
     * 
     * @param document                 The assign contract manager document to be routed.
     * @param annotation               The annotation String.
     * @param documentService          The service to use to route the document.
     * @throws WorkflowException
     */
    private void routeDocument(Document document, String annotation, DocumentService documentService) throws WorkflowException {
        try {
            documentService.routeDocument(document, annotation, null);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getErrorMap());
        }
    }
    
    /**
     * Helper method to route the document.
     * 
     * @param document                 The assign contract manager document to be routed.
     * @param annotation               The annotation String.
     * @param documentService          The service to use to route the document.
     * @throws WorkflowException
     */
    private void saveDocument(Document document, String annotation, DocumentService documentService) throws WorkflowException {
        try {
            documentService.saveDocument(document);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getErrorMap());
        }
    }

    public static <T extends Document> void assertMatch(T document1, T document2) {
        Assert.assertEquals(document1.getDocumentNumber(), document2.getDocumentNumber());
        Assert.assertEquals(document1.getDocumentHeader().getWorkflowDocument().getDocumentType(), document2.getDocumentHeader().getWorkflowDocument().getDocumentType());


        ElectronicInvoiceRejectDocument d1 = (ElectronicInvoiceRejectDocument) document1;
        ElectronicInvoiceRejectDocument d2 = (ElectronicInvoiceRejectDocument) document2;
        Assert.assertEquals(d1.getInvoiceFileName(), d2.getInvoiceFileName());
        Assert.assertEquals(d1.getVendorDunsNumber(), d2.getVendorDunsNumber());
        Assert.assertEquals(d1.getInvoiceRejectItems().size(), d2.getInvoiceRejectItems().size());
        Assert.assertEquals(d1.getInvoiceRejectReasons().size(), d2.getInvoiceRejectReasons().size());

//        for (int i = 0; i < d1.getInvoiceRejectItems().size(); i++) {
//            d1.getInvoiceRejectItem(i).isLike(d2.getInvoiceRejectItem(i));
//        }
//        for (int i = 0; i < d1.getInvoiceRejectReasons().size(); i++) {
//            d1.getInvoiceRejectReason(i).isLike(d2.getInvoiceRejectReason(i));
//        }
    }

}
