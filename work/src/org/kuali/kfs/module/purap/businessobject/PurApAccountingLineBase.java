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
package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.SourceAccountingLine;

/**
 * Purap Accounting Line Base Business Object.
 */
public abstract class PurApAccountingLineBase extends SourceAccountingLine implements PurApAccountingLine, Comparable {

    protected Integer accountIdentifier;
    private Integer itemIdentifier;
    private BigDecimal accountLinePercent;
    private KualiDecimal alternateAmountForGLEntryCreation; //not stored in DB; needed for disencumbrances and such

    public Integer getAccountIdentifier() {
        return accountIdentifier;
    }

    public void setAccountIdentifier(Integer requisitionAccountIdentifier) {
        this.accountIdentifier = requisitionAccountIdentifier;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer requisitionItemIdentifier) {
        this.itemIdentifier = requisitionItemIdentifier;
    }

    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }

    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    }

    /**
     * @see org.kuali.module.purap.bo.PurApAccountingLine#isEmpty()
     */
    public boolean isEmpty() {
        return !(StringUtils.isNotEmpty(getAccountNumber()) || 
                 StringUtils.isNotEmpty(getChartOfAccountsCode()) || 
                 StringUtils.isNotEmpty(getFinancialObjectCode()) || 
                 StringUtils.isNotEmpty(getFinancialSubObjectCode()) || 
                 StringUtils.isNotEmpty(getOrganizationReferenceId()) || 
                 StringUtils.isNotEmpty(getProjectCode()) || 
                 StringUtils.isNotEmpty(getSubAccountNumber()) ||
                 ObjectUtils.isNotNull(getAccountLinePercent()));
    }

    /**
     * @see org.kuali.module.purap.bo.PurApAccountingLine#createBlankAmountsCopy()
     */
    public PurApAccountingLine createBlankAmountsCopy() {
        PurApAccountingLine newAccount = (PurApAccountingLine)ObjectUtils.deepCopy(this);
        newAccount.setAccountLinePercent(BigDecimal.ZERO);
        newAccount.setAmount(KualiDecimal.ZERO);
        return newAccount;
    }


    /**
     * @see org.kuali.module.purap.bo.PurApAccountingLine#accountStringsAreEqual(org.kuali.kfs.bo.SourceAccountingLine)
     */
    public boolean accountStringsAreEqual(SourceAccountingLine accountingLine) {
        // TODO PURAP - need more fields for comparison or not? - look at org.kuali.kfs.bo.AccountingLineBase#getValuesMap()
        if (accountingLine == null) { return false;}
        return new EqualsBuilder()
          .append(getChartOfAccountsCode(), accountingLine.getChartOfAccountsCode())
          .append(getAccountNumber(),accountingLine.getAccountNumber())
          .append(getSubAccountNumber(),accountingLine.getSubAccountNumber())
          .append(getFinancialObjectCode(),accountingLine.getFinancialObjectCode())
          .append(getFinancialSubObjectCode(),accountingLine.getFinancialSubObjectCode())
          .append(getProjectCode(),accountingLine.getProjectCode())
          .append(getOrganizationReferenceId(),accountingLine.getOrganizationReferenceId())
//          .append(getReferenceOriginCode(),accountingLine.getReferenceOriginCode())
//          .append(getReferenceNumber(),accountingLine.getReferenceNumber())
//          .append(getReferenceTypeCode(),accountingLine.getReferenceTypeCode())
          .isEquals();
    }
    
    public boolean accountStringsAreEqual(PurApAccountingLine accountingLine) {
        return accountStringsAreEqual((SourceAccountingLine)accountingLine);
        
    }

    /**
     * @see org.kuali.module.purap.bo.PurApAccountingLine#generateSourceAccountingLine()
     */
    public SourceAccountingLine generateSourceAccountingLine() {
        // TODO PURAP - this method needs to copy any account field we need to display 
        //              and its fields should probably match method 'accountStringsAreEqual' above
        SourceAccountingLine sourceLine = new SourceAccountingLine();
        sourceLine.setChartOfAccountsCode(getChartOfAccountsCode());
        sourceLine.setAccountNumber(getAccountNumber());
        sourceLine.setSubAccountNumber(getSubAccountNumber());
        sourceLine.setFinancialObjectCode(getFinancialObjectCode());
        sourceLine.setFinancialSubObjectCode(getFinancialSubObjectCode());
        sourceLine.setProjectCode(getProjectCode());
        sourceLine.setOrganizationReferenceId(getOrganizationReferenceId());
        sourceLine.setAmount(getAmount());
        return sourceLine;
    }
    
    /**
     * @see org.kuali.kfs.bo.AccountingLineBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chart", getChartOfAccountsCode());
        m.put("account", getAccountNumber());
        m.put("objectCode", getFinancialObjectCode());
        m.put("subAccount", getSubAccountNumber());
        m.put("subObjectCode", getFinancialSubObjectCode());
        m.put("projectCode", getProjectCode());
        m.put("orgRefId", getOrganizationReferenceId());

        return m;
    }

    public int compareTo(Object arg0) {
        if (arg0 instanceof PurApAccountingLine) {
            PurApAccountingLine account = (PurApAccountingLine) arg0;
            return this.getString().compareTo(account.getString());
        }
        return -1;
    }

    public String getString() {
        return getChartOfAccountsCode() + "~" + getAccountNumber() + "~" + getSubAccountNumber() + "~" + 
            getFinancialObjectCode() + "~" + getFinancialSubObjectCode() + "~" + getProjectCode() + "~" + getOrganizationReferenceId();
    }

    public KualiDecimal getAlternateAmountForGLEntryCreation() {
        return alternateAmountForGLEntryCreation;
    }

    public void setAlternateAmountForGLEntryCreation(KualiDecimal alternateAmount) {
        this.alternateAmountForGLEntryCreation = alternateAmount;
    }

    /**
     * @see org.kuali.kfs.bo.AccountingLineBase#getSequenceNumber()
     */
    @Override
    public Integer getSequenceNumber() {
        return this.getAccountIdentifier();
    }
}
