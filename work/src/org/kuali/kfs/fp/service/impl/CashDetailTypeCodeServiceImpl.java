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
package org.kuali.module.financial.service.impl;

import org.kuali.core.service.KualiCodeService;
import org.kuali.module.financial.bo.CashDetailTypeCode;
import org.kuali.module.financial.service.CashDetailTypeCodeService;

/**
 * This is the default implementation for the CashDetailTypeCodeService interface. This implementation used the KualiCodeService,
 * which in turn uses the KualiCodeDao for retrieving values from the database.
 * 
 * 
 */
public class CashDetailTypeCodeServiceImpl implements CashDetailTypeCodeService {
    private KualiCodeService kualiCodeService;

    // Constants for doing the actual lookups
    public final static String CASH_RECEIPT_CHECK = "CRCHK";
    public final static String CASH_RECEIPT_COIN = "CRCOIN";

    /**
     * Constructs a CashDetailTypeCodeServiceImpl instance.
     */
    public CashDetailTypeCodeServiceImpl() {
    }

    /**
     * Gets the kualiCodeService attribute.
     * 
     * @return Returns the kualiCodeService.
     */
    public KualiCodeService getKualiCodeService() {
        return kualiCodeService;
    }

    /**
     * Sets the kualiCodeService attribute value.
     * 
     * @param kualiCodeService The kualiCodeService to set.
     */
    public void setKualiCodeService(KualiCodeService kualiCodeService) {
        this.kualiCodeService = kualiCodeService;
    }

    /**
     * @see org.kuali.core.service.CashDetailTypeCode#getCashReceiptCheckTypeCode()
     */
    public CashDetailTypeCode getCashReceiptCheckTypeCode() {
        return getCashDetailTypeCodeByCode(CASH_RECEIPT_CHECK);
    }

    /**
     * @see org.kuali.core.service.CashDetailTypeCode#getCashReceiptCoinTypeCode()
     */
    public CashDetailTypeCode getCashReceiptCoinTypeCode() {
        return getCashDetailTypeCodeByCode(CASH_RECEIPT_CHECK);
    }

    /**
     * Retrieves a populated instance corresponding to the code passed into this method. This is retrieved via the KualiCodeService
     * and in turn from the database. TODO - uncomment the commented out line and remove the others when the table is in place
     */
    private CashDetailTypeCode getCashDetailTypeCodeByCode(String cashDetailTypeCode) {
        // return (CashDetailTypeCode) kualiCodeService.getByCode(CashDetailTypeCode.class, cashDetailTypeCode);
        return getDummyInstance(cashDetailTypeCode);
    }

    /**
     * This method is a temporary helper method. This should be removed when the lookup table for CashDetailTypeCode business
     * objects is put in place. Then we'll be retrieving the stuff from the database. TODO - remove this method after the table is
     * in place; this is a temp helper method
     * 
     * @param cashDetailTypeCodeCode The code to popluate the dummy instance with.
     * @return
     */
    private CashDetailTypeCode getDummyInstance(String cashDetailTypeCodeCode) {
        CashDetailTypeCode cashDetailTypeCode = new CashDetailTypeCode();
        cashDetailTypeCode.setCode(cashDetailTypeCodeCode);
        return cashDetailTypeCode;
    }
}