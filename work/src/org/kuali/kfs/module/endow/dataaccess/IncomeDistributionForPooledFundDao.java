/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;

public interface IncomeDistributionForPooledFundDao {

    /**
     * Get the list of holding tax lot
     * @return List<HoldingTaxLot>
     */
    //public List<HoldingTaxLot> getHoldingTaxLotForIncomeDistribution();
    
    /**
     * Get the income transaction code for the given security id
     * @param securityId
     * @return String
     */
    public String getIncomeEntraCode(String securityId);
    
    /**
     * Get the list of holding tax lot grouped by the given security id
     * @param securityId
     * @return List<BigDecimal>
     */
    public List<BigDecimal> getHoldingTaxLotListGroupedBySecurityId(String securityId);
    
    /**
     * Get the pay income records for building ECT 
     * @param kemid
     * @return List<KemidPayoutInstruction>
     */
    public List<KemidPayoutInstruction> getKemidPayoutInstructionForECT(String kemid, Date currentDate);
    
    /**
     * Get the list of PooledFundValue for income distribution purposes
     * @return List<PooledFundValue>
     */
    public List<PooledFundValue> getPooledFundValueForIncomeDistribution(Date currentDate);
}
