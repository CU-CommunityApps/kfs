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

<%@ attribute name="capitalAssetNumber" type="java.lang.Long" required="true" description="Capital Asset Number" %>
<%@ attribute name="isTransactionalDocument" required="false" %>

<c:if test="${empty isTransactionalDocument}">
    <c:set var="isTransactionalDocument" value="true" />
</c:if>

<kul:tab tabTitle="Payments Lookup" defaultOpen="false"> 
    <div class="tab-container" align="center">
      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
      	<tr>
            <td class="tab-subhead">Payments Lookup</td>
		</tr>	
		<tr>
            <td>
			  <c:choose>
				<c:when test="${isTransactionalDocument}">
				  <a class="portal_link" href="kr/lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetPayment&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&capitalAssetNumber=${capitalAssetNumber}" target="_blank" title="Payment Information">Click here</a>
				</c:when>
				<c:otherwise>
				  <a class="portal_link" href="lookup.do?methodToCall=search&businessObjectClassName=org.kuali.kfs.module.cam.businessobject.AssetPayment&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true&capitalAssetNumber=${capitalAssetNumber}" target="_blank" title="Payment Information">Click here</a>
				</c:otherwise>
			  </c:choose>
              to view the payment lookup for this asset.
            </td>
		</tr>
	</table>
	</div>
</kul:tab>
