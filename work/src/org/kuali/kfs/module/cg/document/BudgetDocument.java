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
package org.kuali.kfs.module.cg.document;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.AdhocOrg;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetInstitutionCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetNonpersonnel;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriod;
import org.kuali.kfs.module.cg.businessobject.BudgetTask;
import org.kuali.kfs.module.cg.businessobject.BudgetThirdPartyCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetUser;
import org.kuali.kfs.module.cg.document.service.BudgetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingGuid;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.exception.IllegalObjectStateException;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Budget
 */
public class BudgetDocument extends ResearchDocumentBase implements GenericRoutingInfo {
    private static final Logger LOG = Logger.getLogger(BudgetDocument.class);

    private static final long serialVersionUID = -3561859858801995441L;
    private Integer budgetTaskNextSequenceNumber;
    private Integer budgetPeriodNextSequenceNumber;
    private Integer personnelNextSequenceNumber;
    private Integer nonpersonnelNextSequenceNumber;
    private Integer institutionCostShareNextSequenceNumber;
    private Integer thirdPartyCostShareNextSequenceNumber;
    boolean forceRefreshOfBOSubListsForSave = true;
    boolean cleanseBudgetOnSave = true;
    private String periodToDelete;
    private String taskToDelete;

    private Budget budget;

    public Set<RoutingData> routingInfo;

    /**
     * Default no-arg constructor.
     */
    public BudgetDocument() {
        super();
        budget = new Budget();
        budget.setDocumentNumber(this.documentNumber);
        budgetTaskNextSequenceNumber = new Integer(1);
        budgetPeriodNextSequenceNumber = new Integer(1);
        personnelNextSequenceNumber = new Integer(1);
        nonpersonnelNextSequenceNumber = new Integer(1);

        institutionCostShareNextSequenceNumber = new Integer(1);
        thirdPartyCostShareNextSequenceNumber = new Integer(1);
    }

    public void initialize() {
        SpringContext.getBean(BudgetService.class).initializeBudget(this);
    }

    // TODO Can't use this just yet - need to ensure that rules are run prior to this being called
    // /**
    // * Budget Document specific logic to perform prior to saving.
    // *
    // * @see org.kuali.rice.kns.document.DocumentBase#prepareForSave()
    // */
    // @Override
    // public void prepareForSave() {
    // super.prepareForSave();
    // try {
    // SpringContext.getBean(BudgetService.class).prepareBudgetForSave(this);
    // } catch (WorkflowException e) {
    // throw new RuntimeException("no document found for documentNumber '" + this.documentHeader + "'", e);
    // }
    // }

    /**
     * @param o
     */
    public void setBudgetTaskNextSequenceNumber(Integer o) {
        budgetTaskNextSequenceNumber = o;
    }

    /**
     * @return budgetTaskNextSequenceNumber
     */
    public Integer getBudgetTaskNextSequenceNumber() {

        return budgetTaskNextSequenceNumber;
    }

    /**
     * @param o
     */
    public void setBudgetPeriodNextSequenceNumber(Integer o) {
        budgetPeriodNextSequenceNumber = o;
    }

    /**
     * @return budgetPeriodNextSequenceNumber
     */
    public Integer getBudgetPeriodNextSequenceNumber() {
        return budgetPeriodNextSequenceNumber;
    }

    /**
     * @param o
     */
    public void setPersonnelNextSequenceNumber(Integer o) {
        personnelNextSequenceNumber = o;
    }

    /**
     * @return budgetPersonnelNextSequenceNumber
     */
    public Integer getPersonnelNextSequenceNumber() {
        return personnelNextSequenceNumber;
    }

    /**
     * @see org.kuali.module.chart.bo.ResearchAdministrationDocumentBase#generatePEsfromAL(org.kuali.bo.AccountingLineBase, boolean,
     *      int)
     */
    public void generatePEsfromAL(AccountingLineBase line, boolean isSource, int counter) throws IllegalObjectStateException {
        // TODO Auto-generated method stub

    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);

        return m;
    }

    /**
     * @return Returns the budget.
     */
    public Budget getBudget() {
        return budget;
    }

    /**
     * @param budget The budget to set.
     */
    public void setBudget(Budget budget) {
        this.budget = budget;
    }

    public int getPeriodListSize() {
        return this.budget.getPeriods().size();
    }

    public int getTaskListSize() {
        return this.budget.getTasks().size();
    }

    public void addPeriod(BudgetPeriod budgetPeriod) {
        if (budgetPeriod.getBudgetPeriodBeginDate() == null) {
            Date defaultNextBeginDate = this.getBudget().getDefaultNextPeriodBeginDate();
            if (defaultNextBeginDate != null) {
                budgetPeriod.setBudgetPeriodBeginDate(defaultNextBeginDate);
            }
        }

        budgetPeriod.setBudgetPeriodSequenceNumber(getBudgetPeriodNextSequenceNumber());
        budgetPeriod.setDocumentNumber(this.getDocumentNumber());
        this.budget.getPeriods().add(budgetPeriod);

        setBudgetPeriodNextSequenceNumber(new Integer(getBudgetPeriodNextSequenceNumber().intValue() + 1));
    }

    public void addTask(BudgetTask budgetTask) {
        budgetTask.setBudgetTaskSequenceNumber(getBudgetTaskNextSequenceNumber());
        budgetTask.setDocumentNumber(this.getDocumentNumber());
        if (this.budget.isAgencyModularIndicator() && this.budget.getTasks().size() == 0) {
            this.budget.getModularBudget().setBudgetModularTaskNumber(budgetTask.getBudgetTaskSequenceNumber());
        }
        this.budget.getTasks().add(budgetTask);

        setBudgetTaskNextSequenceNumber(new Integer(getBudgetTaskNextSequenceNumber().intValue() + 1));
    }

    public void addPersonnel(BudgetUser budgetUser) {
        budgetUser.initializeBudgetUser(this);
        budgetUser.setCurrentTaskNumber(((BudgetTask) this.getBudget().getTasks().get(0)).getBudgetTaskSequenceNumber());
        budget.getPersonnel().add(budgetUser);
        setPersonnelNextSequenceNumber(new Integer(getPersonnelNextSequenceNumber().intValue() + 1));
    }

    public void addNonpersonnel(BudgetNonpersonnel budgetNonpersonnel) {
        budgetNonpersonnel.setBudgetNonpersonnelSequenceNumber(getNonpersonnelNextSequenceNumber());
        this.budget.getNonpersonnelItems().add(budgetNonpersonnel);
        setNonpersonnelNextSequenceNumber(new Integer(getNonpersonnelNextSequenceNumber().intValue() + 1));
    }

    public void addInstitutionCostShare(List<BudgetPeriod> periods, BudgetInstitutionCostShare budgetInstitutionCostShare) {
        budgetInstitutionCostShare.setBudgetCostShareSequenceNumber(this.getInstitutionCostShareNextSequenceNumber());
        budgetInstitutionCostShare.populateKeyFields(this.getDocumentNumber(), periods);

        this.budget.getInstitutionCostShareItems().add(budgetInstitutionCostShare);

        setInstitutionCostShareNextSequenceNumber(new Integer(getInstitutionCostShareNextSequenceNumber().intValue() + 1));
    }

    public void addThirdPartyCostShare(List<BudgetPeriod> periods, BudgetThirdPartyCostShare budgetThirdPartyCostShare) {
        budgetThirdPartyCostShare.setBudgetCostShareSequenceNumber(this.getThirdPartyCostShareNextSequenceNumber());
        budgetThirdPartyCostShare.populateKeyFields(this.getDocumentNumber(), periods);

        this.budget.getThirdPartyCostShareItems().add(budgetThirdPartyCostShare);

        setThirdPartyCostShareNextSequenceNumber(new Integer(getThirdPartyCostShareNextSequenceNumber().intValue() + 1));
    }

    /**
     * Gets the nonpersonnelNextSequenceNumber attribute.
     * 
     * @return Returns the nonpersonnelNextSequenceNumber.
     */
    public Integer getNonpersonnelNextSequenceNumber() {
        return nonpersonnelNextSequenceNumber;
    }

    /**
     * Sets the nonpersonnelNextSequenceNumber attribute value.
     * 
     * @param nonpersonnelNextSequenceNumber The nonpersonnelNextSequenceNumber to set.
     */
    public void setNonpersonnelNextSequenceNumber(Integer nonpersonnelNextSequenceNumber) {
        this.nonpersonnelNextSequenceNumber = nonpersonnelNextSequenceNumber;
    }

    /**
     * Gets the forceRefreshOfBOSubListsForSave attribute.
     * 
     * @return Returns the forceRefreshOfBOSubListsForSave.
     */
    public boolean isForceRefreshOfBOSubListsForSave() {
        return forceRefreshOfBOSubListsForSave;
    }

    /**
     * Sets the forceRefreshOfBOSubListsForSave attribute value.
     * 
     * @param forceRefreshOfBOSubListsForSave The forceRefreshOfBOSubListsForSave to set.
     */
    public void setForceRefreshOfBOSubListsForSave(boolean forceRefreshOfBOSubListsForSave) {
        this.forceRefreshOfBOSubListsForSave = forceRefreshOfBOSubListsForSave;
    }

    /**
     * @return Returns the budgetInstitutionCostShareNextSequenceNumber.
     */
    public Integer getInstitutionCostShareNextSequenceNumber() {
        return institutionCostShareNextSequenceNumber;
    }

    /**
     * @param budgetInstitutionCostShareNextSequenceNumber The budgetInstitutionCostShareNextSequenceNumber to set.
     */
    public void setInstitutionCostShareNextSequenceNumber(Integer budgetInstitutionCostShareNextSequenceNumber) {
        this.institutionCostShareNextSequenceNumber = budgetInstitutionCostShareNextSequenceNumber;
    }

    public boolean isCleanseBudgetOnSave() {
        return cleanseBudgetOnSave;
    }

    public void setCleanseBudgetOnSave(boolean cleanseBudgetOnSave) {
        this.cleanseBudgetOnSave = cleanseBudgetOnSave;
    }

    /**
     * @return Returns the budgetThirdPartyCostShareNextSequenceNumber.
     */
    public Integer getThirdPartyCostShareNextSequenceNumber() {
        return thirdPartyCostShareNextSequenceNumber;
    }

    /**
     * @param budgetThirdPartyCostShareNextSequenceNumber The budgetThirdPartyCostShareNextSequenceNumber to set.
     */
    public void setThirdPartyCostShareNextSequenceNumber(Integer budgetThirdPartyCostShareNextSequenceNumber) {
        this.thirdPartyCostShareNextSequenceNumber = budgetThirdPartyCostShareNextSequenceNumber;
    }

    public String getPeriodToDelete() {
        return periodToDelete;
    }

    public void setPeriodToDelete(String periodToDelete) {
        this.periodToDelete = periodToDelete;
    }

    public String getTaskToDelete() {
        return taskToDelete;
    }

    public void setTaskToDelete(String taskToDelete) {
        this.taskToDelete = taskToDelete;
    }

    /**
     * Gets the routingInfo attribute.
     * 
     * @return Returns the routingInfo.
     */
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Sets the routingInfo attribute value.
     * 
     * @param routingInfo The routingInfo to set.
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

    @Override
    public List buildListOfDeletionAwareLists() {
        List list = new ArrayList();

        list.add(this.getAdhocPersons());
        list.add(this.getAdhocOrgs());
        list.add(this.getAdhocWorkgroups());

        Budget budget = this.getBudget();
        list.add(budget.getNonpersonnelItems());
        list.add(budget.getAllUserAppointmentTaskPeriods(this.isForceRefreshOfBOSubListsForSave()));
        list.add(budget.getAllUserAppointmentTasks(this.isForceRefreshOfBOSubListsForSave()));
        list.add(budget.getPersonnel());

        list.add(budget.getAllThirdPartyCostSharePeriods(this.isForceRefreshOfBOSubListsForSave()));
        list.add(budget.getThirdPartyCostShareItems());

        list.add(budget.getAllInstitutionCostSharePeriods(this.isForceRefreshOfBOSubListsForSave()));
        list.add(budget.getInstitutionCostShareItems());

        list.add(budget.getInstitutionCostSharePersonnelItems());

        if (budget.getIndirectCost() != null && budget.getIndirectCost().getBudgetTaskPeriodIndirectCostItems() != null) {
            list.add(budget.getIndirectCost().getBudgetTaskPeriodIndirectCostItems());
        }
        else {
            list.add(new ArrayList());
        }

        if (budget.getModularBudget() != null) {
            list.add(budget.getModularBudget().getBudgetModularPeriods());
        }
        else {
            list.add(new ArrayList());
        }

        // Lots of FKs from previous collections point to these two, so they need to handled last
        list.add(budget.getTasks());
        list.add(budget.getPeriods());


        return list;
    }

    public String generateDocumentContent() {
        List referenceObjects = new ArrayList();
        referenceObjects.add("personnel");
        referenceObjects.add("institutionCostShareItems");
        SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(budget, referenceObjects);
        this.refreshReferenceObject("adhocOrgs");

        StringBuffer xml = new StringBuffer("<documentContent>");
        xml.append(buildProjectDirectorReportXml(false));
        xml.append(buildCostShareOrgReportXml(false));
        xml.append(buildAdhocOrgReportXml(false));
        xml.append("</documentContent>");

        return xml.toString();
    }

    /**
     * Build the xml to use when generating the workflow routing report.
     * 
     * @param BudgetUser projectDirector
     * @param boolean encloseContent - whether the generated xml should be enclosed within a <documentContent> tag
     * @return String
     */
    public String buildProjectDirectorReportXml(boolean encloseContent) {
        StringBuffer xml = new StringBuffer();
        if (encloseContent) {
            xml.append("<documentContent>");
        }
        BudgetUser projectDirector = null;

        for (BudgetUser person : this.getBudget().getPersonnel()) {
            if (person.isPersonProjectDirectorIndicator()) {
                projectDirector = person;
                break;
            }
        }

        if (ObjectUtils.isNotNull(projectDirector) && ObjectUtils.isNotNull(projectDirector.getUser())) {
            if (!this.getBudget().isProjectDirectorToBeNamedIndicator()) {
                xml.append("<projectDirector>");
                xml.append(projectDirector.getUser().getPrincipalId());
                xml.append("</projectDirector>");
            }
            xml.append(openRoutingInfoXml());
            if (!StringUtils.isBlank(projectDirector.getFiscalCampusCode())) {
                xml.append(buildRoutingDataXmlForOrg(projectDirector.getFiscalCampusCode(), (StringUtils.isBlank(projectDirector.getPrimaryDepartmentCode()) ? org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).getOrganizationByNamespaceCode(projectDirector.getUser(),KFSConstants.ParameterNamespaces.CHART).getOrganizationCode() : projectDirector.getPrimaryDepartmentCode())));
            }
        }
        xml.append(closeRoutingInfoXml());
        if (encloseContent) {
            xml.append("</documentContent>");
        }
        return xml.toString();
    }

    /**
     * Build the xml to use when generating the workflow org routing report.
     * 
     * @param List<BudgetAdhocOrg> orgs
     * @param boolean encloseContent - whether the generated xml should be enclosed within a <documentContent> tag
     * @return String
     */
    public String buildCostShareOrgReportXml(boolean encloseContent) {

        String costSharePermissionCode = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.BUDGET_COST_SHARE_PERMISSION_CODE);

        StringBuffer xml = new StringBuffer();
        if (encloseContent) {
            xml.append("<documentContent>");
        }

        xml.append(openRoutingInfoXml());
        for (BudgetInstitutionCostShare costShare : this.getBudget().getInstitutionCostShareItems()) {
            if (costShare.isPermissionIndicator() || costSharePermissionCode.equals(CGConstants.COST_SHARE_PERMISSION_CODE_TRUE)) {
                xml.append(buildRoutingDataXmlForOrg(costShare.getChartOfAccountsCode(), (costShare.getOrganizationCode() != null ? costShare.getOrganizationCode() : "")));
            }
        }
        xml.append(closeRoutingInfoXml());
        if (encloseContent) {
            xml.append("</documentContent>");
        }

        return xml.toString();
    }

    /**
     * Build the xml to use when generating the workflow org routing report.
     * 
     * @param List<BudgetAdhocOrg> orgs
     * @param boolean encloseContent - whether the generated xml should be enclosed within a <documentContent> tag
     * @return String
     */
    public String buildAdhocOrgReportXml(boolean encloseContent) {
        StringBuffer xml = new StringBuffer();
        if (encloseContent) {
            xml.append("<documentContent>");
        }
        xml.append(openRoutingInfoXml());
        List<AdhocOrg> orgs = this.getAdhocOrgs();
        for (AdhocOrg org : orgs) {
            xml.append(buildRoutingDataXmlForOrg(org.getFiscalCampusCode(), org.getPrimaryDepartmentCode()));
        }
        xml.append(closeRoutingInfoXml());
        if (encloseContent) {
            xml.append("</documentContent>");
        }
        return xml.toString();
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        routingInfo = new HashSet<RoutingData>();
        Set<OrgReviewRoutingData> organizationRoutingSet = new HashSet<OrgReviewRoutingData>();

        // Populate chart and org values related to the project director
        for (BudgetUser person : this.getBudget().getPersonnel()) {
            if (person.isPersonProjectDirectorIndicator()) {
                String chartOfAccountsCode = person.getFiscalCampusCode();
                String organizationCode;
                if (StringUtils.isBlank(person.getPrimaryDepartmentCode())) {
                    organizationCode = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).getOrganizationByNamespaceCode(person.getUser(),KFSConstants.ParameterNamespaces.CHART).getOrganizationCode();
                }
                else {
                    organizationCode = person.getPrimaryDepartmentCode();
                }
                organizationRoutingSet.add(new OrgReviewRoutingData(chartOfAccountsCode, organizationCode));
            }
        }

        // Populate any chart and org values associated with the cost share
        String costSharePermissionCode = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.BUDGET_COST_SHARE_PERMISSION_CODE);
        for (BudgetInstitutionCostShare costShare : this.getBudget().getInstitutionCostShareItems()) {
            if (costShare.isPermissionIndicator() || costSharePermissionCode.equals(CGConstants.COST_SHARE_PERMISSION_CODE_TRUE)) {
                if (costShare.getChartOfAccountsCode() != null && costShare.getOrganizationCode() != null) {
                    organizationRoutingSet.add(new OrgReviewRoutingData(costShare.getChartOfAccountsCode(), costShare.getOrganizationCode()));
                }
            }
        }
        
        // Populate any ad hoc orgs as well
        for (AdhocOrg org : this.getAdhocOrgs()) {
            organizationRoutingSet.add(new OrgReviewRoutingData(org.getFiscalCampusCode(), org.getPrimaryDepartmentCode()));
        }

        RoutingData organizationRoutingData = new RoutingData();
        organizationRoutingData.setRoutingType(KualiOrgReviewAttribute.class.getSimpleName());
        organizationRoutingData.setRoutingSet(organizationRoutingSet);
        routingInfo.add(organizationRoutingData);
    }

}

