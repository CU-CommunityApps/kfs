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
package org.kuali.kfs.module.ar.document.service;

import java.util.Collection;

import org.kuali.kfs.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

public interface CustomerCreditMemoDocumentService {
    
    /**
     * This method recalculates customer credit memo document based on the user input
     * @return
     */
    public void recalculateCustomerCreditMemoDocument(CustomerCreditMemoDocument customerCreditMemoDocument, boolean blanketApproveDocumentEventFlag);

    
    public Collection<CustomerCreditMemoDocument> getCustomerCreditMemoDocumentByInvoiceDocument(String customerInvoiceDocumentNumber);

    /**
     * This method checks if there is no data to submit
     * @return
     */
    public boolean isThereNoDataToSubmit(CustomerCreditMemoDocument customerCreditMemoDocument);

}