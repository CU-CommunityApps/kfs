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
package org.kuali.module.purap.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApAccountingLineBase;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.AccountsPayableDocumentSpecificService;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.util.ExpiredOrClosedAccount;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccountsPayableServiceImpl implements AccountsPayableService {

    private PurapAccountingService purapAccountingService;
    private PurapGeneralLedgerService purapGeneralLedgerService;
    private DocumentService documentService;
    private PurapService purapService;
    private ParameterService parameterService;

    /**
     * Gets the purapService attribute.
     * 
     * @return Returns the purapService.
     */
    public PurapService getPurapService() {
        return purapService;
    }

    /**
     * Sets the purapService attribute value.
     * 
     * @param purapService The purapService to set.
     */
    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    /**
     * Gets the purapAccountingService attribute.
     * 
     * @return Returns the purapAccountingService.
     */
    public PurapAccountingService getPurapAccountingService() {
        return purapAccountingService;
    }

    /**
     * Sets the purapAccountingService attribute value.
     * 
     * @param purapAccountingService The purapAccountingService to set.
     */
    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    /**
     * Gets the purapGeneralLedgerService attribute.
     * 
     * @return Returns the purapGeneralLedgerService.
     */
    public PurapGeneralLedgerService getPurapGeneralLedgerService() {
        return purapGeneralLedgerService;
    }

    /**
     * Sets the purapGeneralLedgerService attribute value.
     * 
     * @param purapGeneralLedgerService The purapGeneralLedgerService to set.
     */
    public void setPurapGeneralLedgerService(PurapGeneralLedgerService purapGeneralLedgerService) {
        this.purapGeneralLedgerService = purapGeneralLedgerService;
    }

    /**
     * Gets the documentService attribute.
     * 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableService#getExpiredOrClosedAccountList(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public HashMap<String, ExpiredOrClosedAccountEntry> getExpiredOrClosedAccountList(AccountsPayableDocument document) {

        // Retrieve a list of accounts and replacement accounts, where accounts or closed or expired.
        HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccounts = expiredOrClosedAccountsList(document);

        return expiredOrClosedAccounts;
    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableService#generateExpiredOrClosedAccountNote(org.kuali.module.purap.document.AccountsPayableDocument,
     *      java.util.HashMap)
     */
    public void generateExpiredOrClosedAccountNote(AccountsPayableDocument document, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {

        // create a note of all the replacement accounts
        if (!expiredOrClosedAccountList.isEmpty()) {
            addContinuationAccountsNote(document, expiredOrClosedAccountList);
        }

    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableService#generateExpiredOrClosedAccountWarning(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public void generateExpiredOrClosedAccountWarning(AccountsPayableDocument document) {

        // get user
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();

        // get parameter to see if fiscal officers may see the continuation account warning
        String showContinuationAccountWaringFO = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PURAP_AP_SHOW_CONTINUATION_ACCOUNT_WARNING_FISCAL_OFFICERS);

        // TODO (KULPURAP-1569: dlemus) See if/how we want to allow AP users to view the continuation account warning
        // get parameter to see if ap users may see the continuation account warning
        String showContinuationAccountWaringAP = parameterService.getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapConstants.PURAP_AP_SHOW_CONTINUATION_ACCOUNT_WARNING_AP_USERS);

        // versus doing it in their respective documents (preq, credit memo)
        // if not initiate or in process and
        // user is a fiscal officer and a system parameter is set to allow viewing
        // and if the continuation account indicator is set
        if (!(PurapConstants.PaymentRequestStatuses.INITIATE.equals(document.getStatusCode()) || PurapConstants.PaymentRequestStatuses.IN_PROCESS.equals(document.getStatusCode()) || PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW.equals(document.getStatusCode())) && (isFiscalUser(document, user) && "Y".equals(showContinuationAccountWaringFO)) && (document.isContinuationAccountIndicator())) {

            GlobalVariables.getMessageList().add(PurapKeyConstants.MESSAGE_CLOSED_OR_EXPIRED_ACCOUNTS_REPLACED);
        }
    }

    /**
     * @see org.kuali.module.purap.service.AccountsPayableService#processExpiredOrClosedAccount(org.kuali.module.purap.bo.PurApAccountingLineBase,
     *      java.util.HashMap)
     */
    public void processExpiredOrClosedAccount(PurApAccountingLineBase acctLineBase, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {

        ExpiredOrClosedAccountEntry accountEntry = null;
        String acctKey = acctLineBase.getChartOfAccountsCode() + "-" + acctLineBase.getAccountNumber();

        if (expiredOrClosedAccountList.containsKey(acctKey)) {

            accountEntry = expiredOrClosedAccountList.get(acctKey);

            if (accountEntry.getOriginalAccount().isContinuationAccountMissing() == false) {
                acctLineBase.setChartOfAccountsCode(accountEntry.getReplacementAccount().getChartOfAccountsCode());
                acctLineBase.setAccountNumber(accountEntry.getReplacementAccount().getAccountNumber());
                acctLineBase.refreshReferenceObject("chart");
                acctLineBase.refreshReferenceObject("account");
            }
        }
    }

    /**
     * This method creates and adds a note indicating accounts replaced and what they replaced and attaches it to the document.
     * 
     * @param document
     * @param accounts
     */
    private void addContinuationAccountsNote(AccountsPayableDocument document, HashMap<String, ExpiredOrClosedAccountEntry> accounts) {
        String noteText;
        StringBuffer sb = new StringBuffer("");
        ExpiredOrClosedAccountEntry accountEntry = null;
        ExpiredOrClosedAccount originalAccount = null;
        ExpiredOrClosedAccount replacementAccount = null;

        // List the entries using entrySet()
        Set entries = accounts.entrySet();
        Iterator it = entries.iterator();

        // loop through the accounts found to be expired/closed and add if they have a continuation account
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            accountEntry = (ExpiredOrClosedAccountEntry) entry.getValue();
            originalAccount = accountEntry.getOriginalAccount();
            replacementAccount = accountEntry.getReplacementAccount();

            // only print out accounts that were replaced and not missing a continuation account
            if (originalAccount.isContinuationAccountMissing() == false) {
                sb.append(" Account " + originalAccount.getAccountString() + " was replaced with account " + replacementAccount.getAccountString() + " ; ");
            }

        }

        // if a note was created, add it to the document
        if (sb.toString().length() > 0) {
            try {
                DocumentService documentService = SpringContext.getBean(DocumentService.class);
                Note resetNote = documentService.createNoteFromDocument(document, sb.toString());
                documentService.addNoteToDocument(document, resetNote);
            }
            catch (Exception e) {
                throw new RuntimeException(PurapConstants.REQ_UNABLE_TO_CREATE_NOTE + " " + e);
            }
        }
    }

    /**
     * This method generates a list of replacement accounts for expired or closed accounts, as well as expired/closed accounts
     * without a continuation account.
     * 
     * @param document
     * @return
     */
    private HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountsList(AccountsPayableDocument document) {

        HashMap<String, ExpiredOrClosedAccountEntry> list = new HashMap<String, ExpiredOrClosedAccountEntry>();
        ExpiredOrClosedAccountEntry entry = null;
        ExpiredOrClosedAccount originalAcct = null;
        ExpiredOrClosedAccount replaceAcct = null;
        String chartAccount = null;
        Integer POID = document.getPurchaseOrderIdentifier();

        PurchaseOrderDocument po = document.getPurchaseOrderDocument();

        if (po != null) {
            // get list of active accounts
            PurapAccountingService pas = SpringContext.getBean(PurapAccountingService.class);
            List<PurApItem> apItems = document.getItems();
            List<PurchaseOrderItem> poItems = po.getItemsActiveOnly();
            List<PurApItem> poItemsOnDoc = new ArrayList<PurApItem>();
            for (PurchaseOrderItem purchaseOrderItem : poItems) {
                if (ObjectUtils.isNotNull(document.getAPItemFromPOItem(purchaseOrderItem))) {
                    poItemsOnDoc.add(purchaseOrderItem);
                }
            }
            List<SourceAccountingLine> accountList = pas.generateSummary(poItemsOnDoc);

            // loop through accounts
            for (SourceAccountingLine poAccountingLine : accountList) {

                AccountService as = SpringContext.getBean(AccountService.class);
                Account account = as.getByPrimaryId(poAccountingLine.getChartOfAccountsCode(), poAccountingLine.getAccountNumber());

                entry = new ExpiredOrClosedAccountEntry();

                originalAcct = new ExpiredOrClosedAccount(poAccountingLine.getChartOfAccountsCode(), poAccountingLine.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                if (account.isAccountClosedIndicator()) {

                    // 1. if the account is closed, get the continuation account and add it to the list
                    Account continuationAccount = as.getByPrimaryId(account.getContinuationFinChrtOfAcctCd(), account.getContinuationAccountNumber());

                    if (continuationAccount == null) {
                        replaceAcct = new ExpiredOrClosedAccount();
                        originalAcct.setContinuationAccountMissing(true);

                        entry.setOriginalAccount(originalAcct);
                        entry.setReplacementAccount(replaceAcct);

                        list.put(createChartAccountString(originalAcct), entry);
                    }
                    else {
                        replaceAcct = new ExpiredOrClosedAccount(continuationAccount.getChartOfAccountsCode(), continuationAccount.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                        entry.setOriginalAccount(originalAcct);
                        entry.setReplacementAccount(replaceAcct);

                        list.put(createChartAccountString(originalAcct), entry);
                    }
                    // 2. if the account is expired and the current date is <= 90 days from the expiration date, do nothing
                    // 3. if the account is expired and the current date is > 90 days from the expiration date, get the continuation
                    // account and add it to the list
                }
                else if (account.isExpired()) {
                    Account continuationAccount = as.getByPrimaryId(account.getContinuationFinChrtOfAcctCd(), account.getContinuationAccountNumber());

                    // if account is C&G and expired then add to list.
                    if ((account.isForContractsAndGrants() && SpringContext.getBean(DateTimeService.class).dateDiff(account.getAccountExpirationDate(), SpringContext.getBean(DateTimeService.class).getCurrentDate(), true) > 90)) {

                        if (continuationAccount == null) {
                            replaceAcct = new ExpiredOrClosedAccount();
                            originalAcct.setContinuationAccountMissing(true);

                            entry.setOriginalAccount(originalAcct);
                            entry.setReplacementAccount(replaceAcct);

                            list.put(createChartAccountString(originalAcct), entry);
                        }
                        else {
                            replaceAcct = new ExpiredOrClosedAccount(continuationAccount.getChartOfAccountsCode(), continuationAccount.getAccountNumber(), poAccountingLine.getSubAccountNumber());

                            entry.setOriginalAccount(originalAcct);
                            entry.setReplacementAccount(replaceAcct);

                            list.put(createChartAccountString(originalAcct), entry);
                        }
                    }

                    // if account is not C&G, use the same account, do not replace
                }
            }
        }
        return list;
    }

    /**
     * This method creates a chart-account string.
     * 
     * @param ecAccount
     * @return
     */
    private String createChartAccountString(ExpiredOrClosedAccount ecAccount) {
        StringBuffer buff = new StringBuffer("");

        buff.append(ecAccount.getChartOfAccountsCode());
        buff.append("-");
        buff.append(ecAccount.getAccountNumber());

        return buff.toString();
    }

    /**
     * This method determines if the user is a fiscal officer.
     * 
     * @param document
     * @param user
     * @return
     */
    private boolean isFiscalUser(AccountsPayableDocument document, UniversalUser user) {
        boolean isFiscalUser = false;

        if (PaymentRequestStatuses.AWAITING_FISCAL_REVIEW.equals(document.getStatusCode()) && document.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
            isFiscalUser = true;
        }

        return isFiscalUser;
    }

    /**
     * Sets ap doc to canceled. If gl entries have been created cancel entries are created.
     */
    public void cancelAccountsPayableDocument(AccountsPayableDocument apDocument, String currentNodeName) {
        // retrieve and save with canceled status, clear gl entries
        // AccountsPayableDocument apDoc;
        // try {
        // apDoc = (AccountsPayableDocument)documentService.getByDocumentHeaderId(apDocument.getDocumentNumber());
        // }
        // catch (WorkflowException e) {
        // throw new RuntimeException("Can't get workflow doc "+e);
        // }

        if (purapService.isFullDocumentEntryCompleted(apDocument)) {
            purapGeneralLedgerService.generateEntriesCancelAccountsPayableDocument(apDocument);
        }
        AccountsPayableDocumentSpecificService accountsPayableDocumentSpecificService = apDocument.getDocumentSpecificService();
        String cancelledStatusCode = accountsPayableDocumentSpecificService.updateStatusByNode(currentNodeName, apDocument);
        // apDocument.setStatusCode(cancelledStatusCode);
        apDocument.refreshReferenceObject(PurapPropertyConstants.STATUS);
        // close/reopen po?
        accountsPayableDocumentSpecificService.takePurchaseOrderCancelAction(apDocument);
    }

    public void updateItemList(AccountsPayableDocument apDocument) {
        // don't run the following if past full entry
        if (purapService.isFullDocumentEntryCompleted(apDocument)) {
            return;
        }
        if (apDocument instanceof CreditMemoDocument) {
            // TODO: merge this CM code with below PREQ code, it is almost identical
            CreditMemoDocument cm = (CreditMemoDocument) apDocument;
            if (cm.isSourceDocumentPaymentRequest()) {
                // just update encumberances, items shouldn't change, get to them through po (or through preq)
                List<PaymentRequestItem> items = cm.getPaymentRequestDocument().getItems();
                for (PaymentRequestItem preqItem : items) {
                    //skip inactive and below the line
                    if (!preqItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                        continue;
                    }
                    PurchaseOrderItem poItem = preqItem.getPurchaseOrderItem();
                    CreditMemoItem cmItem = (CreditMemoItem)cm.getAPItemFromPOItem(poItem);
                    // take invoiced quantities from the lower of the preq and po if different
                    updateEncumberances(preqItem, poItem, cmItem);
                }

            }
            else if (cm.isSourceDocumentPurchaseOrder()) {
                PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(apDocument.getPurchaseOrderIdentifier());
                List<PurchaseOrderItem> poItems = po.getItems();
                List<CreditMemoItem> cmItems = cm.getItems();
                // iterate through the above the line poItems to find matching
                for (PurchaseOrderItem purchaseOrderItem : poItems) {
                    //skip inactive and below the line
                    if (!purchaseOrderItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                        continue;
                    }
                    
                    CreditMemoItem cmItem = (CreditMemoItem)cm.getAPItemFromPOItem(purchaseOrderItem);
                    //check if any action needs to be taken on the items (i.e. add for new eligible items or remove for ineligible)
                    if(apDocument.getDocumentSpecificService().poItemEligibleForAp(apDocument, purchaseOrderItem)) {
                        //if eligible and not there - add
                    if (ObjectUtils.isNull(cmItem)) {
                        cmItems.add(new CreditMemoItem(cm, purchaseOrderItem));
                        } else {
                            //is eligible and on doc, update encumberances
                            //(this is only qty and amount for now NOTE we should also update other key fields, like description etc in case ammendment modified a line
                            updateEncumberance(purchaseOrderItem, cmItem);
                    }
                    } else { //if not eligible and there - remove
                        if(ObjectUtils.isNotNull(cmItem)) {
                            cmItems.remove(cmItem);
                            //don't update encumberance
                            continue;
                        }
                    }

                }


            } // else do nothing
            return;

            // finally update encumberances (or try to do it as I'm going
        }
        else if (apDocument instanceof PaymentRequestDocument) {


            // get a fresh po
            PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(apDocument.getPurchaseOrderIdentifier());
            PaymentRequestDocument preq = (PaymentRequestDocument) apDocument;

            List<PurchaseOrderItem> poItems = po.getItems();
            List<PaymentRequestItem> preqItems = preq.getItems();
            // iterate through the above the line poItems to find matching
            for (PurchaseOrderItem purchaseOrderItem : poItems) {
                //skip below the line
                if (!purchaseOrderItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                    continue;
                }
                PaymentRequestItem preqItem = (PaymentRequestItem) preq.getAPItemFromPOItem(purchaseOrderItem);
                //check if any action needs to be taken on the items (i.e. add for new eligible items or remove for ineligible)
                if(apDocument.getDocumentSpecificService().poItemEligibleForAp(apDocument, purchaseOrderItem)) {
                    //if eligible and not there - add
                if (ObjectUtils.isNull(preqItem)) {
                    preqItems.add(new PaymentRequestItem(purchaseOrderItem, preq));
                }
                } else { //if not eligible and there - remove
                    if(ObjectUtils.isNotNull(preqItem)) {
                        preqItems.remove(preqItem);
                    }
                }

            }
        }
        // TODO: ctk - call calculate here?
    }

    /**
     * This method updates encumberances
     * 
     * @param preqItem
     * @param poItem
     * @param cmItem
     */
    private void updateEncumberances(PaymentRequestItem preqItem, PurchaseOrderItem poItem, CreditMemoItem cmItem) {
        if (poItem.getItemInvoicedTotalQuantity() != null && preqItem.getItemQuantity() != null && poItem.getItemInvoicedTotalQuantity().isLessThan(preqItem.getItemQuantity())) {
            cmItem.setPreqInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
            cmItem.setPreqExtendedPrice(poItem.getItemInvoicedTotalAmount());
        }
        else {
            cmItem.setPreqInvoicedTotalQuantity(preqItem.getItemQuantity());
            cmItem.setPreqExtendedPrice(preqItem.getExtendedPrice());
        }
    }

    /**
     * This method updates the encumberance related fields.
     * 
     * @param purchaseOrderItem
     * @param cmItem
     */
    private void updateEncumberance(PurchaseOrderItem purchaseOrderItem, CreditMemoItem cmItem) {
        cmItem.setPoInvoicedTotalQuantity(purchaseOrderItem.getItemInvoicedTotalQuantity());
        cmItem.setPoExtendedPrice(purchaseOrderItem.getItemInvoicedTotalAmount());
    }

    public boolean purchaseOrderItemEligibleForPayment(PurchaseOrderItem poi) {
        if (ObjectUtils.isNull(poi)) {
            // LOG.debug("poi was null");
            throw new RuntimeException("item null in purchaseOrderItemEligibleForPayment ... this should never happen");
        }

        // if the po item is not active... skip it
        if (!poi.isItemActiveIndicator()) {
            // LOG.debug("poi was not active: "+poi.toString());
            return false;
        }

        ItemType poiType = poi.getItemType();

        if (poiType.isQuantityBasedGeneralLedgerIndicator()) {
            if (poi.getItemQuantity().isGreaterThan(poi.getItemInvoicedTotalQuantity())) {
                return true;
            }
            return false;
        }
        else { // not quantity based
            if (poi.getItemOutstandingEncumberedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            }
            return false;
        }

    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
