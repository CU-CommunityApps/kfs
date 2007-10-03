/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.service;

import java.util.List;
import java.util.Map;

import org.kuali.core.bo.Parameter;
import org.kuali.module.chart.bo.OrganizationReversion;
import org.kuali.module.chart.bo.OrganizationReversionCategory;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;

/**
 */
public interface OrganizationReversionService {
    /**
     * Get an organization reversion record
     * 
     * @param fiscalYear Fiscal Year
     * @param chartCode Chart
     * @param organizationCode Organization code
     * @return Org Reversion record
     */
    public OrganizationReversion getByPrimaryId(Integer fiscalYear, String chartCode, String organizationCode);

    /**
     * Get org reversion categories
     * 
     * @return map of org reversion categories
     */
    public Map<String, OrganizationReversionCategoryLogic> getCategories();

    /**
     * List of reversion categories in order of the sort code
     * 
     * @return list of org reversion category codes
     */
    public List<OrganizationReversionCategory> getCategoryList();
    
    /**
     * Returns the selection parameter rules that should be used by the Organization Reversion End of Year job to select balances
     * @return a map of rules with the key being the order they are to be invoked in
     */
    public Map<Integer, Parameter> getEndOfYearSelectionRules();
    
    /**
     * Returns the selection parameter rules that should be used by the Organization Reversion Beginning of Year job to select balances
     * @return a map of rules with the key being the order they are to be invoked in
     */
    public Map<Integer, Parameter> getBeginningOfYearSelectionRules();
}
