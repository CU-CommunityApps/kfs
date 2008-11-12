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
<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<c:set var="viewOnly" value="${!empty KualiForm.editingMode['viewOnly']}"/>
<c:set var="tabKey" value="${kfunc:generateTabKey(subTabTitle)}"/>

<kul:tab tabTitle="Assets" defaultOpen="true" tabErrorKey="document.capitalAssetNumber*,document.hiddenFieldForError">
	<div class="tab-container" id="assets" align="center">		

		<html:hidden property="document.hiddenFieldForError"/>
		 	
		<table class="datatable" width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td colspan="3" class="subhead">
			    	<span class="subhead-left">Assets</span>
			  	</td>
			</tr>
			<c:if test="${!viewOnly}">
			    <tr>
				    <th width="10%"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetNumber}" /></th>
					<td class="infoline" valign="top" width="84%">															   
						<kul:htmlControlAttribute attributeEntry="${assetAttributes.capitalAssetNumber}" property="capitalAssetNumber"/>				
						<kul:multipleValueLookup boClassName="org.kuali.kfs.module.cam.businessobject.Asset" lookedUpCollectionName="assetPaymentAssetDetail"/>
					</td>
					<td class="infoline" width="6%" align="center">
					    <input name="methodToCall.insertAssetPaymentAssetDetail" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" class="tinybutton" title="Add an asset" alt="Add an asset" type="image">
				    </td>
			    </tr>
				<tr>
					<td colspan="7">
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="7" style="padding: 0px;border-bottom-style:none;border-left-style:none;border-right-style:none;border-top-style:none;">
					<cams:assetPaymentsAssetInformation/>
				</td>
			</tr>
		</table>
    </div>    
</kul:tab>