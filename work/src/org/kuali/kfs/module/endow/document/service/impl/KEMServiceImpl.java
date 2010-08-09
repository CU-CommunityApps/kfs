/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.batch.AvailableCashUpdateStep;
import org.kuali.kfs.module.endow.document.service.CurrentTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;

public class KEMServiceImpl implements KEMService {

    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    private CurrentTaxLotService currentTaxLotService;

    private static Logger log = org.apache.log4j.Logger.getLogger(KEMServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMService#getMarketValue(java.lang.String)
     */
    public BigDecimal getMarketValue(String securityId) {

        BigDecimal marketValue = currentTaxLotService.getHoldingMarketValueSumForSecurity(securityId);

        return marketValue;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMService#mod10(java.lang.String)
     */
    public String mod10(String prefix) {
        Map<String, Integer> mod10 = new HashMap<String, Integer>();
        char[] userInputChars = prefix.toCharArray();
        int theNinthDigit = 0;

        for (int i = 0; i < userInputChars.length; i++) {

            int mod10MappingForCurrentChar = mapChar(userInputChars[i]);

            if (i % 2 != 0) {

                mod10MappingForCurrentChar *= 2;
            }

            if (mod10MappingForCurrentChar > 9) {
                mod10MappingForCurrentChar = (mod10MappingForCurrentChar % 10) + (mod10MappingForCurrentChar / 10);
            }

            theNinthDigit += mod10MappingForCurrentChar;
        }

        theNinthDigit = (10 - (theNinthDigit % 10)) % 10;

        return String.valueOf(theNinthDigit);
    }

    /**
     * Maps a char to an integer value
     * 
     * @param c the character to be mapped
     * @return the mapped value
     */
    private static int mapChar(char c) {

        if (c >= '0' && c <= '9')
            return c - '0';

        if (c == '*')
            return 36;
        if (c == '#')
            return 37;
        if (c == '@')
            return 38;

        return c - 'A' + 10;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMService#getCurrentSystemProcessDate()
     */
    public String getCurrentSystemProcessDate() {

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        String curentSystemProcessDate = parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_ALL.class, EndowConstants.EndowmentSystemParameter.CURRENT_PROCESS_DATE);

        return curentSystemProcessDate;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMService#getCurrentSystemProcessDate()
     */
    public String getCurrentSystemProcessDateFormated() throws Exception {
        return getDateTimeService().toDateString(getCurrentDate());
    }

    public Date getCurrentSystemProcessDateObject() {
        Date date = null;

        try {
            String systemDateString = getCurrentSystemProcessDate();
            SimpleDateFormat sdfSource = new SimpleDateFormat("dd-MMM-yy");
            date = sdfSource.parse(systemDateString);

        }
        catch (Exception e) {
            log.debug("Issue obtaining System Date", e);
            date = new Date();
        }

        return date;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMService#getCurrentDate()
     */
    public java.sql.Date getCurrentDate() {
        java.sql.Date currentDate = null;
        String useProcessDate = parameterService.getParameterValue(KfsParameterConstants.ENDOWMENT_ALL.class, EndowConstants.EndowmentSystemParameter.USE_PROCESS_DATE);

        if (KFSConstants.ParameterValues.YES.equalsIgnoreCase(useProcessDate)) {
            currentDate = getCurrentProcessDate();
        }
        else {
            currentDate = dateTimeService.getCurrentSqlDate();
        }
        return currentDate;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMService#getCurrentProcessDate()
     */
    public java.sql.Date getCurrentProcessDate() {
        java.sql.Date date = null;

        String currentProcessDate = getCurrentSystemProcessDate();
        try {
            date = dateTimeService.convertToSqlDate(currentProcessDate);
        }
        catch (ParseException ex) {
            throw new RuntimeException("Invalid value for " + EndowConstants.EndowmentSystemParameter.CURRENT_PROCESS_DATE + " system parameter: " + currentProcessDate + ".\n" + ex.getMessage());
        }

        return date;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMService#getAvailableCashPercent() Gets the AVAILABLE_CASH_PERCENT system
     *      parameter
     * @return AVAILABLE_CASH_PERCENT value
     */
    public BigDecimal getAvailableCashPercent() {
        BigDecimal availableCashPercent = BigDecimal.ZERO;

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String systemParameterAvailablePercent = parameterService.getParameterValue(AvailableCashUpdateStep.class, EndowParameterKeyConstants.AvailableCashUpdateConstants.AVAILABLE_CASH_PERCENT);
        availableCashPercent = new BigDecimal(systemParameterAvailablePercent);

        return availableCashPercent;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.service.KEMService#getFiscalYearEndDayAndMonth() Gets the
     *      FISCAL_YEAR_END_DAY_AND_MONTH system parameter
     * @return FISCAL_YEAR_END_DAY_AND_MONTH value
     */
    public Date getFiscalYearEndDayAndMonth() {
        Date fiscalDate = null;

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String yearEndDateAndMonth = parameterService.getParameterValue(Batch.class, EndowConstants.EndowmentSystemParameter.FISCAL_YEAR_END_DAY_AND_MONTH);

        Calendar calendar = Calendar.getInstance();
        yearEndDateAndMonth = yearEndDateAndMonth.concat("/") + calendar.get(Calendar.YEAR);

        try {
            fiscalDate = getDateTimeService().convertToDate(yearEndDateAndMonth);
        }
        catch (ParseException pe) {
            return null;
        }

        return fiscalDate;
    }

    /**
     * Gets the dateTimeService.
     * 
     * @return dateTimeService
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets dateTimeService.
     * 
     * @param dateTimeService
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the parameterService.
     * 
     * @return parameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService.
     * 
     * @param parameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the currentTaxLotService.
     * 
     * @param currentTaxLotService
     */
    public void setCurrentTaxLotService(CurrentTaxLotService currentTaxLotService) {
        this.currentTaxLotService = currentTaxLotService;
    }

}
