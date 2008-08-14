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
package org.kuali.kfs.coa.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.MandatoryTransferEliminationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.KeyValuesService;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * This class creates a new finder for our forms view (creates a drop-down of {@link MandatoryTransferEliminationCode}s)
 */
public class MandatoryTransferEliminationCodeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link MandatoryTransferEliminationCode}s using their code as their key, and their code "-" name as the
     * display value
     * 
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all Mandatory Transfer Elimination Codes
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        List mteCodes = (List) boService.findAll(MandatoryTransferEliminationCode.class);

        // calling comparator.
        MandatoryTransferEliminationCodeComparator mteCodeComparator = new MandatoryTransferEliminationCodeComparator();

        // sort using comparator.
        Collections.sort(mteCodes, mteCodeComparator);

        // create a new list (code, descriptive-name)
        List labels = new ArrayList();

        for (Iterator iter = mteCodes.iterator(); iter.hasNext();) {
            MandatoryTransferEliminationCode mteCode = (MandatoryTransferEliminationCode) iter.next();
            labels.add(new KeyLabelPair(mteCode.getCode(), mteCode.getCode() + " - " + mteCode.getName()));
        }

        return labels;
    }

}
