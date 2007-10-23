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
package org.kuali.module.gl.batch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.ReportService;

public class PosterSummaryReportStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterSummaryReportStep.class);
    private static final String DATE_FORMAT = "MMdd";
    private static final String BUD = "bud";
    private static final String ACT = "act";
    private static final String ENC = "enc";
    public ReportService reportService;
    public OptionsService optionsService;
    public OriginEntryGroupService originEntryGroupService;


    public synchronized boolean execute(String jobName) {
        final String CURRENT_YEAR_LOWER = getParameterService().getParameterValue(getClass(), GLConstants.GlSummaryReport.CURRENT_YEAR_LOWER);
        final String CURRENT_YEAR_UPPER = getParameterService().getParameterValue(getClass(), GLConstants.GlSummaryReport.CURRENT_YEAR_UPPER);
        final String CURRENT_AND_LAST_YEAR = getParameterService().getParameterValue(getClass(), GLConstants.GlSummaryReport.CURRENT_AND_LAST_YEAR);

        Options currentYear = optionsService.getCurrentYearOptions();
        Options nextYear = optionsService.getOptions(currentYear.getUniversityFiscalYear() + 1);
        Options previousYear = optionsService.getOptions(currentYear.getUniversityFiscalYear() - 1);

        Date runDate = getDateTimeService().getCurrentDate();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        String md = sdf.format(runDate);
        this.generatePosterOutputReport(runDate);
        if ((md.compareTo(CURRENT_YEAR_UPPER) > 0) || (md.compareTo(CURRENT_YEAR_LOWER) < 0)) {
            // Current year
            reportService.generateGlSummary(runDate, currentYear, BUD);
            reportService.generateGlSummary(runDate, currentYear, ACT);
            reportService.generateGlEncumbranceSummary(runDate, currentYear, ENC);
        }
        else if ((md.compareTo(CURRENT_AND_LAST_YEAR) > 0)) {
            // Current year and Last year
            reportService.generateGlSummary(runDate, currentYear, BUD);
            reportService.generateGlSummary(runDate, previousYear, BUD);
            reportService.generateGlSummary(runDate, currentYear, ACT);
            reportService.generateGlSummary(runDate, previousYear, ACT);
            reportService.generateGlEncumbranceSummary(runDate, currentYear, ENC);
            reportService.generateGlEncumbranceSummary(runDate, previousYear, ENC);
        }
        else {
            // Current year and next year
            reportService.generateGlSummary(runDate, currentYear, BUD);
            reportService.generateGlSummary(runDate, nextYear, BUD);
            reportService.generateGlSummary(runDate, currentYear, ACT);
            reportService.generateGlSummary(runDate, nextYear, ACT);
            reportService.generateGlEncumbranceSummary(runDate, currentYear, ENC);
            reportService.generateGlEncumbranceSummary(runDate, nextYear, ENC);
        }
        return true;
    }

    // Generate poster output reports
    private void generatePosterOutputReport(Date runDate) {
        List originEntrySourceCodeList = new ArrayList();
        originEntrySourceCodeList.add(OriginEntrySource.MAIN_POSTER_VALID);
        originEntrySourceCodeList.add(OriginEntrySource.REVERSAL_POSTER_VALID);
        originEntrySourceCodeList.add(OriginEntrySource.ICR_POSTER_VALID);
        Collection originEntryGroups = new ArrayList();
        for (Iterator groupIterator = originEntrySourceCodeList.iterator(); groupIterator.hasNext();) {
            String originEntrySourceCode = (String) groupIterator.next();
            originEntryGroups.addAll(originEntryGroupService.getGroupsFromSource(originEntrySourceCode));
        }
        reportService.generatePosterOutputTransactionSummaryReport(runDate, originEntryGroups);
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setOptionsService(OptionsService os) {
        optionsService = os;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }
}
