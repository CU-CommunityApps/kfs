/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.report;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.CorrectionChange;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;
import org.kuali.kfs.gl.businessobject.options.SearchOperatorsFinder;
import org.kuali.kfs.gl.document.service.CorrectionDocumentService;
import org.kuali.kfs.gl.report.PDFPageHelper;
import org.kuali.kfs.module.ld.document.LaborCorrectionDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


/**
 * Online Report for Labor Ledger Correction Process.
 */
public class LaborCorrectionOnlineReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCorrectionOnlineReport.class);

    /**
     * Generate report
     * 
     * @param laborCorrectionDocument
     * @param reportsDirectory
     * @return runDate
     */
    public void generateReport(LaborCorrectionDocument cDocument, String reportsDirectory, Date runDate) {
        LOG.debug("correctionOnlineReport() started");

        Font headerFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD);
        Font sectionFont = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD);
        Font textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
        Font boldTextFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);

        Document document = new Document(PageSize.A4.rotate());

        PDFPageHelper pageHelper = new PDFPageHelper();
        pageHelper.setRunDate(runDate);
        pageHelper.setHeaderFont(headerFont);
        pageHelper.setTitle("Labor Ledger Correction Process Report " + cDocument.getDocumentNumber());

        try {
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            
            String filename = reportsDirectory + "/llcp_" + cDocument.getDocumentNumber() + "_";

            filename = filename + dateTimeService.toDateTimeStringForFilename(runDate);
            filename = filename + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(pageHelper);

            document.open();

            float[] summaryWidths = { 90, 10 };
            PdfPTable summary = new PdfPTable(summaryWidths);

            PdfPCell cell;
            cell = new PdfPCell(new Phrase(" ", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Summary of Input Group", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Total Debits/Blanks: " + cDocument.getCorrectionDebitTotalAmount().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Total Credits: " + cDocument.getCorrectionCreditTotalAmount().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Total No DB/CR: " + cDocument.getCorrectionBudgetTotalAmount().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Row Count: " + cDocument.getCorrectionRowCount(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("System and Edit Method", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("System: " + cDocument.getSystem(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Edit Method: " + cDocument.getMethod(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Input and Output File", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Input Group ID:" + cDocument.getCorrectionInputFileName().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Output Group ID: " + cDocument.getCorrectionOutputFileName().toString(), textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            if (cDocument.getCorrectionInputFileName() != null) {
                cell = new PdfPCell(new Phrase("Input File Name: " + cDocument.getCorrectionInputFileName(), textFont));
                cell.setColspan(2);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                summary.addCell(cell);
            }

            cell = new PdfPCell(new Phrase("Edit Options and Action", sectionFont));
            cell.setColspan(2);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            String processBatch;
            String outputOnly;

            if (cDocument.getCorrectionFileDelete()) {
                processBatch = "No";
            }
            else {
                processBatch = "Yes";
            }

            if (cDocument.getCorrectionSelection()) {
                outputOnly = "Yes";
            }
            else {
                outputOnly = "No";
            }

            cell = new PdfPCell(new Phrase("Process In Batch: " + processBatch, textFont));
            cell.setColspan(2);
            // cell.setBorder(Rectangle.NO_BORDER);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            cell = new PdfPCell(new Phrase("Output only records which match criteria? " + outputOnly, textFont));
            cell.setColspan(2);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            summary.addCell(cell);

            if (cDocument.getCorrectionTypeCode().equals(CorrectionDocumentService.CORRECTION_TYPE_CRITERIA)) {
                cell = new PdfPCell(new Phrase("Search Criteria and Modification Criteria", sectionFont));
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                summary.addCell(cell);

                SearchOperatorsFinder sof = new SearchOperatorsFinder();

                for (Iterator ccgi = cDocument.getCorrectionChangeGroup().iterator(); ccgi.hasNext();) {
                    CorrectionChangeGroup ccg = (CorrectionChangeGroup) ccgi.next();

                    cell = new PdfPCell(new Phrase("Group", boldTextFont));
                    cell.setColspan(2);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    summary.addCell(cell);

                    cell = new PdfPCell(new Phrase("Search Criteria", boldTextFont));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    summary.addCell(cell);

                    for (Iterator ccri = ccg.getCorrectionCriteria().iterator(); ccri.hasNext();) {
                        CorrectionCriteria cc = (CorrectionCriteria) ccri.next();

                        cell = new PdfPCell(new Phrase("Field: " + cc.getCorrectionFieldName() + " operator: " + sof.getKeyLabelMap().get(cc.getCorrectionOperatorCode()) + " value: " + cc.getCorrectionFieldValue(), textFont));
                        cell.setColspan(2);
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                        summary.addCell(cell);
                    }

                    cell = new PdfPCell(new Phrase("Modification Criteria", boldTextFont));
                    cell.setColspan(2);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                    summary.addCell(cell);

                    for (Iterator cchi = ccg.getCorrectionChange().iterator(); cchi.hasNext();) {
                        CorrectionChange cc = (CorrectionChange) cchi.next();

                        cell = new PdfPCell(new Phrase("Field: " + cc.getCorrectionFieldName() + " Replacement Value: " + cc.getCorrectionFieldValue(), textFont));
                        cell.setColspan(2);
                        cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
                        summary.addCell(cell);
                    }
                }
            }
            document.add(summary);

        }
        catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }
        finally {
            document.close();
        }
    }
}
