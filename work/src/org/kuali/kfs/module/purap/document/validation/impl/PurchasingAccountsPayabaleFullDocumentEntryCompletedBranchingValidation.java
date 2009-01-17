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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.BranchingValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class PurchasingAccountsPayabaleFullDocumentEntryCompletedBranchingValidation extends BranchingValidation {
    
    public static final String NOT_FULL_DOCUMENT_ENTRY = "notFullDocumentEntry";
    
    @Override
    protected String determineBranch(AttributedDocumentEvent event) {
     
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)event.getDocument();
        
        if (SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(purapDocument)) {
            return null;
        }else{
            return NOT_FULL_DOCUMENT_ENTRY;
        }
    }
}
