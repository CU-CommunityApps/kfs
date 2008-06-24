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
package org.kuali.kfs.module.cg.document.service;

import java.util.Collection;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetFringeRate;
import org.kuali.kfs.module.cg.businessobject.BudgetUser;


/**
 * This interface defines methods that an Budget Fringe Rate Service must provide
 */
public interface BudgetFringeRateService {
    /**
     * Returns active AppointmentTypes.
     * 
     * @return active AppointmentType objects
     */
    public Collection getDefaultFringeRates();

    public boolean isValidFringeRate(KualiDecimal fringeRate);

    public boolean isValidCostShare(KualiDecimal costShare);

    public void setupDefaultFringeRates(Budget budget);

    public BudgetFringeRate getBudgetFringeRateForPerson(BudgetUser budgetUser);

    public BudgetFringeRate getBudgetFringeRateForDefaultAppointmentType(String documentNumber);

    public BudgetFringeRate getBudgetFringeRate(String documentNumber, String institutionAppointmentTypeCode);

}
