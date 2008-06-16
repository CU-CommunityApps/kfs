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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionAccountBalance;
import org.kuali.module.budget.bo.BudgetConstructionObjectSummary;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountObjectDetailReport;
import org.kuali.module.budget.bo.BudgetConstructionOrgAccountObjectDetailReportTotal;


import org.kuali.module.budget.dao.BudgetConstructionAccountObjectDetailReportDao;
import org.kuali.module.budget.service.BudgetConstructionAccountObjectDetailReportService;
import org.kuali.module.budget.service.BudgetConstructionOrganizationReportsService;
import org.kuali.module.budget.util.BudgetConstructionReportHelper;
import org.kuali.module.chart.bo.ObjectCode;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of BudgetConstructionAccountSummaryReportService.
 */
@Transactional
public class BudgetConstructionAccountObjectDetailReportServiceImpl implements BudgetConstructionAccountObjectDetailReportService {

    private BudgetConstructionAccountObjectDetailReportDao budgetConstructionAccountObjectDetailReportDao;
    private KualiConfigurationService kualiConfigurationService;
    private BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.module.budget.service.BudgetReportsControlListService#updateSubFundSummaryReport(java.lang.String)
     */
    public void updateAccountObjectDetailReport(String personUserIdentifier, boolean consolidated) {
        if (consolidated) {
            budgetConstructionAccountObjectDetailReportDao.updateReportsAccountObjectConsolidatedTable(personUserIdentifier);
        }
        else {
            budgetConstructionAccountObjectDetailReportDao.updateReportsAccountObjectDetailTable(personUserIdentifier);
        }
    }

    public Collection<BudgetConstructionOrgAccountObjectDetailReport> buildReports(Integer universityFiscalYear, String personUserIdentifier, boolean consolidated) {
        Collection<BudgetConstructionOrgAccountObjectDetailReport> reportSet = new ArrayList();
        Collection<BudgetConstructionAccountBalance> accountObjectDetailList;

        // build searchCriteria
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, personUserIdentifier);

        // build order list
        List<String> orderList = buildOrderByList();
        accountObjectDetailList = budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionAccountBalance.class, searchCriteria, orderList);


        // BudgetConstructionReportHelper.deleteDuplicated((List) positionFundingDetailList, fieldsForSubFundTotal())
        List listForCalculateObject = BudgetConstructionReportHelper.deleteDuplicated((List) accountObjectDetailList, fieldsForObject());
        List listForCalculateLevel = BudgetConstructionReportHelper.deleteDuplicated((List) accountObjectDetailList, fieldsForLevel());
        List listForCalculateGexpAndType = BudgetConstructionReportHelper.deleteDuplicated((List) accountObjectDetailList, fieldsForGexpAndType());
        List listForCalculateAccountTotal = BudgetConstructionReportHelper.deleteDuplicated((List) accountObjectDetailList, fieldsForAccountTotal());
        List listForCalculateSubFundTotal = BudgetConstructionReportHelper.deleteDuplicated((List) accountObjectDetailList, fieldsForSubFundTotal());

        // Calculate Total Section
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalObjectList;
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalLevelList;
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalGexpAndTypeList;
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailAccountTotalList;
        List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailSubFundTotalList;

        accountObjectDetailTotalObjectList = calculateObjectTotal((List) accountObjectDetailList, listForCalculateObject);
        accountObjectDetailTotalLevelList = calculateLevelTotal((List) accountObjectDetailList, listForCalculateLevel);
        accountObjectDetailTotalGexpAndTypeList = calculateGexpAndTypeTotal((List) accountObjectDetailList, listForCalculateGexpAndType);
        accountObjectDetailAccountTotalList = calculateAccountTotal((List) accountObjectDetailList, listForCalculateAccountTotal);
        accountObjectDetailSubFundTotalList = calculateSubFundTotal((List) accountObjectDetailList, listForCalculateSubFundTotal);

        for (BudgetConstructionAccountBalance accountObjectDetailEntry : accountObjectDetailList) {
            BudgetConstructionOrgAccountObjectDetailReport accountObjectDetailReport = new BudgetConstructionOrgAccountObjectDetailReport();
            buildReportsHeader(universityFiscalYear, accountObjectDetailReport, accountObjectDetailEntry, consolidated);
            buildReportsBody(universityFiscalYear, accountObjectDetailReport, accountObjectDetailEntry);
            buildReportsTotal(accountObjectDetailReport, accountObjectDetailEntry, accountObjectDetailTotalObjectList, accountObjectDetailTotalLevelList, accountObjectDetailTotalGexpAndTypeList, accountObjectDetailAccountTotalList, accountObjectDetailSubFundTotalList);
            reportSet.add(accountObjectDetailReport);
        }
        return reportSet;
    }


    /**
     * builds report Header
     * 
     * @param BudgetConstructionObjectSummary bcas
     */
    private void buildReportsHeader(Integer universityFiscalYear, BudgetConstructionOrgAccountObjectDetailReport orgAccountObjectDetailReportEntry, BudgetConstructionAccountBalance accountBalance, boolean consolidated) {
        String orgChartDesc = accountBalance.getOrganizationChartOfAccounts().getFinChartOfAccountDescription();
        String chartDesc = accountBalance.getChartOfAccounts().getFinChartOfAccountDescription();
        String orgName = "";
        try {
            orgName = accountBalance.getOrganization().getOrganizationName();
        }
        catch (PersistenceBrokerException e) {
        }
        String reportChartDesc = accountBalance.getChartOfAccounts().getReportsToChartOfAccounts().getFinChartOfAccountDescription();
        String subFundGroupName = accountBalance.getSubFundGroup().getSubFundGroupCode();
        String subFundGroupDes = accountBalance.getSubFundGroup().getSubFundGroupDescription();
        String fundGroupName = accountBalance.getSubFundGroup().getFundGroupCode();
        String fundGroupDes = accountBalance.getSubFundGroup().getFundGroup().getName();

        Integer prevFiscalyear = universityFiscalYear - 1;
        orgAccountObjectDetailReportEntry.setFiscalYear(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgAccountObjectDetailReportEntry.setOrgChartOfAccountsCode(accountBalance.getOrganizationChartOfAccountsCode());

        if (orgChartDesc == null) {
            orgAccountObjectDetailReportEntry.setOrgChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgAccountObjectDetailReportEntry.setOrgChartOfAccountDescription(orgChartDesc);
        }

        orgAccountObjectDetailReportEntry.setOrganizationCode(accountBalance.getOrganizationCode());
        if (orgName == null) {
            orgAccountObjectDetailReportEntry.setOrganizationName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ORGANIZATION_NAME));
        }
        else {
            orgAccountObjectDetailReportEntry.setOrganizationName(orgName);
        }

        orgAccountObjectDetailReportEntry.setChartOfAccountsCode(accountBalance.getChartOfAccountsCode());
        if (chartDesc == null) {
            orgAccountObjectDetailReportEntry.setChartOfAccountDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_CHART_DESCRIPTION));
        }
        else {
            orgAccountObjectDetailReportEntry.setChartOfAccountDescription(chartDesc);
        }

        orgAccountObjectDetailReportEntry.setFundGroupCode(accountBalance.getSubFundGroup().getFundGroupCode());
        if (fundGroupDes == null) {
            orgAccountObjectDetailReportEntry.setFundGroupName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_FUNDGROUP_NAME));
        }
        else {
            orgAccountObjectDetailReportEntry.setFundGroupName(fundGroupDes);
        }

        orgAccountObjectDetailReportEntry.setSubFundGroupCode(accountBalance.getSubFundGroupCode());
        if (subFundGroupDes == null) {
            orgAccountObjectDetailReportEntry.setSubFundGroupDescription(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION));
        }
        else {
            orgAccountObjectDetailReportEntry.setSubFundGroupDescription(subFundGroupDes);
        }

        Integer prevPrevFiscalyear = prevFiscalyear - 1;
        orgAccountObjectDetailReportEntry.setBaseFy(prevPrevFiscalyear.toString() + " - " + prevFiscalyear.toString().substring(2, 4));
        orgAccountObjectDetailReportEntry.setReqFy(prevFiscalyear.toString() + " - " + universityFiscalYear.toString().substring(2, 4));
        orgAccountObjectDetailReportEntry.setHeader1("Object Level Name");
        orgAccountObjectDetailReportEntry.setHeader2a("Lv. FTE");
        orgAccountObjectDetailReportEntry.setHeader2("FTE");
        orgAccountObjectDetailReportEntry.setHeader3("Amount");
        orgAccountObjectDetailReportEntry.setHeader31("FTE");
        orgAccountObjectDetailReportEntry.setHeader40("FTE");
        orgAccountObjectDetailReportEntry.setHeader4("Amount");
        orgAccountObjectDetailReportEntry.setHeader5(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        orgAccountObjectDetailReportEntry.setHeader6(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_HEADER_CHANGE));
        if (consolidated) {
            orgAccountObjectDetailReportEntry.setConsHdr(BCConstants.Report.CONSOLIIDATED);
        }
        else {
            orgAccountObjectDetailReportEntry.setConsHdr(BCConstants.Report.BLANK);
        }

        orgAccountObjectDetailReportEntry.setAccountNumber(accountBalance.getAccountNumber());
        String accountName = accountBalance.getAccount().getAccountName();
        if (accountName == null) {
            orgAccountObjectDetailReportEntry.setAccountName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION));
        }
        else {
            orgAccountObjectDetailReportEntry.setAccountName(accountName);
        }

        orgAccountObjectDetailReportEntry.setSubAccountNumber(accountBalance.getSubAccountNumber());
        //TODO: ask to Gary or Jerry about persistenceBreakerexception
        String subAccountName = "";
        try {
            subAccountName = accountBalance.getSubAccount().getSubAccountName();
        }
        catch (Exception e) {

        }

        if (subAccountName == null) {
            orgAccountObjectDetailReportEntry.setSubAccountName(kualiConfigurationService.getPropertyString(BCKeyConstants.ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION));
        }
        else {
            orgAccountObjectDetailReportEntry.setSubAccountName(subAccountName);
        }


        // For group
        orgAccountObjectDetailReportEntry.setSubAccountNumber(accountBalance.getSubAccountNumber() + accountBalance.getAccountNumber());
        orgAccountObjectDetailReportEntry.setFinancialObjectCode(accountBalance.getFinancialObjectCode());
        orgAccountObjectDetailReportEntry.setFinancialSubObjectCode(accountBalance.getFinancialSubObjectCode());
        // orgAccountObjectDetailReportEntry.setFinancialObjectLevelCode(accountBalance.getFinancialObjectLevelCode());
        orgAccountObjectDetailReportEntry.setIncomeExpenseCode(accountBalance.getIncomeExpenseCode());
        // orgAccountObjectDetailReportEntry.setFinancialConsolidationSortCode(accountBalance.getFinancialConsolidationSortCode());
        orgAccountObjectDetailReportEntry.setFinancialLevelSortCode(accountBalance.getFinancialLevelSortCode());

        // page break

        // page break org_fin_coa_cd, org_cd, sub_fund_grp_cd)%\
        orgAccountObjectDetailReportEntry.setPageBreak(accountBalance.getOrganizationChartOfAccountsCode() + accountBalance.getOrganizationCode() + accountBalance.getSubFundGroupCode());


    }


    /**
     * builds report body
     * 
     * @param BudgetConstructionLevelSummary bcas
     */
    private void buildReportsBody(Integer universityFiscalYear, BudgetConstructionOrgAccountObjectDetailReport orgAccountObjectDetailReportEntry, BudgetConstructionAccountBalance accountBalance) {
        // TODO: Use BudgetConstructionReportHelper
        orgAccountObjectDetailReportEntry.setFinancialObjectName(accountBalance.getFinancialObject().getFinancialObjectCodeName());

        if (accountBalance.getPositionCsfLeaveFteQuantity() != null && !accountBalance.getPositionCsfLeaveFteQuantity().equals(BigDecimal.ZERO)) {
            orgAccountObjectDetailReportEntry.setPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(accountBalance.getPositionCsfLeaveFteQuantity(), 2, true));
        }

        if (accountBalance.getPositionFullTimeEquivalencyQuantity() != null && !accountBalance.getPositionFullTimeEquivalencyQuantity().equals(BigDecimal.ZERO)) {
            orgAccountObjectDetailReportEntry.setPositionFullTimeEquivalencyQuantity(BudgetConstructionReportHelper.setDecimalDigit(accountBalance.getPositionFullTimeEquivalencyQuantity(), 2, true));
        }

        if (accountBalance.getAppointmentRequestedCsfFteQuantity() != null && !accountBalance.getAppointmentRequestedCsfFteQuantity().equals(BigDecimal.ZERO)) {
            orgAccountObjectDetailReportEntry.setAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(accountBalance.getAppointmentRequestedCsfFteQuantity(), 2, true));
        }

        if (accountBalance.getAppointmentRequestedFteQuantity() != null && !accountBalance.getAppointmentRequestedFteQuantity().equals(BigDecimal.ZERO)) {
            orgAccountObjectDetailReportEntry.setAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(accountBalance.getAppointmentRequestedFteQuantity(), 2, true));
        }

        if (accountBalance.getAccountLineAnnualBalanceAmount() != null) {
            orgAccountObjectDetailReportEntry.setAccountLineAnnualBalanceAmount(new Integer(accountBalance.getAccountLineAnnualBalanceAmount().intValue()));
        }

        if (accountBalance.getFinancialBeginningBalanceLineAmount() != null) {
            orgAccountObjectDetailReportEntry.setFinancialBeginningBalanceLineAmount(new Integer(accountBalance.getFinancialBeginningBalanceLineAmount().intValue()));
        }

        if (accountBalance.getAccountLineAnnualBalanceAmount() != null && accountBalance.getFinancialBeginningBalanceLineAmount() != null) {
            int changeAmount = accountBalance.getAccountLineAnnualBalanceAmount().subtract(accountBalance.getFinancialBeginningBalanceLineAmount()).intValue();
            orgAccountObjectDetailReportEntry.setAmountChange(new Integer(changeAmount));
        }
        orgAccountObjectDetailReportEntry.setPercentChange(BudgetConstructionReportHelper.calculatePercent(orgAccountObjectDetailReportEntry.getAmountChange(), orgAccountObjectDetailReportEntry.getFinancialBeginningBalanceLineAmount()));


    }


    /**
     * builds report total
     */

    private void buildReportsTotal(BudgetConstructionOrgAccountObjectDetailReport orgObjectSummaryReportEntry, BudgetConstructionAccountBalance accountBalance, List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalObjectList, List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalLevelList, List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailTotalGexpAndTypeList, List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailAccountTotalList, List<BudgetConstructionOrgAccountObjectDetailReportTotal> accountObjectDetailSubFundTotalList) {

        for (BudgetConstructionOrgAccountObjectDetailReportTotal objectTotal : accountObjectDetailTotalObjectList) {
            if (BudgetConstructionReportHelper.isSameEntry(accountBalance, objectTotal.getBudgetConstructionAccountBalance(), fieldsForObject())) {
                orgObjectSummaryReportEntry.setTotalObjectDescription(accountBalance.getFinancialObject().getFinancialObjectCodeName());

                orgObjectSummaryReportEntry.setTotalObjectPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectTotal.getTotalObjectPositionCsfLeaveFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalObjectPositionFullTimeEquivalencyQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectTotal.getTotalObjectPositionFullTimeEquivalencyQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalObjectFinancialBeginningBalanceLineAmount(objectTotal.getTotalObjectFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setTotalObjectAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectTotal.getTotalObjectAppointmentRequestedCsfFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalObjectAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(objectTotal.getTotalObjectAppointmentRequestedFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalObjectAccountLineAnnualBalanceAmount(objectTotal.getTotalObjectAccountLineAnnualBalanceAmount());

                Integer totalObjectAmountChange = objectTotal.getTotalObjectAccountLineAnnualBalanceAmount() - objectTotal.getTotalObjectFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setTotalObjectAmountChange(totalObjectAmountChange);
                orgObjectSummaryReportEntry.setTotalObjectPercentChange(BudgetConstructionReportHelper.calculatePercent(totalObjectAmountChange, objectTotal.getTotalObjectFinancialBeginningBalanceLineAmount()));
            }
        }

        for (BudgetConstructionOrgAccountObjectDetailReportTotal levelTotal : accountObjectDetailTotalLevelList) {
            if (BudgetConstructionReportHelper.isSameEntry(accountBalance, levelTotal.getBudgetConstructionAccountBalance(), fieldsForLevel())) {
                orgObjectSummaryReportEntry.setTotalLevelDescription(accountBalance.getFinancialObjectLevel().getFinancialObjectLevelName());

                orgObjectSummaryReportEntry.setTotalLevelPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelPositionCsfLeaveFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalLevelPositionFullTimeEquivalencyQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelPositionFullTimeEquivalencyQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalLevelFinancialBeginningBalanceLineAmount(levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setTotalLevelAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelAppointmentRequestedCsfFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalLevelAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(levelTotal.getTotalLevelAppointmentRequestedFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTotalLevelAccountLineAnnualBalanceAmount(levelTotal.getTotalLevelAccountLineAnnualBalanceAmount());

                Integer totalLevelAmountChange = levelTotal.getTotalLevelAccountLineAnnualBalanceAmount() - levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setTotalLevelAmountChange(totalLevelAmountChange);
                orgObjectSummaryReportEntry.setTotalLevelPercentChange(BudgetConstructionReportHelper.calculatePercent(totalLevelAmountChange, levelTotal.getTotalLevelFinancialBeginningBalanceLineAmount()));
            }
        }


        for (BudgetConstructionOrgAccountObjectDetailReportTotal gexpAndTypeTotal : accountObjectDetailTotalGexpAndTypeList) {
            if (BudgetConstructionReportHelper.isSameEntry(accountBalance, gexpAndTypeTotal.getBudgetConstructionAccountBalance(), fieldsForGexpAndType())) {

                orgObjectSummaryReportEntry.setGrossFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setGrossAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount());
                Integer grossAmountChange = gexpAndTypeTotal.getGrossAccountLineAnnualBalanceAmount() - gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setGrossAmountChange(grossAmountChange);
                orgObjectSummaryReportEntry.setGrossPercentChange(BudgetConstructionReportHelper.calculatePercent(grossAmountChange, gexpAndTypeTotal.getGrossFinancialBeginningBalanceLineAmount()));

                if (accountBalance.getIncomeExpenseCode().equals(BCConstants.Report.INCOME_EXP_TYPE_A)) {
                    orgObjectSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_UPPERCASE_REVENUE));
                }
                else {
                    orgObjectSummaryReportEntry.setTypeDesc(kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE_NET_TRNFR));
                }

                orgObjectSummaryReportEntry.setTypePositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypePositionCsfLeaveFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTypePositionFullTimeEquivalencyQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypePositionFullTimeEquivalencyQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTypeFinancialBeginningBalanceLineAmount(gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setTypeAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypeAppointmentRequestedCsfFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setTypeAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(gexpAndTypeTotal.getTypeAppointmentRequestedFteQuantity(), 2, true));

                orgObjectSummaryReportEntry.setTypeAccountLineAnnualBalanceAmount(gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount());
                Integer typeAmountChange = gexpAndTypeTotal.getTypeAccountLineAnnualBalanceAmount() - gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setTypeAmountChange(typeAmountChange);
                orgObjectSummaryReportEntry.setTypePercentChange(BudgetConstructionReportHelper.calculatePercent(typeAmountChange, gexpAndTypeTotal.getTypeFinancialBeginningBalanceLineAmount()));
            }
        }

        for (BudgetConstructionOrgAccountObjectDetailReportTotal accountTotal : accountObjectDetailAccountTotalList) {
            if (BudgetConstructionReportHelper.isSameEntry(accountBalance, accountTotal.getBudgetConstructionAccountBalance(), fieldsForAccountTotal())) {

                if (orgObjectSummaryReportEntry.getSubAccountName().equals(BCConstants.Report.BLANK)) {
                    orgObjectSummaryReportEntry.setAccountNameForAccountTotal(orgObjectSummaryReportEntry.getAccountName());
                }
                else {
                    orgObjectSummaryReportEntry.setAccountNameForAccountTotal(orgObjectSummaryReportEntry.getSubAccountName());
                }
                
                orgObjectSummaryReportEntry.setAccountPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(accountTotal.getAccountPositionCsfLeaveFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setAccountPositionFullTimeEquivalencyQuantity(BudgetConstructionReportHelper.setDecimalDigit(accountTotal.getAccountPositionFullTimeEquivalencyQuantity(), 2, true));
                orgObjectSummaryReportEntry.setAccountAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(accountTotal.getAccountAppointmentRequestedCsfFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setAccountAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(accountTotal.getAccountAppointmentRequestedFteQuantity(), 2, true));
                
                orgObjectSummaryReportEntry.setAccountRevenueFinancialBeginningBalanceLineAmount(accountTotal.getAccountRevenueFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setAccountRevenueAccountLineAnnualBalanceAmount(accountTotal.getAccountRevenueAccountLineAnnualBalanceAmount());

                Integer accountRevenueAmountChange = accountTotal.getAccountRevenueAccountLineAnnualBalanceAmount() - accountTotal.getAccountRevenueFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setAccountRevenueAmountChange(accountRevenueAmountChange);
                orgObjectSummaryReportEntry.setAccountRevenuePercentChange(BudgetConstructionReportHelper.calculatePercent(accountRevenueAmountChange, accountTotal.getAccountRevenueFinancialBeginningBalanceLineAmount()));

                Integer accountGrossFinancialBeginningBalanceLineAmount = accountTotal.getAccountExpenditureFinancialBeginningBalanceLineAmount() - accountTotal.getAccountTrnfrInFinancialBeginningBalanceLineAmount();
                Integer accountGrossAccountLineAnnualBalanceAmount = accountTotal.getAccountExpenditureAccountLineAnnualBalanceAmount() - accountTotal.getAccountTrnfrInAccountLineAnnualBalanceAmount();
                orgObjectSummaryReportEntry.setAccountGrossFinancialBeginningBalanceLineAmount(accountGrossFinancialBeginningBalanceLineAmount);
                orgObjectSummaryReportEntry.setAccountGrossAccountLineAnnualBalanceAmount(accountGrossAccountLineAnnualBalanceAmount);
                Integer accountGrossAmountChange = accountGrossAccountLineAnnualBalanceAmount - accountGrossFinancialBeginningBalanceLineAmount;
                orgObjectSummaryReportEntry.setAccountGrossAmountChange(accountGrossAmountChange);
                orgObjectSummaryReportEntry.setAccountGrossPercentChange(BudgetConstructionReportHelper.calculatePercent(accountGrossAmountChange, accountGrossFinancialBeginningBalanceLineAmount));

                orgObjectSummaryReportEntry.setAccountTrnfrInFinancialBeginningBalanceLineAmount(accountTotal.getAccountTrnfrInFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setAccountTrnfrInAccountLineAnnualBalanceAmount(accountTotal.getAccountTrnfrInAccountLineAnnualBalanceAmount());

                Integer accountTrnfrInAmountChange = accountTotal.getAccountTrnfrInAccountLineAnnualBalanceAmount() - accountTotal.getAccountTrnfrInFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setAccountTrnfrInAmountChange(accountTrnfrInAmountChange);
                orgObjectSummaryReportEntry.setAccountTrnfrInPercentChange(BudgetConstructionReportHelper.calculatePercent(accountTrnfrInAmountChange, accountTotal.getAccountTrnfrInFinancialBeginningBalanceLineAmount()));

                orgObjectSummaryReportEntry.setAccountExpenditureFinancialBeginningBalanceLineAmount(accountTotal.getAccountExpenditureFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setAccountExpenditureAccountLineAnnualBalanceAmount(accountTotal.getAccountExpenditureAccountLineAnnualBalanceAmount());

                Integer accountExpenditureAmountChange = accountTotal.getAccountExpenditureAccountLineAnnualBalanceAmount() - accountTotal.getAccountExpenditureFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setAccountExpenditureAmountChange(accountExpenditureAmountChange);
                orgObjectSummaryReportEntry.setAccountExpenditurePercentChange(BudgetConstructionReportHelper.calculatePercent(accountExpenditureAmountChange, accountTotal.getAccountExpenditureFinancialBeginningBalanceLineAmount()));

                Integer accountDifferenceFinancialBeginningBalanceLineAmount = accountTotal.getAccountRevenueFinancialBeginningBalanceLineAmount() - accountTotal.getAccountExpenditureFinancialBeginningBalanceLineAmount();
                Integer accountDifferenceAccountLineAnnualBalanceAmount = accountTotal.getAccountRevenueAccountLineAnnualBalanceAmount() - accountTotal.getAccountExpenditureAccountLineAnnualBalanceAmount();

                orgObjectSummaryReportEntry.setAccountDifferenceFinancialBeginningBalanceLineAmount(accountDifferenceFinancialBeginningBalanceLineAmount);
                orgObjectSummaryReportEntry.setAccountDifferenceAccountLineAnnualBalanceAmount(accountDifferenceAccountLineAnnualBalanceAmount);

                orgObjectSummaryReportEntry.setAccountDifferenceFinancialBeginningBalanceLineAmount(accountDifferenceFinancialBeginningBalanceLineAmount);
                orgObjectSummaryReportEntry.setAccountDifferenceAccountLineAnnualBalanceAmount(accountDifferenceAccountLineAnnualBalanceAmount);

                Integer accountDifferenceAmountChange = accountDifferenceAccountLineAnnualBalanceAmount - accountDifferenceFinancialBeginningBalanceLineAmount;
                orgObjectSummaryReportEntry.setAccountDifferenceAmountChange(accountDifferenceAmountChange);
                orgObjectSummaryReportEntry.setAccountDifferencePercentChange(BudgetConstructionReportHelper.calculatePercent(accountDifferenceAmountChange, accountDifferenceFinancialBeginningBalanceLineAmount));
            }
        }

        for (BudgetConstructionOrgAccountObjectDetailReportTotal subFundTotal : accountObjectDetailSubFundTotalList) {
            if (BudgetConstructionReportHelper.isSameEntry(accountBalance, subFundTotal.getBudgetConstructionAccountBalance(), fieldsForSubFundTotal())) {

                orgObjectSummaryReportEntry.setSubFundPositionCsfLeaveFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(subFundTotal.getSubFundPositionCsfLeaveFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setSubFundPositionFullTimeEquivalencyQuantity(BudgetConstructionReportHelper.setDecimalDigit(subFundTotal.getSubFundPositionFullTimeEquivalencyQuantity(), 2, true));
                orgObjectSummaryReportEntry.setSubFundAppointmentRequestedCsfFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(subFundTotal.getSubFundAppointmentRequestedCsfFteQuantity(), 2, true));
                orgObjectSummaryReportEntry.setSubFundAppointmentRequestedFteQuantity(BudgetConstructionReportHelper.setDecimalDigit(subFundTotal.getSubFundAppointmentRequestedFteQuantity(), 2, true));
                
                orgObjectSummaryReportEntry.setSubFundRevenueFinancialBeginningBalanceLineAmount(subFundTotal.getSubFundRevenueFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setSubFundRevenueAccountLineAnnualBalanceAmount(subFundTotal.getSubFundRevenueAccountLineAnnualBalanceAmount());

                Integer subFundRevenueAmountChange = subFundTotal.getSubFundRevenueAccountLineAnnualBalanceAmount() - subFundTotal.getSubFundRevenueFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setSubFundRevenueAmountChange(subFundRevenueAmountChange);
                orgObjectSummaryReportEntry.setSubFundRevenuePercentChange(BudgetConstructionReportHelper.calculatePercent(subFundRevenueAmountChange, subFundTotal.getSubFundRevenueFinancialBeginningBalanceLineAmount()));

                Integer subFundGrossFinancialBeginningBalanceLineAmount = subFundTotal.getSubFundExpenditureFinancialBeginningBalanceLineAmount() - subFundTotal.getSubFundTrnfrInFinancialBeginningBalanceLineAmount();
                Integer subFundGrossAccountLineAnnualBalanceAmount = subFundTotal.getSubFundExpenditureAccountLineAnnualBalanceAmount() - subFundTotal.getSubFundTrnfrInAccountLineAnnualBalanceAmount();
                orgObjectSummaryReportEntry.setSubFundGrossFinancialBeginningBalanceLineAmount(subFundGrossFinancialBeginningBalanceLineAmount);
                orgObjectSummaryReportEntry.setSubFundGrossAccountLineAnnualBalanceAmount(subFundGrossAccountLineAnnualBalanceAmount);
                Integer subFundGrossAmountChange = subFundGrossAccountLineAnnualBalanceAmount - subFundGrossFinancialBeginningBalanceLineAmount;
                orgObjectSummaryReportEntry.setSubFundGrossAmountChange(subFundGrossAmountChange);
                orgObjectSummaryReportEntry.setSubFundGrossPercentChange(BudgetConstructionReportHelper.calculatePercent(subFundGrossAmountChange, subFundGrossFinancialBeginningBalanceLineAmount));

                orgObjectSummaryReportEntry.setSubFundTrnfrInFinancialBeginningBalanceLineAmount(subFundTotal.getSubFundTrnfrInFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setSubFundTrnfrInAccountLineAnnualBalanceAmount(subFundTotal.getSubFundTrnfrInAccountLineAnnualBalanceAmount());

                Integer subFundTrnfrInAmountChange = subFundTotal.getSubFundTrnfrInAccountLineAnnualBalanceAmount() - subFundTotal.getSubFundTrnfrInFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setSubFundTrnfrInAmountChange(subFundTrnfrInAmountChange);
                orgObjectSummaryReportEntry.setSubFundTrnfrInPercentChange(BudgetConstructionReportHelper.calculatePercent(subFundTrnfrInAmountChange, subFundTotal.getSubFundTrnfrInFinancialBeginningBalanceLineAmount()));

                orgObjectSummaryReportEntry.setSubFundExpenditureFinancialBeginningBalanceLineAmount(subFundTotal.getSubFundExpenditureFinancialBeginningBalanceLineAmount());
                orgObjectSummaryReportEntry.setSubFundExpenditureAccountLineAnnualBalanceAmount(subFundTotal.getSubFundExpenditureAccountLineAnnualBalanceAmount());

                Integer subFundExpenditureAmountChange = subFundTotal.getSubFundExpenditureAccountLineAnnualBalanceAmount() - subFundTotal.getSubFundExpenditureFinancialBeginningBalanceLineAmount();
                orgObjectSummaryReportEntry.setSubFundExpenditureAmountChange(subFundExpenditureAmountChange);
                orgObjectSummaryReportEntry.setSubFundExpenditurePercentChange(BudgetConstructionReportHelper.calculatePercent(subFundExpenditureAmountChange, subFundTotal.getSubFundExpenditureFinancialBeginningBalanceLineAmount()));

                Integer subFundDifferenceFinancialBeginningBalanceLineAmount = subFundTotal.getSubFundRevenueFinancialBeginningBalanceLineAmount() - subFundTotal.getSubFundExpenditureFinancialBeginningBalanceLineAmount();
                Integer subFundDifferenceAccountLineAnnualBalanceAmount = subFundTotal.getSubFundRevenueAccountLineAnnualBalanceAmount() - subFundTotal.getSubFundExpenditureAccountLineAnnualBalanceAmount();

                orgObjectSummaryReportEntry.setSubFundDifferenceFinancialBeginningBalanceLineAmount(subFundDifferenceFinancialBeginningBalanceLineAmount);
                orgObjectSummaryReportEntry.setSubFundDifferenceAccountLineAnnualBalanceAmount(subFundDifferenceAccountLineAnnualBalanceAmount);

                orgObjectSummaryReportEntry.setSubFundDifferenceFinancialBeginningBalanceLineAmount(subFundDifferenceFinancialBeginningBalanceLineAmount);
                orgObjectSummaryReportEntry.setSubFundDifferenceAccountLineAnnualBalanceAmount(subFundDifferenceAccountLineAnnualBalanceAmount);

                Integer subFundDifferenceAmountChange = subFundDifferenceAccountLineAnnualBalanceAmount - subFundDifferenceFinancialBeginningBalanceLineAmount;
                orgObjectSummaryReportEntry.setSubFundDifferenceAmountChange(subFundDifferenceAmountChange);
                orgObjectSummaryReportEntry.setSubFundDifferencePercentChange(BudgetConstructionReportHelper.calculatePercent(subFundDifferenceAmountChange, subFundDifferenceFinancialBeginningBalanceLineAmount));
            }
        }


    }

    private List calculateObjectTotal(List<BudgetConstructionAccountBalance> bcosList, List<BudgetConstructionAccountBalance> simpleList) {

        BigDecimal totalObjectPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
        Integer totalObjectFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalObjectAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalObjectAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();

        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {

            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcosListEntry : bcosList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcosListEntry, fieldsForObject())) {
                    totalObjectFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalObjectAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalObjectPositionCsfLeaveFteQuantity = totalObjectPositionCsfLeaveFteQuantity.add(bcosListEntry.getPositionCsfLeaveFteQuantity());
                    totalObjectPositionFullTimeEquivalencyQuantity = totalObjectPositionFullTimeEquivalencyQuantity.add(bcosListEntry.getPositionFullTimeEquivalencyQuantity());
                    totalObjectAppointmentRequestedCsfFteQuantity = totalObjectAppointmentRequestedCsfFteQuantity.add(bcosListEntry.getAppointmentRequestedCsfFteQuantity());
                    totalObjectAppointmentRequestedFteQuantity = totalObjectAppointmentRequestedFteQuantity.add(bcosListEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);
            bcObjectTotal.setTotalObjectPositionCsfLeaveFteQuantity(totalObjectPositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalObjectPositionFullTimeEquivalencyQuantity(totalObjectPositionFullTimeEquivalencyQuantity);
            bcObjectTotal.setTotalObjectFinancialBeginningBalanceLineAmount(totalObjectFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalObjectAppointmentRequestedCsfFteQuantity(totalObjectAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalObjectAppointmentRequestedFteQuantity(totalObjectAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalObjectAccountLineAnnualBalanceAmount(totalObjectAccountLineAnnualBalanceAmount);

            returnList.add(bcObjectTotal);

            totalObjectPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalObjectPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
            totalObjectFinancialBeginningBalanceLineAmount = new Integer(0);
            totalObjectAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalObjectAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalObjectAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;

    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    private List calculateLevelTotal(List<BudgetConstructionAccountBalance> bcosList, List<BudgetConstructionAccountBalance> simpleList) {

        BigDecimal totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
        Integer totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer totalLevelAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();

        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {

            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcosListEntry : bcosList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcosListEntry, fieldsForLevel())) {
                    totalLevelFinancialBeginningBalanceLineAmount += new Integer(bcosListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    totalLevelAccountLineAnnualBalanceAmount += new Integer(bcosListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    totalLevelPositionCsfLeaveFteQuantity = totalLevelPositionCsfLeaveFteQuantity.add(bcosListEntry.getPositionCsfLeaveFteQuantity());
                    totalLevelPositionFullTimeEquivalencyQuantity = totalLevelPositionFullTimeEquivalencyQuantity.add(bcosListEntry.getPositionFullTimeEquivalencyQuantity());
                    totalLevelAppointmentRequestedCsfFteQuantity = totalLevelAppointmentRequestedCsfFteQuantity.add(bcosListEntry.getAppointmentRequestedCsfFteQuantity());
                    totalLevelAppointmentRequestedFteQuantity = totalLevelAppointmentRequestedFteQuantity.add(bcosListEntry.getAppointmentRequestedFteQuantity());
                }
            }
            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);
            bcObjectTotal.setTotalLevelPositionCsfLeaveFteQuantity(totalLevelPositionCsfLeaveFteQuantity);
            bcObjectTotal.setTotalLevelPositionFullTimeEquivalencyQuantity(totalLevelPositionFullTimeEquivalencyQuantity);
            bcObjectTotal.setTotalLevelFinancialBeginningBalanceLineAmount(totalLevelFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTotalLevelAppointmentRequestedCsfFteQuantity(totalLevelAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTotalLevelAppointmentRequestedFteQuantity(totalLevelAppointmentRequestedFteQuantity);
            bcObjectTotal.setTotalLevelAccountLineAnnualBalanceAmount(totalLevelAccountLineAnnualBalanceAmount);

            returnList.add(bcObjectTotal);

            totalLevelPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            totalLevelPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
            totalLevelFinancialBeginningBalanceLineAmount = new Integer(0);
            totalLevelAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            totalLevelAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            totalLevelAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;
    }


    private List calculateGexpAndTypeTotal(List<BudgetConstructionAccountBalance> bcabList, List<BudgetConstructionAccountBalance> simpleList) {

        Integer grossFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer grossAccountLineAnnualBalanceAmount = new Integer(0);

        BigDecimal typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal typePositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
        Integer typeFinancialBeginningBalanceLineAmount = new Integer(0);
        BigDecimal typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
        Integer typeAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();
        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {
            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcabListEntry : bcabList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcabListEntry, fieldsForGexpAndType())) {

                    typeFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                    typeAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    typePositionCsfLeaveFteQuantity = typePositionCsfLeaveFteQuantity.add(bcabListEntry.getPositionCsfLeaveFteQuantity());
                    typePositionFullTimeEquivalencyQuantity = typePositionFullTimeEquivalencyQuantity.add(bcabListEntry.getPositionFullTimeEquivalencyQuantity());
                    typeAppointmentRequestedCsfFteQuantity = typeAppointmentRequestedCsfFteQuantity.add(bcabListEntry.getAppointmentRequestedCsfFteQuantity());
                    typeAppointmentRequestedFteQuantity = typeAppointmentRequestedFteQuantity.add(bcabListEntry.getAppointmentRequestedFteQuantity());

                    if (bcabListEntry.getIncomeExpenseCode().equals("B") && !bcabListEntry.getFinancialObjectLevelCode().equals("CORI") && !bcabListEntry.getFinancialObjectLevelCode().equals("TRIN")) {
                        grossFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        grossAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                }
            }
            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);

            bcObjectTotal.setGrossFinancialBeginningBalanceLineAmount(grossFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setGrossAccountLineAnnualBalanceAmount(grossAccountLineAnnualBalanceAmount);

            bcObjectTotal.setTypePositionCsfLeaveFteQuantity(typePositionCsfLeaveFteQuantity);
            bcObjectTotal.setTypePositionFullTimeEquivalencyQuantity(typePositionFullTimeEquivalencyQuantity);
            bcObjectTotal.setTypeFinancialBeginningBalanceLineAmount(typeFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setTypeAppointmentRequestedCsfFteQuantity(typeAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setTypeAppointmentRequestedFteQuantity(typeAppointmentRequestedFteQuantity);
            bcObjectTotal.setTypeAccountLineAnnualBalanceAmount(typeAccountLineAnnualBalanceAmount);

            returnList.add(bcObjectTotal);
            grossFinancialBeginningBalanceLineAmount = new Integer(0);
            grossAccountLineAnnualBalanceAmount = new Integer(0);

            typePositionCsfLeaveFteQuantity = BigDecimal.ZERO;
            typePositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
            typeFinancialBeginningBalanceLineAmount = new Integer(0);
            typeAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
            typeAppointmentRequestedFteQuantity = BigDecimal.ZERO;
            typeAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;
    }

    private List calculateAccountTotal(List<BudgetConstructionAccountBalance> bcabList, List<BudgetConstructionAccountBalance> simpleList) {
        BigDecimal accountPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal accountPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
        BigDecimal accountAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal accountAppointmentRequestedFteQuantity = BigDecimal.ZERO;

        Integer accountRevenueFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer accountRevenueAccountLineAnnualBalanceAmount = new Integer(0);
        Integer accountTrnfrInFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer accountTrnfrInAccountLineAnnualBalanceAmount = new Integer(0);
        Integer accountExpenditureFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer accountExpenditureAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();

        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {
            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcabListEntry : bcabList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcabListEntry, fieldsForAccountTotal())) {

                    accountPositionCsfLeaveFteQuantity = accountPositionCsfLeaveFteQuantity.add(bcabListEntry.getPositionCsfLeaveFteQuantity());
                    accountPositionFullTimeEquivalencyQuantity = accountPositionFullTimeEquivalencyQuantity.add(bcabListEntry.getPositionFullTimeEquivalencyQuantity());
                    accountAppointmentRequestedCsfFteQuantity = accountAppointmentRequestedCsfFteQuantity.add(bcabListEntry.getAppointmentRequestedCsfFteQuantity());
                    accountAppointmentRequestedFteQuantity = accountAppointmentRequestedFteQuantity.add(bcabListEntry.getAppointmentRequestedFteQuantity());

                    if (bcabListEntry.getIncomeExpenseCode().equals("A")) {
                        accountRevenueFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        accountRevenueAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                    else {
                        accountExpenditureFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        accountExpenditureAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }

                    if (bcabListEntry.getIncomeExpenseCode().equals("B")) {
                        if (bcabListEntry.getFinancialObjectLevelCode().equals("CORI") || bcabListEntry.getFinancialObjectLevelCode().equals("TRIN")) {
                            accountTrnfrInFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                            accountTrnfrInAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                        }
                    }
                }
            }

            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);

            bcObjectTotal.setAccountPositionCsfLeaveFteQuantity(accountPositionCsfLeaveFteQuantity);
            bcObjectTotal.setAccountPositionFullTimeEquivalencyQuantity(accountPositionFullTimeEquivalencyQuantity);
            bcObjectTotal.setAccountAppointmentRequestedCsfFteQuantity(accountAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setAccountAppointmentRequestedFteQuantity(accountAppointmentRequestedFteQuantity);

            bcObjectTotal.setAccountRevenueFinancialBeginningBalanceLineAmount(accountRevenueFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setAccountRevenueAccountLineAnnualBalanceAmount(accountRevenueAccountLineAnnualBalanceAmount);
            bcObjectTotal.setAccountTrnfrInFinancialBeginningBalanceLineAmount(accountTrnfrInFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setAccountTrnfrInAccountLineAnnualBalanceAmount(accountTrnfrInAccountLineAnnualBalanceAmount);
            bcObjectTotal.setAccountExpenditureFinancialBeginningBalanceLineAmount(accountExpenditureFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setAccountExpenditureAccountLineAnnualBalanceAmount(accountExpenditureAccountLineAnnualBalanceAmount);


            returnList.add(bcObjectTotal);

            accountRevenueFinancialBeginningBalanceLineAmount = new Integer(0);
            accountRevenueAccountLineAnnualBalanceAmount = new Integer(0);

            accountExpenditureFinancialBeginningBalanceLineAmount = new Integer(0);
            accountExpenditureAccountLineAnnualBalanceAmount = new Integer(0);
        }
        return returnList;
    }


    private List calculateSubFundTotal(List<BudgetConstructionAccountBalance> bcabList, List<BudgetConstructionAccountBalance> simpleList) {

        BigDecimal subFundPositionCsfLeaveFteQuantity = BigDecimal.ZERO;
        BigDecimal subFundPositionFullTimeEquivalencyQuantity = BigDecimal.ZERO;
        BigDecimal subFundAppointmentRequestedCsfFteQuantity = BigDecimal.ZERO;
        BigDecimal subFundAppointmentRequestedFteQuantity = BigDecimal.ZERO;

        Integer subFundRevenueFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer subFundRevenueAccountLineAnnualBalanceAmount = new Integer(0);
        Integer subFundTrnfrInFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer subFundTrnfrInAccountLineAnnualBalanceAmount = new Integer(0);
        Integer subFundExpenditureFinancialBeginningBalanceLineAmount = new Integer(0);
        Integer subFundExpenditureAccountLineAnnualBalanceAmount = new Integer(0);

        List returnList = new ArrayList();

        for (BudgetConstructionAccountBalance simpleBcosEntry : simpleList) {
            BudgetConstructionOrgAccountObjectDetailReportTotal bcObjectTotal = new BudgetConstructionOrgAccountObjectDetailReportTotal();
            for (BudgetConstructionAccountBalance bcabListEntry : bcabList) {
                if (BudgetConstructionReportHelper.isSameEntry(simpleBcosEntry, bcabListEntry, fieldsForSubFundTotal())) {

                    subFundPositionCsfLeaveFteQuantity = subFundPositionCsfLeaveFteQuantity.add(bcabListEntry.getPositionCsfLeaveFteQuantity());
                    subFundPositionFullTimeEquivalencyQuantity = subFundPositionFullTimeEquivalencyQuantity.add(bcabListEntry.getPositionFullTimeEquivalencyQuantity());
                    subFundAppointmentRequestedCsfFteQuantity = subFundAppointmentRequestedCsfFteQuantity.add(bcabListEntry.getAppointmentRequestedCsfFteQuantity());
                    subFundAppointmentRequestedFteQuantity = subFundAppointmentRequestedFteQuantity.add(bcabListEntry.getAppointmentRequestedFteQuantity());

                    if (bcabListEntry.getIncomeExpenseCode().equals("A")) {
                        subFundRevenueFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        subFundRevenueAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }
                    else {
                        subFundExpenditureFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                        subFundExpenditureAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                    }

                    if (bcabListEntry.getIncomeExpenseCode().equals("B")) {
                        if (bcabListEntry.getFinancialObjectLevelCode().equals("CORI") || bcabListEntry.getFinancialObjectLevelCode().equals("TRIN")) {
                            subFundTrnfrInFinancialBeginningBalanceLineAmount += new Integer(bcabListEntry.getFinancialBeginningBalanceLineAmount().intValue());
                            subFundTrnfrInAccountLineAnnualBalanceAmount += new Integer(bcabListEntry.getAccountLineAnnualBalanceAmount().intValue());
                        }
                    }
                }
            }

            bcObjectTotal.setBudgetConstructionAccountBalance(simpleBcosEntry);

            bcObjectTotal.setSubFundPositionCsfLeaveFteQuantity(subFundPositionCsfLeaveFteQuantity);
            bcObjectTotal.setSubFundPositionFullTimeEquivalencyQuantity(subFundPositionFullTimeEquivalencyQuantity);
            bcObjectTotal.setSubFundAppointmentRequestedCsfFteQuantity(subFundAppointmentRequestedCsfFteQuantity);
            bcObjectTotal.setSubFundAppointmentRequestedFteQuantity(subFundAppointmentRequestedFteQuantity);

            bcObjectTotal.setSubFundRevenueFinancialBeginningBalanceLineAmount(subFundRevenueFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setSubFundRevenueAccountLineAnnualBalanceAmount(subFundRevenueAccountLineAnnualBalanceAmount);

            bcObjectTotal.setSubFundTrnfrInFinancialBeginningBalanceLineAmount(subFundTrnfrInFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setSubFundTrnfrInAccountLineAnnualBalanceAmount(subFundTrnfrInAccountLineAnnualBalanceAmount);

            bcObjectTotal.setSubFundExpenditureFinancialBeginningBalanceLineAmount(subFundExpenditureFinancialBeginningBalanceLineAmount);
            bcObjectTotal.setSubFundExpenditureAccountLineAnnualBalanceAmount(subFundExpenditureAccountLineAnnualBalanceAmount);


            returnList.add(bcObjectTotal);

            subFundRevenueFinancialBeginningBalanceLineAmount = new Integer(0);
            subFundRevenueAccountLineAnnualBalanceAmount = new Integer(0);

            subFundExpenditureFinancialBeginningBalanceLineAmount = new Integer(0);
            subFundExpenditureAccountLineAnnualBalanceAmount = new Integer(0);

        }
        return returnList;
    }


    /**
     * builds list of fields for comparing entry of Object
     * 
     * @return List<String>
     */
    private List<String> fieldsForObject() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForLevel());
        fieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        fieldList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return fieldList;
    }

    /**
     * builds list of fields for comparing entry of Level
     * 
     * @return List<String>
     */
    private List<String> fieldsForLevel() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForGexpAndType());
        fieldList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
        return fieldList;
    }

    /**
     * builds list of fields for comparing entry of GexpAndType
     * 
     * @return List<String>
     */
    private List<String> fieldsForGexpAndType() {
        List<String> fieldList = new ArrayList();
        fieldList.addAll(fieldsForAccountTotal());
        fieldList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        return fieldList;
    }

    /**
     * builds list of fields for comparing entry of AccountTotal
     * 
     * @return List<String>
     */
    private List<String> fieldsForAccountTotal() {
        List<String> fieldList = new ArrayList();
        // fieldList.addAll(fieldsForSubFundTotal());
        fieldList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        fieldList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        return fieldList;
    }

    /**
     * builds list of fields for comparing entry of SubFundTotal total
     * 
     * @return List<String>
     */
    private List<String> fieldsForSubFundTotal() {
        List<String> fieldList = new ArrayList();

        fieldList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        fieldList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        fieldList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        return fieldList;
    }


    public List<String> buildOrderByList() {
        List<String> returnList = new ArrayList();
        returnList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        returnList.add(KFSPropertyConstants.SUB_FUND_GROUP_CODE);
        returnList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        returnList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        returnList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        returnList.add(KFSPropertyConstants.INCOME_EXPENSE_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_CONSOLIDATION_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_LEVEL_SORT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        returnList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        return returnList;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setBudgetConstructionAccountObjectDetailReportDao(BudgetConstructionAccountObjectDetailReportDao budgetConstructionAccountObjectDetailReportDao) {
        this.budgetConstructionAccountObjectDetailReportDao = budgetConstructionAccountObjectDetailReportDao;
    }

    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

}
