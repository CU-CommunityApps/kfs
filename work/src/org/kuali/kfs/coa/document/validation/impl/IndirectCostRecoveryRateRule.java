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
package org.kuali.kfs.coa.document.validation.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRate;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Options;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.RiceKeyConstants;


public class IndirectCostRecoveryRateRule extends MaintenanceDocumentRuleBase {

    protected static final String MAINTAINABLE_DETAIL_ERROR_PATH = KNSConstants.MAINTENANCE_NEW_MAINTAINABLE + "indirectCostRecoveryRateDetails";
    protected static final String MAINTAINABLE_DETAIL_ADDLINE_ERROR_PATH = "add.indirectCostRecoveryRateDetails";
    
    private IndirectCostRecoveryRate indirectCostRecoveryRate;
    private IndirectCostRecoveryRateDetail indirectCostRecoveryRateDetail;
    private List<IndirectCostRecoveryRateDetail> indirectCostRecoveryRateDetails;
    
    public void setupConvenienceObjects() {
        indirectCostRecoveryRate = (IndirectCostRecoveryRate) super.getNewBo();
        indirectCostRecoveryRateDetails = indirectCostRecoveryRate.getIndirectCostRecoveryRateDetails();
    }
    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;

        BigDecimal awardIndrCostRcvyRatePctCredits = new BigDecimal(0);
        BigDecimal awardIndrCostRcvyRatePctDebits = new BigDecimal(0);
        
        if (!processYear()) {
            success = false;
        }
        else {
            for(int i = 0;i<indirectCostRecoveryRateDetails.size();i++) {
                if(indirectCostRecoveryRateDetails.get(i).isActive()) {
                    GlobalVariables.getErrorMap().addToErrorPath(MAINTAINABLE_DETAIL_ERROR_PATH + "[" + i + "]");
                    success &= processCollectionLine(indirectCostRecoveryRateDetails.get(i));
                    GlobalVariables.getErrorMap().removeFromErrorPath(MAINTAINABLE_DETAIL_ERROR_PATH + "[" + i + "]");
                    
                    if(indirectCostRecoveryRateDetails.get(i).isActive()) {
                        if(KFSConstants.GL_CREDIT_CODE.equals(indirectCostRecoveryRateDetails.get(i).getTransactionDebitIndicator())) {
                            awardIndrCostRcvyRatePctCredits = awardIndrCostRcvyRatePctCredits.add(indirectCostRecoveryRateDetails.get(i).getAwardIndrCostRcvyRatePct());
                        }
                        if(KFSConstants.GL_DEBIT_CODE.equals(indirectCostRecoveryRateDetails.get(i).getTransactionDebitIndicator())) {
                            awardIndrCostRcvyRatePctDebits = awardIndrCostRcvyRatePctDebits.add(indirectCostRecoveryRateDetails.get(i).getAwardIndrCostRcvyRatePct());
                        }                    
                    }
                }
            }
        }
        success &= checkCreditsAndDebits(awardIndrCostRcvyRatePctCredits, awardIndrCostRcvyRatePctDebits);
        
        return success;
    }
    
    public boolean checkCreditsAndDebits(BigDecimal credits, BigDecimal debits) {
        boolean success = true;

        // global errors, in KeyConstants or KFSconstants or something, use one for the top of the page (mark doc once)
        // include the key word active (informing that only active records are considered)
        if(!(credits.compareTo(debits) == 0)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KNSConstants.GLOBAL_ERRORS, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_PERCENTS_NOT_EQUAL,
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(IndirectCostRecoveryRateDetail.class, KFSPropertyConstants.AWARD_INDR_COST_RCVY_RATE_PCT));
            success = false;
        }

        return success;
    }
 
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = true;
        IndirectCostRecoveryRateDetail item = (IndirectCostRecoveryRateDetail) line;
        success &= processCollectionLine(item);
        return success;
    }
    
    public boolean processCollectionLine(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        
        success &= validateWildcards(item);
        if(success) {
            success &= checkExistence(item) && checkRateFormat(item);
        }
                
        return success;
    }
    
    public boolean checkExistence(IndirectCostRecoveryRateDetail item) {
        boolean success = 
            processYear() && 
            processChart(item) && 
            processAccount(item) && 
            processSubAccount(item) && 
            processObjectCode(item) && 
            processSubObjectCode(item);
        return success;
    }
    
    public boolean processYear() {
        boolean success = true;
        Map pkMap = new HashMap();
        Integer year = indirectCostRecoveryRate.getUniversityFiscalYear();
        pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        if(!checkExistenceFromTable(Options.class, pkMap)) {
            GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KNSConstants.MAINTENANCE_NEW_MAINTAINABLE + KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,
                    RiceKeyConstants.ERROR_EXISTENCE, 
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(IndirectCostRecoveryRate.class, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR));
            success = false;
        }
        return success;
    }
    
    protected boolean processChart(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        if(StringUtils.isNotBlank(chart)) {
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);
                if(!checkExistenceFromTable(Chart.class, pkMap)) {
                    logErrorUtility(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, RiceKeyConstants.ERROR_EXISTENCE);
                    success = false;
                }
            }
        }
        return success;
    }
    
    protected boolean processAccount(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        if(StringUtils.isNotBlank(acct)) {
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!propertyIsWildcard(acct)) {
                pkMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, acct);
                if(!checkExistenceFromTable(Account.class, pkMap)) {
                    logErrorUtility(KFSPropertyConstants.ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE);
                    success = false;
                }
            }
        }
        return success;
    }
    
    protected boolean processSubAccount(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        if(StringUtils.isNotBlank(subAcct) && !StringUtils.containsOnly(subAcct, "-")) { // if doesn't contain only dashes
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!propertyIsWildcard(acct)) {
                pkMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, acct);    
            }
            if(!propertyIsWildcard(subAcct) && StringUtils.isNotBlank(subAcct) && !StringUtils.containsOnly(subAcct, "-")) {
                pkMap.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAcct);
                if(!checkExistenceFromTable(SubAccount.class, pkMap)) {
                    logErrorUtility(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, RiceKeyConstants.ERROR_EXISTENCE);
                    success = false;
                }
            }
        }
        return success;
    }
    
    protected boolean processObjectCode(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        Map pkMap = new HashMap();
        Integer year = indirectCostRecoveryRate.getUniversityFiscalYear();
        String chart = item.getChartOfAccountsCode();
        String objCd = item.getFinancialObjectCode();
        pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        if(StringUtils.isNotBlank(objCd)) {
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!propertyIsWildcard(objCd)) {
                pkMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objCd);
                if(!checkExistenceFromTable(ObjectCode.class, pkMap)) {
                    logErrorUtility(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE);
                    success = false;
                }
            }
        }
        return success;
    }
    
    protected boolean processSubObjectCode(IndirectCostRecoveryRateDetail item) { // chart being a wildcard implies account number being a wildcard, redundant checking?
        boolean success = true;
        Map pkMap = new HashMap();
        Integer year = indirectCostRecoveryRate.getUniversityFiscalYear();
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        if(StringUtils.isNotBlank(subObjCd) && !propertyIsWildcard(subObjCd) && !StringUtils.containsOnly(subObjCd, "-")) {
            pkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
            if(!propertyIsWildcard(chart)) {
                pkMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chart);    
            }
            if(!propertyIsWildcard(acct)) {
                pkMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, acct);    
            }
            if(!propertyIsWildcard(objCd)) {
                pkMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objCd);
            }
            pkMap.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjCd);
            if(!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(subObjCd) && !checkExistenceFromTable(SubObjCd.class, pkMap)) {
                logErrorUtility(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE);
                success = false;
            }
        }
        return success;
    }
    
    public boolean checkExistenceFromTable(Class clazz, Map fieldValues) {
        return getBoService().countMatching(clazz, fieldValues) != 0;
    }
    
    public boolean validateWildcards(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        if(!itemUsesWildcard(item) || (itemUsesWildcard(item) && itemPassesWildcardRules(item))) {
            success = true;
        }
        return success;
    }
    
    public boolean itemPassesWildcardRules(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        String[] wildcards = {GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY, GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT};
        
        success = !itemUsesWildcard(item) || checkWildcardRules(item);
        
        return success;
    }
    
    public boolean itemUsesWildcard(IndirectCostRecoveryRateDetail item) {
        boolean success = false;
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        String[] fields = {chart,acct,subAcct,objCd,subObjCd};
        
        for(int i=0;i<fields.length;i++) {
            success |= propertyIsWildcard(fields[i]);
        }
        
        return success;
    }
    
    public boolean propertyIsWildcard(String property) {
        return GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(property) || GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(property);
    }

    protected boolean checkAccountNumberWildcardRules(IndirectCostRecoveryRateDetail item) {
        String accountNumber = item.getAccountNumber();
        boolean success = true;
        if (!accountNumber.equals(item.getChartOfAccountsCode())) {
            GlobalVariables.getErrorMap().putError(
                    KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER),
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)
                    );
            success = false;
        }
        if (!accountNumber.equals(item.getSubAccountNumber())) {
            GlobalVariables.getErrorMap().putError(
                    KFSPropertyConstants.SUB_ACCOUNT_NUMBER,
                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER),
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER)
                    );
            success = false;
        }
        if (!accountNumber.equals(item.getFinancialSubObjectCode())) {
            GlobalVariables.getErrorMap().putError(
                    KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER),
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)
                    );
            success = false;
        }
        return success;
    }
    
    protected boolean checkAccountNumberNotWildcardRules(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        if (propertyIsWildcard(item.getSubAccountNumber())) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_CANNOT_BE_WILDCARD,
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER),
                    SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER));
            success = false;
        }
        return success;
    }

    protected boolean checkObjectCodeWildcardRules(IndirectCostRecoveryRateDetail item) {
        String financialObjectCode = item.getFinancialObjectCode();
        boolean success = true;
        if (propertyIsWildcard(financialObjectCode)) {
            if (!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(financialObjectCode)) {
                GlobalVariables.getErrorMap().putError(
                        KFSPropertyConstants.FINANCIAL_OBJECT_CODE,
                        KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARD_NOT_VALID,
                        GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT,
                        SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE),
                        GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY
                        );
                success = false;
            }
            else {
                if (!GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(item.getChartOfAccountsCode())) {
                    GlobalVariables.getErrorMap().putError(
                            KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,
                            KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE),
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE)
                            );
                    success = false;
                }
                // if object code is a wildcard and the account is not the same wildcard (i.e. the account "IS" a specific value) then the sub object code must be dashes. 
                if (!financialObjectCode.equals(item.getAccountNumber())) {
                    if (!KFSConstants.getDashFinancialSubObjectCode().equals(item.getFinancialSubObjectCode())) {
                        GlobalVariables.getErrorMap().putError(
                                KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE,
                                KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_WILDCARDS_MUST_MATCH_OBJCD_SUBACCT,
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE),
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER),
                                SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE)
                                );
                        success = false;
                    }
                }
            }
        }
        return success;
    }
    
    public boolean checkWildcardRules(IndirectCostRecoveryRateDetail item) {
        boolean success = checkAtMostOneWildcardUsed(item);
        if (success) {
            if (propertyIsWildcard(item.getFinancialObjectCode())) {
                success &= checkObjectCodeWildcardRules(item);
            }
            else {
            }
            if (propertyIsWildcard(item.getAccountNumber())) {
                success &= checkAccountNumberWildcardRules(item);
            }
            else {
                success &= checkAccountNumberNotWildcardRules(item);
            }
            
            if (!(propertyIsWildcard(item.getFinancialObjectCode()) || propertyIsWildcard(item.getAccountNumber()))) {
                // chart code can't be the only wildcard on the item
                if (success && propertyIsWildcard(item.getChartOfAccountsCode())) {
                    success = false;
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_CHART_CODE_NOT_ONLY_WILDCARD,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                }
            }
            
            if (success) {
                success = checkFinancialSubObjectCode(item);
            }
        }
        return success;
    }

    protected boolean checkAtMostOneWildcardUsed(IndirectCostRecoveryRateDetail item) {
        String chart = item.getChartOfAccountsCode();
        String acct = item.getAccountNumber();
        String subAcct = item.getSubAccountNumber();
        String objCd = item.getFinancialObjectCode();
        String subObjCd = item.getFinancialSubObjectCode();
        
        boolean success = true;
        String errorPropertyName = null;
        
        Set<String> wildcards = new HashSet<String>();
        if (propertyIsWildcard(chart)) {
            wildcards.add(chart);
            errorPropertyName = KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
        }
        if (success && propertyIsWildcard(acct)) {
            wildcards.add(acct);
            if (wildcards.size() > 1) {
                success = false;
                errorPropertyName = KFSPropertyConstants.ACCOUNT_NUMBER;
            }
        }
        if (success && propertyIsWildcard(subAcct)) {
            wildcards.add(subAcct);
            if (wildcards.size() > 1) {
                success = false;
                errorPropertyName = KFSPropertyConstants.SUB_ACCOUNT_NUMBER;
            }
        }
        if (success && propertyIsWildcard(objCd)) {
            wildcards.add(objCd);
            if (wildcards.size() > 1) {
                success = false;
                errorPropertyName = KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
            }
        }
        if (success && propertyIsWildcard(subObjCd)) {
            wildcards.add(subObjCd);
            if (wildcards.size() > 1) {
                success = false;
                errorPropertyName = KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE;
            }
        }
        
        if (!success) {
            GlobalVariables.getErrorMap().putError(
                    errorPropertyName,
                    KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_MULTIPLE_WILDCARDS_ON_ITEM);
        }
        return success;
    }
    
    protected boolean checkFinancialSubObjectCode(IndirectCostRecoveryRateDetail item) {
        String financialSubObjectCode = item.getFinancialSubObjectCode();
        boolean success = true;
        if (StringUtils.isNotBlank(financialSubObjectCode)) {
            // The only time a sub object code can not be dashes or a wildcard is if the chart, account, object code, and sub object code are all non wild cards (specific values).
            if (!StringUtils.containsOnly(financialSubObjectCode, KFSConstants.DASH) && !propertyIsWildcard(financialSubObjectCode)) {
                if (itemUsesWildcard(item)) {
                    success = false;
                    GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_SUB_OBJ_ACTUAL_VALUE_WITH_WILDCARDS,
                            SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(SubObjCd.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE));
                }
            }
        }
        return success;
    }
    public boolean checkRateFormat(IndirectCostRecoveryRateDetail item) {
        boolean success = true;
        BigDecimal zero = new BigDecimal(0.00);
        if(!(item.getAwardIndrCostRcvyRatePct() == null)) {
            if(item.getAwardIndrCostRcvyRatePct().scale() > 3) {
                logErrorUtility(KFSPropertyConstants.AWARD_INDR_COST_RCVY_RATE_PCT, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_PERCENT_INVALID_FORMAT_SCALE);
                success = false;
            }
            if(item.getAwardIndrCostRcvyRatePct().compareTo(zero) <= 0) {
                logErrorUtility(KFSPropertyConstants.AWARD_INDR_COST_RCVY_RATE_PCT, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_RATE_PERCENT_INVALID_FORMAT_ZERO);
                success = false;
            }            
        } else {
            
        }
        return success;
    }
    
    public void logErrorUtility(String propertyName, String errorKey) {
        GlobalVariables.getErrorMap().putError(propertyName, errorKey, SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(IndirectCostRecoveryRateDetail.class, propertyName));
    }
    
}
