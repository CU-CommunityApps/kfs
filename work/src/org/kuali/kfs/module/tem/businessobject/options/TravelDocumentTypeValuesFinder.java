/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

public class TravelDocumentTypeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        keyValues.add(new ConcreteKeyValue(TemConstants.TravelDocTypes.TEM_TRANSACTIONAL_DOCUMENT, TemConstants.TravelDocTypes.TEM_TRANSACTIONAL_DOCUMENT));
        keyValues.add(new ConcreteKeyValue(TemConstants.TravelDocTypes.TRAVEL_TRANSACTIONAL_DOCUMENT, TemConstants.TravelDocTypes.TRAVEL_TRANSACTIONAL_DOCUMENT));
        keyValues.add(new ConcreteKeyValue(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT, TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT));
        keyValues.add(new ConcreteKeyValue(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT, TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT));
        keyValues.add(new ConcreteKeyValue(TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT, TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT));
        keyValues.add(new ConcreteKeyValue(TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT, TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT));

        return keyValues;
    }
}
