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
package org.kuali.kfs.module.ar.batch;

public class CustomerLoadBatchError {

    private String customerName;
    private String propertyName;
    private Class<?> propertyClass;
    private String value;
    private String description;
    
    /**
     * 
     * Default constructor with all internal fields defaulting to empty-string or null.
     */
    public CustomerLoadBatchError() {
        this.customerName = "";
        this.propertyName = "";
        this.propertyClass = null;
        this.value = "";
        this.description = "";
    }
    
    /**
     * 
     * Constructor to initialize with just the customerName.
     * @param customerName The customer name of the document being imported.
     */
    public CustomerLoadBatchError(String customerName) {
        this.customerName = customerName;
    }
    
    /**
     * 
     * Constructs a CustomerLoadBatchError.java.
     * @param customerName The customer name of the document being imported.
     * @param propertyName The name of the property on the Customer BO.
     * @param propertyClass The class of the property named by propertyName.
     * @param value The original value of the field from the batch import document.
     * @param description The description of the error that occurred.
     */
    public CustomerLoadBatchError(String customerName, String propertyName, Class<?> propertyClass, String value, String description) {
        this.customerName = customerName;
        this.propertyName = propertyName;
        this.propertyClass = propertyClass;
        this.value = value;
        this.description = description;
    }

    public String toString() {
        return "[" + customerName + "] (" + propertyClass.toString() + ") " + propertyName + ": '" + value + "' - " + description;
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public void setPropertyClass(Class<?> propertyClass) {
        this.propertyClass = propertyClass;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
