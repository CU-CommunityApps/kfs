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
package org.kuali.module.purap.maintenance;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.ReceivingAddress;
import org.kuali.rice.KNSServiceLocator;

/**
 * ReceivingAddressMaintainableImpl is a special implementation of KualiMaintainableImpl for ReceivingAddresss. 
 * It generates extra locks for other receiving addresses related to the one being updated in the maintenance document,
 * and updates the ones affected during post-processing to enforce certain contraints among these objects. 
 */
public class ReceivingAddressMaintainableImpl extends KualiMaintainableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReceivingAddressMaintainableImpl.class);

    /**
     * Generates the appropriate maintenance locks for {@link ReceivingAddress}
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        ReceivingAddress receivingAddress = (ReceivingAddress) this.businessObject;
        List<MaintenanceLock> locks = super.generateMaintenanceLocks();
        if ( receivingAddress.isDefaultIndicator() && receivingAddress.isActive() ) {
            locks.add(createMaintenanceLock(new String[] { "chartOfAccountsCode", "organizationCode", "defaultIndicator", "active" }));
        }
        return locks;
    }

    /**
     * Creates a maintenance lock for the field names supplied.
     * 
     * @param fieldNames
     * @return the maintenance lock for supplied field names
     */
    private MaintenanceLock createMaintenanceLock(String[] fieldNames) {
        MaintenanceLock lock = new MaintenanceLock();
        lock.setDocumentNumber(this.documentNumber);
        lock.setLockingRepresentation(createLockingRepresentation(fieldNames));
        return lock;
    }

    /**
     * Creates a locking representation for the field names supplied.
     * 
     * @param fieldNames
     * @return locking representation string
     */
    private String createLockingRepresentation(String[] fieldNames) {
        StringBuilder lockRepresentation = new StringBuilder();

        lockRepresentation.append(ReceivingAddress.class.getName());
        lockRepresentation.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);

        DataDictionaryService dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        EncryptionService encryptionService = KNSServiceLocator.getEncryptionService();

        int count = 0;
        for (String fieldName : fieldNames) {
            lockRepresentation.append(fieldName);
            lockRepresentation.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRepresentation.append(retrieveFieldValueForLock(fieldName, dataDictionaryService, encryptionService));
            if (count < (fieldNames.length - 1)) {
                lockRepresentation.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            }
            count += 1;
        }

        return lockRepresentation.toString();
    }

    /**
     * Returns the field value of a given field, converting the value to a String and encrypting it if necessary.
     * 
     * @param fieldName
     * @param ddService
     * @return string field value for a lock
     */
    private String retrieveFieldValueForLock(String fieldName, DataDictionaryService ddService, EncryptionService encryptionService) {
        Object fieldValue = ObjectUtils.getPropertyValue(this.businessObject, fieldName);
        if (fieldValue == null) {
            fieldValue = "";
        }

        // check if field is a secure
        String displayWorkgroup = ddService.getAttributeDisplayWorkgroup(getBoClass(), fieldName);
        if (StringUtils.isNotBlank(displayWorkgroup)) {
            try {
                fieldValue = encryptionService.encrypt(fieldValue);
            }
            catch (GeneralSecurityException e) {
                LOG.error("Unable to encrypt secure field for locking representation " + e.getMessage());
                throw new RuntimeException("Unable to encrypt secure field for locking representation " + e.getMessage());
            }
        }
        return String.valueOf(fieldValue);
    }

    /**
     * Checks if there's any active receiving address set to default other than this one; 
     * if so, set them to non-default. 
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#handleRouteStatusChange(org.kuali.core.bo.DocumentHeader)
     */
    @Override
    public void handleRouteStatusChange(DocumentHeader header) {
        super.handleRouteStatusChange(header);

        ReceivingAddress ra = (ReceivingAddress) getBusinessObject();
        // proceed only if this bo is active and default.
        if ( !ra.isActive() || !ra.isDefaultIndicator() )
            return;
        
        KualiWorkflowDocument workflowDoc = header.getWorkflowDocument();
        // this code is only executed when the final approval occurs
        if (workflowDoc.stateIsProcessed()) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, ra.getChartOfAccountsCode());
            criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, ra.getOrganizationCode());
            criteria.put(PurapPropertyConstants.RCVNG_ADDR_DFLT_IND, true);        
            criteria.put(PurapPropertyConstants.RCVNG_ADDR_ACTIVE, true);        
            List<ReceivingAddress> addresses = (List)SpringContext.getBean(BusinessObjectService.class).findMatching(ReceivingAddress.class, criteria);
            for ( ReceivingAddress rai : addresses ) {
                if ( !rai.getReceivingAddressIdentifier().equals(ra.getReceivingAddressIdentifier()) ) {
                    rai.setDefaultIndicator(false);
                    SpringContext.getBean(BusinessObjectService.class).save(rai);
                }
            }
        }
    }

}
