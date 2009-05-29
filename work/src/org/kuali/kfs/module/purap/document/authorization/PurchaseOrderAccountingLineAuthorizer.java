/*
 * Copyright 2009 The Kuali Foundation.
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

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Accounting line authorizer for Requisition document which allows adding accounting lines at specified nodes
 */
public class PurchaseOrderAccountingLineAuthorizer extends PurapAccountingLineAuthorizer {
    private static final String NEW_UNORDERED_ITEMS_NODE = "NewUnorderedItems";

    /**
     * Allow new lines to be rendered at NewUnorderedItems node
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#renderNewLine(org.kuali.kfs.sys.document.AccountingDocument, java.lang.String)
     */
    @Override
    public boolean renderNewLine(AccountingDocument accountingDocument, String accountingGroupProperty) {
        if (accountingDocument.getDocumentHeader().getWorkflowDocument().getCurrentRouteNodeNames().equals(PurchaseOrderAccountingLineAuthorizer.NEW_UNORDERED_ITEMS_NODE)) return true;
        return super.renderNewLine(accountingDocument, accountingGroupProperty);
    }
    
    @Override
    protected boolean allowAccountingLinesAreEditable(AccountingDocument accountingDocument,
            AccountingLine accountingLine){
        PurApAccountingLine purapAccount = (PurApAccountingLine)accountingLine;
        PurchaseOrderItem poItem = (PurchaseOrderItem)purapAccount.getPurapItem();
        PurchaseOrderDocument po = (PurchaseOrderDocument)accountingDocument;
        
        
        if (poItem != null && !poItem.getItemType().isAdditionalChargeIndicator()) {
            if (!poItem.isItemActiveIndicator()) {
                return false;
            }

            if (poItem.getItemInvoicedTotalAmount() != null && poItem.getItemInvoicedTotalAmount().compareTo(new KualiDecimal(0)) != 0) {
                return false;
            }
            
            if (po.getContainsUnpaidPaymentRequestsOrCreditMemos() && !poItem.isNewItemForAmendment()) {
                return false;
            }
            
        }
        return super.allowAccountingLinesAreEditable(accountingDocument, accountingLine);
    }

    @Override
    public boolean determineEditPermissionOnLine(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty) {
     // the fields in a new line should be always editable
        if (accountingLine.getSequenceNumber() == null) {
            return true;
        }
        
        // check the initiation permission on the document if it is in the state of preroute, but only if
        // the PO status is not In Process.
        KualiWorkflowDocument workflowDocument = accountingDocument.getDocumentHeader().getWorkflowDocument();
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)accountingDocument;
        if (!poDocument.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.IN_PROCESS) && (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved())) {
            boolean hasEditLinePermission = workflowDocument.userIsInitiator(GlobalVariables.getUserSession().getPerson());
            return hasEditLinePermission;
        }
        else {
            return true;
        }
    }
    
}
