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
package org.kuali.kfs.module.cam.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.module.cam.document.workflow.RoutingAssetNumber;
import org.kuali.kfs.module.cam.document.workflow.RoutingAssetTagNumber;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.Country;
import org.kuali.rice.kns.bo.PostalCode;
import org.kuali.rice.kns.bo.State;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.SaveDocumentEvent;
import org.kuali.rice.kns.service.CountryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.MaintenanceDocumentService;
import org.kuali.rice.kns.service.PostalCodeService;
import org.kuali.rice.kns.service.StateService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class EquipmentLoanOrReturnDocument extends FinancialSystemTransactionalDocumentBase implements GenericRoutingInfo {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocument.class);

    private String documentNumber;
    private Date loanDate;
    private Date expectedReturnDate;
    private Date loanReturnDate;
    private String borrowerUniversalIdentifier;
    private String borrowerAddress;
    private String borrowerCityName;
    private String borrowerStateCode;
    private String borrowerZipCode;
    private String borrowerCountryCode;
    private String borrowerPhoneNumber;
    private String borrowerStorageAddress;
    private String borrowerStorageCityName;
    private String borrowerStorageStateCode;
    private String borrowerStorageZipCode;
    private String borrowerStorageCountryCode;
    private String borrowerStoragePhoneNumber;
    private Long capitalAssetNumber;

    private State borrowerState;
    private State borrowerStorageState;
    private Country borrowerCountry;
    private Country borrowerStorageCountry;
    private Person borrowerPerson;
    private Asset asset;
    private PostalCode borrowerPostalZipCode;
    private PostalCode borrowerStoragePostalZipCode;

    // sets document status (i.e. new loan, return, or renew)
    private boolean newLoan;
    private boolean returnLoan;

    private Set<RoutingData> routingInfo;

    /**
     * Default constructor.
     */
    public EquipmentLoanOrReturnDocument() {
        super();
    }

    /**
     * Gets the asset attribute.
     * 
     * @return Returns the asset
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset attribute.
     * 
     * @param asset The asset to set.
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the borrowerCountry attribute.
     * 
     * @return Returns the borrowerCountry
     */
    public Country getBorrowerCountry() {
        borrowerCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(this, borrowerCountryCode, borrowerCountry);
        return borrowerCountry;
    }

    /**
     * Sets the borrowerCountry attribute.
     * 
     * @param borrowerCountry The borrowerCountry to set.
     */
    public void setBorrowerCountry(Country borrowerCountry) {
        this.borrowerCountry = borrowerCountry;
    }

    /**
     * Gets the borrowerState attribute.
     * 
     * @return Returns the borrowerState
     */
    public State getBorrowerState() {
        borrowerState = SpringContext.getBean(StateService.class).getByPrimaryIdIfNecessary(this, borrowerCountryCode, borrowerStateCode, borrowerState);
        return borrowerState;
    }

    /**
     * Sets the borrowerState attribute.
     * 
     * @param borrowerState The borrowerState to set.
     */
    public void setBorrowerState(State borrowerState) {
        this.borrowerState = borrowerState;
    }

    /**
     * Gets the borrowerStorageCountry attribute.
     * 
     * @return Returns the borrowerStorageCountry
     */
    public Country getBorrowerStorageCountry() {
        borrowerStorageCountry = SpringContext.getBean(CountryService.class).getByPrimaryIdIfNecessary(this, borrowerStorageCountryCode, borrowerStorageCountry);
        return borrowerStorageCountry;
    }

    /**
     * Sets the borrowerStorageCountry attribute.
     * 
     * @param borrowerStorageCountry The borrowerStorageCountry to set.
     */
    public void setBorrowerStorageCountry(Country borrowerStorageCountry) {
        this.borrowerStorageCountry = borrowerStorageCountry;
    }

    /**
     * Gets the getBorrowerStorageState attribute.
     * 
     * @return Returns the getBorrowerStorageState
     */
    public State getBorrowerStorageState() {
        borrowerStorageState = SpringContext.getBean(StateService.class).getByPrimaryIdIfNecessary(this, borrowerStorageCountryCode, borrowerStorageStateCode, borrowerStorageState);
        return borrowerStorageState;
    }

    /**
     * Sets the borrowerStorageState attribute.
     * 
     * @param borrowerStorageState The borrowerStorageState to set.
     */
    public void setBorrowerStorageState(State borrowerStorageState) {
        this.borrowerStorageState = borrowerStorageState;
    }

    /**
     * Gets the borrowerPerson attribute.
     * 
     * @return Returns the borrowerPerson
     */
    public Person getBorrowerPerson() {
        borrowerPerson = SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).updatePersonIfNecessary(borrowerUniversalIdentifier, borrowerPerson);
        return borrowerPerson;
    }

    /**
     * Sets the borrowerPerson attribute.
     * 
     * @param borrowerPerson The borrowerPerson to set.
     */
    public void setBorrowerPerson(Person borrowerPerson) {
        this.borrowerPerson = borrowerPerson;
    }

    /**
     * Gets the borrowerAddress attribute.
     * 
     * @return Returns the borrowerAddress
     */
    public String getBorrowerAddress() {
        return borrowerAddress;
    }

    /**
     * Sets the borrowerAddress attribute.
     * 
     * @param borrowerAddress The borrowerAddress to set.
     */
    public void setBorrowerAddress(String borrowerAddress) {
        this.borrowerAddress = borrowerAddress;
    }

    /**
     * Gets the borrowerCityName attribute.
     * 
     * @return Returns the borrowerCityName
     */
    public String getBorrowerCityName() {
        return borrowerCityName;
    }

    /**
     * Sets the borrowerCityName attribute.
     * 
     * @param borrowerCityName The borrowerCityName to set.
     */
    public void setBorrowerCityName(String borrowerCityName) {
        this.borrowerCityName = borrowerCityName;
    }

    /**
     * Gets the borrowerCountryCode attribute.
     * 
     * @return Returns the borrowerCountryCode
     */
    public String getBorrowerCountryCode() {
        return borrowerCountryCode;
    }

    /**
     * Sets the borrowerCountryCode attribute.
     * 
     * @param borrowerCountryCode The borrowerCountryCode to set.
     */
    public void setBorrowerCountryCode(String borrowerCountryCode) {
        this.borrowerCountryCode = borrowerCountryCode;
    }

    /**
     * Gets the borrowerPhoneNumber attribute.
     * 
     * @return Returns the borrowerPhoneNumber
     */
    public String getBorrowerPhoneNumber() {
        return borrowerPhoneNumber;
    }

    /**
     * Sets the borrowerPhoneNumber attribute.
     * 
     * @param borrowerPhoneNumber The borrowerPhoneNumber to set.
     */
    public void setBorrowerPhoneNumber(String borrowerPhoneNumber) {
        this.borrowerPhoneNumber = borrowerPhoneNumber;
    }

    /**
     * Gets the borrowerStateCode attribute.
     * 
     * @return Returns the borrowerStateCode
     */
    public String getBorrowerStateCode() {
        return borrowerStateCode;
    }

    /**
     * Sets the borrowerStateCode attribute.
     * 
     * @param borrowerStateCode The borrowerStateCode to set.
     */
    public void setBorrowerStateCode(String borrowerStateCode) {
        this.borrowerStateCode = borrowerStateCode;
    }

    /**
     * Gets the borrowerStorageAddress attribute.
     * 
     * @return Returns the borrowerStorageAddress
     */
    public String getBorrowerStorageAddress() {
        return borrowerStorageAddress;
    }

    /**
     * Sets the borrowerStorageAddress attribute.
     * 
     * @param borrowerStorageAddress The borrowerStorageAddress to set.
     */
    public void setBorrowerStorageAddress(String borrowerStorageAddress) {
        this.borrowerStorageAddress = borrowerStorageAddress;
    }

    /**
     * Gets the borrowerStorageCityName attribute.
     * 
     * @return Returns the borrowerStorageCityName
     */
    public String getBorrowerStorageCityName() {
        return borrowerStorageCityName;
    }

    /**
     * Sets the borrowerStorageCityName attribute.
     * 
     * @param borrowerStorageCityName The borrowerStorageCityName to set.
     */
    public void setBorrowerStorageCityName(String borrowerStorageCityName) {
        this.borrowerStorageCityName = borrowerStorageCityName;
    }

    /**
     * Gets the borrowerStorageCountryCode attribute.
     * 
     * @return Returns the borrowerStorageCountryCode
     */
    public String getBorrowerStorageCountryCode() {
        return borrowerStorageCountryCode;
    }

    /**
     * Sets the borrowerStorageCountryCode attribute.
     * 
     * @param borrowerStorageCountryCode The borrowerStorageCountryCode to set.
     */
    public void setBorrowerStorageCountryCode(String borrowerStorageCountryCode) {
        this.borrowerStorageCountryCode = borrowerStorageCountryCode;
    }

    /**
     * Gets the borrowerStoragePhoneNumber attribute.
     * 
     * @return Returns the borrowerStoragePhoneNumber
     */
    public String getBorrowerStoragePhoneNumber() {
        return borrowerStoragePhoneNumber;
    }

    /**
     * Sets the borrowerStoragePhoneNumber attribute.
     * 
     * @param borrowerStoragePhoneNumber The borrowerStoragePhoneNumber to set.
     */
    public void setBorrowerStoragePhoneNumber(String borrowerStoragePhoneNumber) {
        this.borrowerStoragePhoneNumber = borrowerStoragePhoneNumber;
    }

    /**
     * Gets the borrowerStorageStateCode attribute.
     * 
     * @return Returns the borrowerStorageStateCode
     */
    public String getBorrowerStorageStateCode() {
        return borrowerStorageStateCode;
    }

    /**
     * Sets the borrowerStorageStateCode attribute.
     * 
     * @param borrowerStorageStateCode The borrowerStorageStateCode to set.
     */
    public void setBorrowerStorageStateCode(String borrowerStorageStateCode) {
        this.borrowerStorageStateCode = borrowerStorageStateCode;
    }

    /**
     * Gets the borrowerStorageZipCode attribute.
     * 
     * @return Returns the borrowerStorageZipCode
     */
    public String getBorrowerStorageZipCode() {
        return borrowerStorageZipCode;
    }

    /**
     * Sets the borrowerStorageZipCode attribute.
     * 
     * @param borrowerStorageZipCode The borrowerStorageZipCode to set.
     */
    public void setBorrowerStorageZipCode(String borrowerStorageZipCode) {
        this.borrowerStorageZipCode = borrowerStorageZipCode;
    }

    /**
     * Gets the borrowerPostalZipCode attribute.
     * 
     * @return Returns the borrowerPostalZipCode
     */
    public PostalCode getBorrowerPostalZipCode() {
        borrowerPostalZipCode = SpringContext.getBean(PostalCodeService.class).getByPrimaryIdIfNecessary(this, borrowerCountryCode, borrowerZipCode, borrowerPostalZipCode);
        return borrowerPostalZipCode;
    }

    /**
     * Sets the borrowerPostalZipCode attribute.
     * 
     * @param borrowerPostalZipCode The borrowerPostalZipCode to set.
     */
    public void setBorrowerPostalZipCode(PostalCode borrowerPostalZipCode) {
        this.borrowerPostalZipCode = borrowerPostalZipCode;
    }

    /**
     * Sets the borrowerStoragePostalZipCode attribute.
     * 
     * @param borrowerStoragePostalZipCode The borrowerStoragePostalZipCode to set.
     */
    public PostalCode getBorrowerStoragePostalZipCode() {
        borrowerStoragePostalZipCode = SpringContext.getBean(PostalCodeService.class).getByPrimaryIdIfNecessary(this, borrowerStorageCountryCode, borrowerStorageZipCode, borrowerStoragePostalZipCode);
        return borrowerStoragePostalZipCode;
    }

    /**
     * Gets the borrowerStoragePostalZipCode attribute.
     * 
     * @return Returns the borrowerStoragePostalZipCode
     */
    public void setborrowerStoragePostalZipCode(PostalCode borrowerStoragePostalZipCode) {
        this.borrowerStoragePostalZipCode = borrowerStoragePostalZipCode;
    }

    /**
     * Gets the borrowerUniversalIdentifier attribute.
     * 
     * @return Returns the borrowerUniversalIdentifier
     */
    public String getBorrowerUniversalIdentifier() {
        return borrowerUniversalIdentifier;
    }

    /**
     * Sets the borrowerUniversalIdentifier attribute.
     * 
     * @param borrowerUniversalIdentifier The borrowerUniversalIdentifier to set.
     */
    public void setBorrowerUniversalIdentifier(String borrowerUniversalIdentifier) {
        this.borrowerUniversalIdentifier = borrowerUniversalIdentifier;
    }

    /**
     * Gets the borrowerZipCode attribute.
     * 
     * @return Returns the borrowerZipCode
     */
    public String getBorrowerZipCode() {
        return borrowerZipCode;
    }

    /**
     * Sets the borrowerZipCode attribute.
     * 
     * @param borrowerZipCode The borrowerZipCode to set.
     */
    public void setBorrowerZipCode(String borrowerZipCode) {
        this.borrowerZipCode = borrowerZipCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the expectedReturnDate attribute.
     * 
     * @return Returns the expectedReturnDate
     */
    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    /**
     * Sets the expectedReturnDate attribute.
     * 
     * @param expectedReturnDate The expectedReturnDate to set.
     */
    public void setExpectedReturnDate(Date expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    /**
     * Gets the loanDate attribute.
     * 
     * @return Returns the loanDate
     */
    public Date getLoanDate() {
        if (loanDate != null) {
            return loanDate;
        }
        else {
            return SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        }
    }

    /**
     * Sets the loanDate attribute.
     * 
     * @param loanDate The loanDate to set.
     */
    public void setLoanDate(Date loanDate) {
        this.loanDate = loanDate;
    }

    /**
     * Gets the loanReturnDate attribute.
     * 
     * @return Returns the loanReturnDate
     */
    public Date getLoanReturnDate() {
        return loanReturnDate;
    }

    /**
     * Sets the loanReturnDate attribute.
     * 
     * @param loanReturnDate The loanReturnDate to set.
     */
    public void setLoanReturnDate(Date loanReturnDate) {
        this.loanReturnDate = loanReturnDate;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#postProcessSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);

        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            MaintenanceDocumentService maintenanceDocumentService = SpringContext.getBean(MaintenanceDocumentService.class);
            AssetService assetService = SpringContext.getBean(AssetService.class);

            maintenanceDocumentService.deleteLocks(this.getDocumentNumber());

            List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
            maintenanceLocks.add(assetService.generateAssetLock(documentNumber, capitalAssetNumber));
            maintenanceDocumentService.storeLocks(maintenanceLocks);
        }
    }

    /**
     * If the document final, unlock the document
     * 
     * @see org.kuali.rice.kns.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();

        KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.stateIsProcessed()) {
            SpringContext.getBean(EquipmentLoanOrReturnService.class).processApprovedEquipmentLoanOrReturn(this);

            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(getDocumentNumber());
        }

        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {
            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(this.getDocumentNumber());
        }
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("documentNumber", this.documentNumber);
        return m;
    }

    /**
     * Gets the capitalAssetNumber attribute.
     * 
     * @return Returns the capitalAssetNumber
     */
    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    /**
     * Sets the capitalAssetNumber attribute.
     * 
     * @param capitalAssetNumber The capitalAssetNumber to set.
     */
    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public boolean isNewLoan() {
        return newLoan;
    }

    public void setNewLoan(boolean newLoan) {
        this.newLoan = newLoan;
    }

    public boolean isReturnLoan() {
        return returnLoan;
    }

    public void setReturnLoan(boolean returnLoan) {
        this.returnLoan = returnLoan;
    }

    /**
     * Gets the routingInfo attribute.
     * 
     * @return Returns the routingInfo.
     */
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Sets the routingInfo attribute value.
     * 
     * @param routingInfo The routingInfo to set.
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        if (KFSConstants.DocumentStatusCodes.INITIATED.equals(getDocumentHeader().getFinancialDocumentStatusCode())) {
            // skip routing info if document is not routed
            return;
        }
        routingInfo = new HashSet<RoutingData>();
        Set<OrgReviewRoutingData> organizationRoutingSet = new HashSet<OrgReviewRoutingData>();
        Set<RoutingAccount> accountRoutingSet = new HashSet<RoutingAccount>();
        Set<RoutingAssetNumber> assetNumberRoutingSet = new HashSet<RoutingAssetNumber>();
        Set<RoutingAssetTagNumber> assetTagNumberRoutingSet = new HashSet<RoutingAssetTagNumber>();

        // Asset information
        organizationRoutingSet.add(new OrgReviewRoutingData(this.getAsset().getOrganizationOwnerChartOfAccountsCode(), this.getAsset().getOrganizationOwnerAccount().getOrganizationCode()));
        accountRoutingSet.add(new RoutingAccount(this.getAsset().getOrganizationOwnerChartOfAccountsCode(), this.getAsset().getOrganizationOwnerAccountNumber()));
        assetNumberRoutingSet.add(new RoutingAssetNumber(this.getAsset().getCapitalAssetNumber().toString()));
        assetTagNumberRoutingSet.add(new RoutingAssetTagNumber(this.getAsset().getCampusTagNumber()));
        // Storing data
        RoutingData organizationRoutingData = new RoutingData();
        organizationRoutingData.setRoutingType(KualiOrgReviewAttribute.class.getSimpleName());
        organizationRoutingData.setRoutingSet(organizationRoutingSet);
        routingInfo.add(organizationRoutingData);

        RoutingData accountRoutingData = new RoutingData();
        accountRoutingData.setRoutingType(KualiAccountAttribute.class.getSimpleName());
        accountRoutingData.setRoutingSet(accountRoutingSet);
        routingInfo.add(accountRoutingData);

        RoutingData assetNumberRoutingData = new RoutingData();
        assetNumberRoutingData.setRoutingType(RoutingAssetNumber.class.getSimpleName());
        assetNumberRoutingData.setRoutingSet(assetNumberRoutingSet);
        routingInfo.add(assetNumberRoutingData);

        RoutingData assetTagNumberRoutingData = new RoutingData();
        assetTagNumberRoutingData.setRoutingType(RoutingAssetTagNumber.class.getSimpleName());
        assetTagNumberRoutingData.setRoutingSet(assetTagNumberRoutingSet);
        routingInfo.add(assetTagNumberRoutingData);

    }

}