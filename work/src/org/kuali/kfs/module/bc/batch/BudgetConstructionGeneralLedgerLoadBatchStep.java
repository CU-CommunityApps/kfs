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
package org.kuali.kfs.module.bc.batch;

import java.util.Date;

import org.kuali.kfs.module.bc.batch.service.GLBudgetLoadService;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.context.SpringContext;


public class BudgetConstructionGeneralLedgerLoadBatchStep extends AbstractStep {

    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
      // @@TODO: the budget should load in the current fiscal year.
      // however, for testing purposes we will hard-wire it in, since we aren't running in a normal environment yet.
      GLBudgetLoadService generalLedgerBudgetLoadService = SpringContext.getBean(GLBudgetLoadService.class); 
      // normally, this would be called with no parameter, and would automatically load the fiscal year following the fiscal year of the run date. 
      generalLedgerBudgetLoadService.loadPendingBudgetConstructionGeneralLedger(2009);
        return true;
    }

}
