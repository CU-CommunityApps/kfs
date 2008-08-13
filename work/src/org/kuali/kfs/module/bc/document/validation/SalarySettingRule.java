/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.validation;

import java.util.List;

import org.kuali.core.rule.BusinessRule;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;

/**
 * Rule classes wishing to respond to that event should implement this interface.
 */
public interface SalarySettingRule extends BusinessRule {

    /**
     * process the rules before the given appointment funding is saved
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the appointment funding can pass all rule before saved, otherwise, false
     */
    public boolean processSaveAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding);

    /**
     * process the rules before the given appointment funding is created
     * 
     * @param existingAppointmentFundings the existing appointment fundings
     * @param appointmentFunding the given appointment funding
     * @return true if the appointment funding can pass all rule before created, otherwise, false
     */
    public boolean processAddAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> existingAppointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding);
}
