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
package org.kuali.module.chart.rules;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.AccountingPeriod;

/**
 * Business rule(s) applicable to AccountingPeriodMaintence documents.
 */
public class AccountingPeriodRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingPeriodRule.class);

    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";

    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    private static final String SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS = "MPRACT";
    private static final String BUDGET_RECORDING_LEVEL_MIXED = "M";

    private AccountingPeriod oldAccountingPeriod;
    private AccountingPeriod newAccountingPeriod;

    public AccountingPeriodRule() {
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    public void setupConvenienceObjects() {

        // setup oldAccountingPeriod convenience objects, make sure all possible sub-objects are populated
        oldAccountingPeriod = (AccountingPeriod) super.getOldBo();

        // setup newAccountingPeriod convenience objects, make sure all possible sub-objects are populated
        newAccountingPeriod = (AccountingPeriod) super.getNewBo();
    }

    /**
     * This method checks the following rules: calls processCustomRouteDocumentBusinessRules but does not fail if any of them fail
     * (this only happens on routing)
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    /**
     * This method checks to see if the fiscal year for any of {@link Options} is the same as the {@link AccountingPeriod}'s fiscal
     * year
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        Boolean foundYear = false;

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        List optionList = (List) boService.findAll(Options.class);
        for (Iterator iter = optionList.iterator(); iter.hasNext();) {
            Options options = (Options) iter.next();
            if (options.getUniversityFiscalYear().compareTo(newAccountingPeriod.getUniversityFiscalYear()) == 0) {
                foundYear = true;
                break;
            }
        }

        if (!foundYear) {
            // display an error
            putFieldError("universityFiscalYear", KFSKeyConstants.ERROR_DOCUMENT_FISCAL_PERIOD_YEAR_DOESNT_EXIST);
        }

        return foundYear;
    }

}