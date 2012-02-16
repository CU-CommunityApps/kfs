/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service;

import java.util.List;

import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * Travel Service
 */
public interface TravelService {
    
    void setBusinessObjectService(BusinessObjectService businessObjectService);
    
    /**
     * Validate a phone number
     *
     * @param phoneNumber to validate
     */
    String validatePhoneNumber(final String phoneNumber, String error);

    /**
     * Validate a phone number
     * 
     * @param countryCode to consider for validation
     * @param phoneNumber to validate
     */
    String validatePhoneNumber(final String countryCode, final String phoneNumber, String error);
    
    /**
     * 
     * This method returns a TEMProfile associated with the principalId.
     * @param principalId
     * @return
     */
    public TEMProfile findTemProfileByPrincipalId(String principalId);
    
    
    public List<PrimaryDestination> findAllDistinctPrimaryDestinations(String tripType);
    
    public List findDefaultPrimaryDestinations(Class clazz, String countryCode);
}