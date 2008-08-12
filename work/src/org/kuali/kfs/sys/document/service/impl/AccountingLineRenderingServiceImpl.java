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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineAuthorizationTransformer;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingTransformation;
import org.kuali.kfs.sys.document.service.AccountingLineTableTransformation;
import org.kuali.kfs.sys.document.web.AccountingLineTableRow;
import org.kuali.kfs.sys.document.web.RenderableElement;
import org.kuali.kfs.sys.document.web.TableJoining;

/**
 * The default implementation of the AccountingLineRenderingService
 */
public class AccountingLineRenderingServiceImpl implements AccountingLineRenderingService {
    private List<AccountingLineFieldRenderingTransformation> fieldTransformations;
    private DataDictionaryService dataDictionaryService;
    private AccountingLineAuthorizationTransformer accountingLineAuthorizationTransformer;
    private List<AccountingLineRenderingTransformation> preTablificationTransformations;
    private List<AccountingLineTableTransformation> postTablificationTransformations;

    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#performPreTablificationTransformations(java.util.List, org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.List)
     */
    public void performPreTablificationTransformations(List<TableJoining> elements, AccountingLineGroupDefinition groupDefinition, AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine, Map unconvertedValues) {
        performAuthorizationTransformations(elements, groupDefinition, accountingDocument, accountingLine, newLine);
        performFieldTransformations(elements, accountingDocument, accountingLine, unconvertedValues);
        for (AccountingLineRenderingTransformation transformation : preTablificationTransformations) {
            transformation.transformElements(elements, accountingLine);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#performPostTablificationTransformations(java.util.List, org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition, org.kuali.kfs.sys.document.AccountingDocument, org.kuali.kfs.sys.businessobject.AccountingLine, boolean)
     */
    public void performPostTablificationTransformations(List<AccountingLineTableRow> rows, AccountingLineGroupDefinition groupDefinition, AccountingDocument document, AccountingLine accountingLine, boolean newLine) {
        for (AccountingLineTableTransformation transformation : postTablificationTransformations) {
            transformation.transformRows(rows);
        }
    }


    /**
     * Performs the authorization transformations
     * @param elements the layout elements which we are authorizing
     * @param accountingLineGroupDefinition the data dictionary definition of the accounting line group
     * @param accountingDocument the accounting line document we're rendering accounting lines for
     * @param accountingLine the accounting line we're rendering
     * @param newLine true if the accounting line is not yet on the form yet, false otherwise
     */
    protected void performAuthorizationTransformations(List<TableJoining> elements, AccountingLineGroupDefinition accountingLineGroupDefinition, AccountingDocument accountingDocument, AccountingLine accountingLine, boolean newLine) {
        accountingLineAuthorizationTransformer.transformElements(elements, accountingLine, accountingDocument, accountingLineGroupDefinition.getAccountingLineAuthorizer(), getDocumentAuthorizer(accountingDocument), newLine);
    }
    
    /**
     * Performs field transformations for pre-rendering
     * @param elements the layout elements that hold fields to transform
     * @param accountingDocument the accounting document with the line we are rendering
     * @param accountingLine the accounting line we are rendering
     * @param unconvertedValues any unconverted values
     */
    protected void performFieldTransformations(List<TableJoining> elements, AccountingDocument accountingDocument, AccountingLine accountingLine, Map unconvertedValues) {
        Map editModes = getEditModes(accountingDocument);
        for (TableJoining layoutElement : elements) {
            layoutElement.performFieldTransformations(fieldTransformations, accountingLine, editModes, unconvertedValues);
        }
    }
    
    /**
     * Retrieves all the edit modes for the document
     * @param document the document to get edit modes for
     * @return a Map of all the edit modes
     */
    protected Map getEditModes(AccountingDocument document) {
        AccountingDocumentAuthorizer documentAuthorizer = getDocumentAuthorizer(document);
        Map editModes = documentAuthorizer.getEditMode(document, GlobalVariables.getUserSession().getFinancialSystemUser());
        editModes.putAll(documentAuthorizer.getEditMode(document, GlobalVariables.getUserSession().getFinancialSystemUser(), document.getSourceAccountingLines(), document.getTargetAccountingLines()));
        return editModes;
    }
 
    /**
     * Creates an accounting document authorizer for the given accounting document
     * @param document the document to get an authorizer for
     * @return an authorizer for the document
     */
    protected AccountingDocumentAuthorizer getDocumentAuthorizer(AccountingDocument document) {
        final Class documentAuthorizerClass = dataDictionaryService.getDataDictionary().getDocumentEntry(document.getClass().getName()).getDocumentAuthorizerClass();
        AccountingDocumentAuthorizer documentAuthorizer = null;
        try {
            documentAuthorizer = (AccountingDocumentAuthorizer)documentAuthorizerClass.newInstance();
        }
        catch (InstantiationException ie) {
            throw new IllegalArgumentException("InstantionException while initiating Document Authorizer", ie);
        }
        catch (IllegalAccessException iae) {
            throw new IllegalArgumentException("IllegalAccessException while initiating Document Authorizer", iae);
        }
        return documentAuthorizer;
    }

    /**
     * Simplify the tree so that it is made up of only table elements and fields
     * @see org.kuali.kfs.sys.document.service.AccountingLineRenderingService#tablify(java.util.List)
     */
    public List<AccountingLineTableRow> tablify(List<TableJoining> elements) {
        List<AccountingLineTableRow> rows = createBlankTableRows(getMaxRowCount(elements));
        tablifyElements(elements, rows);
        return rows;
    }
    
    /**
     * Gets the maximum number of rows needed by any child element
     * @param elements the elements to turn into table rows
     * @return the maximum number of rows requested
     */
    protected int getMaxRowCount(List<TableJoining> elements) {
        int maxCount = 0;
        for (TableJoining element : elements) {
            if (element.getRequestedRowCount() > maxCount) {
                maxCount = element.getRequestedRowCount();
            }
        }
        return maxCount;
    }
    
    /**
     * This method creates a List of blank table rows, based on the requested count
     * @param count the count of table rows
     * @return a List of table rows ready for population
     */
    protected List<AccountingLineTableRow> createBlankTableRows(int count) {
        List<AccountingLineTableRow> rows = new ArrayList<AccountingLineTableRow>();
        for (int i = 0; i < count; i++) {
            rows.add(new AccountingLineTableRow());
        }
        return rows;
    }

    /**
     * Requests each of the given elements to join the table
     * @param elements the elements to join to the table
     * @param rows the table rows to join to
     */
    protected void tablifyElements(List<TableJoining> elements, List<AccountingLineTableRow> rows) {
        for (TableJoining element : elements) {
            element.joinTable(rows);
        }
    }

    /**
     * Gets the fieldTransformations attribute. 
     * @return Returns the fieldTransformations.
     */
    public List<AccountingLineFieldRenderingTransformation> getFieldTransformations() {
        return fieldTransformations;
    }

    /**
     * Sets the fieldTransformations attribute value.
     * @param fieldTransformations The fieldTransformations to set.
     */
    public void setFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations) {
        this.fieldTransformations = fieldTransformations;
    }

    /**
     * Gets the accountingLineAuthorizationTransformer attribute. 
     * @return Returns the accountingLineAuthorizationTransformer.
     */
    public AccountingLineAuthorizationTransformer getAccountingLineAuthorizationTransformer() {
        return accountingLineAuthorizationTransformer;
    }

    /**
     * Sets the accountingLineAuthorizationTransformer attribute value.
     * @param accountingLineAuthorizationTransformer The accountingLineAuthorizationTransformer to set.
     */
    public void setAccountingLineAuthorizationTransformer(AccountingLineAuthorizationTransformer accountingLineAuthorizationTransformer) {
        this.accountingLineAuthorizationTransformer = accountingLineAuthorizationTransformer;
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the postTablificationTransformations attribute. 
     * @return Returns the postTablificationTransformations.
     */
    public List<AccountingLineTableTransformation> getPostTablificationTransformations() {
        return postTablificationTransformations;
    }

    /**
     * Sets the postTablificationTransformations attribute value.
     * @param postTablificationTransformations The postTablificationTransformations to set.
     */
    public void setPostTablificationTransformations(List<AccountingLineTableTransformation> postTablificationTransformations) {
        this.postTablificationTransformations = postTablificationTransformations;
    }

    /**
     * Gets the preTablificationTransformations attribute. 
     * @return Returns the preTablificationTransformations.
     */
    public List<AccountingLineRenderingTransformation> getPreTablificationTransformations() {
        return preTablificationTransformations;
    }

    /**
     * Sets the preTablificationTransformations attribute value.
     * @param preTablificationTransformations The preTablificationTransformations to set.
     */
    public void setPreTablificationTransformations(List<AccountingLineRenderingTransformation> preTablificationTransformations) {
        this.preTablificationTransformations = preTablificationTransformations;
    }
}
