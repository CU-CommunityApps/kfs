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
package org.kuali.module.purap.document.authorization;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.PurapWorkflowConstants.PurchaseOrderDocument.NodeDetailEnum;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurApWorkflowIntegrationService;

/**
 * This class determines permissions for a user to view the
 * buttons on Purchase Order Document.
 * 
 */
public class PurchaseOrderDocumentActionAuthorizer {

    private String docStatus;
    private String documentType;
    private boolean currentIndicator;
    private boolean pendingActionIndicator;
    private boolean purchaseOrderAutomatedIndicator;
    private boolean isUserAuthorized;
    private boolean hasPaymentRequest;
    private List<PaymentRequestDocument> pReqs;
    
    private Date lastTransmitDate;

    private boolean apUser;
    private boolean apSupervisor;
    private boolean fiscalOfficerDelegateUser;
    private boolean approver;

    private PurchaseOrderDocument purchaseOrder;
    private Map editMode;
    
    /**
     * Constructs a PurchaseOrderDocumentActionAuthorizer.
     * 
     * @param po A PurchaseOrderDocument
     */
    public PurchaseOrderDocumentActionAuthorizer(PurchaseOrderDocument po, Map editingMode) {

        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        this.purchaseOrder = po;
        this.editMode = editingMode;
        this.pReqs = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestsByPurchaseOrderId(purchaseOrder.getPurapDocumentIdentifier());
        this.hasPaymentRequest = ((pReqs != null && pReqs.size() > 0) ? true : false);
        // doc indicators
        this.docStatus = po.getStatusCode();
        this.documentType = po.getDocumentHeader().getWorkflowDocument().getDocumentType();
        this.currentIndicator = po.isPurchaseOrderCurrentIndicator();
        this.pendingActionIndicator = po.isPendingActionIndicator();
        this.purchaseOrderAutomatedIndicator = po.getPurchaseOrderAutomaticIndicator();

        String authorizedWorkgroup = SpringContext.getBean(ParameterService.class).getParameterValue(PurchaseOrderDocument.class, PurapParameterConstants.Workgroups.PURAP_DOCUMENT_PO_ACTIONS);

        try {
            this.isUserAuthorized = SpringContext.getBean(KualiGroupService.class).getByGroupName(authorizedWorkgroup).hasMember(user);
        }
        catch (GroupNotFoundException gnfe) {
            this.isUserAuthorized = false;
        }
        
        this.lastTransmitDate = po.getPurchaseOrderLastTransmitDate();
        
    }

    private String getDocStatus() {
        return docStatus;
    }

    private boolean isCurrentIndicator() {
        return currentIndicator;
    }

    private boolean isPendingActionIndicator() {
        return pendingActionIndicator;
    }

    /**
     * Determines whether to display the retransmit button. 
     * The last transmit date must not be null. The purchase order must be current,
     * must not be pending and the purchase order status must be open. If the 
     * purchase order is an Automated Purchase Order (APO) then any users can see
     * the retransmit button, otherwise, only users in the purchasing group can see it.
     * 
     * @return boolean true if the retransmit button can be displayed.
     */
    public boolean canRetransmit() {
        if (purchaseOrder.getPurchaseOrderLastTransmitDate() != null && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && (isUserAuthorized || purchaseOrder.getPurchaseOrderAutomaticIndicator())) {
            return true;
        }
        return false;
    }

    /**
     * Determines whether to display the button to print the pdf on a retransmit document. We're currently 
     * sharing the same button image as the button for creating a retransmit document but this may change someday. 
     * This button should only appear on Retransmit Document. If it is an Automated Purchase Order (APO) then
     * any users can see this button, otherwise, only users in the purchasing group can see it.
     * 
     * @return boolean true if the print retransmit button can be displayed.
     */
    public boolean canPrintRetransmit() {
        if ((isUserAuthorized || purchaseOrder.getPurchaseOrderAutomaticIndicator()) && editMode.containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB) && (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)) && purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CHANGE_IN_PROCESS)) {
          return true;
        }
        return false;
    }
    
    /**
     * Determines whether to display the button to print the pdf for the first time transmit.
     * 
     * @return boolean true if the print button can be displayed.
     */
    public boolean canFirstTransmitPrintPo() {
        boolean isDocumentTransmissionActionRequested = SpringContext.getBean(PurApWorkflowIntegrationService.class).isActionRequestedOfUserAtNodeName(purchaseOrder.getDocumentNumber(), NodeDetailEnum.DOCUMENT_TRANSMISSION.getName(), GlobalVariables.getUserSession().getUniversalUser());
        // If the status is Pending Print and the user is either authorized
        // or an action is requested of them for the document transmission route node, return true to show the print po button.
        if (PurapConstants.PurchaseOrderStatuses.PENDING_PRINT.equals(purchaseOrder.getStatusCode()) && (isUserAuthorized || isDocumentTransmissionActionRequested)) {
            return true;
        }
        //This is so that the user can still do the print po if the transmission method is changed to PRINT during amendment, so that
        //we can fill in the last transmit date to some dates.
        if (purchaseOrder.getPurchaseOrderTransmissionMethodCode() != null && purchaseOrder.getPurchaseOrderTransmissionMethodCode().equals(PurapConstants.POTransmissionMethods.PRINT) && purchaseOrder.getPurchaseOrderLastTransmitDate() == null && purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && purchaseOrder.getDocumentHeader().getWorkflowDocument().stateIsFinal()) {
            return true;
        }
        return false;
    }
    
    /**
     * Determines whether to display the open order button to reopen the purchase order document.
     * If the document status is close, the purchase order is current and not pending and the
     * user is in purchasing group, then we can display this button.
     * 
     * @return boolean true if the open order button can be displayed.
     */
    public boolean canReopen() {
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CLOSED) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            return true;
        }
        return false;
    }
    
    /**
     * Determines whether to display the close order button to close the purchase order document.
     * To display the close PO button, the PO must be in Open status, the PO must have at least 1 Payment Request in any 
     * other statuses than "In Process" status, and the PO cannot have any Payment Requests in "In Process" status. This 
     * button is available to all faculty/staff.
     * 
     * @return boolean true if the close order button can be displayed.
     */
    public boolean canClose() {
        boolean validForDisplayingCloseButton = false;
        
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator()) {

            validForDisplayingCloseButton = true;

            if (ObjectUtils.isNotNull(pReqs)) {
                if (pReqs.size() == 0) {
                    validForDisplayingCloseButton = false;
                }
                else {
                    // None of the PREQs against this PO may be in 'In Process' status.
                    for (PaymentRequestDocument pReq : pReqs) {
                        if (StringUtils.equalsIgnoreCase(pReq.getStatusCode(), PaymentRequestStatuses.IN_PROCESS)) {
                            validForDisplayingCloseButton = false;
                        }
                    }
                }
            }
        }
        return validForDisplayingCloseButton;
    }
    
    /**
     * Determines whether to display the amend and payment hold buttons for the purchase order document.
     * The document status must be open, the purchase order must be current and not pending and the
     * user must be in purchasing group.
     * 
     * @return boolean true if the amend and payment hold buttons can be displayed.
     */
    public boolean canAmendAndHoldPayment() {
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            return true;
        }
        return false;
    }
    
    /**
     * Determines whether to display the void button for the purchase order document.
     * Conditions for displaying Void button (in addition to the purchaseOrder current indicator 
     * is true and pending indicator is false and the user is member of kuali purap purchasing):
     * 1. If the PO is in Pending Print status, or
     * 2. If the PO is in Open status and has no PREQs against it.
     * 
     * @return boolean true if the void button can be displayed.
     */
    public boolean canVoid() {
        if (purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.PENDING_PRINT) || (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.OPEN) && !hasPaymentRequest)) {
                return true;
            }
        }    
        return false;
    }
    
    /**
     * Determines whether to display the remove hold button for the purchase order document.
     * The document status must be payment hold, the purchase order must be current and not
     * pending and the user must be in purchasing group.
     * 
     * @return
     */
    public boolean canRemoveHold() {
        if (purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD) && purchaseOrder.isPurchaseOrderCurrentIndicator() && !purchaseOrder.isPendingActionIndicator() && isUserAuthorized) {
            return true;
        }
        return false;
    }
    
    private boolean isApUser() {
        return apUser;
    }

    public boolean isApSupervisor() {
        return apSupervisor;
    }

    public void setApSupervisor(boolean apSupervisor) {
        this.apSupervisor = apSupervisor;
    }

    private boolean isFiscalOfficerDelegateUser() {
        return fiscalOfficerDelegateUser;
    }

    private boolean isApprover() {
        return approver;
    }
}
