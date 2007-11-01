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
package org.kuali.module.cg.lookup.keyvalues;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.service.KeyValuesService;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cg.bo.ProposalStatus;

/**
 * Gets a custom-formatted list of {@link ProposalStatus} values.
 */
public class ProposalStatusValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {

        Collection<ProposalStatus> codes = SpringContext.getBean(KeyValuesService.class).findAll(ProposalStatus.class);

        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        labels.add(new KeyLabelPair("", ""));

        for (ProposalStatus proposalStatus : codes) {
            if (proposalStatus.isRowActiveIndicator()) {
                labels.add(new KeyLabelPair(proposalStatus.getProposalStatusCode(), proposalStatus.getProposalStatusCode() + "-" + proposalStatus.getProposalStatusDescription()));
            }
        }

        return labels;
    }
}
