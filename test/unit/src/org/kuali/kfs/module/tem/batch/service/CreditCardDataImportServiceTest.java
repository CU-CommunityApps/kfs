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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardAgency;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.SequenceAccessorService;

@ConfigureContext
public class CreditCardDataImportServiceTest extends KualiTestBase{
    private CreditCardDataImportService creditCardImportService;
    private SequenceAccessorService sas;
    private DateTimeService dateTimeService;
    private BusinessObjectService businessObjectService;
    
    private final static String EMPLOYEE_ID = "123456789";
    private final static String VISA_CREDIT_CARD_NUMBER = "4222222222222";
    private final static String MASTER_CREDIT_CARD_NUMBER = "5555555555554444";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }
    
    /**
     * 
     * This method tests {@link CreditCardDataImportService#validateCreditCardData(List<CreditCardStagingData>, dataFileName)}
     */
    @Test
    @ConfigureContext(shouldCommitTransactions = false)
    public void validateCreditCardData() {
        /*TEMProfile profile = createTemProfile();
        businessObjectService.save(profile);
        
        CreditCardStagingData creditCardData = new CreditCardStagingData();
        creditCardData.setId(1);
        creditCardData.setAirNumber("13");
        creditCardData.setCreditCardNumber(VISA_CREDIT_CARD_NUMBER);
        assertEquals(VISA_CREDIT_CARD_NUMBER, creditCardData.getCreditCardNumber());*/
    }
    
    protected TEMProfile createTemProfile() {
        TEMProfile profile = new TEMProfile();
        /*TEMProfileAccount temAccount = new TEMProfileAccount();
        List<TEMProfileAccount> accounts = new ArrayList<TEMProfileAccount>();
        
        CreditCardAgency ccAgency = new CreditCardAgency();
        ccAgency.setCreditCardOrAgencyCode(sas.getNextAvailableSequenceNumber(TemConstants.TEM_CREDIT_CARD_AGENCY_SEQ_NAME).intValue());
        ccAgency.setPaymentIndicator(true);
        ccAgency.setCreditCardTypeCode("VM");
        ccAgency.setObjectId("1234567890011212");
        ccAgency.setCardId("VIS1");
       
        
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
        
        temAccount.setAccountId(sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_ACCOUNT_SEQ_NAME).intValue());
        temAccount.setProfileId(newProfileId);
        temAccount.setAccountNumber(VISA_CREDIT_CARD_NUMBER);
        
        accounts.add(temAccount);        
        profile.setAccounts(accounts);*/
        
        return profile;
    }

}
