/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.service;

import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;

/**
 * Defines methods for sending AR emails.
 */
public interface AREmailService {

    /**
     * This method is used to send emails to the agency
     *
     * @param invoices
     * @throws AddressException
     * @throws MessagingException
     */
    public void sendInvoicesViaEmail(List<ContractsGrantsInvoiceDocument> invoices) throws AddressException, MessagingException;

    /**
     * Send email for upcoming milestones for Award
     */
    public void sendEmail(List<Milestone> milestones, ContractsAndGrantsBillingAward award);

}
