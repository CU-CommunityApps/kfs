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
package org.kuali.module.labor.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.labor.service.LaborScrubberService;

/**
 * Labor scrubber Batch Step.
 */
public class LaborScrubberStep extends AbstractStep {
    private LaborScrubberService laborScrubberService;

    /**
     * @param String jobName
     * @return boolean when success
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName) {
        laborScrubberService.scrubEntries();
        return true;
    }

    /**
     * Sets the labor scrubber service
     * 
     * @param laborScrubberService
     */
    public void setLaborScrubberService(LaborScrubberService laborScrubberService) {
        this.laborScrubberService = laborScrubberService;
    }

}
