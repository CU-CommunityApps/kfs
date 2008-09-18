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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsItemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsSystemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsAssetAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="isRequisition" required="false" description="Determines if this is a requisition document"%>
<%@ attribute name="isPurchaseOrder" required="false" description="Determines if this is a requisition document"%>

<c:if test="${empty isRequisition}">
	<c:set var="isRequisition" value="false"/>
</c:if>

<c:if test="${empty isPurchaseOrder}">
	<c:set var="isPurchaseOrder" value="false"/>
</c:if>

<c:set var="lockCamsEntry"	value="${(not empty KualiForm.editingMode['lockCamsEntry'])}" />

<kul:tab tabTitle="Capital Asset" defaultOpen="false" tabErrorKey="${PurapConstants.CAPITAL_ASSET_TAB_ERRORS}">
	<div class="tab-container" align=center>
		
    <table cellpadding="0" cellspacing="0" class="datatable" summary="System Selection">
	<tr>
		<td colspan="2" class="subhead">System Selection</td>
	</tr>

	<tr>
 		<th width="20%" align=right valign=middle class="bord-l-b">
			<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.capitalAssetSystemTypeCode}" /></div>
		</th>
        <td align=left valign=middle class="datacell">
			<kul:htmlControlAttribute attributeEntry="${documentAttributes.capitalAssetSystemTypeCode}" property="document.capitalAssetSystemTypeCode" readOnly="${lockCamsEntry}" extraReadOnlyProperty="document.capitalAssetSystemType.capitalAssetSystemTypeDescription" />
		</td>
	</tr>
	<tr>
		<th align=right valign=middle class="bord-l-b">
			<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.capitalAssetSystemStateCode}" /></div>
		</th>
        <td align=left valign=middle class="datacell">
			<kul:htmlControlAttribute attributeEntry="${documentAttributes.capitalAssetSystemStateCode}" property="document.capitalAssetSystemStateCode" readOnly="${lockCamsEntry}" extraReadOnlyProperty="document.capitalAssetSystemState.capitalAssetSystemStateDescription" />
		</td>
	</tr>
	<tr>
		<th align=right valign=middle class="bord-l-b">
			Action:
       </th>
	   <td align=left valign=middle class="datacell">
	   		<c:if test="${not lockCamsEntry}">
				<c:choose>
					<c:when test="${empty KualiForm.document.purchasingCapitalAssetItems}">
						<html:image property="methodToCall.selectSystem" src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif" alt="select system" styleClass="tinybutton"/>
					</c:when>
					<c:otherwise>
						<html:image property="methodToCall.changeSystem" src="${ConfigProperties.externalizable.images.url}tinybutton-change.gif" alt="select system" styleClass="tinybutton"/>
						<html:image property="methodToCall.updateCamsView" src="${ConfigProperties.externalizable.images.url}tinybutton-updateview.gif" alt="Update Cams View" styleClass="tinybutton"/>
					</c:otherwise>
				</c:choose>
			</c:if>
		</td>
	</tr>

	</table>

	<c:set var="availabilityOnce" value="${PurapConstants.CapitalAssetAvailability.ONCE}"/>

	<c:if test="${!empty KualiForm.document.purchasingCapitalAssetItems and ( (KualiForm.purchasingItemCapitalAssetAvailability eq availabilityOnce) or (KualiForm.purchasingCapitalAssetSystemCommentsAvailability eq availabilityOnce) or (KualiForm.purchasingCapitalAssetSystemDescriptionAvailability eq availabilityOnce) or (KualiForm.purchasingCapitalAssetSystemAvailability eq availabilityOnce) )}">
	<table cellpadding="0" cellspacing="0" class="datatable" summary="CAMS Systems" style="width:100%">
	<tr>
		<td colspan="12" class="subhead">System Detail</td>
	</tr>	
	<tr>
	<td colspan="12" class="datacell" style="padding:0;">
		<logic:iterate indexId="ctr" name="KualiForm" property="document.purchasingCapitalAssetSystems" id="systemLine">
			<purap:camsDetail ctr="${ctr}" camsSystemAttributes="${camsSystemAttributes}" camsAssetAttributes="${camsAssetAttributes}" camsLocationAttributes="${camsLocationAttributes}" camsAssetSystemProperty="document.purchasingCapitalAssetSystems[${ctr}]" availability="${PurapConstants.CapitalAssetAvailability.ONCE}" isRequisition="${isRequisition}" isPurchaseOrder="${isPurchaseOrder}"/>
		</logic:iterate>
	</td>		
	</tr>
	</table>
	</c:if>

	<c:if test="${!empty KualiForm.document.purchasingCapitalAssetItems}">
		<purap:camsItems itemAttributes="${itemAttributes}" camsItemAttributes="${camsItemAttributes}" camsSystemAttributes="${camsSystemAttributes}" camsAssetAttributes="${camsAssetAttributes}" camsLocationAttributes="${camsLocationAttributes}" isRequisition="${isRequisition}" isPurchaseOrder="${isPurchaseOrder}" />
	</c:if>

	</div>
</kul:tab>