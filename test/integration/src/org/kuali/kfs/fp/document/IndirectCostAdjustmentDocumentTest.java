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

import static org.kuali.core.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.core.util.SpringServiceLocator.getTransactionalDocumentDictionaryService;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testAddAccountingLine;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testConvertIntoCopy;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testConvertIntoCopy_copyDisallowed;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testConvertIntoErrorCorrection;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_errorCorrectionDisallowed;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testRouteDocument;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testSaveDocument;
import static org.kuali.test.fixtures.AccountingLineFixture.ICA_LINE;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;

/**
 * This class is used to test the IndirectCostAdjustmentDocument.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class IndirectCostAdjustmentDocumentTest extends KualiTestBase {
    public static final Class<IndirectCostAdjustmentDocument> DOCUMENT_CLASS = IndirectCostAdjustmentDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), IndirectCostAdjustmentDocument.class);
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        return new ArrayList<AccountingLineFixture>();
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(ICA_LINE);
        return list;
    }




    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = generateTargetAccountingLines();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size()+expectedSourceTotal;
        TransactionalDocumentTestUtils.testAddAccountingLine(DocumentTestUtils.createDocument(getDocumentService(), DOCUMENT_CLASS), sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, getDocumentService());
    }
    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), getDataDictionaryService());
       
    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildDocument(), getTransactionalDocumentDictionaryService());
    }
    public final void testConvertIntoErrorCorrection_errorCorrectionDisallowed() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_errorCorrectionDisallowed(buildDocument(), getDataDictionaryService());
    }
    @TestsWorkflowViaDatabase
    public final void testConvertIntoErrorCorrection() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection(buildDocument(), getExpectedPrePeCount(), getDocumentService(), getTransactionalDocumentDictionaryService());
    }
    @TestsWorkflowViaDatabase
    public final void testRouteDocument() throws Exception {
        TransactionalDocumentTestUtils.testRouteDocument(buildDocument(), getDocumentService());
    }
    
    @TestsWorkflowViaDatabase
    public final void testSaveDocument() throws Exception {
        TransactionalDocumentTestUtils.testSaveDocument(buildDocument(), getDocumentService());
    }
    @TestsWorkflowViaDatabase
    public final void testConvertIntoCopy() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoCopy(buildDocument(), getDocumentService(), getExpectedPrePeCount());
    }
    
    
    //test util methods
    private List<SourceAccountingLine> generateSouceAccountingLines() throws Exception {
        List<SourceAccountingLine> sourceLines = new ArrayList<SourceAccountingLine>();
        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceLines.add(sourceFixture.createSourceAccountingLine());
        }

        return sourceLines;
    }

    private List<TargetAccountingLine> generateTargetAccountingLines() throws Exception {
        List<TargetAccountingLine> targetLines = new ArrayList<TargetAccountingLine>();
        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetLines.add(targetFixture.createTargetAccountingLine());
        }

        return targetLines;
    }

    private IndirectCostAdjustmentDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        IndirectCostAdjustmentDocument document = (IndirectCostAdjustmentDocument) getDocumentParameterFixture();
    
        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceFixture.addAsSourceTo(document);
        }
    
        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetFixture.addAsTargetTo(document);
        }
    
        return document;
    }

    private int getExpectedPrePeCount() {
        return 4;
    }

}
