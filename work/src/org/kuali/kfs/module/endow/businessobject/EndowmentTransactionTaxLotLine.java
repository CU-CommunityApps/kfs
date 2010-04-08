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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;

/**
 * This class provides an implementation for the Tax Lot Lines in the Endowment transactional documents.
 */
public class EndowmentTransactionTaxLotLine extends PersistableBusinessObjectBase {
    private KualiInteger lineNumber;
    private HoldingTaxLot holdingTaxLot;

    private String documentNumber;
    private Long documentLineNumber;
    private String documentLineTypeCode;
    private Long transactionHoldingLongTermNumber;
    private KualiDecimal lotUnits;
    private KualiDecimal lotHoldingCost;
    private KualiDecimal lotLongTermGainLoss;
    private KualiDecimal lotShortTermGainLoss;
    
    private EndowmentTargetTransactionLine transactionLine;
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Gets the lineNumber.
     * 
     * @return lineNumber
     */
    public KualiInteger getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the lineNumber.
     * 
     * @param lineNumber
     */
    public void setLineNumber(KualiInteger lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * Gets the holdingTaxLot.
     * 
     * @return holdingTaxLot
     */
    public HoldingTaxLot getHoldingTaxLot() {
        return holdingTaxLot;
    }

    /**
     *Sets the holdingTaxLot.
     * 
     * @param holdingTaxLot
     */
    public void setHoldingTaxLot(HoldingTaxLot holdingTaxLot) {
        this.holdingTaxLot = holdingTaxLot;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Long getDocumentLineNumber() {
        return documentLineNumber;
    }

    public void setDocumentLineNumber(Long documentLineNumber) {
        this.documentLineNumber = documentLineNumber;
    }

    public String getDocumentLineTypeCode() {
        return documentLineTypeCode;
    }

    public void setDocumentLineTypeCode(String documentLineTypeCode) {
        this.documentLineTypeCode = documentLineTypeCode;
    }

    public Long getTransactionHoldingLongTermNumber() {
        return transactionHoldingLongTermNumber;
    }

    public void setTransactionHoldingLongTermNumber(Long transactionHoldingLongTermNumber) {
        this.transactionHoldingLongTermNumber = transactionHoldingLongTermNumber;
    }

    public KualiDecimal getLotUnits() {
        return lotUnits;
    }

    public void setLotUnits(KualiDecimal lotUnits) {
        this.lotUnits = lotUnits;
    }

    public KualiDecimal getLotHoldingCost() {
        return lotHoldingCost;
    }

    public void setLotHoldingCost(KualiDecimal lotHoldingCost) {
        this.lotHoldingCost = lotHoldingCost;
    }

    public KualiDecimal getLotLongTermGainLoss() {
        return lotLongTermGainLoss;
    }

    public void setLotLongTermGainLoss(KualiDecimal lotLongTermGainLoss) {
        this.lotLongTermGainLoss = lotLongTermGainLoss;
    }

    public KualiDecimal getLotShortTermGainLoss() {
        return lotShortTermGainLoss;
    }

    public void setLotShortTermGainLoss(KualiDecimal lotShortTermGainLoss) {
        this.lotShortTermGainLoss = lotShortTermGainLoss;
    }

    public EndowmentTargetTransactionLine getTransactionLine() {
        return transactionLine;
    }

    public void setTransactionLine(EndowmentTargetTransactionLine transactionLine) {
        this.transactionLine = transactionLine;
    }

}
