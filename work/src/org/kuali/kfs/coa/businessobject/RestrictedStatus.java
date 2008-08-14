/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class RestrictedStatus extends PersistableBusinessObjectBase {

    /**
     * Default no-arg constructor.
     */
    public RestrictedStatus() {

    }

    private String accountRestrictedStatusCode;
    private String accountRestrictedStatusName;

    /**
     * Gets the accountRestrictedStatusCode attribute.
     * 
     * @return Returns the accountRestrictedStatusCode
     */
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }

    /**
     * Sets the accountRestrictedStatusCode attribute.
     * 
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }

    /**
     * Gets the accountRestrictedStatusName attribute.
     * 
     * @return Returns the accountRestrictedStatusName
     */
    public String getAccountRestrictedStatusName() {
        return accountRestrictedStatusName;
    }

    /**
     * Sets the accountRestrictedStatusName attribute.
     * 
     * @param accountRestrictedStatusName The accountRestrictedStatusName to set.
     */
    public void setAccountRestrictedStatusName(String accountRestrictedStatusName) {
        this.accountRestrictedStatusName = accountRestrictedStatusName;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getAccountRestrictedStatusCode() + " - " + getAccountRestrictedStatusName();
        return theString;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("accountRestrictedStatusCode", this.accountRestrictedStatusCode);

        return m;
    }
}
