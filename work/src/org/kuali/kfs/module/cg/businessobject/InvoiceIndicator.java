/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsInvoiceIndicator;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * InvoiceIndicator under Contracts and Grants section. 
 */
public class InvoiceIndicator extends PersistableBusinessObjectBase implements ContractsAndGrantsInvoiceIndicator{
    
    private String invoiceIndicator;
    private String invoiceIndicatorDescription;
    private boolean active;
    
    /**
     * Constructs a InvoiceIndicator.java.
     */
    public InvoiceIndicator() {
        
    }

    /**
     * Gets the invoiceIndicator attribute. 
     * @return Returns the invoiceIndicator.
     */
    public String getInvoiceIndicator() {
        return invoiceIndicator;
    }

    /**
     * Sets the invoiceIndicator attribute value.
     * @param invoiceIndicator The invoiceIndicator to set.
     */
    public void setInvoiceIndicator(String invoiceIndicator) {
        this.invoiceIndicator = invoiceIndicator;
    }

    /**
     * Gets the invoiceIndicatorDescription attribute. 
     * @return Returns the invoiceIndicatorDescription.
     */
    public String getInvoiceIndicatorDescription() {
        return invoiceIndicatorDescription;
    }

    /**
     * Sets the invoiceIndicatorDescription attribute value.
     * @param invoiceIndicatorDescription The invoiceIndicatorDescription to set.
     */
    public void setInvoiceIndicatorDescription(String invoiceIndicatorDescription) {
        this.invoiceIndicatorDescription = invoiceIndicatorDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("invoiceIndicator", this.invoiceIndicator);
        m.put("invoiceIndicatorDescription", this.invoiceIndicatorDescription);
        return m;
    }

}
