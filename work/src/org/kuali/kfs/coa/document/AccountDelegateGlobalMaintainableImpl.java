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
package org.kuali.kfs.coa.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountGlobalDetail;
import org.kuali.kfs.coa.businessobject.AccountDelegate;
import org.kuali.kfs.coa.businessobject.AccountDelegateGlobal;
import org.kuali.kfs.coa.businessobject.AccountDelegateGlobalDetail;
import org.kuali.kfs.coa.businessobject.OrganizationRoutingModel;
import org.kuali.kfs.coa.businessobject.OrganizationRoutingModelName;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.GenericRoutingInfo;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.kns.service.BusinessObjectService;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global delegates
 * and to help with using delegate models
 * 
 * @see OrganizationRoutingModelName
 */
public class AccountDelegateGlobalMaintainableImpl extends KualiGlobalMaintainableImpl implements GenericRoutingInfo {
    private Set<RoutingData> routingInfo;

    /**
     * This method is used for the creation of a delegate from a {@link OrganizationRoutingModelName}
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#setupNewFromExisting()
     */
    @Override
    public void setupNewFromExisting( MaintenanceDocument document, Map<String,String[]> parameters ) {
        super.setupNewFromExisting( document, parameters );

        AccountDelegateGlobal globalDelegate = (AccountDelegateGlobal) this.getBusinessObject();
        // 1. if model name, chart of accounts, and org code are all present
        // then let's see if we've actually got a model record
        if (!StringUtils.isBlank(globalDelegate.getModelName()) && !StringUtils.isBlank(globalDelegate.getModelChartOfAccountsCode()) && !StringUtils.isBlank(globalDelegate.getModelOrganizationCode())) {
            Map pkMap = new HashMap();
            pkMap.put("organizationRoutingModelName", globalDelegate.getModelName());
            pkMap.put("chartOfAccountsCode", globalDelegate.getModelChartOfAccountsCode());
            pkMap.put("organizationCode", globalDelegate.getModelOrganizationCode());

            OrganizationRoutingModelName globalDelegateTemplate = (OrganizationRoutingModelName) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(OrganizationRoutingModelName.class, pkMap);
            if (globalDelegateTemplate != null) {
                // 2. if there is a model record, then let's populate the global delegate
                // based on that
                for (OrganizationRoutingModel model : globalDelegateTemplate.getOrganizationRoutingModel()) {
                    if (model.isActive()) { // only populate with active models
                        AccountDelegateGlobalDetail newDelegate = new AccountDelegateGlobalDetail(model);
                        // allow deletion of the new delegate from the global delegate
                        newDelegate.setNewCollectionRecord(true);
                        globalDelegate.getDelegateGlobals().add(newDelegate);
                    }
                }
            }
        }
    }

    /**
     * This creates the particular locking representation for this global document.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        // create locking rep for each combination of account and object code
        List<MaintenanceLock> maintenanceLocks = new ArrayList();
        AccountDelegateGlobal delegateGlobal = (AccountDelegateGlobal) getBusinessObject();

        // hold all the locking representations in a set to make sure we don't get any duplicates
        Set<String> lockingRepresentations = new HashSet<String>();

        MaintenanceLock maintenanceLock;
        for (AccountGlobalDetail accountGlobalDetail : delegateGlobal.getAccountGlobalDetails()) {
            for (AccountDelegateGlobalDetail delegateGlobalDetail : delegateGlobal.getDelegateGlobals()) {
                StringBuilder lockRep = new StringBuilder();
                lockRep.append(AccountDelegate.class.getName());
                lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
                lockRep.append("chartOfAccountsCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(accountGlobalDetail.getChartOfAccountsCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("accountNumber");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(accountGlobalDetail.getAccountNumber());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("financialDocumentTypeCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(delegateGlobalDetail.getFinancialDocumentTypeCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("accountDelegateSystemId");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(delegateGlobalDetail.getAccountDelegateUniversalId());
                // FIXME above is a bit dangerous b/c it hard codes the attribute names, which could change (particularly
                // accountDelegateSystemId) - guess they should either be constants or obtained by looping through Delegate keys;
                // however, I copied this from elsewhere which had them hard-coded, so I'm leaving it for now

                if (!lockingRepresentations.contains(lockRep.toString())) {
                    maintenanceLock = new MaintenanceLock();
                    maintenanceLock.setDocumentNumber(delegateGlobal.getDocumentNumber());
                    maintenanceLock.setLockingRepresentation(lockRep.toString());
                    maintenanceLocks.add(maintenanceLock);
                    lockingRepresentations.add(lockRep.toString());
                }

                lockRep = new StringBuilder();
                lockRep.append(AccountDelegate.class.getName());
                lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
                lockRep.append("chartOfAccountsCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(accountGlobalDetail.getChartOfAccountsCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("accountNumber");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(accountGlobalDetail.getAccountNumber());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("financialDocumentTypeCode");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append(delegateGlobalDetail.getFinancialDocumentTypeCode());
                lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
                lockRep.append("accountsDelegatePrmrtIndicator");
                lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
                lockRep.append("true");

                if (!lockingRepresentations.contains(lockRep.toString())) {
                    maintenanceLock = new MaintenanceLock();
                    maintenanceLock.setDocumentNumber(delegateGlobal.getDocumentNumber());
                    maintenanceLock.setLockingRepresentation(lockRep.toString());
                    maintenanceLocks.add(maintenanceLock);
                    lockingRepresentations.add(lockRep.toString());
                }
            }
        }
        return maintenanceLocks;
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
    
    /**
     * Makes sure the routingInfo property is initialized and populates account review and org review data 
     * @see org.kuali.kfs.sys.document.workflow.GenericRoutingInfo#populateRoutingInfo()
     */
    public void populateRoutingInfo() {
        if (routingInfo == null) {
            routingInfo = new HashSet<RoutingData>();
        }
        
        routingInfo.add(getOrgReviewData());
    }
    
    /**
     * Generates a RoutingData object with the accounts to review
     * @return a properly initialized RoutingData object for account review
     */
    protected RoutingData getOrgReviewData() {
        RoutingData routingData = new RoutingData();
        routingData.setRoutingType(KualiOrgReviewAttribute.class.getName());
        
        routingData.setRoutingSet(gatherOrgsToReview());
        
        return routingData;
    }
    
    /**
     * Generates the set of OrgReviewRoutingData objects that should be taken into account while determining Org Review
     * @return a Set of OrgReviewRoutingData objects
     */
    protected Set<OrgReviewRoutingData> gatherOrgsToReview() {
        Set<OrgReviewRoutingData> orgsToReview = new HashSet<OrgReviewRoutingData>();
        
        final AccountDelegateGlobal delegateGlobal = (AccountDelegateGlobal)getBusinessObject();
        
        for (AccountGlobalDetail accountGlobalDetail : delegateGlobal.getAccountGlobalDetails()) {
            accountGlobalDetail.refreshReferenceObject("account");
            final OrgReviewRoutingData orgToReview = new OrgReviewRoutingData(accountGlobalDetail.getChartOfAccountsCode(), accountGlobalDetail.getAccount().getOrganizationCode());
            orgsToReview.add(orgToReview);
        }
        
        return orgsToReview;
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return AccountDelegate.class;
    }
}
