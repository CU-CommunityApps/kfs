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

package org.kuali.module.cg.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * 
 */
public class ProposalSubcontractor extends PersistableBusinessObjectBase {

    private String proposalSubcontractorNumber;
    private Long proposalNumber;
    private String subcontractorNumber;
    private KualiDecimal proposalSubcontractorAmount;

    private Subcontractor subcontractor;

    /**
     * Default constructor.
     */
    public ProposalSubcontractor() {

    }

    /**
     * Gets the proposalSubcontractorNumber attribute.
     * 
     * @return Returns the proposalSubcontractorNumber
     * 
     */
    public String getProposalSubcontractorNumber() {
        return proposalSubcontractorNumber;
    }

    /**
     * Sets the proposalSubcontractorNumber attribute.
     * 
     * @param proposalSubcontractorNumber The proposalSubcontractorNumber to set.
     * 
     */
    public void setProposalSubcontractorNumber(String proposalSubcontractorNumber) {
        this.proposalSubcontractorNumber = proposalSubcontractorNumber;
    }


    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     * 
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     * 
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the subcontractorNumber attribute.
     * 
     * @return Returns the subcontractorNumber
     * 
     */
    public String getSubcontractorNumber() {
        return subcontractorNumber;
    }

    /**
     * Sets the subcontractorNumber attribute.
     * 
     * @param subcontractorNumber The subcontractorNumber to set.
     * 
     */
    public void setSubcontractorNumber(String subcontractorNumber) {
        this.subcontractorNumber = subcontractorNumber;
    }


    /**
     * Gets the proposalSubcontractorAmount attribute.
     * 
     * @return Returns the proposalSubcontractorAmount
     * 
     */
    public KualiDecimal getProposalSubcontractorAmount() {
        return proposalSubcontractorAmount;
    }

    /**
     * Sets the proposalSubcontractorAmount attribute.
     * 
     * @param proposalSubcontractorAmount The proposalSubcontractorAmount to set.
     * 
     */
    public void setProposalSubcontractorAmount(KualiDecimal proposalSubcontractorAmount) {
        this.proposalSubcontractorAmount = proposalSubcontractorAmount;
    }

    /**
     * @return Returns the subcontractor.
     */
    public Subcontractor getSubcontractor() {
        return subcontractor;
    }

    /**
     * @param subcontractor The subcontractor to set.
     * @deprecated
     */
    public void setSubcontractor(Subcontractor subcontractor) {
        this.subcontractor = subcontractor;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("proposalSubcontractorNumber", this.proposalSubcontractorNumber);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        m.put("subcontractorNumber", this.subcontractorNumber);
        return m;
    }


}
