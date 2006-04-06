/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCodeCurrent;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CreditCardVendor extends BusinessObjectBase {

	private String financialDocumentCreditCardVendorNumber;
	private String financialDocumentCreditCardVendorName;
	private String financialDocumentCreditCardTypeCode;
	private String incomeFinancialChartOfAccountsCode;
	private String incomeAccountNumber;
	private String incomeFinancialObjectCode;
	private String incomeFinancialSubObjectCode;
	private String incomeSubAccountNumber;
	private String expenseFinancialChartOfAccountsCode;
	private String expenseAccountNumber;
	private String expenseFinancialObjectCode;
	private String expenseFinancialSubObjectCd;
	private String expenseSubAccountNumber;
	private Chart incomeFinancialChartOfAccounts;
	private ObjectCodeCurrent incomeFinancialObject;
	private Account incomeAccount;
	private Chart expenseFinancialChartOfAccounts;
	private ObjectCodeCurrent expenseFinancialObject;
	private Account expenseAccount;

	/**
	 * Default constructor.
	 */
	public CreditCardVendor() {

	}

	/**
	 * Gets the financialDocumentCreditCardVendorNumber attribute.
	 * 
	 * @return - Returns the financialDocumentCreditCardVendorNumber
	 * 
	 */
	public String getFinancialDocumentCreditCardVendorNumber() { 
		return financialDocumentCreditCardVendorNumber;
	}

	/**
	 * Sets the financialDocumentCreditCardVendorNumber attribute.
	 * 
	 * @param - financialDocumentCreditCardVendorNumber The financialDocumentCreditCardVendorNumber to set.
	 * 
	 */
	public void setFinancialDocumentCreditCardVendorNumber(String financialDocumentCreditCardVendorNumber) {
		this.financialDocumentCreditCardVendorNumber = financialDocumentCreditCardVendorNumber;
	}


	/**
	 * Gets the financialDocumentCreditCardVendorName attribute.
	 * 
	 * @return - Returns the financialDocumentCreditCardVendorName
	 * 
	 */
	public String getFinancialDocumentCreditCardVendorName() { 
		return financialDocumentCreditCardVendorName;
	}

	/**
	 * Sets the financialDocumentCreditCardVendorName attribute.
	 * 
	 * @param - financialDocumentCreditCardVendorName The financialDocumentCreditCardVendorName to set.
	 * 
	 */
	public void setFinancialDocumentCreditCardVendorName(String financialDocumentCreditCardVendorName) {
		this.financialDocumentCreditCardVendorName = financialDocumentCreditCardVendorName;
	}


	/**
	 * Gets the financialDocumentCreditCardTypeCode attribute.
	 * 
	 * @return - Returns the financialDocumentCreditCardTypeCode
	 * 
	 */
	public String getFinancialDocumentCreditCardTypeCode() { 
		return financialDocumentCreditCardTypeCode;
	}

	/**
	 * Sets the financialDocumentCreditCardTypeCode attribute.
	 * 
	 * @param - financialDocumentCreditCardTypeCode The financialDocumentCreditCardTypeCode to set.
	 * 
	 */
	public void setFinancialDocumentCreditCardTypeCode(String financialDocumentCreditCardTypeCode) {
		this.financialDocumentCreditCardTypeCode = financialDocumentCreditCardTypeCode;
	}


	/**
	 * Gets the incomeFinancialChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the incomeFinancialChartOfAccountsCode
	 * 
	 */
	public String getIncomeFinancialChartOfAccountsCode() { 
		return incomeFinancialChartOfAccountsCode;
	}

	/**
	 * Sets the incomeFinancialChartOfAccountsCode attribute.
	 * 
	 * @param - incomeFinancialChartOfAccountsCode The incomeFinancialChartOfAccountsCode to set.
	 * 
	 */
	public void setIncomeFinancialChartOfAccountsCode(String incomeFinancialChartOfAccountsCode) {
		this.incomeFinancialChartOfAccountsCode = incomeFinancialChartOfAccountsCode;
	}


	/**
	 * Gets the incomeAccountNumber attribute.
	 * 
	 * @return - Returns the incomeAccountNumber
	 * 
	 */
	public String getIncomeAccountNumber() { 
		return incomeAccountNumber;
	}

	/**
	 * Sets the incomeAccountNumber attribute.
	 * 
	 * @param - incomeAccountNumber The incomeAccountNumber to set.
	 * 
	 */
	public void setIncomeAccountNumber(String incomeAccountNumber) {
		this.incomeAccountNumber = incomeAccountNumber;
	}


	/**
	 * Gets the incomeFinancialObjectCode attribute.
	 * 
	 * @return - Returns the incomeFinancialObjectCode
	 * 
	 */
	public String getIncomeFinancialObjectCode() { 
		return incomeFinancialObjectCode;
	}

	/**
	 * Sets the incomeFinancialObjectCode attribute.
	 * 
	 * @param - incomeFinancialObjectCode The incomeFinancialObjectCode to set.
	 * 
	 */
	public void setIncomeFinancialObjectCode(String incomeFinancialObjectCode) {
		this.incomeFinancialObjectCode = incomeFinancialObjectCode;
	}


	/**
	 * Gets the incomeFinancialSubObjectCode attribute.
	 * 
	 * @return - Returns the incomeFinancialSubObjectCode
	 * 
	 */
	public String getIncomeFinancialSubObjectCode() { 
		return incomeFinancialSubObjectCode;
	}

	/**
	 * Sets the incomeFinancialSubObjectCode attribute.
	 * 
	 * @param - incomeFinancialSubObjectCode The incomeFinancialSubObjectCode to set.
	 * 
	 */
	public void setIncomeFinancialSubObjectCode(String incomeFinancialSubObjectCode) {
		this.incomeFinancialSubObjectCode = incomeFinancialSubObjectCode;
	}


	/**
	 * Gets the incomeSubAccountNumber attribute.
	 * 
	 * @return - Returns the incomeSubAccountNumber
	 * 
	 */
	public String getIncomeSubAccountNumber() { 
		return incomeSubAccountNumber;
	}

	/**
	 * Sets the incomeSubAccountNumber attribute.
	 * 
	 * @param - incomeSubAccountNumber The incomeSubAccountNumber to set.
	 * 
	 */
	public void setIncomeSubAccountNumber(String incomeSubAccountNumber) {
		this.incomeSubAccountNumber = incomeSubAccountNumber;
	}


	/**
	 * Gets the expenseFinancialChartOfAccountsCode attribute.
	 * 
	 * @return - Returns the expenseFinancialChartOfAccountsCode
	 * 
	 */
	public String getExpenseFinancialChartOfAccountsCode() { 
		return expenseFinancialChartOfAccountsCode;
	}

	/**
	 * Sets the expenseFinancialChartOfAccountsCode attribute.
	 * 
	 * @param - expenseFinancialChartOfAccountsCode The expenseFinancialChartOfAccountsCode to set.
	 * 
	 */
	public void setExpenseFinancialChartOfAccountsCode(String expenseFinancialChartOfAccountsCode) {
		this.expenseFinancialChartOfAccountsCode = expenseFinancialChartOfAccountsCode;
	}


	/**
	 * Gets the expenseAccountNumber attribute.
	 * 
	 * @return - Returns the expenseAccountNumber
	 * 
	 */
	public String getExpenseAccountNumber() { 
		return expenseAccountNumber;
	}

	/**
	 * Sets the expenseAccountNumber attribute.
	 * 
	 * @param - expenseAccountNumber The expenseAccountNumber to set.
	 * 
	 */
	public void setExpenseAccountNumber(String expenseAccountNumber) {
		this.expenseAccountNumber = expenseAccountNumber;
	}


	/**
	 * Gets the expenseFinancialObjectCode attribute.
	 * 
	 * @return - Returns the expenseFinancialObjectCode
	 * 
	 */
	public String getExpenseFinancialObjectCode() { 
		return expenseFinancialObjectCode;
	}

	/**
	 * Sets the expenseFinancialObjectCode attribute.
	 * 
	 * @param - expenseFinancialObjectCode The expenseFinancialObjectCode to set.
	 * 
	 */
	public void setExpenseFinancialObjectCode(String expenseFinancialObjectCode) {
		this.expenseFinancialObjectCode = expenseFinancialObjectCode;
	}


	/**
	 * Gets the expenseFinancialSubObjectCd attribute.
	 * 
	 * @return - Returns the expenseFinancialSubObjectCd
	 * 
	 */
	public String getExpenseFinancialSubObjectCd() { 
		return expenseFinancialSubObjectCd;
	}

	/**
	 * Sets the expenseFinancialSubObjectCd attribute.
	 * 
	 * @param - expenseFinancialSubObjectCd The expenseFinancialSubObjectCd to set.
	 * 
	 */
	public void setExpenseFinancialSubObjectCd(String expenseFinancialSubObjectCd) {
		this.expenseFinancialSubObjectCd = expenseFinancialSubObjectCd;
	}


	/**
	 * Gets the expenseSubAccountNumber attribute.
	 * 
	 * @return - Returns the expenseSubAccountNumber
	 * 
	 */
	public String getExpenseSubAccountNumber() { 
		return expenseSubAccountNumber;
	}

	/**
	 * Sets the expenseSubAccountNumber attribute.
	 * 
	 * @param - expenseSubAccountNumber The expenseSubAccountNumber to set.
	 * 
	 */
	public void setExpenseSubAccountNumber(String expenseSubAccountNumber) {
		this.expenseSubAccountNumber = expenseSubAccountNumber;
	}


	/**
	 * Gets the incomeFinancialChartOfAccounts attribute.
	 * 
	 * @return - Returns the incomeFinancialChartOfAccounts
	 * 
	 */
	public Chart getIncomeFinancialChartOfAccounts() { 
		return incomeFinancialChartOfAccounts;
	}

	/**
	 * Sets the incomeFinancialChartOfAccounts attribute.
	 * 
	 * @param - incomeFinancialChartOfAccounts The incomeFinancialChartOfAccounts to set.
	 * @deprecated
	 */
	public void setIncomeFinancialChartOfAccounts(Chart incomeFinancialChartOfAccounts) {
		this.incomeFinancialChartOfAccounts = incomeFinancialChartOfAccounts;
	}

	/**
	 * Gets the incomeFinancialObject attribute.
	 * 
	 * @return - Returns the incomeFinancialObject
	 * 
	 */
	public ObjectCodeCurrent getIncomeFinancialObject() { 
		return incomeFinancialObject;
	}

	/**
	 * Sets the incomeFinancialObject attribute.
	 * 
	 * @param - incomeFinancialObject The incomeFinancialObject to set.
	 * @deprecated
	 */
	public void setIncomeFinancialObject(ObjectCodeCurrent incomeFinancialObject) {
		this.incomeFinancialObject = incomeFinancialObject;
	}

	/**
	 * Gets the incomeAccount attribute.
	 * 
	 * @return - Returns the incomeAccount
	 * 
	 */
	public Account getIncomeAccount() { 
		return incomeAccount;
	}

	/**
	 * Sets the incomeAccount attribute.
	 * 
	 * @param - incomeAccount The incomeAccount to set.
	 * @deprecated
	 */
	public void setIncomeAccount(Account incomeAccount) {
		this.incomeAccount = incomeAccount;
	}

	/**
	 * Gets the expenseFinancialChartOfAccounts attribute.
	 * 
	 * @return - Returns the expenseFinancialChartOfAccounts
	 * 
	 */
	public Chart getExpenseFinancialChartOfAccounts() { 
		return expenseFinancialChartOfAccounts;
	}

	/**
	 * Sets the expenseFinancialChartOfAccounts attribute.
	 * 
	 * @param - expenseFinancialChartOfAccounts The expenseFinancialChartOfAccounts to set.
	 * @deprecated
	 */
	public void setExpenseFinancialChartOfAccounts(Chart expenseFinancialChartOfAccounts) {
		this.expenseFinancialChartOfAccounts = expenseFinancialChartOfAccounts;
	}

	/**
	 * Gets the expenseFinancialObject attribute.
	 * 
	 * @return - Returns the expenseFinancialObject
	 * 
	 */
	public ObjectCodeCurrent getExpenseFinancialObject() { 
		return expenseFinancialObject;
	}

	/**
	 * Sets the expenseFinancialObject attribute.
	 * 
	 * @param - expenseFinancialObject The expenseFinancialObject to set.
	 * @deprecated
	 */
	public void setExpenseFinancialObject(ObjectCodeCurrent expenseFinancialObject) {
		this.expenseFinancialObject = expenseFinancialObject;
	}

	/**
	 * Gets the expenseAccount attribute.
	 * 
	 * @return - Returns the expenseAccount
	 * 
	 */
	public Account getExpenseAccount() { 
		return expenseAccount;
	}

	/**
	 * Sets the expenseAccount attribute.
	 * 
	 * @param - expenseAccount The expenseAccount to set.
	 * @deprecated
	 */
	public void setExpenseAccount(Account expenseAccount) {
		this.expenseAccount = expenseAccount;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("financialDocumentCreditCardVendorNumber", this.financialDocumentCreditCardVendorNumber);
	    return m;
    }
}
