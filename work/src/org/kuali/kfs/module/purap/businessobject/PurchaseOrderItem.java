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

package org.kuali.kfs.module.purap.businessobject;

import static org.kuali.rice.kns.util.KualiDecimal.ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Purchase Order Item Business Object.
 */
public class PurchaseOrderItem extends PurchasingItemBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderItem.class);

    private String documentNumber;
    private KualiDecimal itemInvoicedTotalQuantity;
    private KualiDecimal itemInvoicedTotalAmount;
    private KualiDecimal itemReceivedTotalQuantity;
    private KualiDecimal itemOutstandingEncumberedQuantity;
    private KualiDecimal itemOutstandingEncumberedAmount;
    private boolean itemActiveIndicator = true;
    private KualiDecimal itemDamagedTotalQuantity;
    
    private PurchaseOrderDocument purchaseOrder;
    
    // Not persisted to DB
    private boolean itemSelectedForRetransmitIndicator;
    private boolean movingToSplit;

    /**
     * Default constructor.
     */
    public PurchaseOrderItem() {

    }

    /**
     * Constructor.
     * 
     * @param ri - Requisition Item
     * @param po - Purchase Order Document
     */
    public PurchaseOrderItem(RequisitionItem ri, PurchaseOrderDocument po) {
        super();

        this.setPurchaseOrder(po);

        Integer itemIdentifier = new Integer(SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber("PO_ITM_ID").toString());
        this.setItemIdentifier(itemIdentifier);        
        this.setItemLineNumber(ri.getItemLineNumber());
        this.setItemUnitOfMeasureCode(ri.getItemUnitOfMeasureCode());
        this.setItemQuantity(ri.getItemQuantity());
        this.setItemCatalogNumber(ri.getItemCatalogNumber());
        this.setItemDescription(ri.getItemDescription());
        this.setItemUnitPrice(ri.getItemUnitPrice());
        this.setItemAuxiliaryPartIdentifier(ri.getItemAuxiliaryPartIdentifier());
        this.setItemAssignedToTradeInIndicator(ri.getItemAssignedToTradeInIndicator());

        this.setExternalOrganizationB2bProductReferenceNumber(ri.getExternalOrganizationB2bProductReferenceNumber());
        this.setExternalOrganizationB2bProductTypeName(ri.getExternalOrganizationB2bProductTypeName());

        this.setItemReceivedTotalQuantity(ZERO);
        this.setItemDamagedTotalQuantity(ZERO);
        
        this.setItemTypeCode(ri.getItemTypeCode());

        if (ri.getSourceAccountingLines() != null && ri.getSourceAccountingLines().size() > 0 && 
                !StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,ri.getItemType().getItemTypeCode())) {
            List accounts = new ArrayList();
            for (PurApAccountingLine account : ri.getSourceAccountingLines()) {
                PurchaseOrderAccount poAccount = new PurchaseOrderAccount(account);
                poAccount.setPurchaseOrderItem(this);
                accounts.add(poAccount);
            }
            this.setSourceAccountingLines(accounts);
        }
        // By default, set the item active indicator to true.
        // In amendment, the user can set it to false when they click on
        // the inactivate button.
        this.setItemActiveIndicator(true);
        
        this.setPurchasingCommodityCode(ri.getPurchasingCommodityCode());
        this.setCommodityCode(getCommodityCode());
        
        // If the RequisitionItem has a CapitalAssetItem, create a new PurchasingCapitalAssetItem and add it to the PO.
        if( ri.getCapitalAssetItem() != null ) {
            PurchaseOrderCapitalAssetItem newPOCapitalAssetItem = new PurchaseOrderCapitalAssetItem((RequisitionCapitalAssetItem)ri.getCapitalAssetItem(), itemIdentifier);
            this.setCapitalAssetItem(newPOCapitalAssetItem);
            po.getPurchasingCapitalAssetItems().add(newPOCapitalAssetItem);
        }
    }

    public boolean isItemActiveIndicator() {
        return itemActiveIndicator;
    }

    public void setItemActiveIndicator(boolean itemActiveIndicator) {
        this.itemActiveIndicator = itemActiveIndicator;
    }

    public KualiDecimal getItemInvoicedTotalAmount() {
        return itemInvoicedTotalAmount;
    }

    public void setItemInvoicedTotalAmount(KualiDecimal itemInvoicedTotalAmount) {
        this.itemInvoicedTotalAmount = itemInvoicedTotalAmount;
    }

    public KualiDecimal getItemInvoicedTotalQuantity() {
        return itemInvoicedTotalQuantity;
    }

    public void setItemInvoicedTotalQuantity(KualiDecimal itemInvoicedTotalQuantity) {
        this.itemInvoicedTotalQuantity = itemInvoicedTotalQuantity;
    }

    public KualiDecimal getItemOutstandingEncumberedQuantity() {
        return itemOutstandingEncumberedQuantity;
    }

    public void setItemOutstandingEncumberedQuantity(KualiDecimal itemOutstandingEncumberedQuantity) {
        this.itemOutstandingEncumberedQuantity = itemOutstandingEncumberedQuantity;
    }

    public KualiDecimal getItemOutstandingEncumberedAmount() {
        return itemOutstandingEncumberedAmount;
    }

    public void setItemOutstandingEncumberedAmount(KualiDecimal itemOutstandingEncumbranceAmount) {
        this.itemOutstandingEncumberedAmount = itemOutstandingEncumbranceAmount;
    }

    public KualiDecimal getItemReceivedTotalQuantity() {
        return itemReceivedTotalQuantity;
    }

    public void setItemReceivedTotalQuantity(KualiDecimal itemReceivedTotalQuantity) {
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
    }

    /**
     * Gets the itemDamagedTotalQuantity attribute. 
     * @return Returns the itemDamagedTotalQuantity.
     */
    public KualiDecimal getItemDamagedTotalQuantity() {
        return itemDamagedTotalQuantity;
    }

    /**
     * Sets the itemDamagedTotalQuantity attribute value.
     * @param itemDamagedTotalQuantity The itemDamagedTotalQuantity to set.
     */
    public void setItemDamagedTotalQuantity(KualiDecimal itemDamagedTotalQuantity) {
        this.itemDamagedTotalQuantity = itemDamagedTotalQuantity;
    }

    public PurchaseOrderDocument getPurchaseOrder() {
        if (ObjectUtils.isNull(purchaseOrder)) {
            refreshReferenceObject(PurapPropertyConstants.PURAP_DOC);
        }
        return super.getPurapDocument();
    }

    public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        setPurapDocument(purchaseOrder);
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public boolean isItemSelectedForRetransmitIndicator() {
        return itemSelectedForRetransmitIndicator;
    }

    public void setItemSelectedForRetransmitIndicator(boolean itemSelectedForRetransmitIndicator) {
        this.itemSelectedForRetransmitIndicator = itemSelectedForRetransmitIndicator;
    }    

    public boolean isMovingToSplit() {
        return movingToSplit;
    }

    public void setMovingToSplit(boolean movingToSplit) {
        this.movingToSplit = movingToSplit;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.getItemIdentifier() != null) {
            m.put("itemIdentifier", this.getItemIdentifier().toString());
        }
        return m;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItem#getAccountingLineClass()
     */
    public Class getAccountingLineClass() {
        return PurchaseOrderAccount.class;
    }
    
    /**
     * 
     * This method returns the total item paid amount
     * @return
     */
    public KualiDecimal getItemPaidAmount() {
        if (!(this.isItemActiveIndicator())) {
            return KualiDecimal.ZERO;
        }
        return this.getItemInvoicedTotalAmount();
    }

    public KualiDecimal getItemEncumbranceRelievedAmount() {
        // check that it is active else return zero
        if (this == null || !this.isItemActiveIndicator()) {
            return KualiDecimal.ZERO;
        }
        // setup outstanding amount and get totalEncumberance from this.getExtendedCost()
        KualiDecimal outstandingAmount = KualiDecimal.ZERO;
        KualiDecimal totalEncumberance = this.getExtendedPrice();

        ItemType iT = this.getItemType();
        // if service add the po outstanding amount to outstanding amount
        if (!iT.isQuantityBasedGeneralLedgerIndicator()) {
            outstandingAmount = outstandingAmount.add(this.getItemOutstandingEncumberedAmount());
        }
        else {
            // else add outstanding quantity * unit price
            BigDecimal qty = new BigDecimal(this.getOutstandingQuantity().toString());
            outstandingAmount = outstandingAmount.add(new KualiDecimal(this.getItemUnitPrice().multiply(qty)));
        }


        // return the total encumbrance subtracted by the outstanding amount from above
        return totalEncumberance.subtract(outstandingAmount);
    }
    
    public KualiDecimal getOutstandingQuantity() {
            KualiDecimal outstandingQuantity = (this.getItemQuantity() != null) ? this.getItemQuantity() : KualiDecimal.ZERO;
            KualiDecimal invoicedQuantity = (this.getItemInvoicedTotalQuantity() != null) ? this.getItemInvoicedTotalQuantity() : KualiDecimal.ZERO;
            return outstandingQuantity.subtract(invoicedQuantity);
    }
    
    public boolean isCanInactivateItem() {
        if (versionNumber == null) {
            // don't allow newly added item to be inactivatable.
            return false;
        }
        else if (versionNumber != null && itemActiveIndicator && !getPurchaseOrder().getContainsUnpaidPaymentRequestsOrCreditMemos()) {
            return true;
        }
        return false;
    }
        
    /**
     * Override the method in PurApItemBase so that if the item is
     * not eligible to be displayed in the account summary tab,
     * which is if the item is inactive, we'll return null and
     * the item won't be added to the list of account summary.
     * 
     * @see org.kuali.kfs.module.purap.businessobject.PurApItemBase#getSummaryItem()
     */
    @Override
    public PurApSummaryItem getSummaryItem() {
        if (!this.itemActiveIndicator) {
            return null;
        }
        else {
            return super.getSummaryItem();
        }
    }

    public boolean isNewUnorderedItem(){
        return SpringContext.getBean(PurchaseOrderService.class).isNewUnorderedItem(this);
    }
}
