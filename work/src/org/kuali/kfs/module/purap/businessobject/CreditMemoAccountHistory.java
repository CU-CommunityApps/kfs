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

package org.kuali.kfs.module.purap.businessobject;

import java.sql.Timestamp;

import org.kuali.kfs.module.purap.util.PurApObjectUtils;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DateTimeService;


/**
 * Credit Memo Account History Business Object.
 */
public class CreditMemoAccountHistory extends CreditMemoAccount {

    protected Integer accountHistoryIdentifier;
    private Timestamp accountHistoryTimestamp;

    private CreditMemoAccount creditMemoAccount;
    
    /**
     * Default constructor.
     */
    public CreditMemoAccountHistory() {
    }

    /**
     * Constructor.
     * 
     * @param account - credit memo account
     */
    public CreditMemoAccountHistory(CreditMemoAccount cma) {
        // copy base attributes
        PurApObjectUtils.populateFromBaseClass(PurApAccountingLineBase.class, cma, this);
        PurApObjectUtils.populateFromBaseClass(AccountingLineBase.class, cma, this);
        this.setAccountHistoryTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
    }

    public Integer getAccountHistoryIdentifier() {
        return accountHistoryIdentifier;
    }

    public void setAccountHistoryIdentifier(Integer accountHistoryIdentifier) {
        this.accountHistoryIdentifier = accountHistoryIdentifier;
    }

    public Timestamp getAccountHistoryTimestamp() {
        return accountHistoryTimestamp;
    }

    public void setAccountHistoryTimestamp(Timestamp accountHistoryTimestamp) {
        this.accountHistoryTimestamp = accountHistoryTimestamp;
    }

    public CreditMemoAccount getCreditMemoAccount() {
        return creditMemoAccount;
    }

    @Deprecated
    public void setCreditMemoAccount(CreditMemoAccount creditMemoAccount) {
        this.creditMemoAccount = creditMemoAccount;
    }

}
