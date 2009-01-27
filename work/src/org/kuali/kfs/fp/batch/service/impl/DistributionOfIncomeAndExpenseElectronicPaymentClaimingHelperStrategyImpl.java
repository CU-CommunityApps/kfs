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
package org.kuali.kfs.fp.batch.service.impl;

import java.util.List;

import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.kfs.sys.service.GeneralLedgerInputTypeService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KNSServiceLocator;

public class DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl implements ElectronicPaymentClaimingDocumentGenerationStrategy {
    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.class);
    private DataDictionaryService ddService;
    private DocumentService documentService;
    private ElectronicPaymentClaimingService electronicPaymentClaimingService;
    private ParameterService parameterService;
    
    /**
     * The name of the parameter to get the description for this document; without a description, we can't save the document, and if we don't save the document,
     * there's a chance that electronic payment claims will go to limbo
     */
    private final static String DOCUMENT_DESCRIPTION_PARAM_NAME = "ELECTRONIC_FUNDS_DOCUMENT_DESCRIPTION";
    private final static String DI_WORKFLOW_DOCUMENT_TYPE = "DistributionOfIncomeAndExpenseDocument";
    private final static String URL_PREFIX = "financial";
    private final static String URL_MIDDLE = ".do?methodToCall=docHandler&command=";
    private final static String URL_SUFFIX = "&docId=";
    
    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#createDocumentFromElectronicPayments(java.util.List)
     */
    public String createDocumentFromElectronicPayments(List<ElectronicPaymentClaim> electronicPayments, Person user) {
        DistributionOfIncomeAndExpenseDocument document = null;
        try {
            document = (DistributionOfIncomeAndExpenseDocument)documentService.getNewDocument(getClaimingDocumentWorkflowDocumentType());
            addAccountingLinesToDocument(document, electronicPayments);
            addDescriptionToDocument(document);
            addNotesToDocument(document, electronicPayments, user);
            documentService.saveDocument(document);
            electronicPaymentClaimingService.claimElectronicPayments(electronicPayments, document.getDocumentNumber());
        } catch (WorkflowException we) {
            throw new RuntimeException("WorkflowException while creating a DistributionOfIncomeAndExpenseDocument to claim ElectronicPaymentClaim records.", we);
        }
        
        return getURLForDocument(document);
    }
    
    /**
     * Builds the URL that can be used to redirect to the correct document
     * @param doc the document to build the URL for
     * @return the relative URL to redirect to
     */
    protected String getURLForDocument(DistributionOfIncomeAndExpenseDocument doc) {
        StringBuilder url = new StringBuilder();
        url.append(URL_PREFIX);
        url.append(getClaimingDocumentWorkflowDocumentType().replace("Document", ""));
        url.append(URL_MIDDLE);
        url.append(KEWConstants.ACTIONLIST_COMMAND);
        url.append(URL_SUFFIX);
        url.append(doc.getDocumentNumber());
        return url.toString();
    }
    
    /**
     * Creates notes for the claims (using the ElectronicPaymentClaimingService) and then adds them to the document
     * @param claimingDoc the claiming document
     * @param claims the electronic payments being claimed
     * @param user the user doing the claiming
     */
    protected void addNotesToDocument(DistributionOfIncomeAndExpenseDocument claimingDoc, List<ElectronicPaymentClaim> claims, Person user) {
        for (String noteText: electronicPaymentClaimingService.constructNoteTextsForClaims(claims)) {
            try {
                Note note = documentService.createNoteFromDocument(claimingDoc, noteText);
                documentService.addNoteToDocument(claimingDoc, note);
            } catch (Exception e) {
                LOG.error("Exception while attempting to create or add note: "+e);
            }
        }
    }

    /**
     * Adds an accounting line to the document for each ElectronicPaymentClaim record that is being added
     * @param document the claiming Distribution of Income and Expense document
     * @param electronicPayments the list of ElectronicPaymentClaim records that are being claimed
     */
    protected void addAccountingLinesToDocument(DistributionOfIncomeAndExpenseDocument document, List<ElectronicPaymentClaim> electronicPayments) {
        for (ElectronicPaymentClaim payment: electronicPayments) {
            SourceAccountingLine claimingAccountingLine = copyAccountingLineToNew(payment.getGeneratingAccountingLine(), createNewAccountingLineForDocument(document));
            document.addSourceAccountingLine(claimingAccountingLine);
        }
    }
    
    /**
     * Adds the parameterized description to the document, so the doc can be saved
     * @param document the document to add a description to
     */
    protected void addDescriptionToDocument(DistributionOfIncomeAndExpenseDocument document) {
        String description = parameterService.getParameterValue(DistributionOfIncomeAndExpenseDocument.class, DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.DOCUMENT_DESCRIPTION_PARAM_NAME);
        if (description != null) {
            document.getDocumentHeader().setDocumentDescription(description);
        } else {
            throw new RuntimeException("There is evidently no value for Parameter KFS-FP / Distribution of Income and Expense / "+DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.DOCUMENT_DESCRIPTION_PARAM_NAME+"; please set a value before claiming Electronic Payments");
        }
    }
    
    /**
     * Creates a new accounting line, based on what the source accounting line class for the document is
     * @param document the document that is claiming these payments
     * @return a new, ready-to-be-filled in accounting line of the class that the given document uses for Source Accounting Lines
     */
    protected SourceAccountingLine createNewAccountingLineForDocument(DistributionOfIncomeAndExpenseDocument document) {
        SourceAccountingLine newLine = null;
        try {
            Class accountingLineClass = document.getSourceAccountingLineClass();
            newLine = (SourceAccountingLine)accountingLineClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new RuntimeException(ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
        }
        return newLine;
    }
    
    /**
     * Copies an original accounting line to a new accounting line
     * @param line the original accounting line
     * @return an accounting line that copies that accounting line
     */
    protected SourceAccountingLine copyAccountingLineToNew(SourceAccountingLine line, SourceAccountingLine newLine) {
        newLine.setChartOfAccountsCode(line.getChartOfAccountsCode());
        newLine.setAccountNumber(line.getAccountNumber());
        newLine.setSubAccountNumber(line.getSubAccountNumber());
        newLine.setFinancialObjectCode(line.getFinancialObjectCode());
        newLine.setFinancialSubObjectCode(line.getFinancialSubObjectCode());
        newLine.setProjectCode(line.getProjectCode());
        newLine.setOrganizationReferenceId(line.getOrganizationReferenceId());
        newLine.setAmount(line.getAmount());
        return newLine;
    }
    
    /**
     * Returns the name DistributionOfIncomeAndExpenseDocument workflow document type
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getClaimingDocumentClass()
     */
    public String getClaimingDocumentWorkflowDocumentType() {
        return DistributionOfIncomeAndExpenseElectronicPaymentClaimingHelperStrategyImpl.DI_WORKFLOW_DOCUMENT_TYPE;
    }

    /**
     * Uses the data dictionary to find the label for this document
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentLabel()
     */
    public String getDocumentLabel() {
        try {
            return KNSServiceLocator.getWorkflowInfoService().getDocType(getClaimingDocumentWorkflowDocumentType()).getDocTypeLabel();
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Caught Exception trying to get Workflow Document Type", e);
        }
    }

    /**
     * This always returns true if the given user in the claiming workgroup.
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#userMayUseToClaim(org.kuali.rice.kim.bo.Person)
     */
    public boolean userMayUseToClaim(Person claimingUser) {
        String namespaceCode = KFSConstants.ParameterNamespaces.FINANCIAL;
        String documentTypeName = this.getClaimingDocumentWorkflowDocumentType();
        
        return electronicPaymentClaimingService.isAuthorizedForClaimingElectronicPayment(claimingUser, namespaceCode, documentTypeName);
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#getDocumentCode()
     */
    public String getDocumentCode() {
        DocumentEntry docEntry = ddService.getDataDictionary().getDocumentEntry(getDataDictionaryService().getValidDocumentClassByTypeName(getClaimingDocumentWorkflowDocumentType()).getCanonicalName());
        return SpringContext.getBean(GeneralLedgerInputTypeService.class).getGeneralLedgerInputTypeByDocumentName(docEntry.getDocumentTypeName()).getInputTypeCode();
    }

    /**
     * @see org.kuali.kfs.sys.service.ElectronicPaymentClaimingDocumentGenerationStrategy#isDocumentReferenceValid(java.lang.String)
     */
    public boolean isDocumentReferenceValid(String referenceDocumentNumber) {
        boolean valid = false;
        try {
            long docNumberAsLong = Long.parseLong(referenceDocumentNumber);
            if (docNumberAsLong > 0L) {
                valid = documentService.documentExists(referenceDocumentNumber);
            }
        } catch (NumberFormatException nfe) {
            valid = false; // the doc # can't be parsed into a Long?  Then it ain't no valid!
        }
        return valid;
    }

    /**
     * Sets the ddService attribute value.
     * @param ddService The ddService to set.
     */
    public void setDataDictionaryService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }

    /**
     * Gets the ddService attribute. 
     * @return Returns the ddService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return ddService;
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the documentService attribute. 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the electronicPaymentClaimingService attribute value.
     * @param electronicPaymentClaimingService The electronicPaymentClaimingService to set.
     */
    public void setElectronicPaymentClaimingService(ElectronicPaymentClaimingService electronicPaymentClaimingService) {
        this.electronicPaymentClaimingService = electronicPaymentClaimingService;
    }

    /**
     * Gets the electronicPaymentClaimingService attribute. 
     * @return Returns the electronicPaymentClaimingService.
     */
    public ElectronicPaymentClaimingService getElectronicPaymentClaimingService() {
        return electronicPaymentClaimingService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the parameterService attribute. 
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }
}

