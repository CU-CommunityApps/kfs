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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRParameter;

import org.kuali.kfs.module.ar.report.service.CustomerCreditMemoReportService;
import org.kuali.kfs.module.ar.report.util.CustomerCreditMemoReportDataHolder;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.batch.service.ReportGenerationService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.springframework.transaction.annotation.Transactional;

/**
 * To generate the working progress reports for the effort certification
 */
@Transactional
public class CustomerCreditMemoReportServiceImpl implements CustomerCreditMemoReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerCreditMemoReportServiceImpl.class);

    private ReportGenerationService reportGenerationService;
    private ReportInfo customerCreditMemoReportInfo;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationReportService#generateReportForExtractProcess(org.kuali.module.effort.util.ExtractProcessReportDataHolder, java.util.Date)
     */
    public void generateReportForExtractProcess(CustomerCreditMemoReportDataHolder reportDataHolder, Date runDate) {
        String reportFileName = customerCreditMemoReportInfo.getReportFileName();
        String reportDirectory = customerCreditMemoReportInfo.getReportsDirectory();
        String reportTemplateClassPath = customerCreditMemoReportInfo.getReportTemplateClassPath();
        String reportTemplateName = customerCreditMemoReportInfo.getReportTemplateName();
        ResourceBundle resourceBundle = customerCreditMemoReportInfo.getResourceBundle();
        String subReportTemplateClassPath = customerCreditMemoReportInfo.getSubReportTemplateClassPath();
        Map<String, String> subReports = customerCreditMemoReportInfo.getSubReports();

        Map<String, Object> reportData = reportDataHolder.getReportData();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        String template = reportTemplateClassPath + reportTemplateName;
        String fullReportFileName = reportGenerationService.buildFullFileName(runDate, reportDirectory, reportFileName, "");
        reportGenerationService.generateReportToPdfFile(reportData, template, fullReportFileName);
    }
    
    /**
     * Sets the effortExtractProcessReportInfo attribute value.
     * 
     * @param effortExtractProcessReportInfo The effortExtractProcessReportInfo to set.
     */
    public void setCustomerCreditMemoReportInfo(ReportInfo customerCreditMemoReportInfo) {
        this.customerCreditMemoReportInfo = customerCreditMemoReportInfo;
    }

    /**
     * Sets the reportGenerationService attribute value.
     * 
     * @param reportGenerationService The reportGenerationService to set.
     */
    public void setReportGenerationService(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }
}