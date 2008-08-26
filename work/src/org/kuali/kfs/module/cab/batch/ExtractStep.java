/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.batch;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.batch.service.BatchExtractService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.kns.service.DateTimeService;

public class ExtractStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(ExtractStep.class);
    private BatchExtractService batchExtractService;
    private DateTimeService dateTimeService;

    /**
     * CAB Extract Steps
     * <li>Find all GL transactions created after last extract time matching CAB filter parameters</li>
     * <li>For each GL line perform the following steps</li>
     * <li>Check GL lines belongs to a PO or Not</li>
     * <li>If not PO related, then check for duplicate in CB_GL_ENTRY_T, if not duplicate insert into it</li>
     * <li>If PO related, check against PURAP Account Line History for respective PREQ or CM document</li>
     * <li>If sum of amounts matches grouped by fields "univ_fiscal_yr, fin_coa_cd, account_nbr, sub_acct_nbr, fin_object_cd,
     * fin_sub_obj_cd, univ_fiscal_prd_cd, fdoc_nbr, fdoc_ref_nbr" then it is a valid one, else ignore the GL lines for the document
     * and write to the reconciliation report </li>
     * <li>If CB_PUR_DOC entry doesn't exist, insert new record else update active indicator</li>
     * <li>Insert into CB_PUR_ITM_AST_T when record not exists or inactive, if active exact match found then skip</li>
     * <li>Insert one/multiple entries into CB_PUR_LN_AST_ACCT_T, amount should match exact from account line history</li>
     * 
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String, java.util.Date)
     */
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        ExtractProcessLog processLog = new ExtractProcessLog();
        try {
            Timestamp startTs = dateTimeService.getCurrentTimestamp();
            LOG.info("CAB batch started at " + startTs);
            Collection<Entry> elgibleGLEntries = batchExtractService.findElgibleGLEntries();

            if (elgibleGLEntries != null && !elgibleGLEntries.isEmpty()) {
                List<Entry> fpLines = new ArrayList<Entry>();
                List<Entry> purapLines = new ArrayList<Entry>();
                // Separate PO lines
                batchExtractService.separatePOLines(fpLines, purapLines, elgibleGLEntries);
                // Save the Non-PO lines
                batchExtractService.saveFPLines(fpLines, processLog);
                // Save the PO lines
                batchExtractService.savePOLines(purapLines, processLog);
                // Set the log values
                processLog.setTotalGlCount(elgibleGLEntries.size());
                processLog.setNonPurApGlCount(fpLines.size());
                processLog.setPurApGlCount(purapLines.size());
            }
            else {
                LOG.info("****** No records processed during CAB Extract *******");
            }
            // Update the last extract time stamp
            batchExtractService.updateLastExtractTime(startTs);
            LOG.info("CAB batch finished at " + dateTimeService.getCurrentTimestamp());
        }
        catch (Exception e) {
            processLog.setSuccess(false);
            processLog.setErrorMessage("Unexpected error occured while performing CAB Extract. " + e.toString());
            LOG.error("Unexpected error occured while performing CAB Extract.", e);
            new RuntimeException(e);
        }
        finally {
            batchExtractService.sendStatusEmail(processLog);
            LOG.info("Batch status email is sent successfully.");
        }
        return true;
    }

    /**
     * Gets the batchExtractService attribute.
     * 
     * @return Returns the batchExtractService.
     */
    public BatchExtractService getBatchExtractService() {
        return batchExtractService;
    }

    /**
     * Sets the batchExtractService attribute value.
     * 
     * @param batchExtractService The batchExtractService to set.
     */
    public void setBatchExtractService(BatchExtractService batchExtractService) {
        this.batchExtractService = batchExtractService;
    }

    /**
     * Gets the dateTimeService attribute.
     * 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


}
