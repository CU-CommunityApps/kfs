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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.integration.ld.LaborLedgerBenefitsCalculation;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountOrganizationHierarchy;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionDao;
import org.kuali.kfs.module.bc.document.service.BenefitsCalculationService;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.BudgetParameterService;
import org.kuali.kfs.module.bc.document.service.PermissionService;
import org.kuali.kfs.module.bc.document.validation.event.DeleteMonthlySpreadEvent;
import org.kuali.kfs.module.bc.document.validation.impl.BudgetConstructionRuleUtil;
import org.kuali.kfs.module.bc.document.web.struts.BudgetConstructionForm;
import org.kuali.kfs.module.bc.document.web.struts.MonthlyBudgetForm;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KfsAuthorizationConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.actions.CompleteAction;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentRouteHeaderValue;
import org.kuali.rice.kew.routeheader.service.RouteHeaderService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.SaveDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implements the BudgetDocumentService interface. Methods here operate on objects associated with the Budget Construction document
 * such as BudgetConstructionHeader
 */
public class BudgetDocumentServiceImpl implements BudgetDocumentService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetDocumentServiceImpl.class);

    private BudgetConstructionDao budgetConstructionDao;
    private DocumentDao documentDao;
    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;
    private BenefitsCalculationService benefitsCalculationService;
    private BusinessObjectService businessObjectService;
    private LaborModuleService laborModuleService;
    private ParameterService parameterService;
    private BudgetParameterService budgetParameterService;
    private PermissionService permissionService;
    private FiscalYearFunctionControlService fiscalYearFunctionControlService;
    private OptionsService optionsService;
    private PersistenceService persistenceService;
    private OrganizationService organizationService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getByCandidateKey(java.lang.String, java.lang.String,
     *      java.lang.String, java.lang.Integer)
     */
    @Transactional
    public BudgetConstructionHeader getByCandidateKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear) {
        return budgetConstructionDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#saveDocument(org.kuali.rice.kns.document.Document)
     *      similar to DocumentService.saveDocument()
     */
    @Transactional
    public Document saveDocument(BudgetConstructionDocument budgetConstructionDocument) throws WorkflowException, ValidationException {

        this.saveDocumentNoWorkflow(budgetConstructionDocument);

        // this seems to be the workflow related stuff only needed during a final save from save button or close
        // and user clicks yes when prompted to save or not.
        documentService.prepareWorkflowDocument(budgetConstructionDocument);
        workflowDocumentService.save(budgetConstructionDocument.getDocumentHeader().getWorkflowDocument(), null);
        GlobalVariables.getUserSession().setWorkflowDocument(budgetConstructionDocument.getDocumentHeader().getWorkflowDocument());

        // save any messages up to this point and put them back in after logDocumentAction()
        // this is a hack to get around the problem where messageLists gets cleared
        // that is PostProcessorServiceImpl.doActionTaken(ActionTakenEventDTO), establishGlobalVariables(), which does
        // GlobalVariables.clear()
        // not sure why this doesn't trash the GlobalVariables.getErrorMap()
        List<String> messagesSoFar = GlobalVariables.getMessageList();

        budgetConstructionDocument.getDocumentHeader().getWorkflowDocument().logDocumentAction("Document Updated");

        // putting messages back in
        GlobalVariables.getMessageList().addAll(messagesSoFar);

        return budgetConstructionDocument;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#saveDocumentNoWorkflow(org.kuali.rice.kns.document.Document)
     *      TODO use this for saves before calc benefits service, monthly spread service, salary setting, monthly calls add to
     *      interface this should leave out any calls to workflow related methods maybe call this from saveDocument(doc, eventclass)
     *      above instead of duplicating all the calls up to the point of workflow related calls
     */
    @Transactional
    public Document saveDocumentNoWorkflow(BudgetConstructionDocument bcDoc) throws ValidationException {
        return this.saveDocumentNoWorkFlow(bcDoc, MonthSpreadDeleteType.NONE, true);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#saveDocumentNoWorkFlow(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.BCConstants.MonthSpreadDeleteType, boolean)
     */
    @Transactional
    public Document saveDocumentNoWorkFlow(BudgetConstructionDocument bcDoc, MonthSpreadDeleteType monthSpreadDeleteType, boolean doMonthRICheck) throws ValidationException {

        checkForNulls(bcDoc);

        bcDoc.prepareForSave();

        // validate and save the local objects not workflow objects
        // this eventually calls BudgetConstructionRules.processSaveDocument() which overrides the method in DocumentRuleBase
        if (doMonthRICheck) {
            validateAndPersistDocument(bcDoc, new SaveDocumentEvent(bcDoc));
        }
        else {
            validateAndPersistDocument(bcDoc, new DeleteMonthlySpreadEvent(bcDoc, monthSpreadDeleteType));
        }
        return bcDoc;
    }

    @Transactional
    public void saveMonthlyBudget(MonthlyBudgetForm monthlyBudgetForm, BudgetConstructionMonthly budgetConstructionMonthly) {

        BudgetConstructionForm budgetConstructionForm = (BudgetConstructionForm) GlobalVariables.getUserSession().retrieveObject(monthlyBudgetForm.getReturnFormKey());
        BudgetConstructionDocument bcDoc = budgetConstructionForm.getBudgetConstructionDocument();

        // handle any override situation
        // getting here assumes that the line is not a salary detail line and that overrides are allowed
        KualiInteger changeAmount = KualiInteger.ZERO;
        KualiInteger monthTotalAmount = budgetConstructionMonthly.getFinancialDocumentMonthTotalLineAmount();
        KualiInteger pbglRequestAmount = budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getAccountLineAnnualBalanceAmount();
        if (!monthTotalAmount.equals(pbglRequestAmount)) {

            changeAmount = monthTotalAmount.subtract(pbglRequestAmount);

            // change the pbgl request amount store it and sync the object in session
            budgetConstructionMonthly.refreshReferenceObject("pendingBudgetConstructionGeneralLedger");

            PendingBudgetConstructionGeneralLedger sourceRow = (PendingBudgetConstructionGeneralLedger) businessObjectService.retrieve(budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger());
            sourceRow.setAccountLineAnnualBalanceAmount(monthTotalAmount);
            businessObjectService.save(sourceRow);

            this.addOrUpdatePBGLRow(bcDoc, sourceRow);
            bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(changeAmount));

        }

        businessObjectService.save(budgetConstructionMonthly);
        this.callForBenefitsCalcIfNeeded(bcDoc, budgetConstructionMonthly, changeAmount);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#callForBenefitsCalcIfNeeded(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly, org.kuali.rice.kns.util.KualiInteger)
     */
    @Transactional
    public void callForBenefitsCalcIfNeeded(BudgetConstructionDocument bcDoc, BudgetConstructionMonthly budgetConstructionMonthly, KualiInteger pbglChangeAmount) {

        if (!benefitsCalculationService.isBenefitsCalculationDisabled()) {
            if (budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getPositionObjectBenefit() != null && !budgetConstructionMonthly.getPendingBudgetConstructionGeneralLedger().getPositionObjectBenefit().isEmpty()) {

                bcDoc.setMonthlyBenefitsCalcNeeded(true);
                if (pbglChangeAmount.isNonZero()) {
                    bcDoc.setBenefitsCalcNeeded(true);
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#calculateBenefitsIfNeeded(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    public void calculateBenefitsIfNeeded(BudgetConstructionDocument bcDoc) {

        if (bcDoc.isBenefitsCalcNeeded() || bcDoc.isMonthlyBenefitsCalcNeeded()) {

            if (bcDoc.isBenefitsCalcNeeded()) {
                this.calculateAnnualBenefits(bcDoc);
            }

            if (bcDoc.isMonthlyBenefitsCalcNeeded()) {
                this.calculateMonthlyBenefits(bcDoc);
            }

            // reload from the DB and refresh refs
            this.reloadBenefitsLines(bcDoc);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#calculateBenefits(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    public void calculateBenefits(BudgetConstructionDocument bcDoc) {

        this.calculateAnnualBenefits(bcDoc);
        this.calculateMonthlyBenefits(bcDoc);

        // reload from the DB and refresh refs
        this.reloadBenefitsLines(bcDoc);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#calculateAnnualBenefits(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    private void calculateAnnualBenefits(BudgetConstructionDocument bcDoc) {

        // allow benefits calculation if document's account is not salary setting only lines
        bcDoc.setBenefitsCalcNeeded(false);
        if (!bcDoc.isSalarySettingOnly()) {

            // pbgl lines are saved at this point, calc benefits
            benefitsCalculationService.calculateAnnualBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber());

            // write global message on calc success
            GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_CALCULATED);
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#calculateMonthlyBenefits(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    private void calculateMonthlyBenefits(BudgetConstructionDocument bcDoc) {

        // allow benefits calculation if document's account is not salary setting only lines
        bcDoc.setMonthlyBenefitsCalcNeeded(false);
        if (!bcDoc.isSalarySettingOnly()) {

            // pbgl lines are saved at this point, calc benefits
            benefitsCalculationService.calculateMonthlyBudgetConstructionGeneralLedgerBenefits(bcDoc.getDocumentNumber(), bcDoc.getUniversityFiscalYear(), bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getSubAccountNumber());

            // write global message on calc success
            GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BENEFITS_MONTHLY_CALCULATED);
        }
    }

    /**
     * Does sanity checks for null document object and null documentNumber
     * 
     * @param document
     */
    @NonTransactional
    protected void checkForNulls(Document document) {
        if (document == null) {
            throw new IllegalArgumentException("invalid (null) document");
        }
        else if (document.getDocumentNumber() == null) {
            throw new IllegalStateException("invalid (null) documentHeaderId");
        }
    }

    /**
     * Runs validation and persists a document to the database. TODO This method is just like the one in DocumentServiceImpl. This
     * method exists because the DocumentService Interface does not define this method, so we can't call it. Not sure if this is an
     * oversite or by design. If the interface gets adjusted, fix to call it, otherwise leave this and remove the marker.
     * 
     * @param document
     * @param event
     * @throws WorkflowException
     * @throws ValidationException
     */
    @Transactional
    public void validateAndPersistDocument(Document document, KualiDocumentEvent event) throws ValidationException {
        if (document == null) {
            LOG.error("document passed to validateAndPersist was null");
            throw new IllegalArgumentException("invalid (null) document");
        }
        LOG.info("validating and preparing to persist document " + document.getDocumentNumber());

        // runs business rules event.validate() and creates rule instance and runs rule method recursively
        document.validateBusinessRules(event);

        // calls overriden method for specific document for anything that needs to happen before the save
        // currently nothing for BC document
        document.prepareForSave(event);

        // save the document to the database
        try {
            LOG.info("storing document " + document.getDocumentNumber());
            documentDao.save(document);
        }
        catch (OptimisticLockingFailureException e) {
            LOG.error("exception encountered on store of document " + e.getMessage());
            throw e;
        }

        document.postProcessSave(event);


    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#validateDocument(org.kuali.rice.kns.document.Document)
     */
    @Transactional
    public void validateDocument(Document document) throws ValidationException {
        if (document == null) {
            LOG.error("document passed to validateDocument was null");
            throw new IllegalArgumentException("invalid (null) document");
        }
        LOG.info("validating document " + document.getDocumentNumber());
        document.validateBusinessRules(new SaveDocumentEvent(document));

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getPBGLSalarySettingRows(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    public List<PendingBudgetConstructionGeneralLedger> getPBGLSalarySettingRows(BudgetConstructionDocument bcDocument) {

        List<String> ssObjects = budgetConstructionDao.getDetailSalarySettingLaborObjects(bcDocument.getUniversityFiscalYear(), bcDocument.getChartOfAccountsCode());
        ssObjects.add(KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG);
        List<PendingBudgetConstructionGeneralLedger> pbglSalarySettingRows = budgetConstructionDao.getPBGLSalarySettingRows(bcDocument.getDocumentNumber(), ssObjects);

        return pbglSalarySettingRows;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#addOrUpdatePBGLRow(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    @NonTransactional
    public BudgetConstructionDocument addOrUpdatePBGLRow(BudgetConstructionDocument bcDoc, PendingBudgetConstructionGeneralLedger sourceRow) {

        List<PendingBudgetConstructionGeneralLedger> expenditureRows = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines();

        // add or update salary setting row to set in memory - this assumes at least one row in the set
        // we can't even do salary setting without at least one salary detail row
        int index = 0;
        boolean insertNeeded = true;
        for (PendingBudgetConstructionGeneralLedger expRow : expenditureRows) {
            String expRowKey = expRow.getFinancialObjectCode() + expRow.getFinancialSubObjectCode();
            String sourceRowKey = sourceRow.getFinancialObjectCode() + sourceRow.getFinancialSubObjectCode();
            if (expRowKey.compareToIgnoreCase(sourceRowKey) == 0) {
                // update
                insertNeeded = false;
                expRow.setAccountLineAnnualBalanceAmount(sourceRow.getAccountLineAnnualBalanceAmount());
                expRow.setPersistedAccountLineAnnualBalanceAmount(sourceRow.getAccountLineAnnualBalanceAmount());
                expRow.setVersionNumber(sourceRow.getVersionNumber());
                break;
            }
            else {
                if (expRowKey.compareToIgnoreCase(sourceRowKey) > 0) {
                    // insert here - drop out
                    break;
                }
            }
            index++;
        }
        if (insertNeeded) {
            // insert the row
            sourceRow.setPersistedAccountLineAnnualBalanceAmount(sourceRow.getAccountLineAnnualBalanceAmount());
            expenditureRows.add(index, sourceRow);
        }

        return bcDoc;
    }

    /**
     * Reloads benefits target accounting lines. Usually called right after an annual benefits calculation and the display needs
     * updated with a fresh copy from the database. All old row versions are removed and database row versions are inserted in the
     * list in the correct order.
     * 
     * @param bcDoc
     */
    @Transactional
    private void reloadBenefitsLines(BudgetConstructionDocument bcDoc) {

        // get list of potential fringe objects to use as an in query param
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, bcDoc.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, bcDoc.getChartOfAccountsCode());

        List<String> fringeObjects = new ArrayList<String>();
        List<LaborLedgerBenefitsCalculation> benefitsCalculation = (List<LaborLedgerBenefitsCalculation>) businessObjectService.findMatching(laborModuleService.getLaborLedgerBenefitsCalculationClass(), fieldValues);
        for (LaborLedgerBenefitsCalculation element : benefitsCalculation) {
            fringeObjects.add(element.getPositionFringeBenefitObjectCode());
        }

        List<PendingBudgetConstructionGeneralLedger> dbPBGLFringeLines = budgetConstructionDao.getDocumentPBGLFringeLines(bcDoc.getDocumentNumber(), fringeObjects);
        List<PendingBudgetConstructionGeneralLedger> docPBGLExpLines = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines();

        // holds the request sums of removed, added records and used to adjust the document expenditure request total
        KualiInteger docRequestTotals = KualiInteger.ZERO;
        KualiInteger dbRequestTotals = KualiInteger.ZERO;

        // remove the current set of fringe lines
        ListIterator docLines = docPBGLExpLines.listIterator();
        while (docLines.hasNext()) {
            PendingBudgetConstructionGeneralLedger docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
            if (fringeObjects.contains(docLine.getFinancialObjectCode())) {
                docRequestTotals = docRequestTotals.add(docLine.getAccountLineAnnualBalanceAmount());
                docLines.remove();
            }
        }

        // add the dbset of fringe lines, if any
        if (dbPBGLFringeLines != null && !dbPBGLFringeLines.isEmpty()) {

            if (docPBGLExpLines == null || docPBGLExpLines.isEmpty()) {
                docPBGLExpLines.addAll(dbPBGLFringeLines);
            }
            else {
                ListIterator dbLines = dbPBGLFringeLines.listIterator();
                docLines = docPBGLExpLines.listIterator();
                PendingBudgetConstructionGeneralLedger dbLine = (PendingBudgetConstructionGeneralLedger) dbLines.next();
                PendingBudgetConstructionGeneralLedger docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                boolean dbDone = false;
                boolean docDone = false;
                while (!dbDone) {
                    if (docDone || docLine.getFinancialObjectCode().compareToIgnoreCase(dbLine.getFinancialObjectCode()) > 0) {
                        if (!docDone) {
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.previous();
                        }
                        dbRequestTotals = dbRequestTotals.add(dbLine.getAccountLineAnnualBalanceAmount());
                        dbLine.setPersistedAccountLineAnnualBalanceAmount(dbLine.getAccountLineAnnualBalanceAmount());
                        this.populatePBGLLine(dbLine);
                        docLines.add(dbLine);
                        if (!docDone) {
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                        }
                        if (dbLines.hasNext()) {
                            dbLine = (PendingBudgetConstructionGeneralLedger) dbLines.next();
                        }
                        else {
                            dbDone = true;
                        }
                    }
                    else {
                        if (docLines.hasNext()) {
                            docLine = (PendingBudgetConstructionGeneralLedger) docLines.next();
                        }
                        else {
                            docDone = true;
                        }
                    }
                }
            }
        }

        // adjust the request total for the removed and added recs
        bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(dbRequestTotals.subtract(docRequestTotals)));

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#populatePBGLLine(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    @Transactional
    public void populatePBGLLine(PendingBudgetConstructionGeneralLedger line) {

        final List REFRESH_FIELDS;
        if (StringUtils.isNotBlank(line.getFinancialSubObjectCode())) {
            REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT, KFSPropertyConstants.FINANCIAL_SUB_OBJECT, BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY }));
        }
        else {
            REFRESH_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT, BCPropertyConstants.BUDGET_CONSTRUCTION_MONTHLY }));
        }
        persistenceService.retrieveReferenceObjects(line, REFRESH_FIELDS);

    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getPendingBudgetConstructionAppointmentFundingRequestSum(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger)
     */
    @Transactional
    public KualiInteger getPendingBudgetConstructionAppointmentFundingRequestSum(PendingBudgetConstructionGeneralLedger salaryDetailLine) {
        return budgetConstructionDao.getPendingBudgetConstructionAppointmentFundingRequestSum(salaryDetailLine);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getAccessMode(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Transactional
    public String getAccessMode(BudgetConstructionHeader bcHeader, UniversalUser universalUser) {
        String editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.UNVIEWABLE;
        boolean isFiscalOfcOrDelegate = false;

        // Check for missing Account Reports mapping
        // Root users will have a special cancel button on the document
        // Otherwise just drop through and do normal access mode checks
        // This implies view access for all in this case, but no request is set so security is not an issue
        if (!this.isAccountReportsExist(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber())) {
            editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.VIEW_ONLY;
            return editMode;
        }

        // continue normal access mode checks
        Integer hdrLevel = bcHeader.getOrganizationLevelCode();

        bcHeader.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        isFiscalOfcOrDelegate = permissionService.isAccountManagerOrDelegate(bcHeader.getAccount(), universalUser);

        // special case level 0 access, check if user is fiscal officer or delegate
        if (hdrLevel == 0) {
            if (isFiscalOfcOrDelegate) {
                editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY;
                return editMode;
            }
        }

        // drops here if we need to check for org approver access for any doc level
        editMode = this.getOrgApproverAcessMode(bcHeader, universalUser);
        if (isFiscalOfcOrDelegate && (editMode.equalsIgnoreCase(KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_ORG_APPROVER) || editMode.equalsIgnoreCase(KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_IN_ACCOUNT_HIER))) {

            // user is a FO or delegate and not an Organization approver or not in account's hierarchy,
            // means the doc is really above the user level
            editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.USER_BELOW_DOC_LEVEL;

        }

        return editMode;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getAccessMode(java.lang.Integer, java.lang.String,
     *      java.lang.String, java.lang.String, org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Transactional
    public String getAccessMode(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber, String subAccountNumber, UniversalUser universalUser) {

        BudgetConstructionHeader bcHeader = this.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        return this.getAccessMode(bcHeader, universalUser);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableDocument(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader)
     */
    @NonTransactional
    public boolean isBudgetableDocument(BudgetConstructionHeader bcHeader) {
        if (bcHeader == null) {
            return false;
        }

        Integer budgetYear = bcHeader.getUniversityFiscalYear();
        Account account = bcHeader.getAccount();
        boolean isBudgetableAccount = this.isBudgetableAccount(budgetYear, account, true);

        if (isBudgetableAccount) {
            SubAccount subAccount = bcHeader.getSubAccount();
            String subAccountNumber = bcHeader.getSubAccountNumber();

            return this.isBudgetableSubAccount(subAccount, subAccountNumber);
        }

        return false;
    }

    @NonTransactional
    public boolean isBudgetableDocumentNoWagesCheck(BudgetConstructionHeader bcHeader) {
        if (bcHeader == null) {
            return false;
        }

        Integer budgetYear = bcHeader.getUniversityFiscalYear();
        Account account = bcHeader.getAccount();
        boolean isBudgetableAccount = this.isBudgetableAccount(budgetYear, account, false);

        if (isBudgetableAccount) {
            SubAccount subAccount = bcHeader.getSubAccount();
            String subAccountNumber = bcHeader.getSubAccountNumber();

            return this.isBudgetableSubAccount(subAccount, subAccountNumber);
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableDocument(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @NonTransactional
    public boolean isBudgetableDocument(BudgetConstructionDocument document) {
        if (document == null) {
            return false;
        }

        Integer budgetYear = document.getUniversityFiscalYear();
        Account account = document.getAccount();
        boolean isBudgetableAccount = this.isBudgetableAccount(budgetYear, account, true);

        if (isBudgetableAccount) {
            SubAccount subAccount = document.getSubAccount();
            String subAccountNumber = document.getSubAccountNumber();

            return this.isBudgetableSubAccount(subAccount, subAccountNumber);
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableDocumentNoWagesCheck(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @NonTransactional
    public boolean isBudgetableDocumentNoWagesCheck(BudgetConstructionDocument document) {
        if (document == null) {
            return false;
        }

        Integer budgetYear = document.getUniversityFiscalYear();
        Account account = document.getAccount();
        boolean isBudgetableAccount = this.isBudgetableAccount(budgetYear, account, false);

        if (isBudgetableAccount) {
            SubAccount subAccount = document.getSubAccount();
            String subAccountNumber = document.getSubAccountNumber();

            return this.isBudgetableSubAccount(subAccount, subAccountNumber);
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isAssociatedWithBudgetableDocument(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @NonTransactional
    public boolean isAssociatedWithBudgetableDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        BudgetConstructionHeader bcHeader = this.getBudgetConstructionHeader(appointmentFunding);
        return this.isBudgetableDocument(bcHeader);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableAccount(java.lang.Integer,
     *      org.kuali.kfs.coa.businessobject.Account)
     */
    @NonTransactional
    public boolean isBudgetableAccount(Integer budgetYear, Account account, boolean isWagesCheck) {
        if (budgetYear == null || account == null) {
            return false;
        }

        // account cannot be closed.
        if (!account.isActive()) {
            return false;
        }

        // account cannot be expired before beginning of 6th accounting period, 2 years before budget construction fiscal year.
        Calendar expDate = BudgetConstructionRuleUtil.getNoBudgetAllowedExpireDate(budgetYear);
        if (account.isExpired(expDate)) {
            return false;
        }

        // account cannot be a cash control account
        if (StringUtils.equals(account.getBudgetRecordingLevelCode(), BCConstants.BUDGET_RECORDING_LEVEL_N)) {
            return false;
        }

        // this check is needed for salary setting
        if (isWagesCheck) {

            // account must be flagged as wages allowed
            SubFundGroup subFundGroup = account.getSubFundGroup();
            if (subFundGroup == null || !subFundGroup.isSubFundGroupWagesIndicator()) {
                return false;
            }
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isBudgetableSubAccount(org.kuali.kfs.coa.businessobject.SubAccount,
     *      java.lang.String)
     */
    @NonTransactional
    public boolean isBudgetableSubAccount(SubAccount subAccount, String subAccountNumber) {
        if (StringUtils.isNotEmpty(subAccountNumber) || StringUtils.equals(subAccountNumber, KFSConstants.getDashSubAccountNumber())) {
            return true;
        }

        // sub account must exist and be active.
        if (subAccount == null || !subAccount.isActive()) {
            return false;
        }

        // sub account must not be flagged cost share
        A21SubAccount a21SubAccount = subAccount.getA21SubAccount();
        if (a21SubAccount != null && StringUtils.equals(a21SubAccount.getSubAccountTypeCode(), KFSConstants.SubAccountType.COST_SHARE)) {
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#isAccountReportsExist(java.lang.String, java.lang.String)
     */
    @Transactional
    public boolean isAccountReportsExist(String chartOfAccountsCode, String accountNumber) {

        BudgetConstructionAccountReports accountReports = (BudgetConstructionAccountReports) budgetConstructionDao.getAccountReports(chartOfAccountsCode, accountNumber);
        if (accountReports == null) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#updatePendingBudgetGeneralLedger(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kns.util.KualiInteger)
     */
    @Transactional
    public void updatePendingBudgetGeneralLedger(PendingBudgetConstructionAppointmentFunding appointmentFunding, KualiInteger updateAmount) {
        BudgetConstructionHeader budgetConstructionHeader = this.getBudgetConstructionHeader(appointmentFunding);
        if (budgetConstructionHeader == null) {
            return;
        }

        PendingBudgetConstructionGeneralLedger pendingRecord = this.getPendingBudgetConstructionGeneralLedger(budgetConstructionHeader, appointmentFunding, updateAmount, false);
        businessObjectService.save(pendingRecord);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#updatePendingBudgetGeneralLedgerPlug(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding,
     *      org.kuali.rice.kns.util.KualiInteger)
     */
    @Transactional
    public void updatePendingBudgetGeneralLedgerPlug(PendingBudgetConstructionAppointmentFunding appointmentFunding, KualiInteger updateAmount) {
        if (updateAmount == null) {
            throw new IllegalArgumentException("The update amount cannot be null");
        }

        BudgetConstructionHeader budgetConstructionHeader = this.getBudgetConstructionHeader(appointmentFunding);
        if (budgetConstructionHeader == null) {
            return;
        }

        if (this.canUpdatePlugRecord(appointmentFunding)) {
            PendingBudgetConstructionGeneralLedger plugRecord = this.getPendingBudgetConstructionGeneralLedger(budgetConstructionHeader, appointmentFunding, updateAmount, true);

            KualiInteger annualBalanceAmount = plugRecord.getAccountLineAnnualBalanceAmount();
            KualiInteger beginningBalanceAmount = plugRecord.getFinancialBeginningBalanceLineAmount();

            if ((annualBalanceAmount == null || annualBalanceAmount.isZero()) && (beginningBalanceAmount == null || beginningBalanceAmount.isZero())) {
                businessObjectService.delete(plugRecord);
            }
            else {
                businessObjectService.save(plugRecord);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#updatePendingBudgetGeneralLedgerPlug(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.rice.kns.util.KualiInteger)
     */
    @Transactional
    public PendingBudgetConstructionGeneralLedger updatePendingBudgetGeneralLedgerPlug(BudgetConstructionDocument bcDoc, KualiInteger updateAmount) {

        String twoPlugKey = KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG + KFSConstants.getDashFinancialSubObjectCode();
        List<PendingBudgetConstructionGeneralLedger> expenditureRows = bcDoc.getPendingBudgetConstructionGeneralLedgerExpenditureLines();
        PendingBudgetConstructionGeneralLedger twoPlugRow = null;

        // update or insert the 2plg row - this assumes at least one row in the set
        // we can't even do salary setting without at least one detail line
        int index = 0;
        boolean insertNeeded = true;
        for (PendingBudgetConstructionGeneralLedger expRow : expenditureRows) {
            String expRowKey = expRow.getFinancialObjectCode() + expRow.getFinancialSubObjectCode();
            if (expRowKey.compareToIgnoreCase(twoPlugKey) == 0) {

                // update the existing row
                insertNeeded = false;
                expRow.setAccountLineAnnualBalanceAmount(expRow.getAccountLineAnnualBalanceAmount().add(updateAmount.negated()));
                expRow.setPersistedAccountLineAnnualBalanceAmount(expRow.getAccountLineAnnualBalanceAmount());
                businessObjectService.save(expRow);
                expRow.refresh();
                twoPlugRow = expRow;
                break;
            }
            else {
                if (expRowKey.compareToIgnoreCase(twoPlugKey) > 0) {

                    // case where offsetting salary setting updates under different object codes - insert a new row here
                    break;
                }
            }
            index++;
        }
        if (insertNeeded) {

            // do insert in the middle or at end of list
            String objectCode = KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG;
            String subObjectCode = KFSConstants.getDashFinancialSubObjectCode();
            String objectTypeCode = optionsService.getOptions(bcDoc.getUniversityFiscalYear()).getFinObjTypeExpenditureexpCd();

            PendingBudgetConstructionGeneralLedger pendingRecord = new PendingBudgetConstructionGeneralLedger();

            pendingRecord.setDocumentNumber(bcDoc.getDocumentNumber());
            pendingRecord.setUniversityFiscalYear(bcDoc.getUniversityFiscalYear());
            pendingRecord.setChartOfAccountsCode(bcDoc.getChartOfAccountsCode());
            pendingRecord.setAccountNumber(bcDoc.getAccountNumber());
            pendingRecord.setSubAccountNumber(bcDoc.getSubAccountNumber());

            pendingRecord.setFinancialObjectCode(objectCode);
            pendingRecord.setFinancialSubObjectCode(subObjectCode);
            pendingRecord.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
            pendingRecord.setFinancialObjectTypeCode(objectTypeCode);

            pendingRecord.setFinancialBeginningBalanceLineAmount(KualiInteger.ZERO);
            pendingRecord.setAccountLineAnnualBalanceAmount(updateAmount);

            // store and add to memory set
            pendingRecord.setPersistedAccountLineAnnualBalanceAmount(pendingRecord.getAccountLineAnnualBalanceAmount());
            businessObjectService.save(pendingRecord);
            expenditureRows.add(index, pendingRecord);
            twoPlugRow = pendingRecord;
            bcDoc.setContainsTwoPlug(true);
        }

        bcDoc.setExpenditureAccountLineAnnualBalanceAmountTotal(bcDoc.getExpenditureAccountLineAnnualBalanceAmountTotal().add(updateAmount.negated()));
        return twoPlugRow;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getBudgetConstructionHeader(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @NonTransactional
    public BudgetConstructionHeader getBudgetConstructionHeader(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String chartOfAccountsCode = appointmentFunding.getChartOfAccountsCode();
        String accountNumber = appointmentFunding.getAccountNumber();
        String subAccountNumber = appointmentFunding.getSubAccountNumber();
        Integer fiscalYear = appointmentFunding.getUniversityFiscalYear();

        return this.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, fiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getBudgetConstructionDocument(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @NonTransactional
    public BudgetConstructionDocument getBudgetConstructionDocument(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, appointmentFunding.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, appointmentFunding.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, appointmentFunding.getAccountNumber());
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, appointmentFunding.getSubAccountNumber());

        Collection<BudgetConstructionDocument> documents = businessObjectService.findMatching(BudgetConstructionDocument.class, fieldValues);
        for (BudgetConstructionDocument document : documents) {
            try {
                return (BudgetConstructionDocument) documentService.getByDocumentHeaderId(document.getDocumentHeader().getDocumentNumber());
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Fail to retrieve the document for applointment fundinf" + appointmentFunding, e);
            }
        }

        return null;
    }

    /**
     * determine whether the plug line can be updated or created. If the given appointment funding is in the plug override mode or
     * it associates with a contract and grant account, then no plug can be updated or created
     * 
     * @param appointmentFunding the given appointment funding
     * @return true if the plug line can be updated or created; otherwise, false
     */
    @Transactional
    private boolean canUpdatePlugRecord(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        // no plug if the override mode is enabled
        if (appointmentFunding.isOverride2PlugMode()) {
            return false;
        }

        Account account = appointmentFunding.getAccount();

        // no plug for the account with the sub groups setup as a system parameter
        if (BudgetParameterFinder.getNotGenerate2PlgSubFundGroupCodes().contains(account.getSubFundGroupCode())) {
            return false;
        }

        // no plug for the contract and grant account
        if (account.isForContractsAndGrants()) {
            return false;
        }

        return true;
    }


    /**
     * get a pending budget construction GL record, and set its to the given update amount if it exists in database; otherwise,
     * create it with the given information
     * 
     * @param budgetConstructionHeader the budget construction header of the pending budget construction GL record
     * @param appointmentFunding the appointment funding associated with the pending budget construction GL record
     * @param updateAmount the amount being used to update the retrieved pending budget construction GL record
     * @param is2PLG the flag used to instrcut to retrieve a pending budget construction GL plug record
     * @return a pending budget construction GL record if any; otherwise, create one with the given information
     */
    @Transactional
    private PendingBudgetConstructionGeneralLedger getPendingBudgetConstructionGeneralLedger(BudgetConstructionHeader budgetConstructionHeader, PendingBudgetConstructionAppointmentFunding appointmentFunding, KualiInteger updateAmount, boolean is2PLG) {
        if (budgetConstructionHeader == null) {
            throw new IllegalArgumentException("The given budget construction document header cannot be null");
        }

        if (appointmentFunding == null) {
            throw new IllegalArgumentException("The given pending budget appointment funding cannot be null");
        }

        if (updateAmount == null) {
            throw new IllegalArgumentException("The update amount cannot be null");
        }

        PendingBudgetConstructionGeneralLedger pendingRecord = this.retrievePendingBudgetConstructionGeneralLedger(budgetConstructionHeader, appointmentFunding, is2PLG);

        if (pendingRecord != null) {
            KualiInteger newAnnaulBalanceAmount = pendingRecord.getAccountLineAnnualBalanceAmount().add(updateAmount);
            pendingRecord.setAccountLineAnnualBalanceAmount(newAnnaulBalanceAmount);
        }
        else if (!is2PLG || (is2PLG && updateAmount.isNonZero())) {
            // initialize a new pending record if not plug line or plug line not zero

            Integer budgetYear = appointmentFunding.getUniversityFiscalYear();
            String objectCode = is2PLG ? KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG : appointmentFunding.getFinancialObjectCode();
            String subObjectCode = is2PLG ? KFSConstants.getDashFinancialSubObjectCode() : appointmentFunding.getFinancialSubObjectCode();
            String objectTypeCode = optionsService.getOptions(budgetYear).getFinObjTypeExpenditureexpCd();

            pendingRecord = new PendingBudgetConstructionGeneralLedger();

            pendingRecord.setDocumentNumber(budgetConstructionHeader.getDocumentNumber());
            pendingRecord.setUniversityFiscalYear(appointmentFunding.getUniversityFiscalYear());
            pendingRecord.setChartOfAccountsCode(appointmentFunding.getChartOfAccountsCode());
            pendingRecord.setAccountNumber(appointmentFunding.getAccountNumber());
            pendingRecord.setSubAccountNumber(appointmentFunding.getSubAccountNumber());

            pendingRecord.setFinancialObjectCode(objectCode);
            pendingRecord.setFinancialSubObjectCode(subObjectCode);
            pendingRecord.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
            pendingRecord.setFinancialObjectTypeCode(objectTypeCode);

            pendingRecord.setFinancialBeginningBalanceLineAmount(KualiInteger.ZERO);
            pendingRecord.setAccountLineAnnualBalanceAmount(updateAmount);
        }

        return pendingRecord;
    }

    /**
     * retrieve a pending budget construction GL record based on the given infromation
     * 
     * @param budgetConstructionHeader the budget construction header of the pending budget construction GL record to be retrieved
     * @param appointmentFunding the appointment funding associated with the pending budget construction GL record to be retrieved
     * @param is2PLG the flag used to instrcut to retrieve a pending budget construction GL plug record
     * @return a pending budget construction GL record if any; otherwise, null
     */
    @NonTransactional
    private PendingBudgetConstructionGeneralLedger retrievePendingBudgetConstructionGeneralLedger(BudgetConstructionHeader budgetConstructionHeader, PendingBudgetConstructionAppointmentFunding appointmentFunding, boolean is2PLG) {
        String objectCode = is2PLG ? KFSConstants.BudgetConstructionConstants.OBJECT_CODE_2PLG : appointmentFunding.getFinancialObjectCode();
        String subObjectCode = is2PLG ? KFSConstants.getDashFinancialSubObjectCode() : appointmentFunding.getFinancialSubObjectCode();

        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetConstructionHeader.getDocumentNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetConstructionHeader.getUniversityFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, budgetConstructionHeader.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, budgetConstructionHeader.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, budgetConstructionHeader.getSubAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_BASE_BUDGET);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, optionsService.getOptions(appointmentFunding.getUniversityFiscalYear()).getFinObjTypeExpenditureexpCd());

        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjectCode);

        return (PendingBudgetConstructionGeneralLedger) businessObjectService.findByPrimaryKey(PendingBudgetConstructionGeneralLedger.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#retrievePendingBudgetConstructionGeneralLedger(org.kuali.kfs.module.bc.businessobject.BudgetConstructionHeader)
     */
    @NonTransactional
    public List<PendingBudgetConstructionGeneralLedger> retrievePendingBudgetConstructionGeneralLedger(BudgetConstructionHeader budgetConstructionHeader) {
        Map<String, Object> searchCriteria = new HashMap<String, Object>();

        searchCriteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, budgetConstructionHeader.getDocumentNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, budgetConstructionHeader.getUniversityFiscalYear());
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, budgetConstructionHeader.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, budgetConstructionHeader.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, budgetConstructionHeader.getSubAccountNumber());

        return (List<PendingBudgetConstructionGeneralLedger>) businessObjectService.findMatching(PendingBudgetConstructionGeneralLedger.class, searchCriteria);
    }

    /**
     * Gets the Budget Construction access mode for a Budget Construction document header and Organization Approver user. This
     * method assumes the Budget Document exists in the database and the Account Organization Hierarchy rows exist for the account.
     * This will not check the special case where the document is at level 0. Most authorization routines should use getAccessMode()
     * 
     * @param bcHeader
     * @param u
     * @return
     */
    @Transactional
    private String getOrgApproverAcessMode(BudgetConstructionHeader bcHeader, UniversalUser universalUser) {

        // default the edit mode is just unviewable
        String editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.UNVIEWABLE;

        // get the hierarchy or attempt to build
        List<BudgetConstructionAccountOrganizationHierarchy> rvwHierList = this.retrieveOrBuildAccountOrganizationHierarchy(bcHeader.getUniversityFiscalYear(), bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber());

        if (rvwHierList != null && !rvwHierList.isEmpty()) {

            // get a hashmap copy of the accountOrgHier rows for the account
            Map<String, BudgetConstructionAccountOrganizationHierarchy> rvwHierMap = new HashMap<String, BudgetConstructionAccountOrganizationHierarchy>();
            for (BudgetConstructionAccountOrganizationHierarchy rvwHier : rvwHierList) {
                rvwHierMap.put(rvwHier.getOrganizationChartOfAccountsCode() + rvwHier.getOrganizationCode(), rvwHier);
            }
            // this will hold an level ordered (low to high) subset of accountOrgHier rows where user is approver
            TreeMap<Integer, BudgetConstructionAccountOrganizationHierarchy> rvwHierApproverList = new TreeMap<Integer, BudgetConstructionAccountOrganizationHierarchy>();

            // get the subset of hier rows where the user is an approver
            try {
                List<Org> povOrgs = (List<Org>) permissionService.getOrgReview(universalUser);
                if (povOrgs.isEmpty()) {

                    editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_ORG_APPROVER;

                }
                else {
                    for (Org povOrg : povOrgs) {
                        if (rvwHierMap.containsKey(povOrg.getChartOfAccountsCode() + povOrg.getOrganizationCode())) {
                            rvwHierApproverList.put(rvwHierMap.get(povOrg.getChartOfAccountsCode() + povOrg.getOrganizationCode()).getOrganizationLevelCode(), rvwHierMap.get(povOrg.getChartOfAccountsCode() + povOrg.getOrganizationCode()));
                        }
                    }

                    // check if the user is an approver somewhere in the hier for this document, compare the header level with the
                    // approval level(s)
                    if (!rvwHierApproverList.isEmpty()) {

                        // user is approver somewhere in the account hier, look for a min record above or equal to doc level
                        boolean fnd = false;
                        for (BudgetConstructionAccountOrganizationHierarchy rvwHierApprover : rvwHierApproverList.values()) {
                            if (rvwHierApprover.getOrganizationLevelCode() >= bcHeader.getOrganizationLevelCode()) {
                                fnd = true;
                                if (rvwHierApprover.getOrganizationLevelCode() > bcHeader.getOrganizationLevelCode()) {
                                    editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.VIEW_ONLY;
                                }
                                else {
                                    editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.FULL_ENTRY;
                                }
                                break;
                            }
                        }

                        // if min rec >= doc level not found, the remaining objects must be < doc level
                        if (!fnd) {
                            editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.USER_BELOW_DOC_LEVEL;
                        }
                    }
                    else {
                        // user not an approver in this account's hier, but is an org approver
                        editMode = KfsAuthorizationConstants.BudgetConstructionEditMode.USER_NOT_IN_ACCOUNT_HIER;
                    }
                }
            }
            catch (Exception e) {

                // returning unviewable will cause an authorization exception
                // write a log message with the specific problem
                LOG.error("Can't get the list of pointOfView Orgs from permissionService.getOrgReview() for: " + universalUser.getPersonUserIdentifier(), e);
            }
        }
        else {

            // returning unviewable will cause an authorization exception
            // write a log message with the specific problem
            // TODO probably should add another EditMode constant and
            // raise BCDocumentAuthorization Exception in BudgetConstructionForm.populateAuthorizationFields()
            LOG.error("Budget Construction Document's Account Organization Hierarchy not built due to overflow: " + bcHeader.getUniversityFiscalYear().toString() + "," + bcHeader.getChartOfAccountsCode() + "," + bcHeader.getAccountNumber());

        }

        return editMode;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#retrieveOrBuildAccountOrganizationHierarchy(java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Transactional
    public List<BudgetConstructionAccountOrganizationHierarchy> retrieveOrBuildAccountOrganizationHierarchy(Integer universityFiscalYear, String chartOfAccountsCode, String accountNumber){
        
        List<BudgetConstructionAccountOrganizationHierarchy> accountOrgHier = new ArrayList<BudgetConstructionAccountOrganizationHierarchy>(); 
        BudgetConstructionAccountReports accountReports = (BudgetConstructionAccountReports) budgetConstructionDao.getAccountReports(chartOfAccountsCode, accountNumber);
        if (accountReports != null){
            accountOrgHier = budgetConstructionDao.getAccountOrgHierForAccount(chartOfAccountsCode, accountNumber, universityFiscalYear);
            if (accountOrgHier == null || accountOrgHier.isEmpty()){
                
                // attempt to build it
                String[] rootNode = organizationService.getRootOrganizationCode();
                String rootChart = rootNode[0];
                String rootOrganization = rootNode[1];
                Integer currentLevel = new Integer(1);
                String organizationChartOfAccountsCode = accountReports.getReportsToChartOfAccountsCode(); 
                String organizationCode = accountReports.getReportsToOrganizationCode();
                boolean overFlow = budgetConstructionDao.insertAccountIntoAccountOrganizationHierarchy(rootChart, rootOrganization, universityFiscalYear, chartOfAccountsCode, accountNumber, currentLevel, organizationChartOfAccountsCode, organizationCode);
                if (!overFlow){
                    accountOrgHier = budgetConstructionDao.getAccountOrgHierForAccount(chartOfAccountsCode, accountNumber, universityFiscalYear);                    
                }
            }
        }
        return accountOrgHier;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#instantiateNewBudgetConstructionDocument(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    @Transactional
    public BudgetConstructionDocument instantiateNewBudgetConstructionDocument(BudgetConstructionDocument budgetConstructionDocument) throws WorkflowException{

        budgetConstructionDocument.setOrganizationLevelChartOfAccountsCode(BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CHART_OF_ACCOUNTS_CODE);
        budgetConstructionDocument.setOrganizationLevelOrganizationCode(BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_ORGANIZATION_CODE);
        budgetConstructionDocument.setOrganizationLevelCode(BudgetConstructionConstants.INITIAL_ORGANIZATION_LEVEL_CODE);
        budgetConstructionDocument.setBudgetTransactionLockUserIdentifier(BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);
        budgetConstructionDocument.setBudgetLockUserIdentifier(BudgetConstructionConstants.DEFAULT_BUDGET_HEADER_LOCK_IDS);

        FinancialSystemDocumentHeader kualiDocumentHeader = budgetConstructionDocument.getDocumentHeader();
        budgetConstructionDocument.setDocumentNumber(budgetConstructionDocument.getDocumentHeader().getDocumentNumber());
        kualiDocumentHeader.setOrganizationDocumentNumber(budgetConstructionDocument.getUniversityFiscalYear().toString());
        kualiDocumentHeader.setFinancialDocumentStatusCode(KFSConstants.INITIAL_KUALI_DOCUMENT_STATUS_CD);
        kualiDocumentHeader.setFinancialDocumentTotalAmount(KualiDecimal.ZERO);
        kualiDocumentHeader.setDocumentDescription(String.format("%s %d %s %s", BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION, budgetConstructionDocument.getUniversityFiscalYear(), budgetConstructionDocument.getChartOfAccountsCode(), budgetConstructionDocument.getAccountNumber()));
        kualiDocumentHeader.setExplanation(BudgetConstructionConstants.BUDGET_CONSTRUCTION_DOCUMENT_DESCRIPTION);

        budgetConstructionDao.saveBudgetConstructionDocument(budgetConstructionDocument);
        documentService.prepareWorkflowDocument(budgetConstructionDocument);
        
        RouteHeaderService routeHeaderService = (RouteHeaderService) KEWServiceLocator.getService(KEWServiceLocator.DOC_ROUTE_HEADER_SRV);

        DocumentRouteHeaderValue ourWorkflowDoc = routeHeaderService.getRouteHeader(budgetConstructionDocument.getDocumentHeader().getWorkflowDocument().getRouteHeaderId());

        CompleteAction action = new CompleteAction(ourWorkflowDoc, ourWorkflowDoc.getInitiatorUser(), "created by application UI");
        action.recordAction();

        // there was no need to queue.  we want to mark the document final and save it 
        ourWorkflowDoc.markDocumentEnroute();
        ourWorkflowDoc.markDocumentApproved();
        ourWorkflowDoc.markDocumentProcessed();
        ourWorkflowDoc.markDocumentFinalized();
        routeHeaderService.saveRouteHeader(ourWorkflowDoc);

        return budgetConstructionDocument;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetDocumentService#getPushPullLevelList(org.kuali.kfs.module.bc.document.BudgetConstructionDocument,
     *      org.kuali.rice.kns.bo.user.UniversalUser)
     */
    @Transactional
    public List<BudgetConstructionAccountOrganizationHierarchy> getPushPullLevelList(BudgetConstructionDocument bcDoc, UniversalUser universalUser) {
        List<BudgetConstructionAccountOrganizationHierarchy> pushOrPullList = new ArrayList<BudgetConstructionAccountOrganizationHierarchy>();

        pushOrPullList.addAll(budgetConstructionDao.getAccountOrgHierForAccount(bcDoc.getChartOfAccountsCode(), bcDoc.getAccountNumber(), bcDoc.getUniversityFiscalYear()));

        if (pushOrPullList.size() >= 1) {
            BudgetConstructionAccountOrganizationHierarchy levelZero = new BudgetConstructionAccountOrganizationHierarchy();
            levelZero.setUniversityFiscalYear(bcDoc.getUniversityFiscalYear());
            levelZero.setChartOfAccountsCode(bcDoc.getChartOfAccountsCode());
            levelZero.setAccountNumber(bcDoc.getAccountNumber());
            levelZero.setOrganizationLevelCode(0);
            levelZero.setOrganizationChartOfAccountsCode(pushOrPullList.get(0).getOrganizationChartOfAccountsCode());
            levelZero.setOrganizationCode(pushOrPullList.get(0).getOrganizationCode());
            pushOrPullList.add(0, levelZero);
        }

        return pushOrPullList;
    }

    /**
     * Sets the budgetConstructionDao attribute value.
     * 
     * @param budgetConstructionDao The budgetConstructionDao to set.
     */
    @NonTransactional
    public void setBudgetConstructionDao(BudgetConstructionDao budgetConstructionDao) {
        this.budgetConstructionDao = budgetConstructionDao;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    @NonTransactional
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     * 
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    @NonTransactional
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Sets the documentDao attribute value.
     * 
     * @param documentDao The documentDao to set.
     */
    @NonTransactional
    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }


    /**
     * Sets the benefitsCalculationService attribute value.
     * 
     * @param benefitsCalculationService The benefitsCalculationService to set.
     */
    @NonTransactional
    public void setBenefitsCalculationService(BenefitsCalculationService benefitsCalculationService) {
        this.benefitsCalculationService = benefitsCalculationService;
    }


    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Sets the laborModuleService attribute value.
     * 
     * @param laborModuleService The laborModuleService to set.
     */
    @NonTransactional
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }


    /**
     * Sets the budgetParameterService attribute value.
     * 
     * @param budgetParameterService The budgetParameterService to set.
     */
    @NonTransactional
    public void setBudgetParameterService(BudgetParameterService budgetParameterService) {
        this.budgetParameterService = budgetParameterService;
    }


    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    @NonTransactional
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


    /**
     * Sets the permissionService attribute value.
     * 
     * @param permissionService The permissionService to set.
     */
    @NonTransactional
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }


    /**
     * Sets the fiscalYearFunctionControlService attribute value.
     * 
     * @param fiscalYearFunctionControlService The fiscalYearFunctionControlService to set.
     */
    @NonTransactional
    public void setFiscalYearFunctionControlService(FiscalYearFunctionControlService fiscalYearFunctionControlService) {
        this.fiscalYearFunctionControlService = fiscalYearFunctionControlService;
    }


    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    @NonTransactional
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    /**
     * Gets the persistenceService attribute.
     * 
     * @return Returns the persistenceService.
     */
    @NonTransactional
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * Sets the persistenceService attribute value.
     * 
     * @param persistenceService The persistenceService to set.
     */
    @NonTransactional
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Sets the organizationService attribute value.
     * @param organizationService The organizationService to set.
     */
    @NonTransactional
    public void setOrganizationService(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }
}
