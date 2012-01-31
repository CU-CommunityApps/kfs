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
package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsMilestone;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Milestone to be used for Milestone Schedule under Contracts and Grants
 */
public class Milestone extends PersistableBusinessObjectBase implements ContractsAndGrantsMilestone {

    private Long proposalNumber;
    private Long milestoneNumber;
    private Long milestoneIdentifier;
    private String milestoneDescription;

    private KualiDecimal milestoneAmount;

    private Date milestoneActualCompletionDate;
    private Date milestoneExpectedCompletionDate;
    private String isItBilled;


    private Award award;


    /**
     * Constructs a Milestones.java.
     */
    public Milestone() {
        this.setIsItBilled(KFSConstants.ParameterValues.STRING_NO);
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the milestoneNumber attribute.
     * 
     * @return Returns the milestoneNumber.
     */
    public Long getMilestoneNumber() {
        return milestoneNumber;
    }

    /**
     * Sets the milestoneNumber attribute value.
     * 
     * @param milestoneNumber The milestoneNumber to set.
     */
    public void setMilestoneNumber(Long milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
    }

    /**
     * Gets the milestoneDescription attribute.
     * 
     * @return Returns the milestoneDescription.
     */
    public String getMilestoneDescription() {
        return milestoneDescription;
    }

    /**
     * Sets the milestoneDescription attribute value.
     * 
     * @param milestoneDescription The milestoneDescription to set.
     */
    public void setMilestoneDescription(String milestoneDescription) {
        this.milestoneDescription = milestoneDescription;
    }

    /**
     * Gets the milestoneAmount attribute.
     * 
     * @return Returns the milestoneAmount.
     */
    public KualiDecimal getMilestoneAmount() {
        return milestoneAmount;
    }

    /**
     * Sets the milestoneAmount attribute value.
     * 
     * @param milestoneAmount The milestoneAmount to set.
     */
    public void setMilestoneAmount(KualiDecimal milestoneAmount) {
        this.milestoneAmount = milestoneAmount;
    }

    /**
     * Gets the isItBilled attribute.
     * 
     * @return Returns the isItBilled.
     */
    public String getIsItBilled() {
        return isItBilled;
    }

    /**
     * Sets the isItBilled attribute value.
     * 
     * @param isItBilled The isItBilled to set.
     */
    public void setIsItBilled(String isItBilled) {
        this.isItBilled = isItBilled;
    }

    /**
     * Gets the milestoneActualCompletionDate attribute.
     * 
     * @return Returns the milestoneActualCompletionDate.
     */
    public Date getMilestoneActualCompletionDate() {
        return milestoneActualCompletionDate;
    }

    /**
     * Sets the milestoneActualCompletionDate attribute value.
     * 
     * @param milestoneActualCompletionDate The milestoneActualCompletionDate to set.
     */
    public void setMilestoneActualCompletionDate(Date milestoneActualCompletionDate) {
        this.milestoneActualCompletionDate = milestoneActualCompletionDate;
    }

    /**
     * Gets the milestoneExpectedCompletionDate attribute.
     * 
     * @return Returns the milestoneExpectedCompletionDate.
     */
    public Date getMilestoneExpectedCompletionDate() {
        return milestoneExpectedCompletionDate;
    }

    /**
     * Sets the milestoneExpectedCompletionDate attribute value.
     * 
     * @param milestoneExpectedCompletionDate The milestoneExpectedCompletionDate to set.
     */
    public void setMilestoneExpectedCompletionDate(Date milestoneExpectedCompletionDate) {
        this.milestoneExpectedCompletionDate = milestoneExpectedCompletionDate;
    }

    /**
     * Gets the milestoneIdentifier attribute.
     * 
     * @return Returns the milestoneIdentifier.
     */
    public Long getMilestoneIdentifier() {
        return milestoneIdentifier;
    }

    /**
     * Sets the milestoneIdentifier attribute value.
     * 
     * @param milestoneIdentifier The milestoneIdentifier to set.
     */
    public void setMilestoneIdentifier(Long milestoneIdentifier) {
        this.milestoneIdentifier = milestoneIdentifier;
    }

    /**
     * Gets the award attribute.
     * 
     * @return Returns the award.
     */
    public Award getAward() {
        return award;
    }

    /**
     * Sets the award attribute value.
     * 
     * @param award The award to set.
     */
    public void setAward(Award award) {
        this.award = award;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalNumber", this.proposalNumber);
        m.put("milestoneNumber", milestoneNumber);
        m.put("milestoneIdentifier", milestoneIdentifier);
        m.put("milestoneDescription", milestoneDescription);
        m.put("milestoneAmount", milestoneAmount);
        m.put("isItBilled", isItBilled);
        return m;
    }

}
