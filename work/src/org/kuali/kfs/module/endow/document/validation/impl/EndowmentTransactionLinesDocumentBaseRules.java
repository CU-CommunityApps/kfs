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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KEMIDCurrentAvailableBalance;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionDocumentService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionLinesDocumentService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule;
import org.kuali.kfs.module.endow.document.validation.DeleteTransactionLineRule;
import org.kuali.kfs.module.endow.document.validation.RefreshTransactionLineRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.AbstractKualiDecimal;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;

public class EndowmentTransactionLinesDocumentBaseRules extends EndowmentTransactionalDocumentBaseRule implements AddTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine>, DeleteTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine>, RefreshTransactionLineRule<EndowmentTransactionLinesDocument, EndowmentTransactionLine, Number> {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.DeleteTransactionLineRule#processDeleteTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public boolean processDeleteTransactionLineRules(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine endowmentTransactionLine) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.AddTransactionLineRule#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {
        return validateTransactionLine((EndowmentTransactionLinesDocumentBase) document, line, -1);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.RefreshTransactionLineRule#processRefreshTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.Number)
     */
    public boolean processRefreshTransactionLineRules(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine endowmentTransactionLine, Number index) {
        return validateTransactionLine((EndowmentTransactionLinesDocumentBase) endowmentTransactionLinesDocument, endowmentTransactionLine, (Integer) index);
    }

    /**
     * @see org.kuali.rice.kns.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase = null;

        if (isValid) {
            endowmentTransactionLinesDocumentBase = (EndowmentTransactionLinesDocumentBase) document;

            // validate source transaction lines
            if (endowmentTransactionLinesDocumentBase.getSourceTransactionLines() != null) {
                for (int i = 0; i < endowmentTransactionLinesDocumentBase.getSourceTransactionLines().size(); i++) {
                    EndowmentTransactionLine transactionLine = endowmentTransactionLinesDocumentBase.getSourceTransactionLines().get(i);
                    validateTransactionLine(endowmentTransactionLinesDocumentBase, transactionLine, i);
                }
            }

            // validate target transaction lines
            if (endowmentTransactionLinesDocumentBase.getTargetTransactionLines() != null) {
                for (int i = 0; i < endowmentTransactionLinesDocumentBase.getTargetTransactionLines().size(); i++) {
                    EndowmentTransactionLine transactionLine = endowmentTransactionLinesDocumentBase.getTargetTransactionLines().get(i);
                    validateTransactionLine(endowmentTransactionLinesDocumentBase, transactionLine, i);
                }
            }

        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * This method obtains Prefix for Error fields in UI.
     * 
     * @param line
     * @param index
     * @return
     */
    public String getErrorPrefix(EndowmentTransactionLine line, int index) {
        String ERROR_PREFIX = null;
        if (line instanceof EndowmentSourceTransactionLine) {
            if (index == -1) {
                ERROR_PREFIX = EndowPropertyConstants.SOURCE_TRANSACTION_LINE_PREFIX;
            }
            else {
                ERROR_PREFIX = EndowPropertyConstants.EXISTING_SOURCE_TRANSACTION_LINE_PREFIX + "[" + index + "].";
            }
        }
        else {
            if (index == -1) {
                ERROR_PREFIX = EndowPropertyConstants.TARGET_TRANSACTION_LINE_PREFIX;
            }
            else {
                ERROR_PREFIX = EndowPropertyConstants.EXISTING_TARGET_TRANSACTION_LINE_PREFIX + "[" + index + "].";
            }
        }
        return ERROR_PREFIX;

    }

    /**
     * This method obtains security code from a document.
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @return
     */
    public String getSecurityIDForValidation(EndowmentTransactionLinesDocument endowmentTransactionLinesDocumentBase, EndowmentTransactionLine line) 
    {
        EndowmentSecurityDetailsDocumentBase document = (EndowmentSecurityDetailsDocumentBase) endowmentTransactionLinesDocumentBase;
        if (line instanceof EndowmentSourceTransactionLine) 
            return document.getSourceTransactionSecurity().getSecurityID();
        else
            return document.getTargetTransactionSecurity().getSecurityID();
    }

    /**
     * This method validates a transaction line.
     * 
     * @param line
     * @param index
     * @return
     */
    protected boolean validateTransactionLine(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase, EndowmentTransactionLine line, int index) {
        boolean isValid = true;
        isValid &= !GlobalVariables.getMessageMap().hasErrors();

        String ERROR_PREFIX = getErrorPrefix(line, index);

        if (isValid) {
            GlobalVariables.getMessageMap().clearErrorPath();

            // General not null validation for KemID
            SpringContext.getBean(DictionaryValidationService.class).validateAttributeRequired(line.getClass().getName(), "kemid", line.getKemid(), false, ERROR_PREFIX + EndowPropertyConstants.KEMID);

            // Validate KemID
            if (!validateKemId(line, ERROR_PREFIX))
                return false;

            // Active Kemid
            isValid &= isActiveKemId(line, ERROR_PREFIX);

            // Validate no restriction transaction restriction
            isValid &= validateNoTransactionRestriction(line, ERROR_PREFIX);

            // Validate Income/Principal DropDown
            SpringContext.getBean(DictionaryValidationService.class).validateAttributeRequired(line.getClass().getName(), "transactionIPIndicatorCode", line.getTransactionIPIndicatorCode(), false, ERROR_PREFIX + EndowPropertyConstants.TRANSACTION_IPINDICATOR);
            isValid &= GlobalVariables.getMessageMap().getErrorCount() == 0 ? true : false;
            if (!isValid)
                return isValid;

            //This error is checked in addition save rule method since the sub type is used for determining chart code. 
            if (!isSubTypeEmpty(endowmentTransactionLinesDocumentBase))
                return false;

            //If non-cash transactions 
            if (nonCashTransaction(endowmentTransactionLinesDocumentBase)) 
            {
                // Is Etran code empty
                if (isEndowmentTransactionCodeEmpty(line, ERROR_PREFIX))
                    return false;

                // Validate ETran code
                if (!validateEndowmentTransactionCode(line, ERROR_PREFIX))
                    return false;

                // Validate ETran code as E or I
                isValid &= validateEndowmentTransactionTypeCode(line, ERROR_PREFIX);

                // Validate if the chart is matched between the KEMID and EtranCode
                isValid &= validateChartMatch(line, ERROR_PREFIX);

                // Set Corpus Indicator
                line.setCorpusIndicator(SpringContext.getBean(EndowmentTransactionLinesDocumentService.class).getCorpusIndicatorValueforAnEndowmentTransactionLine(line.getKemid(), line.getEtranCode(), line.getTransactionIPIndicatorCode()));
            }

            // Refresh all references for the given KemId
            // line.getKemidObj().refreshNonUpdateableReferences();

            // Validate if a KEMID can have a principal transaction when IP indicator is P
            isValid &= canKEMIDHaveAPrincipalTransaction(line, ERROR_PREFIX);
        }

        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * This method checks if this is a non-cash transaction. 
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @return
     */
    protected boolean nonCashTransaction(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase) 
    {
        if (EndowConstants.TransactionSubTypeCode.NON_CASH.equalsIgnoreCase(endowmentTransactionLinesDocumentBase.getTransactionSubTypeCode()))
            return true;
        else
            return false;
    }

    /**
     * This method validates the KEMID code.
     * 
     * @param tranSecurity
     * @return
     */
    protected boolean isKemIdCodeEmpty(EndowmentTransactionLine line, String prefix) {
        if (StringUtils.isEmpty(line.getKemid())) {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_REQUIRED);
            return true;
        }

        return false;
    }

    /**
     * This method validates the KemId code and tries to create a KEMID object from the code.
     * 
     * @param line
     * @return
     */
    protected boolean validateKemId(EndowmentTransactionLine line, String prefix) {
        boolean success = true;

        KEMID kemId = (KEMID) SpringContext.getBean(KEMIDService.class).getByPrimaryKey(line.getKemid());
        line.setKemidObj(kemId);
        if (null == kemId) {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_INVALID);
            success = false;
        }

        return success;
    }

    /**
     * This method determines if the KEMID is active.
     * 
     * @param line
     * @return
     */
    protected boolean isActiveKemId(EndowmentTransactionLine line, String prefix) {
        if (line.getKemidObj().isClose()) {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_INACTIVE);
            return false;

        }
        else {
            return true;
        }
    }

    /**
     * This method checks if the KEMID restriction code is "NTRAN"
     * 
     * @param line
     * @return
     */
    protected boolean validateNoTransactionRestriction(EndowmentTransactionLine line, String prefix) {
        if (line.getKemidObj().getTransactionRestrictionCode().equalsIgnoreCase(EndowConstants.TransactionRestrictionCode.TRAN_RESTR_CD_NTRAN)) {
            putFieldError(prefix + EndowPropertyConstants.KEMID, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_NO_TRAN_CODE);
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * This method checks is the Transaction amount entered is greater than Zero.
     * 
     * @param line
     * @return
     */
    protected boolean validateTransactionAmountGreaterThanZero(EndowmentTransactionLine line, String prefix) {
        if (line.getTransactionAmount() != null && line.getTransactionAmount().isGreaterThan(AbstractKualiDecimal.ZERO))
            return true;
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_AMOUNT_GREATER_THAN_ZERO);
            return false;
        }
    }

    /**
     * This method checks is the Transaction Units entered is greater than Zero.
     * 
     * @param line
     * @return
     */
    protected boolean validateTransactionUnitsGreaterThanZero(EndowmentTransactionLine line, String prefix) {
        if (line.getTransactionUnits() != null && line.getTransactionUnits().isGreaterThan(AbstractKualiDecimal.ZERO))
            return true;
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_UNITS_GREATER_THAN_ZERO);
            return false;
        }
    }

    /**
     * This method checks is the Transaction Units & Amount entered are equal.
     * 
     * @param line
     * @param prefix
     * @return
     */
    protected boolean validateTransactionUnitsAmountEqual(EndowmentTransactionLine line, String prefix) {
        if (line.getTransactionUnits() != null && line.getTransactionAmount() != null && line.getTransactionUnits().compareTo(line.getTransactionAmount()) == 0)
            return true;
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_AMOUNT_UNITS_EQUAL);
            return false;
        }
    }

    /**
     * This method checks if the ETRAN code has a type code of E or I.
     * 
     * @param line
     * @return
     */
    protected boolean validateEndowmentTransactionTypeCode(EndowmentTransactionLine line, String prefix) {
        if (line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE) || line.getEtranCodeObj().getEndowmentTransactionTypeCode().equalsIgnoreCase(EndowConstants.EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE))
            return true;
        else {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ENDOWMENT_TRANSACTION_TYPE_CODE_VALIDITY);
            return false;
        }
    }

    /**
     * This method validates the EndowmentTransaction code.
     * 
     * @param tranSecurity
     * @return
     */
    protected boolean isEndowmentTransactionCodeEmpty(EndowmentTransactionLine line, String prefix) {
        if (StringUtils.isEmpty(line.getEtranCode())) {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_REQUIRED);
            return true;
        }

        return false;
    }

    /**
     * This method validates the EndowmentTransaction code and tries to create a EndowmentTransactionCode object from the code.
     * 
     * @param line
     * @return
     */
    protected boolean validateEndowmentTransactionCode(EndowmentTransactionLine line, String prefix) {
        boolean success = true;

        EndowmentTransactionCode etran = (EndowmentTransactionCode) SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(line.getEtranCode());
        line.setEtranCodeObj(etran);
        if (null == etran) {
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_INVALID);
            success = false;
        }

        return success;
    }

    /**
     * This method validates if a KEMID can have a principal transaction when IP indicator is equal to P.
     * 
     * @param line
     * @return
     */
    protected boolean canKEMIDHaveAPrincipalTransaction(EndowmentTransactionLine line, String prefix) {
        boolean canHaveTransaction = true;
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode)) {
            String kemid = line.getKemid();
            if (!SpringContext.getBean(EndowmentTransactionLinesDocumentService.class).canKEMIDHaveAPrincipalActivity(kemid, ipIndicatorCode)) {
                putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_KEMID_CAN_NOT_HAVE_A_PRINCIPAL_TRANSACTION);
                canHaveTransaction = false;
            }
        }
        return canHaveTransaction;
    }

    /**
     * This method validates if the chart is matched between GL Account in the KEMID and GL Link in the Endowment Transaction Code.
     * If the Chart codes do not match, the Etran Code field should be highlighted.
     * 
     * @param line
     * @return
     */
    protected boolean validateChartMatch(EndowmentTransactionLine line, String prefix) {
        boolean isChartMatched = true;
        String kemid = line.getKemid();
        String etranCode = line.getEtranCode();
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        if (!SpringContext.getBean(EndowmentTransactionDocumentService.class).matchChartBetweenKEMIDAndETranCode(kemid, etranCode, ipIndicatorCode)) {
            if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode))
                putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_CHART_CODE_DOES_NOT_MATCH_FOR_PRINCIPAL);
            else
                putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_CHART_CODE_DOES_NOT_MATCH_FOR_INCOME);

            isChartMatched = false;
        }
        return isChartMatched;
    }

    /**
     * For a true endowment, when the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P, a warning message will be placed in the document
     * transaction line notifying the viewer that the transaction will reduce the value of the endowment at the time the transaction
     * line is added. WARNING: This transaction will reduce permanently restricted funds!. However, the transaction line would be
     * added on successfully.
     * 
     * @param line
     * @return
     */
    protected void checkWhetherReducePermanentlyRestrictedFund(EndowmentTransactionLine line, String prefix) {
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        String kemid = line.getKemid();
        if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode) && SpringContext.getBean(KEMIDService.class).isTrueEndowment(kemid)) {
            GlobalVariables.getMessageMap().putWarning(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.WARNING_REDUCE_PERMANENTLY_RESTRICTED_FUNDS);
        }
    }

    /**
     * Upon adding the transaction line, the system will check to see if there are sufficient funds to process the transaction
     * (END_AVAIL_CSH_T). If there are not, a warning message will be placed in the document transaction line notifying the viewer
     * that there are not sufficient funds. -If END_TRAN_LN_T: TRAN_IP_IND_CD is equal to I verify against END_AVAIL_CSH_T:
     * AVAIL_TOT_CSH -If END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P verify against END_AVAIL_CSH_T: AVAIL_PRIN_CSH However, the
     * transaction line would be added on successfully.
     * 
     * @param line
     * @return
     */
    protected void checkWhetherHaveSufficientFundsForCashBasedTransaction(EndowmentTransactionLine line, String prefix) {
        String ipIndicatorCode = line.getTransactionIPIndicatorCode();
        String kemid = line.getKemid();
        KualiDecimal amount = line.getTransactionAmount();

        Map criteria = new HashMap();
        criteria.put(EndowPropertyConstants.KEMID, kemid);
        KEMIDCurrentAvailableBalance theKEMIDCurrentAvailableBalance = (KEMIDCurrentAvailableBalance) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(KEMIDCurrentAvailableBalance.class, criteria);

        if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(ipIndicatorCode)) {
            if (amount.isGreaterThan(new KualiDecimal(theKEMIDCurrentAvailableBalance.getAvailablePrincipalCash()))) {
                GlobalVariables.getMessageMap().putWarning(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.WARNING_NO_SUFFICIENT_FUNDS);
            }
        }
        else {
            if (amount.isGreaterThan(new KualiDecimal(theKEMIDCurrentAvailableBalance.getAvailableTotalCash()))) {
                GlobalVariables.getMessageMap().putWarning(prefix + EndowPropertyConstants.TRANSACTION_LINE_IP_INDICATOR_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.WARNING_NO_SUFFICIENT_FUNDS);
            }
        }
    }


    protected boolean templateMethod(EndowmentTransactionLine line) {
        boolean success = true;

        return success;
    }

    /**
     * This methods checks to ensure for cash Tx do not have a Etran.
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param prefix
     * @return
     */
    protected boolean checkCashTransactionEndowmentCode(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase, EndowmentTransactionLine line, String prefix) 
    {
        // For Cash based Tx the Etran code must be empty,If Tx is Cash based, check for Etran code and if not null display Error message.
        if (!nonCashTransaction(endowmentTransactionLinesDocumentBase) && (!StringUtils.isEmpty(line.getEtranCode())) ) 
        {
            // 
            putFieldError(prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TRANSACTION_LINE_ETRAN_BLANK);
            return false;
        }
    
        return true;
    }
    
     /**
     * Checks that the document has at least one transaction line.
     * 
     * @param document
     * @param isSource
     * @return true if valid, false otherwise
     */
    protected boolean transactionLineSizeGreaterThanZero (EndowmentTransactionLinesDocumentBase document,boolean isSource)
    {
        List<EndowmentTransactionLine> transactionLineList = null;
        if(isSource){
            transactionLineList = document.getSourceTransactionLines();
            if(transactionLineList == null || transactionLineList.size() == 0){
                putFieldError(EndowConstants.TRANSACTION_LINE_TAB_ERROR , EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_FROM_TRANSACTION_LINE_COUNT_INSUFFICIENT);
                return false; 
            }
        }
        else {
            transactionLineList = document.getTargetTransactionLines();
            if(transactionLineList == null || transactionLineList.size() == 0){
                putFieldError(EndowConstants.TRANSACTION_LINE_TAB_ERROR , EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_TO_TRANSACTION_LINE_COUNT_INSUFFICIENT);
                return false; 
            }
        }

        return true;            
    }
}
