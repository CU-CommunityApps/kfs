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
package org.kuali.test.fixtures;

import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

public enum GeneralLedgerPendingEntryFixture {
    EXPECTED_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE("UA", "1912201", "BEER", "D", "AC", "TF", "TE", "9900", false, "BOB"),
    EXPECTED_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE(null, null, "BEER", "C", "AC", "TF", "TE", "9900", false, "BOB"),
    EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE(null, null, null, "C", "AC", "GEC", "EX", "1940", false, "BOB"),
    EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE(null, null, null, "D", "AC", "GEC", "EX", "1940", false, "BOB"),
    EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY(null, null, null, "C", "AC", "GEC", "AS", "8111", false, "BOB"),
    EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY(null, null, null, "D", "AC", "GEC", "AS", "8111", false, "BOB"),
    EXPECTED_GEC_OFFSET_SOURCE_PENDING_ENTRY("BA", "6044900", null, "D", "AC", "GEC", "AS", "8000", true, "BOB"),
    EXPECTED_GEC_OFFSET_TARGET_PENDING_ENTRY(null, null, null, "C", "AC", "GEC", "AS", "8000", true, "BOB"),
    EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE(null, null, "BEER", "D", "AC", "JV", "EX", "9900", false, "BOB"),
    EXPECTED_JV_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE(null, null, null, "D", "AC", "JV", "EX", "9900", false, null),
    EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY(null, null, null, "D", "AC", "JV", "AS", "9980", false, "BOB"),
    EXPECTED_JV_EXPLICIT_TARGET_PENDING_ENTRY(null, null, null, "D", "AC", "JV", "TI", "9980", false, null),
    EXPECTED_OFFSET_SOURCE_PENDING_ENTRY("UA", "1912201", "BEER", "C", "AC", "TF", "AS", "8000", true, "BOB"),
    EXPECTED_OFFSET_TARGET_PENDING_ENTRY(null, null, "BEER", "D", "AC", "TF", "AS", "8000", true, "BOB"),
    EXPECTED_EXPLICIT_SOURCE_PENDING_ENTRY(null, null, null, "D", "AC", "TF", "AS", "9980", false, null),
    EXPECTED_EXPLICIT_TARGET_PENDING_ENTRY(null, null, null, "C", "AC", "TF", "AS", "9980", false, null),
    EXPECTED_FLEXIBLE_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE2("BL", "2231401", null, "C", "AC", "TF", "TE", "9900", false, "BOB"),
    EXPECTED_FLEXIBLE_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE("BL", "2231401", null, "D", "AC", "TF", "TE", "9900", false, "BOB"),
    EXPECTED_FLEXIBLE_OFFSET_SOURCE_PENDING_ENTRY("UA", "1912201", null, "D", "AC", "TF", "AS", "8000", true, "BOB"),
    EXPECTED_FLEXIBLE_OFFSET_SOURCE_PENDING_ENTRY_MISSING_OFFSET_DEFINITION("BL", "2231401", null, "C", "AC", "TF", "--", "----", true, "BOB"),
    EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE(null, null, "BEER", "D", null, "AVAD", "ES", "9900", false, "BOB"),
    EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE(null, null, null, "D", "AC", "AVAD", "ES", "1940", false, "BOB"),
    EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY(null, null, null, "D", "AC", "AVAD", "AS", "8111", false, "BOB"),
    EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY(null, null, null, "D", "AC", "AVAD", "AS", "8111", false, "BOB"),
    ;

    public final String chartOfAccountsCode;
    public final String accountNumber;
    public final String subAccountNumber;
    public final String transactionDebitCreditCode;
    public final String financialBalanceTypeCode;
    public final String financialDocumentTypeCode;
    public final String financialObjectTypeCode;
    public final String financialObjectCode;
    public final boolean transactionEntryOffsetIndicator;
    public final String projectCode;


    private GeneralLedgerPendingEntryFixture(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String transactionDebitCreditCode, String financialBalanceTypeCode, String financialDocumentTypeCode, String financialObjectTypeCode, String financialObjectCode, boolean transactionEntryOffsetIndicator, String projectCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.subAccountNumber = subAccountNumber;
        this.transactionDebitCreditCode = transactionDebitCreditCode;
        this.financialBalanceTypeCode = financialBalanceTypeCode;
        this.financialDocumentTypeCode = financialDocumentTypeCode;
        this.financialObjectTypeCode = financialObjectTypeCode;
        this.financialObjectCode = financialObjectCode;
        this.transactionEntryOffsetIndicator = transactionEntryOffsetIndicator;
        this.projectCode = projectCode;
    }


    public GeneralLedgerPendingEntry createGeneralLedgerPendingEntry() {
        GeneralLedgerPendingEntry glpe = new GeneralLedgerPendingEntry();
        glpe.setChartOfAccountsCode(this.chartOfAccountsCode);
        glpe.setAccountNumber(this.accountNumber);
        glpe.setSubAccountNumber(this.subAccountNumber);
        glpe.setTransactionDebitCreditCode(this.transactionDebitCreditCode);
        glpe.setFinancialBalanceTypeCode(this.financialBalanceTypeCode);
        glpe.setFinancialDocumentTypeCode(this.financialDocumentTypeCode);
        glpe.setFinancialObjectTypeCode(this.financialObjectTypeCode);
        glpe.setFinancialObjectCode(this.financialObjectCode);
        glpe.setTransactionEntryOffsetIndicator(transactionEntryOffsetIndicator);
        glpe.setProjectCode(this.projectCode);

        return glpe;
    }

}
