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

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.TransactionalDocumentRuleTestBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.GeneralErrorCorrectionDocument;
import org.kuali.module.financial.rules.GeneralErrorCorrectionDocumentRule;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;


/**
 * This class tests the General Error Correction Document's persistence, 
 * routing, and PE generation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class GeneralErrorCorrectionDocumentRuleTest 
    extends TransactionalDocumentRuleTestBase {

    private static final String COLLECTION_NAME = "GeneralErrorCorrectionDocumentRuleTest.collection1";
    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiGeneralErrorCorrectionDocument";

    private static final String[] FIXTURE_COLLECTION_NAMES = { COLLECTION_NAME };

    private TransactionalDocumentParameter _docParam2;
    private TransactionalDocumentParameter _docParam1;
    private TransactionalDocumentParameter _gecParam;
    private AccountingLineParameter _sourceLine1;
    private AccountingLineParameter _sourceLine2;
    private AccountingLineParameter _sourceLine3;
    private AccountingLineParameter _sourceLine4;
    private AccountingLineParameter _sourceLine5;
    private AccountingLineParameter _sourceLine6;
    private AccountingLineParameter _assetSourceLine;
    private AccountingLineParameter _targetLine1;
    private AccountingLineParameter _targetLine2;
    private AccountingLineParameter _targetLine3;
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntryExpense;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntryExpense;
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntry;
    private GeneralLedgerPendingEntry _expectedOffSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedOffTargetGlEntry;


    public String[] getFixtureCollectionNames() {
        return FIXTURE_COLLECTION_NAMES;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fixture Methods Start Here                                            //
    ///////////////////////////////////////////////////////////////////////////
    protected final String getDocumentTypeName() {
        return KNOWN_DOCUMENT_TYPENAME;
    }

    public final TargetAccountingLine getAssetTargetLine() throws Exception {
        return (TargetAccountingLine) getAccruedIncomeTargetLineParameter().createLine();
    }

    protected final TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) getAccruedIncomeTargetLineParameter().createLine();
    }

    protected final TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) getCashTargetLineParameter().createLine();
    }

    protected final List getValidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getAccruedIncomeSourceLineParameter().createLine());
        retval.add(getAccruedIncomeSourceLineParameter().createLine());
        return retval;
    }

    protected final List getInvalidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getCashSourceLineParameter().createLine());
        retval.add(getLossOnRetireSourceLineParameter().createLine());
        return retval;
    }

    protected final List getInvalidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getCashTargetLineParameter().createLine());
        retval.add(getLossOnRetireTargetLineParameter().createLine());
        return retval;
    }

    protected final List getValidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getAccruedIncomeTargetLineParameter().createLine());
        retval.add(getAccruedIncomeTargetLineParameter().createLine());
        return retval;
    }

    protected final SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter4().createLine();
    }

    protected final SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter3().createLine();
    }

    protected final SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter5().createLine();
    }

    protected final SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return (SourceAccountingLine) getAccruedIncomeSourceLineParameter().createLine();
    }

    public final SourceAccountingLine getAssetSourceLine() {
        return (SourceAccountingLine) 
            getAccruedIncomeSourceLineParameter().createLine();
    }

    protected final Document createDocument() throws Exception {
        return getGeneralErrorCorrectionDocument()
            .createDocument(getDocumentService());
    }

    protected final TransactionalDocument createDocument5() throws Exception {
        return (TransactionalDocument) getGeneralErrorCorrectionDocument().createDocument(getDocumentService());
    }

    protected final Document createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    protected final Document createDocumentInvalidForSave() throws Exception {
        return getDocumentParameterNoDescription().createDocument(getDocumentService());
    }

    protected final TransactionalDocument createDocumentWithInvalidObjectSubType() throws Exception {
        GeneralErrorCorrectionDocument retval = 
            (GeneralErrorCorrectionDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getInvalidObjectSubTypeTargetLines());
        return retval;
    }

    protected final TransactionalDocument createDocumentUnbalanced() throws Exception {
        GeneralErrorCorrectionDocument retval = 
            (GeneralErrorCorrectionDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.addTargetAccountingLine(getValidObjectSubTypeTargetLine());
        return retval;
    }

    protected final Document createDocumentInvalidDescription() throws Exception {
        return getDocumentParameterNoDescription().createDocument(getDocumentService());
    }

    protected final TransactionalDocument createDocumentWithValidObjectSubType() throws Exception {
        GeneralErrorCorrectionDocument retval = 
            (GeneralErrorCorrectionDocument) createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        return retval;
    }

    /**
     * Accessor for fixture 'sourceLine1'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getCashSourceLineParameter() {
        return _sourceLine1;
    }

    /**
     * Accessor for fixture 'sourceLine1'
     * 
     * @param p AccountingLineParameter
     */
    public final void setCashSourceLineParameter(AccountingLineParameter p) {
        _sourceLine1 = p;
    }

    /**
     * Accessor for fixture 'sourceLine2'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getLossOnRetireSourceLineParameter() {
        return _sourceLine2;
    }

    /**
     * Accessor for fixture 'sourceLine2'
     * 
     * @param p AccountingLineParameter
     */
    public final void setLossOnRetireSourceLineParameter(AccountingLineParameter p) {
        _sourceLine2 = p;
    }

    /**
     * Accessor for fixture 'sourceLine3'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter3() {
        return _sourceLine3;
    }

    /**
     * Accessor for fixture 'sourceLine3'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter3(AccountingLineParameter p) {
        _sourceLine3 = p;
    }

    /**
     * Accessor for fixture 'sourceLine4'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter4(AccountingLineParameter p) {
        _sourceLine4 = p;
    }

    /**
     * Accessor for fixture 'sourceLine4'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter4() {
        return _sourceLine4;
    }

    /**
     * Accessor for fixture 'sourceLine5'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter5() {
        return _sourceLine5;
    }

    /**
     * Accessor for fixture 'sourceLine5'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter5(AccountingLineParameter p) {
        _sourceLine5 = p;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getAccruedIncomeSourceLineParameter() {
        return _sourceLine6;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @param p AccountingLineParameter
     */
    public final void setAccruedIncomeSourceLineParameter(AccountingLineParameter p) {
        _sourceLine6 = p;
    }

    /**
     * Accessor for fixture 'targetLine1'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getCashTargetLineParameter() {
        return _targetLine1;
    }

    /**
     * Accessor for fixture 'targetLine1'
     * 
     * @param p AccountingLineParameter
     */
    public final void setCashTargetLineParameter(AccountingLineParameter p) {
        _targetLine1 = p;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getAccruedIncomeTargetLineParameter() {
        return _targetLine2;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @param p AccountingLineParameter
     */
    public final void setAccruedIncomeTargetLineParameter(AccountingLineParameter p) {
        _targetLine2 = p;
    }

    /**
     * Accessor for fixture 'targetLine3'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getLossOnRetireTargetLineParameter() {
        return _targetLine3;
    }

    /**
     * Accessor for fixture 'targetLine3'
     * 
     * @param p AccountingLineParameter
     */
    public final void setLossOnRetireTargetLineParameter(AccountingLineParameter p) {
        _targetLine3 = p;
    }

    public final TransactionalDocumentParameter getDocumentParameter1() {
        return _docParam1;
    }

    public final void setDocumentParameter1(TransactionalDocumentParameter p) {
        _docParam1 = p;
    }

    public final TransactionalDocumentParameter getDocumentParameterNoDescription() {
        return _docParam2;
    }

    public final void setDocumentParameterNoDescription(TransactionalDocumentParameter p) {
        _docParam2 = p;
    }

    /**
     * Fixture method to get a 
     * <code>{@link TransactionalDocumentParameter}</code> instance for
     * <code>{@link GeneralErrorCorrectionDocument}</code>.
     * 
     * @return TransactionalDocumentParameter
     */
    public final TransactionalDocumentParameter getGeneralErrorCorrectionDocument() {
        return _gecParam;
    }

    /**
     * Fixture method to assign to the test a 
     * <code>{@link TransactionalDocumentParameter}</code> instance for
     * <code>{@link GeneralErrorCorrectionDocument}</code>.
     * 
     * @param p
     */
    public final void setGeneralErrorCorrectionDocument(TransactionalDocumentParameter p) {
        _gecParam = p;
    }

    /**
     * Accessor method for Explicit Source fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return getExpectedGECExplicitSourcePendingEntry();
    }

    /**
     * Accessor method for Explicit Source fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECExplicitSourcePendingEntry() {
        return _expectedExpSourceGlEntry;
    }

    /**
     * Accessor method for Explicit Source fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedGECExplicitSourcePendingEntry(GeneralLedgerPendingEntry e) {
        _expectedExpSourceGlEntry = e;
    }

    /**
     * Accessor method for Explicit Source fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECExplicitSourcePendingEntryForExpense() {
        return _expectedExpSourceGlEntryExpense;
    }

    /**
     * Accessor method for Explicit Source fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedGECExplicitSourcePendingEntryForExpense(GeneralLedgerPendingEntry e) {
        _expectedExpSourceGlEntryExpense = e;
    }

    /**
     * Accessor method for Explicit Target fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return getExpectedGECExplicitTargetPendingEntry();
    }

    /**
     * Accessor method for Explicit Target fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECExplicitTargetPendingEntry() {
        return _expectedExpTargetGlEntry;
    }

    /**
     * Accessor method for Explicit Target fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedGECExplicitTargetPendingEntry(GeneralLedgerPendingEntry e) {
        _expectedExpTargetGlEntry = e;
    }


    /**
     * Accessor method for Explicit Target fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECExplicitTargetPendingEntryForExpense() {
        return _expectedExpTargetGlEntryExpense;
    }

    /**
     * Accessor method for Explicit Target fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedGECExplicitTargetPendingEntryForExpense(GeneralLedgerPendingEntry e) {
        _expectedExpTargetGlEntryExpense = e;
    }

    /**
     * Accessor method for Offset Target fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECOffsetTargetPendingEntry() {
        return _expectedOffTargetGlEntry;
    }

    /**
     * Accessor method for Offset Target fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return getExpectedGECOffsetTargetPendingEntry();
    }

    /**
     * Accessor method for Offset Target fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedGECOffsetTargetPendingEntry(GeneralLedgerPendingEntry e) {
        _expectedOffTargetGlEntry = e;
    }

    /**
     * Accessor method for Offset Source fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedGECOffsetSourcePendingEntry(GeneralLedgerPendingEntry e) {
        _expectedOffSourceGlEntry = e;
    }

    /**
     * Accessor method for Offset Source fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECOffsetSourcePendingEntry() {
        return _expectedOffSourceGlEntry;
    }

    /**
     * Accessor method for Offset Source fixture used for <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return getExpectedGECOffsetSourcePendingEntry();
    }

    /**
     * Accessor method for Explicit Source fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntryForExpense() {
        return getExpectedGECExplicitSourcePendingEntryForExpense();
    }

    /**
     * Accessor method for Explicit Source fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntryForExpense() {
        return getExpectedGECExplicitTargetPendingEntryForExpense();
    }


    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleBase#getExpenseSourceLine()
     */
    protected AccountingLine getExpenseSourceLine() {
        return createLineFromFixture("expenseGECSourceLine");
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleBase#getExpenseTargetLine()
     */
    protected AccountingLine getExpenseTargetLine() {
        return createLineFromFixture("expenseGECTargetLine");
    }
    
    /**
     * Fixture method that returns a <code>{@link SourceAccountingLine}</code> 
     * for sufficient funds checking of an expense source line
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingSourceExpense() {
        return getAccruedIncomeSourceLineParameter();
    }

    /**
     * Fixture method that returns a <code>{@link SourceAccountingLine}</code> 
     * for sufficient funds checking of an asset source line
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingSourceAsset() {
        return getAccruedIncomeSourceLineParameter();
    }

    /**
     * Fixture method that returns a <code>{@link SourceAccountingLine}</code> 
     * for sufficient funds checking of a liability source line
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingSourceLiability() {
        return null;
    }

    /**
     * Fixture method that returns a <code>{@link SourceAccountingLine}</code> 
     * for sufficient funds checking of an income source line
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingSourceIncome() {
        return null;
    }

    /**
     * Fixture method that returns a <code>{@link TargetAccountingLine}</code> 
     * for sufficient funds checking of an expense source line
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingTargetExpense() {
        return getAccruedIncomeTargetLineParameter();
    }

    /**
     * Fixture method that returns a <code>{@link TargetAccountingLine}</code> 
     * for sufficient funds checking of an asset source line
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingTargetAsset() {
        return getAccruedIncomeTargetLineParameter();
    }

    /**
     * Fixture method that returns a <code>{@link TargetAccountingLine}</code> 
     * for sufficient funds checking of an liability source line
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingTargetLiability() {
        return null;
    }

    /**
     * Fixture method that returns a <code>{@link TargetAccountingLine}</code> 
     * for sufficient funds checking of an income source line
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingTargetIncome() {
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fixture Methods End Here                                              //
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Test Methods Start Here                                               //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testAddAccountingLine_InvalidObjectSubType()
     */
    public void testAddAccountingLine_InvalidObjectSubType()
        throws Exception {
        TransactionalDocument doc = createDocumentWithInvalidObjectSubType();
        testAddAccountingLine(doc, false);
    }

    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isExpense_postive_lineAmount() {
        SourceAccountingLine line = (SourceAccountingLine)
            getSufficientFundsCheckingSourceExpense().createLine();
        line.setAmount(new KualiDecimal("3.0"));

        GeneralErrorCorrectionDocumentRule rule = 
            new GeneralErrorCorrectionDocumentRule();
        SufficientFundsItem item = 
            rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
    }

    /**
     * These tests currently don't work. Something should be done during QA 
     * Period to get them to work
    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isAsset__negative_lineAmount() {
        SourceAccountingLine line = (SourceAccountingLine) 
            getSufficientFundsCheckingSourceAsset().createLine();
        line.setAmount(new KualiDecimal("-3.0"));

        GeneralErrorCorrectionDocumentRule rule = 
            new GeneralErrorCorrectionDocumentRule();
        SufficientFundsItem item = 
            rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
        assertFalse(line.getAmount().equals(item.getAmount()));
        assertTrue(line.getAmount().abs().equals(item.getAmount()));
    }

    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isIncome_postive_lineAmount() {
        SourceAccountingLine line = (SourceAccountingLine) 
            getSufficientFundsCheckingSourceIncome().createLine();
        line.setAmount(new KualiDecimal("3.0"));

        GeneralErrorCorrectionDocumentRule rule = 
            new GeneralErrorCorrectionDocumentRule();
        SufficientFundsItem item = 
            rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);
        
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
    }

    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isLiability__negative_lineAmount() {
        SourceAccountingLine line = (SourceAccountingLine) 
            getSufficientFundsCheckingSourceLiability().createLine();
        line.setAmount(new KualiDecimal("-3.0"));
        
        GeneralErrorCorrectionDocumentRule rule = 
            new GeneralErrorCorrectionDocumentRule();
        SufficientFundsItem item = rule
            .processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
        assertFalse(line.getAmount().equals(item.getAmount()));
        assertTrue(line.getAmount().abs().equals(item.getAmount()));
    }

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isExpense_postive_lineAmount() {
        TargetAccountingLine line = (TargetAccountingLine) 
            getSufficientFundsCheckingTargetExpense().createLine();
        line.setAmount(new KualiDecimal("3.0"));

        GeneralErrorCorrectionDocumentRule rule = 
            new GeneralErrorCorrectionDocumentRule();
        SufficientFundsItem item = rule
            .processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
    }

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isAsset__negative_lineAmount() {
        TargetAccountingLine line = (TargetAccountingLine) 
            getSufficientFundsCheckingTargetAsset().createLine();
        line.setAmount(new KualiDecimal("-3.0"));

        GeneralErrorCorrectionDocumentRule rule = 
            new GeneralErrorCorrectionDocumentRule();
        SufficientFundsItem item = rule
            .processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertFalse(line.getAmount().equals(item.getAmount()));
        assertTrue(line.getAmount().abs().equals(item.getAmount()));
    }

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isIncome_postive_lineAmount() {
        TargetAccountingLine line = (TargetAccountingLine) 
            getSufficientFundsCheckingTargetIncome().createLine();
        line.setAmount(new KualiDecimal("3.0"));

        GeneralErrorCorrectionDocumentRule rule = 
            new GeneralErrorCorrectionDocumentRule();
        SufficientFundsItem item = rule
            .processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
    }
    */

    /**
     * tests sufficient funds checking on a 
     * <code>{@link TargetAccountingLine}</code> that is of liability type 
     * with a negative amount.
     *
    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isLiability__negative_lineAmount() {
        TargetAccountingLine line = (TargetAccountingLine) 
            getSufficientFundsCheckingTargetLiability().createLine();
        line.setAmount(new KualiDecimal("-3.0"));

        GeneralErrorCorrectionDocumentRule rule = 
            new GeneralErrorCorrectionDocumentRule();
        SufficientFundsItem item = rule
            .processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertFalse(line.getAmount().equals(item.getAmount()));
        assertTrue(line.getAmount().abs().equals(item.getAmount()));
    }
    */
    ///////////////////////////////////////////////////////////////////////////
    // Test Methods End Here                                                 //
    ///////////////////////////////////////////////////////////////////////////
}
