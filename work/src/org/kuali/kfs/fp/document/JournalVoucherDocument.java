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
package org.kuali.module.financial.document;

import java.sql.Timestamp;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.financial.bo.JournalVoucherAccountingLineParser;

/**
 * This is the business object that represents the JournalVoucherDocument in Kuali. This is a transactional document that will
 * eventually post transactions to the G/L. It integrates with workflow and contains a single group of accounting lines. The Journal
 * Voucher is unique in that we only make use of one accounting line list: the source accounting lines seeing as a JV only records
 * accounting lines as debits or credits.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class JournalVoucherDocument extends TransactionalDocumentBase {
    private static final long serialVersionUID = 1452941605423073277L;
    // document specific attributes
    private String balanceTypeCode; // balanceType key
    private BalanceTyp balanceType;
    private Timestamp reversalDate;

    /**
     * Constructs a JournalVoucherDocument instance.
     */
    public JournalVoucherDocument() {
        super();
        this.balanceType = new BalanceTyp();
    }

    /**
     * This method retrieves the balance typ associated with this document.
     * 
     * @return
     */
    public BalanceTyp getBalanceType() {
        return balanceType;
    }

    /**
     * This method sets the balance type associated with this document.
     * 
     * @param balanceType
     * @deprecated
     */
    public void setBalanceType(BalanceTyp balanceType) {
        this.balanceType = balanceType;
    }

    /**
     * Gets the balanceTypeCode attribute.
     * 
     * @return Returns the balanceTypeCode.
     */
    public String getBalanceTypeCode() {
        return balanceTypeCode;
    }

    /**
     * Sets the balanceTypeCode attribute value.
     * 
     * @param balanceTypeCode The balanceTypeCode to set.
     */
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.balanceTypeCode = balanceTypeCode;
    }

    /**
     * This method retrieves the reversal date associated with this document.
     * 
     * @return
     */
    public Timestamp getReversalDate() {
        return reversalDate;
    }

    /**
     * This method sets the reversal date associated with this document.
     * 
     * @param reversalDate
     */
    public void setReversalDate(Timestamp reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Overrides the base implementation to return an empty string.
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }

    /**
     * Overrides the base implementation to return an empty string.
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.EMPTY_STRING;
    }

//    /**
//     * Override with nothing since the JV doesn't do org or account based routing.
//     * 
//     * @see org.kuali.core.document.Document#populateDocumentForRouting()
//     */
//    public void populateDocumentForRouting() {
//    }

    /**
     * This method calculates the debit total for a JV document keying off of the debit/debit code, only summing the accounting
     * lines with a debitDebitCode that matched the debit constant, and returns the results.
     * 
     * @return
     */
    public KualiDecimal getDebitTotal() {
        KualiDecimal debitTotal = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if (StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(Constants.GL_DEBIT_CODE)) {
                debitTotal = debitTotal.add(al.getAmount());
            }
        }
        
        return debitTotal;
    }

    /**
     * This method calculates the credit total for a JV document keying off of the debit/credit code, only summing the accounting
     * lines with a debitCreditCode that matched the debit constant, and returns the results.
     * 
     * @return
     */
    public KualiDecimal getCreditTotal() {
        KualiDecimal creditTotal = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = sourceAccountingLines.iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();
            if (StringUtils.isNotBlank(al.getDebitCreditCode()) && al.getDebitCreditCode().equals(Constants.GL_CREDIT_CODE)) {
                creditTotal = creditTotal.add(al.getAmount());
            }
        }
        return creditTotal;
    }

    /**
     * This method determines the "total" for the JV document.  If the selected balance type is 
     * an offset generation, then the total is calculated by subtracting the debit accounting lines 
     * from the credit accounting lines.  Otherwise, the total is just the sum of all accounting line 
     * amounts. 
     * 
     * @return KualiDecimal the total of the JV document.
     */
    public KualiDecimal getTotal() {
        
        KualiDecimal total = new KualiDecimal(0);
        AccountingLineBase al = null;
        
        this.refreshReferenceObject("balanceType");
        
        if(this.balanceType.isFinancialOffsetGenerationIndicator()) {  // credits and debits mode
            total = getCreditTotal().subtract(getDebitTotal());
        } else { // single amount mode
            Iterator iter = sourceAccountingLines.iterator();
            while (iter.hasNext()) {
                al = (AccountingLineBase) iter.next();
                total = total.add(al.getAmount());
            }
        }
        return total;
    }

    /**
     * Used to get the appropriate <code>{@link AccountingLineParser}</code> for the <code>Document</code>
     * 
     * @return AccountingLineParser
     */
    public AccountingLineParser getAccountingLineParser() {
        return new JournalVoucherAccountingLineParser();
    }
}