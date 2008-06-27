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
package org.kuali.kfs.coa.document;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.MaintainableSectionDefinition;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.MaintenanceUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryExclusionType;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryType;
import org.kuali.kfs.coa.service.ChartService;

public class IndirectCostRecoveryTypeMaintainableImpl extends KualiMaintainableImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IndirectCostRecoveryTypeMaintainableImpl.class);
    public static final String DOCUMENT_ERROR_PREFIX = "document.";
    public static final String MAINTAINABLE_ERROR_PATH = DOCUMENT_ERROR_PREFIX + "newMaintainableObject";
    public static final String DETAIL_ERROR_PATH = MAINTAINABLE_ERROR_PATH + ".add.indirectCostRecoveryExclusionByTypeDetails";

    @Override
    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo) {
        // PersistableBusinessObject bo = document.getNewMaintainableObject().getBusinessObject();
        Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(bo, collectionName);
        String docTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentType();
        
        List<String> duplicateIdentifierFieldsFromDataDictionary = getDuplicateIdentifierFieldsFromDataDictionary(docTypeName, collectionName);
        
        List<String> existingIdentifierList = getMultiValueIdentifierList(maintCollection, duplicateIdentifierFieldsFromDataDictionary);
        
        Class collectionClass = getMaintenanceDocumentDictionaryService().getCollectionBusinessObjectClass(docTypeName, collectionName);

        List<MaintainableSectionDefinition> sections = getMaintenanceDocumentDictionaryService().getMaintainableSections(docTypeName);
        Map<String, String> template = MaintenanceUtils.generateMultipleValueLookupBOTemplate(sections, collectionName);
        try {
            GlobalVariables.getErrorMap().addToErrorPath(DETAIL_ERROR_PATH);
            int collectionItemNumber = 0; // is there a better way to do this? ie- old school i=0;i<rawValues.size();i++ and rawValues.get(i)?
            for (PersistableBusinessObject nextBo : rawValues) {
                IndirectCostRecoveryExclusionType templatedBo = (IndirectCostRecoveryExclusionType) ObjectUtils.createHybridBusinessObject(collectionClass, nextBo, template);
                templatedBo.setNewCollectionRecord(true);
                prepareBusinessObjectForAdditionFromMultipleValueLookup(collectionName, templatedBo);
                if(!hasBusinessObjectExisted(templatedBo, existingIdentifierList, duplicateIdentifierFieldsFromDataDictionary)) {
                    if(templatedBo.getChartOfAccountsCode().equals(SpringContext.getBean(ChartService.class).getUniversityChart().getChartOfAccountsCode())) {
                        maintCollection.add(templatedBo);
                    } else {
                        GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.IndirectCostRecovery.ERROR_DOCUMENT_ICR_CHART_NOT_UNIVERSITY_CHART_MULTIVALUE_LOOKUP, templatedBo.getChartOfAccountsCode(), SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
                    }
                }
                collectionItemNumber++;
                templatedBo.setActive(true); // TODO remove after active indicator work is complete
            }
            GlobalVariables.getErrorMap().removeFromErrorPath(DETAIL_ERROR_PATH);
        } 
        catch (Exception e) {
            LOG.error("Unable to add multiple value lookup results " + e.getMessage());
            throw new RuntimeException("Unable to add multiple value lookup results " + e.getMessage());
        }
    }
    
    @Override
    public void processAfterNew( MaintenanceDocument document, Map<String,String[]> parameters ) {
        IndirectCostRecoveryType bo = (IndirectCostRecoveryType) document.getNewMaintainableObject().getBusinessObject();
        bo.setActive(true);
    }
    
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        IndirectCostRecoveryType bo = (IndirectCostRecoveryType) document.getNewMaintainableObject().getBusinessObject();
        bo.setActive(true);        
    }
    
}
