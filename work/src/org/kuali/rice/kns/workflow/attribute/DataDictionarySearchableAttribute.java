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
package org.kuali.rice.kns.workflow.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.WorkflowAttributePropertyResolutionService;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.DocumentSearchField;
import org.kuali.rice.kew.docsearch.DocumentSearchRow;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DataDictionaryEntry;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.SearchingAttribute;
import org.kuali.rice.kns.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.kns.datadictionary.WorkflowAttributes;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.web.ui.Field;

/**
 * This class...
 */
public class DataDictionarySearchableAttribute implements SearchableAttribute {

    private static final Logger LOG = Logger.getLogger(DataDictionarySearchableAttribute.class);

    
    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchContent(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public String getSearchContent(DocumentSearchContext documentSearchContext) {

        return "";
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchStorageValues(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        
        String docId = documentSearchContext.getDocumentId();
        DocumentEntry docEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentSearchContext.getDocumentTypeName());

        DocumentService docService = SpringContext.getBean(DocumentService.class);
        Document doc = null;
        try  {
            doc = docService.getByDocumentHeaderIdSessionless(docId);
        } catch (WorkflowException we) {
            
        }
        WorkflowAttributes workflowAttributes = docEntry.getWorkflowAttributes();
        WorkflowAttributePropertyResolutionService waprs = SpringContext.getBean(WorkflowAttributePropertyResolutionService.class);
        return waprs.resolveSearchableAttributeValues(doc, workflowAttributes);
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchingRows(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<DocumentSearchRow> getSearchingRows(DocumentSearchContext documentSearchContext) {
        
       
       DocumentEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentSearchContext.getDocumentTypeName());
       WorkflowAttributes workflowAttributes = entry.getWorkflowAttributes();

       List<DocumentSearchRow> retvals = createFieldRowsForWorkflowAttributes(workflowAttributes);
        
        return retvals;
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#validateUserSearchInputs(java.util.Map, org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, String> paramMap, DocumentSearchContext searchContext) {

        return null;
    }

    /**
     * Creates a list of search fields, one for each primary key of the maintained business object 
     * @param businessObjectClass the class of the maintained business object
     * @return a List of KEW search fields
     */
    protected List<DocumentSearchRow> createFieldRowsForWorkflowAttributes(WorkflowAttributes attrs) {
        List<DocumentSearchRow> searchFields = new ArrayList<DocumentSearchRow>();
        
      List<SearchingTypeDefinition> searchingTypeDefinitions = attrs.getSearchingTypeDefinitions();
      for (SearchingTypeDefinition definition: searchingTypeDefinitions) {
          SearchingAttribute attr = definition.getSearchingAttribute();
          String attributeName = attr.getAttributeName();
          String businessObjectClassName = attr.getBusinessObjectClassName();
          final DataDictionaryEntry boEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDictionaryObjectEntry(businessObjectClassName);
          DocumentSearchField searchField = buildSearchField(attributeName, boEntry);
              List<Field> fieldList = new ArrayList<Field>();
              fieldList.add(searchField);
              if (searchField.isUsingDatePicker()) {
                  fieldList.add(buildDatePickerField(attributeName));
              }
              String quickfinderService = searchField.getQuickFinderClassNameImpl();
//              if (!Utilities.isEmpty(quickfinderService)) {
//                  fieldList.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, quickfinderService));
//              }
              searchFields.add(new DocumentSearchRow(fieldList));
              
      }
        
        return searchFields;
    }
    
    /**
     * Builds a search field for the given property
     * @param propertyName the name of the property to search on
     * @param businessObjectEntry the business object data dictionary for the maintained business object
     * @return a search field for the given property
     */
    protected DocumentSearchField buildSearchField(String propertyName, DataDictionaryEntry dataDictionaryEntry) {
        AttributeDefinition propertyAttribute = getAttributeFromEntry(propertyName, dataDictionaryEntry);
        DocumentSearchField field = buildFieldFromEntry(propertyAttribute);

        // 4. quickfinder?
        
     // TODO: ewestfal - 12-23-2008
        // commenting out the below for now until the new document search code is in place and we've
        // implemented support for rendering links to url-based lookups in KFS
        // also commented out getQuickfinderServiceName and getWorkflowLookupServiceName
        
//        final String quickfinderServiceName = getQuickfinderServiceName(businessObjectEntry.getBusinessObjectClass(), propertyName);
//        
//        if (quickfinderServiceName != null) {
//            field.setQuickFinderClassNameImpl(quickfinderServiceName);
//            field.setHasLookupable(true);
//            field.setDefaultLookupableName(propertyName);
//        }
        return field;
    }
    

    
    /**
     * Builds a date picker to go with a field
     * @param propertyName the name of the property which is going to have a date picker associated with it
     * @return a date picker field
     */
    protected DocumentSearchField buildDatePickerField(String propertyName) {
        return new DocumentSearchField("", "", DocumentSearchField.DATEPICKER, propertyName + "_datepicker", "", null, "");
    }
    
    /**
     * Retrieves the attribute definition from the business object data dictionary entry
     * @param propertyName the property to find the attribute definition for
     * @param businessObjectEntry the business object data dictionary entry
     * @return the found AttributeDefinition
     */
    protected AttributeDefinition getAttributeFromEntry(String propertyName, DataDictionaryEntry dataDictionaryEntry) {
        return dataDictionaryEntry.getAttributeDefinition(propertyName);
    }
    
    /**
     * Builds the field based on the attribute entry
     * @param attributeDefinition the definition of the attribute
     * @return a KEW field based on the attribute definition
     */
    protected DocumentSearchField buildFieldFromEntry(AttributeDefinition attributeDefinition) {
        DocumentSearchField field = new DocumentSearchField(
                attributeDefinition.getLabel(), // label
                "", // help url
                determineKEWFieldType(attributeDefinition), // field type
                attributeDefinition.getName(), // property name
                "", // property value
                getValidValues(attributeDefinition), // field valid values
                "" // quick finder class name
                );
        calibrateField(field, attributeDefinition);
        return field;
    }
   
    /**
     * Determines what KEW field type the given attribute definition should be converted to
     * @param attributeDefinition the attribute definition, which hopefully has a KNS field type
     * @return the name of the KEW field type or null if something is drastically haywire
     */
    protected String determineKEWFieldType(AttributeDefinition attributeDefinition) {
        if (attributeDefinition.getControl().isDatePicker()) return Field.TEXT;
        if (attributeDefinition.getControl().isCheckbox()) return Field.CHECKBOX;
        if (attributeDefinition.getControl().isHidden()) return Field.HIDDEN; // this is an absurd case...
        if (attributeDefinition.getControl().isRadio()) return Field.RADIO;
        if (attributeDefinition.getControl().isSelect()) return Field.DROPDOWN;
        if (attributeDefinition.getControl().isText()) return Field.TEXT;
        if (attributeDefinition.getControl().isTextarea()) return Field.TEXT;
        if (attributeDefinition.getControl().isCurrency()) return Field.TEXT;
        return "";
    }
    
    /**
     * Retrieves a list of key/value pairs if the attribute definition describes a field wth a constrained values set;
     * returns null otherwise
     * @param attributeDefinition the definition of the attribute which may or may not have a constrained value set
     * @return a list of KEW key/label pairs or null if there shouldn't be a constrained value set on this field
     */
    protected List getValidValues(AttributeDefinition attributeDefinition) {
        if (!attributeDefinition.getControl().isSelect() && !attributeDefinition.getControl().isRadio()) return null;
        
        Class<? extends KeyValuesFinder> valuesFinderClass = attributeDefinition.getControl().getValuesFinderClass();
        if (valuesFinderClass == null) return null;
            
        try {
            KeyValuesFinder valuesFinder = valuesFinderClass.newInstance();
            return convertToKEWKeyLabelPairs(valuesFinder.getKeyValues());
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Could not instantiate values finder class: "+valuesFinderClass.getName(), ie);
        }
        catch (IllegalAccessException iae) {
            throw new RuntimeException("Illegal access exception while instantiating values finder class: "+valuesFinderClass.getName(), iae);
        }
    }

    /**
     * "Calibrates" the field so that it isn't some stupid field, like a drop down with no values associated
     * @param field the KEW field to calibrate
     */
    protected void calibrateField(DocumentSearchField field, AttributeDefinition attributeDefinition) {
        if (field.getFieldType() != null) {
            if ((field.getFieldType().equals(Field.RADIO) || field.getFieldType().equals(Field.DROPDOWN)) && field.getFieldValidValues() == null) {
                // oops! see, the problem's here: you've got a raccon in your rotary gasket
                field.setFieldType(Field.TEXT);
            }
            if (field.getFieldType().equals(Field.TEXT)) {
                // ah, you're text!  let's make sure you're a bit flexible about your input!
                field.setAllowWildcards(true);
                field.setCaseSensitive(true);
            }
            
            if (attributeDefinition.getControl().isDatePicker()) {
                field.setHasDatePicker(true);
            }
        }
    }

    /**
     * Converts a List of KNS KeyLabelPair objects into practically identical KEW KeyLabelPair objects
     * @param keyLabelPairs the list of KNS key label pairs
     * @return a list of KEW key label pairs
     */
    protected List convertToKEWKeyLabelPairs(List keyLabelPairs) {
        List kewKeyLabelPairs = new ArrayList();
        for (Object knsKeyLabelPairAsObject : keyLabelPairs) {
            org.kuali.rice.kns.web.ui.KeyLabelPair knsKeyLabelPair = (org.kuali.rice.kns.web.ui.KeyLabelPair)knsKeyLabelPairAsObject;
            kewKeyLabelPairs.add(new org.kuali.rice.kew.util.KeyLabelPair(knsKeyLabelPair.getKey(), knsKeyLabelPair.getLabel()));
        }
        return kewKeyLabelPairs;
    }

    /**
     * Attemps to find a related class for the given property; if it can find one, it goes on to determine what the appropriate workflowLookup service would be to look that business object up
     * @param businessObjectClass the class of the business object that is being maintained
     * @param propertyName the property to find a lookup for
     * @return the name of the workflowLookup service if such a service exists; otherwise, null
     */
//    protected String getQuickfinderServiceName(Class<? extends BusinessObject> businessObjectClass, String propertyName) {
//        try {
//            final BusinessObject pointlessBOInstanceToMakeTheServiceHappy = (BusinessObject)businessObjectClass.newInstance();
//            final BusinessObjectRelationship relationship = SpringContext.getBean(BusinessObjectMetaDataService.class).getBusinessObjectRelationship(pointlessBOInstanceToMakeTheServiceHappy, propertyName);
//            if (relationship != null) {
//                final Class lookingUpClass = relationship.getRelatedClass();
//                String retVal = getWorkflowLookupServiceName(lookingUpClass);
//                return retVal;
//            } else {
//                return null;
//            }
//        }
//        catch (InstantiationException ie) {
//            throw new RuntimeException("Could not instantiate class: "+businessObjectClass.getName()+" to determine search attributes lookup helper", ie);
//        }
//        catch (IllegalAccessException iae) {
//            throw new RuntimeException("IllegalAccessException while trying to instantiate class: "+businessObjectClass.getName()+" to determine search attributes lookup helper", iae);
//        }
//    }
    
    /**
     * Returns the name of the workflow lookupable service for the given business object if one exists; otherwise
     * returns null
     * @param businessObjectClass the business object class to try to find a workflow lookup for
     * @return null if no lookup service name could be found; the name of the service if found
     */
//    protected String getWorkflowLookupServiceName(Class businessObjectClass) {
//        final Map<String, ? extends org.kuali.rice.kns.workflow.attribute.WorkflowLookupableImpl> workflowLookups = SpringContext.getBeansOfType(org.kuali.rice.kns.workflow.attribute.WorkflowLookupableImpl.class);
//        final Set<String> workflowLookupServiceNames = workflowLookups.keySet();
//        final String proposedName = convertBusinessObjectClassToProposedWorklowLookupServiceName(businessObjectClass);
//        return workflowLookupServiceNames.contains(proposedName) ? proposedName : null; 
//    }

    /**
     * Given a business object class, determines what the name of the workflow lookupable to lookup this class would be
     * @param businessObjectClass the business object class to propose a workflow lookupable service name for
     * @return the proposed name of the workflow lookupable service
     */
    protected String convertBusinessObjectClassToProposedWorklowLookupServiceName(Class<? extends BusinessObject> businessObjectClass) {
       return "workflow-"+businessObjectClass.getName()+"-Lookupable"; 
    }

    
}
