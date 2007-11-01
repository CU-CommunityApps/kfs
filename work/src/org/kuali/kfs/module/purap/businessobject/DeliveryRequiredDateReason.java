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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * Delivery Required Date Reason Business Object. Defines the reason why a delivery date is required.
 */
public class DeliveryRequiredDateReason extends PersistableBusinessObjectBase {

    private String deliveryRequiredDateReasonCode;
    private String deliveryRequiredDateReasonDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public DeliveryRequiredDateReason() {

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDeliveryRequiredDateReasonCode() {
        return deliveryRequiredDateReasonCode;
    }

    public void setDeliveryRequiredDateReasonCode(String deliveryRequiredDateReasonCode) {
        this.deliveryRequiredDateReasonCode = deliveryRequiredDateReasonCode;
    }

    public String getDeliveryRequiredDateReasonDescription() {
        return deliveryRequiredDateReasonDescription;
    }

    public void setDeliveryRequiredDateReasonDescription(String deliveryRequiredDateReasonDescription) {
        this.deliveryRequiredDateReasonDescription = deliveryRequiredDateReasonDescription;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("deliveryRequiredDateReasonCode", this.deliveryRequiredDateReasonCode);
        return m;
    }
}
