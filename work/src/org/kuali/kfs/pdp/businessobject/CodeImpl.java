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
 * Created on Jun 29, 2004
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
 * @author jsissom
 */
public abstract class CodeImpl implements Code, UserRequired, Serializable, PersistenceBrokerAware {
    private String code;
    private String description;
    private Timestamp lastUpdate;
    private UniversalUser lastUpdateUser;
    private String lastUpdateUserId;
    private Integer version;

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

    public boolean equals(Object obj) {
        if (!(obj instanceof Code)) {
            return false;
        }
        Code tc = (Code) obj;
        return new EqualsBuilder().append(code, tc.getCode()).isEquals();
    }

    /**
     * @return Returns the code.
     */
    public String getCode() {
        return code;
    }

    public String getData() {
        return code + "~" + description;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * These need to be implemented in classes that extend this one. These are used to make sure the hash value is unique for each
     * value.
     * 
     * @return
     */
    protected abstract int getHashValue1();

    protected abstract int getHashValue2();

    /**
     * @return Returns the lastUpdate.
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @return Returns the version.
     */
    public Integer getVersion() {
        return version;
    }

    public int hashCode() {
        return new HashCodeBuilder(getHashValue1(), getHashValue2()).append(code).toHashCode();
    }

    /**
     * @param code The code to set.
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param lastUpdate The lastUpdate to set.
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @param ojbVerNbr The ojbVerNbr to set.
     */
    public void setVersion(Integer ver) {
        this.version = ver;
    }

    public String toString() {
        return new ToStringBuilder(this).append("code", this.code).append("description", this.description).toString();
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
