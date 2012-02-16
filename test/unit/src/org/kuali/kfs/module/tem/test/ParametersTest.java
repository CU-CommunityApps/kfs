/*
 * Copyright 2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.test;

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.util.BufferedLogger.error;
import static org.kuali.kfs.module.tem.util.BufferedLogger.warn;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.util.TemObjectUtils;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.Parameter;
import org.kuali.rice.kns.service.ParameterService;

@ConfigureContext
public class ParametersTest extends KualiTestBase {
    private ParameterService parameterService = null;
    private static final String DTL_POSTFIX = "_DTL_TYPE";
    private static final boolean strictMode = true;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        parameterService = getParameterService();
    }

    /**
     * Basic test for all the parameters that's pre-populated using parameters.sql
     */
    @Test
    public void testLookup1() throws Exception {
        //setup parameters for testing
        Map<Class, Parameter> criteria = new HashMap<Class, Parameter>();
        criteria.put(TemConstants.TravelParameters.class, setupParameter(PARAM_NAMESPACE, TemConstants.TravelParameters.DOCUMENT_DTL_TYPE));
        criteria.put(TemConstants.TravelAuthorizationParameters.class, setupParameter(PARAM_NAMESPACE, TemConstants.TravelAuthorizationParameters.PARAM_DTL_TYPE));
        criteria.put(TemConstants.TravelReimbursementParameters.class, setupParameter(PARAM_NAMESPACE, TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE));
        criteria.put(KFSParameterKeyConstants.ARRefundDVParameterConstants.class, setupParameter(KFSConstants.ParameterNamespaces.FINANCIAL, KFSParameterKeyConstants.ARRefundDVParameterConstants.DISBURSEMENT_VOUCHER_DTL_TYPE));
        criteria.put(ArConstants.ArRefundingParameters.class, setupParameter(ArConstants.AR_NAMESPACE_CODE, ArConstants.PAYMENT_APPLICATION_DTL_TYPE)); 

        List<String> badParameters = testCriteria(criteria);
        
        if (strictMode) {
            assertTrue("Bad parameter(s): " + badParameters.toString(), badParameters.isEmpty());
        }
        else {
            if (!badParameters.isEmpty()) {
                warn("Bad parameter(s): " + badParameters.toString());
            }
        }        
    }

    private List<String> testCriteria(Map<Class, Parameter> criteria) {
        List<String> badParameters = new ArrayList<String>();

        for (Class key : criteria.keySet()) {
            List<String> parameterList = getParametersByConstantClass(key);
            Parameter p = criteria.get(key);
            badParameters.addAll(checkParameters(p.getParameterNamespaceCode(), p.getParameterDetailTypeCode(), parameterList));
        }

        return badParameters;
    }

    /**
     * Convenience method to populate a new parameter with namespace code and detailtype code
     */
    private Parameter setupParameter(String parameterNameSpace, String parameterDetailTypeCode) {
        Parameter param = new Parameter();
        param.setParameterNamespaceCode(parameterNameSpace);
        param.setParameterDetailTypeCode(parameterDetailTypeCode);

        return param;
    }

    /**
     * Perform parameter lookup
     */
    private List<String> checkParameters(String paramNamespace, String parameterDetailTypeCode, List<String> parameterList) {
        List<String> badParameters = new ArrayList<String>();

        if (parameterList != null) {
            for (String parameterName : parameterList) {
                Parameter p = parameterService.retrieveParameter(paramNamespace, parameterDetailTypeCode, parameterName);
                if (p == null) {
                    badParameters.add(parameterName);
                }
            }
        }
        
        return badParameters;
    }

    protected ParameterService getParameterService() {
        return SpringContext.getBean(ParameterService.class);
    }

    /**
     * Helper method to extract parameters value from a Constant class using java reflection.
     */   
    @SuppressWarnings("rawtypes")
    protected List<String> getParametersByConstantClass(Class c) {
        return getParametersByConstantClass(c, null);
    }

    /**
     * Helper method to extract parameters value from a Constant class using java reflection. 
     * excludedFields is optional.
     */    
    @SuppressWarnings("rawtypes")
    protected List<String> getParametersByConstantClass(Class c, List<String> excludedFields) {
        List<String> parameters = new ArrayList<String>();
        List<Field> fields = TemObjectUtils.getStaticFields(c);
        for (Field f : fields) {
            if (!f.getName().endsWith(DTL_POSTFIX) && (excludedFields == null || (excludedFields != null && !excludedFields.contains(f.getName())))) {
                try {
                    parameters.add((String) f.get(new String()));
                }
                catch (IllegalArgumentException ex) {
                    error("IllegalArgumentException.", ex);
                }
                catch (IllegalAccessException ex) {
                    error("IllegalAccessException.", ex);
                }
            }
        }

        return parameters;
    }
}
