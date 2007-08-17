/*
 * Copyright 2007 The Kuali Foundation. Licensed under the Educational Community License, Version 1.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.kuali.module.labor.util;

import org.kuali.kfs.batch.JobDescriptor;
import org.kuali.kfs.batch.Step;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.UserNameFixture;

@ConfigureContext(session=UserNameFixture.KULUSER)
public class LaborBatchRunner extends KualiTestBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBatchRunner.class);

    public void testRunBatch() {
        JobDescriptor laborBatchJob = SpringContext.getJobDescriptor("laborBatchJob");
        for (Step step : laborBatchJob.getSteps()) {
            runStep(step);
        }
    }
    
    private void runStep(Step step){
        try {
            String stepName = step.getName();
            
            long start = System.currentTimeMillis();
            System.out.println(stepName + " started at " + start);

            boolean isSuccess = step.execute(getClass().getName());

            long elapsedTime = System.currentTimeMillis() - start;
            System.out.println("Execution Time = " + elapsedTime + "(" + isSuccess + ")");
        }
        catch (Exception e) {
            System.out.println(e);
        } 
    }
}