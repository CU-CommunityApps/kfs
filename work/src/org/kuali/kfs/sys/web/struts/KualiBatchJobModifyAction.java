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
package org.kuali.kfs.sys.web.struts;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchJobStatus;
import org.kuali.kfs.sys.batch.service.SchedulerService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;

public class KualiBatchJobModifyAction extends KualiAction {

    private static final String JOB_NAME_PARAMETER = "name";
    private static final String JOB_GROUP_PARAMETER = "group";
    private static final String START_STEP_PARAMETER = "startStep";
    private static final String END_STEP_PARAMETER = "endStep";
    private static final String START_TIME_PARAMETER = "startTime";
    private static final String EMAIL_PARAMETER = "emailAddress";

    private static SchedulerService schedulerService;
    private static ParameterService parameterService;

    private SchedulerService getSchedulerService() {
        if (schedulerService == null) {
            schedulerService = SpringContext.getBean(SchedulerService.class);
        }
        return schedulerService;
    }

    public static ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

    private BatchJobStatus getJob(HttpServletRequest request) {
        // load the given job and map into the form
        String jobName = request.getParameter(JOB_NAME_PARAMETER);
        String jobGroup = request.getParameter(JOB_GROUP_PARAMETER);

        return getSchedulerService().getJob(jobGroup, jobName);
    }


    private boolean canModifyJob(BatchJobStatus job, String actionType) {
        try {
            checkJobAuthorization(job, actionType);
        }
        catch (AuthorizationException ex) {
            return false;
        }
        return true;
    }

    /**
     * Performs the actual authorization check for a given job and action against the current user. This method can be overridden by
     * sub-classes if more granular controls are desired.
     * 
     * @param job
     * @param actionType
     * @throws AuthorizationException
     */
    protected void checkJobAuthorization(BatchJobStatus job, String actionType) throws AuthorizationException {
        String adminWorkgroup = getParameterService().getParameterValue(ParameterConstants.FINANCIAL_SYSTEM_BATCH.class, KFSConstants.SystemGroupParameterNames.JOB_ADMIN_WORKGROUP);
        if (getParameterService().parameterExists(ParameterConstants.FINANCIAL_SYSTEM_BATCH.class, job.getFullName() + KFSConstants.SystemGroupParameterNames.JOB_WORKGROUP_SUFFIX)) {
            String jobSpecificAdminWorkgroup = getParameterService().getParameterValue(ParameterConstants.FINANCIAL_SYSTEM_BATCH.class, job.getFullName() + KFSConstants.SystemGroupParameterNames.JOB_WORKGROUP_SUFFIX);
            if (!(GlobalVariables.getUserSession().getFinancialSystemUser().isMember(adminWorkgroup) || GlobalVariables.getUserSession().getFinancialSystemUser().isMember(jobSpecificAdminWorkgroup))) {
                throw new AuthorizationException(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUserIdentifier(), actionType, job.getFullName());
            }
        }
    }

    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob(request);

        request.setAttribute("job", job);
        request.setAttribute("canRunJob", canModifyJob(job, "runJob"));
        request.setAttribute("canSchedule", canModifyJob(job, "schedule"));
        request.setAttribute("canUnschedule", canModifyJob(job, "unschedule"));
        request.setAttribute("canStopJob", canModifyJob(job, "stopJob"));
        request.setAttribute("userEmailAddress", GlobalVariables.getUserSession().getFinancialSystemUser().getPersonEmailAddress());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward runJob(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob(request);

        checkJobAuthorization(job, "runJob");

        String startStepStr = request.getParameter(START_STEP_PARAMETER);
        String endStepStr = request.getParameter(END_STEP_PARAMETER);
        String startTimeStr = request.getParameter(START_TIME_PARAMETER);
        String emailAddress = request.getParameter(EMAIL_PARAMETER);

        int startStep = Integer.parseInt(startStepStr);
        int endStep = Integer.parseInt(endStepStr);
        Date startTime = SpringContext.getBean(DateTimeService.class).getCurrentDate();
        if (!StringUtils.isBlank(startTimeStr)) {
            startTime = SpringContext.getBean(DateTimeService.class).convertToDateTime(startTimeStr);
        }

        job.runJob(startStep, endStep, startTime, emailAddress);

        // redirect to display form to prevent re-execution of the job by mistake
        return getForward(job);
    }

    public ActionForward stopJob(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob(request);

        checkJobAuthorization(job, "stopJob");

        job.interrupt();

        return getForward(job);
    }


    public ActionForward schedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob(request);

        checkJobAuthorization(job, "schedule");

        job.schedule();

        return getForward(job);
    }

    public ActionForward unschedule(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BatchJobStatus job = getJob(request);

        checkJobAuthorization(job, "unschedule");

        job.unschedule();

        // move to the unscheduled job object since the scheduled one has been removed
        job = getSchedulerService().getJob(SchedulerService.UNSCHEDULED_GROUP, job.getName());

        return getForward(job);
    }

    private ActionForward getForward(BatchJobStatus job) {
        return new ActionForward(SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY) + "/batchModify.do?methodToCall=start&name=" + UrlFactory.encode(job.getName()) + "&group=" + UrlFactory.encode(job.getGroup()), true);
    }
}
