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
package org.kuali.kfs.module.tem.document.maintenance;

import static org.kuali.kfs.module.tem.TemConstants.EMP_TRAVELER_TYP_CD;
import static org.kuali.kfs.module.tem.TemConstants.NONEMP_TRAVELER_TYP_CD;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileAccount;
import org.kuali.kfs.module.tem.datadictionary.mask.CreditCardMaskFormatter;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.mask.Mask;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TemProfileMaintainable extends FinancialSystemMaintainable {

    private static final Logger LOG = Logger.getLogger(TemProfileMaintainable.class);

	/**
     * This will create a new profile from either a principal id or from a customer number depending on what got filled out
     *
     *
     * @see org.kuali.rice.kns.maintenance.Maintainable#setupNewFromExisting()
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String,String[]> parameters) {
        super.processAfterNew(document, parameters);
        TravelerService travelerService = SpringContext.getBean(TravelerService.class);
        SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);

        TemProfile temProfile = (TemProfile) super.getBusinessObject();
        Integer profileId = temProfile.getProfileId();
        if(ObjectUtils.isNull(profileId)) {
            Integer newProfileId = sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_SEQ_NAME).intValue();
            temProfile.setProfileId(newProfileId);
        }
        String principalId = "";
        if (parameters.containsKey(KFSPropertyConstants.PRINCIPAL_ID)) {
            principalId = parameters.get(KFSPropertyConstants.PRINCIPAL_ID)[0];
            if(StringUtils.isNotBlank(principalId)) {
                //we want to set the principal
                Person person = getPersonService().getPerson(principalId);
                temProfile.setPrincipal(person);
                temProfile.setPrincipalId(principalId);
                if(travelerService.isKimPersonEmployee(person)) {
                    temProfile.setTravelerTypeCode(EMP_TRAVELER_TYP_CD);
                } else {
                    temProfile.setTravelerTypeCode(NONEMP_TRAVELER_TYP_CD);
                }
            }
        }
        String customerNumber = "";
        if (parameters.containsKey(TemPropertyConstants.TemProfileProperties.CUSTOMER_NUMBER)) {
            customerNumber = parameters.get(TemPropertyConstants.TemProfileProperties.CUSTOMER_NUMBER)[0];
            if(StringUtils.isNotBlank(customerNumber)) {
                //we want to set the customer
                AccountsReceivableCustomer person = getAccountsReceivableModuleService().findCustomer(customerNumber);
                temProfile.setCustomer(person);
                temProfile.setCustomerNumber(customerNumber);
                if(travelerService.isCustomerEmployee(person)) {
                    temProfile.setTravelerTypeCode(EMP_TRAVELER_TYP_CD);
                } else {
                    temProfile.setTravelerTypeCode(NONEMP_TRAVELER_TYP_CD);
                }
            }
        }

        travelerService.populateTemProfile(temProfile);
        if (document.isNew()) {
            if (StringUtils.isNotBlank(principalId)) {
                document.getDocumentHeader().setDocumentDescription(trimDescription(TemConstants.NEW_TEM_PROFILE_DESCRIPTION_PREFIX + temProfile.getPrincipal().getName()));
            }
            else if (StringUtils.isNotBlank(customerNumber)) {
                document.getDocumentHeader().setDocumentDescription(trimDescription(TemConstants.NEW_TEM_PROFILE_DESCRIPTION_PREFIX + temProfile.getCustomer().getCustomerName()));
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List<Section> getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        Person user = GlobalVariables.getUserSession().getPerson();

        List<Section> sections = super.getSections(document, document.getOldMaintainableObject());
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        TemProfile temProfile = (TemProfile) super.getBusinessObject();
        boolean profileAdmin = getTemRoleService().isProfileAdmin(currentUser, temProfile.getHomeDepartment());

        if (!user.getPrincipalId().equals(((TemProfile)document.getOldMaintainableObject().getBusinessObject()).getPrincipalId())){
            if (!profileAdmin) {
                // user is not the traveler or a profile admin
                for (Section section : sections){
                    if (section.getSectionId().equals(TemPropertyConstants.TemProfileProperties.TEM_PROFILE_ADMINISTRATOR)){
                    	for (Row row : section.getRows()){
                            for (Field field : row.getFields()){
                               if (field.getContainerRows() != null && field.getContainerRows().size() > 0){
                                    for (Row containerRow :field.getContainerRows()){
                                        for (Field containerField : containerRow.getFields()){
                                            //Get only the accounts that have already been created
                                            if (containerField.getPropertyName().contains("]." + TemPropertyConstants.TemProfileProperties.ACCOUNT_NUMBER)){
                                                String[] splitTemp = containerField.getPropertyName().split("\\[");
                                                splitTemp = splitTemp[1].split("\\]");
                                                int index = Integer.parseInt(splitTemp[0]);
                                                containerField.setSecure(true);
                                                CreditCardMaskFormatter formatter = new CreditCardMaskFormatter();
                                                Mask mask = new Mask();
                                                mask.setMaskFormatter(formatter);
                                                String display = mask.maskValue(((TemProfile)document.getDocumentBusinessObject()).getAccounts().get(index).getAccountNumber());
                                                containerField.setDisplayMaskValue(display);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Section section : sections){
            if (section.getSectionId().equals(TemPropertyConstants.TemProfileProperties.TEM_PROFILE_ADMINISTRATOR)){
                if (!profileAdmin) {
                    section.setReadOnly(true);
                }
            }
        }

        return sections;
    }

    /**
     * Populate the TemProfile details
     *
     * @param profile
     */
    protected void populateInfo(TemProfile profile) {
        SpringContext.getBean(TravelerService.class).populateTemProfile(profile);
        SpringContext.getBean(TemProfileService.class).updateACHAccountInfo(profile);
    }

    protected void maskAccountNumbers(TemProfile profile) {
        for (TemProfileAccount account : profile.getAccounts()){
            String accountSubStr = account.getAccountNumber().substring(account.getAccountNumber().length()-4);
            account.setAccountNumber("************"+accountSubStr);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        populateInfo((TemProfile)document.getOldMaintainableObject().getBusinessObject());
        populateInfo((TemProfile)document.getNewMaintainableObject().getBusinessObject());
        super.processAfterEdit(document, parameters);
    }

    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);

        if (documentHeader.getWorkflowDocument().isProcessed()){
            TemProfile temProfile = (TemProfile) super.getBusinessObject();
            if(NONEMP_TRAVELER_TYP_CD.equals(temProfile.getTravelerTypeCode())) {
                updateCustomerPrimaryAddress(temProfile);

            }
        }
    }

    protected void updateCustomerPrimaryAddress(TemProfile temProfile) {
      AccountsReceivableCustomer customer = temProfile.getCustomer();
       for(AccountsReceivableCustomerAddress customerAddress : customer.getAccountsReceivableCustomerAddresses()) {
           if(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY.equals(customerAddress.getAccountsReceivableCustomerAddressType().getCustomerAddressTypeCode())) {
               customerAddress.setCustomerLine1StreetAddress(temProfile.getTemProfileAddress().getStreetAddressLine1());
               customerAddress.setCustomerLine2StreetAddress(temProfile.getTemProfileAddress().getStreetAddressLine2());
               customerAddress.setCustomerCityName(temProfile.getTemProfileAddress().getCityName());
               customerAddress.setCustomerStateCode(temProfile.getTemProfileAddress().getStateCode());
               customerAddress.setCustomerZipCode(temProfile.getTemProfileAddress().getZipCode());
               customerAddress.setCustomerCountryCode(temProfile.getTemProfileAddress().getCountryCode());
               customerAddress.setCustomerEmailAddress(temProfile.getEmailAddress());
               getAccountsReceivableModuleService().saveCustomer(temProfile.getCustomer());
           }
       }
    }

    /**
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.TAX_MANAGER_REQUIRED)) {
            return taxManagerRequiredRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_TRAVELER_REVIEW)) {
            return travelerRequiredRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_PROFILE_REVIEW)) {
            // routes to profile review under the same circumstance as traveler.
            return travelerRequiredRouting();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * Overriding to return - TemProfile doesn't need this
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#populateChartOfAccountsCodeFields()
     */
    @Override
    protected void populateChartOfAccountsCodeFields() {
    }

    /**
     *
     * This method returns true if:<br/>
     * When editing a profile
     * 1. The non-resident alien property has changed<br/>
     * 2. The citizenship property has changed<br/>
     * When creating a new profile
     * 3. The non-resident alien property is set<br/>
     * 4. The citizenship is not US or blank<br/>
     * @return
     */
    protected boolean taxManagerRequiredRouting() {
        TemProfile newTemProfile = (TemProfile) getParentMaintDoc().getNewMaintainableObject().getBusinessObject();
        TemProfile oldTemProfile = (TemProfile) getParentMaintDoc().getOldMaintainableObject().getBusinessObject();

        //edit profile
        if (ObjectUtils.isNotNull(oldTemProfile.getProfileId())) {
            // If NRA changed, route to tax manager
            if (!newTemProfile.getNonResidentAlien().equals(oldTemProfile.getNonResidentAlien())) {
                return true;
            }

            // If citizenship changed, route to tax manager
            if (!StringUtils.equalsIgnoreCase(newTemProfile.getCitizenship(), oldTemProfile.getCitizenship())) {
                return true;
            }
        }
        //new profile
        else {

            // If NRA is selected, route to tax manager
            if (newTemProfile.getNonResidentAlien()) {
                return true;
            }

            //if citizenship is something other than US or blank, route to tax manager
            if (!StringUtils.isBlank(newTemProfile.getCitizenship()) && !newTemProfile.getCitizenship().equals(KFSConstants.COUNTRY_CODE_UNITED_STATES)) {
                return true;
            }

        }

        return false;
    }

    protected boolean travelerRequiredRouting() {
        TemProfile newTemProfile = (TemProfile) getParentMaintDoc().getNewMaintainableObject().getBusinessObject();
        String initiator = getParentMaintDoc().getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        if (newTemProfile.getPrincipalId() != null &&!newTemProfile.getPrincipalId().equals(initiator)) {
            return true;
        }
        return false;
    }

    protected FinancialSystemMaintenanceDocument getParentMaintDoc() {
        FinancialSystemMaintenanceDocument maintDoc = null;
        try {
            maintDoc = (FinancialSystemMaintenanceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(getDocumentNumber());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        return maintDoc;
    }

    @Override
    protected void refreshReferences(String referencesToRefresh) {
        //make call to super
        super.refreshReferences( removeReferenceFromString(referencesToRefresh, "temProfileAddress") );
    }

    /**
     * Removes a named reference from a referencesToRefresh string
     */
    protected String removeReferenceFromString(String referencesToRefresh, String referenceToRemove){
        String newReference = referencesToRefresh;

        if(ObjectUtils.isNotNull(newReference)){
            int index = newReference.indexOf(referenceToRemove);
            if(index != -1){
                //remove from beginning
                if(index == 0){

                    String suffix = "";
                    //add comma at end since there is more after this word
                    if(newReference.length() != referenceToRemove.length()){
                        suffix = ",";
                    }
                    newReference = referencesToRefresh.replaceAll("temProfileAddress" + suffix, "");

                }else{
                    //removing from middle to end... either way, comma will be in front
                    newReference = referencesToRefresh.replaceAll("," + "temProfileAddress", "");
                }
            }
        }

        return newReference;
    }

    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
    }


	/**
	 *
	 * This method trims the descriptionText to 40 characters.
	 * @param descriptionText
	 * @return
	 */
	protected String trimDescription(String descriptionText) {

        if (descriptionText.length() > 40) {
            descriptionText = descriptionText.substring(0, 39);
        }

        return descriptionText;
	}

	/**
	 * Reference getDocumentService.createNoteFromDocument
	 *
	 * This method creates a note on the maintenance doc indicating that a AR Customer record has been generated.
	 * @param temProfile
	 * @return
	 */
	protected Note addCustomerCreatedNote(TemProfile temProfile) {
	    String text = "AR Customer ID " + temProfile.getCustomer().getCustomerNumber() + " has been generated";
        Note note = new Note();

        note.setNotePostedTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        note.setVersionNumber(Long.valueOf(1));
        note.setNoteText(text);
        note.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());

        Person kualiUser = GlobalVariables.getUserSession().getPerson();
        note = getNoteService().createNote(note, temProfile, kualiUser.getPrincipalId());
        return note;
	}


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getNewCollectionLine(java.lang.String)
     */
    @Override
    public PersistableBusinessObject getNewCollectionLine( String collectionName ) {
        PersistableBusinessObject addLine = super.getNewCollectionLine(collectionName);
        if (collectionName.equals("accounts")){
            TemProfileAccount account = (TemProfileAccount) addLine;
            TemProfile temProfile = (TemProfile) super.getBusinessObject();
            account.setProfile(temProfile);
            return account;
        }

        return addLine;
    }

    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {

        return SpringContext.getBean(AccountsReceivableModuleService.class);
    }


    public TemRoleService getTemRoleService(){
        return SpringContext.getBean(TemRoleService.class);
    }

    public TravelerService getTravelerService(){
        return SpringContext.getBean(TravelerService.class);
    }

    public DocumentService getDocumentService(){
        return SpringContext.getBean(DocumentService.class);
    }

    public NoteService getNoteService(){
        return KRADServiceLocator.getNoteService();
    }

}
