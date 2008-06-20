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
package org.kuali.module.financial.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.exceptions.DocumentInitiationAuthorizationException;
import org.kuali.core.exceptions.DocumentTypeAuthorizationException;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSConstants.CashDrawerConstants;
import org.kuali.kfs.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.authorization.KfsAuthorizationConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashManagementService;
import org.kuali.module.financial.service.CashReceiptService;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.clientapp.vo.ValidActionsVO;

/**
 * DocumentAuthorizer containing authorization code for CashManagement documents
 */
public class CashManagementDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(CashManagementDocumentAuthorizer.class);

    /**
     * Overrides to implemented some document specific views.
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, UniversalUser user) {
        // default is UNVIEWABLE for this doctype
        Map editModeMap = new HashMap();
        editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.UNVIEWABLE, "TRUE");

        // update editMode if possible
        try {
            CashManagementDocument cmDoc = (CashManagementDocument) document;

            if (SpringContext.getBean(KualiGroupService.class).getByGroupName(cmDoc.getWorkgroupName()).hasMember(user)) {
                editModeMap.clear();

                KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

                if (workflowDocument.stateIsInitiated()) {
                    editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.FULL_ENTRY, "TRUE");
                }
                else if (workflowDocument.stateIsSaved()) {
                    editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.FULL_ENTRY, "TRUE");

                    editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_CANCEL_DEPOSITS, "TRUE");
                    if (!cmDoc.hasFinalDeposit()) {
                        editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.ALLOW_ADDITIONAL_DEPOSITS, "TRUE");
                    }
                }
                else {
                    editModeMap.put(KfsAuthorizationConstants.CashManagementEditMode.VIEW_ONLY, "TRUE");
                }
            }
        }
        catch (GroupNotFoundException e) {
            // leave editModeMap UNVIEWABLE
        }

        return editModeMap;
    }

    /**
     * @see org.kuali.core.document.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        CashManagementDocument cmDoc = (CashManagementDocument) document;
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        ValidActionsVO validActions = workflowDocument.getRouteHeader().getValidActions();

        if (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {
            // CM document can only be saved (via the save button) if the CashDrawer is not closed
            if (cmDoc.getCashDrawerStatus() == null || cmDoc.getCashDrawerStatus().equals(CashDrawerConstants.STATUS_CLOSED)) {
                flags.setCanSave(false);
            }
            else {
                flags.setCanSave(validActions.contains(EdenConstants.ACTION_TAKEN_SAVED_CD));
            }

            // CM document can only be routed if it contains a Final Deposit
            if (!cmDoc.hasFinalDeposit() || !SpringContext.getBean(CashManagementService.class).allVerifiedCashReceiptsAreDeposited(cmDoc)) {
                flags.setCanRoute(false);
                flags.setCanBlanketApprove(false);
            }
            else {
                flags.setCanRoute(validActions.contains(EdenConstants.ACTION_TAKEN_ROUTED_CD));
                flags.setCanBlanketApprove(validActions.contains(EdenConstants.ACTION_TAKEN_BLANKET_APPROVE_CD));
            }

            if (!SpringContext.getBean(CashManagementService.class).allowDocumentCancellation(cmDoc)) {
                flags.setCanCancel(false);
            }
            else {
                flags.setCanCancel(validActions.contains(EdenConstants.ACTION_TAKEN_CANCELED_CD));
            }
        }

        if (workflowDocument.stateIsEnroute()) {
            flags.setCanApprove(validActions.contains(EdenConstants.ACTION_TAKEN_APPROVED_CD));
            flags.setCanDisapprove(validActions.contains(EdenConstants.ACTION_TAKEN_DENIED_CD));
            flags.setCanFYI(validActions.contains(EdenConstants.ACTION_TAKEN_FYI_CD));
        }

        return flags;
    }

    /**
     * This method checks that all verified cash receipts are deposited
     * 
     * @param cmDoc the cash management document to check
     * @return true if all verified cash receipts are deposited, false if otherwise
     */
    private boolean areAllVerifiedCashReceiptsDeposited(CashManagementDocument cmDoc) {
        boolean theyAre = true;
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getWorkgroupName(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        CashManagementService cms = SpringContext.getBean(CashManagementService.class);
        for (Object o : verifiedReceipts) {
            if (!cms.verifyCashReceiptIsDeposited(cmDoc, (CashReceiptDocument) o)) {
                theyAre = false;
                break;
            }
        }
        return theyAre;
    }

    /**
     * @see org.kuali.core.document.DocumentAuthorizerBase#canInitiate(java.lang.String, org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public void canInitiate(String documentTypeName, UniversalUser user) throws DocumentTypeAuthorizationException {
        try {
            super.canInitiate(documentTypeName, user);
            // to initiate, you have to be a member of the bursar's group for your campus
            String unitName = SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForUser(user);
            if (!user.isMember(unitName)) {
                throw new DocumentTypeAuthorizationException(user.getPersonUserIdentifier(), "initiate", documentTypeName);
            }
        }
        catch (DocumentInitiationAuthorizationException e) {
            throw new DocumentTypeAuthorizationException(user.getPersonUserIdentifier(), "add deposits to", documentTypeName);
        }
    }
}