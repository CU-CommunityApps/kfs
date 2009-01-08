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

import static org.kuali.kfs.sys.KFSConstants.GENERIC_CODE_PROPERTY_NAME;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_TYPE_CODE;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.fp.businessobject.VoucherSourceAccountingLine;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.LaborJournalVoucherDetail;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * Validates that a labor journal voucher document's accounting lines have valid encumbrance code 
 */
public class LaborJournalVoucherExternalEncumbranceValidation extends GenericValidation {
    private LaborJournalVoucherDetail accountingLineForValidation;
    
    /**
     * Validates that the accounting line in the labor journal voucher document for valid encumbrance code 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        LaborJournalVoucherDetail laborJournalVoucherDetail = getAccountingLineForValidation() ;
        if (!externalEncumbranceSpecificBusinessRulesValid(laborJournalVoucherDetail)) {
            result = false ;
        }
        return result ;    
    }

    /**
     * Checks whether employee id exists
     * 
     * @param laborJournalVoucherDetail laborJournalVoucherDetail line will be checked for valid encumbrance code
     * @return True if laborJournalVoucherDetail has the valid encumbrance code, false otherwise.
     */ 
    private boolean externalEncumbranceSpecificBusinessRulesValid(LaborJournalVoucherDetail laborJournalVoucherDetail) {
        boolean externalEncumbranceValid  = true ;
        
        BalanceType balanceTyp = laborJournalVoucherDetail.getBalanceTyp();
        AccountingDocumentRuleHelperService journalVoucherRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class) ;
        if (!journalVoucherRuleUtil.isValidBalanceType(balanceTyp, GENERIC_CODE_PROPERTY_NAME)) {
            externalEncumbranceValid = false ;
        }
        else if (!balanceTyp.isFinBalanceTypeEncumIndicator() && !KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(laborJournalVoucherDetail.getEncumbranceUpdateCode())) {
            externalEncumbranceValid = this.isRequiredReferenceFieldsValid(laborJournalVoucherDetail);
        }
            
        return externalEncumbranceValid ;
    }

    /**
     * This method checks that values exist in the three reference fields that are required if the balance type is set to EXTERNAL
     * ENCUMBRANCE.
     * 
     * @param accountingLine The accounting line being validated.
     * @return True if all of the required external encumbrance reference fields are valid, false otherwise.
     */
    protected boolean isRequiredReferenceFieldsValid(AccountingLine accountingLine) {
        boolean valid = true;

        if (StringUtils.isEmpty(accountingLine.getReferenceOriginCode())) {
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceNumber())) {
            valid = false;
        }
        if (StringUtils.isEmpty(accountingLine.getReferenceTypeCode())) {
            valid = false;
        }
        return valid;
    }    
    
    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public LaborJournalVoucherDetail getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(LaborJournalVoucherDetail accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}

