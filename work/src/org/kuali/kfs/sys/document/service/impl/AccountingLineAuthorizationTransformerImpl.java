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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.document.service.AccountingLineAuthorizationTransformer;
import org.kuali.kfs.sys.document.web.ReadOnlyable;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Like a regular accounting line rendering transformer, though this  
 */
public class AccountingLineAuthorizationTransformerImpl implements AccountingLineAuthorizationTransformer {

    /**
     * Performs transformations to the element rendering tree based on the authorization's reactions to the accounting line
     * @param elements the element rendering tree
     * @param accountingLine the accounting line to be rendered
     * @param document the document that accounting line lives on
     * @param lineAuthorizer the authorizer for the accounting line
     * @param documentAuthorizer the authorizer for the document
     * @param newLine is this line a new line or a line already on a document?
     * 
     */
    public void transformElements(List<TableJoining> elements, AccountingLine accountingLine, AccountingDocument document, AccountingLineAuthorizer lineAuthorizer, AccountingDocumentAuthorizer documentAuthorizer, boolean newLine, String accountingLinePropertyName) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        removeUnviewableBlocks(elements, lineAuthorizer.getUnviewableBlocks(document, accountingLine, newLine, currentUser));
        
        final Map editModesForDocument = documentAuthorizer.getEditMode(document, currentUser);
        final String editMode = lineAuthorizer.getEditModeForAccountingLine(document, accountingLine, newLine, currentUser, editModesForDocument);
        final boolean lineInError = isLineInError(document, accountingLine, accountingLinePropertyName); // never readonlyize a line which has an error associated with it
        
        if (!AuthorizationConstants.EditMode.FULL_ENTRY.equals(editMode) && !lineInError) {
            readOnlyizeAllBlocks(elements);
            setEditableBlocks(elements, lineAuthorizer.getEditableBlocksInReadOnlyLine(document, accountingLine, currentUser));
        } else {
            readOnlyizeReadOnlyBlocks(elements, lineAuthorizer.getReadOnlyBlocks(document, accountingLine, newLine, currentUser));
        }
    }
    
    /**
     * 
     * @param elements the elements of the rendering tree
     * @param unviewableBlocks a Set of the names of blocks that are not viewable
     */
    protected void removeUnviewableBlocks(List<TableJoining> elements, Set<String> unviewableBlocks) {
        if (unviewableBlocks.size() > 0) {
            Set<TableJoining> elementsToRemove = new HashSet<TableJoining>();
            for (TableJoining element : elements) {
                if (unviewableBlocks.contains(element.getName())) {
                    elementsToRemove.add(element);
                } else {
                    element.removeUnviewableBlocks(unviewableBlocks);
                }
            }
            elements.removeAll(elementsToRemove);
        }
    }
    
    /**
     * Makes any blocks within the given set of readOnlyBlocks entirely read only
     * @param element the element rendering tree
     * @param readOnlyBlocks a Set of the names of blocks that should be read only
     */
    protected void readOnlyizeReadOnlyBlocks(List<TableJoining> elements, Set<String> readOnlyBlocks) {
        if (readOnlyBlocks.size() > 0) {
            for (TableJoining element : elements) {
                element.readOnlyizeReadOnlyBlocks(readOnlyBlocks);
            }
        }
    }
    
    /**
     * Makes any blocks within the given set of editableBlocks entirely editable
     * @param element the element rendering tree
     * @param editableBlocks a Set of the names of blocks that should be editable
     */
    protected void setEditableBlocks(List<TableJoining> elements, Set<String> editableBlocks) {
        if (editableBlocks.size() > 0) {
            for (TableJoining element : elements) {
                element.setEditableBlocks(editableBlocks);
            }
        }
    }
    
    /**
     * Makes all blocks on the accounting line read only
     * @param elements a List of table joining elements to make read only
     */
    protected void readOnlyizeAllBlocks(List<TableJoining> elements) {
        for (TableJoining element : elements) {
            if (element instanceof ReadOnlyable) {
                ((ReadOnlyable)element).readOnlyize();
            }
        }
    }
    
    /**
     * Determines whether the given line is in error
     * @param document the accounting document the line will eventually live on if it isn't already
     * @param line the accounting line itself
     * @param accountingLinePropertyName the property prefix for all fields within the accounting line
     * @return true if the accounting line has errors associated with it, false otherwise
     */
    protected boolean isLineInError(AccountingDocument document, AccountingLine line, String accountingLinePropertyName) {
        return GlobalVariables.getErrorMap().containsKeyMatchingPattern(accountingLinePropertyName+"*");
    }
}

