/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.validation.event;

import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.sys.document.validation.SufficientFundsCheckingPreparationRule;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;

/**
 * This class represents the Sufficient Funds Checking Preparation event that is part of an eDoc in Kuali. This is triggered on
 * route or approval
 */
public final class SufficientFundsCheckingPreparationEvent extends KualiDocumentEventBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsCheckingPreparationEvent.class);

    public SufficientFundsCheckingPreparationEvent(String errorPathPrefix, Document document) {
        super("creating sufficient funds checking preparation event for " + getDocumentId(document), errorPathPrefix, document);
    }

    public SufficientFundsCheckingPreparationEvent(TransactionalDocument document) {
        this("", document);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return SufficientFundsCheckingPreparationRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        LOG.debug("invokeRuleMethod() started");

        if (document instanceof GeneralLedgerPostingDocument) {
            LOG.debug("invokeRuleMethod() gl document");

            GeneralLedgerPostingDocument doc = (GeneralLedgerPostingDocument) document;

            SufficientFundsService sufficientFundsService = SpringContext.getBean(SufficientFundsService.class);

            List<SufficientFundsItem> items = sufficientFundsService.checkSufficientFunds(doc);

            // if ( items.size() > 0 ) {
            LOG.error("invokeRuleMethod() No sufficient funds");

            GlobalVariables.getErrorMap().putError("chartOrg", KFSKeyConstants.ERROR_MISSING, new String[] { "Sufficient Funds" });

            return false;
            // }
        }
        return true;
    }
}
