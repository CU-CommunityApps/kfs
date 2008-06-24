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
package org.kuali.kfs.module.bc.document.service;

/**
 * This interface defines the methods that perform Pullup or Pushdown operations on Budget Construction Documents.
 */
public interface BudgetPushPullService {
    
    /**
     * Pulls up Budget Construction documents based on user selected Organizations and the current point of view Organization
     * on the Organization Selection screen running in Pullup mode. 
     * 
     * @param personUniversalIdentifier
     * @param FiscalYear
     * @param pointOfViewCharOfAccountsCode
     * @param pointOfViewOrganizationCode
     */
    public void pullupSelectedOrganizationDocuments(String personUniversalIdentifier, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode);

    /**
     * Pushes down Budget Construction documents based on user selected Organizations and the current point of view Organization
     * on the Organization Selection screen running in Pushdown mode. 
     * 
     * @param personUniversalIdentifier
     * @param FiscalYear
     * @param pointOfViewCharOfAccountsCode
     * @param pointOfViewOrganizationCode
     */
    public void pushdownSelectedOrganizationDocuments(String personUniversalIdentifier, Integer FiscalYear, String pointOfViewCharOfAccountsCode, String pointOfViewOrganizationCode);
}
