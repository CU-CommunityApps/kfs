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

import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class RecurringCashTransferTransactionDocumentExceptionReportLine extends TransactionDocumentForReportLineBase {

    private String sourceKemid;
    private String transferNumber;
    private Integer targetSeqNumber;
    
    public RecurringCashTransferTransactionDocumentExceptionReportLine() {
        this("", "", "", 0);
    }
    
    
    public RecurringCashTransferTransactionDocumentExceptionReportLine(String documentType, String sourceKemid, String transferNumber, Integer targetSeqNumber) {
        this.documentType = documentType;
        this.sourceKemid = sourceKemid;
        this.transferNumber = transferNumber;
        this.targetSeqNumber = targetSeqNumber;
        
    }


    public String getSourceKemid() {
        return sourceKemid;
    }


    public void setSourceKemid(String sourceKemid) {
        this.sourceKemid = sourceKemid;
    }


    public String getTransferNumber() {
        return transferNumber;
    }


    public void setTransferNumber(String transferNumber) {
        this.transferNumber = transferNumber;
    }


    public Integer getTargetSeqNumber() {
        return targetSeqNumber;
    }


    public void setTargetSeqNumber(Integer targetSeqNumber) {
        this.targetSeqNumber = targetSeqNumber;
    }
}
