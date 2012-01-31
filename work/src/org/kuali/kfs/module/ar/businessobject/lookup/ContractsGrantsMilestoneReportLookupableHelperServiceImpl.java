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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsMilestone;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsMilestoneReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * Defines a custom lookup for a Milestone Reports.
 */
public class ContractsGrantsMilestoneReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {

    private static final Log LOG = LogFactory.getLog(ContractsGrantsMilestoneReportLookupableHelperServiceImpl.class);

    private BusinessObjectService businessObjectService;

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

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KNSConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KNSConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsMilestoneReport> displayList = new ArrayList<ContractsGrantsMilestoneReport>();
        Collection<ContractsAndGrantsMilestone> milestones;

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isItBilled", "Yes");
        milestones = (Collection<ContractsAndGrantsMilestone>) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsMilestone.class).getExternalizableBusinessObjectsList(ContractsAndGrantsMilestone.class, map);
        map.clear();
        map.put("isItBilled", "No");
        Collection<ContractsAndGrantsMilestone> notBilledMilestones = (Collection<ContractsAndGrantsMilestone>) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsMilestone.class).getExternalizableBusinessObjectsList(ContractsAndGrantsMilestone.class, map);

        milestones.addAll(notBilledMilestones);

        // build search result fields
        for (ContractsAndGrantsMilestone milestone : milestones) {

            ContractsGrantsMilestoneReport cgMilestoneReport = new ContractsGrantsMilestoneReport();
            cgMilestoneReport.setProposalNumber(milestone.getProposalNumber());

            ContractsAndGrantsCGBAward award = milestone.getAward();
            List<ContractsAndGrantsCGBAwardAccount> awardAccounts = (ObjectUtils.isNull(award)) ? new ArrayList() : award.getActiveAwardAccounts();
            String accountNumber = (awardAccounts.size() > 0) ? awardAccounts.get(0).getAccountNumber() : "";

            cgMilestoneReport.setAccountNumber(accountNumber);
            cgMilestoneReport.setMilestoneNumber(milestone.getMilestoneNumber());
            cgMilestoneReport.setMilestoneExpectedCompletionDate(milestone.getMilestoneExpectedCompletionDate());
            cgMilestoneReport.setMilestoneAmount(milestone.getMilestoneAmount());
            cgMilestoneReport.setIsItBilled(milestone.getIsItBilled());


            // filter using lookupForm.getFieldsForLookup()

            if (ContractsGrantsReportUtils.doesMatchLookupFields(lookupForm.getFieldsForLookup(), cgMilestoneReport, "ContractsGrantsMilestoneReport")) {
                displayList.add(cgMilestoneReport);
            }

        }

        buildResultTable(lookupForm, displayList, resultTable);

        return displayList;
    }

    @Override
    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;
        // iterate through result list and wrap rows with return url and action url
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                col.setPropertyValue(propValue);

                // add url when property is documentNumber
                if (col.getPropertyName().equals("documentNumber")) {
                    String url = ConfigContext.getCurrentContextConfig().getKEWBaseURL() + "/" + KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KEWConstants.COMMAND_PARAMETER + "=" + KEWConstants.DOCSEARCH_COMMAND + "&" + KEWConstants.ROUTEHEADER_ID_PARAMETER + "=" + propValue;

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.DOCUMENT_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KNSConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(createTitleText(ContractsGrantsMilestoneReport.class), ContractsGrantsMilestoneReport.class, fieldList));

                    col.setColumnAnchor(a);
                }
            }

            ResultRow row = new ResultRow(columns, "", ACTION_URLS_EMPTY);

            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);

    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
