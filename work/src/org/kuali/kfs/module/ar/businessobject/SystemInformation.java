package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.State;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class SystemInformation extends PersistableBusinessObjectBase {

	private Integer universityFiscalYear;
	private String processingChartOfAccountCode;
	private String processingOrganizationCode;
	private String salesTaxAccountNumber;
	private String salesTaxFinancialObjectCode;
	private String discountObjectCode;
	private String adjustmentObjectCode;
	private String universityClearingChartOfAccountsCode;
	private String universityClearingAccountNumber;
	private String universityClearingSubAccountNumber;
	private String universityClearingObjectCode;
	private String universityClearingSubObjectCode;
	private String creditCardObjectCode;
	private String lockboxNumber;
	private boolean active;
    private String organizationRemitToAddressName;
    private String organizationRemitToLine1StreetAddress;
    private String organizationRemitToLine2StreetAddress;
    private String organizationRemitToCityName;
    private String organizationRemitToStateCode;
    private String organizationRemitToZipCode;
    private String organizationCheckPayableToName;
    private String financialDocumentInitiatorIdentifier;
    private String wireChartOfAccountsCode;
    private String wireAccountNumber;
    private String wireSubAccountNumber;
    private String wireObjectCode;
    private String wireSubObjectCode;    
    
    private ObjectCode adjustmentFinancialObject;
	private ObjectCode salesTaxFinancialObject;
	private ObjectCode creditCardFinancialObject;
	private SubObjCd universityClearingSubObject;
	private ObjectCode universityClearingObject;
	private ObjectCode discountFinancialObject;
	private Org processingOrganization;
	private Chart processingChartOfAccount;
	private Account salesTaxAccount;
	private Account universityClearingAccount;
	private Chart universityClearingChartOfAccounts;
    private SubAccount universityClearingSubAccount;
    private ObjectCode universityFiscalYearObject;
    private State organizationRemitToState;
    private Chart wireChartOfAccounts;
    private Account wireAccount;
    private SubAccount wireSubAccount;
    private ObjectCode wireObject;
    private SubObjCd wireSubObject;
    
	/**
	 * Default constructor.
	 */
	public SystemInformation() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the processingChartOfAccountCode attribute.
	 * 
	 * @return Returns the processingChartOfAccountCode
	 * 
	 */
	public String getProcessingChartOfAccountCode() { 
		return processingChartOfAccountCode;
	}

	/**
	 * Sets the processingChartOfAccountCode attribute.
	 * 
	 * @param processingChartOfAccountCode The processingChartOfAccountCode to set.
	 * 
	 */
	public void setProcessingChartOfAccountCode(String processingChartOfAccountCode) {
		this.processingChartOfAccountCode = processingChartOfAccountCode;
	}


	/**
	 * Gets the processingOrganizationCode attribute.
	 * 
	 * @return Returns the processingOrganizationCode
	 * 
	 */
	public String getProcessingOrganizationCode() { 
		return processingOrganizationCode;
	}

	/**
	 * Sets the processingOrganizationCode attribute.
	 * 
	 * @param processingOrganizationCode The processingOrganizationCode to set.
	 * 
	 */
	public void setProcessingOrganizationCode(String processingOrganizationCode) {
		this.processingOrganizationCode = processingOrganizationCode;
	}


	/**
	 * Gets the salesTaxAccountNumber attribute.
	 * 
	 * @return Returns the salesTaxAccountNumber
	 * 
	 */
	public String getSalesTaxAccountNumber() { 
		return salesTaxAccountNumber;
	}

	/**
	 * Sets the salesTaxAccountNumber attribute.
	 * 
	 * @param salesTaxAccountNumber The salesTaxAccountNumber to set.
	 * 
	 */
	public void setSalesTaxAccountNumber(String salesTaxAccountNumber) {
		this.salesTaxAccountNumber = salesTaxAccountNumber;
	}


	/**
	 * Gets the salesTaxFinancialObjectCode attribute.
	 * 
	 * @return Returns the salesTaxFinancialObjectCode
	 * 
	 */
	public String getSalesTaxFinancialObjectCode() { 
		return salesTaxFinancialObjectCode;
	}

	/**
	 * Sets the salesTaxFinancialObjectCode attribute.
	 * 
	 * @param salesTaxFinancialObjectCode The salesTaxFinancialObjectCode to set.
	 * 
	 */
	public void setSalesTaxFinancialObjectCode(String salesTaxFinancialObjectCode) {
		this.salesTaxFinancialObjectCode = salesTaxFinancialObjectCode;
	}


	/**
	 * Gets the discountObjectCode attribute.
	 * 
	 * @return Returns the discountObjectCode
	 * 
	 */
	public String getDiscountObjectCode() { 
		return discountObjectCode;
	}

	/**
	 * Sets the discountObjectCode attribute.
	 * 
	 * @param discountObjectCode The discountObjectCode to set.
	 * 
	 */
	public void setDiscountObjectCode(String refundFinancialObjectCode) {
		this.discountObjectCode = refundFinancialObjectCode;
	}


	/**
	 * Gets the adjustmentObjectCode attribute.
	 * 
	 * @return Returns the adjustmentObjectCode
	 * 
	 */
	public String getAdjustmentObjectCode() { 
		return adjustmentObjectCode;
	}

	/**
	 * Sets the adjustmentObjectCode attribute.
	 * 
	 * @param adjustmentObjectCode The adjustmentObjectCode to set.
	 * 
	 */
	public void setAdjustmentObjectCode(String adjustmentObjectCode) {
		this.adjustmentObjectCode = adjustmentObjectCode;
	}


	/**
	 * Gets the universityClearingChartOfAccountsCode attribute.
	 * 
	 * @return Returns the universityClearingChartOfAccountsCode
	 * 
	 */
	public String getUniversityClearingChartOfAccountsCode() { 
		return universityClearingChartOfAccountsCode;
	}

	/**
	 * Sets the universityClearingChartOfAccountsCode attribute.
	 * 
	 * @param universityClearingChartOfAccountsCode The universityClearingChartOfAccountsCode to set.
	 * 
	 */
	public void setUniversityClearingChartOfAccountsCode(String universityClearingChartOfAccountsCode) {
		this.universityClearingChartOfAccountsCode = universityClearingChartOfAccountsCode;
	}


	/**
	 * Gets the universityClearingAccountNumber attribute.
	 * 
	 * @return Returns the universityClearingAccountNumber
	 * 
	 */
	public String getUniversityClearingAccountNumber() { 
		return universityClearingAccountNumber;
	}

	/**
	 * Sets the universityClearingAccountNumber attribute.
	 * 
	 * @param universityClearingAccountNumber The universityClearingAccountNumber to set.
	 * 
	 */
	public void setUniversityClearingAccountNumber(String universityClearingAccountNumber) {
		this.universityClearingAccountNumber = universityClearingAccountNumber;
	}


	/**
	 * Gets the universityClearingSubAccountNumber attribute.
	 * 
	 * @return Returns the universityClearingSubAccountNumber
	 * 
	 */
	public String getUniversityClearingSubAccountNumber() { 
		return universityClearingSubAccountNumber;
	}

	/**
	 * Sets the universityClearingSubAccountNumber attribute.
	 * 
	 * @param universityClearingSubAccountNumber The universityClearingSubAccountNumber to set.
	 * 
	 */
	public void setUniversityClearingSubAccountNumber(String universityClearingSubAccountNumber) {
		this.universityClearingSubAccountNumber = universityClearingSubAccountNumber;
	}


	/**
	 * Gets the universityClearingObjectCode attribute.
	 * 
	 * @return Returns the universityClearingObjectCode
	 * 
	 */
	public String getUniversityClearingObjectCode() { 
		return universityClearingObjectCode;
	}

	/**
	 * Sets the universityClearingObjectCode attribute.
	 * 
	 * @param universityClearingObjectCode The universityClearingObjectCode to set.
	 * 
	 */
	public void setUniversityClearingObjectCode(String universityClearingObjectCode) {
		this.universityClearingObjectCode = universityClearingObjectCode;
	}


	/**
	 * Gets the universityClearingSubObjectCode attribute.
	 * 
	 * @return Returns the universityClearingSubObjectCode
	 * 
	 */
	public String getUniversityClearingSubObjectCode() { 
		return universityClearingSubObjectCode;
	}

	/**
	 * Sets the universityClearingSubObjectCode attribute.
	 * 
	 * @param universityClearingSubObjectCode The universityClearingSubObjectCode to set.
	 * 
	 */
	public void setUniversityClearingSubObjectCode(String universityClearingSubObjectCode) {
		this.universityClearingSubObjectCode = universityClearingSubObjectCode;
	}


	/**
	 * Gets the creditCardObjectCode attribute.
	 * 
	 * @return Returns the creditCardObjectCode
	 * 
	 */
	public String getCreditCardObjectCode() { 
		return creditCardObjectCode;
	}

	/**
	 * Sets the creditCardObjectCode attribute.
	 * 
	 * @param creditCardObjectCode The creditCardObjectCode to set.
	 * 
	 */
	public void setCreditCardObjectCode(String creditCardObjectCode) {
		this.creditCardObjectCode = creditCardObjectCode;
	}


	/**
	 * Gets the lockboxNumber attribute.
	 * 
	 * @return Returns the lockboxNumber
	 * 
	 */
	public String getLockboxNumber() { 
		return lockboxNumber;
	}

	/**
	 * Sets the lockboxNumber attribute.
	 * 
	 * @param lockboxNumber The lockboxNumber to set.
	 * 
	 */
	public void setLockboxNumber(String lockboxNumber) {
		this.lockboxNumber = lockboxNumber;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean getActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
     * Gets the financialDocumentInitiatorIdentifier attribute. 
     * @return Returns the financialDocumentInitiatorIdentifier.
     */
    public String getFinancialDocumentInitiatorIdentifier() {
        return financialDocumentInitiatorIdentifier;
    }

    /**
     * Sets the financialDocumentInitiatorIdentifier attribute value.
     * @param financialDocumentInitiatorIdentifier The financialDocumentInitiatorIdentifier to set.
     */
    public void setFinancialDocumentInitiatorIdentifier(String financialDocumentInitiatorIdentifier) {
        this.financialDocumentInitiatorIdentifier = financialDocumentInitiatorIdentifier;
    }

    /**
     * Gets the organizationCheckPayableToName attribute. 
     * @return Returns the organizationCheckPayableToName.
     */
    public String getOrganizationCheckPayableToName() {
        return organizationCheckPayableToName;
    }

    /**
     * Sets the organizationCheckPayableToName attribute value.
     * @param organizationCheckPayableToName The organizationCheckPayableToName to set.
     */
    public void setOrganizationCheckPayableToName(String organizationCheckPayableToName) {
        this.organizationCheckPayableToName = organizationCheckPayableToName;
    }

    /**
     * Gets the organizationRemitToAddressName attribute. 
     * @return Returns the organizationRemitToAddressName.
     */
    public String getOrganizationRemitToAddressName() {
        return organizationRemitToAddressName;
    }

    /**
     * Sets the organizationRemitToAddressName attribute value.
     * @param organizationRemitToAddressName The organizationRemitToAddressName to set.
     */
    public void setOrganizationRemitToAddressName(String organizationRemitToAddressName) {
        this.organizationRemitToAddressName = organizationRemitToAddressName;
    }

    /**
     * Gets the organizationRemitToCityName attribute. 
     * @return Returns the organizationRemitToCityName.
     */
    public String getOrganizationRemitToCityName() {
        return organizationRemitToCityName;
    }

    /**
     * Sets the organizationRemitToCityName attribute value.
     * @param organizationRemitToCityName The organizationRemitToCityName to set.
     */
    public void setOrganizationRemitToCityName(String organizationRemitToCityName) {
        this.organizationRemitToCityName = organizationRemitToCityName;
    }

    /**
     * Gets the organizationRemitToLine1StreetAddress attribute. 
     * @return Returns the organizationRemitToLine1StreetAddress.
     */
    public String getOrganizationRemitToLine1StreetAddress() {
        return organizationRemitToLine1StreetAddress;
    }

    /**
     * Sets the organizationRemitToLine1StreetAddress attribute value.
     * @param organizationRemitToLine1StreetAddress The organizationRemitToLine1StreetAddress to set.
     */
    public void setOrganizationRemitToLine1StreetAddress(String organizationRemitToLine1StreetAddress) {
        this.organizationRemitToLine1StreetAddress = organizationRemitToLine1StreetAddress;
    }

    /**
     * Gets the organizationRemitToLine2StreetAddress attribute. 
     * @return Returns the organizationRemitToLine2StreetAddress.
     */
    public String getOrganizationRemitToLine2StreetAddress() {
        return organizationRemitToLine2StreetAddress;
    }

    /**
     * Sets the organizationRemitToLine2StreetAddress attribute value.
     * @param organizationRemitToLine2StreetAddress The organizationRemitToLine2StreetAddress to set.
     */
    public void setOrganizationRemitToLine2StreetAddress(String organizationRemitToLine2StreetAddress) {
        this.organizationRemitToLine2StreetAddress = organizationRemitToLine2StreetAddress;
    }

    /**
     * Gets the organizationRemitToStateCode attribute. 
     * @return Returns the organizationRemitToStateCode.
     */
    public String getOrganizationRemitToStateCode() {
        return organizationRemitToStateCode;
    }

    /**
     * Sets the organizationRemitToStateCode attribute value.
     * @param organizationRemitToStateCode The organizationRemitToStateCode to set.
     */
    public void setOrganizationRemitToStateCode(String organizationRemitToStateCode) {
        this.organizationRemitToStateCode = organizationRemitToStateCode;
    }

    /**
     * Gets the organizationRemitToZipCode attribute. 
     * @return Returns the organizationRemitToZipCode.
     */
    public String getOrganizationRemitToZipCode() {
        return organizationRemitToZipCode;
    }

    /**
     * Sets the organizationRemitToZipCode attribute value.
     * @param organizationRemitToZipCode The organizationRemitToZipCode to set.
     */
    public void setOrganizationRemitToZipCode(String organizationRemitToZipCode) {
        this.organizationRemitToZipCode = organizationRemitToZipCode;
    }

    /**
     * Gets the wireAccountNumber attribute. 
     * @return Returns the wireAccountNumber.
     */
    public String getWireAccountNumber() {
        return wireAccountNumber;
    }

    /**
     * Sets the wireAccountNumber attribute value.
     * @param wireAccountNumber The wireAccountNumber to set.
     */
    public void setWireAccountNumber(String wireAccountNumber) {
        this.wireAccountNumber = wireAccountNumber;
    }

    /**
     * Gets the wireChartOfAccountsCode attribute. 
     * @return Returns the wireChartOfAccountsCode.
     */
    public String getWireChartOfAccountsCode() {
        return wireChartOfAccountsCode;
    }

    /**
     * Sets the wireChartOfAccountsCode attribute value.
     * @param wireChartOfAccountsCode The wireChartOfAccountsCode to set.
     */
    public void setWireChartOfAccountsCode(String wireChartOfAccountsCode) {
        this.wireChartOfAccountsCode = wireChartOfAccountsCode;
    }

    /**
     * Gets the wireObjectCode attribute. 
     * @return Returns the wireObjectCode.
     */
    public String getWireObjectCode() {
        return wireObjectCode;
    }

    /**
     * Sets the wireObjectCode attribute value.
     * @param wireObjectCode The wireObjectCode to set.
     */
    public void setWireObjectCode(String wireObjectCode) {
        this.wireObjectCode = wireObjectCode;
    }

    /**
     * Gets the wireSubAccountNumber attribute. 
     * @return Returns the wireSubAccountNumber.
     */
    public String getWireSubAccountNumber() {
        return wireSubAccountNumber;
    }

    /**
     * Sets the wireSubAccountNumber attribute value.
     * @param wireSubAccountNumber The wireSubAccountNumber to set.
     */
    public void setWireSubAccountNumber(String wireSubAccountNumber) {
        this.wireSubAccountNumber = wireSubAccountNumber;
    }

    /**
     * Gets the wireSubObjectCode attribute. 
     * @return Returns the wireSubObjectCode.
     */
    public String getWireSubObjectCode() {
        return wireSubObjectCode;
    }

    /**
     * Sets the wireSubObjectCode attribute value.
     * @param wireSubObjectCode The wireSubObjectCode to set.
     */
    public void setWireSubObjectCode(String wireSubObjectCode) {
        this.wireSubObjectCode = wireSubObjectCode;
    }

    /**
	 * Gets the adjustmentFinancialObject attribute.
	 * 
	 * @return Returns the adjustmentFinancialObject
	 * 
	 */
	public ObjectCode getAdjustmentFinancialObject() { 
		return adjustmentFinancialObject;
	}

	/**
	 * Sets the adjustmentFinancialObject attribute.
	 * 
	 * @param adjustmentFinancialObject The adjustmentFinancialObject to set.
	 * @deprecated
	 */
	public void setAdjustmentFinancialObject(ObjectCode adjustmentFinancialObject) {
		this.adjustmentFinancialObject = adjustmentFinancialObject;
	}

	/**
	 * Gets the salesTaxFinancialObject attribute.
	 * 
	 * @return Returns the salesTaxFinancialObject
	 * 
	 */
	public ObjectCode getSalesTaxFinancialObject() { 
		return salesTaxFinancialObject;
	}

	/**
	 * Sets the salesTaxFinancialObject attribute.
	 * 
	 * @param salesTaxFinancialObject The salesTaxFinancialObject to set.
	 * @deprecated
	 */
	public void setSalesTaxFinancialObject(ObjectCode salesTaxFinancialObject) {
		this.salesTaxFinancialObject = salesTaxFinancialObject;
	}

	/**
	 * Gets the creditCardFinancialObject attribute.
	 * 
	 * @return Returns the creditCardFinancialObject
	 * 
	 */
	public ObjectCode getCreditCardFinancialObject() { 
		return creditCardFinancialObject;
	}

	/**
	 * Sets the creditCardFinancialObject attribute.
	 * 
	 * @param creditCardFinancialObject The creditCardFinancialObject to set.
	 * @deprecated
	 */
	public void setCreditCardFinancialObject(ObjectCode creditCardFinancialObject) {
		this.creditCardFinancialObject = creditCardFinancialObject;
	}

	/**
	 * Gets the universityClearingSubObject attribute.
	 * 
	 * @return Returns the universityClearingSubObject
	 * 
	 */
	public SubObjCd getUniversityClearingSubObject() { 
		return universityClearingSubObject;
	}

	/**
	 * Sets the universityClearingSubObject attribute.
	 * 
	 * @param universityClearingSubObject The universityClearingSubObject to set.
	 * @deprecated
	 */
	public void setUniversityClearingSubObject(SubObjCd universityClearingSubObject) {
		this.universityClearingSubObject = universityClearingSubObject;
	}

	/**
	 * Gets the universityClearingObject attribute.
	 * 
	 * @return Returns the universityClearingObject
	 * 
	 */
	public ObjectCode getUniversityClearingObject() { 
		return universityClearingObject;
	}

	/**
	 * Sets the universityClearingObject attribute.
	 * 
	 * @param universityClearingObject The universityClearingObject to set.
	 * @deprecated
	 */
	public void setUniversityClearingObject(ObjectCode universityClearingObject) {
		this.universityClearingObject = universityClearingObject;
	}

	/**
	 * Gets the discountFinancialObject attribute.
	 * 
	 * @return Returns the discountFinancialObject
	 * 
	 */
	public ObjectCode getDiscountFinancialObject() { 
		return discountFinancialObject;
	}

	/**
	 * Sets the discountFinancialObject attribute.
	 * 
	 * @param discountFinancialObject The discountFinancialObject to set.
	 * @deprecated
	 */
	public void setDiscountFinancialObject(ObjectCode refundFinancialObject) {
		this.discountFinancialObject = refundFinancialObject;
	}

	/**
	 * Gets the processingOrganization attribute.
	 * 
	 * @return Returns the processingOrganization
	 * 
	 */
	public Org getProcessingOrganization() { 
		return processingOrganization;
	}

	/**
	 * Sets the processingOrganization attribute.
	 * 
	 * @param processingOrganization The processingOrganization to set.
	 * @deprecated
	 */
	public void setProcessingOrganization(Org processingOrganization) {
		this.processingOrganization = processingOrganization;
	}

	/**
	 * Gets the processingChartOfAccount attribute.
	 * 
	 * @return Returns the processingChartOfAccount
	 * 
	 */
	public Chart getProcessingChartOfAccount() { 
		return processingChartOfAccount;
	}

	/**
	 * Sets the processingChartOfAccount attribute.
	 * 
	 * @param processingChartOfAccount The processingChartOfAccount to set.
	 * @deprecated
	 */
	public void setProcessingChartOfAccount(Chart processingChartOfAccount) {
		this.processingChartOfAccount = processingChartOfAccount;
	}

	/**
	 * Gets the salesTaxAccount attribute.
	 * 
	 * @return Returns the salesTaxAccount
	 * 
	 */
	public Account getSalesTaxAccount() { 
		return salesTaxAccount;
	}

	/**
	 * Sets the salesTaxAccount attribute.
	 * 
	 * @param salesTaxAccount The salesTaxAccount to set.
	 * @deprecated
	 */
	public void setSalesTaxAccount(Account salesTaxAccount) {
		this.salesTaxAccount = salesTaxAccount;
	}

	/**
	 * Gets the universityClearingAccount attribute.
	 * 
	 * @return Returns the universityClearingAccount
	 * 
	 */
	public Account getUniversityClearingAccount() { 
		return universityClearingAccount;
	}

	/**
	 * Sets the universityClearingAccount attribute.
	 * 
	 * @param universityClearingAccount The universityClearingAccount to set.
	 * @deprecated
	 */
	public void setUniversityClearingAccount(Account universityClearingAccount) {
		this.universityClearingAccount = universityClearingAccount;
	}

	/**
	 * Gets the universityClearingChartOfAccounts attribute.
	 * 
	 * @return Returns the universityClearingChartOfAccounts
	 * 
	 */
	public Chart getUniversityClearingChartOfAccounts() { 
		return universityClearingChartOfAccounts;
	}

	/**
	 * Sets the universityClearingChartOfAccounts attribute.
	 * 
	 * @param universityClearingChartOfAccounts The universityClearingChartOfAccounts to set.
	 * @deprecated
	 */
	public void setUniversityClearingChartOfAccounts(Chart universityClearingChartOfAccounts) {
		this.universityClearingChartOfAccounts = universityClearingChartOfAccounts;
	}

    /**
     * Gets the universityClearingSubAccount attribute. 
     * @return Returns the universityClearingSubAccount.
     */
    public SubAccount getUniversityClearingSubAccount() {
        return universityClearingSubAccount;
    }

    /**
     * Sets the universityClearingSubAccount attribute value.
     * @param universityClearingSubAccount The universityClearingSubAccount to set.
     * @deprecated
     */
    public void setUniversityClearingSubAccount(SubAccount universityClearingSubAccount) {
        this.universityClearingSubAccount = universityClearingSubAccount;
    }    
    
	/**
     * Gets the organizationRemitToState attribute. 
     * @return Returns the organizationRemitToState.
     */
    public State getOrganizationRemitToState() {
        return organizationRemitToState;
    }

    /**
     * Sets the organizationRemitToState attribute value.
     * @param organizationRemitToState The organizationRemitToState to set.
     * @deprecated
     */
    public void setOrganizationRemitToState(State organizationRemitToState) {
        this.organizationRemitToState = organizationRemitToState;
    }

    /**
     * Gets the wireAccount attribute. 
     * @return Returns the wireAccount.
     */
    public Account getWireAccount() {
        return wireAccount;
    }

    /**
     * Sets the wireAccount attribute value.
     * @param wireAccount The wireAccount to set.
     * @deprecated
     */
    public void setWireAccount(Account wireAccount) {
        this.wireAccount = wireAccount;
    }

    /**
     * Gets the wireChartOfAccounts attribute. 
     * @return Returns the wireChartOfAccounts.
     */
    public Chart getWireChartOfAccounts() {
        return wireChartOfAccounts;
    }

    /**
     * Sets the wireChartOfAccounts attribute value.
     * @param wireChartOfAccounts The wireChartOfAccounts to set.
     * @deprecated
     */
    public void setWireChartOfAccounts(Chart wireChartOfAccounts) {
        this.wireChartOfAccounts = wireChartOfAccounts;
    }

    /**
     * Gets the wireObject attribute. 
     * @return Returns the wireObject.
     */
    public ObjectCode getWireObject() {
        return wireObject;
    }

    /**
     * Sets the wireObject attribute value.
     * @param wireObject The wireObject to set.
     * @deprecated
     */
    public void setWireObject(ObjectCode wireObject) {
        this.wireObject = wireObject;
    }

    /**
     * Gets the wireSubAccount attribute. 
     * @return Returns the wireSubAccount.
     */
    public SubAccount getWireSubAccount() {
        return wireSubAccount;
    }

    /**
     * Sets the wireSubAccount attribute value.
     * @param wireSubAccount The wireSubAccount to set.
     * @deprecated
     */
    public void setWireSubAccount(SubAccount wireSubAccount) {
        this.wireSubAccount = wireSubAccount;
    }

    /**
     * Gets the wireSubObject attribute. 
     * @return Returns the wireSubObject.
     */
    public SubObjCd getWireSubObject() {
        return wireSubObject;
    }

    /**
     * Sets the wireSubObject attribute value.
     * @param wireSubObject The wireSubObject to set.
     * @deprecated
     */
    public void setWireSubObject(SubObjCd wireSubObject) {
        this.wireSubObject = wireSubObject;
    }

    /**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("processingChartOfAccountCode", this.processingChartOfAccountCode);
        m.put("processingOrganizationCode", this.processingOrganizationCode);
	    return m;
    }

    public ObjectCode getUniversityFiscalYearObject() {
        return universityFiscalYearObject;
    }

    public void setUniversityFiscalYearObject(ObjectCode universityFiscalYearObject) {
        this.universityFiscalYearObject = universityFiscalYearObject;
    }
    
    /**
     * This method (a hack by any other name...) returns a string so that an organization options can have a link to view its own
     * inquiry page after a look up
     * 
     * @return the String "View System Information"
     */
    public String getSystemInformationViewer() {
        return "View System Information";
    }

}
