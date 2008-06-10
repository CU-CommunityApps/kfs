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
package org.kuali.module.ar.lookup.valuefinder;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.ChartOrgHolder;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.FinancialSystemUserService;
import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.module.ar.service.SystemInformationService;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.module.financial.service.UniversityDateService;

public class SystemInformationValueFinderBase {
    
    protected SystemInformation sysInfo;

    /**
     * Constructs a SystemInformationValueFinderBase.  Sets the SystemInformation BO based on current
     * year, current users chart of account code, and current users organization code
     */
    public SystemInformationValueFinderBase(){
        SystemInformationService service = (SpringContext.getBean(SystemInformationService.class));
        
        Integer currentUniversityFiscalYear =  SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        ChartOrgHolder chartUser = SpringContext.getBean(FinancialSystemUserService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);
        sysInfo = service.getByPrimaryKey(currentUniversityFiscalYear, chartUser.getChartOfAccountsCode(), chartUser.getOrganizationCode());
    }
}
