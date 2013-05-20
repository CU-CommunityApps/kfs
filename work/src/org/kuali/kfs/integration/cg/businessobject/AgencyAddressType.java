/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.integration.cg.businessobject;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddressType;

/**
 * Integration Class for AgencyAddress Type
 */
public class AgencyAddressType implements ContractsAndGrantsAgencyAddressType {


    private String agencyAddressTypeCode;
    private String agencyAddressTypeDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public AgencyAddressType() {

    }

    /**
     * Gets the agencyAddressTypeCode attribute.
     * 
     * @return Returns the agencyAddressTypeCode
     */
    public String getAgencyAddressTypeCode() {
        return agencyAddressTypeCode;
    }

    /**
     * Sets the agencyAddressTypeCode attribute.
     * 
     * @param agencyAddressTypeCode The agencyAddressTypeCode to set.
     */
    public void setAgencyAddressTypeCode(String agencyAddressTypeCode) {
        this.agencyAddressTypeCode = agencyAddressTypeCode;
    }


    /**
     * Gets the agencyAddressTypeDescription attribute.
     * 
     * @return Returns the agencyAddressTypeDescription
     */
    public String getAgencyAddressTypeDescription() {
        return agencyAddressTypeDescription;
    }

    /**
     * Sets the agencyAddressTypeDescription attribute.
     * 
     * @param agencyAddressTypeDescription The agencyAddressTypeDescription to set.
     */
    public void setAgencyAddressTypeDescription(String agencyAddressTypeDescription) {
        this.agencyAddressTypeDescription = agencyAddressTypeDescription;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public void prepareForWorkflow() {

    }

    public void refresh() {

    }


}
