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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.codes.ICRTypeCode;

/**
 * @author jsissom
 *
 */
public class A21SubAccount extends BusinessObjectBase {
  static final long serialVersionUID = -4101590175655031672L;

  private String chartOfAccountsCode;
  private String accountNumber;
  private String subAccountNumber;
  private String subAccountTypeCode;
  private String indirectCostRecoveryTypeCode;
  private String financialIcrSeriesIdentifier;
  private String indirectCostRecoveryChartOfAccountsCode;
  private String indirectCostRecoveryAccountNumber;
  private boolean offCampusIndicator;
  private String costSharingChartOfAccountsCode;
  private String costSharingAccountNumber;
  private String costSharingSubAccountNumber;

  private Chart chartOfAccounts;
  private Account account;
  private SubAccount subAccount;
  private ICRTypeCode icrTypeCode;
  private Chart indirectCostRecoveryChartOfAccounts;
  private Account indirectCostRecoveryAccount;
  private Chart costSharingChartOfAccounts;
  private Account costSharingAccount;
  private SubAccount costSharingSubAccount;
  
  /**
   * 
   */
  public A21SubAccount() {
    super();
  }

/**
 * Gets the serialVersionUID attribute. 
 * @return Returns the serialVersionUID.
 */
public static long getSerialVersionUID() {
    return serialVersionUID;
}
/**
 * Gets the account attribute. 
 * @return Returns the account.
 */
public Account getAccount() {
    return account;
}
/**
 * Sets the account attribute value.
 * @param account The account to set.
 */
public void setAccount(Account account) {
    this.account = account;
}
/**
 * Gets the accountNumber attribute. 
 * @return Returns the accountNumber.
 */
public String getAccountNumber() {
    return accountNumber;
}
/**
 * Sets the accountNumber attribute value.
 * @param accountNumber The accountNumber to set.
 */
public void setAccountNumber(String accountNumber) {
    this.accountNumber = accountNumber;
}
/**
 * Gets the chartOfAccounts attribute. 
 * @return Returns the chartOfAccounts.
 */
public Chart getChartOfAccounts() {
    return chartOfAccounts;
}
/**
 * Sets the chartOfAccounts attribute value.
 * @param chartOfAccounts The chartOfAccounts to set.
 */
public void setChartOfAccounts(Chart chartOfAccounts) {
    this.chartOfAccounts = chartOfAccounts;
}
/**
 * Gets the chartOfAccountsCode attribute. 
 * @return Returns the chartOfAccountsCode.
 */
public String getChartOfAccountsCode() {
    return chartOfAccountsCode;
}
/**
 * Sets the chartOfAccountsCode attribute value.
 * @param chartOfAccountsCode The chartOfAccountsCode to set.
 */
public void setChartOfAccountsCode(String chartOfAccountsCode) {
    this.chartOfAccountsCode = chartOfAccountsCode;
}
/**
 * Gets the costSharingAccount attribute. 
 * @return Returns the costSharingAccount.
 */
public Account getCostSharingAccount() {
    return costSharingAccount;
}
/**
 * Sets the costSharingAccount attribute value.
 * @param costSharingAccount The costSharingAccount to set.
 */
public void setCostSharingAccount(Account costSharingAccount) {
    this.costSharingAccount = costSharingAccount;
}
/**
 * Gets the costSharingAccountNumber attribute. 
 * @return Returns the costSharingAccountNumber.
 */
public String getCostSharingAccountNumber() {
    return costSharingAccountNumber;
}
/**
 * Sets the costSharingAccountNumber attribute value.
 * @param costSharingAccountNumber The costSharingAccountNumber to set.
 */
public void setCostSharingAccountNumber(String costSharingAccountNumber) {
    this.costSharingAccountNumber = costSharingAccountNumber;
}
/**
 * Gets the costSharingChartOfAccounts attribute. 
 * @return Returns the costSharingChartOfAccounts.
 */
public Chart getCostSharingChartOfAccounts() {
    return costSharingChartOfAccounts;
}
/**
 * Sets the costSharingChartOfAccounts attribute value.
 * @param costSharingChartOfAccounts The costSharingChartOfAccounts to set.
 */
public void setCostSharingChartOfAccounts(Chart costSharingChartOfAccounts) {
    this.costSharingChartOfAccounts = costSharingChartOfAccounts;
}
/**
 * Gets the costSharingChartOfAccountsCode attribute. 
 * @return Returns the costSharingChartOfAccountsCode.
 */
public String getCostSharingChartOfAccountsCode() {
    return costSharingChartOfAccountsCode;
}
/**
 * Sets the costSharingChartOfAccountsCode attribute value.
 * @param costSharingChartOfAccountsCode The costSharingChartOfAccountsCode to set.
 */
public void setCostSharingChartOfAccountsCode(String costSharingChartOfAccountsCode) {
    this.costSharingChartOfAccountsCode = costSharingChartOfAccountsCode;
}
/**
 * Gets the costSharingSubAccount attribute. 
 * @return Returns the costSharingSubAccount.
 */
public SubAccount getCostSharingSubAccount() {
    return costSharingSubAccount;
}
/**
 * Sets the costSharingSubAccount attribute value.
 * @param costSharingSubAccount The costSharingSubAccount to set.
 */
public void setCostSharingSubAccount(SubAccount costSharingSubAccount) {
    this.costSharingSubAccount = costSharingSubAccount;
}
/**
 * Gets the costSharingSubAccountNumber attribute. 
 * @return Returns the costSharingSubAccountNumber.
 */
public String getCostSharingSubAccountNumber() {
    return costSharingSubAccountNumber;
}
/**
 * Sets the costSharingSubAccountNumber attribute value.
 * @param costSharingSubAccountNumber The costSharingSubAccountNumber to set.
 */
public void setCostSharingSubAccountNumber(String costSharingSubAccountNumber) {
    this.costSharingSubAccountNumber = costSharingSubAccountNumber;
}
/**
 * Gets the icrTypeCode attribute. 
 * @return Returns the icrTypeCode.
 */
public ICRTypeCode getIcrTypeCode() {
    return icrTypeCode;
}
/**
 * Sets the icrTypeCode attribute value.
 * @param icrTypeCode The icrTypeCode to set.
 */
public void setIcrTypeCode(ICRTypeCode icrTypeCode) {
    this.icrTypeCode = icrTypeCode;
}
/**
 * Gets the indirectCostRecoveryAccount attribute. 
 * @return Returns the indirectCostRecoveryAccount.
 */
public Account getIndirectCostRecoveryAccount() {
    return indirectCostRecoveryAccount;
}
/**
 * Sets the indirectCostRecoveryAccount attribute value.
 * @param indirectCostRecoveryAccount The indirectCostRecoveryAccount to set.
 */
public void setIndirectCostRecoveryAccount(Account indirectCostRecoveryAccount) {
    this.indirectCostRecoveryAccount = indirectCostRecoveryAccount;
}
/**
 * Gets the indirectCostRecoveryAccountNumber attribute. 
 * @return Returns the indirectCostRecoveryAccountNumber.
 */
public String getIndirectCostRecoveryAccountNumber() {
    return indirectCostRecoveryAccountNumber;
}
/**
 * Sets the indirectCostRecoveryAccountNumber attribute value.
 * @param indirectCostRecoveryAccountNumber The indirectCostRecoveryAccountNumber to set.
 */
public void setIndirectCostRecoveryAccountNumber(String indirectCostRecoveryAccountNumber) {
    this.indirectCostRecoveryAccountNumber = indirectCostRecoveryAccountNumber;
}
/**
 * Gets the indirectCostRecoveryChartOfAccounts attribute. 
 * @return Returns the indirectCostRecoveryChartOfAccounts.
 */
public Chart getIndirectCostRecoveryChartOfAccounts() {
    return indirectCostRecoveryChartOfAccounts;
}
/**
 * Sets the indirectCostRecoveryChartOfAccounts attribute value.
 * @param indirectCostRecoveryChartOfAccounts The indirectCostRecoveryChartOfAccounts to set.
 */
public void setIndirectCostRecoveryChartOfAccounts(Chart indirectCostRecoveryChartOfAccounts) {
    this.indirectCostRecoveryChartOfAccounts = indirectCostRecoveryChartOfAccounts;
}
/**
 * Gets the indirectCostRecoveryChartOfAccountsCode attribute. 
 * @return Returns the indirectCostRecoveryChartOfAccountsCode.
 */
public String getIndirectCostRecoveryChartOfAccountsCode() {
    return indirectCostRecoveryChartOfAccountsCode;
}
/**
 * Sets the indirectCostRecoveryChartOfAccountsCode attribute value.
 * @param indirectCostRecoveryChartOfAccountsCode The indirectCostRecoveryChartOfAccountsCode to set.
 */
public void setIndirectCostRecoveryChartOfAccountsCode(String indirectCostRecoveryChartOfAccountsCode) {
    this.indirectCostRecoveryChartOfAccountsCode = indirectCostRecoveryChartOfAccountsCode;
}
/**
 * Gets the indirectCostRecoveryTypeCode attribute. 
 * @return Returns the indirectCostRecoveryTypeCode.
 */
public String getIndirectCostRecoveryTypeCode() {
    return indirectCostRecoveryTypeCode;
}
/**
 * Sets the indirectCostRecoveryTypeCode attribute value.
 * @param indirectCostRecoveryTypeCode The indirectCostRecoveryTypeCode to set.
 */
public void setIndirectCostRecoveryTypeCode(String indirectCostRecoveryTypeCode) {
    this.indirectCostRecoveryTypeCode = indirectCostRecoveryTypeCode;
}

/**
 * Gets the offCampusIndicator attribute. 
 * @return Returns the offCampusIndicator.
 */
public boolean getOffCampusIndicator() {
    return offCampusIndicator;
}
/**
 * Sets the offCampusIndicator attribute value.
 * @param offCampusIndicator The offCampusIndicator to set.
 */
public void setOffCampusIndicator(boolean offCampusIndicator ) {
    this.offCampusIndicator = offCampusIndicator;
}
/**
 * Gets the financialIcrSeriesIdentifier attribute. 
 * @return Returns the financialIcrSeriesIdentifier.
 */
public String getFinancialIcrSeriesIdentifier() {
    return financialIcrSeriesIdentifier;
}
/**
 * Sets the financialIcrSeriesIdentifier attribute value.
 * @param financialIcrSeriesIdentifier The financialIcrSeriesIdentifier to set.
 */
public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
    this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
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
public void setSubAccount(SubAccount subAccount) {
    this.subAccount = subAccount;
}
/**
 * Gets the subAccountNumber attribute. 
 * @return Returns the subAccountNumber.
 */
public String getSubAccountNumber() {
    return subAccountNumber;
}
/**
 * Sets the subAccountNumber attribute value.
 * @param subAccountNumber The subAccountNumber to set.
 */
public void setSubAccountNumber(String subAccountNumber) {
    this.subAccountNumber = subAccountNumber;
}
/**
 * Gets the subAccountTypeCode attribute. 
 * @return Returns the subAccountTypeCode.
 */
public String getSubAccountTypeCode() {
    return subAccountTypeCode;
}
/**
 * Sets the subAccountTypeCode attribute value.
 * @param subAccountTypeCode The subAccountTypeCode to set.
 */
public void setSubAccountTypeCode(String subAccountTypeCode) {
    this.subAccountTypeCode = subAccountTypeCode;
}
  /* (non-Javadoc)
   * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
   */
  protected LinkedHashMap toStringMapper() {
    LinkedHashMap map = new LinkedHashMap();
    map.put("chartOfAccountsCode", getChartOfAccountsCode());
    map.put("accountNumber", getAccountNumber());
    map.put("subAccountNumber", getSubAccountNumber());
    return map;
  }

  
}
