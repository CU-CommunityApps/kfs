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


import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.kns.document.Document;

/**
 * Business PreRules applicable to Purchasing documents
 */
public class RequisitionDocumentPreRules extends PurchasingDocumentPreRulesBase {

    /**
     * @see org.kuali.rice.kns.rules.PreRulesContinuationBase#doRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    public boolean doRules(Document document) {
        boolean preRulesOK = super.doRules(document);
        
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument)document;
        
        if (!purapDocument.isUseTaxIndicator()){
            preRulesOK &= checkForTaxRecalculation(purapDocument);
        }
        
        return preRulesOK;
    }

    
}
