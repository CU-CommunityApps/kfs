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

import java.util.Date;

import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.gl.service.NightlyOutService;

/**
 * A step to clear pending ledger entries.
 */
public class ClearPendingStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ClearPendingStep.class);
    private NightlyOutService nightlyOutService;

    /**
     * Runs the process of deleting copied ledger entries
     * 
     * @param jobName the name of the job that this step is being run as part of
     * @param jobRunDate the time/date the job is run
     * @return that the job completed successfully
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        nightlyOutService.deleteCopiedPendingLedgerEntries();
        return true;
    }

    /**
     * Sets the nightlyOutService attribute value.
     * 
     * @param nightlyOutService The nightlyOutService to set.
     * @see org.kuali.kfs.gl.service.NightlyOutService
     */
    public void setNightlyOutService(NightlyOutService nightlyOutService) {
        this.nightlyOutService = nightlyOutService;
    }
}
