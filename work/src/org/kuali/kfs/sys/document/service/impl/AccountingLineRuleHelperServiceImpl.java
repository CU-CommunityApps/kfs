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
package org.kuali.kfs.sys.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.ObjSubTyp;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService;
import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class AccountingLineRuleHelperServiceImpl implements AccountingLineRuleHelperService {
    private static Logger LOG = Logger.getLogger(AccountingLineRuleHelperServiceImpl.class);
    private DataDictionaryService dataDictionaryService;

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getAccountLabel()
     */
    public String getAccountLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Account.class.getName()).getAttributeDefinition(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getChartLabel()
     */
    public String getChartLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Chart.class.getName()).getAttributeDefinition(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getFundGroupCodeLabel()
     */
    public String getFundGroupCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(FundGroup.class.getName()).getAttributeDefinition(KFSPropertyConstants.CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getObjectCodeLabel()
     */
    public String getObjectCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ObjectCode.class.getName()).getAttributeDefinition(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getObjectSubTypeCodeLabel()
     */
    public String getObjectSubTypeCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ObjSubTyp.class.getName()).getAttributeDefinition(KFSPropertyConstants.CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getObjectTypeCodeLabel()
     */
    public String getObjectTypeCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ObjectType.class.getName()).getAttributeDefinition(KFSConstants.GENERIC_CODE_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getOrganizationCodeLabel()
     */
    public String getOrganizationCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Org.class.getName()).getAttributeDefinition(KFSPropertyConstants.ORGANIZATION_CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getProjectCodeLabel()
     */
    public String getProjectCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(ProjectCode.class.getName()).getAttributeDefinition(KFSPropertyConstants.CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getSubAccountLabel()
     */
    public String getSubAccountLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccount.class.getName()).getAttributeDefinition(KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getSubFundGroupCodeLabel()
     */
    public String getSubFundGroupCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubFundGroup.class.getName()).getAttributeDefinition(KFSPropertyConstants.SUB_FUND_GROUP_CODE).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#getSubObjectCodeLabel()
     */
    public String getSubObjectCodeLabel() {
        return dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubObjCd.class.getName()).getAttributeDefinition(KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME).getShortLabel();
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#hasRequiredOverrides(org.kuali.kfs.sys.businessobject.AccountingLine, java.lang.String)
     */
    public boolean hasRequiredOverrides(AccountingLine line, String overrideCode) {
        boolean retVal = true;
        AccountingLineOverride override = AccountingLineOverride.valueOf(overrideCode);
        Account account = line.getAccount();
        if (AccountingLineOverride.needsExpiredAccountOverride(account) && !override.hasComponent(AccountingLineOverride.COMPONENT.EXPIRED_ACCOUNT)) {
            Account continuation = getUnexpiredContinuationAccountOrNull(account);
            if (continuation == null) {
                GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED_NO_CONTINUATION, new String[] { account.getAccountNumber() });
            }
            else {
                GlobalVariables.getErrorMap().putError(KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_EXPIRED, new String[] { account.getAccountNumber(), continuation.getChartOfAccountsCode(), continuation.getAccountNumber() });
                // todo: ... args in JDK 1.5
            }
            retVal = false;
        }

        ObjectCode objectCode = line.getObjectCode();
        if (AccountingLineOverride.needsObjectBudgetOverride(account, objectCode) && !override.hasComponent(AccountingLineOverride.COMPONENT.NON_BUDGETED_OBJECT)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_PRESENCE_NON_BUDGETED_OBJECT_CODE, new String[] { account.getAccountNumber(), objectCode.getFinancialObjectCode() });
            retVal = false;
        }
        return retVal;
    }
    
    /**
     * @param account
     * @return an unexpired continuation account for the given account, or, if one cannot be found, null
     */
    private Account getUnexpiredContinuationAccountOrNull(Account account) {
        int count = 0;
        while (count++ < 10) { // prevents infinite loops
            String continuationChartCode = account.getContinuationFinChrtOfAcctCd();
            String continuationAccountNumber = account.getContinuationAccountNumber();
            // todo: does AccountService already handle blank keys this way?
            if (StringUtils.isBlank(continuationChartCode) || StringUtils.isBlank(continuationAccountNumber)) {
                return null;
            }
            account = SpringContext.getBean(AccountService.class).getByPrimaryId(continuationChartCode, continuationAccountNumber);
            if (ObjectUtils.isNull(account)) {
                return null;
            }
            if (account.isActive() && !account.isExpired()) {
                return account;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidAccount(org.kuali.kfs.coa.businessobject.Account, org.kuali.rice.kns.datadictionary.DataDictionary)
     */
    public boolean isValidAccount(Account account, DataDictionary dataDictionary) {
        return isValidAccount(account, dataDictionary, KFSConstants.ACCOUNT_NUMBER_PROPERTY_NAME);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidAccount(org.kuali.kfs.coa.businessobject.Account, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidAccount(Account account, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getAccountLabel();

        // make sure it exists
        if (ObjectUtils.isNull(account)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // make sure it's active for usage
        if (!account.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_CLOSED, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidChart(org.kuali.kfs.coa.businessobject.Chart, org.kuali.rice.kns.datadictionary.DataDictionary)
     */
    public boolean isValidChart(Chart chart, DataDictionary dataDictionary) {
        return isValidChart(chart, dataDictionary, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidChart(org.kuali.kfs.coa.businessobject.Chart, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidChart(Chart chart, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getChartLabel();

        // make sure it exists
        if (ObjectUtils.isNull(chart)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // make sure it's active for usage
        if (!chart.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode, org.kuali.rice.kns.datadictionary.DataDictionary)
     */
    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary) {
        return isValidObjectCode(objectCode, dataDictionary, KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectCode(org.kuali.kfs.coa.businessobject.ObjectCode, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidObjectCode(ObjectCode objectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // check active status
        if (!objectCode.isFinancialObjectActiveCode()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectTypeCode(org.kuali.kfs.coa.businessobject.ObjectType, org.kuali.rice.kns.datadictionary.DataDictionary)
     */
    public boolean isValidObjectTypeCode(ObjectType objectTypeCode, DataDictionary dataDictionary) {
        return isValidObjectTypeCode(objectTypeCode, dataDictionary, KFSConstants.OBJECT_TYPE_CODE_PROPERTY_NAME);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidObjectTypeCode(org.kuali.kfs.coa.businessobject.ObjectType, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidObjectTypeCode(ObjectType objectTypeCode, DataDictionary dataDictionary, String errorPropertyName) {
        // note that the errorPropertyName does not match the actual attribute name
        String label = getObjectTypeCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(objectTypeCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // check activity
        if (!objectTypeCode.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidProjectCode(org.kuali.kfs.coa.businessobject.ProjectCode, org.kuali.rice.kns.datadictionary.DataDictionary)
     */
    public boolean isValidProjectCode(ProjectCode projectCode, DataDictionary dataDictionary) {
        return isValidProjectCode(projectCode, dataDictionary, KFSConstants.PROJECT_CODE_PROPERTY_NAME);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidProjectCode(org.kuali.kfs.coa.businessobject.ProjectCode, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidProjectCode(ProjectCode projectCode, DataDictionary dataDictionary, String errorPropertyName) {
        // note that the errorPropertyName does not match the actual attribute name
        String label = getProjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(projectCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // check activity
        if (!projectCode.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubAccount(org.kuali.kfs.coa.businessobject.SubAccount, org.kuali.rice.kns.datadictionary.DataDictionary)
     */
    public boolean isValidSubAccount(SubAccount subAccount, DataDictionary dataDictionary) {
        return isValidSubAccount(subAccount, dataDictionary, KFSConstants.SUB_ACCOUNT_NUMBER_PROPERTY_NAME);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubAccount(org.kuali.kfs.coa.businessobject.SubAccount, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidSubAccount(SubAccount subAccount, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getSubAccountLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subAccount)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // check to make sure it is active
        if (!subAccount.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_DOCUMENT_SUB_ACCOUNT_INACTIVE, label);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubObjectCode(org.kuali.kfs.coa.businessobject.SubObjCd, org.kuali.rice.kns.datadictionary.DataDictionary)
     */
    public boolean isValidSubObjectCode(SubObjCd subObjectCode, DataDictionary dataDictionary) {
        return isValidSubObjectCode(subObjectCode, dataDictionary, KFSConstants.FINANCIAL_SUB_OBJECT_CODE_PROPERTY_NAME);
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#isValidSubObjectCode(org.kuali.kfs.coa.businessobject.SubObjCd, org.kuali.rice.kns.datadictionary.DataDictionary, java.lang.String)
     */
    public boolean isValidSubObjectCode(SubObjCd subObjectCode, DataDictionary dataDictionary, String errorPropertyName) {
        String label = getSubObjectCodeLabel();

        // make sure it exists
        if (ObjectUtils.isNull(subObjectCode)) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // check active flag
        if (!subObjectCode.isActive()) {
            GlobalVariables.getErrorMap().putError(errorPropertyName, KFSKeyConstants.ERROR_INACTIVE, label);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRuleHelperService#validateAccountingLine(org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    public boolean validateAccountingLine(AccountingLine accountingLine) {
        if (accountingLine == null) {
            throw new IllegalStateException(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSKeyConstants.ERROR_DOCUMENT_NULL_ACCOUNTING_LINE));
        }

        // grab the two service instances that will be needed by all the validate methods
        DataDictionary dd = dataDictionaryService.getDataDictionary();

        BusinessObjectEntry accountingLineEntry = dd.getBusinessObjectEntry(SourceAccountingLine.class.getName());

        // retrieve accounting line objects to validate
        accountingLine.refreshReferenceObject("chart");
        Chart chart = accountingLine.getChart();
        accountingLine.refreshReferenceObject("account");
        Account account = accountingLine.getAccount();
        accountingLine.refreshReferenceObject("objectCode");
        ObjectCode objectCode = accountingLine.getObjectCode();

        boolean valid = true;
        valid &= isValidChart(chart, dd);
        valid &= isValidAccount(account, dd);
        // sub account is not required
        if (StringUtils.isNotBlank(accountingLine.getSubAccountNumber())) {
            accountingLine.refreshReferenceObject("subAccount");
            SubAccount subAccount = accountingLine.getSubAccount();
            valid &= isValidSubAccount(subAccount, dd);
        }
        valid &= isValidObjectCode(objectCode, dd);
        // sub object is not required
        if (StringUtils.isNotBlank(accountingLine.getFinancialSubObjectCode())) {
            accountingLine.refreshReferenceObject("subObjectCode");
            SubObjCd subObjectCode = accountingLine.getSubObjectCode();
            valid &= isValidSubObjectCode(subObjectCode, dd);
        }
        // project code is not required
        if (StringUtils.isNotBlank(accountingLine.getProjectCode())) {
            accountingLine.refreshReferenceObject("project");
            ProjectCode projectCode = accountingLine.getProject();
            valid &= isValidProjectCode(projectCode, dd);
        }
        if (StringUtils.isNotBlank(accountingLine.getReferenceOriginCode())) {
            accountingLine.refreshReferenceObject("referenceOrigin");
            OriginationCode referenceOrigin = accountingLine.getReferenceOrigin();
            valid &= isValidReferenceOriginCode(referenceOrigin, accountingLineEntry);
        }
        if (StringUtils.isNotBlank(accountingLine.getReferenceTypeCode())) {
            accountingLine.refreshReferenceObject("referenceType");
            DocumentType referenceType = accountingLine.getReferenceType();
            valid &= isValidReferenceTypeCode(referenceType, accountingLineEntry);
        }
        valid &= hasRequiredOverrides(accountingLine, accountingLine.getOverrideCode());
        return valid;
    }
    
    /**
     * This method will check the reference origin code for existence in the system and whether it can actively be used.
     * 
     * @param referenceOriginCode
     * @param accountingLineEntry
     * @return boolean True if the object is valid; false otherwise.
     */
    private static boolean isValidReferenceOriginCode(OriginationCode referenceOriginCode, BusinessObjectEntry accountingLineEntry) {
        return checkExistence(referenceOriginCode, accountingLineEntry, KFSPropertyConstants.REFERENCE_ORIGIN_CODE, KFSPropertyConstants.REFERENCE_ORIGIN_CODE);
    }
    
    /**
     * This method will check the reference type code for existence in the system and whether it can actively be used.
     * 
     * @param referenceType
     * @param accountingLineEntry
     * @return boolean True if the object is valid; false otherwise.
     */
    private static boolean isValidReferenceTypeCode(DocumentType referenceType, BusinessObjectEntry accountingLineEntry) {
        return checkExistence(referenceType, accountingLineEntry, KFSPropertyConstants.REFERENCE_TYPE_CODE, KFSPropertyConstants.REFERENCE_TYPE_CODE);
    }
    
    /**
     * Checks for the existence of the given Object. This is doing an OJB-proxy-smart check, so assuming the given Object is not in
     * need of a refresh(), this method adds an ERROR_EXISTENCE to the global error map if the given Object is not in the database.
     * 
     * @param toCheck the Object to check for existence
     * @param accountingLineEntry to get the property's label for the error message parameter.
     * @param attributeName the name of the SourceAccountingLine attribute in the DataDictionary accountingLineEntry
     * @param propertyName the name of the property within the global error path.
     * @return whether the given Object exists or not
     */
    private static boolean checkExistence(Object toCheck, BusinessObjectEntry accountingLineEntry, String attributeName, String propertyName) {
        String label = accountingLineEntry.getAttributeDefinition(attributeName).getShortLabel();
        if (ObjectUtils.isNull(toCheck)) {
            GlobalVariables.getErrorMap().putError(propertyName, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        return true;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
