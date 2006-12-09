/*
 * Copyright 2005-2006 The Kuali Foundation.
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
package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.module.financial.bo.PreEncumbranceDocumentAccountingLineParser;
import org.kuali.module.gl.util.SufficientFundsItem;

/**
 * The Pre-Encumbrance document provides the capability to record encumbrances independently of purchase orders, travel, or Physical
 * Plant work orders. These transactions are for the use of the account manager to earmark funds for which unofficial commitments
 * have already been made.
 */
public class PreEncumbranceDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreEncumbranceDocument.class);

    private java.sql.Date reversalDate;

    /**
     * Initializes the array lists and some basic info.
     */
    public PreEncumbranceDocument() {
        super();
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#checkSufficientFunds()
     */
    @Override
    public List<SufficientFundsItem> checkSufficientFunds() {
        LOG.debug("checkSufficientFunds() started");

        // This document does not do sufficient funds checking
        return new ArrayList<SufficientFundsItem>();
    }


    /**
     * 
     * @return Timestamp
     */
    public java.sql.Date getReversalDate() {
        return reversalDate;
    }

    /**
     * 
     * @param reversalDate
     */
    public void setReversalDate(java.sql.Date reversalDate) {
        this.reversalDate = reversalDate;
    }

    /**
     * Overrides the base implementation to return "Encumbrance".
     * 
     * @see org.kuali.core.document.TransactionalDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.ENCUMBRANCE;
    }

    /**
     * Overrides the base implementation to return "Disencumbrance".
     * 
     * @see org.kuali.core.document.TransactionalDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.DISENCUMBRANCE;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new PreEncumbranceDocumentAccountingLineParser();
    }

}
