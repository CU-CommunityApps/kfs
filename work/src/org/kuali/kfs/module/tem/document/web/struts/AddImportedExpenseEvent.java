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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemPropertyConstants.NEW_IMPORTED_EXPENSE_LINE;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.validation.event.AddImportedExpenseLineEvent;
import org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.ObjectUtils;


public class AddImportedExpenseEvent implements Observer {
    protected volatile TravelExpenseService travelExpenseService;

    @SuppressWarnings("null")
    @Override
    public void update(Observable arg0, Object arg1) {
        if (!(arg1 instanceof TravelMvcWrapperBean)) {
            return;
        }
        final TravelMvcWrapperBean wrapper = (TravelMvcWrapperBean) arg1;

        final TravelDocument document = wrapper.getTravelDocument();
        final ImportedExpense newImportedExpenseLine = wrapper.getNewImportedExpenseLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= getRuleService().applyRules(new AddImportedExpenseLineEvent<ImportedExpense>(NEW_IMPORTED_EXPENSE_LINE, document, newImportedExpenseLine));

        if (rulePassed){
            if(newImportedExpenseLine != null){
                document.addExpense(newImportedExpenseLine);
            }

            ImportedExpense newExpense = new ImportedExpense();
            try {
                BeanUtils.copyProperties(newExpense, newImportedExpenseLine);
                newExpense.setConvertedAmount(null);
                newExpense.setExpenseParentId(newExpense.getId());
                newExpense.setId(null);
                newExpense.setNotes(null);
                newExpense.setExpenseLineTypeCode(null);
            }
            catch (IllegalAccessException ex) {
                throw new RuntimeException("Could not copy properties to imported line detail", ex);
            }
            catch (InvocationTargetException ex) {
                throw new RuntimeException("Could not copy properties to imported line detail", ex);
            }

            wrapper.setNewImportedExpenseLine(new ImportedExpense());
            wrapper.getNewImportedExpenseLines().add(newExpense);
            wrapper.setDistribution(getAccountingDistributionService().buildDistributionFrom(document));

            //Add the appropriate source accounting line
            if (newImportedExpenseLine.getCardType() != null && newImportedExpenseLine.getCardType().equals(TemConstants.TRAVEL_TYPE_CTS)){
                HistoricalTravelExpense historicalTravelExpense = getBusinessObjectService().findBySinglePrimaryKey(HistoricalTravelExpense.class, newImportedExpenseLine.getHistoricalTravelExpenseId());
                historicalTravelExpense.refreshReferenceObject(TemPropertyConstants.AGENCY_STAGING_DATA);
                List<TripAccountingInformation> tripAccountInfoList = historicalTravelExpense.getAgencyStagingData().getTripAccountingInformation();
                final ExpenseTypeObjectCode expenseTypeObjectCode = findExpenseTypeObjectCodeForAgencyStagingData(document, historicalTravelExpense.getAgencyStagingData());

                for (TripAccountingInformation tripAccountingInformation : tripAccountInfoList){
                    TemSourceAccountingLine importedLine = new TemSourceAccountingLine();
                    importedLine.setAmount(ObjectUtils.isNotNull(tripAccountingInformation.getAmount()) ? tripAccountingInformation.getAmount() :
                        historicalTravelExpense.getAmount().divide(new KualiDecimal(tripAccountInfoList.size())));
                    importedLine.setChartOfAccountsCode(tripAccountingInformation.getTripChartCode());
                    importedLine.setAccountNumber(tripAccountingInformation.getTripAccountNumber());
                    importedLine.setSubAccountNumber(tripAccountingInformation.getTripSubAccountNumber());
                    if (expenseTypeObjectCode != null) {
                        importedLine.setFinancialObjectCode(expenseTypeObjectCode.getFinancialObjectCode());
                    }
                    importedLine.setFinancialSubObjectCode(tripAccountingInformation.getSubObjectCode());
                    importedLine.setProjectCode(tripAccountingInformation.getProjectCode());
                    importedLine.setOrganizationReferenceId(tripAccountingInformation.getOrganizationReference());
                    importedLine.setCardType(TemConstants.TRAVEL_TYPE_CTS);
                    importedLine.getPostingYear();
                    importedLine.refresh();
                    document.addSourceAccountingLine(importedLine);
                }
            }
        }
    }

    /**
     * Looks up the appropriate ExpenseTypeObjectCode record for the given document and agency staging data
     * @param document the document which has a document type, a traveler type (hopefully), and a trip type
     * @param agencyStagingData the agency staging data, from which we can find the expense type
     * @return the ExpenseTypeObjectCode or null if it could not be determined
     */
    protected ExpenseTypeObjectCode findExpenseTypeObjectCodeForAgencyStagingData(TravelDocument document, AgencyStagingData agencyStagingData) {
        final String documentType = document.getDocumentTypeName();
        final String tripType = StringUtils.isBlank(document.getTripTypeCode()) ? TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRIP_TYPE : document.getTripTypeCode();
        final String travelerType = ObjectUtils.isNull(document.getTraveler()) || StringUtils.isBlank(document.getTraveler().getTravelerTypeCode()) ? TemConstants.ALL_EXPENSE_TYPE_OBJECT_CODE_TRAVELER_TYPE : document.getTraveler().getTravelerTypeCode();
        final ExpenseType expenseType = getTravelExpenseService().getDefaultExpenseTypeForCategory(agencyStagingData.getExpenseTypeCategory());
        if (expenseType == null) {
            return null;
        }
        final ExpenseTypeObjectCode expenseTypeObjectCode = getTravelExpenseService().getExpenseType(expenseType.getCode(), documentType, tripType, travelerType);
        return expenseTypeObjectCode;
    }

    /**
     * Gets the travelReimbursementService attribute.
     *
     * @return Returns the travelReimbursementService.
     */
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    /**
     * Gets the kualiRulesService attribute.
     *
     * @return Returns the kualiRuleseService.
     */
    protected KualiRuleService getRuleService() {
        return SpringContext.getBean(KualiRuleService.class);
    }

    protected AccountingDistributionService getAccountingDistributionService() {
        return SpringContext.getBean(AccountingDistributionService.class);
    }

    protected TravelExpenseService getTravelExpenseService() {
        if (travelExpenseService == null) {
            travelExpenseService = SpringContext.getBean(TravelExpenseService.class);
        }
        return travelExpenseService;
    }
}
