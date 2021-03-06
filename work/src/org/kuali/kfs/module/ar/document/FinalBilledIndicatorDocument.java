/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.FinalBilledIndicatorEntry;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.TransactionalDocumentBase;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class unfinalizes the invoices that have previously been finalized.
 */
public class FinalBilledIndicatorDocument extends TransactionalDocumentBase {

    public FinalBilledIndicatorDocument() {
    }

    private List<FinalBilledIndicatorEntry> invoiceEntries = new ArrayList<FinalBilledIndicatorEntry>();

    /**
     * Gets the invoiceEntries attribute.
     *
     * @return Returns the invoiceEntries.
     */
    public List<FinalBilledIndicatorEntry> getInvoiceEntries() {
        return invoiceEntries;
    }

    /**
     * Sets the invoiceEntries attribute value.
     *
     * @param invoiceEntries The invoiceEntries to set.
     */
    public void setInvoiceEntries(List<FinalBilledIndicatorEntry> invoiceEntries) {
        this.invoiceEntries = invoiceEntries;
    }

    /**
     * @throws WorkflowException
     */
    public void updateContractsGrantsInvoiceDocument() throws WorkflowException {
        for (FinalBilledIndicatorEntry entry : this.getInvoiceEntries()) {
            ContractsGrantsInvoiceDocument invoiceDocument;
            invoiceDocument = (ContractsGrantsInvoiceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(entry.getInvoiceDocumentNumber());
            if (ObjectUtils.isNotNull(invoiceDocument)) {
                invoiceDocument.getInvoiceGeneralDetail().setFinalBillIndicator(false);
                ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
                contractsGrantsInvoiceDocumentService.updateUnfinalizationToAwardAccount(invoiceDocument.getAccountDetails(),invoiceDocument.getProposalNumber());
                SpringContext.getBean(DocumentService.class).updateDocument(invoiceDocument);
            }
        }
    }

    /**
     * @param invoiceEntry
     */
    public void addInvoiceEntry(FinalBilledIndicatorEntry invoiceEntry) {
        invoiceEntry.setDocumentId(getDocumentNumber());
        invoiceEntries.add(invoiceEntry);
    }

    /**
     * @param deleteIndex
     */
    public void removeInvoiceEntry(int deleteIndex) {
        invoiceEntries.remove(deleteIndex);
    }
}
