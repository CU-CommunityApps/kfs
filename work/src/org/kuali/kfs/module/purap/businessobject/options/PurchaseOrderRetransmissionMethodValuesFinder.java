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
package org.kuali.kfs.module.purap.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapConstants.POTransmissionMethods;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderTransmissionMethod;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;

/**
 * Value Finder for Purchase Order Retransmission Methods.
 */
public class PurchaseOrderRetransmissionMethodValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Purchase Order Retransmission Methods.
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        Collection<PurchaseOrderTransmissionMethod> codes = boService.findAll(PurchaseOrderTransmissionMethod.class);
        String retransmitTypes = SpringContext.getBean(ParameterService.class).getParameterValue(PurchaseOrderDocument.class,PurapParameterConstants.PURAP_PO_RETRANSMIT_TRANSMISSION_METHOD_TYPES);
        List labels = new ArrayList();
        if (retransmitTypes != null){
            for (PurchaseOrderTransmissionMethod purchaseOrderTransmissionMethod : codes) {
                if (StringUtils.contains(retransmitTypes,
                                         StringUtils.left(purchaseOrderTransmissionMethod.getPurchaseOrderTransmissionMethodCode(),4))){
                    labels.add(new KeyLabelPair(purchaseOrderTransmissionMethod.getPurchaseOrderTransmissionMethodCode(), purchaseOrderTransmissionMethod.getPurchaseOrderTransmissionMethodDescription()));
                }
            }
        }
        return labels;
    }
}
