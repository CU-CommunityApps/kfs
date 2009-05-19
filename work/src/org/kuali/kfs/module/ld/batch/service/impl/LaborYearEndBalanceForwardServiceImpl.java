/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.batch.service.impl;

import static org.kuali.kfs.module.ld.LaborConstants.DestinationNames.LEDGER_BALANCE;
import static org.kuali.kfs.module.ld.LaborConstants.DestinationNames.ORIGN_ENTRY;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.report.Summary;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.LaborConstants.YearEnd;
import org.kuali.kfs.module.ld.batch.LaborYearEndBalanceForwardStep;
import org.kuali.kfs.module.ld.batch.service.LaborReportService;
import org.kuali.kfs.module.ld.batch.service.LaborYearEndBalanceForwardService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerBalanceForYearEndBalanceForward;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.util.DebitCreditUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.transaction.annotation.Transactional;

/**
 * Labor Ledger Year End � Inception to Date Beginning Balance process moves the Year-to-Date Total plus the Contracts and Grants
 * Beginning Balances to the Contracts and Grants Beginning Balances of the new fiscal year for a designated group of accounts (to
 * be identified by fund group and sub fund group).
 */
@Transactional
public class LaborYearEndBalanceForwardServiceImpl implements LaborYearEndBalanceForwardService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborYearEndBalanceForwardServiceImpl.class);

    private LaborLedgerBalanceService laborLedgerBalanceService;
    private OriginEntryGroupService originEntryGroupService;
    private LaborOriginEntryService laborOriginEntryService;

    private OptionsService optionsService;

    private BusinessObjectService businessObjectService;
    private LaborReportService laborReportService;
    private DateTimeService dateTimeService;
    private ParameterService parameterService;
    private String batchFileDirectoryName;

    private final static int LINE_INTERVAL = 2;

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborYearEndBalanceForwardService#forwardBalance()
     */
    public void forwardBalance() {
        Integer fiscalYear = Integer.valueOf(parameterService.getParameterValue(LaborYearEndBalanceForwardStep.class, YearEnd.OLD_FISCAL_YEAR));
        this.forwardBalance(fiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborYearEndBalanceForwardService#forwardBalance(java.lang.Integer)
     */
    public void forwardBalance(Integer fiscalYear) {
        forwardBalance(fiscalYear, fiscalYear + 1);
    }

    /**
     * @see org.kuali.kfs.module.ld.batch.service.LaborYearEndBalanceForwardService#forwardBalance(java.lang.Integer, java.lang.Integer)
     */
    public void forwardBalance(Integer fiscalYear, Integer newFiscalYear) {
        Date runDate = dateTimeService.getCurrentSqlDate();

        List<Summary> reportSummary = new ArrayList<Summary>();
        Map<Transaction, List<Message>> errorMap = new HashMap<Transaction, List<Message>>();
        //OriginEntryGroup validGroup = originEntryGroupService.createGroup(runDate, LABOR_YEAR_END_BALANCE_FORWARD, true, false, true);
        String balanceForwardsFileName = LaborConstants.BatchFileSystem.BALANCE_FORWARDS_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear.toString());

        int numberOfBalance = businessObjectService.countMatching(LedgerBalance.class, fieldValues);
        int numberOfSelectedBalance = this.processLedgerBalances(fiscalYear, newFiscalYear, balanceForwardsFileName, errorMap, runDate);

        Summary.updateReportSummary(reportSummary, LEDGER_BALANCE, KFSConstants.OperationType.READ, numberOfBalance, 0);
        Summary.updateReportSummary(reportSummary, LEDGER_BALANCE, KFSConstants.OperationType.SELECT, numberOfSelectedBalance, 0);
        Summary.updateReportSummary(reportSummary, LEDGER_BALANCE, KFSConstants.OperationType.REPORT_ERROR, errorMap.size(), 0);
        reportSummary.add(new Summary(reportSummary.size() + LINE_INTERVAL, "", 0));
        Summary.updateReportSummary(reportSummary, ORIGN_ENTRY, KFSConstants.OperationType.INSERT, numberOfSelectedBalance, 0);

        laborReportService.generateStatisticsReport(reportSummary, errorMap, SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY), runDate);
        
        //TODO: Shawn - report
        //laborReportService.generateOutputSummaryReport(balanceForwardsFileName, ReportRegistry.LABOR_YEAR_END_OUTPUT, reportsDirectory, runDate);
    }

    /**
     * post the qualified balances into origin entry table for the further labor ledger processing
     * 
     * @param balanceIterator the given ledger balances that will be carried forward
     * @param newFiscalYear the new fiscal year
     * @param validGroup the group that the posted transaction belongs to
     * @param errorMap the map that records the error messages
     * @param runDate the date the transaction is posted
     * @return the number of qualified balances
     */
    private int processLedgerBalances(Integer fiscalYear, Integer newFiscalYear, String balanceForwardsFileName, Map<Transaction, List<Message>> errorMap, Date runDate) {
        SystemOptions options = optionsService.getOptions(fiscalYear);

        List<String> processableBalanceTypeCodes = this.getProcessableBalanceTypeCode(options);
        List<String> processableObjectTypeCodes = this.getProcessableObjectTypeCodes(options);
        List<String> subFundGroupCodes = this.getSubFundGroupProcessed();
        List<String> fundGroupCodes = this.getFundGroupProcessed();

        //create files
        File balanceForwardsFile = new File(batchFileDirectoryName + File.separator + balanceForwardsFileName);
        PrintStream balanceForwardsPs = null;
        
        try {
            balanceForwardsPs = new PrintStream(balanceForwardsFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("balanceForwardsFile Files doesn't exist " + balanceForwardsFileName);
        }
        
        
        // process the selected balances by balance type and object type
        Map<String, String> fieldValues = new HashMap<String, String>();
        int numberOfSelectedBalance = 0;
        for (String balanceTypeCode : processableBalanceTypeCodes) {
            fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypeCode);

            for (String objectTypeCode : processableObjectTypeCodes) {
                fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, objectTypeCode);

                fieldValues.remove(LaborConstants.ACCOUNT_FIELDS[0]);
                fieldValues.remove(LaborConstants.ACCOUNT_FIELDS[1]);
                List<List<String>> accounts = laborLedgerBalanceService.findAccountsInFundGroups(fiscalYear, fieldValues, subFundGroupCodes, fundGroupCodes);

                for (List<String> account : accounts) {
                    fieldValues.put(LaborConstants.ACCOUNT_FIELDS[0], account.get(0));
                    fieldValues.put(LaborConstants.ACCOUNT_FIELDS[1], account.get(1));

                    Iterator<LedgerBalanceForYearEndBalanceForward> balanceIterator = laborLedgerBalanceService.findBalancesForFiscalYear(fiscalYear, fieldValues, subFundGroupCodes, fundGroupCodes);
                    numberOfSelectedBalance += postSelectedBalancesAsOriginEntries(balanceIterator, newFiscalYear, balanceForwardsPs, errorMap, runDate);
                }
            }
        }
        
        balanceForwardsPs.close();
        return numberOfSelectedBalance;
    }

    /**
     * post the qualified balances into origin entry table for the further labor ledger processing
     * 
     * @param balanceIterator the given ledger balances that will be carried forward
     * @param newFiscalYear the new fiscal year
     * @param validGroup the group that the posted transaction belongs to
     * @param errorMap the map that records the error messages
     * @param runDate the date the transaction is posted
     * @return the number of qualified balances
     */
    private int postSelectedBalancesAsOriginEntries(Iterator<LedgerBalanceForYearEndBalanceForward> balanceIterator, Integer newFiscalYear, PrintStream balanceForwardsPs, Map<Transaction, List<Message>> errorMap, Date runDate) {
        int numberOfSelectedBalance = 0;
        String description = this.getDescription();
        String originationCode = this.getOriginationCode();
        String documentTypeCode = this.getDocumentTypeCode();

        while (balanceIterator != null && balanceIterator.hasNext()) {
            LedgerBalanceForYearEndBalanceForward balance = balanceIterator.next();
            List<Message> errors = null;

            boolean isValidBalance = validateBalance(balance, errors);
            LaborOriginEntry laborOriginEntry = new LaborOriginEntry();
            if (isValidBalance) {
                //laborOriginEntry.setEntryGroupId(validGroup.getId());
                laborOriginEntry.setUniversityFiscalYear(newFiscalYear);
                laborOriginEntry.setFinancialDocumentTypeCode(documentTypeCode);
                laborOriginEntry.setFinancialSystemOriginationCode(originationCode);
                laborOriginEntry.setTransactionLedgerEntryDescription(description);

                this.postAsOriginEntry(balance, laborOriginEntry, balanceForwardsPs, runDate);
                numberOfSelectedBalance++;
            }
            else if (errors != null && !errors.isEmpty()) {
                ObjectUtil.buildObject(laborOriginEntry, balance);
                errorMap.put(laborOriginEntry, errors);
            }
            laborOriginEntry = null;
            balance = null;
        }
        return numberOfSelectedBalance;
    }

    /**
     * determine if the given balance is qualified to be carried forward to new fiscal year
     * 
     * @param balance the given ledger balance that could be carried forward
     * @param errors the error list that is updated if the given balacne is not qualified for carry forward
     * @return true if the balance is qualified; otherwise, false
     */
    private boolean validateBalance(LedgerBalanceForYearEndBalanceForward balance, List<Message> errors) {
        /** This is the placeholder for addtional business rule validation. The former rules were moved down to data access layer. * */
        return true;
    }

    /**
     * post the qualified balance into origin entry table for the further labor ledger processing
     * 
     * @param balance the given ledger balance that will be carried forward
     * @param newFiscalYear the new fiscal year
     * @param validGroup the group that the posted transaction belongs to
     * @param postingDate the date the transaction is posted
     */
    private void postAsOriginEntry(LedgerBalanceForYearEndBalanceForward balance, LaborOriginEntry originEntry, PrintStream balanceForwardsPs, Date postingDate) {
        try {
            originEntry.setAccountNumber(balance.getAccountNumber());
            originEntry.setChartOfAccountsCode(balance.getChartOfAccountsCode());
            originEntry.setSubAccountNumber(balance.getSubAccountNumber());
            originEntry.setFinancialObjectCode(balance.getFinancialObjectCode());
            originEntry.setFinancialSubObjectCode(balance.getFinancialSubObjectCode());
            originEntry.setFinancialBalanceTypeCode(balance.getFinancialBalanceTypeCode());
            originEntry.setFinancialObjectTypeCode(balance.getFinancialObjectTypeCode());

            originEntry.setPositionNumber(balance.getPositionNumber());
            originEntry.setEmplid(balance.getEmplid());
            originEntry.setDocumentNumber(balance.getFinancialBalanceTypeCode() + balance.getAccountNumber());

            originEntry.setProjectCode(KFSConstants.getDashProjectCode());
            originEntry.setUniversityFiscalPeriodCode(KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE);

            KualiDecimal transactionAmount = balance.getAccountLineAnnualBalanceAmount();
            transactionAmount = transactionAmount.add(balance.getContractsGrantsBeginningBalanceAmount());

            originEntry.setTransactionLedgerEntryAmount(transactionAmount.abs());
            originEntry.setTransactionDebitCreditCode(DebitCreditUtil.getDebitCreditCode(transactionAmount, false));

            originEntry.setTransactionLedgerEntrySequenceNumber(null);
            originEntry.setTransactionTotalHours(BigDecimal.ZERO);
            originEntry.setTransactionDate(postingDate);

            //laborOriginEntryService.save(originEntry);
            
            try {
                balanceForwardsPs.printf("%s\n", originEntry.getLine());
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }
        }
        catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * get the fund group codes that are acceptable by year-end process
     * 
     * @return the fund group codes that are acceptable by year-end process
     */
    private List<String> getFundGroupProcessed() {
        return parameterService.getParameterValues(LaborYearEndBalanceForwardStep.class, YearEnd.FUND_GROUP_PROCESSED);
    }

    /**
     * get the fund group codes that are acceptable by year-end process
     * 
     * @return the fund group codes that are acceptable by year-end process
     */
    private List<String> getSubFundGroupProcessed() {
        return parameterService.getParameterValues(LaborYearEndBalanceForwardStep.class, YearEnd.SUB_FUND_GROUP_PROCESSED);
    }

    /**
     * get the balance type codes that are acceptable by year-end process
     * 
     * @return the balance type codes that are acceptable by year-end process
     */
    private List<String> getProcessableBalanceTypeCode(SystemOptions options) {
        List<String> processableBalanceTypeCodes = new ArrayList<String>();
        processableBalanceTypeCodes.add(options.getActualFinancialBalanceTypeCd());
        return processableBalanceTypeCodes;
    }

    /**
     * get the object type codes that are acceptable by year-end process
     * 
     * @param options the given system options
     * @return the object type codes that are acceptable by year-end process
     */
    private List<String> getProcessableObjectTypeCodes(SystemOptions options) {
        List<String> processableObjectTypeCodes = new ArrayList<String>();

        processableObjectTypeCodes.add(options.getFinObjTypeExpenditureexpCd());
        processableObjectTypeCodes.add(options.getFinObjTypeExpNotExpendCode());

        return processableObjectTypeCodes;
    }

    /**
     * get the document type code of the transaction posted by year-end process
     * 
     * @return the document type code of the transaction posted by year-end process
     */
    private String getDocumentTypeCode() {
        return parameterService.getParameterValue(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, GeneralLedgerConstants.ANNUAL_CLOSING_DOCUMENT_TYPE);
    }

    /**
     * get the origination code of the transaction posted by year-end process
     * 
     * @return the origination code of the transaction posted by year-end process
     */
    private String getOriginationCode() {
        return parameterService.getParameterValue(LaborYearEndBalanceForwardStep.class, YearEnd.ORIGINATION_CODE);
    }

    /**
     * get the description of the transaction posted by year-end process
     * 
     * @return the description of the transaction posted by year-end process
     */
    private String getDescription() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(LaborKeyConstants.MESSAGE_YEAR_END_TRANSACTION_DESCRIPTON);
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * 
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the laborLedgerBalanceService attribute value.
     * 
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
    }

    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * Sets the laborReportService attribute value.
     * 
     * @param laborReportService The laborReportService to set.
     */
    public void setLaborReportService(LaborReportService laborReportService) {
        this.laborReportService = laborReportService;
    }

    /**
     * Sets the laborOriginEntryService attribute value.
     * 
     * @param laborOriginEntryService The laborOriginEntryService to set.
     */
    public void setLaborOriginEntryService(LaborOriginEntryService laborOriginEntryService) {
        this.laborOriginEntryService = laborOriginEntryService;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
