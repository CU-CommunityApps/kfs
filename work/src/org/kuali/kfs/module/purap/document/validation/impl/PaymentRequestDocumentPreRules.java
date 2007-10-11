/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.PurapService;

/**
 * Performs prompts and other pre business rule checks for the Payment Request Docuemnt.
 */
public class PaymentRequestDocumentPreRules extends AccountsPayableDocumentPreRulesBase {

    public PaymentRequestDocumentPreRules() {
        super();
    }

    @Override
    public boolean doRules(Document document) {
        boolean preRulesOK = true;
        
        PaymentRequestDocument preq = (PaymentRequestDocument)document;
        if ( (!SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(preq)) ||
             (StringUtils.equals(preq.getStatusCode(),PurapConstants.PaymentRequestStatuses.AWAITING_ACCOUNTS_PAYABLE_REVIEW))){
            if (!confirmPayDayNotOverThresholdDaysAway( preq )) {
                return false;
            }
        }
        preRulesOK &= super.doRules(document);
        return preRulesOK;
    }
    
    private boolean askForConfirmation( String questionType, String messageConstant ) {
        
        String questionText = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(messageConstant);
        if(questionText.contains("{")) {
            questionText = prepareQuestionText(questionType,questionText);
        }
            
        boolean confirmOverride = super.askOrAnalyzeYesNoQuestion(questionType, questionText);
                    
        if (!confirmOverride) {
            event.setActionForwardName(KFSConstants.MAPPING_BASIC);
            return false;
        }
        return true;
    }
    
    private String prepareQuestionText(String questionType, String questionText) {
        if (StringUtils.equals(questionType,PREQDocumentsStrings.THRESHOLD_DAYS_OVERRIDE_QUESTION)) {
            questionText = StringUtils.replace(questionText, "{0}", new Integer(PurapConstants.PREQ_PAY_DATE_DAYS_BEFORE_WARNING).toString());
        }
        return questionText;
    }
    
    public boolean confirmPayDayNotOverThresholdDaysAway( PaymentRequestDocument preq ) {
        // If the pay date is more than the threshold number of days in the future, ask for confirmation.
        PaymentRequestDocumentRule rule = new PaymentRequestDocumentRule();
        if (!rule.validatePayDateNotOverThresholdDaysAway(preq)) {   
            return askForConfirmation(PREQDocumentsStrings.THRESHOLD_DAYS_OVERRIDE_QUESTION,
                    PurapKeyConstants.MESSAGE_PAYMENT_REQUEST_PAYDATE_OVER_THRESHOLD_DAYS);
        }
        return true;
    }
        
    public String getDocumentName(){
        return "Payment Request";
    }
}
