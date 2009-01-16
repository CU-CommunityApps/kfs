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
package org.kuali.kfs.module.cg.document.web.struts;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetFringeRate;
import org.kuali.kfs.module.cg.businessobject.BudgetGraduateAssistantRate;
import org.kuali.kfs.module.cg.businessobject.BudgetInstitutionCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetNonpersonnel;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.businessobject.BudgetTask;
import org.kuali.kfs.module.cg.businessobject.BudgetThirdPartyCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetTypeCode;
import org.kuali.kfs.module.cg.businessobject.BudgetUser;
import org.kuali.kfs.module.cg.businessobject.NonPersonnelCategory;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.ResearchDocument;
import org.kuali.kfs.module.cg.document.service.BudgetPeriodService;
import org.kuali.kfs.module.cg.document.service.BudgetPersonnelService;
import org.kuali.kfs.module.cg.document.service.BudgetTaskService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.HeaderNavigation;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * This class is the action form for KRA Budget.
 */
public class BudgetForm extends ResearchDocumentFormBase {

    private static final long serialVersionUID = 1L;

    private HashMap appointmentTypeGridMappings;

    private transient String DEFAULT_BUDGET_TASK_NAME;
    private transient String TO_BE_NAMED_LABEL;

    private BudgetTask newTask;
    private BudgetPeriod newPeriod;
    private BudgetUser newPersonnel;
    private String newPersonnelType = "person";
    private BudgetFringeRate newFringeRate;
    private BudgetGraduateAssistantRate newGraduateAssistantRate;

    private Person initiator;

    private String[] deleteValues = new String[50];

    private List budgetTypeCodes;

    private List academicYearSubdivisionNames;
    private int numberOfAcademicYearSubdivisions;

    private List nonpersonnelCategories;
    private List newNonpersonnelList;
    private BudgetOverviewFormHelper budgetOverviewFormHelper;
    private BudgetNonpersonnelFormHelper budgetNonpersonnelFormHelper;
    private BudgetNonpersonnelCopyOverFormHelper budgetNonpersonnelCopyOverFormHelper;
    private BudgetIndirectCostFormHelper budgetIndirectCostFormHelper;

    private BudgetInstitutionCostShare newInstitutionCostShare;
    private BudgetThirdPartyCostShare newThirdPartyCostShare;
    private BudgetCostShareFormHelper budgetCostShareFormHelper;

    private Integer currentTaskNumber;
    private Integer previousTaskNumber;
    private Integer currentPeriodNumber;
    private Integer previousPeriodNumber;

    private String currentNonpersonnelCategoryCode;

    private String currentOutputReportType;
    private String currentOutputDetailLevel;
    private String currentOutputAgencyType;
    private String currentOutputAgencyPeriod; // can't use currentPeriodNumber because it has logic in getter

    private boolean supportsModular;
    private boolean auditActivated;
    private boolean includeAdHocPermissions;
    private boolean includeBudgetIdcRates;
    private boolean displayCostSharePermission;


    public BudgetForm() {
        super();

        newPeriod = new BudgetPeriod();
        newTask = new BudgetTask();
        newPersonnel = new BudgetUser();
        newFringeRate = new BudgetFringeRate();
        newGraduateAssistantRate = new BudgetGraduateAssistantRate();
        setDocument(new BudgetDocument());
        newInstitutionCostShare = new BudgetInstitutionCostShare();
        newThirdPartyCostShare = new BudgetThirdPartyCostShare();
        budgetTypeCodes = new ArrayList();
        nonpersonnelCategories = new ArrayList();
        newNonpersonnelList = new ArrayList();
        budgetNonpersonnelCopyOverFormHelper = new BudgetNonpersonnelCopyOverFormHelper();

        academicYearSubdivisionNames = new ArrayList();

        DataDictionary dataDictionary = SpringContext.getBean(DataDictionaryService.class).getDataDictionary();
        DocumentEntry budgetDocumentEntry = dataDictionary.getDocumentEntry(org.kuali.kfs.module.cg.document.BudgetDocument.class.getName());
        this.setHeaderNavigationTabs(budgetDocumentEntry.getHeaderNavigationList().toArray( new HeaderNavigation[] {} ));
    }

    @Override
    public ResearchDocument getResearchDocument() {
        return this.getBudgetDocument();
    }

    /**
     * Checks for methodToCall parameter, and if not populated in form calls utility method to parse the string from the request.
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);
        checkHeaderNavigation();
    }

    /**
     * <p>
     * This method resets the tab states, task number (depending on parameters) and period number.
     * </p>
     * 
     * @param resetTask determines whether task is to be reset.
     * @param resetPeriod determines whether period is to be reset.
     */
    public void newTabState(boolean resetTask, boolean resetPeriod) {
        this.setTabStates(new HashMap());
        if (resetTask)
            this.setCurrentTaskNumber(null);
        if (resetPeriod)
            this.setCurrentPeriodNumber(null);
    }

    public void checkHeaderNavigation() {
        if (!this.getBudgetDocument().getBudget().isAgencyModularIndicator()) {
            disableHeaderNavigation("modular");
        }
        else {
            enableHeaderNavigation("modular");
        }
    }

    public void processValidationFail() {
        Budget budget = this.getBudgetDocument().getBudget();
        for (Iterator i = budget.getPersonnel().iterator(); i.hasNext();) {
            BudgetUser budgetUser = (BudgetUser) i.next();
            if (budgetUser.getPreviousAppointmentTypeCode() != null) {
                budgetUser.setAppointmentTypeCode(budgetUser.getPreviousAppointmentTypeCode());
            }
            if (budgetUser.getPreviousTaskNumber() != null) {
                budgetUser.setCurrentTaskNumber(budgetUser.getPreviousTaskNumber());
            }
        }
    }
    
    public boolean shouldPropertyBePopulatedInForm(String requestParameterName, HttpServletRequest request) {
        //return super.shouldPropertyBePopulatedInForm(requestParameterName, request);
        return true;
    }
    
    public boolean shouldMethodToCallParameterBeUsed(String methodToCallParameterName, String methodToCallParameterValue, HttpServletRequest request) {
        //return super.shouldMethodToCallParameterBeUsed(methodToCallParameterName, methodToCallParameterValue, request);
        return true;
    }

    /**
     * @return Returns the internalBillingDocument.
     */
    public BudgetDocument getBudgetDocument() {
        return (BudgetDocument) getDocument();
    }

    /**
     * @param internalBillingDocument The internalBillingDocument to set.
     */
    public void setBudgetDocument(BudgetDocument budgetDocument) {
        setDocument(budgetDocument);
    }


    public boolean isIncludeAdHocPermissions() {
        return includeAdHocPermissions;
    }

    public void setIncludeAdHocPermissions(boolean include_ad_hocs) {
        this.includeAdHocPermissions = include_ad_hocs;
    }

    /**
     * Gets the includeBudgetIdcRates attribute.
     * 
     * @return Returns the includeBudgetIdcRates.
     */
    public boolean isIncludeBudgetIdcRates() {
        return includeBudgetIdcRates;
    }

    /**
     * Sets the includeBudgetIdcRates attribute value.
     * 
     * @param includeBudgetIdcRates The includeBudgetIdcRates to set.
     */
    public void setIncludeBudgetIdcRates(boolean includeBudgetIdcRates) {
        this.includeBudgetIdcRates = includeBudgetIdcRates;
    }

    /**
     * Gets the newBudgetPeriod attribute.
     * 
     * @return Returns the newBudgetPeriod.
     */
    public BudgetPeriod getNewPeriod() {
        return newPeriod;
    }

    /**
     * Sets the newBudgetPeriod attribute value.
     * 
     * @param newBudgetPeriod The newBudgetPeriod to set.
     */
    public void setNewPeriod(BudgetPeriod newBudgetPeriod) {
        if (newPeriod != null && newPeriod.getBudgetPeriodEndDate() != null) {
            Date oldEndDate = newPeriod.getBudgetPeriodEndDate();
            GregorianCalendar oldEndCal = new GregorianCalendar();
            oldEndCal.setTime(oldEndDate);
            oldEndCal.add(GregorianCalendar.DATE, 1);
            newBudgetPeriod.setBudgetPeriodBeginDate(new Date(oldEndCal.getTimeInMillis()));
        }
        this.newPeriod = newBudgetPeriod;
    }

    /**
     * Gets the newBudgetTask attribute.
     * 
     * @return Returns the newBudgetTask.
     */
    public BudgetTask getNewTask() {
        return newTask;
    }

    /**
     * Sets the newBudgetTask attribute value.
     * 
     * @param newBudgetTask The newBudgetTask to set.
     */
    public void setNewTask(BudgetTask newBudgetTask) {
        this.newTask = newBudgetTask;

        // Set our next task number to be whatever the task list size is + 1.
        // Integer nextSequenceNumber = new Integer(getBudgetDocument().getBudgetTaskNextSequenceNumber().intValue() + 1);

        // Now set the default TaskName for the new task in the addline.
        newTask.setBudgetTaskName(getDefaultBudgetTaskName() + " " + getBudgetDocument().getBudgetTaskNextSequenceNumber().toString());
    }
    
    /**
     * Retrieves the default budget task name
     * @return the default budget task name
     */
    protected String getDefaultBudgetTaskName() {
        if (StringUtils.isBlank(DEFAULT_BUDGET_TASK_NAME)) {
            DEFAULT_BUDGET_TASK_NAME = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.DEFAULT_BUDGET_TASK_NAME);
        }
        return DEFAULT_BUDGET_TASK_NAME;
    }

    /**
     * Gets the newNonpersonnel attribute.
     * 
     * @return Returns the newNonpersonnel.
     */
    public BudgetUser getNewPersonnel() {
        return newPersonnel;
    }

    /**
     * Sets the newNonpersonnel attribute value.
     * 
     * @param newNonpersonnel The newNonpersonnel to set.
     */
    public void setNewPersonnel(BudgetUser newPersonnel) {
        this.newPersonnel = newPersonnel;
    }

    /**
     * Gets the newFringeRate attribute.
     * 
     * @return Returns the newFringeRate.
     */
    public BudgetFringeRate getNewFringeRate() {
        return newFringeRate;
    }

    /**
     * Sets the newFringeRate attribute value.
     * 
     * @param newFringeRate The newFringRate to set.
     */
    public void setNewFringeRate(BudgetFringeRate newFringeRate) {
        this.newFringeRate = newFringeRate;
    }

    /**
     * @return Returns the newGraduateAssistantRate.
     */
    public BudgetGraduateAssistantRate getNewGraduateAssistantRate() {
        return newGraduateAssistantRate;
    }

    /**
     * @param newGraduateAssistantRate The newGraduateAssistantRate to set.
     */
    public void setNewGraduateAssistantRate(BudgetGraduateAssistantRate newGraduateAssistantRate) {
        this.newGraduateAssistantRate = newGraduateAssistantRate;
    }

    /**
     * Sets the newNonpersonnel attribute value.
     * 
     * @param newNonpersonnel The newNonpersonnel to set.
     */
    public void setNewNonpersonnelList(List newNonpersonnelList) {
        this.newNonpersonnelList = newNonpersonnelList;
    }

    public List getNewNonpersonnelList() {
        return newNonpersonnelList;
    }

    public BudgetNonpersonnel getNewNonpersonnel(int index) {
        while (getNewNonpersonnelList().size() <= index) {
            getNewNonpersonnelList().add(new BudgetNonpersonnel());
        }
        return (BudgetNonpersonnel) getNewNonpersonnelList().get(index);
    }

    /**
     * Gets the initiator attribute.
     * 
     * @return Returns the initiator.
     */
    public Person getInitiator() {
        return initiator;
    }

    /**
     * Sets the initiator attribute value.
     * 
     * @param initiator The initiator to set.
     */
    public void setInitiator(Person initiator) {
        this.initiator = initiator;
    }

    /**
     * Gets the initiator org code based on deptid.
     * 
     * @return Returns the user.
     */
    public String getInitiatorOrgCode() {
        if (this.getInitiator() != null) {
            ChartOrgHolder chartOrg = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).getOrganizationByNamespaceCode(this.getInitiator(),CGConstants.CG_NAMESPACE_CODE);
            if ( chartOrg != null ) {
                return chartOrg.getOrganizationCode();
            }
        }
        return "";
    }

    /**
     * @return Returns the newInstitutionCostShare.
     */
    public BudgetInstitutionCostShare getNewInstitutionCostShare() {
        return newInstitutionCostShare;
    }

    /**
     * @param newInstitutionCostShare The newInstitutionCostShare to set.
     */
    public void setNewInstitutionCostShare(BudgetInstitutionCostShare newInstitutionCostShare) {
        this.newInstitutionCostShare = newInstitutionCostShare;
    }

    /**
     * Gets the budgetTypeCodes attribute.
     * 
     * @return Returns the list of budgetTypeCodes.
     */
    public List getBudgetTypeCodes() {
        return budgetTypeCodes;
    }

    /**
     * Sets the budgetTypeCodes attribute value.
     * 
     * @param budgetTypes The budgetTypeCodes to set.
     */
    public void setBudgetTypeCodes(List budgetTypeCodes) {
        this.budgetTypeCodes = budgetTypeCodes;
    }

    /**
     * Gets the budgetTypeCode attribute.
     * 
     * @return Returns the budgetTypeCode.
     */
    public BudgetTypeCode getBudgetTypeCode(int index) {
        while (getBudgetTypeCodes().size() <= index) {
            getBudgetTypeCodes().add(new BudgetTypeCode());
        }
        return (BudgetTypeCode) getBudgetTypeCodes().get(index);
    }

    /**
     * Gets the nonpersonnelCategories attribute.
     * 
     * @return Returns the nonpersonnelCategories.
     */
    public List getNonpersonnelCategories() {
        return nonpersonnelCategories;
    }

    /**
     * Sets the nonpersonnelCategories attribute value.
     * 
     * @param nonpersonnelCategories The nonpersonnelCategories to set.
     */
    public void setNonpersonnelCategories(List nonpersonnelCategories) {
        this.nonpersonnelCategories = nonpersonnelCategories;
    }

    /**
     * Gets the newNonpersonnel attribute.
     * 
     * @return Returns the newNonpersonnel.
     */
    public NonPersonnelCategory getNonpersonnelCategory(int index) {
        while (getNonpersonnelCategories().size() <= index) {
            getNonpersonnelCategories().add(new NonPersonnelCategory());
        }
        return (NonPersonnelCategory) getNonpersonnelCategories().get(index);
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        return super.validate(mapping, request);
    }

    /**
     * Gets the budgetOverviewFormHelper attribute.
     * 
     * @return Returns the budgetOverviewFormHelper.
     */
    public BudgetOverviewFormHelper getBudgetOverviewFormHelper() throws Exception {
        if (budgetOverviewFormHelper == null) {
            budgetOverviewFormHelper = new BudgetOverviewFormHelper(this);
        }
        return budgetOverviewFormHelper;
    }

    /**
     * Sets the budgetOverviewFormHelper attribute value.
     * 
     * @param budgetOverviewFormHelper The budgetOverviewFormHelper to set.
     */
    public void setBudgetOverviewFormHelper(BudgetOverviewFormHelper budgetOverviewFormHelper) {
        this.budgetOverviewFormHelper = budgetOverviewFormHelper;
    }

    /**
     * Gets the budgetNonpersonnelFormHelper attribute.
     * 
     * @return Returns the budgetNonpersonnelFormHelper.
     */
    public BudgetNonpersonnelFormHelper getBudgetNonpersonnelFormHelper() {
        if (budgetNonpersonnelFormHelper == null) {
            budgetNonpersonnelFormHelper = new BudgetNonpersonnelFormHelper(this);
        }
        return budgetNonpersonnelFormHelper;
    }

    /**
     * Sets the budgetNonpersonnelFormHelper attribute value.
     * 
     * @param budgetNonpersonnelFormHelper The budgetNonpersonnelFormHelper to set.
     */
    public void setBudgetNonpersonnelFormHelper(BudgetNonpersonnelFormHelper budgetNonpersonnelFormHelper) {
        this.budgetNonpersonnelFormHelper = budgetNonpersonnelFormHelper;
    }

    /**
     * Gets the budgetNonpersonnelCopyOverFormHelper attribute.
     * 
     * @return Returns the budgetNonpersonnelCopyOverFormHelper.
     */
    public BudgetNonpersonnelCopyOverFormHelper getBudgetNonpersonnelCopyOverFormHelper() {
        return budgetNonpersonnelCopyOverFormHelper;
    }

    /**
     * Sets the budgetNonpersonnelCopyOverFormHelper attribute value.
     * 
     * @param budgetNonpersonnelCopyOverFormHelper The budgetNonpersonnelCopyOverFormHelper to set.
     */
    public void setBudgetNonpersonnelCopyOverFormHelper(BudgetNonpersonnelCopyOverFormHelper budgetNonpersonnelCopyOverFormHelper) {
        this.budgetNonpersonnelCopyOverFormHelper = budgetNonpersonnelCopyOverFormHelper;
    }

    /**
     * Gets the deleteValues attribute.
     * 
     * @return Returns the deleteValues.
     */
    public String[] getDeleteValues() {
        return deleteValues;
    }

    /**
     * Sets the deleteValues attribute value.
     * 
     * @param deleteValues The deleteValues to set.
     */
    public void setDeleteValues(String[] deleteValues) {
        this.deleteValues = deleteValues;
    }

    /**
     * @return Returns the budgetCostShareFormHelper.
     */
    public BudgetCostShareFormHelper getBudgetCostShareFormHelper() throws Exception {
        if (budgetCostShareFormHelper == null) {
            budgetCostShareFormHelper = new BudgetCostShareFormHelper(this);
        }
        return budgetCostShareFormHelper;
    }

    /**
     * @param budgetCostShareFormHelper The budgetCostShareFormHelper to set.
     */
    public void setBudgetCostShareFormHelper(BudgetCostShareFormHelper budgetCostShareFormHelper) {
        this.budgetCostShareFormHelper = budgetCostShareFormHelper;
    }

    /**
     * @return Returns the currentPeriodNumber.
     */
    public Integer getCurrentPeriodNumber() {
        if (currentPeriodNumber == null) {
            currentPeriodNumber = SpringContext.getBean(BudgetPeriodService.class).getFirstBudgetPeriod(((BudgetDocument) getDocument()).getDocumentNumber()).getBudgetPeriodSequenceNumber();
        }
        return currentPeriodNumber;
    }

    /**
     * @param currentPeriodNumber The currentPeriodNumber to set.
     */
    public void setCurrentPeriodNumber(Integer currentPeriodNumber) {
        this.currentPeriodNumber = currentPeriodNumber;
    }

    /**
     * @return Returns the currentTaskNumber.
     */
    public Integer getCurrentTaskNumber() {
        if (currentTaskNumber == null) {
            currentTaskNumber = SpringContext.getBean(BudgetTaskService.class).getFirstBudgetTask(((BudgetDocument) getDocument()).getDocumentNumber()).getBudgetTaskSequenceNumber();
        }
        return currentTaskNumber;
    }

    /**
     * @param currentTaskNumber The currentTaskNumber to set.
     */
    public void setCurrentTaskNumber(Integer currentTaskNumber) {
        this.currentTaskNumber = currentTaskNumber;
    }

    /**
     * @return Returns the currentNonpersonnelCategoryCode.
     */
    public String getCurrentNonpersonnelCategoryCode() {
        return currentNonpersonnelCategoryCode;
    }

    /**
     * @param currentNonpersonnelCategoryCode The currentNonpersonnelCategoryCode to set.
     */
    public void setCurrentNonpersonnelCategoryCode(String currentNonpersonnelCategoryCode) {
        this.currentNonpersonnelCategoryCode = currentNonpersonnelCategoryCode;
    }

    @Override
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        if (this.getBudgetDocument().getBudget().isProjectDirectorToBeNamedIndicator()) {
            getDocInfo().add(new HeaderField("DataDictionary.Budget.attributes.budgetProjectDirectorUniversalIdentifier", getToBeNamedLabel()));
        }
        else if (!ObjectUtils.isNull(this.getBudgetDocument().getBudget().getProjectDirector()) && !ObjectUtils.isNull(this.getBudgetDocument().getBudget().getProjectDirector().getPerson()) && !ObjectUtils.isNull(this.getBudgetDocument().getBudget().getProjectDirector().getPerson().getPrincipalId())) {
            getDocInfo().add(new HeaderField("DataDictionary.Budget.attributes.budgetProjectDirectorUniversalIdentifier", this.getBudgetDocument().getBudget().getProjectDirector().getPerson().getName()));
        }
        if (this.getBudgetDocument().getBudget().isAgencyToBeNamedIndicator()) {
            getDocInfo().add(new HeaderField("DataDictionary.Budget.attributes.budgetAgency", getToBeNamedLabel()));
        }
        else if (!ObjectUtils.isNull(this.getBudgetDocument().getBudget().getBudgetAgency())) {
            getDocInfo().add(new HeaderField("DataDictionary.Budget.attributes.budgetAgency", this.getBudgetDocument().getBudget().getBudgetAgency().getFullName()));
        }
    }
    
    /**
     * Retrieves the "to be named" label
     * @return the "to be named" label
     */
    protected String getToBeNamedLabel() {
        if (StringUtils.isBlank(TO_BE_NAMED_LABEL)) {
            TO_BE_NAMED_LABEL = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.CONTRACTS_AND_GRANTS_DOCUMENT.class, CGConstants.TO_BE_NAMED_LABEL);
        }
        return TO_BE_NAMED_LABEL;
    }

    /**
     * Gets the previousPeriodNumber attribute.
     * 
     * @return Returns the previousPeriodNumber.
     */
    public Integer getPreviousPeriodNumber() {
        return previousPeriodNumber;
    }

    /**
     * Sets the previousPeriodNumber attribute value.
     * 
     * @param previousPeriodNumber The previousPeriodNumber to set.
     */
    public void setPreviousPeriodNumber(Integer previousPeriodNumber) {
        this.previousPeriodNumber = previousPeriodNumber;
    }

    /**
     * Gets the previousTaskNumber attribute.
     * 
     * @return Returns the previousTaskNumber.
     */
    public Integer getPreviousTaskNumber() {
        return previousTaskNumber;
    }

    /**
     * Sets the previousTaskNumber attribute value.
     * 
     * @param previousTaskNumber The previousTaskNumber to set.
     */
    public void setPreviousTaskNumber(Integer previousTaskNumber) {
        this.previousTaskNumber = previousTaskNumber;
    }

    /**
     * Gets the appointmentTypeGridMappings attribute.
     * 
     * @return Returns the appointmentTypeGridMappings.
     */
    public HashMap getAppointmentTypeGridMappings() {
        if (this.appointmentTypeGridMappings == null) {
            this.appointmentTypeGridMappings = SpringContext.getBean(BudgetPersonnelService.class).getAppointmentTypeMappings();
        }
        return this.appointmentTypeGridMappings;
    }

    /**
     * This method sorts collections that are contained within the document/budget. this is only used for the UI, and thus occurs
     * vie the form
     */
    public void sortCollections() {
        Collections.sort(this.getBudgetDocument().getBudget().getPersonnel());
        Collections.sort(this.getBudgetDocument().getBudget().getGraduateAssistantRates());

    }

    public String getAcademicYearSubdivisionNamesString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = getAcademicYearSubdivisionNames().iterator(); iter.hasNext();) {
            sb.append((String) iter.next());
            if (iter.hasNext()) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    public void setAcademicYearSubdivisionNamesString(String names) {
        setAcademicYearSubdivisionNames(Arrays.asList(names.split("-")));
    }

    /**
     * Gets the academicYearSubdivisionNames attribute.
     * 
     * @return Returns the academicYearSubdivisionNames.
     */
    public List getAcademicYearSubdivisionNames() {
        return academicYearSubdivisionNames;
    }

    /**
     * Sets the academicYearSubdivisionNames attribute value.
     * 
     * @param academicYearSubdivisionNames The academicYearSubdivisionNames to set.
     */
    public void setAcademicYearSubdivisionNames(List academicYearSubdivisionNames) {
        this.academicYearSubdivisionNames = academicYearSubdivisionNames;
    }

    /**
     * Gets the number of academic year subdivisions.
     * 
     * @return Returns the number of academic year subdivisions.
     */
    public int getNumberOfAcademicYearSubdivisions() {
        return this.numberOfAcademicYearSubdivisions;
    }

    /**
     * Sets the numberOfAcademicYearSubdivisions attribute value.
     * 
     * @param numberOfAcademicYearSubdivisions The numberOfAcademicYearSubdivisions to set.
     */
    public void setNumberOfAcademicYearSubdivisions(int numberOfAcademicYearSubdivisions) {
        this.numberOfAcademicYearSubdivisions = numberOfAcademicYearSubdivisions;
    }


    /**
     * @return Returns the newThirdPartyCostShare.
     */
    public BudgetThirdPartyCostShare getNewThirdPartyCostShare() {
        return newThirdPartyCostShare;
    }

    /**
     * @param newThirdPartyCostShare The newThirdPartyCostShare to set.
     */
    public void setNewThirdPartyCostShare(BudgetThirdPartyCostShare newThirdPartyCostShare) {
        this.newThirdPartyCostShare = newThirdPartyCostShare;
    }

    /**
     * Gets the newPersonnelType attribute.
     * 
     * @return Returns the newPersonnelType.
     */
    public String getNewPersonnelType() {
        return newPersonnelType;
    }

    /**
     * Sets the newPersonnelType attribute value.
     * 
     * @param newPersonnelType The newPersonnelType to set.
     */
    public void setNewPersonnelType(String newPersonnelType) {
        this.newPersonnelType = newPersonnelType;
    }

    public boolean isSupportsModular() {
        return supportsModular;
    }

    public void setSupportsModular(boolean supportsModular) {
        this.supportsModular = supportsModular;
    }

    public boolean isAuditActivated() {
        return auditActivated;
    }

    public void setAuditActivated(boolean auditActivated) {
        this.auditActivated = auditActivated;
    }

    /**
     * @return Returns the budgetIndirectCostFormHelper.
     */
    public BudgetIndirectCostFormHelper getBudgetIndirectCostFormHelper() {
        return budgetIndirectCostFormHelper;
    }

    /**
     * @param budgetIndirectCostFormHelper The budgetIndirectCostFormHelper to set.
     */
    public void setBudgetIndirectCostFormHelper(BudgetIndirectCostFormHelper budgetIndirectCostFormHelper) {
        this.budgetIndirectCostFormHelper = budgetIndirectCostFormHelper;
    }

    /**
     * Gets the currentOutputAgencyType attribute.
     * 
     * @return Returns the currentOutputAgencyType.
     */
    public String getCurrentOutputAgencyType() {
        return currentOutputAgencyType;
    }

    /**
     * Sets the currentOutputAgencyType attribute value.
     * 
     * @param currentOutputAgencyType The currentOutputAgencyType to set.
     */
    public void setCurrentOutputAgencyType(String currentOutputAgencyType) {
        this.currentOutputAgencyType = currentOutputAgencyType;
    }

    /**
     * Gets the currentOutputDetailLevel attribute.
     * 
     * @return Returns the currentOutputDetailLevel.
     */
    public String getCurrentOutputDetailLevel() {
        return currentOutputDetailLevel;
    }

    /**
     * Sets the currentOutputDetailLevel attribute value.
     * 
     * @param currentOutputDetailLevel The currentOutputDetailLevel to set.
     */
    public void setCurrentOutputDetailLevel(String currentOutputDetailLevel) {
        this.currentOutputDetailLevel = currentOutputDetailLevel;
    }

    /**
     * Gets the currentOutputReportType attribute.
     * 
     * @return Returns the currentOutputReportType.
     */
    public String getCurrentOutputReportType() {
        return currentOutputReportType;
    }

    /**
     * Sets the currentOutputReportType attribute value.
     * 
     * @param currentOutputReportType The currentOutputReportType to set.
     */
    public void setCurrentOutputReportType(String currentOutputReportType) {
        this.currentOutputReportType = currentOutputReportType;
    }

    /**
     * Gets the currentOutputAgencyPeriod attribute.
     * 
     * @return Returns the currentOutputAgencyPeriod.
     */
    public String getCurrentOutputAgencyPeriod() {
        return currentOutputAgencyPeriod;
    }

    /**
     * Sets the currentOutputAgencyPeriod attribute value.
     * 
     * @param currentOutputAgencyPeriod The currentOutputAgencyPeriod to set.
     */
    public void setCurrentOutputAgencyPeriod(String currentOutputAgencyPeriod) {
        this.currentOutputAgencyPeriod = currentOutputAgencyPeriod;
    }

    public boolean isDisplayCostSharePermission() {
        return displayCostSharePermission;
    }

    public void setDisplayCostSharePermission(boolean displayCostSharePermission) {
        this.displayCostSharePermission = displayCostSharePermission;
    }

    /**
     * This is a work around for a problem with html:multibox. See KULERA-835 for details. Essentially it appears that in Kuali
     * html:multibox doesn't handle string arrays correctly. It only handles the first element of a string array.
     * 
     * @param projectTypeCode
     * @return
     */
    public String[] getSelectedBudgetTypesMultiboxFix(String budgetTypeCode) {
        String[] budgetTypes = this.getBudgetDocument().getBudget().getBudgetTypeCodeArray();

        for (int i = 0; i < budgetTypes.length; i++) {
            String budgetType = (String) budgetTypes[i];
            if (budgetType.equals(budgetTypeCode)) {
                return new String[] { budgetTypeCode };
            }
        }

        // don't pass String[0], JSPs don't like that (exception)
        return new String[] { "" };
    }

    /**
     * @see org.kuali.kfs.module.cg.document.web.struts.RoutingForm#getSelectedBudgetTypesMultiboxFix(String)
     */
    public void setSelectedBudgetTypesMultiboxFix(String code, String[] something) {
        this.getBudgetDocument().getBudget().addBudgetTypeCode(code);
    }
}

