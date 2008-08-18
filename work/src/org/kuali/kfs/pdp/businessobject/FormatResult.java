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
/*
 * Created on Aug 17, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;


/**
 * 
 */
public class FormatResult extends TransientBusinessObjectBase implements Comparable {
    private Integer procId;
    private boolean pymtAttachment;
    private boolean pymtSpecialHandling;
    private boolean processImmediate;
    private CustomerProfile cust;
    private int payments;
    private BigDecimal amount;
    private DisbursementType disbursementType;
    private int beginDisbursementNbr;
    private int endDisbursementNbr;
    private String sortGroup = null;

    public FormatResult() {
        super();
        amount = new BigDecimal(0);
        payments = 0;
    }

    public FormatResult(Integer p, CustomerProfile c) {
        procId = p;
        cust = c;
        amount = new BigDecimal(0);
        payments = 0;
    }

    public String getSortGroupId() {
        if (sortGroup == null) {
            if (isProcessImmediate()) {
                return "B";
            }
            else if (isPymtSpecialHandling()) {
                return "C";
            }
            else if (isPymtAttachment()) {
                return "D";
            }
            else {
                return "E";
            }
        }
        else {
            return sortGroup;
        }
    }

    public String getSortGroupName() {
        String sortGroup = getSortGroupId();
        if ("B".equals(sortGroup)) {
            return "Immediate";
        }
        else if ("C".equals(sortGroup)) {
            return "Special Handling";
        }
        else if ("D".equals(sortGroup)) {
            return "Attachment";
        }
        else {
            return "Other";
        }
    }

    public String getSortGroupOverride() {
        return sortGroup;
    }

    public void setSortGroupOverride(String sortGroup) {
        this.sortGroup = sortGroup;
    }

    public boolean isProcessImmediate() {
        return processImmediate;
    }

    public void setProcessImmediate(boolean processImmediate) {
        this.processImmediate = processImmediate;
    }

    public boolean isPymtAttachment() {
        return pymtAttachment;
    }

    public void setPymtAttachment(boolean pymtAttachment) {
        this.pymtAttachment = pymtAttachment;
    }

    public boolean isPymtSpecialHandling() {
        return pymtSpecialHandling;
    }

    public void setPymtSpecialHandling(boolean pymtSpecialHandling) {
        this.pymtSpecialHandling = pymtSpecialHandling;
    }

    public int getBeginDisbursementNbr() {
        return beginDisbursementNbr;
    }

    public void setBeginDisbursementNbr(int beginDisbursementNbr) {
        this.beginDisbursementNbr = beginDisbursementNbr;
    }

    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    public int getEndDisbursementNbr() {
        return endDisbursementNbr;
    }

    public void setEndDisbursementNbr(int endDisbursementNbr) {
        this.endDisbursementNbr = endDisbursementNbr;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public CustomerProfile getCust() {
        return cust;
    }

    public void setCust(CustomerProfile cust) {
        this.cust = cust;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public Integer getProcId() {
        return procId;
    }

    public void setProcId(Integer procId) {
        this.procId = procId;
    }

    public String getSortString() {
        StringBuffer sb = new StringBuffer();
        if (getDisbursementType() != null) {
            if ("CHCK".equals(getDisbursementType().getCode())) {
                sb.append("B");
            }
            else {
                sb.append("A");
            }
        }
        else {
            sb.append("A");
        }
        sb.append(getSortGroupId());
        sb.append(cust.getChartCode());
        sb.append(cust.getOrgCode());
        sb.append(cust.getSubUnitCode());
        return sb.toString();
    }

    public int compareTo(Object a) {
        FormatResult f = (FormatResult) a;

        return this.getSortString().compareTo(f.getSortString());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FormatResult)) {
            return false;
        }
        FormatResult o = (FormatResult) obj;
        return new EqualsBuilder().append(procId, o.getProcId()).append(getSortGroupId(), o.getSortGroupId()).append(cust, o.getCust()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(7, 3).append(procId).append(getSortGroupId()).append(cust).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("procId", procId).append("sortGroupId", getSortGroupId()).append("cust", cust).toString();
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        
        m.put("procId", this.procId);
        
        return m;
    }
}
