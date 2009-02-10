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
package org.kuali.kfs.module.ld.batch.service.impl;

import static org.kuali.kfs.gl.businessobject.OriginEntrySource.LABOR_MAIN_POSTER_VALID;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.service.LaborLedgerEntryService;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.module.ld.util.LaborTestDataPreparator;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;

@ConfigureContext
public class LaborLedgerEntryPosterTestBroken extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;
    private Map fieldValues;
    private OriginEntryGroup group1;
    private Date today;

    private BusinessObjectService businessObjectService;
    private PostTransaction laborLedgerEntryPoster;
    private OriginEntryGroupService originEntryGroupService;
    private LaborLedgerEntryService laborLedgerEntryService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborLedgerEntryPoster.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));

        laborLedgerEntryPoster = SpringContext.getBeansOfType(PostTransaction.class).get("laborLedgerEntryPoster");
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);
        laborLedgerEntryService = SpringContext.getBean(LaborLedgerEntryService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        group1 = originEntryGroupService.createGroup(dateTimeService.getCurrentSqlDate(), LABOR_MAIN_POSTER_VALID, false, false, false);
        today = dateTimeService.getCurrentDate();

        LedgerEntry cleanup = new LedgerEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LedgerEntry.class, fieldValues);
    }

    public void testPost() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("post.numOfData"));
        int expectedMaxSequenceNumber = Integer.valueOf(properties.getProperty("post.expectedMaxSequenceNumber"));
        int expectedInsertion = Integer.valueOf(properties.getProperty("post.expectedInsertion"));

        List<LaborOriginEntry> transactionList = LaborTestDataPreparator.getLaborOriginEntryList(properties, "post.testData", numberOfTestData, group1);
        Map<String, Integer> operationType = new HashMap<String, Integer>();

        for (LaborOriginEntry transaction : transactionList) {
            String operation = laborLedgerEntryPoster.post(transaction, 0, today);
            Integer currentNumber = operationType.get(operation);
            Integer numberOfOperation = currentNumber != null ? currentNumber + 1 : 1;
            operationType.put(operation, numberOfOperation);
        }

        Collection returnValues = businessObjectService.findMatching(LedgerEntry.class, fieldValues);
        assertEquals(numberOfTestData, returnValues.size());

        assertEquals(1, operationType.size());
        assertEquals(expectedInsertion, operationType.get(KFSConstants.OperationType.INSERT).intValue());

        LedgerEntry expected1 = new LedgerEntry();
        ObjectUtil.populateBusinessObject(expected1, properties, "post.expected1", fieldNames, deliminator);
        assertEquals(expectedMaxSequenceNumber, laborLedgerEntryService.getMaxSequenceNumber(expected1).intValue());

        LedgerEntry expected2 = new LedgerEntry();
        ObjectUtil.populateBusinessObject(expected2, properties, "post.expected2", fieldNames, deliminator);
        assertEquals(expectedMaxSequenceNumber, laborLedgerEntryService.getMaxSequenceNumber(expected2).intValue());

        LedgerEntry expected3 = new LedgerEntry();
        ObjectUtil.populateBusinessObject(expected3, properties, "post.expected3", fieldNames, deliminator);
        assertEquals(expectedMaxSequenceNumber, laborLedgerEntryService.getMaxSequenceNumber(expected3).intValue());
    }
}
