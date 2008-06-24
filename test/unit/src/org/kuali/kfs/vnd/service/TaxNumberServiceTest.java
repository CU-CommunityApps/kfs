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
package org.kuali.kfs.vnd.service;


import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.ConfigureContext;

@ConfigureContext
public class TaxNumberServiceTest extends KualiTestBase {

    private TaxNumberService taxNumberService;
    private final String nullString = null;
    private final String emptyString = "";
    private final String first3Zero = "000123456";
    private final String first3Six = "666123456";
    private final String notAllNumber = "000234a2f";
    private final String middle2Zero = "123004567";
    private final String last4Zero = "123450000";
    private final String validTaxNumber = "123456789";
    private final String validTaxNumberDashed = "123-45-6789";
    private final String allZero = "000000000";
    private final String tenDigits = "1234567890";
    private final String twoDigits = "12";

    protected void setUp() throws Exception {
        super.setUp();
        taxNumberService = SpringContext.getBean(TaxNumberService.class);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testIsValidTaxNumber_notAllowedTaxNumber() {
        String[] notAllowedTaxNumbers = getNotAllowedTaxNumbers();
        for (int i = 0; i < notAllowedTaxNumbers.length; i++) {
            assertFalse(taxNumberService.isValidTaxNumber(notAllowedTaxNumbers[i], VendorConstants.TAX_TYPE_SSN));
        }
    }

    private String[] getNotAllowedTaxNumbers() {
        String[] notAllowedTaxNumbers = SpringContext.getBean(ParameterService.class).getParameterValues(VendorDetail.class, VendorParameterConstants.PURAP_NOT_ALLOWED_TAX_NUMBERS).toArray(new String[] {});
        return notAllowedTaxNumbers;
    }
}
