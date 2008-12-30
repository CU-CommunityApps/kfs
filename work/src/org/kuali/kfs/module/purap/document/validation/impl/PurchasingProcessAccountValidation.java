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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.integration.purap.PurApItem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;

public class PurchasingProcessAccountValidation extends PurchasingAccountsPayableProcessAccountValidation {
    
    /**
     * Overrides the method in PurchasingAccountsPayableDocumentRuleBase to also invoke the validateAccountNotExpired for each of
     * the accounts.
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        for (PurApAccountingLine accountingLine : getItemForValidation().getSourceAccountingLines()) {
            boolean notExpired = this.validateAccountNotExpired(accountingLine);
            if (!notExpired) {
                GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_ACCOUNT_EXPIRED, getItemForValidation().getItemIdentifierString() + " has ", accountingLine.getAccount().getAccountNumber());
            }
        }
        valid &= super.validate(event);

        return valid;
    }

    /**
     * Validates that the account is not expired.
     * 
     * @param accountingLine The account to be validated.
     * @return boolean false if the account is expired.
     */
    private boolean validateAccountNotExpired(AccountingLine accountingLine) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        if (accountingLine.getAccount() != null && accountingLine.getAccount().isExpired()) {

            return false;
        }

        return true;
    }
    
}
