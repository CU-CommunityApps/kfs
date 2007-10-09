/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.purap.document;

import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * Purchase Order Document
 */
public class PurchaseOrderPaymentHoldDocument extends PurchaseOrderDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderPaymentHoldDocument.class);

    /**
     * Default constructor.
     */
    public PurchaseOrderPaymentHoldDocument() {
        super();
    }

    public void customPrepareForSave(KualiDocumentEvent event) {
        //do not set the accounts in sourceAccountingLines; this document should not create GL entries
    }


    
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        // DOCUMENT PROCESSED
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForApprovedPODocuments(this);
            //set purap status and status history and status history note
            //TODO: Once we have a note available here, add the note to the next line.
            SpringContext.getBean(PurapService.class).updateStatus(this, PurapConstants.PurchaseOrderStatuses.PAYMENT_HOLD );
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(this);
        }
        // DOCUMENT DISAPPROVED
        else if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedPODocuments(this);
        }
        // DOCUMENT CANCELED
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledPODocuments(this);
        }        
    }

}
