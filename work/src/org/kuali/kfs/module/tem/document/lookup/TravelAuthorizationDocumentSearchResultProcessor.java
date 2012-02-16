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
package org.kuali.kfs.module.tem.document.lookup;

import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_IDENTIFIER_PROPERTY;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationStatusCodeKeys;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;

/**
 * Produces custom search results for {@link TravelAuthorizationDocument}
 * 
 * @author Leo Przybylski (leo [at] rsmart.com)
 */
public class TravelAuthorizationDocumentSearchResultProcessor extends AbstractDocumentSearchResultProcessor implements TravelDocumentSearchResultsProcessor {

    /**
     * Determines if the url column for actions should be rendered. This is done for {@link TravelAuthorizationDocument} instances
     * in the search results that have a workflow document status of FINAL or PROCESSED and on documents that do not have a workflow
     * App Doc Status of REIMB_HELD, CANCELLED, PEND_AMENDMENT, CLOSED, or RETIRED_VERSION.
     * 
     * @param docCriteriaDTO has the workflow document status and app doc status to determine if rendering of the link is necessary
     * @return true if the document should have a reimbursement link
     */
    private boolean showReimbursementURL(DocSearchDTO docCriteriaDTO) {
        // kualitem-401 ... check status of document and don't create if the status is not final or processed
        // KUALITEM check if document is on hold or cancelled. If on hold, no link.

        final String documentStatus = docCriteriaDTO.getDocRouteStatusCode();
        return (documentStatus.equals(KEWConstants.ROUTE_HEADER_FINAL_CD)
                || (documentStatus.equals(KEWConstants.ROUTE_HEADER_PROCESSED_CD)))
                && (!docCriteriaDTO.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.REIMB_HELD))
                && (!docCriteriaDTO.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.CANCELLED))
                && (!docCriteriaDTO.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.PEND_AMENDMENT))
                && (!docCriteriaDTO.getAppDocStatus().equals(TravelAuthorizationStatusCodeKeys.RETIRED_VERSION));
    }

    private boolean otherPaymentMethodsAllowed(DocSearchDTO docCriteriaDTO) {
        boolean enablePayments = getParameterService().getIndicatorParameter(TemConstants.PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE, TemConstants.TravelAuthorizationParameters.ENABLE_VENDOR_PAYMENT_BEFORE_TA_FINAL_APPROVAL_IND);
        if (enablePayments) {
            return true;
        }
        else {
            String date = docCriteriaDTO.getSearchableAttribute("tripBegin").getUserDisplayValue();
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            try {
                Date tripBegin = dateTimeService.convertToDate(date);
                Date now = dateTimeService.getCurrentDate();

                if (now != null && tripBegin != null) {
                    if (now.before(tripBegin)) {
                        return true;
                    }
                }
            }
            catch (ParseException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
            }
        }
        return false;
    }

    private String createPaymentsURL(DocSearchDTO docCriteriaDTO, String tripID) {
        String links = createDisbursementVoucherLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.PRE_TRIP_VENDOR_PAYMENT);
        links += "<br />" + createRequisitionLink(docCriteriaDTO, TemConstants.TravelCustomSearchLinks.REQUISITION);
        String agencyLinks = createAgencySitesLinks(tripID);
        links += (StringUtils.isEmpty(agencyLinks) ? "" : "<br />") + agencyLinks;
        return links;
    }

    @Override
    public DocumentSearchResult addActionsColumn(DocSearchDTO docCriteriaDTO, DocumentSearchResult result) {
        final String columnName = TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;
        String tripID = docCriteriaDTO.getSearchableAttribute(TRVL_IDENTIFIER_PROPERTY).getUserDisplayValue();
        String actionsHTML = "";
        if (showReimbursementURL(docCriteriaDTO)) {
            actionsHTML += createTravelReimbursementLink(tripID);
        }
        if (otherPaymentMethodsAllowed(docCriteriaDTO)) {
            if (!StringUtils.isBlank(actionsHTML)) {
                actionsHTML += "<br />";
            }
            actionsHTML += createPaymentsURL(docCriteriaDTO, tripID);
        }

        final KeyValueSort actions = new KeyValueSort(columnName, "", actionsHTML, actionsHTML, null);
        result.getResultContainers().add(0, actions);

        return result;
    }

    @Override
    public boolean filterSearchResult(DocSearchDTO docCriteriaDTO) {
        return filterByUser(docCriteriaDTO);
    }

    private ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    protected DocumentTypeService getDocumentTypeService() {
        return SpringContext.getBean(DocumentTypeService.class);
    }
}
