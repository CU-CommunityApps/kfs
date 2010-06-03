/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.validation.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.runtime.parser.node.PutExecutor;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument;
import org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocumentBase;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.validation.DeleteTaxLotLineRule;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.ObjectUtils;

public class AssetDecreaseDocumentRules extends EndowmentTransactionLinesDocumentBaseRules implements DeleteTaxLotLineRule<EndowmentTaxLotLinesDocument, EndowmentTransactionTaxLotLine, EndowmentTransactionLine, Number, Number> {

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) document;
        EndowmentTransactionSecurity endowmentTransactionSecurity = assetDecreaseDocument.getSourceTransactionSecurity();

        // Validate at least one Tx was entered.
        if (!transactionLineSizeGreaterThanZero(assetDecreaseDocument, true))
            return false;

        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        if (isValid) {
            isValid &= validateSecurity(isValid, assetDecreaseDocument, true);
            isValid &= validateRegistration(isValid, assetDecreaseDocument, true);
        }

        for (int i = 0; i < assetDecreaseDocument.getSourceTransactionLines().size(); i++) {
            EndowmentTransactionLine txLine = assetDecreaseDocument.getSourceTransactionLines().get(i);

            isValid &= validateAssetDecreaseTransactionLine(false, assetDecreaseDocument, txLine, i, -1);
            if (isValid) {
                isValid &= validateTotalAmountAndUnits(assetDecreaseDocument, txLine, i);
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processAddTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public boolean processAddTransactionLineRules(EndowmentTransactionLinesDocument document, EndowmentTransactionLine line) {
        boolean isValid = true;
        AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) document;

        isValid &= validateSecurity(isValid, assetDecreaseDocument, true);
        isValid &= validateRegistration(isValid, assetDecreaseDocument, true);
        isValid &= super.processAddTransactionLineRules(document, line);

        if (isValid) {
            isValid &= validateAssetDecreaseTransactionLine(true, assetDecreaseDocument, line, -1, -1);
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionLinesDocumentBaseRules#processRefreshTransactionLineRules(org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.Number)
     */
    @Override
    public boolean processRefreshTransactionLineRules(EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine endowmentTransactionLine, Number index) {

        boolean isValid = super.processRefreshTransactionLineRules(endowmentTransactionLinesDocument, endowmentTransactionLine, index);
        if (isValid) {
            isValid &= validateAssetDecreaseTransactionLine(false, endowmentTransactionLinesDocument, endowmentTransactionLine, (Integer) index, -1);
        }
        return isValid;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.validation.impl.EndowmentTransactionalDocumentBaseRule#validateSecurityClassTypeCode(org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocument,
     *      boolean, java.lang.String)
     */
    @Override
    protected boolean validateSecurityClassTypeCode(EndowmentSecurityDetailsDocument document, boolean isSource, String classCodeType) {
        return validateSecurityClassCodeTypeNotLiability(document, true);
    }

    /**
     * Adds validations for the transaction line specific to the Asset decrease document.
     * 
     * @param isAdd
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param index
     * @return true if valid, false otherwise
     */
    protected boolean validateAssetDecreaseTransactionLine(boolean isAdd, EndowmentTransactionLinesDocument endowmentTransactionLinesDocument, EndowmentTransactionLine line, int index, int taxLotLineToDeleteIndex) {
        boolean isValid = true;
        AssetDecreaseDocument assetDecreaseDocument = (AssetDecreaseDocument) endowmentTransactionLinesDocument;
        EndowmentSourceTransactionLine targetTransactionLine = (EndowmentSourceTransactionLine) line;

        isValid &= validateSecurity(isValid, assetDecreaseDocument, true);
        isValid &= validateRegistration(isValid, assetDecreaseDocument, true);

        if (isValid) {
            isValid &= checkCashTransactionEndowmentCode(endowmentTransactionLinesDocument, targetTransactionLine, getErrorPrefix(targetTransactionLine, index));

            if (EndowConstants.TransactionSubTypeCode.CASH.equalsIgnoreCase(assetDecreaseDocument.getTransactionSubTypeCode())) 
            {
                if(endowmentTransactionLinesDocument.isErrorCorrectedDocument())
                {
                    // Validate Amount is Less than Zero.
                    isValid &= validateTransactionAmountLessThanZero(line, getErrorPrefix(targetTransactionLine, index));
                }
                else
                {
                    // Validate Greater then Zero(thus positive) value
                    isValid &= validateTransactionAmountGreaterThanZero(line, getErrorPrefix(targetTransactionLine, index));
                }
            }

            if(endowmentTransactionLinesDocument.isErrorCorrectedDocument())
            {
                // Validate Units is Less than Zero.
                isValid &= validateTransactionUnitsLessThanZero(line, getErrorPrefix(targetTransactionLine, index));
            }
            else
            {
                isValid &= validateTransactionUnitsGreaterThanZero(line, getErrorPrefix(targetTransactionLine, index));
            }

            if (isValid) {
                isValid &= validateSufficientUnits(isAdd, assetDecreaseDocument, line, index, taxLotLineToDeleteIndex);
                isValid &= validateSecurityEtranChartMatch(endowmentTransactionLinesDocument, line, getErrorPrefix(targetTransactionLine, index), true);
            }
        }

        return isValid;
    }

    /**
     * Validates that the KEMID has sufficient units in the tax lots to perform the transaction.
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @param index
     * @return true if valid, false otherwise
     */
    private boolean validateSufficientUnits(boolean isAdd, EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase, EndowmentTransactionLine line, int transLineIndex, int taxLotIndex) {
        EndowmentTransactionSecurity endowmentTransactionSecurity = getEndowmentTransactionSecurity(endowmentTransactionLinesDocumentBase, true);
        boolean isValid = true;
        List<HoldingTaxLot> holdingTaxLots = new ArrayList<HoldingTaxLot>();

        if (isAdd) {
            holdingTaxLots = SpringContext.getBean(HoldingTaxLotService.class).getAllTaxLots(line.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), line.getTransactionIPIndicatorCode());
        }
        else {
            List<EndowmentTransactionTaxLotLine> existingTransactionLines = line.getTaxLotLines();
            for (int i = 0; i < existingTransactionLines.size(); i++) {
                // don't take into account the tax lot line we are now deleting
                if (i != taxLotIndex) {

                    EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine = (EndowmentTransactionTaxLotLine) existingTransactionLines.get(i);
                    HoldingTaxLot holdingTaxLot = SpringContext.getBean(HoldingTaxLotService.class).getByPrimaryKey(line.getKemid(), endowmentTransactionSecurity.getSecurityID(), endowmentTransactionSecurity.getRegistrationCode(), endowmentTransactionTaxLotLine.getTransactionHoldingLotNumber(), line.getTransactionIPIndicatorCode());

                    if (ObjectUtils.isNotNull(holdingTaxLot)) {
                        holdingTaxLots.add(holdingTaxLot);
                    }
                }
            }
        }

        BigDecimal totalTaxLotsUnits = BigDecimal.ZERO;

        if (holdingTaxLots != null && holdingTaxLots.size() > 0) {
            for (HoldingTaxLot holdingTaxLot : holdingTaxLots) {
                totalTaxLotsUnits = totalTaxLotsUnits.add(holdingTaxLot.getUnits());
            }
        }

        BigDecimal lineUnits = null;
        if(endowmentTransactionLinesDocumentBase.isErrorCorrectedDocument())
            lineUnits = line.getTransactionUnits().bigDecimalValue().negate();
        else
            lineUnits = line.getTransactionUnits().bigDecimalValue();
        
        if (lineUnits.compareTo(totalTaxLotsUnits) == 1) {
            isValid = false;
            putFieldError(getErrorPrefix(line, transLineIndex) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ASSET_DECREASE_INSUFFICIENT_UNITS);
        }
        return isValid;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.validation.DeleteTaxLotLineRule#processDeleteTaxLotLineRules(org.kuali.kfs.module.endow.document.EndowmentTaxLotLinesDocument,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine,
     *      org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine, java.lang.Number)
     */
    public boolean processDeleteTaxLotLineRules(EndowmentTaxLotLinesDocument endowmentTaxLotLinesDocument, EndowmentTransactionTaxLotLine taxLotLine, EndowmentTransactionLine transactionLine, Number index, Number taxLotLineIndex) {
        boolean isValid = true;
        isValid &= validateTransactionLine(endowmentTaxLotLinesDocument, transactionLine, (Integer) index);
        if (isValid) {
            isValid &= validateAssetDecreaseTransactionLine(false, endowmentTaxLotLinesDocument, transactionLine, (Integer) index, (Integer) taxLotLineIndex);
        }
        return isValid;
    }

    /**
     * Validates the the amount and units in the transaction line match the total cost and units in the associated tax lot lines.
     * 
     * @param endowmentTransactionLinesDocumentBase
     * @param line
     * @return true if valid, false otherwise
     */
    private boolean validateTotalAmountAndUnits(EndowmentTransactionLinesDocumentBase endowmentTransactionLinesDocumentBase, EndowmentTransactionLine transactionLine, int index) {
        boolean isValid = true;

        BigDecimal lineUnits = null;
        BigDecimal lineAmount = null;
        if(endowmentTransactionLinesDocumentBase.isErrorCorrectedDocument())
        {
            lineUnits = transactionLine.getTransactionUnits().bigDecimalValue().negate();
            lineAmount= transactionLine.getTransactionAmount().bigDecimalValue().negate();
        }
        else
        {
            lineUnits = transactionLine.getTransactionUnits().bigDecimalValue();
            lineAmount= transactionLine.getTransactionAmount().bigDecimalValue().negate();
        }
        
        if (EndowConstants.TransactionSubTypeCode.NON_CASH.equalsIgnoreCase(endowmentTransactionLinesDocumentBase.getTransactionSubTypeCode())) {

            List<EndowmentTransactionTaxLotLine> taxLots = transactionLine.getTaxLotLines();
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal totalUnits = BigDecimal.ZERO;

            if (taxLots != null && taxLots.size() > 0) {
                for (EndowmentTransactionTaxLotLine taxLotLine : taxLots) {
                    totalAmount = totalAmount.add(taxLotLine.getLotHoldingCost());
                    totalUnits = totalUnits.add(taxLotLine.getLotUnits());
                }
            }

            if (lineAmount.compareTo(totalAmount.negate()) != 0) {
                isValid = false;
                putFieldError(getErrorPrefix(transactionLine, index) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ASSET_DECREASE_TOTAL_AMOUNT_DOES_NOT_MATCH);
            }
            if (lineUnits.compareTo(totalUnits.negate()) != 0) {
                isValid = false;
                putFieldError(getErrorPrefix(transactionLine, index) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ASSET_DECREASE_TOTAL_UNITS_DO_NOT_MATCH);
            }
        }

        if (EndowConstants.TransactionSubTypeCode.CASH.equalsIgnoreCase(endowmentTransactionLinesDocumentBase.getTransactionSubTypeCode())) {

            List<EndowmentTransactionTaxLotLine> taxLots = transactionLine.getTaxLotLines();
            BigDecimal totalAmount = BigDecimal.ZERO;
            BigDecimal totalUnits = BigDecimal.ZERO;

            if (taxLots != null && taxLots.size() > 0) {
                for (EndowmentTransactionTaxLotLine taxLotLine : taxLots) {
                    totalAmount = totalAmount.add(taxLotLine.getLotHoldingCost().negate());

                    if (taxLotLine.getLotLongTermGainLoss() != null) {
                        totalAmount = totalAmount.add(taxLotLine.getLotLongTermGainLoss());
                    }

                    if (taxLotLine.getLotShortTermGainLoss() != null) {
                        totalAmount = totalAmount.add(taxLotLine.getLotShortTermGainLoss());
                    }

                    totalUnits = totalUnits.add(taxLotLine.getLotUnits());
                }
            }

            if (lineAmount.compareTo(totalAmount) != 0) {
                isValid = false;
                putFieldError(getErrorPrefix(transactionLine, index) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_AMOUNT, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ASSET_DECREASE_TOTAL_AMOUNT_DOES_NOT_MATCH);
            }
            if (lineUnits.compareTo(totalUnits.negate()) != 0) {
                isValid = false;
                putFieldError(getErrorPrefix(transactionLine, index) + EndowPropertyConstants.TRANSACTION_LINE_TRANSACTION_UNITS, EndowKeyConstants.EndowmentTransactionDocumentConstants.ERROR_ASSET_DECREASE_TOTAL_UNITS_DO_NOT_MATCH);
            }
        }

        return isValid;
    }

}
