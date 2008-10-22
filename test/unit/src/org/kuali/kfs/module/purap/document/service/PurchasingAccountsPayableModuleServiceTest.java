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
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocumentTest;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.DocumentService;

@ConfigureContext(session = KHUNTLEY)
public class PurchasingAccountsPayableModuleServiceTest extends KualiTestBase {
    
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public void testAddAssignedAssetNumbers() {
        Integer purchaseOrderNumber = null;
        PurchaseOrderDocumentTest documentTest = new PurchaseOrderDocumentTest();
        try {
            PurchaseOrderDocument poDocument = documentTest.buildSimpleDocument();
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            poDocument.prepareForSave();       
            AccountingDocumentTestUtils.saveDocument(poDocument, documentService);
            PurchaseOrderDocument result = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocument.getDocumentNumber());
            purchaseOrderNumber = result.getPurapDocumentIdentifier();
        } 
        catch (Exception e) {
            assertTrue(false);
        }
        List<Long> assetNumbers = new ArrayList<Long>();
        assetNumbers.add(new Long("12345"));
        assetNumbers.add(new Long("12346"));
        SpringContext.getBean(PurchasingAccountsPayableModuleService.class).addAssignedAssetNumbers(purchaseOrderNumber, assetNumbers);
        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(purchaseOrderNumber);
        if( po == null ) {
            assertTrue(false);
        }
        List<Note> boNotes = po.getBoNotes();
        boolean hasNote = false;
        for( Note note : boNotes ) {
            if (note.getNoteText().contains("Asset Numbers have been created for this document:")) {
                hasNote = true;
                break;
            }
        }
        assertTrue(hasNote);
    }
    
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public void testGetPurchaseOrderInquiryUrl() {
        Integer purchaseOrderNumber = null;
        PurchaseOrderDocumentTest documentTest = new PurchaseOrderDocumentTest();
        try {
            PurchaseOrderDocument poDocument = documentTest.buildSimpleDocument();
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            poDocument.prepareForSave();       
            AccountingDocumentTestUtils.saveDocument(poDocument, documentService);
            PurchaseOrderDocument result = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(poDocument.getDocumentNumber());
            purchaseOrderNumber = result.getPurapDocumentIdentifier();
        } 
        catch (Exception e) {
            assertTrue(false);
        }
        String url = SpringContext.getBean(PurchasingAccountsPayableModuleService.class).getPurchaseOrderInquiryUrl(purchaseOrderNumber);
        assertFalse(StringUtils.isEmpty(url));
    }
}
