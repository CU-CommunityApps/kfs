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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.businessobject.Pretag;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableActionHistory;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.dataaccess.PurApLineDao;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.PurApLineSession;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
@Transactional
public class PurApLineServiceImpl implements PurApLineService {
    private static final Logger LOG = Logger.getLogger(PurApLineServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private PurApLineDao purApLineDao;
    private PurApInfoService purApInfoService;


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineDocumentService#inActivateDocument(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument)
     */
    public void inActivateDocument(PurchasingAccountsPayableDocument selectedDoc) {
        for (PurchasingAccountsPayableItemAsset item : selectedDoc.getPurchasingAccountsPayableItemAssets()) {
            if (item.isActive()) {
                return;
            }
        }
        selectedDoc.setActive(false);
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#resetSelectedValue(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void resetSelectedValue(PurApLineForm purApLineForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                item.setSelectedValue(false);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processAdditionalChargeAllocate(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public boolean processAllocate(PurchasingAccountsPayableItemAsset allocateSourceLine, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, PurApLineForm purApForm, PurApLineSession purApLineSession) {
        boolean allocatedIndicator = true;
        // setup non-persistent relationship: from account to item and from item to document
        setupObjectRelationship(purApForm);
        // indicator of additional charge allocation
        boolean allocateAddlChrgIndicator = allocateSourceLine.isAdditionalChargeNonTradeInIndicator() | allocateSourceLine.isTradeInAllowance();
        // Maintain this account List for update. So accounts already allocated won't take effect for account not allocated yet.
        List<PurchasingAccountsPayableLineAssetAccount> newAccountList = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        List<PurchasingAccountsPayableActionHistory> actionsTakeHistory = new ArrayList<PurchasingAccountsPayableActionHistory>();

        // For each account in the source item, allocate it to the target items.
        for (PurchasingAccountsPayableLineAssetAccount sourceAccount : allocateSourceLine.getPurchasingAccountsPayableLineAssetAccounts()) {
            // Get allocate to target account list
            List<PurchasingAccountsPayableLineAssetAccount> targetAccounts = getAllocateTargetAccounts(sourceAccount, allocateTargetLines, allocateAddlChrgIndicator);
            if (!targetAccounts.isEmpty()) {
                // Percentage amount to each target account
                allocateByItemAccountAmount(sourceAccount, targetAccounts, newAccountList, actionsTakeHistory);
            }
            else {
                allocatedIndicator = false;
                break;
            }
        }

        if (allocatedIndicator) {
            postAllocateProcess(allocateSourceLine, allocateTargetLines, purApForm, newAccountList);
            // add to the action history
            purApLineSession.getActionsTakenHistory().addAll(actionsTakeHistory);
        }

        return allocatedIndicator;
    }


    /**
     * Setup relationship from account to item and item to doc. In this way, we keep all working objects in the same view as form.
     * 
     * @param purApForm
     */
    private void setupObjectRelationship(PurApLineForm purApForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                item.setPurchasingAccountsPayableDocument(purApDoc);
                for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
                    account.setPurchasingAccountsPayableItemAsset(item);
                }
            }
        }
    }

    /**
     * Process after allocate.
     * 
     * @param selectedLineItem
     * @param allocateTargetLines
     * @param purApForm
     * @param newAccountList
     * @param actionsTake
     */
    private void postAllocateProcess(PurchasingAccountsPayableItemAsset selectedLineItem, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, PurApLineForm purApForm, List<PurchasingAccountsPayableLineAssetAccount> newAccountList) {
        // add new account into each item list
        addNewAccountToItemList(newAccountList);

        // update total cost and unit cost.
        updateLineItemsCost(allocateTargetLines);

        if (ObjectUtils.isNotNull(selectedLineItem.getPurchasingAccountsPayableDocument())) {
            // remove allocate source line item.
            selectedLineItem.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets().remove(selectedLineItem);
            // when an line is removed from the document, we should check if it is the only active line in the document and
            // in-activate document if yes.
            inActivateDocument(selectedLineItem.getPurchasingAccountsPayableDocument());
        }

        // Adjust create asset and apply payment indicator only when allocate additional charges.
        if (selectedLineItem.isAdditionalChargeNonTradeInIndicator() || selectedLineItem.isTradeInAllowance()) {
            setAssetIndicator(purApForm);
        }

        // uncheck select check box
        resetSelectedValue(purApForm);
    }

    /**
     * Reset item total cost and unit cost for each item.
     * 
     * @param allocateTargetLines
     */
    private void updateLineItemsCost(List<PurchasingAccountsPayableItemAsset> lineItems) {
        // update target item unit cost and total cost
        for (PurchasingAccountsPayableItemAsset item : lineItems) {
            setLineItemCost(item);
        }
    }

    /**
     * update account list for each line item
     * 
     * @param updatedAccountList
     */
    private void addNewAccountToItemList(List<PurchasingAccountsPayableLineAssetAccount> newAccountList) {
        PurchasingAccountsPayableItemAsset lineItem = null;
        for (PurchasingAccountsPayableLineAssetAccount newAccount : newAccountList) {
            lineItem = newAccount.getPurchasingAccountsPayableItemAsset();
            if (ObjectUtils.isNotNull(lineItem) && ObjectUtils.isNotNull(lineItem.getPurchasingAccountsPayableLineAssetAccounts())) {
                lineItem.getPurchasingAccountsPayableLineAssetAccounts().add(newAccount);
            }
        }
    }

    /**
     * Set line item total cost and unit cost.
     * 
     * @param item
     */
    private void setLineItemCost(PurchasingAccountsPayableItemAsset item) {
        KualiDecimal totalCost = calculateItemAssetTotalCost(item);
        item.setTotalCost(totalCost);
        setItemAssetUnitCost(item, totalCost);
    }

    /**
     * Allocate one account line to target account lines percentage based on the account line amount.
     * 
     * @param sourceAccount Account line to be allocated.
     * @param targetAccounts Account lines which accept amount.
     */
    private void allocateByItemAccountAmount(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableLineAssetAccount> targetAccounts, List<PurchasingAccountsPayableLineAssetAccount> newAccountList, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory) {
        KualiDecimal targetAccountsTotalAmount = KualiDecimal.ZERO;
        KualiDecimal sourceAccountTotalAmount = sourceAccount.getItemAccountTotalAmount();
        KualiDecimal amountAllocated = KualiDecimal.ZERO;
        KualiDecimal additionalAmount = null;

        // Calculate the targetAccountTotalAmount.
        for (PurchasingAccountsPayableLineAssetAccount targetAccount : targetAccounts) {
            targetAccountsTotalAmount = targetAccountsTotalAmount.add(targetAccount.getItemAccountTotalAmount());
        }

        for (Iterator<PurchasingAccountsPayableLineAssetAccount> iterator = targetAccounts.iterator(); iterator.hasNext();) {
            PurchasingAccountsPayableLineAssetAccount targetAccount = (PurchasingAccountsPayableLineAssetAccount) iterator.next();
            if (iterator.hasNext()) {
                // Not working on the last node. Calculate additional charge amount by percentage
                additionalAmount = targetAccount.getItemAccountTotalAmount().multiply(sourceAccountTotalAmount).divide(targetAccountsTotalAmount);
                amountAllocated = amountAllocated.add(additionalAmount);
            }
            else {
                // Working on the last node, set the additional charge amount to the rest of sourceAccountTotalAmount.
                additionalAmount = sourceAccountTotalAmount.subtract(amountAllocated);
            }

            // Code below mainly handle grouping account lines if they're from the same GL.
            PurchasingAccountsPayableLineAssetAccount newAccount = getMatchingFromAccountList(targetAccounts, sourceAccount.getGeneralLedgerAccountIdentifier(), targetAccount);
            if (newAccount != null) {
                // If exists the same account line and GL entry, update its itemAccountTotalAmount. This account line could be other
                // than targetAccount, but must belong to the same line item.
                updateAccountAmount(additionalAmount, newAccount);
            }
            else {
                // If exist account just created, grouping them and update the account amount.
                newAccount = getMatchingFromAccountList(newAccountList, sourceAccount.getGeneralLedgerAccountIdentifier(), targetAccount);
                if (newAccount != null) {
                    updateAccountAmount(additionalAmount, newAccount);
                }
                else {
                    // If the target account is a different GL entry, create a new account and attach to this line item.
                    newAccount = new PurchasingAccountsPayableLineAssetAccount(targetAccount.getPurchasingAccountsPayableItemAsset(), sourceAccount.getGeneralLedgerAccountIdentifier());
                    newAccount.setItemAccountTotalAmount(additionalAmount);
                    newAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.GENERAL_LEDGER_ENTRY);
                    newAccountList.add(newAccount);
                }
            }

            // add to action history
            addAllocateHistory(sourceAccount, actionsTakeHistory, additionalAmount, newAccount);
        }
    }

    /**
     * Save allocate action into session object.
     * 
     * @param sourceAccount
     * @param actionsTakeHistory
     * @param additionalAmount
     * @param newAccount
     */
    private void addAllocateHistory(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory, KualiDecimal additionalAmount, PurchasingAccountsPayableLineAssetAccount newAccount) {
        PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(sourceAccount.getPurchasingAccountsPayableItemAsset(), newAccount.getPurchasingAccountsPayableItemAsset(), CabConstants.Actions.ALLOCATE);
        newAction.setGeneralLedgerAccountIdentifier(sourceAccount.getGeneralLedgerAccountIdentifier());
        newAction.setItemAccountTotalAmount(additionalAmount);
        newAction.setAccountsPayableItemQuantity(sourceAccount.getPurchasingAccountsPayableItemAsset().getAccountsPayableItemQuantity());
        actionsTakeHistory.add(newAction);
    }


    /**
     * Search matching account in targetAccounts by glIdentifier.
     * 
     * @param targetAccounts
     * @param glIdentifier
     * @return
     */
    private PurchasingAccountsPayableLineAssetAccount getFromTargetAccountList(List<PurchasingAccountsPayableLineAssetAccount> targetAccounts, Long glIdentifier) {
        for (PurchasingAccountsPayableLineAssetAccount account : targetAccounts) {
            if (account.getGeneralLedgerAccountIdentifier().equals(glIdentifier)) {
                return account;
            }
        }
        return null;
    }

    /**
     * Update targetAccount by additionalAmount.
     * 
     * @param additionalAmount
     * @param targetAccount
     */
    private void updateAccountAmount(KualiDecimal additionalAmount, PurchasingAccountsPayableLineAssetAccount targetAccount) {
        KualiDecimal baseAmount = targetAccount.getItemAccountTotalAmount();
        targetAccount.setItemAccountTotalAmount(baseAmount != null ? baseAmount.add(additionalAmount) : additionalAmount);
    }


    /**
     * Searching in accountList by glIdentifier for matching account which associated with the same item as targetAccount.
     * 
     * @param accountList
     * @param glIdentifier
     * @param targetAccount
     * @return
     */
    private PurchasingAccountsPayableLineAssetAccount getMatchingFromAccountList(List<PurchasingAccountsPayableLineAssetAccount> accountList, Long glIdentifier, PurchasingAccountsPayableLineAssetAccount targetAccount) {
        for (PurchasingAccountsPayableLineAssetAccount account : accountList) {
            if (StringUtils.equalsIgnoreCase(targetAccount.getDocumentNumber(), account.getDocumentNumber()) && targetAccount.getAccountsPayableLineItemIdentifier().equals(account.getAccountsPayableLineItemIdentifier()) && targetAccount.getCapitalAssetBuilderLineNumber().equals(account.getCapitalAssetBuilderLineNumber()) && glIdentifier.equals(account.getGeneralLedgerAccountIdentifier())) {
                return account;
            }
        }
        return null;
    }

    /**
     * Get the target account lines which will be used for allocate.
     * 
     * @param sourceAccount
     * @param lineItems
     * @param addtionalCharge
     * @return
     */
    private List<PurchasingAccountsPayableLineAssetAccount> getAllocateTargetAccounts(PurchasingAccountsPayableLineAssetAccount sourceAccount, List<PurchasingAccountsPayableItemAsset> allocateTargetLines, boolean addtionalCharge) {
        GeneralLedgerEntry candidateEntry = null;
        GeneralLedgerEntry sourceEntry = sourceAccount.getGeneralLedgerEntry();
        List<PurchasingAccountsPayableLineAssetAccount> matchingAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();
        List<PurchasingAccountsPayableLineAssetAccount> allAccounts = new ArrayList<PurchasingAccountsPayableLineAssetAccount>();

        // For additional charge allocation, target account selection is based on account lines with the same account number and
        // object code. If no matching, select all account lines. For line item to line items, select all account lines as target.
        for (PurchasingAccountsPayableItemAsset item : allocateTargetLines) {
            for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
                candidateEntry = account.getGeneralLedgerEntry();

                if (ObjectUtils.isNotNull(candidateEntry)) {
                    allAccounts.add(account);
                    // For additional charge, select matching account when account number and object code both match.
                    if (addtionalCharge && StringUtils.equalsIgnoreCase(sourceEntry.getAccountNumber(), candidateEntry.getAccountNumber()) && StringUtils.equalsIgnoreCase(sourceEntry.getFinancialObjectCode(), candidateEntry.getFinancialObjectCode())) {
                        matchingAccounts.add(account);
                    }
                }
            }
        }

        return matchingAccounts.isEmpty() ? allAccounts : matchingAccounts;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getAllocateTargetLines(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset,
     *      org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public List<PurchasingAccountsPayableItemAsset> getAllocateTargetLines(PurchasingAccountsPayableItemAsset selectedLineItem, PurApLineForm purApForm) {
        List<PurchasingAccountsPayableItemAsset> targetLineItems = new TypedArrayList(PurchasingAccountsPayableItemAsset.class);

        for (PurchasingAccountsPayableDocument purApDoc : purApForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // If selected Line is additional charge line, get target lines from the same document.
                // Else if selected Line is trade in allowance, target lines are item lines with TI indicator set.
                // Otherwise, select items with check box set.
                if (item != selectedLineItem && ((selectedLineItem.isAdditionalChargeNonTradeInIndicator() && !item.isAdditionalChargeNonTradeInIndicator() && !item.isTradeInAllowance() && StringUtils.equalsIgnoreCase(selectedLineItem.getDocumentNumber(), item.getDocumentNumber())) || (selectedLineItem.isTradeInAllowance() && item.isItemAssignedToTradeInIndicator()) || (item.isSelectedValue()))) {
                    targetLineItems.add(item);
                }
            }
        }
        return targetLineItems;
    }

    /**
     * Get selected merge lines. If this is merge all action, we need to manually append all additional charge lines since no select
     * box associated with them.
     * 
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getSelectedMergeLines(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public List<PurchasingAccountsPayableItemAsset> getSelectedMergeLines(PurApLineForm purApLineForm) {
        List<PurchasingAccountsPayableItemAsset> mergeLines = new TypedArrayList(PurchasingAccountsPayableItemAsset.class);
        // Check if this is in fact a merge all action because there is no check box for additional charges.
        boolean mergeAll = isMergeAllAction(purApLineForm);

        boolean excludeTradeInAllowance = false;

        // Handle one exception for merge all: when we have TI allowance but no TI indicator line, we should exclude
        if (mergeAll && !isTradeInIndicatorExist(purApLineForm) && isTradeInAllowanceExist(purApLineForm)) {
            excludeTradeInAllowance = true;
        }

        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // If not merge all action, select items are the merge lines. If it is merge all action, all lines should be
                // candidate merge lines except trade-in allowance line when there is no trade-in indicator line.
                if ((!mergeAll && item.isSelectedValue()) || (mergeAll && (!excludeTradeInAllowance || !item.isTradeInAllowance()))) {
                    mergeLines.add(item);
                    // setup non-persistent relationship from item to document.
                    item.setPurchasingAccountsPayableDocument(purApDoc);
                }
            }
        }
        return mergeLines;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isTradeInAllowanceExist(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public boolean isTradeInAllowanceExist(PurApLineForm purApLineForm) {
        boolean tradeInAllowance = false;
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if (item.isTradeInAllowance()) {
                    tradeInAllowance = true;
                    break;
                }
            }
        }
        return tradeInAllowance;
    }

    /**
     * Check if TI indicator exists in all form lines
     * 
     * @param purApLineForm
     * @return
     */
    private boolean isTradeInIndicatorExist(PurApLineForm purApLineForm) {
        boolean tradeInIndicator = false;
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if (item.isItemAssignedToTradeInIndicator()) {
                    tradeInIndicator = true;
                    break;
                }
            }
        }
        return tradeInIndicator;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isTradeInIndicatorExist(java.util.List)
     */
    public boolean isTradeInIndicatorExist(List<PurchasingAccountsPayableItemAsset> itemAssets) {
        boolean tradeInIndicator = false;
        for (PurchasingAccountsPayableItemAsset item : itemAssets) {
            if (item.isItemAssignedToTradeInIndicator()) {
                tradeInIndicator = true;
                break;
            }
        }
        return tradeInIndicator;
    }

    /**
     * If item assets are from the same document, we can ignore additional charges pending.
     * 
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isAdditionalChargePending(java.util.List,
     *      org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public boolean isAdditionalChargePending(List<PurchasingAccountsPayableItemAsset> itemAssets) {
        boolean additionalChargePending = false;
        Boolean diffDocment = false;
        PurchasingAccountsPayableItemAsset firstAsset = itemAssets.get(0);
        PurchasingAccountsPayableItemAsset lastAsset = itemAssets.get(itemAssets.size() - 1);

        // Check if itemAssets are in different PurAp Document. itemAssets is a sorted list which has item assets from the same
        // document grouping together.
        if (ObjectUtils.isNotNull(firstAsset) && ObjectUtils.isNotNull(lastAsset) && !firstAsset.getDocumentNumber().equalsIgnoreCase(lastAsset.getDocumentNumber())) {
            diffDocment = true;
        }

        // check if item assets from different document have additional charges not allocated yet. Bring all lines in
        // the same document as checking candidate.
        if (diffDocment) {
            for (PurchasingAccountsPayableItemAsset item : itemAssets) {
                if (ObjectUtils.isNotNull(item.getPurchasingAccountsPayableDocument())) {
                    for (PurchasingAccountsPayableItemAsset itemLine : item.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets()) {
                        if (itemLine.isAdditionalChargeNonTradeInIndicator()) {
                            additionalChargePending = true;
                            return additionalChargePending;
                        }
                    }
                }
            }
        }
        return additionalChargePending;
    }

    /**
     * Check if the merge action is merge all.
     * 
     * @param purApLineForm
     * @return
     */
    public boolean isMergeAllAction(PurApLineForm purApLineForm) {
        boolean mergeAll = true;

        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // When there is one item line not selected, mergeAll is false
                if (!item.isAdditionalChargeNonTradeInIndicator() && !item.isTradeInAllowance() && !item.isSelectedValue()) {
                    mergeAll = false;
                    break;
                }
            }
        }
        return mergeAll;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isAdditionalChargeExist(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public boolean isAdditionalChargeExist(PurApLineForm purApForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                if (item.isAdditionalChargeNonTradeInIndicator()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processMerge(java.util.List)
     */
    public void processMerge(List<PurchasingAccountsPayableItemAsset> mergeLines, PurApLineForm purApForm, PurApLineSession purApLineSession) {
        PurchasingAccountsPayableItemAsset sourceItem = null;
        PurchasingAccountsPayableLineAssetAccount targetAccount = null;
        // use the first item and its accounts as the target
        PurchasingAccountsPayableItemAsset firstItem = mergeLines.get(0);
        List<PurchasingAccountsPayableLineAssetAccount> firstAccountList = firstItem.getPurchasingAccountsPayableLineAssetAccounts();
        // set new quantity and description to the first item
        preMergeProcess(firstItem, purApForm);

        // Merge accounts from the second item to the first one.
        for (int i = 1; i < mergeLines.size(); i++) {
            sourceItem = mergeLines.get(i);
            for (PurchasingAccountsPayableLineAssetAccount account : sourceItem.getPurchasingAccountsPayableLineAssetAccounts()) {
                // Check if we can grouping the accounts. If yes, update the account amount without moving account line.
                targetAccount = getFromTargetAccountList(firstAccountList, account.getGeneralLedgerAccountIdentifier());
                if (targetAccount != null) {
                    updateAccountAmount(account.getItemAccountTotalAmount(), targetAccount);
                }
                else {
                    account.setDocumentNumber(firstItem.getDocumentNumber());
                    account.setAccountsPayableLineItemIdentifier(firstItem.getAccountsPayableLineItemIdentifier());
                    account.setCapitalAssetBuilderLineNumber(firstItem.getCapitalAssetBuilderLineNumber());
                    firstAccountList.add(account);
                }
            }
        }

        // update action history, remove lines before merge and clean up the user input
        postMergeProcess(mergeLines, purApForm, purApLineSession);
    }

    /**
     * Process before merge.
     * 
     * @param firstItem
     * @param purApForm
     */
    private void preMergeProcess(PurchasingAccountsPayableItemAsset firstItem, PurApLineForm purApForm) {
        // Set new value for quantity and description.
        firstItem.setAccountsPayableItemQuantity(purApForm.getMergeQty());
        firstItem.setAccountsPayableLineItemDescription(purApForm.getMergeDesc());
    }

    /**
     * Process after merge.
     * 
     * @param mergeLines
     * @param purApForm
     */
    private void postMergeProcess(List<PurchasingAccountsPayableItemAsset> mergeLines, PurApLineForm purApForm, PurApLineSession purApLineSession) {
        String actionTypeCode = isMergeAllAction(purApForm) ? CabConstants.Actions.MERGE_ALL : CabConstants.Actions.MERGE;
        PurchasingAccountsPayableItemAsset targetItem = mergeLines.get(0);

        // set unit cost and total cost
        setLineItemCost(targetItem);
        targetItem.setItemAssignedToTradeInIndicator(false);

        // For all merge source lines(the first line is considered as target technically), remove it from the document and update in
        // the action history.
        for (int i = 1; i < mergeLines.size(); i++) {
            PurchasingAccountsPayableItemAsset sourceItem = mergeLines.get(i);

            // Update the action history.
            addMergeHistory(purApLineSession.getActionsTakenHistory(), actionTypeCode, sourceItem, targetItem);

            if (ObjectUtils.isNotNull(sourceItem.getPurchasingAccountsPayableDocument())) {
                // remove mergeLines from the document
                sourceItem.getPurchasingAccountsPayableDocument().getPurchasingAccountsPayableItemAssets().remove(sourceItem);
                // if all active lines are merged to other line, we need to in-activate the current document
                inActivateDocument(sourceItem.getPurchasingAccountsPayableDocument());
            }
        }
        // set the target item itemLineNumber for pre-tagging
        Pretag targetPretag = getTargetPretag(mergeLines, purApForm.getPurchaseOrderIdentifier());
        if (targetPretag != null) {
            targetItem.setItemLineNumber(targetPretag.getItemLineNumber());
        }

        // update create asset/ apply payment indicator if any of the merged lines has the indicator set.
        updateAssetIndicatorAfterMerge(mergeLines);

        // reset user input values.
        resetSelectedValue(purApForm);
        purApForm.setMergeQty(null);
        purApForm.setMergeDesc(null);
    }

    /**
     * Update create asset and apply payment indicators after merge.
     * 
     * @param mergeLines
     */
    private void updateAssetIndicatorAfterMerge(List<PurchasingAccountsPayableItemAsset> mergeLines) {
        boolean existCreateAsset = false;
        boolean existApplyPayment = false;
        PurchasingAccountsPayableItemAsset targetItem = mergeLines.get(0);

        for (int i = 1; i < mergeLines.size(); i++) {
            PurchasingAccountsPayableItemAsset sourceItem = mergeLines.get(i);
            existCreateAsset |= sourceItem.isCreateAssetIndicator();
            existApplyPayment |= sourceItem.isApplyPaymentIndicator();
        }
        targetItem.setCreateAssetIndicator(targetItem.isCreateAssetIndicator() | existCreateAsset);
        targetItem.setApplyPaymentIndicator(targetItem.isApplyPaymentIndicator() | existApplyPayment);
    }

    /**
     * Get the first pre-tag for given itemLines
     * 
     * @param itemLines
     * @param purchaseOrderIdentifier
     * @return
     */
    private Pretag getTargetPretag(List<PurchasingAccountsPayableItemAsset> itemLines, Integer purchaseOrderIdentifier) {
        for (PurchasingAccountsPayableItemAsset item : itemLines) {
            Pretag newTag = getPreTagLineItem(purchaseOrderIdentifier, item.getItemLineNumber());

            if (isPretaggingExisting(newTag))
                return newTag;
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isPretaggingExisting(org.kuali.kfs.module.cab.businessobject.Pretag)
     */
    public boolean isPretaggingExisting(Pretag newTag) {
        return ObjectUtils.isNotNull(newTag) && newTag.getPretagDetails() != null && !newTag.getPretagDetails().isEmpty();
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#isMultipleTagExisting(java.lang.Integer, java.util.Set)
     */
    public boolean isMultipleTagExisting(Integer purchaseOrderIdentifier, Set<Integer> itemLineNumbers) {
        Pretag firstTag = null;
        for (Iterator iterator = itemLineNumbers.iterator(); iterator.hasNext();) {
            Integer itemLineNumber = (Integer) iterator.next();
            Pretag newTag = getPreTagLineItem(purchaseOrderIdentifier, itemLineNumber);

            if (isPretaggingExisting(newTag))
                if (firstTag != null) {
                    // find the second preTagging item
                    return true;
                }
                else {
                    firstTag = newTag;
                }
        }

        return false;
    }

    /**
     * Add merge action to the action history.
     * 
     * @param purApLineSession
     * @param isMergeAllAction
     * @param firstItem
     * @param item
     */
    private void addMergeHistory(List<PurchasingAccountsPayableActionHistory> actionsTakenHistory, String actionTypeCode, PurchasingAccountsPayableItemAsset sourceItem, PurchasingAccountsPayableItemAsset targetItem) {
        // create action history records for each account from the source lines.
        for (PurchasingAccountsPayableLineAssetAccount sourceAccount : sourceItem.getPurchasingAccountsPayableLineAssetAccounts()) {
            PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(sourceItem, targetItem, actionTypeCode);
            newAction.setAccountsPayableItemQuantity(sourceItem.getAccountsPayableItemQuantity());
            newAction.setItemAccountTotalAmount(sourceAccount.getItemAccountTotalAmount());
            newAction.setGeneralLedgerAccountIdentifier(sourceAccount.getGeneralLedgerAccountIdentifier());
            actionsTakenHistory.add(newAction);
        }
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processPercentPayment(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    public void processPercentPayment(PurchasingAccountsPayableItemAsset itemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakenHistory) {
        KualiDecimal oldQty = itemAsset.getAccountsPayableItemQuantity();
        KualiDecimal newQty = new KualiDecimal(1);
        // update quantity, total cost and unit cost.
        if (oldQty.isLessThan(newQty)) {
            itemAsset.setAccountsPayableItemQuantity(newQty);
            setLineItemCost(itemAsset);
        }
        // add to action history
        addPercentPaymentHistory(actionsTakenHistory, itemAsset, oldQty);
    }


    /**
     * Update action history for the percent payment action.
     * 
     * @param actionsTaken
     * @param item
     * @param oldQty
     */
    private void addPercentPaymentHistory(List<PurchasingAccountsPayableActionHistory> actionsTakenHistory, PurchasingAccountsPayableItemAsset item, KualiDecimal oldQty) {
        // create and set up one action history record for this action
        PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(item, item, CabConstants.Actions.PERCENT_PAYMENT);
        // record quantity before percent payment into action history
        newAction.setAccountsPayableItemQuantity(oldQty);
        actionsTakenHistory.add(newAction);
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processSplit(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    public void processSplit(PurchasingAccountsPayableItemAsset splitItemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakeHistory) {
        PurchasingAccountsPayableDocument purApDoc = splitItemAsset.getPurchasingAccountsPayableDocument();
        // create a new item asset from the current item asset.
        PurchasingAccountsPayableItemAsset newItemAsset = new PurchasingAccountsPayableItemAsset(splitItemAsset);

        // set cab line number
        newItemAsset.setCapitalAssetBuilderLineNumber(getAvailableCabLineNumber(splitItemAsset, purApDoc) + 1);

        newItemAsset.setAccountsPayableItemQuantity(splitItemAsset.getSplitQty());

        // Set account list for new item asset and update current account amount value.
        createAccountsForNewItemAsset(splitItemAsset, newItemAsset);
        // set unit cost and total cost in new item
        setLineItemCost(newItemAsset);

        // Adjust current item asset quantity, total cost and unit cost
        splitItemAsset.setAccountsPayableItemQuantity(splitItemAsset.getAccountsPayableItemQuantity().subtract(splitItemAsset.getSplitQty()));
        setLineItemCost(splitItemAsset);

        // add the new item into document and sort.
        purApDoc.getPurchasingAccountsPayableItemAssets().add(newItemAsset);
        Collections.sort(purApDoc.getPurchasingAccountsPayableItemAssets());

        // Add to action history
        addSplitHistory(splitItemAsset, newItemAsset, actionsTakeHistory);

        // clear up user input
        splitItemAsset.setSplitQty(null);
    }

    /**
     * Get the max cab line #. As part of the primary key, it should be the max value among the form item list and DB.
     * 
     * @param splitItemAsset
     * @param purApDoc
     * @return
     */
    private int getAvailableCabLineNumber(PurchasingAccountsPayableItemAsset splitItemAsset, PurchasingAccountsPayableDocument purApDoc) {
        // get the max CAB line number in DB.
        Integer maxDBCabLineNbr = purApLineDao.getMaxCabLineNumber(splitItemAsset.getDocumentNumber(), splitItemAsset.getAccountsPayableLineItemIdentifier());
        // get the max CAB line number in form.
        int availableCabLineNbr = getMaxCabLineNbrForItemInForm(purApDoc, splitItemAsset);

        if (maxDBCabLineNbr.intValue() > availableCabLineNbr) {
            availableCabLineNbr = maxDBCabLineNbr.intValue();
        }
        return availableCabLineNbr;
    }

    /**
     * Search the current active items and return the max CAB line # for split new item .
     * 
     * @param purApDoc
     * @param currentItemAsset
     * @return
     */
    private int getMaxCabLineNbrForItemInForm(PurchasingAccountsPayableDocument purApDoc, PurchasingAccountsPayableItemAsset currentItemAsset) {
        int maxCabLineNbr = 0;
        for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
            if (item.getDocumentNumber().equalsIgnoreCase(currentItemAsset.getDocumentNumber()) && item.getAccountsPayableLineItemIdentifier().equals(currentItemAsset.getAccountsPayableLineItemIdentifier()) && item.getCapitalAssetBuilderLineNumber().intValue() > maxCabLineNbr) {
                maxCabLineNbr = item.getCapitalAssetBuilderLineNumber().intValue();
            }

        }
        return maxCabLineNbr;
    }

    /**
     * Update action history for a split action.
     * 
     * @param currentItemAsset
     * @param newItemAsset
     * @param actionsTaken
     */
    private void addSplitHistory(PurchasingAccountsPayableItemAsset currentItemAsset, PurchasingAccountsPayableItemAsset newItemAsset, List<PurchasingAccountsPayableActionHistory> actionsTakenHistory) {
        // for each account moved from original item to new item, create one action history record
        for (PurchasingAccountsPayableLineAssetAccount account : newItemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
            PurchasingAccountsPayableActionHistory newAction = new PurchasingAccountsPayableActionHistory(currentItemAsset, newItemAsset, CabConstants.Actions.SPLIT);
            newAction.setGeneralLedgerAccountIdentifier(account.getGeneralLedgerAccountIdentifier());
            // quantity moved from original item to new item
            newAction.setAccountsPayableItemQuantity(newItemAsset.getAccountsPayableItemQuantity());
            // account amount moved to new item
            newAction.setItemAccountTotalAmount(account.getItemAccountTotalAmount());
            actionsTakenHistory.add(newAction);
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#saveBusinessObject(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void processSaveBusinessObjects(PurApLineForm purApLineForm, PurApLineSession purApLineSession) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            // auto save items(including deleted items) and accounts due to auto-update setting in OJB.
            businessObjectService.save(purApDoc);
        }
        if (purApLineSession != null) {
            // save to action history table
            List<PurchasingAccountsPayableActionHistory> historyList = purApLineSession.getActionsTakenHistory();
            if (!historyList.isEmpty()) {
                businessObjectService.save(historyList);
                historyList.removeAll(historyList);
            }

            // save to generalLedgerEntry table
            List<GeneralLedgerEntry> glUpdateList = purApLineSession.getGlEntryUpdateList();
            if (!glUpdateList.isEmpty()) {
                businessObjectService.save(glUpdateList);
                glUpdateList.removeAll(glUpdateList);
            }
        }
    }


    /**
     * Create asset account list for new item asset and update the current account amount.
     * 
     * @param oldItemAsset old line item.
     * @param newItemAsset new line item.
     */
    private void createAccountsForNewItemAsset(PurchasingAccountsPayableItemAsset currentItemAsset, PurchasingAccountsPayableItemAsset newItemAsset) {
        KualiDecimal currentQty = currentItemAsset.getAccountsPayableItemQuantity();
        KualiDecimal splitQty = currentItemAsset.getSplitQty();
        List<PurchasingAccountsPayableLineAssetAccount> newAccountsList = newItemAsset.getPurchasingAccountsPayableLineAssetAccounts();
        PurchasingAccountsPayableLineAssetAccount newAccount;
        for (PurchasingAccountsPayableLineAssetAccount currentAccount : currentItemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
            // create accounts for new item asset.
            newAccount = new PurchasingAccountsPayableLineAssetAccount(currentItemAsset, currentAccount.getGeneralLedgerAccountIdentifier());
            newAccount.setItemAccountTotalAmount(currentAccount.getItemAccountTotalAmount().multiply(splitQty).divide(currentQty));
            newAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.GENERAL_LEDGER_ENTRY);
            newAccount.setPurchasingAccountsPayableItemAsset(newItemAsset);
            // newAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.PURAP_ITEM_ASSET);
            newAccountsList.add(newAccount);

            // Adjust current account amount for split item( subtract new account amount for original amount)
            currentAccount.setItemAccountTotalAmount(currentAccount.getItemAccountTotalAmount().subtract(newAccount.getItemAccountTotalAmount()));
        }
    }


    /**
     * Set object code by the first one from the accounting lines.
     * 
     * @param item Selected line item.
     */
    private void setFirstFinancialObjectCode(PurchasingAccountsPayableItemAsset item) {
        String firstFinancialObjectCode = null;
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            if (ObjectUtils.isNotNull(account.getGeneralLedgerEntry())) {
                firstFinancialObjectCode = account.getGeneralLedgerEntry().getFinancialObjectCode();
                break;
            }
        }
        item.setFirstFincialObjectCode(firstFinancialObjectCode);
    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#buildPurApItemAssetsList(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void buildPurApItemAssetList(PurApLineForm purApLineForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // set item non-persistent fields from PurAp PREQ/CM item tables
                purApInfoService.setAccountsPayableItemsFromPurAp(item, purApDoc.getDocumentTypeCode());

                // set line item unit cost and total cost
                setLineItemCost(item);

                // set financial object code
                setFirstFinancialObjectCode(item);
            }
            // For display purpose, move additional charges including trade-in below item lines.
            Collections.sort(purApDoc.getPurchasingAccountsPayableItemAssets());
        }

        // set CAMS Transaction type from PurAp
        purApInfoService.setCamsTransactionFromPurAp(purApLineForm);

        // set create asset/apply payment indicator which are used to control display two buttons.
        setAssetIndicator(purApLineForm);

    }


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#getPreTagLineItem(java.lang.String, java.lang.Integer)
     */
    public Pretag getPreTagLineItem(Integer purchaseOrderIdentifier, Integer lineItemNumber) {

        if (purchaseOrderIdentifier == null || lineItemNumber == null) {
            return null;
        }

        Map<String, Object> pKeys = new HashMap<String, Object>();

        pKeys.put(CabPropertyConstants.Pretag.PURCHASE_ORDER_NUMBER, purchaseOrderIdentifier);
        pKeys.put(CabPropertyConstants.Pretag.ITEM_LINE_NUMBER, lineItemNumber);
        return (Pretag) businessObjectService.findByPrimaryKey(Pretag.class, pKeys);
    }

    /**
     * Set create asset and apply payment indicator. These two indicators are referenced by jsp to control display of these two
     * buttons. How to set these two indicators is based on the business rules. We need to put the following situations into
     * consideration.
     * <p>
     * 1. These two buttons only can not work with additional charges, but if the lines are nothing but additional charges or
     * trade-in allowance without trade-in indicator items, we open these two buttons.
     * <p>
     * 2. For line items, there must be no additional charges from the same document pending for allocation.
     * <p>
     * 3. For line items, if it has trade-in indicator set, there must be no trade-in allowance pending for allocation. Trade-in
     * allowance could from other document but share the same po_id.
     * 
     * @param purApLineForm
     */
    private void setAssetIndicator(PurApLineForm purApLineForm) {
        // get the trade-in allowance in the form-wise
        boolean existTradeInAllowance = isTradeInAllowanceExist(purApLineForm);
        // get the trade-in indicator in the form-wise
        boolean existTradeInIndicator = isTradeInIndicatorExist(purApLineForm);
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            boolean existAdditionalCharge = false;
            boolean existItemAsset = false;
            // check if within the same document, exist both additional charge lines and normal line items.
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                existAdditionalCharge |= item.isAdditionalChargeNonTradeInIndicator();
                existItemAsset |= !item.isAdditionalChargeNonTradeInIndicator() & !item.isTradeInAllowance();
            }

            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // when the indicator is not set yet...
                if (!item.isCreateAssetIndicator() || !item.isApplyPaymentIndicator()) {
                    // If the lines on the purchase order are all additional charge lines, or trade-in allowance then we can apply
                    // payment, or create asset.
                    if ((item.isAdditionalChargeNonTradeInIndicator() && !existItemAsset) || (item.isTradeInAllowance() && !existTradeInIndicator)) {
                        item.setCreateAssetIndicator(true);
                        item.setApplyPaymentIndicator(true);
                    }
                    // For line item(not additional charge line), if there is no pending additional charges and itself not a
                    // trade-in indicator or it has trade-in indicator but no trade-in allowance, we allow apply payment or create
                    // asset.
                    else if (!existAdditionalCharge && (!item.isAdditionalChargeNonTradeInIndicator() && !item.isTradeInAllowance() && (!item.isItemAssignedToTradeInIndicator() || !existTradeInAllowance))) {
                        item.setCreateAssetIndicator(true);
                        item.setApplyPaymentIndicator(true);
                    }
                }
            }
        }

    }


    /**
     * Set item asset unit cost.
     * 
     * @param item line item
     * @param totalCost total cost for this line item.
     */
    private void setItemAssetUnitCost(PurchasingAccountsPayableItemAsset item, KualiDecimal totalCost) {
        // set unit cost
        KualiDecimal quantity = item.getAccountsPayableItemQuantity();
        if (quantity != null && quantity.isNonZero()) {
            item.setUnitCost(totalCost.divide(quantity));
        }
    }

    /**
     * Calculate item asset total cost
     * 
     * @param item
     * @return line item total cost
     */
    public KualiDecimal calculateItemAssetTotalCost(PurchasingAccountsPayableItemAsset item) {
        // Calculate and set total cost
        KualiDecimal totalCost = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            totalCost = totalCost.add(account.getItemAccountTotalAmount());
        }
        return totalCost;
    }


    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Gets the purApLineDao attribute.
     * 
     * @return Returns the purApLineDao.
     */
    public PurApLineDao getPurApLineDao() {
        return purApLineDao;
    }


    /**
     * Sets the purApLineDao attribute value.
     * 
     * @param purApLineDao The purApLineDao to set.
     */
    public void setPurApLineDao(PurApLineDao purApLineDao) {
        this.purApLineDao = purApLineDao;
    }

    /**
     * Gets the purApInfoService attribute.
     * 
     * @return Returns the purApInfoService.
     */
    public PurApInfoService getPurApInfoService() {
        return purApInfoService;
    }

    /**
     * Sets the purApInfoService attribute value.
     * 
     * @param purApInfoService The purApInfoService to set.
     */
    public void setPurApInfoService(PurApInfoService purApInfoService) {
        this.purApInfoService = purApInfoService;
    }

}
