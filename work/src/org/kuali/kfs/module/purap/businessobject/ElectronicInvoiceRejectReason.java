/*
 * Copyright 2006 The Kuali Foundation.
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

package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class ElectronicInvoiceRejectReason extends BusinessObjectBase {

	private Integer invoiceRejectReasonIdentifier;
	private Integer invoiceHeaderInformationIdentifier;
	private String invoiceRejectReasonTypeCode;
	private String invoiceRejectReasonDescription;

    private ElectronicInvoiceHeaderInformation invoiceHeaderInformation;
	private ElectronicInvoiceRejectTypeCode invoiceRejectReasonType;

	/**
	 * Default constructor.
	 */
	public ElectronicInvoiceRejectReason() {

	}

	/**
	 * Gets the invoiceRejectReasonIdentifier attribute.
	 * 
	 * @return Returns the invoiceRejectReasonIdentifier
	 * 
	 */
	public Integer getInvoiceRejectReasonIdentifier() { 
		return invoiceRejectReasonIdentifier;
	}

	/**
	 * Sets the invoiceRejectReasonIdentifier attribute.
	 * 
	 * @param invoiceRejectReasonIdentifier The invoiceRejectReasonIdentifier to set.
	 * 
	 */
	public void setInvoiceRejectReasonIdentifier(Integer invoiceRejectReasonIdentifier) {
		this.invoiceRejectReasonIdentifier = invoiceRejectReasonIdentifier;
	}


	/**
	 * Gets the invoiceHeaderInformationIdentifier attribute.
	 * 
	 * @return Returns the invoiceHeaderInformationIdentifier
	 * 
	 */
	public Integer getInvoiceHeaderInformationIdentifier() { 
		return invoiceHeaderInformationIdentifier;
	}

	/**
	 * Sets the invoiceHeaderInformationIdentifier attribute.
	 * 
	 * @param invoiceHeaderInformationIdentifier The invoiceHeaderInformationIdentifier to set.
	 * 
	 */
	public void setInvoiceHeaderInformationIdentifier(Integer invoiceHeaderInformationIdentifier) {
		this.invoiceHeaderInformationIdentifier = invoiceHeaderInformationIdentifier;
	}


	/**
	 * Gets the invoiceRejectReasonTypeCode attribute.
	 * 
	 * @return Returns the invoiceRejectReasonTypeCode
	 * 
	 */
	public String getInvoiceRejectReasonTypeCode() { 
		return invoiceRejectReasonTypeCode;
	}

	/**
	 * Sets the invoiceRejectReasonTypeCode attribute.
	 * 
	 * @param invoiceRejectReasonTypeCode The invoiceRejectReasonTypeCode to set.
	 * 
	 */
	public void setInvoiceRejectReasonTypeCode(String invoiceRejectReasonTypeCode) {
		this.invoiceRejectReasonTypeCode = invoiceRejectReasonTypeCode;
	}


	/**
	 * Gets the invoiceRejectReasonDescription attribute.
	 * 
	 * @return Returns the invoiceRejectReasonDescription
	 * 
	 */
	public String getInvoiceRejectReasonDescription() { 
		return invoiceRejectReasonDescription;
	}

	/**
	 * Sets the invoiceRejectReasonDescription attribute.
	 * 
	 * @param invoiceRejectReasonDescription The invoiceRejectReasonDescription to set.
	 * 
	 */
	public void setInvoiceRejectReasonDescription(String invoiceRejectReasonDescription) {
		this.invoiceRejectReasonDescription = invoiceRejectReasonDescription;
	}


	/**
	 * Gets the invoiceHeaderInformation attribute.
	 * 
	 * @return Returns the invoiceHeaderInformation
	 * 
	 */
	public ElectronicInvoiceHeaderInformation getInvoiceHeaderInformation() { 
		return invoiceHeaderInformation;
	}

	/**
	 * Sets the invoiceHeaderInformation attribute.
	 * 
	 * @param invoiceHeaderInformation The invoiceHeaderInformation to set.
	 * @deprecated
	 */
	public void setInvoiceHeaderInformation(ElectronicInvoiceHeaderInformation invoiceHeaderInformation) {
		this.invoiceHeaderInformation = invoiceHeaderInformation;
	}

	/**
	 * Gets the invoiceRejectReasonType attribute.
	 * 
	 * @return Returns the invoiceRejectReasonType
	 * 
	 */
	public ElectronicInvoiceRejectTypeCode getInvoiceRejectReasonType() { 
		return invoiceRejectReasonType;
	}

	/**
	 * Sets the invoiceRejectReasonType attribute.
	 * 
	 * @param invoiceRejectReasonType The invoiceRejectReasonType to set.
	 * @deprecated
	 */
	public void setInvoiceRejectReasonType(ElectronicInvoiceRejectTypeCode invoiceRejectReasonType) {
		this.invoiceRejectReasonType = invoiceRejectReasonType;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.invoiceRejectReasonIdentifier != null) {
            m.put("invoiceRejectReasonIdentifier", this.invoiceRejectReasonIdentifier.toString());
        }
	    return m;
    }
}
