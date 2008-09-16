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
package org.kuali.kfs.coa.document;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;

/**
 * An implementation of Maintainable for the SubAccount maintenance document; it exists mainly to populate routing info for account and org review
 */
public class SubAccountMaintainableImpl extends KualiMaintainableImpl implements GenericRoutingInfo {
    private Set<RoutingData> routingInfo;

    /**
     * Makes sure the routingInfo property is initialized and populates account review and org review data 
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        if (routingInfo == null) {
            routingInfo = new HashSet<RoutingData>();
        }
        
        routingInfo.add(getAccountReviewData());
        routingInfo.add(getOrgReviewData());
    }
    
    /**
     * Generates a RoutingData object with the accounts to review
     * @return a properly initialized RoutingData object for account review
     */
    protected RoutingData getAccountReviewData() {
        RoutingData routingData = new RoutingData();
        routingData.setRoutingType(KualiAccountAttribute.class.getName());
        
        Set<RoutingAccount> routingSet = new HashSet<RoutingAccount>();
        routingSet.add(gatherAccountToReview());
        routingData.setRoutingSet(routingSet);
        
        return routingData;
    }
    
    /**
     * @return an OrgReviewRoutingData object populated with the account information that this maintenance document should route to
     */
    protected RoutingAccount gatherAccountToReview() {
        final SubAccount subAccount = (SubAccount)getBusinessObject();
        return new RoutingAccount(subAccount.getChartOfAccountsCode(), subAccount.getAccountNumber());
    }
    
    /**
     * Generates a RoutingData object with the accounts to review
     * @return a properly initialized RoutingData object for account review
     */
    protected RoutingData getOrgReviewData() {
        RoutingData routingData = new RoutingData();
        routingData.setRoutingType(KualiOrgReviewAttribute.class.getName());
        
        Set<OrgReviewRoutingData> routingSet = new HashSet<OrgReviewRoutingData>();
        routingSet.add(gatherOrgToReview());
        routingData.setRoutingSet(routingSet);
        
        return routingData;
    }
    
    /**
     * @return an OrgReviewRoutingData object populated with the organization information that this maintenance document should route to
     */
    protected OrgReviewRoutingData gatherOrgToReview() {
        final SubAccount subAccount = (SubAccount)getBusinessObject();
        subAccount.refreshReferenceObject("account");
        return new OrgReviewRoutingData(subAccount.getChartOfAccountsCode(), subAccount.getAccount().getOrganizationCode());
    }

    /**
     * Gets the routingInfo attribute. 
     * @return Returns the routingInfo.
     */
    public Set<RoutingData> getRoutingInfo() {
        return routingInfo;
    }

    /**
     * Sets the routingInfo attribute value.
     * @param routingInfo The routingInfo to set.
     */
    public void setRoutingInfo(Set<RoutingData> routingInfo) {
        this.routingInfo = routingInfo;
    }

}
