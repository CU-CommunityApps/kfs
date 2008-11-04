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

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;

public interface CustomerInvoiceWriteoffDocumentService {
    
    /**
     * This method setups any default values for a new customer invoice document 
     * @param customerInvoiceWriteoffDocument
     */
    public void setupDefaultValuesForNewCustomerInvoiceWriteoffDocument(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument);
    
    /**
     * This method returns true if a customer invoice writeoff document is approved
     * @param customerInvoiceWriteoffDocumentNumber
     * @return
     */
    public boolean isCustomerInvoiceWriteoffDocumentApproved(String customerInvoiceWriteoffDocumentNumber);
    
    /**
     * This method returns a collection of customer invoice documents that are eligible for writeoff
     * @return
     */
    public Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceDocumentsForInvoiceWriteoffLookup();
    
    /**
     * This method initiates customer invoice writeoff documents based on a collection of customer invoice writeoff lookup results
     * @param customerInvoiceWriteoffLookupResults
     */
    public void createCustomerInvoiceWriteoffDocuments(String personId, Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults ) throws WorkflowException;

    /**
     * 
     * Accepts a lookup result and creates a batch file dropped into the batch system for later asynchronous 
     * processing.
     * 
     * @param personId
     * @param customerInvoiceWriteoffLookupResults
     * @return filename and path of created batch file
     */
    public String sendCustomerInvoiceWriteoffDocumentsToBatch(Person person, Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults);
    
    /**
     * 
     * Creates a new Invoice Writeoff Document based on the indicated Invoice doc number and the initiator.
     * @param initiator Person who initiated the writeoffs.
     * @param invoiceNumber Invoice document number to base the writeoff on.
     * @param note User note to be added to the document.
     * @return Returns the Document Number of the Invoice Writeoff document created.
     * @throws WorkflowException
     */
    public String createCustomerInvoiceWriteoffDocument(Person initiator, String invoiceNumber, String note) throws WorkflowException;
    
}
