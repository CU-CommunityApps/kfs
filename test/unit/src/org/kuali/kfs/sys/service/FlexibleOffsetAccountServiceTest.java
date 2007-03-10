/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import static org.kuali.core.util.SpringServiceLocator.getFlexibleOffsetAccountService;
import static org.kuali.test.fixtures.OffsetAccountFixture.OFFSET_ACCOUNT1;
import static org.kuali.test.util.KualiTestAssertionUtils.assertSparselyEqualBean;

import java.lang.reflect.InvocationTargetException;

import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestUtils;
import org.kuali.test.WithTestSpringContext;

/**
 * This class...
 * 
 * 
 */
@WithTestSpringContext
public class FlexibleOffsetAccountServiceTest extends KualiTestBase {

    public void testGetByPrimaryId_valid() throws NoSuchMethodException, InvocationTargetException {
        TestUtils.mockConfigurationServiceForFlexibleOffsetEnabled(true);
        OffsetAccount offsetAccount = getFlexibleOffsetAccountService().getByPrimaryIdIfEnabled(OFFSET_ACCOUNT1.chartOfAccountsCode, OFFSET_ACCOUNT1.accountNumber, OFFSET_ACCOUNT1.financialOffsetObjectCode);
        assertSparselyEqualBean(OFFSET_ACCOUNT1.createOffsetAccount(), offsetAccount);
        assertEquals(OFFSET_ACCOUNT1.chartOfAccountsCode, offsetAccount.getChart().getChartOfAccountsCode());
        assertEquals(OFFSET_ACCOUNT1.accountNumber, offsetAccount.getAccount().getAccountNumber());
        assertEquals(OFFSET_ACCOUNT1.financialOffsetChartOfAccountCode, offsetAccount.getFinancialOffsetChartOfAccount().getChartOfAccountsCode());
        assertEquals(OFFSET_ACCOUNT1.financialOffsetAccountNumber, offsetAccount.getFinancialOffsetAccount().getAccountNumber());
    }

    public void testGetByPrimaryId_validDisabled() throws NoSuchMethodException, InvocationTargetException {
        TestUtils.mockConfigurationServiceForFlexibleOffsetEnabled(false);
        assertNull(getFlexibleOffsetAccountService().getByPrimaryIdIfEnabled(OFFSET_ACCOUNT1.chartOfAccountsCode, OFFSET_ACCOUNT1.accountNumber, OFFSET_ACCOUNT1.financialOffsetAccountNumber));
    }

    public void testGetByPrimaryId_invalid() {
        TestUtils.mockConfigurationServiceForFlexibleOffsetEnabled(true);
        assertNull(getFlexibleOffsetAccountService().getByPrimaryIdIfEnabled("XX", "XX", "XX"));
    }

    /**
     * Integration test to the database parameter table (not the mock configuration service).
     */
    public void testGetEnabled() {
        // This tests that no RuntimeException is thrown because the parameter is missing from the database
        // or contains a value other than Y or N.
        getFlexibleOffsetAccountService().getEnabled();
    }
}
