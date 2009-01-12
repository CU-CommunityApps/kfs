/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.Document;

public class DisbursementVoucherDocumentPresentationController extends AccountingDocumentPresentationControllerBase {

    /**
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.kns.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        
        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.TAX_ENTRY);
        editModes.add(KfsAuthorizationConstants.DisbursementVoucherEditMode.FRN_ENTRY);

        return editModes;
    }
}
