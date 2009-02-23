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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants ;
import org.kuali.kfs.module.ld.LaborKeyConstants; 
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborJournalVoucherDetail;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.document.LaborJournalVoucherDocument;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * creating a list of Expense Pending entries and Benefit pending Entries
 * 
 * @param accountingDocument the given document
 * @param accountingLine the given accounting line
 * @param sequenceHelperForValidation the given sequenceHelper
 * @return true after creating a list of Expense Pending entries and Benefit pending Entries
 */
public class LaborJournalVoucherrProcessGenerateLaborLedgerPendingEntriesValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private AccountingDocument accountingDocumentForValidation;
    private GeneralLedgerPendingEntrySequenceHelper sequenceHelperForValidation;
    
    /**
     * creating a list of Expense Pending entries and Benefit pending Entries
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        AccountingDocument accountingDocument = getAccountingDocumentForValidation();
        AccountingLine accountingLine = getAccountingLineForValidation();
        GeneralLedgerPendingEntrySequenceHelper sequenceHelper = getSequenceHelperForValidation();
        
        // creating a list of Expense Pending entries and Benefit pending Entries
        return processGenerateLaborLedgerPendingEntries(accountingDocument, accountingLine, sequenceHelper);
    }

    /**
     * @param LaborLedgerPostingDocument the given labor ledger accounting document
     * @return true after creating a list of Expense Pending entries and Benefit pending Entries
     * @see org.kuali.kfs.module.ld.document.validation.impl.LaborExpenseTransferDocumentRules#processGenerateLaborLedgerPendingEntries(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument,
     *      org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateLaborLedgerPendingEntries(AccountingDocument document, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {       
        try {
            LaborJournalVoucherDocument laborJournalVoucherDocument = (LaborJournalVoucherDocument) document;
            LaborLedgerPendingEntry pendingLedgerEntry = new LaborLedgerPendingEntry();

            // populate the explicit entry
            ObjectUtil.buildObject(pendingLedgerEntry, accountingLine);
            GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
            glpeService.populateExplicitGeneralLedgerPendingEntry(laborJournalVoucherDocument, accountingLine, sequenceHelper, pendingLedgerEntry);

            // apply the labor JV specific information
            laborJournalVoucherDocument.customizeExplicitGeneralLedgerPendingEntry(accountingLine, pendingLedgerEntry);
            pendingLedgerEntry.setFinancialDocumentTypeCode(laborJournalVoucherDocument.getOffsetTypeCode());

            if (StringUtils.isBlank(((LaborJournalVoucherDetail) accountingLine).getEmplid())) {
                pendingLedgerEntry.setEmplid(LaborConstants.getDashEmplId());
            }

            if (StringUtils.isBlank(((LaborJournalVoucherDetail) accountingLine).getPositionNumber())) {
                pendingLedgerEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
            }

            laborJournalVoucherDocument.getLaborLedgerPendingEntries().add(pendingLedgerEntry);
            sequenceHelper.increment();
        }
        catch (Exception e) {
            GlobalVariables.getErrorMap().putError("Labor Journal Voucher", "Cannot add a Labor Ledger Pending Entry into the list");          
            return false;
        }

        return true;
    }
        
    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
    
    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
    
    /**
     * Gets the getSequenceHelperForValidation attribute. 
     * @return Returns the getSequenceHelperForValidation.
     */
    public GeneralLedgerPendingEntrySequenceHelper getSequenceHelperForValidation() {
        return sequenceHelperForValidation;
    }

    /**
     * Sets the getSequenceHelperForValidation attribute value.
     * @param getSequenceHelperForValidation The getSequenceHelperForValidation to set.
     */
    public void setGeneralLedgerPendingEntrySequenceHelper(GeneralLedgerPendingEntrySequenceHelper sequenceHelperForValidation) {
        this.sequenceHelperForValidation = sequenceHelperForValidation;
    }    
}
