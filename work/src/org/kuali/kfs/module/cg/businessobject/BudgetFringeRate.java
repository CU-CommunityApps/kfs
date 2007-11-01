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
package org.kuali.module.kra.budget.bo;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * Budget Fringe Rate Business Object
 */
public class BudgetFringeRate extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = -1305514388024735249L;
    private String documentNumber;
    private String institutionAppointmentTypeCode;
    private KualiDecimal contractsAndGrantsFringeRateAmount;
    private KualiDecimal institutionCostShareFringeRateAmount;
    private Timestamp budgetLastUpdateTimestamp;

    private AppointmentType appointmentType;


    /**
     * 
     */
    public BudgetFringeRate() {
        super();
        appointmentType = new AppointmentType();
    }

    public BudgetFringeRate(String documentNumber, String institutionAppointmentTypeCode) {
        this();
        this.documentNumber = documentNumber;
        this.institutionAppointmentTypeCode = institutionAppointmentTypeCode;
    }

    public BudgetFringeRate(String documentNumber, String institutionAppointmentTypeCode, KualiDecimal contractsAndGrantsFringeRateAmount, KualiDecimal institutionCostShareFringeRateAmount, AppointmentType appointmentType) {
        this(documentNumber, institutionAppointmentTypeCode, contractsAndGrantsFringeRateAmount, institutionCostShareFringeRateAmount, appointmentType, null, null);
    }

    public BudgetFringeRate(String documentNumber, AppointmentType appointmentType) {
        this(documentNumber, appointmentType.getAppointmentTypeCode(), appointmentType.getFringeRateAmount(), appointmentType.getCostShareFringeRateAmount(), appointmentType, null, null);
    }


    public BudgetFringeRate(String documentNumber, String institutionAppointmentTypeCode, KualiDecimal contractsAndGrantsFringeRateAmount, KualiDecimal institutionCostShareFringeRateAmount, AppointmentType appointmentType, String objectId, Long versionNumber) {
        super();
        this.documentNumber = documentNumber;
        this.institutionAppointmentTypeCode = institutionAppointmentTypeCode;
        this.contractsAndGrantsFringeRateAmount = contractsAndGrantsFringeRateAmount;
        this.institutionCostShareFringeRateAmount = institutionCostShareFringeRateAmount;
        this.appointmentType = appointmentType;
        setObjectId(objectId);
        setVersionNumber(versionNumber);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringBuilder(java.util.LinkedHashMap)
     */
    protected String toStringBuilder(LinkedHashMap fieldValues) {
        // TODO Auto-generated method stub
        return super.toStringBuilder(fieldValues);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("institutionAppointmentTypeCode", this.institutionAppointmentTypeCode);
        return m;
    }

    /*
     * (non-Javadoc) Doesn't compare timestamps
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = true;

        if (ObjectUtils.isNotNull(obj) && obj instanceof BudgetFringeRate) {
            BudgetFringeRate objCompare = (BudgetFringeRate) obj;
            equals &= this.documentNumber.equals(objCompare.getDocumentNumber());
            equals &= this.institutionAppointmentTypeCode.equals(objCompare.getInstitutionAppointmentTypeCode());
            if (this.contractsAndGrantsFringeRateAmount == null && objCompare.getContractsAndGrantsFringeRateAmount() == null) {
            }
            else {
                equals &= this.contractsAndGrantsFringeRateAmount != null && objCompare.getContractsAndGrantsFringeRateAmount() != null && this.contractsAndGrantsFringeRateAmount.equals(objCompare.getContractsAndGrantsFringeRateAmount());
            }
            if (this.institutionCostShareFringeRateAmount == null && objCompare.getInstitutionCostShareFringeRateAmount() == null) {
            }
            else {
                equals &= this.institutionCostShareFringeRateAmount != null && objCompare.getInstitutionCostShareFringeRateAmount() != null && this.institutionCostShareFringeRateAmount.equals(objCompare.getInstitutionCostShareFringeRateAmount());
            }
        }

        return equals;
    }

    /**
     * @return Returns the budgetLastUpdate.
     */
    public Timestamp getBudgetLastUpdateTimestamp() {
        return budgetLastUpdateTimestamp;
    }

    /**
     * @param budgetLastUpdate The budgetLastUpdate to set.
     */
    public void setBudgetLastUpdateTimestamp(Timestamp budgetLastUpdateTimestamp) {
        this.budgetLastUpdateTimestamp = budgetLastUpdateTimestamp;
    }

    /**
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @return Returns the contractsAndGrantsFringeRateAmount.
     */
    public KualiDecimal getContractsAndGrantsFringeRateAmount() {
        return contractsAndGrantsFringeRateAmount;
    }

    /**
     * @param contractsAndGrantsFringeRateAmount The contractsAndGrantsFringeRateAmount to set.
     */
    public void setContractsAndGrantsFringeRateAmount(KualiDecimal contractsAndGrantsFringeRateAmount) {
        this.contractsAndGrantsFringeRateAmount = contractsAndGrantsFringeRateAmount;
    }

    /**
     * @return Returns the institutionAppointmentTypeCode.
     */
    public String getInstitutionAppointmentTypeCode() {
        return institutionAppointmentTypeCode;
    }

    /**
     * @param institutionAppointmentTypeCode The institutionAppointmentTypeCode to set.
     */
    public void setInstitutionAppointmentTypeCode(String institutionAppointmentTypeCode) {
        this.institutionAppointmentTypeCode = institutionAppointmentTypeCode;
    }

    /**
     * @return Returns the institutionCostShareFringeRateAmount.
     */
    public KualiDecimal getInstitutionCostShareFringeRateAmount() {
        return institutionCostShareFringeRateAmount;
    }

    /**
     * @param institutionCostShareFringeRateAmount The institutionCostShareFringeRateAmount to set.
     */
    public void setInstitutionCostShareFringeRateAmount(KualiDecimal institutionCostShareFringeRateAmount) {
        this.institutionCostShareFringeRateAmount = institutionCostShareFringeRateAmount;
    }

    /**
     * @return Returns the appointmentType.
     */
    public AppointmentType getAppointmentType() {
        return appointmentType;
    }

    /**
     * @param appointmentType The appointmentType to set.
     */
    public void setAppointmentType(AppointmentType appointmentType) {
        this.appointmentType = appointmentType;
    }

    public KualiDecimal getAppointmentTypeFringeRateAmount() {
        return this.appointmentType.getFringeRateAmount();
    }

    public KualiDecimal getAppointmentTypeCostShareFringeRateAmount() {
        return this.appointmentType.getCostShareFringeRateAmount();
    }
}