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
package org.kuali.kfs.module.bc.document.validation.impl;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.bc.businessobject.CalculatedSalaryFoundationTrackerOverride;
import org.kuali.kfs.module.bc.document.service.CalculatedSalaryFoundationTrackerOverrideService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

public class CalculatedSalaryFoundationTrackerOverrideRule extends MaintenanceDocumentRuleBase {

    private CalculatedSalaryFoundationTrackerOverride oldCalculatedSalaryFoundationTrackerOverride;
    private CalculatedSalaryFoundationTrackerOverride newCalculatedSalaryFoundationTrackerOverride;
    private CalculatedSalaryFoundationTrackerOverrideService calculatedSalaryFoundationTrackerOverrideService;

    public CalculatedSalaryFoundationTrackerOverrideRule() {
        super();

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringContext, and configure the bean defs for spring.
        this.setCalculatedSalaryFoundationTrackerOverrideService(SpringContext.getBean(CalculatedSalaryFoundationTrackerOverrideService.class));
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects(document);
        success &= checkFiscalYearIsCurrent(document);
        success &= checkAppointmentIsValid();
        success &= checkPositionIsValid();

        return success;
    }

    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        setupConvenienceObjects(document);
        checkFiscalYearIsCurrent(document);
        checkAppointmentIsValid();
        checkPositionIsValid();
        return success;
    }

    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldCalculatedSalaryFoundationTrackerOverride = (CalculatedSalaryFoundationTrackerOverride) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newCalculatedSalaryFoundationTrackerOverride = (CalculatedSalaryFoundationTrackerOverride) super.getNewBo();
    }

    private boolean checkFiscalYearIsCurrent(MaintenanceDocument document) {
        boolean success = true;

        if ((ObjectUtils.isNotNull(newCalculatedSalaryFoundationTrackerOverride.getUniversityFiscalYear())) && (document.isNew())) {

            Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
            Integer universityFiscalYear = newCalculatedSalaryFoundationTrackerOverride.getUniversityFiscalYear();
            if (!(universityFiscalYear.equals(currentFiscalYear))) {
                putFieldError("universityFiscalYear", KFSKeyConstants.ERROR_FISCAL_YEAR_NOT_CURRENT, "Fiscal Year");
                success &= false;
            }
        }
        return success;
    }

    private boolean checkAppointmentIsValid() {
        boolean success = true;

        if ((ObjectUtils.isNotNull(newCalculatedSalaryFoundationTrackerOverride.getEmplid())) && (ObjectUtils.isNotNull(newCalculatedSalaryFoundationTrackerOverride.getPositionNumber())) && (ObjectUtils.isNotNull(newCalculatedSalaryFoundationTrackerOverride.getUniversityFiscalYear()))) {

            String emplid = newCalculatedSalaryFoundationTrackerOverride.getEmplid();
            String positionNumber = newCalculatedSalaryFoundationTrackerOverride.getPositionNumber();
            Integer universityFiscalYear = newCalculatedSalaryFoundationTrackerOverride.getUniversityFiscalYear();
            boolean result = calculatedSalaryFoundationTrackerOverrideService.isValidAppointment(universityFiscalYear, positionNumber, emplid);
            if (!result) {
                putFieldError("emplid", KFSKeyConstants.ERROR_INVALID_APPOINTMENT, "Employee Id");
                success &= false;
            }
        }
        else {
            putFieldError("emplid", KFSKeyConstants.ERROR_INVALID_APPOINTMENT, "Employee Id");
            success &= false;
        }
        return success;
    }

    private boolean checkPositionIsValid() {
        boolean success = true;

        if ((ObjectUtils.isNotNull(newCalculatedSalaryFoundationTrackerOverride.getPositionNumber())) && (ObjectUtils.isNotNull(newCalculatedSalaryFoundationTrackerOverride.getUniversityFiscalYear()))) {

            String emplid = newCalculatedSalaryFoundationTrackerOverride.getEmplid();
            String positionNumber = newCalculatedSalaryFoundationTrackerOverride.getPositionNumber();
            Integer universityFiscalYear = newCalculatedSalaryFoundationTrackerOverride.getUniversityFiscalYear();
            boolean result = calculatedSalaryFoundationTrackerOverrideService.isValidAppointment(universityFiscalYear, positionNumber, emplid);
            if (!result) {
                putFieldError("positionNumber", KFSKeyConstants.ERROR_INVALID_POSITION, "Position Number");
                success &= false;
            }
        }
        else {
            putFieldError("positionNumber", KFSKeyConstants.ERROR_INVALID_POSITION, "Position Number");
            success &= false;
        }
        return success;
    }

    /**
     * Sets the calculatedSalaryFoundationTrackerOverrideService attribute value.
     * 
     * @param calculatedSalaryFoundationTrackerOverrideService The calculatedSalaryFoundationTrackerOverrideService to set.
     */
    public void setCalculatedSalaryFoundationTrackerOverrideService(CalculatedSalaryFoundationTrackerOverrideService calculatedSalaryFoundationTrackerOverrideService) {
        this.calculatedSalaryFoundationTrackerOverrideService = calculatedSalaryFoundationTrackerOverrideService;
    }

}
