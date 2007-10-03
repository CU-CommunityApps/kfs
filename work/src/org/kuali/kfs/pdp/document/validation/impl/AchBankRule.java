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
package org.kuali.module.pdp.rules;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.rules.AccountingPeriodRule;
import org.kuali.module.pdp.bo.AchBank;

public class AchBankRule extends MaintenanceDocumentRuleBase {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchBank.class);
    
    private AchBank oldAchBank;
    private AchBank newAchBank;

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    public void setupConvenienceObjects() {
        
        LOG.info("setupConvenienceObjects called");

        // setup oldAchBank convenience objects, make sure all possible sub-objects are populated
        oldAchBank = (AchBank) super.getOldBo();

        // setup newAchBank convenience objects, make sure all possible sub-objects are populated
        newAchBank = (AchBank) super.getNewBo();
    }
    
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        
        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        
        boolean validEntry = true;

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();
        
        String typeCode = newAchBank.getBankTypeCode();
        if (!typeCode.equals("1") && !typeCode.equals("2") && !typeCode.equals("3")) {
            putFieldError("bankTypeCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_TYPE_CODE, typeCode);
            validEntry = false;
        }
        
        String officeCode = newAchBank.getBankOfficeCode();
        if (!officeCode.equals("O") && !typeCode.equals("B")) {
            putFieldError("bankOfficeCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_OFFICE_CODE, officeCode);
            validEntry = false;
        }
        
        String bankInstitutionStatusCode = newAchBank.getBankInstitutionStatusCode();
        if (!bankInstitutionStatusCode.equals("") && !bankInstitutionStatusCode.equals("1")) {
            putFieldError("bankInstitutionStatusCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_INST_STATUS_CODE, bankInstitutionStatusCode);
            validEntry = false;
        }
        
        String bankDataViewCode = newAchBank.getBankDataViewCode();
        if (!bankDataViewCode.equals("") && !bankDataViewCode.equals("1")) {
            putFieldError("bankDataViewCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_DATA_VIEW_CODE, bankDataViewCode);
            validEntry = false;
        }
        
        return validEntry;
    }

}
