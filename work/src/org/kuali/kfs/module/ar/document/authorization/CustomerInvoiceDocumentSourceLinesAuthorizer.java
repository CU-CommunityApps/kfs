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
package org.kuali.kfs.module.ar.document.authorization;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.authorization.FinancialProcessingAccountingLineAuthorizer;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.web.AccountingLineRenderingContext;
import org.kuali.kfs.sys.document.web.AccountingLineViewAction;
import org.kuali.rice.kns.service.KualiConfigurationService;

public class CustomerInvoiceDocumentSourceLinesAuthorizer extends FinancialProcessingAccountingLineAuthorizer {

    private static final String RECALCULATE_METHOD_NAME = "recalculateSourceLine";
    private static final String RECALCULATE_LABEL = "Recalculate Source Accounting Line";
    private static final String RECALCULATE_BUTTON_IMAGE = "tinybutton-recalculate.gif";
    private static final String DISCOUNT_METHOD_NAME = "discountSourceLine";
    private static final String DISCOUNT_LABEL = "Discount a Source Accounting Line";
    private static final String DISCOUNT_BUTTON_IMAGE = "tinybutton-discount.gif";
    private static final String REFRESH_METHOD_NAME = "refreshNewSourceLine";
    private static final String REFRESH_LABEL = "Refresh New Source Line";
    private static final String REFRESH_BUTTON_IMAGE = "tinybutton-refresh.gif";

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#getActionMap(org.kuali.kfs.sys.businessobject.AccountingLine,
     *      java.lang.String, java.lang.Integer, java.lang.String)
     */
    @Override
    protected Map<String, AccountingLineViewAction> getActionMap(AccountingLineRenderingContext accountingLineRenderingContext, String accountingLinePropertyName, Integer accountingLineIndex, String groupTitle) {
        Map<String, AccountingLineViewAction> actionMap = super.getActionMap(accountingLineRenderingContext, accountingLinePropertyName, accountingLineIndex, groupTitle);

        CustomerInvoiceDetail invoiceLine = (CustomerInvoiceDetail) accountingLineRenderingContext.getAccountingLine();

        // get the images base directory
        String kfsImagesPath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString("externalizable.images.url");

        // show the Refresh button on the New Line Actions
        if (isNewLine(accountingLineIndex)) {
            actionMap.put(REFRESH_METHOD_NAME, new AccountingLineViewAction(REFRESH_METHOD_NAME, REFRESH_LABEL, kfsImagesPath + REFRESH_BUTTON_IMAGE));
        }
        else {
            // always add the Recalculate button if its in edit mode
            String groupName = super.getActionInfixForExtantAccountingLine(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName);
            String methodName = methodName(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, RECALCULATE_METHOD_NAME);
            actionMap.put(methodName, new AccountingLineViewAction(methodName, RECALCULATE_LABEL, kfsImagesPath + RECALCULATE_BUTTON_IMAGE));

            // only add the Discount button if its not a Discount Line or a Discount Line Parent
            if (showDiscountButton(invoiceLine)) {
                methodName = methodName(accountingLineRenderingContext.getAccountingLine(), accountingLinePropertyName, accountingLineIndex, DISCOUNT_METHOD_NAME);
                actionMap.put(methodName, new AccountingLineViewAction(methodName, DISCOUNT_LABEL, kfsImagesPath + DISCOUNT_BUTTON_IMAGE));
            }
        }

        return actionMap;
    }

    private boolean showDiscountButton(CustomerInvoiceDetail invoiceLine) {
        return (!invoiceLine.isDiscountLine() && !invoiceLine.isDiscountLineParent());
    }

    private String methodName(AccountingLine line, String accountingLineProperty, Integer accountingLineIndex, String methodName) {
        String infix = super.getActionInfixForExtantAccountingLine(line, accountingLineProperty);
        return methodName + ".line" + accountingLineIndex.toString() + ".anchoraccounting" + infix + "Anchor";
    }

    private boolean isNewLine(Integer accountingLineIndex) {
        return (accountingLineIndex == null || accountingLineIndex.intValue() < 0);
    }
    
    /**
     * Overridden to make:
     * 1. chart and account number read only for discount lines
     * 2. invoice item description and amount editable for recurring invoices
     * 
     * @see org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizerBase#determineFieldModifyability(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.kfs.sys.document.web.AccountingLineViewField, java.util.Map)
     */
    @Override
    public boolean determineEditPermissionOnField(AccountingDocument accountingDocument, AccountingLine accountingLine, String accountingLineCollectionProperty, String fieldName) {
        boolean canModify = super.determineEditPermissionOnField(accountingDocument, accountingLine, accountingLineCollectionProperty, fieldName);
        
        if (canModify) {
            boolean discountLineFlag = ((CustomerInvoiceDetail)accountingLine).isDiscountLine();
            if (discountLineFlag) {
                if (StringUtils.equals(fieldName, getChartPropertyName()) || StringUtils.equals(fieldName, getAccountNumberPropertyName())) 
                    canModify = false;
            }
        } // set invoice item description and amount editable for recurring invoices
        else {
            boolean recurredInvoiceIndicator = ((CustomerInvoiceDocument)accountingDocument).getRecurredInvoiceIndicator();
            if (recurredInvoiceIndicator) {
                if (StringUtils.equals(fieldName, getItemDescriptionPropertyName()) || StringUtils.equals(fieldName, getAmountPropertyName()))
                    canModify = true;
            }
                
        }
        
        return canModify;
    }

    /**
     * @return the property name of the chart field, which will be set to read only for discount lines
     */
    protected String getChartPropertyName() {
        return "chartOfAccountsCode";
    }

    /**
     * @return the property name of the account number field, which will be set to read only for discount lines
     */
    protected String getAccountNumberPropertyName() {
        return "accountNumber";
    }
    
    /**
     * @return the property name of the invoice item description field, which will be set editable for recurring invoices
     */
    protected String getItemDescriptionPropertyName() {
        return "invoiceItemDescription";
    }
    
    /**
     * @return the property name of the amount field, which will be set editable for recurring invoices
     */
    protected String getAmountPropertyName() {
        return "amount";
    }
}
