/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report.service.impl;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidHistoricalCash;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;
import org.kuali.kfs.module.endow.dataaccess.KemidBenefittingOrganizationDao;
import org.kuali.kfs.module.endow.dataaccess.KemidDao;
import org.kuali.kfs.module.endow.dataaccess.KemidReportGroupDao;
import org.kuali.kfs.module.endow.report.service.EndowmentReportService;
import org.kuali.kfs.module.endow.report.util.AssetStatementReportDataHolder;
import org.kuali.kfs.module.endow.report.util.EndowmentReportHeaderDataHolder;
import org.kuali.kfs.module.endow.report.util.KemidsWithMultipleBenefittingOrganizationsDataHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;

public abstract class EndowmentReportServiceImpl implements EndowmentReportService {
    
    protected BusinessObjectService businessObjectService;
    protected ParameterService parameterService;
    protected KemidDao kemidDao;
    protected KemidBenefittingOrganizationDao kemidBenefittingOrganizationDao;
    protected KemidReportGroupDao kemidReportGroupDao;
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getInstitutionName()
     */
    public String getInstitutionName() {
        return parameterService.getParameterValue(OrganizationOptions.class, ArConstants.INSTITUTION_NAME);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getReportRequestor()
     */
    public String getReportRequestor() {
        return GlobalVariables.getUserSession().getPerson().getPrincipalName();
    }
        
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getBenefittingCampuses(java.util.List)
     */
    public String getBenefittingCampuses(List<String> campuses) {        
        StringBuffer orgCampuses = new StringBuffer();
        if (ObjectUtils.isNotNull(campuses) && !campuses.isEmpty()) {
            List<String> campusList = kemidBenefittingOrganizationDao.getCampusCodes(EndowPropertyConstants.CA_ORG_CAMPUS_CD, campuses);
            for (String campus : campusList) {
                orgCampuses.append(campus).append(" ");
            }
        } 
        
        if (orgCampuses.toString().isEmpty()) {
            orgCampuses.append("ALL");    
        }
        
        return orgCampuses.toString();
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getBenefittingCharts(java.util.List)
     */
    public String getBenefittingCharts(List<String> charts) {
        StringBuffer orgCharts = new StringBuffer();
        if (ObjectUtils.isNotNull(charts) && !charts.isEmpty()) {
            List<String> chartList = kemidBenefittingOrganizationDao.getAttributeValues(EndowPropertyConstants.KEMID_BENE_CHRT_CD, charts);
            for (String chart : chartList) {
                orgCharts.append(chart).append(" ");
            }
        } 
        
        if (orgCharts.toString().isEmpty()) {
            orgCharts.append("ALL");    
        }
        
        return orgCharts.toString();
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getBenefittingOrganizations(java.util.List)
     */
    public String getBenefittingOrganizations(List<String> organizations) {
        StringBuffer orgs = new StringBuffer();
        if (ObjectUtils.isNotNull(organizations) && !organizations.isEmpty()) {
            List<String> organizationList = kemidBenefittingOrganizationDao.getAttributeValues(EndowPropertyConstants.KEMID_BENE_ORG_CD, organizations);
            for (String org : organizationList) {
                orgs.append(org).append(" ");
            }
        } 
        
        if (orgs.toString().isEmpty()) {
            orgs.append("ALL");    
        }
        
        return orgs.toString();   
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getKemidTypeCodes(java.util.List)
     */
    public String getKemidTypeCodes(List<String> kemidTypeCodes) {
        StringBuffer typeCodes = new StringBuffer();
        if (ObjectUtils.isNotNull(kemidTypeCodes) && !kemidTypeCodes.isEmpty()) {
            List<String> typeCodeList = kemidDao.getAttributeValues(EndowPropertyConstants.KEMID_TYPE_CODE, kemidTypeCodes);
            for (String typeCode : typeCodeList) {
                typeCodes.append(typeCode).append(" ");
            }
        }
        
        if (typeCodes.toString().isEmpty()) {
            typeCodes.append("ALL");    
        } 
        
        return typeCodes.toString();
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getKemidPurposeCodes(java.util.List)
     */
    public String getKemidPurposeCodes(List<String> kemidPurposes) {
        StringBuffer purposes = new StringBuffer();
        if (ObjectUtils.isNotNull(kemidPurposes) && !kemidPurposes.isEmpty()) {
            List<String> purposeList = kemidDao.getAttributeValues(EndowPropertyConstants.KEMID_PRPS_CD, kemidPurposes);
            for (String purpose : purposeList) {
                purposes.append(purpose).append(" ");
            }
        } 
        
        if (purposes.toString().isEmpty()) {
            purposes.append("ALL");    
        }
        
        return purposes.toString();
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getCombineGroupCodes(java.util.List)
     */
    public String getCombineGroupCodes(List<String> combineGroupCodes) {
        StringBuffer groupCodes = new StringBuffer();
        if (ObjectUtils.isNotNull(combineGroupCodes) && !combineGroupCodes.isEmpty()) {
            List<String> groupCodeList = kemidReportGroupDao.getAttributeValues(EndowPropertyConstants.KEMID_REPORT_GRP_CD, combineGroupCodes);
            for (String groupCode : groupCodeList) {
                groupCodes.append(groupCode).append(" ");
            }
        } 
        
        if (groupCodes.toString().isEmpty()) {
            groupCodes.append("ALL");    
        }
        
        return groupCodes.toString();
    }
    
    /**
     *     
     * @see org.kuali.kfs.module.endow.report.service.EndowmentReportService#createReportHeaderSheetData(java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.util.List, java.lang.String, java.lang.String)
     */
    public EndowmentReportHeaderDataHolder createReportHeaderSheetData(
                    List<String> kemidsSelected, 
                    List<String> benefittingOrganziationCampuses,
                    List<String> benefittingOrganziationCharts,
                    List<String> benefittingOrganziations,
                    List<String> typeCodes,
                    List<String> purposeCodes,
                    List<String> combineGroupCodes,
                    String reportName, 
                    String endowmnetOption) {
                
        EndowmentReportHeaderDataHolder reportRequestHeaderDataHolder = new EndowmentReportHeaderDataHolder();
        
        // get request report
        reportRequestHeaderDataHolder.setInstitutionName(getInstitutionName());
        reportRequestHeaderDataHolder.setReportRequested(reportName);
        reportRequestHeaderDataHolder.setRequestedBy(getReportRequestor());
        String endowmentOptionDesc = "";
        if ("B".equalsIgnoreCase(endowmnetOption)) {
            endowmentOptionDesc = EndowConstants.EndowmentReport.BOTH_ENDOWMENT_OPTION;
        } else {
            endowmentOptionDesc = EndowConstants.YES.equalsIgnoreCase(endowmnetOption) ? EndowConstants.EndowmentReport.ONLY_ENDOWMENT : EndowConstants.EndowmentReport.NON_ENDOWED;
        }
        reportRequestHeaderDataHolder.setEndowmentOption(endowmentOptionDesc);
        reportRequestHeaderDataHolder.setReportOption(""); 
        
        // get criteria
        reportRequestHeaderDataHolder.setBenefittingCampus(getBenefittingCampuses(benefittingOrganziationCampuses));
        reportRequestHeaderDataHolder.setBenefittingChart(getBenefittingCharts(benefittingOrganziationCharts));
        reportRequestHeaderDataHolder.setBenefittingOrganization(getBenefittingOrganizations(benefittingOrganziations));
        reportRequestHeaderDataHolder.setKemidTypeCode(getKemidTypeCodes(typeCodes));
        reportRequestHeaderDataHolder.setKemidPurposeCode(getKemidPurposeCodes(purposeCodes));
        reportRequestHeaderDataHolder.setCombineGroupCode(getCombineGroupCodes(combineGroupCodes));
        
        // get kemids selected
        reportRequestHeaderDataHolder.setKemidsSelected(kemidsSelected);

        // get kemdis with multiple benefittting organizations
        reportRequestHeaderDataHolder.setKemidsWithMultipleBenefittingOrganizationsDataHolders(getKemidsWithMultipleBenefittingOrganizations(kemidsSelected));
        
        return reportRequestHeaderDataHolder;
    }
    
    public List<String> getKemidsByOtherCriteria(List<String> benefittingOrganziationCampusCodes, List<String> benefittingOrganziationChartCodes,
            List<String> benefittingOrganziationCodes, List<String> typeCodes, List<String> purposeCodes, List<String> combineGroupCodes) {
      
        // 4.1.9 - If any of the non-required criteria are left blank, ignore it as a criterion
        // The criteria should be the AND combination.
        
        Set<String> kemids = new HashSet<String>();
        
        if (ObjectUtils.isNotNull(benefittingOrganziationCampusCodes) && !benefittingOrganziationCampusCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByCampusCode(benefittingOrganziationCampusCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationChartCodes) && !benefittingOrganziationChartCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_BENE_CHRT_CD, benefittingOrganziationChartCodes));
        }
        if (ObjectUtils.isNotNull(benefittingOrganziationCodes) && !benefittingOrganziationCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidBenefittingOrganizationDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_BENE_ORG_CD, benefittingOrganziationCodes));
        }
        if (ObjectUtils.isNotNull(typeCodes) && !typeCodes.isEmpty()) {        
            retainCommonKemids(kemids, kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_TYPE_CODE, typeCodes));
        }
        if (ObjectUtils.isNotNull(purposeCodes) && !purposeCodes.isEmpty()) {
            retainCommonKemids(kemids, kemidDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_PRPS_CD, purposeCodes));
        }
        if (ObjectUtils.isNotNull(combineGroupCodes) && !combineGroupCodes.isEmpty()) {        
            retainCommonKemids(kemids, kemidReportGroupDao.getKemidsByAttribute(EndowPropertyConstants.KEMID_REPORT_GRP_CD, combineGroupCodes));
        }        
        
        return new ArrayList<String>(kemids);
    }
    
    /** 
     * Retains the kemids that are in common.  
     * 
     * @param kemids
     * @param list
     */
    public void retainCommonKemids(Set<String> kemids, List<String> list) {
        if (list != null && !list.isEmpty()) {
            if (kemids.isEmpty()) {
                kemids.addAll(list);
            } else {
                kemids.retainAll(list);
            }
        }
    }
    
    /**
     * method to remove kemids from the list of kemids where for each kemid, if
     * there is no record in END_HIST_CSH_T table
     * 
     * @param kemids
     * @param beginningDate
     * @param beginningDate
     * @return List<String> newKemids
     */
    public List<String> removeKemidsNotInHistoryCash(List<String> kemids, String beginningDate, String endingDate) {
        List<String> newKemids = new ArrayList();
        
        for (String kemid : kemids) {
            MonthEndDate beginningMED = getPreviousMonthEndDate(convertStringToDate(beginningDate));
            MonthEndDate endingMED = getMonthEndDate(convertStringToDate(endingDate));
            KemidHistoricalCash beginningHistoryCash = getKemidHistoricalCash(kemid, beginningMED.getMonthEndDateId());
            KemidHistoricalCash endingHistoryCash = getKemidHistoricalCash(kemid, endingMED.getMonthEndDateId());
            
            if (beginningHistoryCash != null && endingHistoryCash != null) {
                newKemids.add(kemid);
            }
        }
        
        return newKemids;
    }
    
    protected MonthEndDate getPreviousMonthEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -1);
        return getMonthEndDate(new java.sql.Date(calendar.getTimeInMillis()));
    }
    
    protected Date convertStringToDate(String stringDate) {        
        Date date = null;
        try {
            date = SpringContext.getBean(DateTimeService.class).convertToSqlDate(stringDate);
        } catch (ParseException e) {
        }        
        return date;
    }
    
    protected MonthEndDate getMonthEndDate(Date date) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.MONTH_END_DATE, date);
        return (MonthEndDate) businessObjectService.findByPrimaryKey(MonthEndDate.class, primaryKeys);
    }
    
    protected KemidHistoricalCash getKemidHistoricalCash(String kemid, KualiInteger medId) {
        Map<String,Object> primaryKeys = new HashMap<String,Object>();
        primaryKeys.put(EndowPropertyConstants.ENDOWMENT_HIST_CASH_KEMID, kemid);
        primaryKeys.put(EndowPropertyConstants.ENDOWMENT_HIST_CASH_MED_ID, medId);
        return (KemidHistoricalCash) businessObjectService.findByPrimaryKey(KemidHistoricalCash.class, primaryKeys);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.endow.report.service.TrialBalanceReportService#getKemidsWithMultipleBenefittingOrganizations(java.util.List)
     */
    public List<KemidsWithMultipleBenefittingOrganizationsDataHolder> getKemidsWithMultipleBenefittingOrganizations(List<String> kemids) {
        return kemidBenefittingOrganizationDao.getKemidsWithMultipleBenefittingOrganizations(kemids);
    }
    
    /**
     * 
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * 
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * 
     */
    public void setKemidDao(KemidDao kemidDao) {
        this.kemidDao = kemidDao;
    }

    /**
     * 
     */
    public void setKemidBenefittingOrganizationDao(KemidBenefittingOrganizationDao kemidBenefittingOrganizationDao) {
        this.kemidBenefittingOrganizationDao = kemidBenefittingOrganizationDao;
    }

    /**
     * 
     */
    public void setKemidReportGroupDao(KemidReportGroupDao kemidReportGroupDao) {
        this.kemidReportGroupDao = kemidReportGroupDao;
    }
}
