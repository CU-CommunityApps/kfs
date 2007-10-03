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
package org.kuali.module.labor.document;

import org.kuali.module.financial.document.YearEndDocument;

/**
 * Document Class for the Year End Salary Expense Transfer Document.
 */
public class YearEndSalaryExpenseTransferDocument extends SalaryExpenseTransferDocument implements YearEndDocument {

    /**
     * Class constructor that invokes <code>SalaryExpenseTransferDocument</code> constructor.
     */
    public YearEndSalaryExpenseTransferDocument() {
        super();
    }
}
