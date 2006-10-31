/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.kra.budget.bo;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */

public class BudgetPeriod extends BusinessObjectBase implements Comparable {
    private static final long serialVersionUID = -479888264761887048L;

    private String researchDocumentNumber;
    private Integer budgetPeriodSequenceNumber;
    private Date budgetPeriodBeginDate;
    private Date budgetPeriodEndDate;
    private Integer budgetPeriodParticipantsNumber;

    /**
     * Default no-arg constructor.
     */
    public BudgetPeriod() {
        super();
    }
    
    public BudgetPeriod(String researchDocumentNumber, Integer budgetPeriodSequenceNumber) {
        this();
        this.researchDocumentNumber = researchDocumentNumber;
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
    }
    
    public BudgetPeriod(BudgetPeriod template) {
        this();
        this.researchDocumentNumber = template.getResearchDocumentNumber();
        this.budgetPeriodSequenceNumber = template.getBudgetPeriodSequenceNumber();
        this.budgetPeriodBeginDate = template.getBudgetPeriodBeginDate();
        this.budgetPeriodEndDate = template.getBudgetPeriodEndDate();
        this.budgetPeriodParticipantsNumber = template.getBudgetPeriodParticipantsNumber();
    }
    
    /**
     * @return Returns the budgetNumber.
     */
    public String getResearchDocumentNumber() {
        return researchDocumentNumber;
    }

    /**
     * @param budgetNumber The budgetNumber to set.
     */
    public void setResearchDocumentNumber(String researchDocumentNumber) {
        this.researchDocumentNumber = researchDocumentNumber;
    }

    /**
     * @return Returns the budgetPeriodBeginDate.
     */
    public Date getBudgetPeriodBeginDate() {
        return budgetPeriodBeginDate;
    }

    /**
     * @param budgetPeriodBeginDate The budgetPeriodBeginDate to set.
     */
    public void setBudgetPeriodBeginDate(Date budgetPeriodBeginDate) {
        this.budgetPeriodBeginDate = budgetPeriodBeginDate;
    }

    /**
     * @return Returns the budgetPeriodEndDate.
     */
    public Date getBudgetPeriodEndDate() {
        return budgetPeriodEndDate;
    }

    /**
     * @param budgetPeriodEndDate The budgetPeriodEndDate to set.
     */
    public void setBudgetPeriodEndDate(Date budgetPeriodEndDate) {
        this.budgetPeriodEndDate = budgetPeriodEndDate;
    }

    /**
     * @return Returns the budgetPeriodSequenceNumber.
     */
    public Integer getBudgetPeriodSequenceNumber() {
        if (budgetPeriodSequenceNumber == null) {
            budgetPeriodSequenceNumber = Integer.valueOf("0");
        }
        return budgetPeriodSequenceNumber;
    }

    /**
     * @param budgetPeriodSequenceNumber The budgetPeriodSequenceNumber to set.
     */
    public void setBudgetPeriodSequenceNumber(Integer budgetPeriodSequenceNumber) {
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
    }

    /**
     * @return Returns the budgetPeriodParticipantsNumber.
     */
    public Integer getBudgetPeriodParticipantsNumber() {
        return budgetPeriodParticipantsNumber;
    }

    /**
     * @param budgetPeriodParticipantsNumber The budgetPeriodParticipantsNumber to set.
     */
    public void setBudgetPeriodParticipantsNumber(Integer budgetPeriodParticipantsNumber) {
        this.budgetPeriodParticipantsNumber = budgetPeriodParticipantsNumber;
    }


    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("researchDocumentNumber", this.researchDocumentNumber);
        m.put("budgetPeriodSequenceNumber", this.budgetPeriodSequenceNumber);
        return m;
    }

    public String getBudgetPeriodLabel() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        String startDate = "(no start date)";
        String endDate = "(no end date)";

        if (this.getBudgetPeriodBeginDate() != null) {
            startDate = dateFormat.format(this.getBudgetPeriodBeginDate());
        }

        if (this.getBudgetPeriodEndDate() != null) {
            endDate = dateFormat.format(this.getBudgetPeriodEndDate());
        }

        return startDate + " - " + endDate;
    }

    /**
     * Implementing equals since I need contains to behave reasonably in a hashed datastructure.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                BudgetPeriod other = (BudgetPeriod) obj;

                if (this.getResearchDocumentNumber().equals(other.getResearchDocumentNumber()) && this.getBudgetPeriodSequenceNumber().equals(other.getBudgetPeriodSequenceNumber()) && this.getBudgetPeriodBeginDate().equals(other.getBudgetPeriodBeginDate()) && this.getBudgetPeriodEndDate().equals(other.getBudgetPeriodEndDate())) {
                    equal = true;
                }
            }
        }

        return equal;
    }

    /**
     * Calcluates hashCode based on current values of researchDocumentNumber and budgetPeriodSequenceNumber fields. Somewhat dangerous,
     * since those fields are mutable, but I don't expect people to be editing those values directly for Periods stored in hashed
     * datastructures. (this is based on Account.hashCode()) - TAD
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = getResearchDocumentNumber() + "|" + getBudgetPeriodSequenceNumber() + getBudgetPeriodBeginDate() + "|" + getBudgetPeriodEndDate();

        return hashString.hashCode();
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        return this.getBudgetPeriodSequenceNumber().compareTo(((BudgetPeriod) o).getBudgetPeriodSequenceNumber());
    }
}
