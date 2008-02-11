/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.ar.rules;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.rule.AddCustomerInvoiceDetailRule;

public class CustomerInvoiceDocumentRule extends AccountingDocumentRuleBase implements AddCustomerInvoiceDetailRule<AccountingDocument> {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentRule.class);

    private CustomerInvoiceDocument document = null;

    public boolean isDebit(AccountingDocument financialDocument, AccountingLine accountingLine) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean success = true;
        document = (CustomerInvoiceDocument) approveEvent.getDocument();
        success &= defaultExistenceChecks(document);
        success &= super.processCustomApproveDocumentBusinessRules(approveEvent);
        return success;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document doc) {
        boolean success = true;
        document = (CustomerInvoiceDocument) doc;
        success &= defaultExistenceChecks(document);
        success &= super.processCustomRouteDocumentBusinessRules(document);
        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document doc) {
        boolean success = true;
        document = (CustomerInvoiceDocument) doc;
        success &= defaultExistenceChecks(document);
        success &= super.processCustomSaveDocumentBusinessRules(document);
        return success;
    }


    private boolean defaultExistenceChecks(CustomerInvoiceDocument doc) {
        boolean success = true;

        success &= isValidCustomerNumber(doc);
        success &= isValidBillingChartAndOrganization(doc);
        success &= isValidInvoiceDueDate(doc);
        return success;
    }

    protected boolean isValidCustomerNumber(CustomerInvoiceDocument doc) {
        boolean success = true;
        // TODO: probably have to do some kind of customer lookup here, if it isn't a reference in OJB
        return success;
    }

    /**
     * This method returns true if invoice due date is less than or equal to 90 days from today's date because invoice due date
     * cannot be more than 90 days from invoice creation date
     * 
     * @param doc
     * @return true if invoice due date is less than or equal to 90 days from today's date
     */
    protected boolean isValidInvoiceDueDate(CustomerInvoiceDocument doc) {
        boolean success = true;

        // if invoice due date is provided
        if (doc.getInvoiceDueDate() != null) {
            Timestamp invoiceDueDateTime = new Timestamp(doc.getInvoiceDueDate().getTime());
            Timestamp todayTime = new Timestamp(new Date().getTime());
            success = DateUtils.getDifferenceInDays(todayTime, invoiceDueDateTime) <= ArConstants.VALID_NUMBER_OF_DAYS_INVOICE_DUE_DATE_PAST_INVOICE_DATE;
        }

        return success;

    }

    protected boolean isValidBillingChartAndOrganization(CustomerInvoiceDocument doc) {
        boolean success = true;

        // billbychartofaccountcode is not empty and billedbyorgcode is not empty
        doc.refreshReferenceObject("billByChartOfAccount");
        if (doc.getBillByChartOfAccount() != null) {
            doc.refreshReferenceObject("billedByOrganization");
            if (doc.getBilledByOrganization() == null) {
                // fail with bad org
                success &= false;
            }
        }
        else {
            // fail as both chart and org have to be valid
            success &= false;
        }

        return success;
    }

    protected boolean validateInvoiceItemDetails(CustomerInvoiceDocument doc) {
        // TODO: this will need to loop through each InvoiceDetail and verify that InvoiceItemCode for each is valid and exists
        boolean success = true;
        return success;
    }

    /**
     * 
     * This method checks to make sure that the Invoice Detail is valid
     * 
     * @return
     */
    protected boolean validateInvoiceItemDetail(CustomerInvoiceDetail detail) {
        boolean success = true;
        if (detail.getInvoiceItemQuantity() == null) {
            // rule failure
            success &= false;
        }
        else if (detail.getInvoiceItemQuantity().compareTo(new BigDecimal(1)) < 0) {
            // also fail
            success &= false;
        }

        if (detail.getInvoiceItemUnitPrice() == null) {
            // rule failure
            success &= false;
        }
        else if (detail.getInvoiceItemUnitPrice().compareTo(new KualiDecimal(1)) < 0) {
            // rule failure
            success &= false;
        }

        success &= isValidUnitOfMeasure(detail);
        success &= isValidAccountNumber(detail);
        return success;
    }


    protected boolean isValidUnitOfMeasure(CustomerInvoiceDetail detail) {
        boolean success = true;
        // this most likely will be a parameter service call
        if (!StringUtils.isEmpty(detail.getInvoiceItemUnitOfMeasureCode())) {
            // validate through parameter service
        }
        return success;
    }

    protected boolean isValidAccountNumber(CustomerInvoiceDetail detail) {
        boolean success = true;
        return success;
    }

    /**
     * @see org.kuali.module.ar.rule.AddCustomerInvoiceDetailRule#processAddCustomerInvoiceBusinessRules(org.kuali.kfs.document.AccountingDocument, org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public boolean processAddCustomerInvoiceBusinessRules(AccountingDocument financialDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        boolean isValid = validateCustomerInvoiceDetail(customerInvoiceDetail);

        return isValid;
    }
    
    /**
     * This method returns true if customer invoice detail passes BO data dictionary rules
     * 
     * @param customerInvoiceDetail
     * @return
     */
    private boolean validateCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail){
        // validate the specific customer invoice coming in
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(customerInvoiceDetail);

        boolean isValid = GlobalVariables.getErrorMap().isEmpty();
        
        return isValid;
    }
}
