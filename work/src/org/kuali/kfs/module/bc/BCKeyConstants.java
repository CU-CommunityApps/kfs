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
package org.kuali.kfs.module.bc;

/**
 * Constants for message keys. Should have corresponding key=message in resources.
 */
public class BCKeyConstants {
    public static final String MESSAGE_BUDGET_SYSTEM_VIEW_ONLY = "message.budget.systemViewOnly";
    public static final String MESSAGE_BUDGET_VIEW_ONLY = "message.budget.viewOnly";
    public static final String MESSAGE_BUDGET_EDIT_ACCESS = "message.budget.editAccess";
    public static final String MESSAGE_BUDGET_SUCCESSFUL_CLOSE = "message.budget.successfulClose";
    public static final String MESSAGE_BENEFITS_CALCULATED = "message.budget.benefitsCalculated";
    public static final String MESSAGE_BENEFITS_MONTHLY_CALCULATED = "message.budget.benefitsMonthlyCalculated";
    public static final String MESSAGE_SALARY_SETTING_SAVED = "message.budget.salarySettingSaved";
    public static final String ERROR_BUDGET_AUTHORIZATION_DOCUMENT = "error.budget.authorization.document";
    public static final String ERROR_BUDGET_SUBFUND_NOT_SELECTED = "error.budget.subFundNotSelected";
    public static final String ERROR_BUDGET_OBJECT_CODE_NOT_SELECTED = "error.budget.objectCodeNotSelected";
    public static final String ERROR_BUDGET_REASON_CODE_NOT_SELECTED = "error.budget.reasonCodeNotSelected";
    public static final String ERROR_BUDGET_ORG_NOT_SELECTED = "error.budget.orgNotSelected";
    public static final String ERROR_BUDGET_LINE_EXISTS = "error.budget.lineExists";
    public static final String ERROR_LABOR_OBJECT_IN_NOWAGES_ACCOUNT = "error.budget.laborObjectInNoWagesAccount";
    public static final String ERROR_FRINGE_BENEFIT_OBJECT_NOT_ALLOWED = "error.budget.fringeBenefitObjectNotAllowed";
    public static final String ERROR_SALARY_SETTING_OBJECT_ONLY = "error.budget.salarySettingObjectOnly";
    public static final String ERROR_NO_BUDGET_ALLOWED = "error.budget.noBudgetAllowed";
    public static final String ERROR_ACCOUNT_EXPIRES_WARNING = "error.budget.accountExpires";
    public static final String ERROR_BUDGET_RECORDING_LEVEL_NOT_ALLOWED = "error.budget.budgetRecordingLevelNotAllowed";
    public static final String ERROR_SUB_ACCOUNT_TYPE_NOT_ALLOWED = "error.budget.subAccountTypeNotAllowed";
    public static final String ERROR_NO_DELETE_ALLOWED_WITH_BASE = "error.budget.noDeleteAllowedWithBase";
    public static final String ERROR_NO_DELETE_ALLOWED_SALARY_DETAIL = "error.budget.noDeleteAllowedSalaryDetail";
    public static final String ERROR_MONTHLY_SUM_REQUEST_NOT_EQUAL = "error.budget.monthlySumRequestNotEqual";
    public static final String ERROR_SALARY_SUM_REQUEST_NOT_EQUAL = "error.budget.salarySumRequestNotEqual";
    
    public static final String ERROR_REPORT_GETTING_CHART_DESCRIPTION = "error.budget.report.gettingChartDescription";
    public static final String ERROR_REPORT_GETTING_OBJECT_CODE = "error.budget.report.gettingObjectCode";
    public static final String ERROR_REPORT_GETTING_OBJECT_NAME = "error.budget.report.gettingObjectName";
    public static final String ERROR_REPORT_GETTING_ACCOUNT_DESCRIPTION = "error.budget.report.gettingAccountDescription";
    public static final String ERROR_REPORT_GETTING_SUB_ACCOUNT_DESCRIPTION = "error.budget.report.gettingSubAccountDescription";
    public static final String ERROR_REPORT_GETTING_ORGANIZATION_NAME = "error.budget.report.gettingOrganizationName";
    public static final String ERROR_REPORT_GETTING_FUNDGROUP_NAME = "error.budget.report.gettingFundGroupName";
    public static final String ERROR_REPORT_GETTING_FUNDGROUP_CODE = "error.budget.report.gettingFundGroupCode";
    public static final String ERROR_REPORT_GETTING_SUBFUNDGROUP_DESCRIPTION = "error.budget.report.gettingSubFundGroupDescription";
    public static final String MSG_REPORT_HEADER_ACCOUNT_SUB = "message.budget.report.header.accountSub";
    public static final String MSG_REPORT_HEADER_ACCOUNT_SUB_NAME = "message.budget.report.header.accountSubName"; 
    public static final String MSG_REPORT_HEADER_BASE_AMOUNT = "message.budget.report.header.baseAmount";
    public static final String MSG_REPORT_HEADER_REQ_AMOUNT = "message.budget.report.header.reqAmount";
    public static final String MSG_REPORT_HEADER_CHANGE = "message.budget.report.header.change";
    public static final String MSG_REPORT_INCOME_EXP_DESC_REVENUE = "message.budget.report.incomeExpDescRevenue";
    public static final String MSG_REPORT_INCOME_EXP_DESC_UPPERCASE_REVENUE = "message.budget.report.incomeExpDescUppercaseRevenue";
    public static final String MSG_REPORT_INCOME_EXP_DESC_EXP_GROSS = "message.budget.report.incomeExpDescExpGross";
    public static final String MSG_REPORT_INCOME_EXP_DESC_TRNFR_IN = "message.budget.report.incomeExpDescTrnfrIn";
    public static final String MSG_REPORT_INCOME_EXP_DESC_EXP_NET_TRNFR = "message.budget.report.incomeExpDescExpNetTrnfr";
    public static final String MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE_NET_TRNFR = "message.budget.report.incomeExpDescExpenditureNetTrnfr";
    public static final String MSG_REPORT_INCOME_EXP_DESC_EXPENDITURE = "message.budget.report.incomeExpDescExpenditure";
    
    public static final String MSG_REPORT_HEADER_SUBFUND = "message.budget.report.header.subFund";
    public static final String MSG_REPORT_HEADER_SUBFUND_DESCRIPTION = "message.budget.report.header.subFundDescription";
    
    public static final String ERROR_FILE_IS_REQUIRED = "error.budget.requestImport.missingFile";
    public static final String ERROR_FILE_EMPTY = "error.budget.requestImport.emptyFile";
    public static final String ERROR_FILE_TYPE_IS_REQUIRED = "error.budget.requestImport.missingFileType";
    public static final String ERROR_FILENAME_REQUIRED = "error.budget.requestImport.missingFileName";
    public static final String ERROR_FIELD_SEPARATOR_REQUIRED = "error.budget.requestImport.missingFieldSeparator";
    public static final String ERROR_TEXT_DELIMITER_REQUIRED = "error.budget.requestImport.missingTextFieldDelimiter";
    public static final String ERROR_DISTINCT_DELIMITERS_REQUIRED = "error.budget.requestImport.nonDistinctDelimiters";
    public static final String ERROR_BUDGET_YEAR_REQUIRED = "error.budget.requestImport.missingBudgetYear";
    public static final String MSG_UNLOCK_CONFIRMATION = "message.budget.lock.unlockConfirmation";
    public static final String MSG_LOCK_NOTEXIST = "message.budget.lock.lockNotExist";
    public static final String MSG_UNLOCK_SUCCESSFUL = "message.budget.lock.unlockSuccessful";
    public static final String MSG_UNLOCK_NOTSUCCESSFUL = "message.budget.lock.unlockNotSuccessful";
    public static final String MSG_LOCK_POSITIONKEY = "message.budget.lock.positionKey";
    public static final String MSG_LOCK_POSITIONFUNDINGKEY = "message.budget.lock.positionFundingKey";
    public static final String MSG_LOCK_ACCOUNTKEY = "message.budget.lock.accountKey";
    public static final String ERROR_LOCK_INVALID_USER = "error.budget.lock.invalidUser";
}

