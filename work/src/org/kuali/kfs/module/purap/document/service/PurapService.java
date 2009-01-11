/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PurapItemOperations;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Defines methods that must be implemented by classes providing a PurapService. 
 */
public interface PurapService {

    /**
     * Update the status for the given Purchasing/Accounts Payable document
     * 
     * @param document
     * @param statusToSet
     * @return
     */
    public boolean updateStatus(PurchasingAccountsPayableDocument document, String statusToSet);

    /**
     * Retrieve list of views for given identifier
     * 
     * @param clazz
     * @param accountsPayablePurchasingDocumentLinkIdentifier
     * @return List of views for given identifier
     */
    public List getRelatedViews(Class clazz, Integer accountsPayablePurchasingDocumentLinkIdentifier);

    /**
     * Add the allowed below the line items to the given document
     * 
     * @param document   PurchasingAccountsPayableDocument
     */
    public void addBelowLineItems(PurchasingAccountsPayableDocument document);

    /**
     * Retrieves the below the line items allowed from the parameter table for the given document
     * 
     * @param document  PurchasingAccountsPayableDocument
     * @return Array list of below the line items 
     */
    public String[] getBelowTheLineForDocument(PurchasingAccountsPayableDocument document);

    /**
     * Retrieve the below the line item for a doc by item type (unknown result if multiple of same below the line item
     * type)
     * 
     * @param document the document
     * @param iT the itemType
     * @return below the line item by item type
     */
    public PurApItem getBelowTheLineByType(PurchasingAccountsPayableDocument document, ItemType iT);
    
    /**
     * Return a Date object which is which is a given number of days away from today, in either direction. 
     * 
     * @param offsetDays
     * @return  A java.sql.Date
     */
    public Date getDateFromOffsetFromToday(int offsetDays);

    /**
     * Determine whether a given date is in the past.
     * 
     * @param compareDate An SQL date (not a DateFormatter date, or a util Date)
     * @return True if the given date is before today.
     */
    public boolean isDateInPast(Date compareDate);

    /**
     * Determine whether a given date is more than a given number of days away from the current date.
     * 
     * @param compareDate An SQL date (not a DateFormatter date, or a util Date)
     * @param daysAway An int, positive for future days, negative for past days
     * @return True if the given date is more than the given number of days away in either direction.
     */
    public boolean isDateMoreThanANumberOfDaysAway(Date compareDate, int daysAway);

    /**
     * We are obliged not to simply use a dateDiff and compare the result to 365, because we have to worry about leap years.
     * 
     * @param compareDate An SQL date (not a DateFormatter date, or a util Date)
     * @return True if the date given for comparison is more than a year in the past, not including today.
     */
    public boolean isDateAYearBeforeToday(Date compareDate);

    /**
     * Retrieve the Automatic Purchase Order Limit amount based first on the derived contract limit (see
     * {@link org.kuali.kfs.vnd.document.service.VendorService#getApoLimitFromContract(Integer, String, String)}) and if that is null
     * then based on the {@link org.kuali.kfs.module.purap.businessobject.OrganizationParameter} associated with the given 'chart' and 'org' values.
     * 
     * @param vendorContractGeneratedIdentifier
     * @param chart chart code to use when looking up limit amount on {@link org.kuali.kfs.vnd.businessobject.VendorContract} and
     *        {@link org.kuali.kfs.module.purap.businessobject.OrganizationParameter}
     * @param org organization code to use when looking up limit amount on {@link org.kuali.kfs.vnd.businessobject.VendorContract} and
     *        {@link org.kuali.kfs.module.purap.businessobject.OrganizationParameter}
     * @return a KualiDecimal if a valid limit amount is found or null if one is not found
     */
    public KualiDecimal getApoLimit(Integer vendorContractGeneratedIdentifier, String chart, String org);

    /**
     * Determines if full entry mode has ended for this Purchasing/Accounts Payable document.
     * 
     * @param purapDocument PurchasingAccountsPayableDocument
     * @return a boolean to indicate if document has completed full entry mode
     */
    public boolean isFullDocumentEntryCompleted(PurchasingAccountsPayableDocument purapDocument);

    /**
     * Create a close or reopen purchase order document.
     * 
     * @param purapDocument PurchasingAccountsPayableDocument
     */
    public void performLogicForCloseReopenPO(PurchasingAccountsPayableDocument purapDocument);

    /**
     * Performs the given logic with the given fake user id.  Need this to control the user.
     * 
     * @param requiredPersonPersonUserId
     * @param logicToRun
     * @param objects
     * @return
     * @throws WorkflowException
     * @throws Exception
     */
    public Object performLogicWithFakedUserSession(String requiredPersonPersonUserId, LogicContainer logicToRun, Object... objects) throws WorkflowException, Exception;

    /**
     * Sort the below the line elements of the given document
     * 
     * @param document  PurchasingAccountsPayableDocument to be sorted
     */
    public void sortBelowTheLine(PurchasingAccountsPayableDocument document);
    
    /**
     * Remove items that have not been "entered" which means no data has been added to them so no more processing needs to continue
     * on these items.
     * 
     * @param apDocument  AccountsPayableDocument which contains list of items to be reviewed
     */
    public void deleteUnenteredItems(PurapItemOperations document);
    
    /**
     * Saves the document without doing validation by invoking the saveDocument method of documentService.
     * 
     * @param document The purchase order document to be saved.
     */
    public void saveDocumentNoValidation(Document document);
    
    /**
     * Determines if a document is in a specified node.
     * 
     * @param document
     * @param nodeName
     * @return
     */
    public boolean isDocumentStoppedInRouteNode(PurchasingAccountsPayableDocument document, String nodeName);
    
    /**
     * Returns true if the current date falls within the allowed range to encumber in the next fiscal year.
     * 
     * @return
     */
    public boolean allowEncumberNextFiscalYear();

    /**
     * Returns a list of fiscal years that can be selected from on the document (built for Requisition and Purchase Order).
     * Typically only the current year is returned. However, if the current date falls within the allowed range to encumber in the
     * next fiscal year, the current year and the next current year is returned.
     * 
     * @return List<Integer>
     */
   public List<Integer> getAllowedFiscalYears();
   
   /**
    * Returns true if today is within the APO allowed date range. If the difference between today and this years closing date is
    * less than or equal to the number of days allowed for APO.
    * 
    * @return boolean
    */
   public boolean isTodayWithinApoAllowedRange();

   /**
    * This method can be used to retrieve a list of parameters that
    * match the given fieldValues criteria. You could also specify the "like"
    * criteria in the Map.
    * 
    * @param   fieldValues The Map containing the key value pairs to be used 
    *                      to build the criteria.
    * @return  List of Parameters that match the criteria.
    */
   public List<Parameter> getParametersGivenLikeCriteria(Map<String, String> fieldValues);
   
   /**
    * Calculates sales or use tax for each item if sales tax enabled and item is taxable.
    * 
    * @param purapDocument
    */
   public void calculateTax(PurchasingAccountsPayableDocument purapDocument);
   
   /**
    * Clears the tax from a document.  Useful when changing from use to sales
    * @param purapDocument document
    * @param useTax the whether to clear use or sales
    */
   public void clearTax(PurchasingAccountsPayableDocument purapDocument, boolean useTax);
   
   /**
    * Updates the use tax field, clearing old values if it has changed
    * @param purapDocument document
    * @param newUseTaxIndicatorValue useTaxIndicator to change to
    */
   public void updateUseTaxIndicator(PurchasingAccountsPayableDocument purapDocument, boolean newUseTaxIndicatorValue);
}

