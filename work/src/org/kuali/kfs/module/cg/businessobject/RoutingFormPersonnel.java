

package org.kuali.module.kra.routingform.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.Country;
import org.kuali.core.bo.PostalZipCode;
import org.kuali.core.bo.State;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class RoutingFormPersonnel extends BusinessObjectBase {

	private Long personSystemIdentifier;
	private String documentNumber;
	private String chartOfAccountsCode;
	private String organizationCode;
	private Integer personCreditPercent;
	private Integer personFinancialAidPercent;
	private String personRoleCode;
	private String personPrefixText;
	private String personSuffixText;
	private String personPositionTitle;
	private String personDivisionText;
	private String personLine1Address;
	private String personLine2Address;
	private String personCityName;
	private String personCountyName;
	private String personStateCode;
	private String personCountryCode;
	private String personZipCode;
	private String personPhoneNumber;
	private String personFaxNumber;
	private String personEmailAddress;

    private Org organization;
	private Chart chartOfAccounts;
    private State personState;
    private PostalZipCode personZip;
    private Country personCountry;
    private PersonRole personRole;
    private RoutingFormDocument routingFormDocument;
    private UniversalUser user;
    
	/**
	 * Default constructor.
	 */
	public RoutingFormPersonnel() {

	}

	/**
	 * Gets the personSystemIdentifier attribute.
	 * 
	 * @return Returns the personSystemIdentifier
	 * 
	 */
	public Long getPersonSystemIdentifier() { 
		return personSystemIdentifier;
	}

	/**
	 * Sets the personSystemIdentifier attribute.
	 * 
	 * @param personSystemIdentifier The personSystemIdentifier to set.
	 * 
	 */
	public void setPersonSystemIdentifier(Long personSystemIdentifier) {
		this.personSystemIdentifier = personSystemIdentifier;
	}


	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the organizationCode attribute.
	 * 
	 * @return Returns the organizationCode
	 * 
	 */
	public String getOrganizationCode() { 
		return organizationCode;
	}

	/**
	 * Sets the organizationCode attribute.
	 * 
	 * @param organizationCode The organizationCode to set.
	 * 
	 */
	public void setOrganizationCode(String organizationCode) {
		this.organizationCode = organizationCode;
	}


	/**
	 * Gets the personCreditPercent attribute.
	 * 
	 * @return Returns the personCreditPercent
	 * 
	 */
	public Integer getPersonCreditPercent() { 
		return personCreditPercent;
	}

	/**
	 * Sets the personCreditPercent attribute.
	 * 
	 * @param personCreditPercent The personCreditPercent to set.
	 * 
	 */
	public void setPersonCreditPercent(Integer personCreditPercent) {
		this.personCreditPercent = personCreditPercent;
	}


	/**
	 * Gets the personFinancialAidPercent attribute.
	 * 
	 * @return Returns the personFinancialAidPercent
	 * 
	 */
	public Integer getPersonFinancialAidPercent() { 
		return personFinancialAidPercent;
	}

	/**
	 * Sets the personFinancialAidPercent attribute.
	 * 
	 * @param personFinancialAidPercent The personFinancialAidPercent to set.
	 * 
	 */
	public void setPersonFinancialAidPercent(Integer personFinancialAidPercent) {
		this.personFinancialAidPercent = personFinancialAidPercent;
	}


	/**
	 * Gets the personRoleCode attribute.
	 * 
	 * @return Returns the personRoleCode
	 * 
	 */
	public String getPersonRoleCode() { 
		return personRoleCode;
	}

	/**
	 * Sets the personRoleCode attribute.
	 * 
	 * @param personRoleCode The personRoleCode to set.
	 * 
	 */
	public void setPersonRoleCode(String personRoleCode) {
		this.personRoleCode = personRoleCode;
	}


	/**
	 * Gets the personPrefixText attribute.
	 * 
	 * @return Returns the personPrefixText
	 * 
	 */
	public String getPersonPrefixText() { 
		return personPrefixText;
	}

	/**
	 * Sets the personPrefixText attribute.
	 * 
	 * @param personPrefixText The personPrefixText to set.
	 * 
	 */
	public void setPersonPrefixText(String personPrefixText) {
		this.personPrefixText = personPrefixText;
	}


	/**
	 * Gets the personSuffixText attribute.
	 * 
	 * @return Returns the personSuffixText
	 * 
	 */
	public String getPersonSuffixText() { 
		return personSuffixText;
	}

	/**
	 * Sets the personSuffixText attribute.
	 * 
	 * @param personSuffixText The personSuffixText to set.
	 * 
	 */
	public void setPersonSuffixText(String personSuffixText) {
		this.personSuffixText = personSuffixText;
	}


	/**
	 * Gets the personPositionTitle attribute.
	 * 
	 * @return Returns the personPositionTitle
	 * 
	 */
	public String getPersonPositionTitle() { 
		return personPositionTitle;
	}

	/**
	 * Sets the personPositionTitle attribute.
	 * 
	 * @param personPositionTitle The personPositionTitle to set.
	 * 
	 */
	public void setPersonPositionTitle(String personPositionTitle) {
		this.personPositionTitle = personPositionTitle;
	}


	/**
	 * Gets the personDivisionText attribute.
	 * 
	 * @return Returns the personDivisionText
	 * 
	 */
	public String getPersonDivisionText() { 
		return personDivisionText;
	}

	/**
	 * Sets the personDivisionText attribute.
	 * 
	 * @param personDivisionText The personDivisionText to set.
	 * 
	 */
	public void setPersonDivisionText(String personDivisionText) {
		this.personDivisionText = personDivisionText;
	}


	/**
	 * Gets the personLine1Address attribute.
	 * 
	 * @return Returns the personLine1Address
	 * 
	 */
	public String getPersonLine1Address() { 
		return personLine1Address;
	}

	/**
	 * Sets the personLine1Address attribute.
	 * 
	 * @param personLine1Address The personLine1Address to set.
	 * 
	 */
	public void setPersonLine1Address(String personLine1Address) {
		this.personLine1Address = personLine1Address;
	}


	/**
	 * Gets the personLine2Address attribute.
	 * 
	 * @return Returns the personLine2Address
	 * 
	 */
	public String getPersonLine2Address() { 
		return personLine2Address;
	}

	/**
	 * Sets the personLine2Address attribute.
	 * 
	 * @param personLine2Address The personLine2Address to set.
	 * 
	 */
	public void setPersonLine2Address(String personLine2Address) {
		this.personLine2Address = personLine2Address;
	}


	/**
	 * Gets the personCityName attribute.
	 * 
	 * @return Returns the personCityName
	 * 
	 */
	public String getPersonCityName() { 
		return personCityName;
	}

	/**
	 * Sets the personCityName attribute.
	 * 
	 * @param personCityName The personCityName to set.
	 * 
	 */
	public void setPersonCityName(String personCityName) {
		this.personCityName = personCityName;
	}


	/**
	 * Gets the personCountyName attribute.
	 * 
	 * @return Returns the personCountyName
	 * 
	 */
	public String getPersonCountyName() { 
		return personCountyName;
	}

	/**
	 * Sets the personCountyName attribute.
	 * 
	 * @param personCountyName The personCountyName to set.
	 * 
	 */
	public void setPersonCountyName(String personCountyName) {
		this.personCountyName = personCountyName;
	}


	/**
	 * Gets the personStateCode attribute.
	 * 
	 * @return Returns the personStateCode
	 * 
	 */
	public String getPersonStateCode() { 
		return personStateCode;
	}

	/**
	 * Sets the personStateCode attribute.
	 * 
	 * @param personStateCode The personStateCode to set.
	 * 
	 */
	public void setPersonStateCode(String personStateCode) {
		this.personStateCode = personStateCode;
	}


	/**
	 * Gets the personCountryCode attribute.
	 * 
	 * @return Returns the personCountryCode
	 * 
	 */
	public String getPersonCountryCode() { 
		return personCountryCode;
	}

	/**
	 * Sets the personCountryCode attribute.
	 * 
	 * @param personCountryCode The personCountryCode to set.
	 * 
	 */
	public void setPersonCountryCode(String personCountryCode) {
		this.personCountryCode = personCountryCode;
	}


	/**
	 * Gets the personZipCode attribute.
	 * 
	 * @return Returns the personZipCode
	 * 
	 */
	public String getPersonZipCode() { 
		return personZipCode;
	}

	/**
	 * Sets the personZipCode attribute.
	 * 
	 * @param personZipCode The personZipCode to set.
	 * 
	 */
	public void setPersonZipCode(String personZipCode) {
		this.personZipCode = personZipCode;
	}


	/**
	 * Gets the personPhoneNumber attribute.
	 * 
	 * @return Returns the personPhoneNumber
	 * 
	 */
	public String getPersonPhoneNumber() { 
		return personPhoneNumber;
	}

	/**
	 * Sets the personPhoneNumber attribute.
	 * 
	 * @param personPhoneNumber The personPhoneNumber to set.
	 * 
	 */
	public void setPersonPhoneNumber(String personPhoneNumber) {
		this.personPhoneNumber = personPhoneNumber;
	}


	/**
	 * Gets the personFaxNumber attribute.
	 * 
	 * @return Returns the personFaxNumber
	 * 
	 */
	public String getPersonFaxNumber() { 
		return personFaxNumber;
	}

	/**
	 * Sets the personFaxNumber attribute.
	 * 
	 * @param personFaxNumber The personFaxNumber to set.
	 * 
	 */
	public void setPersonFaxNumber(String personFaxNumber) {
		this.personFaxNumber = personFaxNumber;
	}


	/**
	 * Gets the personEmailAddress attribute.
	 * 
	 * @return Returns the personEmailAddress
	 * 
	 */
	public String getPersonEmailAddress() { 
		return personEmailAddress;
	}

	/**
	 * Sets the personEmailAddress attribute.
	 * 
	 * @param personEmailAddress The personEmailAddress to set.
	 * 
	 */
	public void setPersonEmailAddress(String personEmailAddress) {
		this.personEmailAddress = personEmailAddress;
	}


	/**
	 * Gets the organization attribute.
	 * 
	 * @return Returns the organization
	 * 
	 */
	public Org getOrganization() { 
		return organization;
	}

	/**
	 * Sets the organization attribute.
	 * 
	 * @param organization The organization to set.
	 * @deprecated
	 */
	public void setOrganization(Org organization) {
		this.organization = organization;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

    /**
     * Gets the personCountry attribute. 
     * @return Returns the personCountry.
     */
    public Country getPersonCountry() {
        return personCountry;
    }

    /**
     * Sets the personCountry attribute value.
     * @param personCountry The personCountry to set.
     * @deprecated
     */
    public void setPersonCountry(Country personCountry) {
        this.personCountry = personCountry;
    }

    /**
     * Gets the personState attribute. 
     * @return Returns the personState.
     */
    public State getPersonState() {
        return personState;
    }

    /**
     * Sets the personState attribute value.
     * @param personState The personState to set.
     * @deprecated
     */
    public void setPersonState(State personState) {
        this.personState = personState;
    }

    /**
     * Gets the personZip attribute. 
     * @return Returns the personZip.
     */
    public PostalZipCode getPersonZip() {
        return personZip;
    }

    /**
     * Sets the personZip attribute value.
     * @param personZip The personZip to set.
     * @deprecated
     */
    public void setPersonZip(PostalZipCode personZip) {
        this.personZip = personZip;
    }

    /**
     * Gets the personRole attribute. 
     * @return Returns the personRole.
     */
    public PersonRole getPersonRole() {
        return personRole;
    }

    /**
     * Sets the personRole attribute value.
     * @param personRole The personRole to set.
     * @deprecated
     */
    public void setPersonRole(PersonRole personRole) {
        this.personRole = personRole;
    }
    
    /**
     * Gets the routingFormDocument attribute. 
     * @return Returns the routingFormDocument.
     */
    public RoutingFormDocument getRoutingFormDocument() {
        return routingFormDocument;
    }

    /**
     * Sets the routingFormDocument attribute value.
     * @param routingFormDocument The routingFormDocument to set.
     * @deprecated
     */
    public void setRoutingFormDocument(RoutingFormDocument routingFormDocument) {
        this.routingFormDocument = routingFormDocument;
    }
    
    /**
     * Gets the user attribute. 
     * @return Returns the user.
     */
    public UniversalUser getUser() {
        return user;
    }

    /**
     * Sets the user attribute value.
     * @param user The user to set.
     * @deprecated
     */
    public void setUser(UniversalUser user) {
        this.user = user;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.personSystemIdentifier != null) {
            m.put("personSystemIdentifier", this.personSystemIdentifier.toString());
        }
        m.put("documentNumber", this.documentNumber);
        return m;
    }

}
