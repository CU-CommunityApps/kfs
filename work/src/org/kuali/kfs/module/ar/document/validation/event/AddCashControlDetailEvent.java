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
package org.kuali.module.ar.rule.event;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.rule.AddCashControlDetailRule;
import org.kuali.module.ar.rule.AddCustomerInvoiceDetailRule;

public final class AddCashControlDetailEvent extends CashControlDetailEventBase {

    public AddCashControlDetailEvent(String errorPathPrefix, Document document, CashControlDetail cashControlDetail) {
        super("Adding cash control detail to document " + getDocumentId(document), errorPathPrefix, document, cashControlDetail);
    }

    public Class getRuleInterfaceClass() {
        return AddCustomerInvoiceDetailRule.class;
    }

    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddCashControlDetailRule) rule).processAddCashControlDetailBusinessRules((AccountingDocument) getDocument(), getCashControlDetail());
    }

}
