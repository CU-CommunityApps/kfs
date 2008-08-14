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
package org.kuali.kfs.module.purap.document.routing.attribute;

import java.util.List;

import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.AbstractWorkflowAttribute;
import org.kuali.rice.kew.rule.RuleExtension;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class... TODO delyea - documentation
 */
public class KualiAccountsPayableReviewAttribute extends AbstractWorkflowAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiAccountsPayableReviewAttribute.class);

    private AccountsPayableDocument getAccountsPayableDocument(String documentNumber) {
        try {
            AccountsPayableDocument document = (AccountsPayableDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
            if (ObjectUtils.isNull(document)) {
                String errorMsg = "Error trying to get document using doc id '" + documentNumber + "'";
                LOG.error("getAccountsPayableDocument() " + errorMsg);
                throw new RuntimeException(errorMsg);
            }
            document.refreshNonUpdateableReferences();
            return document;
        }
        catch (WorkflowException e) {
            String errorMsg = "Error trying to get document using doc id '" + documentNumber + "'";
            LOG.error("getAccountsPayableDocument() " + errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * @see org.kuali.rice.kew.plugin.attributes.WorkflowAttribute#isMatch(org.kuali.rice.kew.routeheader.DocumentContent, java.util.List)
     */
    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        AccountsPayableDocument document = getAccountsPayableDocument(docContent.getRouteContext().getDocument().getRouteHeaderId().toString());
        return document.requiresAccountsPayableReviewRouting();
    }

}
