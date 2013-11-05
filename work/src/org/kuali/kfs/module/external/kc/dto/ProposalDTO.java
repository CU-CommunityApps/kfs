package org.kuali.kfs.module.external.kc.dto;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.kuali.rice.core.api.util.type.KualiDecimal;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "proposalDTO", propOrder = {
    "proposalNumber",
    "requestedStartDateTotal",
    "requestedEndDateTotal",
    "proposalTotalAmount",
    "totalDirectCostTotal",
    "totalIndirectCostTotal",
    "proposalLastUpdateDate",
    "awardTypeCode",
    "sponsorCode",
    "statusCode",
    "cfdaNumber",
    "title",
    "sponsorAwardNumber"
})
public class ProposalDTO implements Serializable {

	private static final long serialVersionUID = -7622894652760586766L;
	
    private String proposalNumber;
    private Date requestedStartDateTotal;
    private Date requestedEndDateTotal;
    private KualiDecimal proposalTotalAmount;
    private KualiDecimal totalDirectCostTotal;
    private KualiDecimal totalIndirectCostTotal;
    private Timestamp proposalLastUpdateDate;
    private Integer awardTypeCode;
    private String sponsorCode;
    private Integer statusCode;
    private String cfdaNumber;
    private String title;
    private String sponsorAwardNumber;
    
	public String getProposalNumber() {
		return proposalNumber;
	}
	public void setProposalNumber(String proposalNumber) {
		this.proposalNumber = proposalNumber;
	}
	public Date getRequestedStartDateTotal() {
		return requestedStartDateTotal;
	}
	public void setRequestedStartDateTotal(Date requestedStartDateTotal) {
		this.requestedStartDateTotal = requestedStartDateTotal;
	}
	public Date getRequestedEndDateTotal() {
		return requestedEndDateTotal;
	}
	public void setRequestedEndDateTotal(Date requestedEndDateTotal) {
		this.requestedEndDateTotal = requestedEndDateTotal;
	}
	public KualiDecimal getProposalTotalAmount() {
		return proposalTotalAmount;
	}
	public void setProposalTotalAmount(KualiDecimal proposalTotalAmount) {
		this.proposalTotalAmount = proposalTotalAmount;
	}
	public KualiDecimal getTotalDirectCostTotal() {
		return totalDirectCostTotal;
	}
	public void setTotalDirectCostTotal(KualiDecimal totalDirectCostTotal) {
		this.totalDirectCostTotal = totalDirectCostTotal;
	}
	public KualiDecimal getTotalIndirectCostTotal() {
		return totalIndirectCostTotal;
	}
	public void setTotalIndirectCostTotal(KualiDecimal totalIndirectCostTotal) {
		this.totalIndirectCostTotal = totalIndirectCostTotal;
	}
	public Timestamp getProposalLastUpdateDate() {
		return proposalLastUpdateDate;
	}
	public void setProposalLastUpdateDate(Timestamp proposalLastUpdateDate) {
		this.proposalLastUpdateDate = proposalLastUpdateDate;
	}
	public Integer getAwardTypeCode() {
		return awardTypeCode;
	}
	public void setAwardTypeCode(Integer awardTypeCode) {
		this.awardTypeCode = awardTypeCode;
	}
	public String getSponsorCode() {
		return sponsorCode;
	}
	public void setSponsorCode(String sponsorCode) {
		this.sponsorCode = sponsorCode;
	}
	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	public String getCfdaNumber() {
		return cfdaNumber;
	}
	public void setCfdaNumber(String cfdaNumber) {
		this.cfdaNumber = cfdaNumber;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSponsorAwardNumber() {
		return sponsorAwardNumber;
	}
	public void setSponsorAwardNumber(String sponsorAwardNumber) {
		this.sponsorAwardNumber = sponsorAwardNumber;
	}

}
