/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.kns.util.KualiDecimal;

public class TemSourceAccountingLine extends SourceAccountingLine implements TemAccountingLine {
    private String cardType;
    private Long id;
    
    /**
     * Gets the cardType attribute. 
     * @return Returns the cardType.
     */
    @Override
    public String getCardType() {
        return cardType;
    }

    /**
     * Sets the cardType attribute value.
     * @param cardType The cardType to set.
     */
    @Override
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * Gets the id attribute. 
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    public TemSourceAccountingLine() {
        super();
    }


    /**
     * @see org.kuali.kfs.sys.businessobject.AccountingLineBase#getValuesMap()
     */
    @Override
    public Map getValuesMap() {
        // TODO Auto-generated method stub
        Map temp = super.getValuesMap();
        return temp;
    }
    
    /**
     * Override needed for PURAP GL entry creation (hjs) - please do not add "amount" to this method
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AccountingLine)) {
            return false;
        }
        AccountingLine accountingLine = (AccountingLine) obj;
        return new EqualsBuilder().append(this.getChartOfAccountsCode(), accountingLine.getChartOfAccountsCode()).append(this.getAccountNumber(), accountingLine.getAccountNumber()).append(this.getSubAccountNumber(), accountingLine.getSubAccountNumber()).append(this.getFinancialObjectCode(), accountingLine.getFinancialObjectCode()).append(this.getFinancialSubObjectCode(), accountingLine.getFinancialSubObjectCode()).append(this.getProjectCode(), accountingLine.getProjectCode()).append(this.getOrganizationReferenceId(), accountingLine.getOrganizationReferenceId()).append(this.getAmount(), accountingLine.getAmount()).isEquals();
    }
}
