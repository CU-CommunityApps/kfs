/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.vnd.identity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

public class CommodityReviewRoleTypeServiceImpl extends RoleTypeServiceBase {
    private DocumentTypeService documentTypeService;
    
    @Override
    protected boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        if (qualification.containsKey(KfsKimAttributes.PURCHASING_COMMODITY_CODE) && qualification.get(KfsKimAttributes.PURCHASING_COMMODITY_CODE).matches(roleQualifier.get(KfsKimAttributes.PURCHASING_COMMODITY_CODE).replaceAll("\\*", ".*")) && KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KimConstants.AttributeConstants.CAMPUS_CODE)) {
            Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
            if (roleQualifier.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                potentialParentDocumentTypeNames.add(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
            }
            return potentialParentDocumentTypeNames.isEmpty() || qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME).equalsIgnoreCase(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
        }
        return false;
    }
    
    protected DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        }
        return this.documentTypeService;
    }
}
