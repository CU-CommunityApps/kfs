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
package org.kuali.kfs.integration.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.sys.businessobject.Options;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.coa.businessobject.BalanceTyp;

public interface LaborLedgerEntry extends PersistableBusinessObject{

    /**
     * Gets the universityFiscalYear
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear();

    /**
     * Sets the universityFiscalYear
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear);

    /**
     * Gets the chartOfAccountsCode
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode();

    /**
     * Sets the chartOfAccountsCode
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode);

    /**
     * Gets the accountNumber
     * 
     * @return Returns the accountNumber
     */
    public String getAccountNumber();

    /**
     * Sets the accountNumber
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber);

    /**
     * Gets the subAccountNumber
     * 
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber();

    /**
     * Sets the subAccountNumber
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber);

    /**
     * Gets the financialObjectCode
     * 
     * @return Returns the financialObjectCode
     */
    public String getFinancialObjectCode();

    /**
     * Sets the financialObjectCode
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode);

    /**
     * Gets the financialSubObjectCode
     * 
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode();

    /**
     * Sets the financialSubObjectCode
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode);

    /**
     * Gets the financialBalanceTypeCode
     * 
     * @return Returns the financialBalanceTypeCode
     */
    public String getFinancialBalanceTypeCode();

    /**
     * Sets the financialBalanceTypeCode
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode);

    /**
     * Gets the financialObjectTypeCode
     * 
     * @return Returns the financialObjectTypeCode
     */
    public String getFinancialObjectTypeCode();

    /**
     * Sets the financialObjectTypeCode
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode);

    /**
     * Gets the universityFiscalPeriodCode
     * 
     * @return Returns the universityFiscalPeriodCode
     */
    public String getUniversityFiscalPeriodCode();

    /**
     * Sets the universityFiscalPeriodCode
     * 
     * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
     */
    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode);

    /**
     * Gets the financialDocumentTypeCode
     * 
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode();

    /**
     * Gets the financialSystemOriginationCode
     * 
     * @return Returns the financialSystemOriginationCode.
     */
    public String getFinancialSystemOriginationCode();

    /**
     * Sets the financialSystemOriginationCode
     * 
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode);

    /**
     * Sets the financialDocumentTypeCode
     * 
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode);

    /**
     * Gets the documentNumber
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber();

    /**
     * Sets the documentNumber
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber);

    /**
     * Gets the transactionLedgerEntrySequenceNumber
     * 
     * @return Returns the transactionLedgerEntrySequenceNumber
     */
    public Integer getTransactionLedgerEntrySequenceNumber();

    /**
     * Sets the transactionLedgerEntrySequenceNumber
     * 
     * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
     */
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber);

    /**
     * Gets the positionNumber
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber();

    /**
     * Sets the positionNumber
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber);

    /**
     * Gets the projectCode
     * 
     * @return Returns the projectCode
     */
    public String getProjectCode();

    /**
     * Sets the projectCode
     * 
     * @param projectCode The projectCode to set.
     */
    public void setProjectCode(String projectCode);

    /**
     * Gets the transactionLedgerEntryDescription
     * 
     * @return Returns the transactionLedgerEntryDescription
     */
    public String getTransactionLedgerEntryDescription();

    /**
     * Sets the transactionLedgerEntryDescription
     * 
     * @param transactionLedgerEntryDescription The transactionLedgerEntryDescription to set.
     */
    public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription);

    /**
     * Gets the transactionLedgerEntryAmount
     * 
     * @return Returns the transactionLedgerEntryAmount
     */
    public KualiDecimal getTransactionLedgerEntryAmount();

    /**
     * Sets the transactionLedgerEntryAmount
     * 
     * @param transactionLedgerEntryAmount The transactionLedgerEntryAmount to set.
     */
    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount);

    /**
     * Gets the transactionDebitCreditCode
     * 
     * @return Returns the transactionDebitCreditCode
     */
    public String getTransactionDebitCreditCode();

    /**
     * Sets the transactionDebitCreditCode
     * 
     * @param transactionDebitCreditCode The transactionDebitCreditCode to set.
     */
    public void setTransactionDebitCreditCode(String transactionDebitCreditCode);

    /**
     * Gets the transactionDate
     * 
     * @return Returns the transactionDate
     */
    public Date getTransactionDate();

    /**
     * Sets the transactionDate
     * 
     * @param transactionDate The transactionDate to set.
     */
    public void setTransactionDate(Date transactionDate);

    /**
     * Gets the organizationDocumentNumber
     * 
     * @return Returns the organizationDocumentNumber
     */
    public String getOrganizationDocumentNumber();

    /**
     * Sets the organizationDocumentNumber
     * 
     * @param organizationDocumentNumber The organizationDocumentNumber to set.
     */
    public void setOrganizationDocumentNumber(String organizationDocumentNumber);

    /**
     * Gets the organizationReferenceId
     * 
     * @return Returns the organizationReferenceId
     */
    public String getOrganizationReferenceId();

    /**
     * Sets the organizationReferenceId
     * 
     * @param organizationReferenceId The organizationReferenceId to set.
     */
    public void setOrganizationReferenceId(String organizationReferenceId);

    /**
     * Gets the referenceFinancialDocumentTypeCode
     * 
     * @return Returns the referenceFinancialDocumentTypeCode
     */
    public String getReferenceFinancialDocumentTypeCode();

    /**
     * Sets the referenceFinancialDocumentTypeCode
     * 
     * @param referenceFinancialDocumentTypeCode The referenceFinancialDocumentTypeCode to set.
     */
    public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode);

    /**
     * Gets the referenceFinancialSystemOriginationCode
     * 
     * @return Returns the referenceFinancialSystemOriginationCode
     */
    public String getReferenceFinancialSystemOriginationCode();

    /**
     * Sets the referenceFinancialSystemOriginationCode
     * 
     * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
     */
    public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode);

    /**
     * Gets the referenceFinancialDocumentNumber
     * 
     * @return Returns the referenceFinancialDocumentNumber
     */
    public String getReferenceFinancialDocumentNumber();

    /**
     * Sets the referenceFinancialDocumentNumber
     * 
     * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
     */
    public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber);

    /**
     * Gets the financialDocumentReversalDate
     * 
     * @return Returns the financialDocumentReversalDate
     */
    public Date getFinancialDocumentReversalDate();

    /**
     * Sets the financialDocumentReversalDate
     * 
     * @param financialDocumentReversalDate The financialDocumentReversalDate to set.
     */
    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate);

    /**
     * Gets the transactionEncumbranceUpdateCode
     * 
     * @return Returns the transactionEncumbranceUpdateCode
     */
    public String getTransactionEncumbranceUpdateCode();

    /**
     * Sets the transactionEncumbranceUpdateCode
     * 
     * @param transactionEncumbranceUpdateCode The transactionEncumbranceUpdateCode to set.
     */
    public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode);

    /**
     * Gets the transactionPostingDate
     * 
     * @return Returns the transactionPostingDate
     */
    public Date getTransactionPostingDate();

    /**
     * Sets the transactionPostingDate
     * 
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate);

    /**
     * Gets the payPeriodEndDate
     * 
     * @return Returns the payPeriodEndDate
     */
    public Date getPayPeriodEndDate();

    /**
     * Sets the payPeriodEndDate
     * 
     * @param payPeriodEndDate The payPeriodEndDate to set.
     */
    public void setPayPeriodEndDate(Date payPeriodEndDate);

    /**
     * Gets the transactionTotalHours
     * 
     * @return Returns the transactionTotalHours
     */
    public BigDecimal getTransactionTotalHours();

    /**
     * Sets the transactionTotalHours
     * 
     * @param transactionTotalHours The transactionTotalHours to set.
     */
    public void setTransactionTotalHours(BigDecimal transactionTotalHours);

    /**
     * Gets the payrollEndDateFiscalYear
     * 
     * @return Returns the payrollEndDateFiscalYear
     */
    public Integer getPayrollEndDateFiscalYear();

    /**
     * Sets the payrollEndDateFiscalYear
     * 
     * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
     */
    public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear);

    /**
     * Gets the payrollEndDateFiscalPeriodCode
     * 
     * @return Returns the payrollEndDateFiscalPeriodCode
     */
    public String getPayrollEndDateFiscalPeriodCode();

    /**
     * Sets the payrollEndDateFiscalPeriodCode
     * 
     * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
     */
    public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode);

    /**
     * Gets the emplid
     * 
     * @return Returns the emplid
     */
    public String getEmplid();

    /**
     * Sets the emplid
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid);

    /**
     * Gets the employeeRecord
     * 
     * @return Returns the employeeRecord
     */
    public Integer getEmployeeRecord();

    /**
     * Sets the employeeRecord
     * 
     * @param employeeRecord The employeeRecord to set.
     */
    public void setEmployeeRecord(Integer employeeRecord);

    /**
     * Gets the earnCode
     * 
     * @return Returns the earnCode
     */
    public String getEarnCode();

    /**
     * Sets the earnCode
     * 
     * @param earnCode The earnCode to set.
     */
    public void setEarnCode(String earnCode);

    /**
     * Gets the payGroup
     * 
     * @return Returns the payGroup
     */
    public String getPayGroup();

    /**
     * Sets the payGroup
     * 
     * @param payGroup The payGroup to set.
     */
    public void setPayGroup(String payGroup);

    /**
     * Gets the salaryAdministrationPlan
     * 
     * @return Returns the salaryAdministrationPlan
     */
    public String getSalaryAdministrationPlan();

    /**
     * Sets the salaryAdministrationPlan
     * 
     * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
     */
    public void setSalaryAdministrationPlan(String salaryAdministrationPlan);

    /**
     * Gets the grade
     * 
     * @return Returns the grade
     */
    public String getGrade();

    /**
     * Sets the grade
     * 
     * @param grade The grade to set.
     */
    public void setGrade(String grade);

    /**
     * Gets the runIdentifier
     * 
     * @return Returns the runIdentifier
     */
    public String getRunIdentifier();

    /**
     * Sets the runIdentifier
     * 
     * @param runIdentifier The runIdentifier to set.
     */
    public void setRunIdentifier(String runIdentifier);

    /**
     * Gets the laborLedgerOriginalChartOfAccountsCode
     * 
     * @return Returns the laborLedgerOriginalChartOfAccountsCode
     */
    public String getLaborLedgerOriginalChartOfAccountsCode();

    /**
     * Sets the laborLedgerOriginalChartOfAccountsCode
     * 
     * @param laborLedgerOriginalChartOfAccountsCode The laborLedgerOriginalChartOfAccountsCode to set.
     */
    public void setLaborLedgerOriginalChartOfAccountsCode(String laborLedgerOriginalChartOfAccountsCode);

    /**
     * Gets the laborLedgerOriginalAccountNumber
     * 
     * @return Returns the laborLedgerOriginalAccountNumber
     */
    public String getLaborLedgerOriginalAccountNumber();

    /**
     * Sets the laborLedgerOriginalAccountNumber
     * 
     * @param laborLedgerOriginalAccountNumber The laborLedgerOriginalAccountNumber to set.
     */
    public void setLaborLedgerOriginalAccountNumber(String laborLedgerOriginalAccountNumber);

    /**
     * Gets the laborLedgerOriginalSubAccountNumber
     * 
     * @return Returns the laborLedgerOriginalSubAccountNumber
     */
    public String getLaborLedgerOriginalSubAccountNumber();

    /**
     * Sets the laborLedgerOriginalSubAccountNumber
     * 
     * @param laborLedgerOriginalSubAccountNumber The laborLedgerOriginalSubAccountNumber to set.
     */
    public void setLaborLedgerOriginalSubAccountNumber(String laborLedgerOriginalSubAccountNumber);

    /**
     * Gets the laborLedgerOriginalFinancialObjectCode
     * 
     * @return Returns the laborLedgerOriginalFinancialObjectCode
     */
    public String getLaborLedgerOriginalFinancialObjectCode();

    /**
     * Sets the laborLedgerOriginalFinancialObjectCode
     * 
     * @param laborLedgerOriginalFinancialObjectCode The laborLedgerOriginalFinancialObjectCode to set.
     */
    public void setLaborLedgerOriginalFinancialObjectCode(String laborLedgerOriginalFinancialObjectCode);

    /**
     * Gets the laborLedgerOriginalFinancialSubObjectCode
     * 
     * @return Returns the laborLedgerOriginalFinancialSubObjectCode
     */
    public String getLaborLedgerOriginalFinancialSubObjectCode();

    /**
     * Sets the laborLedgerOriginalFinancialSubObjectCode
     * 
     * @param laborLedgerOriginalFinancialSubObjectCode The laborLedgerOriginalFinancialSubObjectCode to set.
     */
    public void setLaborLedgerOriginalFinancialSubObjectCode(String laborLedgerOriginalFinancialSubObjectCode);

    /**
     * Gets the hrmsCompany
     * 
     * @return Returns the hrmsCompany
     */
    public String getHrmsCompany();

    /**
     * Sets the hrmsCompany
     * 
     * @param hrmsCompany The hrmsCompany to set.
     */
    public void setHrmsCompany(String hrmsCompany);

    /**
     * Gets the setid
     * 
     * @return Returns the setid
     */
    public String getSetid();

    /**
     * Sets the setid
     * 
     * @param setid The setid to set.
     */
    public void setSetid(String setid);

    /**
     * Gets the transactionDateTimeStamp
     * 
     * @return Returns the transactionDateTimeStamp
     */
    public Timestamp getTransactionDateTimeStamp();

    /**
     * Sets the transactionDateTimeStamp
     * 
     * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
     */
    public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp);

    /**
     * Gets the financialObject
     * 
     * @return Returns the financialObject
     */
    public ObjectCode getFinancialObject();

    /**
     * Sets the financialObject
     * 
     * @param financialObject The financialObject to set.
     */
    @Deprecated
    public void setFinancialObject(ObjectCode financialObject);

    /**
     * Gets the chartOfAccounts
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts();

    /**
     * Sets the chartOfAccounts
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts);

    /**
     * Gets the account
     * 
     * @return Returns the account
     */
    public Account getAccount();

    /**
     * Sets the account
     * 
     * @param account The account to set.
     */
    @Deprecated
    public void setAccount(Account account);

    /**
     * Gets the universityFiscalPeriod
     * 
     * @return Returns the universityFiscalPeriod.
     */
    public AccountingPeriod getUniversityFiscalPeriod();

    /**
     * Sets the universityFiscalPeriod
     * 
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     */
    @Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod);

    /**
     * Gets the balanceType
     * 
     * @return Returns the balanceType.
     */
    public BalanceTyp getBalanceType();

    /**
     * Sets the balanceType
     * 
     * @param balanceType The balanceType to set.
     */
    @Deprecated
    public void setBalanceType(BalanceTyp balanceType);

    /**
     * Gets the documentType
     * 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType();

    /**
     * Sets the documentType
     * 
     * @param documentType The documentType to set.
     */
    @Deprecated
    public void setDocumentType(DocumentType documentType);

    /**
     * Gets the financialObjectType
     * 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType();

    /**
     * Sets the financialObjectType
     * 
     * @param financialObjectType The financialObjectType to set.
     */
    @Deprecated
    public void setFinancialObjectType(ObjectType financialObjectType);

    /**
     * Gets the financialSubObject
     * 
     * @return Returns the financialSubObject.
     */
    public SubObjCd getFinancialSubObject();

    /**
     * Sets the financialSubObject
     * 
     * @param financialSubObject The financialSubObject to set.
     */
    @Deprecated
    public void setFinancialSubObject(SubObjCd financialSubObject);

    /**
     * Gets the option
     * 
     * @return Returns the option.
     */
    public Options getOption();

    /**
     * Sets the option
     * 
     * @param option The option to set.
     */
    @Deprecated
    public void setOption(Options option);

    /**
     * Gets the payrollEndDateFiscalPeriod
     * 
     * @return Returns the payrollEndDateFiscalPeriod.
     */
    public AccountingPeriod getPayrollEndDateFiscalPeriod();

    /**
     * Sets the payrollEndDateFiscalPeriod
     * 
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     */
    @Deprecated
    public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod);

    /**
     * Gets the project
     * 
     * @return Returns the project.
     */
    public ProjectCode getProject();

    /**
     * Sets the project
     * 
     * @param project The project to set.
     */
    @Deprecated
    public void setProject(ProjectCode project);

    /**
     * Gets the referenceDocumentType
     * 
     * @return Returns the referenceDocumentType.
     */
    public DocumentType getReferenceDocumentType();

    /**
     * Sets the referenceDocumentType
     * 
     * @param referenceDocumentType The referenceDocumentType to set.
     */
    @Deprecated
    public void setReferenceDocumentType(DocumentType referenceDocumentType);

    /**
     * Gets the referenceOriginationCode
     * 
     * @return Returns the referenceOriginationCode.
     */
    public OriginationCode getReferenceOriginationCode();

    /**
     * Sets the referenceOriginationCode
     * 
     * @param referenceOriginationCode The referenceOriginationCode to set.
     */
    @Deprecated
    public void setReferenceOriginationCode(OriginationCode referenceOriginationCode);

    /**
     * Gets the subAccount
     * 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount();

    /**
     * Sets the subAccount
     * 
     * @param subAccount The subAccount to set.
     */
    @Deprecated
    public void setSubAccount(SubAccount subAccount);

    /**
     * Gets the financialSystemOrigination
     * 
     * @return Returns the financialSystemOrigination.
     */
    public OriginationCode getFinancialSystemOrigination();

    /**
     * Sets the financialSystemOrigination
     * 
     * @param financialSystemOrigination The financialSystemOrigination to set.
     */
    @Deprecated
    public void setFinancialSystemOrigination(OriginationCode financialSystemOrigination);

    /**
     * Gets the laborObject
     * 
     * @return Returns the laborObject.
     */
    public LaborLedgerObject getLaborLedgerObject();

    /**
     * Sets the laborLedgerObject
     * 
     * @param laborLedgerObject The laborLedgerObject to set.
     */
    public void setLaborLedgerObject(LaborLedgerObject laborLedgerObject);

}
