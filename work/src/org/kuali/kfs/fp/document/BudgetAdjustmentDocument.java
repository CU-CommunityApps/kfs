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

package org.kuali.module.financial.document;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.exceptions.ApplicationParameterException;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine;
import org.kuali.module.financial.rules.BudgetAdjustmentRuleConstants;
import org.kuali.module.financial.rules.TransactionalDocumentRuleUtil;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the business object that represents the BudgetAdjustment document in Kuali.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class BudgetAdjustmentDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentDocument.class);

    private Integer nextPositionSourceLineNumber;
    private Integer nextPositionTargetLineNumber;

    /**
     * Default constructor.
     */
    public BudgetAdjustmentDocument() {
        super();
    }

    /**
     * generic, shared logic used to iniate a ba document
     */
    public void initiateDocument() {

        // setting default posting year
        Integer currentYearParam = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
        Integer defaultYearParam = null;

        try {
            defaultYearParam = new Integer(Integer.parseInt(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(BudgetAdjustmentRuleConstants.GLOBAL_FIELD_RESTRICTIONS_GROUP_NM, BudgetAdjustmentRuleConstants.DEFAULT_FISCAL_YEAR_PARM_NM)));
        }
        catch (ApplicationParameterException e) {
            // DO NOTHING: we don't want to throw an error if the default value is not found, just don't set it in the list
        }

        if (defaultYearParam != null) {
            setPostingYear(defaultYearParam);
        }
        else {
            setPostingYear(currentYearParam);
        }

    }

    public Integer getNextPositionSourceLineNumber() {
        return nextPositionSourceLineNumber;
    }

    public void setNextPositionSourceLineNumber(Integer nextPositionSourceLineNumber) {
        this.nextPositionSourceLineNumber = nextPositionSourceLineNumber;
    }

    public Integer getNextPositionTargetLineNumber() {
        return nextPositionTargetLineNumber;
    }

    public void setNextPositionTargetLineNumber(Integer nextPositionTargetLineNumber) {
        this.nextPositionTargetLineNumber = nextPositionTargetLineNumber;
    }

    /**
     * Returns the total current budget amount from the source lines.
     */
    public KualiDecimal getSourceCurrentBudgetTotal() {
        KualiDecimal currentBudgetTotal = new KualiDecimal(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            currentBudgetTotal = currentBudgetTotal.add(line.getCurrentBudgetAdjustmentAmount());
        }

        return currentBudgetTotal;
    }

    /**
     * Returns the total current budget income amount from the source lines.
     */
    public KualiDecimal getSourceCurrentBudgetIncomeTotal() {
        KualiDecimal total = new KualiDecimal(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isIncome(line)) {
                total = total.add(line.getCurrentBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total current budget expense amount from the source lines.
     */
    public KualiDecimal getSourceCurrentBudgetExpenseTotal() {
        KualiDecimal total = new KualiDecimal(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getCurrentBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total current budget amount from the target lines.
     */
    public KualiDecimal getTargetCurrentBudgetTotal() {
        KualiDecimal currentBudgetTotal = new KualiDecimal(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            currentBudgetTotal = currentBudgetTotal.add(line.getCurrentBudgetAdjustmentAmount());
        }

        return currentBudgetTotal;
    }

    /**
     * Returns the total current budget income amount from the target lines.
     */
    public KualiDecimal getTargetCurrentBudgetIncomeTotal() {
        KualiDecimal total = new KualiDecimal(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isIncome(line)) {
                total = total.add(line.getCurrentBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total current budget expense amount from the target lines.
     */
    public KualiDecimal getTargetCurrentBudgetExpenseTotal() {
        KualiDecimal total = new KualiDecimal(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getCurrentBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total base budget amount from the source lines.
     */
    public KualiInteger getSourceBaseBudgetTotal() {
        KualiInteger baseBudgetTotal = new KualiInteger(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            baseBudgetTotal = baseBudgetTotal.add(line.getBaseBudgetAdjustmentAmount());
        }

        return baseBudgetTotal;
    }

    /**
     * Returns the total base budget income amount from the source lines.
     */
    public KualiInteger getSourceBaseBudgetIncomeTotal() {
        KualiInteger total = new KualiInteger(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isIncome(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }
    
    /**
     * Returns the total base budget expense amount from the source lines.
     */
    public KualiInteger getSourceBaseBudgetExpenseTotal() {
        KualiInteger total = new KualiInteger(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total base budget amount from the target lines.
     */
    public KualiInteger getTargetBaseBudgetTotal() {
        KualiInteger baseBudgetTotal = new KualiInteger(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            baseBudgetTotal = baseBudgetTotal.add(line.getBaseBudgetAdjustmentAmount());
        }

        return baseBudgetTotal;
    }

    /**
     * Returns the total base budget income amount from the target lines.
     */
    public KualiInteger getTargetBaseBudgetIncomeTotal() {
        KualiInteger total = new KualiInteger(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isIncome(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }
    
    /**
     * Returns the total base budget expense amount from the target lines.
     */
    public KualiInteger getTargetBaseBudgetExpenseTotal() {
        KualiInteger total = new KualiInteger(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getTotalDollarAmount()
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        // TODO Auto-generated method stub
        return super.getTotalDollarAmount();
    }

    /**
     * Negate accounting line budget amounts.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#convertIntoErrorCorrection()
     */
    @Override
    public void convertIntoErrorCorrection() throws WorkflowException {
        super.convertIntoErrorCorrection();

        if (this.getSourceAccountingLines() != null) {
            for (Iterator iter = this.getSourceAccountingLines().iterator(); iter.hasNext();) {
                BudgetAdjustmentAccountingLine sourceLine = (BudgetAdjustmentAccountingLine) iter.next();
                sourceLine.setBaseBudgetAdjustmentAmount(sourceLine.getBaseBudgetAdjustmentAmount().negated());
                sourceLine.setCurrentBudgetAdjustmentAmount(sourceLine.getCurrentBudgetAdjustmentAmount().negated());
                sourceLine.setFinancialDocumentMonth1LineAmount(sourceLine.getFinancialDocumentMonth1LineAmount().negated());
                sourceLine.setFinancialDocumentMonth2LineAmount(sourceLine.getFinancialDocumentMonth2LineAmount().negated());
                sourceLine.setFinancialDocumentMonth3LineAmount(sourceLine.getFinancialDocumentMonth3LineAmount().negated());
                sourceLine.setFinancialDocumentMonth4LineAmount(sourceLine.getFinancialDocumentMonth4LineAmount().negated());
                sourceLine.setFinancialDocumentMonth5LineAmount(sourceLine.getFinancialDocumentMonth5LineAmount().negated());
                sourceLine.setFinancialDocumentMonth6LineAmount(sourceLine.getFinancialDocumentMonth6LineAmount().negated());
                sourceLine.setFinancialDocumentMonth7LineAmount(sourceLine.getFinancialDocumentMonth7LineAmount().negated());
                sourceLine.setFinancialDocumentMonth8LineAmount(sourceLine.getFinancialDocumentMonth8LineAmount().negated());
                sourceLine.setFinancialDocumentMonth9LineAmount(sourceLine.getFinancialDocumentMonth9LineAmount().negated());
                sourceLine.setFinancialDocumentMonth10LineAmount(sourceLine.getFinancialDocumentMonth10LineAmount().negated());
                sourceLine.setFinancialDocumentMonth11LineAmount(sourceLine.getFinancialDocumentMonth11LineAmount().negated());
                sourceLine.setFinancialDocumentMonth12LineAmount(sourceLine.getFinancialDocumentMonth12LineAmount().negated());
            }
        }

        if (this.getTargetAccountingLines() != null) {
            for (Iterator iter = this.getTargetAccountingLines().iterator(); iter.hasNext();) {
                BudgetAdjustmentAccountingLine targetLine = (BudgetAdjustmentAccountingLine) iter.next();
                targetLine.setBaseBudgetAdjustmentAmount(targetLine.getBaseBudgetAdjustmentAmount().negated());
                targetLine.setCurrentBudgetAdjustmentAmount(targetLine.getCurrentBudgetAdjustmentAmount().negated());
                targetLine.setFinancialDocumentMonth1LineAmount(targetLine.getFinancialDocumentMonth1LineAmount().negated());
                targetLine.setFinancialDocumentMonth2LineAmount(targetLine.getFinancialDocumentMonth2LineAmount().negated());
                targetLine.setFinancialDocumentMonth3LineAmount(targetLine.getFinancialDocumentMonth3LineAmount().negated());
                targetLine.setFinancialDocumentMonth4LineAmount(targetLine.getFinancialDocumentMonth4LineAmount().negated());
                targetLine.setFinancialDocumentMonth5LineAmount(targetLine.getFinancialDocumentMonth5LineAmount().negated());
                targetLine.setFinancialDocumentMonth6LineAmount(targetLine.getFinancialDocumentMonth6LineAmount().negated());
                targetLine.setFinancialDocumentMonth7LineAmount(targetLine.getFinancialDocumentMonth7LineAmount().negated());
                targetLine.setFinancialDocumentMonth8LineAmount(targetLine.getFinancialDocumentMonth8LineAmount().negated());
                targetLine.setFinancialDocumentMonth9LineAmount(targetLine.getFinancialDocumentMonth9LineAmount().negated());
                targetLine.setFinancialDocumentMonth10LineAmount(targetLine.getFinancialDocumentMonth10LineAmount().negated());
                targetLine.setFinancialDocumentMonth11LineAmount(targetLine.getFinancialDocumentMonth11LineAmount().negated());
                targetLine.setFinancialDocumentMonth12LineAmount(targetLine.getFinancialDocumentMonth12LineAmount().negated());
            }
        }
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialDocumentNumber", this.financialDocumentNumber);
        return m;
    }

    @Override
    public Class getSourceAccountingLineClass() {
        return BudgetAdjustmentSourceAccountingLine.class;
    }

    @Override
    public Class getTargetAccountingLineClass() {
        return BudgetAdjustmentTargetAccountingLine.class;
    }

}
