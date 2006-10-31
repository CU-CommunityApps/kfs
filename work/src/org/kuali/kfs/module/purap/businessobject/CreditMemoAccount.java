/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/purap/businessobject/CreditMemoAccount.java,v $
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

package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

/**
 * 
 */
public class CreditMemoAccount extends BusinessObjectBase {

	private Integer creditMemoAccountIdentifier;
	private Integer creditMemoItemIdentifier;
	private String chartOfAccountsCode;
	private String accountNumber;
	private String financialObjectCode;
	private String subAccountNumber;
	private String financialSubObjectCode;
	private String projectCode;
	private String organizationReferenceId;
	private KualiDecimal itemAccountTotalAmount;
	private BigDecimal accountLinePercent;

    private CreditMemoItem creditMemoItem;
	private Chart chartOfAccounts;
	private Account account;
    private SubAccount subAccount;
    
	/**
	 * Default constructor.
	 */
	public CreditMemoAccount() {

	}

	/**
	 * Gets the creditMemoAccountIdentifier attribute.
	 * 
	 * @return - Returns the creditMemoAccountIdentifier
	 * 
	 */
	public Integer getCreditMemoAccountIdentifier() { 
		return creditMemoAccountIdentifier;
	}

	/**
	 * Sets the creditMemoAccountIdentifier attribute.
	 * 
	 * @param - creditMemoAccountIdentifier The creditMemoAccountIdentifier to set.
	 * 
	 */
	public void setCreditMemoAccountIdentifier(Integer creditMemoAccountIdentifier) {
		this.creditMemoAccountIdentifier = creditMemoAccountIdentifier;
	}


	/**
	 * Gets the creditMemoItemIdentifier attribute.
	 * 
	 * @return - Returns the creditMemoItemIdentifier
	 * 
	 */
	public Integer getCreditMemoItemIdentifier() { 
		return creditMemoItemIdentifier;
	}

	/**
	 * Sets the creditMemoItemIdentifier attribute.
	 * 
	 * @param - creditMemoItemIdentifier The creditMemoItemIdentifier to set.
	 * 
	 */
	public void setCreditMemoItemIdentifier(Integer creditMemoItemIdentifier) {
		this.creditMemoItemIdentifier = creditMemoItemIdentifier;
	}

	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return - Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param - chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the accountNumber attribute.
	 * 
	 * @return - Returns the accountNumber
	 * 
	 */
	public String getAccountNumber() { 
		return accountNumber;
	}

	/**
	 * Sets the accountNumber attribute.
	 * 
	 * @param - accountNumber The accountNumber to set.
	 * 
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}


	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return - Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param - financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the subAccountNumber attribute.
	 * 
	 * @return - Returns the subAccountNumber
	 * 
	 */
	public String getSubAccountNumber() { 
		return subAccountNumber;
	}

	/**
	 * Sets the subAccountNumber attribute.
	 * 
	 * @param - subAccountNumber The subAccountNumber to set.
	 * 
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}


	/**
	 * Gets the financialSubObjectCode attribute.
	 * 
	 * @return - Returns the financialSubObjectCode
	 * 
	 */
	public String getFinancialSubObjectCode() { 
		return financialSubObjectCode;
	}

	/**
	 * Sets the financialSubObjectCode attribute.
	 * 
	 * @param - financialSubObjectCode The financialSubObjectCode to set.
	 * 
	 */
	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}


	/**
	 * Gets the projectCode attribute.
	 * 
	 * @return - Returns the projectCode
	 * 
	 */
	public String getProjectCode() { 
		return projectCode;
	}

	/**
	 * Sets the projectCode attribute.
	 * 
	 * @param - projectCode The projectCode to set.
	 * 
	 */
	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
	}


	/**
	 * Gets the organizationReferenceId attribute.
	 * 
	 * @return - Returns the organizationReferenceId
	 * 
	 */
	public String getOrganizationReferenceId() { 
		return organizationReferenceId;
	}

	/**
	 * Sets the organizationReferenceId attribute.
	 * 
	 * @param - organizationReferenceId The organizationReferenceId to set.
	 * 
	 */
	public void setOrganizationReferenceId(String organizationReferenceId) {
		this.organizationReferenceId = organizationReferenceId;
	}


	/**
	 * Gets the itemAccountTotalAmount attribute.
	 * 
	 * @return - Returns the itemAccountTotalAmount
	 * 
	 */
	public KualiDecimal getItemAccountTotalAmount() { 
		return itemAccountTotalAmount;
	}

	/**
	 * Sets the itemAccountTotalAmount attribute.
	 * 
	 * @param - itemAccountTotalAmount The itemAccountTotalAmount to set.
	 * 
	 */
	public void setItemAccountTotalAmount(KualiDecimal itemAccountTotalAmount) {
		this.itemAccountTotalAmount = itemAccountTotalAmount;
	}


	/**
	 * Gets the accountLinePercent attribute.
	 * 
	 * @return - Returns the accountLinePercent
	 * 
	 */
	public BigDecimal getAccountLinePercent() { 
		return accountLinePercent;
	}

	/**
	 * Sets the accountLinePercent attribute.
	 * 
	 * @param - accountLinePercent The accountLinePercent to set.
	 * 
	 */
	public void setAccountLinePercent(BigDecimal accountLinePercent) {
		this.accountLinePercent = accountLinePercent;
	}


	/**
	 * Gets the creditMemoItem attribute.
	 * 
	 * @return - Returns the creditMemoItem
	 * 
	 */
	public CreditMemoItem getCreditMemoItem() { 
		return creditMemoItem;
	}

	/**
	 * Sets the creditMemoItem attribute.
	 * 
	 * @param - creditMemoItem The creditMemoItem to set.
	 * @deprecated
	 */
	public void setCreditMemoItem(CreditMemoItem creditMemoItem) {
		this.creditMemoItem = creditMemoItem;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return - Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param - chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

	/**
	 * Gets the account attribute.
	 * 
	 * @return - Returns the account
	 * 
	 */
	public Account getAccount() { 
		return account;
	}

	/**
	 * Sets the account attribute.
	 * 
	 * @param - account The account to set.
	 * @deprecated
	 */
	public void setAccount(Account account) {
		this.account = account;
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
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.creditMemoAccountIdentifier != null) {
            m.put("creditMemoAccountIdentifier", this.creditMemoAccountIdentifier.toString());
        }
        return m;
    }

}
