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
package org.kuali.module.chart.rules;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectCons;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.ObjectLevelService;

public class ObjectConsRule extends MaintenanceDocumentRuleBase {

    private static ChartService chartService;
    private static ObjectLevelService objectLevelService;
    private static ObjectCodeService objectCodeService;

    public ObjectConsRule() {
        if (chartService == null) {
            objectLevelService = SpringContext.getBean(ObjectLevelService.class);
            objectCodeService = SpringContext.getBean(ObjectCodeService.class);
            chartService = SpringContext.getBean(ChartService.class);
        }
    }

    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        ObjectCons objConsolidation = (ObjectCons) getNewBo();

        checkObjLevelCode(objConsolidation);
        checkEliminationCode(objConsolidation);
        return true;
    }

    /**
     * This method should be overridden to provide custom rules for processing document routing
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        ObjectCons objConsolidation = (ObjectCons) getNewBo();

        success &= checkObjLevelCode(objConsolidation);
        success &= checkEliminationCode(objConsolidation);
        return success;
    }

    /**
     * This method checks to see if the Object Consolidation code matches a pre-existing Object Level code that is already entered.
     * If it does it returns false with an error
     * 
     * @param document
     * @return false if Object Level Code already exists
     */
    private boolean checkObjLevelCode(ObjectCons objConsolidation) {
        boolean success = true;

        ObjLevel objLevel = objectLevelService.getByPrimaryId(objConsolidation.getChartOfAccountsCode(), objConsolidation.getFinConsolidationObjectCode());
        if (objLevel != null) {
            success = false;
            putFieldError("finConsolidationObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCONSMAINT_ALREADY_EXISTS_AS_OBJLEVEL);
        }
        return success;
    }

    /**
     * This method checks that the eliminations object code is really a valid current object code.
     * 
     * @return true if eliminations object code is a valid object code currently, false if otherwise
     */
    private boolean checkEliminationCode(ObjectCons objConsolidation) {
        boolean success = true;

        ObjectCode elimCode = objectCodeService.getByPrimaryIdForCurrentYear(objConsolidation.getChartOfAccountsCode(), objConsolidation.getFinancialEliminationsObjectCode());
        if (elimCode == null) {
            // KULRNE-61 - otherwise, allow the invalid value if the object is at the top of the hieratchy and the eliminiation
            // object code
            // is itself
            if (!objConsolidation.getFinConsolidationObjectCode().equals(objConsolidation.getFinancialEliminationsObjectCode()) || !chartService.getReportsToHierarchy().get(objConsolidation.getChartOfAccountsCode()).equals(objConsolidation.getChartOfAccountsCode())) {
                success = false;
                putFieldError("financialEliminationsObjectCode", KFSKeyConstants.ERROR_DOCUMENT_OBJCONSMAINT_INVALID_ELIM_OBJCODE, new String[] { objConsolidation.getFinancialEliminationsObjectCode() });
            }
        }
        return success;
    }
}
