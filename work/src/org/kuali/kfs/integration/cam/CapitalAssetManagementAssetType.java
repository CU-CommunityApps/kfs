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
package org.kuali.kfs.integration.cam;

import org.kuali.rice.kns.bo.ExternalizableBusinessObject;

/**
 * An interface that declares methods to retrieve information about asset type data collected by FP documents.
 */
public interface CapitalAssetManagementAssetType extends ExternalizableBusinessObject {

    /**
     * Gets the capitalAssetTypeCode attribute.
     * 
     * @return Returns the capitalAssetTypeCode
     * 
     */
    public String getCapitalAssetTypeCode();
    
    /**
     * Gets the capitalAssetTypeDescription attribute.
     * 
     * @return Returns the capitalAssetTypeDescription
     * 
     */
    public String getCapitalAssetTypeDescription();
}
