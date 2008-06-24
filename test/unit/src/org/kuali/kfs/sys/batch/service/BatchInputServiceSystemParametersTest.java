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
package org.kuali.kfs.sys.batch.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.fp.batch.ProcurementCardInputFileType;
import org.kuali.kfs.gl.batch.CollectorInputFileType;
import org.kuali.kfs.ConfigureContext;
import org.kuali.kfs.KualiTestConstants.TestConstants.Data4;

/**
 * Tests system parameters are setup and methods on the batch input types are correctly using them.
 */
@ConfigureContext
public class BatchInputServiceSystemParametersTest extends KualiTestBase {
    private ParameterService parameterService;
    private BatchInputFileService batchInputFileService;

    private BatchInputFileType pcdoBatchInputFileType;
    private BatchInputFileType collectorBatchInputFileType;

    private UniversalUser validWorkgroupUser;
    private UniversalUser invalidWorkgroupUser;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        parameterService = SpringContext.getBean(ParameterService.class);
        batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        pcdoBatchInputFileType = SpringContext.getBean(ProcurementCardInputFileType.class);
        collectorBatchInputFileType = SpringContext.getBean(CollectorInputFileType.class);

        validWorkgroupUser = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(Data4.USER_ID2);
        invalidWorkgroupUser = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(Data4.USER_ID1);
    }

    /**
     * Verifies system parameters needed by the batch upload process exist in the db.
     */
    public final void testSystemParametersExist() throws Exception {
        List<String> activeFileTypes = parameterService.getParameterValues(ParameterConstants.FINANCIAL_SYSTEM_BATCH.class, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME);
        assertTrue("system parameter " + SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME + " is not setup or contains no file types", activeFileTypes != null && activeFileTypes.size() > 0 && StringUtils.isNotBlank(activeFileTypes.get(0)));

        String pcdoUploadWorkgroup = parameterService.getParameterValue(pcdoBatchInputFileType.getUploadWorkgroupParameterComponent(), KFSConstants.SystemGroupParameterNames.FILE_TYPE_WORKGROUP_PARAMETER_NAME);
        assertTrue("system parameter pcdo " + KFSConstants.SystemGroupParameterNames.FILE_TYPE_WORKGROUP_PARAMETER_NAME + " does not exist or has empty value.", StringUtils.isNotBlank(pcdoUploadWorkgroup));

        String collectorUploadWorkgroup = parameterService.getParameterValue(collectorBatchInputFileType.getUploadWorkgroupParameterComponent(), KFSConstants.SystemGroupParameterNames.FILE_TYPE_WORKGROUP_PARAMETER_NAME);
        assertTrue("system parameter collector " + KFSConstants.SystemGroupParameterNames.FILE_TYPE_WORKGROUP_PARAMETER_NAME + " does not exist or has empty value.", StringUtils.isNotBlank(pcdoUploadWorkgroup));
    }


    /**
     * Set SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME to empty and verify both pcdo & collector are inactive
     */
    public final void testIsBatchInputTypeActive_emptySystemParameter() throws Exception {
        setActiveSystemParameter("", true);
        assertFalse("pcdo isActive method not false when active param is empty", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertFalse("collector isActive method not false when active param is empty", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));
    }

    /**
     * Set SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME to an invalid group and verify both pcdo & collector are
     * inactive
     */
    public final void testIsBatchInputTypeActive_invalidSystemParameter() throws Exception {
        setActiveSystemParameter("foo", true);
        assertFalse("pcdo isActive method not false when active param is foo", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertFalse("collector isActive method not false when active param is foo", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));
    }

    /**
     * Set SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME to contain both indentifiers and verify both pcdo & collector
     * are active
     */
    public final void testIsBatchInputTypeActive_systemParameterContainsBoth() throws Exception {
        setActiveSystemParameter(collectorBatchInputFileType.getFileTypeIdentifer() + ";" + pcdoBatchInputFileType.getFileTypeIdentifer(), true);
        assertTrue("pcdo isActive method not true when active param contains identifier", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertTrue("collector isActive method not true when active param contains identifier", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));
    }

    /**
     * Set SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME to contain one indentifier and verify the correct one is
     * active and other type is inactive.
     */
    public final void testIsBatchInputTypeActive_systemParameterContainsOne() throws Exception {
        setActiveSystemParameter(collectorBatchInputFileType.getFileTypeIdentifer(), true);
        assertFalse("pcdo isActive method is true when active param does not contain identifier", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertTrue("collector isActive method not true when active param contains identifier", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));

        setActiveSystemParameter(pcdoBatchInputFileType.getFileTypeIdentifer(), true);
        assertTrue("pcdo isActive method is false when active param contains identifier", batchInputFileService.isBatchInputTypeActive(pcdoBatchInputFileType));
        assertFalse("collector isActive method is true when active param does not contain identifier", batchInputFileService.isBatchInputTypeActive(collectorBatchInputFileType));
    }


    /**
     * Changes the text for the batch input active system parameter, stores and clears cache.
     */
    private final void setActiveSystemParameter(String parameterText, boolean multiValue) throws Exception {
        TestUtils.setSystemParameter(ParameterConstants.FINANCIAL_SYSTEM_BATCH.class, SystemGroupParameterNames.ACTIVE_INPUT_TYPES_PARAMETER_NAME, parameterText);
    }

}
