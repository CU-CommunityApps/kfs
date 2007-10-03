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
package org.kuali.kfs.batch;

import org.apache.log4j.Logger;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.SchedulerService;

public class ScheduleStep extends AbstractStep {
    private static final Logger LOG = Logger.getLogger(ScheduleStep.class);
    private SchedulerService schedulerService;

    /**
     * @see org.kuali.kfs.batch.Step#execute()
     */
    public boolean execute(String jobName) {
        boolean isPastScheduleCutoffTime = false;
        while (schedulerService.hasIncompleteJob() && !isPastScheduleCutoffTime) {
            schedulerService.processWaitingJobs();
            isPastScheduleCutoffTime = schedulerService.isPastScheduleCutoffTime();
            try {
                Thread.sleep(Integer.parseInt(getParameterService().getParameterValue(getClass(), KFSConstants.SystemGroupParameterNames.BATCH_SCHEDULE_STATUS_CHECK_INTERVAL)));
            }
            catch (InterruptedException e) {
                throw new RuntimeException("Schedule step encountered interrupt exception while trying to wait for the specified batch schedule status check interval", e);
            }
        }
        if (isPastScheduleCutoffTime) {
            LOG.info("Schedule exceeded cutoff time, so it was terminated before completion");
        }
        schedulerService.logScheduleResults();
        return !isPastScheduleCutoffTime;
    }

    /**
     * Sets the schedulerService attribute value.
     * 
     * @param schedulerService The schedulerService to set.
     */
    public void setSchedulerService(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
