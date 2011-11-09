/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants.PurchaseOrderEditMode;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.document.Document;


public class PurchaseOrderAmendmentDocumentPresentationController extends PurchaseOrderDocumentPresentationController {
    
    @Override
    protected boolean canEdit(Document document) {
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        // po amend docs in CGIP status are only editable when in Initiated or Saved status
        if (PurchaseOrderStatuses.CHANGE_IN_PROCESS.equals(poDocument.getStatusCode())) {
            WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
            if (!workflowDoc.isInitiated() && !workflowDoc.isSaved()) {
                return false;
            }
        }    
        return super.canEdit(document);
    }
    
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (PurchaseOrderStatuses.CHANGE_IN_PROCESS.equals(poDocument.getStatusCode())) {
            WorkflowDocument workflowDoc = document.getDocumentHeader().getWorkflowDocument();
            //  amendment doc needs to lock its field for initiator while enroute
            if (workflowDoc.isInitiated() || workflowDoc.isSaved()) {
                editModes.add(PurchaseOrderEditMode.AMENDMENT_ENTRY);
            }
        }
        if (PurchaseOrderStatuses.AWAIT_NEW_UNORDERED_ITEM_REVIEW.equals(poDocument.getStatusCode())) {
            editModes.add(PurchaseOrderEditMode.AMENDMENT_ENTRY);
        }
        
        if (SpringContext.getBean(PurapService.class).isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument) document, "New Unordered Items")) {
            editModes.add(PurchaseOrderEditMode.UNORDERED_ITEM_ACCOUNT_ENTRY);
        }
        
        return editModes;
    }

    @Override
    protected boolean canReload(Document document) {
        //  show the reload button if the doc is anything but processed or final
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return (workflowDocument.isSaved() || workflowDocument.isEnroute()) ;
    }

}
