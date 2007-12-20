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

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiRoutingFormDocument"
	htmlFormAction="researchRoutingFormAuditMode"
	headerDispatch="auditmode" headerTabActive="auditmode">
	
	<kul:errors keyMatch="${Constants.DOCUMENT_ERRORS}" />
	
	<kra-rf:routingFormHiddenDocumentFields />
	
	<div align="right">
		<kul:help documentTypeName="${DataDictionary.KualiRoutingFormDocument.documentTypeName}" pageName="Audit Mode" altText="page help"/>
	</div>	
	
	<kra-rf:routingFormAuditMode />
	
</kul:documentPage>