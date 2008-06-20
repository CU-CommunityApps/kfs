/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.test;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.bo.InternalBillingItem;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * DocumentTestUtils
 */
public class DocumentTestUtils {
    /**
     * @param quantity
     * @param stockDescription
     * @param stockNumber
     * @param unitAmount
     * @param unitOfMeasureCode
     * @return new InternalBillingItem initialized with the given values
     */
    public static InternalBillingItem createBillingItem(Integer quantity, String stockDescription, String stockNumber, Double unitAmount, String unitOfMeasureCode) {
        InternalBillingItem item = new InternalBillingItem();

        item.setItemQuantity(quantity);
        // item.setItemServiceDate( timestamp );
        item.setItemStockDescription(stockDescription);
        item.setItemStockNumber(stockNumber);
        item.setItemUnitAmount(new KualiDecimal(unitAmount.toString()));
        item.setUnitOfMeasureCode(unitOfMeasureCode);

        return item;
    }

    public static <D extends Document> D createDocument(DocumentService documentService, Class<D> docmentClass) throws WorkflowException {
        D document = (D) documentService.getNewDocument(docmentClass);
        document.getDocumentHeader().setExplanation("unit test created document");

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setDocumentDescription("unit test created document");

        return document;
    }
}
