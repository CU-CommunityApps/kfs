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
package org.kuali.kfs.module.cab.document.service;

import java.util.List;

import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;


/**
 * This class declares methods used by CAB PurAp Line process
 */
public interface PurApLineService {

    /**
     * Changes percent quantities to a quantity of 1 for selected line item.
     * 
     * @param itemAsset Selected line item.
     */
    void processPercentPayment(PurchasingAccountsPayableItemAsset itemAsset);

    /**
     * Split the selected line item quantity and create a new line item.
     * 
     * @param itemAsset Selected line item.
     * @return
     */
    PurchasingAccountsPayableItemAsset processSplit(PurchasingAccountsPayableItemAsset itemAsset);

    /**
     * Save purApDoc, item assets and account lines for persistence
     * 
     * @param purApLineForm form
     */
    void processSaveBusinessObjects(PurApLineForm purApLineForm);

    /**
     * Set Purchasing order email address and contact phone from PurAp.
     * 
     * @param purApLineForm form
     */
    void setPurchaseOrderInfo(PurApLineForm purApLineForm);

    /**
     * Build PurAp document collection and line item collection.
     * 
     * @param purApLineForm form
     */
    void buildPurApItemAssetsList(PurApLineForm purApLineForm);

    /**
     * Handle additional charge allocate in the same document.
     * 
     * @param purLineForm
     */
    boolean processAllocate(PurchasingAccountsPayableItemAsset selectedLineItem, List<PurchasingAccountsPayableItemAsset> allocateTargetLines);

    /**
     * Get the target lines based on allocation line type
     * 
     * @param selectedLineItem
     * @param purApForm
     * @return
     */
    List<PurchasingAccountsPayableItemAsset> getAllocateTargetLines(PurchasingAccountsPayableItemAsset selectedLineItem, PurApLineForm purApForm);

    /**
     * Get the selected merge lines.
     * 
     * @param purApLineForm
     * @return
     */
    List<PurchasingAccountsPayableItemAsset> getSelectedMergeLines(PurApLineForm purApLineForm);

    /**
     * Reset selectedValue for all line items
     * 
     * @param selectedItems
     */
    void resetSelectedValue(PurApLineForm purApForm);

    /**
     * Merge line items.
     * 
     * @param mergeLines
     */
    void processMerge(List<PurchasingAccountsPayableItemAsset> mergeLines);

    /**
     * Check if the merge action is merge all.
     * 
     * @param purApLineForm
     * @return
     */
    public boolean isMergeAllAction(PurApLineForm purApLineForm);

    /**
     * For line items in itemAssets if they are not in the same PurAp document, check if there is pending additional charges
     * allocation.
     * 
     * @param itemAssets
     * @return
     */
    boolean isAdditionalChargePending(List<PurchasingAccountsPayableItemAsset> itemAssets);

    /**
     * Check if there is TI indicator exists in the given itemAssets List.
     * 
     * @param itemAssets
     * @return
     */
    boolean isTradeInIndicatorExist(List<PurchasingAccountsPayableItemAsset> itemAssets);

    /**
     * Check if there is trade-in allowance not allocated yet.
     * 
     * @param purApLineForm
     * @return
     */
    boolean isTradeInAllowanceExist(PurApLineForm purApLineForm);
}
