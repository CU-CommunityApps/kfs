/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.pdp.dataaccess;

import org.kuali.kfs.pdp.businessobject.AchBank;

public interface AchBankDao {
    /**
     * Save a bank
     * 
     * @param ab
     */
    public void save(AchBank ab);

    /**
     * Get a bank
     *
     * @param bankRoutingNumber
     * @return
     */
    public AchBank getBank(String bankRoutingNumber);
}
