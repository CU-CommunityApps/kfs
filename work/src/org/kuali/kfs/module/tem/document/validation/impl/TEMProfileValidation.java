/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.module.tem.businessobject.TEMProfileArranger;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TEMProfileValidation extends MaintenanceDocumentRuleBase{
    protected static final String TRAVEL_ARRANGERS_SECTION_ID = "TEMProfileArrangers";

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#dataDictionaryValidate(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean dataDictionaryValidate(MaintenanceDocument document) {
        BusinessObjectService businessObjectService = getBusinessObjectService();
        TEMProfile profile = (TEMProfile) document.getNewMaintainableObject().getBusinessObject();
        TravelService travelService = SpringContext.getBean(TravelService.class);

        boolean success = true;
        List<String> paths = GlobalVariables.getMessageMap().getErrorPath();
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        paths.add("document");
        paths.add("newMaintainableObject");


        if (!StringUtils.isEmpty(profile.getPhoneNumber())){
            String errorMessage = travelService.validatePhoneNumber(profile.getCountryCode(), profile.getPhoneNumber(), TemKeyConstants.ERROR_PHONE_NUMBER);
            if (!StringUtils.isBlank(errorMessage)) {
                GlobalVariables.getMessageMap().putError(TEMProfileProperties.PHONE_NUMBER, errorMessage, new String[] { "Phone Number"});
                success = false;
            }
        }

        if (!StringUtils.isEmpty(profile.getDefaultChartCode())){
            fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, profile.getDefaultChartCode());
            List<Chart> chartList = (List<Chart>) businessObjectService.findMatching(Chart.class, fieldValues);
            if (chartList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.DEFAULT_CHART_CODE,
                        TemKeyConstants.ERROR_TEM_PROFILE_CHART_NOT_EXIST, profile.getDefaultChartCode());
                success = false;
            }
        }
        else{
            profile.setChart(null);
        }

        if (success && !StringUtils.isEmpty(profile.getDefaultAccount())){
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, profile.getDefaultAccount());
            List<Account> accountList = (List<Account>) businessObjectService.findMatching(Account.class, fieldValues);
            if (accountList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.DEFAULT_ACCOUNT_NUMBER,
                        TemKeyConstants.ERROR_TEM_PROFILE_ACCOUNT_NOT_EXIST, profile.getDefaultAccount());
                success = false;
            }
        }
        else{
            profile.setAccount(null);
        }

        if (success && !StringUtils.isEmpty(profile.getDefaultSubAccount())){
            fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, profile.getDefaultSubAccount());
            List<SubAccount> subAccountList = (List<SubAccount>) businessObjectService.findMatching(SubAccount.class, fieldValues);
            if (subAccountList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.DEFAULT_SUB_ACCOUNT_NUMBER,
                        TemKeyConstants.ERROR_TEM_PROFILE_SUB_ACCOUNT_NOT_EXIST, profile.getDefaultSubAccount());
                success = false;
            }
        }
        else{
            profile.setSubAccount(null);
        }

        if (!StringUtils.isEmpty(profile.getDefaultProjectCode())){
            fieldValues.clear();
            fieldValues.put(KFSConstants.GENERIC_CODE_PROPERTY_NAME, profile.getDefaultProjectCode());
            List<ProjectCode> subAccountList = (List<ProjectCode>) businessObjectService.findMatching(ProjectCode.class, fieldValues);
            if (subAccountList.size() == 0){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.DEFAULT_PROJECT_CODE,
                        TemKeyConstants.ERROR_TEM_PROFILE_PROJECT_CODE_NOT_EXIST, profile.getDefaultProjectCode());
                success = false;
            }
        }
        else{
            profile.setProject(null);
        }

        for (int i=0;i<profile.getAccounts().size();i++){
            paths.add(TemPropertyConstants.TEMProfileProperties.ACCOUNTS + "[" + i + "]");
            TEMProfileAccount account = profile.getAccounts().get(i);
            paths.remove(paths.size()-1);
        }

        //Arranger section validation
        if(profile.getArrangers() != null){
            int arrangerPrimaryCount = 0;
            Set<String> arrangerId = new HashSet<String>();
            for (TEMProfileArranger arranger : profile.getArrangers()){
                if(arranger.getPrimary()){
                    arrangerPrimaryCount++;
                }

                arrangerId.add(arranger.getPrincipalName());

                paths.add(TemPropertyConstants.TEMProfileProperties.ARRANGERS);

                paths.remove(paths.size()-1);
            }

            //check for multiple primary arrangers; only 1 primary allowed.
            if(arrangerPrimaryCount > 1){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ARRANGERS,
                        TemKeyConstants.ERROR_TEM_PROFILE_ARRANGER_PRIMARY);
                success = false;
            }

            //check for duplicate arrangers
            if(profile.getArrangers().size() != arrangerId.size()){
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ARRANGERS,
                        TemKeyConstants.ERROR_TEM_PROFILE_ARRANGER_DUPLICATE);
                success = false;
            }

        }

        paths.clear();
        success = success && super.dataDictionaryValidate(document);
        if(success){
            Person user = GlobalVariables.getUserSession().getPerson();
            profile.setUpdatedBy(user.getPrincipalName());
            Date current = new Date();
            profile.setLastUpdate(new java.sql.Date(current.getTime()));
        }
        return success;

    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {

        //set other arranger as primary false if the new arranger is the primary
        if (line instanceof TEMProfileArranger){

            TEMProfile profile = (TEMProfile)document.getNewMaintainableObject().getBusinessObject();
            TEMProfileArranger arranger = (TEMProfileArranger)line;



            //check that the arranger's org is under the initiator's
            if (TemConstants.NONEMP_TRAVELER_TYP_CD.equals(profile.getTravelerTypeCode())) {
                TravelService travelService = SpringContext.getBean(TravelService.class);
                String initiatorId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
                final Person initiator = SpringContext.getBean(PersonService.class).getPerson(initiatorId);

            }

            if (arranger.getPrimary()){
                for (TEMProfileArranger tempArranger : ((TEMProfile)document.getNewMaintainableObject().getBusinessObject()).getArrangers()){
                    tempArranger.setPrimary(false);
                }

                if (TemConstants.NONEMP_TRAVELER_TYP_CD.equals(profile.getTravelerTypeCode())) {
                    String profileChart = profile.getHomeDeptChartOfAccountsCode();
                    String profileOrg = profile.getHomeDeptOrgCode();

                    final Person arrangerPerson = SpringContext.getBean(PersonService.class).getPerson(arranger.getPrincipalId());
                    String primaryDeptCode[] = arrangerPerson.getPrimaryDepartmentCode().split("-");
                    if(primaryDeptCode != null && primaryDeptCode.length == 2){
                        profile.setHomeDeptChartOfAccountsCode(primaryDeptCode[0]);
                        profile.setHomeDeptOrgCode(primaryDeptCode[1]);
                    }
                }

            }
        }
        // check for CreditCardAgency accounts
        else if (line instanceof TEMProfileAccount){
            TEMProfileAccount account = (TEMProfileAccount) line;
            //minimum length
            if (StringUtils.isNotEmpty(account.getAccountNumber()) && account.getAccountNumber().length() < 4){
            	String errorMessage[] = null;
                errorMessage = new String[] { "Account Number", "4" };
                GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_MIN_LENGTH, errorMessage);
                return false;
            }

            //not duplicate an existing account in the system
            if (StringUtils.isNotEmpty(account.getAccountNumber())){
                Map<String,Object> criteria = new HashMap<String,Object>();
                criteria.put(TemPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
                criteria.put(TemPropertyConstants.CREDIT_CARD_AGENCY_ID, account.getCreditCardAgencyId());
                Collection<TEMProfileAccount> profileAccounts = getBusinessObjectService().findMatching(TEMProfileAccount.class, criteria);
                if (!profileAccounts.isEmpty()){
                    GlobalVariables.getMessageMap().putError(TemPropertyConstants.TEMProfileProperties.ACCOUNT_NUMBER, TemKeyConstants.ERROR_TEM_PROFILE_ACCOUNT_ID_DUPLICATE, account.getAccountNumber());
                    return false;
                }
            }
        }

        return super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);
    }

    /**
     * Checks that, if the profile represents a non-employee, the profile has an active arranger
     * @param profile the profile to check
     * @return true if the profile passed the validation, false otherwise
     */
    protected boolean checkActiveArrangersForNonEmployees(TEMProfile profile) {
        boolean success = true;
        if (profile != null && !StringUtils.isBlank(profile.getTravelerTypeCode()) && profile.getTravelerTypeCode().equals(TemConstants.NONEMP_TRAVELER_TYP_CD)) {
            // we've got a non-employee.  let's see if they have at least one active arranger
            if (!anyActiveArrangers(profile)) {
                GlobalVariables.getMessageMap().putErrorForSectionId(TRAVEL_ARRANGERS_SECTION_ID, TemKeyConstants.ERROR_TEM_PROFILE_NONEMPLOYEE_MUST_HAVE_ACTIVE_ARRANGER);
                success = false;
            }
        }
        return success;
    }

    /**
     * Determines if the given profile has any active arrangers
     * @param profile the profile to check
     * @return true if there are any active arrangers, false otherwise
     */
    protected boolean anyActiveArrangers(TEMProfile profile) {
        for (TEMProfileArranger arranger : profile.getArrangers()) {
            if (arranger.isActive()) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        TEMProfile profile = (TEMProfile) document.getNewMaintainableObject().getBusinessObject();
        if (ObjectUtils.isNotNull(profile.getHomeDeptOrg())){
            if (!profile.getHomeDeptOrg().isActive()) {
                putFieldError(TEMProfileProperties.HOME_DEPARTMENT, TemKeyConstants.ERROR_TEM_PROFILE_ORGANIZATION_INACTIVE, profile.getHomeDeptChartOfAccountsCode()+"-"+profile.getHomeDeptOrgCode());
                success = false;
            }
        }
        success &= checkActiveArrangersForNonEmployees(profile);
        return success;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }

}
