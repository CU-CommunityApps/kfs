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
package org.kuali.kfs.module.purap.document.service;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;

/**
 * This interface is a non spring managed interface that is implemented by both PaymentRequestService and CreditMemoService
 */
public interface AccountsPayableDocumentSpecificService {
    
    /**
     * Saves the given document without validation.
     * 
     * @param apDoc     An AccountsPayableDocument
     */
    public void saveDocumentWithoutValidation(AccountsPayableDocument apDoc);
    
    public boolean shouldPurchaseOrderBeReversed(AccountsPayableDocument apDoc);

    public void takePurchaseOrderCancelAction(AccountsPayableDocument apDoc);

    public UniversalUser getUniversalUserForCancel(AccountsPayableDocument apDoc);

    public String updateStatusByNode(String currentNodeName, AccountsPayableDocument apDoc);

    public boolean poItemEligibleForAp(AccountsPayableDocument apDoc, PurchaseOrderItem poi);
    
    /**
     * Generates the general ledger entries that need to be created by an AccountsPayableDocument
     * of the specific type of the given AP document.
     * 
     * @param apDoc     An AccountsPayableDocument
     */
    public void generateGLEntriesCreateAccountsPayableDocument(AccountsPayableDocument apDoc);
}
