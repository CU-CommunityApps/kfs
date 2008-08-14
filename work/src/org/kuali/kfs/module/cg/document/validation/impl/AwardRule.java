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

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.kfs.module.cg.businessobject.AwardOrganization;
import org.kuali.kfs.module.cg.businessobject.AwardProjectDirector;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Rules for the Award maintenance document.
 */
public class AwardRule extends CGMaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(AwardRule.class);

    private Award newAwardCopy;

    private static final String GRANT_DESCRIPTION_NPT = "NPT";
    private static final String GRANT_DESCRIPTION_OPT = "OPT";
    private static final String[] NON_FED_GRANT_DESCS = new String[] { GRANT_DESCRIPTION_NPT, GRANT_DESCRIPTION_OPT };

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("Entering AwardRule.processCustomSaveDocumentBusinessRules");
        processCustomRouteDocumentBusinessRules(document);
        LOG.info("Leaving AwardRule.processCustomSaveDocumentBusinessRules");
        return true; // save despite error messages
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("Entering AwardRule.processCustomRouteDocumentBusinessRules");
        boolean success = true;
        success &= checkProposal();
        success &= checkEndAfterBegin(newAwardCopy.getAwardBeginningDate(), newAwardCopy.getAwardEndingDate(), KFSPropertyConstants.AWARD_ENDING_DATE);
        success &= checkPrimary(newAwardCopy.getAwardOrganizations(), AwardOrganization.class, KFSPropertyConstants.AWARD_ORGRANIZATIONS, Award.class);
        success &= checkPrimary(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS, Award.class);
        success &= checkAccounts();
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkProjectDirectorsExist(newAwardCopy.getAwardAccounts(), AwardAccount.class, KFSPropertyConstants.AWARD_ACCOUNTS);
        success &= checkProjectDirectorsStatuses(newAwardCopy.getAwardProjectDirectors(), AwardProjectDirector.class, KFSPropertyConstants.AWARD_PROJECT_DIRECTORS);
        success &= checkFederalPassThrough();
        success &= checkAgencyNotEqualToFederalPassThroughAgency(newAwardCopy.getAgency(), newAwardCopy.getFederalPassThroughAgency(), KFSPropertyConstants.AGENCY_NUMBER, KFSPropertyConstants.FEDERAL_PASS_THROUGH_AGENCY_NUMBER);
        LOG.info("Leaving AwardRule.processCustomRouteDocumentBusinessRules");
        return success;
    }

    /**
     * checks to see if at least 1 award account exists
     * 
     * @return true if the award contains at least 1 {@link AwardAccount}, false otherwise
     */
    private boolean checkAccounts() {
        boolean success = true;
        Collection<AwardAccount> awardAccounts = newAwardCopy.getAwardAccounts();

        if (ObjectUtils.isNull(awardAccounts) || awardAccounts.isEmpty()) {
            String elementLabel = SpringContext.getBean(DataDictionaryService.class).getCollectionElementLabel(Award.class.getName(), KFSPropertyConstants.AWARD_ACCOUNTS, AwardAccount.class);
            putFieldError(KFSPropertyConstants.AWARD_ACCOUNTS, KFSKeyConstants.ERROR_ONE_REQUIRED, elementLabel);
            success = false;
        }

        return success;
    }

    /**
     * checks to see if:
     * <ol>
     * <li> a proposal has already been awarded
     * <li> a proposal is inactive
     * </ol>
     * 
     * @return
     */
    private boolean checkProposal() {
        boolean success = true;
        if (AwardRuleUtil.isProposalAwarded(newAwardCopy)) {
            putFieldError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PROPOSAL_AWARDED, newAwardCopy.getProposalNumber().toString());
            success = false;
        }
        // SEE KULCG-315 for details on why this code is commented out.
        // else if (AwardRuleUtil.isProposalInactive(newAwardCopy)) {
        // putFieldError(KFSPropertyConstants.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_AWARD_PROPOSAL_INACTIVE,
        // newAwardCopy.getProposalNumber().toString());
        // }

        return success;
    }

    /**
     * checks if the required federal pass through fields are filled in if the federal pass through indicator is yes
     * 
     * @return
     */
    private boolean checkFederalPassThrough() {
        boolean success = true;
        success = super.checkFederalPassThrough(newAwardCopy.getFederalPassThroughIndicator(), newAwardCopy.getAgency(), newAwardCopy.getFederalPassThroughAgencyNumber(), Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_INDICATOR);

        if (newAwardCopy.getFederalPassThroughIndicator()) {

            String indicatorLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_INDICATOR);
            if (null == newAwardCopy.getFederalPassThroughFundedAmount()) {
                String amountLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(Award.class, KFSPropertyConstants.FEDERAL_PASS_THROUGH_FUNDED_AMOUNT);
                putFieldError(KFSPropertyConstants.FEDERAL_PASS_THROUGH_FUNDED_AMOUNT, KFSKeyConstants.ERROR_FPT_AGENCY_NUMBER_REQUIRED, new String[] { amountLabel, indicatorLabel });
                success = false;
            }
            String grantDescCode = newAwardCopy.getGrantDescription().getGrantDescriptionCode();
            if (StringUtils.isBlank(grantDescCode) || !Arrays.asList(NON_FED_GRANT_DESCS).contains(grantDescCode)) {
                String grantDescLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeErrorLabel(Award.class, KFSPropertyConstants.GRANT_DESCRIPTION_CODE);
                putFieldError(KFSPropertyConstants.GRANT_DESCRIPTION_CODE, KFSKeyConstants.ERROR_GRANT_DESCRIPTION_INVALID_WITH_FED_PASS_THROUGH_AGENCY_INDICATOR_SELECTED);
                success = false;
            }
        }
        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // oldAward = (Award) super.getOldBo();
        newAwardCopy = (Award) super.getNewBo();
    }

}
