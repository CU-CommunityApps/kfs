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
package org.kuali.module.budget.service;
import java.util.*;
/*
 *   this service intializes/updates the budget construction data used by the
 *   budget module to build a new budget for the coming fiscal year
 */
public interface GenesisService {
   /*
     * build the budget construction GL table from the BALANCE_TYPE_BASE_BUDGET rows in 
     * GL table
    */ 
    public void stepBudgetConstructionGLLoad (Integer universityFiscalYear);

    // this step clears out the database for genesis
    public void clearDBForGenesis(Integer BaseYear);   
    
    // this step creates "placeholder" budget construction headers (no document numbers)
    // in a transactional step, so we can minimize database calls
    public void createProxyBCHeadersTransactional(Integer BaseYear);
    
    // once all needed BC documents have been created, this step runs the rest of genesis
    public void genesisStep(Integer BaseYear);
    
    public void testStep(Integer universityFiscalYear);
    public void testSLFStep(Integer universityFiscalYear);
    public void testSLFAfterStep(Integer universityFiscalYear);
    public void testLockClearance(Integer currentFiscalYear);
    
    public void testChartCreation();
    public void testHierarchyCreation(Integer currentFiscalYear);
}
