/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.cg.service.impl;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAwardAccount;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.Bill;
import org.kuali.kfs.module.cg.businessobject.Milestone;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.dataaccess.BillDao;
import org.kuali.kfs.module.cg.dataaccess.MilestoneDao;
import org.kuali.kfs.module.cg.service.AwardService;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This Class provides implementation to the services required for inter module communication.
 */
@NonTransactional
public class ContractsAndGrantsModuleUpdateServiceImpl implements ContractsAndGrantsModuleUpdateService {
    private AwardService awardService;
    private BillDao billDao;
    private BusinessObjectService businessObjectService;
    private MilestoneDao milestoneDao;

    /**
     * This method sets last Billed Date to award Account.
     * 
     * @param criteria
     * @param invoiceStatus
     * @param lastBilledDate
     */
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, String invoiceStatus, Date lastBilledDate) {
        AwardAccount awardAccount = (AwardAccount) getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        // If the invoice is final, transpose current last billed date to previous and set invoice last billed date to current.

        if (invoiceStatus.equalsIgnoreCase("FINAL")) {
            awardAccount.setPreviousLastBilledDate(awardAccount.getCurrentLastBilledDate());
            awardAccount.setCurrentLastBilledDate(lastBilledDate);
        }

        // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
        else if (invoiceStatus.equalsIgnoreCase("CORRECTED")) {
            awardAccount.setCurrentLastBilledDate(awardAccount.getPreviousLastBilledDate());
            awardAccount.setPreviousLastBilledDate(null);
        }

        getBusinessObjectService().save(awardAccount);

    }

    /**
     * This method sets last billed Date to Award
     * 
     * @param proposalNumber
     * @param lastBilledDate
     */
    public void setLastBilledDateToAward(Long proposalNumber, Date lastBilledDate) {
        Award award = (Award) getBusinessObjectService().findBySinglePrimaryKey(Award.class, proposalNumber);

        award.setLastBilledDate(lastBilledDate);
        getBusinessObjectService().save(award);

    }

    /**
     * This method updates value of isItBilled in Bill BO to Yes
     * 
     * @param criteria
     */
    public void setBillsisItBilled(Criteria criteria, String value) {
        Collection<Bill> bills = getBillDao().getBillsByMatchingCriteria(criteria);
        for (Bill bill : bills) {
            bill.setIsItBilled(value);
            getBusinessObjectService().save(bill);
        }
    }

    /**
     * This method updates value of isItBilled in Milestone BO to Yes
     * 
     * @param criteria
     */
    @SuppressWarnings("null")
    public void setMilestonesisItBilled(Long proposalNumber, List<Long> milestoneIds, String value) {
        Collection<Milestone> milestones = null;
        try {
            milestones = getMilestoneDao().getMatchingMilestoneByProposalIdAndInListOfMilestoneId(proposalNumber, milestoneIds);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        for (Milestone milestone : milestones) {
            milestone.setIsItBilled(value);
            getBusinessObjectService().save(milestone);
        }
    }


    /**
     * This method sets value of LOC Creation Type to Award
     * 
     * @param proposalNumber
     * @param locCreationType
     */
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType) {
        Award award = (Award) getBusinessObjectService().findBySinglePrimaryKey(Award.class, proposalNumber);

        award.setLocCreationType(locCreationType);
        getBusinessObjectService().save(award);
    }

    /**
     * This method sets amount to draw to award Account.
     * 
     * @param criteria
     * @param amountToDraw
     */
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw) {
        AwardAccount awardAccount = (AwardAccount) getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        awardAccount.setAmountToDraw(amountToDraw);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * This method sets loc review indicator to award Account.
     * 
     * @param criteria
     * @param locReviewIndicator
     */
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator) {
        AwardAccount awardAccount = (AwardAccount) getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        awardAccount.setLocReviewIndicator(locReviewIndicator);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * This method sets final billed to award Account.
     * 
     * @param criteria
     * @param finalBilled
     */
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        AwardAccount awardAccount = (AwardAccount) getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        awardAccount.setFinalBilled(finalBilled);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * This method sets invoice Document Status to award Account.
     * 
     * @param criteria
     * @param invoiceDocumentStatus
     */
    public void setAwardAccountInvoiceDocumentStatus(Map<String, Object> criteria, String invoiceDocumentStatus) {
        AwardAccount awardAccount = (AwardAccount) getBusinessObjectService().findByPrimaryKey(AwardAccount.class, criteria);
        awardAccount.setInvoiceDocumentStatus(invoiceDocumentStatus);
        getBusinessObjectService().save(awardAccount);
    }

    /**
     * This method sets values to Award respective to junit testing
     * 
     * @param proposalNumber
     * @param fieldValues
     */
    @SuppressWarnings("deprecation")
    public void setAwardAccountsToAward(Long proposalNumber, List<ContractsAndGrantsCGBAwardAccount> awardAccounts) {

        // Award and proposal is being saved
        Proposal proposal = KNSServiceLocator.getBusinessObjectService().findBySinglePrimaryKey(Proposal.class, proposalNumber);
        if (ObjectUtils.isNull(proposal)) {
            proposal = new Proposal();
            proposal.setProposalNumber(proposalNumber);
        }
        getBusinessObjectService().save(proposal);

        Award award = getBusinessObjectService().findBySinglePrimaryKey(Award.class, proposalNumber);
        if (ObjectUtils.isNull(award)) {
            award = new Award();
            award.setProposalNumber(proposalNumber);

        }
        getBusinessObjectService().save(award);

        List<AwardAccount> awdAccts = new TypedArrayList(AwardAccount.class);


        for (ContractsAndGrantsCGBAwardAccount awardAccount : awardAccounts) {
            Map<String, Object> mapKey = new HashMap<String, Object>();
            mapKey.put("accountNumber", awardAccount.getAccountNumber());
            mapKey.put("chartOfAccountsCode", awardAccount.getChartOfAccountsCode());
            mapKey.put("proposalNumber", awardAccount.getProposalNumber());
            AwardAccount awdAcct = (AwardAccount) KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(AwardAccount.class, mapKey);
            if (ObjectUtils.isNull(awdAcct)) {
                awdAcct = new AwardAccount();
                awdAcct.setAccountNumber(awardAccount.getAccountNumber());
                awdAcct.setChartOfAccountsCode(awardAccount.getChartOfAccountsCode());
                awdAcct.setProposalNumber(awardAccount.getProposalNumber());

            }


            getBusinessObjectService().save(awdAcct);

            awdAccts.add(awdAcct);
        }
        award.setAwardAccounts(awdAccts);


    }


    /**
     * Gets the awardService attribute.
     * 
     * @return Returns the awardService.
     */
    public AwardService getAwardService() {
        return awardService;
    }

    /**
     * Sets the awardService attribute value.
     * 
     * @param awardService The awardService to set.
     */
    public void setAwardService(AwardService awardService) {
        this.awardService = awardService;
    }

    /**
     * Gets the billDao attribute.
     * 
     * @return Returns the billDao.
     */
    public BillDao getBillDao() {
        return billDao;
    }

    /**
     * Sets the billDao attribute value.
     * 
     * @param billDao The billDao to set.
     */
    public void setBillDao(BillDao billDao) {
        this.billDao = billDao;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the milestoneDao attribute.
     * 
     * @return Returns the milestoneDao.
     */
    public MilestoneDao getMilestoneDao() {
        return milestoneDao;
    }

    /**
     * Sets the milestoneDao attribute value.
     * 
     * @param milestoneDao The milestoneDao to set.
     */
    public void setMilestoneDao(MilestoneDao milestoneDao) {
        this.milestoneDao = milestoneDao;
    }


}
