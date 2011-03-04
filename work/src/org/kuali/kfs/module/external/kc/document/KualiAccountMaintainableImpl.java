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
package org.kuali.kfs.module.external.kc.document;

import org.kuali.kfs.coa.businessobject.Account;

/**
 * This class overrides the saveBusinessObject() method which is called during post process from the KualiPostProcessor so that it
 * can automatically deactivate the Sub-Accounts related to the account It also overrides the processAfterCopy so that it sets
 * specific fields that shouldn't be copied to default values {@link KualiPostProcessor}
 */
public class KualiAccountMaintainableImpl extends org.kuali.kfs.coa.document.KualiAccountMaintainableImpl {
   
    /**
     * @see org.kuali.kfs.coa.document.KualiAccountMaintainableImpl#lookupAccountCfda(java.lang.String, java.lang.String)
     */
    @Override
    protected String lookupAccountCfda(String accountNumber, String currentCfda) {
        Account account = (Account) this.getBusinessObject();
        return account.getCfda().getCfdaNumber();
    }
}
