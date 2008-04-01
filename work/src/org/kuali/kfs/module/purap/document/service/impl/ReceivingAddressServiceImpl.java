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
package org.kuali.module.purap.service.impl;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.kuali.module.purap.bo.ReceivingAddress;
import org.kuali.module.purap.dao.ReceivingAddressDao;
import org.kuali.module.purap.service.ReceivingAddressService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ReceivingAddressServiceImpl implements ReceivingAddressService {
    private static Logger LOG = Logger.getLogger(ReceivingAddressServiceImpl.class);

    private ReceivingAddressDao dao;

    public void setReceivingAddressDao(ReceivingAddressDao dao) {
        this.dao = dao;
    }

    /**
     * @see org.kuali.module.purap.service.ReceivingAddressService#findActiveByChartOrg(java.lang.String,java.lang.String)
     */
    public Collection<ReceivingAddress> findActiveByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findActiveByChartOrg(String,String)");
        LOG.debug("Leaving findActiveByChartOrg(String,String)");
        return dao.findActiveByChartOrg(chartCode,orgCode);
    }

    /**
     * @see org.kuali.module.purap.service.ReceivingAddressService#findDefaultByChartOrg(java.lang.String,java.lang.String)
     */
    public Collection<ReceivingAddress> findDefaultByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findDefaultByChartOrg(String,String)");
        LOG.debug("Leaving findDefaultByChartOrg(String,String)");
        return dao.findDefaultByChartOrg(chartCode,orgCode);
    }

    /**
     * @see org.kuali.module.purap.service.ReceivingAddressService#findUniqueDefaultByChartOrg(java.lang.String,java.lang.String)
     */
    public ReceivingAddress findUniqueDefaultByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering findUniqueDefaultByChartOrg(String,String)");
        Collection<ReceivingAddress> addresses  = findDefaultByChartOrg(chartCode,orgCode);      
        if (addresses != null ) {
            Iterator iter = addresses.iterator();
            if (iter.hasNext()) {
                LOG.debug("Leaving findUniqueDefaultByChartOrg(String,String)");       
                return (ReceivingAddress)iter.next();
            }
        }
        LOG.debug("Leaving findUniqueDefaultByChartOrg(String,String)");       
        return null;
        //TODO what if more than one is found? throw an exception
    }
    
    /**
     * @see org.kuali.module.purap.service.ReceivingAddressService#countActiveByChartOrg(java.lang.String,java.lang.String)
     */
    public int countActiveByChartOrg(String chartCode, String orgCode) {
        LOG.debug("Entering countActiveByChartOrg(String,String)");
        LOG.debug("Leaving countActiveByChartOrg(String,String)");
        return dao.countActiveByChartOrg(chartCode,orgCode);
    }

}
