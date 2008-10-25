/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * This class...
 */
public class Budget extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 8113894775967293272L;
    private String documentNumber;
    private Long budgetParentTrackNumber;
    private String budgetName;
    private boolean institutionCostShareIndicator;
    private String budgetProgramAnnouncementNumber;
    private String budgetProjectDirectorUniversalIdentifier;
    private boolean budgetThirdPartyCostShareIndicator;
    private KualiDecimal budgetPersonnelInflationRate;
    private KualiDecimal budgetNonpersonnelInflationRate;
    private String electronicResearchAdministrationGrantNumber;
    private Long routeSheetTrackNumber;
    private String budgetFringeRateDescription;
    private boolean agencyModularIndicator;
    private String budgetAgencyNumber;
    private String federalPassThroughAgencyNumber;
    private String budgetProgramAnnouncementName;
    private String costShareFinChartOfAccountCd;
    private String costShareOrgCd;
    private String budgetTypeCodeText;

    private boolean agencyToBeNamedIndicator;
    private boolean projectDirectorToBeNamedIndicator;

    private Agency budgetAgency;
    private Agency federalPassThroughAgency;
    private ProjectDirector projectDirector;
    private Person person;
    private BudgetModular modularBudget;
    private List tasks;
    private List periods;
    private List fringeRates;
    private List graduateAssistantRates;
    private List nonpersonnelItems;
    private List personnel;
    private List institutionCostShareItems;
    private List thirdPartyCostShareItems;
    private List institutionCostSharePersonnelItems;

    private BudgetIndirectCost indirectCost;

    private List allUserAppointmentTasks;
    private List allUserAppointmentTaskPeriods;
    private List allInstitutionCostSharePeriods;
    private List allThirdPartyCostSharePeriods;
    private List<BudgetIndirectCostLookup> budgetIndirectCostLookups;

    public Budget() {
        super();

        budgetPersonnelInflationRate = new KualiDecimal(SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.DEFAULT_PERSONNEL_INFLATION_RATE));
        budgetNonpersonnelInflationRate = new KualiDecimal(SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.DEFAULT_NONPERSONNEL_INFLATION_RATE));

        tasks = new ArrayList();
        periods = new ArrayList();
        fringeRates = new ArrayList();
        graduateAssistantRates = new ArrayList();
        nonpersonnelItems = new ArrayList();
        personnel = new ArrayList();
        institutionCostShareItems = new ArrayList();
        thirdPartyCostShareItems = new ArrayList();
        institutionCostSharePersonnelItems = new ArrayList();
        budgetIndirectCostLookups = new ArrayList<BudgetIndirectCostLookup>();

    }

    public Budget(String documentNumber) {
        this();
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the budgetAgencyNumber attribute.
     * 
     * @return Returns the budgetAgencyNumber.
     */
    public String getBudgetAgencyNumber() {
        return budgetAgencyNumber;
    }

    /**
     * Sets the budgetAgencyNumber attribute value.
     * 
     * @param budgetAgencyNumber The budgetAgencyNumber to set.
     */
    public void setBudgetAgencyNumber(String budgetAgencyNumber) {
        this.budgetAgencyNumber = budgetAgencyNumber;
    }

    /**
     * @return Returns the projectDirector.
     */
    public ProjectDirector getProjectDirector() {
        return projectDirector;
    }

    /**
     * @param projectDirector The projectDirector to set.
     */
    public void setProjectDirector(ProjectDirector projectDirector) {
        this.projectDirector = projectDirector;
    }


    /**
     * @return Returns the budgetFederalPassThroughIndicator.
     */
    /**
     * @return Returns the budgetFringeRateDescription.
     */
    public String getBudgetFringeRateDescription() {
        return budgetFringeRateDescription;
    }

    /**
     * @param budgetFringeRateDescription The budgetFringeRateDescription to set.
     */
    public void setBudgetFringeRateDescription(String budgetFringeRateDescription) {
        this.budgetFringeRateDescription = budgetFringeRateDescription;
    }

    /**
     * @return Returns the budgetName.
     */
    public String getBudgetName() {
        return budgetName;
    }

    /**
     * @param budgetName The budgetName to set.
     */
    public void setBudgetName(String budgetName) {
        this.budgetName = budgetName;
    }

    /**
     * @return Returns the budgetParentTrackNumber.
     */
    public Long getBudgetParentTrackNumber() {
        return budgetParentTrackNumber;
    }

    /**
     * @param budgetParentTrackNumber The budgetParentTrackNumber to set.
     */
    public void setBudgetParentTrackNumber(Long budgetParentTrackNumber) {
        this.budgetParentTrackNumber = budgetParentTrackNumber;
    }

    /**
     * @return Returns the budgetProgramAnnouncementNumber.
     */
    public String getBudgetProgramAnnouncementNumber() {
        return budgetProgramAnnouncementNumber;
    }

    /**
     * @param budgetProgramAnnouncementNumber The budgetProgramAnnouncementNumber to set.
     */
    public void setBudgetProgramAnnouncementNumber(String budgetProgramAnnouncementNumber) {
        this.budgetProgramAnnouncementNumber = budgetProgramAnnouncementNumber;
    }

    /**
     * @return Returns the budgetProjectDirectorUniversalIdentifier.
     */
    public String getBudgetProjectDirectorUniversalIdentifier() {
        return budgetProjectDirectorUniversalIdentifier;
    }

    /**
     * @param budgetProjectDirectorUniversalIdentifier The budgetProjectDirectorUniversalIdentifier to set.
     */
    public void setBudgetProjectDirectorUniversalIdentifier(String budgetProjectDirectorUniversalIdentifier) {
        this.budgetProjectDirectorUniversalIdentifier = budgetProjectDirectorUniversalIdentifier;
    }

    /**
     * @return Returns the agencyModularIndicator.
     */
    public boolean isAgencyModularIndicator() {
        return agencyModularIndicator;
    }

    /**
     * @param agencyModularIndicator The agencyModularIndicator to set.
     */
    public void setAgencyModularIndicator(boolean agencyModularIndicator) {
        this.agencyModularIndicator = agencyModularIndicator;
    }

    /**
     * @return Returns the budgetNonpersonnelInflationRate.
     */
    public KualiDecimal getBudgetNonpersonnelInflationRate() {
        return budgetNonpersonnelInflationRate;
    }

    /**
     * @param budgetNonpersonnelInflationRate The budgetNonpersonnelInflationRate to set.
     */
    public void setBudgetNonpersonnelInflationRate(KualiDecimal budgetNonpersonnelInflationRate) {
        this.budgetNonpersonnelInflationRate = budgetNonpersonnelInflationRate;
    }

    /**
     * @return Returns the budgetPersonnelInflationRate.
     */
    public KualiDecimal getBudgetPersonnelInflationRate() {
        return budgetPersonnelInflationRate;
    }

    /**
     * @param budgetPersonnelInflationRate The budgetPersonnelInflationRate to set.
     */
    public void setBudgetPersonnelInflationRate(KualiDecimal budgetPersonnelInflationRate) {
        this.budgetPersonnelInflationRate = budgetPersonnelInflationRate;
    }

    /**
     * @return Returns the budgetThirdPartyCostShareIndicator.
     */
    public boolean isBudgetThirdPartyCostShareIndicator() {
        return budgetThirdPartyCostShareIndicator;
    }

    /**
     * @param budgetThirdPartyCostShareIndicator The budgetThirdPartyCostShareIndicator to set.
     */
    public void setBudgetThirdPartyCostShareIndicator(boolean budgetThirdPartyCostShareIndicator) {
        this.budgetThirdPartyCostShareIndicator = budgetThirdPartyCostShareIndicator;
    }

    /**
     * @return Returns the institutionCostShareIndicator.
     */
    public boolean isInstitutionCostShareIndicator() {
        return institutionCostShareIndicator;
    }

    /**
     * @param institutionCostShareIndicator The institutionCostShareIndicator to set.
     */
    public void setInstitutionCostShareIndicator(boolean institutionCostShareIndicator) {
        this.institutionCostShareIndicator = institutionCostShareIndicator;
    }

    /**
     * @return Returns the electronicResearchAdministrationGrantNumber.
     */
    public String getElectronicResearchAdministrationGrantNumber() {
        return electronicResearchAdministrationGrantNumber;
    }

    /**
     * @param electronicResearchAdministrationGrantNumber The electronicResearchAdministrationGrantNumber to set.
     */
    public void setElectronicResearchAdministrationGrantNumber(String electronicResearchAdministrationGrantNumber) {
        this.electronicResearchAdministrationGrantNumber = electronicResearchAdministrationGrantNumber;
    }

    /**
     * @return Returns the periods.
     */
    public List<BudgetPeriod> getPeriods() {
        return periods;
    }

    /**
     * @param periods The periods to set.
     */
    public void setPeriods(List periods) {
        this.periods = periods;
    }

    /**
     * Retrieve a particular task at a given index in the list of tasks.
     * 
     * @param index
     * @return
     */
    public BudgetPeriod getPeriod(int index) {
        while (getPeriods().size() <= index) {
            getPeriods().add(new BudgetPeriod());
        }
        return (BudgetPeriod) getPeriods().get(index);
    }

    /**
     * @return Returns the routeSheetTrackNumber.
     */
    public Long getRouteSheetTrackNumber() {
        return routeSheetTrackNumber;
    }

    /**
     * @param routeSheetTrackNumber The routeSheetTrackNumber to set.
     */
    public void setRouteSheetTrackNumber(Long routeSheetTrackNumber) {
        this.routeSheetTrackNumber = routeSheetTrackNumber;
    }

    /**
     * @return Returns the tasks.
     */
    public List<BudgetTask> getTasks() {
        return tasks;
    }

    /**
     * @param tasks The tasks to set.
     */
    public void setTasks(List tasks) {
        this.tasks = tasks;
    }

    /**
     * Retrieve a particular task at a given index in the list of tasks.
     * 
     * @param index
     * @return
     */
    public BudgetTask getTask(int index) {
        while (getTasks().size() <= index) {
            getTasks().add(new BudgetTask());
        }
        return (BudgetTask) getTasks().get(index);
    }

    /**
     * @return Returns the budgetAgency.
     */
    public Agency getBudgetAgency() {
        return budgetAgency;
    }

    /**
     * @param budgetAgency The budgetAgency to set.
     */
    public void setBudgetAgency(Agency budgetAgency) {
        this.budgetAgency = budgetAgency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        LinkedHashMap map = new LinkedHashMap();
        map.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.Document#populateDocumentForRouting()
     */
    public void populateDocumentForRouting() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.bo.Document#getDocumentTitle()
     */
    public String getDocumentTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @return Returns the fringeRates.
     */
    public List<BudgetFringeRate> getFringeRates() {
        return fringeRates;
    }

    /**
     * @param fringeRates The fringeRates to set.
     */
    public void setFringeRates(List fringeRates) {
        this.fringeRates = fringeRates;
    }

    /**
     * Retrieve a particular fringe rate at a given index in the list of rates.
     * 
     * @param index
     * @return
     */
    public BudgetFringeRate getFringeRate(int index) {
        while (getFringeRates().size() <= index) {
            getFringeRates().add(new BudgetFringeRate());
        }
        return (BudgetFringeRate) getFringeRates().get(index);
    }

    /**
     * @return Returns the graduate assistant rates
     */
    public List<BudgetGraduateAssistantRate> getGraduateAssistantRates() {
        return graduateAssistantRates;
    }

    /**
     * @param graduateAssistantRates The graduate assistant rates to set
     */
    public void setGraduateAssistantRates(List graduateAssistantRates) {
        this.graduateAssistantRates = graduateAssistantRates;
    }

    /**
     * Retreive a particular graduate assistant rate at a given index in the list of rates.
     * 
     * @param index
     * @return
     */
    public BudgetGraduateAssistantRate getGraduateAssistantRate(int index) {
        while (getGraduateAssistantRates().size() <= index) {
            getGraduateAssistantRates().add(new BudgetGraduateAssistantRate());
        }
        return (BudgetGraduateAssistantRate) getGraduateAssistantRates().get(index);
    }


    /**
     * Gets the nonpersonnelItems attribute.
     * 
     * @return Returns the nonpersonnelItems.
     */
    public List<BudgetNonpersonnel> getNonpersonnelItems() {
        return nonpersonnelItems;
    }

    /**
     * Sets the nonpersonnelItems attribute value.
     * 
     * @param nonpersonnelItems The nonpersonnelItems to set.
     */
    public void setNonpersonnelItems(List nonpersonnelItems) {
        this.nonpersonnelItems = nonpersonnelItems;
    }

    /**
     * Retreive a particular nonpersonnelitem at the given index in the list of personnel.
     * 
     * @param index
     * @return
     */
    public BudgetNonpersonnel getNonpersonnelItem(int index) {
        while (getNonpersonnelItems().size() <= index) {
            getNonpersonnelItems().add(new BudgetNonpersonnel());
        }
        return (BudgetNonpersonnel) getNonpersonnelItems().get(index);
    }

    /**
     * Gets the federalPassThroughAgency attribute.
     * 
     * @return Returns the federalPassThroughAgency.
     */
    public Agency getFederalPassThroughAgency() {
        return federalPassThroughAgency;
    }

    /**
     * Sets the federalPassThroughAgency attribute value.
     * 
     * @param federalPassThroughAgency The federalPassThroughAgency to set.
     */
    public void setFederalPassThroughAgency(Agency federalPassThroughAgency) {
        this.federalPassThroughAgency = federalPassThroughAgency;
    }

    /**
     * Gets the federalPassThroughAgencyNumber attribute.
     * 
     * @return Returns the federalPassThroughAgencyNumber.
     */
    public String getFederalPassThroughAgencyNumber() {
        return federalPassThroughAgencyNumber;
    }

    /**
     * Sets the federalPassThroughAgencyNumber attribute value.
     * 
     * @param federalPassThroughAgencyNumber The federalPassThroughAgencyNumber to set.
     */
    public void setFederalPassThroughAgencyNumber(String federalPassThroughAgencyNumber) {
        this.federalPassThroughAgencyNumber = federalPassThroughAgencyNumber;
    }

    /**
     * Gets the personnel attribute.
     * 
     * @return Returns the personnel.
     */
    public List<BudgetUser> getPersonnel() {
        return personnel;
    }

    /**
     * Sets the personnel attribute value.
     * 
     * @param personnel The personnel to set.
     */
    public void setPersonnel(List personnel) {
        this.personnel = personnel;
    }

    /**
     * Retreive a particular person at the given index in the list of personnel.
     * 
     * @param index
     * @return
     */
    public BudgetUser getPersonFromList(int index) {
        while (getPersonnel().size() <= index) {
            getPersonnel().add(new BudgetUser());
        }
        return (BudgetUser) getPersonnel().get(index);
    }

    /**
     * Gets the modularBudget attribute.
     * 
     * @return Returns the modularBudget.
     */
    public BudgetModular getModularBudget() {
        return modularBudget;
    }

    /**
     * Sets the modularBudget attribute value.
     * 
     * @param modularBudget The modularBudget to set.
     */
    public void setModularBudget(BudgetModular modularBudget) {
        this.modularBudget = modularBudget;
    }

    /**
     * Gets the budgetProgramAnnouncementName attribute.
     * 
     * @return Returns the budgetProgramAnnouncementName.
     */
    public String getBudgetProgramAnnouncementName() {
        return budgetProgramAnnouncementName;
    }

    /**
     * Sets the budgetProgramAnnouncementName attribute value.
     * 
     * @param budgetProgramAnnouncementName The budgetProgramAnnouncementName to set.
     */
    public void setBudgetProgramAnnouncementName(String budgetProgramAnnouncementName) {
        this.budgetProgramAnnouncementName = budgetProgramAnnouncementName;
    }

    /**
     * @return Returns the costShareFinChartOfAccountCd.
     */
    public String getCostShareFinChartOfAccountCd() {
        return costShareFinChartOfAccountCd;
    }

    /**
     * @param costShareFinChartOfAccountCd The costShareFinChartOfAccountCd to set.
     */
    public void setCostShareFinChartOfAccountCd(String costShareFinChartOfAccountCd) {
        this.costShareFinChartOfAccountCd = costShareFinChartOfAccountCd;
    }

    /**
     * @return Returns the costShareOrgCd.
     */
    public String getCostShareOrgCd() {
        return costShareOrgCd;
    }

    /**
     * @param costShareOrgCd The costShareOrgCd to set.
     */
    public void setCostShareOrgCd(String costShareOrgCd) {
        this.costShareOrgCd = costShareOrgCd;
    }

    /**
     * @return Returns the budgetTypeCodeText.
     */
    public String getBudgetTypeCodeText() {
        return budgetTypeCodeText;
    }

    /**
     * @param budgetTypeCodeText The budgetTypeCodeText to set.
     */
    public void setBudgetTypeCodeText(String budgetTypeCodeText) {
        this.budgetTypeCodeText = budgetTypeCodeText;
    }

    /**
     * Getters & setters for BudgetTypeCodes in array form - for checkboxes
     * 
     * @return String[]
     */
    public String[] getBudgetTypeCodeArray() {
        String[] array = this.getBudgetTypeCodeText().split("-");
        return array;
    }

    public void addBudgetTypeCode(String budgetTypeCode) {
        this.setBudgetTypeCodeText(this.getBudgetTypeCodeText() + "-" + budgetTypeCode);
    }

    public void setBudgetTypeCodeArray(String[] budgetTypeCodeArray) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < budgetTypeCodeArray.length; i++) {
            if (i != 0) {
                sb.append("-");
            }
            sb.append(budgetTypeCodeArray[i]);
        }
        this.setBudgetTypeCodeText(sb.toString());
    }

    /**
     * @return Returns the institutionCostShareItems.
     */
    public List<BudgetInstitutionCostShare> getInstitutionCostShareItems() {
        return institutionCostShareItems;
    }

    public BudgetInstitutionCostShare getInstitutionCostShareItem(int index) {
        while (getInstitutionCostShareItems().size() <= index) {
            getInstitutionCostShareItems().add(new BudgetInstitutionCostShare());
        }
        return (BudgetInstitutionCostShare) getInstitutionCostShareItems().get(index);
    }

    /**
     * @param institutionCostShareItems The institutionCostShareItems to set.
     */
    public void setInstitutionCostShareItems(List institutionCostShareItems) {
        this.institutionCostShareItems = institutionCostShareItems;
    }

    /**
     * @return Returns the agencyToBeNamedIndicator.
     */
    public boolean isAgencyToBeNamedIndicator() {
        return agencyToBeNamedIndicator;
    }

    /**
     * @param agencyToBeNamedIndicator The agencyToBeNamedIndicator to set.
     */
    public void setAgencyToBeNamedIndicator(boolean agencyToBeNamedIndicator) {
        this.agencyToBeNamedIndicator = agencyToBeNamedIndicator;
    }

    /**
     * @return Returns the projectDirectorToBeNamedIndicator.
     */
    public boolean isProjectDirectorToBeNamedIndicator() {
        return projectDirectorToBeNamedIndicator;
    }

    /**
     * @param projectDirectorToBeNamedIndicator The projectDirectorToBeNamedIndicator to set.
     */
    public void setProjectDirectorToBeNamedIndicator(boolean projectDirectorToBeNamedIndicator) {
        this.projectDirectorToBeNamedIndicator = projectDirectorToBeNamedIndicator;
    }

    /**
     * @return budgetIndirectCost;
     */
    public BudgetIndirectCost getIndirectCost() {
        return indirectCost;
    }

    /**
     * @param budgetIndirectCost
     */
    public void setIndirectCost(BudgetIndirectCost indirectCost) {
        this.indirectCost = indirectCost;
    }

    public void setAllUserAppointmentTasks(List list) {
        this.allUserAppointmentTasks = list;
    }

    public List getAllUserAppointmentTasks() {
        return getAllUserAppointmentTaskPeriods(false);
    }

    public List getAllUserAppointmentTasks(boolean forceRefreshPriorToSave) {
        if (allUserAppointmentTasks == null) {
            List list = new ArrayList();
            for (Iterator i = personnel.iterator(); i.hasNext();) {
                BudgetUser budgetUser = (BudgetUser) i.next();
                if (forceRefreshPriorToSave) {
                    budgetUser = new BudgetUser(budgetUser);
                    budgetUser.refreshReferenceObject("userAppointmentTasks");
                }
                list.addAll(budgetUser.getUserAppointmentTasks());
            }
            return list;
        }
        else {
            return allUserAppointmentTasks;
        }
    }


    public void setAllUserAppointmentTaskPeriods(List list) {
        this.allUserAppointmentTaskPeriods = list;
    }

    public List getAllUserAppointmentTaskPeriods() {
        return getAllUserAppointmentTaskPeriods(false);
    }

    public List<UserAppointmentTaskPeriod> getAllUserAppointmentTaskPeriods(boolean forceRefreshPriorToSave) {
        if (allUserAppointmentTaskPeriods == null) {
            List list = new ArrayList();
            for (Iterator i = getAllUserAppointmentTasks(forceRefreshPriorToSave).iterator(); i.hasNext();) {
                UserAppointmentTask userAppointmentTask = (UserAppointmentTask) i.next();
                if (forceRefreshPriorToSave) {
                    userAppointmentTask = new UserAppointmentTask(userAppointmentTask);
                    userAppointmentTask.refreshReferenceObject("userAppointmentTaskPeriods");
                }
                list.addAll(userAppointmentTask.getUserAppointmentTaskPeriods());
            }
            return list;
        }
        else {
            return allUserAppointmentTaskPeriods;
        }
    }

    /**
     * @return Returns the thirdPartyCostShareItems.
     */
    public List<BudgetThirdPartyCostShare> getThirdPartyCostShareItems() {
        return thirdPartyCostShareItems;
    }

    public BudgetThirdPartyCostShare getThirdPartyCostShareItem(int index) {
        while (getThirdPartyCostShareItems().size() <= index) {
            getThirdPartyCostShareItems().add(new BudgetThirdPartyCostShare());
        }
        return (BudgetThirdPartyCostShare) getThirdPartyCostShareItems().get(index);
    }

    /**
     * @param thirdPartyCostShareItems The thirdPartyCostShareItems to set.
     */
    public void setThirdPartyCostShareItems(List thirdPartyCostShareItems) {
        this.thirdPartyCostShareItems = thirdPartyCostShareItems;
    }

    /**
     * @return Returns the allThirdPartyCostSharePeriods.
     */
    public List getAllThirdPartyCostSharePeriods() {
        return allThirdPartyCostSharePeriods;
    }

    /**
     * @param allThirdPartyCostSharePeriods The allThirdPartyCostSharePeriods to set.
     */
    public void setAllThirdPartyCostSharePeriods(List allThirdPartyCostSharePeriods) {
        this.allThirdPartyCostSharePeriods = allThirdPartyCostSharePeriods;
    }


    public List getAllThirdPartyCostSharePeriods(boolean forceRefreshPriorToSave) {
        if (allThirdPartyCostSharePeriods == null) {
            List list = new ArrayList();
            for (Iterator i = thirdPartyCostShareItems.iterator(); i.hasNext();) {
                BudgetThirdPartyCostShare costShareItem = (BudgetThirdPartyCostShare) i.next();
                if (forceRefreshPriorToSave) {
                    costShareItem = new BudgetThirdPartyCostShare(costShareItem);
                    costShareItem.refreshReferenceObject("budgetPeriodCostShare");
                }
                list.addAll(costShareItem.getBudgetPeriodCostShare());
            }
            return list;
        }
        else {
            return allThirdPartyCostSharePeriods;
        }
    }

    /**
     * @return Returns the allInstitutionCostSharePeriods.
     */
    public List getAllInstitutionCostSharePeriods() {
        return allInstitutionCostSharePeriods;
    }

    /**
     * @param allInstitutionCostSharePeriods The allInstitutionCostSharePeriods to set.
     */
    public void setAllInstitutionCostSharePeriods(List allInstitutionCostSharePeriods) {
        this.allInstitutionCostSharePeriods = allInstitutionCostSharePeriods;
    }


    public List getAllInstitutionCostSharePeriods(boolean forceRefreshPriorToSave) {
        if (allInstitutionCostSharePeriods == null) {
            List list = new ArrayList();
            for (Iterator i = institutionCostShareItems.iterator(); i.hasNext();) {
                BudgetInstitutionCostShare costShareItem = (BudgetInstitutionCostShare) i.next();
                if (forceRefreshPriorToSave) {
                    costShareItem = new BudgetInstitutionCostShare(costShareItem);
                    costShareItem.refreshReferenceObject("budgetPeriodCostShare");
                }
                list.addAll(costShareItem.getBudgetPeriodCostShare());
            }
            return list;
        }
        else {
            return allInstitutionCostSharePeriods;
        }
    }

    /**
     * @return Returns the institutionCostSharePersonnelItems.
     */
    public List getInstitutionCostSharePersonnelItems() {
        return institutionCostSharePersonnelItems;
    }

    public InstitutionCostSharePersonnel getInstitutionCostSharePersonnelItem(int index) {
        while (getInstitutionCostSharePersonnelItems().size() <= index) {
            getInstitutionCostSharePersonnelItems().add(new InstitutionCostSharePersonnel());
        }
        return (InstitutionCostSharePersonnel) getInstitutionCostSharePersonnelItems().get(index);
    }

    /**
     * @param institutionCostSharePersonnelItems The institutionCostSharePersonnelItems to set.
     */
    public void setInstitutionCostSharePersonnelItems(List institutionCostSharePersonnelItems) {
        this.institutionCostSharePersonnelItems = institutionCostSharePersonnelItems;
    }

    public Date getDefaultNextPeriodBeginDate() {
        if (this.getPeriods().size() > 0) {
            BudgetPeriod lastPeriod = (BudgetPeriod) this.getPeriods().get(this.getPeriods().size() - 1);
            if (lastPeriod.getBudgetPeriodEndDate() != null) {
                Date oldEndDate = lastPeriod.getBudgetPeriodEndDate();
                Calendar oldEndCal = new GregorianCalendar();
                oldEndCal.setTime(oldEndDate);
                oldEndCal.add(Calendar.DATE, 1);
                return new Date(oldEndCal.getTimeInMillis());
            }
        }
        return null;
    }

    public void setBudgetIndirectCostLookups(List<BudgetIndirectCostLookup> budgetIndirectCostLookupList) {
        this.budgetIndirectCostLookups = budgetIndirectCostLookupList;
    }

    public List<BudgetIndirectCostLookup> getBudgetIndirectCostLookups() {
        return this.budgetIndirectCostLookups;
    }

    public BudgetIndirectCostLookup getBudgetIndirectCostLookup(int index) {
        while (this.getBudgetIndirectCostLookups().size() <= index) {
            this.getBudgetIndirectCostLookups().add(new BudgetIndirectCostLookup());
        }
        return this.getBudgetIndirectCostLookups().get(index);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

