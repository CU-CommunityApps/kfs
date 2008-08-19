/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.web.struts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.report.service.AccountsReceivableReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.action.KualiAction;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.SimpleBookmark;

/**
 * This class handles Actions for lookup flow
 */

public class CustomerInvoiceAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceAction.class);

    // private static final String TOTALS_TABLE_KEY = "totalsTable";


    public CustomerInvoiceAction() {
        super();
    }



    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }   

    public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceForm csForm = (CustomerInvoiceForm)form;
        csForm.setChartCode(null);
        csForm.setOrgCode(null);
        csForm.setOrgType(null); 
        csForm.setRunDate(null);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }   

    public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceForm ciForm = (CustomerInvoiceForm)form;

        Date date = ciForm.getRunDate();
        AccountsReceivableReportService reportService = SpringContext.getBean(AccountsReceivableReportService.class);
        List<File> reports = new ArrayList<File>();
        if (ciForm.getOrgType() != null) {
            if (ciForm.getOrgType().equals("B"))
                reports = reportService.generateInvoicesByBillingOrg(ciForm.getChartCode(), ciForm.getOrgCode(), date);
            else if (ciForm.getOrgType().equals("P"))
                reports = reportService.generateInvoicesByProcessingOrg(ciForm.getChartCode(), ciForm.getOrgCode(), date);

            // System.out.println("invoiceAction");
            //csForm.setReports(reports);
            //  System.out.println(reports);
        }
        if (reports.size()>0) {
            //   String fileName = csForm.getChartCode()+csForm.getOrgCode()+date+"-ConcatedPDFs.pdf";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                int pageOffset = 0;
                ArrayList master = new ArrayList();
                int f = 0;
                //   File file = new File(fileName);
                Document document = null;
                PdfCopy  writer = null;
                for (Iterator itr = reports.iterator(); itr.hasNext();) {
                    // we create a reader for a certain document
                    String reportName = ((File)itr.next()).getAbsolutePath();
                    PdfReader reader = new PdfReader(reportName);
                    reader.consolidateNamedDestinations();
                    // we retrieve the total number of pages
                    int n = reader.getNumberOfPages();
                    List bookmarks = SimpleBookmark.getBookmark(reader);
                    if (bookmarks != null) {
                        if (pageOffset != 0)
                            SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
                        master.addAll(bookmarks);
                    }
                    pageOffset += n;

                    if (f == 0) {
                        // step 1: creation of a document-object
                        document = new Document(reader.getPageSizeWithRotation(1));
                        // step 2: we create a writer that listens to the document
                        writer = new PdfCopy(document, baos);
                        // step 3: we open the document
                        document.open();
                    }
                    // step 4: we add content
                    PdfImportedPage page;
                    for (int i = 0; i < n; ) {
                        ++i;
                        page = writer.getImportedPage(reader, i);
                        writer.addPage(page);
                    }
                    writer.freeReader(reader);
                    f++;
                }
                if (!master.isEmpty())
                    writer.setOutlines(master);
                // step 5: we close the document

                document.close();
                // csForm.setReports(file);
            }
            catch(Exception e) {
                e.printStackTrace();
            } 
            String fileName;
            if (date != null)
                fileName = ciForm.getChartCode()+ciForm.getOrgCode()+date+"-InvoicesBatchPDFs.pdf";   
            else 
                fileName = ciForm.getChartCode()+ciForm.getOrgCode()+"-InvoiceBatchPDFs.pdf";

            WebUtils.saveMimeOutputStreamAsFile(response, "application/pdf", baos, fileName);
        }
        return null;


    }


}
