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
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherNonEmployeeExpense extends BusinessObjectBase {

	private String financialDocumentNumber;
	private Integer financialDocumentLineNumber;
	private String disbVchrExpenseCode;
	private String disbVchrExpenseCompanyName;
	private KualiDecimal disbVchrExpenseAmount;
	private DisbursementVoucherNonEmployeeTravel financialDocument;
	private TravelExpenseTypeCode disbVchrExpense;
	private TravelCompanyCode disbVchrExpenseCompany;

	/**
	 * Default no-arg constructor.
	 */
	public DisbursementVoucherNonEmployeeExpense() {

	}

	/**
	 * Gets the financialDocumentNumber attribute.
	 * 
	 * @return - Returns the financialDocumentNumber
	 * 
	 */
	public String getFinancialDocumentNumber() { 
		return financialDocumentNumber;
	}
	

	/**
	 * Sets the financialDocumentNumber attribute.
	 * 
	 * @param - financialDocumentNumber The financialDocumentNumber to set.
	 * 
	 */
	public void setFinancialDocumentNumber(String financialDocumentNumber) {
		this.financialDocumentNumber = financialDocumentNumber;
	}

	/**
	 * Gets the financialDocumentLineNumber attribute.
	 * 
	 * @return - Returns the financialDocumentLineNumber
	 * 
	 */
	public Integer getFinancialDocumentLineNumber() { 
		return financialDocumentLineNumber;
	}
	

	/**
	 * Sets the financialDocumentLineNumber attribute.
	 * 
	 * @param - financialDocumentLineNumber The financialDocumentLineNumber to set.
	 * 
	 */
	public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
		this.financialDocumentLineNumber = financialDocumentLineNumber;
	}

	/**
	 * Gets the disbVchrExpenseCode attribute.
	 * 
	 * @return - Returns the disbVchrExpenseCode
	 * 
	 */
	public String getDisbVchrExpenseCode() { 
		return disbVchrExpenseCode;
	}
	

	/**
	 * Sets the disbVchrExpenseCode attribute.
	 * 
	 * @param - disbVchrExpenseCode The disbVchrExpenseCode to set.
	 * 
	 */
	public void setDisbVchrExpenseCode(String disbVchrExpenseCode) {
		this.disbVchrExpenseCode = disbVchrExpenseCode;
	}

	/**
	 * Gets the disbVchrExpenseCompanyName attribute.
	 * 
	 * @return - Returns the disbVchrExpenseCompanyName
	 * 
	 */
	public String getDisbVchrExpenseCompanyName() { 
		return disbVchrExpenseCompanyName;
	}
	

	/**
	 * Sets the disbVchrExpenseCompanyName attribute.
	 * 
	 * @param - disbVchrExpenseCompanyName The disbVchrExpenseCompanyName to set.
	 * 
	 */
	public void setDisbVchrExpenseCompanyName(String disbVchrExpenseCompanyName) {
		this.disbVchrExpenseCompanyName = disbVchrExpenseCompanyName;
	}

	/**
	 * Gets the disbVchrExpenseAmount attribute.
	 * 
	 * @return - Returns the disbVchrExpenseAmount
	 * 
	 */
	public KualiDecimal getDisbVchrExpenseAmount() { 
		return disbVchrExpenseAmount;
	}
	

	/**
	 * Sets the disbVchrExpenseAmount attribute.
	 * 
	 * @param - disbVchrExpenseAmount The disbVchrExpenseAmount to set.
	 * 
	 */
	public void setDisbVchrExpenseAmount(KualiDecimal disbVchrExpenseAmount) {
		this.disbVchrExpenseAmount = disbVchrExpenseAmount;
	}

	/**
	 * Gets the financialDocument attribute.
	 * 
	 * @return - Returns the financialDocument
	 * 
	 */
	public DisbursementVoucherNonEmployeeTravel getFinancialDocument() { 
		return financialDocument;
	}
	

	/**
	 * Sets the financialDocument attribute.
	 * 
	 * @param - financialDocument The financialDocument to set.
	 * @deprecated
	 */
	public void setFinancialDocument(DisbursementVoucherNonEmployeeTravel financialDocument) {
		this.financialDocument = financialDocument;
	}

	/**
	 * Gets the disbVchrExpense attribute.
	 * 
	 * @return - Returns the disbVchrExpense
	 * 
	 */
	public TravelExpenseTypeCode getDisbVchrExpense() { 
		return disbVchrExpense;
	}
	

	/**
	 * Sets the disbVchrExpense attribute.
	 * 
	 * @param - disbVchrExpense The disbVchrExpense to set.
	 * @deprecated
	 */
	public void setDisbVchrExpense(TravelExpenseTypeCode disbVchrExpense) {
		this.disbVchrExpense = disbVchrExpense;
	}

	/**
	 * Gets the disbVchrExpenseCompany attribute.
	 * 
	 * @return - Returns the disbVchrExpenseCompany
	 * 
	 */
	public TravelCompanyCode getDisbVchrExpenseCompany() { 
		return disbVchrExpenseCompany;
	}
	

	/**
	 * Sets the disbVchrExpenseCompany attribute.
	 * 
	 * @param - disbVchrExpenseCompany The disbVchrExpenseCompany to set.
	 * @deprecated
	 */
	public void setDisbVchrExpenseCompany(TravelCompanyCode disbVchrExpenseCompany) {
		this.disbVchrExpenseCompany = disbVchrExpenseCompany;
	}

	/**
	 * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();
          m.put("financialDocumentNumber", this.financialDocumentNumber);
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
  	    return m;
	}
}
