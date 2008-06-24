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
package org.kuali.kfs.fp.businessobject.options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.fp.businessobject.TravelPerDiem;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

/**
 * This class returns list of per diem category value pairs.
 */
public class TravelPerDiemCategoryValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        Map criteria = new HashMap();
        criteria.put("fiscalYear", SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());

        List boList = (List) SpringContext.getBean(KeyValuesService.class).findMatching(TravelPerDiem.class, criteria);
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair("", ""));
        for (Iterator iter = boList.iterator(); iter.hasNext();) {
            TravelPerDiem element = (TravelPerDiem) iter.next();
            keyValues.add(new KeyLabelPair(element.getPerDiemCountryName(), element.getPerDiemCountryName()));
        }

        return keyValues;
    }

}
