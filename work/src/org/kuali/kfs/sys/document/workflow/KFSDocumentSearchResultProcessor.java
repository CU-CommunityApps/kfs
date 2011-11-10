/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.krad.datadictionary.SearchingAttribute;
import org.kuali.rice.krad.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.krad.util.GlobalVariables;

public class KFSDocumentSearchResultProcessor extends StandardDocumentSearchResultProcessor {

    /**
     * Customizes the result set for purap document identified attribute value.  After getting the 
     * customized result set, if key exists for purapDocumentIdentifier, then check the permission for
     * the principal id.  If the permission exists and document status is FINAL, then unmask the field value else
     * mask field with ********. 
     * 
     * @see org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor#generateSearchResult(org.kuali.rice.kew.docsearch.DocSearchDTO, java.util.List)
     */
    @Override
    public DocumentSearchResult generateSearchResult(DocSearchDTO docCriteriaDTO, List<Column> columns) {
        DocumentSearchResult docSearchResult = super.generateSearchResult(docCriteriaDTO, columns);
        
        //do not mask the purapDocumentIdentifier field if the document is not PO or POSP..
        if (!KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER.equalsIgnoreCase(docCriteriaDTO.getDocTypeName()) &&
                !KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER_SPLIT.equalsIgnoreCase(docCriteriaDTO.getDocTypeName())) {
            return docSearchResult;
        }
        
        for (KeyValueSort keyValueSort : docSearchResult.getResultContainers()) {
            if (keyValueSort.getkey().equalsIgnoreCase(KFSPropertyConstants.PURAP_DOC_ID)) {
                //KFSMI-4576 masking PO number...
                String docStatus = docCriteriaDTO.getStatusCode();
                if (!docStatus.equalsIgnoreCase(KewApiConstants.ROUTE_HEADER_FINAL_CD)) {
                    //if document status is not FINAL then check for permission to see
                    //the value needs to be masked....
                    String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();
                    String namespaceCode = KFSConstants.ParameterNamespaces.KNS;
                    String permissionTemplateName = KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD;
                    
                    Map<String,String> roleQualifiers = new HashMap<String,String>();
                    
                    Map<String,String> permissionDetails = new HashMap<String,String>();
                    permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, KFSPropertyConstants.PURCHASE_ORDER_DOCUMENT_SIMPLE_NAME);
                    permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, KFSPropertyConstants.PURAP_DOC_ID);
                    
                    IdentityManagementService identityManagementService = SpringContext.getBean(IdentityManagementService.class);
                    Boolean isAuthorized = identityManagementService.isAuthorizedByTemplateName(principalId, namespaceCode, permissionTemplateName, permissionDetails, roleQualifiers);
                    //the principalId is not authorized to view the value in purapDocumentIdentifier field...so mask the value...
                    if (!isAuthorized) {
                        //not authorized to see... create a string 
                        keyValueSort.setValue("********");
                        keyValueSort.setSortValue("********");
                    }
                }
            }
        }
        
        return docSearchResult;
    }
    
    @Override
    public List<Column> constructColumnList(DocSearchCriteriaDTO criteria,List<DocSearchDTO> docSearchResultRows) {
        List<Column> tempColumns = new ArrayList<Column>();
        List<Column> customDisplayColumnNames = getAndSetUpCustomDisplayColumns(criteria);
        if ((!getShowAllStandardFields()) && (getOverrideSearchableAttributes())) {
            // use only what is contained in displayColumns
            this.addAllCustomColumns(tempColumns, criteria, customDisplayColumnNames);
        } else if (getShowAllStandardFields() && (getOverrideSearchableAttributes())) {
            // do standard fields and use displayColumns for searchable
            // attributes
            this.addStandardSearchColumns(tempColumns,docSearchResultRows);
            this.addAllCustomColumns(tempColumns, criteria,customDisplayColumnNames);
        } else if ((!getShowAllStandardFields()) && (!getOverrideSearchableAttributes())) {
            // do displayColumns and then do standard searchable attributes
            this.addCustomStandardCriteriaColumns(tempColumns, criteria, customDisplayColumnNames);
            this.addSearchableAttributeColumnsNoOverrides(tempColumns,criteria);
        } else if (getShowAllStandardFields() && !getOverrideSearchableAttributes()) {
            this.addStandardSearchColumns(tempColumns,docSearchResultRows);
        }

        List<Column> columns = new ArrayList<Column>();
        this.addRouteHeaderIdColumn(columns);
        columns.addAll(tempColumns);
        this.addRouteLogColumn(columns);
        return columns;
    }

    /**
     * Checks the Data Dictionary to verify the visibility of the fields and adds them to the result.
     * @param criteria used to get DocumentEntry
     * @return List of DocumentSearchColumns to be displayed
     */
    protected List<Column> getCustomDisplayColumns(DocSearchCriteriaDTO criteria) {

        SearchAttributeCriteriaComponent displayCriteria = getSearchableAttributeByFieldName("displayType");
        List<Column> columns = new ArrayList<Column>();

        boolean documentDisplay =  ((displayCriteria != null) && ("document".equals(displayCriteria.getValue())));
        if (documentDisplay) {

            DocumentType documentType = getDocumentType(criteria.getDocTypeFullName());
            DocumentEntry entry = getDocumentEntry(documentType);
            if (entry != null && entry.getWorkflowAttributes() != null) {
                DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);

                List<SearchingTypeDefinition> searchingTypeDefinitions = entry.getWorkflowAttributes().getSearchingTypeDefinitions();
                List<String> searchableAttributeFieldNames = new ArrayList<String>();

                for (SearchingTypeDefinition searchingTypeDefinition : searchingTypeDefinitions) {
                    SearchingAttribute searchingAttribute = searchingTypeDefinition.getSearchingAttribute();
                    if (searchingAttribute.isShowAttributeInResultSet()){
                        String label =  ddService.getAttributeLabel(searchingAttribute.getBusinessObjectClassName(), searchingAttribute.getAttributeName());
                        searchableAttributeFieldNames.add(label);
                        addColumnUsingKey(columns, searchingAttribute.getAttributeName(), label, null);
                    } 
                }
                addSearchableAttributeColumnsBasedOnFields(columns, getSearchCriteria(), searchableAttributeFieldNames);
            }
        }
        
        return columns;

    }

    @Override
    public List<Column> getAndSetUpCustomDisplayColumns(DocSearchCriteriaDTO criteria) {
        List<Column> columns = getCustomDisplayColumns(criteria);
        return super.setUpCustomDisplayColumns(criteria, columns);
    }

    /**
     * Retrieves the data dictionary entry for the document being operated on by the given route context
     * @param context the current route context
     * @return the data dictionary document entry
     */
    protected DocumentEntry getDocumentEntry(DocumentType documentType) {
        return SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentType.getName());
    }

    @Override
    public boolean getShowAllStandardFields() {
        if (searchUsingDocumentInformationResults()) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean getOverrideSearchableAttributes() {
        // TODO Auto-generated method stub
        return false;
    }

    protected boolean searchUsingDocumentInformationResults() {
        SearchAttributeCriteriaComponent displayCriteria = getSearchableAttributeByFieldName("displayType");
        return ((displayCriteria != null) && ("document".equals(displayCriteria.getValue())));
    }
}
