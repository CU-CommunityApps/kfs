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
package org.kuali.kfs.sys.document.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.web.ui.Field;

/**
 * A container which holds a single accounting line and the elements which will render it
 */
public class RenderableAccountingLineContainer implements RenderableElement, AccountingLineRenderingContext {
    private List<AccountingLineTableRow> rows;
    private List<AccountingLineViewAction> actions;
    private AccountingLine accountingLine;
    private String accountingLineProperty;
    private List<Field> fields;
    private List<String> fieldNames;
    private KualiAccountingDocumentFormBase form;
    private String groupLabel;
    private Integer lineCount;
    private List errors;
    
    /**
     * Constructs a RenderableAccountingLineContainer
     * @param form the form being rendered
     * @param accountingLine the accounting line this container will render
     * @param accountingLineProperty the property to that accounting line
     * @param rows the rows to render
     * @param actions the actions associated with this accounting line
     * @param newLine whether this is a new accounting line or not
     * @param groupLabel the label for the group this accounting line is being rendered part of
     */
    public RenderableAccountingLineContainer(KualiAccountingDocumentFormBase form, AccountingLine accountingLine, String accountingLineProperty, List<AccountingLineTableRow> rows, List<AccountingLineViewAction> actions, Integer lineCount, String groupLabel, List errors) {
        this.form = form;
        this.accountingLine = accountingLine;
        this.accountingLineProperty = accountingLineProperty;
        this.rows = rows;
        this.actions = actions;
        this.lineCount = lineCount;
        this.groupLabel = groupLabel;
        this.errors = errors;
    }
    
    /**
     * Gets the accountingLine attribute. 
     * @return Returns the accountingLine.
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    /**
     * Gets the accountingLineProperty attribute. 
     * @return Returns the accountingLineProperty.
     */
    public String getAccountingLineProperty() {
        return accountingLineProperty;
    }

    /**
     * Gets the actions attribute. 
     * @return Returns the actions.
     */
    public List<AccountingLineViewAction> getActions() {
        return actions;
    }

    /**
     * Gets the newLine attribute. 
     * @return Returns the newLine.
     */
    public boolean isNewLine() {
        return lineCount == null;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getCurrentLineCount()
     */
    public Integer getCurrentLineCount() {
        return lineCount;
    }

    /**
     * Gets the rows attribute. 
     * @return Returns the rows.
     */
    public List<AccountingLineTableRow> getRows() {
        return rows;
    }
    
    /**
     * @return the number of cells this accounting line container will render
     */
    public int getCellCount() {
        int maxCells = 0;
        for (AccountingLineTableRow row : rows) {
            final int maxRowCellCount = row.getChildCellCount();
            if (maxCells < maxRowCellCount) {
                maxCells = maxRowCellCount;
            }
        }
        return maxCells;
    }
    
    /**
     * Adds empty cells to a table row
     * @param cellCount the number of cells we should be rendering
     * @param row the row to pad out
     */
    protected void padOutRow(int cellCount, AccountingLineTableRow row) {
        while ((cellCount - row.getChildCellCount()) > 0) {
            row.addCell(new AccountingLineTableCell());
        }
    }
    
    /**
     * While holding an action block, this is not an action block
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }
    
    /**
     * This is never empty
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }
    
    /**
     * This is not hidden 
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        return false;
    }
    
    /**
     * Renders all the rows
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        for (AccountingLineTableRow row : rows) {
            row.renderElement(pageContext, parentTag, renderingContext);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getAccountingLinePropertyPath()
     */
    public String getAccountingLinePropertyPath() {
        return accountingLineProperty;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getActionsForLine()
     */
    public List<AccountingLineViewAction> getActionsForLine() {
        return this.actions;
    }
    
    /**
     * Appends all fields from rows that this contains
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     */
    public void appendFields(List<Field> fields) {
        for (AccountingLineTableRow row : rows) {
            row.appendFields(fields);
        }
    }
    
    /**
     * Returns all of the field names within the accounting line to render
     * @return a List of field names with the accounting line property prefixed
     */
    public List<Field> getFieldsForAccountingLine() {
        if (fields == null) {
            fields = new ArrayList<Field>();
            appendFields(fields);
        }
        return fields;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getFieldNamesForAccountingLine()
     */
    public List<String> getFieldNamesForAccountingLine() {
        if (fieldNames == null) {
            fieldNames = new ArrayList<String>();
            for (Field field : getFieldsForAccountingLine()) {
                fieldNames.add(accountingLineProperty+"."+field.getPropertyName());
            }
        }
        return fieldNames;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested( int reallyHighIndex) {
        for (AccountingLineTableRow row : rows) {
            row.populateWithTabIndexIfRequested(reallyHighIndex);
        }
    }
    
    /**
     * Returns the unconvertedValues for the current form
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getUnconvertedValues()
     */
    public Map getUnconvertedValues() {
        return form.getUnconvertedValues();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#populateValuesForFields()
     */
    public void populateValuesForFields() {
        FieldUtils.populateFieldsFromBusinessObject(getFieldsForAccountingLine(), accountingLine);
        
        BusinessObjectEntry boDDEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(getAccountingLine().getClass().getName());
        
        for (Field field : getFieldsForAccountingLine()) {
            setUnconvertedValueIfNecessary(field);
            setShouldShowSecure(field, boDDEntry);
        }
    }
    
    /**
     * Sees if the given field has an unconverted value living in the unconverted value map and if so,
     * changes the value to that
     * @param field the field to possibly set an unconverted value on
     */
    protected void setUnconvertedValueIfNecessary(Field field) {
        String propertyName = accountingLineProperty+"."+field.getPropertyName();
        if (getUnconvertedValues().get(propertyName) != null) {
            field.setPropertyValue((String)getUnconvertedValues().get(propertyName));
        }
    }
    
    /**
     * Sets the masked value equal to the value if the current user can see the unmasked value for a secure field
     * @param field the field to possible change the value for
     * @param boDDEntry the data dictionary entry for the accounting line
     */
    protected void setShouldShowSecure(Field field, BusinessObjectEntry boDDEntry) {
        if (field.isSecure()) {
            String workgroupName = boDDEntry.getAttributeDefinition(field.getPropertyName()).getDisplayWorkgroup();
        
            if (GlobalVariables.getUserSession().getFinancialSystemUser().isMember(workgroupName)) {
                field.setDisplayMaskValue(field.getPropertyValue());
            }
        }
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getAccountingDocument()
     */
    public AccountingDocument getAccountingDocument() {
        return form.getFinancialDocument();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#fieldsCanRenderDynamicLabels()
     */
    public boolean fieldsCanRenderDynamicLabels() {
        return !form.isHideDetails();
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#fieldsShouldRenderHelp()
     */
    public boolean fieldsShouldRenderHelp() {
        return form.isFieldLevelHelpEnabled();
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getTabState(java.lang.String)
     */
    public String getTabState(String tabKey) {
        return form.getTabState(tabKey);
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#incrementTabIndex()
     */
    public void incrementTabIndex() {
        form.incrementTabIndex();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getGroupLabel()
     */
    public String getGroupLabel() {
       return this.groupLabel; 
    }

    /**
     * Gets the errors attribute. 
     * @return Returns the errors.
     */
    public List getErrors() {
        return errors;
    }
    
}
