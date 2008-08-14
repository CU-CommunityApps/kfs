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
package org.kuali.kfs.coa.businessobject.defaultvalue;

import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;

/**
 * This class represents a value finder that returns the currently logged in user's default chart of accounts code.
 */
public class CurrentUserChartValueFinder implements ValueFinder {

    /**
     * This method returns the current user's default chart of accounts code.
     * 
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        FinancialSystemUser currentUser = ValueFinderUtil.getCurrentFinancialSystemUser();
        if (currentUser != null) {
            return currentUser.getChartOfAccountsCode();
        }
        else {
            return "";
        }
    }

}
