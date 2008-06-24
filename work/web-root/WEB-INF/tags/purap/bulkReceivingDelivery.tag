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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="deliveryReadOnly" required="false"
              description="Boolean to indicate if delivery tab fields are read only" %>              

<c:if test="${empty deliveryReadOnly}">
	<c:set var="deliveryReadOnly" value="false" />
</c:if>

<c:set var="lockAddressToVendor" value="${(not empty KualiForm.editingMode['lockAddressToVendor'])}" />

<kul:tab tabTitle="Delivery" defaultOpen="true" tabErrorKey="${PurapConstants.DELIVERY_TAB_ERRORS}">
    <div class="tab-container" align=center>
    
    	<!---- Final Delivery ---->
        <div class="h2-container">
            <h2>Final Delivery</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Final Delivery Section">
            <tr>
 				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryBuildingName}" 
                    	property="document.deliveryBuildingName"
                    	onchange="submitForm()"
                    	readOnly="true"/>&nbsp;
                     <!-- TODO: figure out how to add fullEntryMode to this (initial try wasn't working) -->
                    <c:if test="${(fullEntryMode) && not(deliveryReadOnly)}">
                    	<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Building"
                    		lookupParameters="document.deliveryCampusCode:campusCode"
                    		fieldConversions="buildingName:document.deliveryBuildingName,campusCode:document.deliveryCampusCode,buildingStreetAddress:document.deliveryBuildingLine1Address,buildingAddressCityName:document.deliveryCityName,buildingAddressStateCode:document.deliveryStateCode,buildingAddressZipCode:document.deliveryPostalCode"/>
                    </c:if>
                </td>           
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToName}" 
                    	property="document.deliveryToName" readOnly="${not (fullEntryMode) or deliveryReadOnly}"/>
                    <c:if test="${fullEntryMode && not(deliveryReadOnly)}">
                        <kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" 
                        	fieldConversions="personName:document.deliveryToName,personEmailAddress:document.deliveryToEmailAddress,personLocalPhoneNumber:document.deliveryToPhoneNumber"/>
                    </c:if>
                </td>
            </tr>
            <tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCampusCode}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute 
                    	attributeEntry="${documentAttributes.deliveryCampusCode}" 
                    	property="document.deliveryCampusCode" 
                    	readOnly="${deliveryReadOnly}"/>                
                </td>           	
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryToPhoneNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryToPhoneNumber}" 
                    	property="document.deliveryToPhoneNumber" readOnly="${not (fullEntryMode) or deliveryReadOnly}"/>
                </td>
            </tr>
			
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingLine1Address}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingLine1Address}" 
                    	property="document.deliveryBuildingLine1Address"  readOnly="${not (fullEntryMod) or deliveryReadOnly}"/>
                </td>			
               
			</tr>
			
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryBuildingRoomNumber}" 
                    	property="document.deliveryBuildingRoomNumber" readOnly="${not (fullEntryMode) or deliveryReadOnly}"/>
                </td>			
                <th align=right valign=middle class="bord-l-b" rowspan="4">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryInstructionText}"/></div>
                </th>
                <td align=left valign=middle class="datacell"  rowspan="4">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryInstructionText}" 
                    	property="document.deliveryInstructionText" readOnly="${not (fullEntryMode) or deliveryReadOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryCityName}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryCityName}" 
                    	property="document.deliveryCityName" readOnly="${not (fullEntryMode) or deliveryReadOnly}"/>
                </td>
            </tr>
            <tr>			
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryStateCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryStateCode}" 
                    	property="document.deliveryStateCode" readOnly="${not (fullEntryMode) or deliveryReadOnly}"/>
                </td>
			</tr>
			<tr>
				<th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.deliveryPostalCode}"/></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.deliveryPostalCode}" 
                    	property="document.deliveryPostalCode" readOnly="${not (fullEntryMode) or deliveryReadOnly}"/>
                </td>
			</tr>
        </table>
	</div>
</kul:tab>
