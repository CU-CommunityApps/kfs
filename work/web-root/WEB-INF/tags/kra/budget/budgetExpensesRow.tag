<%--
 Copyright 2006 The Kuali Foundation.
 
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

<%@ attribute name="tabTitle" required="true" %>
<%@ attribute name="defaultOpen" required="true" %>
<%@ attribute name="amountRequested" required="true" %>
<%@ attribute name="institutionCostShare" required="false" %>
<%@ attribute name="thirdPartyCostShare" required="false" %>

<c:set var="tabKey" value="${KualiForm.currentTabIndex}"/>
<c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}"/>
<c:set var="incrementerDummy" value="${kfunc:incrementTabIndex(KualiForm, currentTabIndex)}" />

<c:set var="currentTab" value="${kfunc:getTabState(KualiForm, currentTabIndex)}"/>
<c:choose>
    <c:when test="${empty currentTab}">
        <c:set var="isOpen" value="${defaultOpen}" />
    </c:when>
    <c:when test="${!empty currentTab}" >
        <c:set var="isOpen" value="${(currentTab == 'OPEN')}" />
    </c:when>
</c:choose>

<!-- if the section has errors, override and set isOpen to true -->
<logic:messagesPresent property="${tabErrorKey}">    
  <c:set var="isOpen" value="true"/>
</logic:messagesPresent>

<html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

<!-- ROW -->

<tbody>
        <tr>
	        <td class="tab-subhead" width="5%">
	          <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	            <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, ${tabKey}); " />
	          </c:if>
	          <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	            <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, ${tabKey}); " />
	          </c:if>
	        </td>
	        <td colspan="3" class="tab-subhead">${tabTitle}</td>
	
	        <td colspan="2" class="tab-subhead" align="right"><div align="right"><b>${amountRequested}</b> </div></td>
	        <td colspan="2" class="tab-subhead" align="right"><div align="right"><b>${institutionCostShare}</b> </div></td>
	        <td class="tab-subhead" align="right"><div align="right"><b>${thirdPartyCostShare}</b> </div></td>
        </tr>
</tbody>


<c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
<tbody style="display: ;" id="tab-${tabKey}-div">
</c:if>
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
<tbody style="display: none;" id="tab-${tabKey}-div">
</c:if>
 
      
        <!-- display errors for this tab -->
        <c:if test="${! (empty tabErrorKey)}">
          <div class="tab-container-error"><kul:errors keyMatch="${tabErrorKey}"/></div>
        </c:if>
      
        <!-- Before the jsp:doBody of the kul:tab tag -->            
        <jsp:doBody/>            
        <!-- After the jsp:doBody of the kul:tab tag -->
 
</tbody>      
