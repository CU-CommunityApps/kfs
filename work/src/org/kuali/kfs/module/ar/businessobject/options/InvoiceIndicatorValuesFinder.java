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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsInvoiceIndicator;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;

/**
 * This class defines the link for Invoice Indicator.
 */
public class InvoiceIndicatorValuesFinder extends KeyValuesBase {

    protected List<KeyLabelPair> keyValues = new ArrayList();

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("unchecked")
    public List<KeyLabelPair> getKeyValues() {

        List<ContractsAndGrantsInvoiceIndicator> boList = (List<ContractsAndGrantsInvoiceIndicator>) SpringContext.getBean(KeyValuesService.class).findAll(ContractsAndGrantsInvoiceIndicator.class);
        keyValues.add(new KeyLabelPair("", ""));
        for (ContractsAndGrantsInvoiceIndicator element : boList) {
            if (element.isActive()) {
                keyValues.add(new KeyLabelPair(element.getInvoiceIndicator(), element.getInvoiceIndicatorDescription()));
            }
        }
        return keyValues;
    }

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase#clearInternalCache()
     */
    @Override
    public void clearInternalCache() {
        keyValues = null;
    }
}
