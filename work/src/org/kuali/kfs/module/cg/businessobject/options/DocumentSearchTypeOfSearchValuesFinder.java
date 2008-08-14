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
package org.kuali.kfs.module.cg.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.workflow.KualiWorkflowUtils;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.web.ui.KeyLabelPair;

/**
 * Gets a custom-formatted list of types searchable via document search.
 */
public class DocumentSearchTypeOfSearchValuesFinder extends KeyValuesBase {

    public static final String DOCUMENT_TYPE_SEPARATOR = ";";

    public static final String[] PROPOSAL_DOCUMENT_TYPE_NAMES = new String[] { "ProposalMaintenanceDocument", KualiWorkflowUtils.C_G_PROPOSAL_DOC_TYPE, "ProposalMaintenanceDocument" };
    public static final String[] AWARD_DOCUMENT_TYPE_NAMES = new String[] { "AwardMaintenanceDocument", KualiWorkflowUtils.C_G_AWARD_DOC_TYPE, "AwardMaintenanceDocument" };

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List<KeyLabelPair> labels = new ArrayList<KeyLabelPair>();
        generatePossibleListElement(labels, "Proposal Documents", PROPOSAL_DOCUMENT_TYPE_NAMES);
        generatePossibleListElement(labels, "Award Documents", AWARD_DOCUMENT_TYPE_NAMES);
        return labels;
    }

    /**
     * Add docTypes to labels in an appropriate String format.
     * 
     * @param labels
     * @param label
     * @param docTypes
     */
    private void generatePossibleListElement(List<KeyLabelPair> labels, String label, String[] docTypes) {
        String documentTypes = "";
        for (int i = 0; i < docTypes.length; i++) {
            String docTypeName = docTypes[i];
            documentTypes = documentTypes.concat(docTypeName + DOCUMENT_TYPE_SEPARATOR);
        }
        if (StringUtils.isNotBlank(documentTypes)) {
            labels.add(new KeyLabelPair(documentTypes, label));
        }
    }

}
