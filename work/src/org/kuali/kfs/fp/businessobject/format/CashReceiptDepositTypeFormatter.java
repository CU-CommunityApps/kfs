/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.web.struts.form;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.Formatter;

public class CashReceiptDepositTypeFormatter extends Formatter {
    private final String INTERIM_CD;
    private final String INTERIM_MSG;

    private final String FINAL_CD;
    private final String FINAL_MSG;

    public CashReceiptDepositTypeFormatter() {
        INTERIM_CD = Constants.DepositConstants.DEPOSIT_TYPE_INTERIM;
        INTERIM_MSG = SpringServiceLocator.getKualiConfigurationService().getPropertyString(
                KeyConstants.Deposit.DEPOSIT_TYPE_INTERIM);

        FINAL_CD = Constants.DepositConstants.DEPOSIT_TYPE_FINAL;
        FINAL_MSG = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.Deposit.DEPOSIT_TYPE_FINAL);
    }


    /**
     * Converts the given statusCode into a status message.
     */
    protected Object convertToObject(String target) {
        Object result = target;

        if (target instanceof String) {
            String message = (String) target;

            if (StringUtils.equals(message, INTERIM_MSG)) {
                result = INTERIM_CD;
            }
            else if (StringUtils.equals(message, FINAL_MSG)) {
                result = FINAL_CD;
            }
        }

        return result;
    }

    /**
     * Converts the given status message into a status code.
     */
    public Object format(Object value) {
        Object formatted = value;

        if (value instanceof String) {
            String statusCode = (String) value;

            if (StringUtils.equals(statusCode, FINAL_CD)) {
                formatted = FINAL_MSG;
            }
            else if (StringUtils.equals(statusCode, INTERIM_CD)) {
                formatted = INTERIM_MSG;
            }
        }

        return formatted;
    }

}
