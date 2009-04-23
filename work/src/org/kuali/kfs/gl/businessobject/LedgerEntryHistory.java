/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.gl.businessobject;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.util.KualiDecimal;


/**
 * Interface for EntryHistory used by GL and Labor
 */
public interface LedgerEntryHistory extends BusinessObject {
    public Integer getUniversityFiscalYear();
    public String getChartOfAccountsCode();
    public String getFinancialObjectCode();
    public String getFinancialBalanceTypeCode();
    public String getUniversityFiscalPeriodCode();
    public String getTransactionDebitCreditCode();
    public KualiDecimal getTransactionLedgerEntryAmount();
    public Integer getRowCount();
}
