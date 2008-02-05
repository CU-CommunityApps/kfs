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
package org.kuali.module.effort.service;

import static org.kuali.test.fixtures.UserNameFixture.KULUSER;
import static org.kuali.test.fixtures.UserNameFixture.PARKE;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.effort.EffortPropertyConstants;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.test.ConfigureContext;
import org.kuali.test.util.TestDataPreparator;

@ConfigureContext(session = KULUSER)
public class EffortCertificationDocumentServiceTest extends KualiTestBase {
    private final Properties properties, message;
    private final String detailFieldNames, documentFieldNames, reportDefinitionFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;
    private EffortCertificationDocumentService effortCertificationDocumentService;

    /**
     * Constructs a EffortCertificationCreateServiceTest.java.
     */
    public EffortCertificationDocumentServiceTest() {
        super();
        String messageFileName = "test/src/org/kuali/module/effort/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/effort/testdata/effortCertificationDocumentService.properties";

        TestDataGenerator generator = new TestDataGenerator(propertiesFileName, messageFileName);
        properties = generator.getProperties();
        message = generator.getMessage();

        deliminator = properties.getProperty("deliminator");

        detailFieldNames = properties.getProperty("detailFieldNames");
        documentFieldNames = properties.getProperty("documentFieldNames");
        reportDefinitionFieldNames = properties.getProperty("reportDefinitionFieldNames");
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        effortCertificationDocumentService = SpringContext.getBean(EffortCertificationDocumentService.class);
    }

    /**
     * check if the service can approperiately create and route effort certification document 
     * 
     * @see effortCertificationDocumentService.createEffortCertificationDocument(EffortCertificationDocumentBuild)
     */
    public void testCreateEffortCertificationDocument() throws Exception {
        String testTarget = "createEffortCertificationDocument.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, "documentCleanup", documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);

        boolean isCreated = effortCertificationDocumentService.createEffortCertificationDocument(documentBuild);
        assertTrue(isCreated);

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);

        int numberOfExpectedDocuments = Integer.valueOf(properties.getProperty(testTarget + "numOfExpectedDocuments"));
        List<EffortCertificationDocument> expectedDocuments = TestDataPreparator.buildExpectedValueList(EffortCertificationDocument.class, properties, testTarget + "expectedDocuments", documentFieldNames, deliminator, numberOfExpectedDocuments);

        assertEquals(numberOfExpectedDocuments, documentList.size());

        List<String> documentKeyFields = ObjectUtil.split(documentFieldNames, deliminator);
        documentKeyFields.remove(KFSPropertyConstants.DOCUMENT_NUMBER);
        assertTrue(TestDataPreparator.hasSameElements(expectedDocuments, documentList, documentKeyFields));
        
        for (EffortCertificationDocument document : documentList) {
            assertEquals(document.getDocumentHeader().getFinancialDocumentStatusCode(), KFSConstants.DocumentStatusCodes.ENROUTE);
        }
    }
    
    /**
     * check if the service can approperiately create and route SET document 
     * 
     * @see effortCertificationDocumentService.generateSalaryExpenseTransferDocument(EffortCertificationDocument)
     */
    public void testGenerateSalaryExpenseTransferDocument() throws Exception {
        String testTarget = "generateSalaryExpenseTransferDocument.";
        Integer fiscalYear = Integer.valueOf(StringUtils.trim(properties.getProperty(testTarget + "fiscalYear")));
        String reportNumber = properties.getProperty(testTarget + "reportNumber");

        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);
        TestDataPreparator.doCleanUpWithReference(EffortCertificationDocumentBuild.class, properties, "documentCleanup", documentFieldNames, deliminator);

        EffortCertificationReportDefinition reportDefinition = this.buildReportDefinition("");
        reportDefinition = TestDataPreparator.persistDataObject(reportDefinition);

        EffortCertificationDocumentBuild documentBuild = this.buildDocumentBuild(testTarget);
        documentBuild = TestDataPreparator.persistDataObject(documentBuild);    

        boolean isCreated = effortCertificationDocumentService.createEffortCertificationDocument(documentBuild);
        assertTrue(isCreated);

        List<EffortCertificationDocument> documentList = TestDataPreparator.findMatching(EffortCertificationDocument.class, properties, "documentCleanup", documentFieldNames, deliminator);

        for (EffortCertificationDocument document : documentList) {
            document.refreshReferenceObject(EffortPropertyConstants.EFFORT_CERTIFICATION_REPORT_DEFINITION);
            try {
                boolean isGenerated = effortCertificationDocumentService.generateSalaryExpenseTransferDocument(document);
                fail("The document cannot pass the rule validation.");
            }
            catch(ValidationException ve) {
                assertTrue(true);
            }
        }
    }
    
    /**
     * build a report defintion object from the given test target
     * 
     * @param testTarget the given test target that specifies the test data being used
     * @return a report defintion object
     */
    private EffortCertificationReportDefinition buildReportDefinition(String testTarget) {
        return TestDataPreparator.buildTestDataObject(EffortCertificationReportDefinition.class, properties, testTarget + "reportDefinitionFieldValues", reportDefinitionFieldNames, deliminator);
    }

    private EffortCertificationDocumentBuild buildDocumentBuild(String testTarget) {
        EffortCertificationDocumentBuild documentBuild = TestDataPreparator.buildTestDataObject(EffortCertificationDocumentBuild.class, properties, testTarget + "documentBuild", documentFieldNames, deliminator);
        List<EffortCertificationDetailBuild> detailBuild = this.buildDetailLineBuild(testTarget);
        documentBuild.setEffortCertificationDetailLinesBuild(detailBuild);
        return documentBuild;
    }
    
    private List<EffortCertificationDetailBuild> buildDetailLineBuild(String testTarget) {
        int numberOfDetailBuild = Integer.valueOf(properties.getProperty(testTarget + "numOfDetailBuild"));
        return TestDataPreparator.buildTestDataList(EffortCertificationDetailBuild.class, properties, testTarget + "detailBuild", detailFieldNames, deliminator, numberOfDetailBuild);
    }
}
