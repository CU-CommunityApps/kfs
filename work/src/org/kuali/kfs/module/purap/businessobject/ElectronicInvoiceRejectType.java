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

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Electronic Invoice Reject Reason Type Code Business Object.
 */
public class ElectronicInvoiceRejectType extends PersistableBusinessObjectBase {

    private String invoiceRejectReasonTypeCode;
    private String invoiceRejectReasonTypeDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public ElectronicInvoiceRejectType() {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean dataObjectMaintenanceCodeActiveIndicator) {
        this.active = dataObjectMaintenanceCodeActiveIndicator;
    }

    public String getInvoiceRejectReasonTypeCode() {
        return invoiceRejectReasonTypeCode;
    }

    public void setInvoiceRejectReasonTypeCode(String invoiceRejectReasonTypeCode) {
        this.invoiceRejectReasonTypeCode = invoiceRejectReasonTypeCode;
    }

    public String getInvoiceRejectReasonTypeDescription() {
        return invoiceRejectReasonTypeDescription;
    }

    public void setInvoiceRejectReasonTypeDescription(String invoiceRejectReasonTypeDescription) {
        this.invoiceRejectReasonTypeDescription = invoiceRejectReasonTypeDescription;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("invoiceRejectReasonTypeCode", this.invoiceRejectReasonTypeCode);
        return m;
    }
}
