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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.ThresholdFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class ThresholdRuleTest extends PurapRuleTestBase {
    
    private ThresholdRule thresholdRule;
    
    public ThresholdRuleTest() {
        super();
    }
    
    @Override
    protected void setUp()
    throws Exception {
        super.setUp();
        thresholdRule = new ThresholdRule();
    }
    
    public final void testThreshold_ChartOfAccount(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccount_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_INVALID);
        assertFalse(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndAccountType(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_ACCOUNTTYPE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndSubAccountType(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_SUBACCOUNTTYPE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndSubAccountType_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_SUBACCOUNTTYPE_INVALID);
        assertFalse(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndAccountTypeAndSubAccountType(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_ACCOUNTTYPE_AND_SUBACCOUNTTYPE);
        assertFalse(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndCommodityCode(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_COMMODITYCODE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndCommodityCode_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_COMMODITYCODE_INVALID);
        assertFalse(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndObjectCode(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_OBJECTCODE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndObjectCode_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_OBJECTCODE_INVALID);
        assertFalse(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndOrgCode(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_ORGCODE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndOrgCode_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_ORGCODE_INVALID);
        assertFalse(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndVendorNumber(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_VENDORNUMBER);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }
    
    public final void testThreshold_ChartOfAccountAndVendorNumber_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_VENDORNUMBER_INVALID);
        assertFalse(thresholdRule.processSaveDocument(doc));
    }
    
    private MaintenanceDocumentBase getMaintenanceDocument(ThresholdFixture thresholdFixture){
        MaintenanceDocumentBase doc = null;
        try {
            doc = (MaintenanceDocumentBase) SpringContext.getBean(DocumentService.class).getNewDocument(PurapConstants.RECEIVING_THRESHOLD_DOCUMENT_TYPE);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        doc.getDocumentHeader().setExplanation("JUnit test document");
        Maintainable maintainableDoc = doc.getNewMaintainableObject();
        maintainableDoc.setBusinessObject(thresholdFixture.getThresholdBO());
        return doc;
    }
    
}

