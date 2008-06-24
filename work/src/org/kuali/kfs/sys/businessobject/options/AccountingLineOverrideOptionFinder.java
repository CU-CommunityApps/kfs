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
package org.kuali.kfs.sys.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.lookup.valueFinder.ValueFinder;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;

public class AccountingLineOverrideOptionFinder extends KeyValuesBase implements ValueFinder {

    public List getKeyValues() {

        List labels = new ArrayList();
        labels.add(new KeyLabelPair(AccountingLineOverride.CODE.NONE, "NONE"));
        labels.add(new KeyLabelPair(AccountingLineOverride.CODE.EXPIRED_ACCOUNT, "EXPIRED_ACCOUNT"));
        labels.add(new KeyLabelPair(AccountingLineOverride.CODE.NON_FRINGE_ACCOUNT_USED, "NON_FRINGE_ACCOUNT_USED"));
        labels.add(new KeyLabelPair(AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED, "EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED"));

        return labels;
    }

    /**
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return AccountingLineOverride.CODE.EXPIRED_ACCOUNT;
    }
}
