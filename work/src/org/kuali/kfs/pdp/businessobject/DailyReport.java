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
package org.kuali.kfs.pdp.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;

public class DailyReport extends TransientBusinessObjectBase {
    private String customer;
    private BigDecimal amount;
    private Integer payments;
    private Integer payees;
    
    private PaymentGroup paymentGroup;
    
    public DailyReport() {
        payments = 0;
        payees = 0;
        amount = new BigDecimal("0");
    }

    public DailyReport(DailyReport dr) {
        this();
        customer = dr.customer;
    }

    public DailyReport(String c, BigDecimal a, Integer pm, Integer py, PaymentGroup paymentGroup) {
        this();
        customer = c;
        amount = a;
        payments = pm;
        payees = py;
        this.paymentGroup = paymentGroup;
    }

    @Override
    public String toString() {
        return customer + " " + amount + " " + payments + " " + payees;
    }

    public void addRow(DailyReport r) {
        payments = payments + r.payments;
        payees = payees + r.payees;
        amount = amount.add(r.amount);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Integer getPayees() {
        return payees;
    }

    public void setPayees(Integer payees) {
        this.payees = payees;
    }

    public Integer getPayments() {
        return payments;
    }

    public void setPayments(Integer payments) {
        this.payments = payments;
    }

    
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put("customer", this.customer);
        m.put("amount", this.amount);
        m.put("payments", this.payments);
        m.put("payees", this.payees);      
        
        return m;
    }

    public PaymentGroup getPaymentGroup() {
        return paymentGroup;
    }

    public void setPaymentGroup(PaymentGroup paymentGroup) {
        this.paymentGroup = paymentGroup;
    }

    
}
