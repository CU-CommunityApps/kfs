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
package org.kuali.module.budget.dao;

public interface BudgetConstructionPositionFundingDetailReportDao {

    /**
     *  cleans Position Funding Detail table.
     * 
     * @param personUserIdentifier
     */
    public void cleanReportsPositionFundingDetailTable(String personUserIdentifier);


    /**
     * 
     * populates the reporting table for PositionFunding so the user can run a report
     * @param personUserIdentifier--the user making the request
     * @param applyAThreshold--true if the report will only list people with increases above (or below) a threshold, false otherwise
     * @param selectOnlyGreaterThanOrEqualToThreshold--true if people at or above the threshold are to be listed: false lists people at or below
     * @param thresholdPercent--percent (a fraction times 100) increase which marks the threshold 
     */
    public void updateReportsPositionFundingDetailTable(String personUserIdentifier, boolean applyAThreshold, boolean selectOnlyGreaterThanOrEqualToThreshold, Number thresholdPercent);

}
