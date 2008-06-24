<%--
 Copyright 2006-2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>
<%@ attribute name="assetPayments" type="java.util.List" required="true" description="Asset payments list" %>
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<%@ attribute name="assetValueObj" type="java.lang.String" required="false" description="Asset object name" %>
	<c:if test="${assetValueObj==null}">
		<c:set var="assetValueObj" value="document.asset" />
	</c:if>
	<c:set var="assetPaymentAttributes" value="${DataDictionary.AssetPayment.attributes}" />
	<c:set var="pos" value="-1" />
<kul:tab tabTitle="Asset Payments" defaultOpen="${!defaultTabHide}">
		<div class="tab-container" align="center">
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">								
			<tr>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialSubObjectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.organizationReferenceId}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.documentNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentTypeCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.transferPaymentCode}" readOnly="true" /></th>
				<th class="grid" align="center"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.accountChargeAmount}" readOnly="true" /></th>
			</tr>
	<c:forEach var="payment" items="${assetPayments}">
	 	<c:set var="pos" value="${pos+1}" />
			<tr>
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].chartOfAccountsCode" attributeEntry="${assetPaymentAttributes.chartOfAccountsCode}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].accountNumber" attributeEntry="${assetPaymentAttributes.accountNumber}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].subAccountNumber" attributeEntry="${assetPaymentAttributes.subAccountNumber}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialObjectCode" attributeEntry="${assetPaymentAttributes.financialObjectCode}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialSubObjectCode" attributeEntry="${assetPaymentAttributes.financialSubObjectCode}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].projectCode" attributeEntry="${assetPaymentAttributes.projectCode}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].organizationReferenceId" attributeEntry="${assetPaymentAttributes.organizationReferenceId}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].documentNumber" attributeEntry="${assetPaymentAttributes.documentNumber}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentTypeCode" attributeEntry="${assetPaymentAttributes.financialDocumentTypeCode}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].purchaseOrderNumber" attributeEntry="${assetPaymentAttributes.purchaseOrderNumber}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].requisitionNumber" attributeEntry="${assetPaymentAttributes.requisitionNumber}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingDate" attributeEntry="${assetPaymentAttributes.financialDocumentPostingDate}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingYear" attributeEntry="${assetPaymentAttributes.financialDocumentPostingYear}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].financialDocumentPostingPeriodCode" attributeEntry="${assetPaymentAttributes.financialDocumentPostingPeriodCode}" readOnly="true"/></td>								
				<td class="grid"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].transferPaymentCode" attributeEntry="${assetPaymentAttributes.transferPaymentCode}" readOnly="true"/></td>								
				<td class="grid" align="right"><kul:htmlControlAttribute property="${assetValueObj}.assetPayments[${pos}].accountChargeAmount" attributeEntry="${assetPaymentAttributes.accountChargeAmount}" readOnly="true"/></td>								
			</tr>
	</c:forEach>
			<tr>
				<th class="grid" align="right" colspan="14"><kul:htmlAttributeLabel noColon="true"  attributeEntry="${assetPaymentAttributes.paymentTotalCost}" readOnly="true" /></th>
				<td class="grid" align="right"><kul:htmlControlAttribute property="${assetValueObj}.paymentTotalCost" attributeEntry="${assetPaymentAttributes.paymentTotalCost}" readOnly="true"/></td>
			</tr>						
		</table>
		</div>
</kul:tab>
