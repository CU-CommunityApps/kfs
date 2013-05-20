/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.rice.core.api.util.KeyValue; import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of string key value pairs for BankruptcyTypeOptions
 */
public class BankruptcyTypeOptionsValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {
        List<KeyValue> activeLabels = new ArrayList<KeyValue>();

        activeLabels.add(new ConcreteKeyValue(CGConstants.CHAPTER11_CODE, CGConstants.CHAPTER11));
        activeLabels.add(new ConcreteKeyValue(CGConstants.CHAPTER7_CODE, CGConstants.CHAPTER7));
        activeLabels.add(new ConcreteKeyValue(CGConstants.CHAPTER13_CODE, CGConstants.CHAPTER13));
        activeLabels.add(new ConcreteKeyValue(CGConstants.JUDGMENT_OBTAINED_CODE, CGConstants.JUDGMENT_OBTAINED));

        return activeLabels;
    }
}