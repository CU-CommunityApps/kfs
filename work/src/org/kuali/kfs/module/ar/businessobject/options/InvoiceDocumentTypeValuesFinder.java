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
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;

/**
 * This class returns list of string key value pairs for Invoice Document Types.
 */
public class InvoiceDocumentTypeValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List<KeyLabelPair> getKeyValues() {
        List<KeyLabelPair> activeLabels = new ArrayList<KeyLabelPair>();
        activeLabels.add(new KeyLabelPair(ArConstants.INV_DOCUMENT_TYPE, ArConstants.INV_DOCUMENT_DESCRIPTION));
        activeLabels.add(new KeyLabelPair(ArConstants.CGIN_DOCUMENT_TYPE, ArConstants.CGIN_DOCUMENT_DESCRIPTION));

        return activeLabels;
    }
}
