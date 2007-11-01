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
/*
 * Created on Nov 2, 2004
 *
 */
package org.kuali.module.pdp.action.format;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.pdp.action.BaseAction;
import org.kuali.module.pdp.service.FormatService;
import org.kuali.module.pdp.service.SecurityRecord;


/**
 * @author delyea
 */
public class FormatResetAction extends BaseAction {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FormatResetAction.class);
    private FormatService formatService;

    public FormatResetAction() {
        super();
        setFormatService(SpringContext.getBean(FormatService.class));
    }

    public void setFormatService(FormatService fas) {
        formatService = fas;
    }

    protected boolean isAuthorized(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        SecurityRecord sr = getSecurityRecord(request);
        return sr.isProcessRole();
    }

    protected ActionForward executeLogic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("executeLogic() started");

        Integer procId = new Integer(request.getParameter("processId"));

        if ((procId != new Integer(0)) && (procId != null)) {
            formatService.resetFormatPayments(procId);
        }

        return mapping.findForward("selection");
    }
}
