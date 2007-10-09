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
package org.kuali.module.purap.rules;

import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.PurapGeneralLedgerService;
import org.kuali.module.purap.service.PurchaseOrderService;

public class PurchaseOrderReopenDocumentRule extends PurchasingDocumentRuleBase {

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument porDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(porDocument);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument porDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(porDocument);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchaseOrderDocument porDocument = (PurchaseOrderDocument) approveEvent.getDocument();
        return isValid &= processValidation(porDocument);
    }

    private boolean processValidation(PurchaseOrderDocument document) {
        boolean valid = true;

        // Check that the PO is not null
        if (ObjectUtils.isNull(document)) {
            throw new ValidationException("Purchase Order Reopen document was null on validation.");
        }
        else {
            // TODO: Get this from Business Rules.
            // Check the PO status.
            PurchaseOrderDocument currentPO = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(document.getPurapDocumentIdentifier());
            if (!StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurapConstants.PurchaseOrderStatuses.CLOSED) &&
                !StringUtils.equalsIgnoreCase(currentPO.getStatusCode(), PurapConstants.PurchaseOrderStatuses.PENDING_REOPEN)) {
                valid = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.STATUS_CODE, PurapKeyConstants.ERROR_PURCHASE_ORDER_STATUS_INCORRECT, PurapConstants.PurchaseOrderStatuses.CLOSED);
            }

            // Check that the user is in purchasing workgroup.
            String initiatorNetworkId = document.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
            UniversalUserService uus = SpringContext.getBean(UniversalUserService.class);
            UniversalUser user = null;
            try {
                user = uus.getUniversalUserByAuthenticationUserId(initiatorNetworkId);
                String purchasingGroup = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.PURCHASING_DOCUMENT.class, PurapParameterConstants.Workgroups.WORKGROUP_PURCHASING);
                if (!uus.isMember(user, purchasingGroup)) {
                    valid = false;
                    GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURAP_DOC_ID, PurapKeyConstants.ERROR_USER_NONPURCHASING);
                }
            }
            catch (UserNotFoundException ue) {
                valid = false;
            }
        }
        return valid;
    }

    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(accountingDocument, accountingLine, explicitEntry);
        PurchaseOrderDocument po = (PurchaseOrderDocument) accountingDocument;
        SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(po, 
                accountingLine, explicitEntry, po.getPurapDocumentIdentifier(), GL_DEBIT_CODE, PurapDocTypeCodes.PO_DOCUMENT, true);

        explicitEntry.setFinancialDocumentTypeCode(PurapDocTypeCodes.PO_REOPEN_DOCUMENT);  //don't think i should have to override this, but default isn't getting the right PO doc
    }

}
