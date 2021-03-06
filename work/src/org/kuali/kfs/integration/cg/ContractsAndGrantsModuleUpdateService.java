/*
 * Copyright 2007-2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.integration.cg;

import java.util.Map;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Methods which allow core KFS modules to interact with the ContractsAndGrants module.
 */
public interface ContractsAndGrantsModuleUpdateService {

    /**
     * This method sets last Billed Date to award Account.
     *
     * @param criteria
     * @param invoiceStatus
     * @param lastBilledDate
     * @param invoiceDocumentStatus
     */
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, String invoiceStatus, java.sql.Date lastBilledDate, String invoiceDocumentStatus);

    /**
     * This method sets last billed Date to Award
     *
     * @param proposalNumber
     * @param lastBilledDate
     */
    public void setLastBilledDateToAward(Long proposalNumber, java.sql.Date lastBilledDate);

    /**
     * This method sets value of LOC Creation Type to Award
     *
     * @param proposalNumber
     * @param locCreationType
     */
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType);

    /**
     * This method sets amount to draw to award Account.
     *
     * @param criteria
     * @param amountToDraw
     */
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw);

    /**
     * This method sets loc review indicator to award Account.
     *
     * @param criteria
     * @param locReviewIndicator
     */
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator);

    /**
     * This method sets final billed to award Account.
     *
     * @param criteria
     * @param finalBilled
     */
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled);

    /**
     * This method sets invoice Document Status to award Account.
     *
     * @param criteria
     * @param invoiceDocumentStatus
     */
    public void setAwardAccountInvoiceDocumentStatus(Map<String, Object> criteria, String invoiceDocumentStatus);

    /**
     * This method sets final billed and last billed date to Award Account.
     *
     * @param mapKey
     * @param finalBilled
     * @param invoiceStatus
     * @param lastBilledDate
     * @param invoiceDocumentStatus
     */
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, String invoiceStatus, java.sql.Date lastBilledDate, String invoiceDocumentStatus);
}
