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
package org.kuali.kfs.module.bc.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionRequestMove;
import org.kuali.kfs.module.bc.businessobject.PayrateImportExport;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.service.PayrateImportService;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PayrateImportServiceImpl implements PayrateImportService {
    
    private BusinessObjectService businessObjectService;
    private LockService lockService;
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#importFile(java.io.InputStream)
     */
    @Transactional
    public StringBuilder importFile(InputStream fileImportStream) {
        this.businessObjectService.delete(new ArrayList<PersistableBusinessObject>(this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class)));
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        StringBuilder messages = new StringBuilder();
        int importCount = 0;
        
        try {
            while(fileReader.ready()) {
                PayrateImportExport temp = new PayrateImportExport();
                String line = fileReader.readLine();
                ObjectUtil.convertLineToBusinessObject(temp, line, DefaultImportFileFormat.fieldLengths, Arrays.asList(DefaultImportFileFormat.fieldNames));
                BudgetConstructionPayRateHolding budgetConstructionPayRateHolding = createHoldingRecord(temp);
                businessObjectService.save(budgetConstructionPayRateHolding);
                importCount++;
            }
        }
        catch (Exception e) {
            messages.append("Import Aborted \n");
            
            return messages;
        }
        
        messages.append("Import count: " + importCount + "\n");
        messages.append("Import complete \n");
        
        return messages;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#update()
     */
    @Transactional
    public StringBuilder update(Integer budgetYear, UniversalUser user) {
        StringBuilder messages = new StringBuilder();
        Map lockMap = new HashMap();
        
        List<BudgetConstructionPayRateHolding> records = (List<BudgetConstructionPayRateHolding>) this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class);
        for (BudgetConstructionPayRateHolding record: records) {
            List<PendingBudgetConstructionAppointmentFunding> fundingRecords = getFundingRecords(record, budgetYear);
            for (PendingBudgetConstructionAppointmentFunding fundingRecord : fundingRecords) {
                BudgetConstructionHeader header = getHeaderRecord(fundingRecord.getChartOfAccountsCode(), fundingRecord.getAccountNumber(), fundingRecord.getSubAccountNumber(), budgetYear);
                String lockingKey = getLockingKeyString(fundingRecord);
                if ( ! lockMap.containsKey(lockingKey) ) {
                    BudgetConstructionLockStatus lockStatus = this.lockService.lockAccount(header, user.getPersonUniversalIdentifier());
                    /*if (!lockStatus.getLockStatus().equals(KFSConstants.BudgetConstructionConstants.LockStatus.SUCCESS) {
                        
                    }*/
                    
                }
            }
            
        }
        return messages;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.service.PayrateImportService#generatePdf(java.lang.StringBuilder, java.io.ByteArrayOutputStream)
     */
    @NonTransactional
    public void generatePdf(StringBuilder logMessages, ByteArrayOutputStream baos) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        
        document.add(new Paragraph(logMessages.toString()));
        
        document.close();
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    private BudgetConstructionPayRateHolding createHoldingRecord(PayrateImportExport record) {
        BudgetConstructionPayRateHolding budgetConstructionPayRateHolding = new BudgetConstructionPayRateHolding();
        
        budgetConstructionPayRateHolding.setEmplid(record.getEmplid());
        budgetConstructionPayRateHolding.setPositionNumber(record.getPositionNumber());
        budgetConstructionPayRateHolding.setPersonName(record.getPersonName());
        budgetConstructionPayRateHolding.setSetidSalary(record.getSetidSalary());
        budgetConstructionPayRateHolding.setSalaryAdministrationPlan(record.getSalaryAdministrationPlan());
        budgetConstructionPayRateHolding.setGrade(record.getGrade());
        budgetConstructionPayRateHolding.setUnionCode(record.getPositionUnionCode());
        budgetConstructionPayRateHolding.setAppointmentRequestedPayRate(record.getAppointmentRequestPayRate().divide(new BigDecimal(100)));
        //TODO: SHOULD I USE CSF FREEZE DATE?
        
        return budgetConstructionPayRateHolding;
    }
    
    private List<PendingBudgetConstructionAppointmentFunding> getFundingRecords(BudgetConstructionPayRateHolding holdingRecord, Integer budgetYear) {
        Map searchCriteria = new HashMap();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        //TODO: get this value from a system parameter
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "2500");
        searchCriteria.put(KFSPropertyConstants.EMPLID, holdingRecord.getEmplid());
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, holdingRecord.getPositionNumber());
        
        return (List<PendingBudgetConstructionAppointmentFunding>) this.businessObjectService.findMatching(PendingBudgetConstructionAppointmentFunding.class, searchCriteria);
    }
    
    private String getLockingKeyString(PendingBudgetConstructionAppointmentFunding record) {
        return record.getUniversityFiscalYear() + "-" + record.getChartOfAccountsCode() + "-" + record.getAccountNumber() + "-" + record.getSubAccountNumber();
    }
    
    public BudgetConstructionHeader getHeaderRecord(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer budgetYear) {
        Map searchCriteria = new HashMap();
        searchCriteria.put("chartOfAccountsCode", chartOfAccountsCode);
        searchCriteria.put("accountNumber", accountNumber);
        searchCriteria.put("subAccountNumber", subAccountNumber);
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetYear);
        
        BudgetConstructionHeader header = (BudgetConstructionHeader)this.businessObjectService.findByPrimaryKey(BudgetConstructionHeader.class, searchCriteria);
        
        return header;
    }
    
    public void setLockService(LockService lockService) {
        this.lockService = lockService;
    }
    
    private static class DefaultImportFileFormat {
        private static final int[] fieldLengths = new int[] {11, 8, 50, 5, 4, 3, 3, 10, 8};
        //TODO: use constants for field names
        private static final String[] fieldNames = new String[] {"emplid", "positionNumber", "personName", "setidSalary()", "salaryAdministrationPlan", "grade", "positionUnionCode", "appointmentRequestPayRate", "csfFreezeDate"};
    }
    
}
