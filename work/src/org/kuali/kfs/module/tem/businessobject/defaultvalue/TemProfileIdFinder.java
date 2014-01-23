/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.defaultvalue;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.valuefinder.ValueFinder;

public class TemProfileIdFinder implements ValueFinder {

    @Override
    public String getValue() {
        SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);
        return sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_SEQ_NAME).toString();
    }

}