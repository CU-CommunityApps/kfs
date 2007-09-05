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

package org.kuali.module.labor.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.UniversityDate;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class LaborLedgerPendingEntry extends GeneralLedgerPendingEntry implements LaborTransaction{

    private String financialSystemOriginationCode;
    private String documentNumber;
	private Integer transactionLedgerEntrySequenceNumber;
	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String subAccountNumber;
	private String financialObjectCode;
	private String financialSubObjectCode;
	private String financialBalanceTypeCode;
	private String financialObjectTypeCode;
	private String universityFiscalPeriodCode;
	private String financialDocumentTypeCode;
	private String positionNumber;
	private String projectCode;
	private String transactionLedgerEntryDescription;
	private KualiDecimal transactionLedgerEntryAmount;
	private String transactionDebitCreditCode;
	private Date transactionDate;
	private String organizationDocumentNumber;
	private String organizationReferenceId;
	private String referenceFinancialDocumentTypeCode;
	private String referenceFinancialSystemOriginationCode;
	private String referenceFinancialDocumentNumber;
	private Date financialDocumentReversalDate;
	private String transactionEncumbranceUpdateCode;
	private Date transactionPostingDate;
	private Date payPeriodEndDate;
	private BigDecimal transactionTotalHours;
	private Integer payrollEndDateFiscalYear;
	private String payrollEndDateFiscalPeriodCode;
	private String financialDocumentApprovedCode;
	private String transactionEntryOffsetCode;
	private Date transactionEntryProcessedTimestamp;
	private String emplid;
	private Integer employeeRecord;
	private String earnCode;
	private String payGroup;
	private String salaryAdministrationPlan;
	private String grade;
	private String runIdentifier;
	private String laborLedgerOriginalChartOfAccountsCode;
	private String laborLedgerOriginalAccountNumber;
	private String laborLedgerOriginalSubAccountNumber;
	private String laborLedgerOriginalFinancialObjectCode;
	private String laborLedgerOriginalFinancialSubObjectCode;
	private String hrmsCompany;
	private String setid;
	private Timestamp transactionDateTimeStamp;

    private ObjectCode financialObject;
	private Account account;
	private Chart chartOfAccounts;
    private ObjectType financialObjectType;
    private BalanceTyp balanceType;
    private AccountingPeriod universityFiscalPeriod;
    private DocumentType documentType;
    private SubAccount subAccount;
    private SubObjCd financialSubObject;
    private ProjectCode project;  
    private DocumentHeader documentHeader;
    private DocumentType referenceFinancialDocumentType;
    private OriginationCode referenceFinancialSystemOrigination;
    private AccountingPeriod payrollEndDateFiscalPeriod;
    private Options option;
    private UniversityDate reversalDate;
    private OriginationCode financialSystemOrigination;    
    
    @Deprecated
    private DocumentHeader financialDocument;

    /**
	 * Default constructor.
	 */
	public LaborLedgerPendingEntry() {
	    super();
	}

    /**
     * Gets the financialSystemOriginationCode attribute. 
     * @return Returns the financialSystemOriginationCode.
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute value.
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }    
    
	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the transactionLedgerEntrySequenceNumber attribute.
	 * 
	 * @return Returns the transactionLedgerEntrySequenceNumber
	 * 
	 */
	public Integer getTransactionLedgerEntrySequenceNumber() { 
		return transactionLedgerEntrySequenceNumber;
	}

	/**
	 * Sets the transactionLedgerEntrySequenceNumber attribute.
	 * 
	 * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
	 * 
	 */
	public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
		this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
	}


	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}


	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the financialSubObjectCode attribute.
	 * 
	 * @return Returns the financialSubObjectCode
	 * 
	 */
	public String getFinancialSubObjectCode() { 
		return financialSubObjectCode;
	}

	/**
	 * Sets the financialSubObjectCode attribute.
	 * 
	 * @param financialSubObjectCode The financialSubObjectCode to set.
	 * 
	 */
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}


	/**
	 * Gets the financialBalanceTypeCode attribute.
	 * 
	 * @return Returns the financialBalanceTypeCode
	 * 
	 */
	public String getFinancialBalanceTypeCode() { 
		return financialBalanceTypeCode;
	}

	/**
	 * Sets the financialBalanceTypeCode attribute.
	 * 
	 * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
	 * 
	 */
	public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
		this.financialBalanceTypeCode = financialBalanceTypeCode;
	}


	/**
	 * Gets the financialObjectTypeCode attribute.
	 * 
	 * @return Returns the financialObjectTypeCode
	 * 
	 */
	public String getFinancialObjectTypeCode() { 
		return financialObjectTypeCode;
	}

	/**
	 * Sets the financialObjectTypeCode attribute.
	 * 
	 * @param financialObjectTypeCode The financialObjectTypeCode to set.
	 * 
	 */
	public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
		this.financialObjectTypeCode = financialObjectTypeCode;
	}


	/**
	 * Gets the universityFiscalPeriodCode attribute.
	 * 
	 * @return Returns the universityFiscalPeriodCode
	 * 
	 */
	public String getUniversityFiscalPeriodCode() { 
		return universityFiscalPeriodCode;
	}

	/**
	 * Sets the universityFiscalPeriodCode attribute.
	 * 
	 * @param universityFiscalPeriodCode The universityFiscalPeriodCode to set.
	 * 
	 */
	public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
		this.universityFiscalPeriodCode = universityFiscalPeriodCode;
	}


	/**
	 * Gets the financialDocumentTypeCode attribute.
	 * 
	 * @return Returns the financialDocumentTypeCode
	 * 
	 */
	public String getFinancialDocumentTypeCode() { 
		return financialDocumentTypeCode;
	}

	/**
	 * Sets the financialDocumentTypeCode attribute.
	 * 
	 * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
	 * 
	 */
	public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
		this.financialDocumentTypeCode = financialDocumentTypeCode;
	}


	/**
	 * Gets the positionNumber attribute.
	 * 
	 * @return Returns the positionNumber
	 * 
	 */
	public String getPositionNumber() { 
		return positionNumber;
	}

	/**
	 * Sets the positionNumber attribute.
	 * 
	 * @param positionNumber The positionNumber to set.
	 * 
	 */
	public void setPositionNumber(String positionNumber) {
		this.positionNumber = positionNumber;
	}


	/**
	 * Gets the projectCode attribute.
	 * 
	 * @return Returns the projectCode
	 * 
	 */
	public String getProjectCode() { 
		return projectCode;
	}

	/**
	 * Sets the projectCode attribute.
	 * 
	 * @param projectCode The projectCode to set.
	 * 
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * Gets the transactionLedgerEntryDescription attribute.
	 * 
	 * @return Returns the transactionLedgerEntryDescription
	 * 
	 */
	public String getTransactionLedgerEntryDescription() { 
		return transactionLedgerEntryDescription;
	}

	/**
	 * Sets the transactionLedgerEntryDescription attribute.
	 * 
	 * @param transactionLedgerEntryDescription The transactionLedgerEntryDescription to set.
	 * 
	 */
	public void setTransactionLedgerEntryDescription(String transactionLedgerEntryDescription) {
		this.transactionLedgerEntryDescription = transactionLedgerEntryDescription;
	}


	/**
	 * Gets the transactionLedgerEntryAmount attribute.
	 * 
	 * @return Returns the transactionLedgerEntryAmount
	 * 
	 */
	public KualiDecimal getTransactionLedgerEntryAmount() { 
		return transactionLedgerEntryAmount;
	}

	/**
	 * Sets the transactionLedgerEntryAmount attribute.
	 * 
	 * @param transactionLedgerEntryAmount The transactionLedgerEntryAmount to set.
	 * 
	 */
	public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
		this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
	}


	/**
	 * Gets the transactionDebitCreditCode attribute.
	 * 
	 * @return Returns the transactionDebitCreditCode
	 * 
	 */
	public String getTransactionDebitCreditCode() { 
		return transactionDebitCreditCode;
	}

	/**
	 * Sets the transactionDebitCreditCode attribute.
	 * 
	 * @param transactionDebitCreditCode The transactionDebitCreditCode to set.
	 * 
	 */
	public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
		this.transactionDebitCreditCode = transactionDebitCreditCode;
	}


	/**
	 * Gets the transactionDate attribute.
	 * 
	 * @return Returns the transactionDate
	 * 
	 */
	public Date getTransactionDate() { 
		return transactionDate;
	}

	/**
	 * Sets the transactionDate attribute.
	 * 
	 * @param transactionDate The transactionDate to set.
	 * 
	 */
	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}


	/**
	 * Gets the organizationDocumentNumber attribute.
	 * 
	 * @return Returns the organizationDocumentNumber
	 * 
	 */
	public String getOrganizationDocumentNumber() { 
		return organizationDocumentNumber;
	}

	/**
	 * Sets the organizationDocumentNumber attribute.
	 * 
	 * @param organizationDocumentNumber The organizationDocumentNumber to set.
	 * 
	 */
	public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
		this.organizationDocumentNumber = organizationDocumentNumber;
	}


	/**
	 * Gets the organizationReferenceId attribute.
	 * 
	 * @return Returns the organizationReferenceId
	 * 
	 */
	public String getOrganizationReferenceId() { 
		return organizationReferenceId;
	}

	/**
	 * Sets the organizationReferenceId attribute.
	 * 
	 * @param organizationReferenceId The organizationReferenceId to set.
	 * 
	 */
	public void setOrganizationReferenceId(String organizationReferenceId) {
		this.organizationReferenceId = organizationReferenceId;
	}


	/**
	 * Gets the referenceFinancialDocumentTypeCode attribute.
	 * 
	 * @return Returns the referenceFinancialDocumentTypeCode
	 * 
	 */
	public String getReferenceFinancialDocumentTypeCode() { 
		return referenceFinancialDocumentTypeCode;
	}

	/**
	 * Sets the referenceFinancialDocumentTypeCode attribute.
	 * 
	 * @param referenceFinancialDocumentTypeCode The referenceFinancialDocumentTypeCode to set.
	 * 
	 */
	public void setReferenceFinancialDocumentTypeCode(String referenceFinancialDocumentTypeCode) {
		this.referenceFinancialDocumentTypeCode = referenceFinancialDocumentTypeCode;
	}


	/**
	 * Gets the referenceFinancialSystemOriginationCode attribute.
	 * 
	 * @return Returns the referenceFinancialSystemOriginationCode
	 * 
	 */
	public String getReferenceFinancialSystemOriginationCode() { 
		return referenceFinancialSystemOriginationCode;
	}

	/**
	 * Sets the referenceFinancialSystemOriginationCode attribute.
	 * 
	 * @param referenceFinancialSystemOriginationCode The referenceFinancialSystemOriginationCode to set.
	 * 
	 */
	public void setReferenceFinancialSystemOriginationCode(String referenceFinancialSystemOriginationCode) {
		this.referenceFinancialSystemOriginationCode = referenceFinancialSystemOriginationCode;
	}


	/**
	 * Gets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @return Returns the referenceFinancialDocumentNumber
	 * 
	 */
	public String getReferenceFinancialDocumentNumber() { 
		return referenceFinancialDocumentNumber;
	}

	/**
	 * Sets the referenceFinancialDocumentNumber attribute.
	 * 
	 * @param referenceFinancialDocumentNumber The referenceFinancialDocumentNumber to set.
	 * 
	 */
	public void setReferenceFinancialDocumentNumber(String referenceFinancialDocumentNumber) {
		this.referenceFinancialDocumentNumber = referenceFinancialDocumentNumber;
	}


	/**
	 * Gets the financialDocumentReversalDate attribute.
	 * 
	 * @return Returns the financialDocumentReversalDate
	 * 
	 */
	public Date getFinancialDocumentReversalDate() { 
		return financialDocumentReversalDate;
	}

	/**
	 * Sets the financialDocumentReversalDate attribute.
	 * 
	 * @param financialDocumentReversalDate The financialDocumentReversalDate to set.
	 * 
	 */
	public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
		this.financialDocumentReversalDate = financialDocumentReversalDate;
	}


	/**
	 * Gets the transactionEncumbranceUpdateCode attribute.
	 * 
	 * @return Returns the transactionEncumbranceUpdateCode
	 * 
	 */
	public String getTransactionEncumbranceUpdateCode() { 
		return transactionEncumbranceUpdateCode;
	}

	/**
	 * Sets the transactionEncumbranceUpdateCode attribute.
	 * 
	 * @param transactionEncumbranceUpdateCode The transactionEncumbranceUpdateCode to set.
	 * 
	 */
	public void setTransactionEncumbranceUpdateCode(String transactionEncumbranceUpdateCode) {
		this.transactionEncumbranceUpdateCode = transactionEncumbranceUpdateCode;
	}


	/**
	 * Gets the transactionPostingDate attribute.
	 * 
	 * @return Returns the transactionPostingDate
	 * 
	 */
	public Date getTransactionPostingDate() { 
		return transactionPostingDate;
	}

	/**
	 * Sets the transactionPostingDate attribute.
	 * 
	 * @param transactionPostingDate The transactionPostingDate to set.
	 * 
	 */
	public void setTransactionPostingDate(Date transactionPostingDate) {
		this.transactionPostingDate = transactionPostingDate;
	}


	/**
	 * Gets the payPeriodEndDate attribute.
	 * 
	 * @return Returns the payPeriodEndDate
	 * 
	 */
	public Date getPayPeriodEndDate() { 
		return payPeriodEndDate;
	}

	/**
	 * Sets the payPeriodEndDate attribute.
	 * 
	 * @param payPeriodEndDate The payPeriodEndDate to set.
	 * 
	 */
	public void setPayPeriodEndDate(Date payPeriodEndDate) {
		this.payPeriodEndDate = payPeriodEndDate;
	}


	/**
	 * Gets the transactionTotalHours attribute.
	 * 
	 * @return Returns the transactionTotalHours
	 * 
	 */
	public BigDecimal getTransactionTotalHours() { 
		return transactionTotalHours;
	}

	/**
	 * Sets the transactionTotalHours attribute.
	 * 
	 * @param transactionTotalHours The transactionTotalHours to set.
	 * 
	 */
	public void setTransactionTotalHours(BigDecimal transactionTotalHours) {
		this.transactionTotalHours = transactionTotalHours;
	}


	/**
	 * Gets the payrollEndDateFiscalYear attribute.
	 * 
	 * @return Returns the payrollEndDateFiscalYear
	 * 
	 */
	public Integer getPayrollEndDateFiscalYear() { 
		return payrollEndDateFiscalYear;
	}

	/**
	 * Sets the payrollEndDateFiscalYear attribute.
	 * 
	 * @param payrollEndDateFiscalYear The payrollEndDateFiscalYear to set.
	 * 
	 */
	public void setPayrollEndDateFiscalYear(Integer payrollEndDateFiscalYear) {
		this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
	}


	/**
	 * Gets the payrollEndDateFiscalPeriodCode attribute.
	 * 
	 * @return Returns the payrollEndDateFiscalPeriodCode
	 * 
	 */
	public String getPayrollEndDateFiscalPeriodCode() { 
		return payrollEndDateFiscalPeriodCode;
	}

	/**
	 * Sets the payrollEndDateFiscalPeriodCode attribute.
	 * 
	 * @param payrollEndDateFiscalPeriodCode The payrollEndDateFiscalPeriodCode to set.
	 * 
	 */
	public void setPayrollEndDateFiscalPeriodCode(String payrollEndDateFiscalPeriodCode) {
		this.payrollEndDateFiscalPeriodCode = payrollEndDateFiscalPeriodCode;
	}


	/**
	 * Gets the financialDocumentApprovedCode attribute.
	 * 
	 * @return Returns the financialDocumentApprovedCode
	 * 
	 */
	public String getFinancialDocumentApprovedCode() { 
		return financialDocumentApprovedCode;
	}

	/**
	 * Sets the financialDocumentApprovedCode attribute.
	 * 
	 * @param financialDocumentApprovedCode The financialDocumentApprovedCode to set.
	 * 
	 */
	public void setFinancialDocumentApprovedCode(String financialDocumentApprovedCode) {
		this.financialDocumentApprovedCode = financialDocumentApprovedCode;
	}


	/**
	 * Gets the transactionEntryOffsetCode attribute.
	 * 
	 * @return Returns the transactionEntryOffsetCode
	 * 
	 */
	public String getTransactionEntryOffsetCode() { 
		return transactionEntryOffsetCode;
	}

	/**
	 * Sets the transactionEntryOffsetCode attribute.
	 * 
	 * @param transactionEntryOffsetCode The transactionEntryOffsetCode to set.
	 * 
	 */
	public void setTransactionEntryOffsetCode(String transactionEntryOffsetCode) {
		this.transactionEntryOffsetCode = transactionEntryOffsetCode;
	}


	/**
	 * Gets the transactionEntryProcessedTimestamp attribute.
	 * 
	 * @return Returns the transactionEntryProcessedTimestamp
	 * 
	 */
	public Date getTransactionEntryProcessedTimestamp() { 
		return transactionEntryProcessedTimestamp;
	}

	/**
	 * Sets the transactionEntryProcessedTimestamp attribute.
	 * 
	 * @param transactionEntryProcessedTimestamp The transactionEntryProcessedTimestamp to set.
	 * 
	 */
	public void setTransactionEntryProcessedTimestamp(Date transactionEntryProcessedTimestamp) {
		this.transactionEntryProcessedTimestamp = transactionEntryProcessedTimestamp;
	}


	/**
	 * Gets the emplid attribute.
	 * 
	 * @return Returns the emplid
	 * 
	 */
	public String getEmplid() { 
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 * 
	 * @param emplid The emplid to set.
	 * 
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}


	/**
	 * Gets the employeeRecord attribute.
	 * 
	 * @return Returns the employeeRecord
	 * 
	 */
	public Integer getEmployeeRecord() { 
		return employeeRecord;
	}

	/**
	 * Sets the employeeRecord attribute.
	 * 
	 * @param employeeRecord The employeeRecord to set.
	 * 
	 */
	public void setEmployeeRecord(Integer employeeRecord) {
		this.employeeRecord = employeeRecord;
	}


	/**
	 * Gets the earnCode attribute.
	 * 
	 * @return Returns the earnCode
	 * 
	 */
	public String getEarnCode() { 
		return earnCode;
	}

	/**
	 * Sets the earnCode attribute.
	 * 
	 * @param earnCode The earnCode to set.
	 * 
	 */
	public void setEarnCode(String earnCode) {
		this.earnCode = earnCode;
	}


	/**
	 * Gets the payGroup attribute.
	 * 
	 * @return Returns the payGroup
	 * 
	 */
	public String getPayGroup() { 
		return payGroup;
	}

	/**
	 * Sets the payGroup attribute.
	 * 
	 * @param payGroup The payGroup to set.
	 * 
	 */
	public void setPayGroup(String payGroup) {
		this.payGroup = payGroup;
	}


	/**
	 * Gets the salaryAdministrationPlan attribute.
	 * 
	 * @return Returns the salaryAdministrationPlan
	 * 
	 */
	public String getSalaryAdministrationPlan() { 
		return salaryAdministrationPlan;
	}

	/**
	 * Sets the salaryAdministrationPlan attribute.
	 * 
	 * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
	 * 
	 */
	public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
		this.salaryAdministrationPlan = salaryAdministrationPlan;
	}


	/**
	 * Gets the grade attribute.
	 * 
	 * @return Returns the grade
	 * 
	 */
	public String getGrade() { 
		return grade;
	}

	/**
	 * Sets the grade attribute.
	 * 
	 * @param grade The grade to set.
	 * 
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}


	/**
	 * Gets the runIdentifier attribute.
	 * 
	 * @return Returns the runIdentifier
	 * 
	 */
	public String getRunIdentifier() { 
		return runIdentifier;
	}

	/**
	 * Sets the runIdentifier attribute.
	 * 
	 * @param runIdentifier The runIdentifier to set.
	 * 
	 */
	public void setRunIdentifier(String runIdentifier) {
		this.runIdentifier = runIdentifier;
	}


	/**
	 * Gets the laborLedgerOriginalChartOfAccountsCode attribute.
	 * 
	 * @return Returns the laborLedgerOriginalChartOfAccountsCode
	 * 
	 */
	public String getLaborLedgerOriginalChartOfAccountsCode() { 
		return laborLedgerOriginalChartOfAccountsCode;
	}

	/**
	 * Sets the laborLedgerOriginalChartOfAccountsCode attribute.
	 * 
	 * @param laborLedgerOriginalChartOfAccountsCode The laborLedgerOriginalChartOfAccountsCode to set.
	 * 
	 */
	public void setLaborLedgerOriginalChartOfAccountsCode(String laborLedgerOriginalChartOfAccountsCode) {
		this.laborLedgerOriginalChartOfAccountsCode = laborLedgerOriginalChartOfAccountsCode;
	}


	/**
	 * Gets the laborLedgerOriginalAccountNumber attribute.
	 * 
	 * @return Returns the laborLedgerOriginalAccountNumber
	 * 
	 */
	public String getLaborLedgerOriginalAccountNumber() { 
		return laborLedgerOriginalAccountNumber;
	}

	/**
	 * Sets the laborLedgerOriginalAccountNumber attribute.
	 * 
	 * @param laborLedgerOriginalAccountNumber The laborLedgerOriginalAccountNumber to set.
	 * 
	 */
	public void setLaborLedgerOriginalAccountNumber(String laborLedgerOriginalAccountNumber) {
		this.laborLedgerOriginalAccountNumber = laborLedgerOriginalAccountNumber;
	}


	/**
	 * Gets the laborLedgerOriginalSubAccountNumber attribute.
	 * 
	 * @return Returns the laborLedgerOriginalSubAccountNumber
	 * 
	 */
	public String getLaborLedgerOriginalSubAccountNumber() { 
		return laborLedgerOriginalSubAccountNumber;
	}

	/**
	 * Sets the laborLedgerOriginalSubAccountNumber attribute.
	 * 
	 * @param laborLedgerOriginalSubAccountNumber The laborLedgerOriginalSubAccountNumber to set.
	 * 
	 */
	public void setLaborLedgerOriginalSubAccountNumber(String laborLedgerOriginalSubAccountNumber) {
		this.laborLedgerOriginalSubAccountNumber = laborLedgerOriginalSubAccountNumber;
	}


	/**
	 * Gets the laborLedgerOriginalFinancialObjectCode attribute.
	 * 
	 * @return Returns the laborLedgerOriginalFinancialObjectCode
	 * 
	 */
	public String getLaborLedgerOriginalFinancialObjectCode() { 
		return laborLedgerOriginalFinancialObjectCode;
	}

	/**
	 * Sets the laborLedgerOriginalFinancialObjectCode attribute.
	 * 
	 * @param laborLedgerOriginalFinancialObjectCode The laborLedgerOriginalFinancialObjectCode to set.
	 * 
	 */
	public void setLaborLedgerOriginalFinancialObjectCode(String laborLedgerOriginalFinancialObjectCode) {
		this.laborLedgerOriginalFinancialObjectCode = laborLedgerOriginalFinancialObjectCode;
	}


	/**
	 * Gets the laborLedgerOriginalFinancialSubObjectCode attribute.
	 * 
	 * @return Returns the laborLedgerOriginalFinancialSubObjectCode
	 * 
	 */
	public String getLaborLedgerOriginalFinancialSubObjectCode() { 
		return laborLedgerOriginalFinancialSubObjectCode;
	}

	/**
	 * Sets the laborLedgerOriginalFinancialSubObjectCode attribute.
	 * 
	 * @param laborLedgerOriginalFinancialSubObjectCode The laborLedgerOriginalFinancialSubObjectCode to set.
	 * 
	 */
	public void setLaborLedgerOriginalFinancialSubObjectCode(String laborLedgerOriginalFinancialSubObjectCode) {
		this.laborLedgerOriginalFinancialSubObjectCode = laborLedgerOriginalFinancialSubObjectCode;
	}


	/**
	 * Gets the hrmsCompany attribute.
	 * 
	 * @return Returns the hrmsCompany
	 * 
	 */
	public String getHrmsCompany() { 
		return hrmsCompany;
	}

	/**
	 * Sets the hrmsCompany attribute.
	 * 
	 * @param hrmsCompany The hrmsCompany to set.
	 * 
	 */
	public void setHrmsCompany(String hrmsCompany) {
		this.hrmsCompany = hrmsCompany;
	}


	/**
	 * Gets the setid attribute.
	 * 
	 * @return Returns the setid
	 * 
	 */
	public String getSetid() { 
		return setid;
	}

	/**
	 * Sets the setid attribute.
	 * 
	 * @param setid The setid to set.
	 * 
	 */
	public void setSetid(String setid) {
		this.setid = setid;
	}


	/**
	 * Gets the transactionDateTimeStamp attribute.
	 * 
	 * @return Returns the transactionDateTimeStamp
	 * 
	 */
	public Timestamp getTransactionDateTimeStamp() { 
		return transactionDateTimeStamp;
	}

	/**
	 * Sets the transactionDateTimeStamp attribute.
	 * 
	 * @param transactionDateTimeStamp The transactionDateTimeStamp to set.
	 * 
	 */
	public void setTransactionDateTimeStamp(Timestamp transactionDateTimeStamp) {
		this.transactionDateTimeStamp = transactionDateTimeStamp;
	}


	/**
	 * Gets the financialObject attribute.
	 * 
	 * @return Returns the financialObject
	 * 
	 */
	public ObjectCode getFinancialObject() { 
		return financialObject;
	}

	/**
	 * Sets the financialObject attribute.
	 * 
	 * @param financialObject The financialObject to set.
	 */
    @Deprecated
	public void setFinancialObject(ObjectCode financialObject) {
		this.financialObject = financialObject;
	}

	/**
	 * Gets the account attribute.
	 * 
	 * @return Returns the account
	 * 
	 */
	public Account getAccount() { 
		return account;
	}

	/**
	 * Sets the account attribute.
	 * 
	 * @param account The account to set.
	 */
    @Deprecated
	public void setAccount(Account account) {
		this.account = account;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 */
    @Deprecated
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

    /**
     * Gets the balanceType attribute. 
     * @return Returns the balanceType.
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * Sets the balanceType attribute value.
     * @param balanceType The balanceType to set.
     */
    @Deprecated
    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the documentType attribute. 
     * @return Returns the documentType.
     */
    public DocumentType getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * @param documentType The documentType to set.
     */
    @Deprecated
    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    /**
     * Gets the financialDocument attribute. 
     * @return Returns the financialDocument.
     */
    @Deprecated
    public DocumentHeader getFinancialDocument() {
        return financialDocument;
    }

    /**
     * Sets the financialDocument attribute value.
     * @param financialDocument The financialDocument to set.
     */
    @Deprecated
    public void setFinancialDocument(DocumentHeader financialDocument) {
        this.financialDocument = financialDocument;
    }

    /**
     * Gets the financialObjectType attribute. 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute value.
     * @param financialObjectType The financialObjectType to set.
     */
    @Deprecated
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    /**
     * Gets the financialSubObject attribute. 
     * @return Returns the financialSubObject.
     */
    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    /**
     * Sets the financialSubObject attribute value.
     * @param financialSubObject The financialSubObject to set.
     */
    @Deprecated
    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    /**
     * Gets the option attribute. 
     * @return Returns the option.
     */
    public Options getOption() {
        return option;
    }

    /**
     * Sets the option attribute value.
     * @param option The option to set.
     */
    @Deprecated
    public void setOption(Options option) {
        this.option = option;
    }

    /**
     * Gets the payrollEndDateFiscalPeriod attribute. 
     * @return Returns the payrollEndDateFiscalPeriod.
     */
    public AccountingPeriod getPayrollEndDateFiscalPeriod() {
        return payrollEndDateFiscalPeriod;
    }

    /**
     * Sets the payrollEndDateFiscalPeriod attribute value.
     * @param payrollEndDateFiscalPeriod The payrollEndDateFiscalPeriod to set.
     */
    @Deprecated
    public void setPayrollEndDateFiscalPeriod(AccountingPeriod payrollEndDateFiscalPeriod) {
        this.payrollEndDateFiscalPeriod = payrollEndDateFiscalPeriod;
    }

    /**
     * Gets the project attribute. 
     * @return Returns the project.
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * Sets the project attribute value.
     * @param project The project to set.
     */
    @Deprecated
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * Gets the referenceFinancialDocumentType attribute. 
     * @return Returns the referenceFinancialDocumentType.
     */
    public DocumentType getReferenceFinancialDocumentType() {
        return referenceFinancialDocumentType;
    }

    /**
     * Sets the referenceFinancialDocumentType attribute value.
     * @param referenceFinancialDocumentType The referenceFinancialDocumentType to set.
     */
    @Deprecated
    public void setReferenceFinancialDocumentType(DocumentType referenceFinancialDocumentType) {
        this.referenceFinancialDocumentType = referenceFinancialDocumentType;
    }

    /**
     * Gets the referenceFinancialSystemOrigination attribute. 
     * @return Returns the referenceFinancialSystemOrigination.
     */
    public OriginationCode getReferenceFinancialSystemOrigination() {
        return referenceFinancialSystemOrigination;
    }

    /**
     * Sets the referenceFinancialSystemOrigination attribute value.
     * @param referenceFinancialSystemOrigination The referenceFinancialSystemOrigination to set.
     */
    @Deprecated
    public void setReferenceFinancialSystemOrigination(OriginationCode referenceFinancialSystemOrigination) {
        this.referenceFinancialSystemOrigination = referenceFinancialSystemOrigination;
    }

    /**
     * Gets the reversalDate attribute. 
     * @return Returns the reversalDate.
     */
    public UniversityDate getReversalDate() {
        return reversalDate;
    }

    /**
     * Sets the reversalDate attribute value.
     * @param reversalDate The reversalDate to set.
     */
    @Deprecated
    public void setReversalDate(UniversityDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     */
    @Deprecated
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the universityFiscalPeriod attribute. 
     * @return Returns the universityFiscalPeriod.
     */
    public AccountingPeriod getUniversityFiscalPeriod() {
        return universityFiscalPeriod;
    }

    /**
     * Sets the universityFiscalPeriod attribute value.
     * @param universityFiscalPeriod The universityFiscalPeriod to set.
     */
    @Deprecated
    public void setUniversityFiscalPeriod(AccountingPeriod universityFiscalPeriod) {
        this.universityFiscalPeriod = universityFiscalPeriod;
    }

    /**
     * Gets the financialSystemOrigination attribute. 
     * @return Returns the financialSystemOrigination.
     */
    public OriginationCode getFinancialSystemOrigination() {
        return financialSystemOrigination;
    }

    /**
     * Sets the financialSystemOrigination attribute value.
     * @param financialSystemOrigination The financialSystemOrigination to set.
     */
    @Deprecated
    public void setFinancialSystemOrigination(OriginationCode financialSystemOrigination) {
        this.financialSystemOrigination = financialSystemOrigination;
    }    
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.transactionLedgerEntrySequenceNumber != null) {
            m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        }
        return m;
    }

    //Helper Methods (as in GL)
    public boolean isSubAccountNumberBlank() {
        return subAccountNumber == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankSubAccountNumber().equals(subAccountNumber);
    }

    public boolean isFinancialObjectCodeBlank() {
        return financialObjectCode == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode().equals(financialObjectCode);
    }

    public boolean isFinancialSubObjectCodeBlank() {
        return financialSubObjectCode == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode().equals(financialSubObjectCode);
    }

    public boolean isProjectCodeBlank() {
        return projectCode == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankProjectCode().equals(projectCode);
    }

    public boolean isFinancialObjectTypeCodeBlank() {
        return financialObjectTypeCode == null || GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType().equals(financialObjectTypeCode);
    }

    /**
     * Gets the documentHeader attribute. 
     * @return Returns the documentHeader.
     */
    public DocumentHeader getDocumentHeader() {
        return documentHeader;
    }

    /**
     * Sets the documentHeader attribute value.
     * @param documentHeader The documentHeader to set.
     */
    public void setDocumentHeader(DocumentHeader documentHeader) {
        this.documentHeader = documentHeader;
    }
}
