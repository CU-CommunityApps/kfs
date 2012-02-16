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
package org.kuali.kfs.module.tem.document.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.pdf.Coversheet;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Travel Reimbursement Service
 * 
 */
public interface TravelReimbursementService {

    /**
     * Locate all {@link TravelReimbursementDocument} instances with the same
     * <code>travelDocumentIdentifier</code>
     *
     * @param travelDocumentIdentifier to locate {@link TravelReimbursementDocument} instances
     * @return {@link List} of {@link TravelReimbursementDocument} instances
     */
    List<TravelReimbursementDocument> findByTravelId(String travelDocumentIdentifier) throws WorkflowException;

    /**
     * Find the {@link TravelReimbursementDocument} instance with the same <code>documentNumber</code>
     *
     * @param documentNumber to locate {@link TravelReimbursementDocument} instances
     * @return  {@link TravelReimbursementDocument}
     */
    TravelReimbursementDocument find(String documentNumber) throws WorkflowException;

    /**
     * Adds {@link PropertyChangeListener} instances to the reimbursement to abstract property changes
     *
     * @param reimbursement to add listeners to
     */
    void addListenersTo(final TravelReimbursementDocument reimbursement);

    /**
     * Use a reimbursement to create a {@link CashControlDocument}. Nothing is returned because the 
     * created document will be blanketApproved and will show up in the "relatedDocuments" section
     *
     * @param reimbursement to use for creating the {@link CashControlDocument}
     */
    void spawnCashControlDocumentFrom(final TravelReimbursementDocument reimbursement) throws WorkflowException;

    /**
     * Notification when the original trip date is changed. A note is left on the workflow document detailing
     * the date change with the message DATE_CHANGED_MESSAGE
     *
     * @param reimbursement {@link TravelReimbursementDocument} for this trip
     * @param start original start {@link Date}
     * @param end original end {@link Date}
     */
    void notifyDateChangedOn(final TravelReimbursementDocument reimbursement, final Date start, final Date end) throws Exception;

    /**
     * 
     * Checks to see if the trip date changed from the TA dates. If the dates have changed, a note is left on the 
     * workflow document detailing the date change with the message DATE_CHANGED_MESSAGE (by calling notifyDateChangedOn())
     *
     * @param travelReqDoc {@link TravelReimbursementDocument} for this trip
     * @param taDoc {@link TravelAuthorizationDocument} for this trip
     */
    void addDateChangedNote(TravelReimbursementDocument travelReqDoc, TravelAuthorizationDocument taDoc);
    
    /**
     * This method uses the values provided to build and populate a cover sheet associated with a given {@link Document}.
     * 
     * @param document {@link TravelReimbursementDocument} to generate a coversheet for 
     * @return {@link Coversheet} instance
     */
    Coversheet generateCoversheetFor(final TravelReimbursementDocument document) throws Exception;

    //void handleNewOtherExpenseDetail(final OtherExpenseDetail newOtherExpenseDetailLine, final ActualExpense newOtherExpenseLine);

    /**
     * This method creates GLPE to disencumber the funds that had already been encumbered.
     * 
     * @param taDoc The document who pending entries need to be disencumbered.
     */
    public void disencumberFunds(TravelReimbursementDocument document);
    
    /**
     * This method searches to make sure that the expense entered doesn't already exist
     * If they exist, disable them in the per diem table and notify the user.
     * @param trDocument
     *          the current doc.
     * @param actualExpense
     *          the expense in question
     */
    public void disableDuplicateExpenses(TravelReimbursementDocument trDocument, ActualExpense actualExpense);
    
    /**
     * This checks to see if the expense no longer exists, and if it doesn't, enables the expense that was disabled
     * @param trDocument
     * @param actualExpense
     */
    public void enableDuplicateExpenses(TravelReimbursementDocument trDocument, ActualExpense actualExpense);


}
