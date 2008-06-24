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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;

/**
 * Document Authorizer for the PO Retransmit document.
 */
public class PurchaseOrderRetransmitDocumentAuthorizer extends PurchaseOrderDocumentAuthorizer {

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizerBase#hasInitiateAuthorization(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public boolean hasInitiateAuthorization(Document document, UniversalUser user) {
        PurchaseOrderDocument po = (PurchaseOrderDocument) document;
        if (po.getPurchaseOrderAutomaticIndicator()) {
            return true;
        }
        else {
            String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(PurchaseOrderDocument.class, PurapParameterConstants.Workgroups.PURAP_DOCUMENT_PO_INITIATE_ACTION);
            try {
                return SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(user);
            }
            catch (GroupNotFoundException e) {
                throw new RuntimeException("Workgroup " + authorizedWorkgroup + " not found", e);
            }
        }
    }

    /**
     * @see org.kuali.core.document.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            // do not allow this document to be saved; once initiated, it must be routed or canceled
            flags.setCanSave(false);
        }

        // NEED TO REDO ANNOTATE CHECK SINCE CHANGED THE VALUE OF FLAGS
        this.setAnnotateFlag(flags);

        return flags;
    }

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.UniversalUser, java.util.List, java.util.List)
     */
    @Override
    public Map getEditMode(Document d, UniversalUser u, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = new HashMap();
        String editMode = PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB;
        editModeMap.put(editMode, "TRUE");
        String viewOnlyEditMode = AuthorizationConstants.EditMode.VIEW_ONLY;
        editModeMap.put(viewOnlyEditMode, "TRUE");

        return editModeMap;
    }

}
