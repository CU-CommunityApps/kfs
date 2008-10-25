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
package org.kuali.kfs.module.cam.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.fixture.AssetGlobalMaintainableFixture;
import org.kuali.kfs.module.cam.fixture.AssetRetirementGlobalMaintainableFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.util.KualiDecimal;

@ConfigureContext(session = khuntley)
//@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class AssetRetirementGlobalMaintainableImplTest extends KualiTestBase {
    private static Logger LOG = Logger.getLogger(AssetRetirementGlobalMaintainableImplTest.class);

    AssetRetirementService assetRetirementService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        assetRetirementService = SpringContext.getBean(AssetRetirementService.class);
    }


    public void testRouteInformation() throws Exception {
        RoutingData routingData = new RoutingData();
        RoutingData organizationRoutingData_a = new RoutingData();
        RoutingData accountRoutingData_a = new RoutingData();
        Set organizationRoutingSet_a  = new HashSet();        
        Set accountRoutingSet_a       = new HashSet();

        String chartOfAccountsCode;
        String accountNumber;
        String organizationcode;
        
        AssetRetirementGlobalMaintainableImpl assetRetirementGlobalMaintainableImpl = new AssetRetirementGlobalMaintainableImpl(); 

        // Creating document        
        AssetRetirementGlobal assetRetirementGlobal = AssetRetirementGlobalMaintainableFixture.RETIREMENT1.newAssetRetirement();        
        if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            assetRetirementGlobal.refreshReferenceObject("mergedTargetCapitalAsset");
            assetRetirementGlobal.getMergedTargetCapitalAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
            
            chartOfAccountsCode = assetRetirementGlobal.getMergedTargetCapitalAsset().getOrganizationOwnerChartOfAccountsCode();
            accountNumber = assetRetirementGlobal.getMergedTargetCapitalAsset().getOrganizationOwnerAccountNumber();
            organizationcode = assetRetirementGlobal.getMergedTargetCapitalAsset().getOrganizationOwnerAccount().getOrganizationCode();

            LOG.info("**** MERGING!*******");
            LOG.info("**** COA     :"+chartOfAccountsCode);
            LOG.info("**** Org Code:"+accountNumber);
            LOG.info("**** Account :"+organizationcode);
            
            organizationRoutingSet_a.add(new OrgReviewRoutingData(chartOfAccountsCode, organizationcode));
            accountRoutingSet_a.add(new RoutingAccount(chartOfAccountsCode, accountNumber));
        }

        for (AssetRetirementGlobalDetail detailLine : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            detailLine.refreshReferenceObject(CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET);

            chartOfAccountsCode = detailLine.getAsset().getOrganizationOwnerChartOfAccountsCode();
            accountNumber = detailLine.getAsset().getOrganizationOwnerAccountNumber();
            organizationcode = detailLine.getAsset().getOrganizationOwnerAccount().getOrganizationCode();

            LOG.info("**** **********************");
            LOG.info("**** COA     :"+detailLine.getAsset().getOrganizationOwnerChartOfAccountsCode());
            LOG.info("**** Org Code:"+detailLine.getAsset().getOrganizationOwnerAccount().getOrganizationCode());
            LOG.info("**** Account :"+detailLine.getAsset().getOrganizationOwnerAccountNumber());

            organizationRoutingSet_a.add(new OrgReviewRoutingData(chartOfAccountsCode, organizationcode));
            accountRoutingSet_a.add(new RoutingAccount(chartOfAccountsCode, accountNumber));
        }

        assetRetirementGlobalMaintainableImpl.setBusinessObject(assetRetirementGlobal);
        
        //Populating routing info.
        assetRetirementGlobalMaintainableImpl.populateRoutingInfo();
        Set<RoutingData> routingInfo_a = assetRetirementGlobalMaintainableImpl.getRoutingInfo();

        //Comparing document data with populated data. 
        for(Iterator i = routingInfo_a.iterator();i.hasNext();) {
            routingData = (RoutingData)i.next();        
            if (routingData.getRoutingTypes().contains(KualiOrgReviewAttribute.class.getSimpleName())) {
                organizationRoutingSet_a =routingData.getRoutingSet();            
            } else if (routingData.getRoutingTypes().contains(KualiAccountAttribute.class.getSimpleName())) {
                accountRoutingSet_a = routingData.getRoutingSet();
            }
        }


        //Testing.
        if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            assetRetirementGlobal.refreshReferenceObject("mergedTargetCapitalAsset");
            assetRetirementGlobal.getMergedTargetCapitalAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
            
            chartOfAccountsCode = assetRetirementGlobal.getMergedTargetCapitalAsset().getOrganizationOwnerChartOfAccountsCode();
            accountNumber = assetRetirementGlobal.getMergedTargetCapitalAsset().getOrganizationOwnerAccountNumber();
            organizationcode = assetRetirementGlobal.getMergedTargetCapitalAsset().getOrganizationOwnerAccount().getOrganizationCode();

            OrgReviewRoutingData orgReviewRoutingData_b = new OrgReviewRoutingData(chartOfAccountsCode, organizationcode);        
            assertTrue(organizationRoutingSet_a.contains(orgReviewRoutingData_b));

            RoutingAccount routingAccount_b = new RoutingAccount(chartOfAccountsCode, accountNumber);
            assertTrue(accountRoutingSet_a.contains(routingAccount_b));
            
            
            //Assert false
            orgReviewRoutingData_b = new OrgReviewRoutingData("??", organizationcode);        
            assertFalse(organizationRoutingSet_a.contains(orgReviewRoutingData_b));

            routingAccount_b = new RoutingAccount("??", accountNumber);
            assertFalse(accountRoutingSet_a.contains(routingAccount_b));
        }

        for (AssetRetirementGlobalDetail detailLine : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            detailLine.refreshReferenceObject(CamsPropertyConstants.AssetRetirementGlobalDetail.ASSET);

            chartOfAccountsCode = detailLine.getAsset().getOrganizationOwnerChartOfAccountsCode();
            accountNumber = detailLine.getAsset().getOrganizationOwnerAccountNumber();
            organizationcode = detailLine.getAsset().getOrganizationOwnerAccount().getOrganizationCode();

            LOG.info("**** **********************");
            LOG.info("**** COA     :"+detailLine.getAsset().getOrganizationOwnerChartOfAccountsCode());
            LOG.info("**** Org Code:"+detailLine.getAsset().getOrganizationOwnerAccount().getOrganizationCode());
            LOG.info("**** Account :"+detailLine.getAsset().getOrganizationOwnerAccountNumber());

            OrgReviewRoutingData orgReviewRoutingData_b = new OrgReviewRoutingData(chartOfAccountsCode, organizationcode);        
            assertTrue(organizationRoutingSet_a.contains(orgReviewRoutingData_b));

            RoutingAccount routingAccount_b = new RoutingAccount(chartOfAccountsCode, accountNumber);
            assertTrue(accountRoutingSet_a.contains(routingAccount_b));

            //Assert false
            orgReviewRoutingData_b = new OrgReviewRoutingData("??", organizationcode);        
            assertFalse(organizationRoutingSet_a.contains(orgReviewRoutingData_b));

            routingAccount_b = new RoutingAccount("??", accountNumber);
            assertFalse(accountRoutingSet_a.contains(routingAccount_b));
        }
    }
}

