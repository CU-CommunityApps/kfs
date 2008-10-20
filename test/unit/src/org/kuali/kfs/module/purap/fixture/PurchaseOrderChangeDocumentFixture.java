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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;


public enum PurchaseOrderChangeDocumentFixture {

    STATUS_IN_PROCESS(PurchaseOrderStatuses.IN_PROCESS), 
    STATUS_PENDING_PRINT(PurchaseOrderStatuses.PENDING_PRINT), 
    STATUS_OPEN(PurchaseOrderStatuses.OPEN), 
    STATUS_AMENDMENT(PurchaseOrderStatuses.AMENDMENT),
    STATUS_PENDING_PAYMENT_HOLD(PurchaseOrderStatuses.PENDING_PAYMENT_HOLD),
    STATUS_PENDING_REMOVE_HOLD(PurchaseOrderStatuses.PENDING_REMOVE_HOLD),
    STATUS_CLOSED(PurchaseOrderStatuses.CLOSED),
    STATUS_PENDING_CLOSE(PurchaseOrderStatuses.PENDING_CLOSE), 
    STATUS_VOID(PurchaseOrderStatuses.VOID);

    private String status;

    private PurchaseOrderChangeDocumentFixture(String status) {
        this.status = status;
    }

    public PurchaseOrderDocument generatePO() {
        PurchaseOrderDocument po = new PurchaseOrderDocument();
        try {
            po = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS.createPurchaseOrderDocument();
            po.setStatusCode(status);
            po.refreshNonUpdateableReferences();
        }
        catch (Exception e) {
            throw new RuntimeException("Problems creating new PO: " + e);
        }
        return po;
    }
}
