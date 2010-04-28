/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import org.kuali.kfs.sys.document.Correctable;
import org.kuali.rice.kns.util.KualiDecimal;

public class CashIncreaseDocument extends EndowmentSecurityDetailsDocumentBase implements Correctable {

    public CashIncreaseDocument() {
        super();
        setTransactionSourceTypeCode("M");
    }
    
    @Override
    public void prepareForSave() 
    {
        super.prepareForSave();
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.IncomePrincipalAmountTotaling#getTotalIncomeAmount()
     */
    public KualiDecimal getTotalIncomeAmount() {

        return this.getTargetIncomeTotal();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.IncomePrincipalAmountTotaling#getTotalPrincipalAmount()
     */
    public KualiDecimal getTotalPrincipalAmount() {

        return this.getTargetPrincipalTotal();
    }

    /**
     * @see org.kuali.kfs.sys.document.AmountTotaling#getTotalDollarAmount()
     */
    public KualiDecimal getTotalDollarAmount() {
        KualiDecimal totalAmount = new KualiDecimal();

        // totalAmount = TotalIncomeAmount + TotalPrincipalAmount
        totalAmount = totalAmount.add(getTotalIncomeAmount());
        totalAmount = totalAmount.add(getTotalPrincipalAmount());

        return totalAmount;
    }


}
