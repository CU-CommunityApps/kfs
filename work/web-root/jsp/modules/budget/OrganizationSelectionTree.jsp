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

<script language="JavaScript" type="text/javascript" src="scripts/budget/organizationSelectionTree.js"></script>

<c:set var="pointOfViewOrgAttributes" value="${DataDictionary.BudgetConstructionOrganizationReports.attributes}" />
<c:set var="pullupOrgAttributes" value="${DataDictionary.BudgetConstructionPullup.attributes}" />
<c:set var="organizationAttributes" value="${DataDictionary.Org.attributes}" />

<kul:page showDocumentInfo="false"
	htmlFormAction="budgetOrganizationSelectionTree" renderMultipart="true"
	docTitle="Organization Selection"
    transactionalDocument="false" showTabButtons="true">
    	    
	<html-el:hidden name="KualiForm" property="returnAnchor" />
	<html-el:hidden name="KualiForm" property="returnFormKey" />
	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="operatingMode" />
	<html-el:hidden name="KualiForm" property="universityFiscalYear" />

    <kul:errors keyMatch="pointOfViewOrg" errorTitle="Errors found in Organization Selection:" />
    <c:forEach items="${KualiForm.messages}" var="message">
	   ${message}
	</c:forEach>

    <br><br>
	
	<bc:budgetConstructionOrgSelection />
	
	<c:if test="${!empty KualiForm.selectionSubTreeOrgs}">		
		<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.REPORTS}">
			<bc:budgetConstructionOrgSelectionReport />
		</c:if>
		
	    <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.ACCOUNT}">
			<bc:budgetConstructionOrgSelectionAccount />
		</c:if>
		
	    <c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.SALSET}">
			<bc:budgetConstructionOrgSelectionSalset />
		</c:if>
		
		<c:if test="${KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP or KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">    
			<bc:budgetConstructionOrgSelectionPushOrPull />     
		</c:if>       
    </c:if>
    
	<kul:panelFooter/>
	
    <div id="globalbuttons" class="globalbuttons">
        <c:if test="${!empty KualiForm.selectionSubTreeOrgs && KualiForm.operatingMode == BCConstants.OrgSelOpMode.PULLUP}">
             <html:image property="methodToCall.performPullUp" src="${ConfigProperties.externalizable.images.url}buttonsmall_pullup.gif" title="Perform Pullup" alt="Perform Pullup" styleClass="globalbuttons" />
        </c:if>
        
        <c:if test="${!empty KualiForm.selectionSubTreeOrgs && KualiForm.operatingMode == BCConstants.OrgSelOpMode.PUSHDOWN}">
             <html:image property="methodToCall.performPushDown" src="${ConfigProperties.externalizable.images.url}buttonsmall_pushdown.gif" title="Perform Pushdown" alt="Perform Pushdown" styleClass="globalbuttons" />
        </c:if>
        
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.returnToCaller" title="close" alt="close"/>
    </div>
</kul:page>
