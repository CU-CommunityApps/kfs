/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.integration.ar;

import java.sql.Date;

import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * Integration interface for Bill
 */
public interface AccountsReceivableBill extends ExternalizableBusinessObject, Inactivatable {


    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber();


    /**
     * Gets the billNumber attribute.
     *
     * @return Returns the billNumber.
     */
    public Long getBillNumber();

    /**
     * Gets the billDescription attribute.
     *
     * @return Returns the billDescription.
     */
    public String getBillDescription();

    /**
     * Gets the billIdentifier attribute.
     *
     * @return Returns the billIdentifier.
     */
    public Long getBillIdentifier();

    /**
     * Gets the billDate attribute.
     *
     * @return Returns the billDate.
     */
    public Date getBillDate();

    /**
     * Gets the estimatedAmount attribute.
     *
     * @return Returns the estimatedAmount.
     */
    public KualiDecimal getEstimatedAmount();

    /**
     * Gets the isBilledIndicator attribute.
     *
     * @return Returns the isBilledIndicator.
     */
    public boolean isBilledIndicator();


}
