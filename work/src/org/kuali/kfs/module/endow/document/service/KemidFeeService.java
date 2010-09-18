/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service;

import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.KemidFee;

public interface KemidFeeService {

    /**
     * updates Waiver Fee Year-To-Date totals.
     * @return true if the amounts updated else return false
     */
    public boolean updateWaiverFeeYearToDateTotals();
    
    /**
     * Gets all the KemidFee records as a collection
     * @return collection <KemidFee> records
     */
    public Collection <KemidFee> getAllKemIdFee() ;
    
    /**
     * Save the collection of KemidFee records to the database.
     */
    public boolean saveKemidFee(Collection <KemidFee> kemIdFeeRecords);
}
