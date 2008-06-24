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
package org.kuali.kfs.coa.document.validation.impl;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;

/**
 * Business rule(s) applicable to {@link ChartMaintenance} documents.
 */
public class ChartRule extends MaintenanceDocumentRuleBase {

    /**
     * This method calls specific rules for routing on Chart Maintenance documents Specifically it checks to make sure that
     * reportsToChart exists if it is not the same code as the newly created Chart and it checks to make sure that the chart manager
     * is valid for the Chart Module
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     * @return false if reports to chart code doesn't exist or user is invalid for this module
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean result = true;

        Chart chart = (Chart) document.getNewMaintainableObject().getBusinessObject();
        ChartService chartService = SpringContext.getBean(ChartService.class);
        FinancialSystemUserService financialSystemUserService = SpringContext.getBean(FinancialSystemUserService.class);  
        

        String chartCode = chart.getChartOfAccountsCode();

        String reportsToChartCode = chart.getReportsToChartOfAccountsCode();

        if (chartCode != null && !chartCode.equals(reportsToChartCode)) { // if not equal to this newly created chart, then must
                                                                            // exist
            Chart reportsToChart = chartService.getByPrimaryId(reportsToChartCode);
            if (reportsToChart == null) {
                result = false;
                putFieldError("reportsToChartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_CHART_REPORTS_TO_CHART_MUST_EXIST);
            }
        }

        FinancialSystemUser chartManager = financialSystemUserService.getFinancialSystemUser(chart.getFinCoaManagerUniversalId());
        if ( chartManager == null ) {
            result = false;
            putFieldError("finCoaManagerUniversal.personUserIdentifier", KFSKeyConstants.ERROR_DOCUMENT_CHART_MANAGER_MUST_EXIST);
        }

        if (chartManager != null && !chartManager.isActiveFinancialSystemUser()) {
            result = false;
            putFieldError("finCoaManagerUniversal.personUserIdentifier", KFSKeyConstants.ERROR_DOCUMENT_CHART_MANAGER_MUST_BE_KUALI_USER);
        }


        return result;

    }

}
