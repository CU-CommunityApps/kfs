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
    private boolean newLine;
    private AccountingLine accountingLine;
    private String accountingLineProperty;
    private boolean renderHelp = false;
    private boolean showDetails = true;
    private List<Field> fields;
    private List<String> fieldNames;
    private Map unconvertedValues;
    
    /**
     * Gets the accountingLine attribute. 
     * @return Returns the accountingLine.
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }
    /**
     * Sets the accountingLine attribute value.
     * @param accountingLine The accountingLine to set.
     */
    public void setAccountingLine(AccountingLine accountingLine) {
        this.accountingLine = accountingLine;
    }
    /**
     * Gets the accountingLineProperty attribute. 
     * @return Returns the accountingLineProperty.
     */
    public String getAccountingLineProperty() {
        return accountingLineProperty;
    }
    /**
     * Sets the accountingLineProperty attribute value.
     * @param accountingLineProperty The accountingLineProperty to set.
     */
    public void setAccountingLineProperty(String accountingLineProperty) {
        this.accountingLineProperty = accountingLineProperty;
    }
    /**
     * Gets the actions attribute. 
     * @return Returns the actions.
     */
    public List<AccountingLineViewAction> getActions() {
        return actions;
    }
    /**
     * Sets the actions attribute value.
     * @param actions The actions to set.
     */
    public void setActions(List<AccountingLineViewAction> actions) {
        this.actions = actions;
    }
    /**
     * Gets the newLine attribute. 
     * @return Returns the newLine.
     */
    public boolean isNewLine() {
        return newLine;
    }
    /**
     * Sets the newLine attribute value.
     * @param newLine The newLine to set.
     */
    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }
    /**
     * Gets the rows attribute. 
     * @return Returns the rows.
     */
    public List<AccountingLineTableRow> getRows() {
        return rows;
    }
    /**
     * Sets the rows attribute value.
     * @param rows The rows to set.
     */
    public void setRows(List<AccountingLineTableRow> rows) {
        this.rows = rows;
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
     * Gets the renderHelp attribute. 
     * @return Returns the renderHelp.
     */
    public boolean isRenderHelp() {
        return renderHelp;
    }
    /**
     * Sets the renderHelp attribute value.
     * @param renderHelp The renderHelp to set.
     */
    public void setRenderHelp(boolean renderHelp) {
        this.renderHelp = renderHelp;
    }
    /**
     * Gets the showDetails attribute. 
     * @return Returns the showDetails.
     */
    public boolean isShowDetails() {
        return showDetails;
    }
    /**
     * Sets the showDetails attribute value.
     * @param showDetails The showDetails to set.
     */
    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#fieldsShouldRenderHelp()
     */
    public boolean fieldsShouldRenderHelp() {
        return renderHelp;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#fieldsCanRenderDynamicLabels()
     */
    public boolean fieldsCanRenderDynamicLabels() {
        return showDetails;
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
    public void populateWithTabIndexIfRequested(int[] passIndexes, int reallyHighIndex) {
        for (AccountingLineTableRow row : rows) {
            row.populateWithTabIndexIfRequested(passIndexes, reallyHighIndex);
        }
    }
    
    /**
     * Gets the unconvertedValues attribute. 
     * @return Returns the unconvertedValues.
     */
    public Map getUnconvertedValues() {
        return unconvertedValues;
    }
    
    /**
     * Sets the unconvertedValues attribute value.
     * @param unconvertedValues The unconvertedValues to set.
     */
    public void setUnconvertedValues(Map unconvertedValues) {
        this.unconvertedValues = unconvertedValues;
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
        if (unconvertedValues.get(propertyName) != null) {
            field.setPropertyValue((String)unconvertedValues.get(propertyName));
        }
    }
    
    protected void setShouldShowSecure(Field field, BusinessObjectEntry boDDEntry) {
        if (field.isSecure()) {
            String workgroupName = boDDEntry.getAttributeDefinition(field.getPropertyName()).getDisplayWorkgroup();
        
            if (GlobalVariables.getUserSession().getFinancialSystemUser().isMember(workgroupName)) {
                field.setSecure(false);
            }
        }
    }
}
