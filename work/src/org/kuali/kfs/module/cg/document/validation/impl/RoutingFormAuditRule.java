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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.cg.KraConstants;
import org.kuali.kfs.module.cg.KraKeyConstants;
import org.kuali.kfs.module.cg.businessobject.ProjectDirector;
import org.kuali.kfs.module.cg.businessobject.RoutingFormBudget;
import org.kuali.kfs.module.cg.businessobject.RoutingFormOrganizationCreditPercent;
import org.kuali.kfs.module.cg.businessobject.RoutingFormPersonnel;
import org.kuali.kfs.module.cg.businessobject.RoutingFormProjectType;
import org.kuali.kfs.module.cg.businessobject.RoutingFormQuestion;
import org.kuali.kfs.module.cg.document.RoutingFormDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;

public class RoutingFormAuditRule {

    /**
     * Runs audit mode business rule checks on a ResearchDocument.
     * 
     * @param Document document
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    public static boolean processRunAuditBusinessRules(Document document) {
        if (!(document instanceof RoutingFormDocument)) {
            return false;
        }

        RoutingFormDocument routingFormDocument = (RoutingFormDocument) document;

        boolean valid = true;

        valid &= processRoutingFormMainPageAuditChecks(routingFormDocument);
        valid &= processRoutingFormProjectDetailsAuditChecks(routingFormDocument);

        return valid;
    }

    private static boolean processRoutingFormProjectDetailsAuditChecks(RoutingFormDocument routingFormDocument) {
        boolean valid = true;

        List<AuditError> auditErrors = new ArrayList<AuditError>();

        for (RoutingFormQuestion routingFormQuestion : routingFormDocument.getRoutingFormQuestions()) {
            if (routingFormQuestion.getYesNoIndicator() == null) {
                valid = false;
            }
        }

        if (!valid) {
            auditErrors.add(new AuditError("document.projectDetails.otherProjectDetailsQuestions", KraKeyConstants.AUDIT_OTHER_PROJECT_DETAILS_NOT_SELECTED, "projectdetails.anchor1"));
            GlobalVariables.getAuditErrorMap().put("projectDetailsAuditErrors", new AuditCluster("Project Details", auditErrors));
        }

        return valid;
    }

    /**
     * Runs audit mode business rule checks on a Main Page.
     * 
     * @param routingFormDocument
     * @return boolean True if the researchDocument is valid, false otherwise.
     */
    private static boolean processRoutingFormMainPageAuditChecks(RoutingFormDocument routingFormDocument) {
        boolean valid = true;
        List<AuditError> hardAuditErrors = new ArrayList<AuditError>();
        List<AuditError> softAuditErrors = new ArrayList<AuditError>();

        // Perform all validation checks for potential hard audit errors

        // Agency/Delivery Info
        valid &= processRoutingFormMainPageAgencyDeliveryAuditChecks(hardAuditErrors, routingFormDocument);

        // Personnel and Units/Orgs
        valid &= processRoutingFormMainPagePersonnelUnitsAuditChecks(hardAuditErrors, routingFormDocument);

        // Submission Details
        valid &= processRoutingFormMainPageSubmissionDetailsAuditChecks(hardAuditErrors, routingFormDocument);

        // Done, finish up
        if (!hardAuditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("mainPageAuditErrors", new AuditCluster("Main Page", hardAuditErrors));
        }

        // Perform all validation checks for potential soft audit errors

        // Soft audit error check
        processRoutingFormMainPageSoftErrors(softAuditErrors, routingFormDocument);

        // Done, finish soft errors
        if (!softAuditErrors.isEmpty()) {
            GlobalVariables.getAuditErrorMap().put("mainPageAuditErrors", new AuditCluster("Main Page", softAuditErrors, true));
        }

        return valid;
    }

    /**
     * Runs audit mode business rule checks on a Main Page, section Agency/Delivery Info.
     * 
     * @param auditErrors
     * @param routingFormBudget
     * @return
     */
    private static boolean processRoutingFormMainPageAgencyDeliveryAuditChecks(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {
        boolean valid = true;

        if (routingFormDocument.isRoutingFormAgencyToBeNamedIndicator() || ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getAgencyNumber())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.agencyNumber", KraKeyConstants.AUDIT_MAIN_PAGE_AGENCY_REQUIRED, "mainpage.anchor1"));
        }

        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getRoutingFormDueDateTypeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.routingFormDueDateTypeCode", KraKeyConstants.AUDIT_MAIN_PAGE_DUE_DATE_TYPE_REQUIRED, "mainpage.anchor1"));
        }

        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getRoutingFormRequiredCopyText())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.routingFormRequiredCopyText", KraKeyConstants.AUDIT_MAIN_PAGE_COPIES_REQUIRED, "mainpage.anchor1"));
        }

        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormAgency().getAgencyAddressDescription())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormAgency.agencyAddressDescription", KraKeyConstants.AUDIT_MAIN_PAGE_ADDRESS_REQUIRED, "mainpage.anchor1"));
        }

        return valid;
    }

    /**
     * Runs audit mode business rule checks on a Main Page, section Personnel and Units/Orgs.
     * 
     * @param auditErrors
     * @param routingFormBudget
     * @return
     */
    private static boolean processRoutingFormMainPagePersonnelUnitsAuditChecks(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {
        final String PERSON_ROLE_CODE_OTHER = SpringContext.getBean(ParameterService.class).getParameterValue(RoutingFormDocument.class, KraConstants.PERSON_ROLE_CODE_OTHER);

        boolean valid = true;
        int projectDirectorCount = 0;

        int i = 0;
        for (RoutingFormPersonnel person : routingFormDocument.getRoutingFormPersonnel()) {
            if (person.isPersonToBeNamedIndicator()) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPersonnel[" + i + "].personUniversalIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_REQUIRED, "mainpage.anchor2"));
            }

            if (person.isProjectDirector()) {
                projectDirectorCount++;

                Map fieldValues = new HashMap();
                fieldValues.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, person.getPersonUniversalIdentifier());
                ProjectDirector projectDirector = (ProjectDirector) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(ProjectDirector.class, fieldValues);
                if (projectDirector == null) {
                    valid = false;
                    auditErrors.add(new AuditError("document.routingFormPersonnel[" + i + "].personUniversalIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_NOT_PD, "mainpage.anchor2"));
                }

                String INVALID_STATUSES = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.RESEARCH_ADMINISTRATION_DOCUMENT.class, KraConstants.PERSONNEL_STATUSES);
                if (ObjectUtils.isNotNull(person.getUser()) && person.getUser().getEmployeeStatusCode() != null && StringUtils.contains(INVALID_STATUSES, person.getUser().getEmployeeStatusCode())) {
                    valid = false;
                    auditErrors.add(new AuditError("document.routingFormPersonnel[" + i + "].user.personUserIdentifier", KraKeyConstants.AUDIT_PERSONNEL_STATUS, "mainpage.anchor2", new String[] { person.getUser().getPersonName(), person.getUser().getEmployeeStatusCode() }));
                }
            }

            if (ObjectUtils.isNull(person.getPersonRoleCode())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPersonnel[" + i + "].personRoleCode", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_ROLE_CODE_REQUIRED, "mainpage.anchor2"));
            }
            else if (PERSON_ROLE_CODE_OTHER.equals(person.getPersonRoleCode()) && ObjectUtils.isNull(person.getPersonRoleText())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPersonnel[" + i + "].personRoleText", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_ROLE_TEXT_REQUIRED, "mainpage.anchor2"));
            }

            if (ObjectUtils.isNull(person.getPersonFinancialAidPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPersonnel[" + i + "].personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_FA_REQUIRED, "mainpage.anchor2"));
            }

            if (ObjectUtils.isNull(person.getPersonCreditPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormPersonnel[" + i + "].personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_PERSON_CREDIT_REQUIRED, "mainpage.anchor2"));
            }

            i++;
        }

        if (projectDirectorCount == 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personUniversalIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_PD_REQUIRED, "mainpage.anchor2"));
        }
        else if (projectDirectorCount > 1) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personUniversalIdentifier", KraKeyConstants.AUDIT_MAIN_PAGE_ONLY_ONE_PD, "mainpage.anchor2"));
        }

        i = 0;
        for (RoutingFormOrganizationCreditPercent org : routingFormDocument.getRoutingFormOrganizationCreditPercents()) {
            if (ObjectUtils.isNull(org.getOrganizationFinancialAidPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormOrganizationCreditPercents[" + i + "].personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_ORG_FA_REQUIRED, "mainpage.anchor2"));
            }

            if (ObjectUtils.isNull(org.getOrganizationCreditPercent())) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormOrganizationCreditPercents[" + i + "].personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_ORG_CREDIT_REQUIRED, "mainpage.anchor2"));
            }

            i++;
        }

        if (!new KualiInteger(100).equals(routingFormDocument.getTotalFinancialAidPercent())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personFinancialAidPercent", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_FA_PERCENT_NOT_100, "mainpage.anchor2"));
        }

        if (!new KualiInteger(100).equals(routingFormDocument.getTotalCreditPercent())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPersonnel.personCreditPercent", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_CREDIT_PERCENT_NOT_100, "mainpage.anchor2"));
        }

        return valid;
    }

    /**
     * Runs audit mode business rule checks on a Main Page, section Submission Details.
     * 
     * @param auditErrors
     * @param routingFormBudget
     * @return
     */
    private static boolean processRoutingFormMainPageSubmissionDetailsAuditChecks(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        final String SUBMISSION_TYPE_CHANGE = parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.SUBMISSION_TYPE_CHANGE);
        final String PROJECT_TYPE_NEW = parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PROJECT_TYPE_NEW);
        final String PROJECT_TYPE_TIME_EXTENTION = parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PROJECT_TYPE_TIME_EXTENTION);
        final String PROJECT_TYPE_BUDGET_REVISION_ACTIVE = parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PROJECT_TYPE_BUDGET_REVISION_ACTIVE);
        final String PROJECT_TYPE_BUDGET_REVISION_PENDING = parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PROJECT_TYPE_BUDGET_REVISION_PENDING);
        final String PROJECT_TYPE_OTHER = parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PROJECT_TYPE_OTHER);
        final String PURPOSE_RESEARCH = parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PURPOSE_RESEARCH);
        final String PURPOSE_OTHER = parameterService.getParameterValue(RoutingFormDocument.class, KraConstants.PURPOSE_OTHER);

        boolean valid = true;

        // Project Type

        // TreeSet so that we get the natural order of projectCodes. Important because
        // KraDevelopmentGroup.KraRoutingFormProjectTypesValid has elements in alphabetic order.
        TreeSet<String> treeSet = new TreeSet();

        // Could do asList(projectTypes).contains but that's a bit less efficient since we need to check for quiet a few of them.
        boolean projectTypeNew = false;
        boolean projectTypeTimeExtention = false;
        boolean projectTypeBudgetRevisionActive = false;
        boolean projectTypeBudgetRevisionPending = false;
        boolean projectTypeOther = false;

        if (routingFormDocument.getRoutingFormProjectTypes() == null || routingFormDocument.getRoutingFormProjectTypes().size() == 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormProjectTypes", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_REQUIRED, "mainpage.anchor3"));
        }
        else if (routingFormDocument.getRoutingFormProjectTypes() != null) {
            for (RoutingFormProjectType routingFormProjectType : routingFormDocument.getRoutingFormProjectTypes()) {
                if (routingFormProjectType.isProjectTypeSelectedIndicator()) {
                    treeSet.add(routingFormProjectType.getProjectTypeCode());
                }

                if (routingFormProjectType.getProjectTypeCode().equals(PROJECT_TYPE_NEW) && routingFormProjectType.isProjectTypeSelectedIndicator()) {
                    projectTypeNew = true;
                }
                else if (routingFormProjectType.getProjectTypeCode().equals(PROJECT_TYPE_TIME_EXTENTION) && routingFormProjectType.isProjectTypeSelectedIndicator()) {
                    projectTypeTimeExtention = true;
                }
                else if (routingFormProjectType.getProjectTypeCode().equals(PROJECT_TYPE_BUDGET_REVISION_ACTIVE) && routingFormProjectType.isProjectTypeSelectedIndicator()) {
                    projectTypeBudgetRevisionActive = true;
                }
                else if (routingFormProjectType.getProjectTypeCode().equals(PROJECT_TYPE_BUDGET_REVISION_PENDING) && routingFormProjectType.isProjectTypeSelectedIndicator()) {
                    projectTypeBudgetRevisionPending = true;
                }
                else if (routingFormProjectType.getProjectTypeCode().equals(PROJECT_TYPE_OTHER) && routingFormProjectType.isProjectTypeSelectedIndicator()) {
                    projectTypeOther = true;

                    if (ObjectUtils.isNull(routingFormDocument.getProjectTypeOtherDescription())) {
                        valid = false;
                        auditErrors.add(new AuditError("document.projectTypeOtherDescription", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_OTHER_REQUIRED, "mainpage.anchor3"));
                    }
                }
            }

            // We could use .toString but rather not rely on the implementation of that.
            String projectTypesString = "";
            for (Iterator<String> iter = treeSet.iterator(); iter.hasNext();) {
                String projectType = iter.next();
                projectTypesString += projectType;
                if (iter.hasNext()) {
                    projectTypesString += "$";
                }
            }

            if (!SpringContext.getBean(ParameterService.class).getParameterEvaluator(RoutingFormDocument.class, KraConstants.PROJECT_TYPES, projectTypesString).evaluationSucceeds()) {
                valid = false;
                auditErrors.add(new AuditError("document.routingFormProjectTypes", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_INVALID, "mainpage.anchor3"));
            }
        }

        if (ObjectUtils.isNotNull(routingFormDocument.getRoutingFormPriorGrantNumber()) && projectTypeNew) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPriorGrantNumber", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_NEW_AND_PRIOR_GRANT, "mainpage.anchor3"));
        }

        if (ObjectUtils.isNotNull(routingFormDocument.getGrantNumber()) && !(projectTypeTimeExtention || projectTypeBudgetRevisionActive || projectTypeBudgetRevisionPending || projectTypeOther)) {
            valid = false;
            auditErrors.add(new AuditError("document.grantNumber", KraKeyConstants.AUDIT_MAIN_PAGE_PROJECT_TYPE_SELECTION_AND_GRANT, "mainpage.anchor3"));
        }

        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormPurposeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormPurposeCode", KraKeyConstants.AUDIT_MAIN_PAGE_PURPOSE_REQUIRED, "mainpage.anchor3"));
        }

        // Purpose
        if (PURPOSE_RESEARCH.equals(routingFormDocument.getRoutingFormPurposeCode()) && ObjectUtils.isNull(routingFormDocument.getResearchTypeCode())) {
            valid = false;
            auditErrors.add(new AuditError("document.researchTypeCode", KraKeyConstants.AUDIT_MAIN_PAGE_PURPOSE_RESEARCH_TYPE_REQUIRED, "mainpage.anchor3"));
        }

        if (PURPOSE_OTHER.equals(routingFormDocument.getRoutingFormPurposeCode()) && ObjectUtils.isNull(routingFormDocument.getRoutingFormOtherPurposeDescription())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormOtherPurposeDescription", KraKeyConstants.AUDIT_MAIN_PAGE_PURPOSE_OTHER_REQUIRED, "mainpage.anchor3"));
        }

        // Title, Lay Description, & Abstract
        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormProjectTitle())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormProjectTitle", KraKeyConstants.AUDIT_MAIN_PAGE_TITLE_REQUIRED, "mainpage.anchor3"));
        }

        if (ObjectUtils.isNull(routingFormDocument.getRoutingFormLayDescription())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormLayDescription", KraKeyConstants.AUDIT_MAIN_PAGE_LAY_DESCRIPTION_REQUIRED, "mainpage.anchor3"));
        }

        if (ObjectUtils.isNull(routingFormDocument.getProjectAbstract())) {
            valid = false;
            auditErrors.add(new AuditError("document.projectAbstract", KraKeyConstants.AUDIT_MAIN_PAGE_ABSTRACT_REQUIRED, "mainpage.anchor3"));
        }

        // Amounts & Dates
        if (routingFormDocument.getRoutingFormBudget() != null) {
            valid &= processRoutingFormMainPageAmountsDateAuditChecks(auditErrors, routingFormDocument);
        }

        return valid;
    }

    /**
     * Runs audit mode business rule checks on a Main Page, section Amounts & Dates.
     * 
     * @param auditErrors
     * @param routingFormBudget
     * @return
     */
    private static boolean processRoutingFormMainPageAmountsDateAuditChecks(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {
        boolean valid = true;

        RoutingFormBudget routingFormBudget = routingFormDocument.getRoutingFormBudget();

        // Required fields
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetDirectAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_INDIRECT_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetStartDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetEndDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetEndDate", KraKeyConstants.AUDIT_MAIN_PAGE_END_DATE_REQUIRED, "mainpage.anchor3"));
        }

        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalDirectAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_DIRECT_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_INDIRECT_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalStartDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_START_DATE_REQUIRED, "mainpage.anchor3"));
        }
        if (ObjectUtils.isNull(routingFormBudget.getRoutingFormBudgetTotalEndDate())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalEndDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_END_DATE_REQUIRED, "mainpage.anchor3"));
        }

        // logic data relation on each row (not relation between the two)
        if (routingFormBudget.getRoutingFormBudgetStartDate() != null && routingFormBudget.getRoutingFormBudgetEndDate() != null && routingFormBudget.getRoutingFormBudgetStartDate().compareTo(routingFormBudget.getRoutingFormBudgetEndDate()) >= 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_BEFORE_END_DATE, "mainpage.anchor3"));
        }
        if (routingFormBudget.getRoutingFormBudgetTotalStartDate() != null && routingFormBudget.getRoutingFormBudgetTotalEndDate() != null && routingFormBudget.getRoutingFormBudgetTotalStartDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalEndDate()) >= 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_START_DATE_BEFORE_TOTAL_END_DATE, "mainpage.anchor3"));
        }

        // Current / Total row relation establishment (subset logic)
        if (routingFormBudget.getRoutingFormBudgetDirectAmount() != null && routingFormBudget.getRoutingFormBudgetTotalDirectAmount() != null && routingFormBudget.getRoutingFormBudgetDirectAmount().isGreaterThan(routingFormBudget.getRoutingFormBudgetTotalDirectAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_GREATER_TOTAL_DIRECT, "mainpage.anchor3"));
        }
        if (routingFormBudget.getRoutingFormBudgetIndirectCostAmount() != null && routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount() != null && routingFormBudget.getRoutingFormBudgetIndirectCostAmount().isGreaterThan(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetIndirectCostAmount", KraKeyConstants.AUDIT_MAIN_PAGE_INDIRECT_GREATER_TOTAL_INDIRECT, "mainpage.anchor3"));
        }
        if (routingFormBudget.getRoutingFormBudgetStartDate() != null && routingFormBudget.getRoutingFormBudgetTotalStartDate() != null && routingFormBudget.getRoutingFormBudgetStartDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalStartDate()) < 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_START_DATE_GREATER_TOTAL_START_DATE, "mainpage.anchor3"));
        }
        if (routingFormBudget.getRoutingFormBudgetEndDate() != null && routingFormBudget.getRoutingFormBudgetTotalEndDate() != null && routingFormBudget.getRoutingFormBudgetEndDate().compareTo(routingFormBudget.getRoutingFormBudgetTotalEndDate()) > 0) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetStartDate", KraKeyConstants.AUDIT_MAIN_PAGE_END_DATE_LESS_TOTAL_END_DATE, "mainpage.anchor3"));
        }

        KualiInteger totalSubcontractorAmountInteger = routingFormDocument.getTotalSubcontractorAmount();
        if (routingFormBudget.getRoutingFormBudgetDirectAmount() != null && routingFormBudget.getRoutingFormBudgetDirectAmount().isLessThan(totalSubcontractorAmountInteger)) {
            valid = false;
            auditErrors.add(new AuditError("document.routingFormBudget", KraKeyConstants.AUDIT_MAIN_PAGE_SUBCONTRACTOR_TOTAL_GREATER_DIRECT, "mainpage.anchor3"));
        }

        return valid;
    }

    /**
     * This method reviews the provided routing form document and validates it for any potential soft audit errors.
     * 
     * @param auditErrors Collection of audit errors to be added to the routing form document as soft audit errors.
     * @param routingFormDocument Instance of the routing form document being validated.
     */
    private static void processRoutingFormMainPageSoftErrors(List<AuditError> auditErrors, RoutingFormDocument routingFormDocument) {

        RoutingFormBudget routingFormBudget = routingFormDocument.getRoutingFormBudget();

        // Amount validation checks
        if (routingFormBudget.getRoutingFormBudgetDirectAmount() != null && routingFormBudget.getRoutingFormBudgetIndirectCostAmount() != null && routingFormBudget.getRoutingFormBudgetDirectAmount().isLessThan(routingFormBudget.getRoutingFormBudgetIndirectCostAmount())) {
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_DIRECT_LESS_INDIRECT, "mainpage.anchor3"));
        }

        if (routingFormBudget.getRoutingFormBudgetTotalDirectAmount() != null && routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount() != null && routingFormBudget.getRoutingFormBudgetTotalDirectAmount().isLessThan(routingFormBudget.getRoutingFormBudgetTotalIndirectCostAmount())) {
            auditErrors.add(new AuditError("document.routingFormBudget.routingFormBudgetTotalDirectAmount", KraKeyConstants.AUDIT_MAIN_PAGE_TOTAL_DIRECT_LESS_TOTAL_INDIRECT, "mainpage.anchor3"));
        }

    }

}
