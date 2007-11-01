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
package org.kuali.module.chart.service.impl;

import org.apache.log4j.Logger;
import org.kuali.module.chart.dao.PriorYearOrganizationDao;
import org.kuali.module.chart.service.PriorYearOrganizationService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class PriorYearOrganizationServiceImpl implements PriorYearOrganizationService {
    private static final Logger LOG = Logger.getLogger(PriorYearOrganizationServiceImpl.class);

    private PriorYearOrganizationDao priorYearOrganizationDao;

    /**
     * Constructs a PriorYearOrganizationServiceImpl.java.
     */
    public PriorYearOrganizationServiceImpl() {
        super();
    }

    /**
     * This method sets the local dao variable to the value provided.
     * 
     * @param priorYearOrganizationDao The PriorYearOrganizationDao to set.
     */
    public void setPriorYearOrganizationDao(PriorYearOrganizationDao priorYearOrganizationDao) {
        this.priorYearOrganizationDao = priorYearOrganizationDao;
    }

    /**
     * @see org.kuali.module.chart.service.PriorYearOrganizationService#populatePriorYearOrganizationFromCurrent()
     */
    public void populatePriorYearOrganizationsFromCurrent() {

        int purgedCount = priorYearOrganizationDao.purgePriorYearOrganizations();
        if (LOG.isInfoEnabled()) {
            LOG.info("number of prior year organizations purged : " + purgedCount);
        }

        int copiedCount = priorYearOrganizationDao.copyCurrentOrganizationsToPriorYearTable();
        if (LOG.isInfoEnabled()) {
            LOG.info("number of current year organizations copied to prior year : " + copiedCount);
        }
    }

}
