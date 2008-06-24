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
package org.kuali.kfs.sys.batch.service;

import java.util.List;

import org.kuali.kfs.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;

@ConfigureContext(session = UserNameFixture.KULUSER, initializeBatchSchedule = true)
public class SchedulerServiceImplTest extends KualiTestBase {

    // tests added to make sure that the scheduler was available during the tests
    public void testGetJobs_unscheduled() {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        List<BatchJobStatus> jobs = s.getJobs("unscheduled");
        for (BatchJobStatus job : jobs) {
            System.out.println(job);
        }
    }

    public void testGetJobs_scheduled() {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        List<BatchJobStatus> jobs = s.getJobs("scheduled");
        for (BatchJobStatus job : jobs) {
            System.out.println(job);
        }
    }

    /*
     * // this job seems to be missing from the system during the test runs public void testScheduledJobTrigger() throws Exception {
     * SchedulerService s = SpringContext.getBean(SchedulerService.class); BatchJobStatus job = s.getJob( "scheduled", "scheduleJob" );
     * assertNotNull( "job must not be null", job ); System.out.println( "scheduleJob Next Run Time: " + job.getNextRunDate() );
     * Calendar cal = Calendar.getInstance(); cal.setTime( job.getNextRunDate() ); assertEquals( "year must be 2099", 2099, cal.get(
     * Calendar.YEAR ) ); }
     */


    /**
     * Test the running of a job. There is not much to test on the results, except that this does not cause an error.
     */

    public void testRunJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob");
        assertNotNull("job must not be null", job);
        System.out.println(job);
        job.runJob(null);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
        Thread.sleep(1000);
        System.out.println(s.getRunningJobs());
        System.out.println(s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob"));
    }

    /**
     * Test that the unschedule job function works and removes the job from the standard scheduled group. Assumes: scrubberJob
     * exists as a job in the scheduled group.
     */

    public void testUnscheduleJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob");
        assertNotNull("job must not be null", job);

        assertTrue("must return isScheduled == true", job.isScheduled());

        s.removeScheduled(job.getName());
        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "scrubberJob");
        assertNull("new attempt to retrieve job must be null", job);

        job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob");
        assertNotNull("job must not be null", job);
        assertFalse("must return isScheduled == false", job.isScheduled());
    }

    /**
     * Test that the schedule job function works and puts the job into the standard scheduled group. Also tests to make sure that
     * BatchJobStatus detects the scheduled status even if it is in the unscheduled group. Assumes: clearOldOriginEntriesJob exists
     * as a job in the unscheduled group.
     */

    public void testScheduleJob() throws Exception {
        SchedulerService s = SpringContext.getBean(SchedulerService.class);
        BatchJobStatus job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "clearOldOriginEntriesJob");
        assertNotNull("job must not be null", job);

        assertFalse("must return isScheduled == false", job.isScheduled());

        job.schedule();

        job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "clearOldOriginEntriesJob");
        assertNotNull("job (in unsched group) must not be null", job);
        assertTrue("must return isScheduled == true", job.isScheduled());

        job = s.getJob(SchedulerService.SCHEDULED_GROUP, "clearOldOriginEntriesJob");
        assertNotNull("job (in sched group) must not be null", job);
        assertTrue("must return isScheduled == true", job.isScheduled());

    }

    /*
     * This test has problems with timing. It needs a job that it can rely on running for a specified period of time before being
     * included in the automated tests.
     */
    /*
     * public void testJobInterrupt() throws Exception{ SchedulerService s = SpringContext.getBean(SchedulerService.class);
     * BatchJobStatus job = s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob" ); job.runJob( null ); // wait for job to
     * enter running status int waitCount = 0; // provide an "out" in case things fail badly while ( !job.isRunning() && waitCount <
     * 500 ) { Thread.sleep( 50 ); waitCount++; } // stop the job job.interrupt(); waitCount = 0; while ( job.isRunning() &&
     * waitCount < 500 ) { Thread.sleep( 50 ); waitCount++; } Thread.sleep( 2000 ); job =
     * s.getJob(SchedulerService.UNSCHEDULED_GROUP, "scrubberJob" ); List<BatchJobStatus> jobs = s.getJobs(); for ( BatchJobStatus
     * b : jobs ) { System.out.println( b ); } assertEquals( "job status not correct", SchedulerService.CANCELLED_JOB_STATUS_CODE,
     * job.getStatus() ); }
     */
}
