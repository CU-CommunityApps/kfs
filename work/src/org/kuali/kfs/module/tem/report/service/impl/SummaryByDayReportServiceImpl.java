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
package org.kuali.kfs.module.tem.report.service.impl;

import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.LODGING_TYPE_CODES;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.TRANSPORTATION_TYPE_CODES;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.report.SummaryByDayReport;
import org.kuali.kfs.module.tem.report.service.SummaryByDayReportService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation of ExpenseSummaryReportService.
 */
@Transactional
public class SummaryByDayReportServiceImpl implements SummaryByDayReportService {

    public static Logger LOG = Logger.getLogger(SummaryByDayReportServiceImpl.class);

    protected ConfigurationService configurationService;
    protected ParameterService parameterService;
    protected PersonService personService;
    protected TravelDocumentService travelDocumentService;
    protected SimpleDateFormat monthDay = new SimpleDateFormat("MM/dd");

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService ConfigurationService) {
        this.configurationService = ConfigurationService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(final PersonService personService) {
        this.personService = personService;
    }

    @Override
    public SummaryByDayReport buildReport(final TravelDocument travelDocument) {
        LOG.debug("Building a "+ SummaryByDayReport.class+ " report for trip id "+ travelDocument.getTravelDocumentIdentifier());
        final SummaryByDayReport retval = new SummaryByDayReport();
        retval.setBeginDate(travelDocument.getTripBegin() != null ? KfsDateUtils.clearTimeFields(travelDocument.getTripBegin()) : new Date());
        retval.setEndDate(travelDocument.getTripEnd() != null ? KfsDateUtils.clearTimeFields(travelDocument.getTripEnd()) : new Date());
        retval.setTripId(travelDocument.getTravelDocumentIdentifier().toString());
        retval.setPurpose(travelDocument.getReportPurpose() == null ? "" : travelDocument.getReportPurpose());
        retval.setInstitution(getParameterService().getParameterValueAsString(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, KfsParameterConstants.INSTITUTION_NAME));

        travelDocument.refreshReferenceObject(TemPropertyConstants.ACTUAL_EXPENSES);

        final Collection<SummaryByDayReport.Detail> transportation = new ArrayList<SummaryByDayReport.Detail>();
        final Collection<SummaryByDayReport.Detail> other = new ArrayList<SummaryByDayReport.Detail>();
        final Collection<SummaryByDayReport.Detail> lodging = new ArrayList<SummaryByDayReport.Detail>();
        final Collection<SummaryByDayReport.Detail> meals = new ArrayList<SummaryByDayReport.Detail>();
        final Collection<SummaryByDayReport.Detail> summary = new ArrayList<SummaryByDayReport.Detail>();
        final Collection<SummaryByDayReport.Detail> perDiemWeeklyTotal = new ArrayList<SummaryByDayReport.Detail>();

        final Map<String, KualiDecimal> summaryData = new TreeMap<String, KualiDecimal>();

        Calendar cal = new GregorianCalendar();
        cal.setTime(retval.getBeginDate());
        int weekCount = 1;
        KualiDecimal milageWeekTotal = KualiDecimal.ZERO;
        KualiDecimal lodgingWeekTotal = KualiDecimal.ZERO;
        KualiDecimal mealWeekTotal = KualiDecimal.ZERO;

        Calendar firstDayOfWeek = new GregorianCalendar();
        firstDayOfWeek.setTime(retval.getBeginDate());
        firstDayOfWeek.add(Calendar.DAY_OF_WEEK, Calendar.MONDAY - cal.get(Calendar.DAY_OF_WEEK));

        Calendar lastDayOfWeek = new GregorianCalendar();
        lastDayOfWeek.setTime(firstDayOfWeek.getTime());
        lastDayOfWeek.add(Calendar.DAY_OF_WEEK, 6);

        firstDayOfWeek.add(Calendar.MILLISECOND, -1);
        lastDayOfWeek.add(Calendar.MILLISECOND, 1);

        int perDiemExpensesSize = travelDocument.getPerDiemExpenses().size();
        for (final PerDiemExpense perDiemExpense: travelDocument.getPerDiemExpenses()){
            if(!(perDiemExpense.getMileageDate().after(firstDayOfWeek.getTime()) && perDiemExpense.getMileageDate().before(lastDayOfWeek.getTime()) && --perDiemExpensesSize > 0)){
                String weekCountFormat = weekCount < 10 ? "0"+weekCount : ""+weekCount;
                SummaryByDayReport.Detail perDiemMilageWeekTotal = new SummaryByDayReport.Detail("Per Diem Milage", milageWeekTotal, "week " + weekCountFormat);
                perDiemWeeklyTotal.add(perDiemMilageWeekTotal);

                SummaryByDayReport.Detail perDiemLodgingWeekTotal = new SummaryByDayReport.Detail("Per Diem Lodging", lodgingWeekTotal, "week " + weekCountFormat);
                perDiemWeeklyTotal.add(perDiemLodgingWeekTotal);

                SummaryByDayReport.Detail perDiemMealWeekTotal = new SummaryByDayReport.Detail("Per Diem Meals & Incidentals", mealWeekTotal, "week " + weekCountFormat);
                perDiemWeeklyTotal.add(perDiemMealWeekTotal);

                firstDayOfWeek.add(Calendar.DAY_OF_WEEK, 7);
                lastDayOfWeek.add(Calendar.DAY_OF_WEEK, 7);
                weekCount++;

                // reset totals
                milageWeekTotal = KualiDecimal.ZERO;
                lodgingWeekTotal = KualiDecimal.ZERO;
                mealWeekTotal = KualiDecimal.ZERO;
            }

            final String expenseDate = monthDay.format(cal.getTime());

            SummaryByDayReport.Detail perDiemMilage = new SummaryByDayReport.Detail("Per Diem Milage", perDiemExpense.getMileageTotal(), expenseDate);
            transportation.add(perDiemMilage);

            SummaryByDayReport.Detail perDiemLodging = new SummaryByDayReport.Detail("Per Diem Lodging", perDiemExpense.getLodgingTotal(), expenseDate);
            lodging.add(perDiemLodging);

            SummaryByDayReport.Detail perDiemMeal = new SummaryByDayReport.Detail("Per Diem Meals & Incidentals", perDiemExpense.getMealsAndIncidentals(), expenseDate);
            meals.add(perDiemMeal);

            milageWeekTotal = milageWeekTotal.add(perDiemExpense.getMileageTotal());
            lodgingWeekTotal = lodgingWeekTotal.add(perDiemExpense.getLodgingTotal());
            mealWeekTotal = mealWeekTotal.add(perDiemExpense.getMealsAndIncidentals());

            incrementSummary(summaryData, expenseDate, perDiemExpense.getDailyTotal());

            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (final ActualExpense expense : travelDocument.getActualExpenses()) {
            expense.refreshReferenceObject(TemPropertyConstants.EXPENSE_TYPE_OBJECT_CODE);
            final String expenseDate = monthDay.format(expense.getExpenseDate());
            final SummaryByDayReport.Detail detail = new SummaryByDayReport.Detail(expense.getExpenseTypeObjectCode().getExpenseType().getName()==null?"":expense.getExpenseTypeObjectCode().getExpenseType().getName(), new KualiDecimal(expense.getExpenseAmount().bigDecimalValue().multiply(expense.getCurrencyRate())), expenseDate);

            if (isTransportationExpense(expense)) {
                transportation.add(detail);
            }
            else if (isLodgingExpense(expense)) {
                lodging.add(detail);
            }
            else if (isMealsExpense(expense)) {
                meals.add(detail);
            }
            else {
                other.add(detail);
            }

            incrementSummary(summaryData, expenseDate, new KualiDecimal(expense.getExpenseAmount().bigDecimalValue().multiply(expense.getCurrencyRate())));
        }

        for (final String expenseDate : summaryData.keySet()) {
            final KualiDecimal expenseAmount = summaryData.get(expenseDate);
            final SummaryByDayReport.Detail detail = new SummaryByDayReport.Detail("TOTAL of all above expenses", expenseAmount, expenseDate);
            summary.add(detail);
        }

        if (perDiemWeeklyTotal.size() > 0) {
            LOG.debug("Adding "+ perDiemWeeklyTotal.size()+ " per diem weekly total");
            retval.setWeeklyTotal(perDiemWeeklyTotal);
        }
        if (other.size() > 0) {
            LOG.debug("Adding "+ other.size()+ " other expenses");
            retval.setOtherExpenses(other);
        }
        if (transportation.size() > 0) {
            LOG.debug("Adding "+ transportation.size()+ " transportation expenses");
            retval.setTransportation(transportation);
        }
        if (lodging.size() > 0) {
            LOG.debug("Adding "+ lodging.size()+ " lodging expenses");
            retval.setLodging(lodging);
        }
        if (meals.size() > 0) {
            LOG.debug("Adding "+ meals.size()+ " meals expenses");
            retval.setMeals(meals);
        }
        if (summary.size() > 0) {
            LOG.debug("Adding "+ summary.size()+ " summary");
            retval.setSummary(summary);
        }
        return retval;
    }

    protected void incrementSummary(final Map<String, KualiDecimal> summaryData, String expenseDate, KualiDecimal expense) {
        KualiDecimal summaryAmount = summaryData.get(expenseDate);
        if (summaryAmount == null) {
            summaryAmount = KualiDecimal.ZERO;
        }
        summaryAmount = summaryAmount.add(expense);
        LOG.debug("Adding "+ summaryAmount+ " for "+ expenseDate+ " to summary data");
        summaryData.put(expenseDate, summaryAmount);
    }

    protected boolean isTransportationExpense(final ActualExpense expense) {
        LOG.debug("Checking if "+ expense+ " is a transportation ");
        return expenseTypeCodeMatchesParameter(expense.getExpenseTypeCode(), TRANSPORTATION_TYPE_CODES);
    }

    protected boolean isLodgingExpense(final ActualExpense expense) {
        LOG.debug("Checking if "+ expense+ " is a lodging ");
        return expenseTypeCodeMatchesParameter(expense.getExpenseTypeCode(), LODGING_TYPE_CODES);
    }

    protected boolean isMealsExpense(final ActualExpense expense) {
        LOG.debug("Checking if "+ expense+ " is a meal ");
        return getTravelDocumentService().isHostedMeal(expense);
    }

    protected boolean expenseTypeCodeMatchesParameter(final String expenseTypeCode, final String parameter) {
        return getParameterService().getParameterValuesAsString(TravelReimbursementDocument.class, parameter).contains(expenseTypeCode);
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the travelDocumentService attribute.
     *
     * @return Returns the travelDocumentService.
     */
    public TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    /**
     * Sets the travelDocumentService attribute value.
     *
     * @param travelDocumentService The travelDocumentService to set.
     */
    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }
}
