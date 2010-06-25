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

import org.kuali.kfs.module.endow.EndowConstants.TransactionSourceTypeCode;
import org.kuali.kfs.module.endow.EndowConstants.TransactionSubTypeCode;

/**
 * The Endowment Unit/Share Adjustment (EUSA) transaction is available for those times when a number of units of a security held by
 * the KEMID must be modified without affecting the original cost or carry value of the security tax lot(s).
 */
public class EndowmentUnitShareAdjustmentDocument extends EndowmentTaxLotLinesDocumentBase {

    /**
     * Constructs a EndowmentUnitShareAdjustmentDocument.java.
     */
    public EndowmentUnitShareAdjustmentDocument() {
        super();
        setTransactionSourceTypeCode(TransactionSourceTypeCode.MANUAL);
        setTransactionSubTypeCode(TransactionSubTypeCode.NON_CASH);

        initializeSubType();
    }

}
