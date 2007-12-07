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
package org.kuali.module.budget.service.impl;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.budget.dao.BenefitsCalculationDao;
import org.kuali.module.budget.service.BenefitsCalculationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the BenefitsCalculationService interface
 */
@Transactional
public class BenefitsCalculationServiceImpl implements BenefitsCalculationService {

    private KualiConfigurationService kualiConfigurationService;
    private BenefitsCalculationDao benefitsCalculationDao;

    /**
     * @see org.kuali.module.budget.service.BenefitsCalculationService#getBenefitsCalculationDisabled()
     */
    public boolean getBenefitsCalculationDisabled() {
        // TODO for now just return false, implement application parameter if decision is made implement this functionality
        return false;

        // return kualiConfigurationService.getApplicationParameterIndicator(KFSConstants.ParameterGroups.SYSTEM,
        // BCConstants.DISABLE_BENEFITS_CALCULATION_FLAG);
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.BenefitsCalculationService#calculateAnnualBudgetConstructionGeneralLedgerBenefits(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    
    public void calculateAnnualBudgetConstructionGeneralLedgerBenefits(String documentNumber,
                                                                       Integer fiscalYear,
                                                                       String chartOfAccounts,
                                                                       String accountNumber,
                                                                       String subAccountNumber)
    {
        // do nothing if benefits calculation is disabled
        if (getBenefitsCalculationDisabled()) return;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BenefitsCalculationService#calculateMonthlyBudgetConstructionGeneralLedgerBenefits(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateMonthlyBudgetConstructionGeneralLedgerBenefits(String documentNumber,
                                                                        Integer fiscalYear,
                                                                        String chartOfAccounts,
                                                                        String accountNumber,
                                                                        String subAccountNumber)
    {
        //do nothing if benefits calculation is disabled
        if (getBenefitsCalculationDisabled()) return;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BenefitsCalculationService#calculateAllBudgetConstructionGeneralLedgerBenefits(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void calculateAllBudgetConstructionGeneralLedgerBenefits(String documentNumber,
                                                                    Integer fiscalYear,
                                                                    String chartOfAccounts,
                                                                    String accountNumber,
                                                                    String subAccountNumber)
    {
        //do nothing if benefits calculation is disabled
        if (getBenefitsCalculationDisabled()) return;
    }



    /**
     * Gets the kualiConfigurationService attribute.
     * 
     * @return Returns the kualiConfigurationService.
     */
    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    /**
     * 
     * This method allows spring to initialize the Dao, so we don't have to look up the bean on each call from the application
     * @param benefitsCalculationDao  the Dao for benefits calculation
     */
    public void setBenefitsCalculationDao(BenefitsCalculationDao benefitsCalculationDao)
    {
        this.benefitsCalculationDao = benefitsCalculationDao;
    }
    

    
}
