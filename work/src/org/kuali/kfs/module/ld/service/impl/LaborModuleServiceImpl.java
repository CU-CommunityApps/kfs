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
package org.kuali.kfs.module.ld.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerBalanceForEffortCertification;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborLedgerEntry;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferSourceAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferTargetAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerObject;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectBenefit;
import org.kuali.kfs.integration.ld.LaborLedgerPositionObjectGroup;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.kfs.module.ld.businessobject.PositionObjectGroup;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.module.ld.service.LaborLedgerEntryService;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This implements the service methods that may be used by outside of labor module
 */
@Transactional
public class LaborModuleServiceImpl implements LaborModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborModuleServiceImpl.class);

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#calculateFringeBenefitFromLaborObject(org.kuali.kfs.integration.businessobject.LaborLedgerObject,
     *      org.kuali.rice.kns.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(laborLedgerObject, salaryAmount);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#calculateFringeBenefit(java.lang.Integer, java.lang.String,
     *      java.lang.String, org.kuali.rice.kns.util.KualiDecimal)
     */
    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount) {
        return getLaborBenefitsCalculationService().calculateFringeBenefit(fiscalYear, chartCode, objectCode, salaryAmount);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#createAndBlankApproveSalaryExpenseTransferDocument(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.List)
     */
    public void createAndBlankApproveSalaryExpenseTransferDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines) throws WorkflowException {
        LOG.info("createSalaryExpenseTransferDocument() start");

        if (sourceAccountingLines == null || sourceAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given source accounting line is empty.");
            return;
        }

        if (targetAccountingLines == null || targetAccountingLines.isEmpty()) {
            LOG.info("Cannot create a salary expense document when the given target accounting line is empty.");
            return;
        }

        WorkflowDocumentService workflowDocumentService = SpringContext.getBean(WorkflowDocumentService.class);
        SalaryExpenseTransferDocument document = (SalaryExpenseTransferDocument) getDocumentService().getNewDocument(SalaryExpenseTransferDocument.class);

        document.setEmplid(sourceAccountingLines.get(0).getEmplid());
        document.setSourceAccountingLines(sourceAccountingLines);
        document.setTargetAccountingLines(targetAccountingLines);

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setDocumentDescription(documentDescription);
        documentHeader.setExplanation(explanation);

        document.prepareForSave();
        document.populateDocumentForRouting();

        String documentTitle = document.getDocumentTitle();
        if (StringUtils.isNotBlank(documentTitle)) {
            document.getDocumentHeader().getWorkflowDocument().setTitle(documentTitle);
        }

        String organizationDocumentNumber = document.getDocumentHeader().getOrganizationDocumentNumber();
        if (StringUtils.isNotBlank(organizationDocumentNumber)) {
            document.getDocumentHeader().getWorkflowDocument().setAppDocId(organizationDocumentNumber);
        }

        this.getBusinessObjectService().save(document);

        workflowDocumentService.blanketApprove(document.getDocumentHeader().getWorkflowDocument(), annotation, adHocRecipients);
        GlobalVariables.getUserSession().setWorkflowDocument(document.getDocumentHeader().getWorkflowDocument());
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#countPendingSalaryExpenseTransfer(java.lang.String)
     */
    public int countPendingSalaryExpenseTransfer(String emplid) {
        String documentTypeCode = getDocumentTypeService().getDocumentTypeCodeByClass(SalaryExpenseTransferDocument.class);

        Map<String, Object> positiveFieldValues = new HashMap<String, Object>();
        positiveFieldValues.put(KFSPropertyConstants.EMPLID, emplid);
        positiveFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);

        List<String> approvedCodes = Arrays.asList(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED, KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED);
        Map<String, Object> negativeFieldValues = new HashMap<String, Object>();
        negativeFieldValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_APPROVED_CODE, approvedCodes);

        return getBusinessObjectService().countMatching(LaborLedgerPendingEntry.class, positiveFieldValues, negativeFieldValues);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#findEmployeesWithPayType(java.util.Map, java.util.List,
     *      java.util.Map)
     */
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().findEmployeesWithPayType(payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#isEmployeeWithPayType(java.lang.String, java.util.Map,
     *      java.util.List, java.util.Map)
     */
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        return getLaborLedgerEntryService().isEmployeeWithPayType(emplid, payPeriods, balanceTypes, earnCodePayGroupMap);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#findLedgerBalances(java.util.Map, java.util.Map, java.util.Set,
     *      java.util.List, java.util.List)
     */
    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        Collection<LaborLedgerBalance> LaborLedgerBalances = new ArrayList<LaborLedgerBalance>();

        Collection<LedgerBalance> ledgerBalances = getLaborLedgerBalanceService().findLedgerBalances(fieldValues, excludedFieldValues, fiscalYears, balanceTypes, positionObjectGroupCodes);
        for (LedgerBalance balance : ledgerBalances) {
            LaborLedgerBalances.add(balance);
        }
        return LaborLedgerBalances;
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborLedgerPositionObjectGroup(java.lang.String)
     */
    public LaborLedgerPositionObjectGroup getLaborLedgerPositionObjectGroup(String positionObjectGroupCode) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(LaborPropertyConstants.POSITION_OBJECT_GROUP_CODE, positionObjectGroupCode);

        return (LaborLedgerPositionObjectGroup) getBusinessObjectService().findByPrimaryKey(PositionObjectGroup.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#doesLaborLedgerPositionObjectGroupExist(java.lang.String)
     */
    public boolean doesLaborLedgerPositionObjectGroupExist(String positionObjectGroupCode) {
        return this.getLaborLedgerPositionObjectGroup(positionObjectGroupCode) != null;
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborLedgerBalanceClass()
     */
    public Class<? extends LaborLedgerBalance> getLaborLedgerBalanceClass() {
        return this.getKualiModuleService().getResponsibleModuleService(LaborLedgerBalance.class).getExternalizableBusinessObjectImplementation(LaborLedgerBalance.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborLedgerEntryClass()
     */
    public Class<? extends LaborLedgerEntry> getLaborLedgerEntryClass() {
        return this.getKualiModuleService().getResponsibleModuleService(LaborLedgerEntry.class).getExternalizableBusinessObjectImplementation(LaborLedgerEntry.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborLedgerObjectClass()
     */
    public Class<? extends LaborLedgerObject> getLaborLedgerObjectClass() {
        return this.getKualiModuleService().getResponsibleModuleService(LaborLedgerObject.class).getExternalizableBusinessObjectImplementation(LaborLedgerObject.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborLedgerBenefitsCalculationClass()
     */
    public Class<? extends LaborLedgerBenefitsCalculation> getLaborLedgerBenefitsCalculationClass() {
        return this.getKualiModuleService().getResponsibleModuleService(LaborLedgerBenefitsCalculation.class).getExternalizableBusinessObjectImplementation(LaborLedgerBenefitsCalculation.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborLedgerPositionObjectGroupClass()
     */
    public Class<? extends LaborLedgerPositionObjectGroup> getLaborLedgerPositionObjectGroupClass() {
        return this.getKualiModuleService().getResponsibleModuleService(LaborLedgerPositionObjectGroup.class).getExternalizableBusinessObjectImplementation(LaborLedgerPositionObjectGroup.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborLedgerBalanceForEffortCertificationClass()
     */
    public Class<? extends LaborLedgerBalanceForEffortCertification> getLaborLedgerBalanceForEffortCertificationClass() {
        return this.getKualiModuleService().getResponsibleModuleService(LaborLedgerBalanceForEffortCertification.class).getExternalizableBusinessObjectImplementation(LaborLedgerBalanceForEffortCertification.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborLedgerObject(java.lang.Integer, java.lang.String,
     *      java.lang.String)
     */
    public LaborLedgerObject retrieveLaborLedgerObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {

        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        return (LaborLedgerObject) getBusinessObjectService().findByPrimaryKey(getLaborLedgerObjectClass(), searchCriteria);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#retrieveLaborLedgerObject(org.kuali.kfs.coa.businessobject.ObjectCode)
     */
    public LaborLedgerObject retrieveLaborLedgerObject(ObjectCode financialObject) {
        if (financialObject == null) {
            throw new IllegalArgumentException("The given financial object cannot be null.");
        }

        Integer fiscalYear = financialObject.getUniversityFiscalYear();
        String chartOfAccountsCode = financialObject.getChartOfAccountsCode();
        String financialObjectCode = financialObject.getFinancialObjectCode();

        return this.retrieveLaborLedgerObject(fiscalYear, chartOfAccountsCode, financialObjectCode);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#hasPendingLaborLedgerEntry(java.lang.String, java.lang.String)
     */
    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber) {
        return getLaborLedgerPendingEntryService().hasPendingLaborLedgerEntry(chartOfAccountsCode, accountNumber);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getExpenseTransferSourceAccountingLineClass()
     */
    public Class<? extends LaborLedgerExpenseTransferSourceAccountingLine> getExpenseTransferSourceAccountingLineClass() {
        return this.getKualiModuleService().getResponsibleModuleService(LaborLedgerExpenseTransferSourceAccountingLine.class).getExternalizableBusinessObjectImplementation(LaborLedgerExpenseTransferSourceAccountingLine.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getExpenseTransferTargetAccountingLineClass()
     */
    public Class<? extends LaborLedgerExpenseTransferTargetAccountingLine> getExpenseTransferTargetAccountingLineClass() {
        return this.getKualiModuleService().getResponsibleModuleService(LaborLedgerExpenseTransferTargetAccountingLine.class).getExternalizableBusinessObjectImplementation(LaborLedgerExpenseTransferTargetAccountingLine.class);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#retrieveLaborObjectBenefitInformation(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public List<LaborLedgerPositionObjectBenefit> retrieveLaborObjectBenefitInformation(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        List<LaborLedgerPositionObjectBenefit> fringeBenefitInformationRecords = new ArrayList<LaborLedgerPositionObjectBenefit>();

        Collection<PositionObjectBenefit> objectBenefits = retrieveLaborObjectBenefits(fiscalYear, chartOfAccountsCode, objectCode);
        fringeBenefitInformationRecords.addAll(objectBenefits);

        return fringeBenefitInformationRecords;
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#retrieveLaborPositionObjectBenefits(java.lang.Integer,
     *      java.lang.String, java.lang.String)
     */
    public Collection<LaborLedgerPositionObjectBenefit> retrieveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);

        return getBusinessObjectService().findMatching(PositionObjectBenefit.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#hasFringeBenefitProducingObjectCodes(java.lang.Integer,
     *      java.util.List)
     */
    public boolean hasFringeBenefitProducingObjectCodes(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        Collection<PositionObjectBenefit> objectBenefits = retrieveLaborObjectBenefits(fiscalYear, chartOfAccountsCode, financialObjectCode);
        return (objectBenefits != null && !objectBenefits.isEmpty());
    }

    /**
     * Calls business object service to retrieve LaborObjectBenefit objects for the given fiscal year, and chart, object code from
     * accounting line.
     * 
     * @param fiscalYear The fiscal year to be used as search criteria for looking up the labor object benefits.
     * @param line The account line the benefits are being retrieved for.
     * @return List of LaborObjectBenefit objects or null if one does not exist for parameters
     */
    private Collection<PositionObjectBenefit> retrieveLaborObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);

        return getBusinessObjectService().findMatching(PositionObjectBenefit.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.integration.service.LaborModuleService#getLaborOriginEntryGroupCount(java.lang.Integer)
     */
    public Integer getLaborOriginEntryGroupCount(Integer groupId) {
        return getLaborOriginEntryService().getGroupCount(groupId);
    }

    /**
     * Gets the laborBenefitsCalculationService attribute.
     * 
     * @return an implementation of the laborBenefitsCalculationService.
     */
    public LaborBenefitsCalculationService getLaborBenefitsCalculationService() {
        return SpringContext.getBean(LaborBenefitsCalculationService.class);
    }

    /**
     * Gets the laborLedgerEntryService attribute.
     * 
     * @return an implementation of the laborLedgerEntryService.
     */
    public LaborLedgerEntryService getLaborLedgerEntryService() {
        return SpringContext.getBean(LaborLedgerEntryService.class);
    }

    /**
     * Gets the laborLedgerBalanceService attribute.
     * 
     * @return an implementation of the laborLedgerBalanceService.
     */
    public LaborLedgerBalanceService getLaborLedgerBalanceService() {
        return SpringContext.getBean(LaborLedgerBalanceService.class);
    }

    /**
     * Gets the documentService attribute.
     * 
     * @return an implementation of the documentService.
     */
    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    /**
     * Gets the documentTypeService attribute.
     * 
     * @return an implementation of the documentTypeService.
     */
    public DocumentTypeService getDocumentTypeService() {
        return SpringContext.getBean(DocumentTypeService.class);
    }

    /**
     * Gets the universityDateService attribute.
     * 
     * @return an implementation of the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return SpringContext.getBean(UniversityDateService.class);
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return an implementation of the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

    /**
     * Gets the laborLedgerPendingEntryService
     * 
     * @return an implementation of the LaborLedgerPendingEntryService
     */
    public LaborLedgerPendingEntryService getLaborLedgerPendingEntryService() {
        return SpringContext.getBean(LaborLedgerPendingEntryService.class);
    }

    /**
     * Returns an instance of the LaborOriginEntryService, for use by services in the module
     * 
     * @return an instance of an implementation of the LaborOriginEntryService
     */
    public LaborOriginEntryService getLaborOriginEntryService() {
        return SpringContext.getBean(LaborOriginEntryService.class);
    }

    /**
     * Gets the KualiModuleService attribute value.
     * 
     * @return an implementation of the KualiModuleService.
     */
    public KualiModuleService getKualiModuleService() {
        return SpringContext.getBean(KualiModuleService.class);
    }
}
