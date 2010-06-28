/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;

public class HoldingHistoryValueAdjustmentDocumentForm extends KualiTransactionalDocumentFormBase {

    public HoldingHistoryValueAdjustmentDocumentForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        //use the DataDictionaryService service to get the document type name...
        return SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(HoldingHistoryValueAdjustmentDocument.class);
    }
    
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(this.getHoldingHistoryValueAdjustmentDocument());
    }
    
    /**
     * This method gets the Holding History Value Adjustment document
     * 
     * @return the HoldingHistoryValueAdjustmentDocument
     */
    public HoldingHistoryValueAdjustmentDocument getHoldingHistoryValueAdjustmentDocument() {
        return (HoldingHistoryValueAdjustmentDocument) getDocument();
    }
    
    
}
