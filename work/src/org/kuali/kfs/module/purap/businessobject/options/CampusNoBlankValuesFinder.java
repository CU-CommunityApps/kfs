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
import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.Campus;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.rice.KNSServiceLocator;

/**
 * Value Finder for Campus with no blank value.
 */
public class CampusNoBlankValuesFinder extends KeyValuesBase {

    /*
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        KeyValuesService boService = KNSServiceLocator.getKeyValuesService();
        Collection codes = boService.findAll(Campus.class);
        List labels = new ArrayList();
        for (Iterator iter = codes.iterator(); iter.hasNext();) {
            Campus campus = (Campus) iter.next();
            labels.add(new KeyLabelPair(campus.getCampusCode(), campus.getCampusCode() + " - " + campus.getCampusName()));
        }

        return labels;
    }

}
