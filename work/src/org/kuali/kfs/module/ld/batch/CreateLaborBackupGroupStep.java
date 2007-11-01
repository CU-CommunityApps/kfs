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
import org.kuali.module.gl.service.OriginEntryGroupService;

/**
 * Create Labor backup Batch Step.
 */
public class CreateLaborBackupGroupStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateLaborBackupGroupStep.class);
    private OriginEntryGroupService originEntryGroupService;

    /**
     * Invokes the method that creates labor backup group
     * 
     * @param jobName
     * @return boolean
     * @see org.kuali.kfs.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName) {
        originEntryGroupService.createLaborBackupGroup();
        return true;
    }

    /**
     * Sets the Origin Entry Group Service
     * 
     * @param oegs
     */
    public void setOriginEntryGroupService(OriginEntryGroupService oegs) {
        originEntryGroupService = oegs;
    }
}
