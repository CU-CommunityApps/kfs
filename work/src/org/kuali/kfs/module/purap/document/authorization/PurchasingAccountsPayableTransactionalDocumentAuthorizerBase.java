/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kns.bo.BusinessObject;

public class PurchasingAccountsPayableTransactionalDocumentAuthorizerBase extends AccountingDocumentAuthorizerBase {

    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        attributes.put(KfsKimAttributes.DOCUMENT_SENSITIVE, "false");
        PurchasingAccountsPayableDocument purapDoc = (PurchasingAccountsPayableDocument) businessObject;
        if (purapDoc.getAccountsPayablePurchasingDocumentLinkIdentifier() != null) {
            List<SensitiveData> sensitiveDataList = SpringContext.getBean(SensitiveDataService.class).getSensitiveDatasAssignedByRelatedDocId(purapDoc.getAccountsPayablePurchasingDocumentLinkIdentifier());
            StringBuffer sensitiveDataCodes = new StringBuffer();
            for (SensitiveData sensitiveData : sensitiveDataList) {
                sensitiveDataCodes.append(sensitiveData.getSensitiveDataCode()).append(";");
            }
            if (sensitiveDataCodes.length() > 0) {
                attributes.put(KfsKimAttributes.DOCUMENT_SENSITIVE, "true");
                attributes.put(KfsKimAttributes.SENSITIVE_DATA_CODE, sensitiveDataCodes.toString().substring(0, sensitiveDataCodes.length() - 1));
                attributes.put(KfsKimAttributes.ACCOUNTS_PAYABLE_PURCHASING_DOCUMENT_LINK_IDENTIFIER, purapDoc.getAccountsPayablePurchasingDocumentLinkIdentifier().toString());
            }
        }
    }
}
