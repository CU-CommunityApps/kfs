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
package org.kuali.kfs.module.endow.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineImpl;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;

public abstract class EndowmentTransactionalDocumentBase extends FinancialSystemTransactionalDocumentBase implements EndowmentTransactionalDocument {

    private String transactionTypeCode;
    private String transactionSubTypeCode;
    private String transactionSourceTypeCode;
    private boolean transactionPosted;


    /**
     * Constructs a EndowmentTransactionalDocumentBase.java.
     */
    public EndowmentTransactionalDocumentBase() {
        super();

    }

    public String getTransactionTypeCode() {
        return transactionTypeCode;
    }

    public void setTransactionTypeCode(String transactionTypeCode) {
        this.transactionTypeCode = transactionTypeCode;
    }

    public String getTransactionSubTypeCode() {
        return transactionSubTypeCode;
    }

    public void setTransactionSubTypeCode(String transactionSubTypeCode) {
        this.transactionSubTypeCode = transactionSubTypeCode;
    }

    public String getTransactionSourceTypeCode() {
        return transactionSourceTypeCode;
    }

    public void setTransactionSourceTypeCode(String transactionSourceTypeCode) {
        this.transactionSourceTypeCode = transactionSourceTypeCode;
    }

    public boolean isTransactionPosted() {
        return transactionPosted;
    }

    public void setTransactionPosted(boolean transactionPosted) {
        this.transactionPosted = transactionPosted;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

}
