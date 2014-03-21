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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TemProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileArranger;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromCustomer;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromKimPerson;
import org.kuali.kfs.module.tem.businessobject.datadictionary.TravelDetailLookupMappedFieldProxy;
import org.kuali.kfs.module.tem.document.authorization.TemProfileAuthorizer;
import org.kuali.kfs.module.tem.document.service.TravelArrangerDocumentService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TemRoleService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.framework.persistence.ojb.conversion.OjbCharBooleanConversion;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class TemProfileLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    public static Logger LOG = Logger.getLogger(TemProfileLookupableHelperServiceImpl.class);

    private TemRoleService temRoleService;
    private TravelerService travelerService;
    private PersonService personService;
    private TemProfileService temProfileService;
    private IdentityManagementService identityManagementService;
    private TravelArrangerDocumentService travelArrangerDocumentService;

    private static final String[] addressLookupFields = { TemProfileProperties.ADDRESS_1, TemProfileProperties.ADDRESS_2, TemProfileProperties.CITY_NAME, TemProfileProperties.STATE_CODE, TemProfileProperties.ZIP_CODE, TemProfileProperties.COUNTRY_CODE };

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {

        boolean arrangeesOnly = false;
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        // split homeDepartment field value into org code and coa code for TemProfile lookup
        if (fieldValues != null) {
            String homeDepartment = fieldValues.get(TemConstants.TEM_PROFILE_HOME_DEPARTMENT);
            if (homeDepartment != null) {
                String[] primaryDepartmentCode = homeDepartment.split("-");
                if (primaryDepartmentCode != null) {
                    if (primaryDepartmentCode.length == 2) {
                        fieldValues.put(TemConstants.TEM_PROFILE_HOME_DEPT_ORG_CODE, primaryDepartmentCode[1]);
                    }

                    fieldValues.put(TemConstants.TEM_PROFILE_HOME_DEPT_COA_CODE, primaryDepartmentCode[0]);
                    fieldValues.remove(TemConstants.TEM_PROFILE_HOME_DEPARTMENT);
                }
            }

            if (fieldValues.containsKey(TemPropertyConstants.TemProfileProperties.ONLY_ARRANGEES_IN_LOOKUP)) {
                final String arrangeesOnlyValue = fieldValues.remove(TemPropertyConstants.TemProfileProperties.ONLY_ARRANGEES_IN_LOOKUP);
                if (!StringUtils.isBlank(arrangeesOnlyValue)) {
                    OjbCharBooleanConversion booleanConverter = new OjbCharBooleanConversion();
                    final Boolean arrangeesOnlyBool = (Boolean)booleanConverter.sqlToJava(arrangeesOnlyValue);
                    arrangeesOnly = arrangeesOnlyBool.booleanValue();
                }
            }
        }

        List<TemProfile> searchResults = (List<TemProfile>) super.getSearchResults(fieldValues);
        List<TemProfile> profiles = new ArrayList<TemProfile>();
        for (TemProfile profile : searchResults){
            //only repopulate kim based tem profiles
            if (!StringUtils.isBlank(profile.getPrincipalId())) {
                getTravelerService().populateTemProfile(profile);
            }
            if (!arrangeesOnly || isArranger(currentUser, profile)) {
                profiles.add(profile);
            }
        }

        // Need to also search kim. This is necessary because data could be different between kim and the tem profile,
        // and the kim data is the data that should be used.

        // First get a list of kim fields to search on.
        Map<String, String> kimLookupFields = convertFieldValues(Person.class, fieldValues, "", TemProfile.class.getName());

        // Get a list of people matching the fields kim fields.
        if (kimLookupNeeded(fieldValues)) {
            List<? extends Person> people = getPersonService().findPeople(kimLookupFields);
            for (Person person : people) {

                // See if the person has a tem profile.
                TemProfile profileFromKim = getTemProfileService().findTemProfileByPrincipalId(person.getPrincipalId());
                if (ObjectUtils.isNotNull(profileFromKim)) {

                    // Found the tem profile for this person, see if the tem profile search returned their profile.
                    getTravelerService().populateTemProfile(profileFromKim);
                    Boolean isFound = false;
                    for (TemProfile profile : profiles) {
                        if (profile.getProfileId() == profileFromKim.getProfileId()) {
                            isFound = true;
                            break;
                        }
                    }
                }
            }
        }

        return profiles;
    }

    /**
     * Determines if the given possibleArranger is an arranger for the given possibleArrangee
     * @param possibleArranger the Person who could be the arranger of the given profile
     * @param possibleArrangee the profile, who could have trips arranged by the possibleArranger
     * @return true if possibleArranger is an arranger for possibleArrangee, false otherwise
     */
    protected boolean isArranger(Person possibleArranger, TemProfile possibleArrangee) {
        if (possibleArranger == null) {
            // there's no current user running this lookup?  Weird.
            return false;
        }
        if (possibleArrangee == null) {
            // there's no profile but we're adding it to a list?  even weirder
            return false;
        }
        final TemProfileArranger temProfileArranger = getTravelArrangerDocumentService().findTemProfileArranger(possibleArranger.getPrincipalId(), possibleArrangee.getProfileId());
        final boolean isOrganizationalApprover = getTravelerService().isArrangeeByOrganization(possibleArranger.getPrincipalId(), possibleArrangee);
        return temProfileArranger != null || isOrganizationalApprover;
    }

    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (allowsMaintenanceEditAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        }
        if (allowsMaintenanceDeleteAction(businessObject)) {
            htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_DELETE_METHOD_TO_CALL, pkNames));
        }
        return htmlDataList;
    }


    /**
     * This method searches through fieldValues for a not null value. Returns true if a not null value is found. Use this to
     * determine if a kim lookup is necessary or not.
     *
     * @param fieldValues
     * @return
     */
    private boolean kimLookupNeeded(Map<String, String> fieldValues) {
        for (String key : addressLookupFields) {
            if (fieldValues.get(key) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getReturnHref(java.util.Properties,
     *      org.kuali.rice.kns.web.struts.form.LookupForm, java.util.List)
     */
    @SuppressWarnings("rawtypes")
    @Override
    protected String getReturnHref(Properties parameters, LookupForm lookupForm, List returnKeys) {
        String url = super.getReturnHref(parameters, lookupForm, returnKeys);
        url = url.replaceAll(TemPropertyConstants.TemProfileProperties.PROFILE_ID, "document." + TemPropertyConstants.TemProfileProperties.PROFILE_ID);
        return url;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSupplementalMenuBar()
     */
    @Override
    public String getSupplementalMenuBar() {

        // we are coming from a document; bail.
        if (parameters.containsKey("docNum")) {
            return "";
        }

        // next we need to get the current user's info
        UserSession user = GlobalVariables.getUserSession();

        final TemProfileAuthorizer authorizer = (TemProfileAuthorizer)SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(TemConstants.TravelDocTypes.TRAVEL_PROFILE_DOCUMENT);

        final TemProfile dummyProfile = new TemProfile();
        // fill the dummy profile with the user's org, so that org qualified roles will have qualifiers
        final ChartOrgHolder chartOrg = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(user.getPerson(), TemConstants.NAMESPACE);
        dummyProfile.setHomeDeptChartOfAccountsCode(chartOrg.getChartOfAccountsCode());
        dummyProfile.setHomeDeptOrgCode(chartOrg.getOrganizationCode());

        boolean canCreateNewProfile = authorizer.canCreateAnyProfile(dummyProfile, user.getPerson());
        boolean canCreateMyProfile = false;

        Map<String, String> criteria = new HashMap<String, String>(2);
        criteria.put("principalId", user.getPrincipalId());

        // If a TEM Profile doesn't exist and user is granted KFS-TEM Edit My TEM Profile permission, allow the user to create their profile
        if (getTemProfileService().findTemProfile(criteria) == null ) {
            canCreateMyProfile = authorizer.canEditOwnProfile(dummyProfile, user.getPerson());
        }

        String url = "<div class=\"createnew\" title=\"Create TEM Profile\">";

        String imageBaseUrl = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

        Properties myProfileParameters = new Properties();
        myProfileParameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEW_METHOD_TO_CALL);
        myProfileParameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TemProfile.class.getName());
        myProfileParameters.put(KFSConstants.OVERRIDE_KEYS, "principalId");
        myProfileParameters.put(KFSConstants.REFRESH_CALLER, "principalId" + "::" + user.getPrincipalId());
        myProfileParameters.put("principalId", user.getPrincipalId());
        String createMineUrl = UrlFactory.parameterizeUrl(KFSConstants.MAINTENANCE_ACTION, myProfileParameters);

        if (canCreateMyProfile) {
            // add in the create mine button
            url += "<a href=\"" + createMineUrl + "\"><img src=\"" + imageBaseUrl + "tinybutton-createmine.gif\" alt=\"refresh\"></a>&nbsp;";
        }

        if (canCreateNewProfile) {
            Properties parameters = new Properties();
            parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TemProfileFromKimPerson.class.getName());
            parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

            String personUrl = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, parameters);

            url += "<a href=\"" + personUrl + "\"><img src=\"" + imageBaseUrl + "tinybutton-createnewfromkim.gif\" alt=\"refresh\"></a>";

            // create new from customer
            Properties custParameters = new Properties();
            custParameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TemProfileFromCustomer.class.getName());
            custParameters.put(KFSConstants.DOC_FORM_KEY, "88888888");
            custParameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

            String custUrl = UrlFactory.parameterizeUrl(KRADConstants.LOOKUP_ACTION, custParameters);
            url = url + "&nbsp;<a href=\"" + custUrl + "\"><img src=\"" + imageBaseUrl + "tinybutton-createnewfromcustomer.gif\" alt=\"refresh\"></a>";
        }
        url += "</div>";

        return url;
    }

    /**
     *
     * @param boClass
     * @param fieldValues
     * @param prefix
     * @param lookupClassName
     * @return
     */
    private Map<String, String> convertFieldValues(Class<? extends BusinessObject> boClass, Map<String, String> fieldValues, String prefix, String lookupClassName) {
        Map<String, String> retval = new HashMap<String, String>();

        LOG.debug("Converting field values " + fieldValues);

        for (final FieldDefinition lookupField : getLookupFieldsFor(lookupClassName)) {
            String attrName = lookupField.getAttributeName();

            if (lookupField instanceof TravelDetailLookupMappedFieldProxy) {
                final TravelDetailLookupMappedFieldProxy mappedField = (TravelDetailLookupMappedFieldProxy) lookupField;
                final String key = mappedField.getAttributeMap().get(boClass.getSimpleName());
                String value = fieldValues.get(attrName);

                if (retval.containsKey(key)) {
                    value = retval.get(key) + value;
                }
                if (key != null) {
                    retval.put(prefix + key, value);
                }
                else {
                    LOG.warn("Got a null key for attribute name " + attrName);
                }
            }
            else if (containsAttribute(boClass, attrName)) {
                retval.put(prefix + attrName, fieldValues.get(attrName));
            }
        }

        return retval;
    }

    /**
     *
     * @param boClass
     * @param attributeName
     * @return
     */
    @SuppressWarnings("rawtypes")
    protected boolean containsAttribute(final Class boClass, final String attributeName) {
        return getDataDictionaryService().isAttributeDefined(boClass, attributeName);
    }

    /**
     *
     * @param className
     * @return
     */
    private Collection<FieldDefinition> getLookupFieldsFor(String className) {
        BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(className);
        return businessObjectEntry.getLookupDefinition().getLookupFields();
    }

    public TemRoleService getTemRoleService() {
        return temRoleService;
    }

    public void setTemRoleService(TemRoleService temRoleService) {
        this.temRoleService = temRoleService;
    }

    /**
     * Gets the travelerService attribute.
     *
     * @return Returns the travelerService.
     */
    public TravelerService getTravelerService() {
        return travelerService;
    }

    /**
     * Sets the travelerService attribute value.
     *
     * @param travelerService The travelerService to set.
     */
    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }


    /**
     * Sets the personService attribute value.
     *
     * @param personService The personService to set.
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * Gets the personService attribute.
     *
     * @return Returns the personService.
     */
    public PersonService getPersonService() {
        return personService;
    }

    /**
     * Gets the temProfileService attribute.
     *
     * @return Returns the temProfileService.
     */
    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    /**
     * Sets the temProfileService attribute value.
     *
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

    public IdentityManagementService getIdentityManagementService() {
        return identityManagementService;
    }

    public void setIdentityManagementService(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
    }

    public TravelArrangerDocumentService getTravelArrangerDocumentService() {
        return travelArrangerDocumentService;
    }

    public void setTravelArrangerDocumentService(TravelArrangerDocumentService travelArrangerDocumentService) {
        this.travelArrangerDocumentService = travelArrangerDocumentService;
    }

}
