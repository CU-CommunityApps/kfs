/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.webService.BudgetCategory;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.Service;


/**
 * This class was generated by Apache CXF 2.2.10
 * Fri Jan 07 09:07:37 HST 2011
 * Generated source version: 2.2.10
 * 
 */


@WebServiceClient(name = "budgetCategorySoapService", 
                  wsdlLocation = "http://test.kc.kuali.org/kc-trunk/remoting/budgetCategorySoapService?wsdl",
                  targetNamespace = "KC") 
public class InstitutionalBudgetCategorySoapService extends Service {

    public final static URL WSDL_LOCATION;
    public final static QName SERVICE = new QName("KC", "budgetCategorySoapService");
    public final static QName BudgetCategoryServicePort = new QName("KC", "budgetCategoryServicePort");
    static {
        URL url = null;
        try {
            url = new URL("http://test.kc.kuali.org/kc-trunk/remoting/budgetCategorySoapService?wsdl");
        } catch (MalformedURLException e) {
            System.err.println("Can not initialize the default wsdl from http://test.kc.kuali.org/kc-trunk/remoting/budgetCategorySoapService?wsdl");
            // e.printStackTrace();
        }
        WSDL_LOCATION = url;
    }

    public InstitutionalBudgetCategorySoapService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public InstitutionalBudgetCategorySoapService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public InstitutionalBudgetCategorySoapService() {
        super(WSDL_LOCATION, SERVICE);
    }
    

    /**
     * 
     * @return
     *     returns BudgetCategoryService
     */
    @WebEndpoint(name = "budgetCategoryServicePort")
    public InstitutionalBudgetCategoryService getBudgetCategoryServicePort() {
        return super.getPort(BudgetCategoryServicePort, InstitutionalBudgetCategoryService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BudgetCategoryService
     */
    @WebEndpoint(name = "budgetCategoryServicePort")
    public InstitutionalBudgetCategoryService getBudgetCategoryServicePort(WebServiceFeature... features) {
        return super.getPort(BudgetCategoryServicePort, InstitutionalBudgetCategoryService.class, features);
    }

}