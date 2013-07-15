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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * This class returns list of payment method key value pairs.
 */
public class PaymentMethodValuesFinder extends KeyValuesBase {

    static List<KeyValue> activeLabels = new ArrayList<KeyValue>();
    static {
        activeLabels.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.ACH_CHECK.getCode(), KFSConstants.PaymentMethod.ACH_CHECK.getCodeAndName()));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.FOREIGN_DRAFT.getCode(), KFSConstants.PaymentMethod.FOREIGN_DRAFT.getCodeAndName()));
        activeLabels.add(new ConcreteKeyValue(KFSConstants.PaymentMethod.WIRE_TRANSFER.getCode(), KFSConstants.PaymentMethod.WIRE_TRANSFER.getCodeAndName()));
    }
    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        return activeLabels;
    }

}
