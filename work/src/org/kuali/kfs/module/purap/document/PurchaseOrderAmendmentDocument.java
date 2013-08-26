/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.module.purap.document;

import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.rice.core.api.util.type.KualiDecimal.ZERO;
import static org.kuali.rice.core.api.util.type.KualiDecimal.ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.service.PurapGeneralLedgerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
/**
 * Purchase Order Amendment Document
 */
public class PurchaseOrderAmendmentDocument extends PurchaseOrderDocument {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderAmendmentDocument.class);

    boolean newUnorderedItem; //Used for routing
    String receivingDeliveryCampusCode; //Used for routing

    /**
     * When Purchase Order Amendment document has been Processed through Workflow, the general ledger entries are created and the PO
     * status remains "OPEN".
     *
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderDocument#doRouteStatusChange()
     */
   @Override
	public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        try {
            // DOCUMENT PROCESSED
            if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isProcessed()) {
                // generate GL entries
                SpringContext.getBean(PurapGeneralLedgerService.class).generateEntriesApproveAmendPurchaseOrder(this);

            // if gl entries created(means there is amount change) for amend purchase order send an FYI to all fiscal officers
            if ((getGlOnlySourceAccountingLines() != null && !getGlOnlySourceAccountingLines().isEmpty())) {
                SpringContext.getBean(PurchaseOrderService.class).sendFyiForGLEntries(this);
            }

                    // update indicators
                    SpringContext.getBean(PurchaseOrderService.class).completePurchaseOrderAmendment(this);

                // update vendor commodity code by automatically spawning vendor maintenance document
                SpringContext.getBean(PurchaseOrderService.class).updateVendorCommodityCode(this);

                // for app doc status
                updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_OPEN);
            }
            // DOCUMENT DISAPPROVED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isDisapproved()) {
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForDisapprovedChangePODocuments(this);
                SpringContext.getBean(PurapService.class).saveDocumentNoValidation(this);

                // for app doc status
                try {
                    String nodeName = SpringContext.getBean(WorkflowDocumentService.class).getCurrentRouteLevelName(this.getFinancialSystemDocumentHeader().getWorkflowDocument());
                    String reqStatus = PurapConstants.PurchaseOrderStatuses.getPurchaseOrderAppDocDisapproveStatuses().get(nodeName);
                    updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.getPurchaseOrderAppDocDisapproveStatuses().get(reqStatus));

                } catch (WorkflowException e) {
                    logAndThrowRuntimeException("Error saving routing data while saving App Doc Status " + getDocumentNumber(), e);
                }
            }
            // DOCUMENT CANCELED
            else if (this.getFinancialSystemDocumentHeader().getWorkflowDocument().isCanceled()) {
                SpringContext.getBean(PurchaseOrderService.class).setCurrentAndPendingIndicatorsForCancelledChangePODocuments(this);

                // for app doc status
                updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_CANCELLED);
            }
        }
        catch (WorkflowException e) {
            logAndThrowRuntimeException("Error saving routing data while saving document with id " + getDocumentNumber(), e);
        }
   }

   /**
    * @see org.kuali.module.purap.rules.PurapAccountingDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.kfs.sys.document.AccountingDocument,
    *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry)
    */
   @Override
   public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
       super.customizeExplicitGeneralLedgerPendingEntry(postable, explicitEntry);

       SpringContext.getBean(PurapGeneralLedgerService.class).customizeGeneralLedgerPendingEntry(this, (AccountingLine)postable, explicitEntry, getPurapDocumentIdentifier(), GL_DEBIT_CODE, PurapDocTypeCodes.PO_DOCUMENT, true);

       // don't think i should have to override this, but default isn't getting the right PO doc
       explicitEntry.setFinancialDocumentTypeCode(PurapDocTypeCodes.PO_AMENDMENT_DOCUMENT);
       explicitEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
   }

   @Override
   public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
       List<GeneralLedgerPendingEntrySourceDetail> accountingLines = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();

       if (getGlOnlySourceAccountingLines() != null) {
           Iterator iter = getGlOnlySourceAccountingLines().iterator();
           while (iter.hasNext()) {
               accountingLines.add((GeneralLedgerPendingEntrySourceDetail) iter.next());
           }
       }
       return accountingLines;
   }


   @Override
   public void populateDocumentForRouting() {
       newUnorderedItem = SpringContext.getBean(PurchaseOrderService.class).hasNewUnorderedItem(this);
       receivingDeliveryCampusCode = SpringContext.getBean(ReceivingService.class).getReceivingDeliveryCampusCode(this);
       super.populateDocumentForRouting();
   }

    public boolean isNewUnorderedItem() {
        return newUnorderedItem;
    }

    public void setNewUnorderedItem(boolean newUnorderedItem) {
        this.newUnorderedItem = newUnorderedItem;
    }

    public String getReceivingDeliveryCampusCode() {
        return receivingDeliveryCampusCode;
    }

    public void setReceivingDeliveryCampusCode(String receivingDeliveryCampusCode) {
        this.receivingDeliveryCampusCode = receivingDeliveryCampusCode;
    }

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.HAS_NEW_UNORDERED_ITEMS)) return isNewUnorderedItem();
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \""+nodeName+"\"");
    }

    @Override
    public Class<? extends AccountingDocument> getDocumentClassForAccountingLineValueAllowedValidation() {
        return PurchaseOrderDocument.class;
    }


    @Override
    public void customPrepareForSave(KualiDocumentEvent event) {
        super.customPrepareForSave(event);

        // Set outstanding encumbered quantity/amount on items
        List<PurchaseOrderItem> poItems = (List<PurchaseOrderItem>) this.getItems();
        for (PurchaseOrderItem item : poItems) {
            if (item.isItemActiveIndicator()) {
                KualiDecimal invoicedTotalQuantity = item.getItemInvoicedTotalQuantity();
                if (item.getItemInvoicedTotalQuantity() == null) {
                    invoicedTotalQuantity = ZERO;
                    item.setItemInvoicedTotalQuantity(ZERO);
                }

                if (item.getItemInvoicedTotalAmount() == null) {
                    item.setItemInvoicedTotalAmount(ZERO);
                }
                if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                    KualiDecimal itemQuantity = item.getItemQuantity() == null ? ZERO : item.getItemQuantity();
                    KualiDecimal outstandingEncumberedQuantity = itemQuantity.subtract(invoicedTotalQuantity);
                    item.setItemOutstandingEncumberedQuantity(outstandingEncumberedQuantity);
                }
                KualiDecimal outstandingEncumbrance = null;

                // If the item is amount based (not quantity based)
                if (item.getItemType().isAmountBasedGeneralLedgerIndicator()) {
                    KualiDecimal totalAmount = item.getTotalAmount() == null ? ZERO : item.getTotalAmount();
                    outstandingEncumbrance = totalAmount.subtract(item.getItemInvoicedTotalAmount());
                    item.setItemOutstandingEncumberedAmount(outstandingEncumbrance);
                }
                else {
                    // if the item is quantity based
                    BigDecimal itemUnitPrice = ObjectUtils.isNull(item.getItemUnitPrice()) ? BigDecimal.ZERO : item.getItemUnitPrice();
                    outstandingEncumbrance = new KualiDecimal(item.getItemOutstandingEncumberedQuantity().bigDecimalValue().multiply(itemUnitPrice));
                    item.setItemOutstandingEncumberedAmount(outstandingEncumbrance);
                }

                List accountLines = (List) item.getSourceAccountingLines();
                Collections.sort(accountLines);

                updateEncumbranceOnAccountingLines(outstandingEncumbrance, accountLines);
            }
        }

        List<SourceAccountingLine> sourceLines = SpringContext.getBean(PurapAccountingService.class).generateSummaryWithNoZeroTotals(this.getItems());
        this.setSourceAccountingLines(sourceLines);
    }

    protected void updateEncumbranceOnAccountingLines(KualiDecimal outstandingEcumbrance, List accountLines) {
        for (Object accountLineObject : accountLines) {
            PurchaseOrderAccount accountLine = (PurchaseOrderAccount) accountLineObject;
            if (!accountLine.isEmpty()) {
                BigDecimal linePercent = accountLine.getAccountLinePercent();
                KualiDecimal accountOutstandingEncumbrance = new KualiDecimal(outstandingEcumbrance.bigDecimalValue().multiply(linePercent).divide(KFSConstants.ONE_HUNDRED.bigDecimalValue()));
                accountLine.setItemAccountOutstandingEncumbranceAmount(accountOutstandingEncumbrance);
            }
        }
    }

    @Override
    protected boolean shouldAdhocFyi() {
        Collection<String> excludeList = new ArrayList<String>();
        if (SpringContext.getBean(ParameterService.class).parameterExists(PurchaseOrderDocument.class, PurapParameterConstants.PO_NOTIFY_EXCLUSIONS)) {
            excludeList = SpringContext.getBean(ParameterService.class).getParameterValuesAsString(PurchaseOrderDocument.class, PurapParameterConstants.PO_NOTIFY_EXCLUSIONS);
        }

        if (getDocumentHeader().getWorkflowDocument().isDisapproved() ) {
            return true;
        }
        if (getDocumentHeader().getWorkflowDocument().isFinal() && !excludeList.contains(getRequisitionSourceCode()) ) {
            return true;
        }
        return false;
    }




}
