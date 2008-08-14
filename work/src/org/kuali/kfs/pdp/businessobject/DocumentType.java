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
 * Created on Jul 9, 2004
 *
 */
package org.kuali.kfs.pdp.businessobject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.UserNotFoundException;
import org.kuali.rice.kns.service.UniversalUserService;

/**
 * @author delyea
 * @hibernate.class table="PDP.PDP_DOC_TYP_CD_T"
 */
public class DocumentType implements UserRequired, Serializable, PersistenceBrokerAware {

    private Integer id; // DOC_TYP_ID
    private String fsOriginCode; // FS_ORIGIN_CD
    private String fdocTypeCode; // FDOC_TYP_CD
    private Timestamp lastUpdate; // LST_UPDT_TS
    private UniversalUser lastUpdateUser;
    private String lastUpdateUserId; // LST_UPDT_USR_ID
    private Integer version; // VER_NBR
    private TransactionType transactionType;
    private DisbursementType disbursementType;

    public DocumentType() {
        super();
    }

    public UniversalUser getLastUpdateUser() {
        return lastUpdateUser;
    }

    public void setLastUpdateUser(UniversalUser s) {
        if (s != null) {
            this.lastUpdateUserId = s.getPersonUniversalIdentifier();
        }
        else {
            this.lastUpdateUserId = null;
        }
        this.lastUpdateUser = s;
    }

    public String getLastUpdateUserId() {
        return lastUpdateUserId;
    }

    public void setLastUpdateUserId(String lastUpdateUserId) {
        this.lastUpdateUserId = lastUpdateUserId;
    }

    public void updateUser(UniversalUserService userService) throws UserNotFoundException {
        UniversalUser u = userService.getUniversalUser(lastUpdateUserId);
        setLastUpdateUser(u);
    }

    /**
     * @hibernate.id column="DOC_TYP_ID" generator-class="sequence"
     * @hibernate.generator-param name="sequence" value="PDP.PDP_DOC_TYP_ID_SEQ"
     * @return Returns the documentTypeId.
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param documentTypeId The documentTypeId to set.
     */
    public void setId(Integer documentTypeId) {
        this.id = documentTypeId;
    }

    /**
     * @return
     * @hibernate.property column="LST_UPDT_TS" not-null="true"
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return
     * @hibernate.property column="FDOC_TYP_CD" length="4"
     */
    public String getFdocTypeCode() {
        return fdocTypeCode;
    }

    /**
     * @return
     * @hibernate.version column="VER_NBR" not-null="true"
     */
    public Integer getVersion() {
        return version;
    }


    /**
     * @return
     * @hibernate.property class="edu.iu.uis.pdp.bo.DisbursementType" column="DISB_TYP_CD" not-null="true"
     */
    public DisbursementType getDisbursementType() {
        return disbursementType;
    }

    /**
     * @return
     * @hibernate.property column="FS_ORIGIN_CD" length="2"
     */
    public String getFsOriginCode() {
        return fsOriginCode;
    }

    /**
     * @return
     * @hibernate.many-to-one class="edu.iu.uis.pdp.bo.TransactionType" column="TRN_TYP_CD" not-null="true"
     */
    public TransactionType getTransactionType() {
        return transactionType;
    }

    /**
     * @param string
     */
    public void setDisbursementType(DisbursementType disbursementType) {
        this.disbursementType = disbursementType;
    }

    /**
     * @param string
     */
    public void setFdocTypeCode(String string) {
        fdocTypeCode = string;
    }

    /**
     * @param string
     */
    public void setFsOriginCode(String string) {
        fsOriginCode = string;
    }

    /**
     * @param timestamp
     */
    public void setLastUpdate(Timestamp timestamp) {
        lastUpdate = timestamp;
    }

    /**
     * @param string
     */
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * @param integer
     */
    public void setVersion(Integer integer) {
        version = integer;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DocumentType)) {
            return false;
        }
        DocumentType o = (DocumentType) obj;
        return new EqualsBuilder().append(id, o.getId()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder(83, 47).append(id).toHashCode();
    }

    public String toString() {
        return new ToStringBuilder(this).append("id", this.id).toString();
    }

    public void beforeInsert(PersistenceBroker broker) throws PersistenceBrokerException {
        lastUpdate = new Timestamp((new Date()).getTime());
    }

    public void afterInsert(PersistenceBroker broker) throws PersistenceBrokerException {

    }

    public void beforeUpdate(PersistenceBroker broker) throws PersistenceBrokerException {
        lastUpdate = new Timestamp((new Date()).getTime());
    }

    public void afterUpdate(PersistenceBroker broker) throws PersistenceBrokerException {

    }

    public void beforeDelete(PersistenceBroker broker) throws PersistenceBrokerException {

    }

    public void afterDelete(PersistenceBroker broker) throws PersistenceBrokerException {

    }

    public void afterLookup(PersistenceBroker broker) throws PersistenceBrokerException {

    }
}
