/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCAmountsNotDrawnReport;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLetterOfCreditReviewDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsLetterOfCreditReviewDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * Defines a custom lookupable for LOC amounts not drawn report.
 */
public class ContractsGrantsLOCAmountsNotDrawnReportLookupableHelperServiceImpl extends ContractsGrantsLOCDrawDetailsReportLookupableHelperServiceImpl {
    private static final Log LOG = LogFactory.getLog(ContractsGrantsLOCDrawDetailsReportLookupableHelperServiceImpl.class);

    private DocumentService documentService;

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsLOCAmountsNotDrawnReport> displayList = new ArrayList<ContractsGrantsLOCAmountsNotDrawnReport>();
        Collection<ContractsGrantsLetterOfCreditReviewDocument> cgLOCReviewDocs = businessObjectService.findAll(ContractsGrantsLetterOfCreditReviewDocument.class);

        for (ContractsGrantsLetterOfCreditReviewDocument cgLOCReviewDoc : cgLOCReviewDocs) {
            ContractsGrantsLetterOfCreditReviewDocument cgLOCReviewDocWithHeader;
            // Docs that have a problem to get documentHeader won't be on the report
            try {
                cgLOCReviewDocWithHeader = (ContractsGrantsLetterOfCreditReviewDocument) documentService.getByDocumentHeaderId(cgLOCReviewDoc.getDocumentNumber());
            }
            catch (WorkflowException e) {
                LOG.debug("WorkflowException happened while retrives documentHeader");
                continue;
            }

            // If there is any problem to get workflowDocument, go to next doc
            WorkflowDocument workflowDocument = null;
            try {
                workflowDocument = cgLOCReviewDocWithHeader.getDocumentHeader().getWorkflowDocument();
            }
            catch (RuntimeException e) {
                LOG.debug(e + " happened" + " : transient workflowDocument is null");
                continue;
            }

            boolean isStatusFinal = false;
            // Check status of document
            if (ObjectUtils.isNotNull(workflowDocument)) {
                isStatusFinal = workflowDocument.isFinal();
            }

            // If status of ContractsGrantsLOCReviewDocument is final or processed, go to next doc
            if (!isStatusFinal) {
                continue;
            }

            List<ContractsGrantsLetterOfCreditReviewDetail> headerReviewDetails = cgLOCReviewDoc.getHeaderReviewDetails();
            List<ContractsGrantsLetterOfCreditReviewDetail> accountReviewDetails = cgLOCReviewDoc.getAccountReviewDetails();
            if (accountReviewDetails.size() > 0) {

                KualiDecimal totalAmountAvailableToDraw = KualiDecimal.ZERO;
                KualiDecimal totalClaimOnCashBalance = KualiDecimal.ZERO;
                KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
                KualiDecimal totalFundsNotDrawn = KualiDecimal.ZERO;

                ContractsGrantsLOCAmountsNotDrawnReport cgLOCAmountNotDrawnReport = new ContractsGrantsLOCAmountsNotDrawnReport();

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : accountReviewDetails) {
                    KualiDecimal claimOnCashBalance = ObjectUtils.isNull(accountReviewDetailEntry.getClaimOnCashBalance()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getClaimOnCashBalance();
                    // PreviousDraw should be sum of amountToDraw?
                    KualiDecimal previousDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountToDraw();
                    KualiDecimal fundsNotDrawn = ObjectUtils.isNull(accountReviewDetailEntry.getFundsNotDrawn()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getFundsNotDrawn();

                    totalClaimOnCashBalance = totalClaimOnCashBalance.add(claimOnCashBalance);
                    totalAmountToDraw = totalAmountToDraw.add(previousDraw);
                    totalFundsNotDrawn = totalFundsNotDrawn.add(fundsNotDrawn);
                }

                for (ContractsGrantsLetterOfCreditReviewDetail accountReviewDetailEntry : headerReviewDetails) {
                    KualiDecimal amountAvailableToDraw = ObjectUtils.isNull(accountReviewDetailEntry.getAmountAvailableToDraw()) ? KualiDecimal.ZERO : accountReviewDetailEntry.getAmountAvailableToDraw();
                    totalAmountAvailableToDraw = totalAmountAvailableToDraw.add(amountAvailableToDraw);
                }

                cgLOCAmountNotDrawnReport.setDocumentNumber(cgLOCReviewDoc.getDocumentNumber());
                cgLOCAmountNotDrawnReport.setLetterOfCreditFundCode(cgLOCReviewDoc.getLetterOfCreditFundCode());
                cgLOCAmountNotDrawnReport.setLetterOfCreditFundGroupCode(cgLOCReviewDoc.getLetterOfCreditFundGroupCode());

                if (ObjectUtils.isNotNull(workflowDocument) && ObjectUtils.isNotNull(workflowDocument.getDateCreated())) {
                    Timestamp ts = new Timestamp(workflowDocument.getDateCreated().toDate().getTime());
                    Date worflowDate = new Date(ts.getTime());
                    cgLOCAmountNotDrawnReport.setLetterOfCreditReviewCreateDate((worflowDate));
                }
                cgLOCAmountNotDrawnReport.setAmountAvailableToDraw(totalAmountAvailableToDraw);
                cgLOCAmountNotDrawnReport.setClaimOnCashBalance(totalClaimOnCashBalance);
                cgLOCAmountNotDrawnReport.setAmountToDraw(totalAmountToDraw);
                cgLOCAmountNotDrawnReport.setFundsNotDrawn(totalFundsNotDrawn);

                // Only adding entries with funds not drawn amount greater than zero
                if (totalFundsNotDrawn.isGreaterThan(KualiDecimal.ZERO)) {
                    if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgLOCAmountNotDrawnReport, "ContractsGrantsLOCAmountsNotDrawnReport")) {
                        displayList.add(cgLOCAmountNotDrawnReport);
                    }
                }
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    /**
     * @return the documentService
     */
    @Override
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * @param documentService the documentService to set
     */
    @Override
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
