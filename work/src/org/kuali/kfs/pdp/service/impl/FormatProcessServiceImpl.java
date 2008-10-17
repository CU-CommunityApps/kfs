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
package org.kuali.kfs.pdp.service.impl;

import java.util.Date;

import org.kuali.kfs.pdp.businessobject.FormatProcess;
import org.kuali.kfs.pdp.businessobject.FormatSelection;
import org.kuali.kfs.pdp.dataaccess.CustomerProfileDao;
import org.kuali.kfs.pdp.dataaccess.DisbursementNumberRangeDao;
import org.kuali.kfs.pdp.dataaccess.FormatProcessDao;
import org.kuali.kfs.pdp.service.FormatProcessService;
import org.kuali.kfs.pdp.service.impl.exception.DisbursementRangeExhaustedException;
import org.kuali.rice.kns.bo.user.UniversalUser;

/**
 * This class provides methods for the format process.
 */
public class FormatProcessServiceImpl implements FormatProcessService {
    
    private FormatProcessDao formatProcessDao;
    private CustomerProfileDao customerProfileDao;
    private DisbursementNumberRangeDao disbursementNumberRangeDao;

    /**
     * @see org.kuali.kfs.pdp.service.FormatProcessService#getDataForFormat(org.kuali.rice.kns.bo.user.UniversalUser)
     */
    public FormatSelection getDataForFormat(UniversalUser user) {
        String campusCode = user.getCampusCode();
        Date formatStartDate = getFormatProcessStartDate(campusCode);

        FormatSelection formatSelection = new FormatSelection();
        formatSelection.setCampus(campusCode);
        formatSelection.setStartDate(formatStartDate);
        
        //if format process not started yet populate the other data as well
        if (formatStartDate == null) {
            formatSelection.setCustomerList(customerProfileDao.getAll());
            formatSelection.setRangeList(disbursementNumberRangeDao.getAll());
        }

        return formatSelection;
    }
    
   
    /**
     * @see org.kuali.kfs.pdp.service.FormatProcessService#getFormatProcessStartDate(java.lang.String)
     */
    public Date getFormatProcessStartDate(String campus) {

        FormatProcess fp = formatProcessDao.getByCampus(campus);
        
        if (fp != null) {
            return new Date(fp.getBeginFormat().getTime());
        }
        else {
            return null;
        }
    }

    /**
     * This method gets the format process dao.
     * @return
     */
    public FormatProcessDao getFormatProcessDao() {
        return formatProcessDao;
    }

    /**
     * This method sets the format process dao.
     * @param formatProcessDao
     */
    public void setFormatProcessDao(FormatProcessDao formatProcessDao) {
        this.formatProcessDao = formatProcessDao;
    }


    /**
     * This method gets the customerProfileDao.
     * @return customerProfileDao
     */
    public CustomerProfileDao getCustomerProfileDao() {
        return customerProfileDao;
    }


    /**
     * This method sets the customerProfileDao.
     * @param customerProfileDao
     */
    public void setCustomerProfileDao(CustomerProfileDao customerProfileDao) {
        this.customerProfileDao = customerProfileDao;
    }


    /**
     * This method gets the disbursementNumberRangeDao.
     * @return disbursementNumberRangeDao
     */
    public DisbursementNumberRangeDao getDisbursementNumberRangeDao() {
        return disbursementNumberRangeDao;
    }


    /**
     * This method sets the disbursementNumberRangeDao.
     * @param disbursementNumberRangeDao
     */
    public void setDisbursementNumberRangeDao(DisbursementNumberRangeDao disbursementNumberRangeDao) {
        this.disbursementNumberRangeDao = disbursementNumberRangeDao;
    }



}
