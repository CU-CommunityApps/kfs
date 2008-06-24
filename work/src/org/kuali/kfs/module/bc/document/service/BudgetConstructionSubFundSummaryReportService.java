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
package org.kuali.kfs.module.bc.document.service;

import java.util.Collection;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgAccountSummaryReport;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrgSubFundSummaryReport;

/**
 * This interface defines the methods for BudgetConstructionOrgSubFundSummaryReports
 */
public interface BudgetConstructionSubFundSummaryReportService {

    /**
     * updates account summary table for SubFundSummaryReport.
     * 
     * @param personUserIdentifier
     * @return
     */
    public void updateSubFundSummaryReport(String personUserIdentifier);
    
    /**
     * builds BudgetConstructionOrgSubFundSummaryReports
     * 
     * @param universityFiscalYear
     * @param accountSummaryList
     * @return
     */
    public Collection<BudgetConstructionOrgSubFundSummaryReport> buildReports(Integer universityFiscalYear,  String personUserIdentifier);

}
