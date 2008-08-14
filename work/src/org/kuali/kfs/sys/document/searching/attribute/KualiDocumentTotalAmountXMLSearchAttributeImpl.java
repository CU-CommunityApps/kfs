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
package org.kuali.kfs.sys.document.searching.attribute;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.rice.kew.docsearch.SearchableAttributeFloatValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kns.workflow.attribute.KualiXmlSearchableAttributeImpl;

/**
 * This class...
 */
public class KualiDocumentTotalAmountXMLSearchAttributeImpl extends KualiXmlSearchableAttributeImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiDocumentTotalAmountXMLSearchAttributeImpl.class);

    private static final String FIELD_DEF_NAME = "";

    /**
     * This method will take the search storage values from the {@link org.kuali.workflow.attribute.KualiXmlSearchableAttributeImpl}
     * method and either add or translate
     */
    @Override
    public List getSearchStorageValues(String docContent) {
        List<SearchableAttributeValue> newSearchAttributeValues = new ArrayList();
        List<SearchableAttributeValue> superList = super.getSearchStorageValues(docContent);
        if (superList.isEmpty()) {
            SearchableAttributeFloatValue attValue = generateZeroDollarSearchableAttributeValue();
            newSearchAttributeValues.add(attValue);
        }
        else {
            for (Iterator iter = superList.iterator(); iter.hasNext();) {
                SearchableAttributeValue searchableValue = (SearchableAttributeValue) iter.next();
                if ((FIELD_DEF_NAME.equals(searchableValue.getSearchableAttributeKey())) && (searchableValue.getSearchableAttributeValue() == null)) {
                    searchableValue.setupAttributeValue("0.00");
                }
                newSearchAttributeValues.add(searchableValue);
            }
        }
        return newSearchAttributeValues;
    }

    private SearchableAttributeFloatValue generateZeroDollarSearchableAttributeValue() {
        SearchableAttributeFloatValue sav = new SearchableAttributeFloatValue();
        sav.setSearchableAttributeKey(FIELD_DEF_NAME);
        sav.setSearchableAttributeValue(BigDecimal.ZERO);
        return sav;
    }

}
