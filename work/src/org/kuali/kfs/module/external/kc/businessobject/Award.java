/*
 * Copyright 2006-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.module.external.kc.businessobject;

import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Defines a financial award object.
 */
public class Award implements ContractsAndGrantsAward {
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";

    private Long proposalNumber;
    private String agencyNumber;
    
    private Proposal proposal;
    private Agency agency;
    private List<AwardAccount> awardAccounts;
    
    /**
     * Default no-args constructor.
     */
    public Award() {
        awardAccounts = new TypedArrayList(AwardAccount.class);
    }

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }
    
    /**
     * @return a String to represent this field on the inquiry
     */
    public String getAwardInquiryTitle() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(AWARD_INQUIRY_TITLE_PROPERTY);
    }


    public Proposal getProposal() {
        return proposal;
    }


    public void prepareForWorkflow() {}


    public void refresh() {}


    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }


    public Agency getAgency() {
        return agency;
    }


    public void setAgency(Agency agency) {
        this.agency = agency;
    }


    /**
     * Gets the awardAccounts list.
     * 
     * @return Returns the awardAccounts.
     */
    public List<AwardAccount> getAwardAccounts() {
        return awardAccounts;
    }

    /**
     * Sets the awardAccounts list.
     * 
     * @param awardAccounts The awardAccounts to set.
     */
    public void setAwardAccounts(List<AwardAccount> awardAccounts) {
        this.awardAccounts = awardAccounts;
    }

    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

}

