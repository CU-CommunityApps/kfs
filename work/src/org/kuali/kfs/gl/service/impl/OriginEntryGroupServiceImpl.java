/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.service.OriginEntryGroupService;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: OriginEntryGroupServiceImpl.java,v 1.21 2006-08-08 00:39:12 schoo Exp $
 */
public class OriginEntryGroupServiceImpl implements OriginEntryGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupServiceImpl.class);

    private OriginEntryGroupDao originEntryGroupDao;
    private OriginEntryDao originEntryDao;
    private DateTimeService dateTimeService;

    public OriginEntryGroupServiceImpl() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getBackupGroups(java.sql.Date)
     */
    public Collection getBackupGroups(Date backupDate) {
        LOG.debug("getBackupGroups() started");

        return originEntryGroupDao.getBackupGroups(backupDate);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#createBackupGroup()
     */
    public void createBackupGroup() {
        LOG.debug("createBackupGroup() started");

        // Get the groups that need to be added
        Date today = dateTimeService.getCurrentSqlDate();
        Collection groups = originEntryGroupDao.getGroupsToBackup(today);

        // Create the new group
        OriginEntryGroup backupGroup = this.createGroup(today, OriginEntrySource.BACKUP, true, true, true);

        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup group = (OriginEntryGroup)iter.next();

            originEntryGroupDao.copyGroup(group,backupGroup);

            group.setProcess(false);
            group.setScrub(false);
            originEntryGroupDao.save(group);
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#deleteOlderGroups(int)
     */
    public void deleteOlderGroups(int days) {
        LOG.debug("deleteOlderGroups() started");

        Calendar today = dateTimeService.getCurrentCalendar();
        today.add(Calendar.DAY_OF_MONTH, 0 - days);

        Collection groups = originEntryGroupDao.getOlderGroups(new java.sql.Date(today.getTime().getTime()));

        if ( groups.size() > 0 ) {
            originEntryDao.deleteGroups(groups);
            originEntryGroupDao.deleteGroups(groups);
        }
    }

    /**
     * 
     * @return the List of all origin entry groups that have a process indicator of false. collection is returned read-only.
     */
    public Collection getOriginEntryGroupsPendingProcessing() {
        LOG.debug("getOriginEntryGroupsPendingProcessing() started");

        Map criteria = new HashMap();
        criteria.put("process", Boolean.FALSE);
        Collection returnCollection = new ArrayList();
        returnCollection = originEntryGroupDao.getMatchingGroups(criteria);
        return returnCollection;
    }

    /**
     * Find an OriginEntryGroup by id.
     * 
     * @param groupId
     * @return the OriginEntryGroup with the given id.
     */
    public OriginEntryGroup getOriginEntryGroup(String groupId) {
        LOG.debug("getOriginEntryGroup() started");

        Map criteria = new HashMap();
        // shawn
        criteria.put("id", groupId);
        Collection matches = originEntryGroupDao.getMatchingGroups(criteria);
        Iterator i = matches.iterator();
        if (i.hasNext()) {
            return (OriginEntryGroup) i.next();
        }
        return null;
    }
    
    public Collection getAllOriginEntryGroup(){
        LOG.debug("getAllOriginEntryGroup() started");
        Map criteria = new HashMap();

        return originEntryGroupDao.getMatchingGroups(criteria);
    }
    

    /**
     * Create a new OriginEntryGroup and persist it to the database.
     */
    public OriginEntryGroup createGroup(Date date, String sourceCode, boolean valid, boolean process, boolean scrub) {
        LOG.debug("createGroup() started");

        OriginEntryGroup oeg = new OriginEntryGroup();
        oeg.setDate(date);
        oeg.setProcess(Boolean.valueOf(process));
        oeg.setScrub(Boolean.valueOf(scrub));
        oeg.setSourceCode(sourceCode);
        oeg.setValid(Boolean.valueOf(valid));

        originEntryGroupDao.save(oeg);

        return oeg;
    }

    /**
     * Get all non-ICR-related OriginEntryGroups waiting to be posted as of postDate.
     */
    public Collection getGroupsToPost() {
        LOG.debug("getGroupsToPost() started");

        return originEntryGroupDao.getPosterGroups(OriginEntrySource.SCRUBBER_VALID);
    }

    /**
     * Get all ICR-related OriginEntryGroups waiting to be posted as of postDate.
     */
    public Collection getIcrGroupsToPost() {
        LOG.debug("getIcrGroupsToPost() started");

        return originEntryGroupDao.getPosterGroups(OriginEntrySource.ICR_POSTER_VALID);
    }

    /**
     * An alias for OriginEntryGroupDao.getScrubberGroups().
     * 
     * @param scrubDate
     */
    public Collection getGroupsToBackup(Date scrubDate) {
        LOG.debug("getGroupsToScrub() started");

        return originEntryGroupDao.getGroupsToBackup(scrubDate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.module.gl.service.OriginEntryGroupService#getMatchingGroups(java.util.Map)
     */
    public Collection getMatchingGroups(Map criteria) {
        LOG.debug("getMatchingGroups() started");

        return originEntryGroupDao.getMatchingGroups(criteria);
    }

    /**
     * Persist an OriginEntryGroup to the database.
     * 
     * @param originEntryGroup
     */
    public void save(OriginEntryGroup originEntryGroup) {
        LOG.debug("save() started");

        originEntryGroupDao.save(originEntryGroup);
    }

    public void setOriginEntryGroupDao(OriginEntryGroupDao oegd) {
        originEntryGroupDao = oegd;
    }

    public void setOriginEntryDao(OriginEntryDao oed) {
        originEntryDao = oed;
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }
    
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id){
        OriginEntryGroup oeg = null;
        try {oeg = originEntryGroupDao.getExactMatchingEntryGroup(id);}
        catch (Exception e){
            
        }
        return oeg;
        
    }
}
