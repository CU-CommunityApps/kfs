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

package org.kuali.module.financial.rules;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.module.financial.document.YearEndDocumentUtil;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * Business rules applicable to <code>YearEndDistributionOfIncomeAndExpenseDocument</code>
 * 
 * 
 */
public class YearEndDistributionOfIncomeAndExpenseDocumentRule extends DistributionOfIncomeAndExpenseDocumentRule {

    /**
     * year end document set:
     * <ol>
     * <li> the fiscal period code = 13
     * <li> fiscal year = previous fiscal year
     * </ol>
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        super.customizeExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, explicitEntry);
        YearEndDocumentUtil.customizeExplicitGeneralLedgerPendingEntry(transactionalDocument, accountingLine, explicitEntry);
    }

}
