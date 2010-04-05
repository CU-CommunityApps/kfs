/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;


public abstract class EndowmentSecurityDetailsDocumentBase extends EndowmentTransactionLinesDocumentBase implements EndowmentSecurityDetailsDocument {

    private EndowmentTransactionSecurity transactionSecurity;

    public String getRegistrationCode() {
        return transactionSecurity.getRegistrationCode();
    }

    public String getSecurityClassCode() {
        return transactionSecurity.getSecurityClassCode();
    }

    public String getSecurityId() {
        return transactionSecurity.getSecurityId();
    }

    public String getSecurityTaxLotIndicator() {
        return transactionSecurity.getSecurityTaxLotIndicator();
    }

    public String getSecurityTransactionCode() {
        return transactionSecurity.getSecurityTransactionCode();
    }

    public void setRegistrationCode(String registrationCode) {
        transactionSecurity.setRegistrationCode(registrationCode);

    }

    public void setSecurityClassCode(String securityClassCode) {
        transactionSecurity.setSecurityClassCode(securityClassCode);

    }

    public void setSecurityId(String securityId) {
        transactionSecurity.setSecurityId(securityId);

    }

    public void setSecurityTaxLotIndicator(String securityTaxLotIndicator) {
        transactionSecurity.setSecurityTaxLotIndicator(securityTaxLotIndicator);

    }

    public void setSecurityTransactionCode(String securityTransactionCode) {
        transactionSecurity.setSecurityTransactionCode(securityTransactionCode);

    }
}
