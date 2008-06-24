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
package org.kuali.kfs.module.bc.document.web.struts;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.text.StrTokenizer;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiLookupAction;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants.LockStatus;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountSelect;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbentSelect;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockSummary;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPositionSelect;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.OrganizationBCDocumentSearchService;
import org.kuali.kfs.module.bc.document.service.OrganizationSalarySettingSearchService;
import org.kuali.kfs.module.bc.document.web.struts.TempListLookupForm;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Action class to display special budget lookup screens.
 */
public class TempListLookupAction extends KualiLookupAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TempListLookupAction.class);

    /**
     * TempListLookupAction can be called to build and display different lists. This method determines what the requested behavior is
     * and either makes a build call for that list or sets up a message (if the list has already been built). If the request parameter
     * showInitialResults is true, an initial search will be performed before display of the screen.
     * 
     * @see org.kuali.core.web.struts.action.KualiLookupAction#start(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */ 
    @Override
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        // determine requested lookup action
        switch (tempListLookupForm.getTempListLookupMode()) {
            case BCConstants.TempListLookupMode.INTENDED_INCUMBENT_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildIntendedIncumbentSelect(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
                break;
       
            case BCConstants.TempListLookupMode.POSITION_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).buildPositionSelect(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
                break;
        
            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_ABOVE_POV:
                // Show Account above current point of view for user
                // The table was already built in OrganizationSelectionTreeAction.performReport
                GlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_REPORT_ACCOUNT_LIST);
                break;
       
            case BCConstants.TempListLookupMode.ACCOUNT_SELECT_BUDGETED_DOCUMENTS:
                // Show Budgeted Docs (BudgetConstructionAccountSelect) in the point of view subtree for the selected org(s)
                // The table was already built in OrganizationSelectionTreeAction.performShowBudgetDocs
                GlobalVariables.getMessageList().add(KFSKeyConstants.budget.MSG_ACCOUNT_LIST);
                break;
        }

        ActionForward forward = super.start(mapping, form, request, response);
        if (tempListLookupForm.isShowInitialResults()) {
            forward = search(mapping, form, request, response);
        }

        return forward;
    }

    /**
     * This differs from KualiLookupAction.clearValues in that any atributes marked hidden will not be cleared. This is to support
     * BC temp tables that use personUniversalIdentifier to operate on the set of rows associated with the current user.
     * 
     * @see org.kuali.core.web.struts.action.KualiLookupAction#clearValues(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward clearValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        LookupForm lookupForm = (LookupForm) form;
        Lookupable kualiLookupable = lookupForm.getLookupable();
        if (kualiLookupable == null) {
            LOG.error("Lookupable is null.");
            throw new RuntimeException("Lookupable is null.");
        }

        for (Iterator iter = kualiLookupable.getRows().iterator(); iter.hasNext();) {
            Row row = (Row) iter.next();
            for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                Field field = (Field) iterator.next();
                if (!field.getFieldType().equals(Field.RADIO) && !field.getFieldType().equals(Field.HIDDEN)) {
                    field.setPropertyValue(field.getDefaultValue());
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiLookupAction#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;
        
        switch (tempListLookupForm.getTempListLookupMode()) {
            case BCConstants.TempListLookupMode.INTENDED_INCUMBENT_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanIntendedIncumbentSelect(tempListLookupForm.getPersonUniversalIdentifier());
                break;
       
            case BCConstants.TempListLookupMode.POSITION_SELECT:
                SpringContext.getBean(OrganizationSalarySettingSearchService.class).cleanPositionSelect(tempListLookupForm.getPersonUniversalIdentifier());
                break;
        
            default:               
                SpringContext.getBean(OrganizationBCDocumentSearchService.class).cleanAccountSelectPullList(tempListLookupForm.getPersonUniversalIdentifier(), tempListLookupForm.getUniversityFiscalYear());
        }
        
        return super.cancel(mapping, form, request, response);
    }

    /**
     * Continues the organization report action after viewing the account list.
     */
    public ActionForward submitReport(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;

        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put(BCConstants.RETURN_FORM_KEY, tempListLookupForm.getFormKey());
        parameters.put(KFSConstants.BACK_LOCATION, basePath + "/" + BCConstants.ORG_SEL_TREE_ACTION);
        if (tempListLookupForm.isReportConsolidation()) {
            parameters.put(BCConstants.Report.REPORT_CONSOLIDATION, "true");
        }
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, tempListLookupForm.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, GlobalVariables.getUserSession().getFinancialSystemUser().getPersonUniversalIdentifier());
        parameters.put(BCConstants.Report.REPORT_MODE, tempListLookupForm.getReportMode());
        parameters.put(BCConstants.CURRENT_POINT_OF_VIEW_KEYCODE, tempListLookupForm.getCurrentPointOfViewKeyCode());

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + BCConstants.ORG_REPORT_SELECTION_ACTION, parameters);

        return new ActionForward(lookupUrl, true);
    }
    
    /**
     * Unlocks a current budget lock and returns back to lock monitor with refreshed locks.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward unlock(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TempListLookupForm tempListLookupForm = (TempListLookupForm) form;
        
        // populate BudgetConstructionLockSummary with the information for the record to unlock (passed on unlock button)
        String unlockKeyString = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isBlank(unlockKeyString)) {
            unlockKeyString = request.getParameter(KNSConstants.METHOD_TO_CALL_PATH);
        }
        BudgetConstructionLockSummary lockSummary = populateLockSummary(unlockKeyString);
        String lockKeyMessage = buildLockKeyMessage(lockSummary.getLockType(), lockSummary.getLockUserId(), lockSummary.getChartOfAccountsCode(), lockSummary.getAccountNumber(), lockSummary.getSubAccountNumber(), lockSummary.getUniversityFiscalYear(), lockSummary.getPositionNumber());
        
        // confirm the unlock
        ActionForward forward = doUnlockConfirmation(mapping, form, request, response, lockSummary.getLockType(), lockKeyMessage);
        if (forward != null) {
            return forward;
        }
        
        // verify lock for user still exists, if not give warning message
        boolean lockExists = SpringContext.getBean(LockService.class).checkLockExists(lockSummary);
        if (!lockExists) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.MSG_LOCK_NOTEXIST, lockSummary.getLockType(), lockKeyMessage);       
        }
        else {
            // do the unlock
            LockStatus lockStatus = SpringContext.getBean(LockService.class).doUnlock(lockSummary);
            if (LockStatus.SUCCESS.equals(lockStatus)) {
                String successMessage = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(BCKeyConstants.MSG_UNLOCK_SUCCESSFUL);
                tempListLookupForm.addMessage(MessageFormat.format(successMessage, lockSummary.getLockType(), lockKeyMessage));
            }
            else {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.MSG_UNLOCK_NOTSUCCESSFUL, lockSummary.getLockType(), lockKeyMessage);
            }
        }
        
        // refresh locks before returning
        return this.search(mapping, form, request, response);
    }    
    

    
    /**
     * Gives a confirmation first time called. The next time will check the confirmation result. If the returned forward is not null, that indicates we are fowarding to the question
     * or they selected No to the confirmation and we should return to the unlock page.
     * 
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward doUnlockConfirmation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String lockType, String lockKeyMessage) throws Exception {
        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
     
        if (question == null) { // question hasn't been asked
            String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(BCKeyConstants.MSG_UNLOCK_CONFIRMATION);
            message = MessageFormat.format(message, lockType, lockKeyMessage);

            return this.performQuestionWithoutInput(mapping, form, request, response, BCConstants.UNLOCK_CONFIRMATION_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION, BCConstants.TEMP_LIST_UNLOCK_METHOD, "");
        }
        else {
            // get result of confirmation, if yes return null which will indicate the unlock can continue
            String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((BCConstants.UNLOCK_CONFIRMATION_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                return null;
            }
        }

        // selected no to confirmation so return to lock screen with refreshed results
        return this.search(mapping, form, request, response);
    }
    
    /**
     * Parses the methodToCall parameter which contains the lock information in a known format. Populates a BudgetConstructionLockSummary
     * that represents the record to unlock.
     * 
     * @param methodToCallString - request parameter containing lock information
     * @return lockSummary populated from request parameter
     */
    protected BudgetConstructionLockSummary populateLockSummary(String methodToCallString) {
        BudgetConstructionLockSummary lockSummary = new BudgetConstructionLockSummary();
        
        // parse lock fields from methodToCall parameter
        String lockType = StringUtils.substringBetween(methodToCallString, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        String lockFieldsString = StringUtils.substringBetween(methodToCallString, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        String lockUser = StringUtils.substringBetween(methodToCallString, KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
    
        // space was replaced by underscore for html
        lockSummary.setLockType(StringUtils.replace(lockType,"_", " "));
        lockSummary.setLockUserId(lockUser);
        
        // parse key fields
        StrTokenizer strTokenizer = new StrTokenizer(lockFieldsString, "%");
        strTokenizer.setIgnoreEmptyTokens(false);
        String fiscalYear = strTokenizer.nextToken();
        if (fiscalYear != null) {
            lockSummary.setUniversityFiscalYear(Integer.parseInt(fiscalYear));
        }

        lockSummary.setChartOfAccountsCode(strTokenizer.nextToken());
        lockSummary.setAccountNumber(strTokenizer.nextToken());
        lockSummary.setSubAccountNumber(strTokenizer.nextToken());
        lockSummary.setPositionNumber(strTokenizer.nextToken());
        
        return lockSummary;
    }
    
    /**
     * Retrieves the message text for the lock key and fills in message parameters based on the lock type.
     * 
     * @return lockKey built from given parameters
     */
    protected String buildLockKeyMessage(String lockType, String lockUserId, String chartOfAccountsCode, String accountNumber, String subAccountNumber, Integer fiscalYear, String positionNumber) {
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

        String lockKeyMessage = "";
        if (BCConstants.LockTypes.POSITION_LOCK.equals(lockType)) {
            lockKeyMessage = kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_LOCK_POSITIONKEY);
            lockKeyMessage = MessageFormat.format(lockKeyMessage, lockUserId, fiscalYear.toString(), positionNumber);
        }
        else if (BCConstants.LockTypes.POSITION_FUNDING_LOCK.equals(lockType)) {
            lockKeyMessage = kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_LOCK_POSITIONFUNDINGKEY);
            lockKeyMessage = MessageFormat.format(lockKeyMessage, lockUserId, fiscalYear.toString(), positionNumber, chartOfAccountsCode, accountNumber, subAccountNumber);          
        }
        else {
            lockKeyMessage = kualiConfigurationService.getPropertyString(BCKeyConstants.MSG_LOCK_ACCOUNTKEY);
            lockKeyMessage = MessageFormat.format(lockKeyMessage, lockUserId, fiscalYear.toString(), chartOfAccountsCode, accountNumber, subAccountNumber);
        }

        return lockKeyMessage;
    }
    
}
