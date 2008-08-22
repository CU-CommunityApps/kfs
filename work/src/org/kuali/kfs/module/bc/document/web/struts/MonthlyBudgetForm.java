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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.util.GlobalVariables;

public class MonthlyBudgetForm extends BudgetExpansionForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MonthlyBudgetForm.class);

    private BudgetConstructionMonthly budgetConstructionMonthly;
    private boolean monthlyPersisted;

    // TODO remove - was here originally for kul:page tag use
    // private String docTypeName;

    // TODO probably need to push these and some url parms to new superclass BCExpansionForm??
    private boolean hideDetails = false;
    protected Map editingMode;

    // url parameters sent from BCDoc
    private String documentNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;

    public MonthlyBudgetForm() {
        super();
        setBudgetConstructionMonthly(new BudgetConstructionMonthly());
        this.editingMode = new HashMap();
    }

    /**
     * Gets the budgetConstructioMonthly attribute.
     * 
     * @return Returns the budgetConstructioMonthly.
     */
    public BudgetConstructionMonthly getBudgetConstructionMonthly() {
        return budgetConstructionMonthly;
    }

    /**
     * Sets the budgetConstructioMonthly attribute value.
     * 
     * @param budgetConstructioMonthly The budgetConstructioMonthly to set.
     */
    public void setBudgetConstructionMonthly(BudgetConstructionMonthly budgetConstructionMonthly) {
        this.budgetConstructionMonthly = budgetConstructionMonthly;
    }

    /**
     * Gets the docTypeName attribute.
     * 
     * @return Returns the docTypeName.
     */
    // TODO remove - was here originally for kul:page tag use
    // public String getDocTypeName() {
    // return docTypeName;
    // }
    /**
     * Sets the docTypeName attribute value.
     * 
     * @param docTypeName The docTypeName to set.
     */
    // TODO remove - was here originally for kul:page tag use
    // public void setDocTypeName(String docTypeName) {
    // this.docTypeName = docTypeName;
    // }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the subAccountNumber attribute.
     * 
     * @return Returns the subAccountNumber.
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     * 
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the monthlyPersisted attribute. 
     * @return Returns the monthlyPersisted.
     */
    public boolean isMonthlyPersisted() {
        return monthlyPersisted;
    }

    /**
     * Sets the monthlyPersisted attribute value.
     * @param monthlyPersisted The monthlyPersisted to set.
     */
    public void setMonthlyPersisted(boolean monthlyPersisted) {
        this.monthlyPersisted = monthlyPersisted;
    }

    /**
     * Gets the hideDetails attribute.
     * 
     * @return Returns the hideDetails.
     */
    public boolean isHideDetails() {
        return hideDetails;
    }

    /**
     * Sets the hideDetails attribute value.
     * 
     * @param hideDetails The hideDetails to set.
     */
    public void setHideDetails(boolean hideDetails) {
        this.hideDetails = hideDetails;
    }

    /**
     * @return Map of editingModes for this BC expansion screen, as set during the most recent call to
     *         populate(javax.servlet.http.HttpServletRequest)
     */
    public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Set editingMode for this BC Expansion screen TODO verify comments from KualiDocumentFormBase - unfortunately necessary, since
     * validation failures bypass the normal populateAuthorizationFields call. (Unfortunate because it makes the UI just a bit
     * easier to hack, until we have the back-end checks of editingMode et al in place.)
     */
    public void setEditingMode(Map editingMode) {
        this.editingMode = editingMode;
    }

    /**
     * Updates authorization-related form fields based on the current form contents TODO should probably move this to extension
     * class
     */
    public void populateAuthorizationFields(BudgetConstructionDocumentAuthorizer documentAuthorizer) {

        useBCAuthorizer(documentAuthorizer);

        // TODO probably need BCAuthorizationConstants extension
        if (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) {
            throw new AuthorizationException(GlobalVariables.getUserSession().getFinancialSystemUser().getPersonName(), "view", this.getAccountNumber() + ", " + this.getSubAccountNumber());
        }

        /*
         * //TODO from KualiDocumentFormBase - remove when ready if (isFormDocumentInitialized()) {
         * useBCAuthorizer(documentAuthorizer); // graceless hack which takes advantage of the fact that here and only here will we
         * have guaranteed access to the // correct DocumentAuthorizer if
         * (getEditingMode().containsKey(AuthorizationConstants.EditMode.UNVIEWABLE)) { throw new
         * AuthorizationException(GlobalVariables.getUserSession().getKfsUser().getPersonName(), "view",
         * this.getAccountNumber()+", "+this.getSubAccountNumber()); } }
         */
    }

    /*
     * TODO should probably move this to extension class
     */
    protected void useBCAuthorizer(BudgetConstructionDocumentAuthorizer documentAuthorizer) {
        UniversalUser kualiUser = GlobalVariables.getUserSession().getFinancialSystemUser();

//        setEditingMode(documentAuthorizer.getEditMode(this.getUniversityFiscalYear(), this.getChartOfAccountsCode(), this.getAccountNumber(), this.getSubAccountNumber(), kualiUser));
        setEditingMode(documentAuthorizer.getEditModeFromSession());

        // TODO probably don't need these, editingmode drives expansion screen actions
        // setDocumentActionFlags(documentAuthorizer.getDocumentActionFlags(document, kualiUser));
    }
}
