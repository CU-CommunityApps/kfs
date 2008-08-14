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
import java.util.List;

import org.kuali.kfs.module.cg.businessobject.BudgetBaseCode;
import org.kuali.kfs.module.cg.document.service.BudgetIndirectCostService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

public class BudgetBaseCodeValuesFinder extends KeyValuesBase {

    public BudgetBaseCodeValuesFinder() {
        super();
    }

    public List getKeyValues() {

        List<BudgetBaseCode> baseCodes = new ArrayList(SpringContext.getBean(BudgetIndirectCostService.class).getDefaultBudgetBaseCodeValues());
        List baseCodeKeyLabelPairList = new ArrayList();
        for (BudgetBaseCode element : baseCodes) {
            baseCodeKeyLabelPairList.add(new KeyLabelPair(element.getBudgetBaseCode(), element.getBudgetBaseDescription()));
        }

        return baseCodeKeyLabelPairList;
    }
}
