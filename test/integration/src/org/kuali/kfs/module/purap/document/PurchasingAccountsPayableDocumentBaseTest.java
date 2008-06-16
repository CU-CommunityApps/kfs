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
package org.kuali.module.purap.document;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.purap.bo.AccountsPayableItem;
import org.kuali.module.purap.rules.PurchasingDocumentRuleBase;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
public class PurchasingAccountsPayableDocumentBaseTest extends KualiTestBase {

    PurchasingAccountsPayableDocument purapDoc;
    Integer currentFY;

    protected void setUp() throws Exception {
        super.setUp();
        GlobalVariables.setMessageList(new ArrayList<String>());
        purapDoc = new PurchaseOrderDocument();
        currentFY = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
    }

    protected void tearDown() throws Exception {
        purapDoc = null;
        super.tearDown();
    }
    
    public void testIsPostingYearNext_UseCurrent() {
        purapDoc.setPostingYear(currentFY);
        assertFalse(purapDoc.isPostingYearNext());
    }

    public void testIsPostingYearNext_UseNext() {
        purapDoc.setPostingYear(currentFY + 1);
        assertTrue(purapDoc.isPostingYearNext());
    }

    public void testIsPostingYearNext_UsePast() {
        purapDoc.setPostingYear(currentFY - 1);
        assertFalse(purapDoc.isPostingYearNext());
    }

    public void testGetPostingYearNextOrCurrent_UseCurrent() {
        purapDoc.setPostingYear(currentFY);
        assertEquals(purapDoc.getPostingYearNextOrCurrent(), currentFY);
    }

    public void testGetPostingYearNextOrCurrent_UseNext() {
        Integer nextFY = currentFY + 1;
        purapDoc.setPostingYear(nextFY);
        assertEquals(purapDoc.getPostingYearNextOrCurrent(), nextFY);
    }

    public void testGetPostingYearNextOrCurrent_UsePast() {
        purapDoc.setPostingYear(currentFY - 1);
        assertEquals(purapDoc.getPostingYearNextOrCurrent(), currentFY);
    }

}
