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
/*
 * Created on Aug 7, 2004
 *
 */
package org.kuali.kfs.pdp.dataaccess.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.pdp.businessobject.Code;
import org.kuali.kfs.pdp.businessobject.UserRequired;
import org.kuali.kfs.pdp.dataaccess.ReferenceDao;
import org.kuali.kfs.pdp.exception.ConfigurationError;


/**
 * @author jsissom
 */
public class ReferenceDaoOjb extends PlatformAwareDaoBaseOjb implements ReferenceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReferenceDaoOjb.class);

    private UniversalUserService userService;

    public ReferenceDaoOjb() {
        super();
    }

    // Inject
    public void setUniversalUserService(UniversalUserService us) {
        userService = us;
    }

    private void updateUser(List l) {
        for (Iterator iter = l.iterator(); iter.hasNext();) {
            updateUser((Code) iter.next());
        }
    }

    private void updateUser(Code b) {
        UserRequired ur = (UserRequired) b;
        try {
            ur.updateUser(userService);
        }
        catch (UserNotFoundException e) {
            b.setLastUpdateUser(null);
        }
    }

    private Class getClass(String name) {
        String fullName = "org.kuali.kfs.pdp.businessobject." + name;

        try {
            return Class.forName(fullName);
        }
        catch (ClassNotFoundException e) {
            throw new ConfigurationError("Unknown type: " + name);
        }
    }

    public Code getCode(String type, String key) {
        LOG.debug("getCode() for " + type);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("code", key);

        Code code = (Code) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(getClass(type), criteria));
        if (code != null) {
            updateUser(code);
        }
        return code;
    }

    public List getAll(String type) {
        LOG.debug("getAll() for " + type);

        QueryByCriteria qbc = new QueryByCriteria(getClass(type));
        qbc.addOrderBy("description", true);

        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        updateUser(l);
        return l;
    }

    public Map getAllMap(String type) {
        LOG.debug("getAllMap() for " + type);

        Map hm = new HashMap();

        for (Iterator iter = getAll(type).iterator(); iter.hasNext();) {
            Code element = (Code) iter.next();
            hm.put(element.getCode(), element);
        }
        return hm;
    }

    public Code addCode(String type, String code, String description, UniversalUser u) {
        Class clazz = getClass(type);
        Code c;

        try {
            c = (Code) clazz.newInstance();
        }
        catch (InstantiationException e) {
            LOG.error("addCode() Can't create instance for " + type, e);
            throw new ConfigurationError("Unable to create instance of " + type);
        }
        catch (IllegalAccessException e) {
            LOG.error("addCode() Can't create instance for " + type, e);
            throw new ConfigurationError("Unable to create instance of " + type);
        }

        c.setCode(code);
        c.setDescription(description);
        c.setLastUpdateUser(u);

        getPersistenceBrokerTemplate().store(c);
        return c;
    }

    // FROM TAFKAT
    public void updateCode(String code, String description, String type, UniversalUser u) {
        LOG.debug("updateCode() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("code", code);

        Code c = (Code) getPersistenceBrokerTemplate().getObjectByQuery(new QueryByCriteria(getClass(type), criteria));

        c.setDescription(description);
        c.setLastUpdateUser(u);

        getPersistenceBrokerTemplate().store(c);

    }

    // OLD PDP
    public void updateCode(Code item, UniversalUser u) {
        LOG.debug("updateCode() started");

        item.setLastUpdateUser(u);

        getPersistenceBrokerTemplate().store(item);
    }

    public void deleteCode(Code item) {
        LOG.debug("deleteCode() started");

        getPersistenceBrokerTemplate().delete(item);
    }
}
