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


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.kuali.core.document.Document;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.service.AssetService;

public class EquipmentLoanOrReturnDocumentRule extends TransactionalDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocumentRule.class);

    private static final String DATEFORMAT = "MM/dd/yyyy";
    public static final String DOCUMENT_NUMBER_PATH = "documentNumber";
    public static final String DOCUMENT_PATH = "document";
    public static final String DOC_HEADER_PATH = DOCUMENT_PATH + "." + DOCUMENT_NUMBER_PATH;
    private AssetService assetService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;

        if (getAssetService().isAssetLocked(equipmentLoanOrReturnDocument.getDocumentNumber(), equipmentLoanOrReturnDocument.getAssetHeader().getCapitalAssetNumber())) {
            return false;
        }

        boolean valid = processValidation(equipmentLoanOrReturnDocument);

        return true;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument = (EquipmentLoanOrReturnDocument) document;

        if (getAssetService().isAssetLocked(equipmentLoanOrReturnDocument.getDocumentNumber(), equipmentLoanOrReturnDocument.getAssetHeader().getCapitalAssetNumber())) {
            return false;
        }

        boolean valid = processValidation(equipmentLoanOrReturnDocument);

        return true;
    }


    private boolean processValidation(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;
        // validate if loan return date and expected loan return date is valid
        valid &= validateLoanDate(equipmentLoanOrReturnDocument);

        return true;
    }

    /**
     * Implementation of the rule that if a document has a recurring payment begin date and end date, the begin date should come
     * before the end date. In EPIC, we needed to play around with this order if the fiscal year is the next fiscal year, since we
     * were dealing just with month and day, but we don't need to do that here; we're dealing with the whole Date object.
     * 
     * @param purDocument the purchasing document to be validated
     * @return boolean false if the begin date is not before the end date.
     */
    private boolean validateLoanDate(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        boolean valid = true;
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        Date maxLoanDate = CalculateMaxLoanDate(equipmentLoanOrReturnDocument);
        Date loanDate = equipmentLoanOrReturnDocument.getLoanDate();

        Date expectReturnDate = equipmentLoanOrReturnDocument.getExpectedReturnDate();
        if (ObjectUtils.isNotNull(expectReturnDate)) {
            if ((dateTimeService.dateDiff(loanDate, expectReturnDate, false) <= 0) || (dateTimeService.dateDiff(expectReturnDate, maxLoanDate, false) <= 0)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.EquipmentLoanOrReturnDocument.LOAN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_EXPECTED_RETURN_DATE);
            }
        }

        Date loanReturnDate = equipmentLoanOrReturnDocument.getLoanReturnDate();
        if (ObjectUtils.isNotNull(loanReturnDate)) {
            if ((dateTimeService.dateDiff(loanDate, loanReturnDate, false) <= 0) || (dateTimeService.dateDiff(loanReturnDate, maxLoanDate, false) <= 0)) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(CamsPropertyConstants.EquipmentLoanOrReturnDocument.LOAN_DATE, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_INVALID_LOAN_RETURN_DATE);
            }
        }

        return valid;
    }


    private Date CalculateMaxLoanDate(EquipmentLoanOrReturnDocument equipmentLoanOrReturnDocument) {
        Date maxDate = null;
        String sLoanDate = new SimpleDateFormat(DATEFORMAT).format(equipmentLoanOrReturnDocument.getLoanDate());
        // String sLoanDate = dateTimeService.toDateString(equipmentLoanOrReturnDocument.getLoanDate()) +"";
        int sMaxLoanYear = Integer.parseInt(sLoanDate.substring(6, 10));
        sMaxLoanYear += 2;
        String maxLoanDate = sLoanDate.substring(0, 6) + sMaxLoanYear;
        try {
            maxDate = new java.sql.Date(((new SimpleDateFormat(DATEFORMAT)).parse(maxLoanDate)).getTime());
        }
        catch (ParseException pE) {
            LOG.error("EquipmentLoanOrReturnAction - convertStringToDate() error occurred.", pE);
        }

        return maxDate;
    }


    /**
     * Convenience method to append the path prefix
     */
    public TypedArrayList putError(String propertyName, String errorKey, String... errorParameters) {
        return GlobalVariables.getErrorMap().putError(DOCUMENT_PATH + "." + propertyName, errorKey, errorParameters);
    }

    public AssetService getAssetService() {
        if (this.assetService == null) {
            this.assetService = SpringContext.getBean(AssetService.class);
        }
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }
}
