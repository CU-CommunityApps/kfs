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
package org.kuali.module.purap.rule;

import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.purap.bo.PurApItem;

public interface ImportPurchasingAccountsPayableItemRule <F extends AccountingDocument> extends PurchasingAccountsPayableItemRule {

    /**
     * Checks all the business rules relevant to importing items.
     * 
     * @param item the PurApItem to check
     * @param financialDocument the PurApDocument to check 
     * @return true if the business rules pass
     */
    public boolean processImportItemBusinessRules(F financialDocument, PurApItem item);
}
