/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.dao.ojb.DocumentDaoOjb;
import org.kuali.kfs.document.AmountTotaling;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PurApAccountingLineBase;
import org.kuali.module.purap.bo.PurApAccountingLineParser;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.Status;
import org.kuali.module.purap.service.PurapAccountingService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.util.PurApOjbCollectionHelper;
import org.kuali.module.purap.util.PurApRelatedViews;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Base class for Purchasing-Accounts Payable Documents.
 */
public abstract class PurchasingAccountsPayableDocumentBase extends AccountingDocumentBase implements PurchasingAccountsPayableDocument, AmountTotaling {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableDocumentBase.class);

    // SHARED FIELDS BETWEEN REQUISITION, PURCHASE ORDER, PAYMENT REQUEST, AND CREDIT MEMO
    private Integer purapDocumentIdentifier;
    private String statusCode;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String vendorCustomerNumber;
    private String vendorName;
    private String vendorLine1Address;
    private String vendorLine2Address;
    private String vendorCityName;
    private String vendorStateCode;
    private String vendorAddressInternationalProvinceName;
    private String vendorPostalCode;
    private String vendorCountryCode;
    private Integer accountsPayablePurchasingDocumentLinkIdentifier;

    // NOT PERSISTED IN DB
    private String vendorNumber;
    private Integer vendorAddressGeneratedIdentifier;
    private Boolean overrideWorkflowButtons = null;
    private transient PurApRelatedViews relatedViews;
    
    // COLLECTIONS
    private List<PurApItem> items;
    private List<SourceAccountingLine> accountsForRouting; // don't use me for anything else!!

    // REFERENCE OBJECTS
    private Status status;
    private VendorDetail vendorDetail;
    private Country vendorCountry;

    // STATIC
    public transient String[] belowTheLineTypes;

    // workaround for purapOjbCollectionHelper - remove when merged into rice
    public boolean allowDeleteAwareCollection = false;
    

    /**
     * Default constructor to be overridden.
     */
    public PurchasingAccountsPayableDocumentBase() {
        items = new TypedArrayList(getItemClass());
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#isPostingYearNext()
     */
    public boolean isPostingYearNext() {
        Integer currentFY = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        return (getPostingYear().compareTo(currentFY) > 0);
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getPostingYearNextOrCurrent()
     */
    public Integer getPostingYearNextOrCurrent() {
        if (isPostingYearNext()) {
            //FY is set to next; use it
            return getPostingYear();
        }
        //FY is NOT set to next; use CURRENT
        return SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }
    
    /**
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return PurApAccountingLineBase.class;
    }    
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getItemClass()
     */
    public abstract Class getItemClass();

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getPurApSourceDocumentIfPossible()
     */
    public abstract PurchasingAccountsPayableDocument getPurApSourceDocumentIfPossible();

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getPurApSourceDocumentLabelIfPossible()
     */
    public abstract String getPurApSourceDocumentLabelIfPossible();

    /**
     * @see org.kuali.core.document.DocumentBase#prepareForSave()
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        customPrepareForSave(event);
        super.prepareForSave(event);
        fixItemReferences();
    }

    /**
     * PURAP documents are all overriding this method to return false because sufficient funds checking should not be performed on
     * route of any PURAP documents. Only the Purchase Order performs a sufficient funds check and it is manually forced during
     * routing.
     * 
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#documentPerformsSufficientFundsCheck()
     */
    @Override
    public boolean documentPerformsSufficientFundsCheck() {
        return false;
    }

    /**
     * @see org.kuali.core.document.DocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(this);
        setAccountsForRouting(SpringContext.getBean(PurapAccountingService.class).generateSummary(getItems()));

        // need to refresh to get the references for the searchable attributes (ie status) and for invoking route levels (ie account
        // objects) -hjs
        refreshNonUpdateableReferences();
        for (SourceAccountingLine sourceLine : getAccountsForRouting()) {
            sourceLine.refreshNonUpdateableReferences();
        }
        super.populateDocumentForRouting();
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#isDocumentStoppedInRouteNode(NodeDetails nodeDetails)
     */
    public boolean isDocumentStoppedInRouteNode(NodeDetails nodeDetails) {
        List<String> currentRouteLevels = new ArrayList<String>();
        try {
            KualiWorkflowDocument workflowDoc = getDocumentHeader().getWorkflowDocument();
            currentRouteLevels = Arrays.asList(getDocumentHeader().getWorkflowDocument().getNodeNames());
            if (currentRouteLevels.contains(nodeDetails.getName()) && workflowDoc.isApprovalRequested()) {
                return true;
            }
            return false;
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Records the specified error message into the Log file and throws a runtime exception.
     * 
     * @param errorMessage the error message to be logged.
     */
    protected void logAndThrowRuntimeException(String errorMessage) {
        this.logAndThrowRuntimeException(errorMessage, null);
    }

    /**
     * Records the specified error message into the Log file and throws the specified runtime exception.
     * 
     * @param errorMessage the specified error message.
     * @param e the specified runtime exception.
     */
    protected void logAndThrowRuntimeException(String errorMessage, Exception e) {
        if (ObjectUtils.isNotNull(e)) {
            LOG.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
        else {
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * Allows child PO classes to customize the prepareForSave method. Most of the subclasses need to call the super's method to get
     * the GL entry creation, but they each need to do different things to prepare for those entries to be created. This is only for
     * PO since it has children classes that need different prep work for GL creation.
     * 
     * @param event the event involved in this action.
     */
    public void customPrepareForSave(KualiDocumentEvent event) {
        // Need this here so that it happens before the GL work is done
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(this);

        // These next 5 lines are temporary changes so that we can use PurApOjbCollectionHelper for release 2.
        // But these 5 lines will not be necessary anymore if the changes in PurApOjbCollectionHelper is
        // merge into Rice. KULPURAP-1370 is the related jira.
        this.allowDeleteAwareCollection = true;
        DocumentDaoOjb docDao = SpringContext.getBean(DocumentDaoOjb.class);
        PurchasingAccountsPayableDocumentBase retrievedDocument = (PurchasingAccountsPayableDocumentBase) docDao.findByDocumentHeaderId(this.getClass(), this.getDocumentNumber());
        if (retrievedDocument != null) {
            retrievedDocument.allowDeleteAwareCollection = true;
        }

        SpringContext.getBean(PurApOjbCollectionHelper.class).processCollections(docDao, this, retrievedDocument);
        this.allowDeleteAwareCollection = false;
        if (retrievedDocument != null) {
            retrievedDocument.allowDeleteAwareCollection = false;
        }
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new PurApAccountingLineParser();
    }
    
    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getPersistedSourceAccountingLinesForComparison()
     */
    @Override
    protected List getPersistedSourceAccountingLinesForComparison() {
        PurapAccountingService purApAccountingService = SpringContext.getBean(PurapAccountingService.class);
        List persistedSourceLines = new ArrayList();

        for (PurApItem item : (List<PurApItem>) this.getItems()) {
            // only check items that already have been persisted since last save
            if (ObjectUtils.isNotNull(item.getItemIdentifier())) {
                persistedSourceLines.addAll(purApAccountingService.getAccountsFromItem(item));
            }
        }
        return persistedSourceLines;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLinesForComparison()
     */
    @Override
    protected List getSourceAccountingLinesForComparison() {
        PurapAccountingService purApAccountingService = SpringContext.getBean(PurapAccountingService.class);
        List currentSourceLines = new ArrayList();
        for (PurApItem item : (List<PurApItem>) this.getItems()) {
            currentSourceLines.addAll(item.getSourceAccountingLines());
        }
        return currentSourceLines;
    }
    
    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();
        if (allowDeleteAwareCollection) {
            managedLists.add(this.getItems());
        }
        return managedLists;
    }

    /**
     * Populate the document for routing to the specified node.
     * 
     * @param routeNodeName the specified node to route to.
     * @return false.
     */
    protected boolean documentWillStopInRouteNode(String routeNodeName) {
        populateDocumentForRouting();
        return false;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("purapDocumentIdentifier", this.purapDocumentIdentifier);
        return m;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addItem(PurApItem item)
     */
    public void addItem(PurApItem item) {
        int itemLinePosition = getItemLinePosition();
        if (ObjectUtils.isNotNull(item.getItemLineNumber()) && (item.getItemLineNumber() > 0) && (item.getItemLineNumber() <= itemLinePosition)) {
            itemLinePosition = item.getItemLineNumber().intValue() - 1;
        }
        
        item.setPurapDocumentIdentifier(this.purapDocumentIdentifier);
        item.setPurapDocument(this);
        
        items.add(itemLinePosition, item);
        renumberItems(itemLinePosition);
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#deleteItem(int lineNum)
     */
    public void deleteItem(int lineNum) {
        if (items.remove(lineNum) == null) {
            // throw error here
        }
        renumberItems(lineNum);
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#renumberItems(int start)
     */
    public void renumberItems(int start) {
        for (int i = start; i < items.size(); i++) {
            PurApItem item = (PurApItem) items.get(i);
            // only set the item line number for above the line items
            if (item.getItemType().isItemTypeAboveTheLineIndicator()) {
                item.setItemLineNumber(new Integer(i + 1));
            }
        }
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#itemSwap(int positionFrom, int positionTo)
     */
    public void itemSwap(int positionFrom, int positionTo) {
        // if out of range do nothing
        if ((positionTo < 0) || (positionTo >= getItemLinePosition())) {
            return;
        }
        PurApItem item1 = this.getItem(positionFrom);
        PurApItem item2 = this.getItem(positionTo);
        Integer oldFirstPos = item1.getItemLineNumber();
        // swap line numbers
        item1.setItemLineNumber(item2.getItemLineNumber());
        item2.setItemLineNumber(oldFirstPos);
        // fix ordering in list
        items.remove(positionFrom);
        items.add(positionTo, item1);
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getItemLinePosition()
     */
    public int getItemLinePosition() {
        int belowTheLineCount = 0;
        for (PurApItem item : items) {
            if (!item.getItemType().isItemTypeAboveTheLineIndicator()) {
                belowTheLineCount++;
            }
        }
        return items.size() - belowTheLineCount;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getItem(int pos)
     */
    public PurApItem getItem(int pos) {
        return (PurApItem) items.get(pos);
    }

    /**
     * Iterates through the items of the document and returns the item with the line number equal to the number given, or null if a
     * match is not found.
     * 
     * @param lineNumber line number to match on.
     * @return the PurchasingAp Item if a match is found, else null.
     */
    public PurApItem getItemByLineNumber(int lineNumber) {
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            PurApItem item = (PurApItem) iter.next();
            if (item.getItemLineNumber().intValue() == lineNumber) {
                return item;
            }
        }
        return null;
    }

    /**
     * Overriding the parent method so that we can just set the posting year without the other stuff that the parent does to the
     * accounting period. We only store the posting year on the doc and don't want the other stuff.
     * 
     * @see org.kuali.kfs.document.LedgerPostingDocumentBase#setPostingYear(java.lang.Integer)
     */
    @Override
    public void setPostingYear(Integer postingYear) {
        this.postingYear = postingYear;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTotalDollarAmount()
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getTotalDollarAmountAllItems();
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#setTotalDollarAmount(KualiDecimal amount)
     */
    public void setTotalDollarAmount(KualiDecimal amount) {
        // do nothing, this is so that the jsp won't complain about totalDollarAmount have no setter method.
    }

    /**
     * Computes the total dollar amount of all items.
     * 
     * @return the total dollar amount of all items.
     */
    public KualiDecimal getTotalDollarAmountAllItems() {
        return getTotalDollarAmountAllItems(null);
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getTotalDollarAmountAllItems(String[] excludedTypes)
     */
    public KualiDecimal getTotalDollarAmountAllItems(String[] excludedTypes) {
        return getTotalDollarAmountWithExclusions(excludedTypes, true);
    }

    /**
     * Computes the total dollar amount of all above the line items.
     * 
     * @return the total dollar amount of all above the line items.
     */
    public KualiDecimal getTotalDollarAmountAboveLineItems() {
        return getTotalDollarAmountAboveLineItems(null);
    }

    /**
     * Computes the total dollar amount of all above the line items with the specified item types excluded.
     * 
     * @param excludedTypes the types of items to be excluded.
     * @return the total dollar amount of all above the line items with the specified item types excluded..
     */
    public KualiDecimal getTotalDollarAmountAboveLineItems(String[] excludedTypes) {
        return getTotalDollarAmountWithExclusions(excludedTypes, false);
    }

    /**
     * Computes the total dollar amount with the specified item types and possibly below the line items excluded.
     * 
     * @param excludedTypes the types of items to be excluded.
     * @param includeBelowTheLine indicates whether below the line items shall be included.
     * @return the total dollar amount with the specified item types excluded.
     */
    public KualiDecimal getTotalDollarAmountWithExclusions(String[] excludedTypes, boolean includeBelowTheLine) {
        if (excludedTypes == null) {
            excludedTypes = new String[] {};
        }

        KualiDecimal total = new KualiDecimal(BigDecimal.ZERO);
        for (PurApItem item : (List<PurApItem>) getItems()) {
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
            ItemType it = item.getItemType();
            if ((includeBelowTheLine || it.isItemTypeAboveTheLineIndicator()) && !ArrayUtils.contains(excludedTypes, it.getItemTypeCode())) {
                KualiDecimal extendedPrice = item.getExtendedPrice();
                KualiDecimal itemTotal = (extendedPrice != null) ? extendedPrice : KualiDecimal.ZERO;
                total = total.add(itemTotal);
            }
        }
        return total;
    }

    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#templateVendorAddress(VendorAddress)
     */
    public void templateVendorAddress(VendorAddress vendorAddress) {
        if (vendorAddress == null) {
            return;
        }
        this.setVendorLine1Address(vendorAddress.getVendorLine1Address());
        this.setVendorLine2Address(vendorAddress.getVendorLine2Address());
        this.setVendorCityName(vendorAddress.getVendorCityName());
        this.setVendorStateCode(vendorAddress.getVendorStateCode());
        this.setVendorPostalCode(vendorAddress.getVendorZipCode());
        this.setVendorCountryCode(vendorAddress.getVendorCountryCode());
    }

    /**
     * Returns the vendor number for this document.
     * 
     * @return the vendor number for this document.
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#getVendorNumber()
     */
    public String getVendorNumber() {
        if (StringUtils.isNotEmpty(vendorNumber)) {
            return vendorNumber;
        }
        else if (ObjectUtils.isNotNull(vendorDetail)) {
            return vendorDetail.getVendorNumber();
        }
        else
            return "";
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public Boolean getOverrideWorkflowButtons() {
        return overrideWorkflowButtons;
    }

    public void setOverrideWorkflowButtons(Boolean overrideWorkflowButtons) {
        this.overrideWorkflowButtons = overrideWorkflowButtons;
    }

    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorCustomerNumber() {
        return vendorCustomerNumber;
    }

    public void setVendorCustomerNumber(String vendorCustomerNumber) {
        this.vendorCustomerNumber = vendorCustomerNumber;
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public void setPurapDocumentIdentifier(Integer identifier) {
        this.purapDocumentIdentifier = identifier;
    }

    public Status getStatus() {
        if (ObjectUtils.isNull(this.status) && StringUtils.isNotEmpty(this.getStatusCode())) {
            this.refreshReferenceObject(PurapPropertyConstants.STATUS);
        }
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public List getItems() {
        return items;
    }

    public void setItems(List items) {
        this.items = items;
    }

    public String getVendorCityName() {
        return vendorCityName;
    }

    public void setVendorCityName(String vendorCityName) {
        this.vendorCityName = vendorCityName;
    }

    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    public String getVendorLine1Address() {
        return vendorLine1Address;
    }

    public void setVendorLine1Address(String vendorLine1Address) {
        this.vendorLine1Address = vendorLine1Address;
    }

    public String getVendorLine2Address() {
        return vendorLine2Address;
    }

    public void setVendorLine2Address(String vendorLine2Address) {
        this.vendorLine2Address = vendorLine2Address;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorPostalCode() {
        return vendorPostalCode;
    }

    public void setVendorPostalCode(String vendorPostalCode) {
        this.vendorPostalCode = vendorPostalCode;
    }

    public String getVendorStateCode() {
        return vendorStateCode;
    }

    public void setVendorStateCode(String vendorStateCode) {
        this.vendorStateCode = vendorStateCode;
    }
    
    public String getVendorAddressInternationalProvinceName() {
        return vendorAddressInternationalProvinceName;
    }

    public void setVendorAddressInternationalProvinceName(String vendorAddressInternationalProvinceName) {
        this.vendorAddressInternationalProvinceName = vendorAddressInternationalProvinceName;
    }

    public Integer getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    public void setVendorAddressGeneratedIdentifier(Integer vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public String[] getBelowTheLineTypes() {
        if (this.belowTheLineTypes == null) {
            this.belowTheLineTypes = SpringContext.getBean(PurapService.class).getBelowTheLineForDocument(this);
        }
        return belowTheLineTypes;
    }

    public Country getVendorCountry() {
        return vendorCountry;
    }

    /**
     * Added only to allow for {@link org.kuali.module.purap.util.PurApObjectUtils} class to work correctly.
     * 
     * @deprecated
     */
    public void setVendorCountry(Country vendorCountry) {
        this.vendorCountry = vendorCountry;
    }

    public List<SourceAccountingLine> getAccountsForRouting() {
        return accountsForRouting;
    }

    public void setAccountsForRouting(List<SourceAccountingLine> accountsForRouting) {
        this.accountsForRouting = accountsForRouting;
    }
    
    /**
     * Determines whether the account is debit. It always returns false.
     * 
     * @param financialDocument The document containing the account to be validated.
     * @param accountingLine The account to be validated.
     * @return boolean false.
     * @see org.kuali.kfs.rule.AccountingLineRule#isDebit(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }
    
    public PurApRelatedViews getRelatedViews() {
        if (relatedViews == null) {
            relatedViews = new PurApRelatedViews(this.documentNumber, this.accountsPayablePurchasingDocumentLinkIdentifier);
        }
        return relatedViews;
    }

    public void setRelatedViews(PurApRelatedViews relatedViews) {
        this.relatedViews = relatedViews;
    }

    @Override
    public void refreshNonUpdateableReferences() {

        super.refreshNonUpdateableReferences();
        fixItemReferences();
    }

    /**
     * This method fixes the item references in this document if it's new
     */
    public void fixItemReferences() {
        //fix item and account references in case this is a new doc (since they will be lost)
        if(ObjectUtils.isNull(this.purapDocumentIdentifier)) {
            for (PurApItem item : (List<PurApItem>)this.getItems()) {
                item.setPurapDocument(this);
                item.fixAccountReferences();
            }
        }
    }

}
