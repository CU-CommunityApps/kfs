<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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
<kul:page showDocumentInfo="false" htmlFormAction="cabGlLine" renderMultipart="true"
	showTabButtons="true" docTitle="General Ledger Processing" 
	transactionalDocument="false" headerDispatch="true" headerTabActive="true"
	sessionDocument="false" headerMenuBar="" feedbackKey="true" defaultMethodToCall="start" >
	
	<kul:tabTop tabTitle="Fiancial Document Capital Edit Info" defaultOpen="true">
		<div class="tab-container" align=center>
		<c:set var="CapitalAssetInformationAttributes"	value="${DataDictionary.CapitalAssetInformation.attributes}" />
			<c:if test="${!empty KualiForm.capitalAssetInformation}">
			<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.documentNumber}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.documentNumber" attributeEntry="${CapitalAssetInformationAttributes.documentNumber}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTagNumber}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetTagNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTagNumber}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetQuantity" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetTypeCode" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetManufacturerName" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetDescription" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetManufacturerModelNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true"/></td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right"><kul:htmlAttributeLabel attributeEntry="${CapitalAssetInformationAttributes.capitalAssetSerialNumber}" readOnly="true" /></th>
				<td class="grid" width="50%"><kul:htmlControlAttribute property="capitalAssetInformation.capitalAssetSerialNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetSerialNumber}" readOnly="true"/></td>
			</tr>
		</table>
		</c:if>
		</div>
	</kul:tabTop>
	<kul:tab tabTitle="GL Entry Processing" defaultOpen="true">
		<div class="tab-container" align=center>
		<c:set var="entryAttributes"	value="${DataDictionary.GeneralLedgerEntry.attributes}" />
		<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
				<tr>
					<th>Select</th>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalYear}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.accountNumber}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.subAccountNumber}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialObjectCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialSubObjectCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialBalanceTypeCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialObjectTypeCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalPeriodCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialDocumentTypeCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialSystemOriginationCode}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.documentNumber}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionLedgerEntryDescription}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionLedgerEntryAmount}" hideRequiredAsterisk="true" scope="col"/>
		            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionDebitCreditCode}" hideRequiredAsterisk="true" scope="col"/>
				</tr>
		<html:hidden property="listSize"/>
		<html:hidden property="primaryGlAccountId"/>
		<c:set var="pos" value="-1" />		   			 
    	<c:forEach var="entry" items="${KualiForm.relatedGlEntries}">
	 	<c:set var="pos" value="${pos+1}" />    	
			<tr>
				<td class="grid">
					<c:choose> 
					<c:when test="${entry.generalLedgerAccountIdentifier == KualiForm.primaryGlAccountId}">
						<html:checkbox property="relatedGlEntries[${pos}].selected" disabled="true" />
					</c:when>
					<c:otherwise> 
						<html:checkbox property="relatedGlEntries[${pos}].selected"/>
					</c:otherwise>
					</c:choose>
					<html:hidden property="relatedGlEntries[${pos}].generalLedgerAccountIdentifier"/>
				</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].universityFiscalYear" 
				attributeEntry="${entryAttributes.universityFiscalYear}" readOnly="true"/></td>
				<td class="grid">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${entry.chartOfAccountsCode}" render="true">
					<kul:htmlControlAttribute property="relatedGlEntries[${pos}].chartOfAccountsCode" 
					attributeEntry="${entryAttributes.chartOfAccountsCode}" readOnly="true"/>
				</kul:inquiry>
				</td>
				<td class="grid">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${entry.chartOfAccountsCode}&accountNumber=${entry.accountNumber}" render="true">
					<kul:htmlControlAttribute property="relatedGlEntries[${pos}].accountNumber" 
					attributeEntry="${entryAttributes.accountNumber}" readOnly="true"/>
					</kul:inquiry>
				</td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].subAccountNumber" 
				attributeEntry="${entryAttributes.subAccountNumber}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].financialObjectCode" 
				attributeEntry="${entryAttributes.financialObjectCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].financialSubObjectCode" 
				attributeEntry="${entryAttributes.financialSubObjectCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].financialBalanceTypeCode" 
				attributeEntry="${entryAttributes.financialBalanceTypeCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].financialObjectTypeCode" 
				attributeEntry="${entryAttributes.financialObjectTypeCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].universityFiscalPeriodCode" 
				attributeEntry="${entryAttributes.universityFiscalPeriodCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].financialDocumentTypeCode" 
				attributeEntry="${entryAttributes.financialDocumentTypeCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].financialSystemOriginationCode" 
				attributeEntry="${entryAttributes.financialSystemOriginationCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].documentNumber" 
				attributeEntry="${entryAttributes.documentNumber}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].transactionLedgerEntryDescription" 
				attributeEntry="${entryAttributes.transactionLedgerEntryDescription}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].transactionLedgerEntryAmount" 
				attributeEntry="${entryAttributes.transactionLedgerEntryAmount}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="relatedGlEntries[${pos}].transactionDebitCreditCode" 
				attributeEntry="${entryAttributes.transactionDebitCreditCode}" readOnly="true"/></td>
			</tr>
		</c:forEach>   			
    	</table>
		</div>
	</kul:tab>
	<kul:panelFooter />
	<div id="globalbuttons" class="globalbuttons">
        <c:if test="${not readOnly}">
	        <!--<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif" styleClass="globalbuttons" 
	        	property="methodToCall.submit" title="submit" alt="submit"/>-->
	    	<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_assets.gif" property="methodToCall.submitAssetGlobal" title="Add Assets" alt="Add Assets"/>
	    	&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
	    	<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_payments.gif" property="methodToCall.submitPaymentGlobal" title="Add Payments" alt="Add Payments"/>
	    	&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;    	
	        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_closes.gif" styleClass="globalbuttons" property="methodToCall.close" title="Close" alt="Close"/>
        </c:if>		
    </div>
</kul:page>