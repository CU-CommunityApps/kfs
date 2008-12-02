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
<%@ attribute name="availability" required="true" description="Determines if this is a capture once tag or for each"%>
<%@ attribute name="poItemInactive" required="false" description="True if the PO item this is a part of is inactive"%>

<c:if test="${empty availability}">
    <c:set var="availability" value="${PurapConstants.CapitalAssetAvailability.EACH}"/>
</c:if>

<c:set var="offCampus" value="false" />
<logic:equal name="KualiForm" property="${camsAssetLocationProperty}.offCampusIndicator" value="Yes">
    <c:set var="offCampus" value="true" />
</logic:equal>

<c:set var="deleteLocationUrl" value="methodToCall.deleteCapitalAssetLocationByItem.(((${ctr}))).((#${ctr2}#))" />
<c:set var="refreshAssetLocationBuildingUrl" value="methodToCall.useOffCampusAssetLocationBuildingByItem.(((${ctr}))).((#${ctr2}#))" />
<c:if test="${PurapConstants.CapitalAssetAvailability.ONCE eq availability}">
    <c:set var="deleteLocationUrl" value="methodToCall.deleteCapitalAssetLocationByDocument.(((${ctr}))).((#${ctr2}#))" />
    <c:set var="refreshAssetLocationBuildingUrl" value="methodToCall.useOffCampusAssetLocationBuildingByDocument.(((${ctr}))).((#${ctr2}#))" />
</c:if>

<table class="datatable" summary="" border="0" cellpadding="0" cellspacing="0" style="width:100%">
<tr>
    <td colspan="4" class="subhead">
        <span class="left">Location</span>
        <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive}">
        <span class="right">
            <html:image property="${deleteLocationUrl}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete a Asset Location" title="Delete a Asset Location" styleClass="tinybutton" />
        </span>
        </c:if>
    </td>
</tr>
<tr>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.itemQuantity}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.itemQuantity}" property="${camsAssetLocationProperty}.itemQuantity" readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive}"/>
    </td>
    <th>&nbsp;</th>
    <td class="datacell">&nbsp;</td>
</tr>
<tr>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.campusCode}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.campusCode}" 
            property="${camsAssetLocationProperty}.campusCode" readOnly="true"/>&nbsp;
        <c:if test="${(fullEntryMode or amendmentEntry) && !poItemInactive}">
            <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.CampusParameter" fieldConversions="campusCode:${camsAssetLocationProperty}.campusCode"/>
        </c:if>
    </td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetCityName}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetCityName}" property="${camsAssetLocationProperty}.capitalAssetCityName" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or poItemInactive}"/>
    </td>
</tr>
<tr>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.buildingCode}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingCode}" 
            property="${camsAssetLocationProperty}.buildingCode" readOnly="true"/>&nbsp;
        <c:if test="${(fullEntryMode or amendmentEntry) and !poItemInactive}">
            <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
                lookupParameters="${camsAssetLocationProperty}.campusCode:campusCode" 
	        	fieldConversions="buildingCode:${camsAssetLocationProperty}.buildingCode,campusCode:${camsAssetLocationProperty}.campusCode"
                anchor="${currentTabIndex}"/>&nbsp;&nbsp;
            <html:image property="${refreshAssetLocationBuildingUrl}" src="${ConfigProperties.externalizable.images.url}tinybutton-offcampus.gif" alt="building not found" styleClass="tinybutton"/>
        </c:if>
    </td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetStateCode}" property="${camsAssetLocationProperty}.capitalAssetStateCode" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or poItemInactive}"/>
    </td>
</tr>
<tr>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetLine1Address}" property="${camsAssetLocationProperty}.capitalAssetLine1Address" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or poItemInactive}"/>
    </td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetPostalCode}" property="${camsAssetLocationProperty}.capitalAssetPostalCode" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or poItemInactive}"/>
    </td>
</tr>
<tr>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.buildingRoomNumber}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.buildingRoomNumber}" property="${camsAssetLocationProperty}.buildingRoomNumber" readOnly="${!(fullEntryMode or amendmentEntry) or poItemInactive}"/>&nbsp;
        <c:if test="${(fullEntryMode or amendmentEntry) && !poItemInactive}">
            <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Room" 
                lookupParameters="${camsAssetLocationProperty}.campusCode:campusCode,${camsAssetLocationProperty}.buildingCode" 
                fieldConversions="buildingRoomNumber:${camsAssetLocationProperty}.buildingRoomNumber"/>
        </c:if>
    </td>
    <kul:htmlAttributeHeaderCell attributeEntry="${camsLocationAttributes.capitalAssetCountryCode}" align="right" />
    <td class="datacell">
        <kul:htmlControlAttribute attributeEntry="${camsLocationAttributes.capitalAssetCountryCode}" property="${camsAssetLocationProperty}.capitalAssetCountryCode" readOnly="${!offCampus or !(fullEntryMode or amendmentEntry) or poItemInactive}"/>
    </td>
</tr>

</tr>
<tr>
</tr>
</table>