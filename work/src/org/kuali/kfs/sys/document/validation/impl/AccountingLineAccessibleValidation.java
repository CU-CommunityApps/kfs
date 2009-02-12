/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedAddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedUpdateAccountingLineEvent;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * A validation that checks whether the given accounting line is accessible to the given user or not
 */
public class AccountingLineAccessibleValidation extends GenericValidation {
    private DataDictionaryService dataDictionaryService;
    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;
    
    /**
     * Indicates what is being done to an accounting line. This allows the same method to be used for different actions.
     */
    public enum AccountingLineAction {
        ADD(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_ADD), DELETE(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE), UPDATE(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);

        public final String accessibilityErrorKey;

        AccountingLineAction(String accessabilityErrorKey) {
            this.accessibilityErrorKey = accessabilityErrorKey;
        }
    }

    /**
     * Validates that the given accounting line is accessible for editing by the current user.
     * <strong>This method expects a document as the first parameter and an accounting line as the second</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {        
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        final String groupName = getGroupName();
        AccountingLineAuthorizer accountingLineAuthorizer = ((FinancialSystemTransactionalDocumentEntry)dataDictionaryService.getDataDictionary().getDictionaryObjectEntry(accountingDocumentForValidation.getClass().getName())).getAccountingLineGroups().get(groupName).getAccountingLineAuthorizer();
        
        boolean isAccessible = accountingLineAuthorizer.hasEditPermissionOnField(accountingDocumentForValidation, accountingLineForValidation, getAccountingLineCollectionProperty(), KFSPropertyConstants.ACCOUNT_NUMBER, currentUser);

        // report errors
        if (!isAccessible) {
            String principalName = currentUser.getPrincipalName();
            
            String[] chartErrorParams = new String[] { accountingLineForValidation.getChartOfAccountsCode(),  principalName};
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, convertEventToMessage(event), chartErrorParams);
            
            String[] accountErrorParams = new String[] { accountingLineForValidation.getAccountNumber(), principalName };
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, convertEventToMessage(event), accountErrorParams);
        }

        return isAccessible;
    }
    
    /**
     * Returns the name of the accounting line group which holds the proper authorizer to do the KIM check
     * @return the name of the accouting line group to get the authorizer from
     */
    protected String getGroupName() {
        return (accountingLineForValidation.isSourceAccountingLine() ? KFSConstants.SOURCE_ACCOUNTING_LINES_GROUP_NAME : KFSConstants.TARGET_ACCOUNTING_LINES_GROUP_NAME);
    }
    
    /**
     * Determines the property of the accounting line collection from the error prefixes
     * @return the accounting line collection property
     */
    protected String getAccountingLineCollectionProperty() {
        String propertyName = null;
        if (GlobalVariables.getErrorMap().getErrorPath().size() > 0) {
            propertyName = ((String)GlobalVariables.getErrorMap().getErrorPath().get(0)).replaceFirst(".*?document\\.", "");
        } else {
            propertyName = accountingLineForValidation.isSourceAccountingLine() ? KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value : KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        }
        if (propertyName.equals("newSourceLine")) return KFSConstants.PermissionAttributeValue.SOURCE_ACCOUNTING_LINES.value;
        if (propertyName.equals("newTargetLine")) return KFSConstants.PermissionAttributeValue.TARGET_ACCOUNTING_LINES.value;
        return propertyName;
    }
    
    /**
     * Determines what error message should be shown based on the event that required this validation
     * @param event the event to use to determine the error message
     * @return the key of the error message to display
     */
    protected String convertEventToMessage(KualiDocumentEvent event) {
        if (event instanceof AttributedAddAccountingLineEvent) {
            return AccountingLineAction.ADD.accessibilityErrorKey;
        } else if (event instanceof AttributedUpdateAccountingLineEvent) {
            return AccountingLineAction.UPDATE.accessibilityErrorKey;
        } else if (event instanceof AttributedDeleteAccountingLineEvent) {
            return AccountingLineAction.DELETE.accessibilityErrorKey;
        } else {
            return "";
        }
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
    
}

