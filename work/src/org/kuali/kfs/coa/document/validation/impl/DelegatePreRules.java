/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.document.validation.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rule.PreRulesCheck;
import org.kuali.rice.kns.rule.event.PreRulesCheckEvent;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * PreRules checks for the {@link Delegate} that needs to occur while still in the Struts processing. This includes defaults
 */
public class DelegatePreRules implements PreRulesCheck {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegatePreRules.class);

    /**
     * Default no-args constructor.
     */
    public DelegatePreRules() {
    }

    /**
     * This method sets some defaults on the {@link Delegate}
     * 
     * @see org.kuali.rice.kns.rule.PreRulesCheck#processPreRuleChecks(org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, org.kuali.rice.kns.rule.event.PreRulesCheckEvent)
     */
    public boolean processPreRuleChecks(ActionForm form, HttpServletRequest request, PreRulesCheckEvent event) {
        LOG.info("Entering processPreRuleChecks");

        // create some references to the relevant objects being looked at
        MaintenanceDocument document = (MaintenanceDocument) event.getDocument();
        AccountDelegate delegate = (AccountDelegate) document.getNewMaintainableObject().getBusinessObject();

        // set the defaults on the document
        setUnconditionalDefaults(delegate);

        return true;
    }

    /**
     * This method sets the approval from and to amounts to "0"
     * 
     * @param delegate
     */
    private void setUnconditionalDefaults(AccountDelegate delegate) {

        // FROM amount defaults to zero
        if (ObjectUtils.isNull(delegate.getFinDocApprovalFromThisAmt())) {
            delegate.setFinDocApprovalFromThisAmt(KualiDecimal.ZERO);
        }

        // TO amount defaults to zero
        if (ObjectUtils.isNull(delegate.getFinDocApprovalToThisAmount())) {
            delegate.setFinDocApprovalToThisAmount(KualiDecimal.ZERO);
        }
    }

}
