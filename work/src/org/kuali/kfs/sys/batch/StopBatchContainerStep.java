/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.sys.batch;

import java.util.Date;

/**
 * StopBatchContainerStep triggers the BatchContainerStep to shut itself down.
 *
 */
public class StopBatchContainerStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(StopBatchContainerStep.class);

    /**
     * The BatchContainerStep recognizes the name of this Step and exits without executing this method.
     */
	@Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {

		LOG.info("Stopping Job: "+ jobName +", Date: "+ jobRunDate);
		return true;
	}
}
