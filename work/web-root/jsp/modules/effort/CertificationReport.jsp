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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<html:xhtml/>

<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />
<c:set var="detailAttributes" value="${DataDictionary.EffortCertificationDetail.attributes}" />

<c:set var="detailLines" value="${KualiForm.detailLines}"/>
<c:set var="newDetailLine" value="${KualiForm.newDetailLine}"/>

<c:set var="documentTypeName" value="EffortCertificationDocument"/>
<c:set var="htmlFormAction" value="effortCertificationRecreate"/>

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="effortCertificationReport"
	documentTypeName="${documentTypeName}" renderMultipart="true"
	showTabButtons="true">
	
	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<er:reportInformation />
	
	<c:set var="isSummaryTabEntry" value="${KualiForm.editingMode[EffortConstants.EffortCertificationEditMode.SUMMARY_TAB_ENTRY]}"/>
 	<c:if test="${isSummaryTabEntry}">
		<er:summaryTab/>	
	</c:if>
	
	<er:detailTab isOpen="${!isSummaryTabEntry}"/>
	
	<kul:notes />
	
	<kul:adHocRecipients/>
	
	<kul:routeLog />
	
	<kul:panelFooter />
	
	<kfs:documentControls transactionalDocument="${document.transactionalDocument}" />

</kul:documentPage>
