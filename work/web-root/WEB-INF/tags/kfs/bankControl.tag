<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<%@ attribute name="property" required="true" description="name of the property that holds the bank code value in the form" %>
<%@ attribute name="objectProperty" required="true" description="name of the property that holds the bank object in the form" %>
<%@ attribute name="depositOnly" required="false" description="boolean indicating whether the bank lookup call should request only deposit banks" %>
<%@ attribute name="disbursementOnly" required="false" description="boolean indicating whether the bank lookup call should request only disbursement banks" %>
<%@ attribute name="readOnly" required="false" description="boolean indicating whether the document is readOnly. If not an additional check is made to verify the bank edit mode was exported." %>
<%@ attribute name="style" required="false" description="style class for the cell" %>

<html:hidden property="documentActionFlags.canEditBank" />
<html:hidden property="documentActionFlags.canViewBank" />
  
<c:if test="${KualiForm.documentActionFlags.canViewBank}">

  <script type='text/javascript' src="scripts/kfs/objectInfo.js"></script>

  <c:if test="${not KualiForm.documentActionFlags.canEditBank}">
    <c:set var="readOnly" value="true" />
  </c:if>
  
  <c:if test="${empty style}">
    <c:set var="style" value="datacell-nowrap" />
  </c:if>

  <c:if test="${depositOnly}">
    <c:set var="lookupParameters" value="'Y':bankDepositIndicator" />
    <c:set var="readOnlyFields" value="bankDepositIndicator" />
  </c:if>

  <c:if test="${disbursementOnly}">
    <c:set var="lookupParameters" value="'Y':bankDisbursementIndicator" />
    <c:set var="readOnlyFields" value="bankDisbursementIndicator" />
  </c:if>

  <td class="${style}">
    <kul:htmlControlAttribute attributeEntry="${DataDictionary.Bank.attributes.bankCode}" property="${property}" readOnly="${readOnly}" onblur="loadBankInfo(document.forms['KualiForm'], '${property}', '${objectProperty}');" />
    <c:if test="${not readOnly}">
      &nbsp;
      <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.Bank" fieldConversions="bankCode:${property}" lookupParameters="${lookupParameters}" readOnlyFields="${readOnlyFields}" autoSearch="true"/>
    </c:if>
  
    <br/>
    <div id="${objectProperty}.div" class="fineprint">
       <bean:write name="KualiForm" property="${objectProperty}.bankName"/>&nbsp;
    </div>
  </td>

</c:if>