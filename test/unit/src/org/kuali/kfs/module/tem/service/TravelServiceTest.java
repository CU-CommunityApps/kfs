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
package org.kuali.kfs.module.tem.service;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerType;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.PhoneNumberValidationPattern;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;

/**
 *
 * This class tests the TravelService class
 */
@ConfigureContext
public class TravelServiceTest extends KualiTestBase{

    private TravelService travelService;
    private DateTimeService dateTimeService;
    private static final int ONE_DAY = 86400;
    private BusinessObjectService businessObjectService;
    private SequenceAccessorService sas;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        final TravelService travelServiceTemp = SpringContext.getBean(TravelService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        sas = SpringContext.getBean(SequenceAccessorService.class);
        travelService = SpringContext.getBean(TravelService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    /**
     *
     * This method tests {@link TravelService#validatePhoneNumber(String)}
     */
    @Test
    public void testValidatePhoneNumber_byPhoneNumber() {
        assertTrue(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber(null, TemKeyConstants.ERROR_PHONE_NUMBER)));
        assertFalse(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber("123-555-1234", TemKeyConstants.ERROR_PHONE_NUMBER)));
        assertFalse(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber("123-555-1234 x1234", TemKeyConstants.ERROR_PHONE_NUMBER)));
    }

    /**
     *
     * This method tests {@link TravelService#validatePhoneNumber(String, String)}
     */
    @Test
    public void testValidatePhoneNumber_byCountryCodeAndPhoneNumber() {
        // validate International phone numbers
        assertTrue(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber(null,null,TemKeyConstants.ERROR_PHONE_NUMBER)));
        assertFalse(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber("UK","555-1234",TemKeyConstants.ERROR_PHONE_NUMBER)));
        assertFalse(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber("UK","555-1234 x1234",TemKeyConstants.ERROR_PHONE_NUMBER)));

        // validate US phone numbers
        PhoneNumberValidationPattern pattern = new PhoneNumberValidationPattern();
        assertTrue(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber("US","1",TemKeyConstants.ERROR_PHONE_NUMBER)));
        assertFalse(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber("US","123-555-1234",TemKeyConstants.ERROR_PHONE_NUMBER)));
        assertFalse(TemKeyConstants.ERROR_PHONE_NUMBER.equals(travelService.validatePhoneNumber("US","123-555-1234 x1234",TemKeyConstants.ERROR_PHONE_NUMBER)));
    }

    /**
     *
     * This method tests {@link TravelService#findTemProfileByPrincipalId(String)}
     */
    @Test
    public void testFindTemProfileByPrincipalId() {
        TemProfile profile = new TemProfile();
        Integer newProfileId = sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_SEQ_NAME).intValue();
        profile.setProfileId(newProfileId);
        profile.getTemProfileAddress().setProfileId(newProfileId);
        profile.setCustomerNumber("555555555");
        profile.setPrincipalId("66666666");
        profile.setDateOfBirth(new Date(Date.parse("03/03/1975")));
        profile.setCitizenship("United States");
        profile.setDriversLicenseExpDate(new Date(Date.parse("03/03/2014")));
        profile.setDriversLicenseNumber("B43212345");
        profile.setUpdatedBy("jamey");
        profile.setLastUpdate(new Date(Date.parse("03/03/2011")));
        profile.setGender("M");
        profile.setNonResidentAlien(false);
        profile.setHomeDeptChartOfAccountsCode("UA");
        profile.setHomeDeptOrgCode("VPIT");

        List<TravelerType> travelerTypes = (List<TravelerType>) businessObjectService.findMatching(TravelerType.class, new HashMap<String, Object>());
        profile.setTravelerType(travelerTypes.get(0));
        profile.setTravelerTypeCode(profile.getTravelerType().getCode());
        profile.setProfileId(-1);
        businessObjectService.save(profile);

        profile = travelService.findTemProfileByPrincipalId("-1");
        assertNull(profile);

        profile = travelService.findTemProfileByPrincipalId("66666666");
        assertNotNull(profile);
    }

}
