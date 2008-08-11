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
 * Rule interface for <code>SalarySettingSaveEvent<code>. Rule classes wishing to respond to that event should implement this interface.
 */
// TODO: update the Java Doc 
public interface SalarySettingRule extends BusinessRule {

    /**
     * Process save business rules for the detail salary setting screen.
     * 
     * @param appointmentFundings the appointment funding lines associated with position
     * @return boolean true if rules were ok, false if errors were found
     */
    public boolean processSave(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings);
    
    /**
     * Process save business rules for the detail salary setting screen.
     * 
     * @param appointmentFundings the appointment funding lines associated with position
     * @return boolean true if rules were ok, false if errors were found
     */
    public boolean processAdjustSalaraySettingLinePercent(PendingBudgetConstructionAppointmentFunding appointmentFundings);
    
    /**
     * Process save business rules for the detail salary setting screen.
     * 
     * @param appointmentFundings the appointment funding lines associated with position
     * @return boolean true if rules were ok, false if errors were found
     */
    public boolean processNormalizePayrateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding);
    
    /**
     * Process save business rules for the detail salary setting screen.
     * 
     * @param appointmentFundings the appointment funding lines associated with position
     * @return boolean true if rules were ok, false if errors were found
     */
    public boolean processAdjustAllSalarySettingLinesPercent(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings);  
    
    /**
     * Process save business rules for the detail salary setting screen.
     * 
     * @param appointmentFunding the appointment funding lines associated with position
     * @return boolean true if rules were ok, false if errors were found
     */
    public boolean processAddAppointmentFunding(PendingBudgetConstructionAppointmentFunding appointmentFunding);
}
