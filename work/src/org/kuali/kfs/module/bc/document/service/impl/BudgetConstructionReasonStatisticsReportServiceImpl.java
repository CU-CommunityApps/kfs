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
package org.kuali.module.budget.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionObjectPick;
import org.kuali.module.budget.bo.BudgetConstructionOrgReasonStatisticsReport;
import org.kuali.module.budget.bo.BudgetConstructionReportThresholdSettings;
import org.kuali.module.budget.bo.BudgetConstructionSalaryTotal;
import org.kuali.module.budget.dao.BudgetConstructionReasonStatisticsReportDao;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.service.BudgetConstructionReasonStatisticsReportService;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.kuali.module.chart.bo.Chart;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionReasonStatisticsReportServiceImpl implements BudgetConstructionReasonStatisticsReportService {

    BudgetConstructionReasonStatisticsReportDao budgetConstructionReasonStatisticsReportDao;
    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    KualiConfigurationService kualiConfigurationService;
    BusinessObjectService businessObjectService;


    public void updateReasonStatisticsReport(String personUserIdentifier, Integer universityFiscalYear, BudgetConstructionReportThresholdSettings budgetConstructionReportThresholdSettings) {
        boolean applyAThreshold = budgetConstructionReportThresholdSettings.isUseThreshold();
        boolean selectOnlyGreaterThanOrEqualToThreshold = budgetConstructionReportThresholdSettings.isUseGreaterThanOperator();
        KualiDecimal thresholdPercent = budgetConstructionReportThresholdSettings.getThresholdPercent();
        if (applyAThreshold) {
            budgetConstructionReasonStatisticsReportDao.updateReasonStatisticsReportsWithAThreshold(personUserIdentifier, universityFiscalYear, selectOnlyGreaterThanOrEqualToThreshold, thresholdPercent);
        }
        else {
            budgetConstructionReasonStatisticsReportDao.updateReasonStatisticsReportsWithoutAThreshold(personUserIdentifier, universityFiscalYear);
        }

    }

    public Collection<BudgetConstructionOrgReasonStatisticsReport> buildReports(Integer universityFiscalYear, String personUserIdentifier) {
        Collection<BudgetConstructionOrgReasonStatisticsReport> reportSet = new ArrayList();


        BudgetConstructionOrgReasonStatisticsReport orgReasonStatisticsReportEntry;
        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionSalaryTotal> reasonStatisticsList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryTotal.class, searchCriteria, orderList);

        // get object codes
        searchCriteria.clear();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        for (BudgetConstructionObjectPick objectPick : objectPickList) {
            objectCodes += objectPick.getFinancialObjectCode() + " ";
        }
        // build reports
        for (BudgetConstructionSalaryTotal reasonStatisticsEntry : reasonStatisticsList) {
            orgReasonStatisticsReportEntry = new BudgetConstructionOrgReasonStatisticsReport();
            buildReportsHeader(universityFiscalYear, objectCodes, orgReasonStatisticsReportEntry, reasonStatisticsEntry);
            buildReportsBody(orgReasonStatisticsReportEntry, reasonStatisticsEntry);
            reportSet.add(orgReasonStatisticsReportEntry);
        }
        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectDump bcod
     */
    public void buildReportsHeader(Integer universityFiscalYear, String objectCodes, BudgetConstructionOrgReasonStatisticsReport orgReasonStatisticsReportEntry, BudgetConstructionSalaryTotal salaryTotalEntry) {

        // set fiscal year
        Integer prevFiscalyear = universityFiscalYear - 1;
        orgReasonStatisticsReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        // get Chart with orgChartCode
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salaryTotalEntry.getOrganizationChartOfAccountsCode());
        Chart chart = (Chart) businessObjectService.findByPrimaryKey(Chart.class, searchCriteria);

        // set OrgCode and Desc
        String orgName = salaryTotalEntry.getOrganization().getOrganizationName();
        orgReasonStatisticsReportEntry.setOrganizationCode(salaryTotalEntry.getOrganizationCode());
        if (orgName == null) {
            orgReasonStatisticsReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgReasonStatisticsReportEntry.setOrganizationName(orgName);
        }
        // set ChartCode and Desc
        if (chart == null) {
            orgReasonStatisticsReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
            orgReasonStatisticsReportEntry.setChartOfAccountsCode(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgReasonStatisticsReportEntry.setChartOfAccountsCode(chart.getChartOfAccountsCode());
            orgReasonStatisticsReportEntry.setChartOfAccountDescription(chart.getFinChartOfAccountDescription());
        }
        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgReasonStatisticsReportEntry.setObjectCodes(objectCodes);
    }


    public void buildReportsBody(BudgetConstructionOrgReasonStatisticsReport orgReasonStatisticsReportEntry, BudgetConstructionSalaryTotal salaryTotalEntry) {
        orgReasonStatisticsReportEntry.setInitialRequestedFteQuantity(salaryTotalEntry.getInitialRequestedFteQuantity());
        if (salaryTotalEntry.getInitialRequestedFteQuantity().equals(BigDecimal.ZERO)) {
            orgReasonStatisticsReportEntry.setTotalInitialRequestedAmount(0);
        }
        else {
            BigDecimal requestedAmount = salaryTotalEntry.getInitialRequestedAmount().divide(salaryTotalEntry.getInitialRequestedFteQuantity());
            orgReasonStatisticsReportEntry.setTotalInitialRequestedAmount(new Integer(BudgetConstructionReportHelper.setDecimalDigit(requestedAmount, 0).intValue()));
        }

        orgReasonStatisticsReportEntry.setAppointmentRequestedFteQuantity(salaryTotalEntry.getAppointmentRequestedFteQuantity());
        if (salaryTotalEntry.getAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
            orgReasonStatisticsReportEntry.setAverageCsfAmount(0);
            orgReasonStatisticsReportEntry.setAverageAppointmentRequestedAmount(0);
        }
        else {
            BigDecimal averageCsfAmount = salaryTotalEntry.getCsfAmount().divide(salaryTotalEntry.getAppointmentRequestedFteQuantity());
            BigDecimal averageRequestedAmount = salaryTotalEntry.getAppointmentRequestedAmount().divide(salaryTotalEntry.getAppointmentRequestedFteQuantity());
            orgReasonStatisticsReportEntry.setAverageCsfAmount(new Integer(BudgetConstructionReportHelper.setDecimalDigit(averageCsfAmount, 0).intValue()));
            orgReasonStatisticsReportEntry.setAverageAppointmentRequestedAmount(new Integer(BudgetConstructionReportHelper.setDecimalDigit(averageRequestedAmount, 0).intValue()));
        }
        orgReasonStatisticsReportEntry.setAverageChange(orgReasonStatisticsReportEntry.getAverageAppointmentRequestedAmount() - orgReasonStatisticsReportEntry.getAverageCsfAmount());

        BigDecimal percentChange = BigDecimal.ZERO;
        if (!orgReasonStatisticsReportEntry.getAverageCsfAmount().equals(0)) {
            percentChange = new BigDecimal(orgReasonStatisticsReportEntry.getAverageChange()).divide(new BigDecimal(orgReasonStatisticsReportEntry.getAverageCsfAmount()));
            BudgetConstructionReportHelper.setDecimalDigit(percentChange, 1);
        }
        orgReasonStatisticsReportEntry.setPercentChange(percentChange);


    }

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        return returnList;
    }

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }


    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReasonStatisticsReportDao(BudgetConstructionReasonStatisticsReportDao budgetConstructionReasonStatisticsReportDao) {
        this.budgetConstructionReasonStatisticsReportDao = budgetConstructionReasonStatisticsReportDao;
    }

}
