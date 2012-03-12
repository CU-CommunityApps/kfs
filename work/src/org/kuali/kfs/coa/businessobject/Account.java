/*
 * Copyright 2005 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.coa.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAccountAwardInformation;
import org.kuali.kfs.coa.businessobject.CFDA;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.ld.LaborBenefitRateCategory;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.location.api.campus.CampusService;
import org.kuali.rice.location.api.postalcode.PostalCodeService;
import org.kuali.rice.location.api.state.StateService;
import org.kuali.rice.location.framework.campus.CampusEbo;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

/**
 * 
 */
public class Account extends PersistableBusinessObjectBase implements AccountIntf, MutableInactivatable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Account.class);
    
    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "Account";

    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String accountName;
    protected boolean accountsFringesBnftIndicator;
    protected Date accountRestrictedStatusDate;
    protected String accountCityName;
    protected String accountStateCode;
    protected String accountStreetAddress;
    protected String accountZipCode;
    protected Date accountCreateDate;
    protected Date accountEffectiveDate;
    protected Date accountExpirationDate;
    protected String acctIndirectCostRcvyTypeCd;
    protected String acctCustomIndCstRcvyExclCd;
    protected String financialIcrSeriesIdentifier;
    protected boolean accountInFinancialProcessingIndicator;
    protected String budgetRecordingLevelCode;
    protected String accountSufficientFundsCode;
    protected boolean pendingAcctSufficientFundsIndicator;
    protected boolean extrnlFinEncumSufficntFndIndicator;
    protected boolean intrnlFinEncumSufficntFndIndicator;
    protected boolean finPreencumSufficientFundIndicator;
    protected boolean financialObjectivePrsctrlIndicator;
    protected String accountCfdaNumber;
    protected boolean accountOffCampusIndicator;
    protected boolean active;

    protected String accountFiscalOfficerSystemIdentifier;
    protected String accountsSupervisorySystemsIdentifier;
    protected String accountManagerSystemIdentifier;
    protected String organizationCode;
    protected String accountTypeCode;
    protected String accountPhysicalCampusCode;
    protected String subFundGroupCode;
    protected String financialHigherEdFunctionCd;
    protected String accountRestrictedStatusCode;
    protected String reportsToChartOfAccountsCode;
    protected String reportsToAccountNumber;
    protected String continuationFinChrtOfAcctCd;
    protected String continuationAccountNumber;
    protected String endowmentIncomeAcctFinCoaCd;
    protected String endowmentIncomeAccountNumber;
    protected String contractControlFinCoaCode;
    protected String contractControlAccountNumber;
    protected String incomeStreamFinancialCoaCode;
    protected String incomeStreamAccountNumber;
    protected Integer contractsAndGrantsAccountResponsibilityId;

    protected Chart chartOfAccounts;
    protected Chart endowmentIncomeChartOfAccounts;
    protected Organization organization;
    protected AccountType accountType;
    protected CampusEbo accountPhysicalCampus;
    protected StateEbo accountState;
    protected SubFundGroup subFundGroup;
    protected HigherEducationFunction financialHigherEdFunction;
    protected RestrictedStatus accountRestrictedStatus;
    protected Account reportsToAccount;
    protected Account continuationAccount;
    protected Account endowmentIncomeAccount;
    protected Account contractControlAccount;
    protected Account incomeStreamAccount;
    protected IndirectCostRecoveryType acctIndirectCostRcvyType;
    protected Person accountFiscalOfficerUser;
    protected Person accountSupervisoryUser;
    protected Person accountManagerUser;
    protected PostalCodeEbo postalZipCode;
    protected BudgetRecordingLevel budgetRecordingLevel;
    protected SufficientFundsCode sufficientFundsCode;
    protected CFDA cfda;

    protected Chart fringeBenefitsChartOfAccount;
    protected Chart continuationChartOfAccount;
    protected Chart incomeStreamChartOfAccounts;
    protected Chart contractControlChartOfAccounts;
    
    // Several kinds of Dummy Attributes for dividing sections on Inquiry page
    protected String accountResponsibilitySectionBlank;
    protected String accountResponsibilitySection;
    protected String contractsAndGrantsSectionBlank;
    protected String contractsAndGrantsSection;
    protected String guidelinesAndPurposeSectionBlank;
    protected String guidelinesAndPurposeSection;
    protected String accountDescriptionSectionBlank;
    protected String accountDescriptionSection;

    protected Boolean forContractsAndGrants;

    protected AccountGuideline accountGuideline;
    protected AccountDescription accountDescription;

    protected List subAccounts;
    protected List<ContractsAndGrantsAccountAwardInformation> awards;
    protected List<IndirectCostRecoveryAccount> indirectCostRecoveryAccounts;
    //added for the employee labor benefit calculation
    private String laborBenefitRateCategoryCode;
    private LaborBenefitRateCategory laborBenefitRateCategory;
    /**
     * Default no-arg constructor.
     */
    public Account() {
        active = true; // assume active is true until set otherwise
        indirectCostRecoveryAccounts = new ArrayList<IndirectCostRecoveryAccount>();
    }
    
    /**
     * This method gathers all SubAccounts related to this account if the account is marked as closed to deactivate
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);

        // retreive all the existing sub accounts for this
        List<SubAccount> bosToDeactivate = new ArrayList();
        Map<String, Object> fieldValues;
        Collection existingSubAccounts;
        if (!isActive()) {
            fieldValues = new HashMap();
            fieldValues.put("chartOfAccountsCode", this.getChartOfAccountsCode());
            fieldValues.put("accountNumber", this.getAccountNumber());
            fieldValues.put("active", true);
            existingSubAccounts = boService.findMatching(SubAccount.class, fieldValues);
            bosToDeactivate.addAll(existingSubAccounts);
        }


        // mark all the sub accounts as inactive
        for (SubAccount subAccount : bosToDeactivate) {
            subAccount.setActive(false);
        }
        return new ArrayList<PersistableBusinessObject>(bosToDeactivate);
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the accountName attribute.
     * 
     * @return Returns the accountName
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the accountName attribute.
     * 
     * @param accountName The accountName to set.
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Gets the _AccountsFringesBnftIndicator_ attribute.
     * 
     * @return Returns the _AccountsFringesBnftIndicator_
     */
    public boolean isAccountsFringesBnftIndicator() {
        return accountsFringesBnftIndicator;
    }

    /**
     * Sets the _AccountsFringesBnftIndicator_ attribute.
     * 
     * @param _AccountsFringesBnftIndicator_ The _AccountsFringesBnftIndicator_ to set.
     */
    public void setAccountsFringesBnftIndicator(boolean _AccountsFringesBnftIndicator_) {
        this.accountsFringesBnftIndicator = _AccountsFringesBnftIndicator_;
    }

    /**
     * Gets the accountRestrictedStatusDate attribute.
     * 
     * @return Returns the accountRestrictedStatusDate
     */
    public Date getAccountRestrictedStatusDate() {
        return accountRestrictedStatusDate;
    }

    /**
     * Sets the accountRestrictedStatusDate attribute.
     * 
     * @param accountRestrictedStatusDate The accountRestrictedStatusDate to set.
     */
    public void setAccountRestrictedStatusDate(Date accountRestrictedStatusDate) {
        this.accountRestrictedStatusDate = accountRestrictedStatusDate;
    }

    /**
     * Gets the accountCityName attribute.
     * 
     * @return Returns the accountCityName
     */
    public String getAccountCityName() {
        return accountCityName;
    }

    /**
     * Sets the accountCityName attribute.
     * 
     * @param accountCityName The accountCityName to set.
     */
    public void setAccountCityName(String accountCityName) {
        this.accountCityName = accountCityName;
    }

    /**
     * Gets the accountStateCode attribute.
     * 
     * @return Returns the accountStateCode
     */
    public String getAccountStateCode() {
        return accountStateCode;
    }

    /**
     * Sets the accountStateCode attribute.
     * 
     * @param accountStateCode The accountStateCode to set.
     */
    public void setAccountStateCode(String accountStateCode) {
        this.accountStateCode = accountStateCode;
    }

    /**
     * Gets the accountStreetAddress attribute.
     * 
     * @return Returns the accountStreetAddress
     */
    public String getAccountStreetAddress() {
        return accountStreetAddress;
    }

    /**
     * Sets the accountStreetAddress attribute.
     * 
     * @param accountStreetAddress The accountStreetAddress to set.
     */
    public void setAccountStreetAddress(String accountStreetAddress) {
        this.accountStreetAddress = accountStreetAddress;
    }

    /**
     * Gets the accountZipCode attribute.
     * 
     * @return Returns the accountZipCode
     */
    public String getAccountZipCode() {
        return accountZipCode;
    }

    /**
     * Sets the accountZipCode attribute.
     * 
     * @param accountZipCode The accountZipCode to set.
     */
    public void setAccountZipCode(String accountZipCode) {
        this.accountZipCode = accountZipCode;
    }

    /**
     * Gets the accountCreateDate attribute.
     * 
     * @return Returns the accountCreateDate
     */
    public Date getAccountCreateDate() {
        return accountCreateDate;
    }

    /**
     * Sets the accountCreateDate attribute.
     * 
     * @param accountCreateDate The accountCreateDate to set.
     */
    public void setAccountCreateDate(Date accountCreateDate) {
        this.accountCreateDate = accountCreateDate;
    }

    /**
     * Gets the accountEffectiveDate attribute.
     * 
     * @return Returns the accountEffectiveDate
     */
    public Date getAccountEffectiveDate() {
        return accountEffectiveDate;
    }

    /**
     * Sets the accountEffectiveDate attribute.
     * 
     * @param accountEffectiveDate The accountEffectiveDate to set.
     */
    public void setAccountEffectiveDate(Date accountEffectiveDate) {
        this.accountEffectiveDate = accountEffectiveDate;
    }

    /**
     * Gets the accountExpirationDate attribute.
     * 
     * @return Returns the accountExpirationDate
     */
    public Date getAccountExpirationDate() {
        return accountExpirationDate;
    }

    /**
     * Sets the accountExpirationDate attribute.
     * 
     * @param accountExpirationDate The accountExpirationDate to set.
     */
    public void setAccountExpirationDate(Date accountExpirationDate) {
        this.accountExpirationDate = accountExpirationDate;
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same as today, then this
     * will return false. It will only return true if the account expiration date is one day earlier than today or earlier. Note
     * that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on date
     * values, not time-values.
     * 
     * @return true or false based on the logic outlined above
     */
    public boolean isExpired() {
        LOG.debug("entering isExpired()");
        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        return this.isExpired(SpringContext.getBean(DateTimeService.class).getCurrentCalendar());
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same date as testDate, then
     * this will return false. It will only return true if the account expiration date is one day earlier than testDate or earlier.
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     * 
     * @param testDate - Calendar instance with the date to test the Account's Expiration Date against. This is most commonly set to
     *        today's date.
     * @return true or false based on the logic outlined above
     */
    public boolean isExpired(Calendar testDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("entering isExpired(" + testDate + ")");
        }

        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        // remove any time-components from the testDate
        testDate = DateUtils.truncate(testDate, Calendar.DAY_OF_MONTH);

        // get a calendar reference to the Account Expiration
        // date, and remove any time components
        Calendar acctDate = Calendar.getInstance();
        acctDate.setTime(this.accountExpirationDate);
        acctDate = DateUtils.truncate(acctDate, Calendar.DAY_OF_MONTH);

        // if the Account Expiration Date is before the testDate
        if (acctDate.before(testDate)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * This method determines whether the account is expired or not. Note that if Expiration Date is the same date as testDate, then
     * this will return false. It will only return true if the account expiration date is one day earlier than testDate or earlier.
     * Note that this logic ignores all time components when doing the comparison. It only does the before/after comparison based on
     * date values, not time-values.
     * 
     * @param testDate - java.util.Date instance with the date to test the Account's Expiration Date against. This is most commonly
     *        set to today's date.
     * @return true or false based on the logic outlined above
     */
    public boolean isExpired(Date testDate) {

        // dont even bother trying to test if the accountExpirationDate is null
        if (this.accountExpirationDate == null) {
            return false;
        }

        Calendar acctDate = Calendar.getInstance();
        acctDate.setTime(testDate);
        return isExpired(acctDate);
    }

    /**
     * Gets the acctIndirectCostRcvyTypeCd attribute.
     * 
     * @return Returns the acctIndirectCostRcvyTypeCd
     */
    public String getAcctIndirectCostRcvyTypeCd() {
        return acctIndirectCostRcvyTypeCd;
    }

    /**
     * Sets the acctIndirectCostRcvyTypeCd attribute.
     * 
     * @param acctIndirectCostRcvyTypeCd The acctIndirectCostRcvyTypeCd to set.
     */
    public void setAcctIndirectCostRcvyTypeCd(String acctIndirectCostRcvyTypeCd) {
        this.acctIndirectCostRcvyTypeCd = acctIndirectCostRcvyTypeCd;
    }

    /**
     * Gets the acctCustomIndCstRcvyExclCd attribute.
     * 
     * @return Returns the acctCustomIndCstRcvyExclCd
     */
    public String getAcctCustomIndCstRcvyExclCd() {
        return acctCustomIndCstRcvyExclCd;
    }

    /**
     * Sets the acctCustomIndCstRcvyExclCd attribute.
     * 
     * @param acctCustomIndCstRcvyExclCd The acctCustomIndCstRcvyExclCd to set.
     */
    public void setAcctCustomIndCstRcvyExclCd(String acctCustomIndCstRcvyExclCd) {
        this.acctCustomIndCstRcvyExclCd = acctCustomIndCstRcvyExclCd;
    }

    /**
     * Gets the financialIcrSeriesIdentifier attribute.
     * 
     * @return Returns the financialIcrSeriesIdentifier
     */
    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }

    /**
     * Sets the financialIcrSeriesIdentifier attribute.
     * 
     * @param financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
     */
    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }

    /**
     * Gets the accountInFinancialProcessingIndicator attribute.
     * 
     * @return Returns the accountInFinancialProcessingIndicator
     */
    public boolean getAccountInFinancialProcessingIndicator() {
        return accountInFinancialProcessingIndicator;
    }

    /**
     * Sets the accountInFinancialProcessingIndicator attribute.
     * 
     * @param accountInFinancialProcessingIndicator The accountInFinancialProcessingIndicator to set.
     */
    public void setAccountInFinancialProcessingIndicator(boolean accountInFinancialProcessingIndicator) {
        this.accountInFinancialProcessingIndicator = accountInFinancialProcessingIndicator;
    }

    /**
     * Gets the budgetRecordingLevelCode attribute.
     * 
     * @return Returns the budgetRecordingLevelCode
     */
    public String getBudgetRecordingLevelCode() {
        return budgetRecordingLevelCode;
    }

    /**
     * Sets the budgetRecordingLevelCode attribute.
     * 
     * @param budgetRecordingLevelCode The budgetRecordingLevelCode to set.
     */
    public void setBudgetRecordingLevelCode(String budgetRecordingLevelCode) {
        this.budgetRecordingLevelCode = budgetRecordingLevelCode;
    }

    /**
     * Gets the accountSufficientFundsCode attribute.
     * 
     * @return Returns the accountSufficientFundsCode
     */
    public String getAccountSufficientFundsCode() {
        return accountSufficientFundsCode;
    }

    /**
     * Sets the accountSufficientFundsCode attribute.
     * 
     * @param accountSufficientFundsCode The accountSufficientFundsCode to set.
     */
    public void setAccountSufficientFundsCode(String accountSufficientFundsCode) {
        this.accountSufficientFundsCode = accountSufficientFundsCode;
    }

    /**
     * Gets the pendingAcctSufficientFundsIndicator attribute.
     * 
     * @return Returns the pendingAcctSufficientFundsIndicator
     */
    public boolean isPendingAcctSufficientFundsIndicator() {
        return pendingAcctSufficientFundsIndicator;
    }

    /**
     * Sets the pendingAcctSufficientFundsIndicator attribute.
     * 
     * @param pendingAcctSufficientFundsIndicator The pendingAcctSufficientFundsIndicator to set.
     */
    public void setPendingAcctSufficientFundsIndicator(boolean pendingAcctSufficientFundsIndicator) {
        this.pendingAcctSufficientFundsIndicator = pendingAcctSufficientFundsIndicator;
    }

    /**
     * Gets the extrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @return Returns the extrnlFinEncumSufficntFndIndicator
     */
    public boolean isExtrnlFinEncumSufficntFndIndicator() {
        return extrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Sets the extrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @param extrnlFinEncumSufficntFndIndicator The extrnlFinEncumSufficntFndIndicator to set.
     */
    public void setExtrnlFinEncumSufficntFndIndicator(boolean extrnlFinEncumSufficntFndIndicator) {
        this.extrnlFinEncumSufficntFndIndicator = extrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Gets the intrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @return Returns the intrnlFinEncumSufficntFndIndicator
     */
    public boolean isIntrnlFinEncumSufficntFndIndicator() {
        return intrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Sets the intrnlFinEncumSufficntFndIndicator attribute.
     * 
     * @param intrnlFinEncumSufficntFndIndicator The intrnlFinEncumSufficntFndIndicator to set.
     */
    public void setIntrnlFinEncumSufficntFndIndicator(boolean intrnlFinEncumSufficntFndIndicator) {
        this.intrnlFinEncumSufficntFndIndicator = intrnlFinEncumSufficntFndIndicator;
    }

    /**
     * Gets the finPreencumSufficientFundIndicator attribute.
     * 
     * @return Returns the finPreencumSufficientFundIndicator
     */
    public boolean isFinPreencumSufficientFundIndicator() {
        return finPreencumSufficientFundIndicator;
    }

    /**
     * Sets the finPreencumSufficientFundIndicator attribute.
     * 
     * @param finPreencumSufficientFundIndicator The finPreencumSufficientFundIndicator to set.
     */
    public void setFinPreencumSufficientFundIndicator(boolean finPreencumSufficientFundIndicator) {
        this.finPreencumSufficientFundIndicator = finPreencumSufficientFundIndicator;
    }

    /**
     * Gets the _FinancialObjectivePrsctrlIndicator_ attribute.
     * 
     * @return Returns the _FinancialObjectivePrsctrlIndicator_
     */
    public boolean isFinancialObjectivePrsctrlIndicator() {
        return financialObjectivePrsctrlIndicator;
    }

    /**
     * Sets the _FinancialObjectivePrsctrlIndicator_ attribute.
     * 
     * @param _FinancialObjectivePrsctrlIndicator_ The _FinancialObjectivePrsctrlIndicator_ to set.
     */
    public void setFinancialObjectivePrsctrlIndicator(boolean _FinancialObjectivePrsctrlIndicator_) {
        this.financialObjectivePrsctrlIndicator = _FinancialObjectivePrsctrlIndicator_;
    }

    /**
     * Gets the accountCfdaNumber attribute.
     * 
     * @return Returns the accountCfdaNumber
     */
    public String getAccountCfdaNumber() {
        return accountCfdaNumber;
    }

    /**
     * Sets the accountCfdaNumber attribute.
     * 
     * @param accountCfdaNumber The accountCfdaNumber to set.
     */
    public void setAccountCfdaNumber(String accountCfdaNumber) {
        this.accountCfdaNumber = accountCfdaNumber;
    }

    /**
     * Gets the related CFDA record for this account
     * 
     * @return a CFDA record
     */
    public CFDA getCfda() {
        return cfda ;
    }
    
    /**
     * Sets the given related CFDA object
     * @param cfda the related CFDA object.
     */
    public void setCfda(CFDA cfda) {
        this.cfda = cfda;
    }
    
    public List<ContractsAndGrantsAccountAwardInformation> getAwards() {
        // TODO this code totally breaks modularization but can't be fixed until data dictionary modularization plans come down the
        // pike
        awards = (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAccountAwardInformation.class).retrieveExternalizableBusinessObjectsList(this, "awards", ContractsAndGrantsAccountAwardInformation.class);
        return awards;
    }
    
    public void setAwards(List awards) {
        this.awards = awards;
    }

    public List<IndirectCostRecoveryAccount> getIndirectCostRecoveryAccounts() {
        return this.indirectCostRecoveryAccounts;
    }
    
    public List<IndirectCostRecoveryAccount> getActiveIndirectCostRecoveryAccounts() {
        List<IndirectCostRecoveryAccount> activeList = new ArrayList<IndirectCostRecoveryAccount>();
        for (IndirectCostRecoveryAccount icr : getIndirectCostRecoveryAccounts()){
            if (icr.isActive()){
                activeList.add(IndirectCostRecoveryAccount.copyICRAccount(icr));
            }
        }
        return activeList;
    }
    
    public void setIndirectCostRecoveryAccounts(List<? extends IndirectCostRecoveryAccount> indirectCostRecoveryAccounts) {
        List<IndirectCostRecoveryAccount> accountIcrList = new ArrayList<IndirectCostRecoveryAccount>();
        for (IndirectCostRecoveryAccount icr : indirectCostRecoveryAccounts){
            accountIcrList.add(icr);
        }
        this.indirectCostRecoveryAccounts = accountIcrList;
    }
    
    /**
     * Gets the accountOffCampusIndicator attribute.
     * 
     * @return Returns the accountOffCampusIndicator
     */
    public boolean isAccountOffCampusIndicator() {
        return accountOffCampusIndicator;
    }

    /**
     * Sets the accountOffCampusIndicator attribute.
     * 
     * @param accountOffCampusIndicator The accountOffCampusIndicator to set.
     */
    public void setAccountOffCampusIndicator(boolean accountOffCampusIndicator) {
        this.accountOffCampusIndicator = accountOffCampusIndicator;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns whether this account is not active or not
     * 
     * @return the opposite of isActive()
     */
    public boolean isClosed() {
        return !active;
    }
    
    /**
     * Sets the closed attribute.
     * 
     * @param closed The closed to set.
     */
    public void setClosed(boolean closed) {
        this.active = !closed;
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Organization getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    /**
     * Gets the accountType attribute.
     * 
     * @return Returns the accountType
     */
    public AccountType getAccountType() {
        return accountType;
    }

    /**
     * Sets the accountType attribute.
     * 
     * @param accountType The accountType to set.
     * @deprecated
     */
    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * Gets the accountPhysicalCampus attribute.
     * 
     * @return Returns the accountPhysicalCampus
     */
    public CampusEbo getAccountPhysicalCampus() {
        return accountPhysicalCampus = StringUtils.isBlank( accountPhysicalCampusCode)?null:((accountPhysicalCampus!=null && accountPhysicalCampus.getCode().equals( accountPhysicalCampusCode))?accountPhysicalCampus: CampusEbo.from( SpringContext.getBean(CampusService.class).getCampus( accountPhysicalCampusCode) ) );
    }

    /**
     * Sets the accountPhysicalCampus attribute.
     * 
     * @param accountPhysicalCampus The accountPhysicalCampus to set.
     * @deprecated
     */
    public void setAccountPhysicalCampus(CampusEbo accountPhysicalCampus) {
        this.accountPhysicalCampus = accountPhysicalCampus;
    }

    /**
     * Gets the accountState attribute
     * 
     * @return Returns the accountState
     */
    public StateEbo getAccountState() {
        accountState = (StringUtils.isBlank(accountStateCode))?null:( accountState == null||!StringUtils.equals( accountState.getCode(),accountStateCode))? StateEbo.from( SpringContext.getBean(StateService.class).getState("US"/*REFACTORME*/,accountStateCode) ) : accountState;
        return accountState;
    }

    /**
     * Sets the accountState attribute
     * 
     * @param state
     * @deprecated
     */
    public void setAccountState(StateEbo state) {
        this.accountState = state;
    }

    /**
     * Gets the subFundGroup attribute.
     * 
     * @return Returns the subFundGroup
     */
    public SubFundGroup getSubFundGroup() {
        return subFundGroup;
    }

    /**
     * Sets the subFundGroup attribute.
     * 
     * @param subFundGroup The subFundGroup to set.
     * @deprecated
     */
    public void setSubFundGroup(SubFundGroup subFundGroup) {
        this.subFundGroup = subFundGroup;
    }

    /**
     * Gets the financialHigherEdFunction attribute.
     * 
     * @return Returns the financialHigherEdFunction
     */
    public HigherEducationFunction getFinancialHigherEdFunction() {
        return financialHigherEdFunction;
    }

    /**
     * Sets the financialHigherEdFunction attribute.
     * 
     * @param financialHigherEdFunction The financialHigherEdFunction to set.
     * @deprecated
     */
    public void setFinancialHigherEdFunction(HigherEducationFunction financialHigherEdFunction) {
        this.financialHigherEdFunction = financialHigherEdFunction;
    }

    /**
     * Gets the accountRestrictedStatus attribute.
     * 
     * @return Returns the accountRestrictedStatus
     */
    public RestrictedStatus getAccountRestrictedStatus() {
        return accountRestrictedStatus;
    }

    /**
     * Sets the accountRestrictedStatus attribute.
     * 
     * @param accountRestrictedStatus The accountRestrictedStatus to set.
     * @deprecated
     */
    public void setAccountRestrictedStatus(RestrictedStatus accountRestrictedStatus) {
        this.accountRestrictedStatus = accountRestrictedStatus;
    }

    /**
     * Gets the reportsToAccount attribute.
     * 
     * @return Returns the reportsToAccount
     */
    public Account getReportsToAccount() {
        return reportsToAccount;
    }

    /**
     * Sets the reportsToAccount attribute.
     * 
     * @param reportsToAccount The reportsToAccount to set.
     * @deprecated
     */
    public void setReportsToAccount(Account reportsToAccount) {
        this.reportsToAccount = reportsToAccount;
    }

    /**
     * Gets the endowmentIncomeAccount attribute.
     * 
     * @return Returns the endowmentIncomeAccount
     */
    public Account getEndowmentIncomeAccount() {
        return endowmentIncomeAccount;
    }

    /**
     * Sets the endowmentIncomeAccount attribute.
     * 
     * @param endowmentIncomeAccount The endowmentIncomeAccount to set.
     * @deprecated
     */
    public void setEndowmentIncomeAccount(Account endowmentIncomeAccount) {
        this.endowmentIncomeAccount = endowmentIncomeAccount;
    }

    /**
     * Gets the contractControlAccount attribute.
     * 
     * @return Returns the contractControlAccount
     */
    public Account getContractControlAccount() {
        return contractControlAccount;
    }

    /**
     * Sets the contractControlAccount attribute.
     * 
     * @param contractControlAccount The contractControlAccount to set.
     * @deprecated
     */
    public void setContractControlAccount(Account contractControlAccount) {
        this.contractControlAccount = contractControlAccount;
    }


    /**
     * Gets the incomeStreamAccount attribute.
     * 
     * @return Returns the incomeStreamAccount
     */
    public Account getIncomeStreamAccount() {
        return incomeStreamAccount;
    }

    /**
     * Sets the incomeStreamAccount attribute.
     * 
     * @param incomeStreamAccount The incomeStreamAccount to set.
     * @deprecated
     */
    public void setIncomeStreamAccount(Account incomeStreamAccount) {
        this.incomeStreamAccount = incomeStreamAccount;
    }

    public Person getAccountFiscalOfficerUser() {
        accountFiscalOfficerUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountFiscalOfficerSystemIdentifier, accountFiscalOfficerUser);
        return accountFiscalOfficerUser;
    }
    
    /**
     * This fix is temporary until Jonathan's fix is reflected to Rice
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#refreshReferenceObject(java.lang.String)
     */
    public void refreshReferenceObject(String referenceObjectName) {
        if (referenceObjectName.equals("accountFiscalOfficerUser") ||
            referenceObjectName.equals("accountSupervisoryUser") ||
            referenceObjectName.equals("accountManagerUser")) {
            // do nothing
        } else {
            super.refreshReferenceObject(referenceObjectName);
        }
    }

   /**
     * @param accountFiscalOfficerUser The accountFiscalOfficerUser to set.
     * @deprecated
     */
    public void setAccountFiscalOfficerUser(Person accountFiscalOfficerUser) {
        this.accountFiscalOfficerUser = accountFiscalOfficerUser;
    }

    public Person getAccountManagerUser() {
        accountManagerUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountManagerSystemIdentifier, accountManagerUser);
        return accountManagerUser;
    }

    /**
     * @param accountManagerUser The accountManagerUser to set.
     * @deprecated
     */
    public void setAccountManagerUser(Person accountManagerUser) {
        this.accountManagerUser = accountManagerUser;
    }


    public Person getAccountSupervisoryUser() {
        accountSupervisoryUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(accountsSupervisorySystemsIdentifier, accountSupervisoryUser);
        return accountSupervisoryUser;
    }

    /**
     * @param accountSupervisoryUser The accountSupervisoryUser to set.
     * @deprecated
     */
    public void setAccountSupervisoryUser(Person accountSupervisoryUser) {
        this.accountSupervisoryUser = accountSupervisoryUser;
    }


    /**
     * @return Returns the continuationAccount.
     */
    public Account getContinuationAccount() {
        return continuationAccount;
    }


    /**
     * @param continuationAccount The continuationAccount to set.
     * @deprecated
     */
    public void setContinuationAccount(Account continuationAccount) {
        this.continuationAccount = continuationAccount;
    }

    /**
     * @return Returns the accountGuideline.
     */
    public AccountGuideline getAccountGuideline() {
        return accountGuideline;
    }


    /**
     * @param accountGuideline The accountGuideline to set.
     */
    public void setAccountGuideline(AccountGuideline accountGuideline) {
        this.accountGuideline = accountGuideline;
    }


    /**
     * Gets the accountDescription attribute.
     * 
     * @return Returns the accountDescription.
     */
    public AccountDescription getAccountDescription() {
        return accountDescription;
    }

    /**
     * Sets the accountDescription attribute value.
     * 
     * @param accountDescription The accountDescription to set.
     */
    public void setAccountDescription(AccountDescription accountDescription) {
        this.accountDescription = accountDescription;
    }

    /**
     * @return Returns the subAccounts.
     */
    public List getSubAccounts() {
        return subAccounts;
    }


    /**
     * @param subAccounts The subAccounts to set.
     */
    public void setSubAccounts(List subAccounts) {
        this.subAccounts = subAccounts;
    }


    /**
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    public String getAccountFiscalOfficerSystemIdentifier() {
        return accountFiscalOfficerSystemIdentifier;
    }

    /**
     * @return Returns the accountFiscalOfficerSystemIdentifier.
     */
    public String getAccountFiscalOfficerSystemIdentifierForSearching() {
        return getAccountFiscalOfficerSystemIdentifier();
    }
    
    
    /**
     * @param accountFiscalOfficerSystemIdentifier The accountFiscalOfficerSystemIdentifier to set.
     */
    public void setAccountFiscalOfficerSystemIdentifier(String accountFiscalOfficerSystemIdentifier) {
        this.accountFiscalOfficerSystemIdentifier = accountFiscalOfficerSystemIdentifier;
    }

    /**
     * @return Returns the accountManagerSystemIdentifier.
     */
    public String getAccountManagerSystemIdentifier() {
        return accountManagerSystemIdentifier;
    }
    
    /**
     * @return Returns the accountManagerSystemIdentifier.
     */
    public String getAccountManagerSystemIdentifierForSearching() {
        return getAccountManagerSystemIdentifier();
    }

    /**
     * @param accountManagerSystemIdentifier The accountManagerSystemIdentifier to set.
     */
    public void setAccountManagerSystemIdentifier(String accountManagerSystemIdentifier) {
        this.accountManagerSystemIdentifier = accountManagerSystemIdentifier;
    }

    /**
     * @return Returns the accountPhysicalCampusCode.
     */
    public String getAccountPhysicalCampusCode() {
        return accountPhysicalCampusCode;
    }

    /**
     * @param accountPhysicalCampusCode The accountPhysicalCampusCode to set.
     */
    public void setAccountPhysicalCampusCode(String accountPhysicalCampusCode) {
        this.accountPhysicalCampusCode = accountPhysicalCampusCode;
    }

    /**
     * @return Returns the accountRestrictedStatusCode.
     */
    public String getAccountRestrictedStatusCode() {
        return accountRestrictedStatusCode;
    }

    /**
     * @param accountRestrictedStatusCode The accountRestrictedStatusCode to set.
     */
    public void setAccountRestrictedStatusCode(String accountRestrictedStatusCode) {
        this.accountRestrictedStatusCode = accountRestrictedStatusCode;
    }

    /**
     * @return Returns the accountsSupervisorySystemsIdentifier.
     */
    public String getAccountsSupervisorySystemsIdentifier() {
        return accountsSupervisorySystemsIdentifier;
    }
    
    /**
     * @return Returns the accountsSupervisorySystemsIdentifier.
     */
    public String getAccountsSupervisorySystemsIdentifierForSearching() {
        return accountsSupervisorySystemsIdentifier;
    }

    /**
     * @param accountsSupervisorySystemsIdentifier The accountsSupervisorySystemsIdentifier to set.
     */
    public void setAccountsSupervisorySystemsIdentifier(String accountsSupervisorySystemsIdentifier) {
        this.accountsSupervisorySystemsIdentifier = accountsSupervisorySystemsIdentifier;
    }

    /**
     * @return Returns the accountTypeCode.
     */
    public String getAccountTypeCode() {
        return accountTypeCode;
    }

    /**
     * @param accountTypeCode The accountTypeCode to set.
     */
    public void setAccountTypeCode(String accountTypeCode) {
        this.accountTypeCode = accountTypeCode;
    }

    /**
     * @return Returns the continuationAccountNumber.
     */
    public String getContinuationAccountNumber() {
        return continuationAccountNumber;
    }

    /**
     * @param continuationAccountNumber The continuationAccountNumber to set.
     */
    public void setContinuationAccountNumber(String continuationAccountNumber) {
        this.continuationAccountNumber = continuationAccountNumber;
    }

    /**
     * @return Returns the continuationFinChrtOfAcctCd.
     */
    public String getContinuationFinChrtOfAcctCd() {
        return continuationFinChrtOfAcctCd;
    }

    /**
     * @param continuationFinChrtOfAcctCd The continuationFinChrtOfAcctCd to set.
     */
    public void setContinuationFinChrtOfAcctCd(String continuationFinChrtOfAcctCd) {
        this.continuationFinChrtOfAcctCd = continuationFinChrtOfAcctCd;
    }

    /**
     * @return Returns the contractControlAccountNumber.
     */
    public String getContractControlAccountNumber() {
        return contractControlAccountNumber;
    }

    /**
     * @param contractControlAccountNumber The contractControlAccountNumber to set.
     */
    public void setContractControlAccountNumber(String contractControlAccountNumber) {
        this.contractControlAccountNumber = contractControlAccountNumber;
    }

    /**
     * @return Returns the contractControlFinCoaCode.
     */
    public String getContractControlFinCoaCode() {
        return contractControlFinCoaCode;
    }

    /**
     * @param contractControlFinCoaCode The contractControlFinCoaCode to set.
     */
    public void setContractControlFinCoaCode(String contractControlFinCoaCode) {
        this.contractControlFinCoaCode = contractControlFinCoaCode;
    }

    /**
     * @return Returns the endowmentIncomeAccountNumber.
     */
    public String getEndowmentIncomeAccountNumber() {
        return endowmentIncomeAccountNumber;
    }

    /**
     * @param endowmentIncomeAccountNumber The endowmentIncomeAccountNumber to set.
     */
    public void setEndowmentIncomeAccountNumber(String endowmentIncomeAccountNumber) {
        this.endowmentIncomeAccountNumber = endowmentIncomeAccountNumber;
    }

    /**
     * @return Returns the endowmentIncomeAcctFinCoaCd.
     */
    public String getEndowmentIncomeAcctFinCoaCd() {
        return endowmentIncomeAcctFinCoaCd;
    }

    /**
     * @param endowmentIncomeAcctFinCoaCd The endowmentIncomeAcctFinCoaCd to set.
     */
    public void setEndowmentIncomeAcctFinCoaCd(String endowmentIncomeAcctFinCoaCd) {
        this.endowmentIncomeAcctFinCoaCd = endowmentIncomeAcctFinCoaCd;
    }

    /**
     * @return Returns the financialHigherEdFunctionCd.
     */
    public String getFinancialHigherEdFunctionCd() {
        return financialHigherEdFunctionCd;
    }

    /**
     * @param financialHigherEdFunctionCd The financialHigherEdFunctionCd to set.
     */
    public void setFinancialHigherEdFunctionCd(String financialHigherEdFunctionCd) {
        this.financialHigherEdFunctionCd = financialHigherEdFunctionCd;
    }

    /**
     * @return Returns the incomeStreamAccountNumber.
     */
    public String getIncomeStreamAccountNumber() {
        return incomeStreamAccountNumber;
    }

    /**
     * @param incomeStreamAccountNumber The incomeStreamAccountNumber to set.
     */
    public void setIncomeStreamAccountNumber(String incomeStreamAccountNumber) {
        this.incomeStreamAccountNumber = incomeStreamAccountNumber;
    }

    /**
     * @return Returns the incomeStreamFinancialCoaCode.
     */
    public String getIncomeStreamFinancialCoaCode() {
        return incomeStreamFinancialCoaCode;
    }

    /**
     * @param incomeStreamFinancialCoaCode The incomeStreamFinancialCoaCode to set.
     */
    public void setIncomeStreamFinancialCoaCode(String incomeStreamFinancialCoaCode) {
        this.incomeStreamFinancialCoaCode = incomeStreamFinancialCoaCode;
    }

    /**
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * @return Returns the reportsToAccountNumber.
     */
    public String getReportsToAccountNumber() {
        return reportsToAccountNumber;
    }

    /**
     * @param reportsToAccountNumber The reportsToAccountNumber to set.
     */
    public void setReportsToAccountNumber(String reportsToAccountNumber) {
        this.reportsToAccountNumber = reportsToAccountNumber;
    }

    /**
     * @return Returns the reportsToChartOfAccountsCode.
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }

    /**
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {        
        this.subFundGroupCode = subFundGroupCode;
        forContractsAndGrants = null;
    }

    /**
     * Gets the postalZipCode attribute.
     * 
     * @return Returns the postalZipCode.
     */
    public PostalCodeEbo getPostalZipCode() {
        postalZipCode = (accountZipCode == null)?null:( postalZipCode == null || !StringUtils.equals( postalZipCode.getCode(),accountZipCode))?PostalCodeEbo.from(SpringContext.getBean(PostalCodeService.class).getPostalCode("US"/*RICE20_REFACTORME*/,accountZipCode)): postalZipCode;
        return postalZipCode;
    }

    /**
     * Sets the postalZipCode attribute value.
     * 
     * @param postalZipCode The postalZipCode to set.
     */
    public void setPostalZipCode(PostalCodeEbo postalZipCode) {
        this.postalZipCode = postalZipCode;
    }

    /**
     * Gets the budgetRecordingLevel attribute.
     * 
     * @return Returns the budgetRecordingLevel.
     */
    public BudgetRecordingLevel getBudgetRecordingLevel() {
        return budgetRecordingLevel;
    }

    /**
     * Sets the budgetRecordingLevel attribute value.
     * 
     * @param budgetRecordingLevel The budgetRecordingLevel to set.
     */
    public void setBudgetRecordingLevel(BudgetRecordingLevel budgetRecordingLevel) {
        this.budgetRecordingLevel = budgetRecordingLevel;
    }

    /**
     * Gets the sufficientFundsCode attribute.
     * 
     * @return Returns the sufficientFundsCode.
     */
    public SufficientFundsCode getSufficientFundsCode() {
        return sufficientFundsCode;
    }

    /**
     * Sets the sufficientFundsCode attribute value.
     * 
     * @param sufficientFundsCode The sufficientFundsCode to set.
     */
    public void setSufficientFundsCode(SufficientFundsCode sufficientFundsCode) {
        this.sufficientFundsCode = sufficientFundsCode;
    }

    /**
     * Gets the acctIndirectCostRcvyType attribute.
     * 
     * @return Returns the acctIndirectCostRcvyType.
     */
    public IndirectCostRecoveryType getAcctIndirectCostRcvyType() {
        return acctIndirectCostRcvyType;
    }

    /**
     * Sets the acctIndirectCostRcvyType attribute value.
     * 
     * @param acctIndirectCostRcvyType The acctIndirectCostRcvyType to set.
     */
    public void setAcctIndirectCostRcvyType(IndirectCostRecoveryType acctIndirectCostRcvyType) {
        this.acctIndirectCostRcvyType = acctIndirectCostRcvyType;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);

        return m;
    }


    /**
     * Implementing equals since I need contains to behave reasonably in a hashed datastructure.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                Account other = (Account) obj;

                if (StringUtils.equals(this.getChartOfAccountsCode(), other.getChartOfAccountsCode())) {
                    if (StringUtils.equals(this.getAccountNumber(), other.getAccountNumber())) {
                        equal = true;
                    }
                }
            }
        }

        return equal;
    }

    /**
     * Calcluates hashCode based on current values of chartOfAccountsCode and accountNumber fields. Somewhat dangerous, since both
     * of those fields are mutable, but I don't expect people to be editing those values directly for Accounts stored in hashed
     * datastructures.
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = getChartOfAccountsCode() + "|" + getAccountNumber();

        return hashString.hashCode();
    }


    /**
     * Convenience method to make the primitive account fields from this Account easier to compare to the account fields from
     * another Account or an AccountingLine
     * 
     * @return String representing the account associated with this Accounting
     */
    public String getAccountKey() {
        String key = getChartOfAccountsCode() + ":" + getAccountNumber();
        return key;
    }

    /**
     * Gets the dummy attribute.
     * 
     * @return Returns the dummy.
     */

    /**
     * Gets the accountResponsibilitySection attribute.
     * 
     * @return Returns the accountResponsibilitySection.
     */
    public String getAccountResponsibilitySection() {
        return accountResponsibilitySection;
    }

    /**
     * Sets the accountResponsibilitySection attribute value.
     * 
     * @param accountResponsibilitySection The accountResponsibilitySection to set.
     */
    public void setAccountResponsibilitySection(String accountResponsibilitySection) {
        this.accountResponsibilitySection = accountResponsibilitySection;
    }

    /**
     * Gets the contractsAndGrantsSection attribute.
     * 
     * @return Returns the contractsAndGrantsSection.
     */
    public String getContractsAndGrantsSection() {
        return contractsAndGrantsSection;
    }

    /**
     * Sets the contractsAndGrantsSection attribute value.
     * 
     * @param contractsAndGrantsSection The contractsAndGrantsSection to set.
     */
    public void setContractsAndGrantsSection(String contractsAndGrantsSection) {
        this.contractsAndGrantsSection = contractsAndGrantsSection;
    }

    /**
     * Gets the accountDescriptionSection attribute.
     * 
     * @return Returns the accountDescriptionSection.
     */
    public String getAccountDescriptionSection() {
        return accountDescriptionSection;
    }

    /**
     * Sets the accountDescriptionSection attribute value.
     * 
     * @param accountDescriptionSection The accountDescriptionSection to set.
     */
    public void setAccountDescriptionSection(String accountDescriptionSection) {
        this.accountDescriptionSection = accountDescriptionSection;
    }

    /**
     * Gets the guidelinesAndPurposeSection attribute.
     * 
     * @return Returns the guidelinesAndPurposeSection.
     */
    public String getGuidelinesAndPurposeSection() {
        return guidelinesAndPurposeSection;
    }

    /**
     * Sets the guidelinesAndPurposeSection attribute value.
     * 
     * @param guidelinesAndPurposeSection The guidelinesAndPurposeSection to set.
     */
    public void setGuidelinesAndPurposeSection(String guidelinesAndPurposeSection) {
        this.guidelinesAndPurposeSection = guidelinesAndPurposeSection;
    }

    /**
     * Gets the accountResponsibilitySectionBlank attribute.
     * 
     * @return Returns the accountResponsibilitySectionBlank.
     */
    public String getAccountResponsibilitySectionBlank() {
        return accountResponsibilitySectionBlank;
    }

    /**
     * Gets the contractsAndGrantsSectionBlank attribute.
     * 
     * @return Returns the contractsAndGrantsSectionBlank.
     */
    public String getContractsAndGrantsSectionBlank() {
        return contractsAndGrantsSectionBlank;
    }

    /**
     * Gets the accountDescriptionSectionBlank attribute.
     * 
     * @return Returns the accountDescriptionSectionBlank.
     */
    public String getAccountDescriptionSectionBlank() {
        return accountDescriptionSectionBlank;
    }

    /**
     * Gets the guidelinesAndPurposeSectionBlank attribute.
     * 
     * @return Returns the guidelinesAndPurposeSectionBlank.
     */
    public String getGuidelinesAndPurposeSectionBlank() {
        return guidelinesAndPurposeSectionBlank;
    }

    /**
     * Gets the endowmentIncomeChartOfAccounts attribute.
     * 
     * @return Returns the endowmentIncomeChartOfAccounts.
     */
    public Chart getEndowmentIncomeChartOfAccounts() {
        return endowmentIncomeChartOfAccounts;
    }

    /**
     * Sets the endowmentIncomeChartOfAccounts attribute value.
     * 
     * @param endowmentIncomeChartOfAccounts The endowmentIncomeChartOfAccounts to set.
     */
    public void setEndowmentIncomeChartOfAccounts(Chart endowmentIncomeChartOfAccounts) {
        this.endowmentIncomeChartOfAccounts = endowmentIncomeChartOfAccounts;
    }

    @Override protected void preUpdate() {
        super.preUpdate();
        try {
            // KULCOA-549: update the sufficient funds table
            // get the current data from the database
            BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
            Account originalAcct = (Account) boService.retrieve(this);

            if (originalAcct != null) {
                if (!originalAcct.getSufficientFundsCode().equals(getSufficientFundsCode()) || originalAcct.isExtrnlFinEncumSufficntFndIndicator() != isExtrnlFinEncumSufficntFndIndicator() || originalAcct.isIntrnlFinEncumSufficntFndIndicator() != isIntrnlFinEncumSufficntFndIndicator() || originalAcct.isPendingAcctSufficientFundsIndicator() != isPendingAcctSufficientFundsIndicator() || originalAcct.isFinPreencumSufficientFundIndicator() != isFinPreencumSufficientFundIndicator()) {
                    SufficientFundRebuild sfr = new SufficientFundRebuild();
                    sfr.setAccountFinancialObjectTypeCode(SufficientFundRebuild.REBUILD_ACCOUNT);
                    sfr.setChartOfAccountsCode(getChartOfAccountsCode());
                    sfr.setAccountNumberFinancialObjectCode(getAccountNumber());
                    if (boService.retrieve(sfr) == null) {
                        boService.save(sfr);
                    }
                }
            }
        }
        catch (Exception ex) {
            LOG.error("Problem updating sufficient funds rebuild table: ", ex);
        }
    }

    /**
     * Gets the forContractsAndGrants attribute.
     * 
     * @return Returns the forContractsAndGrants.
     */
    public boolean isForContractsAndGrants() {
        if ( forContractsAndGrants == null ) {
            forContractsAndGrants = SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(getSubFundGroup());
        }
        return forContractsAndGrants;
    }

    /**
     * determine if the given account is awarded by a federal agency
     * 
     * @param account the given account
     * @param federalAgencyTypeCodes the given federal agency type code
     * @return true if the given account is funded by a federal agency or associated with federal pass through indicator; otherwise,
     *         false
     */
    public boolean isAwardedByFederalAgency(List<String> federalAgencyTypeCodes) {
        return SpringContext.getBean(ContractsAndGrantsModuleService.class).isAwardedByFederalAgency(getChartOfAccountsCode(), getAccountNumber(), federalAgencyTypeCodes);
    }

    /**
     * Gets the contractsAndGrantsAccountResponsibilityId attribute.
     * 
     * @return Returns the contractsAndGrantsAccountResponsibilityId.
     */
    public Integer getContractsAndGrantsAccountResponsibilityId() {
        return contractsAndGrantsAccountResponsibilityId;
    }
    
    /**
     * Sets the contractsAndGrantsAccountResponsibilityId attribute value.
     * 
     * @param contractsAndGrantsAccountResponsibilityId The contractsAndGrantsAccountResponsibilityId to set.
     */
    public void setContractsAndGrantsAccountResponsibilityId(Integer contractsAndGrantsAccountResponsibilityId) {
        this.contractsAndGrantsAccountResponsibilityId = contractsAndGrantsAccountResponsibilityId;
    }
    
    /**
     * Gets the laborBenefitRateCategoryCode attribute. 
     * @return Returns the laborBenefitRateCategoryCode.
     */
    public String getLaborBenefitRateCategoryCode() {
        return laborBenefitRateCategoryCode;
    }

    /**
     * Sets the laborBenefitRateCategoryCode attribute value.
     * @param laborBenefitRateCategoryCode The laborBenefitRateCategoryCode to set.
     */
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode) {
        this.laborBenefitRateCategoryCode = laborBenefitRateCategoryCode;
    }

    /**
     * Gets the laborBenefitRateCategory attribute. 
     * @return Returns the laborBenefitRateCategory.
     */
    public LaborBenefitRateCategory getLaborBenefitRateCategory() {
        laborBenefitRateCategory = (LaborBenefitRateCategory) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(LaborBenefitRateCategory.class).retrieveExternalizableBusinessObjectIfNecessary(this, laborBenefitRateCategory, "LaborBenefitRateCategory");
        return laborBenefitRateCategory;
    }

    /**
     * Sets the laborBenefitRateCategory attribute value.
     * @param laborBenefitRateCategory The laborBenefitRateCategory to set.
     */
    public void setLaborBenefitRateCategory(LaborBenefitRateCategory laborBenefitRateCategory) {
        this.laborBenefitRateCategory = laborBenefitRateCategory;
    }

    
    /**
     * Gets the fringeBenefitsChartOfAccount attribute.
     * 
     * @return Returns the fringeBenefitsChartOfAccount.
     */
    public Chart getFringeBenefitsChartOfAccount() {
        return fringeBenefitsChartOfAccount;
    }

    /**
     * Sets the fringeBenefitsChartOfAccount attribute value.
     * 
     * @param fringeBenefitsChartOfAccount The fringeBenefitsChartOfAccount to set.
     */
    public void setFringeBenefitsChartOfAccount(Chart fringeBenefitsChartOfAccount) {
        this.fringeBenefitsChartOfAccount = fringeBenefitsChartOfAccount;
    }    
    
    /**
     * Gets the continuationChartOfAccount attribute.
     * 
     * @return Returns the continuationChartOfAccount.
     */
    public Chart getContinuationChartOfAccount() {
        return continuationChartOfAccount;
    }

    /**
     * Sets the continuationChartOfAccount attribute value.
     * 
     * @param continuationChartOfAccount The continuationChartOfAccount to set.
     */
    public void setContinuationChartOfAccount(Chart continuationChartOfAccount) {
        this.continuationChartOfAccount = continuationChartOfAccount;
    }    
    
    /**
     * Gets the incomeStreamChartOfAccounts attribute.
     * 
     * @return Returns the incomeStreamChartOfAccounts.
     */
    public Chart getIncomeStreamChartOfAccounts() {
        return incomeStreamChartOfAccounts;
    }

    /**
     * Sets the incomeStreamChartOfAccounts attribute value.
     * 
     * @param incomeStreamChartOfAccounts The incomeStreamChartOfAccounts to set.
     */
    public void setIncomeStreamChartOfAccounts(Chart incomeStreamChartOfAccounts) {
        this.incomeStreamChartOfAccounts = incomeStreamChartOfAccounts;
    }    
    
    /**
    * Gets the incomeStreamChartOfAccounts attribute.
    * 
    * @return Returns the incomeStreamChartOfAccounts.
    */
   public Chart getContractControlChartOfAccounts() {
       return contractControlChartOfAccounts;
   }

   /**
    * Sets the contractControlChartOfAccounts attribute value.
    * 
    * @param contractControlChartOfAccounts The contractControlChartOfAccounts to set.
    */
   public void setContractControlChartOfAccounts(Chart contractControlChartOfAccounts) {
       this.contractControlChartOfAccounts = contractControlChartOfAccounts;
   }    
   
   /**
    * Gets the indirectCostRcvyChartOfAccounts attribute.
    * 
    * @return Returns the indirectCostRcvyChartOfAccounts.
    */
   @Override
   public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
       List<Collection<PersistableBusinessObject>> managedLists = super.buildListOfDeletionAwareLists();
       managedLists.add( new ArrayList<PersistableBusinessObject>( getIndirectCostRecoveryAccounts() ));
       return managedLists;
   }
}
