/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.endow.batch.service.RollFrequencyDatesService;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.module.endow.dataaccess.FeeMethodDao;
import org.kuali.kfs.module.endow.dataaccess.RecurringCashTransferDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.dataaccess.TicklerDao;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the RollFrequencyDatesService batch job.
 */
@Transactional
public class RollFrequencyDatesServiceImpl implements RollFrequencyDatesService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RollFrequencyDatesServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService; 
    
    protected SecurityDao securityDao;
    protected FeeMethodDao feeMethodDao;
    protected TicklerDao ticklerDao;
    protected RecurringCashTransferDao recurringCashTransferDao;
    
    /**
     * Updates some date fields based on the frequency for the activity 
     * @return true if the fields are updated successfully; false otherwise
     */
    public boolean updateFrequencyDate() {
        
        LOG.info("Begin roll frequncy dates ..."); 
                
        // update Security Income Next Pay Dates
        updateSecurityIncomeNextPayDates();
                       
        // update Tickler Next Due Dates
        updateTicklerNextDueDates();
        
        // update Fee Next Process Dates
        updateFeeProcessDates();
        
        // update Next Process Dates
        updateProcessDates();
        
        return true;
    }

    /**
     * This method updates the income next pay dates in Security
     */
    protected void updateSecurityIncomeNextPayDates() {
        
        List<Security> securityRecords = securityDao.getSecuritiesWithNextPayDateEqualToCurrentDate();
        for (Security security : securityRecords) {
            String frequencyCode = security.getIncomePayFrequency();           
            if (frequencyCode != null && !frequencyCode.isEmpty()) {
                Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode);
                if (nextDate != null) {
                    security.setIncomeNextPayDate(nextDate);
                    businessObjectService.save(security);
                }
            }
        }
        LOG.info("Updated Security Income Next Pay Dates"); 
    }
    
    /**
     * This method updates the next due dates in Tickler
     */
    protected void updateTicklerNextDueDates() {
        
        List<Tickler> TicklerRecords = ticklerDao.getTicklerWithNextPayDateEqualToCurrentDate();
        for (Tickler tickler : TicklerRecords) {
            String frequencyCode = tickler.getFrequencyCode();           
            if (frequencyCode != null && !frequencyCode.isEmpty()) {
                Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    tickler.setNextDueDate(nextDate);
                    businessObjectService.save(tickler);
                }
            }
        }
        LOG.info("Updated Tickler Next Due Dates");
    }
    
    /**
     * This method updates the next process dates in FeeMethod
     */
    protected void updateFeeProcessDates() {
        
        List<FeeMethod> feeMethodRecords = feeMethodDao.getFeeMethodWithNextPayDateEqualToCurrentDate();
        for (FeeMethod feeMethod : feeMethodRecords) {                        
            String frequencyCode = feeMethod.getFeeFrequencyCode();           
            if (frequencyCode != null && !frequencyCode.isEmpty()) {                
                Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    feeMethod.setFeeLastProcessDate(feeMethod.getFeeNextProcessDate());
                    feeMethod.setFeeNextProcessDate(nextDate);
                    businessObjectService.save(feeMethod);
                }
            }
        }
        LOG.info("Updated Fee Next Process Dates and Fee Last Process Dates");
    }
    
    /**
     * This method updates the next process dates in EndowmentRecurringCashTransfer
     */
    protected void updateProcessDates() {
        
        List<EndowmentRecurringCashTransfer> recurringCashTransferRecords = recurringCashTransferDao.getRecurringCashTransferWithNextPayDateEqualToCurrentDate();
        for (EndowmentRecurringCashTransfer recurringCashTransfer : recurringCashTransferRecords) {                       
            String frequencyCode = recurringCashTransfer.getFrequencyCode();           
            if (frequencyCode != null && !frequencyCode.isEmpty()) {                
                Date nextDate = calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    recurringCashTransfer.setLastProcessDate(recurringCashTransfer.getNextProcessDate());
                    recurringCashTransfer.setNextProcessDate(nextDate);
                    businessObjectService.save(recurringCashTransfer);
                }
            }
        }
        LOG.info("Updated Next Process Dates and Last Process Dates");
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) 
    {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the calculateProcessDateUsingFrequencyCodeService attribute value.
     * @param calculateProcessDateUsingFrequencyCodeService The calculateProcessDateUsingFrequencyCodeService to set.
     */
    public void setCalculateProcessDateUsingFrequencyCodeService(CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService) {
        this.calculateProcessDateUsingFrequencyCodeService = calculateProcessDateUsingFrequencyCodeService;
    }

    /**
     * Sets the securityDao attribute value.
     * @param securityDao The securityDao to set.
     */
    public void setSecurityDao(SecurityDao securityDao) {
        this.securityDao = securityDao;
    }

    /**
     * Sets the feeMethodDao attribute value.
     * @param feeMethodDao The feeMethodDao to set.
     */
    public void setFeeMethodDao(FeeMethodDao feeMethodDao) {
        this.feeMethodDao = feeMethodDao;
    }

    /**
     * Sets the ticklerDao attribute value.
     * @param ticklerDao The ticklerDao to set.
     */
    public void setTicklerDao(TicklerDao ticklerDao) {
        this.ticklerDao = ticklerDao;
    }

    /**
     * Sets the recurringCashTransferDao attribute value.
     * @param recurringCashTransferDao The recurringCashTransferDao to set.
     */
    public void setRecurringCashTransferDao(RecurringCashTransferDao recurringCashTransferDao) {
        this.recurringCashTransferDao = recurringCashTransferDao;
    }

    
}
