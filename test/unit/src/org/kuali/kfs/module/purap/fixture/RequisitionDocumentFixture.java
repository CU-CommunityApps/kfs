/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.RequisitionDocument;

public enum RequisitionDocumentFixture {

    REQ_ONLY_REQUIRED_FIELDS(null,  // requisitionOrganizationReference1Text
            null,                   // requisitionOrganizationReference2Text
            null,                   // requisitionOrganizationReference3Text
            null,                   // alternate1VendorName
            null,                   // alternate2VendorName
            null,                   // alternate3VendorName
            null,                   // alternate4VendorName
            null,                   // alternate5VendorName
            null,                   // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,  // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,                 // purchasingDocumentFixture
            new RequisitionItemFixture[] {RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1}  // requisitionItemMultiFixtures
            ),
            
    REQ_NO_APO_VALID(null,  // requisitionOrganizationReference1Text
            null,                   // requisitionOrganizationReference2Text
            null,                   // requisitionOrganizationReference3Text
            null,                   // alternate1VendorName
            null,                   // alternate2VendorName
            null,                   // alternate3VendorName
            null,                   // alternate4VendorName
            null,                   // alternate5VendorName
            null,                   // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO,  // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,                 // purchasingDocumentFixture
            new RequisitionItemFixture[] {RequisitionItemFixture.REQ_ITEM_NO_APO}  // requisitionItemMultiFixtures
            ),
                                    
    REQ_APO_VALID(null,  // requisitionOrganizationReference1Text
            null,                   // requisitionOrganizationReference2Text
            null,                   // requisitionOrganizationReference3Text
            null,                   // alternate1VendorName
            null,                   // alternate2VendorName
            null,                   // alternate3VendorName
            null,                   // alternate4VendorName
            null,                   // alternate5VendorName
            null,                   // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_VALID_APO,  // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,                 // purchasingDocumentFixture
            new RequisitionItemFixture[] {RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1}  // requisitionItemMultiFixtures
            ),
            
    REQ_ALTERNATE_APO(null,  // requisitionOrganizationReference1Text
            null,                   // requisitionOrganizationReference2Text
            null,                   // requisitionOrganizationReference3Text
            null,                   // alternate1VendorName
            null,                   // alternate2VendorName
            null,                   // alternate3VendorName
            null,                   // alternate4VendorName
            null,                   // alternate5VendorName
            null,                   // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ALTERNATE_APO,  // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,                 // purchasingDocumentFixture
            new RequisitionItemFixture[] {RequisitionItemFixture.REQ_QTY_APO_ITEM_1,
                                          RequisitionItemFixture.REQ_SERVICE_APO_ITEM_1,
                                          RequisitionItemFixture.REQ_FREIGHT_ITEM_1}  // requisitionItemMultiFixtures
            ),
                            
    REQ_APO_INVALID_ALTERNATE_VENDOR_NAMES(null,  // requisitionOrganizationReference1Text
            null,                   // requisitionOrganizationReference2Text
            null,                   // requisitionOrganizationReference3Text
            "NFL Shop",             // alternate1VendorName
            "Dicks Sporting Goods", // alternate2VendorName
            null,                   // alternate3VendorName
            null,                   // alternate4VendorName
            null,                   // alternate5VendorName
            null,                   // organizationAutomaticPurchaseOrderLimit
            PurchasingAccountsPayableDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,  // purapDocumentFixture
            PurchasingDocumentFixture.REQ_ONLY_REQUIRED_FIELDS,                 // purchasingDocumentFixture
            new RequisitionItemFixture[] {RequisitionItemFixture.REQ_QTY_UNRESTRICTED_ITEM_1}  // requisitionItemMultiFixtures
            );
                    
    public final String requisitionOrganizationReference1Text;
    public final String requisitionOrganizationReference2Text;
    public final String requisitionOrganizationReference3Text;
    public final String alternate1VendorName;
    public final String alternate2VendorName;
    public final String alternate3VendorName;
    public final String alternate4VendorName;
    public final String alternate5VendorName;
    public final KualiDecimal organizationAutomaticPurchaseOrderLimit;
    private PurchasingAccountsPayableDocumentFixture purapDocumentFixture;
    private PurchasingDocumentFixture purchasingDocumentFixture;
    private RequisitionItemFixture[] requisitionItemFixtures;

    private RequisitionDocumentFixture(
            String requisitionOrganizationReference1Text,
            String requisitionOrganizationReference2Text,
            String requisitionOrganizationReference3Text,
            String alternate1VendorName,
            String alternate2VendorName,
            String alternate3VendorName,
            String alternate4VendorName,
            String alternate5VendorName,
            KualiDecimal organizationAutomaticPurchaseOrderLimit,
            PurchasingAccountsPayableDocumentFixture purapDocumentFixture,
            PurchasingDocumentFixture purchasingDocumentFixture,
            RequisitionItemFixture[] requisitionItemFixtures) {
        this.requisitionOrganizationReference1Text = requisitionOrganizationReference1Text;
        this.requisitionOrganizationReference2Text = requisitionOrganizationReference2Text;
        this.requisitionOrganizationReference3Text = requisitionOrganizationReference3Text;
        this.alternate1VendorName = alternate1VendorName;
        this.alternate2VendorName = alternate2VendorName;
        this.alternate3VendorName = alternate3VendorName;
        this.alternate4VendorName = alternate4VendorName;
        this.alternate5VendorName = alternate5VendorName;
        this.organizationAutomaticPurchaseOrderLimit = organizationAutomaticPurchaseOrderLimit;
        this.purapDocumentFixture = purapDocumentFixture;
        this.purchasingDocumentFixture = purchasingDocumentFixture;
        this.requisitionItemFixtures = requisitionItemFixtures;
    }

    public RequisitionDocument createRequisitionDocument() {
        RequisitionDocument doc = purchasingDocumentFixture.createRequisitionDocument(purapDocumentFixture);
        doc.setRequisitionOrganizationReference1Text(this.requisitionOrganizationReference1Text);
        doc.setRequisitionOrganizationReference2Text(this.requisitionOrganizationReference2Text);
        doc.setRequisitionOrganizationReference3Text(this.requisitionOrganizationReference3Text);
        doc.setAlternate1VendorName(this.alternate1VendorName);
        doc.setAlternate2VendorName(this.alternate2VendorName);
        doc.setAlternate3VendorName(this.alternate3VendorName);
        doc.setAlternate4VendorName(this.alternate4VendorName);
        doc.setAlternate5VendorName(this.alternate5VendorName);
        doc.setOrganizationAutomaticPurchaseOrderLimit(this.organizationAutomaticPurchaseOrderLimit);

        for (RequisitionItemFixture requisitionItemFixture : requisitionItemFixtures) {
            requisitionItemFixture.addTo(doc);
        }

        return doc;
    }
    
}
