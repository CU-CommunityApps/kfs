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
package org.kuali.module.pdp.service;

import java.util.Iterator;

import org.kuali.module.pdp.bo.PaymentDetail;

public interface PaymentDetailService {
    public PaymentDetail get(Integer id);

    public PaymentDetail getDetailForEpic(String custPaymentDocNbr, String fdocTypeCode);

    /**
     * This will return an iterator of all the cancelled payment details that haven't already been processed
     * 
     * @param organization
     * @param subUnit
     * @return
     */
    public Iterator getUnprocessedCancelledDetails(String organization, String subUnit);

    /**
     * This will return an iterator of all the paid payment details that haven't already been processed
     * 
     * @param organization
     * @param subUnit
     * @return
     */
    public Iterator getUnprocessedPaidDetails(String organization, String subUnit);
}
