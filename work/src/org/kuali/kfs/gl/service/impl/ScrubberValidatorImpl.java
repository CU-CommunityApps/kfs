/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.service.impl;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.BalanceTypService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.batch.service.OriginEntryLookupService;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.businessobject.GeneralLedgerInputType;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OriginationCodeService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.springframework.util.StringUtils;

/**
 * The default GL implementation of ScrubberValidator
 */

@NonTransactional
public class ScrubberValidatorImpl implements ScrubberValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private ParameterService parameterService;
    private PersistenceService persistenceService;
    private UniversityDateDao universityDateDao;
    private AccountService accountService;
    private OriginationCodeService originationCodeService;
    private PersistenceStructureService persistenceStructureService;
    private ThreadLocal<OriginEntryLookupService> referenceLookup = new ThreadLocal<OriginEntryLookupService>();
    private BalanceTypService balanceTypService;
    
    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
    
    private boolean continuationAccountIndicator = false;

    private static String[] debitOrCredit = new String[] { KFSConstants.GL_DEBIT_CODE, KFSConstants.GL_CREDIT_CODE };

    /**
     * Constructs a ScrubberValidatorImpl instance
     */
    public ScrubberValidatorImpl() {
    }

    private static int count = 0;

    /**
     * Validate a transaction for use in balance inquiry
     * 
     * @param entry Input transaction
     * @see org.kuali.module.gl.service.ScrubberValidator#validateForInquiry(org.kuali.kfs.bo.GeneralLedgerPendingEntry)
     */
    public void validateForInquiry(GeneralLedgerPendingEntry entry) {
        LOG.debug("validateForInquiry() started");

        UniversityDate today = null;

        if (entry.getUniversityFiscalYear() == null) {
            today = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
            entry.setUniversityFiscalYear(today.getUniversityFiscalYear());
        }

        if (entry.getUniversityFiscalPeriodCode() == null) {
            if (today == null) {
                today = SpringContext.getBean(UniversityDateService.class).getCurrentUniversityDate();
            }
            entry.setUniversityFiscalPeriodCode(today.getUniversityFiscalAccountingPeriod());
        }

        if ((entry.getSubAccountNumber() == null) || (!StringUtils.hasText(entry.getSubAccountNumber()))) {
            entry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        if ((entry.getFinancialSubObjectCode() == null) || (!StringUtils.hasText(entry.getFinancialSubObjectCode()))) {
            entry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        if ((entry.getProjectCode() == null) || (!StringUtils.hasText(entry.getProjectCode()))) {
            entry.setProjectCode(KFSConstants.getDashProjectCode());
        }
    }

    /**
     * Validate a transaction in the scrubber
     * 
     * @param originEntry Input transaction (never changed)
     * @param scrubbedEntry Output transaction (scrubbed version of input transaction)
     * @param universityRunDate Date of scrubber run
     * @return List of Message objects based for warnings or errors that happened when validating the transaction
     * @see org.kuali.module.gl.service.ScrubberValidator#validateTransaction(org.kuali.module.gl.bo.OriginEntry, org.kuali.module.gl.bo.OriginEntry, org.kuali.module.gl.bo.UniversityDate, boolean)
     */
    public List<Message> validateTransaction(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate, boolean laborIndicator) {
        LOG.debug("validateTransaction() started");
        
        continuationAccountIndicator = false;
        
        List<Message> errors = new ArrayList<Message>();

        count++;
        if (count % 1000 == 0) {
            LOG.info(count + " " + originEntry.getLine());
        }

        // The cobol checks fdoc_nbr, trn_ldgr_entr_desc, org_doc_nbr, org_reference_id, and fdoc_ref_nbr for characters less than
        // ascii 32 or '~'. If found, it replaces that character with a space and reports a warning. This code does the ~, but not
        // the
        // less than 32 part.
        if ((originEntry.getDocumentNumber() != null) && (originEntry.getDocumentNumber().indexOf("~") > -1)) {
            String d = originEntry.getDocumentNumber();
            scrubbedEntry.setDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getTransactionLedgerEntryDescription() != null) && (originEntry.getTransactionLedgerEntryDescription().indexOf("~") > -1)) {
            String d = originEntry.getTransactionLedgerEntryDescription();
            scrubbedEntry.setTransactionLedgerEntryDescription(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getOrganizationDocumentNumber() != null) && (originEntry.getOrganizationDocumentNumber().indexOf("~") > -1)) {
            String d = originEntry.getOrganizationDocumentNumber();
            scrubbedEntry.setOrganizationDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getOrganizationReferenceId() != null) && (originEntry.getOrganizationReferenceId().indexOf("~") > -1)) {
            String d = originEntry.getOrganizationReferenceId();
            scrubbedEntry.setOrganizationReferenceId(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getReferenceFinancialDocumentNumber() != null) && (originEntry.getReferenceFinancialDocumentNumber().indexOf("~") > -1)) {
            String d = originEntry.getReferenceFinancialDocumentNumber();
            scrubbedEntry.setReferenceFinancialDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }

        // It's important that this check come before the checks for object, sub-object and accountingPeriod
        // because this validation method will set the fiscal year and reload those three objects if the fiscal
        // year was invalid. This will also set originEntry.getOption and workingEntry.getOption. So, it's
        // probably a good idea to validate the fiscal year first thing.
        Message err = validateFiscalYear(originEntry, scrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validateBalanceType(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateTransactionDate(originEntry, scrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validateTransactionAmount(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateChart(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        // Labor Scrubber doesn't validate Account here.
        if (!laborIndicator) {
            err = validateAccount(originEntry, scrubbedEntry, universityRunDate);
            if (err != null) {
                errors.add(err);
            }
        }

        err = validateSubAccount(originEntry, scrubbedEntry, continuationAccountIndicator);
        if (err != null) {
            errors.add(err);
        }

        err = validateProjectCode(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateDocumentType(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateOrigination(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }
        
        err = validateReferenceOrigination(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateDocumentNumber(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateObjectCode(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        // If object code is invalid, we can't check the object type
        if (err == null) {
            err = validateObjectType(originEntry, scrubbedEntry);
            if (err != null) {
                errors.add(err);
            }
        }

        err = validateSubObjectCode(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        // return messages could be multiple from validateReferenceFields
        List<Message> referenceErrors = new ArrayList<Message>();
        referenceErrors = validateReferenceDocumentFields(originEntry, scrubbedEntry);
        if (referenceErrors != null) {
            errors.addAll(referenceErrors);
        }

        err = validateUniversityFiscalPeriodCode(originEntry, scrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validateReversalDate(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }
        
        err = validateDescription(originEntry);
        if (err != null) {
            errors.add(err);
        }

        
        
        return errors;
    }

    /**
     * Validates the account of an origin entry 
     * 
     * @param originEntry the origin entry to find the account of
     * @param workingEntry the copy of the entry to move the account over to if it is valid
     * @param universityRunDate the run date of the scrubber process
     * @return a Message if the account was invalid, or null if no error was encountered
     */
    private Message validateAccount(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateAccount() started");

        Account originEntryAccount = referenceLookup.get().getAccount(originEntry);
        if (originEntryAccount != null) {
            originEntryAccount.setSubFundGroup(referenceLookup.get().getSubFundGroup(originEntryAccount.getSubFundGroupCode()));
        }
            
        if (originEntryAccount == null) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNT_NOT_FOUND, originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber(), Message.TYPE_FATAL);
        }

        if (parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE).equals(originEntry.getFinancialDocumentTypeCode())) {
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            return null;
        }

        if ((originEntryAccount.getAccountExpirationDate() == null) && originEntryAccount.isActive()) {
            // account is neither closed nor expired
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            return null;
        }

        String[] continuationAccountBypassOriginationCodes = parameterService.getParameterValues(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES).toArray(new String[] {});

        String [] continuationAccountBypassBalanceTypeCodes = {"EX","IE","PE"};
        String[] continuationAccountBypassDocumentTypeCodes = parameterService.getParameterValues(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES).toArray(new String[] {});

        // Has an expiration date or is closed
        if ((ArrayUtils.contains(continuationAccountBypassOriginationCodes, originEntry.getFinancialSystemOriginationCode())) && !originEntryAccount.isActive()) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT, originEntryAccount.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber(), Message.TYPE_FATAL);
        }

        if ((ArrayUtils.contains(continuationAccountBypassOriginationCodes, originEntry.getFinancialSystemOriginationCode()) || ArrayUtils.contains(continuationAccountBypassBalanceTypeCodes, originEntry.getFinancialBalanceTypeCode()) || ArrayUtils.contains(continuationAccountBypassDocumentTypeCodes, originEntry.getFinancialDocumentTypeCode().trim())) && originEntryAccount.isActive()) {
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            return null;
        }

        Calendar today = Calendar.getInstance();
        today.setTime(universityRunDate.getUniversityDate());

        long offsetAccountExpirationTime = getAdjustedAccountExpirationDate(originEntryAccount);

        if (isExpired(offsetAccountExpirationTime, today) || !originEntryAccount.isActive()) {
            Message error = continuationAccountLogic(originEntry, workingEntry, today);
            if (error != null) {
                return error;
            }
        }

        workingEntry.setAccountNumber(originEntry.getAccountNumber());
        return null;
    }

    /**
     * Called when the account of the origin entry is expired or closed, this validates the continuation account
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @param today the run date of the scrubber (to test against expiration dates)
     * @return a Message if an error was encountered, otherwise null
     */
    private Message continuationAccountLogic(OriginEntry originEntry, OriginEntry workingEntry, Calendar today) {

        List checkedAccountNumbers = new ArrayList();

        Account continuationAccount = null;
        Account originEntryAccount = referenceLookup.get().getAccount(originEntry);

        String chartCode = originEntryAccount.getContinuationFinChrtOfAcctCd();
        String accountNumber = originEntryAccount.getContinuationAccountNumber();

        for (int i = 0; i < 10; ++i) {
            if (checkedAccountNumbers.contains(chartCode + accountNumber)) {
                // Something is really wrong with the data because this account has already been evaluated.
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CIRCULAR_DEPENDENCY_IN_CONTINUATION_ACCOUNT_LOGIC, Message.TYPE_FATAL);
            }

            if ((chartCode == null) || (accountNumber == null)) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND, Message.TYPE_FATAL);
            }

            // Lookup the account
            continuationAccount = referenceLookup.get().getAccount(chartCode, accountNumber);
            if (null == continuationAccount) {
                // account not found
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND, Message.TYPE_FATAL);
            }
            else {
                // the account exists
                continuationAccount.setSubFundGroup(referenceLookup.get().getSubFundGroup(continuationAccount.getSubFundGroupCode()));
                if (continuationAccount.getAccountExpirationDate() == null) {
                    // No expiration date
                    workingEntry.setAccountNumber(accountNumber);
                    workingEntry.setChartOfAccountsCode(chartCode);
                    
                    // shawn - set subAcount with dashes
                    continuationAccountIndicator = true;
                    //workingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
                    //workingEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_AUTO_FORWARD) + " " + originEntry.getChartOfAccountsCode() + originEntry.getAccountNumber() + originEntry.getTransactionLedgerEntryDescription());
                    workingEntry.setTransactionLedgerEntryDescription("AUTO FR " + originEntry.getChartOfAccountsCode() + originEntry.getAccountNumber() + originEntry.getTransactionLedgerEntryDescription());
                    return MessageBuilder.buildMessage(KFSKeyConstants.MSG_ACCOUNT_CLOSED_TO, workingEntry.getChartOfAccountsCode() + workingEntry.getAccountNumber(), Message.TYPE_WARNING);
                }
                else {
                    // the account does have an expiration date.
                    // This is the only case in which we might go
                    // on for another iteration of the loop.
                    checkedAccountNumbers.add(chartCode + accountNumber);

                    // Add 3 months to the expiration date if it's a contract and grant account.
                    long offsetAccountExpirationTime = getAdjustedAccountExpirationDate(continuationAccount);

                    // Check that the account has not expired.

                    // If the account has expired go around for another iteration.
                    if (isExpired(offsetAccountExpirationTime, today)) {
                        chartCode = continuationAccount.getContinuationFinChrtOfAcctCd();
                        accountNumber = continuationAccount.getContinuationAccountNumber();
                    }
                    else {
                        workingEntry.setAccountNumber(accountNumber);
                        workingEntry.setChartOfAccountsCode(chartCode);
                        
                        //shawn - set subAccount with dashes
                        continuationAccountIndicator = true;
                        //workingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
                        //workingEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_AUTO_FORWARD) + originEntry.getChartOfAccountsCode() + originEntry.getAccountNumber() + originEntry.getTransactionLedgerEntryDescription());
                        workingEntry.setTransactionLedgerEntryDescription("AUTO FR " + originEntry.getChartOfAccountsCode() + originEntry.getAccountNumber() + originEntry.getTransactionLedgerEntryDescription());
                        return MessageBuilder.buildMessage(KFSKeyConstants.MSG_ACCOUNT_CLOSED_TO, workingEntry.getChartOfAccountsCode() + workingEntry.getAccountNumber(), Message.TYPE_WARNING);
                    }
                }
            }
        }

        // We failed to find a valid continuation account.
        return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_LIMIT_REACHED, Message.TYPE_FATAL);
    }

    /**
     * Calculates the expiration date of an adjusted account
     * 
     * @param account the expired account
     * @return the timestamp of the adjusted date
     */
    private long getAdjustedAccountExpirationDate(Account account) {
        long offsetAccountExpirationTime = account.getAccountExpirationDate().getTime();

        if (account.isForContractsAndGrants() && (account.isActive())) {

            String daysOffset = parameterService.getParameterValue(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
            int daysOffsetInt = 3 * 30; // default to 90 days (approximately 3 months)

            if (daysOffset.trim().length() > 0) {

                daysOffsetInt = new Integer(daysOffset).intValue();
            }

            Calendar tempCal = Calendar.getInstance();
            tempCal.setTimeInMillis(offsetAccountExpirationTime);
            tempCal.add(Calendar.DAY_OF_MONTH, daysOffsetInt);
            offsetAccountExpirationTime = tempCal.getTimeInMillis();
        }

        return offsetAccountExpirationTime;
    }

    /**
     * Validates the reversal date of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateReversalDate(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateReversalDate() started");

        if (originEntry.getFinancialDocumentReversalDate() != null) {
//            UniversityDate universityDate = universityDateDao.getByPrimaryKey(originEntry.getFinancialDocumentReversalDate());
            UniversityDate universityDate = referenceLookup.get().getUniversityDate(originEntry.getFinancialDocumentReversalDate());
            if (universityDate == null) {
                Date reversalDate = originEntry.getFinancialDocumentReversalDate();
                SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING);
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_REVERSAL_DATE_NOT_FOUND, format.format(reversalDate), Message.TYPE_FATAL);
            }
            else {
                workingEntry.setFinancialDocumentReversalDate(originEntry.getFinancialDocumentReversalDate());
            }
        }
        return null;
    }

    /**
     * Validates the sub account of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateSubAccount(OriginEntry originEntry, OriginEntry workingEntry, boolean continuationAccountIndicator) {
        LOG.debug("validateSubAccount() started");

        // when continuationAccount used, the subAccountNumber should be changed to dashes and skip validation subAccount process
        if (continuationAccountIndicator) {
            workingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            return null;
        }
                
        // If the sub account number is empty, set it to dashes.
        // Otherwise set the workingEntry sub account number to the
        // sub account number of the input origin entry.
        if (StringUtils.hasText(originEntry.getSubAccountNumber())) {
            // sub account IS specified
            if (!KFSConstants.getDashSubAccountNumber().equals(originEntry.getSubAccountNumber())) {
              SubAccount originEntrySubAccount = referenceLookup.get().getSubAccount(originEntry);
              //SubAccount originEntrySubAccount = getSubAccount(originEntry);
                if (originEntrySubAccount == null) {
                    // sub account is not valid
                    return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND, originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + "-" + originEntry.getSubAccountNumber(), Message.TYPE_FATAL);
                }
                else {
                    // sub account IS valid
                    if (originEntrySubAccount.isActive()) {
                        // sub account IS active
                        workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
                    }
                    else {
                        // sub account IS NOT active
                        if (parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE).equals(originEntry.getFinancialDocumentTypeCode())) {
                            // document IS annual closing
                            workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
                        }
                        else {
                            // document is NOT annual closing
                            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_ACTIVE, originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + "-" + originEntry.getSubAccountNumber(), Message.TYPE_FATAL);
                        }
                    }
                }
            }
            else {
                // the sub account is dashes
                workingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
        }
        else {
            // No sub account is specified.
            workingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        
        
        return null;
        
    }

    /**
     * Validates the project code of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateProjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateProjectCode() started");

        if (StringUtils.hasText(originEntry.getProjectCode()) && !KFSConstants.getDashProjectCode().equals(originEntry.getProjectCode())) {
            ProjectCode originEntryProject = referenceLookup.get().getProjectCode(originEntry);
//            ProjectCode originEntryProject = getProjectCode(originEntry);
            if (originEntryProject == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_PROJECT_CODE_NOT_FOUND, originEntry.getProjectCode(), Message.TYPE_FATAL);
            }
            else {
                if (originEntryProject.isActive()) {
                    workingEntry.setProjectCode(originEntry.getProjectCode());
                }
                else {
                    return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_PROJECT_CODE_MUST_BE_ACTIVE, originEntry.getProjectCode(), Message.TYPE_FATAL);
                }
            }
        }
        else {
            workingEntry.setProjectCode(KFSConstants.getDashProjectCode());
        }

        return null;
    }

    /**
     * Validates the fiscal year of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @param universityRunDate the university date when this scrubber process is being run
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateFiscalYear(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateFiscalYear() started");

        if ((originEntry.getUniversityFiscalYear() == null) || (originEntry.getUniversityFiscalYear().intValue() == 0)) {
            if (!originEntry.getFinancialBalanceTypeCode().equals(KFSConstants.BALANCE_TYPE_A21)){
            
                workingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
                workingEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
                
                // shawn - to display updated values on report
                originEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
                originEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
                
            } else {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_FISCAL_YEAR_CANNOT_BE_NULL_BALANCE_TYPE_A2, Message.TYPE_FATAL);
            }
        }
        else {
            workingEntry.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
        }

        SystemOptions originEntryOption = referenceLookup.get().getSystemOptions(workingEntry.getUniversityFiscalYear());
        if (originEntryOption == null) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND, originEntry.getUniversityFiscalYear() + "", Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Validates the transaction date of the origin entry, make sure it is a valid university date
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @param universityRunDate the university date when this scrubber process is being run
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateTransactionDate(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateTransactionDate() started");

        if (originEntry.getTransactionDate() == null) {
            Date transactionDate = new Date(universityRunDate.getUniversityDate().getTime());

            // Set the transaction date to the run date.
            originEntry.setTransactionDate(transactionDate);
            workingEntry.setTransactionDate(transactionDate);
        }
        else {
            workingEntry.setTransactionDate(originEntry.getTransactionDate());
        }

        // Next, we have to validate the transaction date against the university date table.
//        if (universityDateDao.getByPrimaryKey(originEntry.getTransactionDate()) == null) {
        if (referenceLookup.get().getUniversityDate(originEntry.getTransactionDate()) == null) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_TRANSACTION_DATE_INVALID, originEntry.getTransactionDate().toString(), Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Valides the document type of an origin entry 
     * @param originEntry the origin entry to check
     * @param workingEntryInfo the copy of that entry to move good data over to
     * @return a Message if the document type is invalid, otherwise if valid, null
     */
    private Message validateDocumentType(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateDocumentType() started");

        GeneralLedgerInputType originEntryInputType = referenceLookup.get().getGeneralLedgerInputType(originEntry);
//DocumentType originEntryDocumentType = getDocumentType(originEntry.getFinancialDocumentTypeCode());
        if (originEntryInputType == null) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DOCUMENT_TYPE_NOT_FOUND, originEntry.getFinancialDocumentTypeCode(), Message.TYPE_FATAL);
        }

        workingEntry.setFinancialDocumentTypeCode(originEntry.getFinancialDocumentTypeCode());
        return null;
    }

    /**
     * Validates the origination code of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateOrigination(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateOrigination() started");

        if (StringUtils.hasText(originEntry.getFinancialSystemOriginationCode())) {
            OriginationCode originEntryOrigination = referenceLookup.get().getOriginationCode(originEntry);
            if (originEntryOrigination == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND, originEntry.getFinancialSystemOriginationCode(), Message.TYPE_FATAL);
            }
            if (!originEntryOrigination.isActive()) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ORIGIN_CODE_NOT_ACTIVE, originEntry.getFinancialSystemOriginationCode(), Message.TYPE_FATAL);
            }
            
            workingEntry.setFinancialSystemOriginationCode(originEntry.getFinancialSystemOriginationCode());
        }
        else {
            return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND) + " (" + originEntry.getFinancialSystemOriginationCode() + ")", Message.TYPE_FATAL);
        }
        return null;
    }
    
    
    private Message validateReferenceOrigination(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateOrigination() started");
        String referenceFinancialSystemOriginationCode = originEntry.getReferenceFinancialSystemOriginationCode();
        if (StringUtils.hasText(referenceFinancialSystemOriginationCode)) {
            OriginationCode originEntryOrigination = referenceLookup.get().getOriginationCode(referenceFinancialSystemOriginationCode);
            if (originEntryOrigination == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_REFERENCE_ORIGIN_CODE_NOT_FOUND, " (" + referenceFinancialSystemOriginationCode + ")", Message.TYPE_FATAL);
            }
            else {
                workingEntry.setReferenceFinancialSystemOriginationCode(referenceFinancialSystemOriginationCode);
            }
        }
        
        return null;
    }
    

    /**
     * Validates the document number of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateDocumentNumber(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateDocumentNumber() started");

        if (!StringUtils.hasText(originEntry.getDocumentNumber())) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DOCUMENT_NUMBER_REQUIRED, Message.TYPE_FATAL);
        }
        else {
            workingEntry.setDocumentNumber(originEntry.getDocumentNumber());
            return null;
        }
    }

    /**
     * Validates the chart of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateChart(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateChart() started");

        Chart originEntryChart = referenceLookup.get().getChart(originEntry);
//        Chart originEntryChart = getChart(originEntry);
        if (originEntryChart == null) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CHART_NOT_FOUND, originEntry.getChartOfAccountsCode(), Message.TYPE_FATAL);
        }

        if (!originEntryChart.isActive()) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CHART_NOT_ACTIVE, originEntry.getChartOfAccountsCode(), Message.TYPE_FATAL);
        }

        workingEntry.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
        return null;
        
    }

    /**
     * Validates the object code of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateObjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateObjectCode() started");

        if (!StringUtils.hasText(originEntry.getFinancialObjectCode())) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_OBJECT_CODE_EMPTY, Message.TYPE_FATAL);
        }

        // We're checking the object code based on the year & chart from the working entry.
        workingEntry.setFinancialObjectCode(originEntry.getFinancialObjectCode());

        // the fiscal year can be blank in originEntry, but we're assuming that the year attribute is populated by the validate year
        // method
        ObjectCode workingEntryFinancialObject = referenceLookup.get().getFinancialObject(workingEntry);
        if (workingEntryFinancialObject == null) {
            String objectCodeString = originEntry.getUniversityFiscalYear() + "-" + originEntry.getChartOfAccountsCode() + "-" + originEntry.getFinancialObjectCode();
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND, objectCodeString, Message.TYPE_FATAL);
        }
        
        if (!workingEntryFinancialObject.isActive()) {
            String objectCodeString = originEntry.getUniversityFiscalYear() + "-" + originEntry.getChartOfAccountsCode() + "-" + originEntry.getFinancialObjectCode();
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_ACTIVE, objectCodeString, Message.TYPE_FATAL);
        }

        //TODO: Shawn - need to commented back after using file --> ?? 
        // changed OriginEntry to OriginEntryFull in  ScrubberProcess line 537 (after getting entry from file) 
        //((OriginEntryFull)workingEntry).setFinancialObject(workingEntryFinancialObject);
        //((OriginEntryFull)originEntry).setFinancialObject(workingEntryFinancialObject);
        
        return null;
    }

    /**
     * Assuming that the object code has been validated first, validates the object type of the entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     * @see org.kuali.module.gl.service.ScrubberValidator#validateObjectType(org.kuali.module.gl.bo.OriginEntryFull,
     *      org.kuali.module.gl.bo.OriginEntryFull)
     */
    private Message validateObjectType(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateObjectType() started");

        if (!StringUtils.hasText(originEntry.getFinancialObjectTypeCode())) {
            // If not specified, use the object type from the object code
            ObjectCode workingEntryFinancialObject = referenceLookup.get().getFinancialObject(workingEntry);
            workingEntry.setFinancialObjectTypeCode(workingEntryFinancialObject.getFinancialObjectTypeCode());
        }
        else {
            workingEntry.setFinancialObjectTypeCode(originEntry.getFinancialObjectTypeCode());
        }

        ObjectType workingEntryObjectType = referenceLookup.get().getObjectType(workingEntry);
        if (workingEntryObjectType == null) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND, originEntry.getFinancialObjectTypeCode(), Message.TYPE_FATAL);
        }
        
        if (!workingEntryObjectType.isActive()) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_OBJECT_TYPE_NOT_ACTIVE, originEntry.getFinancialObjectTypeCode(), Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * Validates the sub object code of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateSubObjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateFinancialSubObjectCode() started");

        if (!StringUtils.hasText(originEntry.getFinancialSubObjectCode())) {
            workingEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            return null;
        }

        if (!KFSConstants.getDashFinancialSubObjectCode().equals(originEntry.getFinancialSubObjectCode())) {
            SubObjectCode originEntrySubObject = referenceLookup.get().getFinancialSubObject(originEntry);
            if (originEntrySubObject != null) {
                // Exists
                if (!originEntrySubObject.isActive()) {
                    // if NOT active, set it to dashes
                    workingEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                    return null;
                }
            }
            else {
                // Doesn't exist
                workingEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
                return null;
            }
        }
        workingEntry.setFinancialSubObjectCode(originEntry.getFinancialSubObjectCode());
        return null;
    }

    /**
     * Validates the balance type of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateBalanceType(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateBalanceType() started");
        
        // balance type IS NOT empty
        String balanceTypeCode = originEntry.getFinancialBalanceTypeCode();
        if (StringUtils.hasText(balanceTypeCode)) {
            BalanceType originEntryBalanceType = referenceLookup.get().getBalanceType(originEntry);
            if (originEntryBalanceType == null) {
                // balance type IS NOT valid
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND, " (" + balanceTypeCode + ")", Message.TYPE_FATAL);
            
            } else if (!originEntryBalanceType.isActive()) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_BALANCE_TYPE_NOT_ACTIVE, balanceTypeCode, Message.TYPE_FATAL);
            } else {
                // balance type IS valid
                if (originEntryBalanceType.isFinancialOffsetGenerationIndicator()) {
                    // entry IS an offset
                    if (originEntry.getTransactionLedgerEntryAmount().isNegative()) {
                        // it's an INVALID non-budget transaction
                        return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_TRANS_CANNOT_BE_NEGATIVE_IF_OFFSET, Message.TYPE_FATAL);
                    }
                    else {
                        // it's a VALID non-budget transaction
                        if (!originEntry.isCredit() && !originEntry.isDebit()) { // entries requiring an offset must be either a
                            // debit or a credit
                            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DC_INDICATOR_MUST_BE_D_OR_C, originEntry.getTransactionDebitCreditCode(), Message.TYPE_FATAL);
                        }
                        else {
                            workingEntry.setFinancialBalanceTypeCode(balanceTypeCode);
                        }
                    }
                }
                else {
                    // entry IS NOT an offset, means it's a budget transaction
                    if (StringUtils.hasText(originEntry.getTransactionDebitCreditCode())) {
                        return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DC_INDICATOR_MUST_BE_EMPTY, originEntry.getTransactionDebitCreditCode(), Message.TYPE_FATAL);
                    }
                    else {
                        if (originEntry.isCredit() || originEntry.isDebit()) {
                            // budget transactions must be neither debit nor credit
                            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DC_INDICATOR_MUST_BE_NEITHER_D_NOR_C, originEntry.getTransactionDebitCreditCode(), Message.TYPE_FATAL);
                        }
                        else {
                            // it's a valid budget transaction
                            workingEntry.setFinancialBalanceTypeCode(balanceTypeCode);
                        }
                    }
                }
            }
        }
        else {
            // balance type IS empty. We can't set it if the year isn't set
            SystemOptions workingEntryOption = referenceLookup.get().getSystemOptions(workingEntry);

            if (workingEntryOption != null) {
                workingEntry.setFinancialBalanceTypeCode(workingEntryOption.getActualFinancialBalanceTypeCd());
            }
            else {
                //TODO: shawn - need to change to use MessageBuilder
                return new Message("Unable to set balance type code when year is unknown: " + workingEntry.getUniversityFiscalYear(), Message.TYPE_FATAL);
            }
        }
        return null;
    }

    /**
     * Validates the period code of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @param universityRunDate the university date when this scrubber process is being run
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateUniversityFiscalPeriodCode(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateUniversityFiscalPeriodCode() started");
        
        String periodCode = originEntry.getUniversityFiscalPeriodCode();
        if (!StringUtils.hasText(periodCode)) {
            if (!originEntry.getFinancialBalanceTypeCode().equals(KFSConstants.BALANCE_TYPE_A21)){
                if (universityRunDate.getAccountingPeriod().isOpen()) {
                    
                    workingEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
                    workingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
                    
                    // shawn - for displaying updated value on report
//                    originEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
//                    originEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
                    
                }
                else {
                    return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_CLOSED, " (year " + universityRunDate.getUniversityFiscalYear() + ", period " + universityRunDate.getUniversityFiscalAccountingPeriod(), Message.TYPE_FATAL);
                }
            } else {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_FISCAL_PERIOD_CANNOT_BE_NULL_BALANCE_TYPE_A2, Message.TYPE_FATAL);
            }


        }
        else {
            AccountingPeriod originEntryAccountingPeriod = referenceLookup.get().getAccountingPeriod(originEntry);
            if (originEntryAccountingPeriod == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND, periodCode, Message.TYPE_FATAL);
            }
            else if (!originEntryAccountingPeriod.isActive()) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_ACTIVE, periodCode, Message.TYPE_FATAL);
            }

            workingEntry.setUniversityFiscalPeriodCode(periodCode);
        }

        return null;
    }

    /**
     * If the encumbrance update code = R, ref doc number must exist, ref doc type must be valid and ref origin code must be valid.
     * If encumbrance update code is not R, and ref doc number is empty, make sure ref doc number, ref doc type and ref origin code
     * are null. If encumbrance update code is not R and the ref doc number has a value, ref doc type must be valid and ref origin
     * code must be valid.
     * 
     * @param originEntry the origin entry to check
     * @param workingEntryInfo the copy of the entry to move valid data into
     * @return a Message if an error was encountered, otherwise null
     */
    private List<Message> validateReferenceDocumentFields(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateReferenceDocument() started");

        // 3148 of cobol
        
        List<Message> errors = new ArrayList(); 
        
        boolean numberNullIndicator = !StringUtils.hasText(originEntry.getReferenceFinancialDocumentNumber());
        boolean typeCodeNullIndicator = !StringUtils.hasText(originEntry.getReferenceFinancialDocumentTypeCode());
        boolean originCodeNullIndicator = !StringUtils.hasText(originEntry.getReferenceFinancialSystemOriginationCode());

        // shawn - do we need this?
        boolean editReference = true;
        if (numberNullIndicator) {
            workingEntry.setReferenceFinancialDocumentNumber(null);
            workingEntry.setReferenceFinancialDocumentTypeCode(null);
            workingEntry.setReferenceFinancialSystemOriginationCode(null);

            if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode())) {
                errors.add(MessageBuilder.buildMessage(KFSKeyConstants.ERROR_REF_DOC_NOT_BE_SPACE, Message.TYPE_FATAL));
            }
        }
         else {
            workingEntry.setReferenceFinancialDocumentNumber(originEntry.getReferenceFinancialDocumentNumber());

            if (!typeCodeNullIndicator){
                // Validate reference document type
                GeneralLedgerInputType originEntryReferenceInputType = referenceLookup.get().getReferenceGeneralLedgerInputType(originEntry);
                if (originEntryReferenceInputType != null) {
                    workingEntry.setReferenceFinancialDocumentTypeCode(originEntry.getReferenceFinancialDocumentTypeCode());
                }
                else {
                    errors.add(MessageBuilder.buildMessage(KFSKeyConstants.ERROR_REFERENCE_DOCUMENT_TYPE_NOT_FOUND, originEntry.getReferenceFinancialDocumentTypeCode(), Message.TYPE_FATAL));
                }
            } else {
                errors.add(MessageBuilder.buildMessage(KFSKeyConstants.ERROR_REFERENCE_FIELDS, " " + KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE  + " is missing.", Message.TYPE_FATAL));
            }

            if (!originCodeNullIndicator){
                // Validate reference origin code
                OriginationCode oc = referenceLookup.get().getOriginationCode(originEntry);
                if (oc != null) {
                    workingEntry.setReferenceFinancialSystemOriginationCode(originEntry.getReferenceFinancialSystemOriginationCode());
                }
                else {
                    errors.add(MessageBuilder.buildMessage(KFSKeyConstants.ERROR_REFERENCE_ORIGINATION_CODE_NOT_FOUND, " (" + originEntry.getReferenceFinancialSystemOriginationCode() + ")", Message.TYPE_FATAL));
                }
            } else {
                errors.add(MessageBuilder.buildMessage(KFSKeyConstants.ERROR_REFERENCE_FIELDS, " " + KFSPropertyConstants.REFERENCE_FINANCIAL_SYSTEM_ORIGINATION_CODE + " is missing.", Message.TYPE_FATAL));
            }
        }

        BalanceType workingEntryBalanceType = referenceLookup.get().getBalanceType(workingEntry);

        ObjectType workingEntryObjectType = referenceLookup.get().getObjectType(workingEntry);

        if (workingEntryBalanceType == null || workingEntryObjectType == null) {
            // We are unable to check this because the balance type or object type is invalid.
            // It would be nice if we could still validate the entry, but we can't.
            return errors;
        }

        if (workingEntryBalanceType.isFinBalanceTypeEncumIndicator() && !workingEntryObjectType.isFundBalanceIndicator()) {
            if ((KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode())) || (KFSConstants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(originEntry.getTransactionEncumbranceUpdateCode())) || (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode()))) {
                workingEntry.setTransactionEncumbranceUpdateCode(originEntry.getTransactionEncumbranceUpdateCode());
            }
            else {
                errors.add(MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ENC_UPDATE_CODE_NOT_DRN, " (" + originEntry.getTransactionEncumbranceUpdateCode() + ")", Message.TYPE_FATAL));
            }
        }
        else {
            workingEntry.setTransactionEncumbranceUpdateCode(null);
        }
        return errors;
    }

    /**
     * Validates the entry's transaction amount
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    private Message validateTransactionAmount(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateTransactionAmount() started");

        KualiDecimal amount = originEntry.getTransactionLedgerEntryAmount();
        BalanceType originEntryBalanceType = referenceLookup.get().getBalanceType(originEntry);

        if (originEntryBalanceType == null) {
            // We can't validate the amount without a balance type code
            return null;
        }

        if (originEntryBalanceType.isFinancialOffsetGenerationIndicator()) {
            if (amount.isPositive() || amount.isZero()) {
                workingEntry.setTransactionLedgerEntryAmount(originEntry.getTransactionLedgerEntryAmount());
            }
            else {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_NEGATIVE_AMOUNT, amount.toString(), Message.TYPE_FATAL);
            }
            if (StringHelper.isEmpty(originEntry.getTransactionDebitCreditCode())) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_NEITHER_D_NOR_C, originEntry.getTransactionDebitCreditCode(), Message.TYPE_FATAL);
            }
            if (ObjectHelper.isOneOf(originEntry.getTransactionDebitCreditCode(), debitOrCredit)) {
                workingEntry.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());
            }
            else {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_NEITHER_D_NOR_C, originEntry.getTransactionDebitCreditCode(), Message.TYPE_FATAL);
            }
        }
        else {
            if ((originEntry.getTransactionDebitCreditCode() == null) || (" ".equals(originEntry.getTransactionDebitCreditCode())) || ("".equals(originEntry.getTransactionDebitCreditCode()))) {
                workingEntry.setTransactionDebitCreditCode(KFSConstants.GL_BUDGET_CODE);
            }
            else {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_MUST_BE_SPACE, originEntry.getTransactionDebitCreditCode(), Message.TYPE_FATAL);
            }
        }
        return null;
    }
        
    private Message validateDescription(OriginEntry originEntry){
        
        if (originEntry.getTransactionLedgerEntryDescription().trim().equals(KFSConstants.EMPTY_STRING)){
            
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_DESCRIPTION_CANNOT_BE_BLANK, Message.TYPE_FATAL);
        }
        
        return null;
    }


    /**
     * 
     * This method...
     * @param offsetAccountExpirationTime
     * @param runCalendar
     * @return
     */
    private boolean isExpired(long offsetAccountExpirationTime, Calendar runCalendar) {

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(offsetAccountExpirationTime);

        int expirationYear = expirationDate.get(Calendar.YEAR);
        int runYear = runCalendar.get(Calendar.YEAR);
        int expirationDoy = expirationDate.get(Calendar.DAY_OF_YEAR);
        int runDoy = runCalendar.get(Calendar.DAY_OF_YEAR);

        return (expirationYear < runYear) || (expirationYear == runYear && expirationDoy < runDoy);
    }

    public void setUniversityDateDao(UniversityDateDao udd) {
        universityDateDao = udd;
    }

    public void setKualiConfigurationService(KualiConfigurationService service) {
        kualiConfigurationService = service;
    }

    public void setPersistenceService(PersistenceService ps) {
        persistenceService = ps;
    }

    public void setAccountService(AccountService as) {
        accountService = as;
    }

    public void setOriginationCodeService(OriginationCodeService ocs) {
        originationCodeService = ocs;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets a service that allows origin entries to retrieve references
     * @param the implementation of OriginEntryLookupService to set
     * @see org.kuali.module.gl.service.ScrubberValidator#setReferenceLookup(org.kuali.module.gl.service.OriginEntryLookupService)
     */
    public void setReferenceLookup(OriginEntryLookupService originEntryLookupService) {
        this.referenceLookup.set(originEntryLookupService);
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBalanceTypService(BalanceTypService balanceTypService) {
        this.balanceTypService = balanceTypService;
    }
}

