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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * 
 */
public class RoutingFormConflictOfInterest extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String routingFormConflictOfInterestDescription;

    /**
     * Default constructor.
     */
    public RoutingFormConflictOfInterest() {

    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    /**
     * Gets the routingFormConflictOfInterestDescription attribute.
     * 
     * @return Returns the routingFormConflictOfInterestDescription
     */
    public String getRoutingFormConflictOfInterestDescription() {
        return routingFormConflictOfInterestDescription;
    }

    /**
     * Sets the routingFormConflictOfInterestDescription attribute.
     * 
     * @param routingFormConflictOfInterestDescription The routingFormConflictOfInterestDescription to set.
     */
    public void setRoutingFormConflictOfInterestDescription(String routingFormConflictOfInterestDescription) {
        this.routingFormConflictOfInterestDescription = routingFormConflictOfInterestDescription;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
}
