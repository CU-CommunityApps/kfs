<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<%@ tag description="render the given field in the capital asset info object"%>

<%@ attribute name="readOnly" required="false" description="Whether the capital asset information should be read only" %>	

<script language="JavaScript" type="text/javascript" src="dwr/interface/VendorService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/vnd/objectInfo.js"></script>
	
<c:set var="attributes" value="${DataDictionary.CapitalAssetInformation.attributes}" />	
<c:set var="dataCellCssClass" value="datacell" />
<c:set var="capitalAssetInfoName" value="document.capitalAssetInformation" />

<table class="datatable" style="border-top: 1px solid rgb(153, 153, 153);"  cellpadding="0" cellspacing="0" summary="Capital Asset Information">
   <tr>
   		<td colspan="7" class="tab-subhead" style="border-top: medium;">Create New Assets</td>
   </tr>
   <tr>
   		<td colspan="4" class="tab-subhead" style="border-top: medium;">
   			<br/>System Control Amount: <c:out value="${KualiForm.systemControlAmount}" />
   		</td>
   		<td colspan="3" class="tab-subhead" style="border-top: medium;">
   			<br/>System Control Remainder Amount: <c:out value="${KualiForm.createdAssetsControlAmount}" />
   		</td>
   </tr>

	<c:forEach items="${KualiForm.document.capitalAssetInformation}" var="detailLine" varStatus="status">
		<tr><td colspan="7">
     	<div class="tab-container" align="center">
     		<h3>Capital Asset for Accounting Line</h3>
	     	<table datatable style="border-top: 1px solid rgb(153, 153, 153); width: 98%;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
				<tr>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${attributes.sequenceNumber}"
						useShortLabel="true"
					/>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${attributes.financialDocumentLineTypeCode}"
						useShortLabel="true"
						/>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${attributes.chartOfAccountsCode}"
						useShortLabel="true"
						hideRequiredAsterisk="true"
					/>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${attributes.accountNumber}"
						useShortLabel="false"
					/>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${attributes.financialObjectCode}"
						useShortLabel="true"
					/>
				</tr>
			    <tr>
			    	<td class="datacell">
						<div align="center">
							<kul:htmlControlAttribute attributeEntry="${attributes.sequenceNumber}" property="${capitalAssetInfoName}[${status.index}].sequenceNumber" readOnly="true"/>					
						</div>		            
			        </td>
			        <td class="datacell"><div>
			        	<c:set var="lineType" value="${detailLine.financialDocumentLineTypeCode}" />
			            <c:if test="${lineType eq KFSConstants.SOURCE_ACCT_LINE_TYPE_CODE}">
				        	<c:out value="${KFSConstants.SOURCE}" />
				        </c:if>
			            <c:if test="${lineType eq KFSConstants.TARGET_ACCT_LINE_TYPE_CODE}">
				            	<c:out value="${KFSConstants.TARGET}" />
				        </c:if>
			            </div></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${attributes.chartOfAccountsCode}" property="${capitalAssetInfoName}[${status.index}].chartOfAccountsCode" readOnly="true"/></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${attributes.accountNumber}" property="${capitalAssetInfoName}[${status.index}].accountNumber" readOnly="true"/></td>
			            <td class="datacell"><kul:htmlControlAttribute attributeEntry="${attributes.financialObjectCode}" property="${capitalAssetInfoName}[${status.index}].financialObjectCode" readOnly="true"/></td>
			    </tr>
			</table></div></td>	
	   </tr>
	   
		<tr>
			<td colspan="7">
	     		<div class="tab-container" align="center">
		     		<table datatable style="border-top: 1px solid rgb(153, 153, 153); ; width: 96%;" cellpadding="0" cellspacing="0" summary="Asset for Accounting Lines">
					   <tr> 
							<kul:htmlAttributeHeaderCell literalLabel=""/>	   
					   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetQuantity}" labelFor="${capitalAssetInfoName}.capitalAssetQuantity"/> 
					   	    <kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetTypeCode}" labelFor="${capitalAssetInfoName}.capitalAssetTypeCode"/>
							<kul:htmlAttributeHeaderCell attributeEntry="${attributes.vendorName}" labelFor="${capitalAssetInfoName}.vendorName"/>
							<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerName}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerName"/>
							<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetManufacturerModelNumber}" labelFor="${capitalAssetInfoName}.capitalAssetManufacturerModelNumber"/>
							<kul:htmlAttributeHeaderCell attributeEntry="${attributes.amount}" labelFor="${capitalAssetInfoName}.amount"/>
					   </tr>
	 
					   <tr>
					   		<kul:htmlAttributeHeaderCell literalLabel="${detailLine.capitalAssetLineNumber}"/>	
							<fp:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
								businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
								field="capitalAssetQuantity" lookup="false" inquiry="false" />
				
							<fp:dataCell dataCellCssClass="${dataCellCssClass}"
								businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
								field="capitalAssetTypeCode" lookup="true" inquiry="true"
								boClassSimpleName="AssetPayment" boPackageName="org.kuali.kfs.module.cam.businessobject" 
								lookupOrInquiryKeys="capitalAssetTypeCode"
								businessObjectValuesMap="${capitalAssetInfo.valuesMap}"/>		
								   
							<fp:dataCell dataCellCssClass="${dataCellCssClass}"
								businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
								field="vendorName" lookup="true" inquiry="true" disabled="true"
								boClassSimpleName="VendorDetail" boPackageName="org.kuali.kfs.vnd.businessobject"
								lookupOrInquiryKeys="vendorHeaderGeneratedIdentifier,vendorDetailAssignedIdentifier,vendorName"
								businessObjectValuesMap="${capitalAssetInfo.valuesMap}" />	
							
							<fp:dataCell dataCellCssClass="${dataCellCssClass}"
								businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
								field="capitalAssetManufacturerName" lookup="false" inquiry="false"/>
							
							<fp:dataCell dataCellCssClass="${dataCellCssClass}"
								businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
								field="capitalAssetManufacturerModelNumber" lookup="false" inquiry="false"/>
								
							<fp:dataCell dataCellCssClass="${dataCellCssClass}" dataFieldCssClass="amount"
								businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
								field="amount" lookup="false" inquiry="false" />
								
					   </tr>
		
					   <tr>
					 		<kul:htmlAttributeHeaderCell attributeEntry="${attributes.capitalAssetDescription}" rowspan="${readOnly ? 1 : 2 }"/>
							<fp:dataCell dataCellCssClass="${dataCellCssClass}"
								businessObjectFormName="${capitalAssetInfoName}[${status.index}]" attributes="${attributes}" readOnly="${readOnly}"
								field="capitalAssetDescription" lookup="false" inquiry="false" colSpan="${readOnly ? 6 : 5 }" rowSpan="${readOnly ? 1 : 2 }"/>
							<c:if test="${!readOnly}">
								<kul:htmlAttributeHeaderCell literalLabel="Action"/>
							</c:if>
					   </tr>
						<c:if test="${!readOnly}">
							<tr>
								<td class="infoline">  
									<div style="text-align: center;">	
										<html:image property="methodToCall.addCapitalAssetInfo.line${status.index}.Anchor" 
											src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" 
											title="Add the capital Asset Information"
											alt="Add the capital Asset Information" styleClass="tinybutton" />	
										<br /><br />	 
										<html:image property="methodToCall.deleteCapitalAssetInfo.line${status.index}.Anchor" 
											src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" 
											title="Delete the capital Asset Information"
											alt="Delete the capital Asset Information" styleClass="tinybutton" />
										<br /><br />	 
										<html:image property="methodToCall.clearCapitalAssetInfo.line${status.index}.Anchor" 
											src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" 
											title="Clear the capital Asset Information"
											alt="Clear the capital Asset Information" styleClass="tinybutton" />
									</div>
								</td>
							</tr>
						</c:if>
						
						<c:if test="${not empty detailLine.capitalAssetInformationDetails}" >
						   <tr><td colSpan="7"><center><br/>
								<fp:capitalAssetInfoDetail capitalAssetInfoDetails="${capitalAssetInfoName}[${status.index}].capitalAssetInformationDetails" 
									capitalAssetInfoDetailsName="${capitalAssetInfoName}[${status.index}].capitalAssetInformationDetails" readOnly="${readOnly}"
									capitalAssetInfoIndex="${status.index}"/>
							<br/></center></td></tr>
						</c:if>
					</table>
				</div>
	   		</td>
		</tr>
	</c:forEach>	
</table>
