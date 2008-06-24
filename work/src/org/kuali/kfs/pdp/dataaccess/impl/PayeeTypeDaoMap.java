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
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.Map;

import org.kuali.kfs.pdp.dataaccess.PayeeTypeDao;

public class PayeeTypeDaoOther implements PayeeTypeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeTypeDaoOther.class);

    private Map<String, String> payees;

    public Map<String, String> getAll() {
        LOG.debug("getAll() started");
        return payees;
    }

    public void setPayees(Map<String, String> m) {
        payees = m;
    }
}
