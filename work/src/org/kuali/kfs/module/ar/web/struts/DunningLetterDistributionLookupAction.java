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
package org.kuali.kfs.module.ar.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.web.ui.DunningLetterDistributionResultRow;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction;
import org.kuali.rice.kns.web.struts.form.MultipleValueLookupForm;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Action class for Dunning Letter Distribution Lookup.
 */
public class DunningLetterDistributionLookupAction extends KualiMultipleValueLookupAction {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterDistributionLookupAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiMultipleValueLookupAction#search(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.search(mapping, form, request, response);
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;

        this.selectAll(multipleValueLookupForm, getMaxRowsPerPage(multipleValueLookupForm));

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * This method performs the operations necessary for a multiple value lookup to select all of the results and rerender the page
     *
     * @param multipleValueLookupForm
     * @param maxRowsPerPage
     * @return a list of result rows, used by the UI to render the page
     */
    @Override
    protected List<ResultRow> selectAll(MultipleValueLookupForm multipleValueLookupForm, int maxRowsPerPage) {
        String lookupResultsSequenceNumber = multipleValueLookupForm.getLookupResultsSequenceNumber();

        List<ResultRow> resultTable = null;
        try {
            LookupResultsService lookupResultsService = SpringContext.getBean(LookupResultsService.class);
            resultTable = lookupResultsService.retrieveResultsTable(lookupResultsSequenceNumber, GlobalVariables.getUserSession().getPerson().getPrincipalId());
        }
        catch (Exception e) {
            LOG.error("error occured trying to export multiple lookup results", e);
            throw new RuntimeException("error occured trying to export multiple lookup results");
        }

        Map<String, String> selectedObjectIds = new HashMap<String, String>();

        for (ResultRow row : resultTable) {

            // actual object ids are on sub result rows, not on parent rows
            if (row instanceof DunningLetterDistributionResultRow) {
                for (ResultRow subResultRow : ((DunningLetterDistributionResultRow) row).getSubResultRows()) {
                    String objId = subResultRow.getObjectId();
                    selectedObjectIds.put(objId, objId);
                }
            }
            else {
                String objId = row.getObjectId();
                selectedObjectIds.put(objId, objId);
            }
        }

        multipleValueLookupForm.jumpToPage(multipleValueLookupForm.getViewedPageNumber(), resultTable.size(), maxRowsPerPage);
        if (multipleValueLookupForm.getPreviouslySortedColumnIndex() != null) {
            multipleValueLookupForm.setColumnToSortIndex(Integer.parseInt(multipleValueLookupForm.getPreviouslySortedColumnIndex()));
        }
        multipleValueLookupForm.setCompositeObjectIdMap(selectedObjectIds);

        return resultTable;
    }

    /**
     * This method does the processing necessary to return selected results and sends a redirect back to the lookup caller
     *
     * @param mapping
     * @param form must be an instance of MultipleValueLookupForm
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    public ActionForward prepareToReturnSelectedResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        MultipleValueLookupForm multipleValueLookupForm = (MultipleValueLookupForm) form;
        if (StringUtils.isBlank(multipleValueLookupForm.getLookupResultsSequenceNumber())) {
            // no search was executed
            return prepareToReturnNone(mapping, form, request, response);
        }

        Map<String, String> compositeObjectIdMap = LookupUtils.generateCompositeSelectedObjectIds(multipleValueLookupForm.getPreviouslySelectedObjectIdSet(), multipleValueLookupForm.getDisplayedObjectIdSet(), multipleValueLookupForm.getSelectedObjectIdSet());
        Set<String> compositeObjectIds = compositeObjectIdMap.keySet();

        // The results need to be validated to check if there is at-least one value selected

        boolean success = false;
        if (!compositeObjectIds.isEmpty()) {
            success = true;
        }


        if (success) {

            prepareToReturnSelectedResultBOs(multipleValueLookupForm);

            // build the parameters for the refresh url
            Properties parameters = new Properties();
            parameters.put(KRADConstants.LOOKUP_RESULTS_SEQUENCE_NUMBER, multipleValueLookupForm.getLookupResultsSequenceNumber());
            parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, "viewSummary");

            String dunningLetterDistributionResultRowSummaryUrl = UrlFactory.parameterizeUrl("arDunningLetterDistributionSummary.do", parameters);
            return new ActionForward(dunningLetterDistributionResultRowSummaryUrl, true);

        }
        else {
            return mapping.findForward(RiceConstants.MAPPING_BASIC);
        }
    }
}
