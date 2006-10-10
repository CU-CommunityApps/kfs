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

import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE5;
import static org.kuali.test.fixtures.UserNameFixture.DFOGLE;
import static org.kuali.test.util.KualiTestAssertionUtils.assertEquality;
import static org.kuali.test.util.KualiTestAssertionUtils.assertInequality;

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.DocumentNote;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.document.TransactionalDocumentTestBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.test.monitor.ChangeMonitor;
import org.kuali.test.monitor.DocumentStatusMonitor;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;
/**
 * This class is used to test JournalVoucherDocument.
 * 
 * 
 */
@WithTestSpringContext(session = DFOGLE)
public class JournalVoucherDocumentTest extends TransactionalDocumentTestBase {

    /**
     * Override to set the balance type on the document.<br/>
     * 
     * <p>
     * This is probably not the best way to do this. Matt suggested that a specific <code>{@link DocumentParameter}</code>
     * definition be made to handle the fixtures and the <code>{@link Document}</code> creation if more fields are added and
     * <code>{@link JournalVoucherDocument}</code> becomes more complex and demanding. Something like a
     * <code>JournalVoucherDocumentParameter</code> would probably work to remedy this. For specific details look at the
     * <code>{@link DisbursementVoucherDocumentTest}</code> that Matt worked on.
     * </p>
     * 
     * @see org.kuali.core.document.DocumentTestBase#buildDocument()
     * @see org.kuali.module.financial.document.DisbursementDocumentTest
     * @see org.kuali.test.parameters.DisbursementVoucherDocumentParameter
     * @return Document used in test methods that require a specific <code>{@link Document}</code> instance.
     */
    protected Document buildDocument() throws Exception {
        JournalVoucherDocument jvDoc = (JournalVoucherDocument) super.buildDocument();
        jvDoc.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);
        return jvDoc;
    }

    /**
     * Had to override b/c there are too many differences between the JV and the standard document structure (i.e. GLPEs generate
     * differently, routing isn't standard, etc).
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#testConvertIntoCopy()
     */
    @TestsWorkflowViaDatabase
    public void testConvertIntoCopy() throws Exception {
        // save the original doc, wait for status change
        TransactionalDocument document = (TransactionalDocument) buildDocument();
        getDocumentService().routeDocument(document, "saving copy source document", null);
        // collect some preCopy data
        String preCopyId = document.getFinancialDocumentNumber();
        String preCopyCopiedFromId = document.getDocumentHeader().getFinancialDocumentTemplateNumber();

        int preCopyPECount = document.getGeneralLedgerPendingEntries().size();
        int preCopyNoteCount = document.getDocumentHeader().getNotes().size();

        ArrayList preCopySourceLines = (ArrayList) ObjectUtils.deepCopy((ArrayList) document.getSourceAccountingLines());
        ArrayList preCopyTargetLines = (ArrayList) ObjectUtils.deepCopy((ArrayList) document.getTargetAccountingLines());
        // validate preCopy state
        assertNotNull(preCopyId);
        assertNull(preCopyCopiedFromId);

        assertEquals(1, preCopyPECount);
        assertEquals(0, preCopyNoteCount);

        // do the copy
        document.convertIntoCopy();
        // compare to preCopy state

        String postCopyId = document.getFinancialDocumentNumber();
        assertFalse(postCopyId.equals(preCopyId));
        // verify that docStatus has changed
        // pending entries should be cleared
        int postCopyPECount = document.getGeneralLedgerPendingEntries().size();
        assertEquals(0, postCopyPECount);
        // count 1 note, compare to "copied" text
        int postCopyNoteCount = document.getDocumentHeader().getNotes().size();
        assertEquals(1, postCopyNoteCount);
        DocumentNote note = document.getDocumentHeader().getNote(0);
        assertTrue(note.getFinancialDocumentNoteText().indexOf("copied from") != -1);
        // copiedFrom should be equal to old id
        String copiedFromId = document.getDocumentHeader().getFinancialDocumentTemplateNumber();
        assertEquals(preCopyId, copiedFromId);
        // accounting lines should be have different docHeaderIds but same amounts
        List postCopySourceLines = document.getSourceAccountingLines();
        assertEquals(preCopySourceLines.size(), postCopySourceLines.size());
        for (int i = 0; i < preCopySourceLines.size(); ++i) {
            SourceAccountingLine preCopyLine = (SourceAccountingLine) preCopySourceLines.get(i);
            SourceAccountingLine postCopyLine = (SourceAccountingLine) postCopySourceLines.get(i);

            assertInequality(preCopyLine.getFinancialDocumentNumber(), postCopyLine.getFinancialDocumentNumber());
            assertEquality(preCopyLine.getAmount(), postCopyLine.getAmount());
        }

        List postCopyTargetLines = document.getTargetAccountingLines();
        assertEquals(preCopyTargetLines.size(), postCopyTargetLines.size());
        for (int i = 0; i < preCopyTargetLines.size(); ++i) {
            TargetAccountingLine preCopyLine = (TargetAccountingLine) preCopyTargetLines.get(i);
            TargetAccountingLine postCopyLine = (TargetAccountingLine) postCopyTargetLines.get(i);

            assertInequality(preCopyLine.getFinancialDocumentNumber(), postCopyLine.getFinancialDocumentNumber());
            assertEquality(preCopyLine.getAmount(), postCopyLine.getAmount());
        }
    }

    /**
     * Had to override b/c there are too many differences between the JV and the standard document structure (i.e. GLPEs generate
     * differently, routing isn't standard, etc).
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#testConvertIntoErrorCorrection()
     */
    @TestsWorkflowViaDatabase
    public void testConvertIntoErrorCorrection() throws Exception {
        TransactionalDocument document = (TransactionalDocument) buildDocument();

        // replace the broken sourceLines with one that lets the test succeed
        KualiDecimal balance = new KualiDecimal("21.12");
        ArrayList sourceLines = new ArrayList();
        {
            SourceAccountingLine sourceLine = new SourceAccountingLine();
            sourceLine.setFinancialDocumentNumber(document.getFinancialDocumentNumber());
            sourceLine.setSequenceNumber(new Integer(0));
            sourceLine.setChartOfAccountsCode("BL");
            sourceLine.setAccountNumber("1031400");
            sourceLine.setFinancialObjectCode("1663");
            sourceLine.setAmount(balance);
            sourceLine.setObjectTypeCode("AS");
            sourceLine.setBalanceTypeCode("AC");
            sourceLine.refresh();
            sourceLines.add(sourceLine);
        }
        document.setSourceAccountingLines(sourceLines);


        String documentHeaderId = document.getFinancialDocumentNumber();
        // route the original doc, wait for status change
        getDocumentService().routeDocument(document, "saving errorCorrection source document", null);
        // jv docs go straight to final
        DocumentWorkflowStatusMonitor routeMonitor = new DocumentWorkflowStatusMonitor(getDocumentService(), documentHeaderId, "F");
        assertTrue(ChangeMonitor.waitUntilChange(routeMonitor, 240, 5));
        document = (TransactionalDocument) getDocumentService().getByDocumentHeaderId(documentHeaderId);
        // collect some preCorrect data
        String preCorrectId = document.getFinancialDocumentNumber();
        String preCorrectCorrectsId = document.getDocumentHeader().getFinancialDocumentInErrorNumber();

        int preCorrectPECount = document.getGeneralLedgerPendingEntries().size();
        int preCorrectNoteCount = document.getDocumentHeader().getNotes().size();
        String preCorrectStatus = document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus();

        ArrayList preCorrectSourceLines = (ArrayList) ObjectUtils.deepCopy(new ArrayList(document.getSourceAccountingLines()));
        ArrayList preCorrectTargetLines = (ArrayList) ObjectUtils.deepCopy(new ArrayList(document.getTargetAccountingLines()));
        // validate preCorrect state
        assertNotNull(preCorrectId);
        assertNull(preCorrectCorrectsId);

        assertEquals(1, preCorrectPECount);
        assertEquals(0, preCorrectNoteCount);
        assertEquals("F", preCorrectStatus);
        // do the copy
        document.convertIntoErrorCorrection();
        // compare to preCorrect state

        String postCorrectId = document.getFinancialDocumentNumber();
        assertFalse(postCorrectId.equals(preCorrectId));
        // pending entries should be cleared
        int postCorrectPECount = document.getGeneralLedgerPendingEntries().size();
        assertEquals(0, postCorrectPECount);
        // count 1 note, compare to "correction" text
        int postCorrectNoteCount = document.getDocumentHeader().getNotes().size();
        assertEquals(1, postCorrectNoteCount);
        DocumentNote note = document.getDocumentHeader().getNote(0);
        assertTrue(note.getFinancialDocumentNoteText().indexOf("correction") != -1);
        // correctsId should be equal to old id
        String correctsId = document.getDocumentHeader().getFinancialDocumentInErrorNumber();
        assertEquals(preCorrectId, correctsId);
        // accounting lines should have sign reversed on amounts
        List postCorrectSourceLines = document.getSourceAccountingLines();
        assertEquals(preCorrectSourceLines.size(), postCorrectSourceLines.size());
        for (int i = 0; i < preCorrectSourceLines.size(); ++i) {
            SourceAccountingLine preCorrectLine = (SourceAccountingLine) preCorrectSourceLines.get(i);
            SourceAccountingLine postCorrectLine = (SourceAccountingLine) postCorrectSourceLines.get(i);

            assertEquality(postCorrectId, postCorrectLine.getFinancialDocumentNumber());
            assertEquality(preCorrectLine.getAmount().negated(), postCorrectLine.getAmount());
        }

        List postCorrectTargetLines = document.getTargetAccountingLines();
        assertEquals(preCorrectTargetLines.size(), postCorrectTargetLines.size());
        for (int i = 0; i < preCorrectTargetLines.size(); ++i) {
            TargetAccountingLine preCorrectLine = (TargetAccountingLine) preCorrectTargetLines.get(i);
            TargetAccountingLine postCorrectLine = (TargetAccountingLine) postCorrectTargetLines.get(i);

            assertEquality(postCorrectId, postCorrectLine.getFinancialDocumentNumber());
            assertEquality(preCorrectLine.getAmount().negated(), postCorrectLine.getAmount());
        }
    }

    /**
     * Override b/c the status changing is flakey with this doc b/c routing is special (goes straight to final).
     * 
     * @see org.kuali.core.document.DocumentTestBase#testRouteDocument()
     */
    @TestsWorkflowViaDatabase
    public void testRouteDocument() throws Exception {
        // save the original doc, wait for status change
        Document document = buildDocument();
        assertFalse("R".equals(document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        getDocumentService().routeDocument(document, "saving copy source document", null);
        // jv docs go straight to final
        WorkflowTestUtils.waitForStatusChange(document.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);
        // also check the Kuali (not Workflow) document status
        DocumentStatusMonitor statusMonitor = new DocumentStatusMonitor(getDocumentService(), document.getDocumentHeader().getFinancialDocumentNumber(), Constants.DocumentStatusCodes.APPROVED);
        assertTrue(ChangeMonitor.waitUntilChange(statusMonitor, 240, 5));
    }

    /**
     * 
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    public Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createTransactionalDocument(getDocumentService(), JournalVoucherDocument.class, 2007, "06");
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    public List getTargetAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    @Override
    public List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
	List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE5);
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getUserName()
     */
    @Override
    public UserNameFixture getUserName() {
        return DFOGLE;
    }
}
