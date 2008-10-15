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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Item line Business Object for Credit Memo Document.
 */
public class CreditMemoItem extends AccountsPayableItemBase {
    private KualiDecimal poInvoicedTotalQuantity;
    private BigDecimal poUnitPrice;
    private KualiDecimal poTotalAmount;
    private KualiDecimal preqInvoicedTotalQuantity;
    private BigDecimal preqUnitPrice;
    private KualiDecimal preqTotalAmount;

    /**
     * Default constructor.
     */
    public CreditMemoItem() {
    }

    /**
     * Constructs a CreditMemoItem object from an existing Purchase Order Item. - Delegate
     * 
     * @param cmDocument the Credit Memo Document this item belongs to.
     * @param poItem the Purchase Order Item to copy from.
     */
    public CreditMemoItem(CreditMemoDocument cmDocument, PurchaseOrderItem poItem) {
        this(cmDocument, poItem, new HashMap<String, ExpiredOrClosedAccountEntry>());
    }

    /**
     * Constructs a CreditMemoItem object from an existing Purchase Order Item, and check and process expired or closed accounts
     * item might contain.
     * 
     * @param cmDocument the Credit Memo Document this item belongs to.
     * @param poItem the Purchase Order Item to copy from.
     * @param expiredOrClosedAccountList the list of expired or closed accounts to check against.
     */
    public CreditMemoItem(CreditMemoDocument cmDocument, PurchaseOrderItem poItem, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setItemLineNumber(poItem.getItemLineNumber());
        setPoInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
        setPoUnitPrice(poItem.getItemUnitPrice());
        setPoTotalAmount(poItem.getItemInvoicedTotalAmount());
        setItemTypeCode(poItem.getItemTypeCode());
        
        //recalculate tax
        SpringContext.getBean(PurapService.class).calculateTax(cmDocument);
        
        if ((ObjectUtils.isNotNull(this.getItemType()) && this.getItemType().isAmountBasedGeneralLedgerIndicator())) {
            // setting unit price to be null to be more consistent with other below the line
            this.setItemUnitPrice(null);
        }
        else {
            setItemUnitPrice(poItem.getItemUnitPrice());
        }

        setItemCatalogNumber(poItem.getItemCatalogNumber());
        setItemDescription(poItem.getItemDescription());

        if (getPoInvoicedTotalQuantity() == null) {
            setPoInvoicedTotalQuantity(KualiDecimal.ZERO);
        }
        if (getPoUnitPrice() == null) {
            setPoUnitPrice(BigDecimal.ZERO);
        }
        if (getPoTotalAmount() == null) {
            setPoTotalAmount(KualiDecimal.ZERO);
        }
        
        for (Iterator iter = poItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();

            // check if this account is expired/closed and replace as needed
            SpringContext.getBean(AccountsPayableService.class).processExpiredOrClosedAccount(account, expiredOrClosedAccountList);

            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }

    /**
     * Constructs a CreditMemoItem object from an existing Payment Request or Purchase Order Item.
     * 
     * @param cmDocument the Credit Memo Document this item belongs to.
     * @param preqItem the Payment Request Item to copy from.
     * @param poItem the Purchase Order Item to copy from.
     */
    public CreditMemoItem(CreditMemoDocument cmDocument, PaymentRequestItem preqItem, PurchaseOrderItem poItem) {
        this(cmDocument, preqItem, poItem, new HashMap<String, ExpiredOrClosedAccountEntry>());
        //FIXME: delete this after verifying that we don't need anything from this in the other constructor
//
//        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
//        setItemLineNumber(preqItem.getItemLineNumber());
//
//        // take invoiced quantities from the lower of the preq and po if different
//        if (poItem.getItemInvoicedTotalQuantity() != null && preqItem.getItemQuantity() != null && poItem.getItemInvoicedTotalQuantity().isLessThan(preqItem.getItemQuantity())) {
//            setPreqInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
//            setPreqTotalAmount(poItem.getItemInvoicedTotalAmount());
//        }
//        else {
//            setPreqInvoicedTotalQuantity(preqItem.getItemQuantity());
//            setPreqTotalAmount(preqItem.getTotalAmount());
//        }
//
//        setPreqUnitPrice(preqItem.getItemUnitPrice());
//        setItemTypeCode(preqItem.getItemTypeCode());
//
//        setCapitalAssetTransactionTypeCode(preqItem.getCapitalAssetTransactionTypeCode());
//        
//        if ((ObjectUtils.isNotNull(this.getItemType()) && !this.getItemType().isQuantityBasedGeneralLedgerIndicator())) {
//            // setting unit price to be null to be more consistent with other below the line
//            this.setItemUnitPrice(null);
//        }
//        else {
//            setItemUnitPrice(preqItem.getItemUnitPrice());
//        }
//
//        setItemCatalogNumber(preqItem.getItemCatalogNumber());
//        setItemDescription(preqItem.getItemDescription());
//
//        if (getPreqInvoicedTotalQuantity() == null) {
//            setPreqInvoicedTotalQuantity(KualiDecimal.ZERO);
//        }
//        if (getPreqUnitPrice() == null) {
//            setPreqUnitPrice(BigDecimal.ZERO);
//        }
//        if (getPreqTotalAmount() == null) {
//            setPreqTotalAmount(KualiDecimal.ZERO);
//        }
//
//        for (Iterator iter = preqItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
//            PaymentRequestAccount account = (PaymentRequestAccount) iter.next();
//            getSourceAccountingLines().add(new CreditMemoAccount(account));
//        }
    }

    /**
     * Constructs a CreditMemoItem object from an existing Payment Request Item, and check and process expired or closed accounts
     * item might contain.
     * 
     * @param cmDocument the Credit Memo Document this item belongs to.
     * @param preqItem the Payment Request Item to copy from.
     * @param poItem the Purchase Order Item to copy from.
     * @param expiredOrClosedAccountList the list of expired or closed accounts to check against.
     */
    public CreditMemoItem(CreditMemoDocument cmDocument, PaymentRequestItem preqItem, PurchaseOrderItem poItem, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setItemLineNumber(preqItem.getItemLineNumber());
        this.setPurapDocument(cmDocument);
        
        // take invoiced quantities from the lower of the preq and po if different
        if (poItem.getItemInvoicedTotalQuantity() != null && preqItem.getItemQuantity() != null && poItem.getItemInvoicedTotalQuantity().isLessThan(preqItem.getItemQuantity())) {
            setPreqInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
            setPreqTotalAmount(poItem.getItemInvoicedTotalAmount());
        }
        else {
            setPreqInvoicedTotalQuantity(preqItem.getItemQuantity());
            setPreqTotalAmount(preqItem.getTotalAmount());
        }

        this.setItemTaxAmount( preqItem.getItemTaxAmount() );
        
        //copy use tax items over, and blank out keys (useTaxId and itemIdentifier)
        for(PurApItemUseTax useTaxItem : preqItem.getUseTaxItems()){
            PurApItemUseTax copyUseTaxItem = useTaxItem;
            copyUseTaxItem.setUseTaxId(null);
            copyUseTaxItem.setItemIdentifier(null);
            this.getUseTaxItems().add(copyUseTaxItem);
        }

        setPreqUnitPrice(preqItem.getItemUnitPrice());
        setItemTypeCode(preqItem.getItemTypeCode());
        setItemUnitPrice(preqItem.getItemUnitPrice());
        setItemCatalogNumber(preqItem.getItemCatalogNumber());
        setItemDescription(preqItem.getItemDescription());
        
        setCapitalAssetTransactionTypeCode(preqItem.getCapitalAssetTransactionTypeCode());

        if (getPreqInvoicedTotalQuantity() == null) {
            setPreqInvoicedTotalQuantity(KualiDecimal.ZERO);
        }
        if (getPreqUnitPrice() == null) {
            setPreqUnitPrice(BigDecimal.ZERO);
        }
        if (getPreqTotalAmount() == null) {
            setPreqTotalAmount(KualiDecimal.ZERO);
        }

        for (Iterator iter = preqItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PaymentRequestAccount account = (PaymentRequestAccount) iter.next();

            // check if this account is expired/closed and replace as needed
            SpringContext.getBean(AccountsPayableService.class).processExpiredOrClosedAccount(account, expiredOrClosedAccountList);

            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItemBase#getAccountingLineClass()
     */
    @Override
    public Class<CreditMemoAccount> getAccountingLineClass() {
        return CreditMemoAccount.class;
    }
   
    public KualiDecimal getPoTotalAmount() {
        return poTotalAmount;
    }

    public void setPoTotalAmount(KualiDecimal poTotalAmount) {
        this.poTotalAmount = poTotalAmount;
    }

    public KualiDecimal getPoInvoicedTotalQuantity() {
        return poInvoicedTotalQuantity;
    }

    public void setPoInvoicedTotalQuantity(KualiDecimal poInvoicedTotalQuantity) {
        this.poInvoicedTotalQuantity = poInvoicedTotalQuantity;
    }

    public BigDecimal getPoUnitPrice() {
        return poUnitPrice;
    }

    public void setPoUnitPrice(BigDecimal poUnitPrice) {
        this.poUnitPrice = poUnitPrice;
    }

    public KualiDecimal getPreqTotalAmount() {
        return preqTotalAmount;
    }

    public void setPreqTotalAmount(KualiDecimal preqTotalAmount) {
        this.preqTotalAmount = preqTotalAmount;
    }

    public KualiDecimal getPreqInvoicedTotalQuantity() {
        return preqInvoicedTotalQuantity;
    }

    public void setPreqInvoicedTotalQuantity(KualiDecimal preqInvoicedTotalQuantity) {
        this.preqInvoicedTotalQuantity = preqInvoicedTotalQuantity;
    }

    public BigDecimal getPreqUnitPrice() {
        return preqUnitPrice;
    }

    public void setPreqUnitPrice(BigDecimal preqUnitPrice) {
        this.preqUnitPrice = preqUnitPrice;
    }

    @Override
    public Class getUseTaxClass() {
        return CreditMemoItemUseTax.class;
    }
}
