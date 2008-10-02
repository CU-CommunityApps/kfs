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
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ojb.broker.PersistenceBrokerAware;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Purap Item Business Object.
 */
public interface PurApItem extends PersistableBusinessObject, PersistenceBrokerAware, PurapEnterableItem {

    public abstract Integer getItemIdentifier();

    public abstract void setItemIdentifier(Integer ItemIdentifier);

    public abstract Integer getItemLineNumber();

    public abstract void setItemLineNumber(Integer itemLineNumber);

    public abstract String getItemUnitOfMeasureCode();

    public abstract void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode);

    public abstract String getItemCatalogNumber();

    public abstract void setItemCatalogNumber(String itemCatalogNumber);

    public abstract String getItemDescription();

    public abstract void setItemDescription(String itemDescription);

    public abstract BigDecimal getItemUnitPrice();

    public abstract void setItemUnitPrice(BigDecimal itemUnitPrice);

    public abstract String getItemTypeCode();

    public abstract void setItemTypeCode(String itemTypeCode);

    public abstract String getItemAuxiliaryPartIdentifier();

    public abstract void setItemAuxiliaryPartIdentifier(String itemAuxiliaryPartIdentifier);

    public abstract String getExternalOrganizationB2bProductReferenceNumber();

    public abstract void setExternalOrganizationB2bProductReferenceNumber(String externalOrganizationB2bProductReferenceNumber);

    public abstract String getExternalOrganizationB2bProductTypeName();

    public abstract void setExternalOrganizationB2bProductTypeName(String externalOrganizationB2bProductTypeName);

    public abstract boolean getItemAssignedToTradeInIndicator();

    public abstract void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator);

    public abstract ItemType getItemType();

    /**
     * Sets the itemType attribute.
     * 
     * @param itemType The itemType to set.
     * @deprecated
     */
    public abstract void setItemType(ItemType itemType);

    /**
     * This method resets the transient new account method
     */
    public void resetAccount();

    public KualiDecimal getExtendedPrice();

    public KualiDecimal getTotalAmount();

    public KualiDecimal calculateExtendedPrice();

    public void setExtendedPrice(KualiDecimal extendedPrice);

    public KualiDecimal getItemTaxAmount();

    public void setItemTaxAmount(KualiDecimal itemTaxAmount);

    public PurApAccountingLine getNewSourceLine();

    public void setNewSourceLine(PurApAccountingLine newAccountingLine);

    public abstract Class getAccountingLineClass();

    public List<PurApAccountingLine> getSourceAccountingLines();

    public void setSourceAccountingLines(List<PurApAccountingLine> purapAccountingLines);
    
    public List<PurApAccountingLine> getBaselineSourceAccountingLines();

    public KualiDecimal getItemQuantity();

    public void setItemQuantity(KualiDecimal itemQuantity);

    public String getItemIdentifierString();

    public PurApSummaryItem getSummaryItem();
    
    public <T extends PurchasingAccountsPayableDocument> T getPurapDocument();
    
    public void setPurapDocument(PurchasingAccountsPayableDocument purapDoc);
    
    public Integer getPurapDocumentIdentifier();
    
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier);
    
    public void fixAccountReferences();
}
