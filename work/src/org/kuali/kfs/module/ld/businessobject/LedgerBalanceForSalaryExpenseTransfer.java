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
package org.kuali.module.labor.bo;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties;

/**
 * Labor business object specifically for <code>{@link SalaryExpenseTransferDocument}</code> ledger balance import functionality.
 */
public class LedgerBalanceForSalaryExpenseTransfer extends LedgerBalance implements SegmentedBusinessObject {

    /**
     * Constructs a LedgerBalanceForSalaryExpenseTransfer
     */
    public LedgerBalanceForSalaryExpenseTransfer() {
        super();
    }

    /**
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#isLookupResultsSegmented()
     */
    public boolean isLookupResultsSegmented() {
        return true;
    }

    /**
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#getSegmentedPropertyNames()
     */
    public Collection<String> getSegmentedPropertyNames() {
        return (Collection<String>) Arrays.asList(AccountingPeriodProperties.namesToArray());
    }

    /**
     * Adds the period amount to the return string. Since the return string cannot have string, multiplies the amount by 100 so the
     * decimal places are not lost.
     * 
     * @see org.kuali.module.labor.bo.SegmentedBusinessObject#getAdditionalReturnData(java.lang.String)
     */
    public String getAdditionalReturnData(String segmentedPropertyName) {
        String periodCode = LaborConstants.periodCodeMapping.get(segmentedPropertyName);
        KualiDecimal periodAmount = getAmount(periodCode);
        periodAmount = periodAmount.multiply(new KualiDecimal(100));
        NumberFormat formatter = NumberFormat.getIntegerInstance();
        String formattedAmount = formatter.format(periodAmount);
        return StringUtils.replace(formattedAmount, ",", "");
    }
}
