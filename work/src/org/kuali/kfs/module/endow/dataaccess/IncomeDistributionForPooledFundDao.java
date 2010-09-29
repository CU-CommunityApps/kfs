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
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KemidPayoutInstruction;
import org.kuali.kfs.module.endow.businessobject.Security;

public interface IncomeDistributionForPooledFundDao {

    public List<HoldingTaxLot> getHoldingTaxLotForIncomeDistribution();
    
    public String getIncomeEntraCode(String securityId);
    
    public List<BigDecimal> getHoldingTaxLotListGroupedBy(String securityId);
    
    public List<KemidPayoutInstruction> getKemidPayoutInstructionForECT(String kemid);
    
    public List<Security> getSecurityForIncomeDistribution();
}
