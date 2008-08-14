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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;


public class WriteoffCustomerInvoiceDetail extends CustomerInvoiceDetail {
    
    private CustomerInvoiceDetail postable;
    private CustomerInvoiceWriteoffDocument poster;
    private boolean isUsingOrgAcctDefaultWriteoffFAU;
    private boolean isUsingChartForWriteoff;
    
    public WriteoffCustomerInvoiceDetail(CustomerInvoiceDetail postable, CustomerInvoiceWriteoffDocument poster){
        this.postable = postable;
        this.poster = poster;
        
        String writeoffGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
        isUsingOrgAcctDefaultWriteoffFAU = ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals( writeoffGenerationOption );
        isUsingChartForWriteoff = ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_CHART.equals( writeoffGenerationOption );
        
        if( isUsingOrgAcctDefaultWriteoffFAU ){
            //if is using org account default, I already set the writeoff FAU on
            //the document, so that is needed to do is refresh the FAU objects
            this.poster.refreshReferenceObject("account");
            this.poster.refreshReferenceObject("chartOfAccounts");
            this.poster.refreshReferenceObject("subAccount");
            this.poster.refreshReferenceObject("financialObject");
            this.poster.refreshReferenceObject("financialSubObject");
            this.poster.refreshReferenceObject("project");                      
        } else {
            this.postable.refreshNonUpdateableReferences();
        }        
    }
    
    @Override
    public Account getAccount() {
        if ( isUsingOrgAcctDefaultWriteoffFAU ){
            return poster.getAccount();
        } else {
            return postable.getAccount();
        }
    }
   
   @Override
    public String getAccountNumber() {
        if ( isUsingOrgAcctDefaultWriteoffFAU ){
            return poster.getAccountNumber();
        } else {
            return postable.getAccountNumber();
        }
    }

   @Override
    public KualiDecimal getAmount() {
        return postable.getOpenAmount();
    }

   @Override
    public String getChartOfAccountsCode() {
        if ( isUsingOrgAcctDefaultWriteoffFAU ){
            return poster.getChartOfAccountsCode();
        } else {
            return postable.getChartOfAccountsCode();
        }        
    }

   @Override
    public String getDocumentNumber() {
        return postable.getDocumentNumber();
    }

   @Override
    public String getFinancialDocumentLineDescription() {
        return postable.getFinancialDocumentLineDescription();
    }

   @Override
   public String getFinancialObjectCode() {
       if ( isUsingOrgAcctDefaultWriteoffFAU ){
           return poster.getFinancialObjectCode();
       } else if ( isUsingChartForWriteoff ) {
           return SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_OBJECT_CODE_BY_CHART, this.getChartOfAccountsCode() );
       } else {
           return postable.getAccountsReceivableObjectCode();
       }   
   }

  @Override
   public ObjectCode getObjectCode() {
      if ( isUsingOrgAcctDefaultWriteoffFAU ){
          return poster.getFinancialObject();
      } else if (isUsingChartForWriteoff) {
          //return postable.getChart().getFinAccountsPayableObject();
          //TODO change which object code is returned
          return SpringContext.getBean(ObjectCodeService.class).getByPrimaryIdForCurrentYear(postable.getChartOfAccountsCode(), "5105");
      } else {
          return postable.getAccountsReceivableObject();
      }
   }
  
  @Override
  public String getFinancialSubObjectCode() {
      return GENERAL_LEDGER_PENDING_ENTRY_CODE.getBlankFinancialSubObjectCode(); 
  }

   @Override
    public String getOrganizationReferenceId() {
        if ( isUsingOrgAcctDefaultWriteoffFAU ){
            return poster.getOrganizationReferenceIdentifier();
        } else {
            return postable.getOrganizationReferenceId();
        }
    }

   @Override
    public String getProjectCode() {
        if ( isUsingOrgAcctDefaultWriteoffFAU ){
            return poster.getProjectCode();
        } else {
            return postable.getProjectCode();
        }
    }

   @Override
    public String getSubAccountNumber() {
        if ( isUsingOrgAcctDefaultWriteoffFAU ){
            return poster.getSubAccountNumber();
        } else {
            return postable.getSubAccountNumber();
        }
    }
}
