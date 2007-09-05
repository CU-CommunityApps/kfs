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
package org.kuali.kfs.rules;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.rules.LedgerPostingDocumentRuleBase;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.service.SufficientFundsService;

/**
 * This class contains a helper method used to implement a rule for the CashManagementDocument (a FinancialDocument) as well as to
 * implement rules for TransactionalDocuments.
 */
public class GeneralLedgerPostingDocumentRuleBase extends LedgerPostingDocumentRuleBase {
    /**
     * Logger for this class
     */
    private static final Logger LOG = Logger.getLogger(GeneralLedgerPostingDocumentRuleBase.class);

    /**
     * This populates an GeneralLedgerPendingEntry offsetEntry object instance with values that differ from the values supplied in
     * the explicit entry that it was cloned from. Note that the entries do not contain BOs now.
     * 
     * @param universityFiscalYear
     * @param explicitEntry
     * @param sequenceHelper
     * @param offsetEntry Cloned from the explicit entry
     * @return whether the offset generation is successful
     */
    protected boolean populateOffsetGeneralLedgerPendingEntry(Integer universityFiscalYear, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("populateOffsetGeneralLedgerPendingEntry(Integer, GeneralLedgerPendingEntry, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - start");

        boolean success = true;

        // lookup offset object info
        OffsetDefinition offsetDefinition = SpringContext.getBean(OffsetDefinitionService.class).getByPrimaryId(universityFiscalYear, explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        if (ObjectUtils.isNull(offsetDefinition)) {
            success = false;
            GlobalVariables.getErrorMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_OFFSET_DEFINITION, universityFiscalYear.toString(), explicitEntry.getChartOfAccountsCode(), explicitEntry.getFinancialDocumentTypeCode(), explicitEntry.getFinancialBalanceTypeCode());
        }
        else {
            OffsetAccount flexibleOffsetAccount = SpringContext.getBean(FlexibleOffsetAccountService.class).getByPrimaryIdIfEnabled(explicitEntry.getChartOfAccountsCode(), explicitEntry.getAccountNumber(), getOffsetFinancialObjectCode(offsetDefinition));
            flexOffsetAccountIfNecessary(flexibleOffsetAccount, offsetEntry);
        }

        // update offset entry fields that are different from the explicit entry that it was created from
        offsetEntry.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
        offsetEntry.setTransactionDebitCreditCode(getOffsetEntryDebitCreditCode(explicitEntry));

        String offsetObjectCode = getOffsetFinancialObjectCode(offsetDefinition);
        offsetEntry.setFinancialObjectCode(offsetObjectCode);
        if (offsetObjectCode.equals(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode())) {
            // no BO, so punt
            offsetEntry.setAcctSufficientFundsFinObjCd(AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode());
        }
        else {
            // Need current ObjectCode and Account BOs to get sufficient funds code. (Entries originally have no BOs.)
            // todo: private or other methods to get these BOs, instead of using the entry and leaving some BOs filled in?
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
            offsetEntry.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            ObjectCode financialObject = offsetEntry.getFinancialObject();
            // The ObjectCode reference may be invalid because a flexible offset account changed its chart code.
            if (ObjectUtils.isNull(financialObject)) {
                throw new ReferentialIntegrityException("offset object code " + offsetEntry.getUniversityFiscalYear() + "-" + offsetEntry.getChartOfAccountsCode() + "-" + offsetEntry.getFinancialObjectCode());
            }
            offsetEntry.setAcctSufficientFundsFinObjCd(SpringContext.getBean(SufficientFundsService.class).getSufficientFundsObjectCode(financialObject, offsetEntry.getAccount().getAccountSufficientFundsCode()));
        }

        offsetEntry.setFinancialObjectTypeCode(getOffsetFinancialObjectTypeCode(offsetDefinition));
        offsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        offsetEntry.setTransactionEntryOffsetIndicator(true);
        offsetEntry.setTransactionLedgerEntryDescription(KFSConstants.GL_PE_OFFSET_STRING);

        LOG.debug("populateOffsetGeneralLedgerPendingEntry(Integer, GeneralLedgerPendingEntry, GeneralLedgerPendingEntrySequenceHelper, GeneralLedgerPendingEntry) - end");
        return success;
    }

    /**
     * Applies the given flexible offset account to the given offset entry. Does nothing if flexibleOffsetAccount is null or its COA
     * and account number are the same as the offset entry's.
     * 
     * @param flexibleOffsetAccount may be null
     * @param offsetEntry may be modified
     */
    private static void flexOffsetAccountIfNecessary(OffsetAccount flexibleOffsetAccount, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("flexOffsetAccountIfNecessary(OffsetAccount, GeneralLedgerPendingEntry) - start");

        if (flexibleOffsetAccount == null) {
            LOG.debug("flexOffsetAccountIfNecessary(OffsetAccount, GeneralLedgerPendingEntry) - end");
            return; // They are not required and may also be disabled.
        }
        String flexCoa = flexibleOffsetAccount.getFinancialOffsetChartOfAccountCode();
        String flexAccountNumber = flexibleOffsetAccount.getFinancialOffsetAccountNumber();
        if (flexCoa.equals(offsetEntry.getChartOfAccountsCode()) && flexAccountNumber.equals(offsetEntry.getAccountNumber())) {
            LOG.debug("flexOffsetAccountIfNecessary(OffsetAccount, GeneralLedgerPendingEntry) - end");
            return; // no change, so leave sub-account as is
        }
        if (ObjectUtils.isNull(flexibleOffsetAccount.getFinancialOffsetAccount())) {
            throw new ReferentialIntegrityException("flexible offset account " + flexCoa + "-" + flexAccountNumber);
        }
        offsetEntry.setChartOfAccountsCode(flexCoa);
        offsetEntry.setAccountNumber(flexAccountNumber);
        // COA and account number are part of the sub-account's key, so the original sub-account would be invalid.
        offsetEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());

        LOG.debug("flexOffsetAccountIfNecessary(OffsetAccount, GeneralLedgerPendingEntry) - end");
    }

    /**
     * Helper method that determines the offset entry's financial object code.
     * 
     * @param offsetDefinition
     * @return String
     */
    protected String getOffsetFinancialObjectCode(OffsetDefinition offsetDefinition) {
        LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - start");

        if (null != offsetDefinition) {
            String returnString = getEntryValue(offsetDefinition.getFinancialObjectCode(), AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode());
            LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - end");
            return returnString;
        }
        else {
            LOG.debug("getOffsetFinancialObjectCode(OffsetDefinition) - end");
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectCode();
        }

    }

    /**
     * Helper method that determines the offset entry's financial object type code.
     * 
     * @param offsetDefinition
     * @return String
     */
    protected String getOffsetFinancialObjectTypeCode(OffsetDefinition offsetDefinition) {
        LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - start");

        if (null != offsetDefinition && null != offsetDefinition.getFinancialObject()) {
            String returnString = getEntryValue(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode(), AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType());
            LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - end");
            return returnString;
        }
        else {
            LOG.debug("getOffsetFinancialObjectTypeCode(OffsetDefinition) - end");
            return AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialObjectType();
        }

    }

    /**
     * Helper method that determines the debit/credit code for the offset entry. If the explicit was a debit, the offset is a
     * credit. Otherwise, it's opposite.
     * 
     * @param explicitEntry
     * @return String
     */
    protected String getOffsetEntryDebitCreditCode(GeneralLedgerPendingEntry explicitEntry) {
        LOG.debug("getOffsetEntryDebitCreditCode(GeneralLedgerPendingEntry) - start");

        String offsetDebitCreditCode = KFSConstants.GL_BUDGET_CODE;
        if (KFSConstants.GL_DEBIT_CODE.equals(explicitEntry.getTransactionDebitCreditCode())) {
            offsetDebitCreditCode = KFSConstants.GL_CREDIT_CODE;
        }
        else if (KFSConstants.GL_CREDIT_CODE.equals(explicitEntry.getTransactionDebitCreditCode())) {
            offsetDebitCreditCode = KFSConstants.GL_DEBIT_CODE;
        }

        LOG.debug("getOffsetEntryDebitCreditCode(GeneralLedgerPendingEntry) - end");
        return offsetDebitCreditCode;
    }

    /**
     * A helper method that checks the intended target value for null and empty strings. If the intended target value is not null or
     * an empty string, it returns that value, ohterwise, it returns a backup value.
     * 
     * @param targetValue
     * @param backupValue
     * @return String
     */
    protected final String getEntryValue(String targetValue, String backupValue) {
        LOG.debug("getEntryValue(String, String) - start");

        if (StringUtils.isNotBlank(targetValue)) {
            LOG.debug("getEntryValue(String, String) - end");
            return targetValue;
        }
        else {
            LOG.debug("getEntryValue(String, String) - end");
            return backupValue;
        }
    }
}
