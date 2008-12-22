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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;

/**
 * A contract for classes which wish to provide information about an accounting line which is being rendered
 */
public interface AccountingLineRenderingContext {
    
    /**
     * @return the accounting line that would be rendered by the rendering of the given accounting line context
     */
    public abstract AccountingLine getAccountingLine();
    
    /**
     * @return the property path from the form to the accounting line returned by the getAccountingLine() method
     */
    public abstract String getAccountingLinePropertyPath();
    
    /**
     * @return a List of actions which can be performed on the given line
     */
    public abstract List<AccountingLineViewAction> getActionsForLine();
    
    /**
     * Tells callers if fields should render help or not
     * @return true if fields should render help, false otherwise
     */
    public abstract boolean fieldsShouldRenderHelp();
    
    /**
     * Tells callers if dynamic field labels should even be rendered
     * @return true if dynamic field labels can be labeled, false if they should not be labeled.
     */
    public abstract boolean fieldsCanRenderDynamicLabels();
    
    /**
     * Reports whether the tag to be rendered by this rendering context is "new" - ie, not added yet to the accounting group, but living on the form somewhere - or not
     * @return true if the line is new, false otherwise
     */
    public abstract boolean isNewLine();
    
    /**
     * If this is a new line, returns null; if it is a line in a collection, returns the line number within that collection
     * @return the current line count
     */
    public abstract Integer getCurrentLineCount();
    
    /**
     * Returns all the field names for the given accounting line, prefixed by the accounting line property path
     * @return a list of properly prefixed field names
     */
    public abstract List<String> getFieldNamesForAccountingLine();
    
    /**
     * Returns a Map of all values from the request which were unconverted to actuall business objects
     * @return the Map of unconverted values
     */
    public abstract Map getUnconvertedValues();
    
    /**
     * Forces the population of values for all fields used to render the accounting line
     */
    public abstract void populateValuesForFields();
    
    /**
     * @return the accounting document that this line to render is part of (or will be, once it is successfully added)
     */
    public abstract AccountingDocument getAccountingDocument();
    
    /**
     * Returns the tab state for the given tab key on the current form
     * @param tabKey the tab key to get the state of
     * @return the state (either "OPEN" or "CLOSED")
     */
    public abstract String getTabState(String tabKey);
    
    /**
     * Increments the tab index on the form this rendering is associated with
     */
    public abstract void incrementTabIndex();
    
    /**
     * @return the label of the group this accounting line is part of
     */
    public abstract String getGroupLabel();
    
    /**
     * @return the list of errors on the form
     */
    public abstract List getErrors();
    
    /**
     * @return the form that the rendered accounting line will be associated with
     */
    public abstract KualiAccountingDocumentFormBase getForm();
    
    /**
     * @return the property name of the object that contains the accounting lines
     */
    public abstract String getAccountingLineContainingObjectPropertyName();
    
    /**
     * Determines whether a field is modifyable or not
     * @param field the field to test
     * @return true if the field can be modified, false if the field can only be read
     */
    public abstract boolean isFieldModifyable(AccountingLineViewField field);
}
