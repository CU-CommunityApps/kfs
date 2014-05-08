/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.CashieringItemInProcess;
import org.kuali.kfs.fp.businessobject.CashieringTransaction;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.businessobject.CoinDetail;
import org.kuali.kfs.fp.businessobject.CurrencyDetail;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.businessobject.DepositCashReceiptControl;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.service.CashManagementService;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.document.validation.CashManagingRule;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentStatusCodes.CashReceipt;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.BankCodeValidation;
import org.kuali.kfs.sys.document.validation.impl.GeneralLedgerPostingDocumentRuleBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Business rule(s) applicable to Cash Management Document.
 */
public class CashManagementDocumentRule extends GeneralLedgerPostingDocumentRuleBase implements CashManagingRule {
    private static final Logger LOG = Logger.getLogger(CashManagementDocumentRule.class);

    /**
     * Overrides to validate that the person saving the document is the initiator, validates that the cash drawer is open for
     * initial creation, validates that the cash drawer for the specific verification unit is closed for subsequent saves, and
     * validates that the associate cash receipts are still verified.
     *
     * @param document submitted cash management document
     * @return true if there are no issues processing rules associated with saving a cash management document
     * @see org.kuali.rice.krad.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        CashManagementDocument cmd = (CashManagementDocument) document;

        // verify the cash drawer for the verification unit is closed for post-initialized saves
        verifyCashDrawerForVerificationUnitIsOpenForPostInitiationSaves(cmd);

        // verify deposits
        isValid &= validateDeposits(cmd);

        return isValid;
    }

    /**
     * Overrides to validate that all cash receipts are deposited when routing cash management document.
     *
     * @param document submitted cash management document
     * @return true if there are no issues processing rules associated with routing a cash management document
     * @see org.kuali.rice.krad.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;

        CashManagementDocument cmDoc = (CashManagementDocument) document;
        isValid &= verifyAllVerifiedCashReceiptsDeposited(cmDoc);

        return isValid;
    }

    /**
     * This method checks to make sure that the current system user is the person that initiated this document in the first place.
     *
     * @param cmd submitted cash management document
     */
    protected void verifyUserIsDocumentInitiator(CashManagementDocument cmd) {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (cmd.getDocumentHeader() != null && cmd.getDocumentHeader().getWorkflowDocument() != null) {
            String cmdInitiatorNetworkId = cmd.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
            if (!cmdInitiatorNetworkId.equalsIgnoreCase(currentUser.getPrincipalName())) {
                throw new IllegalStateException("The current user (" + currentUser.getPrincipalName() + ") is not the individual (" + cmdInitiatorNetworkId + ") that initiated this document.");
            }
        }
    }

    /**
     * This method checks to make sure that the cash drawer is closed for the associated verification unit, for post initiation
     * saves for CashManagementDocuments which don't have Final
     *
     * @param cmd submitted cash management document
     */
    protected void verifyCashDrawerForVerificationUnitIsOpenForPostInitiationSaves(CashManagementDocument cmd) {
        if (cmd.getDocumentHeader() != null && cmd.getDocumentHeader().getWorkflowDocument() != null && cmd.getDocumentHeader().getWorkflowDocument() != null) {
            if (cmd.getDocumentHeader().getWorkflowDocument().isSaved()) {
                // now verify that the associated cash drawer is in the appropriate state
                CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByCampusCode(cmd.getCampusCode());
                if (cd == null) {
                    // we got the rule without having a cash drawer?  freaky...
                    throw new RuntimeException("No cash drawer exists for campus code "+cmd.getCampusCode()+"; please create on via the Cash Drawer Maintenance Document before attemping to create a CashManagementDocument for campus "+cmd.getCampusCode());
                }
                if (!cmd.hasFinalDeposit()) {
                    if (!cd.isOpen()) {
                        throw new IllegalStateException("The cash drawer for verification unit \"" + cd.getCampusCode() + "\" is closed.  It should be open when a cash management document for that verification unit is open and being saved.");
                    }
                }
                else {
                    if (!cd.isLocked()) {
                        throw new IllegalStateException("The cash drawer for verification unit \"" + cd.getCampusCode() + "\" is closed.  It should be open when a cash management document for that verification unit is open and being saved.");
                    }
                }
            }
        }
    }


    /**
     * Validates all Deposits associated with the given CashManagementDocument
     *
     * @param cmd submitted cash management document
     * @return true if all deposits in a cash management are valid
     */
    protected boolean validateDeposits(CashManagementDocument cmd) {
        boolean isValid = true;
        boolean isInitiated = cmd.getDocumentHeader().getWorkflowDocument().isInitiated();

        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        int index = 0;
        for (Iterator deposits = cmd.getDeposits().iterator(); deposits.hasNext(); index++) {
            Deposit deposit = (Deposit) deposits.next();

            GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DEPOSIT + "[" + index + "]");
            isValid &= validateDeposit(deposit, isInitiated);
            GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.DEPOSIT + "[" + index + "]");
        }

        GlobalVariables.getMessageMap().removeFromErrorPath(KFSPropertyConstants.DOCUMENT);

        return isValid;
    }

    /**
     * If documentIsInitiated, performs complete dataDictionary-driven validation of the given Deposit. Unconditionally validates
     * the CashReceipts associated with the given Deposit.
     *
     * @param deposit individual deposit from cash management document
     * @param documentIsInitiated if document is initiated
     * @return true if deposit is valid
     */
    protected boolean validateDeposit(Deposit deposit, boolean documentIsInitiated) {
        boolean isValid = true;

        verifyCashReceipts(deposit, documentIsInitiated);

        if (!documentIsInitiated) {
            isValid = performDataDictionaryValidation(deposit);
        }

        return isValid;
    }


    private static final List INITIATED_STATES = Arrays.asList(new String[] { CashReceipt.VERIFIED });
    private static final List UNINITIATED_STATES = Arrays.asList(new String[] { CashReceipt.INTERIM, CashReceipt.FINAL });

    /**
     * Verifies that all CashReceipts associated with the given document are of an appropriate status for the given
     * CashManagementDocument state
     *
     * @param deposit deposit from cash management document
     * @param documentIsInitiated if document is initiated
     */
    protected void verifyCashReceipts(Deposit deposit, boolean documentIsInitiated) {
        List desiredCRStates = null;
        if (documentIsInitiated) {
            desiredCRStates = INITIATED_STATES;
        }
        else {
            desiredCRStates = UNINITIATED_STATES;
        }

        for (Iterator depositCashReceiptControls = deposit.getDepositCashReceiptControl().iterator(); depositCashReceiptControls.hasNext();) {
            DepositCashReceiptControl depositCashReceiptControl = (DepositCashReceiptControl) depositCashReceiptControls.next();
            CashReceiptDocument cashReceipt = depositCashReceiptControl.getCashReceiptDocument();
            String crState = cashReceipt.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode();
            if (!desiredCRStates.contains(crState)) {
                throw new IllegalStateException("Cash receipt document number " + cashReceipt.getDocumentNumber() + " is not in an appropriate state for the associated CashManagementDocument to be submitted.");
            }
        }
    }

    /**
     * Verifies that all verified cash receipts have been deposited
     *
     * @param cmDoc the cash management document that is about to be routed
     * @return true if there are no outstanding verified cash receipts that are not part of a deposit, false if otherwise
     */
    protected boolean verifyAllVerifiedCashReceiptsDeposited(CashManagementDocument cmDoc) {
        boolean allCRsDeposited = true;
        CashManagementService cms = SpringContext.getBean(CashManagementService.class);
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(cmDoc.getCampusCode(), KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        for (Object o : verifiedReceipts) {
            if (!cms.verifyCashReceiptIsDeposited(cmDoc, (CashReceiptDocument) o)) {
                allCRsDeposited = false;
                GlobalVariables.getMessageMap().putError(KFSConstants.CASH_MANAGEMENT_DEPOSIT_ERRORS, KFSKeyConstants.CashManagement.ERROR_NON_DEPOSITED_VERIFIED_CASH_RECEIPT, new String[] { ((CashReceiptDocument) o).getDocumentNumber() });
            }
        }
        return allCRsDeposited;
    }

    /**
     * Performs complete, recursive dataDictionary-driven validation of the given Deposit.
     *
     * @param deposit deposit from cash management document
     * @return true if deposit is validated against data dictionary entry
     */
    protected boolean performDataDictionaryValidation(Deposit deposit) {
        // check for required fields
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(deposit);

        // validate foreign-key relationships
        //KFSMI-798 - refresh() changed to refreshNonUpdateableReferences()
        //Deposit has updatable references, but for validation we do not need to refresh the updatable references.
        //E.g. updatable collections - they might have been set by the user and we would not want to overwrite their changes.
        deposit.refreshNonUpdateableReferences();

        // validate bank code
        BankCodeValidation.validate(deposit.getDepositBankCode(), KFSPropertyConstants.DEPOSIT_BANK_CODE, true, false);

        return GlobalVariables.getMessageMap().hasNoErrors();
    }

    /**
     * Processes the checks to validate that the application of the given cashiering transaction will be valid
     * @see org.kuali.kfs.fp.document.validation.CashManagingRule#processCashieringTransactionApplication(org.kuali.kfs.fp.businessobject.CashieringTransaction)
     */
    @Override
    public boolean processCashieringTransactionApplication(CashDrawer cashDrawer, CashieringTransaction cashieringTransaction) {
        boolean success = true;
        success &= checkMoneyInNoNegatives(cashieringTransaction);
        success &= checkMoneyOutNoNegatives(cashieringTransaction);
        success &= checkAllPaidBackItemsInProcess(cashieringTransaction);
        success &= checkNewItemInProcessDoesNotExceedCashDrawer(cashDrawer, cashieringTransaction);
        success &= checkNewItemInProcessInPast(cashieringTransaction);
        success &= checkTransactionCheckTotalDoesNotExceedCashDrawer(cashDrawer, cashieringTransaction);
        success &= checkItemInProcessIsNotPayingOffItemInProcess(cashieringTransaction);
        if (success) {
            success = checkEnoughCashForMoneyOut(cashDrawer, cashieringTransaction);
        }
        if (success) {
            success &= checkMoneyInMoneyOutBalance(cashieringTransaction);
        }
        return success;
    }

    /**
     * Returns true if none of the denomination amounts in the given currencyDetail is negative.
     *
     * @param currencyDetail the given currencyDetail to be checked.
     * @param propertyPrefix prefix of each denomination amount property
     * @return true if none of the amounts is negative
     */
    protected boolean checkCurrencyNoNegatives(CurrencyDetail currencyDetail, String propertyPrefix) {
        boolean success = true;

        if (currencyDetail.getFinancialDocumentHundredDollarAmount() != null && currencyDetail.getFinancialDocumentHundredDollarAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "hundredDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { currencyDetail.getHundredDollarCount().toString(), "hundred dollar count" });
            success = false;
        }
        if (currencyDetail.getFinancialDocumentFiftyDollarAmount() != null && currencyDetail.getFinancialDocumentFiftyDollarAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "fiftyDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { currencyDetail.getFiftyDollarCount().toString(), "fifty dollar count" });
            success = false;
        }
        if (currencyDetail.getFinancialDocumentTwentyDollarAmount() != null && currencyDetail.getFinancialDocumentTwentyDollarAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "twentyDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { currencyDetail.getTwentyDollarCount().toString(), "twenty dollar count" });
            success = false;
        }
        if (currencyDetail.getFinancialDocumentTenDollarAmount() != null && currencyDetail.getFinancialDocumentTenDollarAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "tenDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { currencyDetail.getTenDollarCount().toString(), "ten dollar count" });
            success = false;
        }
        if (currencyDetail.getFinancialDocumentFiveDollarAmount() != null && currencyDetail.getFinancialDocumentFiveDollarAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "fiveDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { currencyDetail.getFiveDollarCount().toString(), "five dollar count" });
            success = false;
        }
        if (currencyDetail.getFinancialDocumentTwoDollarAmount() != null && currencyDetail.getFinancialDocumentTwoDollarAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "twoDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { currencyDetail.getTwoDollarCount().toString(), "two dollar count" });
            success = false;
        }
        if (currencyDetail.getFinancialDocumentOneDollarAmount() != null && currencyDetail.getFinancialDocumentOneDollarAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "oneDollarCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { currencyDetail.getOneDollarCount().toString(), "one dollar count" });
            success = false;
        }
        if (currencyDetail.getFinancialDocumentOtherDollarAmount() != null && currencyDetail.getFinancialDocumentOtherDollarAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "financialDocumentOtherDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { currencyDetail.getFinancialDocumentOtherDollarAmount().toString(), "other dollar amount" });
            success = false;
        }

        return success;
    }

    /**
     * Returns true if none of the denomination amounts in the given coinDetail is negative.
     *
     * @param coinDetail the given coinDetail to be checked.
     * @param propertyPrefix prefix of each denomination amount property
     * @return true if none of the amounts is negative
     */
    protected boolean checkCoinNoNegatives(CoinDetail coinDetail, String propertyPrefix) {
        boolean success = true;
        Integer coinCount;
        Integer rollCount;

        // since we now introduced roll count for coins, neither coin or roll count shall be negative; since we compute coin/roll count from amount, if amount is negative,
        // at least one of roll or coin count will be negative, while the other could be 0 or negative; we shall put error on whichever are negative.
        if (coinDetail.getFinancialDocumentHundredCentAmount() != null && coinDetail.getFinancialDocumentHundredCentAmount().isNegative()) {
            if ((coinCount = coinDetail.getHundredCentCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "hundredCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { coinCount.toString(), "hundred cent count" });
            }
            if ((rollCount = coinDetail.getHundredCentRollCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "hundredCentRollCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { rollCount.toString(), "hundred cent roll count" });
            }
            success = false;
        }
        if (coinDetail.getFinancialDocumentFiftyCentAmount() != null && coinDetail.getFinancialDocumentFiftyCentAmount().isNegative()) {
            if ((coinCount = coinDetail.getFiftyCentCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "fiftyCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { coinCount.toString(), "fifty cent count" });
            }
            if ((rollCount = coinDetail.getFiftyCentRollCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "fiftyCentRollCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { rollCount.toString(), "fifty cent roll count" });
            }
            success = false;
        }
        if (coinDetail.getFinancialDocumentTwentyFiveCentAmount() != null && coinDetail.getFinancialDocumentTwentyFiveCentAmount().isNegative()) {
            if ((coinCount = coinDetail.getTwentyFiveCentCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "twentyFiveCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { coinCount.toString(), "twentyFive cent count" });
            }
            if ((rollCount = coinDetail.getTwentyFiveCentRollCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "twentyFiveCentRollCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { rollCount.toString(), "twentyFive cent roll count" });
            }
            success = false;
        }
        if (coinDetail.getFinancialDocumentTenCentAmount() != null && coinDetail.getFinancialDocumentTenCentAmount().isNegative()) {
            if ((coinCount = coinDetail.getTenCentCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "tenCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { coinCount.toString(), "ten cent count" });
            }
            if ((rollCount = coinDetail.getTenCentRollCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "tenCentRollCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { rollCount.toString(), "ten cent roll count" });
            }
            success = false;
        }
        if (coinDetail.getFinancialDocumentFiveCentAmount() != null && coinDetail.getFinancialDocumentFiveCentAmount().isNegative()) {
            if ((coinCount = coinDetail.getFiveCentCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "fiveCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { coinCount.toString(), "five cent count" });
            }
            if ((rollCount = coinDetail.getFiveCentRollCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "fiveCentRollCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { rollCount.toString(), "five cent roll count" });
            }
            success = false;
        }
        if (coinDetail.getFinancialDocumentOneCentAmount() != null && coinDetail.getFinancialDocumentOneCentAmount().isNegative()) {
            if ((coinCount = coinDetail.getOneCentCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "oneCentCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { coinCount.toString(), "one cent count" });
            }
            if ((rollCount = coinDetail.getOneCentRollCount()) < 0) {
                GlobalVariables.getMessageMap().putError(propertyPrefix + "oneCentRollCount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { rollCount.toString(), "one cent roll count" });
            }
            success = false;
        }
        if (coinDetail.getFinancialDocumentOtherCentAmount() != null && coinDetail.getFinancialDocumentOtherCentAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError(propertyPrefix + "financialDocumentOtherCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_NOT_NEGATIVE, new String[] { coinDetail.getFinancialDocumentOtherCentAmount().toString(), "other cent amount" });
            success = false;
        }

        return success;
    }

    /**
     * Returns true if none of the entered money-in amounts (currency and coin) is negative in a cashiering transaction
     *
     * @param trans represents cashiering transaction from document
     * @return true if none of the amounts are negative
     */
    public boolean checkMoneyInNoNegatives(CashieringTransaction trans) {
        boolean success = true;

        // money in currency
        // improved previous code: reduce redundant code by calling the shared method checkCurrencyNoNegatives on trans.getMoneyInCurrency()
        checkCurrencyNoNegatives(trans.getMoneyInCurrency(), "document.currentTransaction.moneyInCurrency.");

        // money in coin
        // improved previous code: reduce redundant code by calling the shared method checkCoinNoNegatives on trans.getMoneyInCoin()
        checkCoinNoNegatives(trans.getMoneyInCoin(), "document.currentTransaction.moneyInCoin.");

        // newItemInProcess amount
        if (trans.getNewItemInProcess() != null && trans.getNewItemInProcess().isPopulated() && trans.getNewItemInProcess().getItemAmount().isNegative()) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.newItemInProcess.itemAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_NEW_ITEM_IN_PROCESS_NOT_NEGATIVE, new String[0]);
            success = false;
        }

        // checks
        int count = 0;
        for (Check check : trans.getMoneyInChecks()) {
            if (check.getAmount() != null && check.getAmount().isNegative()) {
                GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyInChecks[" + count + "].amount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CHECK_AMOUNT_NOT_NEGATIVE, new String[] { check.getAmount().toString(), check.getDescription() });
                success = false;
            }
            count += 1;
        }

        return success;
    }

    /**
     * Returns true if none of the entered money-out amounts (currency and coin) is negative in a cashiering transaction
     *
     * @param trans represents cashiering transaction from document
     * @return true if none of the amounts are negative
     */
    public boolean checkMoneyOutNoNegatives(CashieringTransaction trans) {
        boolean success = true;

        /* FIXME FIXED by KFSCNTRB-1793
         * The previous code on checking money out currency has a bug: there were some typos in several places where getMoneyInCurrency is used instead of getMoneyOutCurrency.
         * Fixed this bug, also improved previous code: reduce redundant code by calling the shared method checkCurrencyNoNegatives on trans.getMoneyOutCurrency()
         */
        // money out currency
        checkCurrencyNoNegatives(trans.getMoneyOutCurrency(), "document.currentTransaction.moneyOutCurrency.");

        // money out coin
        // improved previous code: reduce redundant code by calling the shared method checkCoinNoNegatives on trans.getMoneyOutCoin()
        checkCoinNoNegatives(trans.getMoneyOutCoin(), "document.currentTransaction.moneyOutCoin.");

        // open items in process amounts
        int count = 0;
        if (trans.getOpenItemsInProcess() != null) {
            for (CashieringItemInProcess itemInProc : trans.getOpenItemsInProcess()) {
                if (itemInProc.getCurrentPayment() != null && itemInProc.getCurrentPayment().isNegative()) {
                    GlobalVariables.getMessageMap().putError("document.currentTransaction.openItemsInProcess[" + count + "].currentPayment", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_REDUCED_ITEM_IN_PROCESS_NOT_NEGATIVE, new String[] { itemInProc.getItemIdentifier().toString() });
                    success = false;
                }
                count += 1;
            }
        }

        return success;
    }

    /**
     * Returns true if money-in and money-out are in balance with each other
     *
     * @param trans represents cashiering transaction from cash management document
     * @return true if money-in and money-out are balanced
     */
    public boolean checkMoneyInMoneyOutBalance(CashieringTransaction trans) {
        boolean success = true;
        if (!trans.getMoneyInTotal().equals(trans.getMoneyOutTotal())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyInCurrency.financialDocumentHundredDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_IN_OUT_DO_NOT_BALANCE, new String[0]);
            success = false;
        }
        return success;
    }

    /**
     * This method returns true if none of the coin (1 cent, 5 cents, etc) and cash increments (1 dollar, 2 dollars, 5 dollars etc. )
     * from ( money-in + cash drawer ) exceed the amount for that increment from the money-out.
     *
     * @param cmDoc represents cash management document
     * @param trans represents cash transaction from cash management document
     * @return true if none of the coin and cash increments from money-in + cash drawer excreed amount for increments in money-out
     */
    public boolean checkEnoughCashForMoneyOut(CashDrawer cashDrawer, CashieringTransaction trans) {
        boolean success = true;

        // money out currency
        CurrencyDetail moneyInCurrency = trans.getMoneyInCurrency();
        CurrencyDetail moneyOutCurrency = trans.getMoneyOutCurrency();

        KualiDecimal existingHundredDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentHundredDollarAmount() != null) {
            existingHundredDollarAmount = existingHundredDollarAmount.add(cashDrawer.getFinancialDocumentHundredDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentHundredDollarAmount() != null) {
            existingHundredDollarAmount = existingHundredDollarAmount.add(moneyInCurrency.getFinancialDocumentHundredDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentHundredDollarAmount() != null && existingHundredDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentHundredDollarAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentHundredDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "hundred dollar", moneyOutCurrency.getFinancialDocumentHundredDollarAmount().toString(), cashDrawer.getFinancialDocumentHundredDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingFiftyDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentFiftyDollarAmount() != null) {
            existingFiftyDollarAmount = existingFiftyDollarAmount.add(cashDrawer.getFinancialDocumentFiftyDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentFiftyDollarAmount() != null) {
            existingFiftyDollarAmount = existingFiftyDollarAmount.add(moneyInCurrency.getFinancialDocumentFiftyDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentFiftyDollarAmount() != null && existingFiftyDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentFiftyDollarAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentFiftyDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "fifty dollar", moneyOutCurrency.getFinancialDocumentFiftyDollarAmount().toString(), cashDrawer.getFinancialDocumentFiftyDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingTwentyDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTwentyDollarAmount() != null) {
            existingTwentyDollarAmount = existingTwentyDollarAmount.add(cashDrawer.getFinancialDocumentTwentyDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentTwentyDollarAmount() != null) {
            existingTwentyDollarAmount = existingTwentyDollarAmount.add(moneyInCurrency.getFinancialDocumentTwentyDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentTwentyDollarAmount() != null && existingTwentyDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentTwentyDollarAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentTwentyDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "twenty dollar", moneyOutCurrency.getFinancialDocumentTwentyDollarAmount().toString(), cashDrawer.getFinancialDocumentTwentyDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingTenDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTenDollarAmount() != null) {
            existingTenDollarAmount = existingTenDollarAmount.add(cashDrawer.getFinancialDocumentTenDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentTenDollarAmount() != null) {
            existingTenDollarAmount = existingTenDollarAmount.add(moneyInCurrency.getFinancialDocumentTenDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentTenDollarAmount() != null && existingTenDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentTenDollarAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentTenDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "ten dollar", moneyOutCurrency.getFinancialDocumentTenDollarAmount().toString(), cashDrawer.getFinancialDocumentTenDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingFiveDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentFiveDollarAmount() != null) {
            existingFiveDollarAmount = existingFiveDollarAmount.add(cashDrawer.getFinancialDocumentFiveDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentFiveDollarAmount() != null) {
            existingFiveDollarAmount = existingFiveDollarAmount.add(moneyInCurrency.getFinancialDocumentFiveDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentFiveDollarAmount() != null && existingFiveDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentFiveDollarAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentFiveDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "five dollar", moneyOutCurrency.getFinancialDocumentFiveDollarAmount().toString(), cashDrawer.getFinancialDocumentFiveDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingTwoDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTwoDollarAmount() != null) {
            existingTwoDollarAmount = existingTwoDollarAmount.add(cashDrawer.getFinancialDocumentTwoDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentTwoDollarAmount() != null) {
            existingTwoDollarAmount = existingTwoDollarAmount.add(moneyInCurrency.getFinancialDocumentTwoDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentTwoDollarAmount() != null && existingTwoDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentTwoDollarAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentTwoDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "two dollar", moneyOutCurrency.getFinancialDocumentTwoDollarAmount().toString(), cashDrawer.getFinancialDocumentTwoDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingOneDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentOneDollarAmount() != null) {
            existingOneDollarAmount = existingOneDollarAmount.add(cashDrawer.getFinancialDocumentOneDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentOneDollarAmount() != null) {
            existingOneDollarAmount = existingOneDollarAmount.add(moneyInCurrency.getFinancialDocumentOneDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentOneDollarAmount() != null && existingOneDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentOneDollarAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentOneDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "one dollar", moneyOutCurrency.getFinancialDocumentOneDollarAmount().toString(), cashDrawer.getFinancialDocumentOneDollarAmount().toString() });
            success = false;
        }

        KualiDecimal existingOtherDollarAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentOtherDollarAmount() != null) {
            existingOtherDollarAmount = existingOtherDollarAmount.add(cashDrawer.getFinancialDocumentOtherDollarAmount());
        }
        if (moneyInCurrency.getFinancialDocumentOtherDollarAmount() != null) {
            existingOtherDollarAmount = existingOtherDollarAmount.add(moneyInCurrency.getFinancialDocumentOtherDollarAmount());
        }
        if (moneyOutCurrency.getFinancialDocumentOtherDollarAmount() != null && existingOtherDollarAmount.isLessThan(moneyOutCurrency.getFinancialDocumentOtherDollarAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCurrency.financialDocumentOtherDollarAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "other dollar", moneyOutCurrency.getFinancialDocumentOtherDollarAmount().toString(), cashDrawer.getFinancialDocumentOtherDollarAmount().toString() });
            success = false;
        }

        // money out coin
        CoinDetail moneyOutCoin = trans.getMoneyOutCoin();
        CoinDetail moneyInCoin = trans.getMoneyInCoin();
        KualiDecimal existingHundredCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentHundredCentAmount() != null) {
            existingHundredCentAmount = existingHundredCentAmount.add(cashDrawer.getFinancialDocumentHundredCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentHundredCentAmount() != null) {
            existingHundredCentAmount = existingHundredCentAmount.add(moneyInCoin.getFinancialDocumentHundredCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentHundredCentAmount() != null && existingHundredCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentHundredCentAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentHundredCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "hundred cent", moneyOutCoin.getFinancialDocumentHundredCentAmount().toString(), cashDrawer.getFinancialDocumentHundredCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingFiftyCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentFiftyCentAmount() != null) {
            existingFiftyCentAmount = existingFiftyCentAmount.add(cashDrawer.getFinancialDocumentFiftyCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentFiftyCentAmount() != null) {
            existingFiftyCentAmount = existingFiftyCentAmount.add(moneyInCoin.getFinancialDocumentFiftyCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentFiftyCentAmount() != null && existingFiftyCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentFiftyCentAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentFiftyCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "fifty cent", moneyOutCoin.getFinancialDocumentFiftyCentAmount().toString(), cashDrawer.getFinancialDocumentFiftyCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingTwentyFiveCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTwentyFiveCentAmount() != null) {
            existingTwentyFiveCentAmount = existingTwentyFiveCentAmount.add(cashDrawer.getFinancialDocumentTwentyFiveCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentTwentyFiveCentAmount() != null) {
            existingTwentyFiveCentAmount = existingTwentyFiveCentAmount.add(moneyInCoin.getFinancialDocumentTwentyFiveCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentTwentyFiveCentAmount() != null && existingTwentyFiveCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentTwentyFiveCentAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentTwentyFiveCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "twenty five cent", moneyOutCoin.getFinancialDocumentTwentyFiveCentAmount().toString(), cashDrawer.getFinancialDocumentTwentyFiveCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingTenCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentTenCentAmount() != null) {
            existingTenCentAmount = existingTenCentAmount.add(cashDrawer.getFinancialDocumentTenCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentTenCentAmount() != null) {
            existingTenCentAmount = existingTenCentAmount.add(moneyInCoin.getFinancialDocumentTenCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentTenCentAmount() != null && existingTenCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentTenCentAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentTenCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "ten cent", moneyOutCoin.getFinancialDocumentTenCentAmount().toString(), cashDrawer.getFinancialDocumentTenCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingFiveCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentFiveCentAmount() != null) {
            existingFiveCentAmount = existingFiveCentAmount.add(cashDrawer.getFinancialDocumentFiveCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentFiveCentAmount() != null) {
            existingFiveCentAmount = existingFiveCentAmount.add(moneyInCoin.getFinancialDocumentFiveCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentFiveCentAmount() != null && existingFiveCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentFiveCentAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentFiveCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "five cent", moneyOutCoin.getFinancialDocumentFiveCentAmount().toString(), cashDrawer.getFinancialDocumentFiveCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingOneCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentOneCentAmount() != null) {
            existingOneCentAmount = existingOneCentAmount.add(cashDrawer.getFinancialDocumentOneCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentOneCentAmount() != null) {
            existingOneCentAmount = existingOneCentAmount.add(moneyInCoin.getFinancialDocumentOneCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentOneCentAmount() != null && existingOneCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentOneCentAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentOneCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "one cent", moneyOutCoin.getFinancialDocumentOneCentAmount().toString(), cashDrawer.getFinancialDocumentOneCentAmount().toString() });
            success = false;
        }

        KualiDecimal existingOtherCentAmount = KualiDecimal.ZERO;
        if (cashDrawer.getFinancialDocumentOtherCentAmount() != null) {
            existingOtherCentAmount = existingOtherCentAmount.add(cashDrawer.getFinancialDocumentOtherCentAmount());
        }
        if (moneyInCoin.getFinancialDocumentOtherCentAmount() != null) {
            existingOtherCentAmount = existingOtherCentAmount.add(moneyInCoin.getFinancialDocumentOtherCentAmount());
        }
        if (moneyOutCoin.getFinancialDocumentOtherCentAmount() != null && existingOtherCentAmount.isLessThan(moneyOutCoin.getFinancialDocumentOtherCentAmount())) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.moneyOutCoin.financialDocumentOtherCentAmount", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CASH_OUT_EXCEEDS_DRAWER, new String[] { "other cent", moneyOutCoin.getFinancialDocumentOtherCentAmount().toString(), cashDrawer.getFinancialDocumentOtherCentAmount().toString() });
            success = false;
        }

        return success;
    }

    /**
     * This method returns true if the new item in process does not exceed the current amount in the cash drawer reserves
     *
     * @param cmDoc submitted cash management document
     * @param trans transaction from cash management document
     * @return true if the new item in process does not exceed the current amount in the cash drawer reserves
     */
    public boolean checkNewItemInProcessDoesNotExceedCashDrawer(CashDrawer cashDrawer, CashieringTransaction trans) {
        boolean success = true;

        if (trans.getNewItemInProcess().getItemAmount() != null && trans.getNewItemInProcess().getItemAmount().isGreaterThan(calculateTotalCashDrawerReserves(cashDrawer, trans))) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.newItemInProcess", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_AMOUNT_EXCEEDS_DRAWER, new String[] { "new Item In Process", trans.getNewItemInProcess().getItemAmount().toString(), calculateTotalCashDrawerReserves(cashDrawer, trans).toString() });
            success = false;
        }

        return success;
    }

    /**
     * This method returns true if check total from transaction does not exceed the current amount in the cash drawer reserves
     *
     * @param cmDoc submitted cash management document
     * @param trans transaction from cash management document
     * @return true if check total from transaction does not exceed the current amount in the cash drawer reserves
     */
    public boolean checkTransactionCheckTotalDoesNotExceedCashDrawer(CashDrawer cashDrawer, CashieringTransaction trans) {
        boolean success = true;

        if (trans.getTotalCheckAmount().isGreaterThan(calculateTotalCashDrawerReserves(cashDrawer, trans))) {
            GlobalVariables.getMessageMap().putError("document.currentTransaction.newCheck", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_AMOUNT_EXCEEDS_DRAWER, new String[] { "given checks", trans.getTotalCheckAmount().toString(), calculateTotalCashDrawerReserves(cashDrawer, trans).toString() });
            success = false;
        }

        return success;
    }

    /**
     * This method returns true if the current payment amount for the cashiering item in process does not exceed
     * the actual item amount for the item in process
     *
     * @param itemInProc cashiering item in process
     * @param cashieringItemNumber cashiering item number
     * @return true if the current payment amount for the cashiering item in process does not exceed
     *          the actual item amount for the item in process
     */
    public boolean checkPaidBackItemInProcessDoesNotExceedTotal(CashieringItemInProcess itemInProc, int cashieringItemNumber) {
        boolean success = true;
        if (itemInProc.getCurrentPayment() != null && itemInProc.getCurrentPayment().isGreaterThan(itemInProc.getItemAmount())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.CASHIERING_TRANSACTION_OPEN_ITEM_IN_PROCESS_PROPERTY + "[" + cashieringItemNumber + "]", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_AMOUNT_PAID_BACK_EXCEEDS_AMOUNT_LEFT, new String[] { itemInProc.getItemIdentifier().toString() });
            success = false;
        }
        return success;
    }

    /**
     * This method returns true if a new item in process is populated and none of the open item in process' amounts are greater than zero.
     *
     * @param cmDoc submitted cash management document
     * @param trans transaction from cash management document
     * @return true if a new item in process is populated and none of the open item in process' amounts are greater than zero.
     */
    public boolean checkItemInProcessIsNotPayingOffItemInProcess(CashieringTransaction trans) {
        boolean success = true;
        if (trans.getNewItemInProcess().isPopulated()) {
            int count = 0;
            for (CashieringItemInProcess advance : trans.getOpenItemsInProcess()) {
                if (advance.getCurrentPayment() != null && advance.getCurrentPayment().isGreaterThan(KualiDecimal.ZERO)) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.CASHIERING_TRANSACTION_OPEN_ITEM_IN_PROCESS_PROPERTY + "[" + count + "]", KFSKeyConstants.CashManagement.ERROR_DOCUMENT_CASHIERING_TRANSACTION_CANNOT_PAY_OFF_ADVANCE_WITH_ADVANCE, new String[] { advance.getItemIdentifier().toString() });
                    success = false;
                }
                count += 1;
            }
        }
        return success;
    }

    /**
     * This method returns true if all open items in process amounts do not exceed the total for each specific item's amount total
     *
     * @param trans transaction from cash management document
     * @return true if all open items in process amounts do not exceed the total for each specific item's amount total
     */
    public boolean checkAllPaidBackItemsInProcess(CashieringTransaction trans) {
        boolean success = true;
        int count = 0;
        if (trans.getOpenItemsInProcess() != null) {
            for (CashieringItemInProcess itemInProc : trans.getOpenItemsInProcess()) {
                success &= checkPaidBackItemInProcessDoesNotExceedTotal(itemInProc, count);
                count += 1;
            }
        }
        return success;
    }

    /**
     * This method returns true if the current date is after all new items in process' open dates
     *
     * @param trans transaction from cash management document
     * @return true if the current date is after all new items in process' open dates
     */
    public boolean checkNewItemInProcessInPast(CashieringTransaction trans) {
        boolean success = true;
        if (trans.getNewItemInProcess().isPopulated()) {
            if (trans.getNewItemInProcess().getItemOpenDate() != null && convertDateToDayYear(trans.getNewItemInProcess().getItemOpenDate()) > convertDateToDayYear(new Date())) {
                GlobalVariables.getMessageMap().putError("document.currentTransaction.newItemInProcess.itemOpenDate", KFSKeyConstants.CashManagement.ERROR_NEW_ITEM_IN_PROCESS_IN_FUTURE, new String[] {});
                success = false;
            }
        }
        return success;
    }

    /**
     * This method calculates the total cash drawer reserves amount
     *
     * @param cmDoc
     * @param trans
     * @return KualiDecimal as total from cash drawer reserves
     */
    protected KualiDecimal calculateTotalCashDrawerReserves(CashDrawer cashDrawer, CashieringTransaction trans) {
        KualiDecimal reserves = new KualiDecimal(cashDrawer.getTotalAmount().bigDecimalValue());
        reserves = reserves.add(trans.getMoneyInCurrency().getTotalAmount());
        reserves = reserves.add(trans.getMoneyInCoin().getTotalAmount());
        return reserves;
    }

    /**
     * This method returns the current day of year as an int for a specific date.
     *
     * @param d date
     * @return int as day of year
     */
    protected int convertDateToDayYear(Date d) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        return cal.get(Calendar.YEAR) * 366 + cal.get(Calendar.DAY_OF_YEAR);
    }

}

