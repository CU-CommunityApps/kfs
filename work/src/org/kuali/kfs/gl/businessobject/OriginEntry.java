/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.bo;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.OriginationCode;
import org.kuali.core.bo.user.Options;
import org.kuali.core.document.DocumentType;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * @author jsissom
 * 
 */
public class OriginEntry extends BusinessObjectBase implements Transaction {
    static final long serialVersionUID = -2498312988235747448L;

    private Integer entryId;
    private Integer entryGroupId;
    private String accountNumber;
    private String financialDocumentNumber;
    private String financialDocumentReferenceNbr;
    private String referenceFinDocumentTypeCode;
    private Date financialDocumentReversalDate;
    private String financialDocumentTypeCode;
    private String financialBalanceTypeCode;
    private String chartOfAccountsCode;
    private String financialObjectTypeCode;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialSystemOriginationCode;
    private String finSystemRefOriginationCode;
    private String organizationDocumentNumber;
    private String organizationReferenceId;
    private String projectCode;
    private String subAccountNumber;
    private Date transactionDate;
    private String transactionDebitCreditCode;
    private String transactionEncumbranceUpdtCd;
    private Integer trnEntryLedgerSequenceNumber;
    private KualiDecimal transactionLedgerEntryAmount;
    private String transactionLedgerEntryDesc;
    private String universityFiscalPeriodCode;
    private Integer universityFiscalYear;
    private String budgetYear;
    
    // bo references
    private OriginEntryGroup group;
    private Account account;
    private SubAccount subAccount;
    private A21SubAccount a21SubAccount;
    private BalanceTyp balanceType;
    private Chart chart;
    private ObjectCode financialObject;
    private SubObjCd financialSubObject;
    private ObjectType objectType;
    private ProjectCode project;
    private DocumentType documentType;
    private UniversityDate universityDate;
    private Options option;
    private AccountingPeriod accountingPeriod;
    private UniversityDate reversalDate;
    private OriginationCode origination;
    private DocumentType referenceDocumentType;

    /**
     * 
     */
    public OriginEntry() {
        super();
    }

    public OriginEntry(Transaction t) {
        super();
        setTransaction(t);
    }

    public OriginEntry(String line) {
      setFromTextFile(line);
    }

    public void setTransaction(Transaction t) {
        setAccountNumber(t.getAccountNumber());
        setFinancialDocumentNumber(t.getFinancialDocumentNumber());
        setFinancialDocumentReferenceNbr(t.getFinancialDocumentReferenceNbr());
        setReferenceFinDocumentTypeCode(t.getReferenceFinDocumentTypeCode());
        setFinancialDocumentReversalDate(t.getFinancialDocumentReversalDate());
        setFinancialDocumentTypeCode(t.getFinancialDocumentTypeCode());
        setFinancialBalanceTypeCode(t.getFinancialBalanceTypeCode());
        setChartOfAccountsCode(t.getChartOfAccountsCode());
        setFinancialObjectTypeCode(t.getFinancialObjectTypeCode());
        setFinancialObjectCode(t.getFinancialObjectCode());
        setFinancialSubObjectCode(t.getFinancialSubObjectCode());
        setFinancialSystemOriginationCode(t.getFinancialSystemOriginationCode());
        setFinSystemRefOriginationCode(t.getFinSystemRefOriginationCode());
        setOrganizationDocumentNumber(t.getOrganizationDocumentNumber());
        setOrganizationReferenceId(t.getOrganizationReferenceId());
        setProjectCode(t.getProjectCode());
        setSubAccountNumber(t.getSubAccountNumber());
        setTransactionDate(t.getTransactionDate());
        setTransactionDebitCreditCode(t.getTransactionDebitCreditCode());
        setTransactionEncumbranceUpdtCd(t.getTransactionEncumbranceUpdtCd());
        setTrnEntryLedgerSequenceNumber(t.getTrnEntryLedgerSequenceNumber());
        setTransactionLedgerEntryAmount(t.getTransactionLedgerEntryAmount());
        setTransactionLedgerEntryDesc(t.getTransactionLedgerEntryDesc());
        setUniversityFiscalPeriodCode(t.getUniversityFiscalPeriodCode());
        setUniversityFiscalYear(t.getUniversityFiscalYear());
    }

    private java.sql.Date parseDate(String sdate, boolean beLenientWithDates) {
      if ((sdate == null) || (sdate.trim().length() == 0)) {
        return null;
      } else {
          
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(beLenientWithDates);
        
        try {
          java.util.Date d = sdf.parse(sdate);
          return new Date(d.getTime());
        } catch (ParseException e) {
          return null;
        }
      }
    }

    private String formatDate(Date date) {
      if ( date == null ) {
        return "          ";
      } else {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
      }
    }
 
    public void setFromTextFile(String line) {

      // Just in case
      line = line + "                                                                                           ";

      if (!"    ".equals(line.substring(0, 4))) {
          setUniversityFiscalYear(new Integer(line.substring(0, 4)));
      } else {
          setUniversityFiscalYear(null);
      }
      setChartOfAccountsCode(line.substring(4, 6).trim());
      setAccountNumber(line.substring(6, 13).trim());
      setSubAccountNumber(line.substring(13, 18).trim());
      setFinancialObjectCode(line.substring(18, 22).trim());
      setFinancialSubObjectCode(line.substring(22, 25).trim());
      setFinancialBalanceTypeCode(line.substring(25, 27).trim());
      setFinancialObjectTypeCode(line.substring(27, 29).trim());
      setUniversityFiscalPeriodCode(line.substring(29, 31).trim());
      setFinancialDocumentTypeCode(line.substring(31, 35).trim());
      setFinancialSystemOriginationCode(line.substring(35, 37).trim());
      setFinancialDocumentNumber(line.substring(37, 46).trim());
      if (!"     ".equals(line.substring(46, 51))) {
          setTrnEntryLedgerSequenceNumber(new Integer(line.substring(46, 51)));
      } else {
          setTrnEntryLedgerSequenceNumber(null);
      }
      setTransactionLedgerEntryDesc(line.substring(51, 91).trim());
      setTransactionLedgerEntryAmount(new KualiDecimal(line.substring(91, 108).trim()));
      setTransactionDebitCreditCode(line.substring(108, 109));
      setTransactionDate(parseDate(line.substring(109, 119).trim(), false));
      setOrganizationDocumentNumber(line.substring(119, 129).trim());
      setProjectCode(line.substring(129, 139).trim());
      setOrganizationReferenceId(line.substring(139, 147).trim());
      setReferenceFinDocumentTypeCode(line.substring(147, 151).trim());
      setFinSystemRefOriginationCode(line.substring(151, 153).trim());
      setFinancialDocumentReferenceNbr(line.substring(153, 162).trim());
      setFinancialDocumentReversalDate(parseDate(line.substring(162, 172), true));
      setTransactionEncumbranceUpdtCd(line.substring(172, 173));
  }

    private static String SPACES = "                                                                                                              ";

    private String getField(int size,String value) {
      if ( value == null ) {
        return SPACES.substring(0,size);
      } else {
        if ( value.length() < size) {
          return value + SPACES.substring(0,size - value.length());
        } else {
          return value;
        }
      }
    }

    public String getLine() {
      StringBuffer sb = new StringBuffer();
      if ( universityFiscalYear == null ) {
        sb.append("    ");
      } else {
        sb.append(universityFiscalYear);
      }

      sb.append(getField(2,chartOfAccountsCode));
      sb.append(getField(7,accountNumber));
      sb.append(getField(5,subAccountNumber));
      sb.append(getField(4,financialObjectCode));
      sb.append(getField(3,financialSubObjectCode));
      sb.append(getField(2,financialBalanceTypeCode));
      sb.append(getField(2,financialObjectTypeCode));
      sb.append(getField(2,universityFiscalPeriodCode));
      sb.append(getField(4,financialDocumentTypeCode));
      sb.append(getField(2,financialSystemOriginationCode));
      sb.append(getField(9,financialDocumentNumber));
      if ( trnEntryLedgerSequenceNumber == null ) {
        sb.append("     ");
      } else {
          // Format to a length of 5
          String seqNum = trnEntryLedgerSequenceNumber.toString();
          for(int i = 0; i < 8 - seqNum.length(); i++) {
              seqNum = "0" + seqNum;
          }
          sb.append(seqNum);
      }
      sb.append(getField(40,transactionLedgerEntryDesc));
      if ( transactionLedgerEntryAmount == null ) {
        sb.append("                 ");
      } else {
        String a = transactionLedgerEntryAmount.toString();
        sb.append("                 ".substring(0,17 - a.length()));
        sb.append(a);
      }
      sb.append(getField(1,transactionDebitCreditCode));
      sb.append(formatDate(transactionDate));
      sb.append(getField(10,organizationDocumentNumber));
      sb.append(getField(10,projectCode));
      sb.append(getField(8,organizationReferenceId));
      sb.append(getField(4,referenceFinDocumentTypeCode));
      sb.append(getField(2,finSystemRefOriginationCode));
      sb.append(getField(9,financialDocumentReferenceNbr));
      sb.append(formatDate(financialDocumentReversalDate));
      sb.append(getField(1,transactionEncumbranceUpdtCd));
      return sb.toString();
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("entryId", entryId);
        map.put("universityFiscalYear", universityFiscalYear);
        map.put("chartOfAccountsCode", chartOfAccountsCode);
        map.put("accountNumber", accountNumber);
        map.put("subAccountNumber", subAccountNumber);
        map.put("financialObjectCode", financialObjectCode);
        map.put("financialSubObjectCode", financialSubObjectCode);
        map.put("financialBalanceTypeCode", financialBalanceTypeCode);
        map.put("financialObjectTypeCode", financialObjectTypeCode);
        map.put("universityFiscalPeriodCode", universityFiscalPeriodCode);
        map.put("financialDocumentTypeCode", financialDocumentTypeCode);
        map.put("financialSystemOriginationCode", financialSystemOriginationCode);
        map.put("financialDocumentNumber", financialDocumentNumber);
        map.put("trnEntryLedgerSequenceNumber", trnEntryLedgerSequenceNumber);
        return map;
    }

    public OriginEntryGroup getGroup() {
        return group;
    }

    public void setGroup(OriginEntryGroup oeg) {
      if ( oeg != null ) {
        entryGroupId = oeg.getId();
        group = oeg;
      } else {
        entryGroupId = null;
        group = null;
      }
    }

    public A21SubAccount getA21SubAccount() {
      return a21SubAccount;
    }
    public void setA21SubAccount(A21SubAccount subAccount) {
      a21SubAccount = subAccount;
    }
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getTransactionDebitCreditCode() {
        return transactionDebitCreditCode;
    }

    public void setTransactionDebitCreditCode(String transactionDebitCreditCode) {
        this.transactionDebitCreditCode = transactionDebitCreditCode;
    }

    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }

    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
    }

    public Date getFinancialDocumentReversalDate() {
        return financialDocumentReversalDate;
    }

    public void setFinancialDocumentReversalDate(Date financialDocumentReversalDate) {
        this.financialDocumentReversalDate = financialDocumentReversalDate;
    }

    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }

    public String getTransactionEncumbranceUpdtCd() {
        return transactionEncumbranceUpdtCd;
    }

    public void setTransactionEncumbranceUpdtCd(String transactionEncumbranceUpdtCd) {
        this.transactionEncumbranceUpdtCd = transactionEncumbranceUpdtCd;
    }

    public Integer getEntryGroupId() {
        return entryGroupId;
    }

    public void setEntryGroupId(Integer entryGroupId) {
        this.entryGroupId = entryGroupId;
    }

    public Integer getEntryId() {
        return entryId;
    }

    public void setEntryId(Integer entryId) {
        this.entryId = entryId;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    public String getOrganizationDocumentNumber() {
        return organizationDocumentNumber;
    }

    public void setOrganizationDocumentNumber(String organizationDocumentNumber) {
        this.organizationDocumentNumber = organizationDocumentNumber;
    }

    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getFinancialDocumentReferenceNbr() {
        return financialDocumentReferenceNbr;
    }

    public void setFinancialDocumentReferenceNbr(String financialDocumentReferenceNbr) {
        this.financialDocumentReferenceNbr = financialDocumentReferenceNbr;
    }

    public String getReferenceFinDocumentTypeCode() {
        return referenceFinDocumentTypeCode;
    }

    public void setReferenceFinDocumentTypeCode(String referenceFinDocumentTypeCode) {
        this.referenceFinDocumentTypeCode = referenceFinDocumentTypeCode;
    }

    public String getFinSystemRefOriginationCode() {
        return finSystemRefOriginationCode;
    }

    public void setFinSystemRefOriginationCode(String finSystemRefOriginationCode) {
        this.finSystemRefOriginationCode = finSystemRefOriginationCode;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getTrnEntryLedgerSequenceNumber() {
        return trnEntryLedgerSequenceNumber;
    }

    public void setTrnEntryLedgerSequenceNumber(Integer trnEntryLedgerSequenceNumber) {
        this.trnEntryLedgerSequenceNumber = trnEntryLedgerSequenceNumber;
    }

    public KualiDecimal getTransactionLedgerEntryAmount() {
        return transactionLedgerEntryAmount;
    }

    public void setTransactionLedgerEntryAmount(KualiDecimal transactionLedgerEntryAmount) {
        this.transactionLedgerEntryAmount = transactionLedgerEntryAmount;
    }

    public String getTransactionLedgerEntryDesc() {
        return transactionLedgerEntryDesc;
    }

    public void setTransactionLedgerEntryDesc(String transactionLedgerEntryDesc) {
        this.transactionLedgerEntryDesc = transactionLedgerEntryDesc;
    }

    public String getUniversityFiscalPeriodCode() {
        return universityFiscalPeriodCode;
    }

    public void setUniversityFiscalPeriodCode(String universityFiscalPeriodCode) {
        this.universityFiscalPeriodCode = universityFiscalPeriodCode;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    public Chart getChart() {
        return chart;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    public void setFinancialSubObject(SubObjCd financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public Options getOption() {
        return option;
    }

    public void setOption(Options option) {
        this.option = option;
    }

    public ProjectCode getProject() {
        return project;
    }

    public void setProject(ProjectCode project) {
        this.project = project;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public UniversityDate getUniversityDate() {
        return universityDate;
    }

    public void setUniversityDate(UniversityDate universityDate) {
        this.universityDate = universityDate;
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

    public UniversityDate getReversalDate() {
        return reversalDate;
    }

    public void setReversalDate(UniversityDate reversalDate) {
        this.reversalDate = reversalDate;
    }

    public OriginationCode getOrigination() {
        return origination;
    }

    public void setOrigination(OriginationCode origination) {
        this.origination = origination;
    }

    
    public DocumentType getReferenceDocumentType() {
        return referenceDocumentType;
    }

    public void setReferenceDocumentType(DocumentType referenceDocumentType) {
        this.referenceDocumentType = referenceDocumentType;
    }

    /**
     * @return Returns the budgetYear.
     */
    public String getBudgetYear() {
        return budgetYear;
    }

    /**
     * @param budgetYear The budgetYear to set.
     */
    public void setBudgetYear(String budgetYear) {
        this.budgetYear = budgetYear;
    }
    
    public boolean isDebit() {
        return Constants.GL_DEBIT_CODE.equals(this.transactionDebitCreditCode);
    }
    public boolean isCredit() {
        return Constants.GL_CREDIT_CODE.equals(this.transactionDebitCreditCode);
    }


}
