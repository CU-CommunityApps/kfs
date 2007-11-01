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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class LetterOfCreditFundGroup extends PersistableBusinessObjectBase {

    private String letterOfCreditFundGroupCode;
    private String letterOfCreditFundGroupDescription;
    private boolean rowActiveIndicator;

    /**
     * Default constructor.
     */
    public LetterOfCreditFundGroup() {
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     * 
     * @return Returns the letterOfCreditFundGroupCode
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute.
     * 
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the letterOfCreditFundGroupDescription attribute.
     * 
     * @return Returns the letterOfCreditFundGroupDescription
     */
    public String getLetterOfCreditFundGroupDescription() {
        return letterOfCreditFundGroupDescription;
    }

    /**
     * Sets the letterOfCreditFundGroupDescription attribute.
     * 
     * @param letterOfCreditFundGroupDescription The letterOfCreditFundGroupDescription to set.
     */
    public void setLetterOfCreditFundGroupDescription(String letterOfCreditFundGroupDescription) {
        this.letterOfCreditFundGroupDescription = letterOfCreditFundGroupDescription;
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
        m.put("letterOfCreditFundGroupCode", this.letterOfCreditFundGroupCode);
        return m;
    }
}
