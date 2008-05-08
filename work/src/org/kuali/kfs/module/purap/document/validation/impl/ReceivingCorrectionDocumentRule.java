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
package org.kuali.module.purap.rules;

import org.kuali.core.document.Document;
import org.kuali.core.rules.DocumentRuleBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.service.ReceivingService;

public class ReceivingCorrectionDocumentRule extends DocumentRuleBase {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {        
        boolean valid = true;
        ReceivingCorrectionDocument receivingCorrectionDocument = (ReceivingCorrectionDocument)document;
        
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        
        valid &= super.processCustomRouteDocumentBusinessRules(document);
        valid &= canCreateReceivingCorrectionDocument(receivingCorrectionDocument);
        
        return valid;
    }

    /**
     * Determines if it is valid to create a receiving correction document.  Only one
     * receiving correction document can be active at any time per receiving line document.
     * 
     * @param receivingCorrectionDocument
     * @return
     */
    private boolean canCreateReceivingCorrectionDocument(ReceivingCorrectionDocument receivingCorrectionDocument){
        
        boolean valid = true;
        
        if( SpringContext.getBean(ReceivingService.class).canCreateReceivingCorrectionDocument(receivingCorrectionDocument.getReceivingLineDocument(), receivingCorrectionDocument.getDocumentNumber()) == false){
            valid &= false;
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.RECEIVING_LINE_DOCUMENT_NUMBER, PurapKeyConstants.ERROR_RECEIVING_CORRECTION_DOCUMENT_ACTIVE_FOR_RCV_LINE, receivingCorrectionDocument.getDocumentNumber(), receivingCorrectionDocument.getReceivingLineDocumentNumber());
        }
         
        return valid;
    }

}
