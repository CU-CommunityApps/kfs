/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.kra.document;

import java.util.LinkedHashMap;

import org.kuali.core.document.Copyable;
import org.kuali.core.document.TransactionalDocumentBase;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Research Administration Document Base
 */
public abstract class ResearchDocumentBase extends TransactionalDocumentBase implements ResearchDocument, Copyable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchDocumentBase.class);

    /**
     * Sets up the collection instances and common document attributes.
     */
    public ResearchDocumentBase() {
        super();
    }
    
    /**
     * Overridden to note clear and add notes.
     * @see org.kuali.core.document.DocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException, IllegalStateException {
       super.setNewDocumentHeader();
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("versionNumber", getVersionNumber());
        m.put("comp", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isCompletionRequested()));
        m.put("app", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isApprovalRequested()));
        m.put("ack", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isAcknowledgeRequested()));
        m.put("fyi", Boolean.valueOf(getDocumentHeader().getWorkflowDocument().isFYIRequested()));

        return m;
    }
}
