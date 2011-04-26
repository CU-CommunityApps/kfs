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
package org.kuali.kfs.sys.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.kns.lookup.KualiLookupableImpl;

@ConfigureContext
public class SpringConfigurationConsistencyCheck extends KualiTestBase {

    
    public void testAllLookupablesArePrototypes() throws Exception {
        List<String> failingBeans = new ArrayList<String>();
        
        Map<String,KualiLookupableImpl> beans = SpringContext.getBeansOfType(KualiLookupableImpl.class);
//        Map<String,KualiLookupableImpl> beans2 = SpringContext.getBeansOfType(KualiLookupableImpl.class);
        
        for ( String beanName : beans.keySet() ) {
            if ( beans.get(beanName) == SpringContext.getBean(beanName) ) {
                failingBeans.add( "\n *** " + beanName + "is not a singleton and should be." );
            }
        }
        assertEquals( "Beans Failing Non-Singleton check: " + failingBeans, 0, failingBeans.size() );
    }
    
    // TODO: Triggers referenced in module definitions
    // TODO: Jobs referenced in module definitions
    // TODO: FY makers referenced in module definitions
    // TODO: DAOs should extend from the xxxx class
}
