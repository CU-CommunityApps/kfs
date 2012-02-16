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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public abstract class BaseTemAccount extends PersistableBusinessObjectBase {

    private Integer accountId;
    private String name;
    private String accountNumber;
    private Date expirationDate;
    private Date effectiveDate;
    private String note;
    private Boolean active = Boolean.TRUE;

    /**
     * Gets the accountId attribute.
     * 
     * @return Returns the accountId.
     */
    @Column(name = "account_id", nullable = false, length = 19)
    public Integer getAccountId() {
        return accountId;
    }

    /**
     * Sets the accountId attribute value.
     * 
     * @param accountId The accountId to set.
     */
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    /**
     * Gets the name attribute.
     * 
     * @return Returns the name.
     */
    @Column(name = "name", nullable = false, length = 50)
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute value.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    @Column(name = "account_nbr", nullable = false, length = 50)
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the effectiveDate attribute.
     * 
     * @return Returns the expirationDate.
     */
    @Column(name = "effective_date", nullable = true)
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the expirationDate attribute value.
     * 
     * @param expirationDate The expirationDate to set.
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * Gets the expirationDate attribute.
     * 
     * @return Returns the expirationDate.
     */
    @Column(name = "exp_date", nullable = true)
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Sets the expirationDate attribute value.
     * 
     * @param expirationDate The expirationDate to set.
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Gets the note attribute.
     * 
     * @return Returns the note.
     */
    @Column(name = "note", nullable = true, length = 500)
    public String getNote() {
        return note;
    }

    /**
     * Sets the note attribute value.
     * 
     * @param note The note to set.
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    @Column(name = "ACTV_IND", nullable = false, length = 1)
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("accountId", accountId);
        map.put("name", name);
        map.put("accountNumber", accountNumber);
        
        return map;
    }
}
