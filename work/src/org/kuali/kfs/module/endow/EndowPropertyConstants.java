/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow;

import java.sql.Date;

import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

public class EndowPropertyConstants {

    // Endowment common fields
    public static final String KUALICODEBASE_CODE = "code";
    public static final String KUALICODEBASE_NAME = "name";
    public static final String KUALICODEBASE_ACTIVE_INDICATOR = "active";
    public static final String ENDOWCODEBASE_CODE = "code";
    public static final String ENDOWCODEBASE_DESC = "description";

    // Security Reporting Group
    public static final String SECURITY_REPORTING_GROUP_ORDER = "securityReportingGrpOrder";
    public static final String SECURITY_REPORTING_GROUP_ACTIVE_INFICATOR = "active";

    // Endowment Transaction
    public static final String ENDOWMENT_TRANSACTION_GL_LINKS = "glLinks";

    // Class Code
    public static final String CLASS_CODE_SEC_REPORTING_GRP = "securityReportingGrp";
    public static final String CLASS_CODE_SEC_ACCRUAL_METHOD = "securityAccrualMethod";
    public static final String CLASS_CODE_SEC_END_TRANSACTION_CODE = "securityEndowmentTransactionCode";
    public static final String CLASS_CODE_SEC_INCOME_END_TRANSACTION_CODE = "securityIncomeEndowmentTransactionPostCode";
    public static final String CLASS_CODE_TAX_LOT_INDICATOR = "taxLotIndicator";
    public static final String CLASS_CODE_TYPE = "classCodeType";
    public static final String CLASS_CODE_VALUATION_METHOD = "valuationMethod";
    public static final String CLASS_CODE_ENDOWMENT_TRANSACTION = "endowmentTransactionCode";
    public static final String CLASS_CODE_INCOME_ENDOWMENT_TRANSACTION_POST = "incomeEndowmentTransactionPost";

    // Frequency Code
    public static final String FREQUENCY_TYPE = "frequencyType";
    public static final String FREQUENCY_WEEK_DAY = "frequencyWeekDay";
    public static final String FREQUENCY_MONTH = "frequencyMonth";
    public static final String FREQUENCY_DAY_IN_MONTH = "dayInMonth";
    public static final String FREQUENCY_MONTHLY_OCCURENCE = "monthlyOccurence";

    // Security
    public static final String SECURITY_ID = "id";
    public static final String SECURITY_USER_ENTERED_ID_PREFIX = "userEnteredSecurityIDprefix";
    public static final String SECURITY_DESCRIPTION = "description";
    public static final String SECURITY_TICKER_SYMBOL = "tickerSymbol";
    public static final String SECURITY_CLASS_CODE = "securityClassCode";
    public static final String SECURITY_SUBCLASS_CODE = "securitySubclassCode";
    public static final String SECURITY_MATURITY_DATE = "maturityDate";
    public static final String SECURITY_UNIT_VALUE = "unitValue";
    public static final String SECURITY_UNITS_HELD = "unitsHeld";
    public static final String SECURITY_VALUATION_DATE = "valuationDate";
    public static final String SECURITY_UNIT_VALUE_SOURCE = "unitValueSource";
    public static final String SECURITY_PREVIOUS_UNIT_VALUE = "previousUnitValue";
    public static final String SECURITY_PREVIOUS_UNIT_VALUE_DATE = "previousUnitValueDate";
    public static final String SECURITY_CARRY_VALUE = "carryValue";
    public static final String SECURITY_MARKET_VALUE = "marketValue";
    public static final String SECURITY_LAST_TRANSACTION_DATE = "lastTransactionDate";
    public static final String SECURITY_INCOME_PAY_FREQUENCY = "incomePayFrequency";
    public static final String SECURITY_INCOME_NEXT_PAY_DATE = "incomeNextPayDate";
    public static final String SECURITY_INCOME_RATE = "incomeRate";
    public static final String SECURITY_INCOME_CHANGE_DATE = "incomeChangeDate";
    public static final String SECURITY_ISSUE_DATE = "issueDate";
    public static final String SECURITY_DIVIDEND_RECORD_DATE = "dividendRecordDate";
    public static final String SECURITY_EX_DIVIDEND_DATE = "exDividendDate";
    public static final String SECURITY_DIVIDEND_PAY_DATE = "dividendPayDate";
    public static final String SECURITY_DIVIDEND_AMOUNT = "dividendAmount";
    public static final String SECURITY_COMMITMENT_AMOUNT = "commitmentAmount";
    public static final String SECURITY_FOREIGN_WITHHOLD_PERCENTAGE = "foreignWithholdPercentage";
    public static final String SECURITY_NEXT_FISCAL_YEAR_DISTRIBUTION_AMOUNT = "nextFiscalYearDistributionAmount";
    public static final String SECURITY_CURRENT_HOLDERS = "currentHolders";
    public static final String SECURITY_HOLDERS_IN_HISTORY = "holdersInHistory";
    public static final String SECURITY_CLASS_CODE_REF = "classCode";
    public static final String SECURITY_SUBCLASS_CODE_REF = "subclassCode";
    public static final String SECURITY_FREQUENCY_CODE_REF = "frequencyCode";

    public static final String REPORTING_GROUP_DESC = "classCode.reportingGroup.securityReportingGrpDesc";
    public static final String ACCRUAL_METHOD_DESC = "classCode.accrualMethod.accrualMethodDesc";
    public static final String TRANSACTION_CODE_DESC = "classCode.endowmentTransactionCode.codeAndDescription";
    public static final String INCOME_TRANSACTION_CODE_DESC = "classCode.incomeEndowmentTransactionPost.codeAndDescription";

    // HoldingTaxLot fields
    public static final String HOLDING_TAX_LOT_KEMID = "kemid";
    public static final String HOLDING_TAX_LOT_SECURITY_ID = "securityId";
    public static final String HOLDING_TAX_LOT_REGISTRATION_CODE = "registrationCode";
    public static final String HOLDING_TAX_LOT_NUMBER = "lotNumber";
    public static final String HOLDING_TAX_LOT_INCOME_PRINCIPAL_INDICATOR = "incomePrincipalIndicator";
    public static final String HOLDING_TAX_LOT_BALANCE_DATE = "balanceDate";

    // MonthEndDate fields
    public static final String MONTH_END_DATE_ID = "monthEndDateId";
    public static final String MONTH_END_DATE = "monthEndDate";

    // HoldingHistory fields
    public static final String HOLDING_HISTORY_MONTH_END_DATE_ID = "monthEndDateId";

    // KEMID
    public static final String KEMID = "kemid";
    public static final String KEMID_TYPE_CODE = "typeCode";
    public static final String KEMID_USER_ENTERED_MOD10 = "userEnteredKemidMod10";
    public static final String KEMID_DORMANT_IND = "dormantIndicator";
    public static final String KEMID_CLOSED_IND = "closedIndicator";
    public static final String KEMID_CLOSED_TO_KEMID = "closedToKEMID";
    public static final String KEMID_CLOSE_CODE = "closeCode";
    public static final String KEMID_DISPOSITION_OF_FUNDS = "dispositionOfFunds";
    public static final String KEMID_DATE_CLOSED = "dateClosed";
    public static final String KEMID_AGREEMENTS_TAB = "kemidAgreements";
    public static final String KEMID_SOURCE_OF_FUNDS_TAB = "kemidSourcesOfFunds";
    public static final String KEMID_BENEFITTING_ORGS_TAB = "kemidBenefittingOrganizations";
    public static final String KEMID_GENERAL_LEDGER_ACCOUNTS_TAB = "kemidGeneralLedgerAccounts";
    public static final String KEMID_PAY_INSTRUCTIONS_TAB = "kemidPayoutInstructions";
    public static final String KEMID_USE_CRITERIA_TAB = "kemidUseCriteria";
    public static final String KEMID_SPECIAL_INSTRUCTIONS_TAB = "kemidSpecialInstructions";
    public static final String KEMID_REPORT_GROUP_TAB = "kemidReportGroups";
    public static final String KEMID_DONOR_STATEMENTS_TAB = "kemidDonorStatements";
    public static final String KEMID_COMBINE_DONOR_STATEMENTS_TAB = "kemidCombineDonorStatements";
    public static final String KEMID_FEES_TAB = "kemidFees";

    public static final String KEMID_AGREEMENTS_SECTION = "agreements";
    public static final String KEMID_SOURCE_OF_FUNDS_SECTION = "sourcesOfFunds";
    public static final String KEMID_BENEFITTING_ORGS_SECTION = "benefittingOrganizations";
    public static final String KEMID_GENERAL_LEDGER_ACCOUNTS_SECTION = "generalLedgerAccounts";
    public static final String KEMID_PAY_INSTRUCTIONS_SECTION = "payoutInstructions";
    public static final String KEMID_USE_CRITERIA_SECTION = "useCriteria";
    public static final String KEMID_SPECIAL_INSTRUCTIONS_SECTION = "specialInstructions";
    public static final String KEMID_FEES_SECTION = "fees";
    public static final String KEMID_REPORT_GROUP_SECTION = "reportGroup";
    public static final String KEMID_DONOR_STATEMENTS_SECTION = "donorStatements";
    public static final String KEMID_COMBINE_DONOR_STATEMENTS_SECTION = "combineDonorStatements";

    // KemidAgreement
    public static final String KEMID_AGRMNT_ID = "agreementId";
    public static final String KEMID_AGRMNT_TYP_CD = "agreementTypeCode";
    public static final String KEMID_AGRMNT_TYPE = "agreementType";
    public static final String KEMID_AGRMNT_STAT_CD = "agreementStatusCode";
    public static final String KEMID_AGRMNT_STATUS = "agreementStatus";
    public static final String KEMID_AGRMNT_STAT_DT = "agreementStatusDate";
    public static final String KEMID_AGRMNT_OTHR_DOC = "otherAgreementDocumentation";
    public static final String KEMID_AGRMNT_DONR_INTENT = "donorIntentFromAgreement";
    public static final String KEMID_AGRMNT_COMM = "comments";
    public static final String KEMID_AGRMNT_ACTIVE_IND = "active";


    // KEMID Source Of Funds
    public static final String KEMID_FND_SRC_SEQ_NBR = "kemidFundSourceSequenceNumber";
    public static final String KEMID_FND_SRC_CD = "fundSourceCode";
    public static final String KEMID_FND_SRC = "fundSource";
    public static final String KEMID_FND_SRC_OPND_FROM_KEMID = "openedFromKemid";
    public static final String KEMID_FND_SRC_OPND_FROM_KEMID_OBJ_REF = "openedFromKemidObjRef";
    public static final String KEMID_FND_SRC_HIST = "fundHistory";
    public static final String KEMID_FND_SRC_ADDITIONAL_DATA = "additionalSourceData";
    public static final String KEMID_FND_SRC_ACTIVE_IND = "active";

    // KEMID Benefitting Org
    public static final String KEMID_BENE_ORG_SEQ_NBR = "benefittingOrgSeqNumber";
    public static final String KEMID_BENE_ORG_CD = "benefittingOrgCode";
    public static final String KEMID_BENE_ORG = "organization";
    public static final String KEMID_BENE_CHRT_CD = "benefittingChartCode";
    public static final String KEMID_BENE_CHRT = "chart";
    public static final String KEMID_BENE_ORG_PERCENT = "benefitPrecent";
    public static final String KEMID_BENE_ORG_START_DATE = "startDate";
    public static final String KEMID_BENE_ORG_LAST_CHG_DATE = "lastChangeDate";
    public static final String KEMID_BENE_ORG_ACTIVE_IND = "active";

    // KEMID General Ledger Account
    public static final String KEMID_GL_ACCOUNT_IP_INDICATOR_CD = "incomePrincipalIndicatorCode";
    public static final String KEMID_GL_ACCOUNT_CHART_CD = "chartCode";
    public static final String KEMID_GL_ACCOUNT_CHART = "chart";
    public static final String KEMID_GL_ACCOUNT_NBR = "accountNumber";
    public static final String KEMID_GL_ACCOUNT = "account";

    // KEMID Payout Instructions
    public static final String KEMID_PAY_INC_SEQ_NBR = "payoutIncomeSequenceNumber";
    public static final String KEMID_PAY_INC_TO_KEMID = "payIncomeToKemid";
    public static final String KEMID_PAY_INC_TO_KEMID_OBJ_REF = "payIncomeToKemidObjRef";
    public static final String KEMID_PAY_INC_PERCENT_OF_INC_TO_PAY_TO_KEMID = "percentOfIncomeToPayToKemid";
    public static final String KEMID_PAY_INC_AMOUNT = "incomeAmountToPayToKemid";
    public static final String KEMID_PAY_INC_START_DATE = "startDate";
    public static final String KEMID_PAY_INC_END_DATE = "endDate";

    // KEMID Use Criteria
    public static final String KEMID_USE_CRIT_SEQ = "useCriteriaSeq";
    public static final String KEMID_USE_CRIT_CD = "useCriteriaCode";
    public static final String KEMID_USE_CRIT = "useCriteria";
    public static final String KEMID_USE_CRIT_ADDITIONAL_INFO = "useCriteriaAdditionalInfo";
    public static final String KEMID_USE_CRIT_ACTIVE_IND = "active";

    // KEMID Special Instruction
    public static final String KEMID_SPEC_INSTR_SEQ = "instructionSeq";
    public static final String KEMID_SPEC_INSTR_CD = "agreementSpecialInstructionCode";
    public static final String KEMID_SPEC_INSTR = "agreementSpecialInstruction";
    public static final String KEMID_SPEC_INSTR_COMMENTS = "comments";
    public static final String KEMID_SPEC_INSTR_START_DATE = "instructionStartDate";
    public static final String KEMID_SPEC_INSTR_END_DATE = "instructionEndDate";

    // KEMID Fee Method
    public static final String KEMID_FEE_MTHD_CD = "feeMethodCode";
    public static final String KEMID_FEE_MTHD = "feeMethod";
    public static final String KEMID_FEE_SEQ = "feeMethodSeq";
    public static final String KEMID_FEE_CHARGE_FEE_TO_KEMID = "chargeFeeToKemid";
    public static final String KEMID_FEE_CHARGE_FEE_TO_KEMID_OBJ_REF = "chargeFeeToKemidObjRef";
    public static final String KEMID_FEE_PERCENT_OF_FEE_CHARGED_TO_INCOME = "percentOfFeeChargedToIncome";
    public static final String KEMID_FEE_PERCENT_OF_FEE_CHARGED_TO_PRINCIPAL = "percentOfFeeChargedToPrincipal";
    public static final String KEMID_FEE_ACCRUE_FEES = "accrueFees";
    public static final String KEMID_FEE_TOTAL_ACCRUED_FEES = "totalAccruedFees";
    public static final String KEMID_FEE_WAIVE_FEES = "waiveFees";
    public static final String KEMID_FEE_TOTAL_WAIVED_FEES_THIS_FISCAL_YEAR = "totalWaivedFeesThisFiscalYear";
    public static final String KEMID_FEE_TOTAL_WAIVED_FEES = "totalWaivedFees";
    public static final String KEMID_FEE_START_DATE = "feeStartDate";
    public static final String KEMID_FEE_END_DATE = "feeEndDate";

    // KEMID Report Group
    public static final String KEMID_REPORT_GRP_SEQ = "combineGroupSeq";
    public static final String KEMID_REPORT_GRP_CD = "combineGroupCode";
    public static final String KEMID_REPORT_GRP = "combineGroup";
    public static final String KEMID_REPORT_GRP_DATE_ADDED = "dateAdded";
    public static final String KEMID_REPORT_GRP_DATE_TERMINATED = "dateTerminated";

    // KEMID Donor Statement
    public static final String KEMID_DONOR_STATEMENT_ID = "donorId";
    public static final String KEMID_DONOR_STATEMENT_DONOR = "donor";
    public static final String KEMID_DONOR_STATEMENT_SEQ = "donorSeq";
    public static final String KEMID_DONOR_STATEMENT_CD = "donorStatementCode";
    public static final String KEMID_DONOR_STATEMENT = "donorStatement";
    public static final String KEMID_DONOR_STATEMENT_COMBINE_WITH_DONOR_ID = "combineWithDonorId";
    public static final String KEMID_DONOR_STATEMENT_COMBINE_WITH_DONOR = "combineWithDonor";
    public static final String KEMID_DONOR_STATEMENT_DONOR_LABEL = "donorLabel";
    public static final String KEMID_DONOR_STATEMENT_DONOR_LABEL_OBJ_REF = "donorLabelObjRef";
    public static final String KEMID_DONOR_STATEMENT_TERMINATION_REASON = "terminationReason";
    public static final String KEMID_DONOR_STATEMENT_TERMINATION_DATE = "terminationDate";

    // KEMID Combine Donor Statement
    public static final String KEMID_COMBINE_DONOR_STATEMENT_SEQ = "combineDonorSeq";
    public static final String KEMID_COMBINE_DONOR_STATEMENT_TERMINATION_DATE = "terminateCombineDate";
    public static final String KEMID_COMBINE_DONOR_STATEMENT_WITH_KEMID = "combineWithKemid";
    public static final String KEMID_COMBINE_DONOR_STATEMENT_WITH_KEMID_OBJ_REF = "combineWithKemidObjRef";

    // GLLink
    public static final String GL_LINK_ETRAN_CD = "endowmentTransactionCode";
    public static final String GL_LINK_CHART_CD = "chartCode";
    public static final String GL_LINK_OBJECT_CD = "object";
    public static final String GL_LINK_FINANCIAL_OBJECT_CODE = "financialObjectCode";

    // Poole Fund Control
    public static final String POOL_SECURITY_ID = "pooledSecurityID";
    public static final String DISTRIBUTE_GAINS_LOSSES_IND = "distributeGainsAndLossesIndicator";

    // PooledFundValue
    public static final String VALUE_EFFECTIVE_DATE = "valueEffectiveDate";
    public static final String VALUATION_DATE = "valuationDate";
    public static final String INCOME_DISTRIBUTION_PER_UNIT = "incomeDistributionPerUnit";
    public static final String LONG_TERM_GAIN_LOSS_DISTRIBUTION_PER_UNIT = "longTermGainLossDistributionPerUnit";
    public static final String SHORT_TERM_GAIN_LOSS_DISTRIBUTION_PER_UNIT = "shortTermGainLossDistributionPerUnit";
    public static final String DISTRIBUTE_INCOME_ON_DATE = "distributeIncomeOnDate";
    public static final String DISTRIBUTE_LONG_TERM_GAIN_LOSS_ON_DATE = "distributeLongTermGainLossOnDate";
    public static final String DISTRIBUTE_SHORT_TERM_GAIN_LOSS_ON_DATE = "distributeShortTermGainLossOnDate";


    // Cash Sweep Model
    public static final String CASH_SWEEP_MODEL_ID = "cashSweepModelID";

    // Automated Cash Investment Model
    public static final String ACI_MODEL_ID = "aciModelID";
    public static final String INVESTMENT_1_PERCENT = "investment1Percent";
    public static final String INVESTMENT_2_PERCENT = "investment2Percent";
    public static final String INVESTMENT_3_PERCENT = "investment3Percent";
    public static final String INVESTMENT_4_PERCENT = "investment4Percent";

    // PurposeCode
    public static final String PURPOSE_INCOME_CAE_CD = "incomeCAECode";
    public static final String PURPOSE_PRINCIPAL_CAE_CD = "principalCAECode";

    // KEMID CAE
    public static final String CAE_TYPE_CODE_ID = "caeTypeCode";

    // KEMID Donor
    public static final String DONR_ID = "donorID";

    // KEMID Type Code
    public static final String TYPE_CODE = "code";
    public static final String TYPE_INC_RESTR_CD = "incomeRestrictionCode";
    public static final String TYPE_PRINCIPAL_RESTR_CD = "incomeRestrictionCode";
    public static final String TYPE_INCOME_ACI_MODEL_ID = "incomeACIModelId";
    public static final String TYPE_PRINCIPAL_ACI_MODEL_ID = "principalACIModelId";

    // KEMID Type Fee Method
    public static final String FEE_METHOD = "feeMethod";
    public static final String FEE_BASE_CD = "feeBaseCode";

    // KEMID Fee Method
    public static final String FEE_METHOD_CODE = "feeMethodCode";
    public static final String FEE_METHOD_FREQUENCY_CODE = "feeFrequencyCode";

    // KEMID Fee Class Code
    public static final String FEE_CLASS_CODE = "classCode";

    // KEMID Fee Security
    public static final String FEE_SECURITY_CODE = "securityCode";
    public static final String FEE_SECURITY = "security"; // object

    // KEMID Fee Payment Type
    public static final String FEE_PAYMENT_TYPE_CODE = "paymentTypeCode";

    // KEMID Fee Transaction Type
    public static final String FEE_TRANSACTION_TYPE_CODE = "transactionTypeCode";

    // KEMID Fee Rate Definition Code
    public static final String FEE_RATE_DEFINITION_CODE = "feeRateDefinitionCode";

    // KEMID Fee Endowment Transaction Code
    public static final String FEE_ENDOWMENT_TRANSACTION_CODE = "endowmentTransactionCode";

    // KEMID Current Balance
    public static final String CURRENT_BAL_TOTAL_MARKET_VALUE = "totalMarketValue";
    public static final String CURRENT_BAL_PURPOSE_CD = "kemidObj.purposeCode";
    public static final String CURRENT_BAL_KEMID_BALANCE_DATE = "balanceDate";
    public static final String CURRENT_BAL_CLOSED_INDICATOR = "kemidObj.closedIndicator";
    public static final String CURRENT_BAL_KEMID_SHORT_TTL = "kemidObj.shortTitle";

    // KEMID Current Balance Detail
    public static final String KEMID_CRNT_BAL_DET_INC_AT_MARKET = "incomeAtMarket";
    public static final String KEMID_CRNT_BAL_DET_PRIN_AT_MARKET = "principalAtMarket";
    public static final String KEMID_CRNT_BAL_KEMID_SHORT_TTL = "kemidObj.shortTitle";
    public static final String KEMID_CRNT_BAL_PURPOSE_DESC = "kemidObj.purpose.name";

    // KEMID Current Reporting Group
    public static final String KEMID_CRNT_REP_GRP_SEC_ID = "securityId";
    public static final String KEMID_CRNT_REP_GRP_CD = "reportingGroupCode";
    public static final String KEMID_CRNT_REP_GRP_REGIS_CD = "registrationCode";
    public static final String KEMID_CRNT_REP_GRP_IP_IND = "ipIndicator";
    public static final String KEMID_CRNT_REP_GRP_UNITS = "units";
    public static final String KEMID_CRNT_REP_GRP_CARRY_VAL = "carryVal";
    public static final String KEMID_CRNT_REP_GRP_MVAL = "marketVal";
    public static final String KEMID_CRNT_REP_GRP_FY_EST_INC = "nextFYEstimatedIncome";
    public static final String KEMID_CRNT_REP_GRP_FY_REM_EST_INC = "remainderOfFYEstimatedIncome";
    public static final String KEMID_CRNT_REP_GRP_ANNL_INC_EST = "annualEstimatedIncome";
    public static final String KEMID_CRNT_REP_GRP_KEMID_SHORT_TTL = "kemidObj.shortTitle";
    public static final String KEMID_CRNT_REP_GRP_PURPOSE_DESC = "kemidObj.purpose.name";
    public static final String KEMID_CRNT_REP_GRP_DESC = "reportingGroup.name";
    public static final String KEMID_CRNT_REP_GRP_IP_IND_DESC = "incomePrincipalIndicator.name";

    // Current Tax Lot
    public static final String CURRENT_TAX_LOT_KEMID = "kemid";
    public static final String CURRENT_TAX_LOT_SECURITY_ID = "securityId";
    public static final String CURRENT_TAX_LOT_REGIS_CD = "registrationCode";
    public static final String CURRENT_TAX_LOT_LOT_NBR = "lotNumber";
    public static final String CURRENT_TAX_LOT_IP_IND = "incomePrincipalIndicator";
    public static final String CURRENT_TAX_LOT_IP_IND_DESC = "incomePrincipal.name";
    public static final String CURRENT_TAX_LOT_UNITS = "units";
    public static final String CURRENT_TAX_LOT_COST = "cost";
    public static final String CURRENT_TAX_LOT_HOLDING_MVAL = "holdingMarketValue";
    public static final String CURRENT_TAX_LOT_ACQUIRED_DATE = "acquiredDate";
    public static final String CURRENT_TAX_LOT_ANN_EST_INC = "annualEstimatedIncome";
    public static final String CURRENT_TAX_LOT_REMAINDER_OF_FY_EST_INC = "remainderOfFYEstimatedIncome";
    public static final String CURRENT_TAX_LOT_NEXT_FY_EST_INC = "nextFYEstimatedIncome";
    public static final String CURRENT_TAX_LOT_LAST_TRAN_DATE = "lastTransactionDate";
    public static final String CURRENT_TAX_LOT_CURRENT_ACCRUAL = "currentAccrual";
    public static final String CURRENT_TAX_LOT_PRIOR_ACCRUAL = "priorAccrual";
    public static final String CURRENT_TAX_LOT_FOREIGN_TAX_WITHHELD = "foreignTaxWithheld";
    public static final String CURRENT_TAX_LOT_KEMID_SHORT_TTL = "kemidObj.shortTitle";
    public static final String CURRENT_TAX_LOT_KEMID_PURPOSE_CD = "kemidObj.purposeCode";
    public static final String CURRENT_TAX_LOT_BALANCE_DATE = "balanceDate";
    public static final String CURRENT_TAX_LOT_KEMID_CLOSED_IND = "kemidObj.closedIndicator";
    public static final String CURRENT_TAX_LOT_REP_GRP = "security.classCode.securityReportingGrp";
    public static final String CURRENT_TAX_LOT_REGIS_DESC = "registration.name";
    public static final String CURRENT_TAX_LOT_SEC_DESC = "security.description";
    public static final String CURRENT_TAX_LOT_PURPOSE_DESC = "kemidObj.purpose.name";
    public static final String CURRENT_TAX_LOT_INC_PRIN_DESC = "incomePrincipal.name";

    // KEMIDHistoricalBalance
    public static final String KEMID_HIST_BAL_KEMID = "kemid";
    public static final String KEMID_HIST_BAL_CLOSED_IND = "closedIndicator";
    public static final String KEMID_HIST_BAL_DATE_ID = "historyBalanceDateId";
    public static final String KEMID_HIST_BAL_PURPOSE_CD = "purposeCode";
    public static final String KEMID_HIST_BAL_INC_AT_MARKET = "incomeAtMarket";
    public static final String KEMID_HIST_BAL_PRINC_AT_MARKET = "principalAtMarket";
    public static final String KEMID_HIST_BAL_TOTAL_MARKET_VAL = "totalMarketValue";
    public static final String KEMID_HIST_BAL_ANNUAL_EST_INC = "annualEstimatedIncome"; // next 12 months estimated income
    public static final String KEMID_HIST_BAL_REMAINDER_FY_EST_INC = "remainderFYEstimatedIncome";
    public static final String KEMID_HIST_BAL_NEXT_FT_EST_INC = "nextFYEstimatedIncome";

    // KEMIDHistoricalBalanceDetail
    public static final String KEMID_HIST_BAL_DET_KEMID = "kemid";
    public static final String KEMID_HIST_BAL_DET_PURPOSE_CODE = "kemidObj.purposeCode";
    public static final String KEMID_HIST_BAL_DET_DATE_ID = "historyBalanceDateId";
    public static final String KEMID_HIST_BAL_DET_INC_PRIN_IND = "incomePrincipalIndicator";
    public static final String KEMID_HIST_BAL_DET_RPT_GRP_CD = "reportingGroupCode";
    public static final String KEMID_HIST_BAL_DET_VAL_AT_MARKET = "valueAtMarket";
    public static final String KEMID_HIST_BAL_DET_ANNUAL_EST_INC = "annualEstimatedIncome"; // next 12 months estimated
    // income
    public static final String KEMID_HIST_BAL_DET_INC_AT_MARKET = "incomeAtMarket";
    public static final String KEMID_HIST_BAL_DET_PRINC_AT_MARKET = "principalAtMarket";
    public static final String KEMID_HIST_BAL_DET_REMAINDER_FY_EST_INC = "remainderOfFYEstimatedIncome";
    public static final String KEMID_HIST_BAL_DET_NEXT_FT_EST_INC = "nextFYEstimatedIncome";
    public static final String KEMID_HIST_BAL_DET_KEMID_PURPOSE_DESC = "kemidObj.purpose.name";
    public static final String KEMID_HIST_BAL_DET_KEMID_CLOSED_INDICATOR = "kemidObj.closedIndicator";
    public static final String KEMID_HIST_BAL_DET_KEMID_SHORT_TTL = "kemidObj.shortTitle";

    // KEMID Historical Reporting Group
    public static final String KEMID_HIST_REP_GRP_KEMID = "kemid";
    public static final String KEMID_HIST_REP_GRP_LOT_NBR = "lotNumber";
    public static final String KEMID_HIST_REP_GRP_ACQ_DATE = "acquiredDate";
    public static final String KEMID_HIST_REP_GRP_COST = "cost";
    public static final String KEMID_HIST_REP_GRP_CRNT_ACCR = "currentAccrual";
    public static final String KEMID_HIST_REP_GRP_PRIOR_ACCR = "priorAccrual";
    public static final String KEMID_HIST_REP_GRP_FOREIGN_TAX_WITHH = "foreignTaxWithheld";
    public static final String KEMID_HIST_REP_GRP_LAST_TRAN_DATA = "lastTransactionDate";
    public static final String KEMID_HIST_REP_GRP_SEC_ID = "securityId";
    public static final String KEMID_HIST_REP_GRP_CD = "reportingGroupCode";
    public static final String KEMID_HIST_REP_GRP_REGIS_CD = "registrationCode";
    public static final String KEMID_HIST_REP_GRP_IP_IND = "ipIndicator";
    public static final String KEMID_HIST_REP_GRP_UNITS = "units";
    public static final String KEMID_HIST_REP_GRP_CARRY_VAL = "carryVal";
    public static final String KEMID_HIST_REP_GRP_MVAL = "marketVal";
    public static final String KEMID_HIST_REP_GRP_FY_EST_INC = "nextFYEstimatedIncome";
    public static final String KEMID_HIST_REP_GRP_FY_REM_EST_INC = "remainderOfFYEstimatedIncome";
    public static final String KEMID_HIST_REP_GRP_ANNL_INC_EST = "annualEstimatedIncome";
    public static final String KEMID_HIST_REP_GRP_KEMID_SHORT_TTL = "kemidObj.shortTitle";
    public static final String KEMID_HIST_REP_GRP_PURPOSE_DESC = "kemidObj.purpose.name";
    public static final String KEMID_HIST_REP_GRP_DESC = "reportingGroup.name";
    public static final String KEMID_HIST_REP_GRP_IP_IND_DESC = "incomePrincipalIndicator.name";

    // KEMID fee method constants that are used in rule class
    public static final String FEE_CLASS_CODES_COLLECTION_NAME = "feeClassCodes";
    public static final String FEE_SECURITY_COLLECTION_NAME = "feeSecurity";
    public static final String FEE_TRANSACTION_TYPE_COLLECTION_NAME = "feeTransactions";
    public static final String FEE_ENDOWMENT_TRANSACTION_CODE_COLLECTION_NAME = "feeEndowmentTransactionCodes";
    public static final String FEE_PAYMENT_TYPE_COLLECTION_NAME = "feePaymentTypes";
    public static final String FEE_CLASS_CODE_REF = "classCode";
    public static final String FEE_SECURITY_REF = "security";
    public static final String FEE_TRANSACTION_TYPE_REF = "transactionType";
    public static final String FEE_ENDOWMENT_TRANSACTION_CODE_REF = "endowmentTransaction";
    public static final String FEE_CLASS_CODE_ATTRIBUTE = "feeClassCode";
    public static final String FEE_SECURITY_CODE_ATTRIBUTE = "securityCode";
    public static final String FEE_TRANSACTION_TYPE_CODE_ATTRIBUTE = "transactionTypeCode";
    public static final String FEE_ENDOWMENT_TRANSACTION_TYPE_CODE_ATTRIBUTE = "endowmentTransactionCode";
    public static final String FEE_TYPE_CODE = "feeTypeCode";
    public static final String FEE_BASE_CODE = "feeBaseCode";
    public static final String FEE_BY_TRANSACTION_TYPE_CODE = "feeByTransactionType";
    public static final String FEE_BY_ENDOWMENT_TRANSACTION_TYPE_CODE = "feeByETranCode";
    public static final String FEE_BY_CLASS_CODE = "feeByClassCode";
    public static final String FEE_BY_SECURITY_CODE = "feeBySecurityCode";
    public static final String FEE_BALANCE_TYPES_CODE = "feeBalanceTypeCode";
    public static final String CORPUS_TO_PCT_TOLERANCE = "corpusPctTolerance";
    public static final String FIRST_FEE_BREAK_POINT = "firstFeeBreakpoint";
    public static final String SECOND_FEE_BREAK_POINT = "secondFeeBreakpoint";
    public static final String THIRD_FEE_RATE = "thirdFeeRate";
    public static final String FIRST_FEE_RATE = "firstFeeRate";
    public static final String SECOND_FEE_RATE = "secondFeeRate";

    // Tickler Constants
    public static final String TICKLER_NUMBER = "number";
    public static final String TICKLER_CODE = "typeCode";
    public static final String TICKLER_FREQUENCY = "frequencyCode";
    public static final String TICKLER_NEXT_DUE_DATE = "nextDueDate";
    public static final String TICKLER_DETAIL = "detail";
    public static final String TICKLER_ENTRY_DETAIL = "entryDate";
    public static final String TICKLER_TERMINATION_DATE = "terminationDate";
    public static final String TICKLER_ACTIVE = "active";
    public static final String TICKLER_OBJECT_ID = "objectId";
    public static final String TICKLER_VERSION_NUMBER = "versionNumber";
    public static final String TICKLER_SECURITIES = "securities";
    public static final String TICKLER_TYPE_CODE = "code";
    public static final String TICKLER_KEMID = "kemId";
    public static final String TICKLER_SECURITYID = "securityId";
    public static final String TICKLER_RECIPIENT_PRINCIPALID = "principalId";
    public static final String TICKLER_LOOKUP_USER_ID_FIELD = "recipientPrincipals.contact.principalName" ; 
    public static final String TICKLER_LOOKUP_UNIVERSAL_USER_ID_FIELD = "recipientPrincipals.principalId";
    public static final String TICKLER_RECIPIENT_GROUPID = "groupId";
    public static final String TICKLER_LOOKUP_GROUP_NAME_FIELD = "groupLookup.groupName" ; 
    public static final String TICKLER_LOOKUP_GROUP_USER_ID_FIELD = "recipientGroups.groupId";

}
