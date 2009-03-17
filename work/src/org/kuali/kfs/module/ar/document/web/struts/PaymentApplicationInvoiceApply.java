/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.web.struts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentApplicationInvoiceApply implements Serializable {

    private CustomerInvoiceDocument invoice;
    private List<PaymentApplicationInvoiceDetailApply> detailApplications;
    
    private boolean quickApply;
    
    public PaymentApplicationInvoiceApply(CustomerInvoiceDocument invoice) {
        this.invoice = invoice;
        this.detailApplications = new ArrayList<PaymentApplicationInvoiceDetailApply>();
        for (CustomerInvoiceDetail invoiceDetail : invoice.getCustomerInvoiceDetailsWithoutDiscounts()) {
            this.detailApplications.add(new PaymentApplicationInvoiceDetailApply(invoiceDetail));
        }
        this.quickApply = false;
    }

    public KualiDecimal getAmountToApply() {
        KualiDecimal applyAmount = KualiDecimal.ZERO;
        for (PaymentApplicationInvoiceDetailApply detailApplication : detailApplications) {
            applyAmount = applyAmount.add(detailApplication.getAmountApplied());
        }
        return applyAmount;
    }
    
    public List<PaymentApplicationInvoiceDetailApply> getDetailApplications() {
        return detailApplications;
    }
    
    public String getDocumentNumber() {
        return invoice.getDocumentNumber();
    }
    
    public KualiDecimal getOpenAmount() {
        return invoice.getOpenAmount();
    }
    
    public boolean isQuickApply() {
        return quickApply;
    }

    public void setQuickApply(boolean quickApply) {
        this.quickApply = quickApply;
    }

    public CustomerInvoiceDocument getInvoice() {
        return invoice;
    }
    
}
