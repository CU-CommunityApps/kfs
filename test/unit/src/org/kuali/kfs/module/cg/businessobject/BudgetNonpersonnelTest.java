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
package org.kuali.kfs.module.cg.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.module.cg.businessobject.BudgetNonpersonnel;
import org.kuali.kfs.module.cg.businessobject.NonpersonnelObjectCode;
import org.kuali.kfs.ConfigureContext;

/**
 * This class tests methods in BudgetNonpersonnelCopyOverFormHelper.
 */
@ConfigureContext
public class BudgetNonpersonnelTest extends KualiTestBase {

    public void testBudgetNonpersonnel() {
        assertTrue(true);
    }

    public static List createBudgetNonpersonnel(String[] nonpersonnelCategories, String[] subCategories, String[] subcontractorNumber) {
        List budgetNonpersonnelList = new ArrayList();

        if (nonpersonnelCategories.length != subCategories.length) {
            throw new IllegalArgumentException("nonpersonnelCategories and subCategories must equal in length");
        }

        for (int i = 0; i < nonpersonnelCategories.length; i++) {
            BudgetNonpersonnel budgetNonpersonnel = new BudgetNonpersonnel();
            budgetNonpersonnel.setBudgetNonpersonnelSequenceNumber(new Integer(i));
            budgetNonpersonnel.setBudgetPeriodSequenceNumber(new Integer(0));
            budgetNonpersonnel.setBudgetTaskSequenceNumber(new Integer(0));

            budgetNonpersonnel.setSubcontractorNumber(subcontractorNumber[i]);

            budgetNonpersonnel.setBudgetNonpersonnelCategoryCode(nonpersonnelCategories[i]);
            budgetNonpersonnel.setBudgetNonpersonnelSubCategoryCode(subCategories[i]);
            budgetNonpersonnel.setNonpersonnelObjectCode(new NonpersonnelObjectCode(nonpersonnelCategories[i], subCategories[i]));

            budgetNonpersonnel.setCopyToFuturePeriods(false);
            budgetNonpersonnel.setAgencyCopyIndicator(false);
            budgetNonpersonnel.setBudgetInstitutionCostShareCopyIndicator(false);
            budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);

            budgetNonpersonnel.setAgencyRequestAmount(new KualiInteger(1000));
            budgetNonpersonnel.setBudgetInstitutionCostShareAmount(new KualiInteger(2000));
            budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(new KualiInteger(3000));

            budgetNonpersonnel.setBudgetOriginSequenceNumber(null);

            budgetNonpersonnelList.add(budgetNonpersonnel);
        }

        return budgetNonpersonnelList;
    }
}
