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
package org.kuali.kfs.sys.document.service;

import java.util.List;

import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kns.datadictionary.RoutingTypeDefinition;
import org.kuali.rice.kns.datadictionary.WorkflowAttributes;
import org.kuali.rice.kns.document.Document;

/**
 * A service which will resolve workflow attributes into the proper data for routing qualifier resolution 
 */
public interface WorkflowAttributePropertyResolutionService {

    /** 
     * Generates a List of AttributeSet data from the data on the document for the given WorkflowAttributeDefinitions
     * @param document the document to gather data from
     * @param workflowAttributes the workflow attributes which have the routing type qualifiers to resolve
     * @return a List of populated AttributeSet data
     */
    public abstract List<AttributeSet> resolveRoutingTypeQualifiers(Document document, RoutingTypeDefinition routingTypeDefinition);
    
    /**
     * Given a document, returns the searchable attribute values to index for it
     * @param document the document to find indexable searchable attribute values for
     * @param workflowAttributes the WorkflowAttributes data dictionary metadata which contains the searchable attributes to index 
     * @return a List of SearchableAttributeValue objects for index
     */
    public abstract List<SearchableAttributeValue> resolveSearchableAttributeValues(Document document, WorkflowAttributes workflowAttributes);
}
