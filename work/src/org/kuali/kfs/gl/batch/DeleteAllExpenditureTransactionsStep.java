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
package org.kuali.module.gl.batch;

import org.kuali.kfs.batch.AbstractStep;
import org.kuali.kfs.batch.TestingStep;
import org.kuali.module.gl.dao.ExpenditureTransactionDao;
import org.kuali.module.gl.service.ExpenditureTransactionService;
import org.springframework.transaction.annotation.Transactional;

public class DeleteAllExpenditureTransactionsStep extends AbstractStep implements TestingStep {
    private ExpenditureTransactionService expenditureTransactionService;
    
    public boolean execute(String jobName) throws InterruptedException {
        expenditureTransactionService.deleteAllExpenditureTransactions();
        return true;
    }

    public void setExpenditureTransactionService(ExpenditureTransactionService expenditureTransactionService) {
        this.expenditureTransactionService = expenditureTransactionService;
    }
}
