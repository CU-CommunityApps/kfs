/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/unit/src/org/kuali/kfs/sys/dataaccess/TestUniversityDateDao.java,v $
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
package org.kuali.module.gl.dao.ojb;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.test.KualiTestBase;
import org.springframework.beans.factory.BeanFactory;

/**
 * 
 * 
 */
public class TestUniversityDateDao extends KualiTestBase {

    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public void testGetByPrimaryKey() throws Exception {
        BeanFactory factory = SpringServiceLocator.getBeanFactory();
        assertNotNull("Factory shouldn't be null", factory);

        UniversityDateDao dao = (UniversityDateDao) factory.getBean("universityDateDao");
        assertNotNull("Dao shouldn't be null", dao);

        Date missing = sdf.parse("01/01/1901");
        UniversityDate notexist = dao.getByPrimaryKey(new java.sql.Date(missing.getTime()));
        assertNull("01/01/1901 shouldn't exist in table", notexist);

        Date notMissing = sdf.parse("08/14/1993");
        UniversityDate exist = dao.getByPrimaryKey(new java.sql.Date(notMissing.getTime()));
        assertNotNull("08/14/1993 should exist in table", exist);
    }

    public void testGetFirstLastFiscalYearDates() throws Exception {
        BeanFactory factory = SpringServiceLocator.getBeanFactory();
        assertNotNull("Factory shouldn't be null", factory);

        UniversityDateDao dao = (UniversityDateDao) factory.getBean("universityDateDao");
        assertNotNull("Dao shouldn't be null", dao);

        UniversityDate firstFiscalYearDate = dao.getFirstFiscalYearDate(new Integer(2007));
        assertEquals("07/01/2006", sdf.format(firstFiscalYearDate.getUniversityDate()));

        UniversityDate lastFiscalYearDate = dao.getLastFiscalYearDate(new Integer(2007));
        assertEquals("06/30/2007", sdf.format(lastFiscalYearDate.getUniversityDate()));
    }
}
