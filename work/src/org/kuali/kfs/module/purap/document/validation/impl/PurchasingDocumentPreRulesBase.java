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
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItemBase;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocumentBase;
import org.kuali.kfs.module.purap.document.web.struts.PurchasingFormBase;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.rules.PreRulesContinuationBase;

public class PurchasingDocumentPreRulesBase extends PreRulesContinuationBase{

    @Override
    public boolean doRules(Document arg0) {
        return true;
    }
    
    protected boolean checkForTaxRecalculation(PurchasingAccountsPayableDocument purapDocument){
        
        String initialZipCode = ((PurchasingFormBase)form).getInitialZipCode();
        if (StringUtils.isNotEmpty(initialZipCode) && !StringUtils.equals(initialZipCode,((PurchasingDocumentBase)purapDocument).getDeliveryPostalCode())){
            for (PurApItem purApItem : purapDocument.getItems()) {
                PurchasingItemBase item = (PurchasingItemBase)purApItem;
                if (item.getItemTaxAmount() != null){
                
                    StringBuffer questionTextBuffer = new StringBuffer("");        
                    questionTextBuffer.append( "<style type=\"text/css\"> table.questionTable {border-collapse: collapse;} td.msgTd {padding:3px; width:600px; } </style>" );
                    questionTextBuffer.append("<br/><br/><table class=\"questionTable\" align=\"center\">");
                    questionTextBuffer.append("<tr><td class=\"msgTd\">" + PurapConstants.TAX_RECALCULATION_QUESTION + "</td></tr></table>");
                
                    Boolean proceed = super.askOrAnalyzeYesNoQuestion(PurapConstants.TAX_RECALCULATION_INFO, questionTextBuffer.toString());
                   
                    //Set a marker to record that this method has been used.
                    if (proceed && StringUtils.isBlank(event.getQuestionContext())) {
                        event.setQuestionContext(PurapConstants.TAX_RECALCULATION_INFO);
                    }

                    if (!proceed) {
                        event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                        return false;
                    }
                }
            }
        }
       
        return true;
    }
    

}
