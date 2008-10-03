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
<%@ attribute name="itemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsItemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsSystemAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsAssetAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="camsLocationAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="isRequisition" required="false" description="Determines if this is a requisition document"%>
<%@ attribute name="isPurchaseOrder" required="false" description="Determines if this is a requisition document"%>

<c:set var="lockCamsEntry" value="${(not empty KualiForm.editingMode['lockCamsEntry'])}" />

<table cellpadding="0" cellspacing="0" class="datatable" summary="CAMS Items">
	<tr>
		<td colspan="12" class="subhead">CAMS Items</td>
	</tr>
	<tr>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}"/>
		<kul:htmlAttributeHeaderCell><kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" useShortLabel="true" /></kul:htmlAttributeHeaderCell>
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.commodityCode}" nowrap="true" />
		<kul:htmlAttributeHeaderCell><kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemDescription}"/></kul:htmlAttributeHeaderCell>
		<kul:htmlAttributeHeaderCell nowrap="true"><kul:htmlAttributeLabel attributeEntry="${itemAttributes.itemUnitPrice}"/></kul:htmlAttributeHeaderCell>				
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" nowrap="true" />
		<c:if test="${isRequisition}">
		<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemRestrictedIndicator}" nowrap="true" />
		</c:if>
	</tr>

<logic:iterate indexId="ctr" name="KualiForm" property="document.purchasingCapitalAssetItems" id="itemLine">

	<tr>
        <td class="infoline" rowspan="2" valign="middle" align="middle">
        	<b>${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemLineNumber}</b>
        </td>
		<td class="infoline">
			${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemType.itemTypeDescription}
	    </td>
		<td class="infoline">
		    ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemQuantity}
	    </td>
		<td class="infoline">
		    ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemUnitOfMeasureCode}
	    </td>
		<td class="infoline">
		    ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemCatalogNumber}
	    </td>
        <td class="infoline">	
            ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.commodityCode.commodityDescription}
		</td>			    
		<td class="infoline">
		   ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemDescription}
	    </td>
		<td class="infoline">
		    <div align="right">
		        ${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemUnitPrice}
			</div>
		</td>
		<td class="infoline">
			<div align="right">
				${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.extendedPrice}
			</div>
		</td>
		<c:if test="${isRequisition}">
		<td class="infoline">
			<div align="center">
				${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemRestrictedIndicator}
			</div>
		</td>
		</c:if>		
	</tr>

	<!-- Cams Tab -->
    <c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
    <c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
    <c:set var="tabTitle" value="CamsLines-${currentTabIndex}" />
    <c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
    <!--  hit form method to increment tab index -->
    <c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
    <c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

    <%-- default to closed --%>
    <c:choose>
        <c:when test="${empty currentTab}">
            <c:set var="isOpen" value="false" />
        </c:when>
        <c:when test="${!empty currentTab}">
            <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
        </c:when>
    </c:choose>
	
    <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />
    
    <c:set var="itemActive" value="${KualiForm.document.purchasingCapitalAssetItems[ctr].purchasingItem.itemActiveIndicator}"/>
    
	<tr>
	<td class="infoline" valign="middle" colspan="10">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<th colspan="10" style="padding: 0px; border-right: none;">
		    <div align=left>
		  	    <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
			         <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
				</c:if>
			    <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			         <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
			    </c:if>
			    CAMs
			</div>
			</th>
	    </tr>
	
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
			<tr style="display: none;"  id="tab-${tabKey}-div">
		</c:if>  
	        <th colspan="10" style="padding:0;">
				<table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
				<tr>
			    <kul:htmlAttributeHeaderCell attributeEntry="${camsItemAttributes.capitalAssetTransactionTypeCode}" align="right" width="250px"/>
			    <td class="datacell">
					<kul:htmlControlAttribute attributeEntry="${camsItemAttributes.capitalAssetTransactionTypeCode}" property="document.purchasingCapitalAssetItems[${ctr}].capitalAssetTransactionTypeCode" readOnly="${not itemActive}"/>		
				</td>
				</tr>
				</table>
				<purap:camsDetail ctr="${ctr}" camsItemIndex="${ctr}" camsSystemAttributes="${camsSystemAttributes}" camsAssetAttributes="${camsAssetAttributes}" camsLocationAttributes="${camsLocationAttributes}" camsAssetSystemProperty="document.purchasingCapitalAssetItems[${ctr}].purchasingCapitalAssetSystem" availability="${PurapConstants.CapitalAssetAvailability.EACH}" isRequisition="${isRequisition}" isPurchaseOrder="${isPurchaseOrder}" poItemInactive="${not itemActive}"/>
	        </th>    
		<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
		    </tr>
		</c:if>
	
		</table>
	</td>
	</tr>
</logic:iterate>
</table>
