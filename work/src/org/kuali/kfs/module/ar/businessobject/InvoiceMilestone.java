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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsMilestone;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.KualiDecimal;


/**
 * This class extends the Milestones BO to be used exclusively for Contracts Grants Invoice Document with document Number as its
 * key.
 */

public class InvoiceMilestone extends PersistableBusinessObjectBase implements ContractsAndGrantsMilestone {

    private String documentNumber;
    private Long proposalNumber;
    private Long milestoneNumber;
    private Long milestoneIdentifier;
    private String milestoneDescription;
    private String isItBilled;
    private KualiDecimal milestoneAmount;
    private Date milestoneActualCompletionDate;
    private Date milestoneExpectedCompletionDate;


    private ContractsAndGrantsCGBAward award;
    private ContractsGrantsInvoiceDocument invoiceDocument;

    /**
     * Constructs a Milestones.java.
     */
    public InvoiceMilestone() {
        this.setIsItBilled(KFSConstants.ParameterValues.STRING_NO);
    }

    /**
     * Gets the award attribute.
     * 
     * @return Returns the award.
     */
    public ContractsAndGrantsCGBAward getAward() {
        return award = (ContractsAndGrantsCGBAward) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).retrieveExternalizableBusinessObjectIfNecessary(this, award, "award");
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Gets the invoiceDocument attribute.
     * 
     * @return Returns the invoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getInvoiceDocument() {
        return invoiceDocument;
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
     * Gets the milestoneActualCompletionDate attribute.
     * 
     * @return Returns the milestoneActualCompletionDate.
     */
    public Date getMilestoneActualCompletionDate() {
        return milestoneActualCompletionDate;
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
     * Gets the milestoneDescription attribute.
     * 
     * @return Returns the milestoneDescription.
     */
    public String getMilestoneDescription() {
        return milestoneDescription;
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
     * Gets the milestoneIdentifier attribute.
     * 
     * @return Returns the milestoneIdentifier.
     */
    public Long getMilestoneIdentifier() {
        return milestoneIdentifier;
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
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the award attribute value.
     * 
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsCGBAward award) {
        this.award = award;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Sets the invoiceDocument attribute value.
     * 
     * @param invoiceDocument The invoiceDocument to set.
     */
    public void setInvoiceDocument(ContractsGrantsInvoiceDocument invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
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
     * Sets the milestoneActualCompletionDate attribute value.
     * 
     * @param milestoneActualCompletionDate The milestoneActualCompletionDate to set.
     */
    public void setMilestoneActualCompletionDate(Date milestoneActualCompletionDate) {
        this.milestoneActualCompletionDate = milestoneActualCompletionDate;
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
     * Sets the milestoneDescription attribute value.
     * 
     * @param milestoneDescription The milestoneDescription to set.
     */
    public void setMilestoneDescription(String milestoneDescription) {
        this.milestoneDescription = milestoneDescription;
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
     * Sets the milestoneIdentifier attribute value.
     * 
     * @param milestoneIdentifier The milestoneIdentifier to set.
     */
    public void setMilestoneIdentifier(Long milestoneIdentifier) {
        this.milestoneIdentifier = milestoneIdentifier;
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
     * Sets the proposalNumber attribute value.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("milestoneDescription", this.milestoneDescription);
        m.put("isItBilled", this.isItBilled);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        if (this.milestoneNumber != null) {
            m.put("milestoneNumber", this.milestoneNumber.toString());
        }
        if (this.milestoneIdentifier != null) {
            m.put("milestoneIdentifier", this.milestoneIdentifier.toString());
        }
        if (this.milestoneAmount != null) {
            m.put("milestoneAmount", this.milestoneAmount.toString());
        }
        if (this.milestoneActualCompletionDate != null) {
            m.put("milestoneActualCompletionDate", this.milestoneActualCompletionDate.toString());
        }
        if (this.milestoneExpectedCompletionDate != null) {
            m.put("milestoneExpectedCompletionDate", this.milestoneExpectedCompletionDate.toString());
        }
        return m;
    }
}
