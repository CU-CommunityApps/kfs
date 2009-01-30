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
package org.kuali.kfs.module.bc.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAccountReports;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.bo.BusinessObject;

public class BudgetConstructionDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    /**
     * Add role qualifications (chart, account, year, level code) needed from document
     * 
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.kns.bo.BusinessObject,
     *      java.util.Map)
     */
    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);

        BudgetConstructionDocument document = (BudgetConstructionDocument) businessObject;

        attributes.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, document.getChartOfAccountsCode());
        attributes.put(KfsKimAttributes.ACCOUNT_NUMBER, document.getAccountNumber());
        attributes.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, document.getUniversityFiscalYear().toString());
        attributes.put(BCPropertyConstants.ORGANIZATION_LEVEL_CODE, document.getOrganizationLevelCode().toString());
    }

}
