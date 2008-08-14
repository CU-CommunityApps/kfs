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

package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class FederalFunction extends PersistableBusinessObjectBase {

    private String financialFederalFunctionCode;
    private String financialFederalFunctionName;

    /**
     * Default constructor.
     */
    public FederalFunction() {

    }

    /**
     * Gets the financialFederalFunctionCode attribute.
     * 
     * @return Returns the financialFederalFunctionCode
     */
    public String getFinancialFederalFunctionCode() {
        return financialFederalFunctionCode;
    }

    /**
     * Sets the financialFederalFunctionCode attribute.
     * 
     * @param financialFederalFunctionCode The financialFederalFunctionCode to set.
     */
    public void setFinancialFederalFunctionCode(String financialFederalFunctionCode) {
        this.financialFederalFunctionCode = financialFederalFunctionCode;
    }


    /**
     * Gets the financialFederalFunctionName attribute.
     * 
     * @return Returns the financialFederalFunctionName
     */
    public String getFinancialFederalFunctionName() {
        return financialFederalFunctionName;
    }

    /**
     * Sets the financialFederalFunctionName attribute.
     * 
     * @param financialFederalFunctionName The financialFederalFunctionName to set.
     */
    public void setFinancialFederalFunctionName(String financialFederalFunctionName) {
        this.financialFederalFunctionName = financialFederalFunctionName;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialFederalFunctionCode", this.financialFederalFunctionCode);
        return m;
    }
}
