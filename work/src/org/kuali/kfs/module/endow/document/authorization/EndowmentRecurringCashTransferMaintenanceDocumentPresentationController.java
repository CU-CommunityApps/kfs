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
package org.kuali.kfs.module.endow.document.authorization;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;
import org.kuali.kfs.module.endow.businessobject.lookup.CalculateProcessDateUsingFrequencyCodeService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class EndowmentRecurringCashTransferMaintenanceDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> readOnlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        
        EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = (EndowmentRecurringCashTransfer) document.getNewMaintainableObject().getBusinessObject();

        String frequencyCode = endowmentRecurringCashTransfer.getFrequencyCode();
        if (StringUtils.isNotEmpty(frequencyCode)) {
            CalculateProcessDateUsingFrequencyCodeService calculateProcessDateUsingFrequencyCodeService = (CalculateProcessDateUsingFrequencyCodeService) SpringContext.getBean(CalculateProcessDateUsingFrequencyCodeService.class);
            endowmentRecurringCashTransfer.setNextProcessDate(calculateProcessDateUsingFrequencyCodeService.calculateProcessDate(frequencyCode));
        }

        return readOnlyPropertyNames;
    }
}
