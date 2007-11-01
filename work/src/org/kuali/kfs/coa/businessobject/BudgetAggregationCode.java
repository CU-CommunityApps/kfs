/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo.codes;

import org.kuali.core.bo.KualiCodeBase;


/**
 * Budget Aggregation Code Business Object
 */
public class BudgetAggregationCode extends KualiCodeBase {

    public BudgetAggregationCode() {
        super.setActive(true); // always active, plus no column in the table
    }
}