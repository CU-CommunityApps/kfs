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
package org.kuali.kfs.coa.businessobject.defaultvalue;

import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * This class holds utilities to assist with finding current chart and universal users
 */
public class ValueFinderUtil {

    /**
     * This method returns the currently logged in Universal User.
     * 
     * @return the currently logged in Universal User
     * @see Person
     */
    public static Person getCurrentPerson() {
        if (GlobalVariables.getUserSession() != null) {
            return GlobalVariables.getUserSession().getPerson();
        }
        else {
            return null;
        }
    }
}

