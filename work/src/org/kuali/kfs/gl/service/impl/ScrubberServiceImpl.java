/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.service.impl;

import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.GeneratedCostShareOriginEntryObjectCodeOverride;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.batch.service.ScrubberProcess;
import org.kuali.kfs.gl.batch.service.impl.ScrubberProcessImpl;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of ScrubberService
 */
@Transactional
public class ScrubberServiceImpl implements ScrubberService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceImpl.class);

    private ScrubberProcess reportOnlyScrubberProcess;
    private ScrubberProcess scrubberProcess;
    private ScrubberProcess demergerScrubberProcess;
    private ScrubberProcess batchScrubberProcess;
    
    /**
     * This process will call the scrubber in a read only mode. It will scrub a single group, won't create any output in origin
     * entry. It will create a the scrubber report
     * @param group the origin entry group to scrub for report
     * @param documentNumber the id of documents which generated origin entries that should be scrubbed
     * @see org.kuali.kfs.gl.service.ScrubberService#scrubGroupReportOnly(org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    public void scrubGroupReportOnly(String fileName, String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");

        // The logic for this was moved into another object because the process was written using
        // many instance variables which shouldn't be used for Spring services

            //new ScrubberProcessImpl(flexibleOffsetAccountService, accountingCycleCachingService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, scrubberValidator, generatedCostShareOriginEntryObjectCodeOverride, runDateService, batchFileDirectoryName, scrubberReportOnlyWriterService, glcpScrubberLedgerReportWriterService, scrubberListingReportWriterService, null, null, null, null);
        reportOnlyScrubberProcess.scrubGroupReportOnly(fileName, documentNumber);
    }

    /**
     * Scrubs all of the entries in all origin entry groups that are up for scrubbing
     * @see org.kuali.kfs.gl.service.ScrubberService#scrubEntries()
     */
    public void scrubEntries() {
        LOG.debug("scrubEntries() started");

        // The logic for this was moved into another object because the process was written using
        // many instance variables which shouldn't be used for Spring services

            //new ScrubberProcessImpl(flexibleOffsetAccountService, accountingCycleCachingService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, scrubberValidator, generatedCostShareOriginEntryObjectCodeOverride, runDateService, batchFileDirectoryName, scrubberReportWriterService, scrubberLedgerReportWriterService, null, scrubberBadBalanceListingReportWriterService, null, null, null);
        scrubberProcess.scrubEntries();
    }

    /**
     * Scrubs data read in by the Collector
     * 
     * @param batch the data read by the Collector
     * @param collectorReportData statistics about 
     * @param overrideOriginEntryService the implementation of origin entry service to use for this specific Collector scrub
     * @param overrideOriginEntryGroupService the implementation of origin entry group service to use for this specific Collector scrub
     * @return the status returned by the Scrubber
     * @see org.kuali.kfs.gl.service.ScrubberService#scrubCollectorBatch(org.kuali.kfs.gl.batch.CollectorBatch, org.kuali.kfs.gl.report.CollectorReportData, org.kuali.kfs.gl.service.OriginEntryService, org.kuali.kfs.gl.service.OriginEntryGroupService)
     */
    public void scrubCollectorBatch(ScrubberStatus scrubberStatus, CollectorBatch batch, CollectorReportData collectorReportData) {
        // this service is especially developed to support collector scrubbing, demerger, and report generation
            //new ScrubberProcessImpl(flexibleOffsetAccountService, accountingCycleCachingService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, scrubberValidator, generatedCostShareOriginEntryObjectCodeOverride, runDateService, collectorFileDirectoryName, null, null, null, null, null, null, null);
        batchScrubberProcess.scrubCollectorBatch(scrubberStatus, batch, collectorReportData);
    }
    
    public void performDemerger() {
        LOG.debug("performDemerger() started");
            //new ScrubberProcessImpl(flexibleOffsetAccountService, accountingCycleCachingService, dateTimeService, offsetDefinitionService, objectCodeService, kualiConfigurationService, universityDateDao, persistenceService, scrubberValidator, generatedCostShareOriginEntryObjectCodeOverride, runDateService, batchFileDirectoryName, null, null, null, null, demergerReportWriterService, demergerRemovedTransactionsListingReportWriterService, null);
        demergerScrubberProcess.performDemerger();
    }

  
    /**
     * Sets the reportOnlyScrubberProcess attribute value.
     * @param reportOnlyScrubberProcess The reportOnlyScrubberProcess to set.
     */
    public void setReportOnlyScrubberProcess(ScrubberProcess reportOnlyScrubberProcess) {
        this.reportOnlyScrubberProcess = reportOnlyScrubberProcess;
    }

    /**
     * Sets the scrubberProcess attribute value.
     * @param scrubberProcess The scrubberProcess to set.
     */
    public void setScrubberProcess(ScrubberProcess scrubberProcess) {
        this.scrubberProcess = scrubberProcess;
    }

    /**
     * Sets the demergerScrubberProcess attribute value.
     * @param demergerScrubberProcess The demergerScrubberProcess to set.
     */
    public void setDemergerScrubberProcess(ScrubberProcess demergerScrubberProcess) {
        this.demergerScrubberProcess = demergerScrubberProcess;
    }

    /**
     * Sets the batchScrubberProcess attribute value.
     * @param batchScrubberProcess The batchScrubberProcess to set.
     */
    public void setBatchScrubberProcess(ScrubberProcess batchScrubberProcess) {
        this.batchScrubberProcess = batchScrubberProcess;
    }

  
}
