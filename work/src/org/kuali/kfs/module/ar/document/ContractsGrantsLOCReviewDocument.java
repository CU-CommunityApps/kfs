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
package org.kuali.kfs.module.ar.document;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCReviewDetail;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kns.bo.ModuleConfiguration;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Contracts Grants LOC Review Document.
 */
public class ContractsGrantsLOCReviewDocument extends FinancialSystemTransactionalDocumentBase {

    protected String statusCode;
    private String letterOfCreditFundCode;
    private ContractsAndGrantsLetterOfCreditFund letterOfCreditFund;
    private String letterOfCreditFundGroupCode;
    private ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup;
    private List<ContractsGrantsLOCReviewDetail> headerReviewDetails;
    private List<ContractsGrantsLOCReviewDetail> accountReviewDetails;

    public ContractsGrantsLOCReviewDocument() {
        headerReviewDetails = new TypedArrayList(ContractsGrantsLOCReviewDetail.class);
        accountReviewDetails = new TypedArrayList(ContractsGrantsLOCReviewDetail.class);
    }

    /**
     * Gets the statusCode attribute.
     * 
     * @return Returns the statusCode.
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode attribute value.
     * 
     * @param statusCode The statusCode to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     * 
     * @return Returns the letterOfCreditFundGroupCode.
     */
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }


    /**
     * Sets the letterOfCreditFundGroupCode attribute value.
     * 
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }


    /**
     * Gets the letterOfCreditFundCode attribute.
     * 
     * @return Returns the letterOfCreditFundCode.
     */
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    /**
     * Sets the letterOfCreditFundCode attribute value.
     * 
     * @param letterOfCreditFundCode The letterOfCreditFundCode to set.
     */
    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }


    /**
     * Gets the letterOfCreditFund attribute.
     * 
     * @return Returns the letterOfCreditFund.
     */
    public ContractsAndGrantsLetterOfCreditFund getLetterOfCreditFund() {
        return letterOfCreditFund = (ContractsAndGrantsLetterOfCreditFund) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFund.class).retrieveExternalizableBusinessObjectIfNecessary(this, letterOfCreditFund, "letterOfCreditFund");
    }

    /**
     * Sets the letterOfCreditFund attribute value.
     * 
     * @param letterOfCreditFund The letterOfCreditFund to set.
     */
    public void setLetterOfCreditFund(ContractsAndGrantsLetterOfCreditFund letterOfCreditFund) {
        this.letterOfCreditFund = letterOfCreditFund;
    }

    /**
     * Gets the letterOfCreditFundGroup attribute.
     * 
     * @return Returns the letterOfCreditFundGroup.
     */
    public ContractsAndGrantsLetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        return letterOfCreditFundGroup = (ContractsAndGrantsLetterOfCreditFundGroup) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsLetterOfCreditFundGroup.class).retrieveExternalizableBusinessObjectIfNecessary(this, letterOfCreditFundGroup, "letterOfCreditFundGroup");
    }

    /**
     * Sets the letterOfCreditFundGroup attribute value.
     * 
     * @param letterOfCreditFundGroup The letterOfCreditFundGroup to set.
     */
    public void setLetterOfCreditFundGroup(ContractsAndGrantsLetterOfCreditFundGroup letterOfCreditFundGroup) {
        this.letterOfCreditFundGroup = letterOfCreditFundGroup;
    }

    /**
     * Gets the CcaReviewDetails attribute.
     * 
     * @return Returns the CcaReviewDetails.
     */
    public List<ContractsGrantsLOCReviewDetail> getHeaderReviewDetails() {
        // To get the list of invoice Details for total cost
        List<ContractsGrantsLOCReviewDetail> hdrDetails = new TypedArrayList(ContractsGrantsLOCReviewDetail.class);
        for (ContractsGrantsLOCReviewDetail hdrD : headerReviewDetails) {
            if (ObjectUtils.isNull(hdrD.getAccountDescription())) {
                hdrDetails.add(hdrD);
            }
        }

        return hdrDetails;
    }

    /**
     * Sets the CcaReviewDetails attribute value.
     * 
     * @param CcaReviewDetails The CcaReviewDetails to set.
     */
    public void setHeaderReviewDetails(List<ContractsGrantsLOCReviewDetail> headerReviewDetails) {
        this.headerReviewDetails = headerReviewDetails;
    }

    /**
     * Gets the AccountReviewDetails attribute.
     * 
     * @return Returns the AccountReviewDetails.
     */
    public List<ContractsGrantsLOCReviewDetail> getAccountReviewDetails() {
        // To get the list of invoice Details for total cost
        List<ContractsGrantsLOCReviewDetail> acctDetails = new TypedArrayList(ContractsGrantsLOCReviewDetail.class);
        for (ContractsGrantsLOCReviewDetail acctD : accountReviewDetails) {
            if (ObjectUtils.isNotNull(acctD.getAccountDescription())) {
                if (acctD.getAccountDescription().equalsIgnoreCase(ArConstants.ACCOUNT) || acctD.getAccountDescription().equalsIgnoreCase(ArConstants.CONTRACT_CONTROL_ACCOUNT)) {
                    acctDetails.add(acctD);
                }
            }
        }

        return acctDetails;
    }

    /**
     * Sets the AccountReviewDetails attribute value.
     * 
     * @param AccountReviewDetails The AccountReviewDetails to set.
     */
    public void setAccountReviewDetails(List<ContractsGrantsLOCReviewDetail> accountReviewDetails) {
        this.accountReviewDetails = accountReviewDetails;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("statusCode", statusCode);
        m.put("letterOfCreditFundCode", letterOfCreditFundCode);
        m.put("letterOfCreditFund", letterOfCreditFund);
        m.put("letterOfCreditFundGroupCode", letterOfCreditFundGroupCode);
        m.put("letterOfCreditFundGroup", letterOfCreditFundGroup);
        m.put("headerReviewDetails", headerReviewDetails);
        m.put("accountReviewDetails", accountReviewDetails);
        return m;
    }

    /**
     * Initializes the values for a new document.
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        setStatusCode(ArConstants.CustomerCreditMemoStatuses.INITIATE);
    }

    /**
     * Clear out the initially populated fields.
     */
    public void clearInitFields() {
        LOG.debug("clearDocument() started");

        // Clearing document Init fields
        setLetterOfCreditFundGroupCode(null);
        setLetterOfCreditFundCode(null);
    }

    /**
     * populate customer credit memo details based on the invoice info
     */
    public void populateContractsGrantsLOCReviewDetails() {

        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        ContractsGrantsLOCReviewDetail locReviewDtl;
        Map<String, Object> criteria = new HashMap<String, Object>();
        if (ObjectUtils.isNotNull(this.getLetterOfCreditFundGroupCode())) {
            criteria.put("letterOfCreditFund.letterOfCreditFundGroupCode", this.getLetterOfCreditFundGroupCode());
        }
        if (ObjectUtils.isNotNull(this.getLetterOfCreditFundCode())) {
            criteria.put("letterOfCreditFundCode", this.getLetterOfCreditFundCode());
        }
        // To exclude awards with milestones and predetermined schedule.
        criteria.put(ArPropertyConstants.PREFERRED_BILLING_FREQUENCY, ArPropertyConstants.LOC_BILLING_SCHEDULE_CODE);

        List<ContractsAndGrantsCGBAward> awards = contractsGrantsInvoiceDocumentService.getActiveAwardsByCriteria(criteria);

        if (CollectionUtils.isEmpty(awards)) {
            GlobalVariables.getMessageMap().putErrorForSectionId("Contracts Grants LOC Review Initiation", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_NO_AWARDS_RETRIEVED);
        }
        else {

            List<ContractsAndGrantsCGBAward> validAwards = new ArrayList<ContractsAndGrantsCGBAward>();

            // To retrieve the batch file directory name as "reports/cg"
            ModuleConfiguration systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-AR").getModuleConfiguration();

            String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);

            String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());
            String errOutputFile1 = destinationFolderPath + File.separator + ArConstants.BatchFileSystem.LOC_REVIEW_VALIDATION_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;

            validAwards = (List<ContractsAndGrantsCGBAward>) contractsGrantsInvoiceCreateDocumentService.validateAwards(awards, errOutputFile1);


            if (CollectionUtils.isEmpty(validAwards)) {
                GlobalVariables.getMessageMap().putErrorForSectionId("Contracts Grants LOC Review Initiation", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_AWARDS_INVALID);
            }
            else {
                setStatusCode(ArConstants.CustomerCreditMemoStatuses.IN_PROCESS);
                for (ContractsAndGrantsCGBAward award : validAwards) {

                    // To set the amount to draw for the award accounts as a whole.
                    contractsGrantsInvoiceDocumentService.setAwardAccountToDraw(award.getActiveAwardAccounts());

                    KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
                    KualiDecimal amountAvailableToDraw = KualiDecimal.ZERO;
                    KualiDecimal totalClaimOnCashBalance = KualiDecimal.ZERO;
                    KualiDecimal totalAwardBudgetAmount = KualiDecimal.ZERO;
                    // Creating the header row here.
                    locReviewDtl = new ContractsGrantsLOCReviewDetail();
                    locReviewDtl.setDocumentNumber(this.documentNumber);
                    locReviewDtl.setProposalNumber(award.getProposalNumber());
                    locReviewDtl.setAwardDocumentNumber(award.getAwardDocumentNumber());
                    locReviewDtl.setAgencyNumber(award.getAgencyNumber());
                    locReviewDtl.setCustomerNumber(award.getAgency().getCustomerNumber());
                    locReviewDtl.setAwardBeginningDate(award.getAwardBeginningDate());
                    locReviewDtl.setAwardEndingDate(award.getAwardEndingDate());
                    amountAvailableToDraw = contractsGrantsInvoiceDocumentService.getAmountAvailableToDraw(award.getAwardTotalAmount(), award.getActiveAwardAccounts());
                    locReviewDtl.setAmountAvailableToDraw(amountAvailableToDraw);
                    if (ObjectUtils.isNotNull(award.getLetterOfCreditFund())) {
                        locReviewDtl.setLocAmount(award.getLetterOfCreditFund().getLetterOfCreditFundAmount());
                    }


                    headerReviewDetails.add(locReviewDtl);

                    // Creating sub rows for the individual accounts.
                    for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                        locReviewDtl = new ContractsGrantsLOCReviewDetail();
                        locReviewDtl.setDocumentNumber(this.documentNumber);
                        locReviewDtl.setProposalNumber(award.getProposalNumber());
                        locReviewDtl.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
                        locReviewDtl.setAccountNumber(awardAccount.getAccountNumber());
                        locReviewDtl.setAccountExpirationDate(awardAccount.getAccount().getAccountExpirationDate());
                        locReviewDtl.setClaimOnCashBalance(contractsGrantsInvoiceDocumentService.getClaimOnCashforAwardAccount(awardAccount));
                        totalClaimOnCashBalance = totalClaimOnCashBalance.add(locReviewDtl.getClaimOnCashBalance());
                        locReviewDtl.setAwardBudgetAmount(contractsGrantsInvoiceDocumentService.getBudgetAndActualsForAwardAccount(awardAccount, ArPropertyConstants.BUDGET_BALANCE_TYPE));
                        totalAwardBudgetAmount = totalAwardBudgetAmount.add(locReviewDtl.getAwardBudgetAmount());
                        if (ObjectUtils.isNotNull(awardAccount.getAccount().getContractControlAccountNumber()) && awardAccount.getAccountNumber().equalsIgnoreCase(awardAccount.getAccount().getContractControlAccountNumber())) {
                            locReviewDtl.setAccountDescription(ArConstants.CONTRACT_CONTROL_ACCOUNT);
                        }
                        else {
                            locReviewDtl.setAccountDescription(ArConstants.ACCOUNT);
                        }
                        locReviewDtl.setAmountToDraw(awardAccount.getAmountToDraw());
                        locReviewDtl.setHiddenAmountToDraw(awardAccount.getAmountToDraw());
                        totalAmountToDraw = totalAmountToDraw.add(locReviewDtl.getAmountToDraw());
                        accountReviewDetails.add(locReviewDtl);

                    }
                    // Amount to Draw for Header = Sum(Amount of Draw for individual accounts)
                    for (ContractsGrantsLOCReviewDetail detail : getHeaderReviewDetails()) {// To identify the header row
                        if (ObjectUtils.isNotNull(detail.getAgencyNumber()) && ObjectUtils.isNull(detail.getAccountDescription()) && detail.getProposalNumber().equals(award.getProposalNumber())) {
                            detail.setAmountToDraw(totalAmountToDraw);
                            detail.setHiddenAmountToDraw(totalAmountToDraw);
                            detail.setClaimOnCashBalance(totalClaimOnCashBalance);
                            detail.setAwardBudgetAmount(totalAwardBudgetAmount);

                        }
                    }

                }
            }

        }


    }


    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#prepareForSave() To check if the amount to Draw
     *      field has been changed and to set the award locReviewIndicator to true.
     */
    @Override
    public void prepareForSave() {


        super.prepareForSave();

        // 1. compare the hiddenamountodraw and amount to draw field.
        for (ContractsGrantsLOCReviewDetail detail : getAccountReviewDetails()) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("proposalNumber", detail.getProposalNumber());
            ContractsAndGrantsCGBAward award = (ContractsAndGrantsCGBAward) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAward.class, map);
            // to set funds Not Drawn as a difference between amountToDraw and hiddenAmountToDraw.


            // To set amount to Draw to 0 if there are blank values, to avoid exceptions.
            if (ObjectUtils.isNull(detail.getAmountToDraw()) || detail.getAmountToDraw().isNegative()) {
                detail.setAmountToDraw(KualiDecimal.ZERO);
            }
            detail.setFundsNotDrawn(detail.getHiddenAmountToDraw().subtract(detail.getAmountToDraw()));

            if (detail.getFundsNotDrawn().isNegative()) {
                GlobalVariables.getMessageMap().putError("fundsNotDrawn", ArKeyConstants.ContractsGrantsInvoiceConstants.ERROR_DOCUMENT_AMOUNT_TO_DRAW_INVALID);
                detail.setFundsNotDrawn(KualiDecimal.ZERO);
                detail.setAmountToDraw(detail.getHiddenAmountToDraw().subtract(detail.getFundsNotDrawn()));
            }

            if (detail.getHiddenAmountToDraw().compareTo(detail.getAmountToDraw()) != 0) {
                // This means the amount to Draw field has been changed,so

                // a. set locreview indicator to yes in award account
                // b.then set amounts to draw in award account
                for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    // set the amount to Draw in the award Account
                    Map<String, Object> criteria = new HashMap<String, Object>();
                    criteria.put("accountNumber", awardAccount.getAccountNumber());
                    criteria.put("chartOfAccountsCode", awardAccount.getChartOfAccountsCode());
                    criteria.put("proposalNumber", awardAccount.getProposalNumber());
                    if (detail.getAccountNumber().equals(awardAccount.getAccountNumber())) {
                        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setAmountToDrawToAwardAccount(criteria, detail.getAmountToDraw());
                        SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLOCReviewIndicatorToAwardAccount(criteria, true);
                    }
                }
            }
        }

        // To sum up amount to draw values.
        Set<Long> proposalNumberSet = new HashSet<Long>();
        for (ContractsGrantsLOCReviewDetail detail : getHeaderReviewDetails()) {
            // Adding the awards to a set, to get unique values.
            proposalNumberSet.add(detail.getProposalNumber());

        }

        // 2. create invoices. - independent whether the amounts were changed or not.

        // To get the list of awards from the proposal Number set.

        Iterator<Long> iterator = proposalNumberSet.iterator();

        while (iterator.hasNext()) {
            KualiDecimal totalAmountToDraw = KualiDecimal.ZERO;
            Long proposalNumber = iterator.next();
            for (ContractsGrantsLOCReviewDetail detail : getAccountReviewDetails()) {// To identify the header row
                if (ObjectUtils.isNotNull(detail.getAccountDescription()) && detail.getProposalNumber().equals(proposalNumber)) {
                    totalAmountToDraw = totalAmountToDraw.add(detail.getAmountToDraw());

                }
            }


            for (ContractsGrantsLOCReviewDetail detail : getHeaderReviewDetails()) {// To identify the header row
                if (ObjectUtils.isNotNull(detail.getAgencyNumber()) && ObjectUtils.isNull(detail.getAccountDescription()) && detail.getProposalNumber().equals(proposalNumber)) {
                    detail.setAmountToDraw(totalAmountToDraw);

                }
            }
        }

    }


    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        ContractsGrantsInvoiceCreateDocumentService contractsGrantsInvoiceCreateDocumentService = SpringContext.getBean(ContractsGrantsInvoiceCreateDocumentService.class);
        List<ContractsAndGrantsCGBAward> awards = new ArrayList<ContractsAndGrantsCGBAward>();


        Set<Long> proposalNumberSet = new HashSet<Long>();

        super.doRouteStatusChange(statusChangeEvent);
        // performed only when document is in final state
        if (getDocumentHeader().getWorkflowDocument().stateIsFinal()) {

            // 1. compare the hiddenamountodraw and amount to draw field.
            for (ContractsGrantsLOCReviewDetail detail : getHeaderReviewDetails()) {
                // Adding the awards to a set, to get unique values.
                proposalNumberSet.add(detail.getProposalNumber());

            }

            // 2. create invoices. - independent whether the amounts were changed or not.

            // To get the list of awards from the proposal Number set.

            Iterator<Long> iterator = proposalNumberSet.iterator();

            while (iterator.hasNext()) {

                Map<String, Object> map = new HashMap<String, Object>();
                map.put("proposalNumber", iterator.next());
                ContractsAndGrantsCGBAward awd = (ContractsAndGrantsCGBAward) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsCGBAward.class).getExternalizableBusinessObject(ContractsAndGrantsCGBAward.class, map);
                awards.add(awd);
            }
            // To set the loc Creation Type to award based on the LOC Review document being retrieved.

            for (ContractsAndGrantsCGBAward award : awards) {
                if (ObjectUtils.isNotNull(this.getLetterOfCreditFundCode())) {

                    SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLOCCreationTypeToAward(award.getProposalNumber(), ArConstants.LOC_BY_LOC_FUND);
                }
                else {

                    SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLOCCreationTypeToAward(award.getProposalNumber(), ArConstants.LOC_BY_LOC_FUND_GRP);
                }

            }
            // To retrieve the batch file directory name as "reports/cg"
            ModuleConfiguration systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-AR").getModuleConfiguration();

            String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);

            String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());

            String errOutputFile2 = destinationFolderPath + File.separator + ArConstants.BatchFileSystem.LOC_REVIEW_CREATION_ERROR_OUTPUT_FILE + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;


            contractsGrantsInvoiceCreateDocumentService.createCGInvoiceDocumentsByAwards(awards, errOutputFile2);


            // The next important step is to set the locReviewIndicator to false and amount to Draw fields in award Account to zero.
            // This should not affect any further invoicing.
            for (ContractsAndGrantsCGBAward award : awards) {
                for (ContractsAndGrantsCGBAwardAccount awardAccount : award.getActiveAwardAccounts()) {
                    Map<String, Object> criteria = new HashMap<String, Object>();
                    criteria.put("accountNumber", awardAccount.getAccountNumber());
                    criteria.put("chartOfAccountsCode", awardAccount.getChartOfAccountsCode());
                    criteria.put("proposalNumber", awardAccount.getProposalNumber());
                    SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setAmountToDrawToAwardAccount(criteria, KualiDecimal.ZERO);// clear
                    // values
                    SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLOCReviewIndicatorToAwardAccount(criteria, false);// clear
                    // values

                }
                SpringContext.getBean(ContractsAndGrantsModuleUpdateService.class).setLOCCreationTypeToAward(award.getProposalNumber(), null);

            }


        }

    }

}
