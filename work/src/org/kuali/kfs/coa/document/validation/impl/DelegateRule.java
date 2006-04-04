/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Delegate;

/**
 * This class...
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class DelegateRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DelegateRule.class);
    
    private Delegate oldDelegate;
    private Delegate newDelegate;
    
    /**
     * Constructs a DelegateRule.java.
     * 
     */
    public DelegateRule() {
        super();
    }

    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        
        LOG.info("Entering processCustomSaveDocumentBusinessRules()");
        setupConvenienceObjects(document);
    
        //	check that all sub-objects whose keys are specified have matching objects in the db
        checkExistenceAndActive();

        //	check simple rules
        checkSimpleRules();
        
        //  disallow more than one PrimaryRoute for a given Chart/Account/Doctype
        checkOnlyOnePrimaryRoute(document);
        
        //  delegate user must be Active and Professional
        checkDelegateUserRules(document);
        
        return true;
    }

    /**
     * 
     * This method should be overridden to provide custom rules for processing document routing
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        
        LOG.info("Entering processCustomRouteDocumentBusinessRules()");
        setupConvenienceObjects(document);

        //	check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        //	check simple rules
        success &= checkSimpleRules();
        
        //	disallow more than one PrimaryRoute for a given Chart/Account/Doctype
        success &= checkOnlyOnePrimaryRoute(document);
        
        //  delegate user must be Active and Professional
        success &= checkDelegateUserRules(document);
        
        return success;
    }
    
    /**
     * This method should be overridden to provide custom rules for processing document approval.
     * 
     * @param document
     * @return booelan
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;
        
        LOG.info("Entering processCustomApproveDocumentBusinessRules()");
        setupConvenienceObjects(document);

        //	check that all sub-objects whose keys are specified have matching objects in the db
        success &= checkExistenceAndActive();

        //	check simple rules
        success &= checkSimpleRules();
        
        success &= checkOnlyOnePrimaryRoute(document);
        
        //  delegate user must be Active and Professional
        success &= checkDelegateUserRules(document);
        
        return success;
    }

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you
     * have short and easy handles to the new and old objects contained in the 
     * maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load 
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {
        
        //	setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldDelegate = (Delegate) super.getOldBo();

        //	setup newAccount convenience objects, make sure all possible sub-objects are populated
        newDelegate = (Delegate) super.getNewBo();
    }
    
    private boolean checkExistenceAndActive() {
        
        boolean success = true;
        
        //	if both ChartCode and AccountNumber are filled in, validate that they map to real objects
        if (StringUtils.isNotEmpty(newDelegate.getChartOfAccountsCode()) && StringUtils.isNotEmpty(newDelegate.getAccountNumber())) {
            if (ObjectUtils.isNull(newDelegate.getAccount())) {
                putFieldError("accountNumber", KeyConstants.ERROR_EXISTENCE, 
                        "Chart Code and Account Number (" + newDelegate.getChartOfAccountsCode() + "-" + newDelegate.getAccountNumber() + ")");
                success &= false;
            }
        }
        
        //	if documentTypeCode is filled in, validate that the object exists in the db
        if (StringUtils.isNotEmpty(newDelegate.getFinancialDocumentTypeCode())) {
            if (ObjectUtils.isNull(newDelegate.getDocumentType())) {
                putFieldError("financialDocumentTypeCode", KeyConstants.ERROR_EXISTENCE, 
                        "Document Type Code: " + newDelegate.getFinancialDocumentTypeCode());
                success &= false;
            }
        }
        
        //	if userID is filled in, validate that the object exists in the db
        if (newDelegate.getAccountDelegateSystemId() != null) {
            if (ObjectUtils.isNull(newDelegate.getAccountDelegate())) {
                putFieldError("accountDelegateSystemId", KeyConstants.ERROR_EXISTENCE, 
                        "Kuali User ID: " + newDelegate.getAccountDelegateSystemId());
                success &= false;
            }
        }
        
        return success;
    }
    
    private boolean checkSimpleRules() {
        
        boolean success = true;
        KualiDecimal fromAmount = newDelegate.getFinDocApprovalFromThisAmt();
        KualiDecimal toAmount = newDelegate.getFinDocApprovalToThisAmount();
        
        //	start date must be greater than or equal to today
        if (ObjectUtils.isNotNull(newDelegate.getAccountDelegateStartDate())) {
            Timestamp today = getDateTimeService().getCurrentTimestamp();
            today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime());
            if (newDelegate.getAccountDelegateStartDate().before(today)) {
                putFieldError("accountDelegateStartDate", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_STARTDATE_IN_PAST);
                success &= false;
            }
        }
        
        //	FROM amount must be >= 0 (may not be negative)
        if (ObjectUtils.isNotNull(fromAmount)) {
            if (fromAmount.isLessThan(new KualiDecimal(0))) {
                putFieldError("finDocApprovalFromThisAmt", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_FROM_AMOUNT_NONNEGATIVE);
                success &= false;
            }
        }
        
        if (ObjectUtils.isNotNull(fromAmount) && ObjectUtils.isNull(toAmount)) {
            putFieldError("finDocApprovalToThisAmount", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
            success &= false;
        }
        
        //	TO amount must be >= FROM amount or Zero 
        if (ObjectUtils.isNotNull(toAmount)) {
            
            //	case if FROM amount is null then TO amount must be zero
            if (ObjectUtils.isNull(fromAmount) && !toAmount.equals(new KualiDecimal(0))) {
                putFieldError("finDocApprovalToThisAmount", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                success &= false;
            }
            
            //	case if FROM amount is non-null and positive, disallow TO amount being less 
            if (toAmount.isLessThan(fromAmount)) {
                putFieldError("finDocApprovalToThisAmount", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_TO_AMOUNT_MORE_THAN_FROM_OR_ZERO);
                success &= false;
            }
        }
        
        return success;
    }
    
    //	checks to see if there is already a record 
    private boolean checkOnlyOnePrimaryRoute(MaintenanceDocument document) {
        
        boolean success = true;
        boolean checkDb = false;
        boolean newPrimary;
        
        //	exit out immediately if this doc is not requesting a primary route
        newPrimary = newDelegate.isAccountsDelegatePrmrtIndicator();
        if (!newPrimary) {
            return success;
        }
        
        //  if its a new document, we are only interested if they have chosen this one 
        // to be a primary route
        if (document.isNew()) {
            if (newPrimary) {
                checkDb = true;
            }
        }
        
        //  handle an edit, where all we care about is that someone might change it 
        // from NOT a primary TO a primary
        if (document.isEdit()) {
            boolean oldPrimary = oldDelegate.isAccountsDelegatePrmrtIndicator();
            if (!oldPrimary && newPrimary) {
                checkDb = true;
            }
        }
        
        //  if we dont want to check the db for another primary, then exit
        if (!checkDb) {
            return success;
        }
        
        //  okay, so if we get here, then the new value wants to be a primary, 
        // and if an edit, its changed from the old value.  in other words, 
        // we need to check it.
        
        /*
         *   check if there is an ALL primary, if so then the rule fails, we're done
         *  
         *   check if this doctype is already defined with a primary, if so then the rule fails, we're done
         *   
         */
        
        //  **********************************************
        //      TESTING FOR AN ALL PRIMARY IN THE DB
        //
        //	WHERE = an ALL primary route for this account/chart
        Map whereMap;
        whereMap = new HashMap();
        whereMap.put("chartOfAccountsCode", newDelegate.getChartOfAccountsCode());
        whereMap.put("accountNumber", newDelegate.getAccountNumber());
        whereMap.put("financialDocumentTypeCode", "ALL");
        whereMap.put("accountsDelegatePrmrtIndicator", Boolean.valueOf(true));
        whereMap.put("accountDelegateActiveIndicator", Boolean.valueOf(true));
        
        //	find all the matching records
        Collection primaryRoutes;
        primaryRoutes = getBoService().findMatching(Delegate.class, whereMap);
        
        //	if there is at least one result, then this business rule is tripped
        if (primaryRoutes.size() > 0) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALL_TYPES_ALREADY_EXISTS);
            success &= false;
            return success;  // we're done, no sense in continuing
        }
        //
        //  **********************************************

        //  **********************************************
        //      TESTING FOR ANY PRIMARY IF THIS IS ALL
        //
        if ("ALL".equalsIgnoreCase(newDelegate.getFinancialDocumentTypeCode())) {
            
            //  WHERE = any primary route for this account/chart 
            whereMap = new HashMap();
            whereMap.put("chartOfAccountsCode", newDelegate.getChartOfAccountsCode());
            whereMap.put("accountNumber", newDelegate.getAccountNumber());
            whereMap.put("accountsDelegatePrmrtIndicator", Boolean.valueOf(true));
            whereMap.put("accountDelegateActiveIndicator", Boolean.valueOf(true));
            
            //  find all the matching records
            primaryRoutes = getBoService().findMatching(Delegate.class, whereMap);
            
            //  if there is at least one result, then this business rule is tripped
            if (primaryRoutes.size() > 0) {
                
                //  get the docType of the primary route that is blocking this
                String blockingDocType = "";
                for (Iterator iter = primaryRoutes.iterator(); iter.hasNext();) {
                    Delegate delegate = (Delegate) iter.next();
                    blockingDocType = delegate.getFinancialDocumentTypeCode();
                }
                
                //  add the error
                putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_NEW_ALL, 
                                blockingDocType);
                success &= false;
                return success;  // we're done, no sense in continuing
            }
        }
        //
        //  **********************************************
        
        //  **********************************************
        //      TESTING FOR SAME DOCTYPE PRIMARY IF THIS IS NOT ALL
        else {
            
            //  WHERE = primary route for this docType for this account/chart 
            whereMap = new HashMap();
            whereMap.put("chartOfAccountsCode", newDelegate.getChartOfAccountsCode());
            whereMap.put("accountNumber", newDelegate.getAccountNumber());
            whereMap.put("accountsDelegatePrmrtIndicator", Boolean.valueOf(true));
            whereMap.put("financialDocumentTypeCode", newDelegate.getFinancialDocumentTypeCode());
            whereMap.put("accountDelegateActiveIndicator", Boolean.valueOf(true));
            
            //  find all the matching records
            primaryRoutes = getBoService().findMatching(Delegate.class, whereMap);
            
            //  if there is at least one result, then this business rule is tripped
            if (primaryRoutes.size() > 0) {
                putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_PRIMARY_ROUTE_ALREADY_EXISTS_FOR_DOCTYPE);
                success &= false;
                return success;  // we're done, no sense in continuing
            }
        }
        return success;
    }
    
    private boolean checkDelegateUserRules(MaintenanceDocument document) {
        
        boolean success = true;
        
        //  if the user doesnt exist, then do nothing, it'll fail the existence test elsewhere
        if (ObjectUtils.isNull(newDelegate.getAccountDelegate())) {
            return success;
        }
        KualiUser user = newDelegate.getAccountDelegate();
        
        //  user must be of the allowable statuses (A - Active)
        if (apcRuleFails(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, 
                Constants.ChartApcParms.DELEGATE_USER_EMP_STATUSES, 
                user.getEmployeeStatusCode())) {
            success &= false;
            putFieldError("accountDelegate.personUserIdentifier", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_ACTIVE);
        }
        
        //  user must be of the allowable types (P - Professional)
        if (apcRuleFails(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, 
                Constants.ChartApcParms.DELEGATE_USER_EMP_TYPES, 
                user.getEmployeeTypeCode())) {
            success &= false;
            putFieldError("accountDelegate.personUserIdentifier", KeyConstants.ERROR_DOCUMENT_ACCTDELEGATEMAINT_USER_NOT_PROFESSIONAL);
        }
        
        //  user must be 
        return success;
    }
    
}
