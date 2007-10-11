/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.service.impl;

import java.util.List;

import org.kuali.core.bo.Parameter;
import org.kuali.kfs.service.ParameterEvaluator;

public class AlwaysSucceedParameterEvaluatorImpl implements ParameterEvaluator {
    private static final AlwaysSucceedParameterEvaluatorImpl instance = new AlwaysSucceedParameterEvaluatorImpl();
    
    public static ParameterEvaluator getInstance() {
        return instance;
    }

    private AlwaysSucceedParameterEvaluatorImpl() {    
    }

    public boolean constraintIsAllow() {
        return Boolean.TRUE;
    }

    public boolean evaluateAndAddError(String errorMessageKey, String errorFieldName, String errorParameterLabel) {
        return Boolean.TRUE;
    }

    public boolean evaluationSucceeds() {
        return Boolean.TRUE;
    }

    public String getName() {
        return AlwaysSucceedParameterEvaluatorImpl.class.getName();
    }

    public String getParameterValuesForMessage() {
        return AlwaysSucceedParameterEvaluatorImpl.class.getName();
    }

    public String getValue() {
        return AlwaysSucceedParameterEvaluatorImpl.class.getName();
    }

    public void setConstrainedValue(String constrainedValue) {
    }

    public void setConstraintIsAllow(boolean constraintIsAllow) {
    }

    public void setParameter(Parameter parameter) {
    }

    public void setValues(List<String> values) {
    }
}
