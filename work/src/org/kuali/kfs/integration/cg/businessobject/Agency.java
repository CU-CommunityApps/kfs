/*
 * Copyright 2005-2009 The Kuali Foundation
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

package org.kuali.kfs.integration.cg.businessobject;

import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class defines an agency as it is used and referenced within the Contracts and Grants portion of a college or university
 * financial system.
 */
public class Agency implements ContractsAndGrantsBillingAgency, MutableInactivatable {

    private String agencyNumber;
    private String reportingName;
    private String fullName;
    private String agencyTypeCode;
    private String reportsToAgencyNumber;
    private KualiDecimal indirectAmount;
    private boolean inStateIndicator;
    private Agency reportsToAgency;
    private String customerNumber;
    private String customerTypeCode;
    private String dunsPlusFourNumber;
    private boolean active;
    private AccountsReceivableCustomer customer;
    private boolean stateAgencyIndicator;

    /**
     * Gets the agencyTypeCode attribute.
     *
     * @return Returns the agencyTypeCode.
     */
    public String getAgencyTypeCode() {
        return agencyTypeCode;
    }

    /**
     * Sets the agencyTypeCode attribute value.
     *
     * @param agencyTypeCode The agencyTypeCode to set.
     */
    public void setAgencyTypeCode(String agencyTypeCode) {
        this.agencyTypeCode = agencyTypeCode;
    }

    /**
     * Gets the reportsToAgencyNumber attribute.
     *
     * @return Returns the reportsToAgencyNumber.
     */
    public String getReportsToAgencyNumber() {
        return reportsToAgencyNumber;
    }

    /**
     * Sets the reportsToAgencyNumber attribute value.
     *
     * @param reportsToAgencyNumber The reportsToAgencyNumber to set.
     */
    public void setReportsToAgencyNumber(String reportsToAgencyNumber) {
        this.reportsToAgencyNumber = reportsToAgencyNumber;
    }

    /**
     * Default no-arg constructor.
     */
    public Agency() {
    }

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber
     */
    @Override
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     *
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the reportingName attribute.
     *
     * @return Returns the reportingName
     */
    @Override
    public String getReportingName() {
        return reportingName;
    }

    /**
     * Sets the reportingName attribute.
     *
     * @param reportingName The reportingName to set.
     */
    public void setReportingName(String reportingName) {
        this.reportingName = reportingName;
    }

    /**
     * Gets the fullName attribute.
     *
     * @return Returns the fullName
     */
    @Override
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the fullName attribute.
     *
     * @param fullName The fullName to set.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Gets the indirectAmount attribute.
     *
     * @return Returns the indirectAmount
     */
    public KualiDecimal getIndirectAmount() {
        return indirectAmount;
    }

    /**
     * Sets the indirectAmount attribute.
     *
     * @param indirectAmount The indirectAmount to set.
     */
    public void setIndirectAmount(KualiDecimal indirectAmount) {
        this.indirectAmount = indirectAmount;
    }

    /**
     * Gets the inStateIndicator attribute.
     *
     * @return Returns the inStateIndicator
     */
    public boolean isInStateIndicator() {
        return inStateIndicator;
    }

    /**
     * Sets the inStateIndicator attribute.
     *
     * @param inStateIndicator The inStateIndicator to set.
     */
    public void setInStateIndicator(boolean inStateIndicator) {
        this.inStateIndicator = inStateIndicator;
    }

    /**
     * Gets the reportsToAgency attribute.
     *
     * @return Returns the reportsToAgency
     */
    public Agency getReportsToAgency() {
        return reportsToAgency;
    }

    /**
     * Sets the reportsToAgency attribute.
     *
     * @param reportsToAgencyNumber The reportsToAgency to set.
     * @deprecated
     * @todo Why is this deprecated?
     */
    @Deprecated
    public void setReportsToAgency(Agency reportsToAgencyNumber) {
        this.reportsToAgency = reportsToAgencyNumber;
    }

    /**
     * This method compares the passed in agency object against this agency object to check for equality. Equality is defined by if
     * the agency passed in has the same agency number as the agency being compared to.
     *
     * @param agency The agency object to be compared.
     * @return True if the agency passed in is determined to be equal, false otherwise.
     */
    public boolean equals(Agency agency) {
        return this.agencyNumber.equals(agency.getAgencyNumber());
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber.
     */

    @Override
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
     * Sets the customerTypeCode attribute value.
     *
     * @param customerTypeCode The customerTypeCode to set.
     */
    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

    /**
     * Gets the customerTypeCode attribute.
     *
     * @return Returns the customerTypeCode.
     */

    @Override
    public String getCustomerTypeCode() {
        return customerTypeCode;
    }

    /**
     * Gets the dunsPlusFourNumber attribute.
     *
     * @return Returns the dunsPlusFourNumber.
     */

    @Override
    public String getDunsPlusFourNumber() {
        return dunsPlusFourNumber;
    }

    /**
     * Sets the dunsPlusFourNumber attribute value.
     *
     * @param dunsPlusFourNumber The dunsPlusFourNumber to set.
     */
    public void setDunsPlusFourNumber(String dunsPlusFourNumber) {
        this.dunsPlusFourNumber = dunsPlusFourNumber;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency#getCustomer()
     */

    @Override
    public AccountsReceivableCustomer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute.
     *
     * @param customer The customer object to set.
     */
    public void setCustomer(AccountsReceivableCustomer customer) {
        this.customer = customer;
    }

    /**
     * Gets the stateAgencyIndicator attribute.
     *
     * @return Returns the stateAgencyIndicator.
     */

    @Override
    public boolean isStateAgencyIndicator() {
        return stateAgencyIndicator;
    }

    /**
     * Sets the stateAgencyIndicator attribute value.
     *
     * @param stateAgencyIndicator The stateAgencyIndicator to set.
     */
    public void setStateAgencyIndicator(boolean stateAgencyIndicator) {
        this.stateAgencyIndicator = stateAgencyIndicator;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#prepareForWorkflow()
     */
    public void prepareForWorkflow() {
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#refresh()
     */
    @Override
    public void refresh() {
    }

    @Override
    public List<? extends ContractsAndGrantsAgencyAddress> getAgencyAddresses() {
        return null;
    }
}
