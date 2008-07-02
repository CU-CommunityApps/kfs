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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.service.ParameterService;

public class CustomerInvoiceDetailSubFundGroupReceivableValidation extends GenericValidation {

    private CustomerInvoiceDetail customerInvoiceDetail;
    private ParameterService parameterService;

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public boolean validate(AttributedDocumentEvent event) {
        customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        Account account = customerInvoiceDetail.getAccount();
        if (StringUtils.isNotEmpty(account.getSubFundGroupCode())) {
            String receivableObjectCode = parameterService.getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND, account.getSubFundGroupCode());

            if (StringUtils.isEmpty(receivableObjectCode)) {
                GlobalVariables.getErrorMap().putError(KFSConstants.SUB_FUND_GROUP_CODE_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_WITH_NO_AR_OBJ_CD, account.getSubFundGroupCode(), account.getAccountNumber());
                return false;
            }
            else {
                customerInvoiceDetail.setAccountsReceivableObjectCode(receivableObjectCode);
                customerInvoiceDetail.refreshReferenceObject("accountsReceivableObject");

                if (ObjectUtils.isNull(customerInvoiceDetail.getAccountsReceivableObject())) {
                    GlobalVariables.getErrorMap().putError(KFSConstants.SUB_FUND_GROUP_CODE_PROPERTY_NAME, ArConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_AR_OBJ_CD_IN_PARM, receivableObjectCode, account.getSubFundGroupCode(), account.getAccountNumber());
                    return false;
                }
            }
        }

        return true;
    }
    
    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }

    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }    

}
