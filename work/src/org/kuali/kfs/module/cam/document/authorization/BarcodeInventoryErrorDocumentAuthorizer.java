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
package org.kuali.kfs.module.cam.document.authorization;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.cam.document.BarcodeInventoryErrorDocument;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;

public class BarcodeInventoryErrorDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase  {
    @Override
    public FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        FinancialSystemTransactionalDocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        flags.setCanSave(false);
        flags.setCanRoute(false);
        flags.setCanReload(false);
        
        /* 
         * if the document has all rows processed and the person that loaded the bcie is the one opening the page, then allow
         * this user approve the page with the approve button.
         * 
         */
        /*if (((BarcodeInventoryErrorDocument)document).getUploaderUniversalIdentifier().equals(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier())) {
            flags.setCanApprove(true);
            flags.setCanBlanketApprove(true);
            flags.setCanRoute(true);
        } */       
        return flags;
    }

}
