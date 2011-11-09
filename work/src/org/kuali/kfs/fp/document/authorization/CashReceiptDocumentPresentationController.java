/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.fp.document.authorization;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.LedgerPostingDocumentPresentationControllerBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.core.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.kew.api.WorkflowDocument;

public class CashReceiptDocumentPresentationController extends LedgerPostingDocumentPresentationControllerBase {
    private static final String CASH_MANAGEMENT_NODE_NAME = "CashManagement";

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean canApprove(Document document) {
        return this.canApproveOrBlanketApprove(document) ? super.canApprove(document) : false;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canBlanketApprove(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean canBlanketApprove(Document document) {
        return this.canApproveOrBlanketApprove(document) ? super.canBlanketApprove(document) : false;
    }

    protected boolean canApproveOrBlanketApprove(Document document) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isApprovalRequested() && !workflowDocument.isAdHocRequested()) {
            CashReceiptDocument cashReceiptDocument = (CashReceiptDocument) document;

            String campusCode = cashReceiptDocument.getCampusLocationCode();
            CashDrawer cashDrawer = SpringContext.getBean(CashDrawerService.class).getByCampusCode(campusCode);
            if (cashDrawer == null) {
                GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.CashReceipt.ERROR_CASH_DRAWER_DOES_NOT_EXIST, campusCode);
                return false;
            }
            if (cashDrawer.isClosed()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Prevents editing of the document at the CashManagement node
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#canEdit(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean canEdit(Document document) {
        if (document.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().contains(CashReceiptDocumentPresentationController.CASH_MANAGEMENT_NODE_NAME)) return false;
        return super.canEdit(document);
    }
    
    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        addFullEntryEntryMode(document, editModes);
        addChangeRequestMode(document, editModes);
        
        return editModes;
    }
    
    protected void addFullEntryEntryMode(Document document, Set<String> editModes) {
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isEnroute()) {
            List<String> currentRouteLevels = getCurrentRouteLevels(workflowDocument);
            if(currentRouteLevels.contains("CashManagement")) {
                editModes.add(KfsAuthorizationConstants.CashReceiptEditMode.CASH_MANAGER_CONFIRM_MODE);
            }
        }
    }
    
    protected void addChangeRequestMode(Document document, Set<String> editModes) {
        boolean IndValue = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(CashReceiptDocument.class, "CHANGE_REQUEST_ENABLED_IND");
        if(IndValue) {
            editModes.add(KfsAuthorizationConstants.CashReceiptEditMode.CHANGE_REQUEST_MODE);
        }
    }
    
    protected List<String> getCurrentRouteLevels(WorkflowDocument workflowDocument) {
        try {
            return Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
    }
    
}
