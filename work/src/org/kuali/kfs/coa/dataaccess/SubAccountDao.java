/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/dataaccess/SubAccountDao.java,v $
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
package org.kuali.module.chart.dao;

import org.kuali.module.chart.bo.SubAccount;


/**
 * This interface defines basic methods that SubAccount Dao's must provide
 * 
 * 
 */
public interface SubAccountDao {

    /**
     * @param chartOfAccountsCode - part of composite key
     * @param accountNumber - part of composite key
     * @param subAccountNumber - part of composite key
     * @return SubAccount
     * 
     * Retrieves a SubAccount object by primary key.
     */
    public SubAccount getByPrimaryId(String chartOfAccountsCode, String accountNumber, String subAccountNumber);
}
