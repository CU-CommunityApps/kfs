/*
 * Copyright 2011 The Kuali Foundation.
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

import java.io.ByteArrayOutputStream;

import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsSuspendedInvoiceSummaryReportService;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * This class generates Contracts Grants Suspended Invoice Summary reports.
 */
public class ContractsGrantsSuspendedInvoiceSummaryReportServiceImpl extends ContractsGrantsReportServiceImplBase implements ContractsGrantsSuspendedInvoiceSummaryReportService {

    private ReportInfo contractsGrantsSuspendedInvoiceSummaryReportInfo;

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsSuspendedInvoiceSummaryReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder, java.io.ByteArrayOutputStream)
     */
    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {

        return generateReport(reportDataHolder, contractsGrantsSuspendedInvoiceSummaryReportInfo, baos);
    }

    /**
     * @return contractsGrantsSuspendedInvoiceSummaryReportInfo
     */
    public ReportInfo getContractsGrantsSuspendedInvoiceSummaryReportInfo() {
        return contractsGrantsSuspendedInvoiceSummaryReportInfo;
    }

    /**
     * @param contractsGrantsSuspendedInvoiceSummaryReportInfo
     */
    public void setContractsGrantsSuspendedInvoiceSummaryReportInfo(ReportInfo contractsGrantsSuspendedInvoiceSummaryReportInfo) {
        this.contractsGrantsSuspendedInvoiceSummaryReportInfo = contractsGrantsSuspendedInvoiceSummaryReportInfo;
    }

}
