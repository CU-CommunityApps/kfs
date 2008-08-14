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
package org.kuali.kfs.module.cg.document.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetInstitutionCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetNonpersonnel;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriodInstitutionCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetPeriodThirdPartyCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetThirdPartyCostShare;
import org.kuali.kfs.module.cg.businessobject.BudgetUser;
import org.kuali.kfs.module.cg.businessobject.Proposal;
import org.kuali.kfs.module.cg.businessobject.ProposalOrganization;
import org.kuali.kfs.module.cg.businessobject.ProposalProjectDirector;
import org.kuali.kfs.module.cg.businessobject.ProposalResearchRisk;
import org.kuali.kfs.module.cg.businessobject.ProposalSubcontractor;
import org.kuali.kfs.module.cg.businessobject.RoutingFormBudget;
import org.kuali.kfs.module.cg.businessobject.RoutingFormInstitutionCostShare;
import org.kuali.kfs.module.cg.businessobject.RoutingFormOrganization;
import org.kuali.kfs.module.cg.businessobject.RoutingFormOtherCostShare;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.businessobject.RoutingFormProjectType;
import org.kuali.kfs.module.cg.businessobject.RoutingFormResearchRisk;
import org.kuali.kfs.module.cg.businessobject.RoutingFormSubcontractor;
import org.kuali.kfs.module.cg.businessobject.UserAppointmentTask;
import org.kuali.kfs.module.cg.businessobject.UserAppointmentTaskPeriod;
import org.kuali.kfs.module.cg.businessobject.defaultvalue.NextProposalNumberFinder;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.ProposalMaintainableImpl;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.module.cg.document.service.RoutingFormService;
import org.kuali.kfs.module.cg.document.web.struts.BudgetOverviewFormHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoutingFormServiceImpl implements RoutingFormService {

    private DocumentService documentService;
    private MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.module.cg.document.service.RoutingFormService#prepareRoutingFormForSave(org.kuali.kfs.module.cg.document.RoutingFormDocument)
     */
    public void prepareRoutingFormForSave(RoutingFormDocument routingFormDocument) throws WorkflowException {
        // TODO
    }

    public BudgetDocument retrieveBudgetForLinking(String budgetDocumentNumber) throws WorkflowException {
        Document document = documentService.getByDocumentHeaderId(budgetDocumentNumber);

        if (document != null && document instanceof BudgetDocument) {
            return (BudgetDocument) document;
        }
        else {
            return null;
        }
    }

    public void linkImportBudgetDataToRoutingForm(RoutingFormDocument routingFormDocument, String budgetDocumentHeaderId, List<BudgetOverviewFormHelper> periodOverviews) throws WorkflowException {

        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        final String PERSON_ROLE_CODE_PD = parameterService.getParameterValue(RoutingFormDocument.class, CGConstants.PERSON_ROLE_CODE_PROJECT_DIRECTOR);

        Integer minPeriod = routingFormDocument.getRoutingFormBudget().getRoutingFormBudgetMinimumPeriodNumber();
        Integer maxPeriod = routingFormDocument.getRoutingFormBudget().getRoutingFormBudgetMaximumPeriodNumber();

        BudgetDocument budgetDocument = retrieveBudgetForLinking(budgetDocumentHeaderId);

        Budget budget = budgetDocument.getBudget();

        // clear out lists that are going to be re-populated with Budget data.
        routingFormDocument.getRoutingFormInstitutionCostShares().clear();
        routingFormDocument.getRoutingFormOtherCostShares().clear();
        routingFormDocument.getRoutingFormSubcontractors().clear();

        RoutingFormBudget routingFormBudget = routingFormDocument.getRoutingFormBudget();
        routingFormBudget.resetRoutingFormBudgetData();

        for (BudgetOverviewFormHelper periodOverview : periodOverviews) {
            if (routingFormBudget.getRoutingFormBudgetTotalStartDate() == null) {
                routingFormBudget.setRoutingFormBudgetTotalStartDate(periodOverview.getBudgetPeriod().getBudgetPeriodBeginDate());
            }
            routingFormBudget.setRoutingFormBudgetTotalEndDate(periodOverview.getBudgetPeriod().getBudgetPeriodEndDate());
            routingFormBudget.setRoutingFormBudgetTotalIndirectCostAmount(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount().add(periodOverview.getTotalIndirectCostsAgencyRequest()));

            if (!periodOverview.isOverviewShowModular()) {
                routingFormBudget.setRoutingFormBudgetTotalDirectAmount(routingFormBudget.getRoutingFormBudgetTotalDirectAmount().add(periodOverview.getTotalDirectCostsAgencyRequest()));
            }
            else {
                routingFormBudget.setRoutingFormBudgetTotalDirectAmount(routingFormBudget.getRoutingFormBudgetTotalDirectAmount().add(periodOverview.getAdjustedDirectCostsAgencyRequest()));
            }

            if (periodOverview.isSelected()) {
                if (routingFormBudget.getRoutingFormBudgetStartDate() == null) {
                    routingFormBudget.setRoutingFormBudgetStartDate(periodOverview.getBudgetPeriod().getBudgetPeriodBeginDate());
                }
                routingFormBudget.setRoutingFormBudgetEndDate(periodOverview.getBudgetPeriod().getBudgetPeriodEndDate());
                routingFormBudget.setRoutingFormBudgetIndirectCostAmount(routingFormBudget.getRoutingFormBudgetIndirectCostAmount().add(periodOverview.getTotalIndirectCostsAgencyRequest()));

                if (!periodOverview.isOverviewShowModular()) {
                    routingFormBudget.setRoutingFormBudgetDirectAmount(routingFormBudget.getRoutingFormBudgetDirectAmount().add(periodOverview.getTotalDirectCostsAgencyRequest()));
                }
                else {
                    routingFormBudget.setRoutingFormBudgetDirectAmount(routingFormBudget.getRoutingFormBudgetDirectAmount().add(periodOverview.getAdjustedDirectCostsAgencyRequest()));
                }
            }
        }

        // set simple values - just replace the RF version w/ what's in the Budget
        routingFormDocument.setRoutingFormBudgetNumber(budgetDocumentHeaderId);
        routingFormDocument.setRoutingFormAnnouncementNumber(budget.getBudgetProgramAnnouncementNumber());
        routingFormDocument.setAgencyFederalPassThroughNumber(budget.getFederalPassThroughAgencyNumber());
        routingFormDocument.setRoutingFormAgencyToBeNamedIndicator(budget.isAgencyToBeNamedIndicator());
        routingFormDocument.setRoutingFormPurposeCode(budget.getIndirectCost().getBudgetPurposeCode());

        routingFormDocument.getRoutingFormAgency().setAgencyNumber(budget.getBudgetAgencyNumber());

        // Making this call to refresh the document's attributes
        routingFormDocument.getRoutingFormAgency().refreshReferenceObject("agency");

        // Project Director
        for (Iterator i = routingFormDocument.getRoutingFormPersonnel().iterator(); i.hasNext();) {
            RoutingFormPersonnel routingFormPerson = (RoutingFormPersonnel) i.next();
            if (routingFormPerson.isProjectDirector()) {
                i.remove();
                break;
            }
        }

        for (BudgetUser budgetUser : budget.getPersonnel()) {
            if (budgetUser.isPersonProjectDirectorIndicator()) {
                // Get Project Director from Budget
                RoutingFormPersonnel routingFormProjectDirector = new RoutingFormPersonnel(budgetUser, routingFormDocument.getDocumentNumber(), PERSON_ROLE_CODE_PD);
                routingFormProjectDirector.setPersonCreditPercent(new KualiInteger(100));
                routingFormProjectDirector.setPersonFinancialAidPercent(new KualiInteger(100));
                routingFormProjectDirector.refreshNonUpdateableReferences();
                routingFormDocument.addPerson(routingFormProjectDirector);
            }

            // Routing Form Cost Share entries from Personnel
            RoutingFormInstitutionCostShare routingFormInstitutionCostShare = new RoutingFormInstitutionCostShare();
            routingFormInstitutionCostShare.setChartOfAccountsCode(budgetUser.getFiscalCampusCode());
            routingFormInstitutionCostShare.setOrganizationCode(budgetUser.getPrimaryDepartmentCode());
            routingFormInstitutionCostShare.setRoutingFormCostShareAmount(KualiInteger.ZERO);

            for (UserAppointmentTask userAppointmentTask : budgetUser.getUserAppointmentTasks()) {
                for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                    if (userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber().compareTo(minPeriod) >= 0 && userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber().compareTo(maxPeriod) <= 0) {
                        routingFormInstitutionCostShare.setRoutingFormCostShareAmount(routingFormInstitutionCostShare.getRoutingFormCostShareAmount().add(userAppointmentTaskPeriod.getInstitutionCostShareRequestTotalAmount()));
                    }
                }
            }

            if (routingFormInstitutionCostShare.getRoutingFormCostShareAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormInstitutionCostShare(routingFormInstitutionCostShare, true);
            }
        }

        // Routing Form Cost Share entries from Budget Cost Share screen (not personnel)
        for (BudgetInstitutionCostShare budgetInstitutionCostShare : budget.getInstitutionCostShareItems()) {
            RoutingFormInstitutionCostShare routingFormInstitutionCostShare = new RoutingFormInstitutionCostShare();
            routingFormInstitutionCostShare.setChartOfAccountsCode(budgetInstitutionCostShare.getChartOfAccountsCode());
            routingFormInstitutionCostShare.setOrganizationCode(budgetInstitutionCostShare.getOrganizationCode());
            routingFormInstitutionCostShare.setRoutingFormCostShareAmount(KualiInteger.ZERO);

            for (BudgetPeriodInstitutionCostShare budgetPeriodInstitutionCostShare : budgetInstitutionCostShare.getBudgetPeriodCostShare()) {
                if (budgetPeriodInstitutionCostShare.getBudgetCostShareAmount() != null && budgetPeriodInstitutionCostShare.getBudgetPeriodSequenceNumber().compareTo(minPeriod) >= 0 && budgetPeriodInstitutionCostShare.getBudgetPeriodSequenceNumber().compareTo(maxPeriod) <= 0) {
                    routingFormInstitutionCostShare.setRoutingFormCostShareAmount(routingFormInstitutionCostShare.getRoutingFormCostShareAmount().add(budgetPeriodInstitutionCostShare.getBudgetCostShareAmount()));
                }
            }

            if (routingFormInstitutionCostShare.getRoutingFormCostShareAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormInstitutionCostShare(routingFormInstitutionCostShare, true);
            }

        }

        // Other Cost Share from Budget Cost Share screen
        for (BudgetThirdPartyCostShare budgetThirdPartyCostShare : budget.getThirdPartyCostShareItems()) {
            RoutingFormOtherCostShare routingFormOtherCostShare = new RoutingFormOtherCostShare();
            routingFormOtherCostShare.setRoutingFormCostShareSourceName(budgetThirdPartyCostShare.getBudgetCostShareDescription());
            routingFormOtherCostShare.setRoutingFormCostShareAmount(KualiInteger.ZERO);

            for (BudgetPeriodThirdPartyCostShare budgetPeriodThirdPartyCostShare : budgetThirdPartyCostShare.getBudgetPeriodCostShare()) {
                if (budgetPeriodThirdPartyCostShare.getBudgetPeriodSequenceNumber().compareTo(minPeriod) >= 0 && budgetPeriodThirdPartyCostShare.getBudgetPeriodSequenceNumber().compareTo(maxPeriod) <= 0) {
                    routingFormOtherCostShare.setRoutingFormCostShareAmount(routingFormOtherCostShare.getRoutingFormCostShareAmount().add(budgetPeriodThirdPartyCostShare.getBudgetCostShareAmount()));
                }
            }

            if (routingFormOtherCostShare.getRoutingFormCostShareAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormOtherCostShare(routingFormOtherCostShare);
            }
        }

        // Other Cost Share from Subcontractors (only Third Party and Inst. Cost Share amounts)
        // Create Aggregate Entries for Subcontractor Cost Share and Subcontractor entries
        Map<String, RoutingFormOtherCostShare> subcontractorCostShareMap = new HashMap<String, RoutingFormOtherCostShare>();
        Map<String, RoutingFormSubcontractor> subcontractorMap = new HashMap<String, RoutingFormSubcontractor>();
        for (BudgetNonpersonnel budgetNonpersonnel : budget.getNonpersonnelItems()) {
            if (CGConstants.SUBCONTRACTOR_CATEGORY_CODE.equals(budgetNonpersonnel.getBudgetNonpersonnelCategoryCode())) {
                if (budgetNonpersonnel.getBudgetPeriodSequenceNumber().compareTo(minPeriod) >= 0 && budgetNonpersonnel.getBudgetPeriodSequenceNumber().compareTo(maxPeriod) <= 0) {
                    if (subcontractorCostShareMap.containsKey(budgetNonpersonnel.getSubcontractorNumber())) {
                        RoutingFormOtherCostShare subcontractor = subcontractorCostShareMap.get(budgetNonpersonnel.getSubcontractorNumber());
                        subcontractor.setRoutingFormCostShareAmount(subcontractor.getRoutingFormCostShareAmount().add(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount().add(budgetNonpersonnel.getBudgetInstitutionCostShareAmount())));
                    }
                    else {
                        RoutingFormOtherCostShare routingFormOtherCostShare = new RoutingFormOtherCostShare();
                        routingFormOtherCostShare.setRoutingFormCostShareSourceName(budgetNonpersonnel.getBudgetNonpersonnelDescription());
                        routingFormOtherCostShare.setRoutingFormCostShareAmount(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount().add(budgetNonpersonnel.getBudgetInstitutionCostShareAmount()));

                        subcontractorCostShareMap.put(budgetNonpersonnel.getSubcontractorNumber(), routingFormOtherCostShare);
                    }

                    if (subcontractorMap.containsKey(budgetNonpersonnel.getSubcontractorNumber())) {
                        RoutingFormSubcontractor subcontractor = subcontractorMap.get(budgetNonpersonnel.getSubcontractorNumber());
                        subcontractor.setRoutingFormSubcontractorAmount(subcontractor.getRoutingFormSubcontractorAmount().add(budgetNonpersonnel.getAgencyRequestAmount()));
                    }
                    else {
                        RoutingFormSubcontractor subcontractor = new RoutingFormSubcontractor();
                        subcontractor.setRoutingFormSubcontractorNumber(budgetNonpersonnel.getSubcontractorNumber());
                        subcontractor.setRoutingFormSubcontractorAmount(budgetNonpersonnel.getAgencyRequestAmount());

                        subcontractorMap.put(budgetNonpersonnel.getSubcontractorNumber(), subcontractor);
                    }
                }
            }
        }

        for (RoutingFormOtherCostShare routingFormOtherCostShare : subcontractorCostShareMap.values()) {
            if (routingFormOtherCostShare.getRoutingFormCostShareAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormOtherCostShare(routingFormOtherCostShare);
            }
        }

        for (RoutingFormSubcontractor routingFormSubcontractor : subcontractorMap.values()) {
            if (routingFormSubcontractor.getRoutingFormSubcontractorAmount().isGreaterThan(KualiInteger.ZERO)) {
                routingFormDocument.addRoutingFormSubcontractor(routingFormSubcontractor);
            }
        }


    }


    /**
     * This method creates and routes a C&G Proposal based on the values in a Routing Form. This is being done in the KRA-RF space
     * to avoid creating a code dependency from C&G to KRA.
     * 
     * @param routingFormDocument
     * @return
     */
    public Long createAndRouteProposalMaintenanceDocument(RoutingFormDocument routingFormDocument) {

        try {
            MaintenanceDocument proposalMaintenanceDocument = (MaintenanceDocument) documentService.getNewDocument("ProposalAutoGeneratedMaintenanceDocument");

            ProposalMaintainableImpl proposalMaintainable = new ProposalMaintainableImpl(this.createProposalFromRoutingForm(routingFormDocument));
            proposalMaintainable.setDocumentNumber(proposalMaintenanceDocument.getDocumentNumber());

            proposalMaintenanceDocument.setNewMaintainableObject(proposalMaintainable);

            proposalMaintenanceDocument.getDocumentHeader().setDocumentDescription("Created from Routing Form " + routingFormDocument.getDocumentNumber());

            documentService.updateDocument(proposalMaintenanceDocument);

            GlobalVariables.clear();
            documentService.routeDocument(proposalMaintenanceDocument, null, null);

            return ((ProposalMaintainableImpl) proposalMaintenanceDocument.getNewMaintainableObject()).getProposal().getProposalNumber();
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Error creating/routing new Proposal from Routing Form Document.  Routing Form " + routingFormDocument.getDocumentNumber());
        }

    }


    private Proposal createProposalFromRoutingForm(RoutingFormDocument routingFormDocument) {
        Proposal proposal = new Proposal();

        Long newProposalNumber = NextProposalNumberFinder.getLongValue();
        proposal.setProposalNumber(newProposalNumber);
        proposal.setProposalStatusCode(Proposal.PROPOSAL_CODE);

        // Values coming from RoutingFormDocument (ER_RF_DOC_T)
        proposal.setProposalProjectTitle(routingFormDocument.getRoutingFormProjectTitle().length() > 250 ? routingFormDocument.getRoutingFormProjectTitle().substring(0, 250) : routingFormDocument.getRoutingFormProjectTitle());
        proposal.setProposalPurposeCode(routingFormDocument.getRoutingFormPurposeCode());
        proposal.setGrantNumber(routingFormDocument.getGrantNumber());
        proposal.setCfdaNumber(routingFormDocument.getRoutingFormCatalogOfFederalDomesticAssistanceNumber());
        proposal.setProposalFellowName(routingFormDocument.getRoutingFormFellowFullName());
        proposal.setProposalFederalPassThroughIndicator(routingFormDocument.getRoutingFormFederalPassThroughIndicator());
        proposal.setFederalPassThroughAgencyNumber(routingFormDocument.getAgencyFederalPassThroughNumber());

        // There could be multiple types on the RF, but only one of them will pass this rule, and that's the one that should be used
        // to populate the Proposal field.
        for (RoutingFormProjectType routingFormProjectType : routingFormDocument.getRoutingFormProjectTypes()) {
            if (parameterService.getParameterEvaluator(RoutingFormDocument.class, CGConstants.CREATE_PROPOSAL_PROJECT_TYPES, routingFormProjectType.getProjectTypeCode()).evaluationSucceeds()) {
                proposal.setProposalAwardTypeCode(routingFormProjectType.getProjectTypeCode());
                break;
            }
        }


        // Values coming from Routing Form Budget BO (ER_RF_BDGT_T)
        RoutingFormBudget routingFormBudget = routingFormDocument.getRoutingFormBudget();
        proposal.setProposalBeginningDate(routingFormBudget.getRoutingFormBudgetStartDate());
        proposal.setProposalEndingDate(routingFormBudget.getRoutingFormBudgetEndDate());
        proposal.setProposalTotalAmount((routingFormBudget.getRoutingFormBudgetDirectAmount() != null ? routingFormBudget.getRoutingFormBudgetDirectAmount() : KualiInteger.ZERO).add(routingFormBudget.getRoutingFormBudgetIndirectCostAmount() != null ? routingFormBudget.getRoutingFormBudgetIndirectCostAmount() : KualiInteger.ZERO).kualiDecimalValue());
        proposal.setProposalDirectCostAmount((routingFormBudget.getRoutingFormBudgetDirectAmount() != null ? routingFormBudget.getRoutingFormBudgetDirectAmount() : KualiInteger.ZERO).kualiDecimalValue());
        proposal.setProposalIndirectCostAmount((routingFormBudget.getRoutingFormBudgetIndirectCostAmount() != null ? routingFormBudget.getRoutingFormBudgetIndirectCostAmount() : KualiInteger.ZERO).kualiDecimalValue());
        proposal.setProposalDueDate(routingFormDocument.getRoutingFormAgency().getRoutingFormDueDate());

        // Values coming from RoutingFormAgency (ER_RF_AGNCY_T)
        proposal.setAgencyNumber(routingFormDocument.getRoutingFormAgency().getAgencyNumber());

        // Values coming from the list of Subcontractors (ER_RF_SUBCNR_T)
        for (RoutingFormSubcontractor routingFormSubcontractor : routingFormDocument.getRoutingFormSubcontractors()) {


            /**
             * Constructs a ProposalSubcontractor with a Proposal Number and uses a RoutingFormSubcontractor as a template.
             * 
             * @param proposalNumber The proposalNumber for the Proposal that this ProposalSubcontractor will be associated with
             * @param routingFormSubcontractor The routingFormSubcontractor that will act as a template for this
             *        ProposalSubcontractor
             */
            ProposalSubcontractor newProposalSubcontractor = new ProposalSubcontractor();
            newProposalSubcontractor.setProposalNumber(newProposalNumber);
            newProposalSubcontractor.setProposalSubcontractorNumber(routingFormSubcontractor.getRoutingFormSubcontractorSequenceNumber().toString());
            newProposalSubcontractor.setSubcontractorNumber(routingFormSubcontractor.getRoutingFormSubcontractorNumber());
            newProposalSubcontractor.setProposalSubcontractorAmount(routingFormSubcontractor.getRoutingFormSubcontractorAmount().kualiDecimalValue());

            proposal.getProposalSubcontractors().add(newProposalSubcontractor);
        }

        // Get the RF Primary Project Director
        for (RoutingFormPersonnel routingFormPerson : routingFormDocument.getRoutingFormPersonnel()) {
            if (routingFormPerson.isProjectDirector()) {
                RoutingFormPersonnel projectDirector = routingFormPerson;

                ProposalProjectDirector newProposalProjectDirector = new ProposalProjectDirector();
                newProposalProjectDirector.setProposalNumber(newProposalNumber);
                newProposalProjectDirector.setPersonUniversalIdentifier(projectDirector.getPersonUniversalIdentifier());
                newProposalProjectDirector.setProposalPrimaryProjectDirectorIndicator(true);


                proposal.getProposalProjectDirectors().add(newProposalProjectDirector);

                ProposalOrganization projectDirectorOrganization = new ProposalOrganization();
                projectDirectorOrganization.setProposalNumber(newProposalNumber);
                projectDirectorOrganization.setChartOfAccountsCode(projectDirector.getChartOfAccountsCode());
                projectDirectorOrganization.setOrganizationCode(projectDirector.getOrganizationCode());
                projectDirectorOrganization.setProposalPrimaryOrganizationIndicator(true);

                proposal.getProposalOrganizations().add(projectDirectorOrganization);
                break;
            }
        }

        for (RoutingFormOrganization routingFormOrganization : routingFormDocument.getRoutingFormOrganizations()) {
            // construct a new ProposalOrganization using the current RoutingFormOrganization as a template.
            ProposalOrganization proposalOrganization = new ProposalOrganization();
            proposalOrganization.setProposalNumber(newProposalNumber);
            proposalOrganization.setChartOfAccountsCode(routingFormOrganization.getChartOfAccountsCode());
            proposalOrganization.setOrganizationCode(routingFormOrganization.getOrganizationCode());
            proposalOrganization.setProposalPrimaryOrganizationIndicator(routingFormOrganization.getRoutingFormPrimaryOrganizationIndicator());

            // check to see if the list or ProposalOrganizations already contains an entry with this key; add it to the list if it
            // does not.
            if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(proposal.getProposalOrganizations(), proposalOrganization)) {
                proposal.getProposalOrganizations().add(proposalOrganization);
            }
        }

        for (RoutingFormResearchRisk routingFormResearchRisk : routingFormDocument.getRoutingFormResearchRisks()) {
            ProposalResearchRisk newProposalResearchRisk = new ProposalResearchRisk();

            newProposalResearchRisk.setProposalNumber(newProposalNumber);
            newProposalResearchRisk.setResearchRiskTypeCode(routingFormResearchRisk.getResearchRiskTypeCode());
            newProposalResearchRisk.setResearchRiskTypeIndicator(routingFormResearchRisk.getResearchRiskStudies() != null && routingFormResearchRisk.getResearchRiskStudies().size() > 0 || routingFormResearchRisk.getResearchRiskDescription() != null);
            newProposalResearchRisk.setResearchRiskType(routingFormResearchRisk.getResearchRiskType());

            proposal.getProposalResearchRisks().add(newProposalResearchRisk);
        }

        proposal.setProposalSubmissionDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

        return proposal;
    }

    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
