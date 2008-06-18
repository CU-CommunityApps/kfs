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
package org.kuali.kfs.bo;

import java.sql.Date;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.util.KualiDecimal;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class...
 */
public class FinancialSystemDocumentHeader extends DocumentHeader {
    
    private KualiDecimal financialDocumentTotalAmount;
    private String correctedByDocumentId;
    private String financialDocumentInErrorNumber;
    private String financialDocumentStatusCode;
    private Date documentFinalDate;

    /**
     * Constructor - creates empty instances of dependent objects
     * 
     */
    public FinancialSystemDocumentHeader() {
        super();
        financialDocumentStatusCode = KNSConstants.DocumentStatusCodes.INITIATED;
    }

    /**
     * Gets the financialDocumentTotalAmount attribute. 
     * @return Returns the financialDocumentTotalAmount.
     */
    public KualiDecimal getFinancialDocumentTotalAmount() {
        return financialDocumentTotalAmount;
    }

    /**
     * Sets the financialDocumentTotalAmount attribute value.
     * @param financialDocumentTotalAmount The financialDocumentTotalAmount to set.
     */
    public void setFinancialDocumentTotalAmount(KualiDecimal financialDocumentTotalAmount) {
        this.financialDocumentTotalAmount = financialDocumentTotalAmount;
    }

    /**
     * Gets the correctedByDocumentId attribute. 
     * @return Returns the correctedByDocumentId.
     */
    public String getCorrectedByDocumentId() {
        return correctedByDocumentId;
    }

    /**
     * Sets the correctedByDocumentId attribute value.
     * @param correctedByDocumentId The correctedByDocumentId to set.
     */
    public void setCorrectedByDocumentId(String correctedByDocumentId) {
        this.correctedByDocumentId = correctedByDocumentId;
    }

    /**
     * Gets the financialDocumentInErrorNumber attribute. 
     * @return Returns the financialDocumentInErrorNumber.
     */
    public String getFinancialDocumentInErrorNumber() {
        return financialDocumentInErrorNumber;
    }

    /**
     * Sets the financialDocumentInErrorNumber attribute value.
     * @param financialDocumentInErrorNumber The financialDocumentInErrorNumber to set.
     */
    public void setFinancialDocumentInErrorNumber(String financialDocumentInErrorNumber) {
        this.financialDocumentInErrorNumber = financialDocumentInErrorNumber;
    }

    /**
     * Gets the financialDocumentStatusCode attribute. 
     * @return Returns the financialDocumentStatusCode.
     */
    public String getFinancialDocumentStatusCode() {
        return financialDocumentStatusCode;
    }

    /**
     * Sets the financialDocumentStatusCode attribute value.
     * @param financialDocumentStatusCode The financialDocumentStatusCode to set.
     */
    public void setFinancialDocumentStatusCode(String financialDocumentStatusCode) {
        this.financialDocumentStatusCode = financialDocumentStatusCode;
    }

    /**
     * Gets the documentFinalDate attribute. 
     * @return Returns the documentFinalDate.
     */
    public Date getDocumentFinalDate() {
        return documentFinalDate;
    }

    /**
     * Sets the documentFinalDate attribute value.
     * @param documentFinalDate The documentFinalDate to set.
     */
    public void setDocumentFinalDate(Date documentFinalDate) {
        this.documentFinalDate = documentFinalDate;
    }

}
