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
package org.kuali.module.labor.util;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;

public class DebitCreditUtil {

    /**
     * Determine the Debit Credit code based on the given amount. Normally (isReverse flag is set as false), the debit code returns
     * if the amount is positive while the credit code returns if the amount is negative. When isReverse flag is set as true, the
     * credit returns for positive amount and the debit code for negative amount.
     * 
     * @param amount the given amount, which can be either negative or positive number.
     * @param isReversed a flag that indicates if normal accounting practice is used. False for normal accoutning practice; true for
     *        reverse.
     * @return the Debit Credit code based on the given transaction amount and the value of isReversed
     */
    public static String getDebitCreditCode(KualiDecimal amount, boolean isReversed) {
        return getDebitCreditCode(amount, "", isReversed);
    }

    /**
     * Determine the Debit Credit code based on the given amount. Normally (isReverse flag is set as false), the debit code returns
     * if the amount is positive while the credit code returns if the amount is negative. When isReverse flag is set as true, the
     * credit returns for positive amount and the debit code for negative amount.
     * 
     * @param amount the given amount, which can be either negative or positive number.
     * @param currentDebitCreditCode the current debit credit code
     * @param isReversed a flag that indicates if normal accounting practice is used. False for normal accoutning practice; true for
     *        reverse.
     * @return the Debit Credit code based on the given transaction amount and the value of isReversed
     */
    public static String getDebitCreditCode(KualiDecimal amount, String currentDebitCreditCode, boolean isReversed) {
        String debitCreditCode = null;
        if (amount.isNegative()) {
            if (KFSConstants.GL_CREDIT_CODE.equals(currentDebitCreditCode)) {
                debitCreditCode = KFSConstants.GL_DEBIT_CODE;
            }
            else {
                debitCreditCode = KFSConstants.GL_CREDIT_CODE;
            }
        }
        else {
            if (KFSConstants.GL_CREDIT_CODE.equals(currentDebitCreditCode)) {
                debitCreditCode = KFSConstants.GL_CREDIT_CODE;
            }
            else {
                debitCreditCode = KFSConstants.GL_DEBIT_CODE;
            }
        }

        if (isReversed) {
            debitCreditCode = getReverseDebitCreditCode(debitCreditCode);
        }
        return debitCreditCode;
    }

    /**
     * get the reversed debit credit code of the given code
     * 
     * @param currentDebitCreditCode the current debit credit code
     * @return the reversed debit credit code of the given code
     */
    public static String getReverseDebitCreditCode(String currentDebitCreditCode) {
        if (KFSConstants.GL_DEBIT_CODE.equals(currentDebitCreditCode)) {
            return KFSConstants.GL_CREDIT_CODE;
        }

        if (KFSConstants.GL_CREDIT_CODE.equals(currentDebitCreditCode)) {
            return KFSConstants.GL_DEBIT_CODE;
        }

        return KFSConstants.GL_CREDIT_CODE;
    }

    /**
     * Determine the actual amount based on Debit Credit code. If the code is credit code, then change the sign of the given amount;
     * otherwise, do nothing
     * 
     * @param amount the given amount, which can be either negative or positive number.
     * @param currentDebitCreditCode the current debit credit code
     * @return the actual numeric amount of the given amount
     */
    public static KualiDecimal getNumericAmount(KualiDecimal amount, String currentDebitCreditCode) {
        KualiDecimal actualAmount = amount;

        if(amount == null){
            actualAmount = KualiDecimal.ZERO;
        }
        else if (KFSConstants.GL_CREDIT_CODE.equals(currentDebitCreditCode)) {
            actualAmount = actualAmount.multiply(new KualiDecimal(-1));
        }
        return actualAmount;
    }
}
