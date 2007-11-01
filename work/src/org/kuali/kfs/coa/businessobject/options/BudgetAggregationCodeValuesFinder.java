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
package org.kuali.module.chart.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.codes.BudgetAggregationCode;

/**
 * This class returns list of Budget Aggregation Code type value pairs.
 */
public class BudgetAggregationCodeValuesFinder extends KeyValuesBase {

    /**
     * Creates a list of {@link BudgetAggregationCode}s using their code as their key, and their code "-" aggregation code as the
     * display value
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        // get a list of all budget aggregations codes
        KeyValuesService boService = SpringContext.getBean(KeyValuesService.class);
        List budgetAggregationCodes = (List) boService.findAll(BudgetAggregationCode.class);

        // calling comparator.
        BudgetAggregationCodeComparator budgetAggregationCodeComparator = new BudgetAggregationCodeComparator();

        // sort using comparator.
        Collections.sort(budgetAggregationCodes, budgetAggregationCodeComparator);

        // create a new list (code, descriptive-name)
        List labels = new ArrayList();

        for (Iterator iter = budgetAggregationCodes.iterator(); iter.hasNext();) {
            BudgetAggregationCode budgetAggregationCode = (BudgetAggregationCode) iter.next();
            labels.add(new KeyLabelPair(budgetAggregationCode.getCode(), budgetAggregationCode.getCode() + " - " + budgetAggregationCode.getName()));
        }

        return labels;
    }

}