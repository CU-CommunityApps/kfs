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
package org.kuali.module.financial.rules;

import static org.kuali.module.financial.rules.InternalBillingDocumentRuleConstants.INTERNAL_BILLING_DOCUMENT_SECURITY_GROUPING;
import static org.kuali.module.financial.rules.InternalBillingDocumentRuleConstants.RESTRICTED_SUB_FUND_GROUP_CODES;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.POSITIVE;

import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.document.InternalBillingDocument;
import org.kuali.test.KualiTestBaseWithFixtures;

/**
 * This class tests the business rules of the internal billing document. This is not implemented yet and needs to extend
 * TransactionalDocumentRuleTestBase. We'll fully implement this when we get to this document during development.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class InternalBillingDocumentRuleTest extends KualiTestBaseWithFixtures {

    // ////////////////////////////////////////////////////////////////////////
    // Test methods start here //
    // ////////////////////////////////////////////////////////////////////////

    public final void testSave_nullDocument() throws Exception {
        boolean failedAsExpected = false;

        try {
            SpringServiceLocator.getDocumentService().saveDocument(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testIsSubFundGroupAllowed_true() {
        AccountingLine line = createLineFromFixture("expenseSourceLine");
        line.refresh();
        assertGlobalErrorMapEmpty();
        boolean actual = new InternalBillingDocumentRule().isSubFundGroupAllowed(line);
        assertGlobalErrorMapEmpty();
        assertEquals(true, actual);
    }

    public final void testIsSubFundGroupAllowed_false() {
        AccountingLine line = createLineFromFixture("pfipSubFundSourceLine");
        line.refresh();
        assertGlobalErrorMapEmpty();
        boolean actual = new InternalBillingDocumentRule().isSubFundGroupAllowed(line);
        assertGlobalErrorMapContains(PropertyConstants.ACCOUNT_NUMBER, KeyConstants.ERROR_APC_INDIRECT_DENIED_MULTIPLE, new String[] { INTERNAL_BILLING_DOCUMENT_SECURITY_GROUPING + ":" + RESTRICTED_SUB_FUND_GROUP_CODES, null, // ignore
                // source
                // line
                // number
                // since
                // it
                // will
                // often
                // change
                "Account Number", "9544900", "Sub-Fund Group Code", "PFIP", "PFRI;PFIP" });
        assertEquals(false, actual);
    }

    private AccountingLine createLineFromFixture(String accountingLineFixtureName) {
        return (AccountingLine) getFixtureEntry(accountingLineFixtureName).createObject();
    }

    /**
     * tests false is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false ir returned for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests afalse is returned for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), InternalBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }


    /*
     * TODO Commented out until we get data in the db that matches what we need, or we can modify the objects directly
     * (mock-testing) public final void testApplyAddAccountingLineBusinessRulesInvalidStudentFeeContinueEduc() throws Exception {
     * triggerInvalidAddAccountingLineEvents ( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents ( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidStudentFeeContinueEduc() throws Exception {
     * triggerValidAddAccountingLineEvents ( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents ( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidCapObjCodes() throws Exception {
     * triggerValidAddAccountingLineEvents ( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents ( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidCapObjCodes() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); } public final void
     * testApplyAddAccountingLineBusinessRulesInvalidConsolidatedObjCode() throws Exception { triggerValidAddAccountingLineEvents(
     * getTransactionalDocument().getSourceAccountingLines() ); triggerInvalidAddAccountingLineEvents(
     * getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidConsolidatedObjCode() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidObjCodeSubTypes() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidObjCodeSubTypes() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidObjLevelCode() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidObjLevelCode() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidObjType() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidObjType() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidObjSubTypeAssessOrTransOfFunds() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidObjSubTypeAssessOrTransOfFunds() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidFundGroup() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidFundGroup() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidSubFundGroup() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidSubFundGroup() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     */

    /*
     * This following block of commented out code contains valuable data that should be used for this rule class when we get to this
     * document. This data needs to be transformed into the new xml based fixtures framework.
     * 
     * public void fixturesDefault() {
     * 
     * addFixture( KualiRuleTestCase.ACCOUNT, "1912610" ); addFixture( KualiRuleTestCase.BALANCE_TYPE,
     * TransactionalDocumentRuleBase.BALANCE_TYPE_CODE.ACTUAL ); addFixture( KualiRuleTestCase.CHART, "UA" ); addFixture(
     * KualiRuleTestCase.OBJECT_TYPE_CODE, TransactionalDocumentRuleBase.OBJECT_TYPE_CODE.CASH_NOT_INCOME ); addFixture(
     * KualiRuleTestCase.POSTING_YEAR, new Integer( 2005 ) ); addFixture( KualiRuleTestCase.PROJECT, "BOB" ); addFixture(
     * KualiRuleTestCase.SUBACCOUNT, "BEER" ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidStudentFeeContinueEduc() throws Exception { List sourceLines =
     * new ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     * //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     * //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     * 
     * targetLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidStudentFeeContinueEduc() throws Exception { List sourceLines =
     * new ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES); SubFundGroup sfg = new
     * SubFundGroup(); sfg.setSubFundGroupCode(SUB_FUND_GROUP_CODE.CONTINUE_EDUC); sourceLine1.getAccount().setSubFundGroup(sfg);
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     * //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     * 
     * targetLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidCapObjCodes() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine3 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine4 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine5 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine6 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine7 = fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
     * sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
     * sourceLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
     * sourceLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     * sourceLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
     * sourceLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_FUND);
     * sourceLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
     * 
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); sourceLines.add(sourceLine3); sourceLines.add(sourceLine4);
     * sourceLines.add(sourceLine5); sourceLines.add(sourceLine6); sourceLines.add(sourceLine7); addFixture(
     * KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine3 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine4 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine5 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine6 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine7 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
     * targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
     * targetLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
     * targetLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     * targetLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
     * targetLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_FUND);
     * targetLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); targetLines.add(targetLine3); targetLines.add(targetLine4);
     * targetLines.add(targetLine5); targetLines.add(targetLine6); targetLines.add(targetLine7); addFixture(
     * KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidCapObjCodes() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidConsolidatedObjCode() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * ObjectCons consolidatedObj1 = new ObjectCons();
     * consolidatedObj1.setFinConsolidationObjectCode(CONSOLIDATED_OBJECT_CODE.ASSETS); ObjectCons consolidatedObj2 = new
     * ObjectCons(); consolidatedObj2.setFinConsolidationObjectCode(CONSOLIDATED_OBJECT_CODE.LIABILITIES);
     * 
     * sourceLine1.getObjectCode().getFinancialObjectLevel().setFinancialConsolidationObject(consolidatedObj1);
     * sourceLine2.getObjectCode().getFinancialObjectLevel().setFinancialConsolidationObject(consolidatedObj2);
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES,
     * sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().getFinancialObjectLevel().setFinancialConsolidationObject(consolidatedObj1);
     * targetLine2.getObjectCode().getFinancialObjectLevel().setFinancialConsolidationObject(consolidatedObj2);
     * 
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES,
     * targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjCodeSubTypes() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine3 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine4 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine5 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine6 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine7 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine8 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine9 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine10 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine11 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine12 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine13 = fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
     * sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     * sourceLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
     * sourceLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
     * sourceLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.RESERVES);
     * sourceLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
     * sourceLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
     * sourceLine8.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STATE_APP);
     * sourceLine9.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES);
     * sourceLine10.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.PLANT);
     * sourceLine11.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.INVEST);
     * sourceLine12.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.FRINGE_BEN);
     * sourceLine13.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.NON_MANDATORY_TRANSFER);
     * 
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); sourceLines.add(sourceLine3); sourceLines.add(sourceLine4);
     * sourceLines.add(sourceLine5); sourceLines.add(sourceLine6); sourceLines.add(sourceLine7); sourceLines.add(sourceLine8);
     * sourceLines.add(sourceLine9); sourceLines.add(sourceLine10); sourceLines.add(sourceLine11); sourceLines.add(sourceLine12);
     * sourceLines.add(sourceLine13); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine3 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine4 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine5 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine6 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine7 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine8 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine9 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine10 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine11 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine12 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine13 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
     * targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     * targetLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
     * targetLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
     * targetLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.RESERVES);
     * targetLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
     * targetLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
     * targetLine8.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STATE_APP);
     * targetLine9.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES);
     * targetLine10.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.PLANT);
     * targetLine11.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.INVEST);
     * targetLine12.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.FRINGE_BEN);
     * targetLine13.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.NON_MANDATORY_TRANSFER);
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); targetLines.add(targetLine3); targetLines.add(targetLine4);
     * targetLines.add(targetLine5); targetLines.add(targetLine6); targetLines.add(targetLine7); targetLines.add(targetLine8);
     * targetLines.add(targetLine9); targetLines.add(targetLine10); targetLines.add(targetLine11); targetLines.add(targetLine12);
     * targetLines.add(targetLine13); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidObjCodeSubTypes() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjLevelCode() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); ObjLevel level = new
     * ObjLevel(); level.setFinancialObjectLevelCode(OBJECT_LEVEL_CODE.CONTRACT_GRANTS);
     * sourceLine1.getObjectCode().setFinancialObjectLevel(level);
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().setFinancialObjectLevel(level);
     * 
     * 
     * targetLines.add( targetLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidObjLevelCode() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjType() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" );
     * sourceLine1.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.INCOME_NOT_CASH);
     * sourceLine2.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE);
     * 
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES,
     * sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" );
     * targetLine1.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.INCOME_NOT_CASH);
     * targetLine2.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE);
     * 
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES,
     * targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidObjType() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjSubTypeAssessOrTransOfFunds() throws Exception { List
     * sourceLines = new ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
     * sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
     * 
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES,
     * sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
     * targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES,
     * targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidObjSubTypeAssessOrTransOfFunds() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidFundGroup() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * //This sub fund group needs to have a fund group //code of FUND_GROUP_CODE.LOAN_FUND This may not be necessary if we have an
     * appropriate account BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService(); String
     * subFundGroupCode = "SOME_CODE"; Map subFundGroupId = new HashMap(); subFundGroupId.put("code", subFundGroupCode);
     * SubFundGroup sfg = (SubFundGroup) boService.findByPrimaryKey(SubFundGroup.class, subFundGroupId);
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLines.add( targetLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidSubFundGroup() throws Exception { //Doing this test requires
     * grabbing //SUB_FUND_GROUP_CODE.CODE_RETIRE_INDEBT.equals(subFundGroup) //||
     * SUB_FUND_GROUP_CODE.CODE_INVESTMENT_PLANT.equals(subFundGroup) List sourceLines = new ArrayList(); List targetLines = new
     * ArrayList();
     * 
     * //This sub fund group needs to have a fund group //code of FUND_GROUP_CODE.LOAN_FUND This may not be necessary if we have an
     * appropriate account BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService(); String
     * subFundGroupCode = "SOME_CODE"; Map subFundGroupId = new HashMap(); subFundGroupId.put("code", subFundGroupCode);
     * SubFundGroup sfg = (SubFundGroup) boService.findByPrimaryKey(SubFundGroup.class, subFundGroupId);
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLines.add( targetLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     */
}
