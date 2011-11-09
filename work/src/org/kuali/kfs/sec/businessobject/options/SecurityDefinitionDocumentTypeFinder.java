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
package org.kuali.kfs.sec.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.document.SecurityDefinitionMaintainableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.KeyValue; import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.kew.api.docType.DocumentType;
import org.kuali.rice.kew.api.exception.WorkflowException;

import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.core.framework.parameter.ParameterService; import java.util.ArrayList;


/**
 * Returns list of valid document type names for security definition
 */
public class SecurityDefinitionDocumentTypeFinder extends KeyValuesBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityDefinitionDocumentTypeFinder.class);

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();

        // add option to include all document types
        activeLabels.add(new ConcreteKeyValue(SecConstants.ALL_DOCUMENT_TYPE_NAME, SecConstants.ALL_DOCUMENT_TYPE_NAME));

//        WorkflowInfo workflowInfo = new WorkflowInfo();

        List<String> documentTypes = new ArrayList<String>( SpringContext.getBean(ParameterService.class).getParameterValuesAsString(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ACCESS_SECURITY_DOCUMENT_TYPES) );
     
        // copy list so it can be sorted (since it is unmodifiable)
        List<String> sortedDocumentTypes = new ArrayList<String>(documentTypes);
        Collections.sort(sortedDocumentTypes);
        
        for (String documentTypeName : sortedDocumentTypes) {
            DocumentType documentType = null;
            try {
                documentType = workflowInfo.getDocType(documentTypeName);
            }
            catch (WorkflowException e) {
                LOG.error("Invalid document type configured for security: " + documentTypeName);
                throw new RuntimeException("Invalid document type configured for security: " + documentTypeName);
            }

            if (documentType != null) {
                activeLabels.add(new ConcreteKeyValue(documentTypeName, documentType.getLabel()));
            }
        }

        return activeLabels;
    }
}
