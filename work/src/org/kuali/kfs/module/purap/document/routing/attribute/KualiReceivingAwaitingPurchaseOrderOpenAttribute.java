/*
 * Copyright 2008 The Kuali Foundation.
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

import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.routeheader.DocumentContent;
import org.kuali.rice.kew.rule.AbstractWorkflowAttribute;
import org.kuali.rice.kew.rule.RuleExtension;

public class KualiReceivingAwaitingPurchaseOrderOpenAttribute extends AbstractWorkflowAttribute {

    public boolean isMatch(DocumentContent docContent, List<RuleExtension> ruleExtensions) {
        String documentNumber = docContent.getRouteContext().getDocument().getRouteHeaderId().toString();
        return SpringContext.getBean(ReceivingService.class).isAwaitingPurchaseOrderOpen(documentNumber);
    }

}
