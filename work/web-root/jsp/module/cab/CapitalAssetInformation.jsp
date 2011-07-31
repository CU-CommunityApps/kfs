<%--
 Copyright 2005-2008 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<script>
		function selectAll(all, styleid){
		var elms = document.getElementsByTagName("input");
		for(var i=0; i< elms.length; i++){
			if(elms[i].id !=null  && elms[i].id==styleid && !elms[i].disabled){
				elms[i].checked = all.checked;
			}
		}
	}

<c:if test="${!empty KualiForm.currDocNumber}">
	var popUpurl = 'cabGlLine.do?methodToCall=viewDoc&documentNumber=${KualiForm.currDocNumber}';
	window.open(popUpurl, "${KualiForm.currDocNumber}");
</c:if>
	
</script>
<kul:page showDocumentInfo="false" htmlFormAction="cabCapitalAssetInformation" renderMultipart="true"
	showTabButtons="true" docTitle="Capital Asset Information Processing" 
	transactionalDocument="false" headerDispatch="true" headerTabActive="true"
	sessionDocument="false" headerMenuBar="" feedbackKey="true" defaultMethodToCall="start" >
	
	<kul:tabTop tabTitle="Financial Document Capital Asset Info" defaultOpen="true">
		<div class="tab-container" align=center>
		<c:set var="CapitalAssetInformationAttributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />	
		<c:set var="dataCellCssClass" value="datacell" />
		<c:set var="capitalAssetInfoName" value="KualiForm.capitalAssetInformation" />
	    <div align="center" vAlign="middle">
	    <h3>Capital Assets for Accounting Line</h3>
		<table datatable style="border-top: 1px solid rgb(153, 153, 153); width: 100%;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
			<tr>
			 	<c:if test="${!readOnly}">
					<kul:htmlAttributeHeaderCell literalLabel="Action"/>
				</c:if>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetActionIndicator}" labelFor="${capitalAssetInfoName}.capitalAssetActionIndicator"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineNumber}" labelFor="${capitalAssetInfoName}.capitalAssetLineNumber"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" labelFor="${capitalAssetInfoName}.capitalAssetNumber"/> 
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" labelFor="${capitalAssetInfoName}.capitalAssetQuantity"/> 
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" labelFor="${capitalAssetInfoName}.capitalAssetTypeCode"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.vendorName}" labelFor="${capitalAssetInfoName}.vendorName"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerName"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerModelNumber"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" labelFor="${capitalAssetInfoName}.capitalAssetDescription"/>
				<kul:htmlAttributeHeaderCell attributeEntry="${CapitalAssetInformationAttributes.amount}" labelFor="${capitalAssetInfoName}.amount"/>
			</tr>
			<c:forEach items="${KualiForm.capitalAssetInformation}" var="detailLine" varStatus="status">
			<tr>
				<c:if test="${!readOnly}">	
		    	<td class="grid" align="center">
	    			<html:link target="_blank" href="cabGlLine.do?methodToCall=process&documentNumber=${detailLine.documentNumber}&generalLedgerAccountIdentifier=${KualiForm.generalLedgerAccountIdentifier}&capitalAssetLineNumber=${detailLine.capitalAssetLineNumber}">
						Process
					</html:link>
				</td>
				</c:if>
				<td class="grid"><div>
					<c:set var="assetActionType" value="${detailLine.capitalAssetActionIndicator}" />
				    	<c:if test="${assetActionType eq KFSConstants.CapitalAssets.CAPITAL_ASSET_CREATE_ACTION_INDICATOR}">
					    	<c:out value="${KFSConstants.CapitalAssets.CREATE_CAPITAL_ASSETS_TAB_TITLE}" />
					    </c:if>
				        <c:if test="${assetActionType eq KFSConstants.CapitalAssets.CAPITAL_ASSET_MODIFY_ACTION_INDICATOR}">
					    	<c:out value="${KFSConstants.CapitalAssets.MODIFY_CAPITAL_ASSETS_TAB_TITLE}" />
					    </c:if>
				</div></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetLineNumber" 
					attributeEntry="${CapitalAssetInformationAttributes.capitalAssetLineNumber}" readOnly="true"/></td>
				<td class="grid">
					<c:if test="${!empty detailLine.capitalAssetNumber}">
						<kul:inquiry boClassName="org.kuali.kfs.integration.cam.CapitalAssetManagementAsset" keyValues="capitalAssetNumber=${detailLine.capitalAssetNumber}" render="true">
							<kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetNumber" attributeEntry="${CapitalAssetInformationAttributes.capitalAssetNumber}" readOnly="true"/>
						</kul:inquiry>
					</c:if>&nbsp;
				</td>	
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetQuantity" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetQuantity}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetTypeCode" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetTypeCode}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].vendorName" 
								attributeEntry="${CapitalAssetInformationAttributes.vendorName}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetManufacturerName" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerName}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetManufacturerModelNumber" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetManufacturerModelNumber}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].capitalAssetDescription" 
								attributeEntry="${CapitalAssetInformationAttributes.capitalAssetDescription}" readOnly="true"/></td>
				<td class="grid"><kul:htmlControlAttribute property="capitalAssetInformation[${status.index}].amount" 
								attributeEntry="${CapitalAssetInformationAttributes.amount}" readOnly="true"/></td>
			</tr>
			</c:forEach>	
	    </table>
		</div>
	</kul:tabTop>
	
	<kul:tab tabTitle="GL Entry Processing" defaultOpen="true">
		<cams:glEntryProcessing generalLedgerEntry="${KualiForm.generalLedgerEntry}" />
	</kul:tab>
	<kul:panelFooter />
</kul:page>
