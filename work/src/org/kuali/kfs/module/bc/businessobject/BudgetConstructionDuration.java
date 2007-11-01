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

package org.kuali.module.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionDuration extends PersistableBusinessObjectBase {

    private String appointmentDurationCode;
    private String appointmentDurationDescription;
    private boolean rowActiveIndicator;

    /**
     * Default constructor.
     */
    public BudgetConstructionDuration() {

    }

    /**
     * Gets the appointmentDurationCode attribute.
     * 
     * @return Returns the appointmentDurationCode
     */
    public String getAppointmentDurationCode() {
        return appointmentDurationCode;
    }

    /**
     * Sets the appointmentDurationCode attribute.
     * 
     * @param appointmentDurationCode The appointmentDurationCode to set.
     */
    public void setAppointmentDurationCode(String appointmentDurationCode) {
        this.appointmentDurationCode = appointmentDurationCode;
    }


    /**
     * Gets the appointmentDurationDescription attribute.
     * 
     * @return Returns the appointmentDurationDescription
     */
    public String getAppointmentDurationDescription() {
        return appointmentDurationDescription;
    }

    /**
     * Sets the appointmentDurationDescription attribute.
     * 
     * @param appointmentDurationDescription The appointmentDurationDescription to set.
     */
    public void setAppointmentDurationDescription(String appointmentDurationDescription) {
        this.appointmentDurationDescription = appointmentDurationDescription;
    }


    /**
     * Gets the rowActiveIndicator attribute.
     * 
     * @return Returns the rowActiveIndicator.
     */
    public boolean isRowActiveIndicator() {
        return rowActiveIndicator;
    }

    /**
     * Sets the rowActiveIndicator attribute value.
     * 
     * @param rowActiveIndicator The rowActiveIndicator to set.
     */
    public void setRowActiveIndicator(boolean rowActiveIndicator) {
        this.rowActiveIndicator = rowActiveIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("appointmentDurationCode", this.appointmentDurationCode);
        return m;
    }
}
