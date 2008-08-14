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

import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

public class RoutingFormApprovalStatusValuesFinder extends KeyValuesBase {

    public RoutingFormApprovalStatusValuesFinder() {
        super();
    }

    public List getKeyValues() {

        List approvalTypeKeyLabelPairList = new ArrayList();
        approvalTypeKeyLabelPairList.add(new KeyLabelPair("", ""));
        approvalTypeKeyLabelPairList.add(new KeyLabelPair(CGConstants.RESEARCH_RISK_STUDY_STATUS_PENDING, "Pending"));
        approvalTypeKeyLabelPairList.add(new KeyLabelPair(CGConstants.RESEARCH_RISK_STUDY_STATUS_APPROVED, "Approved"));

        return approvalTypeKeyLabelPairList;
    }
}
