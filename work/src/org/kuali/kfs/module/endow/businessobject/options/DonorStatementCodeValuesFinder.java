/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.DonorStatementCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

public class DonorStatementCodeValuesFinder extends KeyValuesBase {
    
    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyValue> getKeyValues() {

        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<DonorStatementCode> codes = boService.findAll(DonorStatementCode.class);
        List<KeyValue> labels = new ArrayList<KeyValue>();
        
        labels.add(new ConcreteKeyValue("", ""));
        for (Iterator<DonorStatementCode> iter = codes.iterator(); iter.hasNext();) {
            DonorStatementCode donorStatementCode = (DonorStatementCode) iter.next();
            labels.add(new ConcreteKeyValue(donorStatementCode.getCode(), donorStatementCode.getName()));
        }

        return labels;
    }

}
