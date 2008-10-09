/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class AchTransactionCode extends PersistableBusinessObjectBase {
    String transactionCode;
    String description;
    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("transactionCode", transactionCode);
        m.put("description", description);
        
        return m;
    }
    
    /**
     * Gets Transaction Code Description
     * 
     * @return
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets Description
     * 
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the Transaction Code
     * 
     * @return
     */
    public String getTransactionCode() {
        return transactionCode;
    }
    
    /**
     * Sets Transaction Code
     * 
     * @param transactionCode
     */
    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

}
