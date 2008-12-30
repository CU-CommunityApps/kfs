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
package org.kuali.kfs.fp.document.authorization;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.InactiveDocumentTypeAuthorizationException;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Document Authorizer for the Budget Adjustment document.
 */
public class BudgetAdjustmentDocumentAuthorizer extends AccountingDocumentAuthorizerBase {

    /**
     * Check if base amount can be edited for the posting year, if so export base amount entry mode.
     * 
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kns.bo.user.KualiUser)
     */
    @Override
    public Map getEditMode(Document document, Person user) {
        Map editModeMap = super.getEditMode(document, user);
        if (SpringContext.getBean(FiscalYearFunctionControlService.class).isBaseAmountChangeAllowed(((BudgetAdjustmentDocument) document).getPostingYear())) {
            editModeMap.put(KfsAuthorizationConstants.BudgetAdjustmentEditMode.BASE_AMT_ENTRY, "TRUE");
        }

        return editModeMap;
    }
}

