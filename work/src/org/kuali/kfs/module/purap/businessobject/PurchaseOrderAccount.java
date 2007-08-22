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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;

/**
 * 
 */
public class PurchaseOrderAccount extends PurApAccountingLineBase {


    /**
     * NOTE FOR POTENTIAL ACCOUNTING LINE REFACTORING documentNumber is needed for PO accounts and not for other PURAP docs, however
     * it is already defined in AccountingLineBase so we don't need to add it here
     */
    // private String documentNumber;
    private KualiDecimal itemAccountOutstandingEncumbranceAmount;
    private KualiDecimal accountLineAmount;

    private PurchaseOrderItem purchaseOrderItem;


    /**
     * Default constructor.
     */
    public PurchaseOrderAccount() {

    }

    public PurchaseOrderAccount(PurApAccountingLine ra) {
        this.setAccountLinePercent(ra.getAccountLinePercent());
        this.setAccountNumber(ra.getAccountNumber());
        this.setChartOfAccountsCode(ra.getChartOfAccountsCode());
        this.setFinancialObjectCode(ra.getFinancialObjectCode());
        this.setFinancialSubObjectCode(ra.getFinancialSubObjectCode());
        this.setOrganizationReferenceId(ra.getOrganizationReferenceId());
        this.setProjectCode(ra.getProjectCode());
        this.setSubAccountNumber(ra.getSubAccountNumber());
    }

    public KualiDecimal getAlternateAmount() {
        if (ObjectUtils.isNull(super.getAlternateAmount())) {
            return getItemAccountOutstandingEncumbranceAmount();
        }
        return super.getAlternateAmount();
    }

    /**
     * Gets the itemAccountOutstandingEncumbranceAmount attribute.
     * 
     * @return Returns the itemAccountOutstandingEncumbranceAmount
     */
    public KualiDecimal getItemAccountOutstandingEncumbranceAmount() {
        return itemAccountOutstandingEncumbranceAmount;
    }

    /**
     * Sets the itemAccountOutstandingEncumbranceAmount attribute.
     * 
     * @param itemAccountOutstandingEncumbranceAmount The itemAccountOutstandingEncumbranceAmount to set.
     */
    public void setItemAccountOutstandingEncumbranceAmount(KualiDecimal itemAccountOutstandingEncumbranceAmount) {
        this.itemAccountOutstandingEncumbranceAmount = itemAccountOutstandingEncumbranceAmount;
    }

    /**
     * Gets the accountLineAmount attribute. 
     * @return Returns the accountLineAmount.
     */
    public KualiDecimal getAccountLineAmount() {
        return accountLineAmount;
    }

    /**
     * Sets the accountLineAmount attribute value.
     * @param accountLineAmount The accountLineAmount to set.
     */
    public void setAccountLineAmount(KualiDecimal accountLineAmount) {
        this.accountLineAmount = accountLineAmount;
    }

    /**
     * Gets the purchaseOrderItem attribute.
     * 
     * @return Returns the purchaseOrderItem.
     */
    public PurchaseOrderItem getPurchaseOrderItem() {
        return purchaseOrderItem;
    }

    /**
     * Sets the purchaseOrderItem attribute value.
     * 
     * @param purchaseOrderItem The purchaseOrderItem to set.
     * @deprecated
     */
    public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
        this.purchaseOrderItem = purchaseOrderItem;
    }

}
