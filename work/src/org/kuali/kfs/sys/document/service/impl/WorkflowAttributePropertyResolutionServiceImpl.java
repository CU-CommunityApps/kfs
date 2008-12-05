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
package org.kuali.kfs.sys.document.service.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.sys.document.service.WorkflowAttributePropertyResolutionService;
import org.kuali.rice.kew.docsearch.SearchableAttributeDateTimeValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeFloatValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeLongValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeStringValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.DocumentCollectionPath;
import org.kuali.rice.kns.datadictionary.DocumentValuePathGroup;
import org.kuali.rice.kns.datadictionary.RoutingTypeDefinition;
import org.kuali.rice.kns.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.kns.datadictionary.WorkflowAttributes;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * The default implementation of the WorkflowAttributePropertyResolutionServiceImpl
 */
public class WorkflowAttributePropertyResolutionServiceImpl implements WorkflowAttributePropertyResolutionService {

    /**
     * Using the proper RoutingTypeDefinition for the current routing node of the document, aardvarks out the proper routing type qualifiers
     * @see org.kuali.kfs.sys.document.service.WorkflowAttributePropertyResolutionService#resolveRoutingTypeQualifiers(Document, RoutingTypeDefinition)
     */
    public List<AttributeSet> resolveRoutingTypeQualifiers(Document document, RoutingTypeDefinition routingTypeDefinition) {
        List<AttributeSet> qualifiers = new ArrayList<AttributeSet>();
        
        if (routingTypeDefinition != null) {
            for (DocumentValuePathGroup documentValuePathGroup : routingTypeDefinition.getDocumentValuePathGroups()) {
                qualifiers.addAll(resolveDocumentValuePath(document, documentValuePathGroup));
            }
        }
        return qualifiers;
    }
    
    /**
     * Resolves all of the values in the given DocumentValuePathGroup from the given BusinessObject
     * @param businessObject the business object which is the source of values
     * @param group the DocumentValuePathGroup which tells us which values we want
     * @return a List of AttributeSets
     */
    protected List<AttributeSet> resolveDocumentValuePath(BusinessObject businessObject, DocumentValuePathGroup group) {
        List<AttributeSet> qualifiers;
        AttributeSet qualifier = new AttributeSet();
        addPathValuesToQualifier(businessObject, group.getDocumentValues(), qualifier);
        if (group.getDocumentCollectionPath() != null) {
            qualifiers = resolveDocumentCollectionPath(businessObject, group.getDocumentCollectionPath());
            for (AttributeSet collectionElementQualifier : qualifiers) {
                copyQualifications(qualifier, collectionElementQualifier);
            }
        } else {
            qualifiers = new ArrayList<AttributeSet>();
            qualifiers.add(qualifier);
        }
        return qualifiers;
    }
    
    /**
     * Resolves document values from a collection path on a given business object
     * @param businessObject the business object which has a collection, each element of which is a source of values
     * @param collectionPath the information about what values to pull from each element of the collection
     * @return a List of AttributeSets
     */
    protected List<AttributeSet> resolveDocumentCollectionPath(BusinessObject businessObject, DocumentCollectionPath collectionPath) {
        List<AttributeSet> qualifiers = new ArrayList<AttributeSet>();
        final Collection collectionByPath = getCollectionByPath(businessObject, collectionPath.getCollectionPath());
        if (!ObjectUtils.isNull(collectionPath)) {
            if (collectionPath.getNestedCollection() != null) {
                // we need to go through the collection...
                for (Object collectionElement : collectionByPath) {
                    AttributeSet qualifier = new AttributeSet();
                    // for each element, we need to get the child qualifiers
                    if (collectionElement instanceof BusinessObject) {
                        List<AttributeSet> childQualifiers = resolveDocumentCollectionPath((BusinessObject)collectionElement, collectionPath.getNestedCollection());
                        for (AttributeSet childQualifier : childQualifiers) {
                            // now we need to get the values for the current element of the collection
                            addPathValuesToQualifier(collectionElement, collectionPath.getDocumentValues(), qualifier);
                            // and move all the child keys to the qualifier
                            copyQualifications(childQualifier, qualifier);
                            qualifiers.add(qualifier);
                        }
                    }
                }
            } else {
                // go through each element in the collection
                for (Object collectionElement : collectionByPath) {
                    AttributeSet qualifier = new AttributeSet();
                    addPathValuesToQualifier(collectionElement, collectionPath.getDocumentValues(), qualifier);
                    qualifiers.add(qualifier);
                }
            }
        }
        return qualifiers;
    }
    
    /**
     * Returns a collection from a path on a business object
     * @param businessObject the business object to get values from
     * @param collectionPath the path to that collection
     * @return hopefully, a collection of objects
     */
    protected Collection getCollectionByPath(BusinessObject businessObject, String collectionPath) {
        if (businessObject instanceof PersistableBusinessObject) {
            ((PersistableBusinessObject)businessObject).refreshReferenceObject(collectionPath);
        }
        return (Collection)ObjectUtils.getPropertyValue(businessObject, collectionPath);
    }
    
    /**
     * Aardvarks values out of a business object and puts them into an AttributeSet, based on a List of paths
     * @param businessObject the business object to get values from
     * @param paths the paths of values to get from the qualifier
     * @param qualifier the qualifier to put values into
     */
    protected void addPathValuesToQualifier(Object businessObject, List<String> paths, AttributeSet qualifier) {
        for (String path : paths) {
            // get the values for the paths of each element of the collection
            final Object value = ObjectUtils.getPropertyValue(businessObject, path);
            if (value != null) {
                qualifier.put(simplifyPath(path), value.toString());
            }
        }
    }
    
    /**
     * Simplifies the path name to the last portion of a possibly nested path
     * @param path a possibly nested property path
     * @return a simplified path
     */
    protected String simplifyPath(String path) {
       final int lastDot = path.lastIndexOf('.');
       if (lastDot >= 0) {
           return path.substring(lastDot + 1);
       }
       return path;
    }
    
    /**
     * Copies all the values from one qualifier to another
     * @param source the source of values
     * @param target the place to write all the values to
     */
    protected void copyQualifications(AttributeSet source, AttributeSet target) {
        for (String key : source.keySet()) {
            target.put(key, source.get(key));
        }
    }

    /**
     * Resolves all of the searching values to index for the given document, returning a list of SearchableAttributeValue implementations
     * @see org.kuali.kfs.sys.document.service.WorkflowAttributePropertyResolutionService#resolveSearchableAttributeValues(org.kuali.rice.kns.document.Document, org.kuali.rice.kns.datadictionary.WorkflowAttributes)
     */
    public List<SearchableAttributeValue> resolveSearchableAttributeValues(Document document, WorkflowAttributes workflowAttributes) {
        List<SearchableAttributeValue> valuesToIndex = new ArrayList<SearchableAttributeValue>();
        for (SearchingTypeDefinition definition : workflowAttributes.getSearchingAttributeDefinitions()) {
            valuesToIndex.addAll(aardvarkValuesForSearchingTypeDefinition(document, definition));
        }
        return valuesToIndex;
    }
    
    /**
     * Pulls SearchableAttributeValue values from the given document for the given searchingTypeDefinition
     * @param document the document to get search values from
     * @param searchingTypeDefinition the current SearchingTypeDefinition to find values for
     * @return a List of SearchableAttributeValue implementations
     */
    protected List<SearchableAttributeValue> aardvarkValuesForSearchingTypeDefinition(Document document, SearchingTypeDefinition searchingTypeDefinition) {
        List<SearchableAttributeValue> searchAttributes = new ArrayList<SearchableAttributeValue>();
        
        final List<Object> searchValues = aardvarkSearchValuesForPaths(document, searchingTypeDefinition.getDocumentValues());
        for (Object value : searchValues) {
            final SearchableAttributeValue searchableAttributeValue = buildSearchableAttribute(searchingTypeDefinition.getSearchingAttribute().getAttributeName(), value);
            if (searchableAttributeValue != null) {
                searchAttributes.add(searchableAttributeValue);
            }
        }
        return searchAttributes;
    }
    
    /**
     * Pulls values as objects from the document for the given paths
     * @param document the document to pull values from
     * @param paths the property paths to pull values
     * @return a List of values as Objects
     */
    protected List<Object> aardvarkSearchValuesForPaths(Document document, List<String> paths) {
        List<Object> searchValues = new ArrayList<Object>();
        for (String path : paths) {
            searchValues.addAll(aardvarkSearchValuesForPath(document, path));
        }
        return searchValues;
    }
    
    /**
     * A recursive method, this grabs values from the given object out of the path
     * @param object an object to grab values from
     * @param path a potentially nested path
     * @return a List of values
     */
    protected List<Object> aardvarkSearchValuesForPath(Object object, String path) {
        List<Object> searchingValues = new ArrayList<Object>();
        
        final String[] splitPath = headAndTailPath(path);
        final String head = splitPath[0];
        final String tail = splitPath[1];
        
        if (object instanceof PersistableBusinessObject) {
            ((PersistableBusinessObject)object).refreshReferenceObject(head);
        }
        final Object headValue = ObjectUtils.getPropertyValue(object, head);
        if (headValue != null) {
            if (tail == null) {
                searchingValues.add(headValue);
            } else {
                // oops!  we've still got path left...
                if (headValue instanceof Collection) {
                    // oh dear, a collection; we've got to loop through this
                    for (Object currentElement : (Collection)headValue) {
                        searchingValues.addAll( aardvarkSearchValuesForPath(currentElement, tail) );
                    }
                } else {
                    searchingValues.addAll( aardvarkSearchValuesForPath(headValue, tail) );
                }
            }
        }
        return searchingValues;
    }
    
    /**
     * Splits the first property off from a path, leaving the tail
     * @param path the path to split
     * @return an array; if the path is nested, the first element will be the first part of the path up to a "." and second element is the rest of the path while if the path is simple, returns the path as the first element and a null as the second element
     */
    protected String[] headAndTailPath(String path) {
        final int firstDot = path.indexOf('.');
        if (firstDot < 0) {
            return new String[] { path, null };
        }
        return new String[] { path.substring(0, firstDot), path.substring(firstDot + 1) };
    }
    
    /**
     * Using the type of the sent in value, determines what kind of SearchableAttributeValue implementation should be passed back 
     * @param attributeKey
     * @param value
     * @return
     */
    protected SearchableAttributeValue buildSearchableAttribute(String attributeKey, Object value) {
        if (value == null) return null;
        if (isDateLike(value)) return buildSearchableDateTimeAttribute(attributeKey, value);
        if (isDecimally(value)) return buildSearchableRealAttribute(attributeKey, value);
        if (isIntable(value)) return buildSearchableFixnumAttribute(attributeKey, value);
        return buildSearchableStringAttribute(attributeKey, value);
    }

    /**
     * Determines if the given value is enough like a date to store it as a SearchableAttributeDateTimeValue
     * @param value the value to determine the type of
     * @return true if it is like a date, false otherwise
     */
    protected boolean isDateLike(Object value) {
        return value instanceof java.util.Date;
    }
    
    /**
     * Determines if the given value is enough like a Float to store it as a SearchableAttributeFloatValue
     * @param value the value to determine of the type of
     * @return true if it is like a "float", false otherwise
     */
    protected boolean isDecimally(Object value) {
        return value instanceof Double || value instanceof Float || value.getClass().equals(Double.TYPE) || value.getClass().equals(Float.TYPE) || value instanceof BigDecimal || value instanceof KualiDecimal;
    }
    
    /**
     * Determines if the given value is enough like a "long" to store it as a SearchableAttributeLongValue
     * @param value the value to determine the type of
     * @return true if it is like a "long", false otherwise
     */
    protected boolean isIntable(Object value) {
        return value instanceof Integer || value instanceof Long || value instanceof Short || value instanceof Byte || value instanceof BigInteger || value.getClass().equals(Integer.TYPE) || value.getClass().equals(Long.TYPE) || value.getClass().equals(Short.TYPE) || value.getClass().equals(Byte.TYPE);
    }
    
    /**
     * Builds a date time SearchableAttributeValue for the given key and value
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to date/time data
     * @return the generated SearchableAttributeDateTimeValue
     */
    protected SearchableAttributeDateTimeValue buildSearchableDateTimeAttribute(String attributeKey, Object value) {
        SearchableAttributeDateTimeValue attribute = new SearchableAttributeDateTimeValue();
        attribute.setSearchableAttributeKey(attributeKey);
        attribute.setSearchableAttributeValue(new Timestamp(((java.util.Date)value).getTime()));
        return attribute;
    }
    
    /**
     * Builds a "float" SearchableAttributeValue for the given key and value
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to "float" data
     * @return the generated SearchableAttributeFloatValue
     */
    protected SearchableAttributeFloatValue buildSearchableRealAttribute(String attributeKey, Object value) {
        SearchableAttributeFloatValue attribute = new SearchableAttributeFloatValue();
        attribute.setSearchableAttributeKey(attributeKey);
        if (value instanceof BigDecimal) {
            attribute.setSearchableAttributeValue((BigDecimal)value);
        } else if (value instanceof KualiDecimal) {
            attribute.setSearchableAttributeValue(new BigDecimal(((KualiDecimal)value).doubleValue()));
        } else {
            attribute.setSearchableAttributeValue(new BigDecimal(((Number)value).doubleValue()));
        }
        return attribute;
    }
    
    /**
     * Builds a "integer" SearchableAttributeValue for the given key and value
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to "integer" type data
     * @return the generated SearchableAttributeLongValue
     */
    protected SearchableAttributeLongValue buildSearchableFixnumAttribute(String attributeKey, Object value) {
        SearchableAttributeLongValue attribute = new SearchableAttributeLongValue();
        attribute.setSearchableAttributeKey(attributeKey);
        attribute.setSearchableAttributeValue(new Long(((Number)value).longValue()));
        return attribute;
    }
    
    /**
     * Our last ditch attempt, this builds a String SearchableAttributeValue for the given key and value
     * @param attributeKey the key for the searchable attribute
     * @param value the value that will be coerced to a String
     * @return the generated SearchableAttributeStringValue
     */
    protected SearchableAttributeStringValue buildSearchableStringAttribute(String attributeKey, Object value) {
        SearchableAttributeStringValue attribute = new SearchableAttributeStringValue();
        attribute.setSearchableAttributeKey(attributeKey);
        attribute.setSearchableAttributeValue(value.toString());
        return attribute;
    }
}
