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
package org.kuali.kfs.fp.service;

import org.kuali.kfs.fp.businessobject.Bank;

/**
 * 
 * This service interface defines methods that a BankService implementation must provide.
 */
public interface BankService {

    /**
     * This method retrieves a bank object who's primary id matches the values provided.
     * 
     * @param financialDocumentBankCode The bank code to be looked up by.
     * @return A Bank object with a matching primary id.
     */
    Bank getByPrimaryId( String financialDocumentBankCode );
}
