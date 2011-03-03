/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.external.kc.businessobject.lookup;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;

/**
 * This class overrids the base getActionUrls method
 */
public class KualiUnitDTOLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiUnitDTOLookupableHelperServiceImpl.class);
    
    ThreadLocal<Person> currentUser = new ThreadLocal<Person>();

    /**
     * If the account is not closed or the user is an Administrator the "edit" link is added The "copy" link is added for Accounts
     * 
     * @returns links to edit and copy maintenance action for the current maintenance record.
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject,
     *      java.util.List)
     */
 

    /**
     * Overridden to allow EBO to use web service to lookup KC Unit.
     * 
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
/*
    public List<? extends BusinessObject> getSearchResults(Map<String, String> parameters) {
 
        List unitList = new ArrayList();
        try {
            ContractsAndGrantsUnit unitDTO = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsUnit.class).createNewObjectFromExternalizableClass(ContractsAndGrantsUnit.class);
            unitList = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsUnit.class).getExternalizableBusinessObjectsListForLookup(unitDTO, (Map)parameters, false);
            if (unitList == null) return Collections.EMPTY_LIST;
           return unitList;
            
        } catch (Exception ex) {
            LOG.error(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND +  ex.getMessage()); 
            GlobalVariables.getMessageMap().putError("errors", "error.blank",ContractsAndGrantsConstants.KcWebService.ERROR_KC_WEB_SERVICE_FAILURE, ex.getMessage());
        }
        
        return Collections.EMPTY_LIST;
    }
*/

}
