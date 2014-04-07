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
package org.kuali.kfs.module.tem.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TemProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.pdp.businessobject.PayeeACHAccount;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class TemProfileServiceImpl implements TemProfileService {

    private BusinessObjectService businessObjectService;
    private PersonService personService;

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfileByPrincipalId(java.lang.String)
     */
    @Override
    public TemProfile findTemProfileByPrincipalId(String principalId) {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemProfileProperties.PRINCIPAL_ID, principalId);
        return findTemProfile(criteria);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfileById(java.lang.Integer)
     */
    @Override
    public TemProfile findTemProfileById(Integer profileId) {
        Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemProfileProperties.PROFILE_ID, String.valueOf(profileId));
        return findTemProfile(criteria);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfile(java.util.Map)
     */
    @Override
    public TemProfile findTemProfile(Map<String, String> criteria) {
        Collection<TemProfile> profiles = getBusinessObjectService().findMatching(TemProfile.class, criteria);
        if(ObjectUtils.isNotNull(profiles) && profiles.size() > 0) {
            return profiles.iterator().next();
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#getAddressFromProfile(org.kuali.kfs.module.tem.businessobject.TemProfile, org.kuali.kfs.module.tem.businessobject.TemProfileAddress)
     */
    @Override
    public TemProfileAddress getAddressFromProfile(TemProfile profile, TemProfileAddress defaultAddress) {

        if(ObjectUtils.isNull(defaultAddress)) {
        	defaultAddress = new TemProfileAddress();
        }

        if (!StringUtils.isEmpty(profile.getPrincipalId())) {
            Person person = getPersonService().getPerson(profile.getPrincipalId());
            TemProfileAddress kimAddress = createTemProfileAddressFromPerson(person, profile.getProfileId(), defaultAddress);
            return kimAddress;
        }
        return defaultAddress;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#createTemProfileAddressFromPerson(org.kuali.rice.kim.bo.Person, java.lang.Integer, org.kuali.kfs.module.tem.businessobject.TemProfileAddress)
     */
    @Override
    public TemProfileAddress createTemProfileAddressFromPerson(Person person, Integer profileId, TemProfileAddress defaultAddress) {
        defaultAddress.setProfileId(profileId);
        defaultAddress.setStreetAddressLine1(StringUtils.upperCase(person.getAddressLine1()));
        defaultAddress.setStreetAddressLine2(StringUtils.upperCase(person.getAddressLine2()));
        defaultAddress.setCityName(StringUtils.upperCase(person.getAddressCity()));
        defaultAddress.setStateCode(StringUtils.upperCase(person.getAddressStateProvinceCode()));
        defaultAddress.setZipCode(person.getAddressPostalCode());
        defaultAddress.setCountryCode(StringUtils.upperCase(person.getAddressCountryCode()));
        return defaultAddress;
    }

	/**
	 * @see org.kuali.kfs.module.tem.service.TemProfileService#getAllActiveTemProfile()
	 */
	@Override
	public List<TemProfile> getAllActiveTemProfile() {
		Map<String,Object> criteria = new HashMap<String,Object>(3);
        criteria.put(KFSPropertyConstants.ACTIVE, true);
		List<TemProfile> profiles = (List<TemProfile>) getBusinessObjectService().findMatching(TemProfile.class, criteria);
		return profiles;
	}

	/**
	 * @see org.kuali.kfs.module.tem.service.TemProfileService#updateACHAccountInfo(org.kuali.kfs.module.tem.businessobject.TemProfile)
	 */
	@Override
    public void updateACHAccountInfo(TemProfile profile){

	    //set defaults
        profile.setAchSignUp("No");
        profile.setAchTransactionType("None");

        if (TemConstants.EMP_TRAVELER_TYP_CD.equals(profile.getTravelerTypeCode()) &&
                profile.getEmployeeId() != null) {
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KFSPropertyConstants.PAYEE_ID_NUMBER, profile.getEmployeeId());
            List<PayeeACHAccount> accounts = (List<PayeeACHAccount>) getBusinessObjectService().findMatching(PayeeACHAccount.class, fieldValues);

            //if there are any ACH accounts matching the employee Id lookup, use the first one for display
            if (!accounts.isEmpty()){
                profile.setAchSignUp("Yes");
                profile.setAchTransactionType(accounts.get(0).getAchTransactionType());
            }
        }
	}

	/**
	 * @see org.kuali.kfs.module.tem.service.TemProfileService#isProfileNonEmploye(org.kuali.kfs.module.tem.businessobject.TemProfile)
	 */
    @Override
    public boolean isProfileNonEmploye(TemProfile profile) {
        return !StringUtils.isBlank(profile.getTravelerTypeCode()) && profile.getTravelerTypeCode().equals(TemConstants.NONEMP_TRAVELER_TYP_CD);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#hasActiveArrangers(org.kuali.kfs.module.tem.businessobject.TemProfile)
     */
    @Override
    public boolean hasActiveArrangers(TemProfile profile) {
        for (TemProfileArranger arranger : profile.getArrangers()) {
            if (arranger.isActive()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfileByEmployeeId(java.lang.String)
     */
    @Override
    public TemProfile findTemProfileByEmployeeId(String employeeId) {
        final Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemProfileProperties.EMPLOYEE_ID, employeeId);
        return findTemProfile(criteria);
    }

    /**
     * @see org.kuali.kfs.module.tem.service.TemProfileService#findTemProfileByCustomerNumber(java.lang.String)
     */
    @Override
    public TemProfile findTemProfileByCustomerNumber(String customerNumber) {
        final Map<String,String> criteria = new HashMap<String,String>(1);
        criteria.put(TemProfileProperties.CUSTOMER_NUMBER, customerNumber);
        return findTemProfile(criteria);
    }

    /**
     * Easily overridable to add more values
     * @see org.kuali.kfs.module.tem.service.TemProfileService#getGenderKeyValues()
     */
    @Override
    public List<KeyValue> getGenderKeyValues() {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("M", "Male"));
        keyValues.add(new ConcreteKeyValue("F", "Female"));
        return keyValues;
    }

    /**
     * Checks the business object service to see if the profile account exists
     * @see org.kuali.kfs.module.tem.service.TemProfileService#doesProfileAccountExist(org.kuali.kfs.module.tem.businessobject.TemProfileAccount, org.kuali.kfs.module.tem.businessobject.TemProfile)
     */
    @Override
    public boolean doesProfileAccountExist(TemProfileAccount account, TemProfile skipProfile) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, account.getCreditCardOrAgencyCode());
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        final Collection<TemProfileAccount> profileAccounts = getBusinessObjectService().findMatching(TemProfileAccount.class, fieldValues);
        if (skipProfile != null && skipProfile.getId() != null) {
            List<TemProfileAccount> otherFolksAccounts = new ArrayList<TemProfileAccount>();
            for (TemProfileAccount profileAccount : profileAccounts) { // loop through all accounts to exhaust any iterator
                if (profileAccount.getProfileId().equals(skipProfile.getId())) {
                    otherFolksAccounts.add(profileAccount);
                }
            }
            return otherFolksAccounts.size() > 0;
        }

        //otherwise, just return the size of the profileAccounts
        return profileAccounts.size() > 0;
    }

    /**
     * Gets the personService attribute.
     * @return Returns the personService.
     */
    public PersonService getPersonService() {
        return personService;
    }

    /**
     * Sets the personService attribute value.
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
