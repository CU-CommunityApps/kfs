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
package org.kuali.kfs.fp.document.validation;

import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.sys.document.AccountingDocument;

/**
 * Defines a rule which gets invoked immediately before a check is updated within a document.
 */
public interface UpdateCheckRule<F extends AccountingDocument> extends CheckRule {
    /**
     * @param financialDocument
     * @param check
     * @return true if the business rules pass, false otherwise.
     */
    public boolean processUpdateCheckRule(F financialDocument, Check check);
}
