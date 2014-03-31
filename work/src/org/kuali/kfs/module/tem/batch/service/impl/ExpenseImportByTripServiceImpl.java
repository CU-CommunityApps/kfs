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


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataValidation;
import org.kuali.kfs.module.tem.TemConstants.CreditCardStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.AgencyDataImportStep;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AgencyServiceFee;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelEncumbranceService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for handling imported expenses using the IU method or "By Trip Id" method.
 *
 * @see org.kuali.kfs.module.tem.document.validation.impl.AgencyStagingDataValidation
 */
@Transactional
public class ExpenseImportByTripServiceImpl extends ExpenseImportServiceBase implements ExpenseImportByTripService  {

    public static Logger LOG = Logger.getLogger(ExpenseImportByTripServiceImpl.class);

    private TravelAuthorizationService travelAuthorizationService;
    private TemProfileService temProfileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private TravelExpenseService travelExpenseService;
    private ImportedExpensePendingEntryService importedExpensePendingEntryService;
    private TravelDocumentService travelDocumentService;
    private TravelEncumbranceService travelEncumbranceService;

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#areMandatoryFieldsPresent(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateMandatoryFieldsPresent(AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        if (StringUtils.isEmpty(agencyData.getTripId())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_ID);
            errorMessages.add(error);
        }

        errorMessages.addAll(validateMissingAccountingInfo(agencyData));

        if (isAmountEmpty(agencyData.getTripExpenseAmount())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_EXPENSE_AMOUNT);
            errorMessages.add(error);
        }
        if (StringUtils.isEmpty(agencyData.getTripInvoiceNumber())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_INVOICE_NUMBER);
            errorMessages.add(error);
        }
        if (ObjectUtils.isNull(agencyData.getTransactionPostingDate())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRANSACTION_POSTING_DATE);
            errorMessages.add(error);
        }
        if (isTripDataMissing(agencyData)) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_AIR_LODGING_RENTAL_MISSING);
            errorMessages.add(error);
        }

        if (!errorMessages.isEmpty()) {
            LOG.error("Missing one or more required fields.");
        }

        return errorMessages;
    }

    /**
     *
     * This method loops through the {@link TripAccountingInformation} and
     * verifies that each one has a Chart Code and Account Number.
     * @param agencyData
     * @return
     */
    @Override
    public List<ErrorMessage> validateMissingAccountingInfo(AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        List<TripAccountingInformation> accountingInfoList = agencyData.getTripAccountingInformation();

        if (accountingInfoList.isEmpty()) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_REQUIRED_ACCOUNT_INFO);
            errorMessages.add(error);
        }

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAgencyData(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateAgencyData(AgencyStagingData agencyData) {
        LOG.info("Validating agency data. tripId: "+ agencyData.getTripId());

        List<ErrorMessage> errorMessages = validateMandatoryFieldsPresent(agencyData);
        if (!errorMessages.isEmpty()) {
            agencyData.setErrorCode(TemConstants.AgencyStagingDataErrorCodes.AGENCY_REQUIRED_FIELDS);
            return errorMessages;
        }

        errorMessages = validateDuplicateData(agencyData);
        if (!errorMessages.isEmpty()) {
            agencyData.setErrorCode(TemConstants.AgencyStagingDataErrorCodes.AGENCY_DUPLICATE_DATA);
            return errorMessages;
        }

        agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);

        errorMessages = validateTripId(agencyData);

        errorMessages.addAll(validateAccountingInfo(agencyData));

        errorMessages.addAll(validateCreditCardAgency(agencyData));

        errorMessages.addAll(validateDistributionCode(agencyData));

        LOG.info("Finished validating agency data. tripId:"+ agencyData.getTripId());
        agencyData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
        if (ObjectUtils.isNull(agencyData.getCreationTimestamp())) {
            agencyData.setCreationTimestamp(dateTimeService.getCurrentTimestamp());
        }
        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateTripId(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateTripId(AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        TravelDocument travelDocument = getTravelDocumentService().getParentTravelDocument(agencyData.getTripId());
        if (ObjectUtils.isNotNull(travelDocument)) {
            return errorMessages;
        }

        LOG.error("Unable to retrieve a travel document for tripId: "+ agencyData.getTripId());
        setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_TRIPID);
        errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRIP_ID));

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAccountingInfo(org.kuali.kfs.module.tem.businessobject.TemProfile, org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateAccountingInfo(AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        // Get ACCOUNTING_LINE_VALIDATION parameter to determine which fields to validate
        Collection<String> validationParameters = getParameterService().getParameterValuesAsString(AgencyDataImportStep.class, TravelParameters.ACCOUNTING_LINE_VALIDATION);
        //don't need to validate any accounting information
        if (ObjectUtils.isNull(validationParameters)) {
            return errorMessages;
        }

        List<TripAccountingInformation> accountingInfo = agencyData.getTripAccountingInformation();
        for (TripAccountingInformation account : accountingInfo) {

            errorMessages.addAll(validateAccountingInfoLine(agencyData, account, validationParameters).values());
        }

        return errorMessages;
    }

    /**
     * This method is called by the AgencyStagingData maintainable for validating the new accounting line.
     * It won't need to set the error codes on the agency staging data object
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAccountingInfo(org.kuali.kfs.module.tem.businessobject.TripAccountingInformation)
     */
    @Override
    public Map<String,ErrorMessage> validateAccountingInfoLine(TripAccountingInformation accountingLine) {
        return validateAccountingInfoLine(null, accountingLine, null);
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAccountingInfo(org.kuali.kfs.module.tem.businessobject.TripAccountingInformation)
     */
    protected Map<String,ErrorMessage> validateAccountingInfoLine(AgencyStagingData agencyData, TripAccountingInformation accountingLine, Collection<String> validationParameters) {
        Map<String,ErrorMessage> errorMap = new HashMap<String,ErrorMessage>();

        if (ObjectUtils.isNull(validationParameters)) {
            // Get ACCOUNTING_LINE_VALIDATION parameter to determine which fields to validate
            validationParameters = getParameterService().getParameterValuesAsString(AgencyDataImportStep.class, TravelParameters.ACCOUNTING_LINE_VALIDATION);

            //don't need to validate any accounting information
            if (ObjectUtils.isNull(validationParameters)) {
                return errorMap;
            }
        }

        boolean setAgencyDataErrorCode = false;

        String tripId = "";
        if (ObjectUtils.isNotNull(agencyData)) {
            setAgencyDataErrorCode = true;
            tripId = agencyData.getTripId();
        }

        ErrorMessage error = null;

        if (validationParameters.contains(AgencyStagingDataValidation.VALIDATE_ACCOUNT)) {
            if (!isAccountNumberValid(accountingLine.getTripChartCode(), accountingLine.getTripAccountNumber())) {

                if (setAgencyDataErrorCode) {
                    LOG.error("Invalid Account in Agency Data record. tripId: "+ tripId + " chart code: " + accountingLine.getTripChartCode() + " account: " + accountingLine.getTripAccountNumber());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT);
                }

                error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCOUNT_NUM, accountingLine.getTripChartCode(), accountingLine.getTripAccountNumber());
                errorMap.put(TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_ACCOUNT_NUMBER, error);
            }
        }

        if (validationParameters.contains(AgencyStagingDataValidation.VALIDATE_SUBACCOUNT)) {
            // sub account is optional
            if (StringUtils.isNotEmpty(accountingLine.getTripSubAccountNumber()) &&
                !isSubAccountNumberValid(accountingLine.getTripChartCode(), accountingLine.getTripAccountNumber(), accountingLine.getTripSubAccountNumber())) {

                if (setAgencyDataErrorCode) {
                    LOG.error("Invalid SubAccount in Agency Data record. tripId: "+ tripId + " chart code: " + accountingLine.getTripChartCode() + " account: " + accountingLine.getTripAccountNumber() + " subaccount: " + accountingLine.getTripSubAccountNumber());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT);
                }
                error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUBACCOUNT, accountingLine.getTripSubAccountNumber());
                errorMap.put(TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_SUBACCOUNT_NUMBER, error);
            }
        }

        // project code is optional
        if (StringUtils.isNotEmpty(accountingLine.getProjectCode()) &&
            !isProjectCodeValid(accountingLine.getProjectCode())) {

            if (setAgencyDataErrorCode) {
                LOG.error("Invalid Project in Agency Data record. tripId: "+ tripId + " project code: " + accountingLine.getProjectCode());
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT);
            }
            error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_PROJECT_CODE, accountingLine.getProjectCode());
            errorMap.put(TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_PROJECT_CODE, error);
        }

        return errorMap;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#isDuplicate(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateDuplicateData(AgencyStagingData agencyData) {

        //make sure all the mandatory fields are present before checking for duplicates
        List<ErrorMessage> errorMessages = validateMandatoryFieldsPresent(agencyData);
        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        List<AgencyStagingData> agencyDataList = checkForDuplicates(agencyData);
        if (ObjectUtils.isNotNull(agencyDataList) && !agencyDataList.isEmpty()) {

            boolean isDuplicate = false;
            String errorMessage = "Found a duplicate entry for Agency Staging Data: Duplicate Ids ";

            for(AgencyStagingData duplicate : agencyDataList) {
                Integer duplicateId = duplicate.getId();
                if (ObjectUtils.isNotNull(agencyData.getId()) && (agencyData.getId().intValue() != duplicateId.intValue())) {
                    errorMessage += duplicate.getId() +" ";
                    isDuplicate = true;
                }
            }
            if (isDuplicate) {
                LOG.error(errorMessage);

                ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_TRIP_DUPLICATE_RECORD, agencyData.getTripId(), agencyData.getAgency(),
                        agencyData.getTransactionPostingDate().toString(), agencyData.getTripExpenseAmount().toString(), agencyData.getItineraryDataString());
                errorMessages.add(error);
            }
        }

        return errorMessages;
    }

    protected List<AgencyStagingData> checkForDuplicates(AgencyStagingData agencyStagingData) {
        // Verify that this isn't a duplicate entry based on the following:
        // Trip ID, Ticket Number or Itinerary Number, Agency Name, Transaction Date, Transaction Amount

        Map<String, Object> fieldValues = new HashMap<String, Object>();

        if (StringUtils.isNotEmpty(agencyStagingData.getTripId())) {
            fieldValues.put(TemPropertyConstants.TRIP_ID, agencyStagingData.getTripId());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getCreditCardOrAgencyCode())) {
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, agencyStagingData.getCreditCardOrAgencyCode());
        }
        if (ObjectUtils.isNotNull(agencyStagingData.getTransactionPostingDate())) {
            fieldValues.put(TemPropertyConstants.TRANSACTION_POSTING_DATE, agencyStagingData.getTransactionPostingDate());
        }
        if (ObjectUtils.isNotNull(agencyStagingData.getTripExpenseAmount())) {
            fieldValues.put(TemPropertyConstants.TRIP_EXPENSE_AMOUNT, agencyStagingData.getTripExpenseAmount());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getAirTicketNumber())) {
            fieldValues.put(TemPropertyConstants.AIR_TICKET_NUMBER, agencyStagingData.getAirTicketNumber());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getLodgingItineraryNumber())) {
            fieldValues.put(TemPropertyConstants.LODGING_ITINERARY_NUMBER, agencyStagingData.getLodgingItineraryNumber());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getRentalCarItineraryNumber())) {
            fieldValues.put(TemPropertyConstants.RENTAL_CAR_ITINERARY_NUMBER, agencyStagingData.getRentalCarItineraryNumber());
        }

        List<AgencyStagingData> agencyDataList = (List<AgencyStagingData>) businessObjectService.findMatching(AgencyStagingData.class, fieldValues);
        return agencyDataList;
    }

    @Override
    public List<ErrorMessage> validateCreditCardAgency(AgencyStagingData agencyData) {
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        if (!isCreditCardAgencyValid(agencyData)) {
            //setErrorCode is already done in isCreditCardAgencyValid()
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_CREDIT_CARD_DATA_INVALID_CCA, agencyData.getCreditCardOrAgencyCode()));
        }

        return errorMessages;
    }

    @Override
    public List<ErrorMessage> validateDistributionCode(AgencyStagingData agencyData) {
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        String distributionCode = agencyData.getDistributionCode();
        if (ObjectUtils.isNotNull(distributionCode)) {
            AgencyServiceFee serviceFee = getAgencyServiceFee(distributionCode);
            if (ObjectUtils.isNull(serviceFee)) {
                LOG.error("Invalid Distribution Code: "+ distributionCode);
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_DI_CD);
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_DISTRIBUTION_CODE, distributionCode));
            }
        }

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#reconciliateExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @SuppressWarnings("null")
    @Override
    public List<ErrorMessage> reconciliateExpense(AgencyStagingData agencyData, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        LOG.info("Reconciling expense for agency data: "+ agencyData.getId()+ " tripId: "+ agencyData.getTripId());

        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

        //only reconcile the record if it is active
        if (agencyData.isActive()) {

            if (AgencyStagingDataErrorCodes.AGENCY_NO_ERROR.equals(agencyData.getErrorCode())) {

                TemConstants.ExpenseTypeMetaCategory expenseTypeCategory = agencyData.getExpenseTypeCategory();

                // This is the "match process" - see if there's credit card data that matches the agency data
                CreditCardStagingData ccData = null;
                if (expenseTypeCategory == TemConstants.ExpenseTypeMetaCategory.AIRFARE) {
                    // see if there's a CC that matches ticket number, service fee number, amount
                    ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getAirTicketNumber(), agencyData.getAirServiceFeeNumber());
                }
                else if (expenseTypeCategory == TemConstants.ExpenseTypeMetaCategory.LODGING) {
                    // see if there's a CC that matches lodging itinerary number and amount
                    ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getLodgingItineraryNumber());
                }
                else if (expenseTypeCategory == TemConstants.ExpenseTypeMetaCategory.RENTAL_CAR) {
                    // see if there's a CC that matches rental car itinerary number and amount
                    ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getRentalCarItineraryNumber());
                }

                if (ObjectUtils.isNotNull(ccData)) {
                    LOG.info("Found a match for Agency: "+ agencyData.getId()+ " Credit Card: "+ ccData.getId()+ " tripId: "+ agencyData.getTripId());

                    final TravelDocument travelDocument = getTravelDocumentService().getParentTravelDocument(agencyData.getTripId());

                    ExpenseTypeObjectCode travelExpenseType = getTravelExpenseType(expenseTypeCategory, travelDocument);
                    if (travelExpenseType != null) {

                        if (isAccountingLinesMatch(agencyData)) {
                            List<TripAccountingInformation> accountingInfo = agencyData.getTripAccountingInformation();

                            HistoricalTravelExpense expense = travelExpenseService.createHistoricalTravelExpense(agencyData, ccData, travelExpenseType);
                            AgencyServiceFee serviceFee = getAgencyServiceFee(agencyData.getDistributionCode());

                            List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();

                            // Need to split up the amounts if there are multiple accounts
                            KualiDecimal remainingAmount = agencyData.getTripExpenseAmount();
                            KualiDecimal numAccounts = new KualiDecimal(accountingInfo.size());
                            KualiDecimal currentAmount = agencyData.getTripExpenseAmount().divide(numAccounts);
                            KualiDecimal remainingFeeAmount = new KualiDecimal(0);
                            KualiDecimal currentFeeAmount = new KualiDecimal(0);
                            if (ObjectUtils.isNotNull(serviceFee)) {
                                remainingFeeAmount = serviceFee.getServiceFee();
                                currentFeeAmount = serviceFee.getServiceFee().divide(numAccounts);
                            }

                            String creditObjectCode = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_CLEARING_OBJECT_CODE);

                            boolean allGlpesCreated = true;

                            for (int i = 0; i < accountingInfo.size(); i++) {
                                TripAccountingInformation info = accountingInfo.get(i);

                                // If its the last account, use the remainingAmount to resolve rounding
                                if (i < accountingInfo.size() - 1) {
                                    remainingAmount = remainingAmount.subtract(currentAmount);
                                    remainingFeeAmount = remainingFeeAmount.subtract(currentFeeAmount);
                                }
                                else {
                                    currentAmount = remainingAmount;
                                    currentFeeAmount = remainingFeeAmount;
                                }
                                String objectCode = info.getObjectCode();
                                if (StringUtils.isEmpty(objectCode)) {
                                    objectCode = travelExpenseType.getFinancialObjectCode();
                                }

                                // set the amount on the accounting info for documents pulling in imported expenses
                                info.setAmount(currentAmount);

                                if (ObjectUtils.isNotNull(serviceFee)) {
                                    // Agency Data has a DI Code that maps to an Agency Service Fee, create GLPEs for it.
                                    LOG.info("Processing Service Fee GLPE for agency: "+ agencyData.getId()+ " tripId: "+ agencyData.getTripId());

                                    final boolean generateOffset = true;
                                    List<GeneralLedgerPendingEntry> pendingEntries = importedExpensePendingEntryService.buildDebitPendingEntry(agencyData, info, sequenceHelper, objectCode, currentFeeAmount, generateOffset);
                                    allGlpesCreated = importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, false, generateOffset);

                                    pendingEntries = importedExpensePendingEntryService.buildServiceFeeCreditPendingEntry(agencyData, info, sequenceHelper, serviceFee, currentFeeAmount, generateOffset);
                                    allGlpesCreated &= importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, true, generateOffset);
                                }

                                final boolean generateOffset = true;
                                List<GeneralLedgerPendingEntry> pendingEntries = importedExpensePendingEntryService.buildDebitPendingEntry(agencyData, info, sequenceHelper, objectCode, currentAmount, generateOffset);
                                allGlpesCreated &= importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, false, generateOffset);

                                pendingEntries = importedExpensePendingEntryService.buildCreditPendingEntry(agencyData, info, sequenceHelper, creditObjectCode, currentAmount, generateOffset);
                                allGlpesCreated &= importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, true, generateOffset);
                            }

                            if (entries.size() > 0 && allGlpesCreated) {

                                GeneralLedgerPendingEntry glpe = entries.get(0);

                                //add the GLPE document number to the historical expense entry
                                expense.setDocumentNumber(glpe.getDocumentNumber());
                                //add the TravelDocument document number to the historical expense entry
                                expense.setDocumentType(travelDocument.getDocumentTypeName());
                                //set the travel company to be the merchant name from the credit card data
                                expense.setTravelCompany(ccData.getMerchantName());

                                businessObjectService.save(entries);
                                businessObjectService.save(expense);
                                ccData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_MOVED_TO_HISTORICAL);
                                ccData.setMoveToHistoryIndicator(true);
                                businessObjectService.save(ccData);
                                agencyData.setMoveToHistoryIndicator(true);
                                agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_MOVED_TO_HISTORICAL);

                                // nota bene: agency staging data object can NOT be saved here b/c the AgencyStagingDataMaint doc calls this method and will save it itself once processing completes.
                                // batch steps which call this method need to save the business object after calling this method
                            }
                            else {
                                LOG.error("An error occurred while creating GLPEs for agency data: "+ agencyData.getId()+ " tripId"+ agencyData.getTripId()+ ". Will not reconcile expense.");
                                errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_GLPE_CREATION));
                            }
                        }
                        else {
                            LOG.error("The accounting lines on the agency data record do not have a match on the travel document. Agency data: "+ agencyData.getId() +"; tripId: "+ agencyData.getTripId() +"; documentNumber: "+ travelDocument.getDocumentNumber());
                            errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_ACCOUNTING_LINE_MATCH, travelDocument.getDocumentNumber()));
                        }
                    }
                    else {
                        LOG.info("No expense type object code could be found for agency data: "+agencyData.getId()+" tripId: "+agencyData.getTripId());
                        errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_EXPENSE_TYPE_OBJECT_CODE, expenseTypeCategory.getCode(), travelDocument.getDocumentTypeName(), travelDocument.getTripTypeCode(), travelDocument.getTraveler().getTravelerTypeCode()));
                    }
                }
                else {
                    LOG.info("No match found for agency data: "+ agencyData.getId()+ " tripId:"+ agencyData.getTripId());

                    if (expenseTypeCategory == TemConstants.ExpenseTypeMetaCategory.AIRFARE) {
                        errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_TRIP_MATCH_AIR, agencyData.getTripExpenseAmount().toString(), agencyData.getAirTicketNumber(), agencyData.getAirServiceFeeNumber()));
                    }
                    else if (expenseTypeCategory == TemConstants.ExpenseTypeMetaCategory.LODGING) {
                        errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_TRIP_MATCH, agencyData.getTripExpenseAmount().toString(), agencyData.getLodgingItineraryNumber()));
                    }
                    else if (expenseTypeCategory == TemConstants.ExpenseTypeMetaCategory.RENTAL_CAR) {
                        errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_TRIP_MATCH, agencyData.getTripExpenseAmount().toString(), agencyData.getRentalCarItineraryNumber()));
                    }
                    else {
                        errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_INVALID_EXPENSE_TYPE_CATEGORY));
                    }
                }
            }
            else {
                LOG.info("Agency Data: "+ agencyData.getId() +"; expected errorCode="+ AgencyStagingDataErrorCodes.AGENCY_NO_ERROR +", received errorCode="+ agencyData.getErrorCode() +". Will not attempt to reconcile expense.");
                errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_INVALID_ERROR_CODE, AgencyStagingDataErrorCodes.AGENCY_NO_ERROR, agencyData.getErrorCode()));
            }
        }
        else {
            LOG.info("Agency Data: "+ agencyData.getId() +", is not active. Will not attempt to reconcile expense.");
            errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_RECON_ACTIVE));
        }

        LOG.info("Finished reconciling expense for agency data: "+ agencyData.getId()+ ", Trip Id: "+ agencyData.getTripId() +". Agency data "+ (errors.isEmpty() ? "was":"was not") +" reconciled.");
        return errors;
    }

    /**
     * This method checks whether the accounting lines on the agency staging data record have a match with the source accounting lines on the travel document
     *
     * @param agencyDataAccountingLines
     * @param travelDocumentAccountingLines
     * @return
     */
    protected boolean isAccountingLinesMatch(AgencyStagingData agencyStagingData) {

        List<TripAccountingInformation> agencyDataAccountingLines = agencyStagingData.getTripAccountingInformation();

        //check that the accounting lines on the agency record have a matching source accounting line on the trip
        List<TemSourceAccountingLine> tripSourceAccountingLines = getSourceAccountingLinesByTrip(agencyStagingData.getTripId());

        // Get ACCOUNTING_LINE_VALIDATION parameter to determine which fields to validate
        Collection<String> validationParams = getParameterService().getParameterValuesAsString(AgencyDataImportStep.class, TravelParameters.ACCOUNTING_LINE_VALIDATION);
        if (ObjectUtils.isNull(validationParams)) {
           LOG.info("Did not find the parameter, "+ TravelParameters.ACCOUNTING_LINE_VALIDATION +". Will not validate matching accounting lines.");
           return true;
        }

        for(TripAccountingInformation agencyAccount : agencyDataAccountingLines) {

            boolean foundAMatch = false;
            for(TemSourceAccountingLine sourceAccountingLine : tripSourceAccountingLines) {

                boolean account = true, subaccount = true, objectcode = true, subobjectcode = true;

                if (validationParams.contains(TemConstants.AgencyStagingDataValidation.VALIDATE_ACCOUNT)) {
                    //need to clean the empty vs null data for comparison in StringUtils.equals
                    String agencyAccountNumber = StringUtils.trimToEmpty(agencyAccount.getTripAccountNumber());
                    String sourceAccountNumber = StringUtils.trimToEmpty(sourceAccountingLine.getAccountNumber());

                    if ( !StringUtils.equals(agencyAccountNumber, sourceAccountNumber)) {
                        account = false;
                    }
                }
                if (validationParams.contains(TemConstants.AgencyStagingDataValidation.VALIDATE_SUBACCOUNT)) {
                    String agencySubAccountNumber = StringUtils.trimToEmpty(agencyAccount.getTripSubAccountNumber());
                    String sourceSubAccountNumber = StringUtils.trimToEmpty(sourceAccountingLine.getSubAccountNumber());

                    if ( !StringUtils.equals(agencySubAccountNumber, sourceSubAccountNumber) ) {
                        subaccount = false;
                    }
                }

                if (account && subaccount && objectcode && subobjectcode) {
                    LOG.debug("Found accounting line match on DocumentId: "+ sourceAccountingLine.getDocumentNumber() +", "+ sourceAccountingLine);
                    foundAMatch = true;
                    break;
                }
                else {
                    LOG.info("Document accounting line did not match agency accounting line: AgncyStgDataId:"+ agencyAccount.getAgencyStagingDataId() +",trpAcctInfoId: "+ agencyAccount.getId() +", documentNumber:"+ sourceAccountingLine.getDocumentNumber());
                    LOG.info("Agency Data Accounting Line: "+ agencyAccount);
                    LOG.info("Document Accounting Line: "+ sourceAccountingLine);
                }
            }
            //the agency data accounting line does not have a matching source account on the trip- return false
            if (!foundAMatch) {
                return false;
            }
        }
        //all agency data accounting lines have matching source accounts on the trip- return true
        return true;
    }

    /**
     *
     * This method gets the {@link TemTravelExpenseTypeCode} associated with the expense type and travel document
     * @param expenseTypeParamCode
     * @param travelDocumentIdentifier
     * @return
     */
    protected ExpenseTypeObjectCode getTravelExpenseType(TemConstants.ExpenseTypeMetaCategory expenseCategory, TravelDocument travelDoc) {
        // get the default expense type for category
        final ExpenseType expenseType = getTravelExpenseService().getDefaultExpenseTypeForCategory(expenseCategory);

        // Is there a document associated with this trip?  If so, let's grab the trip type and traveler type from that
        if (ObjectUtils.isNotNull(travelDoc)) {

            ExpenseTypeObjectCode expenseTypeObjectCode = travelExpenseService.getExpenseType(expenseType.getCode(), travelDoc.getDocumentTypeName(), travelDoc.getTripTypeCode(), travelDoc.getTraveler().getTravelerTypeCode());
            if (expenseTypeObjectCode == null) {
                LOG.error("Unable to retrieve ExpenseTypeObjectCode for ExpenseTypeCode:"+ expenseType.getCode() +", DocTypeName:"+ travelDoc.getDocumentTypeName() +", TripType:"+ travelDoc.getTripTypeCode() +", TravelerType:"+ travelDoc.getTraveler().getTravelerTypeCode());
            }

            return expenseTypeObjectCode;
        }
        LOG.error("Unable to retrieve TemTravelExpenseTypeCode");
        return null; // we shouldn't ever get here if the trip id was validated, but hey.  You never know.
    }

    /**
     *
     * This method returns the {@link AgencyServiceFee} by distribution code (distributionCode).
     * @param distributionCode
     * @return
     */
    protected AgencyServiceFee getAgencyServiceFee(String distributionCode) {
        if (StringUtils.isNotEmpty(distributionCode)) {
            Map<String,String> criteria = new HashMap<String,String>(1);
            criteria.put(TemPropertyConstants.DISTRIBUTION_CODE, distributionCode);
            criteria.put(TemPropertyConstants.ACTIVE_IND, TemConstants.YES);
            List<AgencyServiceFee> serviceFee = (List<AgencyServiceFee>) getBusinessObjectService().findMatching(AgencyServiceFee.class, criteria);
            if (ObjectUtils.isNotNull(serviceFee) && serviceFee.size() > 0) {
                return serviceFee.get(0);
            }
        }
        return null;
    }

    protected List<TemSourceAccountingLine> getSourceAccountingLinesByTrip(String travelDocumentIdentifier) {

        Collection<String> travelDocumentNumbers = getTravelDocumentService().getApprovedTravelDocumentNumbersByTrip(travelDocumentIdentifier);
        LOG.info("Will attempt to retrieve source accounting lines for the following approved documents: "+ travelDocumentNumbers);

        ArrayList temSourceAccountingLines = new ArrayList<TemSourceAccountingLine>();

        if (!travelDocumentNumbers.isEmpty()) {

            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, travelDocumentNumbers);
            fieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_TYPE_CODE, KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE);

            temSourceAccountingLines.addAll(getBusinessObjectService().findMatchingOrderBy(TemSourceAccountingLine.class, fieldValues, KFSPropertyConstants.SEQUENCE_NUMBER, true));
        }

        return temSourceAccountingLines;
    }

    /**
     * Gets the travelAuthorizationService attribute.
     * @return Returns the travelAuthorizationService.
     */
    public TravelAuthorizationService getTravelAuthorizationService() {
        return travelAuthorizationService;
    }

    /**
     * Sets the travelAuthorizationService attribute value.
     * @param travelAuthorizationService The travelAuthorizationService to set.
     */
    public void setTravelAuthorizationService(TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
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

    /**
     * Gets the travelExpenseService attribute.
     * @return Returns the travelExpenseService.
     */
    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    /**
     * Sets the travelExpenseService attribute value.
     * @param travelExpenseService The travelExpenseService to set.
     */
    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }

    public ImportedExpensePendingEntryService getImportedExpensePendingEntryService() {
        return importedExpensePendingEntryService;
    }

    public void setImportedExpensePendingEntryService(ImportedExpensePendingEntryService importedExpensePendingEntryService) {
        this.importedExpensePendingEntryService = importedExpensePendingEntryService;
    }

    public TravelDocumentService getTravelDocumentService() {
        return this.travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    public TravelEncumbranceService getTravelEncumbranceService() {
        return this.travelEncumbranceService;
    }

    public void setTravelEncumbranceService(TravelEncumbranceService travelEncumbranceService) {
        this.travelEncumbranceService = travelEncumbranceService;
    }

}
