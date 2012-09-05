/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.Map;

import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelDocumentBase;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

abstract public class TravelArrangeableAuthorizer extends AccountingDocumentAuthorizerBase implements ReturnToFiscalOfficerAuthorizer {

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase#addRoleQualification(org.kuali.rice.kns.bo.BusinessObject,java.util.Map)
     */
    @Override
    protected void addRoleQualification(BusinessObject businessObject, Map<String, String> attributes) {
        super.addRoleQualification(businessObject, attributes);
        TravelDocumentBase document = (TravelDocumentBase) businessObject;
        // add the document amount
        if (ObjectUtils.isNotNull(document.getProfileId())) {
            attributes.put(TEMProfileProperties.PROFILE_ID, document.getProfileId().toString());
        }
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#canEditDocumentOverview(org.kuali.rice.kns.document.Document,org.kuali.rice.kim.bo.Person)
     */
    @Override
    public boolean canEditDocumentOverview(Document document, Person user) {
        // override base implementation to only allow initiator to edit doc overview
        return isAuthorizedByTemplate(document, KNSConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.EDIT_DOCUMENT, user.getPrincipalId());
    }

    /**
     * @param travelDocument
     * @param user
     * @return
     */
    public boolean canSave(TravelDocument travelDocument, Person user) {
        if (ObjectUtils.isNull(user)) {
            return false;
        }
        // Only initiator, arranger and FO can save doc enroute;
        return getTravelService().isUserInitiatorOrArranger(travelDocument, user) || getTravelDocumentService().isResponsibleForAccountsOn(travelDocument, user.getPrincipalId());
    }

    /**
     * 
     * @return
     */
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }
    
    /**
     * 
     * @return
     */
    public TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }
}
