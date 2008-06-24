/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.module.cg.businessobject.AwardStatus;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Allows custom handling of {@link AwardStatus} values in the UI.
 */
public class AwardStatusValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        Collection<AwardStatus> codes = SpringContext.getBean(KeyValuesService.class).findAll(AwardStatus.class);

        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair("", ""));

        for (AwardStatus awardStatus : codes) {
            if (awardStatus.isRowActiveIndicator()) {
                labels.add(new KeyLabelPair(awardStatus.getAwardStatusCode(), awardStatus.getAwardStatusCode() + "-" + awardStatus.getAwardStatusDescription()));
            }
        }

        return labels;
    }
}
