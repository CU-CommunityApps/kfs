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
package org.kuali.kfs.sys.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentTypeService;

/**
 * Default implementation of the <code>BankService</code> interface.
 * 
 * @see org.kuali.kfs.fp.service.BankService
 */
public class BankServiceImpl implements BankService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BankServiceImpl.class);
    
    private BusinessObjectService businessObjectService;
    private DocumentTypeService documentTypeService;
    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.fp.service.BankService#getByPrimaryId(java.lang.String)
     */
    public Bank getByPrimaryId(String bankCode) {
        Map primaryKeys = new HashMap();
        primaryKeys.put(KFSPropertyConstants.BANK_CODE, bankCode);

        return (Bank) businessObjectService.findByPrimaryKey(Bank.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.sys.service.BankService#getDefaultBankByDocType(java.lang.Class)
     */
    public Bank getDefaultBankByDocType(Class documentClass) {
        String documentTypeCode = documentTypeService.getDocumentTypeCodeByClass(documentClass);
        if (StringUtils.isBlank(documentTypeCode)) {
            throw new RuntimeException("Document type not found for document class: " + documentClass.getName());
        }

        if (parameterService.parameterExists(Bank.class, KFSParameterKeyConstants.DEFAULT_BANK_BY_DOCUMENT_TYPE)) {
            List<String> parmValues = parameterService.getParameterValues(Bank.class, KFSParameterKeyConstants.DEFAULT_BANK_BY_DOCUMENT_TYPE, documentTypeCode);
            if (parmValues != null && !parmValues.isEmpty()) {
                String defaultBankCode = parmValues.get(0);
                
                Bank defaultBank = this.getByPrimaryId(defaultBankCode);
                
                // check active status, if not return continuation bank if active
                if (!defaultBank.isActive() && defaultBank.getContinuationBank() != null && defaultBank.getContinuationBank().isActive()) {
                    return defaultBank.getContinuationBank();
                }
                
                return defaultBank;
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.sys.service.BankService#isBankSpecificationEnabled()
     */
    public boolean isBankSpecificationEnabled() {
        return parameterService.getIndicatorParameter(Bank.class, KFSParameterKeyConstants.ENABLE_BANK_SPECIFICATION_IND);
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the documentTypeService attribute value.
     * 
     * @param documentTypeService The documentTypeService to set.
     */
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
