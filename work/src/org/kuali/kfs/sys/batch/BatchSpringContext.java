/*
 * Copyright 2007-2008 The Kuali Foundation
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

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;

public class BatchSpringContext {
    private static final Logger LOG = Logger.getLogger(BatchSpringContext.class);
    
    public static Step getStep(String beanId) {
        return SpringContext.getBeansOfType(Step.class).get(beanId);
    }

    public static JobDescriptor getJobDescriptor(String beanId) {
        LOG.info("getJobDescriptor:::::" + beanId);
        return SpringContext.getBeansOfType(JobDescriptor.class).get(beanId);
    }

    public static TriggerDescriptor getTriggerDescriptor(String beanId) {
        return SpringContext.getBeansOfType(TriggerDescriptor.class).get(beanId);
    }

    public static BatchInputFileType getBatchInputFileType(String beanId) {
        return SpringContext.getBeansOfType(BatchInputFileType.class).get(beanId);
    }

    public static BatchInputFileSetType getBatchInputFileSetType(String beanId) {
        return SpringContext.getBeansOfType(BatchInputFileSetType.class).get(beanId);
    }
}
