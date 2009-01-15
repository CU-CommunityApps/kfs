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
package org.kuali.kfs.module.cg.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriodTest;
import org.kuali.kfs.module.cg.businessobject.BudgetTask;
import org.kuali.kfs.module.cg.businessobject.BudgetTaskPeriodIndirectCost;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.kns.util.KualiInteger;

@ConfigureContext
public class BudgetIndirectCostFormHelperTest extends KualiTestBase {

    public void testInitializeTotals() {

        BudgetTask task1 = new BudgetTask();
        task1.setDocumentNumber("1234");
        task1.setBudgetTaskSequenceNumber(new Integer(0));

        BudgetTask task2 = new BudgetTask();
        task2.setDocumentNumber("1234");
        task2.setBudgetTaskSequenceNumber(new Integer(1));

        List tasks = new ArrayList();
        tasks.add(task1);
        tasks.add(task2);

        List periods = BudgetPeriodTest.createBudgetPeriods(2);
        for (int i = 0; i < periods.size(); i++) {
            BudgetPeriod period = (BudgetPeriod) periods.get(i);
            period.setDocumentNumber("1234");
            period.setBudgetPeriodSequenceNumber(i);
        }

        BudgetIndirectCostFormHelper formHelper = new BudgetIndirectCostFormHelper();
        formHelper.initializeTotals(tasks, periods);

        List taskTotals = formHelper.getTaskTotals();
        List periodTotals = formHelper.getPeriodTotals();

        assertEquals(periodTotals.size(), 2);
        assertEquals(taskTotals.size(), 2);

        BudgetTaskPeriodIndirectCost idc1 = (BudgetTaskPeriodIndirectCost) taskTotals.get(0);
        assertEquals(idc1.getBudgetTaskSequenceNumber(), new Integer(0));
        assertEquals(idc1.getTask(), task1);

        BudgetTaskPeriodIndirectCost idc2 = (BudgetTaskPeriodIndirectCost) taskTotals.get(1);
        assertEquals(idc2.getBudgetTaskSequenceNumber(), new Integer(1));
        assertEquals(idc2.getTask(), task2);

        BudgetTaskPeriodIndirectCost idc3 = (BudgetTaskPeriodIndirectCost) periodTotals.get(0);
        assertEquals(idc3.getBudgetPeriodSequenceNumber(), new Integer(0));
        assertEquals(idc3.getPeriod(), (BudgetPeriod) periods.get(0));

        BudgetTaskPeriodIndirectCost idc4 = (BudgetTaskPeriodIndirectCost) periodTotals.get(1);
        assertEquals(idc4.getBudgetPeriodSequenceNumber(), new Integer(1));
        assertEquals(idc4.getPeriod(), (BudgetPeriod) periods.get(1));
    }

    public void testUpdateTotals() {

        BudgetTaskPeriodIndirectCost idcTask0Exist = new BudgetTaskPeriodIndirectCost();
        idcTask0Exist.setBudgetTaskSequenceNumber(new Integer(0));
        idcTask0Exist.setTotalDirectCost(new KualiInteger(1500));
        idcTask0Exist.setBaseCost(new KualiInteger(1000));
        idcTask0Exist.setCalculatedIndirectCost(new KualiInteger(100));
        idcTask0Exist.setCostShareBaseCost(new KualiInteger(500));
        idcTask0Exist.setCostShareCalculatedIndirectCost(new KualiInteger(300));
        idcTask0Exist.setCostShareUnrecoveredIndirectCost(new KualiInteger(200));

        List taskTotalsList = new ArrayList();
        taskTotalsList.add(idcTask0Exist);

        BudgetIndirectCostFormHelper formHelper = new BudgetIndirectCostFormHelper();
        formHelper.setTaskTotals(taskTotalsList);

        BudgetTaskPeriodIndirectCost idcTask0Update = new BudgetTaskPeriodIndirectCost();
        idcTask0Update.setBudgetTaskSequenceNumber(new Integer(0));
        idcTask0Update.setTotalDirectCost(new KualiInteger(500));
        idcTask0Update.setBaseCost(new KualiInteger(400));
        idcTask0Update.setCalculatedIndirectCost(new KualiInteger(300));
        idcTask0Update.setCostShareBaseCost(new KualiInteger(200));
        idcTask0Update.setCostShareCalculatedIndirectCost(new KualiInteger(100));
        idcTask0Update.setCostShareUnrecoveredIndirectCost(new KualiInteger(50));

        BudgetTaskPeriodIndirectCost idcPeriod0Exist = new BudgetTaskPeriodIndirectCost();
        idcPeriod0Exist.setBudgetPeriodSequenceNumber(new Integer(0));
        idcPeriod0Exist.setTotalDirectCost(new KualiInteger(1500));
        idcPeriod0Exist.setBaseCost(new KualiInteger(1000));
        idcPeriod0Exist.setCalculatedIndirectCost(new KualiInteger(100));
        idcPeriod0Exist.setCostShareBaseCost(new KualiInteger(500));
        idcPeriod0Exist.setCostShareCalculatedIndirectCost(new KualiInteger(300));
        idcPeriod0Exist.setCostShareUnrecoveredIndirectCost(new KualiInteger(200));

        List periodTotalsList = new ArrayList();
        periodTotalsList.add(idcPeriod0Exist);

        formHelper.setPeriodTotals(periodTotalsList);

        BudgetTaskPeriodIndirectCost idcPeriod0Update = new BudgetTaskPeriodIndirectCost();
        idcPeriod0Update.setBudgetPeriodSequenceNumber(new Integer(0));
        idcPeriod0Update.setTotalDirectCost(new KualiInteger(500));
        idcPeriod0Update.setBaseCost(new KualiInteger(400));
        idcPeriod0Update.setCalculatedIndirectCost(new KualiInteger(300));
        idcPeriod0Update.setCostShareBaseCost(new KualiInteger(200));
        idcPeriod0Update.setCostShareCalculatedIndirectCost(new KualiInteger(100));
        idcPeriod0Update.setCostShareUnrecoveredIndirectCost(new KualiInteger(50));

        List idcItems = new ArrayList();
        idcItems.add(idcTask0Update);
        idcItems.add(idcPeriod0Update);
        formHelper.updateTotals(idcItems);

        BudgetTaskPeriodIndirectCost idcTask0Merge = (BudgetTaskPeriodIndirectCost) formHelper.getTaskTotals().get(0);
        assertEquals(idcTask0Merge.getTotalDirectCost(), new KualiInteger(2000));
        assertEquals(idcTask0Merge.getBaseCost(), new KualiInteger(1400));
        assertEquals(idcTask0Merge.getCalculatedIndirectCost(), new KualiInteger(400));
        assertEquals(idcTask0Merge.getCostShareBaseCost(), new KualiInteger(700));
        assertEquals(idcTask0Merge.getCostShareCalculatedIndirectCost(), new KualiInteger(400));
        assertEquals(idcTask0Merge.getCostShareUnrecoveredIndirectCost(), new KualiInteger(250));

        BudgetTaskPeriodIndirectCost idcPeriod0Merge = (BudgetTaskPeriodIndirectCost) formHelper.getTaskTotals().get(0);
        assertEquals(idcPeriod0Merge.getTotalDirectCost(), new KualiInteger(2000));
        assertEquals(idcPeriod0Merge.getBaseCost(), new KualiInteger(1400));
        assertEquals(idcPeriod0Merge.getCalculatedIndirectCost(), new KualiInteger(400));
        assertEquals(idcPeriod0Merge.getCostShareBaseCost(), new KualiInteger(700));
        assertEquals(idcPeriod0Merge.getCostShareCalculatedIndirectCost(), new KualiInteger(400));
        assertEquals(idcPeriod0Merge.getCostShareUnrecoveredIndirectCost(), new KualiInteger(250));

        BudgetTaskPeriodIndirectCost subTotalMerge = formHelper.getPeriodSubTotal();
        assertEquals(subTotalMerge.getTotalDirectCost(), new KualiInteger(1000));
        assertEquals(subTotalMerge.getBaseCost(), new KualiInteger(800));
        assertEquals(subTotalMerge.getCalculatedIndirectCost(), new KualiInteger(600));
        assertEquals(subTotalMerge.getCostShareBaseCost(), new KualiInteger(400));
        assertEquals(subTotalMerge.getCostShareCalculatedIndirectCost(), new KualiInteger(200));
        assertEquals(subTotalMerge.getCostShareUnrecoveredIndirectCost(), new KualiInteger(100));
    }
}
