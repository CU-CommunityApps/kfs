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
package org.kuali.kfs.module.endow.document.service;

import java.util.Collection;
import java.util.Date;

import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * MonthEndDateService interface to provide the method to get month end date id
 */
public interface MonthEndDateService {

    /**
     * gets month end id
     * 
     * @param monthEndDate
     * @return monthEndDateId
     */
    public KualiInteger getMonthEndId(Date monthEndDate);
    
    
    /**
     * gets the next month end id for the new record
     * 
     * @return monthEndDateId
     */
    public KualiInteger getNextMonthEndIdForNewRecord();
    
}
