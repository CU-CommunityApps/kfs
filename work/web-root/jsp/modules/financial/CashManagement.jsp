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

<%--
  HACK: CashManagementDocument isn't a transactionalDocument, but its XML file claims that it is,
  which is why this JSP abuses some of the standard transactionalDocument tags
--%>
<c:set var="allowAdditionalDeposits" value="${KualiForm.editingMode['allowAdditionalDeposits']}" />
<c:set var="showDeposits" value="${allowAdditionalDeposits || (!empty KualiForm.document.deposits)}" />

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashManagement" documentTypeName="CashManagementDocument" renderMultipart="true" showTabButtons="true">
    <kfs:hiddenDocumentFields isFinancialDocument="false"/>
    
    <kfs:documentOverview editingMode="${KualiForm.editingMode}"/>
    
    <c:if test="${!empty KualiForm.document.checks}">
      <logic:iterate indexId="ctr" name="KualiForm" property="document.checks" id="currentCheck">
        <fin:hiddenCheckLine propertyName="document.checks[${ctr}]" displayHidden="false" />
      </logic:iterate>
    </c:if>
    
    <cm:cashDrawerActivity/>
    
    <c:if test="${!showDeposits}">
        <kul:hiddenTab forceOpen="true" />
    </c:if>
    <c:if test="${showDeposits}">
        <cm:deposits editingMode="${KualiForm.editingMode}"/>
    </c:if>

    <cm:cashieringActivity />
    
    <c:if test="${!empty KualiForm.recentlyClosedItemsInProcess}">
      <cm:recentlyClosedMiscAdvances />
    </c:if>

    <c:if test="${KualiForm.document.bankCashOffsetEnabled}" >
        <gl:generalLedgerPendingEntries />
    </c:if>
    
    <kul:notes/>
    <kul:adHocRecipients />
    <kul:routeLog/>
    <kul:panelFooter/>
    
    <kfs:documentControls transactionalDocument="false"/>
</kul:documentPage>
