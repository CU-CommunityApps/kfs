/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ec.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.web.struts.CertificationReportForm;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.core.util.KeyLabelPair;

/**
 * This class...
 */
public class PositionNumberValuesFinder extends KeyValuesBase {
    private List keyValues;

    /**
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        CertificationReportForm form = (CertificationReportForm) GlobalVariables.getKualiForm();
        EffortCertificationDocument document = (EffortCertificationDocument)form.getDocument();
        List keyValues = new ArrayList();
        List<String> positionNumberList = document.getPositionList();
        for (String positionNumber : positionNumberList) {
            keyValues.add(new KeyLabelPair(positionNumber, positionNumber));
        }
        
        return keyValues;
    }

}
