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
package org.kuali.kfs.module.ar.report.util;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;

public class CustomerStatementDetailReportDataHolder {
    
    private String documentNumber;
    private Date documentFinalDate;
    private String documentDescription;
    private KualiDecimal financialDocumentTotalAmountCharge;
    private KualiDecimal financialDocumentTotalAmountCredit;
    private String orgName;
    private String fein;
    private String docType;
    
    
    public CustomerStatementDetailReportDataHolder(FinancialSystemDocumentHeader docHeader, Org processingOrg, String docType) {
       documentDescription = docHeader.getDocumentDescription();
      // financialDocumentTotalAmount = docHeader.getFinancialDocumentTotalAmount();
       if (docType.equals("Credit Memo"))
           financialDocumentTotalAmountCredit = docHeader.getFinancialDocumentTotalAmount();
       else if (docType.equals("Invoice"))
           financialDocumentTotalAmountCharge = docHeader.getFinancialDocumentTotalAmount();
       documentNumber = docHeader.getDocumentNumber();
       documentFinalDate = docHeader.getDocumentFinalDate();
       this.docType = docType;
       orgName = processingOrg.getOrganizationName();
       
       String fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear().toString();
       Map<String, String> criteria = new HashMap<String, String>();
       criteria.put("universityFiscalYear", fiscalYear);
       criteria.put("processingChartOfAccountCode", processingOrg.getChartOfAccountsCode());
       criteria.put("processingOrganizationCode", processingOrg.getOrganizationCode());
         
         
       SystemInformation sysinfo = (SystemInformation)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SystemInformation.class, criteria);
       fein = sysinfo.getUniversityFederalEmployerIdentificationNumber();
       
    }

    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the documentFinalDate attribute. 
     * @return Returns the documentFinalDate.
     */
    public Date getDocumentFinalDate() {
        return documentFinalDate;
    }

    /**
     * Sets the documentFinalDate attribute value.
     * @param documentFinalDate The documentFinalDate to set.
     */
    public void setDocumentFinalDate(Date documentFinalDate) {
        this.documentFinalDate = documentFinalDate;
    }

    /**
     * Gets the documentDescription attribute. 
     * @return Returns the documentDescription.
     */
    public String getDocumentDescription() {
        return documentDescription;
    }

    /**
     * Sets the documentDescription attribute value.
     * @param documentDescription The documentDescription to set.
     */
    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    /**
     * Gets the orgName attribute. 
     * @return Returns the orgName.
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * Sets the orgName attribute value.
     * @param orgName The orgName to set.
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /**
     * Gets the fein attribute. 
     * @return Returns the fein.
     */
    public String getFein() {
        return fein;
    }

    /**
     * Sets the fein attribute value.
     * @param fein The fein to set.
     */
    public void setFein(String fein) {
        this.fein = fein;
    }

    /**
     * Gets the docType attribute. 
     * @return Returns the docType.
     */
    public String getDocType() {
        return docType;
    }

    /**
     * Sets the docType attribute value.
     * @param docType The docType to set.
     */
    public void setDocType(String docType) {
        this.docType = docType;
    }

    /**
     * Gets the financialDocumentTotalAmountCharge attribute. 
     * @return Returns the financialDocumentTotalAmountCharge.
     */
    public KualiDecimal getFinancialDocumentTotalAmountCharge() {
        return financialDocumentTotalAmountCharge;
    }

    /**
     * Sets the financialDocumentTotalAmountCharge attribute value.
     * @param financialDocumentTotalAmountCharge The financialDocumentTotalAmountCharge to set.
     */
    public void setFinancialDocumentTotalAmountCharge(KualiDecimal financialDocumentTotalAmountCharge) {
        this.financialDocumentTotalAmountCharge = financialDocumentTotalAmountCharge;
    }

    /**
     * Gets the financialDocumentTotalAmountCredit attribute. 
     * @return Returns the financialDocumentTotalAmountCredit.
     */
    public KualiDecimal getFinancialDocumentTotalAmountCredit() {
        return financialDocumentTotalAmountCredit;
    }

    /**
     * Sets the financialDocumentTotalAmountCredit attribute value.
     * @param financialDocumentTotalAmountCredit The financialDocumentTotalAmountCredit to set.
     */
    public void setFinancialDocumentTotalAmountCredit(KualiDecimal financialDocumentTotalAmountCredit) {
        this.financialDocumentTotalAmountCredit = financialDocumentTotalAmountCredit;
    }
    
    
}
    