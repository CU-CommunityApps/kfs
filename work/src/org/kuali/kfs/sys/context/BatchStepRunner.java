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
package org.kuali.kfs.sys.context;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.service.ParameterService;

public class BatchStepRunner {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchStepRunner.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: You must pass the name of the step to run on the command line.");
            System.exit(8);
        }
        try {
            Log4jConfigurer.configureLogging(false);
            SpringContext.initializeBatchApplicationContext();
            String[] stepNames;
            if (args[0].indexOf(",") > 0) {
                stepNames = StringUtils.split(args[0], ",");
            }
            else {
                stepNames = new String[] { args[0] };
            }
            ParameterService parameterService = SpringContext.getBean(ParameterService.class);
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            
            String jobName = args.length >= 2 ? args[1] : KFSConstants.BATCH_STEP_RUNNER_JOB_NAME;
            Date jobRunDate = dateTimeService.getCurrentDate();
            LOG.info("Executing job: " + jobName + " steps: " + Arrays.toString(stepNames));
            for (int i = 0; i < stepNames.length; ++i) {
                Step step = BatchSpringContext.getStep(stepNames[i]);
                if ( step != null ) {
                    ModuleService module = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService( step.getClass() );                    
                    Appender ndcAppender = null; 
                    String nestedDiagnosticContext = 
                            StringUtils.substringAfter( module.getModuleConfiguration().getNamespaceCode(), "-").toLowerCase()
                            + File.separator + step.getName()
                            + "-" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());
                    boolean ndcSet = false;;
                    try {
                        ndcAppender = new FileAppender(Logger.getRootLogger().getAppender("LogFile").getLayout(), getLogFileName(nestedDiagnosticContext));
                        ndcAppender.addFilter(new NDCFilter(nestedDiagnosticContext));
                        Logger.getRootLogger().addAppender(ndcAppender);
                        NDC.push(nestedDiagnosticContext);
                        ndcSet = true;
                    } catch (Exception ex) {
                        LOG.warn("Could not initialize custom logging for step: " + step.getName(), ex);
                    }
                    try {
                        if (!Job.runStep(parameterService, jobName, i, step, jobRunDate) ) {
                            System.exit(4);
                        }
                    } catch ( RuntimeException ex ) {                        
                        throw ex;
                    } finally {
                        if ( ndcSet ) {
                            ndcAppender.close();
                            Logger.getRootLogger().removeAppender(ndcAppender);
                            NDC.pop();
                        }
                    }
                } else {
                    throw new IllegalArgumentException( "Unable to find step: " + stepNames[i] );
                }
            }
            LOG.info("Finished executing job: " + jobName + " steps: " + Arrays.toString(stepNames));
            System.exit(0);
        }
        catch (Throwable t) {
            System.err.println("ERROR: Exception caught: ");
            t.printStackTrace(System.err);
            System.exit(8);
        }
    }
    
    protected static String getLogFileName(String nestedDiagnosticContext) {
        return SpringContext.getBean( KualiConfigurationService.class ).getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY) 
                + File.separator 
                + nestedDiagnosticContext + ".log";
    }

}
