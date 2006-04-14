/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.kra.bo.AgencyExtension;
import org.kuali.module.kra.bo.Budget;
import org.kuali.module.kra.bo.BudgetModular;
import org.kuali.module.kra.bo.BudgetModularPeriod;
import org.kuali.module.kra.bo.BudgetNonpersonnel;
import org.kuali.module.kra.bo.BudgetNonpersonnelTest;
import org.kuali.module.kra.bo.BudgetPeriod;
import org.kuali.module.kra.bo.BudgetPeriodTest;
import org.kuali.module.kra.bo.UserAppointmentTaskPeriod;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests service methods in BudgetModularService.
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetModularServiceTest extends KualiTestBaseWithSpring {
    
    private BudgetModularService budgetModularService;
    private BudgetNonpersonnelService budgetNonpersonnelService;
    
    private List nonpersonnelCategories;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        budgetModularService = SpringServiceLocator.getBudgetModularService();
        budgetNonpersonnelService = SpringServiceLocator.getBudgetNonpersonnelService();
        nonpersonnelCategories = budgetNonpersonnelService.getAllNonpersonnelCategories();
    }
    
    protected Budget setupBudget() {
        Budget budget = new Budget();
        
        Agency agency = new Agency();
        agency.setAgencyExtension(new AgencyExtension());
        agency.getAgencyExtension().setAgencyModularIndicator(true);
        agency.getAgencyExtension().setBudgetModularIncrementAmount(new Integer(25000));
        agency.getAgencyExtension().setBudgetPeriodMaximumAmount(new Integer(250000));
        
        budget.setPeriods(BudgetPeriodTest.createBudgetPeriods(2));
        
        budget.setBudgetAgency(agency);
        return budget;
    }
    
    
    public void testGenerateModularBudget() {
        KualiDecimal zeroValue = new KualiDecimal(0);
        
        // Case 1: Budget with no costs
        Budget budget = setupBudget();
        budgetModularService.generateModularBudget(budget, nonpersonnelCategories);
        BudgetModular modularBudget = budget.getModularBudget();
        
        assertFalse(modularBudget.isInvalidMode());
        assertEquals(modularBudget.getIncrements().size(), 10);
        assertEquals(modularBudget.getBudgetModularDirectCostAmount(), zeroValue);
        assertEquals(modularBudget.getTotalActualDirectCostAmount(), zeroValue);
        assertEquals(modularBudget.getTotalAdjustedModularDirectCostAmount(), zeroValue);
        assertEquals(modularBudget.getTotalConsortiumAmount(), zeroValue);
        assertEquals(modularBudget.getTotalDirectCostAmount(), zeroValue);
        assertEquals(modularBudget.getTotalModularDirectCostAmount(), zeroValue);
        
        for (Iterator iter = modularBudget.getBudgetModularPeriods().iterator(); iter.hasNext();) {
            BudgetModularPeriod modularPeriod = (BudgetModularPeriod) iter.next();
            assertEquals(modularPeriod.getActualDirectCostAmount(), zeroValue);
            assertEquals(modularPeriod.getConsortiumAmount(), zeroValue);
            assertEquals(modularPeriod.getTotalPeriodDirectCostAmount(), zeroValue);
            assertEquals(modularPeriod.getBudgetAdjustedModularDirectCostAmount(), new Integer(0));
        }
        
        // Case 2: Budget with personnel, nonpersonnel, consortium costs
        budget = setupBudget();
        
        String [] categories = {"CO", "CO", "FL", "SC"};
        String [] subCategories = {"C1", "C1", "F5", "R2"};
        List nonpersonnelItems = BudgetNonpersonnelTest.createBudgetNonpersonnel(categories, subCategories);
        for (Iterator iter = nonpersonnelItems.iterator(); iter.hasNext();) {
            BudgetNonpersonnel nonpersonnel = (BudgetNonpersonnel) iter.next();
            nonpersonnel.setBudgetPeriodSequenceNumber(new Integer(2));
        }
        budget.setNonpersonnelItems(nonpersonnelItems);
        
        List userAppointmentTaskPeriods = new ArrayList();
        
        BudgetPeriod period1 = (BudgetPeriod) budget.getPeriods().get(0);
            
        UserAppointmentTaskPeriod taskPeriod = new UserAppointmentTaskPeriod();
        taskPeriod.setBudgetPeriodSequenceNumber(period1.getBudgetPeriodSequenceNumber());
        taskPeriod.setAgencyRequestTotalAmount(new Integer(39000));
        taskPeriod.setAgencyFringeBenefitTotalAmount(new Integer(13000));
        userAppointmentTaskPeriods.add(taskPeriod);
            
        UserAppointmentTaskPeriod taskPeriod2 = new UserAppointmentTaskPeriod();
        taskPeriod2.setBudgetPeriodSequenceNumber(period1.getBudgetPeriodSequenceNumber());
        taskPeriod2.setAgencyRequestTotalAmount(new Integer(43000));
        taskPeriod2.setAgencyFringeBenefitTotalAmount(new Integer(8500));
        userAppointmentTaskPeriods.add(taskPeriod2);
        
        BudgetPeriod period2 = (BudgetPeriod) budget.getPeriods().get(1);
        
        UserAppointmentTaskPeriod taskPeriod3 = new UserAppointmentTaskPeriod();
        taskPeriod3.setBudgetPeriodSequenceNumber(period2.getBudgetPeriodSequenceNumber());
        taskPeriod3.setAgencyRequestTotalAmount(new Integer(74000));
        taskPeriod3.setAgencyFringeBenefitTotalAmount(new Integer(21500));
        userAppointmentTaskPeriods.add(taskPeriod3);
        
        budget.setAllUserAppointmentTaskPeriods(userAppointmentTaskPeriods);
        
        budgetModularService.generateModularBudget(budget, nonpersonnelCategories);
        modularBudget = budget.getModularBudget();
        
        assertFalse(modularBudget.isInvalidMode());
        assertEquals(modularBudget.getIncrements().size(), 10);
        assertEquals(modularBudget.getBudgetModularDirectCostAmount(), new KualiDecimal(125000));
        assertEquals(modularBudget.getTotalActualDirectCostAmount(), new KualiDecimal(202000));
        assertEquals(modularBudget.getTotalAdjustedModularDirectCostAmount(), new KualiDecimal(250000));
        assertEquals(modularBudget.getTotalConsortiumAmount(), new KualiDecimal(1000));
        assertEquals(modularBudget.getTotalDirectCostAmount(), new KualiDecimal(251000));
        assertEquals(modularBudget.getTotalModularDirectCostAmount(), new KualiDecimal(250000));
        
        BudgetModularPeriod modularPeriod1 = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(0);
        assertEquals(modularPeriod1.getActualDirectCostAmount(), new KualiDecimal(103500));
        assertEquals(modularPeriod1.getConsortiumAmount(), zeroValue);
        assertEquals(modularPeriod1.getTotalPeriodDirectCostAmount(), new KualiDecimal(125000));
        assertEquals(modularPeriod1.getBudgetAdjustedModularDirectCostAmount(), new Integer(125000));
        
        BudgetModularPeriod modularPeriod2 = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(1);
        assertEquals(modularPeriod2.getActualDirectCostAmount(), new KualiDecimal(98500));
        assertEquals(modularPeriod2.getConsortiumAmount(), new KualiDecimal(1000));
        assertEquals(modularPeriod2.getTotalPeriodDirectCostAmount(), new KualiDecimal(126000));
        assertEquals(modularPeriod2.getBudgetAdjustedModularDirectCostAmount(), new Integer(125000));
        
        // Case 3: Budget with costs > # of periods * period maximum
        budget = setupBudget();
        
        BudgetPeriod periodInvalid = (BudgetPeriod) budget.getPeriods().get(0);
        
        UserAppointmentTaskPeriod taskPeriod4 = new UserAppointmentTaskPeriod();
        taskPeriod4.setBudgetPeriodSequenceNumber(periodInvalid.getBudgetPeriodSequenceNumber());
        taskPeriod4.setAgencyRequestTotalAmount(new Integer(1000000));
        userAppointmentTaskPeriods = new ArrayList();
        userAppointmentTaskPeriods.add(taskPeriod4);
        budget.setAllUserAppointmentTaskPeriods(userAppointmentTaskPeriods);
        
        budgetModularService.generateModularBudget(budget, nonpersonnelCategories);
        modularBudget = budget.getModularBudget();
        
        assertTrue(modularBudget.isInvalidMode());
        assertEquals(modularBudget.getIncrements().size(), 1);
        assertNull(modularBudget.getBudgetModularDirectCostAmount());
        assertEquals(modularBudget.getTotalActualDirectCostAmount(), new KualiDecimal(1000000));
        assertNull(modularBudget.getTotalAdjustedModularDirectCostAmount());
        assertEquals(modularBudget.getTotalConsortiumAmount(), zeroValue);
        assertNull(modularBudget.getTotalDirectCostAmount());
        assertNull(modularBudget.getTotalModularDirectCostAmount());
        
        BudgetModularPeriod modularPeriodInvalid1 = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(0);
        assertEquals(modularPeriodInvalid1.getActualDirectCostAmount(), new KualiDecimal(1000000));
        assertEquals(modularPeriodInvalid1.getConsortiumAmount(), zeroValue);
        assertNull(modularPeriodInvalid1.getTotalPeriodDirectCostAmount());
        assertNull(modularPeriodInvalid1.getBudgetAdjustedModularDirectCostAmount());
        
        BudgetModularPeriod modularPeriodInvalid2 = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(1);
        assertEquals(modularPeriodInvalid2.getActualDirectCostAmount(), zeroValue);
        assertEquals(modularPeriodInvalid2.getConsortiumAmount(), zeroValue);
        assertNull(modularPeriodInvalid2.getTotalPeriodDirectCostAmount());
        assertNull(modularPeriodInvalid2.getBudgetAdjustedModularDirectCostAmount());
    }
    
    
    public void testResetModularBudget() {
        
        // Case 1: Budget with no costs
        Budget budget = setupBudget();
        budgetModularService.resetModularBudget(budget);
        BudgetModular modularBudget = budget.getModularBudget();
        
        assertFalse(modularBudget.isInvalidMode());
        for (Iterator iter = modularBudget.getBudgetModularPeriods().iterator(); iter.hasNext();) {
            BudgetModularPeriod modularPeriod = (BudgetModularPeriod) iter.next();
            assertEquals(modularPeriod.getBudgetAdjustedModularDirectCostAmount(), new Integer(0));
        }
        
        // Case 2: Budget with personnel, nonpersonnel, consortium costs
        budget = setupBudget();
        
        String [] categories = {"CO", "CO", "FL", "SC"};
        String [] subCategories = {"C1", "C1", "F5", "R2"};
        List nonpersonnelItems = BudgetNonpersonnelTest.createBudgetNonpersonnel(categories, subCategories);
        for (Iterator iter = nonpersonnelItems.iterator(); iter.hasNext();) {
            BudgetNonpersonnel nonpersonnel = (BudgetNonpersonnel) iter.next();
            nonpersonnel.setBudgetPeriodSequenceNumber(new Integer(2));
        }
        budget.setNonpersonnelItems(nonpersonnelItems);
        
        List userAppointmentTaskPeriods = new ArrayList();
        
        BudgetPeriod period1 = (BudgetPeriod) budget.getPeriods().get(0);
            
        UserAppointmentTaskPeriod taskPeriod = new UserAppointmentTaskPeriod();
        taskPeriod.setBudgetPeriodSequenceNumber(period1.getBudgetPeriodSequenceNumber());
        taskPeriod.setAgencyRequestTotalAmount(new Integer(39000));
        taskPeriod.setAgencyFringeBenefitTotalAmount(new Integer(13000));
        userAppointmentTaskPeriods.add(taskPeriod);
            
        UserAppointmentTaskPeriod taskPeriod2 = new UserAppointmentTaskPeriod();
        taskPeriod2.setBudgetPeriodSequenceNumber(period1.getBudgetPeriodSequenceNumber());
        taskPeriod2.setAgencyRequestTotalAmount(new Integer(43000));
        taskPeriod2.setAgencyFringeBenefitTotalAmount(new Integer(8500));
        userAppointmentTaskPeriods.add(taskPeriod2);
        
        BudgetPeriod period2 = (BudgetPeriod) budget.getPeriods().get(1);
        
        UserAppointmentTaskPeriod taskPeriod3 = new UserAppointmentTaskPeriod();
        taskPeriod3.setBudgetPeriodSequenceNumber(period2.getBudgetPeriodSequenceNumber());
        taskPeriod3.setAgencyRequestTotalAmount(new Integer(74000));
        taskPeriod3.setAgencyFringeBenefitTotalAmount(new Integer(21500));
        userAppointmentTaskPeriods.add(taskPeriod3);
        
        budget.setAllUserAppointmentTaskPeriods(userAppointmentTaskPeriods);
        
        budgetModularService.resetModularBudget(budget);
        modularBudget = budget.getModularBudget();
        
        assertFalse(modularBudget.isInvalidMode());
        
        BudgetModularPeriod modularPeriod1 = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(0);
        assertEquals(modularPeriod1.getBudgetAdjustedModularDirectCostAmount(), new Integer(125000));
        
        BudgetModularPeriod modularPeriod2 = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(1);
        assertEquals(modularPeriod2.getBudgetAdjustedModularDirectCostAmount(), new Integer(125000));
        
        // Case 3: Budget with costs > # of periods * period maximum
        budget = setupBudget();
        
        BudgetPeriod periodInvalid = (BudgetPeriod) budget.getPeriods().get(0);
        
        UserAppointmentTaskPeriod taskPeriod4 = new UserAppointmentTaskPeriod();
        taskPeriod4.setBudgetPeriodSequenceNumber(periodInvalid.getBudgetPeriodSequenceNumber());
        taskPeriod4.setAgencyRequestTotalAmount(new Integer(1000000));
        userAppointmentTaskPeriods = new ArrayList();
        userAppointmentTaskPeriods.add(taskPeriod4);
        budget.setAllUserAppointmentTaskPeriods(userAppointmentTaskPeriods);
        
        budgetModularService.resetModularBudget(budget);
        modularBudget = budget.getModularBudget();
        
        BudgetModularPeriod modularPeriodInvalid1 = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(0);
        assertNull(modularPeriodInvalid1.getBudgetAdjustedModularDirectCostAmount());
        
        BudgetModularPeriod modularPeriodInvalid2 = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(1);
        assertNull(modularPeriodInvalid2.getBudgetAdjustedModularDirectCostAmount());
    }
    
    
    public void testAgencySupportsModular() {
        
        // Case 1: Agency is null.
        assertFalse(budgetModularService.agencySupportsModular(null));
        
        // Case 2: Supports modular is true.
        Agency agency = new Agency();
        AgencyExtension agencyExtension = new AgencyExtension();
        agencyExtension.setAgencyModularIndicator(true);
        agency.setAgencyExtension(agencyExtension);
        assertTrue(budgetModularService.agencySupportsModular(agency));
        
        // Case 3: Supports modular is false.
        agency.getAgencyExtension().setAgencyModularIndicator(false);
        assertFalse(budgetModularService.agencySupportsModular(agency));
        
        // Case 5: Agency extension and reports to agency both null.
        agency.setAgencyExtension(null);
        assertFalse(budgetModularService.agencySupportsModular(agency));
    }
}
