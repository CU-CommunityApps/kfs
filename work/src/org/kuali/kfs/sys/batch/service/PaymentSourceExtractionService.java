/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.document.PaymentSource;

/**
 *
 * This service interface defines the methods that a PaymentSourceExtractionService implementation must provide.
 *
 */
public interface PaymentSourceExtractionService {

    /**
     * Extract all disbursement vouchers that need to be paid from the database and prepares them for payment.
     *
     * @return True if the extraction of payments is successful, false if not.
     */
    public boolean extractPayments();

    /**
     * Pulls all disbursement voucher which pay checks and which are marked as "immediate payment" from the database and builds payment information for them
     */
    public void extractImmediatePayments();

    /**
     * Creates a batch payment for a single disbursement voucher
     * @param disbursementVoucher the voucher to immediately extract
     */
    public abstract void extractSingleImmediatePayment(PaymentSource paymentSource);

    /**
     * Extracts the DV as immediate payment upon user's request after it routes to FINAL.
     */
    public void extractDvAsImmediatePayment(DisbursementVoucherDocument dvDoc);

}
