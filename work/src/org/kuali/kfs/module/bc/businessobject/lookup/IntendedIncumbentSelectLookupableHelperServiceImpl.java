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
package org.kuali.kfs.module.bc.businessobject.lookup;

import java.util.Properties;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbentSelect;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.util.UrlFactory;

/**
 * Lookupable helper service implementation for the intended incumbent select screen.
 */
public class IntendedIncumbentSelectLookupableHelperServiceImpl extends SelectLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getActionUrls(org.kuali.rice.kns.bo.BusinessObject)
     */
    @Override
    public String getActionUrls(BusinessObject businessObject) {
        BudgetConstructionIntendedIncumbentSelect intendedIncumbentSelect = (BudgetConstructionIntendedIncumbentSelect) businessObject;

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.INCUMBENT_SALARY_SETTING_METHOD);

        String[] universityFiscalYear = (String[]) super.getParameters().get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear[0]);

        parameters.put(KFSPropertyConstants.EMPLID, intendedIncumbentSelect.getEmplid());
        parameters.put(BCPropertyConstants.SINGLE_ACCOUNT_MODE, "false");
        parameters.put(BCPropertyConstants.ADD_LINE, "false");

        String url = UrlFactory.parameterizeUrl(BCConstants.INCUMBENT_SALARY_SETTING_ACTION, parameters);

        return url = "<a href=\"" + url + "\" target=\"blank\" title=\"Incmbnt Salset\">Incmbnt Salset</a>";
    }

}
