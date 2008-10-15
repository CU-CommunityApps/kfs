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
package org.kuali.kfs.module.purap.businessobject;

import java.util.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class SensitiveDataAssignment  extends PersistableBusinessObjectBase {

    private  Integer sensitiveDataAssignmentIdentifier;
    private  Integer purapDocumentIdentifier;
    private  String sensitiveDataAssignmentReasonText;
    private  String sensitiveDataAssignmentPersonIdentifier;
    private  Date sensitiveDataAssignmentChangeDate;
    
    
    protected LinkedHashMap toStringMapper() {
            LinkedHashMap m = new LinkedHashMap();
            m.put("sensitiveDataAssignmentIdentifier", this.sensitiveDataAssignmentIdentifier);
            return m;
        }


    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }


    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }


    public Date getSensitiveDataAssignmentChangeDate() {
        return sensitiveDataAssignmentChangeDate;
    }


    public void setSensitiveDataAssignmentChangeDate(Date sensitiveDataAssignmentChangeDate) {
        this.sensitiveDataAssignmentChangeDate = sensitiveDataAssignmentChangeDate;
    }


    public Integer getSensitiveDataAssignmentIdentifier() {
        return sensitiveDataAssignmentIdentifier;
    }


    public void setSensitiveDataAssignmentIdentifier(Integer sensitiveDataAssignmentIdentifier) {
        this.sensitiveDataAssignmentIdentifier = sensitiveDataAssignmentIdentifier;
    }


    public String getSensitiveDataAssignmentPersonIdentifier() {
        return sensitiveDataAssignmentPersonIdentifier;
    }


    public void setSensitiveDataAssignmentPersonIdentifier(String sensitiveDataAssignmentPersonIdentifier) {
        this.sensitiveDataAssignmentPersonIdentifier = sensitiveDataAssignmentPersonIdentifier;
    }


    public String getSensitiveDataAssignmentReasonText() {
        return sensitiveDataAssignmentReasonText;
    }


    public void setSensitiveDataAssignmentReasonText(String sensitiveDataAssignmentReasonText) {
        this.sensitiveDataAssignmentReasonText = sensitiveDataAssignmentReasonText;
    }

}
