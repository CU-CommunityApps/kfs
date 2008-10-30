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
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementReason;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentAuthorizerBase;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.DocumentActionFlags;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.rice.kns.exception.DocumentInitiationAuthorizationException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * AssetAuthorizer for Asset edit.
 */
public class AssetRetirementAuthorizer extends FinancialSystemMaintenanceDocumentAuthorizerBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementAuthorizer.class);

    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);
    private static AssetRetirementService assetRetirementService = SpringContext.getBean(AssetRetirementService.class);
    private static BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);


    /**
     * Returns the set of authorization restrictions (if any) that apply to this Asset Retirement in this context.
     * 
     * @param document
     * @param user
     * @return a new set of {@link MaintenanceDocumentAuthorizations} that marks certain fields as necessary
     */
    public MaintenanceDocumentAuthorizations getFieldAuthorizations(MaintenanceDocument document, Person user) {

        MaintenanceDocumentAuthorizations auths = super.getFieldAuthorizations(document, user);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getNewMaintainableObject().getBusinessObject();

        if (!assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            auths.addHiddenAuthField(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_NUMBER);
            auths.addHiddenAuthField(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET_DESC);            
        }

        return auths;
    }


    /**
     * Hide action buttons in the screen when the user not allowed to proceed.
     * 
     * @param actionFlags
     */
    private void hideActions(DocumentActionFlags actionFlags) {
       actionFlags.setCanAdHocRoute(false);
        actionFlags.setCanApprove(false);
        actionFlags.setCanBlanketApprove(false);
        actionFlags.setCanRoute(false);
        actionFlags.setCanSave(false);
    }

    /**
     * Checks whether the BA document is active for the year end posting year.
     * 
     * @see org.kuali.rice.kns.authorization.DocumentAuthorizer#canInitiate(java.lang.String, org.kuali.rice.kns.bo.user.KualiUser)
     */
    @Override
    public void canInitiate(String documentTypeName, Person user) {

        super.canInitiate(documentTypeName, user);
        String refreshCaller = GlobalVariables.getKualiForm().getRefreshCaller();
        String reasonCode = StringUtils.substringAfter(refreshCaller, "::");  
        Map<String, Object> pkMap = new HashMap<String, Object>();
        pkMap.put(CamsPropertyConstants.AssetRetirementReason.RETIREMENT_REASON_CODE, reasonCode);

        AssetRetirementReason assetRetirementReason = (AssetRetirementReason) businessObjectService.findByPrimaryKey(AssetRetirementReason.class, pkMap);
        if (reasonCode.equals("M")){
            throw new DocumentInitiationAuthorizationException(CamsKeyConstants.Retirement.ERROR_DISALLOWED_MERGE_RETIREMENT_REASON_CODE, new String[]{reasonCode, "AssetRetirementGlobal"});
        }
        else if (assetRetirementReason != null && assetRetirementReason.isRetirementReasonRestrictionIndicator()) {
            throw new DocumentInitiationAuthorizationException(CamsKeyConstants.Retirement.ERROR_DISALLOWED_RETIREMENT_REASON_CODE, new String[]{reasonCode,  "AssetRetirementGlobal"});
        }
        else if (Arrays.asList(parameterService.getParameterValue(AssetGlobal.class, CamsConstants.Parameters.MERGE_SEPARATE_RETIREMENT_REASONS).split(";")).contains(reasonCode) && !user.isMember(CamsConstants.Workgroups.WORKGROUP_MERGE_SEPARATE_WORKGROUP)) {
            throw new DocumentInitiationAuthorizationException(CamsKeyConstants.Retirement.ERROR_DISALLOWED_MERGE_SEPARATE_REASON_CODE, new String[]{reasonCode, "AssetRetirementGlobal"});
        }
        else if (Arrays.asList(parameterService.getParameterValue(AssetRetirementGlobal.class, CamsConstants.Parameters.RAZE_RETIREMENT_REASONS).split(";")).contains(reasonCode) && !user.isMember(CamsConstants.Workgroups.WORKGROUP_RAZE_WORKGROUP)) {
            throw new DocumentInitiationAuthorizationException(CamsKeyConstants.Retirement.ERROR_DISALLOWED_RAZE_REASON_CODE, new String[]{reasonCode, "AssetRetirementGlobal"});
        }
    }

}

