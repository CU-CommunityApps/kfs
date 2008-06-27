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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class CustomerInvoiceDetailDiscountGreaterThanParentValidation extends GenericValidation {

    private CustomerInvoiceDetail discountCustomerInvoiceDetail;
    private CustomerInvoiceDocument customerInvoiceDocument;
    private CustomerInvoiceDetailService customerInvoiceDetailService;

    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;

        CustomerInvoiceDetail parentCustomerInvoiceDetail = discountCustomerInvoiceDetail.getParentDiscountCustomerInvoiceDetail();

        if (ObjectUtils.isNotNull(parentCustomerInvoiceDetail)) {

            // make a copy to not mess up the existing parent reference
            CustomerInvoiceDetail copyOfParentCustomerInvoiceDetail = new CustomerInvoiceDetail();
            copyOfParentCustomerInvoiceDetail.setInvoiceItemQuantity(parentCustomerInvoiceDetail.getInvoiceItemQuantity());
            copyOfParentCustomerInvoiceDetail.setInvoiceItemUnitPrice(parentCustomerInvoiceDetail.getInvoiceItemUnitPrice());

            // update amounts
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDocument, copyOfParentCustomerInvoiceDetail);
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDocument, discountCustomerInvoiceDetail);

            // return true if abs(discount line amount) IS NOT greater than parent line
            if (discountCustomerInvoiceDetail.getAmount().abs().isGreaterThan(copyOfParentCustomerInvoiceDetail.getAmount().abs())) {
                GlobalVariables.getErrorMap().putError(ArConstants.CustomerInvoiceDocumentFields.INVOICE_ITEM_UNIT_PRICE, ArConstants.ERROR_CUSTOMER_INVOICE_DETAIL_DISCOUNT_AMOUNT_GREATER_THAN_PARENT_AMOUNT);
                success = false;
            }
        }

        return success;
    }

    public CustomerInvoiceDetailService getCustomerInvoiceDetailService() {
        return customerInvoiceDetailService;
    }


    public void setCustomerInvoiceDetailService(CustomerInvoiceDetailService customerInvoiceDetailService) {
        this.customerInvoiceDetailService = customerInvoiceDetailService;
    }

    public CustomerInvoiceDetail getDiscountCustomerInvoiceDetail() {
        return discountCustomerInvoiceDetail;
    }


    public void setDiscountCustomerInvoiceDetail(CustomerInvoiceDetail discountCustomerInvoiceDetail) {
        this.discountCustomerInvoiceDetail = discountCustomerInvoiceDetail;
    }


    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

}
