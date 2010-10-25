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
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.Tickler;
import org.kuali.kfs.module.endow.dataaccess.AutomatedCashInvestmentModelDao;
import org.kuali.kfs.module.endow.dataaccess.CashSweepModelDao;
import org.kuali.kfs.module.endow.dataaccess.FeeMethodDao;
import org.kuali.kfs.module.endow.dataaccess.RecurringCashTransferDao;
import org.kuali.kfs.module.endow.dataaccess.SecurityDao;
import org.kuali.kfs.module.endow.dataaccess.TicklerDao;
import org.kuali.kfs.module.endow.document.service.FrequencyCodeService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the RollFrequencyDatesService batch job.
 */
@Transactional
public class RollFrequencyDatesServiceImpl implements RollFrequencyDatesService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RollFrequencyDatesServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected FrequencyCodeService frequencyCodeService;
    
    protected SecurityDao securityDao;
    protected FeeMethodDao feeMethodDao;
    protected TicklerDao ticklerDao;
    protected RecurringCashTransferDao recurringCashTransferDao;
    protected AutomatedCashInvestmentModelDao automatedCashInvestmentModelDao;
    protected CashSweepModelDao cashSweepModelDao;
        
    protected ReportWriterService rollFrequencyDatesReportWriterService;
    
    /**
     * Updates some date fields based on the frequency for the activity 
     * @return true if the fields are updated successfully; false otherwise
     */
    public boolean updateFrequencyDate() {
        
        LOG.info("Begin the batch Roll Frequncy Dates ...");
        
        // update Security Income Next Pay Dates
        updateSecurityIncomeNextPayDates();
                       
        // update Tickler Next Due Dates
        updateTicklerNextDueDates();
        
        // update Fee Next Process Dates
        updateFeeProcessDates();
        
        // update Recurring Cash Transfer Next Process Dates
        updateRecurringCashTransferProcessDates();
        
        // update Cash Investment Model Next Due Dates
        updateAutomatedCashInvestmentModelNextDueDates();
        
        // update Cash Sweep Model Next Due Dates
        updateCashSweepModelNextDueDates();
        
        LOG.info("The batch Roll Frequncy Dates was finished.");
        
        return true;
    }

    /**
     * This method updates the income next pay dates in Security
     */
    protected void updateSecurityIncomeNextPayDates() {
        
        int counter = 0;
        List<Security> securityRecords = securityDao.getSecuritiesWithNextPayDateEqualToCurrentDate();
        if (securityRecords != null) {
            for (Security security : securityRecords) {
                String frequencyCode = security.getIncomePayFrequency();           
                Date nextDate = frequencyCodeService.calculateProcessDate(frequencyCode);
                if (nextDate != null) {
                    security.setIncomeNextPayDate(nextDate);
                    if (updateBusinessObject(security)) {
                        counter++;
                    } else {
                        LOG.error("Failed to update Security " + security.getId());
                    }
                }
            }
        }
        generateReport("END_SEC_T", counter, true);
        LOG.info("Total Security Income Next Pay Dates updated in END_SEC_T: " + counter); 
    }
    
    /**
     * This method updates the next due dates in Tickler
     */
    protected void updateTicklerNextDueDates() {
        
        int counter = 0;
        List<Tickler> ticklerRecords = ticklerDao.getTicklerWithNextPayDateEqualToCurrentDate();
        if (ticklerRecords != null) {
            for (Tickler tickler : ticklerRecords) {
                String frequencyCode = tickler.getFrequencyCode();           
                Date nextDate = frequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    tickler.setNextDueDate(nextDate);
                    if (updateBusinessObject(tickler)) {
                        counter++;
                    } else {
                        LOG.error("Failed to update Tickler " + tickler.getNumber());
                    }
                }                
            }
        }
        generateReport("END_TKLR_T", counter, false);
        LOG.info("Total Tickler Next Due Dates updated in END_TKLR_T: " + counter);
    }
    
    /**
     * This method updates the next process dates in FeeMethod
     */
    protected void updateFeeProcessDates() {
        
        int counter = 0;
        List<FeeMethod> feeMethodRecords = feeMethodDao.getFeeMethodWithNextPayDateEqualToCurrentDate();
        if (feeMethodRecords != null) {
            for (FeeMethod feeMethod : feeMethodRecords) {                        
                String frequencyCode = feeMethod.getFeeFrequencyCode();           
                Date nextDate = frequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    feeMethod.setFeeLastProcessDate(feeMethod.getFeeNextProcessDate());
                    feeMethod.setFeeNextProcessDate(nextDate);
                    if (updateBusinessObject(feeMethod)) {
                        counter++;
                    } else {
                        LOG.error("Failed to update FeeMethod " + feeMethod.getCode());
                    }
                }
            }
        }
        generateReport("END_FEE_MTHD_T", counter, false);
        LOG.info("Total Fee Next Process Dates and Fee Last Process Dates updated in END_FEE_MTHD_T: " + counter);
    }
    
    /**
     * This method updates the next process dates in EndowmentRecurringCashTransfer
     */
    protected void updateRecurringCashTransferProcessDates() {
        
        int counter = 0;
        List<EndowmentRecurringCashTransfer> recurringCashTransferRecords = recurringCashTransferDao.getRecurringCashTransferWithNextPayDateEqualToCurrentDate();
        if (recurringCashTransferRecords != null) {
            for (EndowmentRecurringCashTransfer recurringCashTransfer : recurringCashTransferRecords) {                       
                String frequencyCode = recurringCashTransfer.getFrequencyCode();           
                Date nextDate = frequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    recurringCashTransfer.setLastProcessDate(recurringCashTransfer.getNextProcessDate());
                    recurringCashTransfer.setNextProcessDate(nextDate);
                    if (updateBusinessObject(recurringCashTransfer)) {
                        counter++;
                    } else {
                        LOG.error("Failed to update EndowmentRecurringCashTransfer " + recurringCashTransfer.getTransferNumber());
                    }
                }                
            }
        }
        generateReport("END_REC_CSH_XFR_T", counter, false);
        LOG.info("Total Next Process Dates and Last Process Dates updated in END_REC_CSH_XFR_T: " + counter);
    }
    
    protected void updateAutomatedCashInvestmentModelNextDueDates() {
        
        int counter = 0;
        List<AutomatedCashInvestmentModel> aciRecords = automatedCashInvestmentModelDao.getAutomatedCashInvestmentModelWithNextPayDateEqualToCurrentDate();
        if (aciRecords != null) {
            for (AutomatedCashInvestmentModel aci : aciRecords) {                        
                String frequencyCode = aci.getAciFrequencyCode();           
                Date nextDate = frequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    aci.setAciNextDueDate(nextDate);
                    if (updateBusinessObject(aci)) {
                        counter++;
                    } else {
                        LOG.error("Failed to update FeeMethod " + aci.getAciModelID());
                    }
                }
            }
        }
        generateReport("END_AUTO_CSH_INVEST_MDL_T", counter, false);
        LOG.info("Total ACI Next Due Dates updated in END_AUTO_CSH_INVEST_MDL_T: " + counter);
    }

    protected void updateCashSweepModelNextDueDates() {
        
        int counter = 0;
        List<CashSweepModel> csmRecords = cashSweepModelDao.getCashSweepModelWithNextPayDateEqualToCurrentDate();
        if (csmRecords != null) {
            for (CashSweepModel csm : csmRecords) {                        
                String frequencyCode = csm.getCashSweepFrequencyCode();           
                Date nextDate = frequencyCodeService.calculateProcessDate(frequencyCode); 
                if (nextDate != null) {
                    csm.setCashSweepNextDueDate(nextDate);
                    if (updateBusinessObject(csm)) {
                        counter++;
                    } else {
                        LOG.error("Failed to update FeeMethod " + csm.getCashSweepModelID());
                    }
                }
            }
        }
        generateReport("END_CSH_SWEEP_MDL_T", counter, false);
        LOG.info("Total Cash Sweep Model Next Due Dates updated in END_CSH_SWEEP_MDL_T: " + counter);
    }
    
    /**
     * Generates the statistic report for updated tables 
     * @param tableName
     * @param counter
     * @param isFirstReport
     */
    protected void generateReport(String tableName, int counter, boolean isFirstReport) {
        
        try {
            if (isFirstReport) {
                // write the title
                rollFrequencyDatesReportWriterService.writeSubTitle("<rollFrequencyDatesJob> Number of Records Updated");
                rollFrequencyDatesReportWriterService.writeNewLines(1);
            }
            rollFrequencyDatesReportWriterService.writeFormattedMessageLine(tableName + ": %s", counter);
            
        } catch (Exception e) {
            LOG.error("Failed to generate the statistic report: " + e.getMessage());
            rollFrequencyDatesReportWriterService.writeFormattedMessageLine("Failed to generate the statistic report: " + e.getMessage());
        }
    }
    
    /**
     * Updates business object
     * @param businessObject
     * @return boolean
     */
    protected boolean updateBusinessObject(PersistableBusinessObject businessObject) {
    
        boolean result = true;
        try {
            businessObjectService.save(businessObject);
        } catch (IllegalArgumentException e) {
            result = false;
        }
        
        return result;
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

    /**
     * Sets the rollFrequencyDatesReportWriterService attribute value.
     * @param rollFrequencyDatesReportWriterService The rollFrequencyDatesReportWriterService to set.
     */
    public void setRollFrequencyDatesReportWriterService(ReportWriterService rollFrequencyDatesReportWriterService) {
        this.rollFrequencyDatesReportWriterService = rollFrequencyDatesReportWriterService;
    }

    /**
     * Sets the automatedCashInvestmentModelDao attribute value.
     * @param automatedCashInvestmentModelDao The automatedCashInvestmentModelDao to set.
     */
    public void setAutomatedCashInvestmentModelDao(AutomatedCashInvestmentModelDao automatedCashInvestmentModelDao) {
        this.automatedCashInvestmentModelDao = automatedCashInvestmentModelDao;
    }

    /**
     * Sets the cashSweepModelDao attribute value.
     * @param cashSweepModelDao The cashSweepModelDao to set.
     */
    public void setCashSweepModelDao(CashSweepModelDao cashSweepModelDao) {
        this.cashSweepModelDao = cashSweepModelDao;
    }
    
    /**
     * Gets the frequencyCodeService attribute. 
     * @return Returns the frequencyCodeService.
     */
    protected FrequencyCodeService getFrequencyCodeService() {
        return frequencyCodeService;
    }

    /**
     * Sets the frequencyCodeService attribute value.
     * @param frequencyCodeService The frequencyCodeService to set.
     */
    public void setFrequencyCodeService(FrequencyCodeService frequencyCodeService) {
        this.frequencyCodeService = frequencyCodeService;
    }
}
