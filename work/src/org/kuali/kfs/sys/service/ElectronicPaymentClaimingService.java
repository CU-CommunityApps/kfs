/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.document.AdvanceDepositDocument;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;

/**
 * A service which helps in the claiming of ElectronicPaymentClaim records
 */
public interface ElectronicPaymentClaimingService {
    
    /** 
     * Constructs a List of Notes that detail which ElectronicPaymentClaim records have been claimed by a document
     * @param claims the ElectronicPaymentClaim record that will be claimed by a document
     * @param claimingUser the user who's actually claiming ElectronicPaymentClaim records
     * @return a List of Notes that will summarize that claiming.
     */
    public abstract List<String> constructNoteTextsForClaims(List<ElectronicPaymentClaim> claims);
    
    /**
     * Returns a list of which document types the given user can claim Electronic Payment Claims with.
     * @param user the user attempting to use a document to claim ElectronicPaymentClaim records
     * @return a list of ElectronicPaymentClaimingDocumentGenerationStrategy document helper implementations
     */
    public abstract List<ElectronicPaymentClaimingDocumentGenerationStrategy> getClaimingDocumentChoices(Person user);
    
    /**
     * Given a List of ElectronicPaymentClaim records and a ElectronicPaymentClaimingDocumentGenerationStrategy document helper implementation, creates a document that
     * will claim; this method should also do the work of "claiming" each of the given ElectronicPaymentClaim records by filling in their referenceFinancialDocumentNumber field. 
     * @param claims the List of ElectronicPaymentClaim records to claim with a document
     * @param documentCreationHelper the document helper which will help this method in constructing the claiming document
     * @param user the Person record of the user who is claiming the given electronic payments
     * @return the URL to redirect to, so the user can edit the document
     */
    public abstract String createPaymentClaimingDocument(List<ElectronicPaymentClaim> claims, ElectronicPaymentClaimingDocumentGenerationStrategy documentCreationHelper, Person user);
    
    /**
     * Unclaims all ElectronicPaymentClaim records claimed by the given document, by setting the ElectronicPaymentClaim's reference document to null.
     * @param document the document that claimed ElectronicPaymentClaims and now needs to give them back
     */
    public abstract void declaimElectronicPaymentClaimsForDocument(Document document);
    
    /**
     * Sets the referenceFinancialDocumentNumber on each of the payments passed in with the given document number and then saves them. 
     * @param payments a list of payments to claim
     * @param docmentNumber the document number of the claiming document
     */
    public abstract void claimElectronicPayments(List<ElectronicPaymentClaim> payments, String documentNumber);
    
    /**
     * Returns a list of SAVED electronic payment claims from the lines of an AdvanceDepositDocument
     * @param doc the document that is generating electronic payment claim records
     * @return a list of the generated electronic payment claim records
     */
    public abstract List<ElectronicPaymentClaim> generateElectronicPaymentClaimRecords(AdvanceDepositDocument doc);
    
    /**
     * This method uses the ELECTRONIC_PAYMENT_CLAIM_ACCOUNTS_PARAMETER to find which accounts should 
     * cause an accounting line to create an ElectronicPaymentClaim record.
     * @return a List of Maps, where each Map represents an account that electronic funds are posted to. Each Map has a chart of accounts code as a key and a List of account numbers as a value.
     */
    public abstract Map<String, List<String>> getElectronicFundAccounts();  
    
    /**
     * This method...
     * @param user
     * @param namespaceCode
     * @param workflowDocumentTypeName
     * @return
     */
    public abstract boolean isAuthorizedForClaimingElectronicPayment(Person user, String namespaceCode, String workflowDocumentTypeName);

}

