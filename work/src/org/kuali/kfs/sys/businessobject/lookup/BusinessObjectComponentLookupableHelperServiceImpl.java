/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.Collections;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectComponent;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.BeanPropertyComparator;

public class BusinessObjectComponentLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService;

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        List<BusinessObjectComponent> matchingBusinessObjectComponents = kfsBusinessObjectMetaDataService.findBusinessObjectComponents(fieldValues.get(KFSPropertyConstants.NAMESPACE_CODE), fieldValues.get(KFSPropertyConstants.COMPONENT_LABEL));
        if (getDefaultSortColumns().size() > 0) {
            Collections.sort(matchingBusinessObjectComponents, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return matchingBusinessObjectComponents;
    }

    public void setKfsBusinessObjectMetaDataService(KfsBusinessObjectMetaDataService kfsBusinessObjectMetaDataService) {
        this.kfsBusinessObjectMetaDataService = kfsBusinessObjectMetaDataService;
    }
}
