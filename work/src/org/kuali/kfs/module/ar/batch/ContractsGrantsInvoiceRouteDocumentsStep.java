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
package org.kuali.kfs.module.ar.batch;

import java.util.Date;

import org.kuali.kfs.module.ar.batch.service.ContractsGrantsInvoiceCreateDocumentService;
import org.kuali.kfs.sys.batch.AbstractStep;

/**
 * This step will call a service method to route cgin documents that are in 'I' status.
 */
public class ContractsGrantsInvoiceRouteDocumentsStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsInvoiceRouteDocumentsStep.class);
    protected ContractsGrantsInvoiceCreateDocumentService cgInvoiceDocumentCreateService;

    /**
     * @see org.kuali.kfs.sys.batch.Step#execute(java.lang.String, java.util.Date)
     */
    @Override
    public boolean execute(String jobName, Date jobRunDate) {

        try {

            Thread.sleep(300000);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return cgInvoiceDocumentCreateService.routeContractsGrantsInvoiceDocuments();
    }

    /**
     * Sets the cgInvoiceDocumentCreateService attribute value.
     *
     * @param cgInvoiceDocumentCreateService The cgInvoiceDocumentCreateService to set.
     */
    public void setCgInvoiceDocumentCreateService(ContractsGrantsInvoiceCreateDocumentService cgInvoiceDocumentCreateService) {
        this.cgInvoiceDocumentCreateService = cgInvoiceDocumentCreateService;
    }


}
