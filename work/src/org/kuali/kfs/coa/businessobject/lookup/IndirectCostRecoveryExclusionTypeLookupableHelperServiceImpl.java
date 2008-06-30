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
package org.kuali.kfs.coa.businessobject.lookup;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;

/**
 * This class overrides the base getActionUrls to set it to an empty string
 */
public class IndirectCostRecoveryExclusionTypeLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @returns Empty string because we don't want any action links to be displayed on the lookups results page.
     * @see org.kuali.core.lookup.LookupableHelperService#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    public String getActionUrls(BusinessObject businessObject) {
        return "";
    }

}
