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

package org.kuali.kfs.coa.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * 
 */
public class OrganizationRoutingModelName extends PersistableBusinessObjectBase {
    private static final Logger LOG = Logger.getLogger(OrganizationRoutingModelName.class);

    private String chartOfAccountsCode;
    private String organizationCode;
    private String organizationRoutingModelName;
    private List<OrganizationRoutingModel> organizationRoutingModel;

    private Org organization;
    private Chart chartOfAccounts;

    /**
     * Default constructor.
     */
    public OrganizationRoutingModelName() {
        organizationRoutingModel = new TypedArrayList(OrganizationRoutingModel.class);
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
     * Gets the organizationRoutingModelName attribute.
     * 
     * @return Returns the organizationRoutingModelName
     */
    public String getOrganizationRoutingModelName() {
        return organizationRoutingModelName;
    }

    /**
     * Sets the organizationRoutingModelName attribute.
     * 
     * @param organizationRoutingModelName The organizationRoutingModelName to set.
     */
    public void setOrganizationRoutingModelName(String organizationRoutingModelName) {
        this.organizationRoutingModelName = organizationRoutingModelName;
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
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the organizationRoutingModel attribute.
     * 
     * @return Returns the organizationRoutingModel.
     */
    public List<OrganizationRoutingModel> getOrganizationRoutingModel() {
        return organizationRoutingModel;
    }

    /**
     * Sets the organizationRoutingModel attribute value.
     * 
     * @param organizationRoutingModel The organizationRoutingModel to set.
     */
    public void setOrganizationRoutingModel(List<OrganizationRoutingModel> organizationRoutingModel) {
        this.organizationRoutingModel = organizationRoutingModel;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("organizationCode", this.organizationCode);
        m.put("organizationRoutingModelName", this.organizationRoutingModelName);
        return m;
    }

    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#linkEditableUserFields()
     */
    @Override
    public void linkEditableUserFields() {
        super.linkEditableUserFields();
        if (this == null) {
            throw new IllegalArgumentException("parameter passed in was null");
        }
        List bos = new ArrayList();
        bos.addAll(getOrganizationRoutingModel());
        SpringContext.getBean(BusinessObjectService.class).linkUserFields(bos);
    }
}
