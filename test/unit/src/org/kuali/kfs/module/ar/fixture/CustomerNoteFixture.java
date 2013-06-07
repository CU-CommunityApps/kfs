/*
 * Copyright 2012 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.CustomerNote;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum CustomerNoteFixture {

    CUSTOMERNOTE1("ABB2", 1, "First Note");
    
    public String customerNumber;
    public Integer customerNoteIdentifier;
    public String noteText;
    
    private CustomerNoteFixture(String customerNumber, Integer customerNoteIdentifier, String noteText) {
        this.customerNumber = customerNumber;
        this.customerNoteIdentifier = customerNoteIdentifier;
        this.noteText = noteText;
    }
    
    public CustomerNote createCustomerNote() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        CustomerNote customerNote = new CustomerNote();
        customerNote.setCustomerNumber(customerNumber);
        customerNote.setCustomerNoteIdentifier(customerNoteIdentifier);
        customerNote.setNoteText(noteText);
        
        businessObjectService.save(customerNote);
        return customerNote;
    }
}
