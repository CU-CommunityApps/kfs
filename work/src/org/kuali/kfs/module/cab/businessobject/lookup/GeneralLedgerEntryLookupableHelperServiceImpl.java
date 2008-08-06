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
package org.kuali.kfs.module.cab.businessobject.lookup;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;

/**
 * This class overrids the base getActionUrls method
 */
public class GeneralLedgerEntryLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerEntryLookupableHelperServiceImpl.class);

    @Override
    public String getActionUrls(BusinessObject bo) {
        GeneralLedgerEntry entry = (GeneralLedgerEntry) bo;
        return "<a href=\"../cabGlLine.do?methodToCall=start&generalLedgerAccountIdentifier=" + entry.getGeneralLedgerAccountIdentifier() + "\">" + "Assets" + "</a>";
    }

}
