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
package org.kuali.kfs.module.purap.document.validation;

import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.module.purap.businessobject.PurApItem;

/**
 * Add Purchasing Accounts Payable Item Rule Interface.
 * Defines a rule which gets invoked immediately before an item line is added to a Purchasing Accounts Payable Document.
 */
public interface AddPurchasingAccountsPayableItemRule<F extends AccountingDocument> extends PurchasingAccountsPayableItemRule {

    /**
     * Checks all the business rules relevant to adding an Item
     * 
     * @param item the PurApItem to check
     * @param financialDocument the PurApDocument to check 
     * @return true if the business rules pass
     */
    public boolean processAddItemBusinessRules(F financialDocument, PurApItem item);
}
