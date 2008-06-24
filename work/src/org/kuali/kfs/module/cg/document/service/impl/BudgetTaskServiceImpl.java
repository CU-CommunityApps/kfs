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

package org.kuali.kfs.module.cg.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.BudgetTask;
import org.kuali.kfs.module.cg.document.service.BudgetTaskService;
import org.springframework.transaction.annotation.Transactional;

public class BudgetTaskServiceImpl implements BudgetTaskService {

    private BusinessObjectService businessObjectService;

    /**
     * @return Returns the budgetTaskDao.
     */

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.service.BudgetPeriodService#getBudgetPeriod(java.lang.Long, java.lang.Integer)
     */
    public BudgetTask getBudgetTask(String documentNumber, Integer budgetTaskSequenceNumber) {
        return (BudgetTask) businessObjectService.retrieve(new BudgetTask(documentNumber, budgetTaskSequenceNumber));
    }

    public BudgetTask getFirstBudgetTask(String documentNumber) {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        List budgetTasks = new ArrayList(businessObjectService.findMatchingOrderBy(BudgetTask.class, fieldValues, KFSPropertyConstants.BUDGET_TASK_SEQUENCE_NUMBER, true));

        // there should always be a budgetTask by the time we get to here, so we want an exception thrown if there's not
        return (BudgetTask) budgetTasks.get(0);
    }

    public int getTaskIndex(Integer budgetTaskSequenceNumber, List budgetTaskList) {
        int taskIndexNumber = -1;
        Iterator budgetTaskListIter = budgetTaskList.iterator();

        for (int i = 0; budgetTaskListIter.hasNext(); i++) {
            BudgetTask budgetTask = (BudgetTask) budgetTaskListIter.next();

            if (budgetTask.getBudgetTaskSequenceNumber().equals(budgetTaskSequenceNumber)) {
                taskIndexNumber = i;
                break;
            }
        }

        return taskIndexNumber;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
