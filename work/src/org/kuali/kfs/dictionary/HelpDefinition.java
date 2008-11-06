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

package org.kuali.kfs.dictionary;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;

public class HelpDefinition extends org.kuali.rice.kns.datadictionary.HelpDefinition {
    private String parameterClass;
    private transient ParameterService parameterService;
    /**
     * Gets the parameterClass attribute. 
     * @return Returns the parameterClass.
     */
    public String getParameterClass() {
        return parameterClass;
    }
    /**
     * Sets the parameterClass attribute value.
     * @param parameterClass The parameterClass to set.
     */
    public void setParameterClass(String parameterClass) {
        parameterService = getParameterService();
        try{
            this.setParameterNamespace(parameterService.getNamespace(Class.forName(parameterClass)));
            this.setParameterDetailType(parameterService.getDetailType(Class.forName(parameterClass)));
        }catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid class name: " + parameterClass);
        }

    }
    /**
     * @return Returns the parameterService.
     */
    private ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
}
