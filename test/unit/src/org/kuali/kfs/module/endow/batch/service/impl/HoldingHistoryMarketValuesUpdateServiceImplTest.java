/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;
import java.util.Collection;

import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.module.endow.fixture.EndowmentHoldingValueAdjustmentDocumentFixture;
import org.kuali.kfs.module.endow.fixture.HoldingHistoryFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.kns.service.DocumentService;

@ConfigureContext(session = khuntley)
public class HoldingHistoryMarketValuesUpdateServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(HoldingHistoryMarketValuesUpdateServiceImplTest.class);

    private DocumentService documentService;
    private UnitTestSqlDao unitTestSqlDao;
    private HoldingHistoryMarketValuesUpdateServiceImpl holdingHistoryMarketValuesUpdateService;    
    private HoldingHistoryValueAdjustmentDocument ehva;
    private KEMID kemid;
    private Security security;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (null == documentService) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        holdingHistoryMarketValuesUpdateService = (HoldingHistoryMarketValuesUpdateServiceImpl) TestUtils.getUnproxiedService("mockHoldingHistoryMarketValuesUpdateService");

        unitTestSqlDao.sqlCommand("insert into END_SEC_T columns(SEC_ID, SEC_CLS_CD, SEC_UNIT_VAL, SEC_INC_PAY_FREQ, SEC_INC_NEXT_PAY_DT, SEC_RT, ROW_ACTV_IND, OBJ_ID, VER_NBR, NXT_FSCL_YR_DSTRB_AMT) values ('TESTSECID', '025', '1', 'M01', TO_DATE('08/01/2010', 'mm/dd/yyyy'), '20', 'Y', sys_guid(), '1', '100.20')");
      //  security = SecurityFixture.ENDOWMENT_SECURITY_RECORD.createSecurityRecord();
        kemid = KemIdFixture.KEMID_RECORD.createKemidRecord();
        ehva = EndowmentHoldingValueAdjustmentDocumentFixture.EHVA_ONLY_REQUIRED_FIELDS.createHoldingHistoryValueAdjustmentDocument();
        ehva.refreshNonUpdateableReferences();
    }
    
    /**
     * Test to find the document from the workflow
     */
    public final void testFindDocumentForMarketValueUpdate() {
        String documentHeaderId = ehva.getDocumentNumber();
        HoldingHistoryValueAdjustmentDocument ehvaFromWorkFlow = (HoldingHistoryValueAdjustmentDocument) holdingHistoryMarketValuesUpdateService.findDocumentForMarketValueUpdate(documentHeaderId);
        assertTrue("The document from workflow is not the same.", ehva == ehvaFromWorkFlow);
    }
    
    /**
     * Test to check the market value method
     */
    public final void testGetMarketValue() {
        BigDecimal marketValue = BigDecimal.ONE;
        ehva.refreshNonUpdateableReferences();
        BigDecimal marketValueFromService = holdingHistoryMarketValuesUpdateService.getMarketValue(ehva, BigDecimal.valueOf(100L));
        assertTrue("Market Value should be equal to 1.00", (marketValue.compareTo(marketValueFromService) == 0));
    }
    
    /**
     * Test to check sure updateHoldingHistoryRecords method from the service.
     */
    public final void testUpdateHoldingHistoryRecords() {
        Collection<HoldingHistory> holdingHistoryRecords = holdingHistoryMarketValuesUpdateService.holdingHistoryService.getHoldingHistoryBySecuritIdAndMonthEndId(ehva.getSecurityId(), ehva.getHoldingMonthEndDate());
        assertTrue("There should not be any records in HoldingHistory table.", !holdingHistoryMarketValuesUpdateService.updateHoldingHistoryRecords(ehva));
        
        //add a row to END_HLDG_HIST_T table for testing...
        HoldingHistoryFixture.HOLDING_HISTORY_RECORD.createHoldingHistoryRecord();
        holdingHistoryRecords = holdingHistoryMarketValuesUpdateService.holdingHistoryService.getHoldingHistoryBySecuritIdAndMonthEndId(ehva.getSecurityId(), ehva.getHoldingMonthEndDate());
        assertTrue("There should have been only 1 record in END_HLDG_HIST_T table.", holdingHistoryRecords.size() == 1);
        assertTrue("Updating of Holding History Record failed.", holdingHistoryMarketValuesUpdateService.updateHoldingHistoryRecords(ehva));
        
        holdingHistoryRecords = holdingHistoryMarketValuesUpdateService.holdingHistoryService.getHoldingHistoryBySecuritIdAndMonthEndId(ehva.getSecurityId(), ehva.getHoldingMonthEndDate());
        for (HoldingHistory holdingHistory : holdingHistoryRecords ) {
            assertTrue("Market Value should be 2.00", holdingHistory.getMarketValue().compareTo(BigDecimal.valueOf(2L)) == 0);
        }
    }
    
    /**
     * Test to do updateHoldingHistoryMarketValues from the service.
     */
    public final void testUpdateHoldingHistoryMarketValues() {
        HoldingHistoryFixture.HOLDING_HISTORY_RECORD.createHoldingHistoryRecord();
        
        ehva.refreshNonUpdateableReferences();
        assertTrue("Update of Holding History Market Vales failed.", holdingHistoryMarketValuesUpdateService.updateHoldingHistoryMarketValues());
    }
}
