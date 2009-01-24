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

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
// @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class AssetMaintainableImplTest extends KualiTestBase {
    private static Logger LOG = Logger.getLogger(AssetMaintainableImplTest.class);

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
// TODO fix for kim
//    public void testRouteInformation() throws Exception {
//        RoutingData routingData = new RoutingData();
//        RoutingData organizationRoutingData_a = new RoutingData();
//        RoutingData accountRoutingData_a = new RoutingData();
//        Set organizationRoutingSet_a = new HashSet();
//        Set accountRoutingSet_a = new HashSet();
//        Set assetNumberRoutineSet_a = new HashSet();
//        Set assetTagNumberRoutineSet_a = new HashSet();
//
//        AssetMaintainableImpl assetMaintainableImpl = new AssetMaintainableImpl();
//        assetMaintainableImpl.setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);
//
//        // Creating document
//        Asset asset = AssetMaintainableFixture.ASSET1.newAsset();
//        asset.setCampusTagNumber("TAGNO");
//        asset.refreshReferenceObject(CamsPropertyConstants.AssetGlobal.ORGANIZATION_OWNER_ACCOUNT);
//        assetMaintainableImpl.setBusinessObject(asset);
//// TODO fix for kim
////        // Populating routing info.
////        assetMaintainableImpl.populateRoutingInfo();
////        Set<RoutingData> routingInfo_a = assetMaintainableImpl.getRoutingInfo();
////
////        // Comparing document data with populated data.
////        for (Iterator i = routingInfo_a.iterator(); i.hasNext();) {
////            routingData = (RoutingData) i.next();
////            if (routingData.getRoutingTypes().contains(KualiOrgReviewAttribute.class.getSimpleName())) {
////                organizationRoutingSet_a = routingData.getRoutingSet();
////            }
////            else if (routingData.getRoutingTypes().contains(KualiAccountAttribute.class.getSimpleName())) {
////                accountRoutingSet_a = routingData.getRoutingSet();
////            }
////            else if (routingData.getRoutingTypes().contains(RoutingAssetNumber.class.getSimpleName())) {
////                assetNumberRoutineSet_a = routingData.getRoutingSet();
////            }
////            else if (routingData.getRoutingTypes().contains(RoutingAssetTagNumber.class.getSimpleName())) {
////                assetTagNumberRoutineSet_a = routingData.getRoutingSet();
////            }
////        }
//
//        // Asset global information
//        OrgReviewRoutingData orgReviewRoutingData_b = new OrgReviewRoutingData(asset.getOrganizationOwnerChartOfAccountsCode(), asset.getOrganizationOwnerAccount().getOrganizationCode());
//        assertTrue(organizationRoutingSet_a.contains(orgReviewRoutingData_b));
//
//        RoutingAccount routingAccount_b = new RoutingAccount(asset.getOrganizationOwnerChartOfAccountsCode(), asset.getOrganizationOwnerAccountNumber());
//        assertTrue(accountRoutingSet_a.contains(routingAccount_b));
//
//        RoutingAssetNumber routingAssetNumber_b = new RoutingAssetNumber(asset.getCapitalAssetNumber().toString());
//        assertTrue(assetNumberRoutineSet_a.contains(routingAssetNumber_b));
//
//        RoutingAssetTagNumber routingAssetTagNumber_b = new RoutingAssetTagNumber(asset.getCampusTagNumber());
//        assertTrue(assetTagNumberRoutineSet_a.contains(routingAssetTagNumber_b));
//
//        // assert False
//
//        // Asset global information
//        orgReviewRoutingData_b = new OrgReviewRoutingData("??", asset.getOrganizationOwnerAccount().getOrganizationCode());
//        assertFalse(organizationRoutingSet_a.contains(orgReviewRoutingData_b));
//
//        routingAccount_b = new RoutingAccount("??", asset.getOrganizationOwnerAccountNumber());
//        assertFalse(accountRoutingSet_a.contains(routingAccount_b));
//
//        routingAssetNumber_b = new RoutingAssetNumber("??");
//        assertFalse(assetNumberRoutineSet_a.contains(routingAssetNumber_b));
//
//        routingAssetTagNumber_b = new RoutingAssetTagNumber("??");
//        assertFalse(assetTagNumberRoutineSet_a.contains(routingAssetTagNumber_b));
//    }
}
