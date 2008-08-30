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
package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class FunctionalFieldDescription extends PersistableBusinessObjectBase {
    private String namespaceCode;
    private String componentClass;
    private String propertyName;
    private String description;
    private boolean active;

    private BusinessObjectProperty businessObjectProperty;
    
    public FunctionalFieldDescription() {        
    }
    
    public FunctionalFieldDescription(String componentClass, String propertyName) {
        setComponentClass(componentClass);
        setPropertyName(propertyName);
    }
        
    @Override
    public void refreshNonUpdateableReferences() {
        if (StringUtils.isNotBlank(getComponentClass()) && StringUtils.isNotBlank(getPropertyName()) && ((businessObjectProperty == null) || !getComponentClass().equals(businessObjectProperty.getBusinessObjectComponent().getComponentClass()) || !getPropertyName().equals(businessObjectProperty.getPropertyName()))) {
            setBusinessObjectProperty(SpringContext.getBean(KfsBusinessObjectMetaDataService.class).getBusinessObjectProperty(getComponentClass(), getPropertyName()));
            setNamespaceCode(businessObjectProperty.getNamespaceCode());
        }
    }
    
    public String getNamespaceCode() {
        return namespaceCode;
    }

    public void setNamespaceCode(String namespaceCode) {
        this.namespaceCode = namespaceCode;
    }

    public String getComponentClass() {
        return componentClass;
    }

    public void setComponentClass(String componentClass) {
        this.componentClass = componentClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public BusinessObjectProperty getBusinessObjectProperty() {
        return businessObjectProperty;
    }

    public void setBusinessObjectProperty(BusinessObjectProperty businessObjectProperty) {
        this.businessObjectProperty = businessObjectProperty;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        if (businessObjectProperty != null) {
            return businessObjectProperty.toStringMapper();
        }
        return new LinkedHashMap<String, String>();
    }
}
