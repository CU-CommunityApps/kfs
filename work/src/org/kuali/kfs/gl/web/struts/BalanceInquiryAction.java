/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will 
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.web.struts.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.lookup.CollectionIncomplete;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.uidraw.Field;
import org.kuali.core.web.uidraw.Row;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.web.struts.form.BalanceInquiryForm;

/**
 * This class handles Actions for lookup flow
 * 
 * @author Kuali Nervous System Team ()
 */

public class BalanceInquiryAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryAction.class);

    private static final String TOTALS_TABLE_KEY = "totalsTable";

    private KualiConfigurationService kualiConfigurationService;
    private String[] totalTitles;

    public BalanceInquiryAction() {
        super();
        kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
    }

    private void setTotalTitles() {
        totalTitles = new String[7];

        totalTitles[0] = kualiConfigurationService.getPropertyString(KeyConstants.AccountBalanceService.INCOME);
        totalTitles[1] = kualiConfigurationService.getPropertyString(KeyConstants.AccountBalanceService.INCOME_FROM_TRANSFERS);
        totalTitles[2] = kualiConfigurationService.getPropertyString(KeyConstants.AccountBalanceService.INCOME_TOTAL);
        totalTitles[3] = kualiConfigurationService.getPropertyString(KeyConstants.AccountBalanceService.EXPENSE);
        totalTitles[4] = kualiConfigurationService.getPropertyString(KeyConstants.AccountBalanceService.EXPENSE_FROM_TRANSFERS);
        totalTitles[5] = kualiConfigurationService.getPropertyString(KeyConstants.AccountBalanceService.EXPENSE_TOTAL);
        totalTitles[6] = kualiConfigurationService.getPropertyString(KeyConstants.AccountBalanceService.TOTAL);

    }

    private String[] getTotalTitles() {
        if (null == totalTitles) {
            setTotalTitles();
        }

        return totalTitles;
    }

    /**
     * Entry point to lookups, forwards to jsp for search render.
     */
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * search - sets the values of the data entered on the form on the jsp into a map and then searches for the results.
     */
    public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BalanceInquiryForm lookupForm = (BalanceInquiryForm) form;

        Lookupable kualiLookupable = lookupForm.getLookupable();

        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Collection displayList = new ArrayList();
        Collection resultTable = new ArrayList();

        kualiLookupable.validateSearchParameters(lookupForm.getFields());

        try {
            displayList = SpringServiceLocator.getPersistenceService().performLookup(lookupForm, kualiLookupable, resultTable, true);

            Object[] resultTableAsArray = resultTable.toArray();

            CollectionIncomplete incompleteDisplayList = (CollectionIncomplete) displayList;
            Long totalSize = ((CollectionIncomplete) displayList).getActualSizeIfTruncated();

            request.setAttribute(Constants.REQUEST_SEARCH_RESULTS_SIZE, totalSize);

            String lookupableName = kualiLookupable.getClass().getName().substring(kualiLookupable.getClass().getName().lastIndexOf('.') + 1);

            if (lookupableName.startsWith("AccountBalanceByConsolidation")) {


                Collection totalsTable = new ArrayList();

                int listIndex = 0;
                int arrayIndex = 0;
                int listSize = incompleteDisplayList.size();

                for (; listIndex < listSize;) {

                    AccountBalance balance = (AccountBalance) incompleteDisplayList.get(listIndex);

                    boolean ok = ObjectHelper.isOneOf(balance.getTitle(), getTotalTitles());
                    if (ok) {

                        if (totalSize > 7) {
                            totalsTable.add(resultTableAsArray[arrayIndex]);
                        }
                        resultTable.remove(resultTableAsArray[arrayIndex]);

                        incompleteDisplayList.remove(balance);
                        // account for the removal of the balance which resizes the list
                        listIndex--;
                        listSize--;

                    }

                    listIndex++;
                    arrayIndex++;

                }

                request.setAttribute(Constants.REQUEST_SEARCH_RESULTS, resultTable);

                request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
                GlobalVariables.getUserSession().addObject(TOTALS_TABLE_KEY, totalsTable);

            }
            else {

                request.setAttribute(Constants.REQUEST_SEARCH_RESULTS, resultTable);

            }

            if (request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY) != null) {
                GlobalVariables.getUserSession().removeObject(request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY));
            }

            request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, GlobalVariables.getUserSession().addObject(resultTable));

        }
        catch (NumberFormatException e) {
            GlobalVariables.getErrorMap().putError(PropertyConstants.UNIVERSITY_FISCAL_YEAR, KeyConstants.ERROR_CUSTOM, new String[] { "Fiscal Year must be a four-digit number" });
        }
        catch (Exception e) {
            GlobalVariables.getErrorMap().putError(Constants.DOCUMENT_ERRORS, KeyConstants.ERROR_CUSTOM, new String[] { "Please report the server error." });
            LOG.error("Application Errors", e);
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * refresh - is called when one quickFinder returns to the previous one. Sets all the values and performs the new search.
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        Map fieldValues = new HashMap();
        Map values = lookupForm.getFields();

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();

            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();

                if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                    if (request.getParameter(field.getPropertyName()) != null) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()));
                    }
                    else if (values.get(field.getPropertyName()) != null) {
                        field.setPropertyValue(values.get(field.getPropertyName()));
                    }
                }
                fieldValues.put(field.getPropertyName(), field.getPropertyValue());
            }
        }
        fieldValues.put(Constants.DOC_FORM_KEY, lookupForm.getFormKey());
        fieldValues.put(Constants.BACK_LOCATION, lookupForm.getBackLocation());

        if (kualiLookupable.checkForAdditionalFields(fieldValues)) {
            for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();
                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();
                    if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
                        if (request.getParameter(field.getPropertyName()) != null) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                            fieldValues.put(field.getPropertyName(), request.getParameter(field.getPropertyName()));
                        }
                        else if (values.get(field.getPropertyName()) != null) {
                            field.setPropertyValue(values.get(field.getPropertyName()));
                        }
                    }
                }
            }
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Just returns as if return with no value was selected.
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LookupForm lookupForm = (LookupForm) form;

        String backUrl = lookupForm.getBackLocation() + "?methodToCall=refresh&docFormKey=" + lookupForm.getFormKey();
        return new ActionForward(backUrl, true);
    }


    /**
     * clearValues - clears the values of all the fields on the jsp.
     */
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!field.getFieldType().equals(Field.RADIO)) {
                    field.setPropertyValue(field.getDefaultValue());
                }
            }
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward viewResults(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setAttribute(Constants.SEARCH_LIST_REQUEST_KEY, request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY));
        request.setAttribute(Constants.REQUEST_SEARCH_RESULTS, GlobalVariables.getUserSession().retrieveObject(request.getParameter(Constants.SEARCH_LIST_REQUEST_KEY)));
        request.setAttribute(Constants.REQUEST_SEARCH_RESULTS_SIZE, request.getParameter(Constants.REQUEST_SEARCH_RESULTS_SIZE));

        String boClassName = ((BalanceInquiryForm) form).getBusinessObjectClassName();
        String lookupableName = boClassName.substring(boClassName.lastIndexOf('.') + 1);

        if (lookupableName.startsWith("AccountBalanceByConsolidation")) {
            Object totalsTable = GlobalVariables.getUserSession().retrieveObject(TOTALS_TABLE_KEY);
            request.setAttribute(TOTALS_TABLE_KEY, totalsTable);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }

}