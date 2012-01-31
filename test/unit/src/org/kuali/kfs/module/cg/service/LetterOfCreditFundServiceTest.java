/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.cg.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.cg.businessobject.LetterOfCreditFund;
import org.kuali.kfs.module.cg.fixture.LetterOfCreditFundFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * This class tests the LetterOfCreditFundService
 */
@ConfigureContext(session = khuntley)
public class LetterOfCreditFundServiceTest extends KualiTestBase {


    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    public void testGetByPrimaryId() throws Exception {
        LetterOfCreditFund testEntry = LetterOfCreditFundFixture.CG_LOCF.createLetterOfCreditFund();
        SpringContext.getBean(BusinessObjectService.class).save(testEntry);
        LetterOfCreditFund letterOfCreditFundEntry = SpringContext.getBean(LetterOfCreditFundService.class).getByPrimaryId(testEntry.getLetterOfCreditFundCode());
        assertNotNull(letterOfCreditFundEntry);
    }
}