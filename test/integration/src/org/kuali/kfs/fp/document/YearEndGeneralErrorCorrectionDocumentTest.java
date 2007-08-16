/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.document;

import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.test.fixtures.AccountingLineFixture.GEC_LINE1;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.bo.GECSourceAccountingLine;
import org.kuali.module.financial.bo.GECTargetAccountingLine;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.AccountingLineFixture;

/**
 * This class is used to test GeneralErrorCorrectionDocument.
 * 
 * 
 */
@ConfigureContext(session = KHUNTLEY)
public class YearEndGeneralErrorCorrectionDocumentTest extends KualiTestBase {

    public static final Class<YearEndGeneralErrorCorrectionDocument> DOCUMENT_CLASS = YearEndGeneralErrorCorrectionDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), YearEndGeneralErrorCorrectionDocument.class);
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(GEC_LINE1);
        return list;
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(GEC_LINE1);
        return list;
    }

    private YearEndGeneralErrorCorrectionDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        YearEndGeneralErrorCorrectionDocument document = (YearEndGeneralErrorCorrectionDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {

            document.addSourceAccountingLine(sourceFixture.createAccountingLine(GECSourceAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextSourceLineNumber()));
        }

        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            document.addTargetAccountingLine(targetFixture.createAccountingLine(GECTargetAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextTargetLineNumber()));
        }

        return document;
    }


    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = generateTargetAccountingLines();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size();
        AccountingDocumentTestUtils.testAddAccountingLine(DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), SpringContext.getBean(DataDictionaryService.class));
       
    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }
    public final void testConvertIntoErrorCorrection_errorCorrectionDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_errorCorrectionDisallowed(buildDocument(), SpringContext.getBean(DataDictionaryService.class));
    }
    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_invalidYear(buildDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class), SpringContext.getBean(AccountingPeriodService.class));
    }
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(buildDocument(), getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testRouteDocument() throws Exception {
        AccountingDocumentTestUtils.testRouteDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
    }
    
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {
        AccountingDocumentTestUtils.testSaveDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
    }
    
    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testConvertIntoCopy() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy(buildDocument(), SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
    }
    //test util methods
    private List<SourceAccountingLine> generateSouceAccountingLines() throws Exception {
        List<SourceAccountingLine> sourceLines = new ArrayList<SourceAccountingLine>();
        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceLines.add(sourceFixture.createAccountingLine(GECSourceAccountingLine.class, sourceFixture.debitCreditCode));
        }

        return sourceLines;
    }

    private List<TargetAccountingLine> generateTargetAccountingLines() throws Exception {
        List<TargetAccountingLine> targetLines = new ArrayList<TargetAccountingLine>();
        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetLines.add(targetFixture.createAccountingLine(GECTargetAccountingLine.class, targetFixture.debitCreditCode));
        }

        return targetLines;
    }

    private int getExpectedPrePeCount() {
        return 4;
    }

}
