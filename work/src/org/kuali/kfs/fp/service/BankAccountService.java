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

import org.kuali.kfs.fp.businessobject.BankAccount;

/**
 * 
 * This service interface defines methods that a BankAccountServiceImplementation must provide.
 */
public interface BankAccountService {
    
    /**
     * This method retrieves a bank account who's primary id matches the values passed in.
     * 
     * @param financialDocumentBankCode The bank code
     * @param finDocumentBankAccountNumber The bank account number
     * @return A BankAccount object who's primary id matches the values provided.
     */
    BankAccount getByPrimaryId( String financialDocumentBankCode, String finDocumentBankAccountNumber );
}
