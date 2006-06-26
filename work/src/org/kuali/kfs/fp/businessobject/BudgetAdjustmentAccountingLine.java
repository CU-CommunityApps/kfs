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
package org.kuali.module.financial.bo;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;

/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public interface BudgetAdjustmentAccountingLine extends AccountingLine{

    public abstract KualiDecimal getMonthlyLinesTotal();

    public abstract KualiInteger getBaseBudgetAdjustmentAmount();

    public abstract void setBaseBudgetAdjustmentAmount(KualiInteger baseBudgetAdjustmentAmount);

    public abstract String getBudgetAdjustmentPeriodCode();

    public abstract void setBudgetAdjustmentPeriodCode(String budgetAdjustmentPeriodCode);

    public abstract KualiDecimal getCurrentBudgetAdjustmentAmount();

    public abstract void setCurrentBudgetAdjustmentAmount(KualiDecimal currentBudgetAdjustmentAmount);

    /**
     * Gets the financialDocumentMonth1LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth1LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth1LineAmount();

    /**
     * Sets the financialDocumentMonth1LineAmount attribute.
     * 
     * @param financialDocumentMonth1LineAmount The financialDocumentMonth1LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth1LineAmount(KualiDecimal financialDocumentMonth1LineAmount);

    /**
     * Gets the financialDocumentMonth2LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth2LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth2LineAmount();

    /**
     * Sets the financialDocumentMonth2LineAmount attribute.
     * 
     * @param financialDocumentMonth2LineAmount The financialDocumentMonth2LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth2LineAmount(KualiDecimal financialDocumentMonth2LineAmount);

    /**
     * Gets the financialDocumentMonth3LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth3LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth3LineAmount();

    /**
     * Sets the financialDocumentMonth3LineAmount attribute.
     * 
     * @param financialDocumentMonth3LineAmount The financialDocumentMonth3LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth3LineAmount(KualiDecimal financialDocumentMonth3LineAmount);

    /**
     * Gets the financialDocumentMonth4LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth4LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth4LineAmount();

    /**
     * Sets the financialDocumentMonth4LineAmount attribute.
     * 
     * @param financialDocumentMonth4LineAmount The financialDocumentMonth4LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth4LineAmount(KualiDecimal financialDocumentMonth4LineAmount);

    /**
     * Gets the financialDocumentMonth5LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth5LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth5LineAmount();

    /**
     * Sets the financialDocumentMonth5LineAmount attribute.
     * 
     * @param financialDocumentMonth5LineAmount The financialDocumentMonth5LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth5LineAmount(KualiDecimal financialDocumentMonth5LineAmount);

    /**
     * Gets the financialDocumentMonth6LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth6LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth6LineAmount();

    /**
     * Sets the financialDocumentMonth6LineAmount attribute.
     * 
     * @param financialDocumentMonth6LineAmount The financialDocumentMonth6LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth6LineAmount(KualiDecimal financialDocumentMonth6LineAmount);

    /**
     * Gets the financialDocumentMonth7LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth7LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth7LineAmount();

    /**
     * Sets the financialDocumentMonth7LineAmount attribute.
     * 
     * @param financialDocumentMonth7LineAmount The financialDocumentMonth7LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth7LineAmount(KualiDecimal financialDocumentMonth7LineAmount);

    /**
     * Gets the financialDocumentMonth8LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth8LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth8LineAmount();

    /**
     * Sets the financialDocumentMonth8LineAmount attribute.
     * 
     * @param financialDocumentMonth8LineAmount The financialDocumentMonth8LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth8LineAmount(KualiDecimal financialDocumentMonth8LineAmount);

    /**
     * Gets the financialDocumentMonth9LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth9LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth9LineAmount();

    /**
     * Sets the financialDocumentMonth9LineAmount attribute.
     * 
     * @param financialDocumentMonth9LineAmount The financialDocumentMonth9LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth9LineAmount(KualiDecimal financialDocumentMonth9LineAmount);

    /**
     * Gets the financialDocumentMonth10LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth10LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth10LineAmount();

    /**
     * Sets the financialDocumentMonth10LineAmount attribute.
     * 
     * @param financialDocumentMonth10LineAmount The financialDocumentMonth10LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth10LineAmount(KualiDecimal financialDocumentMonth10LineAmount);

    /**
     * Gets the financialDocumentMonth11LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth11LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth11LineAmount();

    /**
     * Sets the financialDocumentMonth11LineAmount attribute.
     * 
     * @param financialDocumentMonth11LineAmount The financialDocumentMonth11LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth11LineAmount(KualiDecimal financialDocumentMonth11LineAmount);

    /**
     * Gets the financialDocumentMonth12LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth12LineAmount
     * 
     */
    public abstract KualiDecimal getFinancialDocumentMonth12LineAmount();

    /**
     * Sets the financialDocumentMonth12LineAmount attribute.
     * 
     * @param financialDocumentMonth12LineAmount The financialDocumentMonth12LineAmount to set.
     * 
     */
    public abstract void setFinancialDocumentMonth12LineAmount(KualiDecimal financialDocumentMonth12LineAmount);

    /**
     * Gets the fringeBenefitIndicator attribute.
     * 
     * @return - Returns the fringeBenefitIndicator
     * 
     */
    public abstract boolean isFringeBenefitIndicator();

    /**
     * Sets the fringeBenefitIndicator attribute.
     * 
     * @param fringeBenefitIndicator The fringeBenefitIndicator to set.
     * 
     */
    public abstract void setFringeBenefitIndicator(boolean fringeBenefitIndicator);
    
    
}