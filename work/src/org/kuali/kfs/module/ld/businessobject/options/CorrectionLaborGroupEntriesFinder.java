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
package org.kuali.kfs.module.ld.businessobject.options;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * Entries Finder for Labor Balance Type Code. Returns a list of key values of Labor Origin Entry Groups for LLCP.
 */
public class CorrectionLaborGroupEntriesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        OriginEntryGroupService originEntryGroupService = SpringContext.getBean(OriginEntryGroupService.class);
        Collection<OriginEntryGroup> groupList = originEntryGroupService.getAllOriginEntryGroup();
        List<OriginEntryGroup> sortedGroupList = (List) groupList;
        OriginEntryGroup.GroupTypeComparator oegTypeComparator = new OriginEntryGroup.GroupTypeComparator();
        Collections.sort(sortedGroupList, oegTypeComparator);

        String groupException = "";
        for (int i = 0; i < KFSConstants.LLCP_GROUP_FILTER_EXCEPTION.length; i++) {
            groupException += KFSConstants.LLCP_GROUP_FILTER_EXCEPTION[i] + " ";
        }

        for (OriginEntryGroup oeg : sortedGroupList) {
            if (oeg.getSourceCode().startsWith("L") && !groupException.contains(oeg.getSourceCode())) {
                activeLabels.add(new KeyLabelPair(oeg.getId().toString(), oeg.getName()));
            }

        }

        return activeLabels;
    }
}
