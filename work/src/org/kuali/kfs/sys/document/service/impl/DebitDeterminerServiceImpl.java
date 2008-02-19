/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPostable;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.document.GeneralLedgerPoster;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.kfs.service.OptionsService;

/**
 * Default implementation of the DebitDeterminerService.
 */
public class DebitDeterminerServiceImpl implements DebitDeterminerService {
    private static Logger LOG = Logger.getLogger(DebitDeterminerServiceImpl.class);
    private static final String isDebitCalculationIllegalStateExceptionMessage = "an invalid debit/credit check state was detected";
    private static final String isErrorCorrectionIllegalStateExceptionMessage = "invalid (error correction) document not allowed";
    private static final String isInvalidLineTypeIllegalArgumentExceptionMessage = "invalid accounting line type";
    
    private AccountingDocumentRuleHelperService accountingDocumentRuleUtil;
    private OptionsService optionsService;

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#disallowErrorCorrectionDocumentCheck(org.kuali.kfs.document.GeneralLedgerPoster)
     */
    public void disallowErrorCorrectionDocumentCheck(GeneralLedgerPoster poster) {
        LOG.debug("disallowErrorCorrectionDocumentCheck(AccountingDocumentRuleBase, AccountingDocument) - start");

        if (isErrorCorrection(poster)) {
            throw new IllegalStateException(isErrorCorrectionIllegalStateExceptionMessage);
        }

        LOG.debug("disallowErrorCorrectionDocumentCheck(AccountingDocumentRuleBase, AccountingDocument) - end");
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isAsset(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isAsset(GeneralLedgerPostable postable) {
        LOG.debug("isAsset(AccountingLine) - start");

        boolean returnboolean = isAssetTypeCode(accountingDocumentRuleUtil.getObjectCodeTypeCodeWithoutSideEffects(postable));
        LOG.debug("isAsset(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isAssetTypeCode(java.lang.String)
     */
    public boolean isAssetTypeCode(String objectTypeCode) {
        LOG.debug("isAssetTypeCode(String) - start");

        boolean returnboolean = optionsService.getCurrentYearOptions().getFinancialObjectTypeAssetsCd().equals(objectTypeCode);
        LOG.debug("isAssetTypeCode(String) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isDebitCode(java.lang.String)
     */
    public boolean isDebitCode(String debitCreditCode) {
        LOG.debug("isDebitCode(String) - start");

        boolean returnboolean = StringUtils.equals(KFSConstants.GL_DEBIT_CODE, debitCreditCode);
        LOG.debug("isDebitCode(String) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isDebitConsideringNothingPositiveOnly(org.kuali.kfs.document.GeneralLedgerPoster, org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isDebitConsideringNothingPositiveOnly(GeneralLedgerPoster poster, GeneralLedgerPostable postable) {
        LOG.debug("isDebitConsideringNothingPositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - start");

        boolean isDebit = false;
        KualiDecimal amount = postable.getAmount();
        boolean isPositiveAmount = amount.isPositive();
        // isDebit if income/liability/expense/asset and line amount is positive
        if (isPositiveAmount && (isIncomeOrLiability(postable) || isExpenseOrAsset(postable))) {
            isDebit = true;
        }
        else {
            // non error correction
            if (!isErrorCorrection(poster)) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);

            }
            // error correction
            else {
                isDebit = false;
            }
        }

        LOG.debug("isDebitConsideringNothingPositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - end");
        return isDebit;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isDebitConsideringSection(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
     */
    public boolean isDebitConsideringSection(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        LOG.debug("isDebitConsideringSection(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - start");

        KualiDecimal amount = accountingLine.getAmount();
        // zero amounts are not allowed
        if (amount.isZero()) {
            throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
        }
        boolean isDebit = false;
        boolean isPositiveAmount = accountingLine.getAmount().isPositive();
        // source line
        if (accountingLine.isSourceAccountingLine()) {
            // income/liability/expense/asset
            if (isIncomeOrLiability(accountingLine) || isExpenseOrAsset(accountingLine)) {
                isDebit = !isPositiveAmount;
            }
            else {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
        }
        // target line
        else {
            if (accountingLine.isTargetAccountingLine()) {
                if (isIncomeOrLiability(accountingLine) || isExpenseOrAsset(accountingLine)) {
                    isDebit = isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            else {
                throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
            }
        }

        LOG.debug("isDebitConsideringSection(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - end");
        return isDebit;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isDebitConsideringSectionAndTypePositiveOnly(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
     */
    public boolean isDebitConsideringSectionAndTypePositiveOnly(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        LOG.debug("isDebitConsideringSectionAndTypePositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - start");

        boolean isDebit = false;
        KualiDecimal amount = accountingLine.getAmount();
        boolean isPositiveAmount = amount.isPositive();
        // non error correction - only allow amount >0
        if (!isPositiveAmount && !isErrorCorrection(accountingDocument)) {
            throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
        }
        // source line
        if (accountingLine.isSourceAccountingLine()) {
            // could write below block in one line using == as XNOR operator, but that's confusing to read:
            // isDebit = (rule.isIncomeOrLiability(accountingLine) == isPositiveAmount);
            if (isPositiveAmount) {
                isDebit = isIncomeOrLiability(accountingLine);
            }
            else {
                isDebit = isExpenseOrAsset(accountingLine);
            }
        }
        // target line
        else {
            if (accountingLine.isTargetAccountingLine()) {
                if (isPositiveAmount) {
                    isDebit = isExpenseOrAsset(accountingLine);
                }
                else {
                    isDebit = isIncomeOrLiability(accountingLine);
                }
            }
            else {
                throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
            }
        }

        LOG.debug("isDebitConsideringSectionAndTypePositiveOnly(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - end");
        return isDebit;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isDebitConsideringType(org.kuali.kfs.document.GeneralLedgerPoster, org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isDebitConsideringType(GeneralLedgerPoster poster, GeneralLedgerPostable postable) {
        LOG.debug("isDebitConsideringType(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - start");

        KualiDecimal amount = postable.getAmount();
        // zero amounts are not allowed
        if (amount.isZero()) {
            throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
        }
        boolean isDebit = false;
        boolean isPositiveAmount = postable.getAmount().isPositive();

        // income/liability
        if (isIncomeOrLiability(postable)) {
            isDebit = !isPositiveAmount;
        }
        // expense/asset
        else {
            if (isExpenseOrAsset(postable)) {
                isDebit = isPositiveAmount;
            }
            else {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
        }

        LOG.debug("isDebitConsideringType(AccountingDocumentRuleBase, AccountingDocument, AccountingLine) - end");
        return isDebit;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isErrorCorrection(org.kuali.kfs.document.GeneralLedgerPoster)
     */
    public boolean isErrorCorrection(GeneralLedgerPoster poster) {
        return StringUtils.isNotBlank(poster.getDocumentHeader().getFinancialDocumentInErrorNumber());
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isExpense(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isExpense(GeneralLedgerPostable postable) {
        LOG.debug("isExpense(AccountingLine) - start");

        boolean returnboolean = accountingDocumentRuleUtil.isExpense(postable);
        LOG.debug("isExpense(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isExpenseOrAsset(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isExpenseOrAsset(GeneralLedgerPostable postable) {
        LOG.debug("isExpenseOrAsset(AccountingLine) - start");

        boolean returnboolean = isAsset(postable) || isExpense(postable);
        LOG.debug("isExpenseOrAsset(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isIncome(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isIncome(GeneralLedgerPostable postable) {
        LOG.debug("isIncome(AccountingLine) - start");

        boolean returnboolean = accountingDocumentRuleUtil.isIncome(postable);
        LOG.debug("isIncome(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isIncomeOrLiability(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isIncomeOrLiability(GeneralLedgerPostable postable) {
        LOG.debug("isIncomeOrLiability(AccountingLine) - start");

        boolean returnboolean = isLiability(postable) || isIncome(postable);
        LOG.debug("isIncomeOrLiability(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isLiability(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isLiability(GeneralLedgerPostable postable) {
        LOG.debug("isLiability(AccountingLine) - start");

        boolean returnboolean = isLiabilityTypeCode(accountingDocumentRuleUtil.getObjectCodeTypeCodeWithoutSideEffects(postable));
        LOG.debug("isLiability(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isLiabilityTypeCode(java.lang.String)
     */
    public boolean isLiabilityTypeCode(String objectTypeCode) {
        LOG.debug("isLiabilityTypeCode(String) - start");

        boolean returnboolean = optionsService.getCurrentYearOptions().getFinObjectTypeLiabilitiesCode().equals(objectTypeCode);
        LOG.debug("isLiabilityTypeCode(String) - end");
        return returnboolean;
    }

    /**
     * @see org.kuali.kfs.service.DebitDeterminerService#isRevenue(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    public boolean isRevenue(GeneralLedgerPostable postable) {
        LOG.debug("isRevenue(AccountingLine) - start");

        boolean returnboolean = !isExpense(postable);
        LOG.debug("isRevenue(AccountingLine) - end");
        return returnboolean;
    }

    /**
     * Sets the accountingDocumentRuleUtils attribute value.
     * @param accountingDocumentRuleUtils The accountingDocumentRuleUtils to set.
     */
    public void setAccountingDocumentRuleUtils(AccountingDocumentRuleHelperService accountingDocumentRuleUtil) {
        this.accountingDocumentRuleUtil = accountingDocumentRuleUtil;
    }

    /**
     * Sets the optionsService attribute value.
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Gets the isDebitCalculationIllegalStateExceptionMessage attribute. 
     * @return Returns the isDebitCalculationIllegalStateExceptionMessage.
     */
    public String getDebitCalculationIllegalStateExceptionMessage() {
        return isDebitCalculationIllegalStateExceptionMessage;
    }

    /**
     * Gets the isErrorCorrectionIllegalStateExceptionMessage attribute. 
     * @return Returns the isErrorCorrectionIllegalStateExceptionMessage.
     */
    public String getErrorCorrectionIllegalStateExceptionMessage() {
        return isErrorCorrectionIllegalStateExceptionMessage;
    }

    /**
     * Gets the isInvalidLineTypeIllegalArgumentExceptionMessage attribute. 
     * @return Returns the isInvalidLineTypeIllegalArgumentExceptionMessage.
     */
    public String getInvalidLineTypeIllegalArgumentExceptionMessage() {
        return isInvalidLineTypeIllegalArgumentExceptionMessage;
    }

    
}
