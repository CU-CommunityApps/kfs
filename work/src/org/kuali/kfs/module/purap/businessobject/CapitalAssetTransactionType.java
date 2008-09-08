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

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Capital Asset Transaction Type Business Object.
 */
public class CapitalAssetTransactionType extends PersistableBusinessObjectBase {

    private String capitalAssetTransactionTypeCode;
    private String capitalAssetTransactionTypeDescription;
    private boolean capitalAssetNonquantityDrivenAllowIndicator;
    private String capitalAssetQuantitySubtypeRequiredText;
    private String capitalAssetNonquantitySubtypeRequiredText;
    private boolean active;

    /**
     * Default constructor.
     */
    public CapitalAssetTransactionType() {

    }
    
    /**
     * Constructs a CapitalAssetTransactionType.java.
     * @param capitalAssetTransactionTypeCode
     */
    public CapitalAssetTransactionType(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public String getCapitalAssetTransactionTypeCode() {
        return capitalAssetTransactionTypeCode;
    }

    public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
        this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
    }

    public String getCapitalAssetTransactionTypeDescription() {
        return capitalAssetTransactionTypeDescription;
    }

    public void setCapitalAssetTransactionTypeDescription(String capitalAssetTransactionTypeDescription) {
        this.capitalAssetTransactionTypeDescription = capitalAssetTransactionTypeDescription;
    }

    public boolean getCapitalAssetNonquantityDrivenAllowIndicator() {
        return capitalAssetNonquantityDrivenAllowIndicator;
    }

    public void setCapitalAssetNonquantityDrivenAllowIndicator(boolean capitalAssetNonquantityDrivenAllowIndicator) {
        this.capitalAssetNonquantityDrivenAllowIndicator = capitalAssetNonquantityDrivenAllowIndicator;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("capitalAssetTransactionTypeCode", this.capitalAssetTransactionTypeCode);
        return m;
    }

	public String getCapitalAssetNonquantitySubtypeRequiredText() {
		return capitalAssetNonquantitySubtypeRequiredText;
	}

	public void setCapitalAssetNonquantitySubtypeRequiredText(
			String capitalAssetNonquantitySubtypeRequiredText) {
		this.capitalAssetNonquantitySubtypeRequiredText = capitalAssetNonquantitySubtypeRequiredText;
	}

	public String getCapitalAssetQuantitySubtypeRequiredText() {
		return capitalAssetQuantitySubtypeRequiredText;
	}

	public void setCapitalAssetQuantitySubtypeRequiredText(
			String capitalAssetQuantitySubtypeRequiredText) {
		this.capitalAssetQuantitySubtypeRequiredText = capitalAssetQuantitySubtypeRequiredText;
	}
}
