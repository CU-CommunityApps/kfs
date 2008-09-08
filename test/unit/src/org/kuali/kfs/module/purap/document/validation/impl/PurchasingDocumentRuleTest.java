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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjLevel;
import org.kuali.kfs.coa.businessobject.ObjSubTyp;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.businessobject.CapitalAssetTransactionType;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.DeliveryRequiredDateFixture;
import org.kuali.kfs.module.purap.fixture.ItemFieldsFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentWithCommodityCodeFixture;
import org.kuali.kfs.module.purap.fixture.PurchasingCapitalAssetFixture;
import org.kuali.kfs.module.purap.fixture.RecurringPaymentBeginEndDatesFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCommodityCodeFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class contains tests of the rule validation methods present in PurchasingDocumentRuleBase. These should include any tests
 * that test functionality that is common to all Purchasing documents.
 */
@ConfigureContext(session = KHUNTLEY)
public class PurchasingDocumentRuleTest extends PurapRuleTestBase {

    PurchasingDocumentRuleBase rules;

    protected void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setMessageList(new ArrayList<String>());
        rules = new PurchasingDocumentRuleBase();
    }

    protected void tearDown() throws Exception {
        rules = null;
        super.tearDown();
    }

    /**
     * These methods test how the method validating the input to the Payment Info tab on Purchasing documents,
     * PurchasingDocumentRuleBase.processPaymentInfoValidation, works for Requisitions and POs with different combinations of
     * beginning and ending dates, fiscal years, and recurring payment types.
     */
    public void testProcessPaymentInfoValidation_Req_RightOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_RIGHT_ORDER.populateDocument();
        assertTrue(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_Req_WrongOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_WRONG_ORDER.populateDocument();
        assertFalse(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_Req_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_SEQUENTIAL_NEXT_FY.populateDocument();
        assertTrue(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_Req_Non_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.REQ_NON_SEQUENTIAL_NEXT_FY.populateDocument();
        assertFalse(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_PO_RightOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_RIGHT_ORDER.populateDocument();
        assertTrue(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_PO_WrongOrder() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_WRONG_ORDER.populateDocument();
        assertFalse(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_PO_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_SEQUENTIAL_NEXT_FY.populateDocument();
        assertTrue(rules.processPaymentInfoValidation(document));
    }

    public void testProcessPaymentInfoValidation_PO_Non_Sequential_Next_FY() {
        PurchasingDocument document = RecurringPaymentBeginEndDatesFixture.PO_NON_SEQUENTIAL_NEXT_FY.populateDocument();
        assertFalse(rules.processPaymentInfoValidation(document));
    }
    
    /**
     * Validates that if the delivery required date is not entered on a requisition,
     * it will pass the rule.
     */
    public void testProcessDeliveryValidation_Req_No_DeliveryRequiredDate() {
        PurchasingDocument document = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a requisition is equal to the
     * current date, it will pass the rule.
     */
    public void testProcessDeliveryValidation_Req_DeliveryRequiredDateCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_EQUALS_CURRENT_DATE.createRequisitionDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a requisition is before the
     * current date, it will not pass the rule.
     */
    public void testProcessDeliveryValidation_Req_DeliveryRequiredDateBeforeCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_BEFORE_CURRENT_DATE.createRequisitionDocument();
        assertFalse(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a requisition is after the
     * current date, it will pass the rule.
     */
    public void testProcessDeliveryValidation_Req_DeliveryRequiredDateAfterCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_AFTER_CURRENT_DATE.createRequisitionDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date is not entered on a purchase order,
     * it will pass the rule.
     */
    public void testProcessDeliveryValidation_PO_No_DeliveryRequiredDate() {
        PurchasingDocument document = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a purchase order is equal to the
     * current date, it will pass the rule.
     */
    public void testProcessDeliveryValidation_PO_DeliveryRequiredDateCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_EQUALS_CURRENT_DATE.createPurchaseOrderDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a purchase order is before the
     * current date, it will not pass the rule.
     */
    public void testProcessDeliveryValidation_PO_DeliveryRequiredDateBeforeCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_BEFORE_CURRENT_DATE.createPurchaseOrderDocument();
        assertFalse(rules.processDeliveryValidation(document));
    }
    
    /**
     * Validates that if the delivery required date on a purchase order is after the
     * current date, it will pass the rule.
     */
    public void testProcessDeliveryValidation_PO_DeliveryRequiredDateAfterCurrent() {
        PurchasingDocument document = DeliveryRequiredDateFixture.DELIVERY_REQUIRED_AFTER_CURRENT_DATE.createPurchaseOrderDocument();
        assertTrue(rules.processDeliveryValidation(document));
    }
    
    // Tests of validateItemQuantity
    
    public void testValidateItemQuantity_WithQuantity_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertTrue(rules.validateItemQuantity(item));
    }
    
    public void testValidateItemQuantity_WithoutQuantity_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.NO_QUANTITY_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertFalse(rules.validateItemQuantity(item));
    }
    
    public void testValidateItemQuantity_WithQuantity_Service() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_SERVICE.populateRequisitionItem();
        assertFalse(rules.validateItemQuantity(item));
    }
    
    public void testValidateItemQuantity_WithoutQuantity_Service() {
        RequisitionItem item = ItemFieldsFixture.NO_QUANTITY_ABOVE_SERVICE.populateRequisitionItem();
        assertTrue(rules.validateItemQuantity(item));
    }
    
    // Tests of validateUnitOfMeasure
    
    public void testValidateUnitOfMeasure_WithUOM_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertTrue(rules.validateUnitOfMeasure(item));
    }
    
    public void testValidateUnitOfMeasure_WithoutUOM_QuantityBased() {
        RequisitionItem item = ItemFieldsFixture.NO_UOM_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertFalse(rules.validateUnitOfMeasure(item));
    }
    
    public void testValidateUnitOfMeasure_WithoutUOM_Service() {
        RequisitionItem item = ItemFieldsFixture.NO_UOM_ABOVE_SERVICE.populateRequisitionItem();
        assertTrue(rules.validateUnitOfMeasure(item));
    }    
    
    // Tests of validateItemDescription
    
    public void testValidateItemDescription_WithDescription_Above() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertTrue(rules.validateItemDescription(item));
    }
        
    public void testValidateItemDescription_WithoutDescription_Above() {
        RequisitionItem item = ItemFieldsFixture.NO_DESC_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertFalse(rules.validateItemDescription(item));
    }
    
    // Tests of validateCommodityCodes.
    
    /**
     * Tests that, if a commodity code is not entered on the item, but the system parameter
     * requires the item to have commodity code, it will give validation error about
     * the commodity code is required.
     * 
     * @throws Exception
     */
    public void testMissingCommodityCodeWhenRequired() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_NO_APO_VALID;
        rules.processItemValidation(reqFixture.createRequisitionDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.ERROR_REQUIRED));
        assertTrue(GlobalVariables.getErrorMap().fieldHasMessage("document.item[0]." + PurapPropertyConstants.ITEM_COMMODITY_CODE, KFSKeyConstants.ERROR_REQUIRED));
        GlobalVariables.getErrorMap().clear();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentFixture poFixture = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS;
        rules.processItemValidation(poFixture.createPurchaseOrderDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.ERROR_REQUIRED));
        assertTrue(GlobalVariables.getErrorMap().fieldHasMessage("document.item[0]." + PurapPropertyConstants.ITEM_COMMODITY_CODE, KFSKeyConstants.ERROR_REQUIRED));
    
    }
    
    /**
     * Tests that, if a valid and active commodity code is entered and is required according to the 
     * system parameter, the validation should return true (successful).
     * 
     * @throws Exception
     */
    public void testValidActiveCommodityCode() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_VALID_ACTIVE_COMMODITY_CODE;
        assertTrue(rules.processItemValidation(fixture.createRequisitionDocument()));
        GlobalVariables.getErrorMap().clear();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_VALID_ACTIVE_COMMODITY_CODE;
        assertTrue(rules.processItemValidation(poFixture.createPurchaseOrderDocument()));
    }
    
    /**
     * Tests that, if a commodity code entered on the item is inactive, it will give validation error
     * about inactive commodity code.
     * 
     * @throws Exception
     */
    public void testInactiveCommodityCodeValidation() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_VALID_INACTIVE_COMMODITY_CODE;
        rules.processItemValidation(fixture.createRequisitionDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE));
        GlobalVariables.getErrorMap().clear();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_VALID_INACTIVE_COMMODITY_CODE;
        rules.processItemValidation(poFixture.createPurchaseOrderDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INACTIVE));
    }
    
    /**
     * Tests that, if a commodity code entered on the item has not existed yet in the database, it will give 
     * validation error about invalid commodity code.
     * 
     * @throws Exception
     */
    public void testNonExistenceCommodityCodeValidation() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_NON_EXISTENCE_COMMODITY_CODE;
        rules.processItemValidation(fixture.createRequisitionDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INVALID));
        GlobalVariables.getErrorMap().clear();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentWithCommodityCodeFixture poFixture = PurchaseOrderDocumentWithCommodityCodeFixture.PO_NON_EXISTENCE_COMMODITY_CODE;
        rules.processItemValidation(poFixture.createPurchaseOrderDocument());
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(PurapKeyConstants.PUR_COMMODITY_CODE_INVALID));
        
    }
    
    // Tests of validateItemUnitPrice
    
    public void testValidateItemUnitPrice_Positive_Above() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_ABOVE_QUANTITY_BASED.populateRequisitionItem();
        assertTrue(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Negative_Above() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_QUANTITY_BASED.populateRequisitionItem();
        assertFalse(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Positive_Discount() {
        RequisitionItem item = ItemFieldsFixture.POSITIVE_UNIT_PRICE_DISCOUNT.populateRequisitionItem();
        assertFalse(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Negative_Discount() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_DISCOUNT.populateRequisitionItem();
        assertTrue(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Positive_TradeIn() {
        RequisitionItem item = ItemFieldsFixture.POSITIVE_UNIT_PRICE_TRADEIN.populateRequisitionItem();
        assertFalse(rules.validateItemUnitPrice(item));
    }
    
    public void testValidateItemUnitPrice_Negative_TradeIn() {
        RequisitionItem item = ItemFieldsFixture.NEGATIVE_UNIT_PRICE_TRADEIN.populateRequisitionItem();
        assertTrue(rules.validateItemUnitPrice(item));
    }
    
    // Tests of validateBelowTheLineItemNoUnitCost
    
    public void testValidateBelowTheLineItemNoUnitCost_WithUnitCost() {
        RequisitionItem item = ItemFieldsFixture.ALL_FIELDS_BELOW.populateRequisitionItem();
        List<PurApAccountingLine> accountingLines = new ArrayList<PurApAccountingLine>();
        accountingLines.add(new RequisitionAccount());
        item.setSourceAccountingLines(accountingLines);
        assertTrue(rules.validateBelowTheLineItemNoUnitCost(item));
    }
    
    public void testValidateBelowTheLineItemNoUnitCost_NoUnitCost() {
        RequisitionItem item = ItemFieldsFixture.NO_UNIT_PRICE_BELOW.populateRequisitionItem();
        List<PurApAccountingLine> accountingLines = new ArrayList<PurApAccountingLine>();
        accountingLines.add(new RequisitionAccount());
        item.setSourceAccountingLines(accountingLines);
        assertFalse(rules.validateBelowTheLineItemNoUnitCost(item));
    }
}
