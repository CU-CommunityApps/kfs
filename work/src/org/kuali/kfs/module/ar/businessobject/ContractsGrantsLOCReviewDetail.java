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

import org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Defines a detail in the Contracts and Grants Letter of Credit Review Document.
 */
public class ContractsGrantsLOCReviewDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Long proposalNumber;
    private Long locReviewDetailIdentifier;
    private Date awardBeginningDate;
    private Date awardEndingDate;
    private String agencyNumber;
    private String customerNumber;
    private String awardDocumentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private Date accountExpirationDate;
    private String accountDescription;
    private KualiDecimal awardBudgetAmount = KualiDecimal.ZERO;
    private KualiDecimal claimOnCashBalance = KualiDecimal.ZERO;
    private KualiDecimal amountToDraw = KualiDecimal.ZERO;
    private KualiDecimal hiddenAmountToDraw = KualiDecimal.ZERO;// This would be used for comparision with AmountToDraw field when
                                                                // user modifies it - not persisted
    private KualiDecimal fundsNotDrawn = KualiDecimal.ZERO; // Difference between amountToDraw and hiddenAmountToDraw.
    private KualiDecimal locAmount = KualiDecimal.ZERO;// This field would be visible only for the contract control account row.
    private KualiDecimal amountAvailableToDraw = KualiDecimal.ZERO;// This field would be visible only for the contract control
                                                                   // account row.
    private ContractsGrantsLOCReviewDocument contractsGrantsLOCReviewDocument;

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
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
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }


    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }


    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    /**
     * Gets the awardBudgetAmount attribute.
     * 
     * @return Returns the awardBudgetAmount.
     */
    public KualiDecimal getAwardBudgetAmount() {
        return awardBudgetAmount;
    }


    /**
     * Sets the awardBudgetAmount attribute value.
     * 
     * @param awardBudgetAmount The awardBudgetAmount to set.
     */
    public void setAwardBudgetAmount(KualiDecimal awardBudgetAmount) {
        this.awardBudgetAmount = awardBudgetAmount;
    }


    /**
     * Gets the amountToDraw attribute.
     * 
     * @return Returns the amountToDraw.
     */
    public KualiDecimal getAmountToDraw() {
        return amountToDraw;
    }


    /**
     * Sets the amountToDraw attribute value.
     * 
     * @param amountToDraw The amountToDraw to set.
     */
    public void setAmountToDraw(KualiDecimal amountToDraw) {
        this.amountToDraw = amountToDraw;
    }


    /**
     * Gets the hiddenAmountToDraw attribute.
     * 
     * @return Returns the hiddenAmountToDraw.
     */
    public KualiDecimal getHiddenAmountToDraw() {
        return hiddenAmountToDraw;
    }


    /**
     * Sets the hiddenAmountToDraw attribute value.
     * 
     * @param hiddenAmountToDraw The hiddenAmountToDraw to set.
     */
    public void setHiddenAmountToDraw(KualiDecimal hiddenAmountToDraw) {
        this.hiddenAmountToDraw = hiddenAmountToDraw;
    }


    /**
     * Gets the amountAvailableToDraw attribute.
     * 
     * @return Returns the amountAvailableToDraw.
     */
    public KualiDecimal getAmountAvailableToDraw() {
        return amountAvailableToDraw;
    }


    /**
     * Sets the amountAvailableToDraw attribute value.
     * 
     * @param amountAvailableToDraw The amountAvailableToDraw to set.
     */
    public void setAmountAvailableToDraw(KualiDecimal amountAvailableToDraw) {
        this.amountAvailableToDraw = amountAvailableToDraw;
    }


    /**
     * Gets the contractsGrantsLOCReviewDocument attribute.
     * 
     * @return Returns the contractsGrantsLOCReviewDocument.
     */
    public ContractsGrantsLOCReviewDocument getContractsGrantsLOCReviewDocument() {
        return contractsGrantsLOCReviewDocument;
    }


    /**
     * Sets the contractsGrantsLOCReviewDocument attribute value.
     * 
     * @param contractsGrantsLOCReviewDocument The contractsGrantsLOCReviewDocument to set.
     */
    public void setContractsGrantsLOCReviewDocument(ContractsGrantsLOCReviewDocument contractsGrantsLOCReviewDocument) {
        this.contractsGrantsLOCReviewDocument = contractsGrantsLOCReviewDocument;
    }


    /**
     * Gets the locAmount attribute.
     * 
     * @return Returns the locAmount.
     */
    public KualiDecimal getLocAmount() {
        return locAmount;
    }


    /**
     * Sets the locAmount attribute value.
     * 
     * @param locAmount The locAmount to set.
     */
    public void setLocAmount(KualiDecimal locAmount) {
        this.locAmount = locAmount;
    }


    /**
     * Gets the accountDescription attribute.
     * 
     * @return Returns the accountDescription.
     */
    public String getAccountDescription() {
        return accountDescription;
    }


    /**
     * Sets the accountDescription attribute value.
     * 
     * @param accountDescription The accountDescription to set.
     */
    public void setAccountDescription(String accountDescription) {
        this.accountDescription = accountDescription;
    }


    /**
     * Gets the awardDocumentNumber attribute.
     * 
     * @return Returns the awardDocumentNumber.
     */
    public String getAwardDocumentNumber() {
        return awardDocumentNumber;
    }


    /**
     * Sets the awardDocumentNumber attribute value.
     * 
     * @param awardDocumentNumber The awardDocumentNumber to set.
     */
    public void setAwardDocumentNumber(String awardDocumentNumber) {
        this.awardDocumentNumber = awardDocumentNumber;
    }


    /**
     * Gets the accountExpirationDate attribute.
     * 
     * @return Returns the accountExpirationDate.
     */
    public Date getAccountExpirationDate() {
        return accountExpirationDate;
    }


    /**
     * Sets the accountExpirationDate attribute value.
     * 
     * @param accountExpirationDate The accountExpirationDate to set.
     */
    public void setAccountExpirationDate(Date accountExpirationDate) {
        this.accountExpirationDate = accountExpirationDate;
    }


    /**
     * Gets the claimOnCashBalance attribute.
     * 
     * @return Returns the claimOnCashBalance.
     */
    public KualiDecimal getClaimOnCashBalance() {
        return claimOnCashBalance;
    }


    /**
     * Sets the claimOnCashBalance attribute value.
     * 
     * @param claimOnCashBalance The claimOnCashBalance to set.
     */
    public void setClaimOnCashBalance(KualiDecimal claimOnCashBalance) {
        this.claimOnCashBalance = claimOnCashBalance;
    }


    /**
     * Gets the fundsNotDrawn attribute.
     * 
     * @return Returns the fundsNotDrawn.
     */
    public KualiDecimal getFundsNotDrawn() {
        return fundsNotDrawn;
    }


    /**
     * Sets the fundsNotDrawn attribute value.
     * 
     * @param fundsNotDrawn The fundsNotDrawn to set.
     */
    public void setFundsNotDrawn(KualiDecimal fundsNotDrawn) {
        this.fundsNotDrawn = fundsNotDrawn;
    }


    /**
     * Gets the awardBeginningDate attribute.
     * 
     * @return Returns the awardBeginningDate.
     */
    public Date getAwardBeginningDate() {
        return awardBeginningDate;
    }


    /**
     * Sets the awardBeginningDate attribute value.
     * 
     * @param awardBeginningDate The awardBeginningDate to set.
     */
    public void setAwardBeginningDate(Date awardBeginningDate) {
        this.awardBeginningDate = awardBeginningDate;
    }


    /**
     * Gets the awardEndingDate attribute.
     * 
     * @return Returns the awardEndingDate.
     */
    public Date getAwardEndingDate() {
        return awardEndingDate;
    }


    /**
     * Sets the awardEndingDate attribute value.
     * 
     * @param awardEndingDate The awardEndingDate to set.
     */
    public void setAwardEndingDate(Date awardEndingDate) {
        this.awardEndingDate = awardEndingDate;
    }


    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }


    /**
     * Sets the agencyNumber attribute value.
     * 
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }


    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customerNumber.
     */
    public String getCustomerNumber() {
        return customerNumber;
    }


    /**
     * Sets the customerNumber attribute value.
     * 
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    /**
     * Gets the locReviewDetailIdentifier attribute.
     * 
     * @return Returns the locReviewDetailIdentifier.
     */
    public Long getLocReviewDetailIdentifier() {
        return locReviewDetailIdentifier;
    }


    /**
     * Sets the locReviewDetailIdentifier attribute value.
     * 
     * @param locReviewDetailIdentifier The locReviewDetailIdentifier to set.
     */
    public void setLocReviewDetailIdentifier(Long locReviewDetailIdentifier) {
        this.locReviewDetailIdentifier = locReviewDetailIdentifier;
    }


    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        m.put("agencyNumber", this.agencyNumber);
        m.put("customerNumber", this.customerNumber);
        m.put("awardDocumentNumber", this.awardDocumentNumber);
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("accountNumber", this.accountNumber);
        m.put("accountDescription", this.accountDescription);
        if (this.proposalNumber != null) {
            m.put("proposalNumber", this.proposalNumber.toString());
        }
        if (this.locReviewDetailIdentifier != null) {
            m.put("locReviewDetailIdentifier", this.locReviewDetailIdentifier.toString());
        }
        if (this.awardBeginningDate != null) {
            m.put("awardBeginningDate", this.awardBeginningDate.toString());
        }
        if (this.awardEndingDate != null) {
            m.put("awardEndingDate", this.awardEndingDate.toString());
        }
        if (this.accountExpirationDate != null) {
            m.put("accountExpirationDate", this.accountExpirationDate.toString());
        }
        if (this.awardBudgetAmount != null) {
            m.put("awardBudgetAmount", this.awardBudgetAmount.toString());
        }
        if (this.claimOnCashBalance != null) {
            m.put("claimOnCashBalance", this.claimOnCashBalance.toString());
        }
        if (this.amountToDraw != null) {
            m.put("amountToDraw", this.amountToDraw.toString());
        }
        if (this.fundsNotDrawn != null) {
            m.put("fundsNotDrawn", this.fundsNotDrawn.toString());
        }
        if (this.locAmount != null) {
            m.put("locAmount", this.locAmount.toString());
        }
        if (this.amountAvailableToDraw != null) {
            m.put("amountAvailableToDraw", this.amountAvailableToDraw.toString());
        }
        return m;
    }

}
