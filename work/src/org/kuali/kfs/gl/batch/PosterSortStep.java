/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.io.File;
import java.util.Comparator;
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.BatchSortService;
import org.kuali.kfs.gl.exception.LoadException;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.rice.kns.util.GlobalVariables;
import org.springframework.util.StopWatch;

/**
 * A step to run the scrubber process.
 */
public class PosterSortStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterSortStep.class);
    private String batchFileDirectoryName;
    /**
     * Runs the scrubber process.
     * 
     * @param jobName the name of the job this step is being run as part of
     * @param jobRunDate the time/date the job was started
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);
        String inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String outputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        
        BatchSortUtil.sortTextFileWithFields(inputFile, outputFile, new PosterSortComparator());

        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("scrubber step of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
    }

    
    public static class PosterSortComparator implements Comparator {

        public int compare(Object object1, Object object2) {
            String string1 = (String) object1;
            String string2 = (String) object2;
            StringBuffer sb1 = new StringBuffer();
            
            sb1.append(string1.substring(0, 51));
            // Shawn - something wrong
            //sb1.append(string1.substring(129, 147));
            sb1.append(string1.substring(137, 155));
            
            StringBuffer sb2 = new StringBuffer();
            sb2.append(string1.substring(0, 51));
            // Shawn - something wrong
            //sb2.append(string1.substring(129, 147));
            sb2.append(string1.substring(137, 155));
            return sb1.toString().compareTo(sb2.toString());
        }
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
