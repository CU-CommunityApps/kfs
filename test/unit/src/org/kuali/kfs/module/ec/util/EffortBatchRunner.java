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
package org.kuali.kfs.module.ec.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerEntry;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ec.batch.service.EffortCertificationCreateService;
import org.kuali.kfs.module.ec.batch.service.EffortCertificationExtractService;
import org.kuali.kfs.module.ec.testdata.EffortTestDataPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.SpringContextForBatchRunner;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.Log4jConfigurer;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.spring.Logged;

/**
 * This batch runner is just for testing purpose and used by effort team members.
 */
public class EffortBatchRunner {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortBatchRunner.class);

    private final Properties properties, message;
    private final String balanceFieldNames, entryFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;
    private LaborModuleService laborModuleService;

    private Class<? extends LaborLedgerBalance> ledgerBalanceClass;
    private Class<? extends LaborLedgerEntry> ledgerEntryClass;

    public EffortBatchRunner() {
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/effortCertificationExtractServiceProformance.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty("deliminator");

        balanceFieldNames = properties.getProperty("balanceFieldNames");
        entryFieldNames = properties.getProperty("entryFieldNames");

        Log4jConfigurer.configureLogging(false);

        SpringContextForBatchRunner.initializeApplicationContext();
        businessObjectService = SpringContextForBatchRunner.getBean(BusinessObjectService.class);
        laborModuleService = SpringContext.getBean(LaborModuleService.class);

        KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        ledgerBalanceClass = kualiModuleService.getResponsibleModuleService(LaborLedgerBalance.class).createNewObjectFromExternalizableClass(LaborLedgerBalance.class).getClass();
        ledgerEntryClass = kualiModuleService.getResponsibleModuleService(LaborLedgerEntry.class).createNewObjectFromExternalizableClass(LaborLedgerEntry.class).getClass();
    }

    public void loadData() {
        this.doCleanup();

        int numberOfEmplid = Integer.valueOf(StringUtils.trim(properties.getProperty("emplid.numOfEmplid")));
        for (int i = 1; i <= numberOfEmplid; i++) {
            String emplid = StringUtils.trim(properties.getProperty("emplid" + i));
            this.loadDataForEmployee(emplid, i);
        }
    }

    private void doCleanup() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, "2007");

        businessObjectService.deleteMatching(ledgerBalanceClass, fieldValues);
        businessObjectService.deleteMatching(ledgerEntryClass, fieldValues);

    }

    private void loadDataForEmployee(String emplid, Integer sequenceNumber) {
        int numberOfEntries = Integer.valueOf(StringUtils.trim(properties.getProperty("ledgerEntry.numOfEntries")));
        List<LaborLedgerEntry> ledgerEntries = TestDataPreparator.buildTestDataList(ledgerEntryClass, properties, "ledgerEntry.seed", entryFieldNames, deliminator, numberOfEntries);
        for (LaborLedgerEntry entry : ledgerEntries) {
            entry.setEmplid(emplid);
            entry.setTransactionLedgerEntrySequenceNumber(sequenceNumber);
        }
        businessObjectService.save(ledgerEntries);

        int numberOfBalances = Integer.valueOf(StringUtils.trim(properties.getProperty("ledgerBalance.numOfBalances")));
        List<LaborLedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(ledgerBalanceClass, properties, "ledgerBalance.seed", balanceFieldNames, deliminator, numberOfBalances);
        for (LaborLedgerBalance balance : ledgerBalances) {
            balance.setEmplid(emplid);
        }
        businessObjectService.save(ledgerBalances);
    }

    @Logged
    public static void main(String[] args) {
        EffortBatchRunner batchRunner = new EffortBatchRunner();

        try {
            if (StringUtils.equalsIgnoreCase("-load", args[0])) {
                System.out.println("Loading data into labor Entry and balance tables ...");
                batchRunner.loadData();
            }
            else if (StringUtils.equalsIgnoreCase("-extract", args[0])) {
                System.out.println("Extracting Effort Certifications ...");
                EffortCertificationExtractService effortCertificationExtractService = SpringContextForBatchRunner.getBean(EffortCertificationExtractService.class);
                effortCertificationExtractService.extract(2007, "B01");
            }
            else if (StringUtils.equalsIgnoreCase("-create", args[0])) {
                System.out.println("Creating Effort Certifications ...");
                EffortCertificationCreateService effortCertificationCreateService = SpringContextForBatchRunner.getBean(EffortCertificationCreateService.class);
                effortCertificationCreateService.create(2007, "B01");
            }
            else {
                throw new IllegalArgumentException("Wrong argument -- The argument only can be -load or -extract");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }
}
