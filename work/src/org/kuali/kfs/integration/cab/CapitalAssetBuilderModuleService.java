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
package org.kuali.kfs.integration.cab;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.purap.businessobject.AccountsPayableItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public interface CapitalAssetBuilderModuleService {

    /**
     * Validates the capital asset field requirements based on system parameter and chart for individual system type. This also
     * calls validations for quantity on locations equal quantity on line items, validates that the transaction type allows asset
     * number and validates the non quantity driven allowed indicator.
     * 
     * @param systemState
     * @param capitalAssetItems
     * @param chartCode
     * @param documentType
     * @return
     */
    public boolean validateIndividualCapitalAssetSystemFromPurchasing(String systemState, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType);

    /**
     * Validates the capital asset field requirements based on system parameter and chart for one system type. This also calls
     * validations that the transaction type allows asset number and validates the non quantity driven allowed indicator.
     * 
     * @param systemState
     * @param capitalAssetSystems
     * @param capitalAssetItems
     * @param chartCode
     * @param documentType
     * @return
     */
    public boolean validateOneSystemCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType);

    /**
     * Validates the capital asset field requirements based on system parameter and chart for multiple system type. This also calls
     * validations that the transaction type allows asset number and validates the non quantity driven allowed indicator.
     * 
     * @param systemState
     * @param capitalAssetSystems
     * @param capitalAssetItems
     * @param chartCode
     * @param documentType
     * @return
     */
    public boolean validateMultipleSystemsCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems, List<PurchasingCapitalAssetItem> capitalAssetItems, String chartCode, String documentType);

//FIXME: not called anywhere, is this still needed (was returning false in impl
//    /**
//     * Validates whether transaction type is allowed for the given subtypes. Validates that the object codes must be either all
//     * capital or all expense.
//     * 
//     * @param accountingLines The accounting lines to be validated.
//     * @param transactionType The transaction type to be validated.
//     * @return boolean true if the transaction type is allowed for the given subtypes and the object codes are either all capital or
//     *         expense.
//     */
//    public boolean validateAccounts(List<SourceAccountingLine> accountingLines, String transactionType);

    /**
     * validate the capitalAssetManagementAsset data associated with the given accounting document
     * 
     * @param accountingDocument the given accounting document
     * @param capitalAssetManagementAsset data to be validated
     * @return validation succeeded or errors present
     */
    public boolean validateFinancialProcessingData(AccountingDocument accountingDocument, CapitalAssetInformation capitalAssetInformation);

    // Methods moved from PurchasingDocumentRuleBase

    public boolean validateItemCapitalAssetWithErrors(RecurringPaymentType recurringPaymentType, PurApItem item, boolean apoCheck);
//FIXME: not needed delete after testing
//    public boolean validateItemCapitalAssetWithWarnings(RecurringPaymentType recurringPaymentType, PurApItem item);

    public boolean validateAccountingLinesNotCapitalAndExpense(HashSet<String> capitalOrExpenseSet, String itemIdentifier, ObjectCode objectCode);

    public boolean warningObjectLevelCapital(List<PurApItem> items);
    
    public boolean validateLevelCapitalAssetIndication(BigDecimal unitPrice, ObjectCode objectCode, String itemIdentifier);
    
    public boolean validateObjectCodeVersusTransactionType(ObjectCode objectCode, CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType, String itemIdentifier, boolean quantityBasedItem);

    public boolean validateCapitalAssetTransactionTypeVersusRecurrence(CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType, RecurringPaymentType recurringPaymentType, String itemIdentifier);

    public boolean isCapitalAssetObjectCode(ObjectCode oc);

    public List<CapitalAssetBuilderAssetTransactionType> getAllAssetTransactionTypes();

    public String getValueFromAvailabilityMatrix(String fieldName, String systemType, String systemState);

    /**
     * External modules can notify CAB if a document changed its route status. CAB Uses this notification to release records or to
     * update other modules about the changes
     * 
     * @param documentHeader DocumentHeader
     */
    public void notifyRouteStatusChange(DocumentHeader documentHeader);

    public boolean doesItemNeedCapitalAsset(PurApItem item);

    public boolean validateUpdateCAMSView(List<PurApItem> purapItems);

    public boolean validateAccountsPayableItems(List<PurApItem> apItems);

    public boolean validateAddItemCapitalAssetBusinessRules(ItemCapitalAsset asset);

    public boolean validateCapitalAssetsForAutomaticPurchaseOrderRule(List<PurApItem> itemList);

    /**
     * determine whether there is any object code of the given source accounting lines with a capital asset object sub type
     * 
     * @param accountingLines the given source accounting lines
     * @return true if there is at least one object code of the given source accounting lines with a capital asset object sub type;
     *         otherwise, false
     */
    public boolean hasCapitalAssetObjectSubType(AccountingDocument accountingDocument);

}
