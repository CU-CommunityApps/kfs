/*
 * Copyright 2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.integration.ld.LaborLedgerPendingEntryForSearching;
import org.kuali.kfs.integration.ld.LaborLedgerPostingDocumentForSearching;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocument;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineGroupDefinition;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.KewApiConstants.SearchableAttributeConstants;
import org.kuali.rice.kew.api.document.DocumentWithContent;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeFactory;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.api.extension.ExtensionDefinition;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.attribute.DataDictionarySearchableAttribute;

public class FinancialSystemSearchableAttribute extends DataDictionarySearchableAttribute {

    // used to map the special fields to the DD Entry that validate it.
    private static Map<String, String> magicFields = new HashMap<String, String>();

    static {
        magicFields.put("chartOfAccountsCode", "SourceAccountingLine");
        magicFields.put("organizationCode", "Organization");
        magicFields.put("accountNumber", "SourceAccountingLine");
        magicFields.put("financialDocumentTypeCode", "GeneralLedgerPendingEntry");
        magicFields.put("financialDocumentTotalAmount", "FinancialSystemDocumentHeader");
    }


    public List<Row> getSearchingRows(String documentTypeName) {
        DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);

        List<Row> docSearchRows = super.getSearchingRows(documentTypeName);

        DocumentEntry entry = (DocumentEntry) ddService.getDataDictionary().getDocumentEntry(documentTypeName);

        if (entry == null) {
            return docSearchRows;
        }
        Class<? extends Document> docClass = entry.getDocumentClass();

        List<String> displayedFieldNames = new ArrayList<String>();

        if (AccountingDocument.class.isAssignableFrom(docClass)) {
            Map<String, AccountingLineGroupDefinition> alGroups = ((FinancialSystemTransactionalDocumentEntry) entry).getAccountingLineGroups();
            Class alClass = SourceAccountingLine.class;

            if (ObjectUtils.isNotNull(alGroups)) {
                if (alGroups.containsKey("source")) {
                    alClass = alGroups.get("source").getAccountingLineClass();
                }
            }

            BusinessObject alBusinessObject = null;

            Class orgClass = Organization.class;
            BusinessObject orgBusinessObject = null;

            try {
                alBusinessObject = (BusinessObject) alClass.newInstance();
                orgBusinessObject = (BusinessObject) orgClass.newInstance();

            }
            catch (Exception cnfe) {
                throw new RuntimeException(cnfe);
            }

            Field chartField = FieldUtils.getPropertyField(alClass, "chartOfAccountsCode", true);
            chartField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            displayedFieldNames.add("chartOfAccountsCode");
            LookupUtils.setFieldQuickfinder(alBusinessObject, "chartOfAccountsCode", chartField, displayedFieldNames);

            Field orgField = FieldUtils.getPropertyField(orgClass, "organizationCode", true);
            orgField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            displayedFieldNames.clear();
            displayedFieldNames.add("organizationCode");
            LookupUtils.setFieldQuickfinder(new Account(), "organizationCode", orgField, displayedFieldNames);

            Field accountField = FieldUtils.getPropertyField(alClass, "accountNumber", true);
            accountField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);
            displayedFieldNames.clear();
            displayedFieldNames.add("accountNumber");
            LookupUtils.setFieldQuickfinder(alBusinessObject, "accountNumber", accountField, displayedFieldNames);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(chartField);
            docSearchRows.add(new Row(fieldList));

            fieldList = new ArrayList<Field>();
            fieldList.add(accountField);
            docSearchRows.add(new Row(fieldList));

            fieldList = new ArrayList<Field>();
            fieldList.add(orgField);
            docSearchRows.add(new Row(fieldList));
        }

        boolean displayedLedgerPostingDoc = false;
        if (LaborLedgerPostingDocumentForSearching.class.isAssignableFrom(docClass)) {
            Class boClass = GeneralLedgerPendingEntry.class;

            Field searchField = FieldUtils.getPropertyField(boClass, "financialDocumentTypeCode", true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);

            displayedFieldNames.clear();
            displayedFieldNames.add("financialDocumentTypeCode");
            LookupUtils.setFieldQuickfinder(new GeneralLedgerPendingEntry(), "financialDocumentTypeCode", searchField, displayedFieldNames);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(searchField);
            docSearchRows.add(new Row(fieldList));
            displayedLedgerPostingDoc = true;
        }

        if (GeneralLedgerPostingDocument.class.isAssignableFrom(docClass) && !displayedLedgerPostingDoc) {
            Class boClass = GeneralLedgerPendingEntry.class;

            Field searchField = FieldUtils.getPropertyField(boClass, "financialDocumentTypeCode", true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_STRING);

            displayedFieldNames.clear();
            displayedFieldNames.add("financialDocumentTypeCode");
            LookupUtils.setFieldQuickfinder(new GeneralLedgerPendingEntry(), "financialDocumentTypeCode", searchField, displayedFieldNames);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(searchField);
            docSearchRows.add(new Row(fieldList));

        }

        if (AmountTotaling.class.isAssignableFrom(docClass)) {
            Class boClass = FinancialSystemDocumentHeader.class;

            Field searchField = FieldUtils.getPropertyField(boClass, "financialDocumentTotalAmount", true);
            searchField.setFieldDataType(SearchableAttributeConstants.DATA_TYPE_FLOAT);

            List<Field> fieldList = new ArrayList<Field>();
            fieldList.add(searchField);
            docSearchRows.add(new Row(fieldList));

        }


        Row resultType = createSearchResultReturnRow();
        docSearchRows.add(resultType);
        return docSearchRows;
    }

    public List<DocumentAttribute> extractDocumentAttributes(ExtensionDefinition extensionDefinition, DocumentWithContent documentWithContent) {
        List<DocumentAttribute> extractDocumentAttributes = super.extractDocumentAttributes(extensionDefinition, documentWithContent);

        String docId = documentWithContent.getDocument().getDocumentId();
        DocumentService docService = SpringContext.getBean(DocumentService.class);
        Document doc = null;
        try {
            doc = docService.getByDocumentHeaderIdSessionless(docId);
        }
        catch (WorkflowException we) {

        }

        if (doc instanceof AmountTotaling) {
            extractDocumentAttributes.add(DocumentAttributeFactory.createDecimalAttribute("financialDocumentTotalAmount", ((AmountTotaling) doc).getTotalDollarAmount().bigDecimalValue()));
        }

        if (doc instanceof AccountingDocument) {
            AccountingDocument accountingDoc = (AccountingDocument) doc;
            extractDocumentAttributes.addAll(harvestAccountingDocumentSearchableAttributes(accountingDoc));
        }

        boolean indexedLedgerDoc = false;
        if (doc instanceof LaborLedgerPostingDocumentForSearching) {
            LaborLedgerPostingDocumentForSearching LLPostingDoc = (LaborLedgerPostingDocumentForSearching) doc;
            extractDocumentAttributes.addAll(harvestLLPDocumentSearchableAttributes(LLPostingDoc));
            indexedLedgerDoc = true;
        }

        if (doc instanceof GeneralLedgerPostingDocument && !indexedLedgerDoc) {
            GeneralLedgerPostingDocument GLPostingDoc = (GeneralLedgerPostingDocument) doc;
            extractDocumentAttributes.addAll(harvestGLPDocumentSearchableAttributes(GLPostingDoc));
        }


        return extractDocumentAttributes;
    }

    /**
     * @see org.kuali.rice.krad.workflow.attribute.DataDictionarySearchableAttribute#validateUserSearchInputs(java.util.Map,
     *      org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */

    @Override
    public List<RemotableAttributeError> validateDocumentAttributeCriteria(ExtensionDefinition extensionDefinition, DocumentSearchCriteria documentSearchCriteria) {
        // this list is irrelevant. the validation errors are put on the stack in the validationService.
        List<RemotableAttributeError> errors = super.validateDocumentAttributeCriteria(extensionDefinition, documentSearchCriteria);
        DictionaryValidationService validationService = SpringContext.getBean(DictionaryValidationService.class);

        // validate the document attribute values
        Map<String, List<String>> documentAttributeValues = documentSearchCriteria.getDocumentAttributeValues();
        for (String key : documentAttributeValues.keySet()) {
            if (magicFields.containsKey(key)) {
                List<String> values = documentAttributeValues.get(key);
                if (values != null && !values.isEmpty()) {
                    for (String value : values) {
                        validationService.validateAttributeFormat(magicFields.get(key), key, value, key);
                    }
                }
            }
        }

        return errors;
    }


    /**
     * Harvest chart of accounts code, account number, and organization code as searchable attributes from an accounting document
     * 
     * @param accountingDoc the accounting document to pull values from
     * @return a List of searchable values
     */
    protected List<DocumentAttribute> harvestAccountingDocumentSearchableAttributes(AccountingDocument accountingDoc) {
        List<DocumentAttribute> searchAttrValues = new ArrayList<DocumentAttribute>();

        for (Iterator itr = accountingDoc.getSourceAccountingLines().iterator(); itr.hasNext();) {
            AccountingLine accountingLine = (AccountingLine) itr.next();
            addSearchableAttributesForAccountingLine(searchAttrValues, accountingLine);
        }
        for (Iterator itr = accountingDoc.getTargetAccountingLines().iterator(); itr.hasNext();) {
            AccountingLine accountingLine = (AccountingLine) itr.next();
            addSearchableAttributesForAccountingLine(searchAttrValues, accountingLine);
        }

        return searchAttrValues;
    }

    /**
     * Harvest GLPE document type as searchable attributes from a GL posting document
     * 
     * @param GLPDoc the GLP document to pull values from
     * @return a List of searchable values
     */
    protected List<DocumentAttribute> harvestGLPDocumentSearchableAttributes(GeneralLedgerPostingDocument GLPDoc) {
        List<DocumentAttribute> searchAttrValues = new ArrayList<DocumentAttribute>();

        for (Iterator itr = GLPDoc.getGeneralLedgerPendingEntries().iterator(); itr.hasNext();) {
            GeneralLedgerPendingEntry glpe = (GeneralLedgerPendingEntry) itr.next();
            addSearchableAttributesForGLPE(searchAttrValues, glpe);
        }
        return searchAttrValues;
    }

    /**
     * Harvest LLPE document type as searchable attributes from a LL posting document
     * 
     * @param LLPDoc the LLP document to pull values from
     * @return a List of searchable values
     */
    protected List<DocumentAttribute> harvestLLPDocumentSearchableAttributes(LaborLedgerPostingDocumentForSearching LLPDoc) {
        List<DocumentAttribute> searchAttrValues = new ArrayList<DocumentAttribute>();

        for (Iterator itr = LLPDoc.getLaborLedgerPendingEntriesForSearching().iterator(); itr.hasNext();) {
            LaborLedgerPendingEntryForSearching llpe = (LaborLedgerPendingEntryForSearching) itr.next();
            addSearchableAttributesForLLPE(searchAttrValues, llpe);
        }
        return searchAttrValues;
    }


    /**
     * Pulls the default searchable attributes - chart code, account number, and account organization code - from a given accounting
     * line and populates the searchable attribute values in the given list
     * 
     * @param searchAttrValues a List of SearchableAttributeValue objects to populate
     * @param accountingLine an AccountingLine to get values from
     */
    protected void addSearchableAttributesForAccountingLine(List<DocumentAttribute> searchAttrValues, AccountingLine accountingLine) {
        searchAttrValues.add(DocumentAttributeFactory.createStringAttribute(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountingLine.getChartOfAccountsCode()));
        searchAttrValues.add(DocumentAttributeFactory.createStringAttribute(KFSPropertyConstants.ACCOUNT_NUMBER, accountingLine.getAccountNumber()));
        searchAttrValues.add(DocumentAttributeFactory.createStringAttribute(KFSPropertyConstants.ORGANIZATION_CODE, accountingLine.getAccount().getOrganizationCode()));
    }

    /**
     * Pulls the default searchable attribute - financialSystemTypeCode - from a given accounting line and populates the searchable
     * attribute values in the given list
     * 
     * @param searchAttrValues a List of SearchableAttributeValue objects to populate
     * @param glpe a GeneralLedgerPendingEntry to get values from
     */
    protected void addSearchableAttributesForGLPE(List<DocumentAttribute> searchAttrValues, GeneralLedgerPendingEntry glpe) {
        searchAttrValues.add(DocumentAttributeFactory.createStringAttribute(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, glpe.getFinancialDocumentTypeCode()));

    }

    /**
     * Pulls the default searchable attribute - financialSystemTypeCode from a given accounting line and populates the searchable
     * attribute values in the given list
     * 
     * @param searchAttrValues a List of SearchableAttributeValue objects to populate
     * @param llpe a LaborLedgerPendingEntry to get values from
     */
    protected void addSearchableAttributesForLLPE(List<DocumentAttribute> searchAttrValues, LaborLedgerPendingEntryForSearching llpe) {
        searchAttrValues.add(DocumentAttributeFactory.createStringAttribute(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, llpe.getFinancialDocumentTypeCode()));
    }

    private Row createSearchResultReturnRow() {
        String attributeName = "displayType";
        Field searchField = new Field();
        searchField.setPropertyName(attributeName);
        searchField.setFieldType(Field.RADIO);
        searchField.setFieldLabel("Search Result Type");
        searchField.setIndexedForSearch(false);
        searchField.setBusinessObjectClassName("");
        searchField.setFieldHelpName("");
        searchField.setFieldHelpSummary("");
        searchField.setColumnVisible(false);
        List<KeyValue> values = new ArrayList<KeyValue>();
        values.add(new ConcreteKeyValue("document", "Document Specific Data"));
        values.add(new ConcreteKeyValue("workflow", "Workflow Data"));
        searchField.setFieldValidValues(values);
        searchField.setPropertyValue("document");

        List<Field> fieldList = new ArrayList<Field>();
        fieldList.add(searchField);

        return new Row(fieldList);

    }

}
