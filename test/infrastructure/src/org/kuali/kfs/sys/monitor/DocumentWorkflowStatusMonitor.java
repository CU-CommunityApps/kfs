/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.sys.monitor;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

/**
 * DocumentWorkflowStatusMonitor
 */
public class DocumentWorkflowStatusMonitor extends ChangeMonitor {
    final DocumentService documentService;
    final private String docHeaderId;
    final private String[] desiredWorkflowStates;

    public DocumentWorkflowStatusMonitor(DocumentService documentService, String docHeaderId, String desiredWorkflowStatus) {
        this.documentService = documentService;
        this.docHeaderId = docHeaderId;
        this.desiredWorkflowStates = new String[] { desiredWorkflowStatus };
    }

    public DocumentWorkflowStatusMonitor(DocumentService documentService, String docHeaderId, String[] desiredWorkflowStates) {
        this.documentService = documentService;
        this.docHeaderId = docHeaderId;
        this.desiredWorkflowStates = desiredWorkflowStates;
    }

    public boolean valueChanged() throws Exception {
        Document d = documentService.getByDocumentHeaderId(docHeaderId.toString());

        DocumentStatus currentStatus = d.getDocumentHeader().getWorkflowDocument().getStatus();

        for (int i = 0; i < desiredWorkflowStates.length; i++) {
            if (StringUtils.equals(desiredWorkflowStates[i], currentStatus.getCode())) {
                return true;
            }
        }
        return false;
    }
}
