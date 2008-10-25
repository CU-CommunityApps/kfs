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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceReceivableProjectCodeValidationTest extends KualiTestBase {
    
    private CustomerInvoiceReceivableProjectCodeValidation validation;
    private final static String VALID_PROJECT_CODE = "BOB";
    private final static String INVALID_PROJECT_CODE = "123";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceReceivableProjectCodeValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }    
    
    public void testValidProjectCode_True(){
        validation.getCustomerInvoiceDocument().setPaymentProjectCode(VALID_PROJECT_CODE);
        assertTrue(validation.validate(null));
    }
    
    public void testValidProjectCode_False(){
        validation.getCustomerInvoiceDocument().setPaymentProjectCode(INVALID_PROJECT_CODE);
        assertFalse(validation.validate(null));
    }

}

