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
package org.kuali.module.chart.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.chart.service.PriorYearAccountService;
import org.kuali.module.chart.service.PriorYearOrganizationService;

/**
 * This class updates the prior year data in the prior year account and prior year org tables to set it to the new year This is
 * typically run at year end
 */
public class UpdatePriorYearDataStep extends AbstractStep {

    private PriorYearAccountService priorYearAccountService;
    private PriorYearOrganizationService priorYearOrganizationService;

    /**
     * Executes the table updates when
     * 
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName) throws InterruptedException {
        priorYearAccountService.populatePriorYearAccountsFromCurrent();
        priorYearOrganizationService.populatePriorYearOrganizationsFromCurrent();
        return true;
    }

    /**
     * Service setter for Spring injection
     * 
     * @param priorYearAccountService
     */
    public void setPriorYearAccountService(PriorYearAccountService priorYearAccountService) {
        this.priorYearAccountService = priorYearAccountService;
    }

    /**
     * Service setter for Spring injection
     * 
     * @param priorYearOrganizationService
     */
    public void setPriorYearOrganizationService(PriorYearOrganizationService priorYearOrganizationService) {
        this.priorYearOrganizationService = priorYearOrganizationService;
    }

}
