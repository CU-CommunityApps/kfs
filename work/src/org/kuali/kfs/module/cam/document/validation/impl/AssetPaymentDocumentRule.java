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
package org.kuali.module.cams.rules;

import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.RicePropertyConstants;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.OriginationCodeService;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.dao.AssetRetirementDao;
import org.kuali.module.cams.dao.AssetTransferDao;
import org.kuali.module.cams.document.AssetPaymentDocument;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.gl.bo.UniversityDate;

public class AssetPaymentDocumentRule extends AccountingDocumentRuleBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentDocumentRule.class);

    /*
     * This method was overrided because the asset payment only uses source and not target lines. Not gl pending entries are needed.
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean valid = this.isSourceAccountingLinesRequiredNumberForRoutingMet((AccountingDocument) document);
        valid &= validateAssetEligibilityForPayment(((AssetPaymentDocument) document).getAsset().getCapitalAssetNumber());
        return valid;
    }

    /**
     * 
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean result = true;
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) accountingLine;

        // Validating the fiscal year and month because, the object code validation needs to have this a valid fiscal year
        result &= this.validateFiscalPeriod(assetPaymentDetail.getFinancialDocumentPostingYear(), assetPaymentDetail.getFinancialDocumentPostingPeriodCode());

        // Validating document type code
        result &= this.validateDocumentType(assetPaymentDetail.getExpenditureFinancialDocumentTypeCode());

        //Validating origination code
        result &= validateOriginationCode(assetPaymentDetail.getExpenditureFinancialSystemOriginationCode());
        
        // Validating posting date which must exists in the university date table
        result &= this.validatePostedDate(assetPaymentDetail.getExpenditureFinancialDocumentPostedDate());

        return result;
    }

    /**
     * 
     * Checks the asset has pending transfer and/or retirement documents
     * 
     * @param capitalAssetNumber
     * @return boolean
     */
    private boolean validateAssetEligibilityForPayment(Long capitalAssetNumber) {
        boolean isValid = true;
        Iterator<String> pendingTransferDocuments = SpringContext.getBean(AssetTransferDao.class).getAssetPendingTransferDocuments(capitalAssetNumber);
        Iterator<String> pendingRetirementDocuments = SpringContext.getBean(AssetRetirementDao.class).getAssetPendingRetirementDocuments(capitalAssetNumber);

        List<String> pendingDocuments = new ArrayList<String>();
        while (pendingTransferDocuments.hasNext()) {
            pendingDocuments.add(pendingTransferDocuments.next());
        }

        while (pendingRetirementDocuments.hasNext()) {
            pendingDocuments.add(pendingTransferDocuments.next());
        }

        if (!pendingDocuments.isEmpty()) {
            // This error will appear at the bottom of the page in the other error section.
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX, CamsKeyConstants.Payment.ERROR_ASSET_PAYMENT_DOCS_PENDING, "Document number(s):" + pendingDocuments.toString());
            isValid = false;
        }
        return isValid;
    }


    /**
     * 
     * Validates the document type really exists
     * 
     * @param expenditureFinancialDocumentTypeCode
     * @return boolean
     */
    private boolean validateDocumentType(String expenditureFinancialDocumentTypeCode) {
        boolean result = true;
        String label;
        if (!StringUtils.isBlank(expenditureFinancialDocumentTypeCode)) {
            Map<String, Object> keyToFind = new HashMap<String, Object>();
            keyToFind.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, expenditureFinancialDocumentTypeCode);

            if (SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DocumentType.class, keyToFind) == null) {
                label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(DocumentType.class.getName()).getAttributeDefinition(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }
        }
        return result;
    }


    /**
     * 
     * Validates that origination code exists
     *  
     * @param originationCode
     * @return boolean
     */    
    private boolean validateOriginationCode(String originationCode) {
        boolean result = true;
        if (!StringUtils.isBlank(originationCode)) {
            if (SpringContext.getBean(OriginationCodeService.class).getByPrimaryKey(originationCode) == null) {
                String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(OriginationCode.class.getName()).getAttributeDefinition(RicePropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE).getLabel();
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.ORIGINATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
                result = false;
            }
        }
        return result;        
    }

    /**
     * 
     * Validates the existance of a valid fiscal year and fiscal month in the university date table
     * 
     * @param fiscalYear
     * @param fiscalMonth
     * @return boolean
     */
    public boolean validateFiscalPeriod(Integer fiscalYear, String fiscalMonth) {
        boolean result = true;

        Map<String, Object> keyToFind = new HashMap<String, Object>();
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_FISCAL_ACCOUNTING_PERIOD, fiscalMonth);
        if (SpringContext.getBean(BusinessObjectService.class).countMatching(UniversityDate.class, keyToFind) == 0) {
            String labelFiscalYear = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR).getLabel();
            String labelFiscalMonth = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH).getLabel();

            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_YEAR, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalYear);
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_FISCAL_MONTH, KFSKeyConstants.ERROR_EXISTENCE, labelFiscalMonth);
            result = false;
        }
        return result;
    }


    /**
     * 
     * This method validates the given posted date exists in the university date table and that the posted date is not greater than
     * the current fiscal year
     * 
     * @param postedDate
     * @return boolean
     */
    public boolean validatePostedDate(Date postedDate) {
        Calendar documentPostedDate = Calendar.getInstance();
        documentPostedDate.setTime(postedDate);

        boolean result = true;
        Map<String, Object> keyToFind = new HashMap<String, Object>();
        keyToFind.put(KFSPropertyConstants.UNIVERSITY_DATE, postedDate);
        if (SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(UniversityDate.class, keyToFind) == null) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(AssetPaymentDetail.class.getName()).getAttributeDefinition(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE).getLabel();
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, KFSKeyConstants.ERROR_EXISTENCE, label);
            result = false;
        }

        // Validating the posted document date is not greater than the current fiscal year.
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        if (documentPostedDate.get(Calendar.YEAR) > currentFiscalYear.intValue()) {
            GlobalVariables.getErrorMap().putError(CamsPropertyConstants.AssetPaymentDetail.DOCUMENT_POSTING_DATE, CamsKeyConstants.Payment.ERROR_INVALID_DOC_POST_DATE);
            result = false;
        }
        return result;
    }
}