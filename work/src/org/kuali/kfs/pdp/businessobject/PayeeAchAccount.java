/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.UniversalUserService;

public class PayeeAchAccount extends PersistableBusinessObjectBase {

    private Integer achAccountGeneratedIdentifier;
    private String bankRoutingNumber;
    private String bankAccountNumber;
    private String payeeName;
    private String payeeEmailAddress;
    private Integer vendorHeaderGeneratedIdentifier;
    private Integer vendorDetailAssignedIdentifier;
    private String personUniversalIdentifier;
    private String payeeSocialSecurityNumber;
    private String payeeFederalEmployerIdentificationNumber;
    private String payeeIdentifierTypeCode;
    private String psdTransactionCode;
    private boolean active;
    private String bankAccountTypeCode;

    private AchBank bankRouting;
    private VendorDetail vendorDetail;
    private UniversalUser user;

    /**
     * Default constructor.
     */
    public PayeeAchAccount() {

    }

    /**
     * Gets the achAccountGeneratedIdentifier attribute.
     * 
     * @return Returns the achAccountGeneratedIdentifier
     */
    public Integer getAchAccountGeneratedIdentifier() {
        return achAccountGeneratedIdentifier;
    }

    /**
     * Sets the achAccountGeneratedIdentifier attribute.
     * 
     * @param achAccountGeneratedIdentifier The achAccountGeneratedIdentifier to set.
     */
    public void setAchAccountGeneratedIdentifier(Integer achAccountGeneratedIdentifier) {
        this.achAccountGeneratedIdentifier = achAccountGeneratedIdentifier;
    }


    /**
     * Gets the bankRoutingNumber attribute.
     * 
     * @return Returns the bankRoutingNumber
     */
    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }

    /**
     * Sets the bankRoutingNumber attribute.
     * 
     * @param bankRoutingNumber The bankRoutingNumber to set.
     */
    public void setBankRoutingNumber(String bankRoutingNumber) {
        this.bankRoutingNumber = bankRoutingNumber;
    }


    /**
     * Gets the bankAccountNumber attribute.
     * 
     * @return Returns the bankAccountNumber
     */
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    /**
     * Sets the bankAccountNumber attribute.
     * 
     * @param bankAccountNumber The bankAccountNumber to set.
     */
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }


    /**
     * Gets the payeeName attribute.
     * 
     * @return Returns the payeeName
     */
    public String getPayeeName() {
        return payeeName;
    }

    /**
     * Sets the payeeName attribute.
     * 
     * @param payeeName The payeeName to set.
     */
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }


    /**
     * Gets the payeeEmailAddress attribute.
     * 
     * @return Returns the payeeEmailAddress
     */
    public String getPayeeEmailAddress() {
        return payeeEmailAddress;
    }

    /**
     * Sets the payeeEmailAddress attribute.
     * 
     * @param payeeEmailAddress The payeeEmailAddress to set.
     */
    public void setPayeeEmailAddress(String payeeEmailAddress) {
        this.payeeEmailAddress = payeeEmailAddress;
    }


    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @return Returns the vendorHeaderGeneratedIdentifier
     */
    public Integer getVendorHeaderGeneratedIdentifier() {
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute.
     * 
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     */
    public void setVendorHeaderGeneratedIdentifier(Integer vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }


    /**
     * Gets the vendorDetailAssignedIdentifier attribute.
     * 
     * @return Returns the vendorDetailAssignedIdentifier
     */
    public Integer getVendorDetailAssignedIdentifier() {
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute.
     * 
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     */
    public void setVendorDetailAssignedIdentifier(Integer vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    /**
     * Gets the personUniversalIdentifier attribute.
     * 
     * @return Returns the personUniversalIdentifier
     */
    public String getPersonUniversalIdentifier() {
        return personUniversalIdentifier;
    }

    /**
     * Sets the personUniversalIdentifier attribute.
     * 
     * @param personUniversalIdentifier The personUniversalIdentifier to set.
     */
    public void setPersonUniversalIdentifier(String personUniversalIdentifier) {
        this.personUniversalIdentifier = personUniversalIdentifier;
    }


    /**
     * Gets the payeeSocialSecurityNumber attribute.
     * 
     * @return Returns the payeeSocialSecurityNumber
     */
    public String getPayeeSocialSecurityNumber() {
        return payeeSocialSecurityNumber;
    }

    /**
     * Sets the payeeSocialSecurityNumber attribute.
     * 
     * @param payeeSocialSecurityNumber The payeeSocialSecurityNumber to set.
     */
    public void setPayeeSocialSecurityNumber(String payeeSocialSecurityNumber) {
        this.payeeSocialSecurityNumber = payeeSocialSecurityNumber;
    }


    /**
     * Gets the payeeFederalEmployerIdentificationNumber attribute.
     * 
     * @return Returns the payeeFederalEmployerIdentificationNumber
     */
    public String getPayeeFederalEmployerIdentificationNumber() {
        return payeeFederalEmployerIdentificationNumber;
    }

    /**
     * Sets the payeeFederalEmployerIdentificationNumber attribute.
     * 
     * @param payeeFederalEmployerIdentificationNumber The payeeFederalEmployerIdentificationNumber to set.
     */
    public void setPayeeFederalEmployerIdentificationNumber(String payeeFederalEmployerIdentificationNumber) {
        this.payeeFederalEmployerIdentificationNumber = payeeFederalEmployerIdentificationNumber;
    }


    /**
     * Gets the payeeIdentifierTypeCode attribute.
     * 
     * @return Returns the payeeIdentifierTypeCode
     */
    public String getPayeeIdentifierTypeCode() {
        return payeeIdentifierTypeCode;
    }

    /**
     * Sets the payeeIdentifierTypeCode attribute.
     * 
     * @param payeeIdentifierTypeCode The payeeIdentifierTypeCode to set.
     */
    public void setPayeeIdentifierTypeCode(String payeeIdentifierTypeCode) {
        this.payeeIdentifierTypeCode = payeeIdentifierTypeCode;
    }


    /**
     * Gets the psdTransactionCode attribute.
     * 
     * @return Returns the psdTransactionCode
     */
    public String getPsdTransactionCode() {
        return psdTransactionCode;
    }

    /**
     * Sets the psdTransactionCode attribute.
     * 
     * @param psdTransactionCode The psdTransactionCode to set.
     */
    public void setPsdTransactionCode(String psdTransactionCode) {
        this.psdTransactionCode = psdTransactionCode;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the bankAccountTypeCode attribute.
     * 
     * @return Returns the bankAccountTypeCode.
     */
    public String getBankAccountTypeCode() {
        return bankAccountTypeCode;
    }

    /**
     * Sets the bankAccountTypeCode attribute value.
     * 
     * @param bankAccountTypeCode The bankAccountTypeCode to set.
     */
    public void setBankAccountTypeCode(String bankAccountTypeCode) {
        this.bankAccountTypeCode = bankAccountTypeCode;
    }

    /**
     * Gets the bankRouting attribute.
     * 
     * @return Returns the bankRouting.
     */
    public AchBank getBankRouting() {
        return bankRouting;
    }

    /**
     * Sets the bankRouting attribute value.
     * 
     * @param bankRouting The bankRouting to set.
     * @deprecated
     */
    public void setBankRouting(AchBank bankRouting) {
        this.bankRouting = bankRouting;
    }
    
    /**
     * Gets the user attribute.
     * 
     * @return Returns the user.
     */
    public UniversalUser getUser() {
        user = SpringContext.getBean(UniversalUserService.class).updateUniversalUserIfNecessary(personUniversalIdentifier, user);
        return user;
    }

    /**
     * Sets the user attribute value.
     * 
     * @param user The user to set.
     */
    public void setUser(UniversalUser user) {
        this.user = user;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.achAccountGeneratedIdentifier != null) {
            m.put("achAccountGeneratedIdentifier", this.achAccountGeneratedIdentifier.toString());
        }
        return m;
    }

    public VendorDetail getVendorDetail() {
        return vendorDetail;
    }

    public void setVendorDetail(VendorDetail vendorDetail) {
        this.vendorDetail = vendorDetail;
    }

    public String getVendorNumber() {
        // using the code from the VendorDetail to generate the vendor number
        VendorDetail vDUtil = new VendorDetail();
        vDUtil.setVendorHeaderGeneratedIdentifier(vendorHeaderGeneratedIdentifier);
        vDUtil.setVendorDetailAssignedIdentifier(vendorDetailAssignedIdentifier);
        return vDUtil.getVendorNumber();
    }

    public void setVendorNumber(String vendorNumber) {
        // using the code from the VendorDetail to set the 2 component fields of the vendor number
        VendorDetail vDUtil = new VendorDetail();
        vDUtil.setVendorNumber(vendorNumber);
        setVendorHeaderGeneratedIdentifier(vDUtil.getVendorHeaderGeneratedIdentifier());
        setVendorDetailAssignedIdentifier(vDUtil.getVendorDetailAssignedIdentifier());
    }
}
