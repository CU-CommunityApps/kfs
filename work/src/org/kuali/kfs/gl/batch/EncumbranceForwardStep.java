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
package org.kuali.kfs.gl.batch;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.YearEndService;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntrySource;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.springframework.util.StopWatch;

/**
 * A step to run the year end process of forwarding encumbrances into the next fiscal year
 */
public class EncumbranceForwardStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceForwardStep.class);
    private YearEndService yearEndService;
    private OriginEntryGroupService originEntryGroupService;

    public static final String TRANSACTION_DATE_FORMAT_STRING = "yyyy-MM-dd";

    /**
     * This step runs the forward encumbrance process, including retrieving the parameters needed to run the job, creating the
     * origin entry group where output origin entries will go, and having the job's reports generated.
     * 
     * @param jobName the name of the job that this step is being run as part of
     * @param jobRunDate the time/date when the job was started
     * @return true if the step completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#performStep()
     */
    public boolean execute(String jobName, java.util.Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);

        Map jobParameters = new HashMap();
        Integer varFiscalYear = null;
        Date varTransactionDate = null;

        String FIELD_FISCAL_YEAR = GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR;
        String FIELD_TRANSACTION_DATE = GeneralLedgerConstants.ColumnNames.TRANSACTION_DT;

        // Get the current fiscal year.
        varFiscalYear = new Integer(getParameterService().getParameterValue(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM));

        // Get the current date (transaction date).
        try {
            DateFormat transactionDateFormat = new SimpleDateFormat(TRANSACTION_DATE_FORMAT_STRING);
            varTransactionDate = new Date(transactionDateFormat.parse(getParameterService().getParameterValue(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_TRANSACTION_DATE_PARM)).getTime());
        }
        catch (ParseException pe) {
            LOG.error("Failed to parse TRANSACTION_DT from kualiConfigurationService");
            throw new RuntimeException("Unable to get transaction date from kualiConfigurationService", pe);
        }

        jobParameters.put(GeneralLedgerConstants.ColumnNames.UNIVERSITY_FISCAL_YEAR, varFiscalYear);
        jobParameters.put(GeneralLedgerConstants.ColumnNames.UNIV_DT, varTransactionDate);

        OriginEntryGroup originEntryGroup = originEntryGroupService.createGroup(varTransactionDate, OriginEntrySource.YEAR_END_ENCUMBRANCE_CLOSING, true, false, true);
        Map<String, Integer> forwardEncumbranceCounts = new HashMap<String, Integer>();

        yearEndService.forwardEncumbrances(originEntryGroup, jobParameters, forwardEncumbranceCounts);

        yearEndService.generateForwardEncumbrancesReports(originEntryGroup, jobParameters, forwardEncumbranceCounts);

        stopWatch.stop();
        LOG.info(jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");

        return true;
    }

    /**
     * Sets the yearEndService attribute, allowing the injection of an implementation of that service
     * 
     * @param yearEndService the yearEndService to set
     * @see org.kuali.module.gl.service.YearEndService
     */
    public void setYearEndService(YearEndService yearEndService) {
        this.yearEndService = yearEndService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     * @see org.kuali.kfs.gl.service.OriginEntryGroupService
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

}
