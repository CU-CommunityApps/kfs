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
package org.kuali.kfs.module.tem.batch.service;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImportTypes;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAddress;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

@ConfigureContext
public class ExpenseImportByTripServiceTest extends KualiTestBase {
    
    private ExpenseImportByTripService expenseImportByTripService;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    private SequenceAccessorService sas;
    private ParameterService parameterService;
    
    private static final String TRIP_ID = "12345678";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        expenseImportByTripService = SpringContext.getBean(ExpenseImportByTripService.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        sas = SpringContext.getBean(SequenceAccessorService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
    }
    
    
    /**
     * 
     * This method tests {@link ExpenseImportByTripService#validateAccountingInfo(TEMProfile, AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testValidateAccountingInfo() {
        AgencyStagingData agency = createAgencyStagingData();
        TEMProfile profile = createTemProfile();
        TravelAuthorizationDocument ta = createTA();
        
        // success case
        agency = expenseImportByTripService.validateAccountingInfo(profile, agency, ta);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR));

        TripAccountingInformation accountingInfo = agency.getTripAccountingInformation().get(0);
        
        // test with an invalid account
        accountingInfo.setTripAccountNumber("");
        agency = expenseImportByTripService.validateAccountingInfo(profile, agency, ta);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT));
        
        // test with an invalid sub-account
        profile = createTemProfile();
        accountingInfo.setTripSubAccountNumber("ZZ");
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        agency = expenseImportByTripService.validateAccountingInfo(profile, agency, ta);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT));
        
        // test with an invalid project code
        profile = createTemProfile();
        accountingInfo.setProjectCode("COOL");
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        agency = expenseImportByTripService.validateAccountingInfo(profile, agency, ta);
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT));

    }
    
    /**
     * 
     * This method tests {@link ExpenseImportByTripService#validateTripId(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testValidateTripId() throws Exception {
        
        // invalid trip id
        AgencyStagingData agency = createAgencyStagingData();
        TravelDocument invalidTa = expenseImportByTripService.validateTripId(agency);
        assertTrue(ObjectUtils.isNull(invalidTa));
        assertTrue(agency.getErrorCode().equals(AgencyStagingDataErrorCodes.AGENCY_INVALID_TRIPID));
    }
    
 
    /**
     * 
     * This method tests {@link ExpenseImportByTripService#isDuplicate(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testIsDuplicate() {
        AgencyStagingData dbData = createAgencyStagingData();
        businessObjectService.save(dbData);
        
        // duplicate entry test
        AgencyStagingData importData = createAgencyStagingData();
        assertTrue(expenseImportByTripService.isDuplicate(importData));

        // not a duplicate
        importData.setTripId("987654321");
        assertFalse(expenseImportByTripService.isDuplicate(importData));
    }
    
    /**
     * 
     * This method tests {@link ExpenseImportByTripService#areMandatoryFieldsPresent(AgencyStagingData)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void testAreMandatoryFieldsPresent() {
        AgencyStagingData agency = createAgencyStagingData();
        // all fields present
        assertTrue(expenseImportByTripService.areMandatoryFieldsPresent(agency));

        // missing fields, testing in reverse order of the if block to hit all possible checks
        agency.setAirTicketNumber("");
        assertFalse(expenseImportByTripService.areMandatoryFieldsPresent(agency));
        agency.setTripInvoiceNumber("");
        assertFalse(expenseImportByTripService.areMandatoryFieldsPresent(agency));
        agency.setTripExpenseAmount("");
        assertFalse(expenseImportByTripService.areMandatoryFieldsPresent(agency));
        agency.getTripAccountingInformation().get(0).setTripAccountNumber("");
        assertFalse(expenseImportByTripService.areMandatoryFieldsPresent(agency));
        agency.setTransactionPostingDate(null);
        assertFalse(expenseImportByTripService.areMandatoryFieldsPresent(agency));
        agency.setAlternateTripId(null);
        assertFalse(expenseImportByTripService.areMandatoryFieldsPresent(agency));
        agency.setTripId("");
        assertFalse(expenseImportByTripService.areMandatoryFieldsPresent(agency));
        agency.setCreditCardOrAgencyCode("");
        assertFalse(expenseImportByTripService.areMandatoryFieldsPresent(agency));
    }

    protected TEMProfile createTemProfile() {
        TEMProfile profile = new TEMProfile();
        Integer newProfileId = sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_SEQ_NAME).intValue();
        profile.setProfileId(newProfileId);
        profile.getTemProfileAddress().setProfileId(newProfileId);
        profile.setDefaultChartCode("BL");
        profile.setDefaultAccount("1031400");
        profile.setDefaultSubAccount("ADV");
        profile.setDefaultProjectCode("KUL");
        profile.setDateOfBirth(dateTimeService.getCurrentSqlDate());
        profile.setGender("M");
        profile.setHomeDeptOrgCode("BL");
        profile.setHomeDeptChartOfAccountsCode("BL");
        return profile;
    }
    
    protected AgencyStagingData createAgencyStagingData() {
        AgencyStagingData agency = new AgencyStagingData();
        
        agency.setImportBy(ExpenseImportTypes.IMPORT_BY_TRIP);
        agency.setTravelerName("Traveler Bob");
        agency.setCreditCardOrAgencyCode("1234");
        agency.setTripId(TRIP_ID);
        agency.setAlternateTripId("12345678");
        agency.setTransactionPostingDate(dateTimeService.getCurrentSqlDate());
        agency.setTripExpenseAmount(new KualiDecimal(123.45));
        agency.setTripInvoiceNumber("invoice12345");
        agency.setAirTicketNumber("12345678");
        
        TripAccountingInformation account = new TripAccountingInformation();
        account.setTripChartCode("BL");
        account.setTripAccountNumber("1031400");
        agency.addTripAccountingInformation(account);
        
        agency.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        return agency;
    }
    
    protected TravelAuthorizationDocument createTA() {
        TravelAuthorizationDocument ta = new TravelAuthorizationDocument();
        ta.setTravelDocumentIdentifier(TRIP_ID);
        SourceAccountingLine line = new SourceAccountingLine();
        line.setAccountNumber("1031400");
        line.setSubAccountNumber("ADV");
        line.setFinancialObjectCode("6000");
        
        return ta;
    }
}
