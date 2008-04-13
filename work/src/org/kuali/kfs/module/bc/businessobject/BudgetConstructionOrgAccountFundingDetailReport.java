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
package org.kuali.module.budget.bo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.web.comparator.StringValueComparator;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.BCConstants;

/**
 * Budget Construction Organization Account Funding Detail Report Business Object.
 */
public class BudgetConstructionOrgAccountFundingDetailReport {

    // Header parts
    private String fiscalYear;
    private String orgChartOfAccountsCode;
    private String orgChartOfAccountDescription;
    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String consHdr;
    private String fundGroupCode;
    private String fundGroupName;
    private String subFundGroupCode;
    private String subFundGroupDescription;
    private String baseFy;
    private String reqFy;
    
    private String header1;
    private String header2;
    private String header2a;
    private String header3;
    private String header31;
    private String header4;
    private String header40;
    private String header5;
    private String header6;

    // Groups
    private String financialObjectLevelCode;
    private String incomeExpenseCode;
    private String financialConsolidationSortCode; 
    private String financialLevelSortCode;
    
    // Body parts
    
    
    //from BudgetConstructionIntendedIncumbent
    private String iuClassificationLevel;
    
    //from PendingBudgetConstructionAppointmentFunding
    private String financialSubObjectCode;
    
    //from BudgetConstructionAdministrativePost
    private String administrativePost;
    
    //from BudgetConstructionPosition
    private String positionNumber;
    private Integer iuNormalWorkMonths;
    private Integer iuPayMonths;
    private String positionSalaryPlanDefault;
    private String positionGradeDefault;
    private BigDecimal positionStandardHoursDefault;
    
    //from BudgetConstructionCalculatedSalaryFoundationTracker
    private BigDecimal csfTimePercent;
    private Integer csfAmount;
    private BigDecimal csfFullTimeEmploymentQuantity;
    
    //from PendingBudgetConstructionAppointmentFunding
    private Integer appointmentFundingMonth;
    private Integer appointmentRequestedAmount;
    private BigDecimal appointmentRequestedTimePercent;
    private String appointmentRequestedFteQuantity;
    private String appointmentFundingDurationCode;
    private Integer appointmentRequestedCsfAmount;
    private BigDecimal appointmentRequestedCsfTimePercent;
    private String appointmentRequestedCsfFteQuantity;
    private Integer appointmentTotalIntendedAmount;
    private String appointmentTotalIntendedFteQuantity;
    

    // Total parts
    
    private String totalObjectname;
    
    private Integer totalObjectPositionCsfAmount;
    private Integer totalObjectAppointmentRequestedAmount;
    private String totalObjectPositionCsfFteQuantity;
    private String totalObjectAppointmentRequestedQuantity;
    private Integer totalObjectAmountChange;
    private BigDecimal totalObjectPercentChange;
    
    private String totalAccountname;
    private String totalSubAccountname;
    
    private Integer totalAccountPositionCsfAmount;
    private Integer totalAccountAppointmentRequestedAmount;
    private String totalAccountPositionCsfFteQuantity;
    private String totalAccountAppointmentRequestedQuantity;
    private Integer totalAccountAmountChange;
    private BigDecimal totalAccountPercentChange;
        
    
    /**
     * Gets the baseFy
     * 
     * @return Returns the baseFy.
     */
    public String getBaseFy() {
        return baseFy;
    }

    /**
     * Sets the baseFy
     * 
     * @param baseFy The baseFy to set.
     */
    public void setBaseFy(String baseFy) {
        this.baseFy = baseFy;
    }

    /**
     * Gets the consHdr
     * 
     * @return Returns the consHdr.
     */
    public String getConsHdr() {
        return consHdr;
    }

    /**
     * Sets the consHdr
     * 
     * @param consHdr The consHdr to set.
     */
    public void setConsHdr(String consHdr) {
        this.consHdr = consHdr;
    }

    /**
     * Gets the fiscalYear
     * 
     * @return Returns the fiscalYear.
     */
    public String getFiscalYear() {
        return fiscalYear;
    }

    /**
     * Sets the fiscalYear
     * 
     * @param fiscalYear The fiscalYear to set.
     */
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    /**
     * Gets the fundGroupCode
     * 
     * @return Returns the fundGroupCode.
     */
    public String getFundGroupCode() {
        return fundGroupCode;
    }

    /**
     * Sets the fundGroupCode
     * 
     * @param fundGroupCode The fundGroupCode to set.
     */
    public void setFundGroupCode(String fundGroupCode) {
        this.fundGroupCode = fundGroupCode;
    }

    /**
     * Gets the header1
     * 
     * @return Returns the header1.
     */
    public String getHeader1() {
        return header1;
    }

    /**
     * Sets the header1
     * 
     * @param header1 The header1 to set.
     */
    public void setHeader1(String header1) {
        this.header1 = header1;
    }

    /**
     * Gets the header2
     * 
     * @return Returns the header2.
     */
    public String getHeader2() {
        return header2;
    }

    /**
     * Sets the header2
     * 
     * @param header2 The header2 to set.
     */
    public void setHeader2(String header2) {
        this.header2 = header2;
    }

    /**
     * Gets the header3
     * 
     * @return Returns the header3.
     */
    public String getHeader3() {
        return header3;
    }

    /**
     * Sets the header3
     * 
     * @param header3 The header3 to set.
     */
    public void setHeader3(String header3) {
        this.header3 = header3;
    }

    /**
     * Gets the header4
     * 
     * @return Returns the header4.
     */
    public String getHeader4() {
        return header4;
    }

    /**
     * Sets the header4
     * 
     * @param header4 The header4 to set.
     */
    public void setHeader4(String header4) {
        this.header4 = header4;
    }

    /**
     * Gets the header5
     * 
     * @return Returns the header5.
     */
    public String getHeader5() {
        return header5;
    }

    /**
     * Sets the header5
     * 
     * @param header5 The header5 to set.
     */
    public void setHeader5(String header5) {
        this.header5 = header5;
    }

    /**
     * Gets the header6
     * 
     * @return Returns the header6.
     */
    public String getHeader6() {
        return header6;
    }

    /**
     * Sets the header6
     * 
     * @param header6 The header6 to set.
     */
    public void setHeader6(String header6) {
        this.header6 = header6;
    }

    /**
     * Gets the organizationCode
     * 
     * @return Returns the organizationCode.
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * Gets the organizationName
     * 
     * @return Returns the organizationName.
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * Sets the organizationName
     * 
     * @param organizationName The organizationName to set.
     */
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * Gets the reqFy
     * 
     * @return Returns the reqFy.
     */
    public String getReqFy() {
        return reqFy;
    }

    /**
     * Sets the reqFy
     * 
     * @param reqFy The reqFy to set.
     */
    public void setReqFy(String reqFy) {
        this.reqFy = reqFy;
    }

    /**
     * Gets the subFundGroupCode
     * 
     * @return Returns the subFundGroupCode.
     */
    public String getSubFundGroupCode() {
        return subFundGroupCode;
    }

    /**
     * Sets the subFundGroupCode
     * 
     * @param subFundGroupCode The subFundGroupCode to set.
     */
    public void setSubFundGroupCode(String subFundGroupCode) {
        this.subFundGroupCode = subFundGroupCode;
    }

    /**
     * Gets the subFundGroupDescription
     * 
     * @return Returns the subFundGroupDescription.
     */
    public String getSubFundGroupDescription() {
        return subFundGroupDescription;
    }

    /**
     * Sets the subFundGroupDescription
     * 
     * @param subFundGroupDescription The subFundGroupDescription to set.
     */
    public void setSubFundGroupDescription(String subFundGroupDescription) {
        this.subFundGroupDescription = subFundGroupDescription;
    }

    /**
     * Gets the fundGroupName
     * 
     * @return Returns the fundGroupName.
     */
    public String getFundGroupName() {
        return fundGroupName;
    }

    /**
     * Sets the fundGroupName
     * 
     * @param fundGroupName The fundGroupName to set.
     */
    public void setFundGroupName(String fundGroupName) {
        this.fundGroupName = fundGroupName;
    }

    /**
     * Gets the chartOfAccountDescription
     * 
     * @return Returns the chartOfAccountDescription.
     */
    public String getChartOfAccountDescription() {
        return chartOfAccountDescription;
    }

    /**
     * Sets the chartOfAccountDescription
     * 
     * @param chartOfAccountDescription The chartOfAccountDescription to set.
     */
    public void setChartOfAccountDescription(String chartOfAccountDescription) {
        this.chartOfAccountDescription = chartOfAccountDescription;
    }

    /**
     * Gets the chartOfAccountsCode
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the orgChartOfAccountDescription
     * 
     * @return Returns the orgChartOfAccountDescription.
     */
    public String getOrgChartOfAccountDescription() {
        return orgChartOfAccountDescription;
    }

    /**
     * Sets the orgChartOfAccountDescription
     * 
     * @param orgChartOfAccountDescription The orgChartOfAccountDescription to set.
     */
    public void setOrgChartOfAccountDescription(String orgChartOfAccountDescription) {
        this.orgChartOfAccountDescription = orgChartOfAccountDescription;
    }

    /**
     * Gets the orgChartOfAccountsCode
     * 
     * @return Returns the orgChartOfAccountsCode.
     */
    public String getOrgChartOfAccountsCode() {
        return orgChartOfAccountsCode;
    }

    /**
     * Sets the orgChartOfAccountsCode
     * 
     * @param orgChartOfAccountsCode The orgChartOfAccountsCode to set.
     */
    public void setOrgChartOfAccountsCode(String orgChartOfAccountsCode) {
        this.orgChartOfAccountsCode = orgChartOfAccountsCode;
    }

    public String getAppointmentRequestedCsfFteQuantity() {
        return appointmentRequestedCsfFteQuantity;
    }

    public void setAppointmentRequestedCsfFteQuantity(String appointmentRequestedCsfFteQuantity) {
        this.appointmentRequestedCsfFteQuantity = appointmentRequestedCsfFteQuantity;
    }

    public String getAppointmentRequestedFteQuantity() {
        return appointmentRequestedFteQuantity;
    }

    public void setAppointmentRequestedFteQuantity(String appointmentRequestedFteQuantity) {
        this.appointmentRequestedFteQuantity = appointmentRequestedFteQuantity;
    }

    public String getHeader2a() {
        return header2a;
    }

    public void setHeader2a(String header2a) {
        this.header2a = header2a;
    }

    public String getHeader31() {
        return header31;
    }

    public void setHeader31(String header31) {
        this.header31 = header31;
    }

    public String getHeader40() {
        return header40;
    }

    public void setHeader40(String header40) {
        this.header40 = header40;
    }


    public String getFinancialObjectLevelCode() {
        return financialObjectLevelCode;
    }

    public void setFinancialObjectLevelCode(String financialObjectLevelCode) {
        this.financialObjectLevelCode = financialObjectLevelCode;
    }

    public String getFinancialConsolidationSortCode() {
        return financialConsolidationSortCode;
    }

    public void setFinancialConsolidationSortCode(String financialConsolidationSortCode) {
        this.financialConsolidationSortCode = financialConsolidationSortCode;
    }

    public String getIncomeExpenseCode() {
        return incomeExpenseCode;
    }

    public void setIncomeExpenseCode(String incomeExpenseCode) {
        this.incomeExpenseCode = incomeExpenseCode;
    }

    public String getFinancialLevelSortCode() {
        return financialLevelSortCode;
    }

    public void setFinancialLevelSortCode(String financialLevelSortCode) {
        this.financialLevelSortCode = financialLevelSortCode;
    }

}
