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
package org.kuali.kfs.coa.businessobject.lookup;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;

/**
 * This class overrids the base getActionUrls method
 */
public class KualiAccountLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    
    ThreadLocal<FinancialSystemUser> currentUser = new ThreadLocal<FinancialSystemUser>();
    /**
     * If the account is not closed or the user is an Administrator the "edit" link is added The "copy" link is added for Accounts
     * 
     * @returns links to edit and copy maintenance action for the current maintenance record.
     */
    @Override
    public String getActionUrls(BusinessObject bo) {
        StringBuffer actions = new StringBuffer();
        Account theAccount = (Account) bo;
        
        FinancialSystemUser user = currentUser.get();
        if ( user == null ) {
            user = SpringContext.getBean(FinancialSystemUserService.class).convertUniversalUserToFinancialSystemUser( GlobalVariables.getUserSession().getFinancialSystemUser() );
            currentUser.set(user);
        }
        if (!theAccount.isAccountClosedIndicator() || user.isAdministratorUser()) {
            actions.append(getMaintenanceUrl(bo, "edit"));
        }
        else {
            actions.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        actions.append("&nbsp;&nbsp;");
        actions.append(getMaintenanceUrl(bo, "copy"));
        return actions.toString();
    }
}
