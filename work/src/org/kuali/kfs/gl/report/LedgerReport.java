/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.util;

import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.kuali.core.util.KualiDecimal;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 
 */
public class LedgerReport {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerReport.class);
    public static final String PDF_FILE_EXTENSION = ".pdf";

    private Font headerFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);
    private Font textFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);
    private Font totalFieldFont = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD);

    /**
     * This method generates report based on the given map of ledger entries
     * 
     * @param ledgerEntries the given ledger entry map
     * @param reportingDate the reporting date
     * @param title the report title
     * @param fileprefix the prefix of the generated report file
     * @param destinationDirectory the directory where the report is located
     */
    public void generateReport(Map ledgerEntries, Date runDate, String title, String fileprefix, String destinationDirectory) {
        LOG.debug("generateReport() started");
        this.generateReport(ledgerEntries.values(), runDate, title, fileprefix, destinationDirectory);
    }

    /**
     * This method generates report based on the given collection of ledger entries
     * 
     * @param entryCollection the given collection of ledger entries
     * @param reportingDate the reporting date
     * @param title the report title
     * @param fileprefix the prefix of the generated report file
     * @param destinationDirectory the directory where the report is located
     */
    public void generateReport(Collection entryCollection, Date reportingDate, String title, String fileprefix, String destinationDirectory) {
        LOG.debug("generateReport() started");
        this.generatePDFReport(entryCollection, reportingDate, title, fileprefix, destinationDirectory);
    }

    /**
     * This method generates report based on the given holder of ledger entries
     * 
     * @param ledgerEntryHolder the given holder of ledger entries
     * @param reportingDate the reporting date
     * @param title the report title
     * @param fileprefix the prefix of the generated report file
     * @param destinationDirectory the directory where the report is located
     */
    public void generateReport(LedgerEntryHolder ledgerEntryHolder, Date reportingDate, String title, String fileprefix, String destinationDirectory) {
        LOG.debug("generateReport() started");
        this.generatePDFReport(ledgerEntryHolder, reportingDate, title, fileprefix, destinationDirectory);
    }

    // generate the PDF report with the given information
    private void generatePDFReport(Object ledgerEntryHolder, Date reportingDate, String title, String fileprefix, String destinationDirectory) {
        Document document = new Document(PageSize.A4.rotate());

        PDFPageHelper pageHelper = new PDFPageHelper();
        pageHelper.setRunDate(reportingDate);
        pageHelper.setHeaderFont(headerFont);
        pageHelper.setTitle(title);

        try {
            String filename = destinationDirectory + "/" + fileprefix + "_";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            filename = filename + sdf.format(reportingDate);
            filename = filename + PDF_FILE_EXTENSION;

            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filename));
            writer.setPageEvent(pageHelper);

            document.open();
            PdfPTable ledgerEntryTable = this.drawPdfTable(ledgerEntryHolder);
            document.add(ledgerEntryTable);
        }
        catch (Exception de) {
            LOG.error("generateReport() Error creating PDF report", de);
            throw new RuntimeException("Report Generation Failed");
        }
        finally {
            this.closeDocument(document);
        }
    }

    // draw a PDF table populated with the data held by ledger entry holder
    public PdfPTable drawPdfTable(Object ledgerEntryHolder) {
        PdfPTable ledgerEntryTable = null;
        if (ledgerEntryHolder instanceof Collection) {
            ledgerEntryTable = this.buildPdfTable((Collection) ledgerEntryHolder);
        }
        else if (ledgerEntryHolder instanceof LedgerEntryHolder) {
            ledgerEntryTable = this.buildPdfTable((LedgerEntryHolder) ledgerEntryHolder);
        }
        return ledgerEntryTable;
    }

    // draw a PDF table from a collection
    private PdfPTable buildPdfTable(Collection entryCollection) {

        if (entryCollection == null || entryCollection.size() <= 0) {
            return this.buildEmptyTable();
        }

        float[] warningWidths = { 3, 3, 6, 3, 8, 10, 8, 10, 8, 10, 8 };
        PdfPTable ledgerEntryTable = new PdfPTable(warningWidths);
        ledgerEntryTable.setHeaderRows(1);
        ledgerEntryTable.setWidthPercentage(100);

        this.addHeader(ledgerEntryTable, headerFont);

        for (Iterator reportIter = entryCollection.iterator(); reportIter.hasNext();) {
            LedgerEntry ledgerEntry = (LedgerEntry) reportIter.next();
            this.addRow(ledgerEntryTable, ledgerEntry, textFont, false);
        }

        return ledgerEntryTable;
    }

    // draw a PDF table from ledger entry holder
    private PdfPTable buildPdfTable(LedgerEntryHolder ledgerEntryHolder) {
        SortedMap ledgerEntries = new TreeMap(ledgerEntryHolder.getLedgerEntries());
        Collection entryCollection = ledgerEntries.values();
        Map subtotalMap = ledgerEntryHolder.getSubtotals();

        if (entryCollection == null || entryCollection.size() <= 0) {
            return this.buildEmptyTable();
        }

        float[] warningWidths = { 3, 3, 6, 3, 8, 10, 8, 10, 8, 10, 8 };
        PdfPTable ledgerEntryTable = new PdfPTable(warningWidths);
        ledgerEntryTable.setHeaderRows(1);
        ledgerEntryTable.setWidthPercentage(100);

        this.addHeader(ledgerEntryTable, headerFont);

        String tempBalanceType = "--";
        for (Iterator reportIter = entryCollection.iterator(); reportIter.hasNext();) {
            LedgerEntry ledgerEntry = (LedgerEntry) reportIter.next();

            // add the subtotal rows
            if (!ledgerEntry.getBalanceType().equals(tempBalanceType)) {
                if (subtotalMap.containsKey(tempBalanceType)) {
                    LedgerEntry subtotal = (LedgerEntry) subtotalMap.get(tempBalanceType);
                    this.addRow(ledgerEntryTable, subtotal, totalFieldFont, true);
                }
                tempBalanceType = ledgerEntry.getBalanceType();
            }
            this.addRow(ledgerEntryTable, ledgerEntry, textFont, false);

            // deal with the subtotal after adding the last row
            if (!reportIter.hasNext() && subtotalMap.containsKey(tempBalanceType)) {
                LedgerEntry subtotal = (LedgerEntry) subtotalMap.get(tempBalanceType);
                this.addRow(ledgerEntryTable, subtotal, totalFieldFont, true);
            }
        }
        this.addRow(ledgerEntryTable, ledgerEntryHolder.getGrandTotal(), totalFieldFont, true);

        return ledgerEntryTable;
    }

    // draw a table with an informative messge, instead of data
    private PdfPTable buildEmptyTable() {
        float[] tableWidths = { 100 };

        PdfPTable ledgerEntryTable = new PdfPTable(tableWidths);
        ledgerEntryTable.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase("No entries found!", headerFont));
        ledgerEntryTable.addCell(cell);

        return ledgerEntryTable;
    }

    // add a table header
    private void addHeader(PdfPTable ledgerEntryTable, Font headerFont) {

        PdfPCell cell = new PdfPCell(new Phrase("BAL TYP", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("ORIG", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("YEAR", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("PRD", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Record Count", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Debit Amount", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Debit Count", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Credit Amount", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("Credit Count", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("No D/C Code Amount", headerFont));
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase("No D/C Code Count", headerFont));
        ledgerEntryTable.addCell(cell);
    }

    // add a row with the given ledger entry into PDF table
    private void addRow(PdfPTable ledgerEntryTable, LedgerEntry ledgerEntry, Font textFont, boolean isTotal) {
        PdfPCell cell = null;
        if (isTotal) {
            String balanceType = ledgerEntry.getBalanceType() != null ? "(" + ledgerEntry.getBalanceType() + ")" : "";
            String totalDescription = ledgerEntry.getOriginCode() + balanceType + ":";

            cell = new PdfPCell(new Phrase(totalDescription, textFont));
            cell.setColspan(4);
            ledgerEntryTable.addCell(cell);
        }
        else {
            cell = new PdfPCell(new Phrase(ledgerEntry.getBalanceType(), textFont));
            ledgerEntryTable.addCell(cell);

            cell = new PdfPCell(new Phrase(ledgerEntry.getOriginCode(), textFont));
            ledgerEntryTable.addCell(cell);

            String fiscalYear = (ledgerEntry.getFiscalYear() != null) ? ledgerEntry.getFiscalYear().toString() : "";
            cell = new PdfPCell(new Phrase(fiscalYear, textFont));
            ledgerEntryTable.addCell(cell);

            cell = new PdfPCell(new Phrase(ledgerEntry.getPeriod(), textFont));
            ledgerEntryTable.addCell(cell);
        }

        cell = new PdfPCell(new Phrase(this.formatNumber(new Integer(ledgerEntry.getRecordCount())), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(ledgerEntry.getDebitAmount()), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(new Integer(ledgerEntry.getDebitCount())), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(ledgerEntry.getCreditAmount()), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(new Integer(ledgerEntry.getCreditCount())), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(ledgerEntry.getNoDCAmount()), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);

        cell = new PdfPCell(new Phrase(this.formatNumber(new Integer(ledgerEntry.getNoDCCount())), textFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        ledgerEntryTable.addCell(cell);
    }

    // format the given number based on its type: Integer or BigDecimal
    private String formatNumber(Number number) {
        DecimalFormat decimalFormat = new DecimalFormat();

        if (number instanceof Integer) {
            decimalFormat.applyPattern("###,###");
        }
        else if (number instanceof KualiDecimal) {
            decimalFormat.applyPattern("###,###,###,##0.00");
        }
        return decimalFormat.format(number);
    }

    // close the document and release the resource
    private void closeDocument(Document document) {
        try {
            if ((document != null) && document.isOpen()) {
                document.close();
            }
        }
        catch (Throwable t) {
            LOG.error("generateReport() Exception closing report", t);
        }
    }
}
