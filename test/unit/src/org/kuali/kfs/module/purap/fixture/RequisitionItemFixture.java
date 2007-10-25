/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License"),
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

import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.document.RequisitionDocument;

public enum RequisitionItemFixture {
    
    REQ_QTY_UNRESTRICTED_ITEM_1 (
            false,                              // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_1,  // purApItemFixture
            new RequisitionAccountingLineFixture[] {RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1}   // requisitionAccountMultiFixtures
            ),
    
    REQ_ITEM_NO_APO (
            false,                              // itemRestrictedIndicator
            PurApItemFixture.BASIC_QTY_ITEM_NO_APO,  // purApItemFixture
            new RequisitionAccountingLineFixture[] {RequisitionAccountingLineFixture.BASIC_REQ_ACCOUNT_1}   // requisitionAccountMultiFixtures
            ),
    ;
    
    private boolean itemRestrictedIndicator;
    private PurApItemFixture purApItemFixture;
    private RequisitionAccountingLineFixture[] requisitionAccountingLineFixtures;

    
    private RequisitionItemFixture(  
            boolean itemRestrictedIndicator,
            PurApItemFixture purApItemFixture,
            RequisitionAccountingLineFixture[] requisitionAccountingLineFixtures) {
        this.itemRestrictedIndicator = itemRestrictedIndicator;
        this.purApItemFixture = purApItemFixture;
        this.requisitionAccountingLineFixtures = requisitionAccountingLineFixtures;
    }

    
    public void addTo(RequisitionDocument requisitionDocument) {
        RequisitionItem item=null;
        item = (RequisitionItem)this.createRequisitionItem(purApItemFixture); 
        requisitionDocument.addItem(item);
        // iterate over the accounts
        for (RequisitionAccountingLineFixture requisitionAccountMultiFixture : requisitionAccountingLineFixtures) {
            requisitionAccountMultiFixture.addTo(item);
        }
    }
    
     /**
     * 
     * TODO: ckirschenman (would it make more sense to attach the incoming fixture to the fixture definition above?)
     * @param purApItemFixture
     * @return
     */
    public PurApItem createRequisitionItem(PurApItemFixture purApItemFixture) {
        RequisitionItem item = (RequisitionItem)purApItemFixture.createPurApItem(RequisitionItem.class);
        item.setItemRestrictedIndicator(itemRestrictedIndicator);
        return item;
    }

    /**
     * 
     * @return
     */
//    public PurApItem createRequisitionItem() {
//        return createRequisitionItem(defaultItemFixture);
//    }
//
//    
//    
//    /**
//     * 
//     * This method adds an item to a document
//     * @param document
//     * @param purApItemFixture
//     * @throws IllegalAccessException
//     * @throws InstantiationException
//     */
//    public RequisitionItem addTo(PurchasingAccountsPayableDocument document, PurApItemFixture purApItemFixture) 
//        throws IllegalAccessException, InstantiationException {
//        RequisitionItem item = (RequisitionItem)this.createRequisitionItem(purApItemFixture); 
//        document.addItem(item);
//        return item;
//        
//    }   
}
