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
package org.kuali.kfs.module.ar.document;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerMaintenableImpl extends FinancialSystemMaintainable {

    @Override
    @SuppressWarnings("unchecked")
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);

        Maintainable oldMaintainable = document.getOldMaintainableObject();
        Maintainable newMaintainable = document.getNewMaintainableObject();

        Customer oldCustomer = (Customer) oldMaintainable.getBusinessObject();
        Customer newCustomer = (Customer) newMaintainable.getBusinessObject();

        // when we create new customer set the customerRecordAddDate to current date
        if (getMaintenanceAction().equalsIgnoreCase(KNSConstants.MAINTENANCE_NEW_ACTION)) {
            Date currentDate = new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
            newCustomer.setCustomerRecordAddDate(currentDate);

        }

        List oldAddresses = oldCustomer.getCustomerAddresses();
        List newAddresses = newCustomer.getCustomerAddresses();

        // if new address was added or one of the old addresses was changes set customerAddressChangeDate to the current date
        if (oldAddresses != null && newAddresses != null) {
            if (oldAddresses.size() != newAddresses.size()) {
                Date currentDate = new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
                newCustomer.setCustomerAddressChangeDate(currentDate);
            }
            else {
                for (int i = 0; i < oldAddresses.size(); i++) {
                    CustomerAddress oldAddress = (CustomerAddress) oldAddresses.get(i);
                    CustomerAddress newAddress = (CustomerAddress) newAddresses.get(i);
                    if (oldAddress.compareTo(newAddress) != 0) {
                        Date currentDate = new Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime());
                        newCustomer.setCustomerAddressChangeDate(currentDate);

                        break;
                    }
                }
            }
        }
    }

    @Override
    public PersistableBusinessObject initNewCollectionLine(String collectionName) {

        PersistableBusinessObject businessObject = super.initNewCollectionLine(collectionName);
        Customer customer = (Customer) this.businessObject;

        if (collectionName.equalsIgnoreCase(ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES)) {

            CustomerAddress customerAddress = (CustomerAddress) businessObject;

            // set default address name to customer name
            customerAddress.setCustomerAddressName(customer.getCustomerName());

            if (KNSConstants.MAINTENANCE_NEW_ACTION.equalsIgnoreCase(getMaintenanceAction())) {

                boolean hasPrimaryAddress = false;

                for (CustomerAddress tempAddress : customer.getCustomerAddresses()) {
                    if (ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY.equalsIgnoreCase(tempAddress.getCustomerAddressTypeCode())) {
                        hasPrimaryAddress = true;
                        break;
                    }
                }
                // if maintenance action is NEW and customer already has a primary address set default value for address type code
                // to "Alternate"
                if (hasPrimaryAddress) {
                    customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
                }
                // otherwise set default value for address type code to "Primary"
                else {
                    customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY);
                }
            }

            // if maintenance action is EDIT or COPY set default value for address type code to "Alternate"
            if (KNSConstants.MAINTENANCE_EDIT_ACTION.equalsIgnoreCase(getMaintenanceAction()) || KNSConstants.MAINTENANCE_COPY_ACTION.equalsIgnoreCase(getMaintenanceAction())) {
                customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
            }

        }

        return businessObject;

    }

    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        // TODO - what is the condition required for this split?
        return true;
    }

}
