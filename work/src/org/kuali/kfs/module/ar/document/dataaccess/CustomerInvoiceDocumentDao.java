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
package org.kuali.kfs.module.ar.document.dataaccess;

import java.util.Collection;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;

public interface CustomerInvoiceDocumentDao {
    
    /**
     * This method retrieves all CustomerInvoiceDocument objects in the system
     * 
     * @return all CustomerInvoiceDocument objects
     */
    public Collection getAll();   
    
    /**
     * 
     * Retrieves all Open invoices, with outstanding balances.
     * 
     * @return
     */
    public Collection getAllOpen();
    
    /**
     * 
     * Retrieves all Open invoices from the specified Customer Number.
     * @param customerNumber
     * @return
     */
    public Collection getOpenByCustomerNumber(String customerNumber);
    
    /**
     * 
     * Retrieves all Open invoices, by the specified Customer Name.
     * 
     * NOTE - this search uses customerName as a leading substring search, 
     * so it will return anything matching a customerName that begins with the 
     * value passed in.  ie, a LIKE customerName* search.
     * 
     * @param customerName
     * @return
     */
    public Collection getOpenByCustomerName(String customerName);
    
    /**
     * 
     * Retrieves all Open invoices, by the specified Customer Type Code.
     * @param customerTypeCode
     * @return
     */
    public Collection getOpenByCustomerType(String customerTypeCode);
    
    /**
     * @param organizationInvoiceNumber
     * @return
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber);
    
    /**
     * @param documentNumber
     * @return
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String documentNumber);
}
