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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetBaseCode extends PersistableBusinessObjectBase {

    private String budgetBaseCode;
    private String budgetBaseDescription;
    private boolean active;

    /**
     * Default no-arg constructor.
     */
    public BudgetBaseCode() {

    }

    /**
     * Gets the budgetBaseCode attribute.
     * 
     * @return Returns the budgetBaseCode
     */
    public String getBudgetBaseCode() {
        return budgetBaseCode;
    }

    /**
     * Sets the budgetBaseCode attribute.
     * 
     * @param budgetBaseCode The budgetBaseCode to set.
     */
    public void setBudgetBaseCode(String budgetBaseCode) {
        this.budgetBaseCode = budgetBaseCode;
    }

    /**
     * Gets the budgetBaseDescription attribute.
     * 
     * @return Returns the budgetBaseDescription
     */
    public String getBudgetBaseDescription() {
        return budgetBaseDescription;
    }

    /**
     * Sets the budgetBaseDescription attribute.
     * 
     * @param budgetBaseDescription The budgetBaseDescription to set.
     */
    public void setBudgetBaseDescription(String budgetBaseDescription) {
        this.budgetBaseDescription = budgetBaseDescription;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        // m.put("<unique identifier 1>", this.<UniqueIdentifier1>());
        // m.put("<unique identifier 2>", this.<UniqueIdentifier2>());

        return m;
    }
}
