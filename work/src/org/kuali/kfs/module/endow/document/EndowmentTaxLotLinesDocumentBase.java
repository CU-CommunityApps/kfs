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
package org.kuali.kfs.module.endow.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineParser;
import org.kuali.kfs.module.endow.businessobject.TaxLotLine;
import org.kuali.rice.kns.util.KualiDecimal;

public abstract class EndowmentTaxLotLinesDocumentBase extends EndowmentSecurityDetailsDocumentBase implements EndowmentTaxLotLinesDocument {


    private List<TaxLotLine> taxLotLines;

    public List<TaxLotLine> getTaxLotLines() {
        return taxLotLines;
    }

    public void setTaxLotLines(List<TaxLotLine> taxLotLines) {
        this.taxLotLines = taxLotLines;
    }

    public EndowmentTaxLotLinesDocumentBase() {
        super();
        taxLotLines = new ArrayList<TaxLotLine>();
    }

}
