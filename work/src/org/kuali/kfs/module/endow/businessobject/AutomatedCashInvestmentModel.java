/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

public class AutomatedCashInvestmentModel extends PersistableBusinessObjectBase implements Inactivateable {

    private Integer aciModelID;
    private String aciModelName;
    private String ipIndicator;
    private String investment1SecurityID;
    private String investment1RegistrationCode;
    private BigDecimal investment1Percent;
    private String investment2SecurityID;
    private String investment2RegistrationCode;
    private BigDecimal investment2Percent;
    private String investment3SecurityID;
    private String investment3RegistrationCode;
    private BigDecimal investment3Percent;
    private String investment4SecurityID;
    private String investment4RegistrationCode;
    private BigDecimal investment4Percent;
    private String aciFrequencyCode;
    private Date dateOfLastACIModelChange;
    private boolean active;

    private PooledFundControl investment1;
    private PooledFundControl investment2;
    private PooledFundControl investment3;
    private PooledFundControl investment4;
    private RegistrationCode investment1RegistrationCodeObj;
    private RegistrationCode investment2RegistrationCodeObj;
    private RegistrationCode investment3RegistrationCodeObj;
    private RegistrationCode investment4RegistrationCodeObj;
    private FrequencyCode aciFrequencyCodeObj;
    private IncomePrincipalIndicator ipIndicatorObj;

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.ACI_MODEL_ID, this.aciModelID);
        return m;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the aciModelID
     * 
     * @return aciModelID
     */
    public Integer getAciModelID() {
        return aciModelID;
    }

    /**
     * Sets the aciModelID
     * 
     * @param aciModelID
     */
    public void setAciModelID(Integer aciModelID) {
        this.aciModelID = aciModelID;
    }

    /**
     * Gets the aciModelName
     * 
     * @return aciModelName
     */
    public String getAciModelName() {
        return aciModelName;
    }

    /**
     * Sets the aciModelName
     * 
     * @param aciModelName
     */
    public void setAciModelName(String aciModelName) {
        this.aciModelName = aciModelName;
    }

    /**
     * Gets the ipIndicator
     * 
     * @return ipIndicator
     */
    public String getIpIndicator() {
        return ipIndicator;
    }

    /**
     * Sets the ipIndicator
     * 
     * @param ipIndicator
     */
    public void setIpIndicator(String ipIndicator) {
        this.ipIndicator = ipIndicator;
    }

    /**
     * Gets the investment1SecurityID
     * 
     * @return investment1SecurityID
     */
    public String getInvestment1SecurityID() {
        return investment1SecurityID;
    }

    /**
     * Sets the investment1SecurityID
     * 
     * @param investment1SecurityID
     */
    public void setInvestment1SecurityID(String investment1SecurityID) {
        this.investment1SecurityID = investment1SecurityID;
    }

    /**
     * Gets the investment1RegistrationCode
     * 
     * @return investment1RegistrationCode
     */
    public String getInvestment1RegistrationCode() {
        return investment1RegistrationCode;
    }

    /**
     * Sets the investment1RegistrationCode
     * 
     * @param investment1RegistrationCode
     */
    public void setInvestment1RegistrationCode(String investment1RegistrationCode) {
        this.investment1RegistrationCode = investment1RegistrationCode;
    }

    /**
     * Gets the investment1Percent
     * 
     * @return investment1Percent
     */
    public BigDecimal getInvestment1Percent() {
        return investment1Percent;
    }

    /**
     * Sets the investment1Percent
     * 
     * @param investment1Percent
     */
    public void setInvestment1Percent(BigDecimal investment1Percent) {
        if (investment1Percent == null) {
            investment1Percent = new BigDecimal(0.0000);
        }
        this.investment1Percent = investment1Percent;
    }

    /**
     * Gets the investment2SecurityID
     * 
     * @return investment2SecurityID
     */
    public String getInvestment2SecurityID() {
        return investment2SecurityID;
    }

    /**
     * Sets the investment2SecurityID
     * 
     * @param investment2SecurityID
     */
    public void setInvestment2SecurityID(String investment2SecurityID) {
        this.investment2SecurityID = investment2SecurityID;
    }

    /**
     * Gets the investment2RegistrationCode
     * 
     * @return investment2RegistrationCode
     */
    public String getInvestment2RegistrationCode() {
        return investment2RegistrationCode;
    }

    /**
     * Sets the investment2RegistrationCode
     * 
     * @param investment2RegistrationCode
     */
    public void setInvestment2RegistrationCode(String investment2RegistrationCode) {
        this.investment2RegistrationCode = investment2RegistrationCode;
    }

    /**
     * Gets the investment2Percent
     * 
     * @return investment2Percent
     */
    public BigDecimal getInvestment2Percent() {
        return investment2Percent;
    }

    /**
     * Sets the investment2Percent
     * 
     * @param investment2Percent
     */
    public void setInvestment2Percent(BigDecimal investment2Percent) {
        if (investment2Percent == null) {
            investment2Percent = new BigDecimal(0.0000);
        }
        this.investment2Percent = investment2Percent;
    }

    /**
     * Gets the investment3SecurityID
     * 
     * @return investment3SecurityID
     */
    public String getInvestment3SecurityID() {
        return investment3SecurityID;
    }

    /**
     * Sets the investment3SecurityID
     * 
     * @param investment3SecurityID
     */
    public void setInvestment3SecurityID(String investment3SecurityID) {
        this.investment3SecurityID = investment3SecurityID;
    }

    /**
     * Gets the investment3RegistrationCode
     * 
     * @return investment3RegistrationCode
     */
    public String getInvestment3RegistrationCode() {
        return investment3RegistrationCode;
    }

    /**
     * Sets the investment3RegistrationCode
     * 
     * @param investment3RegistrationCode
     */
    public void setInvestment3RegistrationCode(String investment3RegistrationCode) {
        this.investment3RegistrationCode = investment3RegistrationCode;
    }

    /**
     * Gets the investment3Percent
     * 
     * @return investment3Percent
     */
    public BigDecimal getInvestment3Percent() {
        return investment3Percent;
    }

    /**
     * Sets the investment3Percent
     * 
     * @param investment3Percent
     */
    public void setInvestment3Percent(BigDecimal investment3Percent) {
        if (investment3Percent == null) {
            investment3Percent = new BigDecimal(0.0000);
        }
        this.investment3Percent = investment3Percent;
    }

    /**
     * Gets the investment4SecurityID
     * 
     * @return investment4SecurityID
     */
    public String getInvestment4SecurityID() {
        return investment4SecurityID;
    }

    /**
     * Sets the investment4SecurityID
     * 
     * @param investment4SecurityID
     */
    public void setInvestment4SecurityID(String investment4SecurityID) {
        this.investment4SecurityID = investment4SecurityID;
    }

    /**
     * Gets the investment4RegistrationCode
     * 
     * @return investment4RegistrationCode
     */
    public String getInvestment4RegistrationCode() {
        return investment4RegistrationCode;
    }

    /**
     * Sets the investment4RegistrationCode
     * 
     * @param investment4RegistrationCode
     */
    public void setInvestment4RegistrationCode(String investment4RegistrationCode) {
        this.investment4RegistrationCode = investment4RegistrationCode;
    }

    /**
     * Gets the investment4Percent
     * 
     * @return investment4Percent
     */
    public BigDecimal getInvestment4Percent() {
        return investment4Percent;
    }

    /**
     * Sets the investment4Percent
     * 
     * @param investment4Percent
     */
    public void setInvestment4Percent(BigDecimal investment4Percent) {
        if (investment4Percent == null) {
            investment4Percent = new BigDecimal(0.0000);
        }
        this.investment4Percent = investment4Percent;
    }

    /**
     * Gets the aciFrequencyCode
     * 
     * @return aciFrequencyCode
     */
    public String getAciFrequencyCode() {
        return aciFrequencyCode;
    }

    /**
     * Sets the aciFrequencyCode
     * 
     * @param aciFrequencyCode
     */
    public void setAciFrequencyCode(String aciFrequencyCode) {
        this.aciFrequencyCode = aciFrequencyCode;
    }

    /**
     * Gets the dateOfLastACIModelChange
     * 
     * @return dateOfLastACIModelChange
     */
    public Date getDateOfLastACIModelChange() {
        return dateOfLastACIModelChange;
    }

    /**
     * Sets the dateOfLastACIModelChange
     * 
     * @param dateOfLastACIModelChange
     */
    public void setDateOfLastACIModelChange(Date dateOfLastACIModelChange) {
        this.dateOfLastACIModelChange = dateOfLastACIModelChange;
    }

    /**
     * Gets the aciFrequencyCode Object
     * 
     * @return aciFrequencyCodeObj
     */
    public FrequencyCode getAciFrequencyCodeObj() {
        return aciFrequencyCodeObj;
    }

    /**
     * Sets the aciFrequencyCode Object
     * 
     * @param aciFrequencyCodeObj
     */
    public void setAciFrequencyCodeObj(FrequencyCode aciFrequencyCodeObj) {
        this.aciFrequencyCodeObj = aciFrequencyCodeObj;
    }

    /**
     * Gets the investment1 Pooled Fund Control
     * 
     * @return investment1
     */
    public PooledFundControl getInvestment1() {
        return investment1;
    }

    /**
     * Sets the investment1
     * 
     * @param investment1 Pooled Fund Control
     */
    public void setInvestment1(PooledFundControl investment1) {
        this.investment1 = investment1;
    }

    /**
     * Gets the investment2 Pooled Fund Control
     * 
     * @return investment2
     */
    public PooledFundControl getInvestment2() {
        return investment2;
    }

    /**
     * Sets the investment2
     * 
     * @param investment2 Pooled Fund Control
     */
    public void setInvestment2(PooledFundControl investment2) {
        this.investment2 = investment2;
    }

    /**
     * Gets the investment3 Pooled Fund Control
     * 
     * @return investment3
     */
    public PooledFundControl getInvestment3() {
        return investment3;
    }

    /**
     * Sets the investment3
     * 
     * @param investment3 Pooled Fund Control
     */
    public void setInvestment3(PooledFundControl investment3) {
        this.investment3 = investment3;
    }

    /**
     * Gets the investment4 Pooled Fund Control
     * 
     * @return investment4
     */
    public PooledFundControl getInvestment4() {
        return investment4;
    }

    /**
     * Sets the investment4
     * 
     * @param investment4 Pooled Fund Control
     */
    public void setInvestment4(PooledFundControl investment4) {
        this.investment4 = investment4;
    }

    /**
     * Gets the investment1RegistrationCodeObj
     * 
     * @return investment1RegistrationCodeObj
     */
    public RegistrationCode getInvestment1RegistrationCodeObj() {
        return investment1RegistrationCodeObj;
    }

    /**
     * Sets the investment1RegistrationCodeObj
     * 
     * @param investment1RegistrationCodeObj
     */
    public void setInvestment1RegistrationCodeObj(RegistrationCode investment1RegistrationCodeObj) {
        this.investment1RegistrationCodeObj = investment1RegistrationCodeObj;
    }

    /**
     * Gets the investment2RegistrationCodeObj
     * 
     * @return investment2RegistrationCodeObj
     */
    public RegistrationCode getInvestment2RegistrationCodeObj() {
        return investment2RegistrationCodeObj;
    }

    /**
     * Sets the investment2RegistrationCodeObj
     * 
     * @param investment2RegistrationCodeObj
     */
    public void setInvestment2RegistrationCodeObj(RegistrationCode investment2RegistrationCodeObj) {
        this.investment2RegistrationCodeObj = investment2RegistrationCodeObj;
    }

    /**
     * Gets the investment3RegistrationCodeObj
     * 
     * @return investment3RegistrationCodeObj
     */
    public RegistrationCode getInvestment3RegistrationCodeObj() {
        return investment3RegistrationCodeObj;
    }

    /**
     * Sets the investment3RegistrationCodeObj
     * 
     * @param investment3RegistrationCodeObj
     */
    public void setInvestment3RegistrationCodeObj(RegistrationCode investment3RegistrationCodeObj) {
        this.investment3RegistrationCodeObj = investment3RegistrationCodeObj;
    }

    /**
     * Gets the investment4RegistrationCodeObj
     * 
     * @return investment4RegistrationCodeObj
     */
    public RegistrationCode getInvestment4RegistrationCodeObj() {
        return investment4RegistrationCodeObj;
    }

    /**
     * Sets the investment4RegistrationCodeObj
     * 
     * @param investment4RegistrationCodeObj
     */
    public void setInvestment4RegistrationCodeObj(RegistrationCode investment4RegistrationCodeObj) {
        this.investment4RegistrationCodeObj = investment4RegistrationCodeObj;
    }

    /**
     * Gets the ipIndicatorObj
     * 
     * @return ipIndicatorObj
     */
    public IncomePrincipalIndicator getIpIndicatorObj() {
        return ipIndicatorObj;
    }

    /**
     * Sets the ipIndicatorObj
     * 
     * @param ipIndicatorObj
     */
    public void setIpIndicatorObj(IncomePrincipalIndicator ipIndicatorObj) {
        this.ipIndicatorObj = ipIndicatorObj;
    }
    
    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */    
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(this.aciModelID.toString())) {
            return KFSConstants.EMPTY_STRING;
        }
        String theString = this.getAciModelID().toString() + " - " + this.getAciModelName();
        
        return theString;
    }

}
