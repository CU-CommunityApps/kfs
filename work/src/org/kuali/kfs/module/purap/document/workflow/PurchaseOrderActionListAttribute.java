/*
 * Copyright 2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.workflow;

import org.kuali.rice.kew.actionlist.CustomActionListAttribute;
import org.kuali.rice.kew.api.action.ActionSet;
import org.kuali.rice.kew.api.actionlist.DisplayParameters;

public class PurchaseOrderActionListAttribute implements CustomActionListAttribute {

    @Override
    public ActionSet getLegalActions(String principalId, org.kuali.rice.kew.api.action.ActionItem actionItem) throws Exception {
        return ActionSet.Builder.create().build();
    }

    @Override
    public DisplayParameters getDocHandlerDisplayParameters(String principalId, org.kuali.rice.kew.api.action.ActionItem actionItem) throws Exception {
        return null;
    }
    
    
}
