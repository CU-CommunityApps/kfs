/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.batch;

import java.util.Date;

import org.kuali.kfs.module.ar.batch.service.LockboxService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.kew.api.exception.WorkflowException;

public class LockboxStep extends AbstractStep {

    private LockboxService lockboxService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LockboxStep.class);

    @Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        boolean resultInd = true;
        try {
            resultInd = lockboxService.processLockboxes();
        } catch (WorkflowException e) {
            LOG.error("problem during lockboxService.processLockboxes()", e);
            throw new RuntimeException("Could not process lockbox documents", e);
        }
        return resultInd;
    }

    public void setLockboxService(LockboxService ls) {
        lockboxService = ls;
    }

}
