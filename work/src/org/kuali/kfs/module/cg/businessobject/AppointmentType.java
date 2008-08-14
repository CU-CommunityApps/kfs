/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.businessobject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.ojb.broker.PersistenceBroker;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Account Business Object
 */
public class AppointmentType extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 5817907435877665832L;
    private String appointmentTypeCode;
    private String appointmentTypeDescription;
    private String appointmentTypeAbbrieviation;
    private KualiDecimal fringeRateAmount;
    private KualiDecimal costShareFringeRateAmount;
    private Timestamp lastUpdate;
    private boolean displayGrid;
    private boolean active;
    private List appointmentTypeEffectiveDateItems = new ArrayList();
    private String relatedAppointmentTypeCode;
    private AppointmentType relatedAppointmentType;

    /*******************************************************************************************************************************
     * 
     * 
     */
    public AppointmentType() {
    }

    public AppointmentType(String appointmentTypeCode) {
        this();
        this.appointmentTypeCode = appointmentTypeCode;
    }


    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return Returns the appointmentTypeAbbrieviation.
     */
    public String getAppointmentTypeAbbrieviation() {
        return appointmentTypeAbbrieviation;
    }

    /**
     * @param appointmentTypeAbbrieviation The appointmentTypeAbbrieviation to set.
     */
    public void setAppointmentTypeAbbrieviation(String appointmentTypeAbbrieviation) {
        this.appointmentTypeAbbrieviation = appointmentTypeAbbrieviation;
    }

    /**
     * @return Returns the appointmentTypeDescription.
     */
    public String getAppointmentTypeDescription() {
        return appointmentTypeDescription;
    }

    /**
     * @param appointmentTypeDescription The appointmentTypeDescription to set.
     */
    public void setAppointmentTypeDescription(String appointmentTypeDescription) {
        this.appointmentTypeDescription = appointmentTypeDescription;
    }

    /**
     * @return Returns the costShareFringeRateAmount.
     */
    public KualiDecimal getCostShareFringeRateAmount() {
        return costShareFringeRateAmount;
    }

    /**
     * @param costShareFringeRateAmount The costShareFringeRateAmount to set.
     */
    public void setCostShareFringeRateAmount(KualiDecimal costShareFringeRateAmount) {
        this.costShareFringeRateAmount = costShareFringeRateAmount;
    }


    /**
     * @return Returns the fringeRateAmount.
     */
    public KualiDecimal getFringeRateAmount() {
        return fringeRateAmount;
    }

    /**
     * @param fringeRateAmount The fringeRateAmount to set.
     */
    public void setFringeRateAmount(KualiDecimal fringeRateAmount) {
        this.fringeRateAmount = fringeRateAmount;
    }

    /**
     * @return Returns the appointmentTypeCode.
     */
    public String getAppointmentTypeCode() {
        return appointmentTypeCode;
    }

    /**
     * @return Returns the lastUpdate.
     */
    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void beforeInsert(PersistenceBroker persistenceBroker) {
        super.beforeInsert(persistenceBroker);
        this.lastUpdate = SpringContext.getBean(DateTimeService.class).getCurrentTimestamp();
    }

    public void beforeUpdate(PersistenceBroker persistenceBroker) {
        super.beforeUpdate(persistenceBroker);
        this.lastUpdate = SpringContext.getBean(DateTimeService.class).getCurrentTimestamp();
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("appointmentTypeCode", this.appointmentTypeCode);

        return m;
    }

    /**
     * Sets the appointmentTypeCode attribute value.
     * 
     * @param appointmentTypeCode The appointmentTypeCode to set.
     */
    public void setAppointmentTypeCode(String appointmentTypeCode) {
        this.appointmentTypeCode = appointmentTypeCode;
    }

    /**
     * Sets the lastUpdate attribute value.
     * 
     * @param lastUpdate The lastUpdate to set.
     */
    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Gets the appointmentTypeEffectiveDateItems attribute.
     * 
     * @return Returns the appointmentTypeEffectiveDateItems
     */
    public List getAppointmentTypeEffectiveDateItems() {
        return appointmentTypeEffectiveDateItems;
    }

    /**
     * Sets the appointmentTypeEffectiveDateItems attribute.
     * 
     * @param appointmentTypeEffectiveDateItems The appointmentTypeEffectiveDateItems to set.
     */
    public void setAppointmentTypeEffectiveDateItems(List appointmentTypeEffectiveDateItems) {
        this.appointmentTypeEffectiveDateItems = appointmentTypeEffectiveDateItems;
    }


    public boolean isDisplayGrid() {
        return displayGrid;
    }


    public void setDisplayGrid(boolean displayGrid) {
        this.displayGrid = displayGrid;
    }


    public AppointmentType getRelatedAppointmentType() {
        return relatedAppointmentType;
    }


    public void setRelatedAppointmentType(AppointmentType relatedAppointmentType) {
        this.relatedAppointmentType = relatedAppointmentType;
    }


    public String getRelatedAppointmentTypeCode() {
        return relatedAppointmentTypeCode;
    }


    public void setRelatedAppointmentTypeCode(String relatedAppointmentTypeCode) {
        this.relatedAppointmentTypeCode = relatedAppointmentTypeCode;
    }
}
