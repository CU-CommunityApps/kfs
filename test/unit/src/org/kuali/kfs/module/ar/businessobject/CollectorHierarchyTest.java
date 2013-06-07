/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.LinkedList;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.document.MaintenanceDocumentTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests CollectionHierarchy Maintenance Document
 */
@ConfigureContext(session = UserNameFixture.khuntley)
public class CollectorHierarchyTest extends KualiTestBase {
    
    public MaintenanceDocument document;
    public DocumentService documentService;
    public static final Class<MaintenanceDocument> DOCUMENT_CLASS = MaintenanceDocument.class;
    private CollectorHierarchy collectorHierarchy;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument("CLHR");
        document.getDocumentHeader().setDocumentDescription("Test Document");
        documentService = SpringContext.getBean(DocumentService.class);
        
        collectorHierarchy = new CollectorHierarchy();
        collectorHierarchy.setPrincipalId("1499309452");
        
        // set Collector Information list
        CollectorInformation collectorInformation = new CollectorInformation();
        collectorInformation.setPrincipalId("1204808831");
        
        List list = new LinkedList();
        list.add(collectorInformation);
        
        collectorHierarchy.setCollectorInformations(list);
        
        document.getNewMaintainableObject().setBusinessObject(collectorHierarchy);
        document.getNewMaintainableObject().setBoClass(collectorHierarchy.getClass());
    }

    /**
     * This method calls testSaveDocument method.
     * 
     * @throws Exception
     */
    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void testSaveDocument() throws Exception {
        MaintenanceDocumentTestUtils.testSaveDocument(document, documentService);
    }

    /**
     * This method tests getNewDocument() method on Collector Hierarchy maintenance document.
     * 
     * @throws Exception
     */
    public void testGetNewDocument() throws Exception {
        Document document = (Document) documentService.getNewDocument("CLHR");
        // verify document was created
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());
    }
}
