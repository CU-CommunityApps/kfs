/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableInvoiceTemplate;
import org.kuali.kfs.module.ar.businessobject.InvoiceTemplate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KeyValuesService;
import org.kuali.rice.krad.service.KualiModuleService;

/**
 * This class defines the link for Billing Frequency
 */
public class AgencyInvoiceTemplateValuesFinder extends KeyValuesBase {

    protected List<KeyValue> keyValues = new ArrayList();

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {
        List<AccountsReceivableInvoiceTemplate> boList = (List<AccountsReceivableInvoiceTemplate>) SpringContext.getBean(KeyValuesService.class).findAll(AccountsReceivableInvoiceTemplate.class);
        keyValues.add(new ConcreteKeyValue("", ""));
        for (AccountsReceivableInvoiceTemplate element : boList) {
            if (!element.isAccessRestrictedIndicator() && element.isActive()) {
                keyValues.add(new ConcreteKeyValue(element.getInvoiceTemplateCode(), element.getInvoiceTemplateDescription()));
            }
            else {
                if (element.isValidOrganization() && element.isActive()) {
                    keyValues.add(new ConcreteKeyValue(element.getInvoiceTemplateCode(), element.getInvoiceTemplateDescription()));
                }
            }
        }
        return keyValues;
    }

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesBase#clearInternalCache()
     */
    @Override
    public void clearInternalCache() {
        keyValues = null;
    }

}