/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

/**
 * Value Finder for Payment Request Statuses.
 */
public class PaymentRequestStatusValuesFinder extends KeyValuesBase {

    /**
     * Overide this method to sort the PO statuses for proper display. 
     * 
     * @see org.kuali.kfs.module.purap.businessobject.options.PurApStatusKeyValuesBase#getKeyValues()
     */
    public List getKeyValues() {
        // get all PREQ statuses
        HashMap<String, String> keyValues = PaymentRequestStatuses.getAllAppDocStatuses();
        SortedSet<String> sortedKeys= new TreeSet<String>(keyValues.keySet());
                
        // generate output
        List labels = new ArrayList();
        for (String sortedKey : sortedKeys) {
            labels.add(new KeyLabelPair(sortedKey, sortedKey));
        }
        return labels;
    }
}
