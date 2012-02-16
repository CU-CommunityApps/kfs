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
package org.kuali.kfs.module.tem.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

@ConfigureContext(session = khuntley)
public class TravelReimbursementServiceTest extends KualiTestBase {
    private static final int EXPENSE_AMOUNT = 100;
    private TravelReimbursementDocument tr = null;
    private TravelerDetail traveler = null;

    private TravelReimbursementService trService;
    private DocumentService documentService;

    private static final Logger LOG = Logger.getLogger(TravelReimbursementServiceTest.class);

    @Before
    public void setUp() throws Exception {
        super.setUp();
        // setup services
        trService = SpringContext.getBean(TravelReimbursementService.class);
        documentService = SpringContext.getBean(DocumentService.class);

        tr = DocumentTestUtils.createDocument(documentService, TravelReimbursementDocument.class);
        documentService.prepareWorkflowDocument(tr);

        // setup traveler
        traveler = new TravelerDetail() {
            public void refreshReferenceObject(String referenceObjectName) {
                // do nothing
            }
        };
        traveler.setTravelerTypeCode(TemConstants.EMP_TRAVELER_TYP_CD);
        traveler.setCustomer(new Customer());
        tr.setTraveler(traveler);
    }

    @After
    public void tearDown() throws Exception {
        trService = null;
        super.tearDown();
    }

    /**
     * This method test {@link TravelReimbursementService#findByTravelId(Integer)} using travelDocumentIdentifier
     */
    @Test
    public void testFindByTravelDocumentIdentifier() throws WorkflowException {
        documentService.saveDocument(tr);
        
        // test find for non existent travelDocumentIdentifier
        List<TravelReimbursementDocument> result = (List<TravelReimbursementDocument>) trService.findByTravelId("-1");
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        // test find for existing travelDocument
        result = (List<TravelReimbursementDocument>) trService.find(tr.getTravelDocumentIdentifier());
        assertNotNull(result);
        assertFalse(result.isEmpty());        
    }

    /**
     * This method test {@link TravelReimbursementService#find(String)} using documentNumber
     */
    @Test
    public void testFindByDocumentNumber() throws WorkflowException {        
        // test find for non existent documentNumber
        assertNull(trService.find(tr.getDocumentHeader().getDocumentNumber()));
        
        // test find for existent documentNumber
        documentService.saveDocument(tr);
        assertNotNull(trService.find(tr.getDocumentHeader().getDocumentNumber()));
    }

    /**
     * This method tests {@link TravelReimbursementService#addListenersTo(TravelReimbursementDocument)}
     */
    @Test
    public void testAddListenersTo() {
        boolean success = false;

        try {
            trService.addListenersTo(tr);
            success = true;
        }
        catch (NullPointerException e) {
            success = false;
            LOG.warn("NPE.", e);
        }

        assertTrue(success);
    }

    /**
     * This method tests {@link TravelReimbursementService#spawnCashControlDocumentFrom(TravelReimbursementDocument)}
     * 
     * @throws WorkflowException
     */
    @Test
    public void testSpawnCashControlDocumentFrom() throws WorkflowException {
        tr.setTraveler(traveler);

        boolean businessRuleValidation = true;
        try {
            trService.spawnCashControlDocumentFrom(tr);
        }
        catch (ValidationException e) {
            businessRuleValidation = false;
            LOG.warn("Business rule eval failed.", e);
        }

        assertFalse(businessRuleValidation);
    }

    /**
     * This method tests
     * {@link TravelReimbursementService#notifyDateChangedOn(TravelReimbursementDocument, java.util.Date, java.util.Date)}
     * 
     * @throws Exception
     */
    @Test
    public void testNotifyDateChangedOn() throws Exception {
        boolean success = false;

        try {
            trService.notifyDateChangedOn(tr, new java.util.Date(), new java.util.Date());
            success = true;
        }
        catch (NullPointerException e) {
            success = false;
            LOG.warn("NPE.", e);
        }

        assertFalse(success);

        try {
            tr.setTripBegin(new Timestamp(new java.util.Date().getTime()));
            tr.setTripEnd(new Timestamp(new java.util.Date().getTime()));

            trService.notifyDateChangedOn(tr, new java.util.Date(), new java.util.Date());
            success = true;
        }
        catch (NullPointerException e) {
            success = false;
            LOG.warn("NPE.", e);
        }

        assertTrue(success);
    }

    /**
     * This method tests {@link TravelReimbursementService#generateCoversheetFor(TravelReimbursementDocument)}
     * 
     * @throws Exception
     */
    @Test
    public void testGenerateCoversheetFor() throws Exception {
        Coversheet cover = null;

        try {
            cover = trService.generateCoversheetFor(new TravelReimbursementDocument());
        }
        catch (RuntimeException e) {
            LOG.warn("Workflow doc is null.", e);
        }

        assertNull(cover);

        cover = trService.generateCoversheetFor(tr);
        assertNotNull(cover);
    }
}
