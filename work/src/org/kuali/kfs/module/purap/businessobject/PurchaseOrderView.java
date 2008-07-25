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

import java.sql.Timestamp;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Purchase Order View Business Object.
 */
public class PurchaseOrderView extends AbstractRelatedView {

    private Boolean purchaseOrderCurrentIndicator;
    private List<Note> notes;

    private String purchaseOrderStatusCode;
    private String recurringPaymentTypeCode;
    private String vendorChoiceCode;
    private Timestamp recurringPaymentEndDate;
    private KualiDecimal totalEncumbrance;
    private KualiDecimal totalAmount;
    
    public boolean isPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    public boolean getPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    public void setPurchaseOrderCurrentIndicator(boolean purchaseOrderCurrentIndicator) {
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
    }

    public String getPurchaseOrderStatusCode() {
        return purchaseOrderStatusCode;
    }

    public void setPurchaseOrderStatusCode(String purchaseOrderStatusCode) {
        this.purchaseOrderStatusCode = purchaseOrderStatusCode;
    }

    public String getRecurringPaymentTypeCode() {
        return recurringPaymentTypeCode;
    }

    public void setRecurringPaymentTypeCode(String recurringPaymentTypeCode) {
        this.recurringPaymentTypeCode = recurringPaymentTypeCode;
    }

    public String getVendorChoiceCode() {
        return vendorChoiceCode;
    }

    public void setVendorChoiceCode(String vendorChoiceCode) {
        this.vendorChoiceCode = vendorChoiceCode;
    }

    public Timestamp getRecurringPaymentEndDate() {
        return recurringPaymentEndDate;
    }

    public void setRecurringPaymentEndDate(Timestamp recurringPaymentEndDate) {
        this.recurringPaymentEndDate = recurringPaymentEndDate;
    }

    public KualiDecimal getTotalEncumbrance() {
        return totalEncumbrance;
    }

    public void setTotalEncumbrance(KualiDecimal totalEncumbrance) {
        this.totalEncumbrance = totalEncumbrance;
    }

    public void setPurchaseOrderCurrentIndicator(Boolean purchaseOrderCurrentIndicator) {
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
    }
    
    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * The next four methods are overridden but shouldnt be! If they arent overridden, they dont show up in the tag, not sure why at
     * this point! (AAP)
     * 
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getPurapDocumentIdentifier()
     */
    @Override
    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }

    @Override
    public String getDocumentIdentifierString() {
        return super.getDocumentIdentifierString();
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getDocumentNumber()
     */
    @Override
    public String getDocumentNumber() {
        return super.getDocumentNumber();
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getNotes()
     */
    @Override
    public List<Note> getNotes() {
        if (this.isPurchaseOrderCurrentIndicator()) {
            if (notes == null) {
                notes = new TypedArrayList(Note.class);
                List<Note> tmpNotes = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderNotes(this.getPurapDocumentIdentifier());
                for (Note note : tmpNotes) {
                    notes.add(note);
                }
            }
        }
        else {
            notes = null;
        }
        return notes;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getUrl()
     */
    @Override
    public String getUrl() {
        return super.getUrl();
    }
    
}
