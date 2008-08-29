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
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="ctr" required="true" description="item count"%>
<%@ attribute name="ctr2" required="true" description="item count"%>
<%@ attribute name="camsAssetLocationProperty" required="true" description="String that represents the prefix of the property name to store into the document on the form."%>
<%@ attribute name="isEditable" required="true" description="Determines if a cams location is editable"%>
<%@ attribute name="availability" required="true" description="Determines if this is a capture once tag or for each"%>

<c:if test="${empty isEditable}">
	<c:set var="isEditable" value="false"/>
</c:if>

<c:if test="${empty availability}">
	<c:set var="availability" value="${PurapConstants.CapitalAssetAvailability.EACH}"/>
</c:if>

<c:set var="deleteLocationUrl" value="methodToCall.deleteCapitalAssetLocationByItem.(((${ctr}))).((#${ctr2}#))" />
<c:if test="${PurapConstants.CapitalAssetAvailability.ONCE eq availability}">
	<c:set var="deleteLocationUrl" value="methodToCall.deleteCapitalAssetLocationByDocument.(((${ctr}))).((#${ctr2}#))" />
</c:if>

<table class="datatable" summary="" border="0" cellpadding="0" cellspacing="0" style="width:100%">
<tr>
	<td colspan="4" class="subhead">
		<span class="left">Location</span>
		<c:if test="${!isEditable}">
		<span class="right">
			<html:image property="${deleteLocationUrl}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete a Asset Location" title="Delete a Asset Location" styleClass="tinybutton" />
		</span>
		</c:if>
	</td>
</tr>
<tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.itemQuantity}" align="right" />
	<td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.itemQuantity}" property="${camsAssetLocationProperty}.itemQuantity" readOnly="${!isEditable}"/>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" property="${camsAssetLocationProperty}.capitalAssetLine1Address" readOnly="${!isEditable}"/>
	</td>
    </tr>
    <tr>
    	<kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.buildingCode}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingCode}" property="${camsAssetLocationProperty}.buildingCode" readOnly="${!isEditable}"/>
		<c:if test="${isEditable}">
        <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
        	lookupParameters="${camsAssetLocationProperty}.campusCode:campusCode"
        	fieldConversions="buildingCode:${camsAssetLocationProperty}.buildingCode,campusCode:${camsAssetLocationProperty}.campusCode,buildingStreetAddress:${camsAssetLocationProperty}.capitalAssetLine1Address,buildingAddressCityName:${camsAssetLocationProperty}.capitalAssetCityName,buildingAddressStateCode:${camsAssetLocationProperty}.capitalAssetStateCode,buildingAddressZipCode:${camsAssetLocationProperty}.capitalAssetPostalCode" />
		</c:if>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetCityName}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetCityName}" property="${camsAssetLocationProperty}.capitalAssetCityName" readOnly="${!isEditable}"/>
	</td>
</tr>
<tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.campusCode}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.campusCode}" property="${camsAssetLocationProperty}.campusCode" readOnly="${!isEditable}"/>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" align="right" />
	<td class="datacell">
    	<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" property="${camsAssetLocationProperty}.capitalAssetStateCode" readOnly="${!isEditable}"/>
	</td>
</tr>
<tr>
	<kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.buildingRoomNumber}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingRoomNumber}" property="${camsAssetLocationProperty}.buildingRoomNumber" readOnly="${!isEditable}"/>
	</td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" align="right" />
    <td class="datacell">
		<kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" property="${camsAssetLocationProperty}.capitalAssetPostalCode" readOnly="${!isEditable}"/>
	</td>
</tr>
</table>