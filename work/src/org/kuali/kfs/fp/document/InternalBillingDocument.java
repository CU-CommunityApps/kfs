/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.InternalBillingItem;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.SaveDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;


/**
 * This is the business object that represents the InternalBillingDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines: Expense
 * and Income.
 */
public class InternalBillingDocument extends CapitalAssetInformationDocumentBase implements Copyable, Correctable, AmountTotaling {

    protected List items;
    protected Integer nextItemLineNumber;

    protected List<CapitalAssetInformation> capitalAssetInformation;
    protected transient CapitalAssetManagementModuleService capitalAssetManagementModuleService;

    /**
     * Initializes the array lists and some basic info.
     */
    public InternalBillingDocument() {
        super();
        setItems(new ArrayList());
        this.nextItemLineNumber = new Integer(1);
    }

    /**
     * Adds a new item to the item list.
     * 
     * @param item
     */
    public void addItem(InternalBillingItem item) {
        item.setItemSequenceId(this.nextItemLineNumber);
        this.items.add(item);
        this.nextItemLineNumber = new Integer(this.nextItemLineNumber.intValue() + 1);
    }

    /**
     * Retrieve a particular item at a given index in the list of items. For Struts, the requested item and any intervening ones are
     * initialized if necessary.
     * 
     * @param index
     * @return the item
     */
    public InternalBillingItem getItem(int index) {
        while (getItems().size() <= index) {
            getItems().add(new InternalBillingItem());
        }
        return (InternalBillingItem) getItems().get(index);
    }

    /**
     * @return Returns the items.
     */
    public List getItems() {
        return items;
    }

    /**
     * Allows items (in addition to accounting lines) to be deleted from the database after being saved there.
     * 
     * @see org.kuali.rice.kns.document.TransactionalDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        
        managedLists.add(getItems());
        List<CapitalAssetInformation> capitalAssets = this.getCapitalAssetInformation();
        for (CapitalAssetInformation capitalAsset : capitalAssets) {
            if (ObjectUtils.isNotNull(capitalAsset) && ObjectUtils.isNotNull(capitalAsset.getCapitalAssetInformationDetails())) {
                managedLists.add(capitalAsset.getCapitalAssetInformationDetails());
            }
        }
        
        return managedLists;
    }

    /**
     * Iterates through the list of items and sums up their totals.
     * 
     * @return the total
     */
    public KualiDecimal getItemTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
        for (Iterator iterator = items.iterator(); iterator.hasNext();) {
            total = total.add(((InternalBillingItem) iterator.next()).getTotal());
        }
        return total;
    }

    /**
     * Retrieves the next item line number.
     * 
     * @return The next available item line number
     */
    public Integer getNextItemLineNumber() {
        return this.nextItemLineNumber;
    }

    /**
     * @param items
     */
    public void setItems(List items) {
        this.items = items;
    }

    /**
     * Setter for OJB to get from database and JSP to maintain state in hidden fields. This property is also incremented by the
     * <code>addItem</code> method.
     * 
     * @param nextItemLineNumber
     */
    public void setNextItemLineNumber(Integer nextItemLineNumber) {
        this.nextItemLineNumber = nextItemLineNumber;
    }

    /**
     * @return "Income"
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.INCOME;
    }

    /**
     * @return "Expense"
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.EXPENSE;
    }

    /**
     * This method determines if an accounting line is a debit accounting line by calling IsDebitUtils.isDebitConsideringSection().
     * 
     * @param transactionalDocument The document containing the accounting line being analyzed.
     * @param accountingLine The accounting line being reviewed to determine if it is a debit line or not.
     * @return True if the accounting line is a debit accounting line, false otherwise.
     * @see IsDebitUtils#isDebitConsideringSection(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.kns.rule.AccountingLineRule#isDebit(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        return isDebitUtils.isDebitConsideringSection(this, (AccountingLine) postable);
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return this.capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;        
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this);
    }


    /**
     * @see org.kuali.rice.kns.document.DocumentBase#postProcessSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this, documentTypeName);
        }
    }


    /**
     * @return CapitalAssetManagementModuleService
     */
    CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        if (capitalAssetManagementModuleService == null) {
            capitalAssetManagementModuleService = SpringContext.getBean(CapitalAssetManagementModuleService.class);
        }
        return capitalAssetManagementModuleService;
    }
}
