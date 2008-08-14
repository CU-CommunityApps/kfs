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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Rules for the Award maintenance document.
 */
public class AwardRuleUtil {
    /**
     * Determines if a proposal has already been awarded
     * 
     * @param award the award to check the proposal for
     * @return true if the award's proposal has already been awarded
     */
    public static boolean isProposalAwarded(Award award) {
        if (ObjectUtils.isNull(award)) {
            return false;
        }

        Long proposalNumber = award.getProposalNumber();
        Map<String, Object> awardPrimaryKeys = new HashMap<String, Object>();
        awardPrimaryKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        Award result = (Award) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Award.class, awardPrimaryKeys);

        boolean awarded = ObjectUtils.isNotNull(result) && !StringUtils.equals(award.getObjectId(), result.getObjectId());

        return awarded;
    }


    /**
     * Per KULCG-315 - Proposals should not be designated as inactive. This functionality is not yet implemented and this rule
     * should not be applied at this time. I'm leaving this code here in case the functionality gets added down the road.
     */
    // /**
    // * determines if a proposal is inactive
    // *
    // * @param award the award to check the proposal for
    // * @return true if the award's proposal has already been set to inactive
    // */
    // public static boolean isProposalInactive(Award award) {
    // if (ObjectUtils.isNull(award)) {
    // return false;
    // }
    //
    // Long proposalNumber = award.getProposalNumber();
    // Map<String, Object> awardPrimaryKeys = new HashMap<String, Object>();
    // awardPrimaryKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
    // Award result = (Award) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Award.class, awardPrimaryKeys);
    //
    // boolean inactive = ObjectUtils.isNotNull(result) && !result.isActive();
    //
    // return inactive;
    // }
}
