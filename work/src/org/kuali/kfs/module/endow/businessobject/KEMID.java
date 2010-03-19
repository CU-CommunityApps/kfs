/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * This KEMID class is an identifier for a specific set of Funds held by the institution as an endowment of funds functioning as an
 * endowment. Associated with each KEMID are fields containing information that determine what sort of activity may take place for
 * the KEMID and how and when automated activity is yo occur. In addition, fields will be used to define the KEMID that can be
 * instrumental in the development of reports for the institution.
 */
public class KEMID extends PersistableBusinessObjectBase implements Inactivateable {

    private String kemid;
    private String shortTitle;
    private String longTitle;
    private Date dateOpened;
    private Date dateEstablished;
    private String typeCode;
    private String incomeCAECode;
    private String principalCAECode;
    private String responsibleAdminCode;
    private String transactionRestrictionCode;
    private Integer cashSweepModelId;
    private Integer incomeACIModelId;
    private Integer principalACIModelId;
    private boolean dormantIndicator;
    private String purposeCode;
    private boolean active;

    private String closedToKEMID;
    private String closeCode;
    private String dispositionOfFunds;
    private Date dateClosed;

    private PurposeCode purpose;
    private CloseCode reasonClosed;
    private TypeCode type;
    private CAECode incomeCAE;
    private CAECode principalCAE;
    private ResponsibleAdministrationCode responsibleAdministration;
    private TransactionRestrictionCode transactionRestriction;
    private CashSweepModel cashSweepModel;
    private AutomatedCashInvestmentModel incomeACIModel;
    private AutomatedCashInvestmentModel principalACIModel;

    private EndowmentCorpusValues endowmentCorpusValues;

    // collections
    private List<KemidAgreement> kemidAgreements;
    private List<KemidSourceOfFunds> kemidSourcesOfFunds;
    private List<KemidBenefittingOrganization> kemidBenefittingOrganizations;
    private List<KemidGeneralLedgerAccount> kemidGeneralLedgerAccounts;
    private List<KemidPayoutInstruction> kemidPayoutInstructions;
    private List<KemidUseCriteria> kemidUseCriteria;
    private List<KemidSpecialInstruction> kemidSpecialInstructions;
    private List<KemidFee> kemidFees;
    private List<KemidReportGroup> kemidReportGroups;
    private List<KemidDonorStatement> kemidDonorStatements;
    private List<KemidCombineDonorStatement> kemidCombineDonorStatements;

    /**
     * Constructs a KEMID.java.
     */
    public KEMID() {
        super();
        kemidAgreements = new TypedArrayList(KemidAgreement.class);
        kemidSourcesOfFunds = new TypedArrayList(KemidSourceOfFunds.class);
        kemidBenefittingOrganizations = new TypedArrayList(KemidBenefittingOrganization.class);
        kemidGeneralLedgerAccounts = new TypedArrayList(KemidGeneralLedgerAccount.class);
        kemidPayoutInstructions = new TypedArrayList(KemidPayoutInstruction.class);
        kemidUseCriteria = new TypedArrayList(KemidUseCriteria.class);
        kemidSpecialInstructions = new TypedArrayList(KemidSpecialInstruction.class);
        kemidFees = new TypedArrayList(KemidFee.class);
        kemidReportGroups = new TypedArrayList(KemidReportGroup.class);
        kemidDonorStatements = new TypedArrayList(KemidDonorStatement.class);
        kemidCombineDonorStatements = new TypedArrayList(KemidCombineDonorStatement.class);
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        return m;
    }

    /**
     * Gets the longTitle.
     * 
     * @return longTitle
     */
    public String getLongTitle() {
        return longTitle;
    }

    /**
     * Sets the longTitle.
     * 
     * @param longTitle
     */
    public void setLongTitle(String longTitle) {
        this.longTitle = longTitle;
    }

    /**
     * Gets the dateOpened.
     * 
     * @return dateOpened
     */
    public Date getDateOpened() {
        return dateOpened;
    }

    /**
     * Sets the dateOpened.
     * 
     * @param dateOpened
     */
    public void setDateOpened(Date dateOpened) {
        this.dateOpened = dateOpened;
    }

    /**
     * Gets the dateEstablished.
     * 
     * @return dateEstablished
     */
    public Date getDateEstablished() {
        return dateEstablished;
    }

    /**
     * Sets the dateEstablished.
     * 
     * @param dateEstablished
     */
    public void setDateEstablished(Date dateEstablished) {
        this.dateEstablished = dateEstablished;
    }

    /**
     * Gets the typeCode.
     * 
     * @return typeCode
     */
    public String getTypeCode() {
        return typeCode;
    }

    /**
     * Sets the typeCode.
     * 
     * @param typeCode
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * Gets the incomeCAECode.
     * 
     * @return incomeCAECode
     */
    public String getIncomeCAECode() {
        return incomeCAECode;
    }

    /**
     * Sets the incomeCAECode.
     * 
     * @param incomeCAECode
     */
    public void setIncomeCAECode(String incomeCAECode) {
        this.incomeCAECode = incomeCAECode;
    }

    /**
     * Gets the incomeCAECode.
     * 
     * @return incomeCAECode
     */
    public String getPrincipalCAECode() {
        return principalCAECode;
    }

    /**
     * Sets the incomeCAECode.
     * 
     * @param principalCAECode
     */
    public void setPrincipalCAECode(String principalCAECode) {
        this.principalCAECode = principalCAECode;
    }

    /**
     * Gets the responsibleAdminCode.
     * 
     * @return responsibleAdminCode
     */
    public String getResponsibleAdminCode() {
        return responsibleAdminCode;
    }

    /**
     * Sets the responsibleAdminCode.
     * 
     * @param responsibleAdminCode
     */
    public void setResponsibleAdminCode(String responsibleAdminCode) {
        this.responsibleAdminCode = responsibleAdminCode;
    }

    /**
     * Gets the transactionRestrictionCode.
     * 
     * @return transactionRestrictionCode
     */
    public String getTransactionRestrictionCode() {
        return transactionRestrictionCode;
    }

    /**
     * Sets the transactionRestrictionCode.
     * 
     * @param transactionRestrictionCode
     */
    public void setTransactionRestrictionCode(String transactionRestrictionCode) {
        this.transactionRestrictionCode = transactionRestrictionCode;
    }

    /**
     * Gets the cashSweepModelId;
     * 
     * @return cashSweepModelId
     */
    public Integer getCashSweepModelId() {
        return cashSweepModelId;
    }

    /**
     * Sets the cashSweepModelId.
     * 
     * @param cashSweepModelId
     */
    public void setCashSweepModelId(Integer cashSweepModelId) {
        this.cashSweepModelId = cashSweepModelId;
    }

    /**
     * Gets the incomeACIModelId.
     * 
     * @return incomeACIModelId
     */
    public Integer getIncomeACIModelId() {
        return incomeACIModelId;
    }

    /**
     * Sets the incomeACIModelId.
     * 
     * @param incomeACIModelId
     */
    public void setIncomeACIModelId(Integer incomeACIModelId) {
        this.incomeACIModelId = incomeACIModelId;
    }

    /**
     * Gets the principalACIModelId.
     * 
     * @return principalACIModelId
     */
    public Integer getPrincipalACIModelId() {
        return principalACIModelId;
    }

    /**
     * Sets the principalACIModelId.
     * 
     * @param principalACIModelId
     */
    public void setPrincipalACIModelId(Integer principalACIModelId) {
        this.principalACIModelId = principalACIModelId;
    }

    /**
     * Gets the dormantIndicator.
     * 
     * @return dormantIndicator
     */
    public boolean isDormantIndicator() {
        return dormantIndicator;
    }

    /**
     * Sets the dormantIndicator.
     * 
     * @param dormantIndicator
     */
    public void setDormantIndicator(boolean dormantIndicator) {
        this.dormantIndicator = dormantIndicator;
    }

    /**
     * Gets the closedToKEMID.
     * 
     * @return closedToKEMID
     */
    public String getClosedToKEMID() {
        return closedToKEMID;
    }

    /**
     * Sets the closedToKEMID.
     * 
     * @param closedToKEMID
     */
    public void setClosedToKEMID(String closedToKEMID) {
        this.closedToKEMID = closedToKEMID;
    }

    /**
     * Gets the closeCode.
     * 
     * @return closeCode
     */
    public String getCloseCode() {
        return closeCode;
    }

    /**
     * Sets the closeCode.
     * 
     * @param closeCode
     */
    public void setCloseCode(String closeCode) {
        this.closeCode = closeCode;
    }

    /**
     * Gets the dispositionOfFunds.
     * 
     * @return dispositionOfFunds
     */
    public String getDispositionOfFunds() {
        return dispositionOfFunds;
    }

    /**
     * Sets the dispositionOfFunds.
     * 
     * @param dispositionOfFunds
     */
    public void setDispositionOfFunds(String dispositionOfFunds) {
        this.dispositionOfFunds = dispositionOfFunds;
    }

    /**
     * Gets the dateClosed.
     * 
     * @return dateClosed
     */
    public Date getDateClosed() {
        return dateClosed;
    }

    /**
     * Sets the dateClosed.
     * 
     * @param dateClosed
     */
    public void setDateClosed(Date dateClosed) {
        this.dateClosed = dateClosed;
    }


    /**
     * Gets the kemid.
     * 
     * @return
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the shortTitle.
     * 
     * @return shortTitle
     */
    public String getShortTitle() {
        return shortTitle;
    }

    /**
     * Sets the shortTitle.
     * 
     * @param shortTitle
     */
    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    /**
     * Gets kemid-shortTitle.
     * 
     * @return kemid-shortTitle
     */
    public String getKemidAndShortTitle() {
        return getKemid() + " - " + getShortTitle();
    }

    /**
     * Setter for kemidAndShortTitle. Does nothing.
     * 
     * @param kemidAndShortTitle
     */
    public void setKemidAndShortTitle(String kemidAndShortTitle) {

    }


    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the purpose.
     * 
     * @return purpose
     */
    public PurposeCode getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose.
     * 
     * @param purpose
     */
    public void setPurpose(PurposeCode purpose) {
        this.purpose = purpose;
    }

    /**
     * Gets the purposeCode.
     * 
     * @return purposeCode
     */
    public String getPurposeCode() {
        return purposeCode;
    }

    /**
     * Sets the purposeCode.
     * 
     * @param purposeCode
     */
    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    /**
     * Gets the reasonClosed.
     * 
     * @return reasonClosed
     */
    public CloseCode getReasonClosed() {
        return reasonClosed;
    }

    /**
     * Sets the reasonClosed.
     * 
     * @param reasonClosed
     */
    public void setReasonClosed(CloseCode reasonClosed) {
        this.reasonClosed = reasonClosed;
    }

    /**
     * Gets the type.
     * 
     * @return type
     */
    public TypeCode getType() {
        return type;
    }

    /**
     * Sets the type.
     * 
     * @param type
     */
    public void setType(TypeCode type) {
        this.type = type;
    }

    /**
     * Gets the transactionRestriction.
     * 
     * @return transactionRestriction
     */
    public TransactionRestrictionCode getTransactionRestriction() {
        return transactionRestriction;
    }

    /**
     * Sets the transactionRestriction.
     * 
     * @param transactionRestriction
     */
    public void setTransactionRestriction(TransactionRestrictionCode transactionRestriction) {
        this.transactionRestriction = transactionRestriction;
    }

    /**
     * Gets the cashSweepModel.
     * 
     * @return cashSweepModel
     */
    public CashSweepModel getCashSweepModel() {
        return cashSweepModel;
    }

    /**
     * Sets the cashSweepModel.
     * 
     * @param cashSweepModel
     */
    public void setCashSweepModel(CashSweepModel cashSweepModel) {
        this.cashSweepModel = cashSweepModel;
    }

    /**
     * Gets the incomeACIModel.
     * 
     * @return incomeACIModel
     */
    public AutomatedCashInvestmentModel getIncomeACIModel() {
        return incomeACIModel;
    }

    /**
     * Sets the incomeACIModel.
     * 
     * @param incomeACIModel
     */
    public void setIncomeACIModel(AutomatedCashInvestmentModel incomeACIModel) {
        this.incomeACIModel = incomeACIModel;
    }

    /**
     * Gets the principalACIModel.
     * 
     * @return principalACIModel
     */
    public AutomatedCashInvestmentModel getPrincipalACIModel() {
        return principalACIModel;
    }

    /**
     * Sets the principalACIModel.
     * 
     * @param principalACIModel
     */
    public void setPrincipalACIModel(AutomatedCashInvestmentModel principalACIModel) {
        this.principalACIModel = principalACIModel;
    }

    /**
     * Gets the kemidAgreements.
     * 
     * @return kemidAgreements
     */
    public List<KemidAgreement> getKemidAgreements() {
        return kemidAgreements;
    }

    /**
     * Sets the kemidAgreements.
     * 
     * @param kemidAgreements
     */
    public void setKemidAgreements(List<KemidAgreement> kemidAgreements) {
        this.kemidAgreements = kemidAgreements;
    }

    /**
     * Gets the kemidSourcesOfFunds.
     * 
     * @return kemidSourcesOfFunds
     */
    public List<KemidSourceOfFunds> getKemidSourcesOfFunds() {
        return kemidSourcesOfFunds;
    }

    /**
     * Sets the kemidSourcesOfFunds.
     * 
     * @param kemidSourcesOfFunds
     */
    public void setKemidSourcesOfFunds(List<KemidSourceOfFunds> kemidSourcesOfFunds) {
        this.kemidSourcesOfFunds = kemidSourcesOfFunds;
    }

    /**
     * Gets the kemidBenefittingOrganizations.
     * 
     * @return kemidBenefittingOrganizations
     */
    public List<KemidBenefittingOrganization> getKemidBenefittingOrganizations() {
        return kemidBenefittingOrganizations;
    }

    /**
     * Sets the kemidBenefittingOrganizations.
     * 
     * @param kemidBenefittingOrganizations
     */
    public void setKemidBenefittingOrganizations(List<KemidBenefittingOrganization> kemidBenefittingOrganizations) {
        this.kemidBenefittingOrganizations = kemidBenefittingOrganizations;
    }

    /**
     * Gets the kemidGeneralLedgerAccounts.
     * 
     * @return kemidGeneralLedgerAccounts
     */
    public List<KemidGeneralLedgerAccount> getKemidGeneralLedgerAccounts() {
        return kemidGeneralLedgerAccounts;
    }

    /**
     * Sets the kemidGeneralLedgerAccounts.
     * 
     * @param kemidGeneralLedgerAccounts
     */
    public void setKemidGeneralLedgerAccounts(List<KemidGeneralLedgerAccount> kemidGeneralLedgerAccounts) {
        this.kemidGeneralLedgerAccounts = kemidGeneralLedgerAccounts;
    }

    /**
     * Gets the kemidPayoutInstructions.
     * 
     * @return kemidPayoutInstructions
     */
    public List<KemidPayoutInstruction> getKemidPayoutInstructions() {
        return kemidPayoutInstructions;
    }

    /**
     * Sets the kemidPayoutInstructions.
     * 
     * @param kemidPayoutInstructions
     */
    public void setKemidPayoutInstructions(List<KemidPayoutInstruction> kemidPayoutInstructions) {
        this.kemidPayoutInstructions = kemidPayoutInstructions;
    }

    /**
     * Gets the kemidUseCriteria.
     * 
     * @return kemidUseCriteria
     */
    public List<KemidUseCriteria> getKemidUseCriteria() {
        return kemidUseCriteria;
    }

    /**
     * Sets the kemidUseCriteria.
     * 
     * @param kemidUseCriteria
     */
    public void setKemidUseCriteria(List<KemidUseCriteria> kemidUseCriteria) {
        this.kemidUseCriteria = kemidUseCriteria;
    }

    /**
     * Gets the kemidSpecialInstructions.
     * 
     * @return kemidSpecialInstructions
     */
    public List<KemidSpecialInstruction> getKemidSpecialInstructions() {
        return kemidSpecialInstructions;
    }

    /**
     * Sets the kemidSpecialInstructions.
     * 
     * @param kemidSpecialInstructions
     */
    public void setKemidSpecialInstructions(List<KemidSpecialInstruction> kemidSpecialInstructions) {
        this.kemidSpecialInstructions = kemidSpecialInstructions;
    }

    /**
     * Gets the kemidFees.
     * 
     * @return kemidFees
     */
    public List<KemidFee> getKemidFees() {
        return kemidFees;
    }

    /**
     * Sets the kemidFees.
     * 
     * @param kemidFees
     */
    public void setKemidFees(List<KemidFee> kemidFees) {
        this.kemidFees = kemidFees;
    }

    /**
     * Gets the kemidReportGroups.
     * 
     * @return kemidReportGroups
     */
    public List<KemidReportGroup> getKemidReportGroups() {
        return kemidReportGroups;
    }

    /**
     * Sets the kemidReportGroups.
     * 
     * @param kemidReportGroups
     */
    public void setKemidReportGroups(List<KemidReportGroup> kemidReportGroups) {
        this.kemidReportGroups = kemidReportGroups;
    }

    /**
     * Gets the kemidDonorStatements.
     * 
     * @return kemidDonorStatements
     */
    public List<KemidDonorStatement> getKemidDonorStatements() {
        return kemidDonorStatements;
    }

    /**
     * Sets the kemidDonorStatements.
     * 
     * @param kemidDonorStatements
     */
    public void setKemidDonorStatements(List<KemidDonorStatement> kemidDonorStatements) {
        this.kemidDonorStatements = kemidDonorStatements;
    }

    /**
     * Gets the kemidCombineDonorStatements.
     * 
     * @return kemidCombineDonorStatements
     */
    public List<KemidCombineDonorStatement> getKemidCombineDonorStatements() {
        return kemidCombineDonorStatements;
    }

    /**
     * Sets the kemidCombineDonorStatements.
     * 
     * @param kemidCombineDonorStatements
     */
    public void setKemidCombineDonorStatements(List<KemidCombineDonorStatement> kemidCombineDonorStatements) {
        this.kemidCombineDonorStatements = kemidCombineDonorStatements;
    }

    /**
     * Gets the endowmentCorpusValues.
     * 
     * @return endowmentCorpusValues
     */
    public EndowmentCorpusValues getEndowmentCorpusValues() {
        return endowmentCorpusValues;
    }

    /**
     * Sets the endowmentCorpusValues.
     * 
     * @param endowmentCorpusValues
     */
    public void setEndowmentCorpusValues(EndowmentCorpusValues endowmentCorpusValues) {
        this.endowmentCorpusValues = endowmentCorpusValues;
    }

    /**
     * Gets the incomeCAE.
     * 
     * @return incomeCAE
     */
    public CAECode getIncomeCAE() {
        return incomeCAE;
    }

    /**
     * Sets the incomeCAE.
     * 
     * @param incomeCAE
     */
    public void setIncomeCAE(CAECode incomeCAE) {
        this.incomeCAE = incomeCAE;
    }

    /**
     * Gets the principalCAE.
     * 
     * @return principalCAE
     */
    public CAECode getPrincipalCAE() {
        return principalCAE;
    }

    /**
     * Sets the principalCAE.
     * 
     * @param principalCAE
     */
    public void setPrincipalCAE(CAECode principalCAE) {
        this.principalCAE = principalCAE;
    }

    /**
     * Gets the responsibleAdministration.
     * 
     * @return responsibleAdministration
     */
    public ResponsibleAdministrationCode getResponsibleAdministration() {
        return responsibleAdministration;
    }

    /**
     * Sets the responsibleAdministration.
     * 
     * @param responsibleAdministration
     */
    public void setResponsibleAdministration(ResponsibleAdministrationCode responsibleAdministration) {
        this.responsibleAdministration = responsibleAdministration;
    }

    /**
     * Returns KEMID concatenated with Short Label
     * 
     * @return
     */
    public String getKemIdLabel() {
        String kemID = getKemid() != null ? getKemid() : "";
        String shortTitle = getShortTitle() != null ? getShortTitle() : "";
        String kemIdLabel = kemID + " - " + shortTitle;
        return kemIdLabel;
    }
}
