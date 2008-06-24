/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.validation.impl;

import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * Business rule(s) applicable to Year End General Error Correction documents. All other business rules are inherited from the
 * parent non-year end version.
 */
public class YearEndGeneralErrorCorrectionDocumentRule extends GeneralErrorCorrectionDocumentRule {
    /**
     * Overriding to return the corresponding parent class GeneralErrorCorrectionDocument.
     * 
     * @param financialDocument The financial document the class will be determined for.
     * @return The class type of the document passed in.
     * 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#getAccountingLineDocumentClass(org.kuali.kfs.sys.document.AccountingDocument)
     */
    @Override
    protected Class getAccountingLineDocumentClass(AccountingDocument financialDocument) {
        return GeneralErrorCorrectionDocument.class;
    }


}
