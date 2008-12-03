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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCommodityCodeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;

/**
 * This class is used to create and test populated Requisition Documents
 * that are not eligible to become APO. 
 * One of the ineligibility criterias is if the APO Limit is null.
 * However, currently there is no known way to simulate this criteria
 * yet, so we'll skip this particular criteria for now. 
 * All the other criterias for APO ineligibility are already added in this class.
 * The methods here tested are in RequisitionService.
 * 
 * @see org.kuali.kfs.module.purap.document.service.RequisitionServiceTest
 */
@ConfigureContext(session = khuntley)
public class NegativeAPOTest extends KualiTestBase {

    private RequisitionService reqService;
    private KualiConfigurationService kualiConfigurationService;
    private UnitTestSqlDao unitTestSqlDao;
    
    private RequisitionDocument requisitionDocument = null;

    protected void setUp() throws Exception {
        super.setUp();
        if (null == reqService) {
            reqService = SpringContext.getBean(RequisitionService.class);
        }
        if (null == kualiConfigurationService) {
            kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        }
        if (null == unitTestSqlDao) {
            unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        }
    }

    protected void tearDown() throws Exception {
        requisitionDocument = null;
        super.tearDown();
    }

     //Requisition total > APO Limit.
     public void testInvalidAPOTotalGreaterThanAPOLimitNoRouting() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_NO_APO_VALID.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_TOTAL_GREATER_THAN_APO_LIMIT);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }
     }
     
     //Requisition total <= 0.
     public void testInvalidAPOTotalNotGreaterThanZero() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_TOTAL_NOT_GREATER_THAN_ZERO.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));      
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_TOTAL_NOT_GREATER_THAN_ZERO);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }
     }
     
     //Requisition contains restricted item.
     public void testInvalidAPORequisitionContainsRestrictedItem() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_CONTAINS_RESTRICTED_ITEM.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));       
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_CONTAINS_RESTRICTED_ITEM);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }         
     }
     
     //Vendor was not selected from the vendor database.
     public void testInvalidAPOVendorNotSelected() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));    
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_VENDOR_NOT_SELECTED_FROM_VENDOR_DATABASE);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }             
     }
     
     //Missing some of the vendor address fields that are required in Purchase Order.
     public void testInvalidAPOMissingVendorAddressFields() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
         requisitionDocument.setVendorCityName(null);
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));    
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_MISSING_SOME_VENDOR_ADDRESS_FIELDS);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }    
     }
     
     //Error retrieving vendor from database.
     public void testInvalidAPOVendorNotInDB() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_ERROR_RETRIEVING_VENDOR_FROM_DATABASE.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));                 
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_ERROR_RETRIEVING_VENDOR_FROM_DATABASE);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     //Selected Vendor has restricted indicator equals to true.
     public void testInvalidAPOVendorIsRestricted() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_WITH_RESTRICTED_VENDOR.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));   
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_SELECTED_VENDOR_IS_RESTRICTED);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     //Payment type is marked as recurring.
     public void testInvalidAPOPaymentTypeRecurring() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_PAYMENT_TYPE_RECURRING.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));      
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_PAYMENT_TYPE_IS_RECURRING);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     //PO Total Limit is not null and not zero.
     public void testInvalidAPOPOTotalLimitNonzero() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_PO_TOTAL_LIMIT_NON_ZERO.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument)); 
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_PO_TOTAL_LIMIT_IS_NOT_EMPTY);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     // Requisition contains additional suggested vendor names.
     @ConfigureContext(session = khuntley, shouldCommitTransactions=false)
     public void testInvalidAPOHasAlternateVendorName() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_ALTERNATE_VENDOR_NAMES.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));        
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_CONTAINS_ALTERNATE_VENDOR_NAMES);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }           
     }
     
     // Requisition has failed capital asset rules.
     public void testInvalidAPOCapitalAssetFailure() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_FAILS_CAPITAL_ASSET_RULE.createRequisitionDocument();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument)); 
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_FAILS_CAPITAL_ASSET_RULES);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     // Requisition is set to encumber next fiscal year and approval is not within APO allowed date range.
     /*public void testInvalidAPOApprovalOutsideAllowedDateRange() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_APPROVAL_OUTSIDE_ALLOWED_DATE_RANGE.createRequisitionDocument();
         
         String sql = "UPDATE SH_UNIV_DATE_T";
                  sql += "SET UNIV_FISCAL_YR='2006'";
         unitTestSqlDao.sqlCommand(sql);
         
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument)); 
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_FAILS_CAPITAL_ASSET_RULES);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }*/
     
     // Requisition contains inactive commodity codes.
     public void testInvalidAPOHasInactiveCommodityCode() throws Exception {
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
         RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_APO_INACTIVE_COMMODITY_CODE;
         RequisitionDocument requisitionDocument = fixture.createRequisitionDocument();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_CONTAINS_INACTIVE_COMMODITY_CODE);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     // Requisition contains an item that is marked as sensitive data.
     public void testInvalidAPOHasCommodityCodeWithSensitiveData() throws Exception {
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
         RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_APO_COMMODITY_CODE_WITH_SENSITIVE_DATA;
         RequisitionDocument requisitionDocument = fixture.createRequisitionDocument();
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_COMMODITY_CODE_WITH_SENSITIVE_DATA);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     // There are missing commodity code(s).
     public void testInvalidAPOMissingCommodityCode() throws Exception {
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
         //This fixture is intentionally used because it is an otherwise valid fixture for APO, except
         //that it does not contain commodity code, which should fail the APO criteria.
         RequisitionDocumentFixture fixture = RequisitionDocumentFixture.REQ_APO_VALID;
         RequisitionDocument requisitionDocument = fixture.createRequisitionDocument();
         requisitionDocument.refreshReferenceObject("vendorDetail");
         assertFalse(reqService.isAutomaticPurchaseOrderAllowed(requisitionDocument));
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = kualiConfigurationService.getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_MISSING_COMMODITY_CODE);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
}

