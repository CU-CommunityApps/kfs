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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.CreditCardStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseTypes;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService;
import org.kuali.kfs.module.tem.batch.service.TemBatchService;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.CreditCardImportData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.ObjectUtils;

public class CreditCardDataImportServiceImpl implements CreditCardDataImportService{
    
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditCardDataImportServiceImpl.class);
    private BatchInputFileService batchInputFileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private TemBatchService temBatchService;    
    private TemProfileService temProfileService;
    private TravelExpenseService travelExpenseService;
    
    private List<BatchInputFileType> creditCardDataImportFileTypes;
    private String creditCardDataFileErrorDirectory;

    @Override
    public boolean importCreditCardData() {
        boolean success = true;

        for (BatchInputFileType inputFileType : creditCardDataImportFileTypes) {
            List<String> inputFileNames = batchInputFileService.listInputFileNamesWithDoneFile(inputFileType);

            for (String dataFileName : inputFileNames) {
                success &= this.importCreditCardDataFile(dataFileName, inputFileType);
            }
        }

        return success;
    }

    @Override
    public boolean importCreditCardDataFile(String dataFileName, BatchInputFileType inputFileType) {
        String fileExtension = "." + inputFileType.getFileExtension();
        String doneFileName = temBatchService.getCompanionFileName(dataFileName, fileExtension, TemConstants.DONE_FILE_SUFFIX);
        File doneFile = temBatchService.getFileByAbsolutePath(doneFileName);

        try {
            FileInputStream fileContents = new FileInputStream(dataFileName);

            byte[] fileByteContent = IOUtils.toByteArray(fileContents);
            CreditCardImportData creditCardData = (CreditCardImportData) batchInputFileService.parse(inputFileType, fileByteContent);
            IOUtils.closeQuietly(fileContents);
            
            LOG.info("Credit Card Import - validating: " + dataFileName);
            
            List<CreditCardStagingData> validCreditCardList = this.validateCreditCardData(creditCardData, dataFileName);
                        
            boolean isAllValid = validCreditCardList.size() == creditCardData.getCreditCardData().size();
            if (!isAllValid) {
                String error = "The Credit Card data records to be loaded are rejected due to data problem. Please check the Credit Card data report.";
                throw new RuntimeException(error);
            }
            
            businessObjectService.save(validCreditCardList);
            
        }
        catch (Exception ex) {
            LOG.error("Failed to process the file : " + dataFileName, ex);
            
            temBatchService.moveErrorFile(dataFileName, this.getCreditCardDataFileErrorDirectory(), creditCardDataFileErrorDirectory);
            
            return false;
        }
        finally {
            boolean doneFileDeleted = doneFile.delete();
        }

        return true;
    }

    @Override
    public List<CreditCardStagingData> validateCreditCardData(CreditCardImportData creditCardList, String dataFileName) {
        List<CreditCardStagingData> validData = new ArrayList<CreditCardStagingData>();
        
        Integer count = 1;
        for(CreditCardStagingData creditCardData: creditCardList.getCreditCardData()){
            LOG.info("Validating credit card import. Record# " + count + " of " + creditCardList.getCreditCardData().size());
            creditCardData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_NO_ERROR);
            if(validateCreditCardAgency(creditCardData)){

                if(creditCardList.getImportBy().equals(ExpenseImportTypes.IMPORT_BY_TRAVELLER)){
                    TEMProfileAccount temProfileAccount  = findTraveler(creditCardData);
                    
                    if(ObjectUtils.isNull(temProfileAccount)){
                        LOG.error("Invalid Traveler in Credit Card Data record.");
                        creditCardData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_INVALID_CARD);
                    }
                    else{                        
                        //Set Traveler Id for UCD
                        if(ObjectUtils.isNull(creditCardData.getTravelerId()) || creditCardData.getTravelerId() == 0){
                            Integer travelerId = new Integer(temProfileAccount.getProfile().getEmployeeId()).intValue();
                            creditCardData.setTravelerId(travelerId);
                        }
                        
                        creditCardData.setTemProfileId(temProfileAccount.getProfileId());
                        
                        //Set expense type code to O-Other if null
                        if(creditCardData.getExpenseTypeCode() == null){
                            creditCardData.setExpenseTypeCode(ExpenseTypes.OTHER);
                        }

                        //Set Credit Card Key(traveler Id + Credit Card Agency + Credit Card number 
                        creditCardData.setCreditCardKey(creditCardData.getTravelerId() + temProfileAccount.getCreditCardAgency().getCreditCardOrAgencyCode()+ creditCardData.getCreditCardNumber());
                        
                        // need to do the duplicate check at this point, since the CC key is one of the fields being checked
                        if (!isDuplicate(creditCardData)) {
                            creditCardData.setMoveToHistoryIndicator("Y");
                            creditCardData.setImportBy(creditCardList.getImportBy());
                            creditCardData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
                            validData.add(creditCardData);
                        }
                        
                    }
                }
                
                if(creditCardList.getImportBy().equals(ExpenseImportTypes.IMPORT_BY_TRIP)){
                    if (!isDuplicate(creditCardData)) {
                        creditCardData.setImportBy(creditCardList.getImportBy());
                        creditCardData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
                        validData.add(creditCardData);
                    }
                }
            }
            LOG.info("Finished validating credit card data record.");
            count++;
        }

        return validData;
    }
    
    @Override
    public boolean isDuplicate(CreditCardStagingData creditCardData){
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        
        if(StringUtils.isNotEmpty(creditCardData.getCreditCardKey())){
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_KEY, creditCardData.getCreditCardKey());
        }
        if(StringUtils.isNotEmpty(creditCardData.getReferenceNumber())){
            fieldValues.put(TemPropertyConstants.REFERENCE_NUMBER, creditCardData.getReferenceNumber());
        }
        if(ObjectUtils.isNotNull(creditCardData.getTransactionAmount())){
            fieldValues.put(TemPropertyConstants.TRANSACTION_AMOUNT, creditCardData.getTransactionAmount());
        }
        if(ObjectUtils.isNotNull(creditCardData.getTransactionDate())){
            fieldValues.put(TemPropertyConstants.TRANSACTION_DATE, creditCardData.getTransactionDate());
        }
        if(ObjectUtils.isNotNull(creditCardData.getBankPostDate())){
            fieldValues.put(TemPropertyConstants.BANK_POSTED_DATE, creditCardData.getBankPostDate());
        }
        if(StringUtils.isNotEmpty(creditCardData.getMerchantName())){
            fieldValues.put(TemPropertyConstants.MERCHANT_NAME, creditCardData.getMerchantName());
        }
        //TODO: Posting amount is missing in staging
        /*if(StringUtils.isNotEmpty(creditCardData.getBankPostAmount())){
            fieldValues.put(TemPropertyConstants.BANK_POST_AMOUNT, creditCardData.getBankPostAmount());
        }*/
        
        List<CreditCardStagingData> creditCardDataList = (List<CreditCardStagingData>) businessObjectService.findMatching(CreditCardStagingData.class, fieldValues);
        
        if (ObjectUtils.isNull(creditCardDataList) || creditCardDataList.size() == 0) {
            return false;
        }
        LOG.error("Found a duplicate entry for credit card. Matching credit card id: " + creditCardDataList.get(0).getId());
        return true;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#findTraveler(org.kuali.kfs.module.tem.businessobject.CreditCardStagingData)
     */
    @Override
    public TEMProfileAccount findTraveler(CreditCardStagingData creditCardData){
        Map<String,String> criteria = new HashMap<String,String>(1);        
        criteria.put(TemPropertyConstants.ACCOUNT_NUMBER, creditCardData.getCreditCardNumber());
        
        Collection<TEMProfileAccount> temProfileAccounts = getBusinessObjectService().findMatching(TEMProfileAccount.class, criteria);
        
        if(ObjectUtils.isNotNull(temProfileAccounts) && temProfileAccounts.size() > 0) {
            return temProfileAccounts.iterator().next();
        }
        
        return null;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#validateCreditCardAgency(org.kuali.kfs.module.tem.businessobject.CreditCardStagingData)
     */
    @Override
    public boolean validateCreditCardAgency(CreditCardStagingData creditCardData){
        CreditCardAgency ccAgency = getTravelExpenseService().getCreditCardAgency(creditCardData.getCreditCardOrAgencyCode());
        if (ObjectUtils.isNull(ccAgency)) {
            LOG.error("Mandatory Field Credit Card Or Agency Code is invalid: " + creditCardData.getCreditCardOrAgencyCode());
            creditCardData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_INVALID_CC_AGENCY);
            return false;
        }
        
        return true;
    }
    
    /**
     * @see org.kuali.kfs.module.tem.batch.service.CreditCardDataImportService#moveCreditCardDataToHistoricalExpenseTable()
     */
    @Override
    public boolean moveCreditCardDataToHistoricalExpenseTable() {
        List<CreditCardStagingData> creditCardData = travelExpenseService.retrieveValidCreditCardData();
        
        if (ObjectUtils.isNotNull(creditCardData) && creditCardData.size() > 0) {            
            for(CreditCardStagingData creditCard: creditCardData){
                LOG.info("Creating historical travel expense for credit card: " + creditCard.getId());
                HistoricalTravelExpense expense = travelExpenseService.createHistoricalTravelExpense(creditCard);
                businessObjectService.save(expense);
                
                //Mark as moved to historical
                creditCard.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_MOVED_TO_HISTORICAL);
                LOG.info("Finished creating historical travel expense for credit card: " + creditCard.getId() + " Historical Travel Expense: " + expense.getId());
            }
            businessObjectService.save(creditCardData);
        }        
        
        return true;
    }
    
    /**
     * Gets the batchInputFileService attribute. 
     * @return Returns the batchInputFileService.
     */
    public BatchInputFileService getBatchInputFileService() {
        return batchInputFileService;
    }

    /**
     * Sets the batchInputFileService attribute value.
     * @param batchInputFileService The batchInputFileService to set.
     */
    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    /**
     * Gets the creditCardDataImportFileTypes attribute. 
     * @return Returns the creditCardDataImportFileTypes.
     */
    public List<BatchInputFileType> getCreditCardDataImportFileTypes() {
        return creditCardDataImportFileTypes;
    }

    /**
     * Sets the creditCardDataImportFileTypes attribute value.
     * @param creditCardDataImportFileTypes The creditCardDataImportFileTypes to set.
     */
    public void setCreditCardDataImportFileTypes(List<BatchInputFileType> creditCardDataImportFileTypes) {
        this.creditCardDataImportFileTypes = creditCardDataImportFileTypes;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the creditCardDataFileErrorDirectory attribute. 
     * @return Returns the creditCardDataFileErrorDirectory.
     */
    public String getCreditCardDataFileErrorDirectory() {
        return creditCardDataFileErrorDirectory;
    }

    /**
     * Sets the creditCardDataFileErrorDirectory attribute value.
     * @param creditCardDataFileErrorDirectory The creditCardDataFileErrorDirectory to set.
     */
    public void setCreditCardDataFileErrorDirectory(String creditCardDataFileErrorDirectory) {
        this.creditCardDataFileErrorDirectory = creditCardDataFileErrorDirectory;
    }
    
    /**
     * Gets the temBatchService attribute. 
     * @return Returns the temBatchService.
     */
    public TemBatchService getTemBatchService() {
        return temBatchService;
    }

    /**
     * Sets the temBatchService attribute value.
     * @param temBatchService The temBatchService to set.
     */
    public void setTemBatchService(TemBatchService temBatchService) {
        this.temBatchService = temBatchService;
    }
    

    /**
     * Gets the temProfileService attribute. 
     * @return Returns the temProfileService.
     */
    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    /**
     * Sets the temProfileService attribute value.
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public TravelExpenseService getTravelExpenseService(){
        return this.travelExpenseService;
    }
    
    /**
     * 
     * This method...
     * @param argTravelExpenseService
     */
    public void setTravelExpenseService(TravelExpenseService argTravelExpenseService){
        this.travelExpenseService = argTravelExpenseService;
    }
    
    /**
     * Gets the dateTimeService attribute. 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
