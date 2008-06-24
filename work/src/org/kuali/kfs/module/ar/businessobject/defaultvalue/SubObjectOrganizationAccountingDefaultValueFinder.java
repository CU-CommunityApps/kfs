/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject.defaultvalue;

import org.kuali.core.lookup.valueFinder.ValueFinder;

public class SubObjectOrganizationAccountingDefaultValueFinder extends ObjectOrganizationAccountingDefaultValueFinder implements ValueFinder {

    /**
     * Returns default sub account number from organization accounting default if BO is not null.
     * 
     * @see org.kuali.core.lookup.valueFinder.ValueFinder#getValue()
     */
    public String getValue() {
        return (organizationAccountingDefault != null)? organizationAccountingDefault.getDefaultInvoiceFinancialSubObjectCode() : "";
    }

}
