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
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromCustomer;
import org.kuali.kfs.module.tem.businessobject.TemProfileFromKimPerson;
import org.kuali.kfs.module.tem.datadictionary.MappedDefinition;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;

public class TEMProfileLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    
    public static Logger LOG = Logger.getLogger(TEMProfileLookupableHelperServiceImpl.class);
    
    private TravelerService travelerService;
    private TravelService travelService;
    private PersonService<Person> personService;
    private IdentityManagementService identityManagementService;
    private TemProfileService temProfileService;

    private static final String[] addressLookupFields = { TEMProfileProperties.ADDRESS_1, TEMProfileProperties.ADDRESS_2, TEMProfileProperties.CITY_NAME, TEMProfileProperties.STATE, TEMProfileProperties.ZIP_CODE, TEMProfileProperties.COUNTRY };

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        // first we need to flag the user searching to see if they are a Profile Admin or Arranger
        Person user = GlobalVariables.getUserSession().getPerson();
        String docNum = fieldValues.get("docNum");
        String docType = null;
        if(StringUtils.isNotEmpty(docNum)) {
        	docType = GlobalVariables.getUserSession().getWorkflowDocument(docNum).getDocumentType();
        }
        boolean isProfileAdmin = travelService.checkUserTEMRole(user, TemConstants.TEM_PROFILE_ADMIN);
        boolean isAssignedArranger = travelService.checkUserTEMRole(user, TemConstants.TEM_ASSIGNED_PROFILE_ARRANGER);
        boolean isOrgArranger = travelService.checkUserTEMRole(user, TemConstants.TEM_ORGANIZATION_PROFILE_ARRANGER);
        boolean isRiskManagement = travelService.checkUserTEMRole(user, TemConstants.RISK_MANAGEMENT);

        boolean isArrangerDoc = false;
        if(TemConstants.TravelDocTypes.TRAVEL_ARRANGER_DOCUMENT.equals(docType)) {
            isArrangerDoc = true;
        }
        // split homeDepartment field value into org code and coa code for TEMProfile lookup
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
        }

        List<TEMProfile> searhResults = (List<TEMProfile>) super.getSearchResults(fieldValues);
        List<TEMProfile> profiles = new ArrayList<TEMProfile>();
        for (TEMProfile profile : searhResults){
            if(getTravelerService().canIncludeProfileInSearch(profile, docType, user, isProfileAdmin, isAssignedArranger, isOrgArranger, isArrangerDoc, isRiskManagement)) {
                getTravelerService().populateTEMProfile(profile);
                profiles.add(profile);
            }
        }

        // Need to also search kim. This is necessary because data could be different between kim and the tem profile,
        // and the kim data is the data that should be used.

        // First get a list of kim fields to search on.
        Map<String, String> kimLookupFields = convertFieldValues(Person.class, fieldValues, "", TEMProfile.class.getName());

        // Get a list of people matching the fields kim fields.
        if (kimLookupNeeded(fieldValues)) {
            List<? extends Person> people = getPersonService().findPeople(kimLookupFields);
            for (Person person : people) {

                // See if the person has a tem profile.
                TEMProfile profileFromKim = getTravelService().findTemProfileByPrincipalId(person.getPrincipalId());
                if (ObjectUtils.isNotNull(profileFromKim)) {

                    // Found the tem profile for this person, see if the tem profile search returned their profile.
                    getTravelerService().populateTEMProfile(profileFromKim);
                    Boolean isFound = false;
                    for (TEMProfile profile : profiles) {
                        if (profile.getProfileId() == profileFromKim.getProfileId()) {
                            isFound = true;
                            break;
                        }
                    }

                    // Something is different between the tem profile and the kim data, add the updated profile to the list.
                    // TODO: check this again because this is causing a terminated employee to be included in the tem profile
                    // non-employee lookup. Commenting this out for now.
                    /*
                     * if (!isFound) { profiles.add(profileFromKim); }
                     */
                }
            }
        }

        return profiles;
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
        url = url.replaceAll(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, "document." + TemPropertyConstants.TEMProfileProperties.PROFILE_ID);
        return url;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getSupplementalMenuBar()
     */
    @Override
    public String getSupplementalMenuBar() {

        // next we need to get the current user's info
        UserSession user = GlobalVariables.getUserSession();

        boolean canCreateNewProfile = false;
        boolean canCreateMyProfile = false;
        AttributeSet permissionDetails = new AttributeSet();
        // permissionDetails.put(KimAttributes.NAMESPACE_CODE, "KFS-TEM");
        if (getIdentityManagementService().hasPermission(user.getPrincipalId(), "KFS-TEM", "Create TEM Profile", permissionDetails)) {
            canCreateNewProfile = true;
        }

        Map<String, String> criteria = new HashMap<String, String>(2);
        criteria.put("principalId", user.getPrincipalId());
        criteria.put("active", "true");

        // If an active TEM Profile doesn't exist, allow the user to create their profile
        if (getTemProfileService().findTemProfile(criteria) == null) {
            canCreateMyProfile = true;
        }

        String url = "<div class=\"createnew\" title=\"Create TEM Profile\">";

        String imageBaseUrl = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);

        Properties myProfileParameters = new Properties();
        myProfileParameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.MAINTENANCE_NEWWITHEXISTING_ACTION);
        myProfileParameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TEMProfile.class.getName());
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

            String personUrl = UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, parameters);

            url += "<a href=\"" + personUrl + "\"><img src=\"" + imageBaseUrl + "tinybutton-createnewfromkim.gif\" alt=\"refresh\"></a>";

            // create new from customer
            Properties custParameters = new Properties();
            custParameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, TemProfileFromCustomer.class.getName());
            custParameters.put(KFSConstants.DOC_FORM_KEY, "88888888");
            custParameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

            String custUrl = UrlFactory.parameterizeUrl(KNSConstants.LOOKUP_ACTION, custParameters);
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

            if (lookupField instanceof MappedDefinition) {
                final MappedDefinition mappedField = (MappedDefinition) lookupField;
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
        return getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(className).getLookupDefinition().getLookupFields();
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
    public void setPersonService(PersonService<Person> personService) {
        this.personService = personService;
    }

    /**
     * Gets the personService attribute.
     * 
     * @return Returns the personService.
     */
    public PersonService<Person> getPersonService() {
        return personService;
    }

    /**
     * Gets the travelService attribute.
     * 
     * @return Returns the travelService.
     */
    public TravelService getTravelService() {
        return travelService;
    }

    /**
     * Sets the travelService attribute value.
     * 
     * @param travelService The travelService to set.
     */
    public void setTravelService(TravelService travelService) {
        this.travelService = travelService;
    }


    /**
     * Gets the identityManagementService attribute.
     * 
     * @return Returns the identityManagementService.
     */
    public IdentityManagementService getIdentityManagementService() {
        return identityManagementService;
    }


    /**
     * Sets the identityManagementService attribute value.
     * 
     * @param identityManagementService The identityManagementService to set.
     */
    public void setIdentityManagementService(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
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

}
