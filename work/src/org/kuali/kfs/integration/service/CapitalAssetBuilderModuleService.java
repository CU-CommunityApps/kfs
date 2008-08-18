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
package org.kuali.kfs.integration.service;

import java.util.List;

import org.kuali.kfs.integration.businessobject.CapitalAssetSystem;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.kns.util.KualiDecimal;

public interface CapitalAssetBuilderModuleService {

    public boolean validateIndividualCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems) ;
    
    public boolean validateOneSystemCapitalAssetSystemFromPurchasing(String systemState, CapitalAssetSystem capitalAssetSystem) ;
    
    public boolean validateMultipleSystemsCapitalAssetSystemFromPurchasing(String systemState, List<CapitalAssetSystem> capitalAssetSystems) ;
    

    /**
     * Retrieve the threshold amount from system parameter and return true if 
     * docTotal exceeds value from parameter.
     * 
     * @param   docTotal  The amount in KualiDecimal from the document to be verified.
     * @return  boolean true if the amount to be verified is greater than the threshold
     *          amount from system parameter.
     */
    public boolean doesDocumentExceedThreshold(KualiDecimal docTotal);
    
    /**
     *  Validates whether transaction type is allowed for the given subtypes.
     *  Validates that the object codes must be either all capital or all expense.
     * 
     * @param accountingLines  The accounting lines to be validated.
     * @param transactionType  The transaction type to be validated.
     * 
     * @return boolean true if the transaction type is allowed for the given
     *         subtypes and the object codes are either all capital or expense.
     */
    public boolean validateAccounts(List<SourceAccountingLine> accountingLines, String transactionType);

}
