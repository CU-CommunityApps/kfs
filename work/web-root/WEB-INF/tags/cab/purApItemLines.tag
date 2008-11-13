<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ attribute name="activeIndicator" required="true" description="The display active/inactive line item indicator"%>
<%@ attribute name="title" required="true" description="tab title"%>
<%@ attribute name="defaultOpen" required="false" description="tab title"%>
<%@ attribute name="tabErrorKey" required="false" description="tab error keys"%>
<script language="JavaScript" type="text/javascript" src="scripts/cab/selectCheckBox.js"></script>

<c:set var="purApDocumentAttributes" value="${DataDictionary.PurchasingAccountsPayableDocument.attributes}" />
<c:set var="purApItemAssetAttributes" value="${DataDictionary.PurchasingAccountsPayableItemAsset.attributes}" />
<kul:tab tabTitle="${title}" defaultOpen="${defaultOpen}" tabErrorKey="${tabErrorKey }">
<div class="tab-container" align="center">
<table width="100%" cellpadding="0" cellspacing="0" class="datatable">	
	<tr>
		<td class="tab-subhead"  width="100%" colspan="17">Line Items</td>
	</tr>	
	<tr>
		<th class="grid" align="center">
		<c:if test="${activeIndicator=='true'}">
			<html:checkbox property="selectAll" onclick="selectSources(this)"/>Select		
		</c:if>
		</th>
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApDocumentAttributes.purapDocumentIdentifier}"/>
  		<th class="grid" align="center">Doc Type
  		<th class="grid" align="center">Invoice Status
  		<th class="grid" align="center">Line #
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}"/>
  		<th class="grid" align="center">Split Qty
  		<th class="grid" align="center">Unit Cost
  		<th class="grid" align="center">Object Code
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.accountsPayableLineItemDescription}"/>
  		<kul:htmlAttributeHeaderCell attributeEntry="${purApItemAssetAttributes.capitalAssetTransactionTypeCode}"/>
	    <th class="grid" align="center">TI</th>
	    <th class="grid" align="center">Action</th>
	</tr>
   	<c:set var="chkcount" value="0" />
    <c:forEach items="${KualiForm.purApDocs}" var="purApDoc" >
    	<c:set var="docPos" value="${docPos+1}" />
    	<c:set var="linePos" value="0" />
    	<html:hidden property="purApDocs[${docPos-1}].versionNumber" />
    	<html:hidden property="purApDocs[${docPos-1}].active" />
    	<html:hidden property="purApDocs[${docPos-1}].documentNumber" />
    	<html:hidden property="purApDocs[${docPos-1}].purapDocumentIdentifier" />
    	<html:hidden property="purApDocs[${docPos-1}].purchaseOrderIdentifier" />
    	<html:hidden property="purApDocs[${docPos-1}].documentTypeCode" />
    	<c:forEach items="${purApDoc.purchasingAccountsPayableItemAssets}" var="assetLine" >
	    	<c:set var="linePos" value="${linePos+1}" />
	    	<c:if test="${(assetLine.active && activeIndicator=='true') || (!assetLine.active && activeIndicator == 'false')}">
	    		<cab:purApLineDetail chkcount="${chkcount}" docPos="${docPos}" linePos="${linePos}" itemLine="${assetLine}" >
	    		</cab:purApLineDetail>
	    	<c:if test="${!assetLine.additionalChargeNonTradeInIndicator}">
				<c:set var="chkcount" value="${chkcount+1}" />
			</c:if>
	    	</c:if>
		</c:forEach>
	</c:forEach>
	<c:if test="${activeIndicator=='true'}">
	<tr>
		<th class="grid" align="right" colspan="6">Merge Qty</th>
		<td class="infoline" colspan="2"><kul:htmlControlAttribute property="mergeQty" attributeEntry="${purApItemAssetAttributes.accountsPayableItemQuantity}"/></td>
		<td class="grid" colspan="5" rowspan="2">&nbsp;&nbsp;
			<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-merge.gif" styleClass="tinybutton" property="methodToCall.merge" title="merge" alt="merge"/>&nbsp;&nbsp;&nbsp;
		</td>
	</tr>
	<tr>
		<th class="grid" align="right" colspan="6">Merge Description</th>
		<td class="infoline" colspan="2"><kul:htmlControlAttribute property="mergeDesc" attributeEntry="${purApItemAssetAttributes.accountsPayableLineItemDescription}"/></td>
	</tr>
	</c:if>
</table>
</div>
</kul:tab>
