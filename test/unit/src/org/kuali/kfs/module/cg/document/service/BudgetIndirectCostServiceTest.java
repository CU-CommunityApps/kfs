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
package org.kuali.kfs.module.cg.document.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetIndirectCost;
import org.kuali.kfs.module.cg.businessobject.BudgetNonpersonnel;
import org.kuali.kfs.module.cg.businessobject.BudgetNonpersonnelTest;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriodTest;
import org.kuali.kfs.module.cg.businessobject.BudgetTask;
import org.kuali.kfs.module.cg.businessobject.BudgetTaskPeriodIndirectCost;
import org.kuali.kfs.module.cg.businessobject.UserAppointmentTaskPeriod;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class BudgetIndirectCostServiceTest extends KualiTestBase {

    protected void populateBudgetTasksPeriods(BudgetDocument budgetDocument) {

        List periods = BudgetPeriodTest.createBudgetPeriods(2);
        for (int i = 0; i < periods.size(); i++) {
            BudgetPeriod period = (BudgetPeriod) periods.get(i);
            period.setDocumentNumber("1234");
            period.setBudgetPeriodSequenceNumber(i);
        }

        BudgetTask task1 = new BudgetTask();
        task1.setDocumentNumber("1234");
        task1.setBudgetTaskSequenceNumber(new Integer(0));

        BudgetTask task2 = new BudgetTask();
        task2.setDocumentNumber("1234");
        task2.setBudgetTaskSequenceNumber(new Integer(1));

        List tasks = new ArrayList();
        tasks.add(task1);
        tasks.add(task2);

        Budget budget = new Budget();
        budget.setDocumentNumber("1234");
        budget.setTasks(tasks);
        budget.setPeriods(periods);

        budgetDocument.setBudget(budget);
    }

    public void testReconcileIndirectCost() {
        BudgetDocument budgetDocument = new BudgetDocument();
        // Test new document
        populateBudgetTasksPeriods(budgetDocument);

        SpringContext.getBean(BudgetIndirectCostService.class).reconcileIndirectCost(budgetDocument);

        BudgetIndirectCost indirectCost = budgetDocument.getBudget().getIndirectCost();
        assertEquals(indirectCost.getDocumentNumber(), "1234");

        List idcList = indirectCost.getBudgetTaskPeriodIndirectCostItems();
        assertEquals(idcList.size(), 4);

        BudgetTaskPeriodIndirectCost idc1 = (BudgetTaskPeriodIndirectCost) idcList.get(0);
        assertEquals(idc1.getDocumentNumber(), "1234");
        assertEquals(idc1.getBudgetTaskSequenceNumber(), new Integer(0));
        assertEquals(idc1.getBudgetPeriodSequenceNumber(), new Integer(0));

        BudgetTaskPeriodIndirectCost idc2 = (BudgetTaskPeriodIndirectCost) idcList.get(1);
        assertEquals(idc2.getDocumentNumber(), "1234");
        assertEquals(idc2.getBudgetTaskSequenceNumber(), new Integer(0));
        assertEquals(idc2.getBudgetPeriodSequenceNumber(), new Integer(1));

        BudgetTaskPeriodIndirectCost idc3 = (BudgetTaskPeriodIndirectCost) idcList.get(2);
        assertEquals(idc3.getDocumentNumber(), "1234");
        assertEquals(idc3.getBudgetTaskSequenceNumber(), new Integer(1));
        assertEquals(idc3.getBudgetPeriodSequenceNumber(), new Integer(0));

        BudgetTaskPeriodIndirectCost idc4 = (BudgetTaskPeriodIndirectCost) idcList.get(3);
        assertEquals(idc4.getDocumentNumber(), "1234");
        assertEquals(idc4.getBudgetTaskSequenceNumber(), new Integer(1));
        assertEquals(idc4.getBudgetPeriodSequenceNumber(), new Integer(1));

        // Test existing document to be updated
        BudgetTask newBudgetTask = new BudgetTask();
        newBudgetTask.setDocumentNumber("1234");
        newBudgetTask.setBudgetTaskSequenceNumber(2);

        budgetDocument.getBudget().getTasks().add(newBudgetTask);
        budgetDocument.getBudget().getPeriods().remove(0);
        budgetDocument.getBudget().getTasks().remove(1);

        SpringContext.getBean(BudgetIndirectCostService.class).reconcileIndirectCost(budgetDocument);

        indirectCost = budgetDocument.getBudget().getIndirectCost();
        assertFalse(indirectCost.getBudgetIndirectCostCostShareIndicator());
        assertFalse(indirectCost.getBudgetUnrecoveredIndirectCostIndicator());

        idcList = indirectCost.getBudgetTaskPeriodIndirectCostItems();
        assertEquals(idcList.size(), 2);

        idc1 = (BudgetTaskPeriodIndirectCost) idcList.get(0);
        assertEquals(idc1.getDocumentNumber(), "1234");
        assertEquals(idc1.getBudgetTaskSequenceNumber(), new Integer(0));
        assertEquals(idc1.getBudgetPeriodSequenceNumber(), new Integer(1));

        idc2 = (BudgetTaskPeriodIndirectCost) idcList.get(1);
        assertEquals(idc2.getDocumentNumber(), "1234");
        assertEquals(idc2.getBudgetTaskSequenceNumber(), new Integer(2));
        assertEquals(idc2.getBudgetPeriodSequenceNumber(), new Integer(1));
    }

    public void testRefreshIndirectCost() {
        BudgetDocument budgetDocument = new BudgetDocument();
        Budget budget = budgetDocument.getBudget();

        List periods = BudgetPeriodTest.createBudgetPeriods(2);
        for (int i = 0; i < periods.size(); i++) {
            BudgetPeriod period = (BudgetPeriod) periods.get(i);
            period.setDocumentNumber("1234");
            period.setBudgetPeriodSequenceNumber(i);
        }
        budget.setPeriods(periods);

        String[] categories = { "CO", "CO", "FL", "SC" };
        String[] subCategories = { "C1", "C1", "F5", "R2" };
        String[] subcontractorNumber = { "", "", "", "1" };
        List nonpersonnelItems = BudgetNonpersonnelTest.createBudgetNonpersonnel(categories, subCategories, subcontractorNumber);
        for (Iterator iter = nonpersonnelItems.iterator(); iter.hasNext();) {
            BudgetNonpersonnel nonpersonnel = (BudgetNonpersonnel) iter.next();
            nonpersonnel.setBudgetPeriodSequenceNumber(new Integer(2));
        }
        budget.setNonpersonnelItems(nonpersonnelItems);

        List userAppointmentTaskPeriods = new ArrayList();

        assertFalse("Budget periods should not be empty.", budget.getPeriods().isEmpty());
        BudgetPeriod period1 = (BudgetPeriod) budget.getPeriods().get(0);

        UserAppointmentTaskPeriod taskPeriod = new UserAppointmentTaskPeriod();
        taskPeriod.setBudgetPeriodSequenceNumber(period1.getBudgetPeriodSequenceNumber());
        taskPeriod.setAgencyRequestTotalAmount(new KualiInteger(39000));
        taskPeriod.setAgencyFringeBenefitTotalAmount(new KualiInteger(13000));
        userAppointmentTaskPeriods.add(taskPeriod);

        UserAppointmentTaskPeriod taskPeriod2 = new UserAppointmentTaskPeriod();
        taskPeriod2.setBudgetPeriodSequenceNumber(period1.getBudgetPeriodSequenceNumber());
        taskPeriod2.setAgencyRequestTotalAmount(new KualiInteger(43000));
        taskPeriod2.setAgencyFringeBenefitTotalAmount(new KualiInteger(8500));
        userAppointmentTaskPeriods.add(taskPeriod2);

        BudgetPeriod period2 = (BudgetPeriod) budget.getPeriods().get(1);

        UserAppointmentTaskPeriod taskPeriod3 = new UserAppointmentTaskPeriod();
        taskPeriod3.setBudgetPeriodSequenceNumber(period2.getBudgetPeriodSequenceNumber());
        taskPeriod3.setAgencyRequestTotalAmount(new KualiInteger(74000));
        taskPeriod3.setAgencyFringeBenefitTotalAmount(new KualiInteger(21500));
        userAppointmentTaskPeriods.add(taskPeriod3);

        budget.setAllUserAppointmentTaskPeriods(userAppointmentTaskPeriods);

    }
}
