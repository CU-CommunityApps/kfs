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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Org;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class RoutingFormOrganization extends PersistableBusinessObjectBase {

    private String chartOfAccountsCode;
    private String organizationCode;
    private String documentNumber;
    private boolean budgetIndicator;
    private boolean routingFormPrimaryOrganizationIndicator;

    private Org organization;

    /**
     * Default constructor.
     */
    public RoutingFormOrganization() {

    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    /**
     * Gets the organizationCode attribute.
     * 
     * @return Returns the organizationCode
     */
    public String getOrganizationCode() {
        return organizationCode;
    }

    /**
     * Sets the organizationCode attribute.
     * 
     * @param organizationCode The organizationCode to set.
     */
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the budgetIndicator attribute.
     * 
     * @return Returns the budgetIndicator
     */
    public boolean getBudgetIndicator() {
        return budgetIndicator;
    }

    /**
     * Sets the budgetIndicator attribute.
     * 
     * @param budgetIndicator The budgetIndicator to set.
     */
    public void setBudgetIndicator(boolean budgetIndicator) {
        this.budgetIndicator = budgetIndicator;
    }

    /**
     * Gets the routingFormPrimaryOrganizationIndicator attribute.
     * 
     * @return Returns the routingFormPrimaryOrganizationIndicator
     */
    public boolean getRoutingFormPrimaryOrganizationIndicator() {
        return routingFormPrimaryOrganizationIndicator;
    }

    /**
     * Sets the routingFormPrimaryOrganizationIndicator attribute.
     * 
     * @param routingFormPrimaryOrganizationIndicator The routingFormPrimaryOrganizationIndicator to set.
     */
    public void setRoutingFormPrimaryOrganizationIndicator(boolean routingFormPrimaryOrganizationIndicator) {
        this.routingFormPrimaryOrganizationIndicator = routingFormPrimaryOrganizationIndicator;
    }

    /**
     * Gets the organization attribute.
     * 
     * @return Returns the organization
     */
    public Org getOrganization() {
        return organization;
    }

    /**
     * Sets the organization attribute.
     * 
     * @param organization The organization to set.
     * @deprecated
     */
    public void setOrganization(Org organization) {
        this.organization = organization;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
