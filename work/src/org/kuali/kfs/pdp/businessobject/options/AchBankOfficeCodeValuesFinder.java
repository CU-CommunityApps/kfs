/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.pdp.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * returns valid effort reporting fiscal periods 1 - 12
 */
public class AchBankOfficeCodeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair(PdpConstants.AchBankOfficeCodes.AchBankOfficeCode_O, PdpConstants.AchBankOfficeCodes.AchBankOfficeCode_O));
        keyValues.add(new KeyLabelPair(PdpConstants.AchBankOfficeCodes.AchBankOfficeCode_B, PdpConstants.AchBankOfficeCodes.AchBankOfficeCode_B));
        
        return keyValues;
    }
}
