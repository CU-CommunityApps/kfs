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
package org.kuali.kfs.integration.cg;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;
import org.kuali.rice.kns.bo.Inactivateable;

/**
 * Integration interface for LetterOfCreditFundGroup
 */
public interface ContractsAndGrantsLetterOfCreditFundGroup extends Inactivateable, ExternalizableBusinessObject {


    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     * 
     * @return Returns the letterOfCreditFundGroupCode
     */
    public String getLetterOfCreditFundGroupCode();


    /**
     * Gets the letterOfCreditFundGroupDescription attribute.
     * 
     * @return Returns the letterOfCreditFundGroupDescription
     */
    public String getLetterOfCreditFundGroupDescription();

}
