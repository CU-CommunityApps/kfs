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

package org.kuali.module.purap.document;

import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapWorkflowConstants.NodeDetails;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * Purchase Order Amendment Document
 */
public class PurchaseOrderAmendmentDocument extends PurchaseOrderDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderAmendmentDocument.class);

    /**
	 * Default constructor.
	 */
	public PurchaseOrderAmendmentDocument() {
        super();
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        LOG.info("prepareForSave(KualiDocumentEvent) do not create gl entries");
        //TODO For now, do nothing because amendment should not perform the same save prep as PO
    }

    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        
        // DOCUMENT PROCESSED
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            //FIXME not creating gl entries yet...not quite working yet
            //getPurapGeneralLedgerService().generateEntriesApproveAmendPo(po);
            
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForApprovedPODocuments(this);

            //set purap status and status history and status history note
            SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(this, PurapConstants.PurchaseOrderStatuses.OPEN );

            SpringContext.getBean(PurchaseOrderService.class).saveDocumentWithoutValidation(this);
        }
        // DOCUMENT DISAPPROVED
        else if (getDocumentHeader().getWorkflowDocument().stateIsDisapproved()) {
            SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedPODocuments(this);
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentWithoutValidation(this);
        }
        // DOCUMENT CANCELED
        else if (getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
            // TODO delyea - is this the correct status.... does it affect counts/reporting?
            SpringContext.getBean(PurapService.class).updateStatusAndStatusHistory(this, PurapConstants.PurchaseOrderStatuses.CANCELLED);
            SpringContext.getBean(PurchaseOrderService.class).cancelAmendment(this);
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentWithoutValidation(this);
        }
    }

}
