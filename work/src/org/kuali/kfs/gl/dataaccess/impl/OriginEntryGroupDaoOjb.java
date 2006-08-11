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
package org.kuali.module.gl.dao.ojb;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: OriginEntryGroupDaoOjb.java,v 1.13 2006-08-11 17:11:54 schoo Exp $
 * TODO Oracle Specific code here
 */
public class OriginEntryGroupDaoOjb extends PersistenceBrokerDaoSupport implements OriginEntryGroupDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryGroupDaoOjb.class);

    public OriginEntryGroupDaoOjb() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#copyGroup(org.kuali.module.gl.bo.OriginEntryGroup, org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void copyGroup(OriginEntryGroup fromGroup,OriginEntryGroup toGroup) {
        LOG.debug("copyGroup() started");

        String sql = "insert into GL_ORIGIN_ENTRY_T (ORIGIN_ENTRY_ID, OBJ_ID, VER_NBR, ORIGIN_ENTRY_GRP_ID, ACCOUNT_NBR,FDOC_NBR, FDOC_REF_NBR, FDOC_REF_TYP_CD, " +
                "FDOC_REVERSAL_DT, FDOC_TYP_CD, FIN_BALANCE_TYP_CD, FIN_COA_CD,FIN_OBJ_TYP_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FS_ORIGIN_CD, FS_REF_ORIGIN_CD, " +
                "ORG_DOC_NBR, ORG_REFERENCE_ID,PROJECT_CD, SUB_ACCT_NBR, TRANSACTION_DT, TRN_DEBIT_CRDT_CD, TRN_ENCUM_UPDT_CD, TRN_ENTR_SEQ_NBR, TRN_LDGR_ENTR_AMT," +
                "TRN_LDGR_ENTR_DESC, UNIV_FISCAL_PRD_CD, UNIV_FISCAL_YR, TRN_SCRBBR_OFST_GEN_IND, BDGT_YR) " +
                "select gl_origin_entry_t_seq.nextval, sys_guid(), 1, " + toGroup.getId() + ", ACCOUNT_NBR, FDOC_NBR, FDOC_REF_NBR, FDOC_REF_TYP_CD, FDOC_REVERSAL_DT, " + 
                "FDOC_TYP_CD, FIN_BALANCE_TYP_CD, FIN_COA_CD, FIN_OBJ_TYP_CD, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FS_ORIGIN_CD, FS_REF_ORIGIN_CD, ORG_DOC_NBR,ORG_REFERENCE_ID, " +
                "PROJECT_CD, SUB_ACCT_NBR, TRANSACTION_DT, TRN_DEBIT_CRDT_CD, TRN_ENCUM_UPDT_CD, TRN_ENTR_SEQ_NBR, TRN_LDGR_ENTR_AMT,TRN_LDGR_ENTR_DESC, UNIV_FISCAL_PRD_CD, " +
                "UNIV_FISCAL_YR, TRN_SCRBBR_OFST_GEN_IND, BDGT_YR from GL_ORIGIN_ENTRY_T where ORIGIN_ENTRY_GRP_ID = " + fromGroup.getId();

        sqlCommand(sql);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getOlderGroups(Date)
     */
    public Collection<OriginEntryGroup> getOlderGroups(Date day) {
        LOG.debug("getOlderGroups() started");

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan("date", day);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(OriginEntryGroup.class,criteria));
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#deleteGroups(java.util.Collection)
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups) {
        LOG.debug("deleteGroups() started");

        List ids = new ArrayList();
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup)iter.next();
            ids.add(element.getId());
        }
        Criteria criteria = new Criteria();
        criteria.addIn("id",ids);

        getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(OriginEntryGroup.class,criteria));
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getMatchingGroups(java.util.Map)
     */
    public Collection getMatchingGroups(Map searchCriteria) {
        LOG.debug("getPendingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iterator = searchCriteria.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next().toString();
            criteria.addEqualTo(key, searchCriteria.get(key));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getPosterGroups(java.lang.String)
     */
    public Collection getPosterGroups(String groupSourceCode) {
        LOG.debug("getPosterGroups() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("sourceCode", groupSourceCode);
        criteria.addEqualTo("process", Boolean.TRUE);
        criteria.addEqualTo("valid", Boolean.TRUE);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getBackupGroups(java.sql.Date)
     */
    public Collection getBackupGroups(Date groupDate) {
        LOG.debug("getGroupsToBackup() started");

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan("date", groupDate);
        criteria.addEqualTo("sourceCode", OriginEntrySource.BACKUP);
        criteria.addEqualTo("scrub", Boolean.TRUE);
        criteria.addEqualTo("process", Boolean.TRUE);
        criteria.addEqualTo("valid", Boolean.TRUE);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getScrubberGroups(java.sql.Date)
     */
    public Collection getGroupsToBackup(Date groupDate) {
        LOG.debug("getScrubberGroups() started");

        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan("date", groupDate);
        criteria.addEqualTo("scrub", Boolean.TRUE);
        criteria.addEqualTo("process", Boolean.TRUE);
        criteria.addEqualTo("valid", Boolean.TRUE);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntryGroup.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#save(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void save(OriginEntryGroup group) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(group);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getExactMatchingEntryGroup(java.lang.Integer)
     */
    public OriginEntryGroup getExactMatchingEntryGroup(Integer id) {
        LOG.debug("getMatchingEntries() started");
        return (OriginEntryGroup) getPersistenceBrokerTemplate().getObjectById(OriginEntryGroup.class, id);
    }

    /**
     * Run a sql command
     * 
     * @param sql
     * @return
     */
    private int sqlCommand(String sql) {
        LOG.info("sqlCommand() started: " + sql);

        Statement stmt = null;

        try {
            Connection c = getPersistenceBroker(true).serviceConnectionManager().getConnection();
            stmt = c.createStatement();
            return stmt.executeUpdate(sql);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to execute: " + e.getMessage());
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to close connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryGroupDao#getRecentGroups(Date)
     */
    public Collection<OriginEntryGroup> getRecentGroups(Date day) {
        LOG.debug("getOlderGroups() started");

        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan("date", day);

        return getPersistenceBrokerTemplate().getCollectionByQuery(QueryFactory.newQuery(OriginEntryGroup.class,criteria));
    }
    
    
}
