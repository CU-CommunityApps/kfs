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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountSalaryDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionBalanceByAccount;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.dao.BudgetConstructionDocumentAccountObjectDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionDocumentAccountObjectDetailReportService;
import org.kuali.module.budget.service.BudgetConstructionReportsServiceHelper;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionLevelSummaryReportService.
 */
@Transactional
public class BudgetConstructionDocumentAccountObjectDetailReportServiceImpl implements BudgetConstructionDocumentAccountObjectDetailReportService {
    private BudgetConstructionDocumentAccountObjectDetailReportDao budgetConstructionDocumentAccountObjectDetailReportDao;
    private KualiConfigurationService kualiConfigurationService;
    private BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper;

    public void updateDocumentAccountObjectDetailReportTable(String personUserIdentifier, String documentNumber, Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        budgetConstructionDocumentAccountObjectDetailReportDao.updateDocumentAccountObjectDetailReportTable(personUserIdentifier, documentNumber, universityFiscalYear, chartOfAccountsCode, accountNumber, subAccountNumber);
    }

    /**
     * @see org.kuali.module.budget.service.BudgetConstructionLevelSummaryReportService#buildReports(java.lang.Integer,
     *      java.util.Collection)
     */
    public Collection<BudgetConstructionAccountSalaryDetailReport> buildReports(String personUserIdentifier) {
        Collection<BudgetConstructionAccountSalaryDetailReport> reportSet = new ArrayList();

        // build order list
        List<String> orderList = buildOrderByList();
        Collection<BudgetConstructionBalanceByAccount> balanceByAccountList = budgetConstructionReportsServiceHelper.getDataForBuildingReports(BudgetConstructionBalanceByAccount.class, personUserIdentifier, orderList);


        /*
         * for (BudgetConstructionMonthly bcMonthly : budgetConstructionMonthlyList) { accountSalaryDetailReport = new
         * BudgetConstructionAccountSalaryDetailReport(); buildReportsHeader(bcMonthly, accountSalaryDetailReport);
         * buildReportsBody(bcMonthly, accountSalaryDetailReport);
         * 
         * reportSet.add(accountSalaryDetailReport); }
         */
        return reportSet;
    }

    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectSummary bcas
     */
    private void buildReportsHeader(BudgetConstructionBalanceByAccount balanceByAccount, BudgetConstructionAccountSalaryDetailReport accountMonthlyDetailReport) {
        accountMonthlyDetailReport.setUniversityFiscalYear(balanceByAccount.getUniversityFiscalYear());
        accountMonthlyDetailReport.setChartOfAccountsCode(balanceByAccount.getChartOfAccountsCode());
        balanceByAccount.getAccount().getOrganizationCode();
        balanceByAccount.getAccount().getOrganization().getOrganizationName();
        balanceByAccount.getAccount().getSubFundGroup().getFundGroupCode();
        balanceByAccount.getAccount().getSubFundGroup().getFundGroup().getName();
        accountMonthlyDetailReport.setAccountNumber(balanceByAccount.getAccountNumber());
        accountMonthlyDetailReport.setSubAccountNumber(balanceByAccount.getSubAccountNumber());
        accountMonthlyDetailReport.setAccountName(balanceByAccount.getAccount().getAccountName());
        try {
            accountMonthlyDetailReport.setSubAccountName(balanceByAccount.getSubAccount().getSubAccountName());
        }
        catch (Exception e) {
            accountMonthlyDetailReport.setSubAccountName("");
        }
    }

    /**
     * builds report body
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    private void buildReportsBody(BudgetConstructionMonthly bcMonthly, BudgetConstructionAccountSalaryDetailReport accountMonthlyDetailReport) {
        accountMonthlyDetailReport.setFinancialObjectCode(bcMonthly.getFinancialObjectCode());
        accountMonthlyDetailReport.setFinancialSubObjectCode(bcMonthly.getFinancialSubObjectCode());
        accountMonthlyDetailReport.setFinancialObjectCodeShortName(bcMonthly.getFinancialObject().getFinancialObjectCodeShortName());
        accountMonthlyDetailReport.setObjCodeSubObjCode(accountMonthlyDetailReport.getFinancialObjectCode() + accountMonthlyDetailReport.getFinancialSubObjectCode());

        Integer financialDocumentMonth1LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth1LineAmount());
        Integer financialDocumentMonth2LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth2LineAmount());
        Integer financialDocumentMonth3LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth3LineAmount());
        Integer financialDocumentMonth4LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth4LineAmount());
        Integer financialDocumentMonth5LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth5LineAmount());
        Integer financialDocumentMonth6LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth6LineAmount());
        Integer financialDocumentMonth7LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth7LineAmount());
        Integer financialDocumentMonth8LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth8LineAmount());
        Integer financialDocumentMonth9LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth9LineAmount());
        Integer financialDocumentMonth10LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth10LineAmount());
        Integer financialDocumentMonth11LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth11LineAmount());
        Integer financialDocumentMonth12LineAmount = BudgetConstructionReportHelper.convertKualiInteger(bcMonthly.getFinancialDocumentMonth12LineAmount());

        Integer annualAmount = financialDocumentMonth1LineAmount + financialDocumentMonth2LineAmount + financialDocumentMonth3LineAmount + financialDocumentMonth4LineAmount + financialDocumentMonth5LineAmount + financialDocumentMonth6LineAmount + financialDocumentMonth7LineAmount + financialDocumentMonth8LineAmount + financialDocumentMonth9LineAmount + financialDocumentMonth10LineAmount + financialDocumentMonth11LineAmount + financialDocumentMonth12LineAmount;

        accountMonthlyDetailReport.setAnnualAmount(annualAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth1LineAmount(financialDocumentMonth1LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth2LineAmount(financialDocumentMonth2LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth3LineAmount(financialDocumentMonth3LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth4LineAmount(financialDocumentMonth4LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth5LineAmount(financialDocumentMonth5LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth6LineAmount(financialDocumentMonth6LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth7LineAmount(financialDocumentMonth7LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth8LineAmount(financialDocumentMonth8LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth9LineAmount(financialDocumentMonth9LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth10LineAmount(financialDocumentMonth10LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth11LineAmount(financialDocumentMonth11LineAmount);
        accountMonthlyDetailReport.setFinancialDocumentMonth12LineAmount(financialDocumentMonth12LineAmount);

    }

    /**
     * builds orderByList for sort order.
     * 
     * @return returnList
     */
    private List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.TYPE_FINANCIAL_REPORT_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.LEVEL_FINANCIAL_REPORT_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBudgetConstructionReportsServiceHelper(BudgetConstructionReportsServiceHelper budgetConstructionReportsServiceHelper) {
        this.budgetConstructionReportsServiceHelper = budgetConstructionReportsServiceHelper;
    }

    public void setBudgetConstructionDocumentAccountObjectDetailReportDao(BudgetConstructionDocumentAccountObjectDetailReportDao budgetConstructionDocumentAccountObjectDetailReportDao) {
        this.budgetConstructionDocumentAccountObjectDetailReportDao = budgetConstructionDocumentAccountObjectDetailReportDao;
    }

}
